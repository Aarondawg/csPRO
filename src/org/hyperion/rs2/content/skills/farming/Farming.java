package org.hyperion.rs2.content.skills.farming;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

/**
 * This class is handling most of the content having to do with Farming.
 * 
 * @author Brown
 */

public class Farming {

	/**
	 * Array containing all the weeded farming paths in game. Those goes full,
	 * nearly full, medium, nearly empty, empty. Please notice the above one was
	 * just something I mixed up.
	 */
	private static final int[][] WEED_PATHS = { { 8573, 8574, 8575, 8576, 8577,
			8578, 8579 }, // Allotment
	};

	/**
	 * Contains all paths with plants spawned when you plant a seed and they
	 * grow. {Good stages, Good stages + water, diseased stages, dead stages};
	 * 
	 */
	public static final int GOOD_SHAPE = 0;
	public static final int GOOD_SHAPE_AND_WATER = 1;
	public static final int DISEASED_SHAPE = 2;
	public static final int DEAD_SHAPE = 3;

	public static final int[][][] PLANT_OBJECTS = { { // Good potatoes //Good
														// potatoes + water
														// //Diseased potatoes
														// //Dead potatoes.
	{ 8558, 8559, 8560, 8561, 8562 }, { 8563, 8564, 8565, 8566 },
			{ 8567, 8568, 8569 }, { 8570, 8571, 8572 } }, };

	private static final int[][] PLANTING_LEVEL_REQS = {
			{ 1 /* Potatoes */, 5 /* Onions */, 7 /* Cabbages */,
					12 /* Tomatoes */, 20 /* Sweetcorn */,
					31 /* Strawberries */, 47 /* Water melons */}, // Allotments
			{ 3 /* Barley */, 4 /* Hammerstone */, 8 /* Asgarian */,
					14 /* Jute */, 16 /* Yanillian */, 21 /* Krandorian */,
					28 /* Wildbloods */}, // Hops
			{ 15 /* Oak */, 30 /* Willow */, 45 /* Maple */, 60 /* Yew */, 75 /* Magic */}, // Trees
			{ 27, /* Apple */33, /* Banana */39, /* Orange trees */42, /*
																	 * Curry
																	 * trees
																	 */51, /* Pineapple */
					57, /* Papaya */68, /* Palm trees */}, // Fruit trees
			{ 10 /* Redberry */, 22 /* Cadava */, 36 /* Dwell */,
					48 /* Janger */, 59 /* White */, 70 /* Poison ivy */}, // Bushes
			{ 2 /* Marigolds */, 11 /* Rosemary */, 23 /* Scarecrow */,
					24 /* Nasturtiums */, 25 /* Woad */, 26 /* Limpwurt */}, // Flowers
			{ 9, /* Guam */14, /* Merrantil */19, /* Tarromin */26, /* Harralander */
					29, /* Goutweed */32, /* Rannar weed */38, /* Toadflax */44, /*
																			 * Irit
																			 * weed
																			 */
					50, /* Avantoe */56, /* Kwuam */62, /* Snapdragon */67, /* Cadantine */
					73, /* Lantadyme */79, /* Dwarf weed */85 /* Torstol */}, // Herbs
			{ 53 /* Bittercrap musrooms */, 55, /* Cacti */63, /* Belladonna */
					72, /* Calquat trees */83 /* Spirit trees */}, // Special
	};

