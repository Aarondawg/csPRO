package org.hyperion.rs2.content.skills;

import java.util.Random;

import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

public class Slayer {

	/**
	 * Slayer master ID's.
	 */
	public static final int TURAEL = 70; // Easiest slayer tasks.
	public static final int MAZCHNA = 1596; // Second easiest slayer tasks.
	public static final int VANNAKA = 1597; // Medium slayer tasks
	public static final int CHAELDAR = 1598; // Hard slayer tasks.
	public static final int DURADEL = 1599; // Hardest slayer tasks.

	private static Random r = new Random();

	public static void handleSlayerGem(Player player) {
		SlayerTask task = player.getSlayerTask();
		String name = task.getName();
		int master = task.getSlayerMaster();
		int count = task.getCount();
		DialogueLoader dl = DialogueLoader.forId(master);

	}

	public static String[] getTips(Player player) {
		// TODO: I have no idea how to do those atm.
		// Probably using player.getSlayer().getName() :s
		return new String[] { "I do not have any tips for this kind of",
				"monster unfortuantly. Good luck!" };
	}

	/*
	 * hello there ------ what can i help you with?
	 * 
	 * Option list 1: Select an option How am I doing so far? Who are you? Where
	 * are you? Got any tips for me? Nothing really.
	 * 
	 * option 1- how am i doing so far
	 * 
	 * you are currently assigned to kill -----, only ----- more to go.
	 * 
	 * Option list 2:: Who are you? Where are you? Got any tips for me? Thats
	 * all thanks.
	 */

	public static String[] getTaskDialogue(Player player, int id) {
		System.out.println("Getting slayer task??");
		int index = 0;
		int count = 0;
		String taskName = "";
		switch (id) {
		case TURAEL:
			index = getTuraelIndex(player.getSlayerTask().getName());
			System.out.println("Index: " + index);
			// npcIds = TURAEL_MONSTERS[index];
			count = getCount(15, 50);
			taskName = TURAEL_NAMES[index];
			break;
		case MAZCHNA:
			// TODO Level check.
			index = getMazchnaIndex(player.getSlayerTask().getName(), player
					.getSkills().getLevelForExperience(Skills.SLAYER));
			// npcIds = MAZCHNA_MONSTERS[index];
			count = getCount(40, 70);
			taskName = MAZCHNA_NAMES[index];
			if (taskName.equals("Wall beasts")) {
				count = getCount(10, 20);
			}
			break;
		case VANNAKA:
			// TODO: Level check.
			index = getVannakaIndex(player.getSlayerTask().getName(), player
					.getSkills().getLevelForExperience(Skills.SLAYER));
			// npcIds = VANNAKA_MONSTERS[index];
			count = getCount(60, 120);
			taskName = VANNAKA_NAMES[index];

			if (taskName.equals("Crocodiles")
					|| taskName.equals("Earth warriors")
					|| taskName.equals("Elves")
					|| taskName.equals("Green dragons")) {
				count = getCount(30, 60);
			}
			if (taskName.equals("Sea snakes")) {
				count = getCount(50, 120);
			}
			break;
		case CHAELDAR:
			index = getChaeldarIndex(player.getSlayerTask().getName(), player
					.getSkills().getLevelForExperience(Skills.SLAYER));
			// npcIds = CHAELDAR_MONSTERS[index];
			count = getCount(110, 170);
			taskName = CHAELDAR_NAMES[index];
			if (taskName.equals("Bronze Dragons")
					|| taskName.equals("Mutated zygomites")) {
				count = getCount(30, 60);
			}
			if (taskName.equals("Elves")) {
				count = getCount(30, 90);
			}
			break;
		case DURADEL:
			index = getDuradelIndex(player.getSlayerTask().getName(), player
					.getSkills().getLevelForExperience(Skills.SLAYER));
			// npcIds = DURADEL_MONSTERS[index];
			count = getCount(130, 200);
			/*
			 * npcIds =
			 * getSuperHighLevelNPCs(player.getSkills().getCombatLevel(),
			 * player.getSkills().getLevelForExperience(Skills.SLAYER)); amount
			 * = getAmount(130, 200); final int[] FORTYTOAIGHTY = { 4673, 4674,
			 * 4675, 4676, 4418, 1591, 3068, 3069, 3070, 3071, 3590, 4527, 4528,
			 * 4529, 4530, 4531, 4532, 4533 }; /* 40 - 80 npcs.
			 *//*
				 * case 4673://Black dragons case 4674://Black dragons case
				 * 4675://Black dragons case 4676://Black dragons case
				 * 4418://Gorak..? case 1591://Iron dragon. case 3068://Skeletal
				 * Wyvern case 3069://Skeletal Wyvern case 3070://Skeletal
				 * Wyvern case 3071://Skeletal Wyvern case 3590://Steel dragon.
				 * case 4527://Suqah case 4528://Suqah case 4529://Suqah case
				 * 4530://Suqah case 4531://Suqah case 4532://Suqah case
				 * 4533://Suqah
				 *//*
					 * for(int npc : npcIds) { for(int i : FORTYTOAIGHTY) {
					 * if(npc == i) { amount = getAmount(40, 80); break; } } }
					 */
			break;
		}
		player.setSlayerTask(new SlayerTask(taskName, count, index, id));
		return new String[] { "Your new task is to kill " + count + " "
				+ taskName + "." };
	}

