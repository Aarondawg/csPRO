package org.hyperion.rs2.model.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Combat.AttackType;

/**
 * Contains equipment utility methods.
 * 
 * @author Graham Edgecombe
 * @author Lothy
 * 
 */
public class Equipment {

	/**
	 * The size of the equipment container.
	 */
	public static final int SIZE = 14;

	/**
	 * Items which are classified as capes.
	 */
	public static int CAPES[] = { 3781, 3783, 3785, 3787, 3789, 3777, 3779,
			3759, 3761, 3763, 3765, 6111, 6570, 6568, 1007, 1019, 1021, 1023,
			1027, 1029, 1031, 1052, 2412, 2413, 2414, 4304, 4315, 4317, 4319,
			4321, 4323, 4325, 4327, 4329, 4331, 4333, 4335, 4337, 4339, 4341,
			4343, 4345, 4347, 4349, 4351, 4353, 4355, 4357, 4359, 4361, 4363,
			4365, 4367, 4369, 4371, 4373, 4375, 4377, 4379, 4381, 4383, 4385,
			4387, 4389, 4391, 4393, 4395, 4397, 4399, 4401, 4403, 4405, 4407,
			4409, 4411, 4413, 4514, 4516, 6070, 6568, 6570, 6959, 8284, 9074,
			9101, 9747, 9748, 9750, 9751, 9753, 9754, 9756, 9757, 9759, 9760,
			9762, 9763, 9765, 9766, 9768, 9769, 9771, 9772, 9774, 9775, 9777,
			9778, 9780, 9781, 9783, 9784, 9786, 9787, 9789, 9790, 9792, 9793,
			9795, 9796, 9798, 9799, 9801, 9802, 9804, 9805, 9807, 9808, 9810,
			9811, 9813, 9948, 9949, 10498, 10499 };

	/**
	 * Items which are classified as boots.
	 */
	public static int BOOTS[] = { 7596, 6619, 7159, 7991, 6666, 6061, 6106, 88,
			89, 626, 628, 630, 632, 634, 1061, 1837, 1846, 2577, 2579, 2894,
			2904, 2914, 2924, 2934, 3061, 3105, 3107, 3791, 4097, 4107, 4117,
			4119, 4121, 4123, 4125, 4127, 4129, 4131, 4310, 5064, 5345, 5557,
			6069, 6106, 6143, 6145, 6147, 6328, 6920, 6349, 6357, 3393 };

	/**
	 * Items which are classified as gloves.
	 */
	public static int GLOVES[] = { 7595, 6629, 2491, 1065, 2487, 2489, 3060,
			1495, 775, 777, 778, 6708, 1059, 1063, 1065, 1580, 2487, 2489,
			2491, 2902, 2912, 2922, 2932, 2942, 3060, 3799, 4095, 4105, 4115,
			4308, 5556, 6068, 6110, 6149, 6151, 6153, 6922, 7454, 7455, 7456,
			7457, 7458, 7459, 7460, 7461, 7462, 6330, 3391, 10336, 8842 };

	/**
	 * Items which are classified as shields.
	 */
	public static int SHIELDS[] = { 7342, 7348, 7354, 7360, 7334, 7340, 7347,
			7352, 7358, 7356, 7350, 7344, 7332, 7338, 7336, 7360, 1171, 1173,
			1175, 1177, 1179, 1181, 1183, 1185, 1187, 1189, 1191, 1193, 1195,
			1197, 1199, 1201, 1540, 2589, 2597, 2603, 2611, 2621, 2629, 2659,
			2667, 2675, 2890, 3122, 3488, 3758, 3839, 3840, 3841, 3842, 3843,
			3844, 4072, 4156, 4224, 4225, 4226, 4227, 4228, 4229, 4230, 4231,
			4232, 4233, 4234, 4302, 4507, 4512, 6215, 6217, 6219, 6221, 6223,
			6225, 6227, 6229, 6231, 6233, 6235, 6237, 6239, 6241, 6243, 6245,
			6247, 6249, 6251, 6253, 6255, 6257, 6259, 6261, 6263, 6265, 6267,
			6269, 6271, 6273, 6275, 6277, 6279, 6524, 6889, 8844, 8845, 8846,
			8847, 8848, 8849, 8850, 11283, 11284, };

	/**
	 * Items which are classified as hats.
	 */
	public static int HATS[] = { 4041, 4042, 4502, 7319, 7321, 7323, 7325,
			7327, 1167, 8074, 4168, 1169, 6665, 6665, 7321, 6886, 6547, 6548,
			2645, 2647, 2649, 4856, 4857, 4858, 4859, 4880, 4881, 4882, 4883,
			4904, 4905, 4906, 4907, 4928, 4929, 4930, 4931, 4952, 4953, 4954,
			4955, 4976, 4977, 4978, 4979, 4732, 4753, 4611, 6188, 6182, 4511,
			4056, 4071, 4724, 2639, 2641, 2643, 2665, 6109, 5525, 5527, 5529,
			5531, 5533, 5535, 5537, 5539, 5541, 5543, 5545, 5547, 5549, 5551,
			74, 579, 656, 658, 660, 662, 664, 740, 1017, 1037, 1040, 1042,
			1044, 1046, 1048, 1050, 1053, 1055, 1057, 1137, 1139, 1141, 1143,
			1145, 1147, 1149, 1151, 1153, 1155, 1157, 1159, 1161, 1163, 1165,
			1506, 1949, 2422, 2581, 2587, 2595, 2605, 2613, 2619, 2627, 2631,
			2633, 2635, 2637, 2651, 2657, 2673, 2900, 2910, 2920, 2930, 2940,
			2978, 2979, 2980, 2981, 2982, 2983, 2984, 2985, 2986, 2987, 2988,
			2989, 2990, 2991, 2992, 2993, 2994, 2995, 3057, 3385, 3486, 3748,
			3749, 3751, 3753, 3797, 4071, 4089, 3755, 4099, 4109, 4164, 4302,
			4506, 4511, 4513, 4515, 4551, 4567, 4708, 4716, 4724, 4745, 4753,
			4856, 4857, 4858, 4859, 4880, 4881, 4882, 4883, 4904, 4905, 4906,
			4907, 4952, 4953, 4954, 4955, 4976, 4977, 4978, 4979, 5013, 5014,
			5554, 5574, 6109, 6128, 6131, 6137, 6182, 6188, 6335, 6337, 6339,
			6345, 6355, 6365, 6375, 6382, 6392, 6400, 6918, 6656, 2581, 7539,
			7394, 7396, 7534, 5574, 6885, 6858, 6860, 6862, 6856, 6326, 6128,
			6137, 7400, 7323, 7325, 7327, 7003, 10334, 10342, 10548/*
																	 * Fighter
																	 * hat
																	 */,
			10828 /* Helm of nezinot */, 9749, 9752, 9755, 9758, 9761, 9764,
			9767, 9770, 9773, 9776, 9779, 9782, 9785, 9788, 9791, 9794, 9797,
			9800, 9803, 9806, 9809, 9812, 9814, 9950, 9950, };

