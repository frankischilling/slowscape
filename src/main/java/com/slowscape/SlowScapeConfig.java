package com.slowscape;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("slowscape")
public interface SlowScapeConfig extends Config
{
	@ConfigItem(
		keyName = "allowExtraActionsToday",
		name = "Allow extra actions today",
		description = "Allow intentional SlowScape exceptions today while continuing to count actions.",
		position = 0
	)
	default boolean allowExtraActionsToday()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showOverlay",
		name = "Show overlay",
		description = "Show the SlowScape daily action status overlay.",
		position = 1
	)
	default boolean showOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showBlockedActionAlert",
		name = "Show blocked alert",
		description = "Show a temporary overlay when SlowScape blocks a counted action.",
		position = 2
	)
	default boolean showBlockedActionAlert()
	{
		return true;
	}

	@ConfigItem(
		keyName = "unlockTime",
		name = "Unlock time",
		description = "Local time when the next SlowScape action unlocks each day. Use h:mm format, such as 12:00 or 9:30.",
		position = 3
	)
	default String unlockTime()
	{
		return SlowScapeUnlockSchedule.DEFAULT_UNLOCK_TIME;
	}

	@ConfigItem(
		keyName = "unlockMeridiem",
		name = "Unlock AM/PM",
		description = "Whether the unlock time is AM or PM.",
		position = 4
	)
	default SlowScapeMeridiem unlockMeridiem()
	{
		return SlowScapeMeridiem.AM;
	}

	@ConfigItem(
		keyName = "showUnlockTimer",
		name = "Show unlock timer",
		description = "Show the next unlock date and remaining time on the overlay.",
		position = 5
	)
	default boolean showUnlockTimer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "resetDailyState",
		name = "Reset today",
		description = "Clear today's SlowScape action count and first action record, then turn this option back off.",
		position = 6
	)
	default boolean resetDailyState()
	{
		return false;
	}
}
