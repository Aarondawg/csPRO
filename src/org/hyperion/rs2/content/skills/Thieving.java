package org.hyperion.rs2.content.skills;

import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.Damage.HitType;
import org.hyperion.rs2.model.region.Region;

public class Thieving {

	private static final int[] THIEVING_STALLS = {
	// vegatable stall??
	2561, // Bakers stall..
	};

	private static final int[] THIEIVNG_STALL_LEVEL_REQUIREMENTS = {
	// -1, vegatable stall??
	5, // Bakers stall..
	};

	private static final Item[][] THIEVING_STALL_LOOT = {
	// {}, //vegatable stall??
	{ new Item(2309) /* Bread */, new Item(1891) /* Cake */, new Item(1893), /*
																			 * 2/
																			 * 3
																			 * cake
																			 */
			new Item(1895), /* Slice of cake. */new Item(1897) /*
																 * Chokolade
																 * cake
																 */,
			new Item(1899), /* 2/3 chokolade cake */new Item(1901) /*
																	 * HOPEFULLY
																	 * SLICE OF
																	 * CHOKOLADE
																	 * CAKE
																	 */}, // Bakers
																			// stall..
	};

	private static final double[] THIEVING_EXPERIENCE = {
	// -1, vegatable stall??
	16, // Bakers stall.
	};

	public static boolean handleObjectClick(final Player player,
			final GameObject o) {
		if (o == null) {
			System.out.println("The fuck?");
			return false;
		}
		for (int index = 0; index < THIEVING_STALLS.length; index++) {
			if (THIEVING_STALLS[index] == o.getDefinition().getId()) {
				if (player.getSkills().getLevel(Skills.THIEVING) < THIEIVNG_STALL_LEVEL_REQUIREMENTS[index]) {
					player.getActionSender().sendMessage(
							"You need a Thieving level of "
									+ THIEIVNG_STALL_LEVEL_REQUIREMENTS[index]
									+ " in order to steal from this stall.");
					return true;
				}
				final Region[] regions = World.getWorld().getRegionManager()
						.getSurroundingRegions(o.getLocation());
				player.getActionSender().sendMessage(
						"You attempt to steal from the stall..");
				for (final Region r : regions) {
					for (final NPC n : r.getNpcs()) {
						if (n.getLocation()
								.withinRange(player.getLocation(), 5)
								&& n.getLocation().hasLineOfSight(
										player.getLocation())) {
							n.forceChat("Hey! Get away from there!");
							if (n.getDefinition().isAttackable()) {
								Combat.attack(n, player);
							}
							return true;
						}
					}
				}
				player.playAnimation(Animation.create(881));
				final int fIndex = index;
				player.getActionQueue().addAction(new Action(player, 1200) {

					@Override
					public QueuePolicy getQueuePolicy() {
						return QueuePolicy.NEVER;
					}

					@Override
					public WalkablePolicy getWalkablePolicy() {
						return WalkablePolicy.NON_WALKABLE;
					}

					@Override
					public void execute() {
						Item loot = THIEVING_STALL_LOOT[fIndex][r
								.nextInt(THIEVING_STALL_LOOT[fIndex].length)];
						if (player.getInventory().add(loot)) {
							player.getActionSender().sendMessage(
									"You successfully stole a "
											+ loot.getDefinition().getName()
													.toLowerCase() + ".");
							player.getSkills().addExperience(Skills.THIEVING,
									THIEVING_EXPERIENCE[fIndex]);
							for (final Region r : regions) {
								for (final Player p : r.getPlayers()) {
									p.getActionSender().sendCreateObject(634,
											o.getType(), o.getRotation(),
											o.getLocation());
								}
							}
							World.getWorld().submit(new Event(5000) { // TODO:
																		// Array
																		// with
																		// the
																		// real
																		// delay?

										@Override
										public void execute() {
											for (final Region r : regions) {
												for (final Player p : r
														.getPlayers()) {
													p.getActionSender()
															.sendCreateObject(
																	o.getDefinition()
																			.getId(),
																	o.getType(),
																	o.getRotation(),
																	o.getLocation());
												}
											}
											this.stop();
										}

									});
							this.stop();
						} else {
							player.getActionSender()
									.sendMessage(
											"You don't have enough space in your inventory.");
						}
					}

				});
				return true;
			}
		}
		return false;
	}

	private static final Animation THIEVING_ANIMATION = Animation.create(881);
	private static final Graphic BIRDS_WHEN_STUNNED = Graphic.create(80,
			(100 << 16));
	private static final Random r = new Random();

