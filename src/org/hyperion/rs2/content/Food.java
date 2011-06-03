package org.hyperion.rs2.content;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.container.Duel;

/**
 * Simple class containing all the food we're eating.
 * 
 * @author Brown.
 */
public class Food {

	/**
	 * The eating animation.
	 */
	private static final Animation EATING = Animation.create(829);

	/**
	 * Holds all the information about food. {ID, HEAL, (NEW ID)} The (NEW ID)
	 * is optimal, and only used for pizzas and others.
	 */
	private static final short[][] FOOD = { { 247, 2 }, // Jangerberries
			{ 315, 3 }, // Shrimps
			{ 319, 1 }, // Anchovies
			{ 325, 4 }, // Sardine
			{ 329, 9 }, // Salmon
			{ 333, 7 }, // Trout
			{ 337, 6 }, // Giant carp
			{ 339, 7 }, // Cod
			{ 347, 5 }, // Herring
			{ 351, 8 }, // Pike
			{ 355, 6 }, // Mackerel
			{ 361, 10 }, // Tuna
			{ 365, 13 }, // Bass
			{ 373, 14 }, // Swordfish
			{ 379, 12 }, // Lobster
			{ 385, 20 }, // Shark
			{ 391, 22 }, // Manta ray
			{ 397, 21 }, // Sea turtle
			{ 403, 4 }, // Edible seaweed
			// {464, hardcode}, //Strange fruit
			// {1851, cantbeeaten}, //Tenti pineapple
			{ 1861, 3 }, // Ugthanki meat
			{ 1869, 2 }, // Chopped tomato
			{ 1871, 1 /* sadtoseeamancrymessage */}, // Chopped onion
			// {1875, goodquestion}, //Onion & tomato
			{ 1883, 19 /* randommessages */}, // Ugthanki kebab
			{ 1885, 19 /* randommessages */}, // Ugthanki kebab
			{ 1891, 4, 1893 }, // Cake
			{ 1893, 4, 1895 }, // 2/3 cake
			{ 1895, 4 }, // Slice of cake
			{ 1897, 5, 1899 }, // Chocolate cake
			{ 1899, 5, 1901 }, // 2/3 chocolate cake
			{ 1901, 5 }, // Chocolate slice
			{ 1942, 1 }, // Potato
			{ 1957, 1 }, // Onion
			{ 1959, 14 }, // Pumpkin
			{ 1961, 12 }, // Easter egg
			{ 1963, 2 }, // Banana
			{ 1965, 1 }, // Cabbage
			{ 1967, 2 }, // Cabbage
			{ 1969, 2 }, // Spinach roll
			// {1971, randomandstuff}, //Kebab
			{ 1973, 3 }, // Chocolate bar
			{ 1982, 2 }, // Tomato
			// {1984, nonesplitout}, //Rotten apple
			{ 1985, 2 }, // Cheese
			{ 2003, 11 }, // Stew
			{ 2011, 19 }, // Curry
			{ 2102, 2 }, // Lemon
			{ 2104, 1 }, // Lemon chunks
			{ 2106, 1 }, // Lemon slices
			{ 2108, 2 }, // Orange
			{ 2110, 1 }, // Orange chunks
			{ 2112, 1 }, // Orange slices
			// {2114, uneatable}, //Pineapple
			{ 2116, 2 }, // Pineapple chunks
			{ 2118, 2 }, // Pineapple ring
			{ 2120, 2 }, // Lime
			{ 2122, 2 }, // Lime chunks
			{ 2124, 2 }, // Lime slices
			{ 2126, 2 }, // Dwellberries
			{ 2128, 1 }, // Equa leaves
			// {2130, idk}, //Pot of cream
			{ 2140, 3 }, // Cooked chicken
			{ 2142, 3 }, // Cooked meat
			{ 2149, 11 }, // Lava eel
			{ 2152, 3 }, // Toad's legs
			/*
			 * {2154, idk}, //Equa toad's legs {2156, idk}, //Spicy toad's legs
			 * {2158, idk}, //Seasoned legs {2160, idk}, //Spicy worm {2162,
			 * idk}, //King worm {2173, idk}, //Odd gnomebowl {2179, idk},
			 * //Unfinished bowl {2181, idk}, //Unfinished bowl {2183, idk},
			 * //Unfinished bowl {2185, idk}, //Chocolate bomb {2187, idk},
			 * //Tangled toad's legs {2189, idk}, //Unfinished bowl {2191, idk},
			 * //Worm hole {2193, idk}, //Unfinished bowl {2195, idk}, //Veg
			 * ball {2197, idk}, //Odd crunchies {2203, idk}, //Unfinished
			 * crunchy {2205, idk}, //Worm crunchies {2207, idk}, //Unfinished
			 * crunchy {2209, idk}, //Chocchip crunchies {2211, idk},
			 * //Unfinished crunchy {2213, idk}, //Spicy crunchies {2215, idk},
			 * //Unfinished crunchy {2217, idk}, //Toad crunchies {2219, idk},
			 * //Premade w'm batta {2221, idk}, //Premade t'd batta {2223, idk},
			 * //Premade c+t batta {2225, idk}, //Premade fr't batta {2227,
			 * idk}, //Premade veg batta {2229, idk}, //Premade choc bomb {2231,
			 * idk}, //Premade ttl {2233, idk}, //Premade worm hole {2235, idk},
			 * //Premade veg ball {2237, idk}, //Premade w'm crun' {2239, idk},
			 * //Premade ch' crunch {2241, idk}, //Premade s'y crunch {2243,
			 * idk}, //Premade t'd crunch {2245, idk}, //Odd batta {2251, idk},
			 * //Unfinished batta {2253, idk}, //Worm batta {2255, idk}, //Toad
			 * batta {2257, idk}, //Unfinished batta {2259, idk}, //Cheese+tom
			 * batta {2261, idk}, //Unfinished batta {2263, idk}, //Unfinished
			 * batta {2265, idk}, //Unfinished batta {2267, idk}, //Unfinished
			 * batta {2269, idk}, //Unfinished batta {2271, idk}, //Unfinished
			 * batta {2273, idk}, //Unfinished batta {2275, idk}, //Unfinished
			 * batta {2277, idk}, //Fruit batta {2279, idk}, //Unfinished batta
			 * {2281, idk}, //Vegetable batta
			 */
			{ 2289, 7, 2291 }, // Plain pizza
			{ 2291, 7 }, // 1/2 plain pizza
			{ 2293, 8, 2295 }, // Meat pizza
			{ 2295, 8 }, // 1/2 meat pizza
			{ 2297, 9, 2299 }, // Anchovy pizza
			{ 2299, 9 }, // 1/2 anchovy pizza
			{ 2301, 11, 2303 }, // Pineapple pizza
			{ 2303, 11 }, // 1/2 p'apple pizza
			{ 2309, 5 }, // Bread
			{ 2323, 7, 2335 }, // Apple pie
			{ 2325, 5, 2333 }, // Redberry pie
			{ 2327, 6, 2331 }, // Meat pie
			{ 2331, 6 }, // Half a meat pie
			{ 2333, 5 }, // Half a redberry pie
			{ 2335, 7 }, // Half an apple pie
			// {2343, idk}, //Cooked oomlie wrap
			// {2379, DONE}, //Rock cake
			// {2398, idk}, //Cave nightshade
			{ 2878, 10 }, // Cooked chompy
	/*
	 * {3144, IDK}, //Cooked karambwan {3146, IDK}, //Poison karambwan {3151,
	 * IDK}, //Karambwanji {3162, IDK}, //Sliced banana {3168, IDK}, //Seaweed
	 * sandwich {3177, IDK}, //Banana left {3228, IDK}, //Cooked rabbit {3369,
	 * IDK}, //Thin snail meat {3371, IDK}, //Lean snail meat {3373, IDK}, //Fat
	 * snail meat {3381, IDK}, //Cooked slimy eel {4012, IDK}, //Monkey nuts
	 * {4014, IDK}, //Monkey bar {4016, IDK}, //Banana stew {4291, IDK},
	 * //Cooked chicken {4293, IDK}, //Cooked meat {4485, IDK}, //White pearl
	 * {4517, IDK}, //Giant frog legs {4558, IDK}, //Blue sweets {4559, IDK},
	 * //Deep blue sweets {4560, IDK}, //White sweets {4561, IDK}, //Purple
	 * sweets {4562, IDK}, //Red sweets {4563, IDK}, //Green sweets {4564, IDK},
	 * //Pink sweets {4608, IDK}, //Super kebab {4620, IDK}, //Black mushroom
	 * {5003, IDK}, //Cave eel {5004, IDK}, //Frog spawn {5504, IDK},
	 * //Strawberry {5733, IDK}, //Rotten potato {5972, IDK}, //Papaya fruit
	 * {5982, IDK}, //Watermelon {5984, IDK}, //Watermelon slice {5988, IDK},
	 * //Cooked sweetcorn {6202, IDK}, //Fishlike thing {6206, IDK}, //Fishlike
	 * thing {6297, IDK}, //Spider on stick {6299, IDK}, //Spider on shaft
	 * {6311, IDK}, //Gout tuber {6469, IDK}, //White tree fruit {6701, IDK},
	 * //Baked potato {6703, IDK}, //Potato with butter {6705, IDK}, //Potato
	 * with cheese {6768, IDK}, //Poisoned cheese {6794, IDK}, //Choc-ice {6883,
	 * IDK}, //Peach {6961, IDK}, //Baguette {6962, IDK}, //Triangle sandwich
	 * {6963, IDK}, //Roll {6965, IDK}, //Square sandwich {6969, IDK}, //Shark
	 * {7054, IDK}, //Chilli potato {7056, IDK}, //Egg potato {7058, IDK},
	 * //Mushroom potato {7060, IDK}, //Tuna potato {7062, IDK}, //Chilli con
	 * carne {7064, IDK}, //Egg and tomato {7066, IDK}, //Mushroom & onion
	 * {7068, IDK}, //Tuna and corn {7070, IDK}, //Minced meat {7072, IDK},
	 * //Spicy sauce {7078, IDK}, //Scrambled egg {7082, IDK}, //Fried mushrooms
	 * {7084, IDK}, //Fried onions {7086, IDK}, //Chopped tuna {7088, IDK},
	 * //Sweetcorn {7178, IDK}, //Garden pie {7180, IDK}, //Half a garden pie
	 * {7188, IDK}, //Fish pie {7190, IDK}, //Half a fish pie {7198, IDK},
	 * //Admiral pie {7200, IDK}, //Half an admiral pie {7208, IDK}, //Wild pie
	 * {7210, IDK}, //Half a wild pie {7218, IDK}, //Summer pie {7220, IDK},
	 * //Half a summer pie {7223, IDK}, //Roast rabbit {7228, IDK}, //Cooked
	 * chompy {7479, IDK}, //Spicy stew {7509, IDK}, //Dwarven rock cake {7510,
	 * IDK}, //Dwarven rock cake {7521, IDK}, //Cooked crab meat {7523, IDK},
	 * //Cooked crab meat {7524, IDK}, //Cooked crab meat {7525, IDK}, //Cooked
	 * crab meat {7526, IDK}, //Cooked crab meat {7530, IDK}, //Cooked fishcake
	 * {7568, IDK}, //Cooked jubbly {7572, IDK}, //Red banana {7573, IDK},
	 * //Tchiki monkey nuts {7574, IDK}, //Sliced red banana {7575, IDK},
	 * //Tchiki nut paste {7579, IDK}, //Stuffed snake {7928, IDK}, //Easter egg
	 * {7929, IDK}, //Easter egg {7930, IDK}, //Easter egg {7931, IDK}, //Easter
	 * egg {7932, IDK}, //Easter egg {7933, IDK}, //Easter egg {7934, IDK},
	 * //Field ration {7943, IDK}, //Fresh monkfish {7946, IDK}, //Monkfish
	 * {9052, IDK}, //Locust meat {9475, IDK}, //Mint cake {9527, IDK}, //Fruit
	 * batta {9529, IDK}, //Toad batta {9531, IDK}, //Worm batta {9533, IDK},
	 * //Vegetable batta {9535, IDK}, //Cheese+tom batta {9538, IDK}, //Toad
	 * crunchies {9540, IDK}, //Spicy crunchies {9542, IDK}, //Worm crunchies
	 * {9544, IDK}, //Chocchip crunchies {9547, IDK}, //Worm hole {9549, IDK},
	 * //Veg ball {9551, IDK}, //Tangled toad's legs {9553, IDK}, //Chocolate
	 * bomb {9980, IDK}, //Roast bird meat {9988, IDK}, //Roast beast meat
	 * {9994, IDK}, //Spicy tomato {9996, IDK}, //Spicy minced meat {10136,
	 * IDK}, //Rainbow fish {10476, IDK}, //Purple sweets {10538, IDK},
	 * //Defender horn {10960, IDK}, //Green gloop soup {10961, IDK},
	 * //Frogspawn gumbo {10962, IDK}, //Frogburger {10963, IDK}, //Coated
	 * frogs' legs {10964, IDK}, //Bat shish {10965, IDK}, //Fingers {10966,
	 * IDK}, //Grubs à la mode {10967, IDK}, //Roast frog {10968, IDK},
	 * //Mushrooms {10969, IDK}, //Fillets {10970, IDK}, //Loach {10971, IDK},
	 * //Eel sushi {11023, IDK}, //Magic egg {11026, IDK}, //Chocolate kebbit
	 * {11205, IDK}, //Shrunk ogleroot
	 */
	};

