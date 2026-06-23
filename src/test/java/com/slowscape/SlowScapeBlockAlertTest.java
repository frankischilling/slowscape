package com.slowscape;

import java.time.Duration;
import java.time.Instant;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SlowScapeBlockAlertTest
{
	private final SlowScapeBlockAlert alert = new SlowScapeBlockAlert();
	private final Instant now = Instant.parse("2026-06-23T18:00:00Z");

	@Test
	public void alertIsVisibleUntilDurationExpires()
	{
		alert.show("Today's action is already spent.", now, Duration.ofSeconds(6));

		assertTrue(alert.isVisible(now.plusSeconds(5)));
		assertEquals("Today's action is already spent.", alert.getMessage(now.plusSeconds(5)));
		assertFalse(alert.isVisible(now.plusSeconds(6)));
		assertEquals("", alert.getMessage(now.plusSeconds(6)));
	}

	@Test
	public void laterAlertRefreshesMessageAndExpiry()
	{
		alert.show("First block.", now, Duration.ofSeconds(6));
		alert.show("Second block.", now.plusSeconds(5), Duration.ofSeconds(6));

		assertTrue(alert.isVisible(now.plusSeconds(10)));
		assertEquals("Second block.", alert.getMessage(now.plusSeconds(10)));
	}

	@Test
	public void clearHidesAlert()
	{
		alert.show("Blocked.", now, Duration.ofSeconds(6));

		alert.clear();

		assertFalse(alert.isVisible(now));
		assertEquals("", alert.getMessage(now));
	}
}
