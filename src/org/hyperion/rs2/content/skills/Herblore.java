package org.hyperion.rs2.content.skills;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;

public class Herblore {

	private static final Animation makePotions = Animation.create(363);

	public static void cleanHerb(Player player, Item unknown, int unknownSlot,
			int known, double exp) {
		player.getActionSender().sendMessage("You identified the herb.");
		player.getInventory().remove(unknownSlot, unknown);
		player.getInventory().add(new Item(known, 1));
		player.getSkills().addExperience(Skills.HERBLORE, exp);
	}

	private static final Item VIAL = new Item(227);

	public static void unfinishedPotions(final Player player, final Item herb,
			final Item replaceWith) {
		player.playAnimation(makePotions);
		// player.getActionSender().sendSound(2612, 100, 0);
		player.getActionSender().sendMessage(
				"You put the "
						+ herb.getDefinition().getName().replace(" herb", "")
								.toLowerCase() + " into the vial of water."); // Ripped
																				// straight
																				// from
																				// RS
		World.getWorld().submit(new Event(2000) {
			@Override
			public void execute() {
				if (player.getInventory().remove(herb) > 0
						&& player.getInventory().remove(VIAL) > 0) {
					player.getInventory().add(replaceWith);
				}
				this.stop();
			}
		});
		return;
	}

	private static void finishedPotions(final Player player,
			final Item unfinishedPotion, final Item secondIngredient,
			final int finishedPotion, final double exp) {
		player.playAnimation(makePotions);
		// player.getActionSender().sendSound(2612, 100, 0);
		player.getActionSender().sendMessage(
				"You mix your "
						+ secondIngredient.getDefinition().getName()
								.toLowerCase() + " into your potion"); // Ripped
																		// straight
																		// from
																		// RS
		World.getWorld().submit(new Event(2000) {
			@Override
			public void execute() {
				if (player.getInventory().remove(unfinishedPotion) > 0
						&& player.getInventory().remove(secondIngredient) > 0) {
					player.getInventory().add(new Item(finishedPotion, 1));
					player.getSkills().addExperience(Skills.HERBLORE, exp);
				}
				this.stop();
			}
		});

	}

	// Guam, Marrentill, Tarromin, Harralander, Ranarr, Toadflax, Irit, Avantoe,
	// Kwuarm, Snapdragon, Cadantine, Dwarf, Lantadyme, Torstol, Magic Essence
	private static final int[] CLEAN_HERBS = { 249, 251, 253, 255, 257, 2998,
			259, 261, 9017, 3000, 263, 265, 267, 2481, 269 };
	private static final int[] UNFINISHEDPOTIONS = { 91, 93, 95, 97, 99, 3002,
			101, 103, 9019, 3004, 105, 107, 109, 2483, 111 };
	private static final int[] LEVEL_REQUIREMENTS_UNFINISHED = { 3, 5, 12, 22,
			30, 34, 45, 50, 55, 57, 63, 66, 72, 76, 78 };

	// Eye, Unicorn, Limp, Eggs,
	private static final int[] SECOND_INGREDIENTS = { 221, 235, 225, 223, 1975,
			239, 2152, 231, 221, 235, 231, 2970, 225, 9018, 241, 241, 245,
			2483, 111 };
	private static final double[] EXPEREIENCE_TABLE = { 25, 37.5, 50, 62.5,
			67.5, 75, 80, 87.5, 100, 106.3, 112.5, 117.5, 125, 130, 137.5, /*
																			 * 142.5
																			 * Super
																			 * Restore
																			 * ,
																			 *//*
																				 * DEFENCEPOTION150
																				 * ,
																				 */
			157.5, 162.5, 172.5, 175 };

	// Attack, Anti, Strength, Rest, Energy, Def, Agility, Prayer, Sup-attack,
	// sup-anti, fishing, sup-energy, sup-strength, magic-ess, wep-poison,
	// anti-fire, range, magic pot, zamorak brew,
	private static final int[] FINISHED_POTIONS = { 121, 175, 115, 127, 3010,
			113, 111, 139, 145, 181, 151, 3018, 157, 9020, 187, 2454, 169,
			3138, 247, 3042, 189 };

	private static final int[] UNFINISHEDPOTIONS2 = { 91, 93, 95, 97, 97, 99,
			3002, 99, 101, 101, 103, 103, 105, 9019, 105, 2483, 109, 3138, 247 };

	private static final double[] LEVEL_REQUIREMENTS_FINISHED = { 3, 5, 12, 22,
			26, 30, 34, 38, 45, 48, 50, 52, 55, 57, 60, 69, 72, 76, 78 };

	public static boolean handleItemOnItem(Player player, int itemUsed,
			int usedWith) {
		for (int i = 0; i < CLEAN_HERBS.length; i++) {
			if (itemUsed == 227 && usedWith == CLEAN_HERBS[i]
					|| itemUsed == CLEAN_HERBS[i] && usedWith == 227) {
				if (player.getSkills().getLevel(Skills.HERBLORE) > LEVEL_REQUIREMENTS_UNFINISHED[i]) {
					unfinishedPotions(player, new Item(CLEAN_HERBS[i]),
							new Item(UNFINISHEDPOTIONS[i]));
				} else {
					player.getActionSender().sendMessage(
							"You need a Herblore level of "
									+ LEVEL_REQUIREMENTS_UNFINISHED[i]
									+ " in order to make this potion.");
				}
				return true;
			}
		}
		for (int i = 0; i < UNFINISHEDPOTIONS2.length; i++) {
			if (itemUsed == UNFINISHEDPOTIONS2[i]
					&& usedWith == SECOND_INGREDIENTS[i]
					|| itemUsed == SECOND_INGREDIENTS[i]
					&& usedWith == UNFINISHEDPOTIONS2[i]) {
				if (player.getSkills().getLevel(Skills.HERBLORE) > LEVEL_REQUIREMENTS_FINISHED[i]) {
					finishedPotions(player, new Item(UNFINISHEDPOTIONS2[i]),
							new Item(SECOND_INGREDIENTS[i]),
							FINISHED_POTIONS[i], EXPEREIENCE_TABLE[i]);
				} else {
					player.getActionSender().sendMessage(
							"You need a Herblore level of "
									+ LEVEL_REQUIREMENTS_FINISHED[i]
									+ " in order to make this potion.");
				}
				return true;
			}
		}
		return false;
	}

	private static final int[] GRIMMY_HERBS = { 199, 201, 203, 205, 207, 209,
			211, 213, 215, 217, 219, 2485/* Unsure if id is corrent. :( */, 3049 /*
																				 * Unsure
																				 * if
																				 * id
																				 * is
																				 * corrent
																				 * .
																				 * :
																				 * (
																				 */};
	private static final int[] CLEAN_HERBS1 = { 249, 251, 253, 255, 257, 259,
			261, 263, 265, 267, 269, 2481, 3000 };
	private static final double[] EXP_TABLE = { 2.5, 3.8, 5, 6.3, 7.5, 8, 7.8,
			8.8, 10, 11.3, 11.8, 13.1, 12.5, 13.1, 13.8, 15 };

	public static boolean handleItemSelect(Player player, Item item, int slot) {
		for (int i = 0; i < GRIMMY_HERBS.length; i++) {
			if (item.getId() == GRIMMY_HERBS[i]) {
				cleanHerb(player, item, slot, CLEAN_HERBS1[i], EXP_TABLE[i]);
				return true;
			}
		}
		return false;
	}

}
