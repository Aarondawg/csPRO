package org.hyperion.rs2.util;

import org.hyperion.rs2.model.ItemDefinition;

public class ItemConfiguration {

	/**
	 * Returns the ranged level needed for ItemID.
	 * 
	 * @param itemId
	 *            The id of the item to check.
	 * @return The ranged level requirement to weild the item.
	 */
	public static int getCLRanged(int itemId) {
		String itemName = ItemDefinition.forId(itemId).getName().toLowerCase();
		if (itemName.contains("arrow") || itemName.contains("dart")
				|| itemName.contains("javelin")
				|| itemName.contains("thrownaxe") || itemName.contains("knife")) {
			if (itemName.contains("rune")) {
				return 40;
			} else if (itemName.contains("adamant")) {
				return 30;
			} else if (itemName.contains("mithril")) {
				return 20;
			} else if (itemName.contains("steel")) {
				return 5;
			} else {
				return 1;
			}
		}
		if (itemId == 2499)
			return 50;
		if (itemId == 1135)
			return 40;
		if (itemId == 1099)
			return 40;
		if (itemId == 1065)
			return 40;
		if (itemId == 2501)
			return 60;
		if (itemId == 2503)
			return 70;
		if (itemId == 2487)
			return 50;
		if (itemId == 2489)
			return 60;
		if (itemId == 2495)
			return 60;
		if (itemId == 2491)
			return 70;
		if (itemId == 2493)
			return 50;
		if (itemId == 2505)
			return 60;
		if (itemId == 2507)
			return 70;
		if (itemId == 851)
			return 30;
		if (itemId == 853)
			return 30;
		if (itemId == 847)
			return 20;
		if (itemId == 849)
			return 20;
		if (itemId == 843)
			return 5;
		if (itemId == 845)
			return 5;
		if (itemId == 859)
			return 50;
		if (itemId == 861)
			return 50;
		if (itemId == 7370)
			return 40;
		if (itemId == 7372)
			return 40;
		if (itemId == 7378)
			return 40;
		if (itemId == 7380)
			return 40;
		if (itemId == 7374)
			return 50;
		if (itemId == 7376)
			return 50;
		if (itemId == 7382)
			return 50;
		if (itemId == 7384)
			return 50;
		itemName = ItemDefinition.forId(itemId).getName();
		if (itemName.equals("Coif"))
			return 20;
		if (itemName.startsWith("Studded chaps"))
			return 20;
		if (itemName.startsWith("Studded"))
			return 20;
		if (itemName.equals("Karils coif"))
			return 70;
		if (itemName.equals("Karils leathertop"))
			return 70;
		if (itemName.equals("Karils leatherskirt"))
			return 70;
		if (itemName.equals("Robin hood hat"))
			return 40;
		if (itemName.equals("Ranger boots"))
			return 40;
		if (itemName.equals("Crystal bow full"))
			return 70;
		if (itemName.equals("New crystal bow"))
			return 70;
		if (itemName.equals("Karils crossbow"))
			return 70;
		if (itemId == 2497) // Black chaps..
			return 70;
		if (itemName.equals("Toktz-xil-ul"))
			return 60;
		if (itemName.equals("Seercull"))
			return 50;
		if (itemName.equals("Bolt rack"))
			return 70;
		else
			return 1;
	}

