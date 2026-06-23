package com.slowscape;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SlowScapeBlockedActionOverlayTest
{
	@Test
	public void usesWideAlertPanelForCenteredText()
	{
		SlowScapeBlockedActionOverlay overlay = new SlowScapeBlockedActionOverlay(null, null);

		assertEquals(520, overlay.getPreferredSize().width);
	}
}
