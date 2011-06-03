package org.hyperion.rs2.content.skills;

import java.util.Random;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;

public class Runecrafting {

	private static final Animation RUNECRAFTING_ANIMATION = Animation
			.create(791);
	private static final Graphic RUNECRAFTING_GRAPHICS = Graphic.create(186);

	private static final Random RANDOM = new Random();

	public static void craft(final Player player, final int rune,
			final int lvlReq, final double exp, int x2, int x3, int x4, int x5,
			int x6, int x7, int x8, int x9, int x10) {
		Item item = new Item(rune);

		if (player.getSkills().getLevel(20) < lvlReq) {
			player.getActionSender().sendMessage(
					"You need atleast " + lvlReq
							+ " Runecrafting to make this.");
			return;
		}
		int essenceAmount = player.getInventory().getCount(1436);
		if (!player.getInventory().contains(1436)) {
			player.getActionSender()
					.sendMessage("You need essence to do this.");
			return;
		}
		player.playAnimation(RUNECRAFTING_ANIMATION);
		player.playGraphics(RUNECRAFTING_GRAPHICS);
		player.getActionSender().sendMessage(
				"You bind the temple's power into "
						+ item.getDefinition().getName().toLowerCase() + "s.");
		player.getSkills().addExperience(20, exp * essenceAmount);
		int multipler = 1;

		player.getInventory().remove(new Item(1436, essenceAmount));

		if (player.getSkills().getLevel(20) >= x2
				&& player.getSkills().getLevel(20) < x3)
			multipler = 2;
		if (player.getSkills().getLevel(20) >= x3
				&& player.getSkills().getLevel(20) < x4)
			multipler = 3;
		if (player.getSkills().getLevel(20) >= x4
				&& player.getSkills().getLevel(20) < x5)
			multipler = 4;
		if (player.getSkills().getLevel(20) >= x5
				&& player.getSkills().getLevel(20) < x6)
			multipler = 5;
		if (player.getSkills().getLevel(20) >= x6
				&& player.getSkills().getLevel(20) < x7)
			multipler = 6;
		if (player.getSkills().getLevel(20) >= x7
				&& player.getSkills().getLevel(20) < x8)
			multipler = 7;
		if (player.getSkills().getLevel(20) >= x8
				&& player.getSkills().getLevel(20) < x9)
			multipler = 8;
		if (player.getSkills().getLevel(20) >= x9
				&& player.getSkills().getLevel(20) < x10)
			multipler = 9;
		if (player.getSkills().getLevel(20) >= x10) {
			multipler = 10;
		}
		player.getInventory().add(new Item(rune, essenceAmount * multipler));
	}

	private static final Animation TELEPORT_ANIMATION = Animation.create(827);

	public static boolean teleport(final Player player, final int x, final int y) {
		player.playAnimation(TELEPORT_ANIMATION);
		player.getActionSender().sendMessage(
				"You feel a powerful force take hold of you...");
		World.getWorld().submit(new Event(1200) {
			public void execute() {
				player.setTeleportTarget(Location.create(x, y, 0));
				this.stop();
			}
		});
		return true;
	}

