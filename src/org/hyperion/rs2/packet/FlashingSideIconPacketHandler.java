package org.hyperion.rs2.packet;

import org.hyperion.rs2.content.quest.impl.TutorialIsland;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

public class FlashingSideIconPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int icon = packet.get();
		switch (icon) {
		/*
		 * "Settings" - When you first walk with the Tutorial Guide..
		 */
		case 11:
			player.editQuestInfo(TutorialIsland.QUEST_INFO_INDEX,
					TutorialIsland.MAIN_QUEST_STAGE_INDEX, 1); // Hopefully the
																// stage is
																// currently 0,
																// and we set it
																// to 1.
			// DialogueLoader.getNextDialogue(player,
			// player.getCurrentDialogueLoader(), 7);
			break;
		/*
		 * We were just given the tinderbox / axe..
		 */
		case 3:
			player.getActionSender().sendObjectHints(
					Location.create(3100, 3096, 140), 2);
			player.getActionSender().sendMessage("Cut down a tree.");
			break;
		/*
		 * We just created our first fire.
		 */
		case 1:
			TutorialIsland.sendHeadIcon(player, 943);
			break;
		/*
		 * We just cooked a bread..
		 */
		case 13:
			player.getActionSender().sendMessage(
					"This is your music tab, which might be handled later on.");
			player.getActionSender().sendObjectHints(
					Location.create(3073, 3090, 130), 6);
			break;
		/*
		 * We'd just exit the chefs house..
		 */
		case 12:
			player.getActionSender()
					.sendMessage(
							"This is your emote tab, which can be used to express warious emotions.");
			TutorialIsland.sendHeadIcon(player, 949);
			player.getActionSender().sendMessage(
					"Follow this path to meet your next instructor.");
			break;
		/*
		 * Quest panel..
		 */
		case 2:
			TutorialIsland.sendHeadIcon(player, 949);
			player.editQuestInfo(TutorialIsland.QUEST_INFO_INDEX,
					TutorialIsland.MAIN_QUEST_STAGE_INDEX, 13);
			player.getActionSender().sendMessage("This is your Quest Journal.");
			break;
		/*
		 * Attacking type
		 */
		case 0:
			player.getActionSender()
					.sendMessage(
							"In this tab, you can set your current fighting styles, along with other features.");
			player.getActionSender().sendSidebarInterface(4, 387);
			player.getActionSender().sendFlashingTab(4);
			break;
		case 4:
			player.getActionSender()
					.sendMessage(
							"And this is your equipment screen. All worn equipment will be shown here.");
			player.editQuestInfo(TutorialIsland.QUEST_INFO_INDEX,
					TutorialIsland.MAIN_QUEST_STAGE_INDEX, 24);
			break;
		}

	}

}