	/**
	 * Items which are classified as amulets.
	 */
	public static int AMULETS[] = { 1654, 1656, 1658, 1660, 1662, 1664, 8081,
			8033, 7968, 6585, 86, 87, 295, 421, 552, 589, 1478, 1692, 1694,
			1696, 1698, 1700, 1702, 1704, 1706, 1708, 1710, 1712, 1725, 1727,
			1729, 1731, 4021, 4081, 4250, 4677, 6040, 6041, 6208, 1718, 1722,
			6859, 6863, 6857, 10344 };

	/**
	 * Items which are classified as rings.
	 */
	public static int RINGS[] = { 773, 1635, 1637, 1639, 1641, 1643, 1645,
			2550, 2552, 2554, 2556, 2558, 2560, 2562, 2564, 2566, 2568, 2570,
			2572, 4202, 4657, 6465, 6737, 6731, 6735, 6735, 6583, 6733 };

	/**
	 * Items which are classified as body.
	 */
	public static int BODY[] = { 7362, 7364, 636, 638, 640, 642, 644, 426,
			1005, 1757, 7592, 6617, 7376, 544, 7372, 7370, 577, 3793, 3775,
			3773, 3771, 3769, 3767, 6139, 1135, 2499, 2501, 1035, 540, 5553,
			4757, 1833, 6388, 6384, 4111, 4101, 4091, 6186, 6184, 6180, 3058,
			4509, 4504, 4069, 4728, 4736, 4712, 6107, 2661, 3140, 1101, 1103,
			1105, 1107, 1109, 1111, 1113, 1115, 1117, 1119, 1121, 1123, 1125,
			1127, 1129, 1131, 1133, 2583, 2591, 2599, 2607, 2615, 2623, 2653,
			2669, 3481, 4712, 4720, 4728, 4749, 4892, 4893, 4894, 4895, 4916,
			4917, 4918, 4919, 4964, 4965, 4966, 4967, 6107, 6133, 6322, 6322,
			6129, 75, 6916, 6916, 4111, 6654, 6654, 75, 7399, 7390, 7374, 5575,
			2503, 6341, 6351, 3387, 5030, 5032, 5034, 5030, 5032, 5034, 7392,
			546 };

	/**
	 * Items which are classified as legs.
	 */
	public static int LEGS[] = { 7378, 7380, 7382, 7368, 7366, 7388, 646, 648,
			650, 652, 654, 428, 1097, 1095, 7593, 6625, 8020, 8015, 7384, 6141,
			1835, 538, 1033, 5555, 4759, 6386, 6390, 2497, 2495, 2493, 1099,
			4113, 4103, 4093, 6924, 6187, 6185, 6181, 3059, 4510, 4505, 4070,
			6108, 538, 542, 548, 1011, 1013, 1015, 1067, 1069, 1071, 1073,
			1075, 1077, 1079, 1081, 1083, 1085, 1087, 1089, 1091, 1093, 2585,
			2593, 2601, 2609, 2617, 2625, 2655, 2663, 2671, 3059, 3389, 3472,
			3473, 3474, 3475, 3476, 3477, 3478, 3479, 3480, 3483, 3485, 3795,
			4087, 4585, 4712, 4714, 4722, 4730, 4738, 4751, 4759, 4874, 4875,
			4876, 4877, 4898, 4899, 4900, 4901, 4922, 4923, 4924, 4925, 4946,
			4947, 4948, 4949, 4970, 4971, 4972, 4973, 4994, 4995, 4996, 4997,
			5048, 5050, 5052, 5576, 6107, 6130, 6187, 6390, 6386, 6390, 6396,
			6404, 6135, 6809, 6916, 4091, 4111, 6655, 6654, 7398, 7398, 7386,
			6324, 6343, 6353, 3387, 5036, 5038, 5040, 10332, 10340, 10346, 8840 };

	/**
	 * Items which are classified as platebody.
	 */
	public static int PLATEBODY[] = { 636, 638, 640, 642, 644, 426, 8031, 8027,
			6617, 544, 577, 3793, 3773, 3775, 3771, 3769, 3767, 6139, 1035,
			540, 5553, 4757, 1833, 1835, 6388, 6384, 4111, 4101, 4868, 4869,
			4870, 4871, 4892, 4893, 4894, 4895, 4916, 4917, 4918, 4919, 4940,
			4941, 4942, 4943, 4964, 4965, 4966, 4967, 4988, 4989, 4990,
			0x2f9a0eb, 6186, 6184, 6180, 3058, 4509, 4504, 4069, 4728, 4736,
			4712, 6107, 2661, 3140, 1115, 1117, 1119, 1121, 1123, 1125, 1127,
			2583, 2591, 2599, 2607, 2615, 6322, 2623, 2653, 2669, 3481, 4720,
			4728, 4749, 2661, 6129, 6916, 4091, 6654, 6133, 75, 7399, 7390,
			5575, 6341, 6351, 3387, 5030, 5032, 5034, 7392, 10330, 10338,
			10348, 10551, 8839 };

	/**
	 * Items which are classified as full helmets.
	 */
	public static int FULL_HELM[] = { 4041, 4042, 1147, 3748, 6137, 6128, 3753,
			3755, 3749, 3751, 1149, 3751, 7594, 4708, 4716, 4745, 4732, 5554,
			4753, 4732, 4753, 6188, 4511, 4056, 4071, 4724, 6109, 2665, 1153,
			1155, 1157, 1159, 1161, 1163, 1165, 2587, 2595, 2605, 2613, 2619,
			2627, 2657, 2673, 3486, 6402, 6394, 6131, 74, 7539, 7539, 7534,
			5574, 6326 };

	/**
	 * Items which are classified as full masks.
	 */
	public static int FULL_MASK[] = { 4502, 6623, 7990, 7594, 1153, 1155, 1157,
			1159, 1161, 1163, 1165, 4732, 5554, 4753, 4611, 6188, 3507, 4511,
			4056, 4071, 4724, 2665, 1053, 1055, 1057 };