	public static boolean talismanTele(Player player, int itemID, int objectID,
			int x, int y) {
		switch (objectID) {
		case 2452: // Air altar ruins
			if (x == 2984 && y == 3291) {
				if (itemID == 1438) {
					teleport(player, 2842, 4828);
					return true;
				}
			}
			break;
		case 2478: // Air altar
			if (x == 2843 && y == 4833) {
				if (itemID == 5525) {
					player.getInventory().replace(5525, 5527);
					return true;
				}
			}
			break;
		case 2453: // Mind altar ruins
			if (x == 2981 && y == 3513) {
				if (itemID == 1448) {
					teleport(player, 2793, 4828);
					return true;
				}
			}
			break;
		case 2479: // Mind altar
			if (x == 2785 && y == 4840) {
				if (itemID == 5525) {
					player.getInventory().replace(5525, 5529);
					return true;
				}
			}
			break;
		case 2454: // Water altar ruins
			if (x == 3184 && y == 3164) {
				if (itemID == 1444) {
					teleport(player, 2726, 4832);
					return true;
				}
			}
			break;
		case 2480: // Water altar
			if (x == 2715 && y == 4835) {
				if (itemID == 5525) {
					player.getInventory().replace(5525, 5531);
					return true;
				}
			}
			break;
		case 2455: // Earth altar ruins
			if (x == 3305 && y == 3473) {
				if (itemID == 1440) {
					teleport(player, 2655, 4830);
					return true;
				}
			}
			break;
		case 2481: // Earth altar
			if (x == 2657 && y == 4840) {
				if (itemID == 5525) {
					player.getInventory().replace(5525, 5535);
					return true;
				}
			}
			break;
		case 2456: // Fire altar ruins
			if (x == 3312 && y == 3254) {
				if (itemID == 1442) {
					teleport(player, 2574, 4849);
					return true;
				}
			}
			break;
		case 2482: // Fire altar
			if (x == 2584 && y == 4837) {
				if (itemID == 5525) {
					player.getInventory().replace(5525, 5537);
					return true;
				}
			}
			break;
		case 2457: // Body altar ruins
			if (x == 3052 && y == 3444) {
				if (itemID == 1446) {
					teleport(player, 2523, 4826);
					return true;
				}
			}
			break;
		case 2483: // Body altar
			if (x == 2524 && y == 4831) {
				if (itemID == 5525) {
					player.getInventory().replace(5525, 5533);
					return true;
				}
			}
			break;
		case 2458: // Cosmic altar ruins
			if (x == 2407 && y == 4376) {
				int[] xCoords = { 2162, 2142, 2122, 2142 };
				int[] yCoords = { 4833, 4853, 4833, 4813 };
				int loc = RANDOM.nextInt(xCoords.length);
				if (itemID == 1454) {
					teleport(player, xCoords[loc], yCoords[loc]);
					return true;
				}
			}
			break;
		case 2484: // Cosmic altar
			if (x == 2141 && y == 4832) {
				if (itemID == 5525) {
					player.getInventory().replace(5525, 5539);
					return true;
				}
			}
			break;
		case 2461: // Chaos altar ruins
			if (x == 3059 && y == 3590) {
				if (itemID == 1452) {
					teleport(player, 2281, 4837);
					return true;
				}
			}
			break;
		case 2487: // Chaos altar
			if (x == 2270 && y == 4841) {
				if (itemID == 5525) {
					player.getInventory().replace(5525, 5543);
					return true;
				}
			}
			break;
		case 2460: // Nature altar ruins
			if (x == 2868 && y == 3018) {
				if (itemID == 1462) {
					teleport(player, 2400, 4835);
					return true;
				}
			}
			break;
		case 2486: // Nature altar
			if (x == 2399 && y == 4840) {
				if (itemID == 5525) {
					player.getInventory().replace(5525, 5541);
					return true;
				}
			}
			break;
		case 2459: // Law altar ruins
			if (x == 2857 && y == 3380) {
				if (itemID == 1458) {
					teleport(player, 2464, 4818);
					return true;
				}
			}
			break;
		case 2485: // Law altar
			if (x == 2463 && y == 4831) {
				if (itemID == 5525) {
					player.getInventory().replace(5525, 5545);
					return true;
				}
			}
			break;
		case 2462: // Death altar ruins
			if (x == 1859 && y == 4638) {
				if (itemID == 1456) {
					teleport(player, 2208, 4828);
					return true;
				}
			}
			break;
		case 2488: // Death altar
			if (x == 2204 && y == 4835) {
				if (itemID == 5525) {
					player.getInventory().replace(5525, 5547);
					return true;
				}
			}
			break;

		}
		return false;
	}

