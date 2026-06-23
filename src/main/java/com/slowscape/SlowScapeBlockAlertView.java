package com.slowscape;

final class SlowScapeBlockAlertView
{
	private static final String FIRST_ACTION_PREFIX = "Already spent on: ";
	private static final int DETAIL_LIMIT = 56;

	private final String title;
	private final String reason;
	private final String detail;

	private SlowScapeBlockAlertView(String title, String reason, String detail)
	{
		this.title = title;
		this.reason = reason;
		this.detail = detail;
	}

	static SlowScapeBlockAlertView from(String message)
	{
		String cleanMessage = message == null ? "" : message.trim();
		String detail = "";
		if (cleanMessage.startsWith(FIRST_ACTION_PREFIX))
		{
			detail = shorten("First action: " + cleanMessage.substring(FIRST_ACTION_PREFIX.length()));
		}

		return new SlowScapeBlockAlertView("Action blocked", "Today's action is already spent", detail);
	}

	String getTitle()
	{
		return title;
	}

	String getReason()
	{
		return reason;
	}

	String getDetail()
	{
		return detail;
	}

	private static String shorten(String value)
	{
		if (value.length() <= DETAIL_LIMIT)
		{
			return value;
		}

		return value.substring(0, DETAIL_LIMIT - 3).trim() + "...";
	}
}
