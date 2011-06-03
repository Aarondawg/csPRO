package org.hyperion.rs2.content.skills;

import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.quest.impl.TutorialIsland;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Equipment;

public class Smithing {

	public static final int SMITHING_INTERFACE = 312;
	public static final int SMELTING_INTERFACE = 311;
	private static final Random r = new Random();

	public static class SmeltingAction extends Action {

		private static final Animation SMELTING_ANIMATION = Animation
				.create(899);

		public SmeltingAction(Player player, int mainOre, int secondaryOre,
				double exp, int resultBar, int resultBarAmount,
				int secondaryOreAmount, boolean isIron, boolean requiresTwoOres) {
			super(player, 0);
			this.mainOre = new Item(mainOre);
			this.secondaryOre = new Item(secondaryOre, secondaryOreAmount);
			this.exp = exp;
			this.resultBar = new Item(resultBar);
			this.resultBarAmount = resultBarAmount;
			this.isIron = isIron;
			this.requiresTwoOres = requiresTwoOres;
		}

		public void execute() {
			if (getPlayer().isDead() || resultBarAmount <= 0) {
				this.stop();
				return;
			}

			/**
			 * If player is done with smithing all his bars, then we'll stop the
			 * event, AND return.
			 */
			if (!getPlayer().getInventory().contains(secondaryOre)
					|| !getPlayer().getInventory().contains(mainOre)) {
				getPlayer().getActionSender().sendMessage(
						"You ran out of ores.");
				this.stop();
				return;
			}
			getPlayer().playAnimation(SMELTING_ANIMATION);
			getPlayer().getActionSender().sendMessage(
					"You smelt the "
							+ mainOre.getDefinition().getName().toLowerCase()
							+ " in the furnace.");
			World.getWorld().submit(new Event(1700) {

				public void execute() {
					if (!getPlayer().getInventory().contains(secondaryOre)
							|| !getPlayer().getInventory().contains(mainOre)) {
						// getPlayer().getActionSender().sendMessage("You ran out of ores.");
						// //Message will come in the primary event.
						this.stop();
						return;
					}
					if (isIron && !hasRingOfForgin(getPlayer())) {// Ring of
																	// forging.
						int choice = r.nextInt(2);
						if (choice == 0) {
							getPlayer()
									.getActionSender()
									.sendMessage(
											"The ore is too impure and you fail to refine it.");
							getPlayer().getInventory().remove(mainOre);
						} else {
							getPlayer().getActionSender().sendMessage(
									"You smelt a bar of iron.");
							getPlayer().getInventory().remove(mainOre);
							getPlayer().getInventory().add(resultBar);
							getPlayer().getSkills().addExperience(
									Skills.SMITHING, exp);
						}
					} else if (isIron && hasRingOfForgin(getPlayer())) {// Ring
																		// of
																		// forging.
						getPlayer().getActionSender().sendMessage(
								"You smelt a bar of iron.");
						getPlayer().getInventory().remove(mainOre);
						getPlayer().getInventory().add(resultBar);
						getPlayer().getSkills().addExperience(Skills.SMITHING,
								exp);
					} else {
						getPlayer().getActionSender().sendMessage(
								"You smelt a bar of "
										+ resultBar.getDefinition().getName()
												.toLowerCase()
												.replace(" bar", "") + ".");
						getPlayer().getInventory().remove(mainOre, 1);
						if (requiresTwoOres) {
							getPlayer().getInventory().remove(secondaryOre);
						}
						getPlayer().getInventory().add(resultBar);
						getPlayer().getSkills().addExperience(Skills.SMITHING,
								exp);
						TutorialIsland.smeltedBar(getPlayer(), resultBar);
					}
					this.stop();
				}
			});
			this.setDelay(3500);
			if (--resultBarAmount <= 0) {
				this.stop();
			}

		}

		private final Item mainOre;
		private final Item secondaryOre;
		private int resultBarAmount;
		private final Item resultBar;
		private final double exp;
		private final boolean isIron;
		private final boolean requiresTwoOres;

		@Override
		public QueuePolicy getQueuePolicy() {
			return QueuePolicy.NEVER;
		}

		@Override
		public WalkablePolicy getWalkablePolicy() {
			return WalkablePolicy.NON_WALKABLE;
		}
	}

	private static boolean hasRingOfForgin(Player player) {
		Item ring = player.getEquipment().get(Equipment.SLOT_RING);
		return ring == null ? false : ring.getId() == 2568;
	}

	public static class AnvilAction extends Action {

		private static final Animation SMITHING_ANIMATION = Animation
				.create(898);

		/**
		 * Engine for forging bars into some result (knife, platebody etc).
		 * 
		 * @param exp
		 * @param resultAmount
		 * @param result
		 * @param requiredBars
		 * @param bar
		 */
		public AnvilAction(final Player player, Item bar, int amount,
				Item result, double exp) {
			super(player, 0);
			this.bar = bar;
			this.result = result;
			this.amount = amount;
			this.exp = exp;
			player.getActionSender().sendCloseInterface();
		}

