package com.slowscape;

import java.lang.reflect.Modifier;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SlowScapeConfigAccessibilityTest
{
	@Test
	public void configEnumReturnTypesArePublic()
	{
		assertTrue(Modifier.isPublic(SlowScapeMeridiem.class.getModifiers()));
	}
}
