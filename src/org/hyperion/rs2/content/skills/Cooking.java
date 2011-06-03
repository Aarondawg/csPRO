package org.hyperion.rs2.content.skills;

import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.quest.Quest;
import org.hyperion.rs2.content.quest.QuestHandler;
import org.hyperion.rs2.content.quest.impl.TutorialIsland;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Equipment;

public class Cooking {

	/**
	 * The cooking interface.
	 */
	public static final int COOKING_INTERFACE = 307;

	/**
	 * A static instance of the Random object provided by Sun. Use this as
	 * r.nextInt(maxVal);
	 */
	private static final Random r = new Random();

	/**
	 * An array holding all the objects we can cook on. Eg: Range, Stove, Fire
	 * etc. Horrible conventions xD
	 */
	private static final int[][] COOKING_OBJECTS = {
			{ 114/* Cooking range */, 2728/* Range */, 2729/* Range */,
					2730/* Range */, 2731/* Range */, 2859/* Range */, 3039/*
																		 * Range
																		 * (in
																		 * Falador
																		 * Castle
																		 * upstairs
																		 * I
																		 * guess
																		 * )
																		 */,
					4172/* Cooking range. */, 5275/* Cooking range. */, 8750/*
																		 * Cooking
																		 * range
																		 * .
																		 */,
					9682/* Range */, 9683/* Range chimney???? */,
					12102/* Range */, 9085/* Stove */, 9086/* Stove */,
					9087/* Stove */, 9088/* Stove */, 12269/*
														 * Stove (for POH i
														 * guess)
														 */, 13539/*
																 * Iron range
																 * (for POH i
																 * guess)
																 */, 13540/*
																		 * Iron
																		 * range
																		 * (for
																		 * POH i
																		 * guess
																		 * )
																		 */,
					13541/* Iron range (for POH i guess) */,
					13542/* Fancy range?? */, 13543/* Fancy range?? */, 13544/*
																		 * Fancy
																		 * range
																		 * ??
																		 */,
					14919/* Range */, 12312 /* Range */
			}, { 2732, // Regulair fire.
			} };

	/**
	 * An array holding the raw items we want to cook.
	 */
	private static final Item[] RAW_ITEMS = { new Item(2132), // Raw beef.
			new Item(4287), // Raw beef - Green one from the ghost land.
			new Item(2134), // Raw rat meat.
			new Item(2136), // Raw bear meat.
			new Item(2138), // Raw chicken.
			new Item(4289), // Raw chicken - Green one from the ghost land.
			new Item(321), // Raw anchovies
			new Item(317), // Raw shrimps
			new Item(3150), // Karambwanji
			new Item(1859), // Ugthanki
			new Item(2307), // Bread
			new Item(327), // Sardine
			// Crayfish ??
			// new Item(3142), //Karambwan, As this thing have two cooking
			// results, it should be hardcoded :S
			// Ugthanki Kebab
			new Item(3226), // Rabbit
			new Item(345), // Herring
			new Item(1942), // Potato
			// Spicy Sauce ??
			new Item(2321), // Uncooked berry pie (Hopefully redberry)
			new Item(353), // Mackerel
			// Chilli Con Carne ??
			new Item(3363), // Thin snail.
			// Scrambled Eggs ??
			new Item(335), // Trout
			// Roasted rabbit - ON SPIT?!?!? - I never actually did that.
			// Jungle spider - No idea.
			new Item(3365), // Lean snai
			new Item(341), // Cod
			new Item(2319), // Uncooked meat pie.
			new Item(349), // Pike.
			// Nettle Tea
			// Crab meat
			// Cream
			// Roasted Beast Meat
			new Item(3367), // Fat snail.
			// Egg and Tomato
			new Item(2001), // Stew
			new Item(331), // Salmon
			// Slimey Eel??
			new Item(5986), // Sweetcorn.
			new Item(7168), // Mud pie.
			// Karambwan ??
			// Chompy Bird - Only cookable on an orge spit.
			new Item(2317), // Apple Pie
			new Item(359), // Tuna
			// Fishcake xD
			new Item(7178), // Garden pie.
			new Item(2287), // Uncooked pizza.
			// Wine.
			new Item(10138),// Rainbow fish??
			// Cave Eel.
			// Butter.
			// Baked potato with butter
			new Item(1889), // Uncooked Cake
			new Item(377), // Lobster
			// Jubbly
			// Fried Onions
			new Item(363), // Raw bass.
			// Meat pizza. ItemOnItem - PlainPizza + Cooked Meat
			new Item(371), // Raw swordfish.
			// Fried mushrooms
			// Baked potato with butter and cheese
			new Item(7186), // Fish pie.
			// Cheese
			// Chokolate cake. ItemOnIem - Cake + Chokolate.
			// Oomlie Wrap
			// Egg and Tomato potato
			// Lava Eel
			// Anchovy pizza. ItemOnItem - PlainPizza + Cooked Anchovys
			// Mushrooms and Onions
			new Item(2009), // Uncooked curry
			new Item(7944), // Monkfish.
			// Mushroom and Onion Potato
			// Pineapple pizza. ItemOnItem - PlainPizza + Pineapple slices.
			// Tuna and Corn
			new Item(7196), // Admiral pie
			new Item(383), // Shark
			new Item(395), // Sea turtle.
			new Item(7206), // Raw wild pie.
			new Item(389), // Raw manta ray.
			new Item(7216), // Raw summer pie.
	};

