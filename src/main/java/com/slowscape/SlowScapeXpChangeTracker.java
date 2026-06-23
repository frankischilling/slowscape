package com.slowscape;

import java.util.EnumMap;
import net.runelite.api.Skill;

final class SlowScapeXpChangeTracker
{
	private final EnumMap<Skill, Integer> xpBySkill = new EnumMap<>(Skill.class);

	boolean observe(Skill skill, int xp)
	{
		Integer previousXp = xpBySkill.put(skill, xp);
		return previousXp != null && xp > previousXp;
	}

	void reset()
	{
		xpBySkill.clear();
	}
}
