package org.hyperion.rs2.content.quest.impl;

import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.content.quest.Quest;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;

public class LunarDiplomacy implements Quest {

	@Override
	public void dialogueEnded(Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getConfigId() {
		return 823;
	}

	@Override
	public int getConfigValue() {
		return 190;
	}

	@Override
	public void getDialogueForQuestStage(DialogueLoader dl, Player player,
			NPC npc) {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] getQuestLines(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuestName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleDialogueActions(Player player,
			DialogueLoader dialogueLoader, int nextDialogueId) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFinished(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStarted(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleObjectClicking(Player player, int objectId,
			Location loc, int option) {
		// TODO Auto-generated method stub
		return false;
	}

}
