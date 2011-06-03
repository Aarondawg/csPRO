package org.hyperion.rs2.content.quest;

import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;

public interface Quest {

	public void dialogueEnded(Player player);

	public void getDialogueForQuestStage(DialogueLoader dl, Player player,
			NPC npc);

	public boolean isStarted(Player player);

	public boolean isFinished(Player player);

	public String[] getQuestLines(Player player);

	public String getQuestName();

	public int getConfigId();

	public int getConfigValue();

	public void handleDialogueActions(Player player,
			DialogueLoader dialogueLoader, int nextDialogueId);

	public boolean handleObjectClicking(Player player, int objectId,
			Location loc, int option);

}