	private static int getCount(int fromRange, int toRange) {
		return (int) ((java.lang.Math.random() * (toRange - fromRange)) + fromRange);
	}

	/**
	 * All those arrays below belongs to Turael, and maybe some other slayer
	 * masters aswell.
	 */

	private static final int[] BANSHEES = { 1612, };

	private static final int[] BATS = { 78, 412, 3711, 4345, };

	private static final int[] BIRDS = { 41, 44, 45, 46, 48, 131, 136, 137,
			138, 139, 951, 1015, 1016, 1017, 1018, 1401, 1402, 1403, 1475,
			1476, 1620, 1621, 1692, 1996, 2313, 2314, 2315, 2252, 2459, 2460,
			2461, 2462, 2463, 2464, 2465, 2466, 2467, 2468, 2693, 2694, 3366,
			3367, 3368, 3369, 3370, 3371, 3375, 3476, 4971, 3675, 3676, 4972, };

	private static final int[] BEARS = { 105, 106, 1195, 1196, 1197, 1326,
			1327, 3645, 3664, };

	private static final int[] CAVE_BUGS = { 1832, 5750 };

	private static final int[] CAVE_SLIMES = { 1831 };

	private static final int[] COWS = { 81, 397, 955, 1691, 1766, 1767, 1768,
			1886, 2310, 3309, 5211, /* 5603 Unicow lol */};

	private static final int[] CRAWLING_HANDS = { 1648, 1649, 1650, 1651, 1652,
			1653, 1654, 1655, 1656, 1657 };

	private static final int[] DESERT_LIZARDS = { 2804, 2805, 2806 };

	private static final int[] DOGS = { 99, 1593, 1594, 3582, 5417, 5418 };

	private static final int[] DWARVES = { 118, 119, 120, 121, 1795, 1796,
			1796, 2423, 3219, 3220, 3221, 3268, 3269, 3270, 3271, 3272, 3273,
			3274, 3275 };

	private static final int[] GHOSTS = { 103, 104, 491, 1541, 1549, 1707,
			1708, 4387, 4388, 5342, 5343, 5344, 5345, 5346, 5347, 5348, 5349,
			5350, 5351, 5352, 5369, 5370, 5371, 5372, 5373, 5374, 6094, 6095,
			6097, 6098 };

	private static final int[] GOBLINS = { 100, 101, 102, 298, 299, 444, 445,
			489, 1769, 1770, 1771, 1772, 1773, 1774, 1775, 1776, 1822, 1823,
			1824, 1825, 2069, 2070, 2071, 2072, 2073, 2074, 2075, 2076, 2077,
			2078, 2274, 2275, 2276, 2277, 2278, 2279, 2280, 2281, 2678, 2679,
			2680, 2681, 3060, 3264, 3265, 3266, 3267, 3648, 3663, 4261, 4262,
			4263, 4264, 4265, 4266, 4267, 4268, 4269, 4270, 4271, 4272, 4273,
			4274, 4275, 4276, 4407, 4408, 4409, 4410, 4411, 4412, 4479, 4480,
			4481, 4482, 4483, 4484, 4485, 4486, 4487, 4488, 4489, 4490, 4491,
			4492, 4499 };