	/**
	 * An array holding the level requirements.
	 */
	private static final int[] LEVEL_REQUIREMENTS = { 1, // Raw beef.
			1, // Raw beef - Green one from the ghost land.
			1, // Raw rat meat
			1, // Raw bear meat.
			1, // Raw chicken.
			1, // Raw chicken - Green one from the ghost land.
			1, // Anchovies
			1, // Shrimps
			1, // Karambwanji
			1, // Ugthanki
			1, // Bread
			1, // Sardine
			1, // Rabbit
			5, // Herring
			7, // Potato
			10, // Redberry pie.
			10, // Mackerel
			12, // Thin snail.
			15, // Trout
			17, // Lean snai
			18, // Cod
			20, // Meat pie.
			20, // Pike
			22, // Fat snail.
			25, // Stew
			25, // Salmon
			28, // Sweetcorn.
			29, // Mud pie
			30, // Apple pie.
			30, // Tuna
			34, // Garden pie.
			35, // Pizza
			35, // Rainbow fish.
			40, // Cake.
			40, // Lobster.
			43, // Bass.
			45, // Swordfish.
			47, // Fish pie.
			60, // Curry.
			62, // Monkfish.
			70, // Admiral pie.
			80, // Shark.
			82, // Sea turtle.
			85, // Wild pie.
			91, // Manta ray.
			95, // Summer pie.
	};

	/**
	 * An array of failed burnt items.
	 */
	private static final Item[] BURNT_ITEMS = { new Item(2146), // Burnt meat.
			new Item(2146), // Burnt meat.
			new Item(2146), // Burnt meat.
			new Item(2146), // Burnt meat.
			new Item(2144), // Burnt chicken.
			new Item(2144), // Burnt chicken - ghost land.
			new Item(323), // Hopefully burnt Anchovies.
			new Item(323), // Hopefully the same as burnt shrimp.
			new Item(323), // Karambwanji //TODO: Find real burning ID for this
							// one??
			new Item(2146), // Ugthanki //Burnt meat.
			new Item(2311), // Bread
			new Item(369), // Sardine
			new Item(7222), // Burnt Rabbit
			new Item(357), // Herring
			new Item(6699), // Potato //TODO: Potatoes ALWAYS burns on fire.
			new Item(2329), // Burnt pie. (redberry pie)
			new Item(357), // Mackerel
			new Item(3375), // Thin snail.
			new Item(343), // Trout
			new Item(3375), // Lean snail.
			new Item(343), // Cod
			new Item(2329), // Burnt pie.(meat pie)
			new Item(343), // Pike.
			new Item(3375), // Fat snail.
			new Item(2005), // Burnt stew
			new Item(343), // Salmon
			new Item(5990), // Sweetcorn.
			new Item(2329), // Burnt pie.(mud pie)
			new Item(2329), // Burnt pie.(apple pie)
			new Item(367), // Tuna
			new Item(2329), // Burnt pie (garden pie)
			new Item(2305), // Burnt pizza.
			new Item(10140),// Rainbow fish??
			new Item(1903), // Burnt Cake
			new Item(381), // Burnt lobster
			new Item(367), // Burnt bass.
			new Item(375), // Burnt Swordfish.
			new Item(2329), // Burnt pie (fish pie)
			new Item(2013), // Burnt curry.
			new Item(7948), // Monkfish.
			new Item(2329), // Burnt pie (Admiral pie)
			new Item(387), // Shark.
			new Item(399), // Sea turtle.
			new Item(2329), // Burnt pie (Wild pie)
			new Item(393), // Burnt manta ray.
			new Item(2329), // Burnt pie (Summer pie)
	};

