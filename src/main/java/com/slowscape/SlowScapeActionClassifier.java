package com.slowscape;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.runelite.api.MenuAction;

final class SlowScapeActionClassifier
{
	private static final Set<String> COUNTED_WIDGET_VERBS = new HashSet<>(Arrays.asList(
		"buy",
		"sell",
		"withdraw",
		"deposit",
		"make",
		"smith",
		"cook",
		"spin",
		"claim",
		"collect",
		"cast",
		"teleport",
		"equip",
		"wear",
		"wield",
		"eat",
		"drink",
		"bury",
		"drop",
		"use",
		"clean",
		"fill",
		"empty",
		"craft",
		"fletch",
		"smelt",
		"tan",
		"pay",
		"trade",
		"store",
		"take",
		"place",
		"remove"
	));

	private static final Set<String> PRODUCTION_CONTINUATION_VERBS = new HashSet<>(Arrays.asList(
		"make",
		"make-x",
		"make-all",
		"smith",
		"cook",
		"spin",
		"craft",
		"fletch",
		"smelt",
		"tan"
	));

	SlowScapeActionType classify(MenuAction action, String option, boolean itemOp)
	{
		if (action == null)
		{
			return SlowScapeActionType.FREE;
		}

		switch (action)
		{
			case WALK:
			case CANCEL:
			case EXAMINE_OBJECT:
			case EXAMINE_NPC:
			case EXAMINE_ITEM_GROUND:
			case EXAMINE_ITEM:
			case EXAMINE_WORLD_ENTITY:
			case SET_HEADING:
			case RUNELITE:
			case RUNELITE_HIGH_PRIORITY:
			case RUNELITE_LOW_PRIORITY:
			case RUNELITE_OVERLAY:
			case RUNELITE_OVERLAY_CONFIG:
			case RUNELITE_INFOBOX:
			case RUNELITE_WIDGET:
			case RUNELITE_PLAYER:
				return SlowScapeActionType.FREE;
			case WIDGET_CONTINUE:
			case WIDGET_CLOSE:
			case WIDGET_TARGET:
			case ITEM_USE:
				return SlowScapeActionType.CONTINUATION;
			case GAME_OBJECT_FIRST_OPTION:
			case GAME_OBJECT_SECOND_OPTION:
			case GAME_OBJECT_THIRD_OPTION:
			case GAME_OBJECT_FOURTH_OPTION:
			case GAME_OBJECT_FIFTH_OPTION:
			case NPC_FIRST_OPTION:
			case NPC_SECOND_OPTION:
			case NPC_THIRD_OPTION:
			case NPC_FOURTH_OPTION:
			case NPC_FIFTH_OPTION:
			case GROUND_ITEM_FIRST_OPTION:
			case GROUND_ITEM_SECOND_OPTION:
			case GROUND_ITEM_THIRD_OPTION:
			case GROUND_ITEM_FOURTH_OPTION:
			case GROUND_ITEM_FIFTH_OPTION:
			case WORLD_ENTITY_FIRST_OPTION:
			case WORLD_ENTITY_SECOND_OPTION:
			case WORLD_ENTITY_THIRD_OPTION:
			case WORLD_ENTITY_FOURTH_OPTION:
			case WORLD_ENTITY_FIFTH_OPTION:
			case PLAYER_FIRST_OPTION:
			case PLAYER_SECOND_OPTION:
			case PLAYER_THIRD_OPTION:
			case PLAYER_FOURTH_OPTION:
			case PLAYER_FIFTH_OPTION:
			case PLAYER_SIXTH_OPTION:
			case PLAYER_SEVENTH_OPTION:
			case PLAYER_EIGHTH_OPTION:
			case WIDGET_TARGET_ON_GAME_OBJECT:
			case WIDGET_TARGET_ON_NPC:
			case WIDGET_TARGET_ON_GROUND_ITEM:
			case WIDGET_TARGET_ON_PLAYER:
			case WIDGET_TARGET_ON_WIDGET:
			case ITEM_USE_ON_GAME_OBJECT:
			case ITEM_USE_ON_NPC:
			case ITEM_USE_ON_GROUND_ITEM:
			case ITEM_USE_ON_PLAYER:
			case ITEM_USE_ON_ITEM:
			case WIDGET_USE_ON_ITEM:
				return SlowScapeActionType.COUNTED;
			case CC_OP:
			case CC_OP_LOW_PRIORITY:
			case WIDGET_FIRST_OPTION:
			case WIDGET_SECOND_OPTION:
			case WIDGET_THIRD_OPTION:
			case WIDGET_FOURTH_OPTION:
			case WIDGET_FIFTH_OPTION:
			case WIDGET_TYPE_1:
			case WIDGET_TYPE_4:
			case WIDGET_TYPE_5:
			case ITEM_FIRST_OPTION:
			case ITEM_SECOND_OPTION:
			case ITEM_THIRD_OPTION:
			case ITEM_FOURTH_OPTION:
			case ITEM_FIFTH_OPTION:
				return itemOp || hasCountedWidgetVerb(option) ? SlowScapeActionType.COUNTED : SlowScapeActionType.FREE;
			default:
				return SlowScapeActionType.FREE;
		}
	}

	boolean isProductionContinuation(MenuAction action, String option)
	{
		if (action != MenuAction.CC_OP
			&& action != MenuAction.CC_OP_LOW_PRIORITY
			&& action != MenuAction.WIDGET_FIRST_OPTION
			&& action != MenuAction.WIDGET_SECOND_OPTION
			&& action != MenuAction.WIDGET_THIRD_OPTION
			&& action != MenuAction.WIDGET_FOURTH_OPTION
			&& action != MenuAction.WIDGET_FIFTH_OPTION
			&& action != MenuAction.WIDGET_TYPE_1
			&& action != MenuAction.WIDGET_TYPE_4
			&& action != MenuAction.WIDGET_TYPE_5)
		{
			return false;
		}

		return PRODUCTION_CONTINUATION_VERBS.contains(firstToken(option));
	}

	private static boolean hasCountedWidgetVerb(String option)
	{
		return COUNTED_WIDGET_VERBS.contains(firstToken(option));
	}

	private static String firstToken(String option)
	{
		if (option == null)
		{
			return "";
		}

		String normalized = option
			.replace('\u00a0', ' ')
			.trim()
			.toLowerCase(Locale.ROOT);
		int space = normalized.indexOf(' ');
		return space == -1 ? normalized : normalized.substring(0, space);
	}
}