	/**
	 * Items which are classified as arrows.
	 */
	public static int ARROWS[] = { 78, /* Ice arrows */
	598, /* Bronze fire arrows */
	877, /* Bronze bolts */
	878, /* Bronze bolts(p) */
	879, /* Opal bolts */
	880, /* Pearl bolts */
	881, /* Barbed bolts */
	882, /* Bronze arrow */
	883, /* Bronze arrow(p) */
	884, /* Iron arrow */
	885, /* Iron arrow(p) */
	886, /* Steel arrow */
	887, /* Steel arrow(p) */
	888, /* Mithril arrow */
	889, /* Mithril arrow(p) */
	890, /* Adamant arrow */
	891, /* Adamant arrow(p) */
	892, /* Rune arrow */
	893, /* Rune arrow(p) */
	942, /* Bronze fire arrows */
	2532, /* Iron fire arrows */
	2533, /* Iron fire arrows */
	2534, /* Steel fire arrows */
	2535, /* Steel fire arrows */
	2536, /* Mithril fire arrows */
	2537, /* Mithril fire arrows */
	2538, /* Addy fire arrows */
	2539,/* Addy fire arrows */
	2540, /* Rune fire arrows */
	2541, /* Rune fire arrows */
	2866, /* Ogre arrow */
	4150, /* Broad arrows */
	4160, /* Broad arrows */
	4172, /* Broad arrows */
	4173, /* Broad arrows */
	4174, /* Broad arrows */
	4175,/* Broad arrows */
	4740, /* Bolt rack */
	5616, /* Bronze arrow(+) */
	5617,/* Iron arrow(+) */
	5618, /* Steel arrow(+) */
	5619, /* Mithril arrow(+) */
	5620, /* Adamant arrow(+) */
	5621, /* Rune arrow(+) */
	5622, /* Bronze arrow(s) */
	5623, /* Iron arrow(s) */
	5624, /* Steel arrow(s) */
	5625, /* Mithril arrow(s) */
	5626, /* Adamant arrow(s) */
	5627, /* Rune arrow(s) */
	6061, /* Bronze bolts(p+) */
	6062, /* Bronze bolts(p++) */
	8882, /* Bone bolts */
	9139, /* Blurite bolts */
	9140, /* Iron bolts */
	9141, /* Steel bolts */
	9142, /* Mithril bolts */
	9143, /* Adamant bolts */
	9144, /* Runite bolts */
	9145, /* Silver bolts */
	9236, /* Opal bolts (e) */
	9237, /* Jade bolts (e) */
	9238, /* Pearl bolts (e) */
	9239, /* Topaz bolts (e) */
	9240, /* Sapphire bolts (e) */
	9241, /* Emerald bolts (e) */
	9242, /* Ruby bolts (e) */
	9243, /* Diamond bolts (e) */
	9244, /* Dragon bolts (e) */
	9245, /* Onyx bolts (e) */
	9286, /* Blurite bolts(p) */
	9287, /* Iron bolts (p) */
	9288, /* Steel bolts (p) */
	9289, /* Mithril bolts (p) */
	9290, /* Addy bolts (p) */
	9291, /* Runite bolts (p) */
	9292, /* Silver bolts (p) */
	9293, /* Blurite bolts(p+) */
	9294, /* Iron bolts(p+) */
	9295, /* Steel bolts(p+) */
	9296, /* Mithril bolts(p+) */
	9297, /* Addy bolts(p+) */
	9298, /* Runite bolts(p+) */
	9299, /* Silver bolts(p+) */
	9300, /* Blurite bolts(p++) */
	9301, /* Iron bolts(p++) */
	9302, /* Steel bolts(p++) */
	9303, /* Mithril bolts(p++) */
	9304, /* Addy bolts(p++) */
	9305, /* Runite bolts(p++) */
	9306, /* Silver bolts(p++) */
	9335, /* Jade bolts */
	9336, /* Topaz bolts */
	9337, /* Sapphire bolts */
	9338, /* Emerald bolts */
	9339, /* Ruby bolts */
	9340, /* Diamond bolts */
	9341, /* Dragon bolts */
	9342, /* Onyx bolts */
	13083, /* Black bolts */
	13084, /* Black bolts(p) */
	13085, /* Black bolts(p+) */
	13086, /* Black bolts(p++) */
	11212, /* Dragon arrow */
	11217, /* Dragon fire arrows */
	11222, /* Dragon fire arrows */
	11227, /* Dragon arrow(p) */
	11228, /* Dragon arrow(p+) */
	11229, /* Dragon arrow(p++) */
	};

	/**
	 * The helmet slot.
	 */
	public static final int SLOT_HELM = 0;

	/**
	 * The cape slot.
	 */
	public static final int SLOT_CAPE = 1;

	/**
	 * The amulet slot.
	 */
	public static final int SLOT_AMULET = 2;

	/**
	 * The weapon slot.
	 */
	public static final int SLOT_WEAPON = 3;

	/**
	 * The chest slot.
	 */
	public static final int SLOT_CHEST = 4;

	/**
	 * The shield slot.
	 */
	public static final int SLOT_SHIELD = 5;

	/**
	 * The bottoms slot.
	 */
	public static final int SLOT_BOTTOMS = 7;

	/**
	 * The gloves slot.
	 */
	public static final int SLOT_GLOVES = 9;

	/**
	 * The boots slot.
	 */
	public static final int SLOT_BOOTS = 10;

	/**
	 * The rings slot.
	 */
	public static final int SLOT_RING = 12;

	/**
	 * The arrows slot.
	 */
	public static final int SLOT_ARROWS = 13;

	/**
	 * Equipment type map.
	 */
	private static final Map<Integer, EquipmentType> equipmentTypes = new HashMap<Integer, EquipmentType>();

	/**
	 * Equipment interface id.
	 */
	public static final int INTERFACE1 = 387;

	/**
	 * Equipment interface id.
	 */
	public static final int INTERFACE2 = 465;

	/**
	 * Equipment type enum.
	 * 
	 * @author Lothy
	 * @author Miss Silabsoft
	 * 
	 */
	public enum EquipmentType {
		CAPE("Cape", Equipment.SLOT_CAPE), BOOTS("Boots", Equipment.SLOT_BOOTS), GLOVES(
				"Gloves", Equipment.SLOT_GLOVES), SHIELD("Shield",
				Equipment.SLOT_SHIELD), HAT("Hat", Equipment.SLOT_HELM), AMULET(
				"Amulet", Equipment.SLOT_AMULET), ARROWS("Arrows",
				Equipment.SLOT_ARROWS), RING("Ring", Equipment.SLOT_RING), BODY(
				"Body", Equipment.SLOT_CHEST), LEGS("Legs",
				Equipment.SLOT_BOTTOMS), PLATEBODY("Plate body",
				Equipment.SLOT_CHEST), FULL_HELM("Full helm",
				Equipment.SLOT_HELM), FULL_MASK("Full mask",
				Equipment.SLOT_HELM), WEAPON("Weapon", Equipment.SLOT_WEAPON), NONE(
				"None", -1);

