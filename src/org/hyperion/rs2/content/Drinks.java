package org.hyperion.rs2.content;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Duel;

public class Drinks {

	// Those are the same, cept for super/normal
	private static int[] SUPER_POTIONS = { 2436, 145, 147, 149, 229, 2440, 157,
			159, 161, 229, 2442, 163, 165, 167, 229 };
	private static int[] POTIONS = { 2428, 121, 123, 125, 229, 113, 115, 117,
			119, 229, 2432, 133, 135, 137, 229 };
	// Magic (4) (3) (2) (1) (0) //Ranged (1) (2) (3) (4) (0) //Prayer (4) (3)
	// (2) (1) (0) //Sara brew. (1) (2) (3) (4) (0) //Restore //Super restore
	// //Anti pots
	private static int[] OTHERS = { 3040, 3042, 3044, 3046, 229, 2444, 169,
			171, 173, 229, 2434, 139, 141, 143, 229, 6685, 6687, 6689, 6691,
			229, 2430, 127, 129, 131, 229, 3024, 3026, 3028, 3030, 229, 2446,
			175, 177, 179, 229 };

	public static boolean getPotions(Player player, int potion, int slot) {
		for (int i = 0; i < POTIONS.length; i++) {// We take the length of
													// SUPER_POTIONS, even
													// though we could have used
													// POTIONS.
			if (SUPER_POTIONS[i] == potion) {
				drink(player, potion, slot, SUPER_POTIONS[++i], getStat(i),
						(int) (5.0 + Math.floor(player.getSkills()
								.getLevelForExperience(getStat(i))) * 0.15),
						false);
				return true;
			} else if (POTIONS[i] == potion) {
				drink(player, potion, slot, POTIONS[++i], getStat(i),
						(int) (3.0 + Math.floor(player.getSkills()
								.getLevelForExperience(getStat(i))) * 0.10),
						false);
				return true;
			}
		}
		for (int i = 0; i < OTHERS.length; i++) {
			if (OTHERS[i] == potion) {
				/*
				 * Make sure we're aloud to drink before we continue.
				 */
				if (player.getRequestManager().isDueling()) {
					if (player.getRequestManager().getDuel()
							.isRuleToggled(Duel.NO_DRINKS)) {
						player.getActionSender().sendMessage(
								"Drinking has been disabled in this duel!");
						return true;
					}
				}
				if (i <= 4) {// Magic potions.
					drink(player, potion, slot, OTHERS[++i], Skills.MAGIC,
							(int) Math.round(4.0 + Math.floor(player
									.getSkills().getLevelForExperience(
											Skills.MAGIC)) * 0.0), false);
					return true;
				}
				if (i <= 9) {// Ranged potions.
					drink(player, potion, slot, OTHERS[++i], Skills.RANGE,
							(int) Math.round(4.0 + Math.floor(player
									.getSkills().getLevelForExperience(
											Skills.RANGE)) * 0.1), false);
					return true;
				}
				if (i <= 14) {// Prayer
					int amount = (int) Math.round(Math.floor(player.getSkills()
							.getLevelForExperience(Skills.RANGE) * 0.25) + 7);
					if ((amount + player.getSkills().getLevel(Skills.PRAYER)) > player
							.getSkills().getLevelForExperience(Skills.PRAYER)) {
						amount = (player.getSkills().getLevelForExperience(
								Skills.PRAYER) - player.getSkills().getLevel(
								Skills.PRAYER));
					}
					prayerPotion(player, potion, slot, OTHERS[++i],
							Skills.PRAYER, amount);// THANKS TIP.IT!
					return true;
				}
				if (i <= 19) {// Sara brew
					saraBrew(player, potion, slot, OTHERS[++i]);// THANKS
																// TIP.IT!
					return true;
				}
				if (i <= 24) {// Restore
					restore(player, potion, slot, OTHERS[++i], false);// THANKS
																		// TIP.IT!
					return true;
				}
				if (i <= 28) {// Super restore
					restore(player, potion, slot, OTHERS[++i], true);// THANKS
																		// TIP.IT!
					return true;
				}
				if (i <= 33) {// Anti poison potion!
					drink(player, potion, slot, OTHERS[++i], 0, -1, true);
					// player.poisonHit = 0;
					return true;
				}
				return true;
			}
		}
		return false;
	}

