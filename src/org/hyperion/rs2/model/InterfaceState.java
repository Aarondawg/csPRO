package org.hyperion.rs2.model;

import java.util.ArrayList;
import java.util.List;

import org.hyperion.rs2.content.skills.Cooking;
import org.hyperion.rs2.content.skills.Crafting;
import org.hyperion.rs2.content.skills.Fletching;
import org.hyperion.rs2.content.skills.Smithing;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.ContainerListener;
import org.hyperion.rs2.model.container.Duel;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.container.Trade;

/**
 * Contains information about the state of interfaces open in the client.
 * 
 * @author Graham Edgecombe
 * 
 */
public class InterfaceState {

	/**
	 * The current open interface.
	 */
	private int currentInterface = -1;

	/**
	 * The active enter amount interface.
	 */
	private int enterAmountInterfaceId = -1;

	/**
	 * The active enter amount id.
	 */
	private int enterAmountId;

	/**
	 * The active enter amount slot.
	 */
	private int enterAmountSlot;

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * A list of container listeners used on interfaces that have containers.
	 */
	private List<ContainerListener> containerListeners = new ArrayList<ContainerListener>();

	/**
	 * Creates the interface state.
	 */
	public InterfaceState(Player player) {
		this.player = player;
	}

	/**
	 * Checks if the specified interface is open.
	 * 
	 * @param id
	 *            The interface id.
	 * @return <code>true</code> if the interface is open, <code>false</code> if
	 *         not.
	 */
	public boolean isInterfaceOpen(int id) {
		return currentInterface == id;
	}

	/**
	 * Gets the current open interface.
	 * 
	 * @return The current open interface.
	 */
	public int getCurrentInterface() {
		return currentInterface;
	}

	/**
	 * Called when an interface is opened.
	 * 
	 * @param id
	 *            The interface.
	 */
	public void interfaceOpened(int id) {
		if (currentInterface != -1) {
			interfaceClosed(id);
		}
		currentInterface = id;
	}

	/**
	 * Called when an interface is closed.
	 */
	public void interfaceClosed(int id) {
		/*
		 * We check if the player just won a duel duel (The only case of this
		 * interface displaying)
		 */
		if (currentInterface == 110
				&& player.getRequestManager().getDuel() != null) {
			Item[] won = player
					.getRequestManager()
					.getDuel()
					.getStake(
							player.getRequestManager().getDuel()
									.getOther(player)).toArray();
			Inventory.addInventoryItems(player, won);
			player.getRequestManager().getDuel().reset();
		}
		if (currentInterface == 233 && id != currentInterface
				&& player.getConstruction().getBuildingRoom() != null) {
			for (GameObject object : player.getConstruction().getBuildingRoom()
					.getHotspots()) {
				Location local = Location.create(object.getLocation().getX()
						+ player.getConstruction().getBuildingRoom().getX(),
						object.getLocation().getY()
								+ player.getConstruction().getBuildingRoom()
										.getY(), object.getLocation().getZ());
				player.getActionSender().sendDestroyLocalObject(
						object.getType(), object.getRotation(), local);
			}
			player.getConstruction().setBuildingRoom(null, -1);
		}
		/*
		 * We check if we're trading, and make sure the current interface is not
		 * the "first trading interface"
		 */
		if (player.getRequestManager().isTrading()
				&& currentInterface != Trade.FIRST_TRADING_INTERFACE) {
			player.getRequestManager().getTrade().close();
		}
		/*
		 * We check if we're dueling, and make sure the current interface is not
		 * the "first dueling interface"
		 */
		if (player.getRequestManager().isDueling()
				&& currentInterface != Duel.FIRST_DUELING_INTERFACE) {
			player.getRequestManager().getDuel().close();
		}
		currentInterface = -1;
		enterAmountInterfaceId = -1;
		for (ContainerListener l : containerListeners) {
			player.getInventory().removeListener(l);
			player.getEquipment().removeListener(l);
			player.getBank().removeListener(l);
			if (player.getCurrentShop() != null) {
				player.getCurrentShop().getItems().removeListener(l);
			}
		}
	}

	/**
	 * Adds a listener to an interface that is closed when the inventory is
	 * closed.
	 * 
	 * @param container
	 *            The container.
	 * @param containerListener
	 *            The listener.
	 */
	public void addListener(Container container,
			ContainerListener containerListener) {
		container.addListener(containerListener);
		containerListeners.add(containerListener);
	}

	/**
	 * Called to open the enter amount interface.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param slot
	 *            The slot.
	 * @param id
	 *            The id.
	 */
	public void openEnterAmountInterface(int interfaceId, int slot, int id) {
		enterAmountInterfaceId = interfaceId;
		enterAmountSlot = slot;
		enterAmountId = id;
		player.getActionSender().sendEnterAmountInterface();
	}

