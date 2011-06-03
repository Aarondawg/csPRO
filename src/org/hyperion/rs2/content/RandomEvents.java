package org.hyperion.rs2.content;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

public class RandomEvents {

	/**
	 * The skill id's sorted by the GenieLamp interface button id's - 3. ;)
	 */
	private static final int[] GENIE_LAMP_SKILL_IDS = { Skills.ATTACK,
			Skills.STRENGTH, Skills.RANGE, Skills.MAGIC, Skills.DEFENCE,
			Skills.HITPOINTS, Skills.PRAYER, Skills.AGILITY, Skills.HERBLORE,
			Skills.THIEVING, Skills.CRAFTING, Skills.RUNECRAFTING,
			Skills.MINING, Skills.SMITHING, Skills.FISHING, Skills.COOKING,
			Skills.FIREMAKING, Skills.WOODCUTTING, Skills.FLETCHING,
			Skills.SLAYER, Skills.FARMING, Skills.CONSTRUCTION, Skills.HUNTER, };

	/**
	 * Handles the genie lamp button clicking.
	 */
	public static void handleGenieLamp(Player player, int buttonId) {
		if (buttonId == 26) { // Confirm.
			if (player.getGenieLampIndex() == -1) {
				player.getActionSender()
						.sendMessage(
								"You need to select a skill before confirming your wish!");
				return;
			}
			final int skillId = GENIE_LAMP_SKILL_IDS[player.getGenieLampIndex()];
			player.getActionSender().sendMessage(
					"The lamp grants you " + 10
							* player.getSkills().getLevel(skillId)
							* Skills.EXP_RATE + " "
							+ Skills.SKILL_NAME[skillId] + " XP!");
			player.getSkills().addExperience(skillId,
					(10 * player.getSkills().getLevel(skillId)));
			player.getInventory().remove(new Item(2528));
			player.getActionSender().sendCloseInterface();
			player.getActionSender().sendConfig(261, 0);
			player.setGenieLampIndex(-1);
		} else {
			player.setGenieLampIndex(buttonId - 3);
			player.getActionSender().sendConfig(261, buttonId - 2);
		}
	}

}