	/**
	 * An array of finished cooked items.
	 **/
	private static final Item[] COOKED_ITEMS = { new Item(2142), // Cooked meat.
			new Item(4293), // Cooked meat - Green one from the ghost land.
			new Item(2142), // Cooked meat.
			new Item(2142), // Cooked meat.
			new Item(2140), // Cooked chicken.
			new Item(4291), // Cooked chicken - Green one from the ghost land.
			new Item(319), // Anchovies
			new Item(315), // Shrimps
			new Item(3151), // Karambwanji
			new Item(1861), // Ugthanki
			new Item(2309), // Bread
			new Item(325), // Sardine
			new Item(3228), // Rabbit
			new Item(347), // Herring
			new Item(6701), // Potato
			new Item(2325), // Redberry pie.
			new Item(355), // Mackerel
			new Item(3369), // Thin snail.
			new Item(333), // Trout
			new Item(3371), // Lean snail.
			new Item(339), // Cod
			new Item(2327), // Meat pie.
			new Item(351), // Pike.
			new Item(3373), // Fat snail.
			new Item(2003), // Stew
			new Item(329), // Salmon
			new Item(5988), // Sweetcorn.
			new Item(7170), // Mud pie.
			new Item(2323), // Apple pie
			new Item(361), // Tuna
			new Item(7178), // Garden pie.
			new Item(2289), // Plain pizza.
			new Item(10136),// Rainbow fish??
			new Item(1891), // Cake
			new Item(379), // Lobster
			new Item(365), // Bass.
			new Item(373), // Swordfish.
			new Item(7188), // Fish pie.
			new Item(2011), // Curry.
			new Item(7946), // Monkfish.
			new Item(7198), // Admiral pie
			new Item(385), // Shark.
			new Item(397), // Sea turtle.
			new Item(7208), // Wild pie.
			new Item(391), // Manta ray
			new Item(7218), // Summer pie.
	};

	/**
	 * An array of experience gained.
	 */
	private static final double[] EXPERIENCE_TABLE = { 30, // Raw beef.
			30, // Raw beef - Green one from the ghost land.
			30, // Raw rat meat
			30, // Raw bear meat.
			30, // Raw chicken.
			30, // Raw chicken - Green one from the ghost land.
			30, // Anchovies
			30, // Shrimps
			10, // Karambwanji
			40, // Ugthanki
			40, // Bread
			40, // Sardine
			30, // Rabbit
			50, // Herring
			15, // Potato
			78, // Redberry pie.
			60, // Mackerel
			70, // Thin snail.
			70, // Trout
			80, // Lean snail.
			75, // Cod
			110, // Meat pie.
			80, // Pike
			95, // Fat snail
			117, // Stew
			90, // Salmon
			104, // Sweetcorn.
			128, // Mud pie
			130, // Apple pie.
			100, // Tuna
			138, // Garden pie.
			143, // Pizza
			200, // Rainbow fish.
			180, // Cake
			120, // Lobster
			130, // Bass
			140, // Swordfish.
			164, // Fish pie.
			221, // Curry.
			150, // Monkfish.
			210, // Admiral pie.
			210, // Shark.
			221.3, // Sea turtle.
			240, // Wild pie.
			216, // Manta ray
			260, // Summer pie.
	};

	/**
	 * An array of the approximate levels to stop burning food. {without
	 * gauntles, with gauntles}
	 */
	// So yeah, redo all those xD.
	private static final int[][] STOP_BURNING_LEVELS = { { 25, 20 }, // Raw beef
																		// //No
																		// idea
																		// about
																		// those
																		// :(
			{ 25, 20 }, // Raw beef - Green one from the ghost land. //No idea
						// about those :(
			{ 25, 20 }, // Raw rat meat //No idea about those :(
			{ 30, 20 }, // Raw bear meat. //No idea about those :(
			{ 25, 20 }, // Raw chicken. //No idea about those :(
			{ 25, 20 }, // Raw chicken - Green one from the ghost land. //No
						// idea about those :(
			{ 25, 20 }, // Anchovies //No idea about those :(
			{ 25, 20 }, // Shrimps //No idea about those :(
			{ 20, 15 }, // Karambwanji //No idea about those :(
			{ 25, 20 }, // Ugthanki //No idea about those :(
			{ 25, 20 }, // Bread //No idea about those :(
			{ 25, 20 }, // Sardine //No idea about those :(
			{ 25, 20 }, // Rabbit //No idea about those :(
			{ 30, 25 }, // Herring //No idea about those :(
			{ 30, 25 }, // Potato //No idea about those :(
			{ 32, 27 }, // Redberry pie //No idea about those :(
			{ 32, 27 }, // Mackerel //No idea about those :(
			{ 34, 29 }, // Thin snail //No idea about those :(
			{ 37, 32 }, // Trout //No idea about those :(
			{ 39, 34 }, // Lean snail //No idea about those :(
			{ 40, 35 }, // Cod //No idea about those :(
			{ 42, 37 }, // Meat pie //No idea about those :(
			{ 42, 37 }, // Pike //No idea about those :(
			{ 44, 39 }, // Fat snail //No idea about those :(
			{ 47, 42 }, // Stew //No idea about those :(
			{ 47, 42 }, // Salmon //No idea about those :(
			{ 50, 45 }, // Sweetcorn //No idea about those :(
			{ 51, 46 }, // Mud pie //No idea about those :(
			{ 52, 47 }, // Apple pie //No idea about those :(
			{ 52, 47 }, // Tuna //No idea about those :(
			{ 56, 51 }, // Garden pie //No idea about those :(
			{ 57, 52 }, // Pizza //No idea about those :(
			{ 57, 52 }, // Rainbow fish. //No idea about those :(
			{ 61, 56 }, // Cake. //No idea about those :(
			{ 61, 56 }, // Lobster. //No idea about those :(
			{ 64, 59 }, // Bass. //No idea about those :(
			{ 70, 75 }, // Swordfish.
			{ 70, 75 }, // Fish pie.
			{ 77, 72 }, // Curry. //No idea about those :(
			{ 79, 74 }, // Monkfish. //No idea about those :(
			{ 89, 84 }, // Admiral pie. //No idea about those :(
			{ 99, 94 }, // Shark. //No idea about those :(
			{ 101, 96 }, // Sea turtle. //No idea about those :(
			{ 104, 99 }, // Wild pie. //No idea about those :(
			{ 110, 105 }, // Manta ray. //No idea about those :(
			{ 114, 109 }, // Summer pie. //No idea about those :(
	};

