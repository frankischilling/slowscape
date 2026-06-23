package com.slowscape;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SlowScapeBlockAlertViewTest
{
	@Test
	public void formatsBlockedMessageWithFirstActionDetail()
	{
		SlowScapeBlockAlertView view = SlowScapeBlockAlertView.from("Already spent on: Talk-to Hans");

		assertEquals("Action blocked", view.getTitle());
		assertEquals("Today's action is already spent", view.getReason());
		assertEquals("First action: Talk-to Hans", view.getDetail());
	}

	@Test
	public void formatsBlockedMessageWithoutFirstActionDetail()
	{
		SlowScapeBlockAlertView view = SlowScapeBlockAlertView.from("Already spent");

		assertEquals("Action blocked", view.getTitle());
		assertEquals("Today's action is already spent", view.getReason());
		assertEquals("", view.getDetail());
	}

	@Test
	public void shortensLongFirstActionDetail()
	{
		SlowScapeBlockAlertView view = SlowScapeBlockAlertView.from(
			"Already spent on: Use Extremely Long Item Name on Another Extremely Long Target Name");

		assertEquals("First action: Use Extremely Long Item Name on Another...", view.getDetail());
	}
}