	private static final int[] ICEFIENDS = { 3406, };

	private static final int[] MINOTAURS = { 4404, 4405, 4406, };

	private static final int[] MONKEYS = { 132, 1455, 1456, 1457, 1458, 1459,
			1460, 1463, 1464, 1465, 1466, 1467, 1480, 1481, 1484, 1485, 1487,
			4344, 4371, 4372, 5852, };

	private static final int[] SCORPIONS = { 107, 108, 109, 144, 271, 493,
			1477, 4402, 4403 };

	private static final int[] SKELETONS = { 91, 92, 93, 459, 1471, 1973, 2036,
			2037, 3065, 3291, 3581, 3697, 3698, 3699, 3700, 3701, 3702, 3703,
			3704, 3705, 4384, 4385, 4386, 5332, 5333, 5334, 5335, 5336, 5337,
			5338, 5339, 5340, 5341, 5359, 5340, 5341, 5359, 5365, 5366, 5367,
			5368, 5381, 5384, 5385, 5386, 5387, 5388, 5389, 5390, 5391, 5392,
			5411, 5412, 5422, 6091, 6092, 6093, 6103, 6104, 6105, 6106, 6107, };

	private static final int[] SPIDERS = { 58, 59, 60, 61, 62, 63, 64, 134,
			977, 1004, 1009, 1473, 1478, 2034, 2035, 2491, 2492, 2850, 3585,
			4401, };

	private static final int[] WOLFES = { 95, 96, 97, 141, 142, 143, 839, 1198,
			1330, 1558, 1559, 1951, 1952, 1953, 1954, 1955, 1956, 4413, 4414,
			6006, 6007, 6008, 6009, 6010, 9011, 6012, 6013, 6014, 6015, 6016,
			6017, 6018, 6019, 6020, 6021, 6022, 6023, 6024, 6025, 6046, 6047,
			6048, 6049, 6050, 6051, 6052, };

	private static final int[] ZOMBIES = { 73, 74, 75, 76, 77, 419, 420, 421,
			422, 423, 424, 1465, 1466, 1467, 1485, 1486, 2058, 2837, 2838,
			2839, 2840, 2841, 2842, 2843, 2844, 2845, 2846, 2847, 2848, 3066,
			3622, 4392, 4393, 4394, 5293, 5294, 5295, 5296, 5297, 5298, 5299,
			5300, 5301, 5302, 5303, 5304, 5305, 5306, 5307, 5308, 5309, 5310,
			5311, 5312, 5313, 5314, 5315, 5316, 5317, 5318, 5319, 5320, 5321,
			5322, 5323, 5324, 5325, 5326, 5327, 5328, 5329, 5330, 5331, 5375,
			5376, 5377, 5378, 5379, 5380, 5393, 5394, 5395, 5396, 5397, 5398,
			5399, 5400, 5401, 5402, 5403, 5404, 5405, 5406, 5407, 5408, 5409,
			5410, 5629, 5630, 5631, 5632, 5633, 5634, 5635, 5636, 5637, 5638,
			5639, 5640, 5641, 5642, 5643, 5644, 5645, 5646, 5647, 5648, 5649,
			5650, 5651, 5652, 5653, 5654, 5655, 5656, 5657, 5658, 5659, 5660,
			5661, 5662, 5663, 5664, 5665, 6088, 6089, 6090, 6099, 6100, };

	public static String[] TURAEL_NAMES = { "Banchees", "Bats", "Birds",
			"Bears", "Cave bugs", "Cave slimes", "Cows", "Crawling Hands",
			"Desert Lizards", "Dogs", "Dwarves", "Ghosts", "Goblins",
			"Icefiends", "Minotaurs", "Monkeys", "Scorpions", "Skeletons",
			"Spiders", "Wolves", "Zombies" };

