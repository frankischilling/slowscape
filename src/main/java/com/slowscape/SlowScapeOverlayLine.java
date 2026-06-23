package com.slowscape;

import java.util.Objects;

final class SlowScapeOverlayLine
{
	private final String left;
	private final String right;

	SlowScapeOverlayLine(String left, String right)
	{
		this.left = left;
		this.right = right;
	}

	String getLeft()
	{
		return left;
	}

	String getRight()
	{
		return right;
	}

	@Override
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}

		if (!(other instanceof SlowScapeOverlayLine))
		{
			return false;
		}

		SlowScapeOverlayLine that = (SlowScapeOverlayLine) other;
		return Objects.equals(left, that.left) && Objects.equals(right, that.right);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(left, right);
	}
}