	public static boolean portalTele(Player player, int object, int x, int y) {
		// player.getUpdateFlags().setFacePointUpdateRequired(x, y, true);
		if (object == 2478 && x == 2843 && y == 4833) { // Air altar
			craft(player, 556, 1, 5, 11, 22, 33, 44, 55, 66, 77, 88, 99);
			return true;
		}
		if (object == 2479 && x == 2785 && y == 4840) { // Mind altar
			craft(player, 558, 2, 5.5, 14, 28, 42, 56, 70, 84, 98, 101, 101);
			return true;
		}
		if (object == 2480 && x == 2715 && y == 4835) { // Water altar
			craft(player, 555, 5, 6, 19, 38, 57, 76, 95, 101, 101, 101, 101);
			return true;
		}
		if (object == 2481 && x == 2657 && y == 4840) { // Earth altar
			craft(player, 557, 9, 6.5, 26, 52, 78, 101, 101, 101, 101, 101, 101);
			return true;
		}
		if (object == 2482 && x == 2584 && y == 4837) { // Fire altar
			craft(player, 554, 14, 7, 46, 92, 101, 101, 101, 101, 101, 101, 101);
			return true;
		}
		if (object == 2483 && x == 2524 && y == 4831) { // Body altar
			craft(player, 559, 20, 7.5, 46, 92, 101, 101, 101, 101, 101, 101,
					101);
			return true;
		}
		if (object == 2484 && x == 2141 && y == 4832) { // Cosmic altar
			craft(player, 564, 27, 8, 59, 101, 101, 101, 101, 101, 101, 101,
					101);
			return true;
		}
		if (object == 2487 && x == 2270 && y == 4841) { // Chaos
			craft(player, 562, 35, 8.5, 101, 101, 101, 101, 101, 101, 101, 101,
					101);
			return true;
		}
		if (object == 2486 && x == 2399 && y == 4840) { // Nature
			craft(player, 561, 44, 9, 101, 101, 101, 101, 101, 101, 101, 101,
					101);
			return true;
		}
		if (object == 2485 && x == 2463 && y == 4831) { // Law
			craft(player, 563, 54, 9.5, 101, 101, 101, 101, 101, 101, 101, 101,
					101);
			return true;
		}
		if (object == 2488 && x == 2204 && y == 4835) { // Death
			craft(player, 560, 65, 10, 101, 101, 101, 101, 101, 101, 101, 101,
					101);
			return true;
		}

		if (object == 2465 && x == 2841 && y == 4828) { // Air portal
			player.getActionSender().sendMessage(
					"You step through the portal...");
			player.setTeleportTarget(Location.create(2984, 3290, 0));
			return true;
		}
		if (object == 2466 && x == 2793 && y == 4827) { // Mind portal
			player.getActionSender().sendMessage(
					"You step through the portal...");
			player.setTeleportTarget(Location.create(2981, 3512, 0));
			return true;
		}
		if (object == 2467 && x == 2727 && y == 4832) { // Water portal
			player.getActionSender().sendMessage(
					"You step through the portal...");
			player.setTeleportTarget(Location.create(3184, 3163, 0));
			return true;
		}
		if (object == 2468 && x == 2655 && y == 4829) { // Earth portal
			player.getActionSender().sendMessage(
					"You step through the portal...");
			player.setTeleportTarget(Location.create(3305, 3472, 0));
			return true;
		}
		if (object == 2469 && x == 2574 && y == 4850) { // Fire portal
			player.getActionSender().sendMessage(
					"You step through the portal...");
			player.setTeleportTarget(Location.create(3312, 3253, 0));
			return true;
		}
		if (object == 2470 && x == 2523 && y == 4825) { // Body portal
			player.getActionSender().sendMessage(
					"You step through the portal...");
			player.setTeleportTarget(Location.create(3052, 3443, 0));
			return true;
		}
		if (object == 2471 && x == 2163 && y == 4833) { // Cosmic portal
			player.getActionSender().sendMessage(
					"You step through the portal...");
			player.setTeleportTarget(Location.create(2410,
					4376 + RANDOM.nextInt(3), 0));

		}
		if (object == 2474 && x == 2282 && y == 4837) { // Chaos portal
			player.getActionSender().sendMessage(
					"You step through the portal...");
			player.setTeleportTarget(Location.create(3058 + RANDOM.nextInt(3),
					3589, 0));
			return true;
		}
		if (object == 2473 && x == 2400 && y == 4834) { // Nature portal
			player.getActionSender().sendMessage(
					"You step through the portal...");
			player.setTeleportTarget(Location.create(2868 + RANDOM.nextInt(3),
					3017, 0));
			return true;
		}
		if (object == 2472 && x == 2464 && y == 4817) { // Law portal
			player.getActionSender().sendMessage(
					"You step through the portal...");
			player.setTeleportTarget(Location.create(2857 + RANDOM.nextInt(3),
					3379, 0));
			return true;
		}
		if (object == 2475 && x == 2208 && y == 4829) { // Death portal
			player.getActionSender().sendMessage(
					"You step through the portal...");
			player.setTeleportTarget(Location.create(1859 + RANDOM.nextInt(3),
					4637, 0));
			return true;
		}
		if (object == 2492
				&& ((x == 2933 && y == 4815) || (x == 2889 && y == 4813)
						|| (x == 2885 && y == 4850) || (x == 2932 && y == 4854))) { // Rune
																					// essense
																					// exit..
			player.getActionSender().sendMessage(
					"You step through the portal...");
			// TODO: Get exit location based on the NPC we used to enter with..
			// Currently, this is just Aubury.
			player.setTeleportTarget(Location.create(3252 + RANDOM.nextInt(2),
					3399 + RANDOM.nextInt(6), 0));
		}
		return false;
	}