	public static final int[][] TURAEL_MONSTERS = { BANSHEES, BATS, BIRDS,
			BEARS, CAVE_BUGS, CAVE_SLIMES, COWS, CRAWLING_HANDS,
			DESERT_LIZARDS, DOGS, DWARVES, GHOSTS, GOBLINS, ICEFIENDS,
			MINOTAURS, MONKEYS, SCORPIONS, SKELETONS, SPIDERS, WOLFES, ZOMBIES, };

	/**
	 * Gets the array index of npc's for the weakest of the slayer masters,
	 * Turael.
	 * 
	 * @param previousName
	 *            Used to make sure we aren't getting the same task twice.
	 * @return An array index used to get an NPC array and a Name array.
	 */
	private static int getTuraelIndex(String previousName) {
		int index = r.nextInt(TURAEL_MONSTERS.length);
		while (TURAEL_NAMES[index] == previousName) {
			index = r.nextInt(TURAEL_MONSTERS.length);
		}
		return index;
	}

	/**
	 * All the array's below belongs to Mazchna, and probably some other masters
	 * aswell.
	 */

	private static final int[] CATABLEPONS = { 4397, 4398, 4399 };

	private static final int[] CAVE_CRAWLERS = { 1600, 1601, 1602, 1603 };

	private static final int[] COCKATRICES = { 1620, 1621 };

	private static final int[] FLESH_CRAWLERS = { 4389, 4390, 4391 };

	private static final int[] GHOULS = { 1218, 3059 /* Ghoul champion */};

	private static final int[] HILL_GIANTS = { 117, 4689, 4690, 4691, 4692,
			4693 };

	private static final int[] HOBGOBINS = { 122, 123, 2685, 2686, 2687, 2688,
			3061, 3583, 4898 };

	private static final int[] ICE_WARRIORS = { 125, 145, 3073 };

	private static final int[] KALPHITES = { 1153, 1154, 1155, 1156, 1157,
			1158, 1159, 1160, 3589, 3835, 3836 };

	/*
	 * 1153: Kalphite Worker 1154: Kalphite Soldier 1155: Kalphite Guardian
	 * 1156: Kalphite Worker 1157: Kalphite Guardian 1158: Kalphite Queen 1159:
	 * Kalphite Queen 1160: Kalphite Queen 1994: Jackal 3589: Kalphite Soldier
	 * 3835: Kalphite Queen 3836: Kalphite Queen
	 */
	private static final int[] MOGRES = { 2801 };

	private static final int[] PYREFIENDS = { 1633, 1634, 1635, 1636 };

	private static final int[] ROCK_SLUGS = { 1622, 1623 };

	// private static final int[] VAMPIRES = { 1023, 1220, 1223, 1225 };
	private static final int[] VAMPIRES = { 1220, 1223 };

	public static String[] MAZCHNA_NAMES = { "Banshees", "Bats", "Bears",
			"Catablepons", "Cave crawlers", "Cave slimes", "Cockatrices",
			"Crawling Hands", "Desert Lizards", "Dogs", "Flesh crawlers",
			"Ghouls", "Ghosts", "Hill Giants", "Hobgobins", "Ice warriors",
			"Kalphites", "Mogres", "Pyrefiends", "Rock slugs", "Skeletons",
			"Vampires", "Wolfs", "Zombies" };

	public static final int[][] MAZCHNA_MONSTERS = { BANSHEES, BATS, BEARS,
			CATABLEPONS, CAVE_CRAWLERS, CAVE_SLIMES, COCKATRICES,
			CRAWLING_HANDS, DESERT_LIZARDS, DOGS, FLESH_CRAWLERS, GHOULS,
			GHOSTS, HILL_GIANTS, HOBGOBINS, ICE_WARRIORS, KALPHITES, MOGRES,
			PYREFIENDS, ROCK_SLUGS, SKELETONS, VAMPIRES, WOLFES, ZOMBIES };

	// TODO: Fill in those.
	private static final int[] MAZCHNA_LEVELREQS = { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

	};

	/**
	 * Gets the array index of npc's for the medium weak slayer masters,
	 * Mazchna.
	 * 
	 * @param previousName
	 *            Used to make sure we aren't getting the same task twice.
	 * @return An array index used to get an NPC array and a Name array.
	 */
	private static int getMazchnaIndex(String previousName, int slayerLevel) {
		int index = r.nextInt(MAZCHNA_MONSTERS.length);
		while (MAZCHNA_NAMES[index] == previousName
				|| slayerLevel >= MAZCHNA_LEVELREQS[index]) {
			index = r.nextInt(MAZCHNA_MONSTERS.length);
		}
		return index;
	}