		@Override
		public void execute() {
			System.out.println("Amount: " + amount);
			if (getPlayer() == null) {
				this.stop();
				return;
			}
			if (getPlayer().isDead() || amount <= 0) {
				this.stop();
				return;
			}
			/**
			 * If player is done with smithing all his bars, then we return.
			 */
			if (!getPlayer().getInventory().contains(bar)) {
				getPlayer().getActionSender().sendMessage(
						"You don't have enough bars!");
				this.stop();
				return;
			}
			getPlayer().playAnimation(SMITHING_ANIMATION);
			getPlayer().getInventory().remove(bar);
			getPlayer().getInventory().add(result);
			// Message could be delayed sligthly, but whatever.
			getPlayer().getActionSender().sendMessage(
					"You successfully smith "
							+ (result.getCount() > 1 ? "some "
									: result.getDefinition().getName()
											.startsWith("A") ? "an " : "a ")
							+ result.getDefinition().getName().toLowerCase()
							+ ".");
			getPlayer().getSkills().addExperience(Skills.SMITHING,
					(exp * bar.getCount()));
			TutorialIsland.itemSmith(getPlayer(), result);
			this.setDelay(2200);
			if (--amount <= 0) {
				this.stop();
				return;
			}
		}

		private final Item bar;
		private final Item result;
		private final double exp;
		private int amount;

		@Override
		public QueuePolicy getQueuePolicy() {
			return QueuePolicy.NEVER;
		}

		@Override
		public WalkablePolicy getWalkablePolicy() {
			return WalkablePolicy.NON_WALKABLE;
		}
	}

	public static void anvilSmithing(Player player, int lvlReq, int bar,
			int requiredBars, int result, int amount, int resultAmount,
			double exp) {
		Item res = new Item(bar, requiredBars);
		int[][] info = player.getQuestInfo();
		final int stage = info[TutorialIsland.QUEST_INFO_INDEX][0];
		if (stage != TutorialIsland.MAX_STAGE) { // If its not finished
			if (result != 1205) {
				player.getActionSender().sendMessage(
						"You can only smith bronze daggers at the moment.");
				return;
			}
		}
		if (smithingLevel(player, lvlReq)) {
			if (player.getInventory().contains(2347)) {// Hammer
				if (player.getInventory().contains(res)) {
					// player.resetSkilling();
					// player.isSmithing = true;
					player.getActionQueue().addAction(
							new AnvilAction(player, res, amount, new Item(
									result, resultAmount), exp));
				} else {
					System.out.println("SAY WHAT!?");
					player.getActionSender().sendMessage(
							"You don't have the required amount of bars.");
				}
			} else {
				player.getActionSender().sendMessage(
						"You need a hammer in order to smith.");
			}
		} else {
			player.getActionSender().sendMessage(
					"You need a Smithing level of at least "
							+ lvlReq
							+ " in order to smith a "
							+ new Item(result).getDefinition().getName()
									.toLowerCase() + ".");
		}
	}

	public static void furnaceSmelting(Player player, int lvlReq, int mainOre1,
			int secondaryOre1, int secondaryOreAmount, int resultBar1,
			int resultBarAmount, double exp, boolean isIron1,
			boolean requiresTwoOres1) {
		player.getActionSender().sendCloseInterface();
		String resultName = new Item(resultBar1).getDefinition().getName()
				.toLowerCase();
		if (smithingLevel(player, lvlReq)) {
			if (player.getInventory().contains(mainOre1)) {
				if (player.getInventory().contains(secondaryOre1)) {
					// player.resetSkilling();
					// player.isSmelting = true;
					player.getActionQueue().addAction(
							new SmeltingAction(player, mainOre1, secondaryOre1,
									exp, resultBar1, resultBarAmount,
									secondaryOreAmount, isIron1,
									requiresTwoOres1));
					// appendFurnaceEngine(player, mainOre1, secondaryOre1, exp,
					// resultBar1, resultBarAmount, secondaryOreAmount, isIron1,
					// requiresTwoOres1);
				} else {
					player.getActionSender().sendMessage(
							"You don't have enough "
									+ new Item(secondaryOre1).getDefinition()
											.getName().toLowerCase()
									+ "'s in order to smelt a " + resultName
									+ ".");
				}
			} else {
				player.getActionSender().sendMessage(
						"You don't have enough "
								+ new Item(mainOre1).getDefinition().getName()
										.toLowerCase()
								+ "'s in order to smelt a " + resultName + ".");
			}
		} else {
			player.getActionSender().sendMessage(
					"You need a Smithing level of at least " + lvlReq
							+ " in order to smelt " + resultName + "'s.");
		}
	}

