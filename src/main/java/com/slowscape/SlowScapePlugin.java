package com.slowscape;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "SlowScape"
)
public class SlowScapePlugin extends Plugin
{
	private static final long PRODUCTION_CONTINUATION_SECONDS = 30;
	private static final long BLOCK_MESSAGE_THROTTLE_SECONDS = 5;
	private static final Duration BLOCK_ALERT_DURATION = Duration.ofSeconds(6);
	private static final String RESET_DAILY_STATE_KEY = "resetDailyState";

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private SlowScapeConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SlowScapeOverlay overlay;

	@Inject
	private SlowScapeBlockedActionOverlay blockedActionOverlay;

	private final SlowScapeActionClassifier actionClassifier = new SlowScapeActionClassifier();
	private final SlowScapeXpChangeTracker xpChangeTracker = new SlowScapeXpChangeTracker();
	private final SlowScapeSnapshotChangeTracker inventoryChangeTracker = new SlowScapeSnapshotChangeTracker();
	private final SlowScapeSnapshotChangeTracker equipmentChangeTracker = new SlowScapeSnapshotChangeTracker();
	private final SlowScapeUnlockSchedule unlockSchedule = new SlowScapeUnlockSchedule(ZoneId.systemDefault());
	private final SlowScapeBlockAlert blockAlert = new SlowScapeBlockAlert();

	private SlowScapeDailyTracker dailyTracker;
	private Instant productionContinuationUntil = Instant.MIN;
	private Instant lastBlockedMessageAt = Instant.MIN;
	private boolean announcedLoginState;

	@Override
	protected void startUp() throws Exception
	{
		dailyTracker = new SlowScapeDailyTracker(
			new SlowScapeConfigStateStore(configManager),
			() -> unlockSchedule.currentPeriodDate(Instant.now(), config.unlockTime(), config.unlockMeridiem()),
			Instant::now);
		overlayManager.add(overlay);
		overlayManager.add(blockedActionOverlay);
		log.debug("SlowScape started");
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		overlayManager.remove(blockedActionOverlay);
		xpChangeTracker.reset();
		inventoryChangeTracker.reset();
		equipmentChangeTracker.reset();
		blockAlert.clear();
		productionContinuationUntil = Instant.MIN;
		lastBlockedMessageAt = Instant.MIN;
		announcedLoginState = false;
		log.debug("SlowScape stopped");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			resetObservationBaselines();
			if (!announcedLoginState)
			{
				announceCurrentState();
				announcedLoginState = true;
			}
			return;
		}

		if (event.getGameState() == GameState.LOGIN_SCREEN
			|| event.getGameState() == GameState.LOGIN_SCREEN_AUTHENTICATOR)
		{
			announcedLoginState = false;
			xpChangeTracker.reset();
			inventoryChangeTracker.reset();
			equipmentChangeTracker.reset();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!SlowScapeConfigStateStore.GROUP.equals(event.getGroup())
			|| !RESET_DAILY_STATE_KEY.equals(event.getKey())
			|| !"true".equals(event.getNewValue())
			|| dailyTracker == null)
		{
			return;
		}

		dailyTracker.resetToday();
		productionContinuationUntil = Instant.MIN;
		lastBlockedMessageAt = Instant.MIN;
		configManager.setConfiguration(SlowScapeConfigStateStore.GROUP, RESET_DAILY_STATE_KEY, false);
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			sendGameMessage("SlowScape daily action state reset for today.");
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (!isLoggedIn())
		{
			return;
		}

		MenuAction action = event.getMenuAction();
		String option = event.getMenuOption();
		SlowScapeActionType actionType = actionClassifier.classify(action, option, event.isItemOp());
		if (actionType != SlowScapeActionType.COUNTED)
		{
			return;
		}

		if (isAllowedProductionContinuation(action, option))
		{
			return;
		}

		String summary = summarize(event);
		SlowScapeActionGateResult result = dailyTracker.trySpend(summary, config.allowExtraActionsToday());
		if (result.isBlocked())
		{
			event.consume();
			showBlockedAlert(result.getRecord());
			sendBlockedMessage(result.getRecord());
			return;
		}

