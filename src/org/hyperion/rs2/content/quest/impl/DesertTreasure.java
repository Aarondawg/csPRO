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

public class DesertTreasure implements Quest {

	private static final int QUEST_ID = 45;
	private static final int QUEST_INFO_INDEX = 2;
	private static final int QUEST_STAGE_INDEX = 0;
	private static final int MAX_STAGE = 50000; // REDO WHEN WE'RE DONE WRITING
												// THE QUEST.

	/**
	 * The lines going in the quest info thing.
	 */
	private static final String[][] QUEST_LINES = { {}, };

	@Override
	public void dialogueEnded(Player player) {
		// Probably not going to happen anything inhere.

	}

	@Override
	public int getConfigId() {
		return 440;
	}

	@Override
	public int getConfigValue() {
		return 15;
	}

	@Override
	public void getDialogueForQuestStage(DialogueLoader dl, Player player,
			NPC npc) {
		// ALRIGHT! This is getting kinda tricky, because theres multiple paths.
		// So you should switch the npc.getId(), and get different "stages" from
		// the info int[][]
		// Ask me if you're in trouble. (Most likely, because this explanation
		// sucks).
		int[][] info = player.getQuestInfo();
		DialogueLoader.handleQuestDialogue(player, dl, QuestHandler
				.getDialougeForQuestStage(dl, QUEST_ID,
						info[QUEST_INFO_INDEX][0]));

	}

	@Override
	public String[] getQuestLines(Player player) {
		int stage = player.getQuestInfo()[QUEST_INFO_INDEX][QUEST_STAGE_INDEX];
		return QUEST_LINES[stage];
	}

	@Override
	public String getQuestName() {
		return "Desert Treasure";
	}

	@Override
	public void handleDialogueActions(Player player,
			DialogueLoader dialogueLoader, int nextDialogueId) {
		Dialogue dialogue = null;
		switch (nextDialogueId) { // CAREFULL!!!! THIS WAS RIPPED FROM THE BLACK
									// KNIGHTS FORTRESS XD
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
	public boolean handleObjectClicking(Player player, int objectId,
			Location loc, int option) {
		// TODO Auto-generated method stub
		return false;
	}

}