	public static void parseValues(Player player, int slot, int itemId,
			int option) {
		switch (itemId) {
		/*
		 * Bronze items.
		 */
		// TODO: Bronze wire..? LvlReq = 4.
		case 1205: // Dagger.
			anvilSmithing(player, 1, 2349, 1, 1205, option, 1, 12.5);
			return;
		case 1277: // Sword
			anvilSmithing(player, 4, 2349, 1, 1277, option, 1, 12.5);
			return;
		case 1321: // Scimitar
			anvilSmithing(player, 5, 2349, 2, 1321, option, 1, 12.5);
			return;
		case 1291: // Longsword
			anvilSmithing(player, 6, 2349, 2, 1291, option, 1, 12.5);
			return;
		case 1307:// 2h sword.
			anvilSmithing(player, 14, 2349, 3, 1307, option, 1, 12.5);
			return;
		case 1351:// Axe
			anvilSmithing(player, 1, 2349, 1, 1351, option, 1, 12.5);
			return;
		case 1422:// Mace
			anvilSmithing(player, 2, 2349, 1, 1422, option, 1, 12.5);
			return;
		case 1337:// Warhammer.
			anvilSmithing(player, 9, 2349, 1, 1337, option, 1, 12.5);
			return;
		case 1375:// Battleaxe.
			anvilSmithing(player, 10, 2349, 3, 1375, option, 1, 12.5);
			return;
		case 3095:// Claws
			anvilSmithing(player, 13, 2349, 2, 3095, option, 1, 12.5);
			return;
		case 1103:// Chainbody
			anvilSmithing(player, 11, 2349, 3, 1103, option, 1, 12.5);
			return;
		case 1075:// Platelegs.
			anvilSmithing(player, 14, 2349, 3, 1075, option, 1, 12.5);
			return;
		case 1087:// Plateskirt.
			anvilSmithing(player, 14, 2349, 3, 1087, option, 1, 12.5);
			return;
		case 1117:// Platebody
			anvilSmithing(player, 16, 2349, 5, 1117, option, 1, 12.5);
			return;
		case 1139:// Med helm.
			anvilSmithing(player, 3, 2349, 1, 1139, option, 1, 12.5);
			return;
		case 1155:// Full helm.
			anvilSmithing(player, 7, 2349, 2, 1155, option, 1, 12.5);
			return;
		case 1173:// Square shield.
			anvilSmithing(player, 8, 2349, 2, 1173, option, 1, 12.5);
			return;
		case 1189:// Kite shield.
			anvilSmithing(player, 12, 2349, 3, 1189, option, 1, 12.5);
			return;
		case 4819:// Nails
			anvilSmithing(player, 4, 2349, 1, 4819, option, 15, 12.5);
			return;
		case 819:// Dart tips
			anvilSmithing(player, 4, 2349, 1, 819, option, 10, 12.5);
			return;
		case 39:// Arrowtips
			anvilSmithing(player, 5, 2349, 1, 39, option, 15, 12.5);
			return;
		case 864:// Throwing knives.
			anvilSmithing(player, 7, 2349, 1, 864, option, 5, 12.5);
			return;
		case 877:// Bolts
			anvilSmithing(player, 3, 2349, 1, 877, option, 15, 12.5);
			return;
		case 9420:// Crossbow limbs.
			anvilSmithing(player, 6, 2349, 1, 9420, option, 1, 12.5);
			return;
			/*
			 * Iron items.
			 */
		case 1203:// Dagger.
			anvilSmithing(player, 15, 2351, 1, 1203, option, 1, 25);
			return;
		case 1279:// Sword
			anvilSmithing(player, 19, 2351, 1, 1279, option, 1, 25);
			return;
		case 1323:// Scimitar.
			anvilSmithing(player, 20, 2351, 2, 1323, option, 1, 25);
			return;
		case 1293:// Long sword.
			anvilSmithing(player, 21, 2351, 2, 1293, option, 1, 25);
			return;
		case 1309:// 2h sword..
			anvilSmithing(player, 29, 2351, 3, 1203, option, 1, 25);
			return;
		case 1349:// Axe
			anvilSmithing(player, 16, 2351, 1, 1349, option, 1, 25);
			return;
		case 1420:// Mace
			anvilSmithing(player, 17, 2351, 1, 1420, option, 1, 25);
			return;
		case 1335:// Warhammer
			anvilSmithing(player, 24, 2351, 1, 1335, option, 1, 25);
			return;
		case 1363:// Battleaxe
			anvilSmithing(player, 25, 2351, 3, 1363, option, 1, 25);
			return;
		case 3096:// Claws.
			anvilSmithing(player, 28, 2351, 2, 3096, option, 1, 25);
			return;
		case 1101:// Chainbody
			anvilSmithing(player, 26, 2351, 3, 1101, option, 1, 25);
			return;
		case 1067:// Platelegs.
			anvilSmithing(player, 31, 2351, 3, 1067, option, 1, 25);
			return;
		case 1081:// Plateskirt.
			anvilSmithing(player, 31, 2351, 3, 1081, option, 1, 25);
			return;
		case 1115:// Platebody
			anvilSmithing(player, 33, 2351, 5, 1115, option, 1, 25);
			return;
		case 1137:// Med helm.
			anvilSmithing(player, 18, 2351, 1, 1137, option, 1, 25);
			return;
		case 1153:// Full helm.
			anvilSmithing(player, 22, 2351, 2, 1153, option, 1, 25);
			return;
		case 1175:// Square shield.
			anvilSmithing(player, 23, 2351, 2, 1175, option, 1, 25);
			return;
		case 1191:// Kite shield.
			anvilSmithing(player, 27, 2351, 3, 1191, option, 1, 25);
			return;
		case 4820:// Nails
			anvilSmithing(player, 20, 2351, 1, 4820, option, 15, 25);
			return;
		case 820:// Dart tips
			anvilSmithing(player, 19, 2351, 1, 820, option, 10, 25);
			return;
		case 40:// Arrowtips
			anvilSmithing(player, 20, 2351, 1, 40, option, 15, 25);
			return;
		case 863:// Throwing knives.
			anvilSmithing(player, 22, 2351, 1, 863, option, 5, 25);
			return;
		case 9140:// Bolts
			anvilSmithing(player, 18, 2351, 1, 9140, option, 15, 25);
			return;
		case 9423:// Crossbow limbs.
			anvilSmithing(player, 23, 2351, 1, 9423, option, 1, 25);
			return;
			/*
			 * Steel items.
			 */
		case 1207:// Dagger.
			anvilSmithing(player, 30, 2353, 1, 1207, option, 1, 37.5);
			return;
		case 1281:// Sword
			anvilSmithing(player, 34, 2353, 1, 1281, option, 1, 37.5);
			return;
		case 1325:// Scimitar.
			anvilSmithing(player, 35, 2353, 2, 1325, option, 1, 37.5);
			return;
		case 1295:// Long sword.
			anvilSmithing(player, 36, 2353, 2, 1295, option, 1, 37.5);
			return;
		case 1311:// 2h sword..
			anvilSmithing(player, 44, 2353, 3, 1311, option, 1, 37.5);
			return;
		case 1353:// Axe
			anvilSmithing(player, 31, 2353, 1, 1353, option, 1, 37.5);
			return;
		case 1424:// Mace
			anvilSmithing(player, 32, 2353, 1, 1424, option, 1, 37.5);
			return;
		case 1339:// Warhammer
			anvilSmithing(player, 39, 2353, 1, 1339, option, 1, 37.5);
			return;
		case 1365:// Battleaxe
			anvilSmithing(player, 40, 2353, 3, 1365, option, 1, 37.5);
			return;
		case 3097:// Claws.
			anvilSmithing(player, 43, 2353, 2, 3097, option, 1, 37.5);
			return;
		case 1105:// Chainbody
			anvilSmithing(player, 41, 2353, 3, 1105, option, 1, 37.5);
			return;
		case 1069:// Platelegs.
			anvilSmithing(player, 46, 2353, 3, 1069, option, 1, 37.5);
			return;
		case 1083:// Plateskirt.
			anvilSmithing(player, 46, 2353, 3, 1083, option, 1, 37.5);
			return;
		case 1119:// Platebody
			anvilSmithing(player, 48, 2353, 5, 1119, option, 1, 37.5);
			return;
		case 4544:// Bullseye lantern.
			anvilSmithing(player, 49, 2353, 1, 4544, option, 1, 37.5);
			return;
		case 1141:// Med helm.
			anvilSmithing(player, 33, 2353, 1, 1141, option, 1, 37.5);
			return;
		case 1157:// Full helm.
			anvilSmithing(player, 37, 2353, 2, 1157, option, 1, 37.5);
			return;
		case 1177:// Square shield.
			anvilSmithing(player, 38, 2353, 2, 1177, option, 1, 37.5);
			return;
		case 1193:// Kite shield.
			anvilSmithing(player, 42, 2353, 3, 1193, option, 1, 37.5);
			return;
		case 1539:// Nails
			anvilSmithing(player, 34, 2353, 1, 1539, option, 15, 37.5);
			return;
		case 821:// Dart tips
			anvilSmithing(player, 34, 2353, 1, 821, option, 10, 37.5);
			return;
		case 41:// Arrowtips
			anvilSmithing(player, 35, 2353, 1, 41, option, 15, 37.5);
			return;
		case 865:// Throwing knives.
			anvilSmithing(player, 37, 2353, 1, 865, option, 5, 37.5);
			return;
		case 2370:// Studs
			anvilSmithing(player, 36, 2353, 1, 2370, option, 15, 37.5);
			return;
		case 9141:// Bolts
			anvilSmithing(player, 33, 2353, 1, 9141, option, 15, 37.5);
			return;
		case 9425:// Crossbow limbs.
			anvilSmithing(player, 36, 2353, 1, 9425, option, 1, 37.5);
			return;
			/*
			 * Mithril items.
			 */
		case 1209:// Dagger.
			anvilSmithing(player, 50, 2359, 1, 1209, option, 1, 50);
			return;
		case 1285:// Sword
			anvilSmithing(player, 50, 2359, 1, 1285, option, 1, 50);
			return;
		case 1329:// Scimitar.
			anvilSmithing(player, 55, 2359, 2, 1329, option, 1, 50);
			return;
		case 1299:// Long sword.
			anvilSmithing(player, 56, 2359, 2, 1299, option, 1, 50);
			return;
		case 1315:// 2h sword..
			anvilSmithing(player, 64, 2359, 3, 1315, option, 1, 50);
			return;
		case 1355:// Axe
			anvilSmithing(player, 51, 2359, 1, 1355, option, 1, 50);
			return;
		case 1428:// Mace
			anvilSmithing(player, 52, 2359, 1, 1428, option, 1, 50);
			return;
		case 1343:// Warhammer
			anvilSmithing(player, 59, 2359, 1, 1343, option, 1, 50);
			return;
		case 1369:// Battleaxe
			anvilSmithing(player, 60, 2359, 3, 1369, option, 1, 50);
			return;
		case 3099:// Claws.
			anvilSmithing(player, 63, 2359, 2, 3099, option, 1, 50);
			return;
		case 1109:// Chainbody
			anvilSmithing(player, 61, 2359, 3, 1109, option, 1, 50);
			return;
		case 1071:// Platelegs.
			anvilSmithing(player, 66, 2359, 3, 1071, option, 1, 50);
			return;
		case 1085:// Plateskirt.
			anvilSmithing(player, 66, 2359, 3, 1085, option, 1, 50);
			return;
		case 1121:// Platebody
			anvilSmithing(player, 68, 2359, 5, 1121, option, 1, 50);
			return;
		case 1143:// Med helm.
			anvilSmithing(player, 50, 2359, 1, 1143, option, 1, 50);
			return;
		case 1159:// Full helm.
			anvilSmithing(player, 57, 2359, 2, 1159, option, 1, 50);
			return;
		case 1181:// Square shield.
			anvilSmithing(player, 58, 2359, 2, 1181, option, 1, 50);
			return;
		case 1197:// Kite shield.
			anvilSmithing(player, 62, 2359, 3, 1197, option, 1, 50);
			return;
		case 4822:// Nails
			anvilSmithing(player, 54, 2359, 1, 4822, option, 15, 50);
			return;
		case 822:// Dart tips
			anvilSmithing(player, 54, 2359, 1, 822, option, 10, 50);
			return;
		case 42:// Arrowtips
			anvilSmithing(player, 55, 2359, 1, 42, option, 15, 50);
			return;
		case 866:// Throwing knives.
			anvilSmithing(player, 57, 2359, 1, 866, option, 5, 50);
			return;
		case 9142:// Bolts
			anvilSmithing(player, 53, 2359, 1, 9142, option, 15, 50);
			return;
		case 9427:// Crossbow limbs.
			anvilSmithing(player, 56, 2359, 1, 9427, option, 1, 50);
			return;
		case 9416:// Mithril grapple.
			anvilSmithing(player, 59, 2359, 1, 9416, option, 1, 50);
			return;
			/*
			 * Adamant items.
			 */
		case 1211:// Dagger.
			anvilSmithing(player, 70, 2361, 1, 1211, option, 1, 62.5);
			return;
		case 1287:// Sword
			anvilSmithing(player, 74, 2361, 1, 1287, option, 1, 62.5);
			return;
		case 1331:// Scimitar.
			anvilSmithing(player, 75, 2361, 2, 1331, option, 1, 62.5);
			return;
		case 1301:// Long sword.
			anvilSmithing(player, 76, 2361, 2, 1301, option, 1, 62.5);
			return;
		case 1317:// 2h sword..
			anvilSmithing(player, 84, 2361, 3, 1317, option, 1, 62.5);
			return;
		case 1357:// Axe
			anvilSmithing(player, 71, 2361, 1, 1357, option, 1, 62.5);
			return;
		case 1430:// Mace
			anvilSmithing(player, 72, 2361, 1, 1430, option, 1, 62.5);
			return;
		case 1345:// Warhammer
			anvilSmithing(player, 79, 2361, 1, 1345, option, 1, 62.5);
			return;
		case 1371:// Battleaxe
			anvilSmithing(player, 80, 2361, 3, 1371, option, 1, 62.5);
			return;
		case 3100:// Claws.
			anvilSmithing(player, 83, 2361, 2, 3100, option, 1, 62.5);
			return;
		case 1111:// Chainbody
			anvilSmithing(player, 81, 2361, 3, 1111, option, 1, 62.5);
			return;
		case 1073:// Platelegs.
			anvilSmithing(player, 86, 2361, 3, 1073, option, 1, 62.5);
			return;
		case 1091:// Plateskirt.
			anvilSmithing(player, 86, 2361, 3, 1091, option, 1, 62.5);
			return;
		case 1123:// Platebody
			anvilSmithing(player, 88, 2361, 5, 1123, option, 1, 62.5);
			return;
		case 1145:// Med helm.
			anvilSmithing(player, 73, 2361, 1, 1145, option, 1, 62.5);
			return;
		case 1161:// Full helm.
			anvilSmithing(player, 77, 2361, 2, 1161, option, 1, 62.5);
			return;
		case 1183:// Square shield.
			anvilSmithing(player, 78, 2361, 2, 1183, option, 1, 62.5);
			return;
		case 1199:// Kite shield.
			anvilSmithing(player, 82, 2361, 3, 1199, option, 1, 62.5);
			return;
		case 4823:// Nails
			anvilSmithing(player, 74, 2361, 1, 4823, option, 15, 62.5);
			return;
		case 823:// Dart tips
			anvilSmithing(player, 74, 2361, 1, 823, option, 10, 62.5);
			return;
		case 43:// Arrowtips
			anvilSmithing(player, 75, 2361, 1, 43, option, 15, 62.5);
			return;
		case 867:// Throwing knives.
			anvilSmithing(player, 77, 2361, 1, 867, option, 5, 62.5);
			return;
		case 9143:// Bolts
			anvilSmithing(player, 73, 2361, 1, 9143, option, 15, 62.5);
			return;
		case 9429:// Crossbow limbs.
			anvilSmithing(player, 76, 2361, 1, 9429, option, 1, 62.5);
			return;
			/*
			 * Rune Items.
			 */
		case 1213:// Dagger.
			anvilSmithing(player, 85, 2363, 1, 1213, option, 1, 75);
			return;
		case 1289:// Sword
			anvilSmithing(player, 89, 2363, 1, 1289, option, 1, 75);
			return;
		case 1333:// Scimitar.
			anvilSmithing(player, 90, 2363, 2, 1333, option, 1, 75);
			return;
		case 1303:// Long sword.
			anvilSmithing(player, 91, 2363, 2, 1303, option, 1, 75);
			return;
		case 1319:// 2h sword..
			anvilSmithing(player, 99, 2363, 3, 1319, option, 1, 75);
			return;
		case 1359:// Axe
			anvilSmithing(player, 86, 2363, 1, 1359, option, 1, 75);
			return;
		case 1432:// Mace
			anvilSmithing(player, 87, 2363, 1, 1432, option, 1, 75);
			return;
		case 1347:// Warhammer
			anvilSmithing(player, 94, 2363, 1, 1347, option, 1, 75);
			return;
		case 1373:// Battleaxe
			anvilSmithing(player, 95, 2363, 3, 1373, option, 1, 75);
			return;
		case 3101:// Claws.
			anvilSmithing(player, 98, 2363, 2, 3101, option, 1, 75);
			return;
		case 1113:// Chainbody
			anvilSmithing(player, 96, 2363, 3, 1113, option, 1, 75);
			return;
		case 1079:// Platelegs.
			anvilSmithing(player, 99, 2363, 3, 1079, option, 1, 75);
			return;
		case 1093:// Plateskirt.
			anvilSmithing(player, 99, 2363, 3, 1093, option, 1, 75);
			return;
		case 1127:// Platebody
			anvilSmithing(player, 99, 2363, 5, 1127, option, 1, 75);
			return;
		case 1147:// Med helm.
			anvilSmithing(player, 88, 2363, 1, 1145, option, 1, 62.5);
			return;
		case 1163:// Full helm.
			anvilSmithing(player, 92, 2363, 2, 1163, option, 1, 62.5);
			return;
		case 1185:// Square shield.
			anvilSmithing(player, 93, 2363, 2, 1185, option, 1, 62.5);
			return;
		case 1201:// Kite shield.
			anvilSmithing(player, 97, 2363, 3, 1201, option, 1, 62.5);
			return;
		case 4824:// Nails
			anvilSmithing(player, 89, 2363, 1, 4824, option, 15, 62.5);
			return;
		case 824:// Dart tips
			anvilSmithing(player, 89, 2363, 1, 824, option, 10, 62.5);
			return;
		case 44:// Arrowtips
			anvilSmithing(player, 90, 2363, 1, 44, option, 15, 62.5);
			return;
		case 868:// Throwing knives.
			anvilSmithing(player, 92, 2363, 1, 868, option, 5, 62.5);
			return;
		case 9144:// Bolts
			anvilSmithing(player, 88, 2363, 1, 9144, option, 15, 62.5);
			return;
		case 9431:// Crossbow limbs.
			anvilSmithing(player, 91, 2363, 1, 9429, option, 1, 62.5);
			return;
		}
	}

