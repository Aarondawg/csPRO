package org.hyperion.rs2.content.quest.impl;

import org.hyperion.rs2.content.Dialogue;
import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.content.DialogueLoader.Type;
import org.hyperion.rs2.content.quest.Quest;
import org.hyperion.rs2.content.quest.QuestHandler;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;

public class BlackKnightsFortress implements Quest {

	private static final int QUEST_INFO_INDEX = 1;// That thing gets info from
													// an array in player.java
	private static final int QUEST_STAGE_INDEX = 0;
	private static final int MAX_STAGE = 5000;// TODO: Fix this lel
	private static final int QUEST_ID = 14;

	private static final String[][] questLines = {
			{
					"I can start this quest by speaking to Sir Amik Varze at the ",
					"White Knights' castle in Falador",
					"I must have a total of at least 13 Quest Points.",
					"I would have an advantage if I could fight Level 33 Knights",
					"and if I had a Smithing of level 26." },
			{ "I should go check up on the Black Knights Fortress." }, { "" },
			{ "" }, { "" }, { "" }, { "" }, { "" }, };

	@Override
	public void dialogueEnded(Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getConfigId() {
		return 130;
	}

	@Override
	public int getConfigValue() {
		return 4;
	}

	@Override
	public String[] getQuestLines(Player player) {
		return questLines[player.getQuestInfo()[QUEST_INFO_INDEX][QUEST_STAGE_INDEX]];
	}

	@Override
	public String getQuestName() {
		return "Black Knights Fortress";
	}

	@Override
	public void handleDialogueActions(Player player,
			DialogueLoader dialogueLoader, int nextDialogueId) {
		Dialogue dialogue = null;
		switch (nextDialogueId) {
		case 7:// We just accepted the quest.
			player.editQuestInfo(QUEST_INFO_INDEX, QUEST_STAGE_INDEX, 1); // Hopefully
																			// the
																			// stage
																			// is
																			// currently
																			// 0,
																			// and
																			// we
																			// set
																			// it
																			// to
																			// 1.
			dialogue = dialogueLoader.getDialouges()[8]; // We play dialogue
															// number 8.
			player.setNextDialogueIds(new int[] { 9 }); // We set the next
														// dialogue to 9.
			break;
		}
		DialogueLoader.dialogue(player,
				dialogue.getType() == Type.PLAYER ? player : new NPC(
						NPCDefinition.forId(dialogueLoader.getNpcId())),
				dialogue.getEmotion(), dialogue.getLines());
		player.setCurrentDialogueLoader(dialogueLoader);
	}

	@Override
	public boolean isFinished(Player player) {
		int[][] stages = player.getQuestInfo();
		return stages[QUEST_INFO_INDEX][QUEST_STAGE_INDEX] == MAX_STAGE;
	}

	@Override
	public boolean isStarted(Player player) {
		int[][] stages = player.getQuestInfo();
		return stages[QUEST_INFO_INDEX][QUEST_STAGE_INDEX] != 0;
	}

	@Override
	public void getDialogueForQuestStage(DialogueLoader dl, Player player,
			NPC npc) {
		int[][] stages = player.getQuestInfo();
		DialogueLoader.handleQuestDialogue(player, dl, QuestHandler
				.getDialougeForQuestStage(dl, QUEST_ID,
						stages[QUEST_INFO_INDEX][0]));
	}

	@Override
	public boolean handleObjectClicking(Player player, int objectId,
			Location loc, int option) {
		return false;
	}

}