	private static final Animation TELE_OTHER_ANIMATION = Animation
			.create(1818);
	private static final Graphic TELE_OTHER_GRAPHICS = Graphic.create(343);

	public static void teleToAbyss(final Player player, final NPC npc) {
		final int[] abyssX = { 3016, 3016, 3021, 3039, 3058, 3063, 3058, 3034 };
		final int[] abyssY = { 4848, 4831, 4815, 4806, 4812, 4827, 4848, 4854 };
		final int loc = RANDOM.nextInt(abyssX.length);
		if (player.isTeleblocked()) {
			player.getActionSender().sendMessage(
					"A magical force stops you from teleporting.");
			return;
		}
		npc.forceChat("Veniens! Sallakar! Rinnesset!");
		npc.playAnimation(TELE_OTHER_ANIMATION);
		npc.playGraphics(TELE_OTHER_GRAPHICS);
		player.playGraphics(TELE_OTHER_GRAPHICS);
		player.setCanWalk(false);
		World.getWorld().submit(new Event(1500) {
			public void execute() {
				// player.animate(1);
				player.setTeleportTarget(Location.create(abyssX[loc],
						abyssY[loc], 0));
				player.getSkulls().setup(20); // Makes the skulling timer 20
												// minutes.
				player.getSkills().setLevel(Skills.PRAYER, 0);
				player.setCanWalk(true);

				this.stop();
			}
		});
	}

	private static final Location[] RUNE_ESSENCE = {
			Location.create(2899, 4818, 0), Location.create(2910, 4832, 0),
			Location.create(2898, 4844, 0), Location.create(2921, 4845, 0),
			Location.create(2924, 4818, 0), };

	public static void teleportToRuneEssenceMiningArea(final Player player,
			NPC npc) {
		// TODO: Save the NPC for teleporting back eventually.
		npc.forceChat("Senventior Disthine Molenko!");
		npc.playAnimation(TELE_OTHER_ANIMATION);
		npc.playGraphics(TELE_OTHER_GRAPHICS);
		player.setCanWalk(false);
		World.getWorld().submit(new Event(1200) {

			@Override
			public void execute() {
				player.setTeleportTarget(RUNE_ESSENCE[RANDOM
						.nextInt(RUNE_ESSENCE.length)]);
				player.setCanWalk(true);
				this.stop();
			}

		});

	}

	private static final Animation KNEEL = Animation.create(1331);
	private static final Animation STAND_UP = Animation.create(1332);

