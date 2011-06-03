package org.hyperion.rs2.content.skills;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;

/**
 * Fishing skill handler
 * 
 * @author Brown
 */
public class Fishing {

	/**
	 * A map of all the FishingSpots we have in the game.
	 */
	private static final Map<Location, NPC> FISHING_SPOTS = new HashMap<Location, NPC>();

	/**
	 * A list of all the Whirlpools we have in the game.
	 */
	private static final List<Location> WHIRLPOOLS = new ArrayList<Location>();

	/**
	 * Used to get the whirl pool id for NPC ids.
	 */
	private static Map<Integer, Integer> WHIRL_POOL_MAP = new HashMap<Integer, Integer>();

	/**
	 * The java.util.Random instance used for this class.
	 */
	private static final Random r = new Random();

	/**
	 * A simple resetting animation.
	 */
	private static final Animation RESET = Animation.create(-1);

	/**
	 * Our fishing action.
	 */
	public static class FishingAction extends Action {

		/**
		 * Constructs the fishing action.
		 * 
		 * @param fisher
		 *            The fisher.
		 * @param spot
		 *            The fishing spot.
		 * @param currentSpotLocation
		 *            The location we're fishing at.
		 */
		public FishingAction(Player fisher, FishingSpot spot,
				Location currentSpotLocation) {
			super(fisher, 600/* Slight delay when we start up. */);
			this.spot = spot;
			this.currentSpotLocation = currentSpotLocation;
		}

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
			/*
			 * The player currently fishing.
			 */
			Player player = getPlayer();

			/*
			 * We check if there's a whirl pool at the spot, set our own
			 * whirlpool flag to true, and reset the cycles.
			 */
			if (!whirlpool && containsWhirlpool(currentSpotLocation)) {
				cycles = 0;
				whirlpool = true;
			}

			/*
			 * Then we check our own whirlpool flag, and see if the cycle is
			 * above or equals 5.
			 */
			if (whirlpool) {
				if (!containsWhirlpool(currentSpotLocation)) {
					whirlpool = false;
				} else if (cycles >= 5) {
					/*
					 * If so, we remove the tool from the players inventory.
					 */
					player.getInventory().remove(spot.getTool());
				}
			}

			/*
			 * A player will only stop fishing if the fishing spot is moving
			 * around, or he walks away (Which is done automaticly)
			 */
			if (!spotExists(currentSpotLocation, spot) && !whirlpool) {
				this.stop();
				return;
			}

			/*
			 * We make sure the player is facing towards the spot.
			 */
			player.face(currentSpotLocation);

			/*
			 * This is the minimum level requirement dealing with the spot we're
			 * fishing at.
			 */
			final int levelReq = spot.getLevels()[0];

			/*
			 * We check if the players fishing level is high enough to fish at
			 * this spot.
			 */
			if (player.getSkills().getLevel(Skills.FISHING) < levelReq) {
				player.getActionSender().sendMessage(
						"You need a Fishing level of at least " + levelReq
								+ " in order to fish at this spot.");
				player.playAnimation(RESET);
				this.stop();
				return;
			}

			/*
			 * If we somehow lost our fishing tool, we stop the action as well.
			 * is moving around, or he walks away.
			 */
			if (!player.getInventory().contains(spot.getTool())) {
				player.getActionSender().sendMessage(
						"You need a "
								+ spot.getTool().getDefinition().getName()
										.toLowerCase()
								+ " in order to fish at this spot.");
				player.playAnimation(RESET);
				this.stop();
				return;
			}

			/*
			 * We make sure the players inventory contains the bait needed (In
			 * case we need some to fish at the spot)
			 */
			if (spot.getBait() != null) {
				if (!player.getInventory().contains(spot.getBait())) {
					player.getActionSender().sendMessage(
							"You need more "
									+ spot.getBait().getDefinition().getName()
											.toLowerCase()
									+ " in order to fish at this spot.");
					player.playAnimation(RESET);
					this.stop();
					return;
				}
			}

			/*
			 * If we're been in the cycle for a number of times remaindable with
			 * 2 (tick(600) * 4 ms = 2400 ms), we play the animation.
			 */
			player.playAnimation(spot.getAnimation());