		/**
		 * The description.
		 */
		private String description;

		/**
		 * The slot.
		 */
		private int slot;

		/**
		 * Creates the equipment type.
		 * 
		 * @param description
		 *            The description.
		 * @param slot
		 *            The slot.
		 */
		private EquipmentType(String description, int slot) {
			this.description = description;
			this.slot = slot;
		}

		/**
		 * Gets the description.
		 * 
		 * @return The description.
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Gets the slot.
		 * 
		 * @return The slot.
		 */
		public int getSlot() {
			return slot;
		}

	}

	/**
	 * Static initializer block to populate the type map.
	 */
	static {
		for (int cape : CAPES) {
			equipmentTypes.put(cape, EquipmentType.CAPE);
		}
		for (int boots : BOOTS) {
			equipmentTypes.put(boots, EquipmentType.BOOTS);
		}
		for (int gloves : GLOVES) {
			equipmentTypes.put(gloves, EquipmentType.GLOVES);
		}
		for (int shield : SHIELDS) {
			equipmentTypes.put(shield, EquipmentType.SHIELD);
		}
		for (int hat : HATS) {
			equipmentTypes.put(hat, EquipmentType.HAT);
		}
		for (int amulet : AMULETS) {
			equipmentTypes.put(amulet, EquipmentType.AMULET);
		}
		for (int arrowType : ARROWS) {
			equipmentTypes.put(arrowType, EquipmentType.ARROWS);
		}
		for (int ring : RINGS) {
			equipmentTypes.put(ring, EquipmentType.RING);
		}
		for (int body : BODY) {
			equipmentTypes.put(body, EquipmentType.BODY);
		}
		for (int legs : LEGS) {
			equipmentTypes.put(legs, EquipmentType.LEGS);
		}
		for (int plateBody : PLATEBODY) {
			equipmentTypes.put(plateBody, EquipmentType.PLATEBODY);
		}
		for (int fullHelm : FULL_HELM) {
			equipmentTypes.put(fullHelm, EquipmentType.FULL_HELM);
		}
		for (int fullMask : FULL_MASK) {
			equipmentTypes.put(fullMask, EquipmentType.FULL_MASK);
		}
	}

	/**
	 * Gets an equipment type.
	 * 
	 * @param item
	 *            The item.
	 * @return The equipment type.
	 */
	public static EquipmentType getType(Item item) {
		int id = item.getId();
		if (equipmentTypes.containsKey(id)) {
			return equipmentTypes.get(id);
		} else {
			return EquipmentType.WEAPON;
		}
	}

	/**
	 * Checks if an item is of a specific type.
	 * 
	 * @param type
	 *            The type.
	 * @param item
	 *            The item.
	 * @return <code>true</code> if the types are the same, <code>false</code>
	 *         if not.
	 */
	public static boolean is(EquipmentType type, Item item) {
		return getType(item).equals(type);
	}

	/**
	 * Gets the stand animation for a specific item.
	 * 
	 * @param item
	 *            The item we're getting the animation for.
	 */
	public static int getStandAnim(Item item) {
		if (item == null) {
			return 0x328;
		}
		String name = item.getDefinition().getName();
		int id = item.getDefinition().getId();
		if (id == 4718) {// Dharocks axe
			return 2065;
		} else if (id == 4755) {// Veracs flail
			return 2061;
		} else if (id == 4734) {// Karils crossbow
			return 2074;
		} else if (id == 6528) {/* Tzhaar-ket-om */
			return 0x811;
		} else if (id == 2415// Sara staff
				|| id == 2416// Guthix staff
				|| id == 2417) {// Zamorak staff.
			return 0x328;
		} else if (name.endsWith("wand")) {
			return 809;
		} else if (id == 4726 /* Guthans warspear */|| name.endsWith("spear")
				|| name.endsWith("halberd") || name.contains("Staff")
				|| name.contains("staff") || id == 1305 /* Dragon longsword */) {
			return 809;
		} else if (name.endsWith("2h sword")) {
			return 2561;
		} else if (id == 4153) {
			return 1662;
		} else if (id == 10887) {
			return 5869;
		}
		return 0x328;
	}

	/**
	 * Gets the walk animation for a specific item.
	 * 
	 * @param item
	 *            The item we're getting the animation for.
	 */
	public static int getWalkAnim(Item item) {
		if (item == null) {
			return 0x333;
		}
		String name = item.getDefinition().getName();
		int id = item.getDefinition().getId();
		if (id == 2415// Sara staff
				|| id == 2416// Guthix staff
				|| id == 2417) {// Zamorak staff.
			return 0x333;
		} else if (id == 4718) {// Dharock axe.
			return 2064;
		} else if (id == 4755) {// Veracs flail
			return 2060;
		} else if (id == 4734) {// Karils crossbow
			return 2076;
		} else if (id == 4153) {// Granite maul
			return 1663;
		} else if (id == 4151) {// Whip
			return 1660;
		} else if (name.endsWith("2h sword") || id == 6528 /* Tzhaar-ket-om */) {
			return 2562;
		} else if (id == 4726 /* Guthans warspear */|| name.contains("spear")
				|| name.endsWith("halberd") || name.contains("Staff")
				|| name.contains("staff")) {
			return 1146;
		} else if (id == 10887) {// Barrelchest anchor
			return 5867;
		}
		return 0x333;
	}

	/**
	 * Gets the run animation for a specific item.
	 * 
	 * @param item
	 *            The item we're getting the animation for.
	 */
	public static int getRunAnim(Item item) {
		if (item == null) {
			return 0x338;
		}
		String name = item.getDefinition().getName();
		int id = item.getDefinition().getId();
		if (id == 4718 /* Dharocks axe */|| name.endsWith("2h sword")
				|| id == 6528 /* Tzhaar-ket-om */) {
			return 2563;
		} else if (id == 2415// Sara staff
				|| id == 2416// Guthix staff
				|| id == 2417) {// Zamorak staff.
			return 0x338;
		} else if (id == 4755) {// Veracs flail
			return 1831;
		} else if (id == 4734) {// Karils crossbow
			return 2077;
		} else if (id == 4726 /* Guthans warspear */|| name.contains("Spear")
				|| name.endsWith("halberd") || name.contains("Staff")
				|| name.contains("staff")) {
			return 1210;
		} else if (id == 4151) {// Whip
			return 1661;
		} else if (id == 4153) {// Granite maul
			return 1664;
		} else if (id == 10887) {// Barrelchest anchor
			return 5868;
		}
		return 0x338;
	}