	/**
	 * All the arrays below belongs to Vannaka, and probably some other masters
	 * aswell.
	 */

	private static final int[] ABERRANT_SPECTRES = { 1604, 1605, 1606, 1607 };

	private static final int[] ANKOUS = { 4381, 4382, 4383 };

	private static final int[] BASILISKS = { 1616, 1617 };

	private static final int[] BLOODVELDS = { 1618, 1619 };

	private static final int[] BRINE_RATS = { 3707 };

	private static final int[] CROCODILES = { 1993 };

	private static final int[] DUST_DEVILS = { 1624 };

	private static final int[] EARTH_WARRIORS = { 124 };

	private static final int[] ELVES = { 1183, 1184, 1185, 2359, 2360, 2361,
			2362 };

	private static final int[] GREEN_DRAGONS = { 4677, 4678, 4679, 4680 };

	private static final int[] HARPIE_BUG_SWARMS = { 3153 };

	private static final int[] ICE_GIANTS = { 111, 3072, 4685, 4686, 4687 };

	private static final int[] INFERNAL_MAGES = { 1643, 1644, 1645, 1646, 1647 };

	private static final int[] JELLYS = { 1637, 1638, 1639, 1640, 1641, 1642 };

	private static final int[] JUNGLE_HORRORS = { 4348, 4349, 4350, 4351, 4352 };

	private static final int[] KILLERWATTS = { 3201, 3202 };

	private static final int[] LESSER_DEMONS = { 82,
			3064/* Lesser demon champ */, 4694, 4695, 4696, 4697, 6101 };

	private static final int[] MOLANISKS = { 5751 };

	private static final int[] MOSS_GIANTS = { 112, 1587, 1588, 1681, 4534,
			4688, 4706 };

	private static final int[] OGRES = { 114, 115, 374, 3587 };

	private static final int[] OTHERWORLDLY_BEINGS = { 126 };

	private static final int[] SEA_SNAKES = { 3939, 3940, 3943 };

	private static final int[] SHADES = { 425, 426, 427, 428, 429, 430, 3617 };

	private static final int[] SHADOW_WARRIORS = { 158 };

	private static final int[] TUROTHS = { 1626, 1627, 1628, 1629, 1630, 1631,
			1632 };

	// TODO: Werewolf men for this..??
	private static final int[] WEREWOLFS = { 6006, 6007, 6008, 6009, 6010,
			6011, 6012, 6013, 6014, 6015, 6016, 6017, 6018, 6019, 6020, 6021,
			6022, 6023, 6024, 6025 };

	public static String[] VANNAKA_NAMES = { "Aberrant spectres", "Ankous",
			"Banshees", "Basilisks", "Bloodvelds", "Brine rats", "Cockatrices",
			"Crocodiles", "Dust devis", "Earth warriors", "Elves", "Ghouls",
			"Green dragons", "Harpie bug swarms", "Hill giants", "Ice giants",
			"Ice warriors", "Infernal mages", "Jellys", "Jungle horrors",
			"Killerwatts", "Lesser demons", "Mogres", "Molanisks",
			"Moss giants", "Ogres", "Otherworldly beings", "Pyrefiends",
			"Sea snakes", "Shades", "Shadow warriors", "Turoths", "Vampires",
			"Werewolfs" };

	public static final int[][] VANNAKA_MONSTERS = { ABERRANT_SPECTRES, ANKOUS,
			BANSHEES, BASILISKS, BLOODVELDS, BRINE_RATS, COCKATRICES,
			CROCODILES, DUST_DEVILS, EARTH_WARRIORS, ELVES, GHOULS,
			GREEN_DRAGONS, HARPIE_BUG_SWARMS, HILL_GIANTS, ICE_GIANTS,
			ICE_WARRIORS, INFERNAL_MAGES, JELLYS, JUNGLE_HORRORS, KILLERWATTS,
			LESSER_DEMONS, MOGRES, MOLANISKS, MOSS_GIANTS, OGRES,
			OTHERWORLDLY_BEINGS, PYREFIENDS, SEA_SNAKES, SHADES,
			SHADOW_WARRIORS, TUROTHS, VAMPIRES, WEREWOLFS, };// 34