			/*
			 * We calculate the max index of the fish we can fish with our
			 * current fishing level.
			 */
			int maxIndex = 0;
			for (int level : spot.getLevels()) {
				if (player.getSkills().getLevel(Skills.FISHING) >= level) {
					maxIndex++;
				}
			}

			/*
			 * We get a random index.
			 */
			int index = r.nextInt(maxIndex);

			/*
			 * We get a fish using this index.
			 */
			Item fish = spot.getFish()[index];

			/*
			 * We have sure the player have space for this fish..
			 */
			if (!player.getInventory().hasRoomFor(fish)) {
				player.playAnimation(RESET);
				player.getActionSender().sendMessage(
						"You don't have enough space in your inventory.");
				this.stop();
				return;
			}

			/*
			 * We check if the fisher receives this fish, based on his fishing
			 * level etc.
			 */
			if (!shouldBurn(player.getSkills().getLevel(Skills.FISHING),
					spot.getLevels()[index] + 25)) {
				// if(r.nextInt(30) <
				// player.getSkills().getLevel(Skills.FISHING) -
				// spot.getLevels()[index]) {
				player.getInventory().add(fish);
				player.getActionSender()
						.sendMessage(
								"You catch "
										+ (fish.getId() == 321
												|| fish.getId() == 317 ? "some "
												: "a ")
										+ fish.getDefinition().getName()
												.toLowerCase()
												.replace("raw ", "") + ".");
				player.getSkills().addExperience(Skills.FISHING,
						spot.getExperience()[index]);
				if (spot.getBait() != null) {
					player.getInventory().remove(spot.getBait());
				}
			}

			/*
			 * The first delay is 0, so we start fishing instantly, but we want
			 * to delay it a bit.
			 */
			this.setDelay(3000);

			/*
			 * We increase the cycle.
			 */
			cycles++;
		}

		/**
		 * Calculates the chance of burning any item.
		 * 
		 * @param playerLevel
		 *            The current cooking level of the Player.
		 * @param noBurnLevel
		 *            The level to stop burning the food, the player is
		 *            currently cooking.
		 * @return True if the food should burn, false if not.
		 */
		public static boolean shouldBurn(int playerLevel, int noBurnLevel) {
			int levelsToStopBurn = noBurnLevel - playerLevel;
			if (levelsToStopBurn > 20) {
				levelsToStopBurn = 20; // Makes the chance of burning
										// approximatly 60%.
			}
			return r.nextInt(34) <= levelsToStopBurn;
		}

		/**
		 * Whirlpool flag.
		 */
		private boolean whirlpool = false;

		/**
		 * The cycles.
		 */
		private int cycles = 0;

		/**
		 * The location we're fishing at.
		 */
		private final Location currentSpotLocation;

