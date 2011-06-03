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

public class BetweenARock implements Quest {

	private static final int QUEST_INDEX = 7;
	private static final int MAIN_QUEST_STAGE_INDEX = 0;
	private static final int QUEST_ID = 34;

	@Override
	public void dialogueEnded(Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getConfigId() {
		return 433;
	}

	@Override
	public int getConfigValue() {
		return 112;
	}

	@Override
	public void getDialogueForQuestStage(DialogueLoader dl, Player player,
			NPC npc) {
		int stage = player.getQuestInfo()[QUEST_INDEX][MAIN_QUEST_STAGE_INDEX];
		DialogueLoader.handleQuestDialogue(player, dl,
				QuestHandler.getDialougeForQuestStage(dl, QUEST_ID, stage));
		// Now, this method will pop up with the dialogue having the correct
		// quest stage.
	}

	@Override
	public String[] getQuestLines(Player player) {
		return new String[] { "Fail." };
	}

	@Override
	public String getQuestName() {
		return "Between a Rock";
	}

	@Override
	public void handleDialogueActions(Player player,
			DialogueLoader dialogueLoader, int nextDialogueId) {
		Dialogue dialogue = null;
		switch (nextDialogueId) {

		}
		if (dialogue != null) {
			DialogueLoader.dialogue(player,
					dialogue.getType() == Type.PLAYER ? player : new NPC(
							NPCDefinition.forId(dialogueLoader.getNpcId())),
					dialogue.getEmotion(), dialogue.getLines());
			player.setCurrentDialogueLoader(dialogueLoader);
		}

	}

	@Override
	public boolean handleObjectClicking(Player player, int objectId,
			Location loc, int option) {
		return false;
	}

	@Override
	public boolean isFinished(Player player) {
		return false;
	}

	@Override
	public boolean isStarted(Player player) {
		return false;
	}

}