	// TODO: Dust devils etc.
	private static final int[] VANNAKA_LEVELREQS = { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, };

	/**
	 * Gets the array index of npc's for the medium slayer masters, Vannaka.
	 * 
	 * @param previousName
	 *            Used to make sure we aren't getting the same task twice.
	 * @return An array index used to get an NPC array and a Name array.
	 */
	private static int getVannakaIndex(String previousName, int slayerLevel) {
		int index = r.nextInt(VANNAKA_MONSTERS.length);
		while (VANNAKA_NAMES[index] == previousName
				|| slayerLevel >= VANNAKA_LEVELREQS[index]) {
			index = r.nextInt(VANNAKA_MONSTERS.length);
		}
		return index;
	}

	private static final int[] BLUE_DRAGONS = { 55, 4681, 4682, 4683, 4684,
			5178 };

	private static final int[] BRONZE_DRAGONS = { 1590 };

	private static final int[] CAVE_HORRORS = { 4353, 4354, 4355, 4356, 4357 };

	private static final int[] DAGANNOTHS = { 1338, 1339, 1340, 1341, 1342,
			1343, 1344, 1345, 1346, 1347, 1348, 1349, 1350, 1351, 1352, 1353,
			1354, 1355, 1356, 2880, 2881, 2882, 2883, 2887, 2888, 3591 };

	private static final int[] FEVER_SPIDERS = { 2850 };

	private static final int[] GARGOYLES = { 1610, 1611 };

	private static final int[] KURASKS = { 1608, 1609 };

	private static final int[] MUTATED_ZYGOMITES = { 3346, 3347 };

	private static final int[] TROLLS = { 72, 391, 392, 394, 395, 396, 1101,
			1102, 1103, 1104, 1105, 1106, 1107, 1108, 1109, 1110, 1111, 1112,
			1113, 1114, 1115, 1116, 1117, 1118, 1119, 1120, 1121, 1122, 1123,
			1124, 1125, 1126, 1127, 1128, 1129, 1130, 1131, 1132, 1133, 1134,
			1138, 1560, 1561, 1562, 1563, 1564, 1565, 1566, 1936, 1937, 1938,
			1939, 1940, 1941, 1942, 3584, 3840, 3841, 3842, 3843, 3845, 3847,
			5472, 5473, 5474, 5475, 5476, 5521, 5522, 5523, 5524, 5525, 5526,
			5527, 5528 };

	public static final String[] CHAELDAR_NAMES = { "Aberrant spectres",
			"Banshees", "Basilisks", "Bloodvelds", "Blue dragons",
			"Brine rats", "Bronze dragons", "Cave crawlers", "Cave horrors",
			"Crawling hands", "Dagannoths", "Dust devis", "Elves",
			"Fever spiders", "Gargoyles", "Harpie bug swarms",
			"Infernal mages", "Jellys", "Jungle horrors", "Kalphites",
			"Kurasks", "Lesser demons", "Mutated zygomites", "Shadow warriors",
			"Trolls", "Turoths",
	/* "Warped terrorbirds", "Warped tortoises" */};

	public static final int[][] CHAELDAR_MONSTERS = { ABERRANT_SPECTRES,
			BANSHEES, BASILISKS, BLOODVELDS, BLUE_DRAGONS, BRINE_RATS,
			BRONZE_DRAGONS, CAVE_CRAWLERS, CAVE_HORRORS, CRAWLING_HANDS,
			DAGANNOTHS, DUST_DEVILS, ELVES, FEVER_SPIDERS, GARGOYLES,
			HARPIE_BUG_SWARMS, INFERNAL_MAGES, JELLYS, JUNGLE_HORRORS,
			KALPHITES, KURASKS, LESSER_DEMONS, MUTATED_ZYGOMITES,
			SHADOW_WARRIORS, TROLLS, TUROTHS, };

	private static final int[] CHAELDAR_LEVELREQS = { 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };

