package org.hyperion.rs2.model.container.impl;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.ContainerListener;
import org.hyperion.rs2.model.container.Equipment;

/**
 * A listener which updates the weapon tab.
 * 
 * @author Graham Edgecombe
 * 
 */
public class WeaponContainerListener implements ContainerListener {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Creates the listener.
	 * 
	 * @param player
	 *            The player.
	 */
	public WeaponContainerListener(Player player) {
		this.player = player;
	}

	@Override
	public void itemChanged(Container container, int slot) {
		if (slot == Equipment.SLOT_WEAPON) {
			sendWeapon();
		}
	}

	@Override
	public void itemsChanged(Container container, int[] slots) {
		for (int slot : slots) {
			if (slot == Equipment.SLOT_WEAPON) {
				sendWeapon();
				return;
			}
		}
	}

	@Override
	public void itemsChanged(Container container) {
		sendWeapon();
	}

	/**
	 * Sends weapon information.
	 */
	public void sendWeapon() {
		Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
		int id = -1;
		String name = null;
		if (weapon == null) {
			name = "Unarmed";
		} else {
			name = weapon.getDefinition().getName();
			id = weapon.getId();
		}
		String genericName = filterWeaponName(name).trim();
		sendWeapon(id, name, genericName);
		player.getSpecials().setSpecialWeapon(setSpecials(id));
	}

	/**
	 * Sends weapon information.
	 * 
	 * @param id
	 *            The id.
	 * @param name
	 *            The name.
	 * @param genericName
	 *            The filtered name.
	 */
	private void sendWeapon(int id, String name, String genericName) {
		if (name.equals("Unarmed")) {
			player.getActionSender().sendSidebarInterface(0, 92);
			player.getActionSender().sendString(name, 92, 0);
		} else if (name.endsWith("whip")) {
			player.getActionSender().sendSidebarInterface(0, 93);
			player.getActionSender().sendString(name, 93, 0);
		} else if (name.endsWith("c'bow")) {
			player.getActionSender().sendSidebarInterface(0, 79);
			player.getActionSender().sendString(name, 79, 0);
		} else if (name.endsWith("Scythe")) {
			/*
			 * player.getActionSender().sendSidebarInterface(0, 776);
			 * player.getActionSender().sendInterfaceModel(777, 200, id);
			 * player.getActionSender().sendString(779, name);
			 */
		} else if (name.endsWith("bow") || name.startsWith("Crystal bow")
				|| name.startsWith("Toktz-xil-ul") || name.equals("Seercull")) {
			player.getActionSender().sendSidebarInterface(0, 77);
			player.getActionSender().sendString(name, 77, 0);
		} else if (name.startsWith("Staff") || name.endsWith("staff")
				|| name.equals("Toktz-mej-tal")) {
			player.getActionSender().sendSidebarInterface(0, 90);
			player.getActionSender().sendString(name, 90, 0);
		} else if (name.endsWith("dart") || name.endsWith("knife")
				|| name.endsWith("javelin") || name.endsWith("thrownaxe")
				|| name.equals("Toktz-xil-ul")) {
			player.getActionSender().sendSidebarInterface(0, 91);
			player.getActionSender().sendString(name, 91, 0);
		} else if (name.endsWith("spear") || name.equals("Guthans warspear")) {
			player.getActionSender().sendSidebarInterface(0, 87);
			player.getActionSender().sendString(name, 87, 0);
		} else if (name.endsWith("claws")) {
			player.getActionSender().sendSidebarInterface(0, 78);
			player.getActionSender().sendString(name, 78, 0);
		} else if (name.endsWith("2h sword")) {
			player.getActionSender().sendSidebarInterface(0, 81);
			player.getActionSender().sendString(name, 81, 0);
		} else if (name.equals("Granite maul") || name.equals("Tzhaar-ket-om")
				|| name.equals("Torags hammers")) {
			player.getActionSender().sendSidebarInterface(0, 76);
			player.getActionSender().sendString(name, 76, 0);
		} else if (name.equals("Veracs flail") || name.endsWith("mace")
				|| name.equals("Barrelchest anchor")) {
			player.getActionSender().sendSidebarInterface(0, 88);
			player.getActionSender().sendString(name, 88, 0);
		} else if (genericName.startsWith("dagger")) {
			player.getActionSender().sendSidebarInterface(0, 89);
			player.getActionSender().sendString(name, 89, 0);
		} else if (genericName.startsWith("pickaxe")) {
			player.getActionSender().sendSidebarInterface(0, 83);
			player.getActionSender().sendString(name, 83, 0);
		} else if (genericName.startsWith("axe")
				|| genericName.startsWith("battleaxe")) {
			player.getActionSender().sendSidebarInterface(0, 75);
			player.getActionSender().sendString(name, 75, 0);
		} else if (genericName.startsWith("Axe")
				|| genericName.startsWith("Battleaxe")) {
			player.getActionSender().sendSidebarInterface(0, 75);
			player.getActionSender().sendString(name, 75, 0);
		} else if (genericName.startsWith("halberd")) {
			player.getActionSender().sendSidebarInterface(0, 84);
			player.getActionSender().sendString(name, 84, 0);
		} else {
			player.getActionSender().sendSidebarInterface(0, 82);
			player.getActionSender().sendString(name, 82, 0);
		}
	}

	private boolean setSpecials(int weaponId) {
		switch (weaponId) {

		case 4151:
			player.getActionSender().sendInterfaceConfig(93, 10, false);
			return true;

		case 1215:
		case 1231:
		case 5680:
		case 5698:
		case 8872:
		case 8874:
		case 8876:
		case 8878:
			player.getActionSender().sendInterfaceConfig(89, 12, false);
			return true;

		case 35:
		case 1305:
		case 4587:
		case 6746:
		case 11037:
			player.getActionSender().sendInterfaceConfig(82, 12, false);
			return true;

		case 7158:
			player.getActionSender().sendInterfaceConfig(81, 12, false);
			return true;

		case 859:
		case 861:
		case 6724:
		case 10284:
		case 11235:
			player.getActionSender().sendInterfaceConfig(77, 10, false);
			return true;

		case 8880:
			player.getActionSender().sendInterfaceConfig(79, 10, false);
			return true;

		case 3101:
			player.getActionSender().sendInterfaceConfig(78, 12, false);
			return true;

		case 1434:
		case 11061:
		case 10887:
			player.getActionSender().sendInterfaceConfig(88, 12, false);
			return true;

		case 1377:
		case 6739:
			player.getActionSender().sendInterfaceConfig(75, 12, false);
			return true;

		case 4153:
			player.getActionSender().sendInterfaceConfig(76, 10, false);
			return true;

		case 3204:
			player.getActionSender().sendInterfaceConfig(84, 10, false);
			return true;

		case 1249: // Dragon spear
		case 1263: // Dragon spear(p)
		case 3176: // Dragon spear(kp)
		case 5716: // Dragon spear(p+)
		case 5730: // Dragon spear(p++)
			player.getActionSender().sendInterfaceConfig(87, 12, false);
			return true;

		}
		return false;
	}

	/**
	 * Filters a weapon name.
	 * 
	 * @param name
	 *            The original name.
	 * @return The filtered name.
	 */
	private String filterWeaponName(String name) {
		final String[] filtered = new String[] { "Iron", "Steel", "Scythe",
				"Black", "Mithril", "Adamant", "Rune", "Granite", "Dragon",
				"Drag", "Crystal", "Bronze", };
		for (String filter : filtered) {
			name = name.replaceAll(filter, "");
		}
		return name;
	}

}
