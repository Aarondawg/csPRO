package org.hyperion.rs2.packet;

import org.hyperion.rs2.content.Jewellery;
import org.hyperion.rs2.content.Shop;
import org.hyperion.rs2.content.skills.Smithing;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Duel;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.net.Packet;

/**
 * Remove item options.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ItemOptionPacketHandler implements PacketHandler {

	/**
	 * Option 1 opcode.
	 */
	private static final int OPTION_1 = 56;

	/**
	 * Option 2 opcode.
	 */
	private static final int OPTION_2 = 96;

	/**
	 * Option 3 opcode.
	 */
	private static final int OPTION_3 = 218;

	/**
	 * Option 4 opcode.
	 */
	private static final int OPTION_4 = 212;

	/**
	 * Option 5 opcode.
	 */
	private static final int OPTION_5 = 243;

	@Override
	public void handle(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case OPTION_1:
			handleItemOption1(player, packet);
			break;
		case OPTION_2:
			handleItemOption2(player, packet);
			break;
		case OPTION_3:
			handleItemOption3(player, packet);
			break;
		case OPTION_4:
			handleItemOption4(player, packet);
			break;
		case OPTION_5:
			handleItemOption5(player, packet);
			break;
		}
	}

	/**
	 * Handles item option 1.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOption1(Player player, Packet packet) {
		System.out.println("Item option 1");
		int interfaceId = packet.getShort();
		packet.getShort();
		int slot = packet.getLEShortA();
		int itemId = packet.getShortA();
		if (player.getConstruction().handleItemOptions(interfaceId, slot,
				itemId, 1)) {
			return;
		}

		switch (interfaceId) {
		case Equipment.INTERFACE1:
		case Equipment.INTERFACE2:
			if (slot >= 0 && slot < Equipment.SIZE) {
				if (Container.transfer(player.getEquipment(),
						player.getInventory(), slot, itemId)) {
					player.getBonuses().refresh();
					player.refreshCombatVariables(slot);
				} else {
					player.getActionSender().sendMessage(
							"You do not have enough space in your inventory.");
				}
			}
			break;
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				Bank.deposit(player, slot, itemId, 1);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Bank.SIZE) {
				Bank.withdraw(player, slot, itemId, 1);
			}
			break;
		case Shop.SHOP_INTERFACE:
			if (slot >= 0 && slot < Shop.SIZE) {
				Shop shop = player.getCurrentShop();
				if (shop != null) {
					ItemDefinition def = shop.getItems().get(slot)
							.getDefinition();
					int price = shop.getName().contains("General Store") ? def
							.getGeneralStorePrice() : def
							.getSpecialStorePrice();
					player.getActionSender().sendMessage(
							"The "
									+ def.getName().toLowerCase()
									+ " currently costs "
									+ price
									+ " "
									+ ItemDefinition.forId(shop.getCurrency())
											.getName().toLowerCase() + ".");
				}
			}
			break;
		case Shop.SHOP_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Shop.SIZE) {
				Shop shop = player.getCurrentShop();
				if (shop != null) {
					ItemDefinition def = player.getInventory().get(slot)
							.getDefinition();
					int price = def.getLowAlcValue();// shop.getName().contains("General Store")
														// ?
														// def.getLowAlcValue()
														// :
														// def.getHighAlcValue();
					player.getActionSender().sendMessage(
							"The shop will buy the "
									+ def.getName().toLowerCase()
									+ " for "
									+ price
									+ " "
									+ ItemDefinition.forId(shop.getCurrency())
											.getName().toLowerCase() + ".");
				}
			}
			break;
		case Smithing.SMITHING_INTERFACE:
			Smithing.parseValues(player, slot, itemId, 1);
			break;
		case Trade.FIRST_TRADING_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager().getTrade()
						.removeItem(player, slot, 1);
			}
			break;
		case Trade.INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager().getTrade()
						.offerItem(player, slot, 1);
			}
			break;
		case Duel.FIRST_DUELING_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager().getDuel()
						.removeItem(player, slot, 1);
			}
			break;
		case Duel.INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager().getDuel().stakeItem(player, slot, 1);
			}
			break;
		}
	}

	/**
	 * Handles item option 2.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOption2(Player player, Packet packet) {
		System.out.println("Item option 2");
		int interfaceSet = packet.getInt2();
		int interfaceId = interfaceSet >> 16;
		int itemId = packet.getShort() & 0xFFFF;
		int slot = packet.getLEShort() & 0xFFFF;
		switch (interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				Bank.deposit(player, slot, itemId, 5);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Bank.SIZE) {
				Bank.withdraw(player, slot, itemId, 5);
			}
			break;
		case Shop.SHOP_INTERFACE:
			if (slot >= 0 && slot < Shop.SIZE) {
				Shop.buy(player, slot, itemId, 1);
			}
			break;
		case Shop.SHOP_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Shop.SIZE) {
				Shop.sell(player, slot, itemId, 1);
			}
			break;
		case Smithing.SMITHING_INTERFACE:
			Smithing.parseValues(player, slot, itemId, 5);
			break;
		case Equipment.INTERFACE1:
			if (!Jewellery.rubItem(player, slot, itemId, true)) {
				player.getActionSender().sendMessage(
						"You can't operate this item.");
			}
			break;
		case Trade.FIRST_TRADING_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager().getTrade()
						.removeItem(player, slot, 5);
			}
			break;
		case Trade.INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager().getTrade()
						.offerItem(player, slot, 5);
			}
			break;
		case Duel.FIRST_DUELING_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager().getDuel()
						.removeItem(player, slot, 5);
			}
			break;
		case Duel.INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager().getDuel().stakeItem(player, slot, 5);
			}
			break;
		}

	}

	/**
	 * Handles item option 3.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOption3(Player player, Packet packet) {
		System.out.println("Item option 3");
		int itemId = packet.getShortA() & 0xFFFF;
		int interfaceSet = packet.getInt();
		int interfaceId = interfaceSet >> 16;
		int slot = packet.getShortA() & 0xFFFF;
		switch (interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				Bank.deposit(player, slot, itemId, 10);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Bank.SIZE) {
				Bank.withdraw(player, slot, itemId, 10);
			}
			break;
		case Shop.SHOP_INTERFACE:
			if (slot >= 0 && slot < Shop.SIZE) {
				Shop.buy(player, slot, itemId, 5);
			}
			break;
		case Shop.SHOP_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Shop.SIZE) {
				Shop.sell(player, slot, itemId, 5);
			}
			break;
		case Smithing.SMITHING_INTERFACE:
			Smithing.parseValues(player, slot, itemId, 10);
			break;
		case Trade.FIRST_TRADING_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager().getTrade()
						.removeItem(player, slot, 10);
			}
			break;
		case Trade.INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager().getTrade()
						.offerItem(player, slot, 10);
			}
			break;
		case Duel.FIRST_DUELING_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager().getDuel()
						.removeItem(player, slot, 10);
			}
			break;
		case Duel.INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager().getDuel()
						.stakeItem(player, slot, 10);
			}
			break;
		}
	}

	/**
	 * Handles item option 4.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOption4(Player player, Packet packet) {
		System.out.println("Item option 4");
		int slot = packet.getShortA() & 0xFFFF;
		int itemId = packet.getLEShortA() & 0xFFFF;
		int interfaceSet = packet.getLEInt();
		int interfaceId = interfaceSet >> 16;

		switch (interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				Bank.deposit(player, slot, itemId, player.getInventory()
						.getCount(itemId));
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Bank.SIZE) {
				Bank.withdraw(player, slot, itemId,
						player.getBank().getCount(itemId));
			}
			break;
		case Shop.SHOP_INTERFACE:
			if (slot >= 0 && slot < Shop.SIZE) {
				Shop.buy(player, slot, itemId, 10);
			}
			break;
		case Shop.SHOP_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Shop.SIZE) {
				Shop.sell(player, slot, itemId, 10);
			}
			break;
		case Trade.FIRST_TRADING_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager()
						.getTrade()
						.removeItem(
								player,
								slot,
								player.getRequestManager().getTrade()
										.getOffer(player).getCount(itemId));
			}
			break;
		case Trade.INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager()
						.getTrade()
						.offerItem(player, slot,
								player.getInventory().getCount(itemId));
			}
			break;
		case Duel.FIRST_DUELING_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager()
						.getDuel()
						.removeItem(
								player,
								slot,
								player.getRequestManager().getDuel()
										.getStake(player).getCount(itemId));
			}
			break;
		case Duel.INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getRequestManager()
						.getDuel()
						.stakeItem(player, slot,
								player.getInventory().getCount(itemId));
			}
			break;
		}
	}

	/**
	 * Handles item option 5.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOption5(Player player, Packet packet) {
		System.out.println("Item option 5");
		int slot = packet.getShortA() & 0xFFFF;
		int itemId = packet.getShortA() & 0xFFFF;
		int interfaceSet = packet.getInt2();
		int interfaceId = interfaceSet >> 16;
		switch (interfaceId) {
		case Bank.PLAYER_INVENTORY_INTERFACE:
		case Trade.FIRST_TRADING_INTERFACE:
		case Trade.INVENTORY_INTERFACE:
		case Duel.INVENTORY_INTERFACE:
		case Duel.FIRST_DUELING_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(
						interfaceId, slot, itemId);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Bank.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(
						interfaceId, slot, itemId);
			}
			break;
		}
	}

}