	/**
	 * Gets the array index of npc's for the hard slayer masters, Chaeldar.
	 * 
	 * @param previousName
	 *            Used to make sure we aren't getting the same task twice.
	 * @return An array index used to get an NPC array and a Name array.
	 */
	private static int getChaeldarIndex(String previousName, int slayerLevel) {
		int index = r.nextInt(CHAELDAR_MONSTERS.length);
		while (CHAELDAR_NAMES[index] == previousName
				|| slayerLevel >= CHAELDAR_LEVELREQS[index]) {
			index = r.nextInt(CHAELDAR_MONSTERS.length);
		}
		return index;
	}

	private static final int[] ABYSSAL_DEMONS = { 1615 };

	// private static final int[] AQUANITES = {}; //Hmm?

	private static final int[] BLACK_DEMONS = { 84, 677, 4702, 4703, 4704, 4705 };

	private static final int[] BLACK_DRAGONS = { 54, 4673, 4674, 4675, 4676 };

	private static final int[] DARK_BEASTS = { 2783 };

	private static final int[] FIRE_GIANTS = { 110, 1582, 1583, 1584, 1585,
			1586 };

	private static final int[] GORAKS = { 4418 };

	private static final int[] GREATER_DEMONS = { 83, 4698, 4699, 4700, 4701 };

	private static final int[] HELLHOUNDS = { 49, 1575/* Skeletal hellhound */,
			3586 };

	private static final int[] IRON_DRAGONS = { 1591 };

	private static final int[] NECHRYAELS = { 1613 };

	private static final int[] SCABRITES = {/* Start of Locust riders */5251,
			5252, 5255, 5256,/* end of them */1969, 2001, 2009, 4500, 5250,
			5253, 5254 }; // Might be needing alot of ids.

	private static final int[] SKELETON_WYVERNS = { 3068, 3069, 3070, 3071 };

	// private static final int[] SPIRITUAL_MAGES = {}; //God wars lel

	private static final int[] STEEL_DRAGONS = { 1592, 3590 };

	private static final int[] SUQAHS = { 4527, 4528, 4529, 4530, 4531, 4532,
			4533 };

	private static final int[] WATERFIENDS = { 5361 };

	public static final String[] DURADEL_NAMES = { "Aberrant spectres",
			"Abyssal demons",/* "Aquanites", */"Black demons", "Black dragons",
			"Bloodvelds", "Dagannoths", "Dark beasts", "Dust devis",
			"Fire giants", "Gargoyles", "Goraks", "Greater demons",
			"Hellhound", "Iron dragon", "Kalphites",
			/* "Mithril dragons", */"Nechryaels", "Scabarites",
			"Skeleton Wyvern", /* "Spiritual Mage", */"Steel dragon", "Suqah",
			"Waterfiend",
	/* "Warped terrorbirds" */};

	public static final int[][] DURADEL_MONSTERS = {
			ABERRANT_SPECTRES,
			ABYSSAL_DEMONS,
			// AQUANITES,
			BLACK_DEMONS, BLACK_DRAGONS, BLOODVELDS, DAGANNOTHS, DARK_BEASTS,
			DUST_DEVILS, FIRE_GIANTS, GARGOYLES, GORAKS, GREATER_DEMONS,
			HELLHOUNDS, IRON_DRAGONS, KALPHITES, NECHRYAELS, SCABRITES,
			SKELETON_WYVERNS,
			// SPIRITUAL_MAGES,
			STEEL_DRAGONS, SUQAHS, WATERFIENDS

	};

	private static final int[] DURADEL_LEVELREQS = { 0, 85,// Abbysal demons.
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };

	/**
	 * Gets the array index of npc's for the hardest slayer masters, Duradel.
	 * 
	 * @param previousName
	 *            Used to make sure we aren't getting the same task twice.
	 * @return An array index used to get an NPC array and a Name array.
	 */
	private static int getDuradelIndex(String previousName, int slayerLevel) {
		int index = r.nextInt(DURADEL_MONSTERS.length);
		while (DURADEL_NAMES[index] == previousName
				|| slayerLevel >= DURADEL_LEVELREQS[index]) {
			index = r.nextInt(DURADEL_MONSTERS.length);
		}
		return index;
	}

}
