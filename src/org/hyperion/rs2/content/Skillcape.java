package org.hyperion.rs2.content;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Equipment;

/**
 * Handles skill cape emotes.
 * 
 * @author Alpinestars from R-S.
 * @author Brown
 */
public class Skillcape {

	/**
	 * Contains all we need to know about skill capes.
	 */
	private static final int[][] SKILLCAPE_DATA = {
			{ Skills.ATTACK, 9747, 9748, 4959, 823 },
			{ Skills.DEFENCE, 9753, 9754, 4961, 824 },
			{ Skills.STRENGTH, 9750, 9751, 4981, 828 },
			{ Skills.HITPOINTS, 9768, 9769, 4971, 833 },
			{ Skills.RANGE, 9756, 9757, 4973, 832 },
			{ Skills.PRAYER, 9759, 9760, 4979, 829 },
			{ Skills.MAGIC, 9762, 9763, 4939, 813 },
			{ Skills.COOKING, 9801, 9802, 4955, 821 },
			{ Skills.WOODCUTTING, 9807, 9808, 4957, 822 },
			{ Skills.FLETCHING, 9783, 9784, 4937, 812 },
			{ Skills.FISHING, 9798, 9799, 4951, 819 },
			{ Skills.FIREMAKING, 9804, 9805, 4975, 831 },
			{ Skills.CRAFTING, 9780, 9781, 4949, 818 },
			{ Skills.SMITHING, 9795, 9796, 4943, 815 },
			{ Skills.MINING, 9792, 9793, 4941, 814 },
			{ Skills.HERBLORE, 9774, 9775, 4969, 835 },
			{ Skills.AGILITY, 9771, 9772, 4977, 830 },
			{ Skills.THIEVING, 9777, 9778, 4965, 826 },
			{ Skills.SLAYER, 9786, 9787, 4967, 827 },
			{ Skills.FARMING, 9810, 9811, 4963, 825 },
			{ Skills.RUNECRAFTING, 9765, 9766, 4947, 817 },
			{ Skills.HUNTER, 9948, 9949, 5158, 907 },
			{ Skills.CONSTRUCTION, 9789, 9790, 4953, 820 },
			{ -1, 9813, -1, 4945, 816 } };

	/**
	 * Handles a skill cape emote: checks appropriate levels, finds the correct
	 * animation + graphic, etc.
	 * 
	 * @param player
	 *            The player wanting to do an emote.
	 */
	public final static void performEmote(final Player player) {
		final Item cape = player.getEquipment().get(Equipment.SLOT_CAPE);
		if (cape != null) {
			for (int i = 0; i < SKILLCAPE_DATA.length; i++) {
				if ((SKILLCAPE_DATA[i][1] == cape.getId())
						|| (SKILLCAPE_DATA[i][2] == cape.getId())) {
					if (SKILLCAPE_DATA[i][0] == -1
							|| player.getSkills().getLevelForExperience(
									SKILLCAPE_DATA[i][0]) >= 99) {
						player.getWalkingQueue().reset();
						player.setCanWalk(false);
						World.getWorld().submit(new Event(3000) {// TODO: Get
																	// real
																	// emote
																	// lengths.
																	// :s

									@Override
									public void execute() {
										player.setCanWalk(true);
										this.stop();
									}

								});
						player.playAnimation(Animation
								.create(SKILLCAPE_DATA[i][3]));
						player.playGraphics(Graphic
								.create(SKILLCAPE_DATA[i][4]));
						return;
					} else {
						player.getActionSender()
								.sendMessage(
										"You need to be level 99 "
												+ Skills.SKILL_NAME[SKILLCAPE_DATA[i][0]]
												+ " to do the "
												+ cape.getDefinition()
														.getName() + " emote.");
						return;
					}
				}
			}
		}
		player.getActionSender()
				.sendMessage(
						"You need to be wearing a skillcape to do the skillcape emote.");
	}

}