	/**
	 * Returns true, if the players crafting level is high enough.
	 * */
	private static boolean smithingLevel(Player player, int level) {
		return (player.getSkills().getLevel(Skills.SMITHING) >= level);
	}

	private static final Item[][] RUNE_ITEMS = { { new Item(1213, 1),// dagger
			new Item(1289, 1),// sword
			new Item(1333, 1),// scimitar
			new Item(1303, 1),// longsword
			new Item(1319, 1),// 2 hand sword
	}, { new Item(1359, 1),// axe
			new Item(1432, 1),// mace
			new Item(1347, 1),// warhammer
			new Item(1373, 1),// battleaxe
			new Item(3101, 1),// claws
	}, { new Item(1113, 1),// chainbody
			new Item(1079, 1),// platelegs
			new Item(1093, 1),// plateskirt
			new Item(1127, 1),// platebody
	// new Item(4540, 1),//oil lantern frame
			}, { new Item(1147, 1),// medium helm
					new Item(1163, 1),// full helm
					new Item(1185, 1),// square shield OH HI TOMMI :D
					new Item(1201, 1),// kite shield
					new Item(4824, 15),// nails
			}, { new Item(824, 15),// dart tips
					new Item(44, 15),// arrow tips
					new Item(868, 5),// throwing knives
			// new Item(4819, 1),//other..?????????
			// new Item(4819, 1),//Studs, only used for rune I guess.
			}, { new Item(9144, 15),// bolts
					new Item(9431, 1),// limbs
			// new Item(864, 1), Only used for rune.
			} };