	private static void restore(final Player player, int potion, int slot,
			int replace, boolean isSuperRes) {
		/*
		 * if(player.getTemporaryAttribute("DuelDrinks") != null) {
		 * player.getActionSender
		 * ().sendMessage("Drinking has been disabled in this duel!"); return; }
		 */
		if (System.currentTimeMillis() - player.getLastDrink() < 1500) {
			return;
		}
		Item item = player.getInventory().get(slot);
		final int[] COMBAT_SKILLS = { Skills.ATTACK, Skills.STRENGTH,
				Skills.DEFENCE, Skills.MAGIC, Skills.RANGE };
		if (item.getId() == potion && item.getCount() == 1) {
			player.getInventory().set(slot, null);
			player.getActionSender().sendMessage(
					"You drink the "
							+ item.getDefinition().getName().toLowerCase()
							+ ".");
			player.setLastDrink(System.currentTimeMillis());
			if (replace != -1) {
				player.getInventory().set(slot, new Item(replace, 1));
			}
		}
		player.playAnimation(Animation.create(829));
		if (isSuperRes) {
			World.getWorld().submit(new Event(1500) {
				public void execute() {
					for (int i = 0; i < Skills.SKILL_COUNT; i++) {
						if (!(i == Skills.HITPOINTS)) {
							int amount = (int) Math.round(Math
									.floor((player.getSkills()
											.getLevelForExperience(i) * 0.25) + 8));
							if ((amount + player.getSkills().getLevel(i)) > player
									.getSkills().getLevelForExperience(i)) {
								amount = (player.getSkills()
										.getLevelForExperience(i) - player
										.getSkills().getLevel(i));
							}
							player.getSkills().setLevel(i,
									(player.getSkills().getLevel(i) + amount));
						}
					}
					player.getActionSender().sendSkills();
					this.stop();
				}
			});
		} else {
			World.getWorld().submit(new Event(1500) {
				public void execute() {
					for (int i : COMBAT_SKILLS) {
						if (!(i == Skills.HITPOINTS)) {
							int amount = (int) Math.round(Math
									.floor((player.getSkills()
											.getLevelForExperience(i) * 0.3) + 10));
							if ((amount + player.getSkills().getLevel(i)) > player
									.getSkills().getLevelForExperience(i)) {
								amount = (player.getSkills()
										.getLevelForExperience(i) - player
										.getSkills().getLevel(i));
							}
							player.getSkills().setLevel(i,
									(player.getSkills().getLevel(i) + amount));
						}
					}
					player.getActionSender().sendSkills();
					this.stop();
				}
			});
		}
	}

	private static void saraBrew(final Player player, int potion, int slot,
			int replace) {
		/*
		 * if(player.getTemporaryAttribute("DuelDrinks") != null) {
		 * player.getActionSender
		 * ().sendMessage("Drinking has been disabled in this duel!"); return; }
		 */
		if (System.currentTimeMillis() - player.getLastDrink() < 1500)
			return;
		Item item = player.getInventory().get(slot);
		if (item.getId() == potion && item.getCount() == 1) {
			player.getInventory().set(slot, null);
			player.getActionSender().sendMessage(
					"You drink the "
							+ item.getDefinition().getName().toLowerCase()
							+ ".");
			player.setLastDrink(System.currentTimeMillis());
			if (replace != -1) {
				player.getInventory().set(slot, new Item(replace, 1));
			}
		}
		// player.setBusy(true);
		player.playAnimation(Animation.create(829));
		World.getWorld().submit(new Event(1500) {
			public void execute() {
				int healHp = (int) Math.round(Math.floor((player.getSkills()
						.getLevel(Skills.HITPOINTS) * 1.15) + 2));
				if (healHp > (int) Math.round(Math.floor((player.getSkills()
						.getLevelForExperience(Skills.HITPOINTS) * 1.15) + 2))) {
					healHp = (int) Math.round(Math
							.floor((player.getSkills().getLevelForExperience(
									Skills.HITPOINTS) * 1.15) + 2));
				}
				int healDef = (int) Math.round(Math.floor((player.getSkills()
						.getLevel(Skills.DEFENCE) * 1.2) + 2));
				if (healDef > (int) Math.round(Math.floor((player.getSkills()
						.getLevelForExperience(Skills.DEFENCE) * 1.2) + 2))) {
					healDef = (int) Math.round(Math.floor((player.getSkills()
							.getLevelForExperience(Skills.DEFENCE) * 1.2) + 2));
				}
				player.getSkills().setLevel(Skills.DEFENCE, healDef);
				player.getSkills().setLevel(Skills.HITPOINTS, healHp);
				player.getSkills().setLevel(
						Skills.ATTACK,
						(int) Math.round(Math.floor((player.getSkills()
								.getLevel(Skills.ATTACK) * 0.92) - 1)));
				player.getSkills().setLevel(
						Skills.MAGIC,
						(int) Math.round(Math.floor((player.getSkills()
								.getLevel(Skills.MAGIC) * 0.92) - 1)));
				player.getSkills().setLevel(
						Skills.RANGE,
						(int) Math.round(Math.floor((player.getSkills()
								.getLevel(Skills.RANGE) * 0.92) - 1)));
				player.getSkills().setLevel(
						Skills.STRENGTH,
						(int) Math.round(Math.floor((player.getSkills()
								.getLevel(Skills.STRENGTH) * 0.92) - 1)));
				player.getActionSender().sendSkills();
				this.stop();
			}
		});
	}

