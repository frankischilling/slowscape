package com.slowscape;

import java.time.Instant;
import java.time.LocalDate;

final class SlowScapeDailyRecord
{
	private static final SlowScapeDailyRecord EMPTY = new SlowScapeDailyRecord("", 0, "", "", "");

	private final String date;
	private final int actionCount;
	private final String firstAction;
	private final String firstActionAt;
	private final String lastAction;

	private SlowScapeDailyRecord(String date, int actionCount, String firstAction, String firstActionAt, String lastAction)
	{
		this.date = clean(date);
		this.actionCount = Math.max(0, actionCount);
		this.firstAction = clean(firstAction);
		this.firstActionAt = clean(firstActionAt);
		this.lastAction = clean(lastAction);
	}

	static SlowScapeDailyRecord empty()
	{
		return EMPTY;
	}

	static SlowScapeDailyRecord of(String date, int actionCount, String firstAction, String firstActionAt, String lastAction)
	{
		if (date == null || date.trim().isEmpty())
		{
			return empty();
		}

		return new SlowScapeDailyRecord(date, actionCount, firstAction, firstActionAt, lastAction);
	}

	SlowScapeDailyRecord forDate(LocalDate today)
	{
		return isForDate(today) ? this : emptyForDate(today);
	}

	SlowScapeDailyRecord recordAction(LocalDate today, Instant now, String actionSummary)
	{
		String summary = clean(actionSummary);
		if (summary.isEmpty())
		{
			summary = "Counted action";
		}

		String todayString = today.toString();
		String timestamp = now.toString();
		if (actionCount == 0 || !isForDate(today))
		{
			return new SlowScapeDailyRecord(todayString, 1, summary, timestamp, summary);
		}

		return new SlowScapeDailyRecord(date, actionCount + 1, firstAction, firstActionAt, summary);
	}

	boolean isForDate(LocalDate today)
	{
		return today != null && today.toString().equals(date);
	}

	boolean hasSpentAction()
	{
		return actionCount > 0;
	}

	String getDate()
	{
		return date;
	}

	int getActionCount()
	{
		return actionCount;
	}

	String getFirstAction()
	{
		return firstAction;
	}

	String getFirstActionAt()
	{
		return firstActionAt;
	}

	String getLastAction()
	{
		return lastAction;
	}

	private static SlowScapeDailyRecord emptyForDate(LocalDate today)
	{
		return new SlowScapeDailyRecord(today == null ? "" : today.toString(), 0, "", "", "");
	}

	private static String clean(String value)
	{
		return value == null ? "" : value.trim();
	}
}
