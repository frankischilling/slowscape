package com.slowscape;

import java.time.Duration;
import java.time.Instant;

final class SlowScapeBlockAlert
{
	private String message = "";
	private Instant expiresAt = Instant.MIN;

	void show(String message, Instant now, Duration duration)
	{
		this.message = message == null ? "" : message;
		this.expiresAt = now.plus(duration);
	}

	boolean isVisible(Instant now)
	{
		return now.isBefore(expiresAt) && !message.isEmpty();
	}

	String getMessage(Instant now)
	{
		return isVisible(now) ? message : "";
	}

	void clear()
	{
		message = "";
		expiresAt = Instant.MIN;
	}
}