	public static boolean isTwoHanded(ItemDefinition def) {
		String weaponName = def.getName();
		int itemId = def.getId();
		if (itemId == 4212)
			return true;
		else if (itemId == 4214)
			return true;
		else if (weaponName.contains("Ahrim's"))
			return true;
		else if (weaponName.contains("Dharok's"))
			return true;
		else if (weaponName.contains("Guthan's"))
			return true;
		else if (weaponName.contains("Karil's"))
			return true;
		else if (weaponName.contains("Torag's"))
			return true;
		else if (weaponName.contains("Verac's"))
			return true;
		else if (weaponName.endsWith("2h sword"))
			return true;
		else if (weaponName.endsWith("longbow"))
			return true;
		else if (weaponName.equals("Seercull"))
			return true;
		else if (weaponName.endsWith("shortbow"))
			return true;
		else if (weaponName.endsWith("Longbow"))
			return true;
		else if (weaponName.endsWith("Shortbow"))
			return true;
		else if (weaponName.endsWith("bow full"))
			return true;
		else if (weaponName.endsWith("halberd"))
			return true;
		else if (weaponName.equals("Granite maul"))
			return true;
		else if (weaponName.equals("Karils crossbow"))
			return true;
		else if (weaponName.equals("Torags hammers"))
			return true;
		else if (weaponName.equals("Veracs flail"))
			return true;
		else if (weaponName.equals("Dharoks greataxe"))
			return true;
		else if (weaponName.equals("Guthans warspear"))
			return true;
		else if (weaponName.equals("Tzhaar-ket-om"))
			return true;
		else if (weaponName.equals("Barrelchest"))
			return true;
		else if (weaponName.equals("Dark bow"))
			return true;
		else if (weaponName.equals("Barrelchest anchor"))
			return true;
		else
			return false;
	}

	public static boolean containsWeapon(Player player, int id) {
		return player.getEquipment().get(SLOT_WEAPON) == null ? false : player
				.getEquipment().get(SLOT_WEAPON).getId() == id;
	}

	public static int getAttackAnimation(Player player) {
		Item weapon = player.getEquipment().get(SLOT_WEAPON);
		if (weapon == null) {

		}
		if (weapon != null) {

			switch (weapon.getId()) {
			case 10887: // Barrelchest anchor.
				return 5865;
			case 4718: // Dharocks axe.
				if (player.getAttackStyle() != Combat.AGGRESSIVE) {
					return 2067;
				} else {
					return 2066;
				}
			case 4153: // G maul.
				return 1665;
			case 4755:// Veracs flail.
				return 2062;
			case 4151: // Whip
				return 1658;
			case 5698: // Dds
				return 402;
			case 4734: // Karils cbow.
				return 2075;
			default:
			}

			String name = weapon.getDefinition().getName().toLowerCase();

			if (name.contains("scimitar")) {
				switch (player.getAttackButton()) {
				case 1:
				case 2:
				case 4:
					return 451;
				case 3:
					return 412;
				}
			} else if (name.contains("warhammer")) {
				switch (player.getAttackButton()) {
				case 1:
				case 2:
				case 4:
					return 401;
				case 3:
					return 400;
				}
			} else if (name.contains("dagger")) {
				switch (player.getAttackButton()) {
				case 1:
				case 2:
				case 4:
					return 386;
				case 3:
					return 390;
				}
			} else if (name.contains("spear")) {
				switch (player.getAttackButton()) {
				case 1:
				case 4:
					return 428;
				case 3:
					return 429;
				case 2:
					return 440;
				}
			} else if (name.contains("sword")) {
				switch (player.getAttackButton()) {
				case 1:
				case 2:
				case 4:
					return 451;
				case 3:
					return 412;
				}
				/*
				 * } else if(name.contains("longsword")){
				 * switch(player.getAttackButton()) { case 1: case 2: case 4:
				 * return 451; case 3: return 412; }
				 */
			} else if (name.contains("battle axe")) {
				switch (player.getAttackButton()) {
				case 1:
				case 2:
				case 4:
					return 395;
				case 3:
					return 401;
				}
			} else if (name.contains("axe")) {
				switch (player.getAttackButton()) {
				case 1:
				case 2:
				case 4:
					return 395;
				case 3:
					return 401;
				}
			} else if (name.contains("pickaxe")) {
				switch (player.getAttackButton()) {
				case 1:
				case 2:
				case 4:
					return 401;
				case 3:
					return 400;
				}
			} else if (name.contains("mace")) {
				switch (player.getAttackButton()) {
				case 1:
				case 2:
				case 4:
					return 401;
				case 3:
					return 400;
				}
			} else if (name.contains("2h")) {
				switch (player.getAttackButton()) {
				case 1:
				case 2:
					return 7041;
				case 3:
					return 7048;
				case 4:
					return 7049;
				}
			} else if (name.contains("halbred")) {
				switch (player.getAttackButton()) {
				case 1:
				case 3:
				case 4:
					return 428;
				case 2:
					return 440;
				}
			} else if (name.contains("claws")) {
				switch (player.getAttackButton()) {
				case 1:
				case 2:
				case 4:
					return 393;
				case 3:
					return 1067;
				}
			} else if (name.contains("butterfly net")) {
				switch (player.getAttackButton()) {
				case 1:
				case 4:
					return 428;
				case 3:
					return 429;
				case 2:
					return 440;
				}
			} else if (name.endsWith("crossbow") || name.endsWith("c'bow")) {
				return 4230;
			} else if (name.contains("knife")) {
				return 806;
			} else if (name.contains("dart")) {
				return 6600;
			} else if (name.contains("bow")) {
				return 426;
			}
		}
		if (player.getAttackStyle() != Combat.AGGRESSIVE) {
			return 422;
		} else {
			return 423;
		}

	}

	public static int getDefenceAnimation(Player player) {
		int weapon = player.getEquipment().get(Equipment.SLOT_WEAPON) != null ? player
				.getEquipment().get(Equipment.SLOT_WEAPON).getId()
				: -1;
		int shield = player.getEquipment().get(Equipment.SLOT_SHIELD) != null ? player
				.getEquipment().get(Equipment.SLOT_SHIELD).getId()
				: -1;
		if (weapon == -1 && shield == -1) {
			return 404;
		}
		if (shield != -1) {
			// Shield emotes
			switch (shield) {
			case 8844:
			case 8845:
			case 8846:
			case 8847:
			case 8848:
			case 8849:
			case 8850:
				return 4177; // Defenders..
			default:
				return 403;
			}
			// Player doesn't have shield so you gotta get def emote for weapon
		} else {
			if (isTwoHanded(ItemDefinition.forId(weapon))) {
				return 410;
			}
			switch (weapon) {
			case 4151: // Whip.
				return 1659;
			case 4153: // Granite maul
				return 1666;
			case 10887:
				return 5866;
			default:
				return 404;
			}
		}

	}

