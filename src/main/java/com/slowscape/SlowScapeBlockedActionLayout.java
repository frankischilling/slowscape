package com.slowscape;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class SlowScapeBlockedActionLayout
{
	static final int MIN_WIDTH = 260;
	static final int MAX_WIDTH = 520;
	static final int HORIZONTAL_PADDING = 12;
	static final int VERTICAL_PADDING = 9;
	static final int LINE_GAP = 2;

	private final int width;
	private final int height;
	private final List<Line> lines;

	private SlowScapeBlockedActionLayout(int width, int height, List<Line> lines)
	{
		this.width = width;
		this.height = height;
		this.lines = Collections.unmodifiableList(new ArrayList<>(lines));
	}

	static SlowScapeBlockedActionLayout from(SlowScapeBlockAlertView view, FontMetrics metrics)
	{
		List<String> rawLines = new ArrayList<>();
		rawLines.add(view.getTitle());
		rawLines.add(view.getReason());
		if (!view.getDetail().isEmpty())
		{
			rawLines.add(view.getDetail());
		}

		int maxContentWidth = MAX_WIDTH - (HORIZONTAL_PADDING * 2);
		int desiredContentWidth = 0;
		for (String line : rawLines)
		{
			desiredContentWidth = Math.max(desiredContentWidth, Math.min(metrics.stringWidth(line), maxContentWidth));
		}

		int width = Math.min(MAX_WIDTH, Math.max(MIN_WIDTH, desiredContentWidth + (HORIZONTAL_PADDING * 2)));
		int contentWidth = width - (HORIZONTAL_PADDING * 2);
		List<Line> fittedLines = new ArrayList<>();
		for (String line : rawLines)
		{
			String fitted = SlowScapeTextFitter.fit(line, metrics, contentWidth);
			if (!fitted.isEmpty())
			{
				fittedLines.add(new Line(fitted));
			}
		}

		int lineCount = fittedLines.size();
		int height = (VERTICAL_PADDING * 2) + (metrics.getHeight() * lineCount);
		if (lineCount > 1)
		{
			height += LINE_GAP * (lineCount - 1);
		}

		return new SlowScapeBlockedActionLayout(width, height, fittedLines);
	}

	int getWidth()
	{
		return width;
	}

	int getHeight()
	{
		return height;
	}

	int getContentWidth()
	{
		return width - (HORIZONTAL_PADDING * 2);
	}

	List<Line> getLines()
	{
		return lines;
	}

	static final class Line
	{
		private final String text;

		private Line(String text)
		{
			this.text = text;
		}

		String getText()
		{
			return text;
		}
	}
}
