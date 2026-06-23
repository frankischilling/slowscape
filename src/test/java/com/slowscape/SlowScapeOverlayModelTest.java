package com.slowscape;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SlowScapeOverlayModelTest
{
	private final SlowScapeOverlayModel model = new SlowScapeOverlayModel(ZoneId.of("UTC"));

	@Test
	public void usesShortDateLabelAndShowsUnlockTimer()
	{
		SlowScapeDailyRecord record = SlowScapeDailyRecord.of("2026-06-23", 1, "Talk-to Hans", "2026-06-23T10:00:00Z", "Talk-to Hans");
		List<SlowScapeOverlayLine> lines = model.lines(record, false, true,
			Instant.parse("2026-06-23T22:30:00Z"),
			Instant.parse("2026-06-24T00:00:00Z"));

		assertTrue(lines.contains(new SlowScapeOverlayLine("Date", "2026-06-23")));
		assertTrue(lines.contains(new SlowScapeOverlayLine("Unlocks", "Jun 24 12:00 AM")));
		assertTrue(lines.contains(new SlowScapeOverlayLine("Time left", "1h 30m")));
		assertFalse(lines.contains(new SlowScapeOverlayLine("Today", "2026-06-23")));
	}

	@Test
	public void showsExtraActionsWhenOverrideHasBeenUsed()
	{
		SlowScapeDailyRecord record = SlowScapeDailyRecord.of("2026-06-23", 3, "Talk-to Hans", "2026-06-23T10:00:00Z", "Chop Tree");

		List<SlowScapeOverlayLine> lines = model.lines(record, true, false,
			Instant.parse("2026-06-23T22:30:00Z"),
			Instant.parse("2026-06-24T00:00:00Z"));

		assertEquals("Override", lines.get(1).getRight());
		assertTrue(lines.contains(new SlowScapeOverlayLine("Extra used", "2")));
	}
}