	/**
	 * Returns the magic level needed for ItemID.
	 * 
	 * @param itemId
	 *            The id of the item to check.
	 * @return Returns the magic level requirement to weild the item.
	 */
	public static int getCLMagic(int itemId) {
		String itemName = ItemDefinition.forId(itemId).getName();
		if (itemName.equals("Mystic hat"))
			return 40;
		if (itemName.equals("Mystic robe top"))
			return 40;
		if (itemName.equals("Mystic robe bottom"))
			return 40;
		if (itemName.equals("Mystic gloves"))
			return 40;
		if (itemName.equals("Mystic boots"))
			return 40;
		if (itemName.equals("Slayer's staff"))
			return 50;
		if (itemName.equals("Enchanted hat"))
			return 40;
		if (itemName.equals("Enchanted top"))
			return 40;
		if (itemName.equals("Enchanted robe"))
			return 40;
		if (itemName.equals("Splitbark helm"))
			return 40;
		if (itemName.equals("Splitbark body"))
			return 40;
		if (itemName.equals("Splitbark gauntlets"))
			return 40;
		if (itemName.equals("Splitbark legs"))
			return 40;
		if (itemName.equals("Splitbark greaves"))
			return 40;
		if (itemName.equals("Infinity gloves"))
			return 50;
		if (itemName.equals("Infinity hat"))
			return 50;
		if (itemName.equals("Infinity top"))
			return 50;
		if (itemName.equals("Infinity bottoms"))
			return 50;
		if (itemName.equals("Infinity boots"))
			return 50;
		if (itemName.equals("Ahrims hood"))
			return 70;
		if (itemName.equals("Ahrims robetop"))
			return 70;
		if (itemName.equals("Ahrims robeskirt"))
			return 70;
		if (itemName.equals("Ahrims staff"))
			return 70;
		if (itemName.equals("Saradomin cape"))
			return 60;
		if (itemName.equals("Saradomin staff"))
			return 60;
		if (itemName.equals("Zamorak cape"))
			return 60;
		if (itemName.equals("Zamorak staff"))
			return 60;
		if (itemName.equals("Guthix cape"))
			return 60;
		if (itemName.equals("Guthix staff"))
			return 60;
		if (itemName.equals("mud staff"))
			return 30;
		if (itemName.equals("Fire battlestaff"))
			return 30;
		return 1;
	}

	/**
	 * Returns the strength level needed to weild ItemID.
	 * 
	 * @param itemId
	 *            The item id to check.
	 * @return The strength level requirement for the item.
	 */
	public static int getCLStrength(int itemId) {
		String itemName = ItemDefinition.forId(itemId).getName();
		if (itemName.equals("Torags hammers"))
			return 70;
		if (itemName.equals("Dharoks greataxe"))
			return 70;
		if (itemName.equals("Granite maul"))
			return 50;
		if (itemName.equals("Granite legs"))
			return 99;
		if (itemName.equals("Tzhaar-ket-om"))
			return 60;
		if (itemName.equals("Granite shield"))
			return 50;
		return 1;
	}

	/**
	 * Returns the attack level needed for ItemID.
	 * 
	 * @param itemId
	 *            The item id to check.
	 * @return The attack level needed to weild the item.
	 */
	public static int getCLAttack(int itemId) {
		String itemName = ItemDefinition.forId(itemId).getName().toLowerCase();
		if (itemName.contains("dagger") || itemName.contains("spear")
				|| itemName.contains("pickaxe") || itemName.contains("sword")
				|| itemName.contains("scimitar")
				|| itemName.contains("warhammer") || itemName.contains("axe")
				|| itemName.contains("mace") || itemName.contains("halberd")
				|| itemName.contains("claws")) {
			if (itemName.contains("steel")) {
				return 5;
			} else if (itemName.contains("black") || itemName.contains("white")) {
				return 10;
			} else if (itemName.contains("mithril")) {
				return 20;
			} else if (itemName.contains("adamant")
					|| itemName.contains("addy")) {
				return 30;
			} else if (itemName.contains("rune")) {
				return 40;
			} else if (itemName.contains("dragon")) {
				return 60;
			} else {
				return 1;
			}
		}
		if (itemName.equals("abyssal whip")) {
			return 70;
		} else if (itemName.equals("veracs flail")) {
			return 70;
		} else if (itemName.equals("torags hammers")) {
			return 70;
		} else if (itemName.equals("dharoks greataxe")) {
			return 70;
		} else if (itemName.equals("guthans warspear")) {
			return 70;
		} else if (itemName.equals("ahrims staff")) {
			return 70;
		} else if (itemName.equals("granite maul")) {
			return 50;
		} else if (itemName.equals("toktz-xil-ak")) {
			return 60;
		} else if (itemName.equals("tzhaar-ket-em")) {
			return 60;
		} else if (itemName.equals("toktz-xil-ek")) {
			return 60;
		} else if (itemName.equals("mud staff")) {
			return 30;
		} else if (itemName.equals("lava battlestaff")) {
			return 30;
		} else if (itemName.equals("toktz-mej-tal")) {
			return 60;
		} else if (itemName.equals("ancient staff")) {
			return 50;
		}
		System.out.println("Unhandled attack level: " + itemName);
		return 0;
	}