	public static int getDarkbowDrawbackGraphics(Player player) {
		String name = null;
		if (player.getEquipment().get(SLOT_WEAPON) != null) {
			if (player.getEquipment().get(SLOT_ARROWS) != null) {
				if (player.getEquipment().get(SLOT_ARROWS).getCount() > 1) {
					if (isWieldingThrowables(player)) {
						name = player.getEquipment().get(SLOT_WEAPON)
								.getDefinition().getName();
					} else {
						name = player.getEquipment().get(SLOT_ARROWS) == null ? ""
								: player.getEquipment().get(SLOT_ARROWS)
										.getDefinition().getName();
					}
					if (name.equals("Ice arrows")) {
						return 1110;
					}
					if (name.contains("fire")) {
						return 1113;
					}
					for (int i = 0; i < DARKBOW_PROJECTYLE_DATA.length/* / 2 */; i++) {
						String data = (String) DARKBOW_PROJECTYLE_DATA[i][0][0];
						if (name.equals(data)) {
							return (Integer) DARKBOW_PROJECTYLE_DATA[i][1][0];
						}

					}
				} else {
					return getDrawbackGraphics(player);
				}

			}
		}
		return -1;
	}

	public static int getDrawbackGraphics(Player player) {
		String name = null;
		if (player.getEquipment().get(SLOT_WEAPON) != null) {
			if (isWieldingThrowables(player)) {
				name = player.getEquipment().get(SLOT_WEAPON).getDefinition()
						.getName();
			} else {
				name = player.getEquipment().get(SLOT_ARROWS) == null ? ""
						: player.getEquipment().get(SLOT_ARROWS)
								.getDefinition().getName();
			}
			if (name.equals("Ice arrows")) {
				return 25;
			}
			if (name.contains("bolts")) {
				return 28;
			}
			if (name.contains("fire")) {
				return 26;
			}
			if (name.contains("Crystal") || name.contains("crystal")) {
				return 250;
			}
			for (int i = 0; i < RANGED_PROJECTYLE_DATA.length; i++) {
				String data = (String) RANGED_PROJECTYLE_DATA[i][0][0];
				if (name.equals(data)) {
					return (Integer) RANGED_PROJECTYLE_DATA[i][1][0];
				}

			}
		}
		return -1;
	}

	private static final int[] THROWABLES = new int[] { 863/* Iron */,
			864/* Bronze knife */, 865/* Steel knife */,
			866/* Mithril knife */, 867/* Adamant knife */,
			868/* Rune knife */, 869/* Black knife */, 870/* Bronze knife(p) */,
			871/* Iron knife(p) */, 872/* Steel knife(p) */, 873/*
																 * Mithril
																 * knife(p)
																 */, 874/*
																		 * Black
																		 * knife
																		 * (p)
																		 */,
			875/* Adamant knife(p) */, 876/* Rune knife(p) */, 6522/*
																	 * Toktz-xil-
																	 * ul
																	 */,
			806/* Bronze dart */, 807/* Iron dart */, 808/* Steel dart */,
			3093/* Black dart */, 3094/* Black dart(p) */,
			5631/* Black dart(+) */, 5638/* Black dart(s) */,
			809/* Mithril dart */, 810/* Adamant dart */,
			811/* Rune dart */, 11230/* Dragon dart */,
	// TODO: More poison stuff.
	};

	private static final int[] KNIVES_STRENGTH = { 4/* Iron */,
			3/* Bronze knife */, 7/* Steel knife */, 10/* Mithril knife */,
			14/* Adamant knife */, 24/* Rune knife */, 8/* Black knife */,
			3/* Bronze knife(p) */, 4/* Iron knife(p) */, 7/* Steel knife(p) */,
			10/* Mithril knife(p) */, 8/* Black knife(p) */,
			14/* Adamant knife(p) */, 24/* Rune knife(p) */, 49/* Toktz-xil-ul */,
			1/* Bronze dart */, 3/* Iron dart */, 4/* Steel dart */, 6/*
																	 * Black
																	 * dart
																	 */, 6/*
																		 * Black
																		 * dart
																		 * (p)
																		 */,
			6/* Black dart(+) */, 6/* Black dart(s) */, 7/* Mithril dart */, 10/*
																		 * Adamant
																		 * dart
																		 */,
			14/* Rune dart */, 20/* Dragon dart */,
	// TODO: More poison stuff.
	};