		/**
		 * The fishing spot we're fishing at.
		 */
		private final FishingSpot spot;

	}

	public static enum FishingSpot {

		/*
		 * Contains Shrimps and Anchovies.
		 */
		NORMAL_NETTING(new int[] { 316 }, 303 /* Small fishing net */, -1 /*
																		 * No
																		 * bait
																		 */,
				621, new int[] { 1, 15 },
				new int[] { 317/* Shrimp */, 321 /* Anchovies */}, new double[] {
						10, 50 }),

		/*
		 * Contains Sardines and Herrings.
		 */
		SEA_BAITING(new int[] { 316 }, 307 /* Fishing rod */, 313 /*
																	 * Fishing
																	 * bait
																	 */,
				622/* NOT SURE */, new int[] { 5, 10 }, new int[] {
						327 /* Sardine */, 345 /* Herring */}, new double[] {
						20, 30 }),

		/*
		 * Contains Trouts, Salmons and Rainbow fish (only with stripy feathers)
		 * //TODO: Rainbow fish support? LOL.
		 */
		FLY_LURING(new int[] { 309 }, 309 /* Fly fishing rod */, 314/* Feather */,
				622, new int[] { 20, 30 },
				new int[] { 335 /* Trout */, 331 /* Salmon */}, new double[] {
						50, 70 }),

		/*
		 * Contains Pikes.
		 */
		RIVER_BAITING(new int[] { 309 }, 307 /* Fishing rod */, 313 /*
																	 * Fishing
																	 * bait
																	 */,
				622/* NOT SURE */, new int[] { 25 },
				new int[] { 349 /* Pike */}, new double[] { 60 }),

		/*
		 * Contains lobsters obviously.
		 */
		LOBSTER_CAGING(new int[] { 312 }, 301 /* Lobster pot */, -1/* No bait */,
				619, new int[] { 40 }, new int[] { 377 /* Lobster */},
				new double[] { 90 }),

		/*
		 * Contains Tuna and Swordfishes.
		 */
		HARPOONING(new int[] { 312 }, 311 /* Harpoon */, -1/* No bait */, 618,
				new int[] { 35, 50 },
				new int[] { 359 /* Tuna */, 371 /* Swordfish */}, new double[] {
						80, 100 }),

		// TODO: Casket, Oyster Seaweed, Leather boots/Gloves, (Maybe more?)
		// support.
		/*
		 * Contains Mackerel, Cod, Bass, Casket, Seaweed as well as Junk.
		 */
		BIG_NETTING(new int[] { 313 }, 305 /* Big fishing net */,
				-1/* No bait */, 620, new int[] { 16, 23, 46 }, new int[] {
						353 /* Mackerel */, 341 /* Cod */, 363 /* Bass */},
				new double[] { 20, 45, 100 }),

		/*
		 * Contains sharks obviously.
		 */
		SHARK_HARPOONING(new int[] { 313 }, 311 /* Harpoon */, -1/* No bait */,
				618, new int[] { 76 }, new int[] { 383 /* Shark */},
				new double[] { 110 }),

		/*
		 * Contains monkfishes obviously.
		 */
		MONKFISH_NETTING(new int[] {}, 303 /* Small fishing net */, -1/*
																		 * No
																		 * bait
																		 */,
				621, new int[] { 62 }, new int[] { 7944 }, new double[] { 120 });

		/**
		 * Constructs a fishing spot.
		 */
		private FishingSpot(int[] npcIds, int tool, int bait, int animation,
				int[] levels, int[] fish, double[] exp) {
			this.npcIds = npcIds;
			this.tool = new Item(tool);
			this.bait = bait == -1 ? null : new Item(bait);
			this.animation = Animation.create(animation);
			this.levels = levels;
			Item[] fishes = new Item[fish.length];
			for (int index = 0; index < fish.length; index++) {
				fishes[index] = new Item(fish[index]);
			}
			this.fish = fishes;
			this.exp = exp;
		}

		/**
		 * Gets the bait needed to fish at this spot.
		 * 
		 * @return <code>null</code> if none, else - the fishing bait.
		 */
		public Item getBait() {
			return bait;
		}

		/**
		 * Gets all the fishes for this spot.
		 * 
		 * @return the fishes the spot contains.
		 */
		public Item[] getFish() {
			return fish;
		}

		/**
		 * Gets the animation for this fishing spot.
		 * 
		 * @return The animation for this spot.
		 */
		public Animation getAnimation() {
			return animation;
		}

		/**
		 * Gets the level requirement array for this spot.
		 * 
		 * @return The level requirements.
		 */
		public int[] getLevels() {
			return levels;
		}

		/**
		 * Gets the experience array for this spot.
		 * 
		 * @return The spots experience rewards.
		 */
		public double[] getExperience() {
			return exp;
		}

		/**
		 * Gets the tool needed to fish at this spot.
		 * 
		 * @return The needed tool.
		 */
		public Item getTool() {
			return tool;
		}

		/**
		 * Gets the NPC id's that belongs to this spot.
		 * 
		 * @return The NPC id's.
		 */
		public int[] getNPCIds() {
			return npcIds;
		}

		/**
		 * The NPC ids of this spot.
		 */
		private final int[] npcIds;

		/**
		 * The tool needed in order to fish at this spot.
		 */
		private final Item tool;

		/**
		 * The level requirements for this spot - based on the fish's index.
		 * {smallest level, then increasing.}
		 */
		private final int[] levels;

		/**
		 * The fishes we can fish at this spot.
		 */
		private final Item[] fish;

		/**
		 * The experience reward for this spot - based on the fish's index.
		 */
		private final double[] exp;

		/**
		 * The animation for this spot.
		 */
		private final Animation animation;

		/**
		 * The fishing bait needed for this spot.
		 */
		private final Item bait;

		/**
		 * Gets a fishing by an NPC id + packet option.
		 * 
		 * @param id
		 *            The NPC id.
		 * @return The FishingSpot, or <code>null</code> if the NPC is not a
		 *         FishingSpot.
		 */
		public static FishingSpot getSpot(int id, int option) {
			/*
			 * NPC option 1.
			 */
			if (option == 1) {
				switch (id) {
				/*
				 * Lure/Bait spot.
				 */
				case 309:
					return FLY_LURING;
					/*
					 * Lobster/Swords and tuna spot.
					 */
				case 312:
					return LOBSTER_CAGING;
					/*
					 * Net /baiting spot.
					 */
				case 316:
					return NORMAL_NETTING;
					/*
					 * Net/harpooning spot.
					 */
				case 313:
					return BIG_NETTING;
				}
				/*
				 * NPC option 2.
				 */
			} else {
				switch (id) {
				/*
				 * Lure/Bait spot.
				 */
				case 309:
					return RIVER_BAITING;
					/*
					 * Lobster/Swords and tuna spot.
					 */
				case 312:
					return HARPOONING;
					/*
					 * Net /baiting spot.
					 */
				case 316:
					return SEA_BAITING;
					/*
					 * Net/harpooning spot.
					 */
				case 313:
					return SHARK_HARPOONING;
				}
			}
			return null;
		}
	}

	/**
	 * This is basically a location set for each "fishing place" (Barbarian,
	 * Draynor, two spots in Camelot, Karamja, etc)
	 */
	private static final Location[][] FISHING_SPOT_LOCATION_SETS = {
			{ Location.create(3110, 3434, 0), Location.create(3110, 3433, 0),
					Location.create(3110, 3432, 0),
					Location.create(3104, 3425, 0),
					Location.create(3104, 3424, 0) }, // Barbarian village.
			{ Location.create(3085, 3231, 0), Location.create(3085, 3230, 0),
					Location.create(3086, 3228, 0),
					Location.create(3086, 3227, 0) }, // Draynor village.
			{ Location.create(2926, 3176, 0), Location.create(2926, 3177, 0),
					Location.create(2926, 3178, 0),
					Location.create(2926, 3179, 0),
					Location.create(2926, 3180, 0),
					Location.create(2925, 3181, 0),
					Location.create(2924, 3181, 0),
					Location.create(2923, 3180, 0),
					Location.create(2923, 3179, 0),
					Location.create(2923, 3178, 0),
					Location.create(2921, 3178, 0) }, // Karamja.
			{ Location.create(2860, 3426, 0), Location.create(2859, 3426, 0),
					Location.create(2855, 3423, 0),
					Location.create(2854, 3423, 0),
					Location.create(2853, 3423, 0),
					Location.create(2845, 3429, 0),
					Location.create(2844, 3429, 0),
					Location.create(2838, 3431, 0),
					Location.create(2837, 3431, 0),
					Location.create(2836, 3431, 0) }, // Catherby
	/*
	 * {}, //Catherby north {}, //Catherby south {}, //Fishing guild ---- stuff.
	 * :D
	 */
	};

	/**
	 * This basically contains all the IDs to spawn - in the spots above.
	 */
	private static final int[][] FISHING_SPOT_IDS = { { 309, 309, 309 }, // Barbarian
																			// village.
			{ 316, 316 }, // Draynor village.
			{ 312, 312, 312, 316, 316 }, // Karamja.
			{ 313, 313, 313, 312, 312, 312 }, // Catherby
	/*
	 * {}, //Catherby north {}, //Catherby south {}, //Fishing guild ---- stuff.
	 * :D
	 */
	};

	/**
	 * Fishing spots will be the only NPC spawns that we "hardcode" - which we
	 * do in this method.
	 */
	public static void spawnFishingSpots() {
		for (int index = 0; index < FISHING_SPOT_IDS.length; index++) {
			for (int index2 = 0; index2 < FISHING_SPOT_IDS[index].length; index2++) {
				NPC npc = NPC.create(
						NPCDefinition.forId(FISHING_SPOT_IDS[index][index2]),
						FISHING_SPOT_LOCATION_SETS[index][index2], null, null);
				npc.setWalkingType(0);
				World.getWorld().getNPCs().add(npc);
				FISHING_SPOTS.put(FISHING_SPOT_LOCATION_SETS[index][index2],
						npc);
			}
		}
		World.getWorld().submit(new FishingSpotEvent());
	}

	/**
	 * Checks if a specific location contains a given FishingSpot.
	 * 
	 * @param loc
	 *            The location we're checking.
	 * @param spot
	 *            The fishing spot we want to check for.
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	private static boolean spotExists(Location loc, FishingSpot spot) {
		NPC npc = FISHING_SPOTS.get(loc);
		if (npc != null) {
			for (int id : spot.getNPCIds()) {
				if (npc.getDefinition().getId() == id) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if a specific location contains a Whirlpool.
	 * 
	 * @param currentSpotLocation
	 *            The location we're checking.
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public static boolean containsWhirlpool(Location currentSpotLocation) {
		return WHIRLPOOLS.contains(currentSpotLocation);
	}

	/**
	 * Takes care of the fishing spots moving around.
	 */
	private static class FishingSpotEvent extends Event {

		/**
		 * Creates the fishing spot event, and makes it run every 600
		 * milliseconds.
		 */
		public FishingSpotEvent() {
			super(600);
		}

		@Override
		public void execute() {
			if (r.nextInt(20) == 0) {
				/*
				 * For each "place" of fishing spots..
				 */
				for (Location[] array : FISHING_SPOT_LOCATION_SETS) {
					/*
					 * We create a list of the locations we actually have spots
					 * at.
					 */
					List<Location> spots = new ArrayList<Location>();
					/*
					 * As well as one without any spots in currently.
					 */
					List<Location> free = new ArrayList<Location>();
					/*
					 * By looping through all the locations at this "place".
					 */
					for (Location loc : array) {
						/*
						 * And check if the map contains them.
						 */
						if (FISHING_SPOTS.containsKey(loc)) {
							spots.add(loc);
						} else {
							free.add(loc);
						}
					}
					/*
					 * Great, now we have our lists, and we pick a random
					 * fishing spot to move..
					 */
					if (spots.size() == 0) { // This will never be the case when
												// we're done adding everything.
						return;
					}
					final Location currentLoc = spots.get(r.nextInt(spots
							.size()));
					final NPC spot = FISHING_SPOTS.get(currentLoc);
					/*
					 * Oops, we create a Whirlpool :o
					 */
					if (r.nextInt(10) == 0 && !WHIRLPOOLS.contains(currentLoc)) {
						spot.setInvisible(true);
						WHIRLPOOLS.add(currentLoc);
						final NPC npc = NPC.create(NPCDefinition
								.forId(WHIRL_POOL_MAP.get(spot.getDefinition()
										.getId())), currentLoc, null, null);
						npc.setWalkingType(0);
						World.getWorld().getNPCs().add(npc);
						World.getWorld().submit(
								new Event((r.nextInt(4) + 1) * 3000) {

									@Override
									public void execute() {
										WHIRLPOOLS.remove(currentLoc);
										World.getWorld().unregister(npc);
										spot.setInvisible(false);
										this.stop();
									}

								});
					} else {
						FISHING_SPOTS.remove(currentLoc);
						final Location newLoc = free
								.get(r.nextInt(free.size()));
						spot.setTeleportTarget(newLoc);
						FISHING_SPOTS.put(newLoc, spot);
					}

				}
			}

		}

	}

	static {
		/*
		 * Lure/Bait spot.
		 */
		WHIRL_POOL_MAP.put(309, 403);

		/*
		 * Lobster/Swords and tuna spot.
		 */
		WHIRL_POOL_MAP.put(312, 405);

		/*
		 * Net /baiting spot.
		 */
		WHIRL_POOL_MAP.put(316, 404);

		/*
		 * Net/harpooning spot.
		 */
		WHIRL_POOL_MAP.put(313, 406);
	}

}