	/**
	 * Checks if the item we're clicking is eatable, and eats it.
	 * 
	 * @param player
	 *            The player clicking items.
	 * @param item
	 *            The item from the packet.
	 * @param slot
	 *            The slot of the item clicked.
	 * @return true if the player has clicked food and dont need to check other
	 *         classes, false if otherwise
	 */
	public static boolean eat(final Player player, Item item, int slot) {
		if (player.isDead()) {
			return false;
		}
		if (System.currentTimeMillis() - player.getLastEat() < 1500) {
			return false;
		}
		for (int i = 0; i < FOOD.length; i++) {
			if (item.getId() == FOOD[i][0]) {
				if (player.getRequestManager().isDueling()) {
					if (player.getRequestManager().getDuel()
							.isRuleToggled(Duel.NO_FOOD)) {
						player.getActionSender().sendMessage(
								"You cannot eat during this duel.");
						return true;
					}
				}
				player.playAnimation(EATING);
				if (FOOD[i].length > 2) {
					player.getInventory().set(slot, new Item(FOOD[i][2]));
				} else {
					player.getInventory().remove(slot, item);
				}
				player.getActionSender().sendMessage(
						"You eat the "
								+ item.getDefinition().getName().toLowerCase()
								+ "...");
				if (player.getSkills().getLevel(Skills.HITPOINTS) < player
						.getSkills().getLevelForExperience(Skills.HITPOINTS)) {
					player.getActionSender().sendMessage(
							"It heals some health.");
					player.heal(FOOD[i][1]);
				}
				player.setLastEat(System.currentTimeMillis());
				return true;
			}
		}
		/*
		 * Use this switch for "special food".
		 */
		switch (item.getId()) {
		case 10476: // Purple sweets
			player.getInventory().remove(slot, new Item(item.getId()) /*
																	 * Only one
																	 * of them.
																	 */);
			int oneTwoOrThree = (int) (Math.random() * (3 + 1));
			player.heal((oneTwoOrThree <= 0) ? 3 : oneTwoOrThree);
			// Increase energy by 10%
			player.setRunEnergy(player.getRunEnergy()
					+ (player.getRunEnergy() / 10));
			if (player.getRunEnergy() > 100) {
				player.setRunEnergy(100);
			}
			player.playAnimation(EATING);
			player.getActionSender().sendMessage(
					"The sugary goodness heals some energy.");
			return true;
		case 7509: // Rock cakes.
			player.getInventory().remove(slot, item);
			Combat.inflictDamage(player, null, 1);
			player.playAnimation(EATING);
			player.forceChat("Ow! I nearly broke a tooth!");
			return true;
		default:
			return false;
		}

	}

}
