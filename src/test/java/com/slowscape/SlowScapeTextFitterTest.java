package com.slowscape;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SlowScapeTextFitterTest
{
	@Test
	public void keepsTextThatAlreadyFits()
	{
		FontMetrics metrics = metrics();

		assertEquals("Talk-to Hans", SlowScapeTextFitter.fit("Talk-to Hans", metrics, 400));
	}

	@Test
	public void shortensTextToFitPixelWidth()
	{
		FontMetrics metrics = metrics();
		String text = "First action: Use Extremely Long Item Name on Another Extremely Long Target Name";
		int maxWidth = metrics.stringWidth("First action: Use Extremely...");

		String fitted = SlowScapeTextFitter.fit(text, metrics, maxWidth);

		assertTrue(fitted.endsWith("..."));
		assertTrue(fitted.length() < text.length());
		assertTrue(metrics.stringWidth(fitted) <= maxWidth);
	}

	@Test
	public void removesTextWhenEllipsisWillNotFit()
	{
		FontMetrics metrics = metrics();

		assertEquals("", SlowScapeTextFitter.fit("Talk-to Hans", metrics, metrics.stringWidth("..")));
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
