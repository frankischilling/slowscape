package com.slowscape;

final class SlowScapeSnapshotChangeTracker
{
	private String lastSnapshot;

	boolean observe(String snapshot)
	{
		String current = snapshot == null ? "" : snapshot;
		if (lastSnapshot == null)
		{
			lastSnapshot = current;
			return false;
		}

		if (lastSnapshot.equals(current))
		{
			return false;
		}

		lastSnapshot = current;
		return true;
	}

	void reset()
	{
		lastSnapshot = null;
	}
}
