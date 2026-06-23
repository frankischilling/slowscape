package com.slowscape;

final class SlowScapeActionGateResult
{
	private final boolean allowed;
	private final boolean blocked;
	private final boolean overrideActive;
	private final SlowScapeDailyRecord record;

	private SlowScapeActionGateResult(boolean allowed, boolean blocked, boolean overrideActive, SlowScapeDailyRecord record)
	{
		this.allowed = allowed;
		this.blocked = blocked;
		this.overrideActive = overrideActive;
		this.record = record;
	}

	static SlowScapeActionGateResult allowed(SlowScapeDailyRecord record, boolean overrideActive)
	{
		return new SlowScapeActionGateResult(true, false, overrideActive, record);
	}

	static SlowScapeActionGateResult blocked(SlowScapeDailyRecord record)
	{
		return new SlowScapeActionGateResult(false, true, false, record);
	}

	boolean isAllowed()
	{
		return allowed;
	}

	boolean isBlocked()
	{
		return blocked;
	}

	boolean isOverrideActive()
	{
		return overrideActive;
	}

	SlowScapeDailyRecord getRecord()
	{
		return record;
	}
}