	public static void npcThievingEvent(final Player player, final NPC npc,
			final int lootId, final int lootAmount, final double exp,
			final int stunnedHit, final boolean failed) {
		if (player == null) {
			return;
		}
		if (player.getTemporaryAttribute("Stunned") != null) {
			player.getActionSender().sendMessage("You're stunned..!");
			return;
		}
		if (System.currentTimeMillis() - player.lastNPCClick <= 2200) {
			return;
		}
		player.playAnimation(THIEVING_ANIMATION);
		player.getActionSender().sendMessage(
				"You attempt to steal from the "
						+ npc.getDefinition().getName().toLowerCase() + ".");
		World.getWorld().submit(new Event(1500) {
			public void execute() {
				if (failed) {
					npc.forceChat("What do you think you're doing?!!");
					npc.setInteractingEntity(player);
					// TODO: Npc should walk away..?
					player.getActionSender().sendMessage("You were stunned.");
					player.playGraphics(BIRDS_WHEN_STUNNED);
					HitType hitType = HitType.NORMAL_DAMAGE;
					if (stunnedHit == 0) {
						hitType = HitType.NO_DAMAGE;
					}
					Hit hit = new Hit(stunnedHit, hitType);
					Combat.inflictDamage(player, null, hit);// 0 or 1.
					player.setCanWalk(false);
					player.setTemporaryAttribute("Stunned", player);
					World.getWorld().submit(new Event(7000) {
						public void execute() {
							player.setCanWalk(true);
							player.removeTemporaryAttribute("Stunned");
							this.stop();
						}
					});
				} else {
					if (lootId == -1 || lootAmount == -1) {
						// Logger.getInstance().warning("Didnt get correct values from the thieving arrays.");
						this.stop();
						return;
					}
					player.getActionSender().sendMessage(
							"You receive some "
									+ ItemDefinition.forId(lootId).getName()
											.toLowerCase() + ".");
					player.getInventory().add(
							new Item(lootId, lootId == 995 ? lootAmount
									* Skills.EXP_RATE : lootAmount));
					player.getSkills().addExperience(Skills.THIEVING, exp);
				}
				this.stop();
			}
		});
		player.lastNPCClick = System.currentTimeMillis();
	}

	public static boolean checkForThieving(Player player, NPC npc) {
		int npcId = npc.getDefinition().getId();
		for (int index = 0; index < NPCS.length; index++) {
			for (int i = 1; i < NPCS[index].length; i++) {
				if (NPCS[index][i] == npcId) {
					pickPocketing(player, npc, NPCS[index][0], index);
					return true;
				}
			}
		}
		return false;
	}

	private static void pickPocketing(Player player, NPC npc,
			int levelRequired, int index) {
		if (player.getSkills().getLevel(Skills.THIEVING) < levelRequired) {
			player.getActionSender().sendMessage(
					"You need a Thieving level of at least " + levelRequired
							+ " to steal from this npc.");
			return;
		}
		boolean failed = r.nextInt(levelRequired + 1) > r.nextInt(player
				.getSkills().getLevel(Skills.THIEVING) + 1);
		int lootId = -1;
		int lootAmount = -1;
		int stunnedHit = 1;
		if (!failed) {
			int random = r.nextInt(LOOT[index].length);
			if (random % 2 == 0) { // Even, which means this is the "lootId".
				lootId = LOOT[index][random];
				lootAmount = LOOT[index][random + 1];
			} else { // Obviously uneven, which means this is the "lootAmount".
				lootAmount = LOOT[index][random];
				lootId = LOOT[index][random - 1];
			}
			if (lootAmount < 0) { // If the looting amount is negative, it means
									// that it should be random.
				lootAmount = 1 + r.nextInt(lootAmount * (-1)); // Get the
																// positive
																// value, and
																// make it
																// random we
																// plus it by
																// one, to make
																// sure its not
																// 0..
			}
			if (!player.getInventory().hasRoomFor(new Item(lootId, lootAmount))) {
				player.getActionSender().sendMessage(
						"You dont have enough room in your inventory.");
				return;
			}
		} else {
			stunnedHit = (index == 2 || index == 3 || index == 4) ? r
					.nextInt(STUNNING_HIT[index] + 1) : STUNNING_HIT[index];
		}
		npcThievingEvent(player, npc, lootId, lootAmount, NPC_EXP[index],
				stunnedHit, failed);
	}

	public static final double[] NPC_EXP = { 8, 14.5, 18.5, 22.2, 22.2, 26,
			36.5, 40, 43, 46.8, 65, 65, 79.4, 84.3, 84.3, 137.5, 137.5, 151.8,
			198.3, 273.3, 353.3 };