	/**
	 * One of the two arrays holding the items used on each other.
	 */
	private static final Item[] ITEM_ON_ITEM_ARRAY_1 = { new Item(2313), // pie
																			// dish
			new Item(2315), // pie shell for redberry pie
			new Item(2315), new Item(7172), new Item(7174), // Pie Shell, part
															// gardening pie,
															// part gardening
															// pie.
			new Item(2315), new Item(7212), new Item(7214), // Pie Shell, part
															// summer pie, part
															// summer pie.

	};

	/**
	 * One of the two arrays holding the items used on each other.
	 */
	private static final Item[] ITEM_ON_ITEM_ARRAY_2 = { new Item(1953), // pastry
																			// dough
			new Item(1951), // redberries
			new Item(1982), new Item(1957), new Item(1965), // Tomato, onion,
															// cabbage
			new Item(5504), new Item(5982), new Item(1955), // Strawberry,
															// watermelon, apple

	};

	/**
	 * The results given when using two items on each other.
	 */
	private static final Item[] ITEM_ON_ITEM_RESULT = { new Item(2315), // pie
																		// shell
			new Item(2321), // redberry pie
			new Item(7172), new Item(7174), new Item(7178), // Part gardening
															// pie, part
															// gardening pie,
															// gardening pie.
			new Item(7212), new Item(7214), new Item(7216), // Part summer pie,
															// summer pie,
															// summer pie.

	};

	/**
	 * The requirement needed to cook/mix the items.
	 */
	private static final int[] ITEM_ON_ITEM_LEVEL_REQUIREMENTS = { -1, // Just
																		// adding
																		// the
																		// pastry
																		// dough
																		// into
																		// the
																		// dish
			10, // Berry pie
			34, 34, 34, // Garden pies.
			95, 95, 95, // Summer pies.
	};

	/**
	 * All the mixes that shouldn't give any cooking exp, should have a -1 in
	 * here.
	 */
	private static final double[] ITEM_ON_ITEM_EXPERIENCE = { -1, -1, -1, -1,
			-1, // Part gardening pie, part gardening pie, gardening pie,
			-1, -1, -1 };

	/**
	 * Use this to check if the two items a specific player is using on each
	 * other is related to cooking. This method will also do the actual action
	 * needed.
	 * 
	 * @param player
	 *            The player using two items on each other.
	 * @param used
	 *            The item used.
	 * @param usedWith
	 *            The item the other item is being used with.
	 * @param slotUsed
	 *            The slot of the item used.
	 * @param slotUsedWith
	 *            The slot of the item the other item is used with.
	 * @return True if the items were used with each other, false if not.
	 */
	public static boolean checkForItemOnItem(Player player, Item used,
			Item usedWith, int slotUsed, int slotUsedWith) {
		for (int index = 0; index < ITEM_ON_ITEM_ARRAY_1.length; index++) {
			if (ITEM_ON_ITEM_ARRAY_1[index].getId() == used.getId()
					&& ITEM_ON_ITEM_ARRAY_2[index].getId() == usedWith.getId()
					|| ITEM_ON_ITEM_ARRAY_2[index].getId() == used.getId()
					&& ITEM_ON_ITEM_ARRAY_1[index].getId() == usedWith.getId()) {
				mixCookingItems(player, used, usedWith, slotUsed, slotUsedWith,
						ITEM_ON_ITEM_RESULT[index],
						ITEM_ON_ITEM_EXPERIENCE[index],
						// ITEM_ON_ITEM_SEND_MESSAGES[index],
						ITEM_ON_ITEM_LEVEL_REQUIREMENTS[index]);
				return true;
			}
		}
		return false;
	}

