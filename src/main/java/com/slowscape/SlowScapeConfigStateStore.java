package com.slowscape;

import net.runelite.client.config.ConfigManager;

final class SlowScapeConfigStateStore implements SlowScapeStateStore
{
	static final String GROUP = "slowscape";
	private static final String KEY_STATE_DATE = "stateDate";
	private static final String KEY_ACTION_COUNT = "actionCount";
	private static final String KEY_FIRST_ACTION = "firstAction";
	private static final String KEY_FIRST_ACTION_AT = "firstActionAt";
	private static final String KEY_LAST_ACTION = "lastAction";

	private final ConfigManager configManager;

	SlowScapeConfigStateStore(ConfigManager configManager)
	{
		this.configManager = configManager;
	}

	@Override
	public SlowScapeDailyRecord load()
	{
		return SlowScapeDailyRecord.of(
			configManager.getRSProfileConfiguration(GROUP, KEY_STATE_DATE),
			parseInt(configManager.getRSProfileConfiguration(GROUP, KEY_ACTION_COUNT)),
			configManager.getRSProfileConfiguration(GROUP, KEY_FIRST_ACTION),
			configManager.getRSProfileConfiguration(GROUP, KEY_FIRST_ACTION_AT),
			configManager.getRSProfileConfiguration(GROUP, KEY_LAST_ACTION));
	}

	@Override
	public void save(SlowScapeDailyRecord record)
	{
		if (record == null || record.getDate().isEmpty())
		{
			configManager.unsetRSProfileConfiguration(GROUP, KEY_STATE_DATE);
			configManager.unsetRSProfileConfiguration(GROUP, KEY_ACTION_COUNT);
			configManager.unsetRSProfileConfiguration(GROUP, KEY_FIRST_ACTION);
			configManager.unsetRSProfileConfiguration(GROUP, KEY_FIRST_ACTION_AT);
			configManager.unsetRSProfileConfiguration(GROUP, KEY_LAST_ACTION);
			return;
		}

		configManager.setRSProfileConfiguration(GROUP, KEY_STATE_DATE, record.getDate());
		configManager.setRSProfileConfiguration(GROUP, KEY_ACTION_COUNT, Integer.toString(record.getActionCount()));
		configManager.setRSProfileConfiguration(GROUP, KEY_FIRST_ACTION, record.getFirstAction());
		configManager.setRSProfileConfiguration(GROUP, KEY_FIRST_ACTION_AT, record.getFirstActionAt());
		configManager.setRSProfileConfiguration(GROUP, KEY_LAST_ACTION, record.getLastAction());
	}

	private static int parseInt(String value)
	{
		if (value == null || value.trim().isEmpty())
		{
			return 0;
		}

		try
		{
			return Integer.parseInt(value.trim());
		}
		catch (NumberFormatException ex)
		{
			return 0;
		}
	}
}
