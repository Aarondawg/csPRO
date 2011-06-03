package org.hyperion.rs2.content.quest;

import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.content.Dialogue;
import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.content.quest.impl.*;
import org.hyperion.rs2.model.Player;

public class QuestHandler {

	private static final Map<Integer, Quest> quests = new HashMap<Integer, Quest>();;

	public static Quest getQuest(int id) {
		return getQuests().get(id);
	}

	// All non-member quests thanks to BloodIsle.
	public static int configIds[] = { 130, 29, 222, 31, 176, 32, 62, 160, 122,
			71, 273, 107, 144, 63, 179, 146, 178, 67 };
	public static int configCompleteValues[] = { 4, 2, 3, 100, 10, 3, 6, 2, 7,
			4, 110, 5, 100, 6, 21, 4, 3, 3 };

	public static void sendQuestInterface(Player player, int questId) {
		Quest quest = getQuest(questId);
		String name;
		String[] lines;
		if (quest == null) {
			name = "Sorry";
			lines = new String[] {
					"Sorry, this quest is not available at the moment.",
					"If you feel like this is an important quest, please",
					"request it at our forums: ", Constants.FORUMS, "",
					"Currently available quests are: ", "Lost City" };
		} else {
			name = quest.getQuestName();
			lines = quest.getQuestLines(player);
		}
		player.getActionSender().sendInterface(275);
		player.getActionSender().sendString(name, 275, 2);
		for (int i = 0; i < 133; i++) {
			player.getActionSender().sendString("", 275, (i + 4));
		}
		for (int i = 0; i < lines.length; i++) {
			player.getActionSender().sendString(lines[i], 275, (i + 4));
		}
	}

	public static Dialogue getDialougeForQuestStage(DialogueLoader dl,
			int questId, int stage) {
		// Simply double loop this with like an array of quest id's, if more
		// npcs on the same quest :)
		Dialogue dia = null;
		if (dl.getQuestId() == questId) {
			for (Dialogue d : dl.getDialouges()) {
				if (d.getQuestStage() <= stage && d.getQuestStage() != -1) {
					dia = d;
				}
			}
		}
		return dia;
	}

	/**
	 * Lights up the started quests, and sends the players quest points.
	 */
	public static void sendQuestLogin(Player player) {
		for (Quest quest : getQuests().values()) {
			player.getActionSender().sendConfig(
					quest.getConfigId(),
					quest.isFinished(player) ? quest.getConfigValue() : quest
							.isStarted(player) ? 1 : 0);
		}
		player.getActionSender().sendConfig(101, player.getQuestPoints());
	}

	public static Map<Integer, Quest> getQuests() {
		return quests;
	}

	static {
		quests.put(0, new TutorialIsland());
		quests.put(14, new BlackKnightsFortress());
		quests.put(83, new LostCity());
		quests.put(34, new BetweenARock());
	}

	public static boolean isQuestEquipItem(Player player, int wearId) {
		Quest quest = null;
		for (int item : LostCity.LOST_CITY_WEARABLE_ITEMS) {
			if (wearId == item) {
				quest = quests.get(83);
				break;
			}
		}
		if (quest != null) {
			if (quest.isFinished(player)) {
				return false;
			} else {
				player.getActionSender().sendMessage(
						"You need to complete the " + quest.getQuestName()
								+ " quest in order to wear this item!");
				return true;
			}
		}
		return false;
	}
}
