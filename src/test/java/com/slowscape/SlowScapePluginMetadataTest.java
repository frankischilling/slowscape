package com.slowscape;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SlowScapePluginMetadataTest
{
	@Test
	public void pluginCanBeConstructed()
	{
		assertNotNull(new SlowScapePlugin());
	}

	@Test
	public void configTypeIsAvailable()
	{
		assertNotNull(SlowScapeConfig.class);
	}
}