	/**
	 * Called to open the enter amount interface.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param id
	 *            The id.
	 */
	public void openEnterAmountInterface(int interfaceId, int id) {
		enterAmountInterfaceId = interfaceId;
		enterAmountId = id;
		player.getActionSender().sendEnterAmountInterface();
	}

	/**
	 * Called to open the enter amount interface.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 */
	public void openEnterAmountInterface(int interfaceId) {
		enterAmountInterfaceId = interfaceId;
		player.getActionSender().sendEnterAmountInterface();
	}

	/**
	 * Checks if the enter amount interface is open.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isEnterAmountInterfaceOpen() {
		return enterAmountInterfaceId != -1;
	}

	/**
	 * Called when the enter amount interface is closed.
	 * 
	 * @param amount
	 *            The amount that was entered.
	 */
	public void closeEnterAmountInterface(int amount) {
		try {
			switch (enterAmountInterfaceId) {
			case Bank.PLAYER_INVENTORY_INTERFACE:
				Bank.deposit(player, enterAmountSlot, enterAmountId, amount);
				break;
			case Bank.BANK_INVENTORY_INTERFACE:
				Bank.withdraw(player, enterAmountSlot, enterAmountId, amount);
				break;
			case Trade.FIRST_TRADING_INTERFACE:
				if (player.getRequestManager().getTrade() != null) {
					player.getRequestManager().getTrade()
							.removeItem(player, enterAmountSlot, amount);
				}
				break;
			case Trade.INVENTORY_INTERFACE:
				if (player.getRequestManager().getTrade() != null) {
					player.getRequestManager().getTrade()
							.offerItem(player, enterAmountSlot, amount);
				}
				break;
			case Duel.FIRST_DUELING_INTERFACE:
				if (player.getRequestManager().getDuel() != null) {
					player.getRequestManager().getDuel()
							.removeItem(player, enterAmountSlot, amount);
				}
				break;
			case Duel.INVENTORY_INTERFACE:
				if (player.getRequestManager().getDuel() != null) {
					player.getRequestManager().getDuel()
							.stakeItem(player, enterAmountSlot, amount);
				}
				break;
			case Cooking.COOKING_INTERFACE:
				Cooking.cook(player, amount);
				break;

			case 303:
				if (enterAmountId == 1) { // This means its longbows. 
					if (player.getFletchingVariables().isFletching()) {
						switch (player.getFletchingVariables()
								.getFletchingType()) {
						case OAK_LOGS:
							Fletching.fletchLogs(player, 20, amount, 1522, 56,
									25);
							break;
						case WILLOW_LOGS:
							Fletching.fletchLogs(player, 35, amount, 1519, 58,
									41.5);
							break;
						case MAPLE_LOGS:
							Fletching.fletchLogs(player, 50, amount, 1517, 62,
									58.25);
							break;
						case YEW_LOGS:
							Fletching.fletchLogs(player, 65, amount, 1515, 66,
									75);
							break;
						case MAGIC_LOGS:
							Fletching.fletchLogs(player, 80, amount, 1513, 70,
									91.5);
							break;
						}
					}
				} else if (enterAmountId == 2) { // This means its shortbows.
					switch (player.getFletchingVariables().getFletchingType()) {
					case OAK_LOGS:
						Fletching
								.fletchLogs(player, 20, amount, 1522, 54, 16.5);
						break;
					case WILLOW_LOGS:
						Fletching.fletchLogs(player, 35, amount, 1519, 60,
								33.25);
						break;
					case MAPLE_LOGS:
						Fletching.fletchLogs(player, 50, amount, 1517, 64, 50);
						break;
					case YEW_LOGS:
						Fletching
								.fletchLogs(player, 65, amount, 1515, 68, 76.5);
						break;
					case MAGIC_LOGS:
						Fletching.fletchLogs(player, 80, amount, 1513, 72,
								83.25);
						break;
					}
				}
				break;
			/*
			 * This is used for crafting, aswell as fletching.
			 */
			case 304:
				if (player.getCraftingVariables().isCrafting()) {
					switch (player.getCraftingVariables().getLeatherType()) {
					case GREEN_DRAGON_LEATHER:
						Crafting.leatherCrafting(player, 63, amount, 1745, 3,
								1135, 186);
						break;
					case BLUE_DRAGON_LEATHER:
						Crafting.leatherCrafting(player, 71, amount, 2505, 3,
								2499, 210);
						break;
					case RED_DRAGON_LEATHER:
						Crafting.leatherCrafting(player, 77, amount, 2507, 3,
								2501, 234);
						break;
					case BLACK_DRAGON_LEATHER:
						Crafting.leatherCrafting(player, 84, amount, 2509, 3,
								2503, 258);
						break;
					}
				} else if (player.getFletchingVariables().isFletching()) {
					switch (enterAmountId) {// Those are the button ids.
					case 5:
						Fletching.fletchLogs(player, 1, amount, 1511, 52, 5);
						break;
					case 9:
						Fletching.fletchLogs(player, 5, amount, 1511, 50, 5);
						break;
					case 13:
						Fletching.fletchLogs(player, 10, amount, 1511, 48, 10);
						break;
					}
				}
				break;
			/*
			 * Hard leather body interface, might have some other use aswell.
			 */
			case 309:
				if (player.getCraftingVariables().isCrafting()) {
					Crafting.leatherCrafting(player, 28, amount, 1743, 1, 1131,
							35);
				}
				break;
			/*
			 * Tanning interface.
			 */
			case 154:
				switch (enterAmountId) {
				/*
				 * Soft leather.
				 */
				case 1741:
					Crafting.tan(player, amount, 1, 1739, 1741);
					break;
				/*
				 * Hard leather.
				 */
				case 1743:
					Crafting.tan(player, amount, 3, 1739, 1743);
					break;
				/*
				 * Green d leather.
				 */
				case 1745:
					Crafting.tan(player, amount, 20, 1753, 1745);
					break;
				/*
				 * Blue d leather.
				 */
				case 2505:
					Crafting.tan(player, amount, 20, 1751, 2505);
					break;
				/*
				 * Red d leather.
				 */
				case 2507:
					Crafting.tan(player, amount, 20, 1749, 2507);
					break;
				/*
				 * Black d leather.
				 */
				case 2509:
					Crafting.tan(player, amount, 20, 1747, 2509);
					break;
				}
				break;
			case Smithing.SMELTING_INTERFACE:
				switch (enterAmountId) {
				case 2363:// Rune
					Smithing.furnaceSmelting(player, 85, 451, 453, 8, 2363,
							amount, 50, false, true);
					break;
				case 2361:// Adamant
					Smithing.furnaceSmelting(player, 70, 449, 453, 4, 2361,
							amount, 37.5, false, true);
					break;
				case 2359:// Mithril
					Smithing.furnaceSmelting(player, 50, 447, 453, 4, 2359,
							amount, 30, false, true);
					break;
				case 2357:// Gold
					Smithing.furnaceSmelting(player, 40, 444, 444, 1, 2357,
							amount, 22.5, false, false);
					break;
				case 2353:// Steel
					Smithing.furnaceSmelting(player, 30, 440, 453, 2, 2353,
							amount, 17.5, false, true);
					break;
				case 2355:// Silver
					Smithing.furnaceSmelting(player, 20, 442, 442, 1, 2355,
							amount, 13.7, false, false);
					break;
				case 2351:// Iron
					Smithing.furnaceSmelting(player, 15, 440, 440, 1, 2351,
							amount, 12.5, true, false);
					break;
				case 2349:
					Smithing.furnaceSmelting(player, 1, 438, 436, 1, 2349,
							amount, 6.2, false, true);
					break;
				}
				break;
			/*
			 * Spinning interface.
			 */
			case 459:
				switch (enterAmountId) {
				case 10814:
					Crafting.spin(player, 30, amount, 10814, 954, 25);
					break;
				case 9436:
					Crafting.spin(player, 10, amount, 9436, 9438, 15);
					break;
				case 6043:
					Crafting.spin(player, 10, amount, 6043, 9438, 15);
					break;
				case 6045:
					Crafting.spin(player, 10, amount, 6045, 9438, 15);
					break;
				case 6047:
					Crafting.spin(player, 10, amount, 6047, 9438, 15);
					break;
				case 6049:
					Crafting.spin(player, 10, amount, 6049, 9438, 15);
					break;
				case 6051:
					Crafting.spin(player, 19, amount, 6051, 6038, 30);
					break;
				case 6053:
					Crafting.spin(player, 10, amount, 6053, 9438, 15);
					break;
				case 1737:
					Crafting.spin(player, 1, amount, 1737, 1759, 2.5);
					break;
				case 1779:
					Crafting.spin(player, 10, amount, 1779, 1777, 13);
					break;
				}
				break;
			}
		} finally {
			enterAmountInterfaceId = -1;
		}
	}

	public void openEnterNameInterface(int iterfaceId) {
		// TODO: Configs if we use it for more than cons..
		player.getActionSender().sendNameRequest();
	}

	public enum InterfaceOption {
		CONSTRUCTION, BARROWS,
	}

}
