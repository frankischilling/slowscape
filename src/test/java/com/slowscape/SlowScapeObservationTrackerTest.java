package com.slowscape;

import net.runelite.api.Skill;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SlowScapeObservationTrackerTest
{
	@Test
	public void firstXpSyncAfterLoginDoesNotCountAsGain()
	{
		SlowScapeXpChangeTracker tracker = new SlowScapeXpChangeTracker();

		assertFalse(tracker.observe(Skill.ATTACK, 1200));
		assertFalse(tracker.observe(Skill.STRENGTH, 400));
	}

	@Test
	public void laterXpIncreaseCountsAsGain()
	{
		SlowScapeXpChangeTracker tracker = new SlowScapeXpChangeTracker();
		tracker.observe(Skill.ATTACK, 1200);

		assertTrue(tracker.observe(Skill.ATTACK, 1230));
		assertFalse(tracker.observe(Skill.ATTACK, 1230));
	}

	@Test
	public void resetForLoginForgetsOldXpBaselines()
	{
		SlowScapeXpChangeTracker tracker = new SlowScapeXpChangeTracker();
		tracker.observe(Skill.ATTACK, 1200);
		tracker.reset();

		assertFalse(tracker.observe(Skill.ATTACK, 1200));
	}

	@Test
	public void firstSnapshotSyncAfterLoginDoesNotCountAsChange()
	{
		SlowScapeSnapshotChangeTracker tracker = new SlowScapeSnapshotChangeTracker();

		assertFalse(tracker.observe("995:100;"));
	}

	@Test
	public void laterSnapshotDifferenceCountsAsChange()
	{
		SlowScapeSnapshotChangeTracker tracker = new SlowScapeSnapshotChangeTracker();
		tracker.observe("995:100;");

		assertTrue(tracker.observe("995:101;"));
		assertFalse(tracker.observe("995:101;"));
	}
}
