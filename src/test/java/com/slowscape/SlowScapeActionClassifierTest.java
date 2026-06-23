package com.slowscape;

import net.runelite.api.MenuAction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SlowScapeActionClassifierTest
{
	private final SlowScapeActionClassifier classifier = new SlowScapeActionClassifier();

	@Test
	public void walkingCancelAndExamineAreFree()
	{
		assertEquals(SlowScapeActionType.FREE, classifier.classify(MenuAction.WALK, "Walk here", false));
		assertEquals(SlowScapeActionType.FREE, classifier.classify(MenuAction.CANCEL, "Cancel", false));
		assertEquals(SlowScapeActionType.FREE, classifier.classify(MenuAction.EXAMINE_OBJECT, "Examine", false));
		assertEquals(SlowScapeActionType.FREE, classifier.classify(MenuAction.EXAMINE_NPC, "Examine", false));
		assertEquals(SlowScapeActionType.FREE, classifier.classify(MenuAction.EXAMINE_ITEM_GROUND, "Examine", false));
	}

	@Test
	public void dialogueCloseAndTargetSelectionAreContinuations()
	{
		assertEquals(SlowScapeActionType.CONTINUATION, classifier.classify(MenuAction.WIDGET_CONTINUE, "Continue", false));
		assertEquals(SlowScapeActionType.CONTINUATION, classifier.classify(MenuAction.WIDGET_CLOSE, "Close", false));
		assertEquals(SlowScapeActionType.CONTINUATION, classifier.classify(MenuAction.WIDGET_TARGET, "Use", false));
	}

	@Test
	public void runeliteMenuActionsAreFree()
	{
		assertEquals(SlowScapeActionType.FREE, classifier.classify(MenuAction.RUNELITE, "Mark", false));
		assertEquals(SlowScapeActionType.FREE, classifier.classify(MenuAction.RUNELITE_OVERLAY, "Configure", false));
		assertEquals(SlowScapeActionType.FREE, classifier.classify(MenuAction.RUNELITE_WIDGET, "Reset", false));
	}

	@Test
	public void worldEntityAndItemTargetActionsAreCounted()
	{
		assertEquals(SlowScapeActionType.COUNTED, classifier.classify(MenuAction.GAME_OBJECT_FIRST_OPTION, "Chop down", false));
		assertEquals(SlowScapeActionType.COUNTED, classifier.classify(MenuAction.NPC_FIRST_OPTION, "Talk-to", false));
		assertEquals(SlowScapeActionType.COUNTED, classifier.classify(MenuAction.GROUND_ITEM_FIRST_OPTION, "Take", false));
		assertEquals(SlowScapeActionType.COUNTED, classifier.classify(MenuAction.WORLD_ENTITY_FIRST_OPTION, "Enter", false));
		assertEquals(SlowScapeActionType.COUNTED, classifier.classify(MenuAction.WIDGET_TARGET_ON_GAME_OBJECT, "Use", false));
		assertEquals(SlowScapeActionType.COUNTED, classifier.classify(MenuAction.WIDGET_TARGET_ON_NPC, "Cast", false));
	}

	@Test
	public void itemOpsAndActionProducingWidgetsAreCounted()
	{
		assertEquals(SlowScapeActionType.COUNTED, classifier.classify(MenuAction.CC_OP, "Eat", true));
		assertEquals(SlowScapeActionType.COUNTED, classifier.classify(MenuAction.CC_OP, "Drop", true));
		assertEquals(SlowScapeActionType.COUNTED, classifier.classify(MenuAction.CC_OP, "Buy 1", false));
		assertEquals(SlowScapeActionType.COUNTED, classifier.classify(MenuAction.CC_OP, "Smith", false));
		assertEquals(SlowScapeActionType.COUNTED, classifier.classify(MenuAction.WIDGET_FIRST_OPTION, "Collect", false));
	}

	@Test
	public void unknownInterfaceActionsAreFreeByDefault()
	{
		assertEquals(SlowScapeActionType.FREE, classifier.classify(MenuAction.CC_OP, "View guide", false));
		assertEquals(SlowScapeActionType.FREE, classifier.classify(MenuAction.WIDGET_SECOND_OPTION, "Sort", false));
	}
}