	/**
	 * Gets the level we want to boost, based on the potionId's position in the
	 * array.
	 */
	private static int getStat(int potionPosition) {
		if (potionPosition <= 4) {
			return Skills.ATTACK;
		}
		if (potionPosition <= 9) {
			return Skills.STRENGTH;
		}
		if (potionPosition <= 14) {
			return Skills.DEFENCE;
		}
		return -1;
	}

	private static void drink(Player player, int id, int slot, int replace,
			int stat, int amount, boolean antiPot) {
		if (player.getRequestManager().isDueling()) {
			if (player.getRequestManager().getDuel()
					.isRuleToggled(Duel.NO_DRINKS)) {
				player.getActionSender().sendMessage(
						"Drinking has been disabled in this duel!");
				return;
			}
		}
		if (System.currentTimeMillis() - player.getLastDrink() < 1500) {
			return;
		}
		Item item = player.getInventory().get(slot);
		if (item.getId() == id && item.getCount() == 1) {
			player.getInventory().set(slot, null);
			player.getActionSender()
					.sendMessage(
							"You drink the "
									+ item.getDefinition().getName()
											.toLowerCase()
									+ "."
									+ (antiPot ? "and your poison has been cured!"
											: ""));
			player.setLastDrink(System.currentTimeMillis());
			if (replace != -1) {
				player.getInventory().set(slot, new Item(replace, 1));
			}
			potionEvent(player, stat, amount);
		}
	}

	/**
	 * Had to create another method for this one, because its using "getLevel"
	 * instead of getLevelForXp, which took me 1 hour to work out.
	 */
	private static void prayerPotion(final Player player, int id, int slot,
			int replace, final int stat, final int amount) {
		if (System.currentTimeMillis() - player.getLastDrink() < 1500)
			return;
		Item item = player.getInventory().get(slot);
		System.out.println(item.getId() + "  " + id);
		if (item.getId() == id && item.getCount() == 1) {
			player.getInventory().set(slot, null);
			player.getActionSender().sendMessage(
					"You drink the "
							+ item.getDefinition().getName().toLowerCase()
							+ ".");
			player.setLastDrink(System.currentTimeMillis());
			if (replace != -1) {
				player.getInventory().set(slot, new Item(replace, 1));
			}
			player.playAnimation(Animation.create(829));
		}
		World.getWorld().submit(new Event(1500) {
			public void execute() {
				player.getSkills().setLevel(stat,
						player.getSkills().getLevel(stat) + amount);
				player.getActionSender().sendSkills();
				this.stop();
			}
		});
	}

	private static void potionEvent(final Player player, final int stat,
			final int amount) {
		player.playAnimation(Animation.create(829));
		World.getWorld().submit(new Event(1500) {
			public void execute() {
				if (stat != -1) {
					player.getSkills().setLevel(
							stat,
							player.getSkills().getLevelForExperience(stat)
									+ amount);
				}
				player.getActionSender().sendSkills();
				this.stop();
			}
		});
	}

}
