package com.slowscape;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "SlowScape"
)
public class SlowScapePlugin extends Plugin
{
	@Override
	protected void startUp() throws Exception
	{
		log.debug("SlowScape started");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("SlowScape stopped");
	}

	@Provides
	SlowScapeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SlowScapeConfig.class);
	}
}