	/**
	 * TODO: Finish this
	 * 
	 * @param player
	 * @param objectID
	 */
	public static boolean handleAbyssObject(final Player player,
			final int objectID) {
		switch (objectID) {
		case 7143: // Rocks
			if (Mining.checkForPick(player) < 1) {
				player.getActionSender().sendMessage(
						"You do not have a pickaxe that you can use.");
				return true;
			}
			player.setCanWalk(false);
			player.getActionSender().sendMessage(
					"You attempt to mine your way through...");
			player.playAnimation(Mining.getMiningAnimation(player));
			World.getWorld().submit(new Event(2500) {
				public void execute() {
					if (RANDOM.nextInt(4) == 0) {
						player.getActionSender().sendMessage(
								"...but fail to break-up the rock.");
					} else {
						player.getActionSender().sendMessage(
								"...and you manage to break through the rock.");
						if (objectID == 7143)
							player.setTeleportTarget(Location.create(3031,
									4820, 0));
						if (objectID == 7153)
							player.setTeleportTarget(Location.create(3047,
									4820, 0));
					}
					player.setCanWalk(true);
					this.stop();
				}
			});
			break;
		case 7148: // Passage - just tele
			player.setTeleportTarget(Location.create(3038, 4846, 0));
			break;
		case 7147: // Gap
		case 7149: // Kneel down: 1331, stand up: 1332
			player.setCanWalk(false);
			player.getActionSender().sendMessage(
					"You attempt to squeeze through the narrow gap...");
			player.playAnimation(KNEEL);
			World.getWorld().submit(new Event(2500) {
				public void execute() {
					if (RANDOM.nextInt(4) == 0) {
						player.getActionSender().sendMessage(
								"...and you manage to crawl through.");
						if (objectID == 7147) {
							player.setTeleportTarget(Location.create(3032,
									4844, 0));
						} else if (objectID == 7149) {
							player.setTeleportTarget(Location.create(3046,
									4844, 0));
						}
					} else {
						player.getActionSender()
								.sendMessage(
										"...but you are not agile enough to get through.");
						player.playAnimation(STAND_UP);
					}
					player.setCanWalk(true);
					this.stop();
				}
			});
			break;
		case 7146: // Eyes - random set of emotes from emotes tab
		case 7150:
			int[] emotes = { 855, 856, 857, 858, 859, 860, 861, 862, 863, 864,
					865, 866, 2113, 2109, 2111, 2106, 2107, 2108, 0x558, 2105,
					2110, 2112, 0x84F, 0x850, 1131, 1130, 1129, 1128, 4275,
					1745, 4280, 4276, 3544, 3543, 2836, 6111, 7531 };
			int index = RANDOM.nextInt(emotes.length);
			player.setCanWalk(false);
			player.getActionSender().sendMessage(
					"You attempt to distract the eyes...");
			player.playAnimation(Animation.create(emotes[index]));
			World.getWorld().submit(new Event(2500) {
				public void execute() {
					if (RANDOM.nextInt(3) == 0) {
						player.getActionSender().sendMessage(
								"...and you manage to sneak through.");
						if (objectID == 7146)
							player.setTeleportTarget(Location.create(3027,
									4840, 0));
						if (objectID == 7150)
							player.setTeleportTarget(Location.create(3052,
									4838, 0));
					} else {
						player.getActionSender().sendMessage(
								"...but you fail to sneak through.");
					}
					player.setCanWalk(true);
					this.stop();
				}
			});
			break;
		case 7145: // Boil - firemaking emote
		case 7151:
			player.getActionSender().sendMessage("Try using another obstacle.");
			break;
		case 7144: // Tendrils - woodcutting emote
		case 7152:
			player.getActionSender().sendMessage("Try using another obstacle.");
			break;

		case 7133: // Nature rift
			teleport(player, 2400, 4835);
			break;
		case 7132: // Cosmic rift
			int[] xCoords = { 2162, 2142, 2122, 2142 };
			int[] yCoords = { 4833, 4853, 4833, 4813 };
			int loc = RANDOM.nextInt(xCoords.length);
			teleport(player, xCoords[loc], yCoords[loc]);
			break;
		case 7141: // Blood rift
			player.getActionSender().sendMessage(
					"A strange power blocks your exit");
			break;
		case 7129: // Fire rift
			teleport(player, 2574, 4849);
			break;
		case 7130: // Earth rift
			teleport(player, 2655, 4830);
			break;
		case 7131: // Body rift
			teleport(player, 2523, 4826);
			break;
		case 7140: // Mind rift
			teleport(player, 2793, 4828);
			break;
		case 7139: // Air rift
			teleport(player, 2842, 4828);
			break;
		case 7138: // Soul rift
			player.getActionSender().sendMessage(
					"A strange power blocks your exit");
			break;
		case 7137: // Water rift
			teleport(player, 2726, 4832);
			break;
		case 7136: // Death rift
			teleport(player, 2208, 4828);
			break;
		case 7135: // Law rift
			teleport(player, 2464, 4818);
			break;
		case 7134: // Chaos rift
			teleport(player, 2281, 4837);
			break;
		default:
			return false;
		}
		return true;
	}

}