	/**
	 * Sends all the Rune items from the Rune item array, and put them on our
	 * Smithing interface.
	 * */
	public static void initializeRuneItems(Player player) {
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 146, 93,
				RUNE_ITEMS[0]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 147, 93,
				RUNE_ITEMS[1]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 148, 93,
				RUNE_ITEMS[2]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 149, 93,
				RUNE_ITEMS[3]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 150, 93,
				RUNE_ITEMS[4]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 151, 93,
				RUNE_ITEMS[5]);
	}

	private static Item[][] ADAMANT_ITEMS = { { new Item(1211, 1),// dagger
			new Item(1287, 1),// sword
			new Item(1331, 1),// scimitar
			new Item(1301, 1),// longsword
			new Item(1317, 1),// 2 hand sword
	}, { new Item(1357, 1),// axe
			new Item(1430, 1),// mace
			new Item(1345, 1),// warhammer
			new Item(1371, 1),// battleaxe
			new Item(3100, 1),// claws
	}, { new Item(1111, 1),// chainbody
			new Item(1073, 1),// platelegs
			new Item(1091, 1),// plateskirt
			new Item(1123, 1),// platebody
	// new Item(4540, 1),//oil lantern frame
			}, { new Item(1145, 1),// medium helm
					new Item(1161, 1),// full helm
					new Item(1183, 1),// square shield OH HI TOMMI :D
					new Item(1199, 1),// kite shield
					new Item(4823, 15),// nails
			}, { new Item(823, 15),// dart tips
					new Item(43, 15),// arrow tips
					new Item(867, 5),// throwing knives
			// new Item(4819, 1),//other..?????????
			// new Item(4819, 1),//Studs, only used for adamant I guess.
			}, { new Item(9143, 15),// bolts
					new Item(9429, 1),// limbs
			// new Item(864, 1), Only used for adamant.
			},

	};

	/**
	 * Sends all the Adamant items from the Adamant item array, and put them on
	 * our Smithing interface.
	 * */
	public static void initializeAdamantItems(Player player) {
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 146, 93,
				ADAMANT_ITEMS[0]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 147, 93,
				ADAMANT_ITEMS[1]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 148, 93,
				ADAMANT_ITEMS[2]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 149, 93,
				ADAMANT_ITEMS[3]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 150, 93,
				ADAMANT_ITEMS[4]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 151, 93,
				ADAMANT_ITEMS[5]);
	}

	private static final Item[][] MITHRIL_ITEMS = { { new Item(1209, 1),// dagger
			new Item(1285, 1),// sword
			new Item(1329, 1),// scimitar
			new Item(1299, 1),// longsword
			new Item(1315, 1),// 2 hand sword
	}, { new Item(1355, 1),// axe
			new Item(1428, 1),// mace
			new Item(1343, 1),// warhammer
			new Item(1369, 1),// battleaxe
			new Item(3099, 1),// claws
	}, { new Item(1109, 1),// chainbody
			new Item(1071, 1),// platelegs
			new Item(1085, 1),// plateskirt
			new Item(1121, 1),// platebody
	// new Item(4540, 1),//oil lantern frame
			}, { new Item(1143, 1),// medium helm
					new Item(1159, 1),// full helm
					new Item(1181, 1),// square shield OH HI TOMMI :D
					new Item(1197, 1),// kite shield
					new Item(4822, 15),// nails
			}, { new Item(822, 15),// dart tips
					new Item(42, 15),// arrow tips
					new Item(866, 5),// throwing knives
			// new Item(4819, 1),//other..?????????
			// new Item(4819, 1),//Studs, only used for mithril I guess.
			}, { new Item(9142, 15),// bolts
					new Item(9427, 1),// limbs
					new Item(9416, 1), // Grapple tip, only used for mithril.
			} };

	/**
	 * Sends all the Mithril items from the Mithril item array, and put them on
	 * our Smithing interface.
	 * */
	public static void initializeMithrilItems(Player player) {
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 146, 93,
				MITHRIL_ITEMS[0]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 147, 93,
				MITHRIL_ITEMS[1]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 148, 93,
				MITHRIL_ITEMS[2]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 149, 93,
				MITHRIL_ITEMS[3]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 150, 93,
				MITHRIL_ITEMS[4]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 151, 93,
				MITHRIL_ITEMS[5]);
	}

	private static final Item[][] STEEL_ITEMS = { { new Item(1207, 1),// dagger
			new Item(1281, 1),// sword
			new Item(1325, 1),// scimitar
			new Item(1295, 1),// longsword
			new Item(1311, 1),// 2 hand sword
	}, { new Item(1353, 1),// axe
			new Item(1424, 1),// mace
			new Item(1339, 1),// warhammer
			new Item(1365, 1),// battleaxe
			new Item(3097, 1),// claws
	}, { new Item(1105, 1),// chainbody
			new Item(1069, 1),// platelegs
			new Item(1083, 1),// plateskirt
			new Item(1119, 1),// platebody
			new Item(4544, 1),// Bullseye lantern.//TODO: SendString.
	}, { new Item(1141, 1),// medium helm
			new Item(1157, 1),// full helm
			new Item(1177, 1),// square shield OH HI TOMMI :D
			new Item(1193, 1),// kite shield
			new Item(1539, 15),// nails
	}, { new Item(821, 15),// dart tips
			new Item(41, 15),// arrow tips
			new Item(865, 5),// throwing knives
			new Item(-1, 1),// other..????????? //We have to send -1 here, if we
							// didn't, the Studs would be placed in the other
							// spot.
			new Item(2370, 1),// Studs, only used for steel I guess.
	}, { new Item(9141, 15),// bolts
			new Item(9425, 1),// limbs
	// new Item(864, 1), Only used for mithril.
			} };

	/**
	 * Sends all the Steel items from the Steel item array, and put them on our
	 * Smithing interface.
	 * */
	public static void initializeSteelItems(Player player) {
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 146, 93,
				STEEL_ITEMS[0]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 147, 93,
				STEEL_ITEMS[1]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 148, 93,
				STEEL_ITEMS[2]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 149, 93,
				STEEL_ITEMS[3]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 150, 93,
				STEEL_ITEMS[4]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 151, 93,
				STEEL_ITEMS[5]);
	}

	private static final Item[][] IRON_ITEMS = { { new Item(1203, 1),// dagger
			new Item(1279, 1),// sword
			new Item(1323, 1),// scimitar
			new Item(1293, 1),// longsword
			new Item(1309, 1),// 2 hand sword
	}, { new Item(1349, 1),// axe
			new Item(1420, 1),// mace
			new Item(1335, 1),// warhammer
			new Item(1363, 1),// battleaxe
			new Item(3096, 1),// claws
	}, { new Item(1101, 1),// chainbody
			new Item(1067, 1),// platelegs
			new Item(1081, 1),// plateskirt
			new Item(1115, 1),// platebody
	// new Item(4540, 1),//oil lantern frame
			}, { new Item(1137, 1),// medium helm
					new Item(1153, 1),// full helm
					new Item(1175, 1),// square shield OH HI TOMMI :D
					new Item(1191, 1),// kite shield
					new Item(4820, 15),// nails
			}, { new Item(820, 15),// dart tips
					new Item(40, 15),// arrow tips
					new Item(863, 5),// throwing knives
			// new Item(4819, 1),//other..?????????
			// new Item(4819, 1),//Studs, only used for steel I guess.
			}, { new Item(9140, 15),// bolts
					new Item(9423, 1),// limbs
			// new Item(864, 1), Only used for mithril.
			}, };

	/**
	 * Sends all the iron items from the iron item array, and put them on our
	 * Smithing interface.
	 * */
	public static void initializeIronItems(Player player) {
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 146, 93,
				IRON_ITEMS[0]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 147, 93,
				IRON_ITEMS[1]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 148, 93,
				IRON_ITEMS[2]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 149, 93,
				IRON_ITEMS[3]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 150, 93,
				IRON_ITEMS[4]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 151, 93,
				IRON_ITEMS[5]);
	}

	private static final Item[][] BRONZE_ITEMS = { { new Item(1205),// dagger
			new Item(1277),// sword
			new Item(1321),// scimitar
			new Item(1291),// longsword
			new Item(1307),// 2 hand sword
	}, { new Item(1351),// axe
			new Item(1422),// mace
			new Item(1337),// warhammer
			new Item(1375),// battleaxe
			new Item(3095),// claws
	}, { new Item(1103),// chainbody
			new Item(1075),// platelegs
			new Item(1087),// plateskirt
			new Item(1117),// platebody
	}, { new Item(1139),// medium helm
			new Item(1155),// full helm
			new Item(1173),// square shield OH HI TOMMI :D
			new Item(1189),// kite shield
			new Item(4819, 15),// nails
	}, { new Item(819, 15),// dart tips
			new Item(39, 15),// arrow tips
			new Item(864, 5),// throwing knives
	// items5.add(new Item(4819, 1),//other..?????????
	// items5.add(new Item(4819, 1),//Studs, only used for steel I guess.
			}, { new Item(877, 15),// bolts
					new Item(9420),// limbs
			// items6.add(new Item(864), Only used for mithril.
			} };

	/**
	 * Sends all the bronze items from the bronze item array, and put them on
	 * our Smithing interface.
	 * */
	public static void initializeBronzeItems(Player player) {
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 146, 93,
				BRONZE_ITEMS[0]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 147, 93,
				BRONZE_ITEMS[1]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 148, 93,
				BRONZE_ITEMS[2]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 149, 93,
				BRONZE_ITEMS[3]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 150, 93,
				BRONZE_ITEMS[4]);
		player.getActionSender().sendUpdateItems(SMITHING_INTERFACE, 151, 93,
				BRONZE_ITEMS[5]);
	}

}
