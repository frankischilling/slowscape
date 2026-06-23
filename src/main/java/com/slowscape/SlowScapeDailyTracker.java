package com.slowscape;

import java.time.Instant;
import java.time.LocalDate;
import java.util.function.Supplier;

final class SlowScapeDailyTracker
{
	private final SlowScapeStateStore stateStore;
	private final Supplier<LocalDate> todaySupplier;
	private final Supplier<Instant> nowSupplier;

	SlowScapeDailyTracker(SlowScapeStateStore stateStore, Supplier<LocalDate> todaySupplier, Supplier<Instant> nowSupplier)
	{
		this.stateStore = stateStore;
		this.todaySupplier = todaySupplier;
		this.nowSupplier = nowSupplier;
	}

	SlowScapeActionGateResult trySpend(String actionSummary, boolean allowExtraActionsToday)
	{
		LocalDate today = todaySupplier.get();
		SlowScapeDailyRecord record = loadToday(today);
		if (!record.hasSpentAction())
		{
			SlowScapeDailyRecord updated = record.recordAction(today, nowSupplier.get(), actionSummary);
			stateStore.save(updated);
			return SlowScapeActionGateResult.allowed(updated, false);
		}

		if (allowExtraActionsToday)
		{
			SlowScapeDailyRecord updated = record.recordAction(today, nowSupplier.get(), actionSummary);
			stateStore.save(updated);
			return SlowScapeActionGateResult.allowed(updated, true);
		}

		return SlowScapeActionGateResult.blocked(record);
	}

	boolean recordIfUnspent(String actionSummary)
	{
		LocalDate today = todaySupplier.get();
		SlowScapeDailyRecord record = loadToday(today);
		if (record.hasSpentAction())
		{
			return false;
		}

		stateStore.save(record.recordAction(today, nowSupplier.get(), actionSummary));
		return true;
	}

	boolean hasSpentToday()
	{
		return loadToday(todaySupplier.get()).hasSpentAction();
	}

	SlowScapeDailyRecord snapshot()
	{
		return loadToday(todaySupplier.get());
	}

	void resetToday()
	{
		stateStore.save(SlowScapeDailyRecord.empty().forDate(todaySupplier.get()));
	}

	private SlowScapeDailyRecord loadToday(LocalDate today)
	{
		SlowScapeDailyRecord record = stateStore.load();
		SlowScapeDailyRecord todayRecord = record.forDate(today);
		if (!todayRecord.isForDate(today) || !todayRecord.getDate().equals(record.getDate()))
		{
			stateStore.save(todayRecord);
		}
		return todayRecord;
	}
}
