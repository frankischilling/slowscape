package com.slowscape;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;

final class SlowScapeBlockedActionOverlay extends OverlayPanel
{
	private static final Color BACKGROUND = new Color(45, 27, 24, 235);
	private static final Color BORDER = new Color(28, 16, 14, 245);
	private static final Color BLOCKED = new Color(255, 126, 92);
	private static final Color TEXT = new Color(245, 239, 232);
	private static final Color MUTED = new Color(218, 190, 178);
	private static final int ALERT_WIDTH = 520;

	private final SlowScapePlugin plugin;
	private final SlowScapeConfig config;

	@Inject
	SlowScapeBlockedActionOverlay(SlowScapePlugin plugin, SlowScapeConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_CENTER);
		setPreferredSize(new Dimension(ALERT_WIDTH, 0));
		setResizable(false);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showBlockedActionAlert())
		{
			return null;
		}

		String message = plugin.getBlockedActionAlertMessage(Instant.now());
		if (message.isEmpty())
		{
			return null;
		}
		SlowScapeBlockAlertView view = SlowScapeBlockAlertView.from(message);
		SlowScapeBlockedActionLayout layout = SlowScapeBlockedActionLayout.from(view, graphics.getFontMetrics());

		graphics.setColor(BACKGROUND);
		graphics.fillRect(0, 0, layout.getWidth(), layout.getHeight());
		graphics.setColor(BORDER);
		graphics.drawRect(0, 0, layout.getWidth() - 1, layout.getHeight() - 1);

		FontMetrics metrics = graphics.getFontMetrics();
		int baseline = SlowScapeBlockedActionLayout.VERTICAL_PADDING + metrics.getAscent();
		for (int i = 0; i < layout.getLines().size(); i++)
		{
			SlowScapeBlockedActionLayout.Line line = layout.getLines().get(i);
			Color color = i == 0 ? BLOCKED : i == 1 ? TEXT : MUTED;
			drawCenteredText(graphics, line.getText(), color, baseline, layout.getWidth());
			baseline += metrics.getHeight() + SlowScapeBlockedActionLayout.LINE_GAP;
		}

		return new Dimension(layout.getWidth(), layout.getHeight());
	}

	private static void drawCenteredText(Graphics2D graphics, String text, Color color, int baseline, int width)
	{
		FontMetrics metrics = graphics.getFontMetrics();
		int x = (width - metrics.stringWidth(text)) / 2;
		drawShadowedText(graphics, text, color, x, baseline);
	}

	private static void drawShadowedText(Graphics2D graphics, String text, Color color, int x, int baseline)
	{
		graphics.setColor(Color.BLACK);
		graphics.drawString(text, x + 1, baseline + 1);
		graphics.setColor(color);
		graphics.drawString(text, x, baseline);
	}
}
