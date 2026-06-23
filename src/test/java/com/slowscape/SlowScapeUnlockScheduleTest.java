package com.slowscape;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SlowScapeUnlockScheduleTest
{
	private final SlowScapeUnlockSchedule schedule = new SlowScapeUnlockSchedule(ZoneId.of("America/New_York"));

	@Test
	public void beforeConfiguredUnlockUsesPreviousPeriodDate()
	{
		Instant now = Instant.parse("2026-06-23T12:00:00Z");

		assertEquals(LocalDate.of(2026, 6, 22), schedule.currentPeriodDate(now, "9:30", SlowScapeMeridiem.AM));
		assertEquals(Instant.parse("2026-06-23T13:30:00Z"), schedule.nextUnlock(now, "9:30", SlowScapeMeridiem.AM));
	}

	@Test
	public void afterConfiguredUnlockUsesCurrentPeriodDate()
	{
		Instant now = Instant.parse("2026-06-23T14:00:00Z");

		assertEquals(LocalDate.of(2026, 6, 22), schedule.currentPeriodDate(now, "9:30", SlowScapeMeridiem.PM));
		assertEquals(Instant.parse("2026-06-24T01:30:00Z"), schedule.nextUnlock(now, "9:30", SlowScapeMeridiem.PM));
	}

	@Test
	public void noonAndMidnightUseExpectedTwelveHourMeaning()
	{
		SlowScapeUnlockSchedule utcSchedule = new SlowScapeUnlockSchedule(ZoneId.of("UTC"));
		Instant now = Instant.parse("2026-06-23T13:00:00Z");

		assertEquals(LocalDate.of(2026, 6, 23), utcSchedule.currentPeriodDate(now, "12:00", SlowScapeMeridiem.PM));
		assertEquals(Instant.parse("2026-06-24T12:00:00Z"), utcSchedule.nextUnlock(now, "12:00", SlowScapeMeridiem.PM));
		assertEquals(Instant.parse("2026-06-24T00:00:00Z"), utcSchedule.nextUnlock(now, "12:00", SlowScapeMeridiem.AM));
	}

	@Test
	public void invalidUnlockTimeFallsBackToMidnight()
	{
		SlowScapeUnlockSchedule utcSchedule = new SlowScapeUnlockSchedule(ZoneId.of("UTC"));
		Instant now = Instant.parse("2026-06-23T01:00:00Z");

		assertEquals(LocalDate.of(2026, 6, 23), utcSchedule.currentPeriodDate(now, "not a time", SlowScapeMeridiem.PM));
		assertEquals(Instant.parse("2026-06-24T00:00:00Z"), utcSchedule.nextUnlock(now, "not a time", SlowScapeMeridiem.PM));
	}

	@Test
	public void oldTwentyFourHourConfigValuesStillWork()
	{
		SlowScapeUnlockSchedule utcSchedule = new SlowScapeUnlockSchedule(ZoneId.of("UTC"));
		Instant now = Instant.parse("2026-06-23T20:00:00Z");

		assertEquals(LocalDate.of(2026, 6, 22), utcSchedule.currentPeriodDate(now, "21:30", SlowScapeMeridiem.AM));
		assertEquals(Instant.parse("2026-06-23T21:30:00Z"), utcSchedule.nextUnlock(now, "21:30", SlowScapeMeridiem.AM));
	}
}