	/**
	 * Contains the all the arrow strengths. All the ones with 0's are
	 * unfinished.
	 */
	private static int[] ARROW_STRENGTH = { 0, /* Ice arrows */
	7, /* Bronze fire arrows */
	10, /* Bronze bolts */
	10, /* Bronze bolts(p) */
	0, /* Opal bolts */
	0, /* Pearl bolts */
	12, /* Barbed bolts */
	7, /* Bronze arrow */
	7, /* Bronze arrow(p) */
	10, /* Iron arrow */
	10, /* Iron arrow(p) */
	16, /* Steel arrow */
	16, /* Steel arrow(p) */
	22, /* Mithril arrow */
	22, /* Mithril arrow(p) */
	31, /* Adamant arrow */
	31, /* Adamant arrow(p) */
	49, /* Rune arrow */
	49, /* Rune arrow(p) */
	7, /* Bronze fire arrows */
	10, /* Iron fire arrows */
	10, /* Iron fire arrows */
	16, /* Steel fire arrows */
	16, /* Steel fire arrows */
	22, /* Mithril fire arrows */
	22, /* Mithril fire arrows */
	31, /* Addy fire arrows */
	31,/* Addy fire arrows */
	49, /* Rune fire arrows */
	49, /* Rune fire arrows */
	22, /* Ogre arrow */
	0, /* Broad arrows */
	0, /* Broad arrows */
	0, /* Broad arrows */
	0, /* Broad arrows */
	0, /* Broad arrows */
	0,/* Broad arrows */
	0, /* Bolt rack */
	7, /* Bronze arrow(+) */
	10,/* Iron arrow(+) */
	16, /* Steel arrow(+) */
	22, /* Mithril arrow(+) */
	31, /* Adamant arrow(+) */
	49, /* Rune arrow(+) */
	7, /* Bronze arrow(s) */
	10, /* Iron arrow(s) */
	16, /* Steel arrow(s) */
	22, /* Mithril arrow(s) */
	31, /* Adamant arrow(s) */
	49, /* Rune arrow(s) */
	10, /* Bronze bolts(p+) */
	10, /* Bronze bolts(p++) */
	49, /* Bone bolts */
	0, /* Blurite bolts */
	46, /* Iron bolts */
	64, /* Steel bolts */
	82, /* Mithril bolts */
	100, /* Adamant bolts */
	115, /* Runite bolts */
	36, /* Silver bolts */
	0, /* Opal bolts (e) */
	0, /* Jade bolts (e) */
	0, /* Pearl bolts (e) */
	66, /* Topaz bolts (e) */
	83, /* Sapphire bolts (e) */
	85, /* Emerald bolts (e) */
	103, /* Ruby bolts (e) */
	105, /* Diamond bolts (e) */
	117, /* Dragon bolts (e) */
	22, /* Onyx bolts (e) */
	0, /* Blurite bolts(p) */
	46, /* Iron bolts (p) */
	64, /* Steel bolts (p) */
	82, /* Mithril bolts (p) */
	100, /* Addy bolts (p) */
	115, /* Runite bolts (p) */
	36, /* Silver bolts (p) */
	0, /* Blurite bolts(p+) */
	46, /* Iron bolts(p+) */
	64, /* Steel bolts(p+) */
	82, /* Mithril bolts(p+) */
	100, /* Addy bolts(p+) */
	115, /* Runite bolts(p+) */
	36, /* Silver bolts(p+) */
	0, /* Blurite bolts(p++) */
	46, /* Iron bolts(p++) */
	64, /* Steel bolts(p++) */
	82, /* Mithril bolts(p++) */
	100, /* Addy bolts(p++) */
	115, /* Runite bolts(p++) */
	36, /* Silver bolts(p++) */
	0, /* Jade bolts */
	66, /* Topaz bolts */
	83, /* Sapphire bolts */
	85, /* Emerald bolts */
	103, /* Ruby bolts */
	105, /* Diamond bolts */
	117, /* Dragon bolts */
	22, /* Onyx bolts */
	75, /* Black bolts */
	75, /* Black bolts(p) */
	75, /* Black bolts(p+) */
	75, /* Black bolts(p++) */
	60, /* Dragon arrow */
	60, /* Dragon fire arrows */
	60, /* Dragon fire arrows */
	60, /* Dragon arrow(p) */
	60, /* Dragon arrow(p+) */
	60, /* Dragon arrow(p++) */
	};

	public static int getRangedStrength(Player player) {
		Item arrows = player.getEquipment().get(SLOT_ARROWS);
		Item weapon = player.getEquipment().get(SLOT_WEAPON);
		if (isWieldingThrowables(player)) {
			if (weapon != null) {
				for (int index = 0; index < THROWABLES.length; index++) {
					if (THROWABLES[index] == weapon.getId()) {
						return KNIVES_STRENGTH[index];
					}
				}
			}
		} else {
			if (arrows != null) {
				for (int index = 0; index < ARROWS.length; index++) {
					if (ARROWS[index] == arrows.getId()) {
						return ARROW_STRENGTH[index];
					}
				}
			}
		}
		return 0;
	}

	public static final Object[][][] DARKBOW_PROJECTYLE_DATA = {
			// gfx drawback,
			{ { "Bronze arrow" }, { 1104 } }, { { "Iron arrow" }, { 1105 } },
			{ { "Steel arrow" }, { 1106 } }, { { "Mithril arrow" }, { 1107 } },
			{ { "Adamant arrow" }, { 1108 } }, { { "Rune arrow" }, { 1109 } },
			{ { "Dragon arrow" }, { 1111 } }, };

	public static int getProjectileId(Player player) {
		String name = null;
		if (player.getEquipment().get(SLOT_WEAPON) != null) {
			if (isWieldingThrowables(player)) {
				name = player.getEquipment().get(SLOT_WEAPON).getDefinition()
						.getName();
			} else {
				name = player.getEquipment().get(SLOT_ARROWS) == null ? ""
						: player.getEquipment().get(SLOT_ARROWS)
								.getDefinition().getName();
			}
			if (name.equals("Ice arrows")) {
				return 16;
			}
			if (name.contains("fire")) {
				return 17;
			}
			if (name.contains("bolts")) {
				return 27;
			}
			if (name.contains("Crystal") || name.contains("crystal")) {
				return 249;
			}
			for (int i = 0; i < RANGED_PROJECTYLE_DATA.length; i++) {
				String data = (String) RANGED_PROJECTYLE_DATA[i][0][0];
				if (name.equals(data)) {
					return (Integer) RANGED_PROJECTYLE_DATA[i][1][1];
				}

			}
			return -1;
		}
		return -1;
	}

	public static final Object[][][] RANGED_PROJECTYLE_DATA = {
			// gfx drawback, gfx projectile,
			/* Arrows */
			{ { "Bronze arrow" }, { 19, 10 } },
			{ { "Iron arrow" }, { 18, 9 } },
			{ { "Steel arrow" }, { 20, 11 } },
			{ { "Mithril arrow" }, { 21, 12 } },
			{ { "Adamant arrow" }, { 22, 13 } },
			{ { "Rune arrow" }, { 24, 15 } },
			{ { "Dragon arrow" }, { 1116, 1120 } },
			{ { "Bolt rack" }, { 28, 27 } },
			/* knives */
			{ { "Bronze knife" }, { 219, 212 } },
			{ { "Iron knife" }, { 220, 213 } },
			{ { "Steel knife" }, { 221, 214 } },
			{ { "Black knife" }, { 222, 215 } },
			{ { "Mithril knife" }, { 223, 216 } },
			{ { "Adamant knife" }, { 224, 217 } },
			{ { "Rune knife" }, { 225, 218 } },
			/* Darts */
			{ { "Bronze dart" }, { 232, 226 } },
			{ { "Iron dart" }, { 233, 227 } },
			{ { "Steel dart" }, { 234, 228 } },
			{ { "Black dart" }, { 235, 229 } },
			{ { "Mithril dart" }, { 236, 230 } },
			{ { "Adamant dart" }, { 237, 231 } },
			{ { "Rune dart" }, { 238, 232 } },
			{ { "Dragon dart" }, { 1123, 1122 } }, // TODO: Find the real
													// projectile id :s
			/* Javelin */
			{ { "Bronze javelin" }, { 206, 200 } },
			{ { "Steel javelin" }, { 207, 201 } },
			{ { "Black javelin" }, { 208, 202 } },
			{ { "Mithril javelin" }, { 209, 203 } },
			{ { "Adamant javelin" }, { 210, 204 } },
			{ { "Rune javelin" }, { 211, 205 } },
			/* tzhaar */
			{ { "Toktz-xil-ul" }, { 442, 443 } },
			// {{"Tzhaar-ket"}, {443, 70}},
			/* thrownaxe */
			{ { "Bronze thrownaxe" }, { 47, 35 } },
			{ { "Iron thrownaxe" }, { 42, 36 } },
			{ { "Steel thrownaxe" }, { 44, 37 } },
			{ { "Mithril thrownaxe" }, { 45, 38 } },
			{ { "Adamnt thrownaxe" }, { 46, 39 } },
			{ { "Rune thrownaxe" }, { 48, 41 } },
			/* Misc */
			{ { "Crystal bow" }, { 250, 249 } },
			{ { "Crossbow" }, { 28, 27 } }, };