	private static final Item[][] FARMING_SEEDS = {
			{ new Item(5318, 3), /* Potatoes */new Item(5319, 3), /* Onions */
					new Item(5324, 3) /* Cabbages */, new Item(5322, 3), /* Tomatoes */
					new Item(5320, 3), /* Sweetcorn */new Item(5323, 3), /* Strawberries */
					new Item(5321, 3), /* Water melons */}, // Allotments},
			{ new Item(5305, 4), /* Barley */new Item(5307, 4), /* Hammerstone */
					new Item(5308, 4), /* Asgarian */new Item(5306, 3), /* Jute */
					new Item(5309, 3), /* Yanillian */new Item(5310, 3), /* Krandorian */
					new Item(5311, 3) /* Wildbloods */}, // Hops
	// TODO: Use the following trees in like pots, lol. {new Item(5312), /*Oak*/
	// new Item(5313), /*Willow*/ new Item(5314), /*Maple*/ new Item(5315),
	// /*Yew*/ new Item(5316), /*Magic*/ new Item(5317), /*Sprite tree*/},
	// //Trees
	// {27, /*Apple*/ 33, /*Banana*/ 39, /*Orange trees*/ 42, /*Curry trees*/
	// 51, /*Pineapple*/ 57, /*Papaya*/ 68, /*Palm trees*/}, //Fruit trees
	// {10 /*Redberry*/, 22 /*Cadava*/, 36 /*Dwell*/, 48 /*Janger*/, 59
	// /*White*/, 70 /*Poison ivy*/}, //Bushes
	// {2 /*Marigolds*/, 11 /*Rosemary*/, 23 /*Scarecrow*/, 24 /*Nasturtiums*/,
	// 25 /*Woad*/, 26 /*Limpwurt*/}, //Flowers
	// {9, /*Guam*/ 14, /*Merrantil*/ 19, /*Tarromin*/ 26, /*Harralander*/ 29,
	// /*Goutweed*/ 32, /*Rannar weed*/ 38, /*Toadflax*/ 44, /*Irit weed*/ 50,
	// /*Avantoe*/ 56, /*Kwuam*/62, /*Snapdragon*/67, /*Cadantine*/ 73,
	// /*Lantadyme*/ 79, /*Dwarf weed*/85 /*Torstol*/}, //Herbs
	// {53 /*Bittercrap musrooms*/, 55, /*Cacti*/ 63, /*Belladonna*/ 72,
	// /*Calquat trees*/ 83 /*Spirit trees*/}, //Specialf
	};// Fill up with seed id and amount needed to plant.
	private static final Item RAKE = new Item(1); // TODO: Get the real id.
	private static final Item SEED_DIBBER = new Item(1); // TODO: Get the real
															// id.

	public static boolean handleItemOnObject(Player player,
			Location objectLocation, int objectId, Item item) {
		System.out.println("Farming stuff.");
		/*
		 * We check if the object we're using an item on is an unused path.
		 */
		for (int pIndex = 0; pIndex < WEED_PATHS.length; pIndex++) {
			for (int oIndex = 0; oIndex < WEED_PATHS[pIndex].length; oIndex++) {
				if (WEED_PATHS[pIndex][oIndex] == objectId) {
					if (item == RAKE) {
						removeWeeds(player, objectId, pIndex, oIndex,
								objectLocation);
						return true;
					}
					/*
					 * We check if the item used is a seed..
					 */
					for (int i = 0; i < FARMING_SEEDS.length; i++) {
						for (int j = 0; j < FARMING_SEEDS[i].length; i++) {
							if (oIndex == 0) {// Means its an empty patch
								if (FARMING_SEEDS[i][j].getId() == item.getId()) {
									System.out
											.println("The item id was correct.");
									if (player.getSkills().getLevel(
											Skills.FARMING) < PLANTING_LEVEL_REQS[i][j]) {
										player.getActionSender()
												.sendMessage(
														"You need a Farming of level "
																+ PLANTING_LEVEL_REQS[i][j]
																+ " in order to plant this plant.");
										return true;
									}
									if (!player.getInventory().contains(
											SEED_DIBBER)) {
										player.getActionSender()
												.sendMessage(
														"You need a seed dibber in order to plant this plant.");
										return true;
									}
									FarmingPlant plant = new FarmingPlant(
											player, objectLocation, i, j, 10,
											false);
									boolean add = true;
									for (FarmingObject fo : player
											.getFarmingObjectArray()) {
										if (player.replaceFarmingObjects(fo,
												plant)) {
											add = false;
										}
									}
									if (add) {
										player.addToFarmingList(plant);
									}
									return true;
								}
							}

						}
					}
					// If the item wasn't a seed or a rake, we send the nothing
					// interesting happends message.
					player.getActionSender().sendMessage(
							"Nothing interesting happends.");
					return true;// Yet we return true, because I don't think
								// farming paths is used for anything else.
				}
			}

		}
		return false;
	}

	/**
	 * Removes weed from a farming path. Checks if the farming path is without
	 * plants and stuff as well.
	 * 
	 * @param player
	 *            The player trying to remove weed from a path.
	 * @param path
	 *            The object id the player used his rake with.
	 * @param pathIndex
	 *            The path index.
	 * @param pathIndex1
	 *            The second path index.
	 * @param objectLocation
	 */
	private static void removeWeeds(Player player, int path, int pathIndex,
			int pathIndex1, Location objectLocation) {

		// TODO Auto-generated method stub

	}

}
