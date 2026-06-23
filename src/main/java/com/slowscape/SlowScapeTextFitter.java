package com.slowscape;

import java.awt.FontMetrics;

final class SlowScapeTextFitter
{
	private static final String ELLIPSIS = "...";

	private SlowScapeTextFitter()
	{
	}

	static String fit(String value, FontMetrics metrics, int maxWidth)
	{
		String text = value == null ? "" : value.trim();
		if (text.isEmpty() || maxWidth <= 0)
		{
			return "";
		}
		if (metrics.stringWidth(text) <= maxWidth)
		{
			return text;
		}
		if (metrics.stringWidth(ELLIPSIS) > maxWidth)
		{
			return "";
		}

		int low = 0;
		int high = text.length();
		while (low < high)
		{
			int middle = (low + high + 1) / 2;
			String candidate = text.substring(0, middle).trim() + ELLIPSIS;
			if (metrics.stringWidth(candidate) <= maxWidth)
			{
				low = middle;
			}
			else
			{
				high = middle - 1;
			}
		}

		String prefix = text.substring(0, low).trim();
		return prefix.isEmpty() ? "" : prefix + ELLIPSIS;
	}
}