	/**
	 * Returns the defence level needed for ItemID.
	 * 
	 * @param itemId
	 *            The item id to check.
	 * @return Returns the defence level requirement to weild the item.
	 */
	public static int getCLDefence(int itemId) {
		String itemName = ItemDefinition.forId(itemId).getName().toLowerCase();
		if (itemName.contains("plate") || itemName.contains("body")
				|| itemName.contains("helm") || itemName.contains("shield")) {
			if (itemName.contains("steel")) {
				return 5;
			} else if (itemName.contains("black")) {
				return 10;
			} else if (itemName.contains("mithril")) {
				return 20;
			} else if (itemName.contains("adamant")
					|| itemName.contains("addy")) {
				return 30;
			} else if (itemName.contains("rune")) {
				return 40;
			} else if (itemName.contains("dragon")) {
				return 60;
			}
		}
		if (itemName.startsWith("studded chaps"))
			return 1;
		if (itemName.startsWith("studded"))
			return 20;
		if (itemName.contains("zamorak") || itemName.contains("guthix")
				|| itemName.contains("zamorak") || itemName.contains("gilded"))
			return 40;
		if (itemName.equals("fighter torso"))
			return 40;
		if (itemName.equals("toktz-ket-xil"))
			return 60;
		if (itemName.contains("torag") || itemName.contains("verac")
				|| itemName.contains("ahrim") || itemName.contains("dharock")
				|| itemName.contains("karil"))
			return 70;
		itemName = ItemDefinition.forId(itemId).getName();
		if (itemName.equals("Granite shield"))
			return 50;
		if (itemName.equals("New crystal shield"))
			return 70;
		if (itemName.equals("Archer helm"))
			return 45;
		if (itemName.equals("Berserker helm"))
			return 45;
		if (itemName.equals("Warrior helm"))
			return 45;
		if (itemName.equals("Farseer helm"))
			return 45;
		if (itemName.equals("Initiate helm"))
			return 20;
		if (itemName.equals("Initiate platemail"))
			return 20;
		if (itemName.equals("Initiate platelegs"))
			return 20;
		if (itemName.equals("Dragonhide body"))
			return 40;
		if (itemName.equals("Mystic hat"))
			return 20;
		if (itemName.equals("Mystic robe top"))
			return 20;
		if (itemName.equals("Mystic robe bottom"))
			return 20;
		if (itemName.equals("Mystic gloves"))
			return 20;
		if (itemName.equals("Mystic boots"))
			return 20;
		if (itemName.equals("Enchanted hat"))
			return 20;
		if (itemName.equals("Enchanted top"))
			return 20;
		if (itemName.equals("Enchanted robe"))
			return 20;
		if (itemName.equals("Splitbark helm"))
			return 40;
		if (itemName.equals("Splitbark body"))
			return 40;
		if (itemName.equals("Splitbark gauntlets"))
			return 40;
		if (itemName.equals("Splitbark legs"))
			return 40;
		if (itemName.equals("Splitbark greaves"))
			return 40;
		if (itemName.equals("Infinity gloves"))
			return 25;
		if (itemName.equals("Infinity hat"))
			return 25;
		if (itemName.equals("Infinity top"))
			return 25;
		if (itemName.equals("Infinity bottoms"))
			return 25;
		if (itemName.equals("Infinity boots"))
			return 25;
		return 1;
	}

}