	/**
	 * Mixes the items provided, and adds the cooking experience provided, if
	 * the value is more than -1.
	 * 
	 * @param player
	 *            The player using two items on each other.
	 * @param used
	 *            The item used.
	 * @param usedWith
	 *            The item the other item is being used with.
	 * @param slotUsed
	 *            The slot of the item used.
	 * @param slotUsedWith
	 *            The slot of the item the other item is used with.
	 * @param result
	 *            The item result.
	 * @param experience
	 *            The experience reviecved, if value is above -1.
	 * @param sendMessage
	 *            The message send for the player.
	 * @param levelReq
	 *            The level requirement needed, -1 if none.
	 */
	public static final void mixCookingItems(Player player, Item used,
			Item usedWith, int slotUsed, int slotUsedWith, Item result,
			double experience, /* String sendMessage, */int levelReq) {
		if (levelReq != -1) {
			if (player.getSkills().getLevel(Skills.COOKING) < levelReq) {
				player.getActionSender().sendMessage(
						"You need a Cooking level of at least " + levelReq
								+ " in order to mix those items.");
				return;
			}
		}
		player.getInventory().remove(slotUsed, used);
		player.getInventory().remove(slotUsedWith, usedWith);
		player.getActionSender().sendMessage(
				"You mix the " + used.getDefinition().getName().toLowerCase()
						+ " with the "
						+ usedWith.getDefinition().getName().toLowerCase()
						+ "..");
		player.getActionSender().sendMessage(
				".. and turn it into a "
						+ result.getDefinition().getName().toLowerCase() + "."); // Message
																					// is
																					// getting
																					// too
																					// big
																					// :(
		player.getInventory().add(result);
		if (experience != -1) {
			player.getSkills().addExperience(Skills.COOKING, experience);
		}
	}

	/**
	 * Handles the cooking interface button clicks.
	 */
	public static void handleInterfaceButtons(Player player, int button) {
		switch (button) {
		case 6:
			cook(player, 1);
			break;
		case 5:
			cook(player, 5);
			break;
		case 4:
			player.getInterfaceState().openEnterAmountInterface(
					COOKING_INTERFACE,
					player.getCookingVariables().getCookingItem());
			break;
		case 3:
			cook(player,
					player.getInventory().getCount(
							player.getCookingVariables().getCookingItem()));
			break;
		}
	}

	/**
	 * Checks for cooking item on object related things.
	 * 
	 * @param player
	 *            The player.
	 * @param itemId
	 *            The item id.
	 * @param itemSlot
	 *            The item slot.
	 * @param objectId
	 *            The object id.
	 * @return True if the item on objects were cooking related, false if not.
	 */
	public static boolean checkForItemOnObject(Player player, int itemId,
			int itemSlot, int objectId) {
		if (sendCookingInterface(player, itemId, objectId)) {
			return true;
		}
		if (checkForWaterFillingItemAndObjects(player, itemId, itemSlot,
				objectId)) {
			return true;
		}
		if (checkForFarmingMill(player, itemId, itemSlot, objectId)) {
			return true;
		}
		return false;
	}

	private static final Item[] FARMING_MILL_ITEMS = { new Item(1947), // Grain.
	};

	private static final int[] FARMING_MILL_OBJECTS = { 2714, // Where you put
																// grain in.
																// "Grain hopper"
	};

	private static final Location[] FLOUR_BIN_LOCATIONS = { Location.create(
			3166, 3306, 0), };

	public static void sendFlourBins(Player player) {
		for (Location bin : FLOUR_BIN_LOCATIONS) {
			if (player.getLocation().isWithinDistance(bin)) {
				if (player.getCookingVariables().getFlourBinAmount() > 0) {
					player.getActionSender().sendCreateObject(1782, 10, 0, bin);
				}
			}
		}
	};

