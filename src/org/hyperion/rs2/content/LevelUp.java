package org.hyperion.rs2.content;

import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

public class LevelUp {

	private static Graphic levelUp = Graphic.create(199, 2);

	/**
	 * Called when a player levels up.
	 * 
	 * @param player
	 *            The player.
	 * @param skill
	 *            The skill id.
	 */
	public static void levelUp(Player player, int skill) {
		player.setTemporaryAttribute("leveledUp", skill);
		player.setTemporaryAttribute("leveledUp[" + skill + "]", Boolean.TRUE);
		player.playGraphics(levelUp);
		player.getActionSender()
				.sendMessage(
						"You've just advanced a "
								+ Skills.SKILL_NAME[skill]
								+ " level! You have reached level "
								+ player.getSkills().getLevelForExperience(
										skill) + ".");
		player.getActionSender()
				.sendLevelUp(
						"Congratulations, you have just advanced a "
								+ Skills.SKILL_NAME[skill] + " level!",
						"You have now reached level "
								+ player.getSkills().getLevelForExperience(
										skill) + ".", SKILL_INTERFACES[skill]);
	}

	public static final int[] SKILL_INTERFACES = { 158, 161, 175, 167, 171,
			170, 168, 159, 177, 165, 164, 163, 160, 174, 169, 166, 157, 176,
			173, 162, 172, 395, 395 };// Needs the hunter interface..? :(

}
