package com.slowscape;

import java.time.Instant;
import java.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SlowScapeDailyTrackerTest
{
	private final MemoryStateStore stateStore = new MemoryStateStore();
	private LocalDate today = LocalDate.of(2026, 6, 23);
	private Instant now = Instant.parse("2026-06-23T16:00:00Z");

	@Test
	public void firstCountedActionSpendsToday()
	{
		SlowScapeDailyTracker tracker = tracker();

		SlowScapeActionGateResult result = tracker.trySpend("Talk-to Hans", false);

		assertTrue(result.isAllowed());
		assertFalse(result.isBlocked());
		assertEquals(1, tracker.snapshot().getActionCount());
		assertEquals("2026-06-23", tracker.snapshot().getDate());
		assertEquals("Talk-to Hans", tracker.snapshot().getFirstAction());
		assertEquals("Talk-to Hans", tracker.snapshot().getLastAction());
	}

	@Test
	public void secondCountedActionOnSameDayIsBlocked()
	{
		SlowScapeDailyTracker tracker = tracker();
		tracker.trySpend("Talk-to Hans", false);

		SlowScapeActionGateResult result = tracker.trySpend("Chop down Tree", false);

		assertFalse(result.isAllowed());
		assertTrue(result.isBlocked());
		assertEquals(1, tracker.snapshot().getActionCount());
		assertEquals("Talk-to Hans", tracker.snapshot().getLastAction());
	}

	@Test
	public void overrideAllowsExtraActionsButKeepsCounting()
	{
		SlowScapeDailyTracker tracker = tracker();
		tracker.trySpend("Talk-to Hans", false);

		SlowScapeActionGateResult result = tracker.trySpend("Chop down Tree", true);

		assertTrue(result.isAllowed());
		assertFalse(result.isBlocked());
		assertTrue(result.isOverrideActive());
		assertEquals(2, tracker.snapshot().getActionCount());
		assertEquals("Chop down Tree", tracker.snapshot().getLastAction());
	}

	@Test
	public void nextLocalDateStartsFresh()
	{
		SlowScapeDailyTracker tracker = tracker();
		tracker.trySpend("Talk-to Hans", false);
		today = LocalDate.of(2026, 6, 24);
		now = Instant.parse("2026-06-24T12:00:00Z");

		SlowScapeActionGateResult result = tracker.trySpend("Chop down Tree", false);

		assertTrue(result.isAllowed());
		assertFalse(result.isBlocked());
		assertEquals(1, tracker.snapshot().getActionCount());
		assertEquals("2026-06-24", tracker.snapshot().getDate());
		assertEquals("Chop down Tree", tracker.snapshot().getFirstAction());
	}

	@Test
	public void zeroActionRecordKeepsItsDate()
	{
		SlowScapeDailyRecord record = SlowScapeDailyRecord.of("2026-06-23", 0, "", "", "");

		assertEquals("2026-06-23", record.getDate());
		assertEquals(0, record.getActionCount());
	}

	@Test
	public void resetClearsActionStateForToday()
	{
		SlowScapeDailyTracker tracker = tracker();
		tracker.trySpend("Talk-to Hans", false);

		tracker.resetToday();

		assertEquals("2026-06-23", tracker.snapshot().getDate());
		assertEquals(0, tracker.snapshot().getActionCount());
		assertEquals("", tracker.snapshot().getFirstAction());
		assertEquals("", tracker.snapshot().getLastAction());
	}

	private SlowScapeDailyTracker tracker()
	{
		return new SlowScapeDailyTracker(stateStore, () -> today, () -> now);
	}

	private static final class MemoryStateStore implements SlowScapeStateStore
	{
		private SlowScapeDailyRecord record = SlowScapeDailyRecord.empty();

		@Override
		public SlowScapeDailyRecord load()
		{
			return record;
		}

		@Override
		public void save(SlowScapeDailyRecord record)
		{
			this.record = record;
		}
	}
}
