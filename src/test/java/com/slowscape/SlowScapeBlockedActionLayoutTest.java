package com.slowscape;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SlowScapeBlockedActionLayoutTest
{
	@Test
	public void usesMinimumWidthForShortMessages()
	{
		SlowScapeBlockedActionLayout layout = SlowScapeBlockedActionLayout.from(
			SlowScapeBlockAlertView.from("Already spent"),
			metrics());

		assertEquals(260, layout.getWidth());
	}

	@Test
	public void keepsEveryLineInsideTheBox()
	{
		FontMetrics metrics = metrics();
		SlowScapeBlockedActionLayout layout = SlowScapeBlockedActionLayout.from(
			SlowScapeBlockAlertView.from(
				"Already spent on: Exchange Ignisia with Extraordinarily Long Target Name"),
			metrics);

		for (SlowScapeBlockedActionLayout.Line line : layout.getLines())
		{
			assertTrue(metrics.stringWidth(line.getText()) <= layout.getContentWidth());
		}
	}

	private static FontMetrics metrics()
	{
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			return graphics.getFontMetrics();
		}
		finally
		{
			graphics.dispose();
		}
	}
}
