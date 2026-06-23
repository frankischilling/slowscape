package com.slowscape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

final class SlowScapeOverlay extends OverlayPanel
{
	private static final Color AVAILABLE = new Color(120, 220, 120);
	private static final Color SPENT = new Color(255, 150, 120);
	private static final Color OVERRIDE = new Color(255, 220, 120);
	private static final int OVERLAY_WIDTH = 230;
	private static final int LINE_TEXT_GAP = 8;

	private final SlowScapePlugin plugin;
	private final SlowScapeConfig config;
	private final SlowScapeOverlayModel model = new SlowScapeOverlayModel(ZoneId.systemDefault());

	@Inject
	SlowScapeOverlay(SlowScapePlugin plugin, SlowScapeConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setPreferredSize(new Dimension(OVERLAY_WIDTH, 0));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showOverlay())
		{
			return null;
		}

		SlowScapeDailyRecord record = plugin.getCurrentRecord();
		boolean override = config.allowExtraActionsToday();
		boolean spent = record.hasSpentAction();
		Color stateColor = override ? OVERRIDE : spent ? SPENT : AVAILABLE;
		List<SlowScapeOverlayLine> lines = model.lines(
			record,
			override,
			config.showUnlockTimer(),
			Instant.now(),
			plugin.getNextUnlock());

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("SlowScape")
			.color(stateColor)
			.build());

		FontMetrics metrics = graphics.getFontMetrics();
		for (SlowScapeOverlayLine line : lines)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left(line.getLeft())
				.right(fitRightText(line, metrics))
				.rightColor(rightColor(line, stateColor))
				.build());
		}

		return super.render(graphics);
	}

	private static String fitRightText(SlowScapeOverlayLine line, FontMetrics metrics)
	{
		if (!"First".equals(line.getLeft()))
		{
			return line.getRight();
		}

		int contentWidth = OVERLAY_WIDTH - (ComponentConstants.STANDARD_BORDER * 2);
		int rightWidth = contentWidth - metrics.stringWidth(line.getLeft()) - LINE_TEXT_GAP;
		return SlowScapeTextFitter.fit(line.getRight(), metrics, rightWidth);
	}

	private static Color rightColor(SlowScapeOverlayLine line, Color stateColor)
	{
		if ("Status".equals(line.getLeft()))
		{
			return stateColor;
		}
		if ("Extra used".equals(line.getLeft()))
		{
			return OVERRIDE;
		}
		return Color.WHITE;
	}
}