	private static boolean checkForFarmingMill(Player player, int itemId,
			int itemSlot, int objectId) {
		for (Item item : FARMING_MILL_ITEMS) {
			if (item.getId() == itemId) {
				for (int object : FARMING_MILL_OBJECTS) {
					if (object == objectId) {
						switch (objectId) {
						case 2714:// Grain hopper.
							if (player.getCookingVariables()
									.isUsingGrainHopper()) {
								player.getActionSender()
										.sendMessage(
												"The hopper is already filled with grain.");
							} else {
								player.getActionSender().sendMessage(
										"You put some grain in the hopper..");
								player.getInventory().remove(item, itemSlot);
								player.getCookingVariables()
										.setUsingGrainHopper(true);
							}
							break;
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	private static final Item FLOUR = new Item(1933);
	private static final Item POT = new Item(1931);

	public static boolean checkForObjectClicking(Player player, int objectId,
			Location loc) {
		switch (objectId) {
		/*
		 * Operate hopper controls.
		 */
		case 2718:
			if (player.getCookingVariables().isUsingGrainHopper()) {
				if (player.getCookingVariables().getFlourBinAmount() <= 10) {
					player.getActionSender()
							.sendMessage(
									"You operate the hopper. The grain slides down the chute.");
					player.getCookingVariables().setUsingGrainHopper(false);
					player.getCookingVariables().increaseFlourInFlourBin();
					// sendFlourBins(player);
				} else {
					player.getActionSender().sendMessage(
							"The flour bin cannot contain anymore flour.");
				}
			} else {
				player.getActionSender().sendMessage(
						"The hopper doesn't contain any grain.");
			}
			// TODO: player.getActionSender().animateObject(objx, objy,
			// animationID, tileObjectType, orientation);
			return true;
			/*
			 * Collect flour from bin.
			 */
		case 1782:
			if (player.getCookingVariables().getFlourBinAmount() > 0) {
				if (player.getInventory().contains(1931)) {// Pot
					if (player.getCookingVariables().getFlourBinAmount() == 1) {
						player.getActionSender()
								.sendMessage(
										"You fill a pot with the last of the flour in the bin.");
						player.getActionSender().sendCreateObject(1781, 10, 0,
								loc);
					} else {
						player.getActionSender().sendMessage(
								"You fill a pot with the flour from the bin.");
					}
					player.getInventory().remove(POT);
					player.getInventory().add(FLOUR);
					player.getCookingVariables().decreaseFlourInFlourBin();
				} else {
					player.getActionSender().sendMessage(
							"I need an empty pot to hold the flour in.");
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * The filled items.
	 */
	private static final Item[] WATER_CONTAINERS_FILLED = { new Item(1937), // Jug
																			// Of
																			// Water
			new Item(1921), // Bowl Of Water
			new Item(1929), // Bucket Of Water
			new Item(227), // Vial of water.
			new Item(1823), // Filled waterskin (4)
			new Item(1823), // Filled waterskin (4)
			new Item(1823), // Filled waterskin (4)
			new Item(1823), // Filled waterskin (4)
	};

	/**
	 * The items before they are filled
	 */
	private static final Item[] WATER_CONTAINERS_UNFILLED = { new Item(1935), // Unfilled
																				// Jug
			new Item(1923), // Unfilled Bowl
			new Item(1925), // Unfilled Bucket
			new Item(229), // Empty vial.
			new Item(1825), // Filled waterskin (3)
			new Item(1827), // Filled waterskin (2)
			new Item(1829), // Filled waterskin (1)
			new Item(1831), // Empty waterskin
	};

	/**
	 * All objects containing water ingame.
	 */
	// Well !?!??! Only for buckets I think, just ignore them.
	private static final int[] WATER_CONTAINING_OBJECTS = { 153, // Fountain
			879, // Fountain
			880, // Fountain
			2654, // Sinclair family fountain
			2864, // Fountain
			6232, // Fountain
			10436, // Fountain
			10437, // Fountain
			11007, // Fountain
			11759, // Fountain
			13478, // Small fountain
			13479, // Large fountain
			13480, // Ornamental fountain
			21764, // Fountain
			24161, // Fountain
			24214, // Fountain
			24265, // Fountain

			873, // Sink
			874, // Sink
			4063, // Sink
			6151, // Sink
			8699, // Sink
			9143, // Sink
			9684, // Sink
			10175, // Sink
			12279, // Sink
			12974, // Sink
			13563, // Sink
			13564, // Sink
			14868, // Sink
			14917, // Sink
			15678, // Sink
			16704, // Sink
			16705, // Sink
			20358, // Sink
			22715, // Sink
			24112, // Sink
			24314, // Sink

			11661, // Waterpump in falador.

			/*
			 * In doubt of the following:
			 */
			4176, // Tap
			4285, // Tap
			4482, // Tap

	// Cbf doing anymore, this is all fine ;)
	};

	/**
	 * Checks if some items is fillable with water, and fills them.
	 * 
	 * @param player
	 *            The player.
	 * @param itemId
	 *            The item id.
	 * @param itemSlot
	 *            The item slot.
	 * @param objectId
	 *            The object id.
	 * @return True if we filled an item with water, false if not.
	 */
	private static boolean checkForWaterFillingItemAndObjects(Player player,
			int itemId, int itemSlot, int objectId) {
		boolean checkForWaterFilling = false;
		for (int id : WATER_CONTAINING_OBJECTS) {
			if (id == objectId) {
				checkForWaterFilling = true;
				break;
			}
		}
		if (checkForWaterFilling) {
			checkForWaterFilling = false;
			for (int index = 0; index < WATER_CONTAINERS_UNFILLED.length; index++) {
				if (WATER_CONTAINERS_UNFILLED[index].getId() == itemId) {
					player.getInventory().remove(itemSlot,
							WATER_CONTAINERS_UNFILLED[index]);
					player.getActionSender().sendMessage(
							"You fill the "
									+ WATER_CONTAINERS_UNFILLED[index]
											.getDefinition().getName()
											.toLowerCase() + " with water.");
					player.getInventory().add(WATER_CONTAINERS_FILLED[index]);
					checkForWaterFilling = true;
					break;
				} else if (WATER_CONTAINERS_FILLED[index].getId() == itemId) {
					player.getActionSender().sendMessage(
							"The "
									+ WATER_CONTAINERS_FILLED[index]
											.getDefinition().getName()
											.toLowerCase()
									+ " is already full.");
					checkForWaterFilling = true;
					break;
				}
			}
		}
		return checkForWaterFilling;
	}

	/**
	 * Checks if the objectId is a range, Makes sure we have the level required,
	 * and sends the cooking interface with the correct item id on it.
	 * 
	 * @param player
	 *            The player.
	 * @param itemId
	 *            The item id.
	 * @param objectId
	 *            The object id.
	 * @return True if the object was a range, and the item was cookable, false
	 *         if not.
	 */
	private static boolean sendCookingInterface(Player player, int itemId,
			int objectId) {
		if (!cookingObject(player, objectId) || getItemIndex(itemId) == -1) {
			System.out.println("Failed here. #1");
			return false;
		}
		int index = getItemIndex(itemId);
		int levelToCook = LEVEL_REQUIREMENTS[index];
		if (player.getSkills().getLevel(Skills.COOKING) < levelToCook) {
			player.getActionSender().sendMessage(
					"You need a Cooking level of at least "
							+ levelToCook
							+ " in order to cook a "
							+ ItemDefinition.forId(itemId).getName()
									.replace("raw", "").toLowerCase() + ".");
			return true;// We didn't successfully do what we were told, but the
						// items existed in the array.
		}
		player.getCookingVariables().setCookingItem(itemId);
		player.getActionSender().sendInterfaceModel(307, 2, 175, itemId);
		player.getActionSender().sendChatboxInterface(307);
		return true;
	}

	/**
	 * Loops through an array of cooking objects, and checks if our objectId is
	 * in there.
	 * 
	 * @param objectId
	 *            The objectId
	 * @return True if the array contains it, false if not.
	 */
	private static boolean cookingObject(Player player, int objectId) {
		for (int id : COOKING_OBJECTS[0]) {
			if (id == objectId) {
				player.getCookingVariables().setCookingOnFire(false);
				return true;
			}
		}
		for (int id : COOKING_OBJECTS[1]) {
			if (id == objectId) {
				player.getCookingVariables().setCookingOnFire(true);
				return true;
			}
		}
		return false;
	}

	/**
	 * Makes sure we don't get a null-pointer exception, and starts the cooking
	 * event.
	 */
	public static void cook(Player player, int amount) {
		player.getActionSender().sendCloseInterface();
		if (player.getCookingVariables().getCookingItem() == -1) {
			return;
		}
		int index = getItemIndex(player.getCookingVariables().getCookingItem()); // Can't
																					// be
																					// -1
																					// anymore.
		player.getActionQueue().addAction(
				new CookingAction(player, index, amount));
	}

	/**
	 * Gets the item index from the array of raw food items.
	 * 
	 * @param itemId
	 *            The id of the item we want an index of.
	 * @return The item index. (-1 if it doesn't exist).
	 */
	private static int getItemIndex(int itemId) {
		for (int id = 0; id < RAW_ITEMS.length; id++) {
			if (RAW_ITEMS[id].getId() == itemId) {
				return id;
			}
		}
		return -1;// Woops.
	}

	/**
	 * Gets the approximate level to stop burning a specific cooking item.
	 * 
	 * @param index
	 *            The index of the item we're cooking.
	 * @param gloves
	 *            The players glove, notice: this might be null.
	 * @return The approximate level to stop burning the cooking item provided.
	 */
	private static int getStopBurningLevel(int index, Item gloves) {
		return STOP_BURNING_LEVELS[index][(gloves == null ? false : gloves
				.getId() == 775) ? 1 : 0];
	}

	/**
	 * The cooking event "member" class. This class is handling regular
	 * stove/fire cooking. Its getting all the needed stuff from the arrays in
	 * the top of this class, based on the index read in the constructor.
	 */
	private static class CookingAction extends Action {

		/**
		 * The cooking animations.
		 */
		private static final Animation RANGE_ANIMATION = Animation.create(896);
		private static final Animation FIRE_ANIMATION = Animation.create(897);

		/**
		 * The cooking event constructor.
		 * 
		 * @param player
		 *            The chef.
		 * @param index
		 *            The index from which we get all the needed stuff.
		 * @param amount
		 *            The amount of items we want to cook.
		 */
		public CookingAction(Player player, int index, int amount) {
			super(player, 0);
			this.rawItem = RAW_ITEMS[index];
			this.noBurnLevel = getStopBurningLevel(index, player.getEquipment()
					.get(Equipment.SLOT_GLOVES));
			this.levelToCook = LEVEL_REQUIREMENTS[index];
			this.burntItem = BURNT_ITEMS[index];
			this.cookedItem = COOKED_ITEMS[index];
			this.exp = EXPERIENCE_TABLE[index];
			this.amount = amount;
		}

		@Override
		public void execute() {
			final Player player = getPlayer();
			if (amount <= 0) {
				this.stop();
				getPlayer().getCookingVariables().setCookingItem(-1);
				return;
			}
			if (!player.getInventory().contains(rawItem)) {
				player.getActionSender().sendMessage(
						"You have run out of "
								+ rawItem.getDefinition().getName()
										.replaceFirst("Raw", "") + " to cook.");
				this.stop();
				player.getCookingVariables().setCookingItem(-1);
				return;
			}
			if (player.getSkills().getLevel(Skills.COOKING) < levelToCook) {
				player.getActionSender().sendMessage(
						"You need a Cooking of level " + levelToCook
								+ " in order to cook this meal.");
				this.stop();
				player.getCookingVariables().setCookingItem(-1);
				return;
			}
			int playerLevel = player.getSkills().getLevel(Skills.COOKING);
			boolean shouldBurn = (playerLevel >= noBurnLevel) ? false
					: shouldBurn(playerLevel, noBurnLevel);
			if (player.getCookingVariables().isCookingOnFire()) {
				/*
				 * if(!fireContainer.contains(location)) { this.stop(); return;
				 * }
				 */
				player.playAnimation(FIRE_ANIMATION);
			} else {
				player.playAnimation(RANGE_ANIMATION);
			}
			player.getInventory().remove(rawItem);
			if (shouldBurn) {
				player.getInventory().add(burntItem);
				player.getActionSender().sendMessage(
						"Oops! You accidently burnt the "
								+ rawItem.getDefinition().getName()
										.toLowerCase() + ".");
			} else {
				player.getInventory().add(cookedItem);
				player.getActionSender().sendMessage(
						"You succesfully cook the "
								+ cookedItem.getDefinition().getName()
										.replaceFirst("Cooked ", "")
										.toLowerCase() + ".");// Removes the
																// string
																// "Cooked" from
																// some food,
																// eg.
																// "Cooked meat"
																// because it
																// looks super
																// idiotic.
				if (cookedItem.getId() == 2309) {
					TutorialIsland.finishedBread(player);
				}
				player.getSkills().addExperience(Skills.COOKING, exp);
			}
			this.setDelay(3000);
			if (--amount <= 0) {
				this.stop();
				player.getCookingVariables().setCookingItem(-1);
			}
		}

		private final Item rawItem;
		private final Item burntItem;
		private final Item cookedItem;
		private final int noBurnLevel;
		private final int levelToCook;
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

	/**
	 * Calculates the chance of burning any item.
	 * 
	 * @param playerLevel
	 *            The current cooking level of the Player.
	 * @param noBurnLevel
	 *            The level to stop burning the food, the player is currently
	 *            cooking.
	 * @return True if the food should burn, false if not.
	 */
	public static boolean shouldBurn(int playerLevel, int noBurnLevel) {
		int levelsToStopBurn = noBurnLevel - playerLevel;
		if (levelsToStopBurn > 20) {
			levelsToStopBurn = 20; // Makes the chance of burning approximatly
									// 60%.
		}
		return r.nextInt(34) <= levelsToStopBurn;
	}
}
