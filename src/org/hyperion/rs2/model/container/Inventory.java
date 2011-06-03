package org.hyperion.rs2.model.container;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.GroundItemController;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container.Type;

/**
 * A utility class for the player's inventory.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Inventory {

	/**
	 * The size of the inventory container.
	 */
	public static final int SIZE = 28;

	/**
	 * Inventory interface id.
	 */
	public static final int INTERFACE = 149;

	private static final int[] SUPPORTED_STAFFS = { 1381, // Staff of air
			1383, // Staff of water
			1385, // Staff of earth
			1387, // Staff of fire
	};

	/**
	 * Attempts to add an item into the next free slot, and sends a message if
	 * not.
	 * 
	 * @param player
	 *            The player.
	 * @param item
	 *            The item.
	 * @return <code>true</code> if the item was added, <code>false</code> if
	 *         not.
	 */
	public static boolean addInventoryItem(Player player, Item item) {
		if (!player.getInventory().add(item)) {
			player.getActionSender().sendMessage(
					"You don't have enough space in your inventory.");
			return false;
		}
		return true;
	}

	/**
	 * Attempts to add an array of item into the inventory, and puts them on the
	 * floor if not.
	 * 
	 * @param player
	 *            The player.
	 * @param items
	 *            The items.
	 * @return <code>true</code> if the item was added, <code>false</code> if
	 *         not.
	 */
	public static void addInventoryItems(Player player, Item[] items) {
		for (Item item : items) {
			if (item != null) {
				if (!player.getInventory().add(item)) {
					GroundItemController.createGroundItem(item, player,
							player.getLocation());
				}
			}
		}
	}

	public static int removeRune(Player player, int slot, Item item) {
		if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
			Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
			for (int staff : SUPPORTED_STAFFS) {
				if (weapon.getId() == staff) {
					switch (item.getId()) {
					case 556: // Air runes
						if (weapon.getId() == 1381) {
							return 0;
						}
						break;
					case 555: // Water runes..
						if (weapon.getId() == 1383) {
							return 0;
						}
						break;
					case 557: // Earth runes..
						if (weapon.getId() == 1385) {
							return 0;
						}
						break;
					case 554: // Fire runes..
						if (weapon.getId() == 1387) {
							return 0;
						}
						break;
					}
				}
			}
		}
		return player.getInventory().remove(slot, item);
	}

	public static boolean containsRune(Player player, Item item) {
		if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
			Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
			for (int staff : SUPPORTED_STAFFS) {
				if (weapon.getId() == staff) {
					switch (item.getId()) {
					case 556: // Air runes
						if (weapon.getId() == 1381) {
							return true;
						}
						break;
					case 555: // Water runes..
						if (weapon.getId() == 1383) {
							return true;
						}
						break;
					case 557: // Earth runes..
						if (weapon.getId() == 1385) {
							return true;
						}
						break;
					case 554: // Fire runes..
						if (weapon.getId() == 1387) {
							return true;
						}
						break;
					}
				}
			}
		}
		return player.getInventory().contains(item);
	}

}
