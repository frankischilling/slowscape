package com.slowscape;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

final class SlowScapeUnlockSchedule
{
	static final String DEFAULT_UNLOCK_TIME = "12:00";

	private final ZoneId zoneId;

	SlowScapeUnlockSchedule(ZoneId zoneId)
	{
		this.zoneId = zoneId;
	}

	LocalDate currentPeriodDate(Instant now, String configuredUnlockTime)
	{
		return currentPeriodDate(now, configuredUnlockTime, SlowScapeMeridiem.AM);
	}

	LocalDate currentPeriodDate(Instant now, String configuredUnlockTime, SlowScapeMeridiem meridiem)
	{
		ZonedDateTime zonedNow = now.atZone(zoneId);
		LocalTime unlockTime = parseUnlockTime(configuredUnlockTime, meridiem);
		if (zonedNow.toLocalTime().isBefore(unlockTime))
		{
			return zonedNow.toLocalDate().minusDays(1);
		}

		return zonedNow.toLocalDate();
	}

	Instant nextUnlock(Instant now, String configuredUnlockTime)
	{
		return nextUnlock(now, configuredUnlockTime, SlowScapeMeridiem.AM);
	}

	Instant nextUnlock(Instant now, String configuredUnlockTime, SlowScapeMeridiem meridiem)
	{
		ZonedDateTime zonedNow = now.atZone(zoneId);
		LocalTime unlockTime = parseUnlockTime(configuredUnlockTime, meridiem);
		ZonedDateTime nextUnlock = zonedNow.toLocalDate().atTime(unlockTime).atZone(zoneId);
		if (!nextUnlock.toInstant().isAfter(now))
		{
			nextUnlock = nextUnlock.plusDays(1);
		}

		return nextUnlock.toInstant();
	}

	private static LocalTime parseUnlockTime(String configuredUnlockTime, SlowScapeMeridiem meridiem)
	{
		if (configuredUnlockTime == null || configuredUnlockTime.trim().isEmpty())
		{
			return LocalTime.MIDNIGHT;
		}

		String value = configuredUnlockTime.trim();
		String[] parts = value.split(":");
		if (parts.length != 2)
		{
			return LocalTime.MIDNIGHT;
		}

		int hour;
		int minute;
		try
		{
			hour = Integer.parseInt(parts[0]);
			minute = Integer.parseInt(parts[1]);
		}
		catch (NumberFormatException ex)
		{
			return LocalTime.MIDNIGHT;
		}

		if (minute < 0 || minute > 59)
		{
			return LocalTime.MIDNIGHT;
		}

		if (hour >= 0 && hour <= 23 && (hour == 0 || hour > 12))
		{
			return LocalTime.of(hour, minute);
		}

		if (hour < 1 || hour > 12)
		{
			return LocalTime.MIDNIGHT;
		}

		SlowScapeMeridiem effectiveMeridiem = meridiem == null ? SlowScapeMeridiem.AM : meridiem;
		if (effectiveMeridiem == SlowScapeMeridiem.AM)
		{
			return LocalTime.of(hour == 12 ? 0 : hour, minute);
		}

		return LocalTime.of(hour == 12 ? 12 : hour + 12, minute);
	}
}
