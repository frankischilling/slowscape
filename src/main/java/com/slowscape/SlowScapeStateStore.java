package com.slowscape;

interface SlowScapeStateStore
{
	SlowScapeDailyRecord load();

	void save(SlowScapeDailyRecord record);
}