	public static final int[] STUNNING_HIT = { 1, 1, 2, 3, 3, 2, 2, 1, 3, 2, 2,
			5, 3, 3, 5, 3, 5, 4, 1, 4, 5 };

	public static final int[][] LOOT = {
			{ 995, 3 },
			{ 995, 9, 5318, -100 }, // TODO: Level 1 clue scroll..
			{ 995, -15, 590, 1, 1627, 1625, 1, 1623, 1, 1621, 1, 321, 1, 2318,
					1, 4298, 1, 4300, 1, 4302, 1, 4304, 1, 4306, 1, 1267, 1,
					685, 1, 688, 1, 697, 199, 1, 201, 1, 203, 1 }, // Todo:Clue-
																	// Level 1
																	// and
																	// Herbs.
			{ 995, -15, 590, 1, 1627, 1625, 1, 1623, 1, 1621, 1, 321, 1, 2318,
					1, 4298, 1, 4300, 1, 4302, 1, 4304, 1, 4306, 1, 1267, 1,
					685, 1, 688, 1, 697, 199, 1, 201, 1, 203, 1 },// todo:clue-
																	// level 1
																	// and
																	// herbs.
			{ 995, -50, 1739, 1, 1167, 1, 1129, 1, 1095, 1151, 1, 1173, 1, 590,
					1, 1627, 1625, 1, 1623, 1, 1621, 1, 321, 1, 2318, 1, 4298,
					1, 4300, 1, 4302, 1, 4304, 1, 4306, 1, 1267, 1, 685, 1,
					688, 1, 697, 199, 1, 201, 1, 203, 1 },// todo:clue- level 1
															// and herbs.
			{ 995, 18 },
			{ 556, 8, 1219, 1, 1523, 1, 2357, 1, 7919, 1, 995, -45 },
			{ 7548, -50, 7550, -50, 5096, -50, 5097, -50, 5098, -50, 5099, -50,
					5100, -50, 5101, -50, 5102, -50, 5103, -50, 5104, -50,
					5105, -50, 5106, -50, 5291, -50, 5292, -50, 5293, -50,
					5294, -50, 5295, -50, 5296, -50, 5297, -50, 5298, -50,
					5299, -50, 5300, -50, 5301, -50, 5302, -50, 5303, -50,
					5304, -50, 5305, -50, 5306, -50, 5307, -50, 5308, -50,
					5309, -50, 5310, -50, 5311, -50, 5317, -50, 5318, -50,
					5319, -50, 5320, -50, 5321, -50, 5322, -50, 5323, -50,
					5324, -50 }, { 995, 30 }, { 995, 40 }, { 995, 40 },
			{ 179, 1, 1523, 1, 995, 30 }, { 995, 50 }, { 995, 50 },
			{ 995, 60, 2309, 1 }, { 995, 60 }, { 995, 80, 562, 2 },
			{ 995, -400, 2150, 1, 680, 1, 569, 1, 557, 1, 2162, 1 },
			{ 1601, 1, 1993, 1, 560, 2, 995, -300, 565, 1, 680, 1, 569, 1 },
			{}, // TODO: Elves.
	};

	/**
	 * Array of arrays containing thieveable npcs. Index is the same as the
	 * array of arrays above. Layout is levelreq, npc, npc, npc, npc and so on.
	 */
	public static final int[][] NPCS = {
			{ 1, 1, 2, 3, 4, 5, 6, 16, 24, 25, 170 }, // Men and Women.
			{ 10, 7, 1757, 1758 }, // Farmers.
			{ 15, 1715 }, // ham member(female).
			{ 20, 1714 }, // ham member (male).
			{ 23, 1710, 1711, 1712 },// ham guard.
			{ 25, 15, 18 }, // warriors.
			{ 32, 187 }, // Rogue
			{ 38, 2234, 2235 }, // Master farmer.
			{ 40, 9, 32 }, // guard.
			{}, // TODO: Fremennik Citizens
			{ 45, 1882, 1883 },// Bandits.
			{ 53, 1926, 1931 },// Desert bandit.
			{ 55, 23, 26 }, // Ardounge knights.
			{ 55, 1880, 1881 }, // Bandit(Level 56)
			{ 65, 34 }, // Watchman
			{ 65, 1905 }, // Menaphite Thug
			{ 70, 2256 }, // Paladin.
			{ 75, 66, 67, 68 }, // Gnomes.
			{ 80, 21 }, // Hero.
			{}, // TODO: Elves.
	};

}