		productionContinuationUntil = Instant.now().plusSeconds(PRODUCTION_CONTINUATION_SECONDS);
		if (result.isOverrideActive())
		{
			sendGameMessage("SlowScape exception allowed: counted extra action #" + result.getRecord().getActionCount()
				+ " today (" + summary + ").");
		}
		else
		{
			sendGameMessage("SlowScape action spent for today: " + summary + ".");
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		if (!isLoggedIn())
		{
			return;
		}

		if (!xpChangeTracker.observe(event.getSkill(), event.getXp()) || dailyTracker.hasSpentToday())
		{
			return;
		}

		if (dailyTracker.recordIfUnspent("XP gained: " + event.getSkill().getName()))
		{
			sendGameMessage("SlowScape action spent for today: XP gained in " + event.getSkill().getName() + ".");
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (!isLoggedIn() || dailyTracker.hasSpentToday())
		{
			return;
		}

		if (event.getContainerId() == InventoryID.INV)
		{
			String snapshot = fingerprint(event.getItemContainer());
			if (inventoryChangeTracker.observe(snapshot))
			{
				recordObservedAction("Inventory changed");
			}
			return;
		}

		if (event.getContainerId() == InventoryID.WORN)
		{
			String snapshot = fingerprint(event.getItemContainer());
			if (equipmentChangeTracker.observe(snapshot))
			{
				recordObservedAction("Equipment changed");
			}
		}
	}

	SlowScapeDailyRecord getCurrentRecord()
	{
		return dailyTracker == null ? SlowScapeDailyRecord.empty().forDate(LocalDate.now()) : dailyTracker.snapshot();
	}

	Instant getNextUnlock()
	{
		return unlockSchedule.nextUnlock(Instant.now(), config.unlockTime(), config.unlockMeridiem());
	}

	String getBlockedActionAlertMessage(Instant now)
	{
		return blockAlert.getMessage(now);
	}

	@Provides
	SlowScapeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SlowScapeConfig.class);
	}

	private void resetObservationBaselines()
	{
		xpChangeTracker.reset();
		inventoryChangeTracker.reset();
		equipmentChangeTracker.reset();
	}

	private boolean isLoggedIn()
	{
		return client.getGameState() == GameState.LOGGED_IN && dailyTracker != null;
	}

	private boolean isAllowedProductionContinuation(MenuAction action, String option)
	{
		return dailyTracker.hasSpentToday()
			&& Instant.now().isBefore(productionContinuationUntil)
			&& actionClassifier.isProductionContinuation(action, option);
	}

	private void recordObservedAction(String summary)
	{
		if (dailyTracker.recordIfUnspent(summary))
		{
			sendGameMessage("SlowScape action spent for today: " + summary + ".");
		}
	}

	private void announceCurrentState()
	{
		SlowScapeDailyRecord record = dailyTracker.snapshot();
		if (config.allowExtraActionsToday())
		{
			sendGameMessage("SlowScape override is active. Extra actions will be allowed and counted today.");
		}
		else if (record.hasSpentAction())
		{
			sendGameMessage("SlowScape action already spent today: " + record.getFirstAction() + ".");
		}
		else
		{
			sendGameMessage("SlowScape action available today.");
		}
	}

	private void sendBlockedMessage(SlowScapeDailyRecord record)
	{
		Instant now = Instant.now();
		if (lastBlockedMessageAt.plusSeconds(BLOCK_MESSAGE_THROTTLE_SECONDS).isAfter(now))
		{
			return;
		}

		lastBlockedMessageAt = now;
		sendGameMessage("SlowScape blocked this action. Today's action was already spent"
			+ firstActionSuffix(record) + ".");
	}

	private void showBlockedAlert(SlowScapeDailyRecord record)
	{
		blockAlert.show("Already spent" + firstActionSuffix(record), Instant.now(), BLOCK_ALERT_DURATION);
	}

	private static String firstActionSuffix(SlowScapeDailyRecord record)
	{
		return record.getFirstAction().isEmpty() ? "" : " on: " + record.getFirstAction();
	}

	private void sendGameMessage(String message)
	{
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
	}

	private static String summarize(MenuOptionClicked event)
	{
		String option = clean(event.getMenuOption());
		String target = clean(event.getMenuTarget());
		if (target.isEmpty())
		{
			return option.isEmpty() ? "Counted action" : option;
		}

		return option + " " + target;
	}

	private static String clean(String value)
	{
		if (value == null)
		{
			return "";
		}

		return value.replaceAll("<[^>]*>", "").trim();
	}

	private static String fingerprint(ItemContainer itemContainer)
	{
		if (itemContainer == null)
		{
			return "";
		}

		StringBuilder fingerprint = new StringBuilder();
		for (Item item : itemContainer.getItems())
		{
			fingerprint.append(item.getId()).append(':').append(item.getQuantity()).append(';');
		}
		return fingerprint.toString();
	}
}
