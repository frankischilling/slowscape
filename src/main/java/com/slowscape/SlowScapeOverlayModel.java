package com.slowscape;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class SlowScapeOverlayModel
{
	private static final DateTimeFormatter UNLOCK_FORMATTER = DateTimeFormatter
		.ofPattern("MMM d h:mm a", Locale.US);

	private final ZoneId zoneId;

	SlowScapeOverlayModel(ZoneId zoneId)
	{
		this.zoneId = zoneId;
	}

	List<SlowScapeOverlayLine> lines(
		SlowScapeDailyRecord record,
		boolean override,
		boolean showUnlockTimer,
		Instant now,
		Instant nextUnlock)
	{
		List<SlowScapeOverlayLine> lines = new ArrayList<>();
		boolean spent = record.hasSpentAction();

		lines.add(new SlowScapeOverlayLine("Date", record.getDate()));
		lines.add(new SlowScapeOverlayLine("Status", override ? "Override" : spent ? "Spent" : "Available"));
		lines.add(new SlowScapeOverlayLine("Actions", Integer.toString(record.getActionCount())));

		if (showUnlockTimer)
		{
			lines.add(new SlowScapeOverlayLine("Unlocks", UNLOCK_FORMATTER.format(nextUnlock.atZone(zoneId))));
			lines.add(new SlowScapeOverlayLine("Time left", formatRemaining(now, nextUnlock)));
		}

		if (override && record.getActionCount() > 1)
		{
			lines.add(new SlowScapeOverlayLine("Extra used", Integer.toString(record.getActionCount() - 1)));
		}

		if (spent)
		{
			lines.add(new SlowScapeOverlayLine("First", record.getFirstAction()));
		}

		return lines;
	}

	private static String formatRemaining(Instant now, Instant nextUnlock)
	{
		Duration duration = Duration.between(now, nextUnlock);
		if (duration.isNegative() || duration.isZero())
		{
			return "0m";
		}

		long totalMinutes = duration.toMinutes();
		long days = totalMinutes / (24 * 60);
		long hours = (totalMinutes % (24 * 60)) / 60;
		long minutes = totalMinutes % 60;
		if (days > 0)
		{
			return days + "d " + hours + "h";
		}
		if (hours > 0)
		{
			return hours + "h " + minutes + "m";
		}
		return minutes + "m";
	}
}