	public static boolean isWieldindBow(Player player) {
		int weapon = player.getEquipment().get(SLOT_WEAPON) != null ? player
				.getEquipment().get(SLOT_WEAPON).getId() : -1;
		if (weapon == -1) {
			return false;
		}
		for (int bow : BOWS) {
			if (weapon == bow) {
				return true;
			}
		}
		return false;
	}

	private static final int[] BOWS = { 837, 767, 839, 841, 843, 845, 847, 849,
			851, 853, 855, 857, 859, 861, 2883, 4827, 6724, 11235, 9705, 767,
			837, 4734/* Karils crossbow */, 10156, 11165, 13081, 9174, 9176,
			9177, 9179, 9181, 9183, 9185, 8880, 4212, 4214, 4215, 4216, 4217,
			4218, 4219, 4220, 4221, 4222, 4223 /* Crystal bows. */};

	public static boolean isWieldingThrowables(Player player) {
		int id = player.getEquipment().get(SLOT_WEAPON) != null ? player
				.getEquipment().get(SLOT_WEAPON).getId() : -1;
		if (id == -1) {
			return false;
		}
		for (int knife : THROWABLES) {
			if (knife == id) {
				return true;
			}
		}
		return false;
	}

	private static final Random r = new Random();

	public static Item removeAmmo(Entity entity) {
		if (entity instanceof Player) {
			Player p = (Player) entity;
			boolean weaponSlot = isWieldingThrowables(p);
			Item ammo = p.getEquipment().get(
					weaponSlot ? SLOT_WEAPON : SLOT_ARROWS);
			if (ammo != null) {
				int amt = ammo.getCount() - 1;
				if (amt <= 0) {
					p.getEquipment().set(
							weaponSlot ? SLOT_WEAPON : SLOT_ARROWS, null);
				} else {
					p.getEquipment().set(
							weaponSlot ? SLOT_WEAPON : SLOT_ARROWS,
							new Item(ammo.getId(), amt));
				}
				if (r.nextBoolean()) {
					return null;
				} else {
					return new Item(ammo.getId());
				}
			}
			if (p.getEquipment().get(SLOT_WEAPON) != null
					&& p.getEquipment().get(SLOT_WEAPON).getDefinition()
							.getName().contains("rystal")) {

			} else {
				return null;
			}
		}
		return null;
	}

	public static final int[][] ARROWS_SORTED_BY_LEVEL = {
			{ 882, 883, 5616, 5622 }, // Bronze arrows..
			{ 884, 885, 5617, 5623 }, // Iron arrows..
			{ 886, 887, 5618, 5624 }, // Steel arrows..
			{ 888, 889, 5619, 5625, /* 7552 & 7553 ?? */}, // Mithril arrows..
			{ 890, 891, 5620, 5626 }, // Adamant arrows..
			{ 892, 893, 5621, 5627 }, // Rune arrows..
			{ 11212, 11227, 11228, 11229 }, // Dragon arrows..
	};

	public static final int[][] BOLTS_SORTED_BY_LEVEL = {
			{ 877, 878, 6061, 6062, 879, 881/* Barbed bolts?? */, 9236 }, // Bronze
			{ 9139, 9286, 9293, 9300, 9235, 9237 }, // Blurite
			{ 9140, 9287, 9294, 9301, 9238, 880, /*
												 * Folliowing bolts are made of
												 * Silver:
												 */9145, 9292, 9299, 9309 }, // Iron
			{ 9141, 9288, 9295, 9302, 9336, 9239 }, // Steel
			{ 9142, 9289, 9296, 9303, 9237, 9240, 9241, 9338 }, // Mithril
			{ 9143, 9290, 9297, 9304, 9242, 9339, 9243, 9340 }, // Adamant
			{ 9144, 9291, 9298, 9305, 9244, 9241, 9245, 9242 }, // Rune
	};

	public static boolean wearingDharock(Player player) {
		Container equipment = player.getEquipment();
		if (equipment.get(SLOT_HELM) == null
				|| equipment.get(SLOT_CHEST) == null
				|| equipment.get(SLOT_BOTTOMS) == null
				|| equipment.get(SLOT_WEAPON) == null) {
			return false;
		}
		return (equipment.get(SLOT_HELM).getDefinition().getName()
				.startsWith("Dharoks")
				&& equipment.get(SLOT_CHEST).getDefinition().getName()
						.startsWith("Dharoks")
				&& equipment.get(SLOT_BOTTOMS).getDefinition().getName()
						.startsWith("Dharoks") && equipment.get(SLOT_WEAPON)
				.getDefinition().getName().startsWith("Dharoks"));
	}

	/*
	 * Rofl, the hats doesn't even exist in 459.
	 */
	public static boolean hasVoid(Player player, AttackType type) {
		Container equipment = player.getEquipment();
		if (equipment.get(SLOT_HELM) == null
				|| equipment.get(SLOT_CHEST) == null
				|| equipment.get(SLOT_BOTTOMS) == null) {
			return false;
		}
		int robes[] = { 8839, 8840 }; // Void Robe id's
		int hats[] = { 3, 4, 5 }; // Void Hat id's
		if (equipment.get(SLOT_CHEST).getId() == robes[0]
				&& equipment.get(SLOT_BOTTOMS).getId() == robes[1]) { // If
																		// player
																		// has
																		// Void
																		// robes
			int hat = equipment.get(SLOT_HELM).getId();
			switch (type) {
			case RANGED:
				if (hat == hats[0]) {
					return true;
				}
				break;
			case MAGIC:
				if (hat == hats[1]) {
					return true;
				}
				break;
			case MELEE:
				if (hat == hats[2]) {
					return true;
				}
				break;
			}
		}
		return false;
	}

}
