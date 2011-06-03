package org.hyperion.rs2.content.skills;

import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;

public class Crafting {

	private static final Animation LEATHER_CRAFTING = Animation.create(1249);
	private static final Random r = new Random();
	private static final Item THREAD = new Item(1734);

	public static void spin(Player player, int levelReq, int amount,
			int itemToDelete, int itemToRecieve, double xpToReceive) {
		player.getActionSender().sendCloseInterface();
		if (player.getSkills().getLevel(Skills.CRAFTING) >= levelReq) {
			if (player.getInventory().contains(itemToDelete)) {
				if (amount > player.getInventory().getCount(itemToDelete)) {
					amount = player.getInventory().getCount(itemToDelete);
				}
				// player.resetSkilling();
				// player.isCrafting = true;
				player.getActionQueue().addAction(
						new SpinningAction(player, amount, itemToDelete,
								itemToRecieve, xpToReceive));
			} else
				player.getActionSender().sendMessage(
						"You don't have enough "
								+ new Item(itemToDelete).getDefinition()
										.getName().toLowerCase()
								+ " in order to craft "
								+ new Item(itemToRecieve).getDefinition()
										.getName().toLowerCase() + "'s.");
		} else
			player.getActionSender().sendMessage(
					"You need a Crafting level of "
							+ levelReq
							+ " in order to craft "
							+ new Item(itemToRecieve).getDefinition().getName()
									.toLowerCase() + "'s.");
	}

	public static void leatherCrafting(Player player, int levelReq, int amount,
			int leatherId, int leatherAmount, int itemToRecieve,
			double xpToReceive) {
		player.getActionSender().sendCloseInterface();
		if (player.getSkills().getLevel(Skills.CRAFTING) >= levelReq) {
			if (player.getInventory().contains(1734)) {
				if (player.getInventory().contains(leatherId, leatherAmount)) {
					if (amount > player.getInventory().getCount(leatherId)) {
						amount = player.getInventory().getCount(leatherId);
					}
					// player.resetSkilling();
					// player.isCrafting = true;
					// player.getCrafting().amountToCraft = amount;
					player.getActionQueue().addAction(
							new LeatherCraftingAction(player, amount,
									leatherId, leatherAmount, itemToRecieve,
									xpToReceive));
					// appendLeatherEngine(player, amount, leatherId,
					// leatherAmount, itemToRecieve, xpToReceive);
				} else {
					player.getActionSender().sendMessage(
							"You don't have enough hides.");
				}
			} else {
				Item item = new Item(itemToRecieve);
				player.getActionSender().sendMessage(
						"You need some thread, in order to craft "
								+ item.getDefinition().getName() + ".");
			}
		} else {
			Item item = new Item(itemToRecieve);
			player.getActionSender().sendMessage(
					"You need a Crafting level of " + levelReq
							+ " in order to craft a "
							+ item.getDefinition().getName() + ".");
		}
	}

	/**
	 * Initializes the tanning screen for the player. This needs to be redone,
	 * and light up when people have the correct itens in inventory.
	 */
	public static void sendTanningInterface(Player player) {
		player.getActionSender().sendInterface(324);
		player.getActionSender().sendString("Soft Leather", 324, 108);
		player.getActionSender().sendString("Hard Leather", 324, 109);
		player.getActionSender().sendString("Snakeskin", 324, 110);
		player.getActionSender().sendString("Snakeskin", 324, 111);
		player.getActionSender().sendString("Green d'hide", 324, 112);
		player.getActionSender().sendString("Blue d'hide", 324, 113);
		player.getActionSender().sendString("Red d'hide", 324, 114);
		player.getActionSender().sendString("Black d'hide", 324, 115);
		player.getActionSender().sendString("1 coins", 324, 116);
		player.getActionSender().sendString("3 coins", 324, 117);
		player.getActionSender().sendString("20 coins", 324, 118);
		player.getActionSender().sendString("15 coins", 324, 119);
		player.getActionSender().sendString("20 coins", 324, 120);
		player.getActionSender().sendString("20 coins", 324, 121);
		player.getActionSender().sendString("20 coins", 324, 122);
		player.getActionSender().sendString("20 coins", 324, 123);
		player.getActionSender().sendInterfaceModel(324, 100, 275, 1739);
		player.getActionSender().sendInterfaceModel(324, 101, 275, 1739);
		player.getActionSender().sendInterfaceModel(324, 102, 275, 7801);
		player.getActionSender().sendInterfaceModel(324, 103, 275, 6287);
		player.getActionSender().sendInterfaceModel(324, 104, 275, 1753);
		player.getActionSender().sendInterfaceModel(324, 105, 275, 1751);
		player.getActionSender().sendInterfaceModel(324, 106, 275, 1749);
		player.getActionSender().sendInterfaceModel(324, 107, 275, 1747);
	}

	public static void tan(Player player, int amount, int payment,
			int deletedItem, int addedItem) {
		Item iPayment = new Item(995, payment * amount);
		player.getActionSender().sendCloseInterface();
		if (player.getInventory().contains(deletedItem)) {
			if (player.getInventory().contains(iPayment)) {
				if (amount > player.getInventory().getCount(deletedItem)) {
					amount = player.getInventory().getCount(deletedItem);
				}
				player.getInventory().remove(iPayment);
				player.getInventory().remove(new Item(deletedItem, amount));
				player.getInventory().add(new Item(addedItem, amount));
			} else {
				player.getActionSender().sendMessage(
						"You do not have enough coins.");
			}
		} else {
			player.getActionSender().sendMessage(
					"You don't have enough rough hides in your inventory.");
		}
	}

	public enum DragonLeatherType {
		GREEN_DRAGON_LEATHER, BLUE_DRAGON_LEATHER, RED_DRAGON_LEATHER, BLACK_DRAGON_LEATHER,
	}

	/*
	 * public enum DragonLeatherType { GREEN_DRAGON_LEATHER(1753),
	 * BLUE_DRAGON_LEATHER(1751), RED_DRAGON_LEATHER(1749),
	 * BLACK_DRAGON_LEATHER(1747);
	 * 
	 * private int id;
	 * 
	 * private DragonLeatherType(int id) { this.id = id; }
	 * 
	 * /** Gets the dragon leather id.
	 * 
	 * @return The dragon leather id.
	 *//*
		 * public int toId() { return id; } }
		 */

	public static boolean dragonLeatherCrafting(Player player, int childClicked) {
		switch (player.getCraftingVariables().getLeatherType()) {
		/*
		 * Green dragon leather.
		 */
		case GREEN_DRAGON_LEATHER:
			switch (childClicked) {
			/*
			 * Bodies.
			 */
			case 8: // Craft 1.
				Crafting.leatherCrafting(player, 63, 1, 1745, 3, 1135, 186);
				return true;
			case 7:// Craft 5.
				Crafting.leatherCrafting(player, 63, 5, 1745, 3, 1135, 186);
				return true;
			case 6:// Craft 10.
				Crafting.leatherCrafting(player, 63, 10, 1745, 3, 1135, 186);
				return true;
			case 5:// Craft x.
					// player.setTemporaryAttribute("xGreenBodyLvlReq", 63);
				player.getInterfaceState().openEnterAmountInterface(304);
				// player.getActionSender().sendAmountRequest();
				return true;
				/*
				 * Vambs
				 */
			case 12:// Craft 1.
				Crafting.leatherCrafting(player, 57, 1, 1745, 1, 1065, 62);
				return true;
			case 11:// Craft 5.
				Crafting.leatherCrafting(player, 57, 5, 1745, 1, 1065, 62);
				return true;
			case 10:// Craft 10.
				Crafting.leatherCrafting(player, 57, 10, 1745, 1, 1065, 62);
				return true;
			case 9: // Craft x.
				player.getInterfaceState().openEnterAmountInterface(304);
				return true;
				/*
				 * Chaps.
				 */
			case 16:// Craft 1.
				Crafting.leatherCrafting(player, 60, 1, 1745, 2, 1099, 124);
				return true;
			case 15:// Craft 5.
				Crafting.leatherCrafting(player, 60, 5, 1745, 2, 1099, 124);
				return true;
			case 14:// Craft 10.
				Crafting.leatherCrafting(player, 60, 10, 1745, 2, 1099, 124);
				return true;
			case 13: // Craft x.
				player.getInterfaceState().openEnterAmountInterface(304);
				return true;
			}
			break;
		/*
		 * Blue dragon leather.
		 */
		case BLUE_DRAGON_LEATHER:
			switch (childClicked) {
			/*
			 * Bodies.
			 */
			case 8: // Craft 1.
				Crafting.leatherCrafting(player, 71, 1, 2505, 3, 2499, 210);
				return true;
			case 7:// Craft 5.
				Crafting.leatherCrafting(player, 71, 5, 2505, 3, 2499, 210);
				return true;
			case 6:// Craft 10.
				Crafting.leatherCrafting(player, 71, 10, 2505, 3, 2499, 210);
				return true;
			case 5:// Craft x.
				player.getInterfaceState().openEnterAmountInterface(304);
				return true;
				/*
				 * Vambs
				 */
			case 12:// Craft 1.
				Crafting.leatherCrafting(player, 66, 1, 2505, 1, 2487, 70);
				return true;
			case 11:// Craft 5.
				Crafting.leatherCrafting(player, 66, 5, 2505, 1, 2487, 70);
				return true;
			case 10:// Craft 10.
				Crafting.leatherCrafting(player, 66, 10, 2505, 1, 2487, 70);
				return true;
			case 9: // Craft x.
				player.getInterfaceState().openEnterAmountInterface(304);
				return true;
				/*
				 * Chaps.
				 */
			case 16:// Craft 1.
				Crafting.leatherCrafting(player, 68, 1, 2505, 2, 2493, 140);
				return true;
			case 15:// Craft 5.
				Crafting.leatherCrafting(player, 68, 5, 2505, 2, 2493, 140);
				return true;
			case 14:// Craft 10.
				Crafting.leatherCrafting(player, 68, 10, 2505, 2, 2493, 140);
				return true;
			case 13: // Craft x.
				player.getInterfaceState().openEnterAmountInterface(304);
				return true;
			}
			break;
		/*
		 * Red dragon leather.
		 */
		case RED_DRAGON_LEATHER:
			switch (childClicked) {
			/*
			 * Bodies.
			 */
			case 8: // Craft 1.
				Crafting.leatherCrafting(player, 77, 1, 2507, 3, 2501, 234);
				return true;
			case 7:// Craft 5.
				Crafting.leatherCrafting(player, 77, 5, 2507, 3, 2501, 234);
				return true;
			case 6:// Craft 10.
				Crafting.leatherCrafting(player, 77, 10, 2507, 3, 2501, 234);
				return true;
			case 5:// Craft x.
				player.getInterfaceState().openEnterAmountInterface(304);
				return true;
				/*
				 * Vambs
				 */
			case 12:// Craft 1.
				Crafting.leatherCrafting(player, 73, 1, 2507, 1, 2489, 78);
				return true;
			case 11:// Craft 5.
				Crafting.leatherCrafting(player, 73, 5, 2507, 1, 2489, 78);
				return true;
			case 10:// Craft 10.
				Crafting.leatherCrafting(player, 73, 10, 2507, 1, 2489, 78);
				return true;
			case 9: // Craft x.
				player.getInterfaceState().openEnterAmountInterface(304);
				return true;
				/*
				 * Chaps.
				 */
			case 16:// Craft 1.
				Crafting.leatherCrafting(player, 75, 1, 2507, 2, 2495, 156);
				return true;
			case 15:// Craft 5.
				Crafting.leatherCrafting(player, 75, 5, 2507, 2, 2495, 156);
				return true;
			case 14:// Craft 10.
				Crafting.leatherCrafting(player, 75, 10, 2507, 2, 2495, 156);
				return true;
			case 13: // Craft x.
				player.getInterfaceState().openEnterAmountInterface(304);
				return true;
			}
			break;
		/*
		 * Black dragon leather.
		 */
		case BLACK_DRAGON_LEATHER:
			switch (childClicked) {
			/*
			 * Bodies.
			 */
			case 8: // Craft 1.
				Crafting.leatherCrafting(player, 84, 1, 2509, 3, 2503, 258);
				return true;
			case 7:// Craft 5.
				Crafting.leatherCrafting(player, 84, 5, 2509, 3, 2503, 258);
				return true;
			case 6:// Craft 10.
				Crafting.leatherCrafting(player, 84, 10, 2509, 3, 2503, 258);
				return true;
			case 5:// Craft x.
				player.getInterfaceState().openEnterAmountInterface(304);
				return true;
				/*
				 * Vambs
				 */
			case 12:// Craft 1.
				Crafting.leatherCrafting(player, 79, 1, 2509, 1, 2491, 86);
				return true;
			case 11:// Craft 5.
				Crafting.leatherCrafting(player, 79, 5, 2509, 1, 2491, 86);
				return true;
			case 10:// Craft 10.
				Crafting.leatherCrafting(player, 79, 10, 2509, 1, 2491, 86);
				return true;
			case 9: // Craft x.
				player.getInterfaceState().openEnterAmountInterface(304);
				return true;
				/*
				 * Chaps.
				 */
			case 16:// Craft 1.
				Crafting.leatherCrafting(player, 82, 1, 2509, 2, 2497, 172);
				return true;
			case 15:// Craft 5.
				Crafting.leatherCrafting(player, 82, 5, 2509, 2, 2497, 172);
				return true;
			case 14:// Craft 10.
				Crafting.leatherCrafting(player, 82, 10, 2509, 2, 2497, 172);
				return true;
			case 13: // Craft x.
				player.getInterfaceState().openEnterAmountInterface(304);
				return true;
			}
		}
		return false;
	}

	public static boolean hardLeatherBodies(Player player, int childClicked) {
		switch (childClicked) {
		case 6:// Make 1.
			leatherCrafting(player, 28, 1, 1743, 1, 1131, 35);
			break;
		case 5:// Make 5.
			leatherCrafting(player, 28, 5, 1743, 1, 1131, 35);
			break;
		case 4:// Make x.
			player.getInterfaceState().openEnterAmountInterface(309);
			// player.setTemporaryAttribute("xHardLeatherBodiesLvlReq", 28);
			// player.getActionSender().sendAmountRequest();
			break;
		case 3:// Make all.
			leatherCrafting(player, 28, player.getInventory().getCount(1743),
					1743, 1, 1131, 35);
			break;
		}
		return false;
	}

	public static boolean leatherCraftingInterface(Player player, int buttonId) {
		switch (buttonId) {
		/*
		 * Normal gloves.
		 */
		case 115:// Craft 1.
			leatherCrafting(player, 1, 1, 1741, 1, 1059, 13.8);
			return true;
		case 114:// Craft 5.
			leatherCrafting(player, 1, 5, 1741, 1, 1059, 13.8);
			return true;
		case 113:// Craft 10.
			leatherCrafting(player, 1, 10, 1741, 1, 1059, 13.8);
			return true;
			/*
			 * Armour
			 */
		case 112: // Craft 1.
			leatherCrafting(player, 14, 1, 1741, 1, 1129, 27);
			return true;
		case 111: // Craft 5.
			leatherCrafting(player, 14, 5, 1741, 1, 1129, 27);
			return true;
		case 110: // Craft 10.
			leatherCrafting(player, 14, 10, 1741, 1, 1129, 27);
			return true;
			/*
			 * Boots
			 */
		case 118:// Craft 1.
			leatherCrafting(player, 7, 1, 1741, 1, 1061, 16.3);
			return true;
		case 117:// Craft 5.
			leatherCrafting(player, 7, 5, 1741, 1, 1061, 16.3);
			return true;
		case 116:// Craft 10.
			leatherCrafting(player, 7, 10, 1741, 1, 1061, 16.3);
			return true;
			/*
			 * Vambs
			 */
		case 121:// Craft 1
			leatherCrafting(player, 11, 1, 1741, 1, 1063, 22);
			return true;
		case 120:// Craft 5.
			leatherCrafting(player, 11, 5, 1741, 1, 1063, 22);
			return true;
		case 119:// Craft 10.
			leatherCrafting(player, 11, 10, 1741, 1, 1063, 22);
			return true;
			/*
			 * Chaps
			 */
		case 124:// Craft 1.
			leatherCrafting(player, 18, 1, 1741, 1, 1095, 27);
			return true;
		case 123:// Craft 5.
			leatherCrafting(player, 18, 5, 1741, 1, 1095, 27);
			return true;
		case 122:// Craft 10.
			leatherCrafting(player, 18, 10, 1741, 1, 1095, 27);
			return true;
			/*
			 * Coif
			 */
		case 127:// Craft 1
			leatherCrafting(player, 38, 1, 1741, 1, 1169, 37);
			return true;
		case 126:// Craft 5.
			leatherCrafting(player, 38, 5, 1741, 1, 1169, 37);
			return true;
		case 125:// Craft 10.
			leatherCrafting(player, 38, 10, 1741, 1, 1169, 37);
			return true;
			/*
			 * Cowl
			 */
		case 130:// Craft 1
			leatherCrafting(player, 9, 1, 1741, 1, 1167, 18.5);
			return true;
		case 129:// Craft 5.
			leatherCrafting(player, 9, 5, 1741, 1, 1167, 18.5);
			return true;
		case 128:// Craft 10.
			leatherCrafting(player, 9, 10, 1741, 1, 1167, 18.5);
			return true;
		default:
			System.out
					.println("Unknown buttonId on the Leather crafting interface: "
							+ buttonId);
		}
		return false;
	}

	public static boolean tanningButtons(Player player, int buttonId) {
		switch (buttonId) {
		/*
		 * Soft leather.
		 */
		case 148: // tan 1.
			tan(player, 1, 1, 1739, 1741);
			return true;
		case 140: // tan 5
			tan(player, 5, 1, 1739, 1741);
			return true;
		case 132: // X
			player.getInterfaceState().openEnterAmountInterface(324, 1741);
			// player.getActionSender().sendAmountRequest();
			/*
			 * player.setTemporaryAttribute("tanXDeletedItem", 1739);
			 * player.setTemporaryAttribute("tanXAddedItem", 1741);
			 * player.setTemporaryAttribute("tanXPayment", 1);
			 */
			return true;
		case 124: // All
			tan(player, player.getInventory().getCount(1739), 1, 1739, 1741);
			return true;
			/*
			 * Hard leather.
			 */
		case 149: // tan 1.
			tan(player, 1, 3, 1739, 1743);
			return true;
		case 141: // tan 5
			tan(player, 5, 3, 1739, 1743);
			return true;
		case 133: // X
			player.getInterfaceState().openEnterAmountInterface(324, 1743);
			/*
			 * player.getActionSender().sendAmountRequest();
			 * player.setTemporaryAttribute("tanXDeletedItem", 1739);
			 * player.setTemporaryAttribute("tanXAddedItem", 1743);
			 * player.setTemporaryAttribute("tanXPayment", 3);
			 */
			return true;
		case 125: // All
			tan(player, player.getInventory().getCount(1739), 3, 1739, 1743);
			return true;
			/*
			 * TODO: Snake skin.
			 */
			/*
			 * Green d' leather.
			 */
		case 152:// tan 1.
			tan(player, 1, 20, 1753, 1745);
			return true;
		case 144:// tan 5.
			tan(player, 5, 20, 1753, 1745);
			return true;
		case 136:// tan x.
			player.getInterfaceState().openEnterAmountInterface(324, 1745);
			/*
			 * player.getActionSender().sendAmountRequest();
			 * player.setTemporaryAttribute("tanXDeletedItem", 1753);
			 * player.setTemporaryAttribute("tanXAddedItem", 1745);
			 * player.setTemporaryAttribute("tanXPayment", 20);
			 */
			return true;
		case 128:// tan all.
			tan(player, player.getInventory().getCount(1753), 20, 1753, 1745);
			return true;
			/*
			 * Blue d' leather.
			 */
		case 153:// tan 1.
			tan(player, 1, 20, 1751, 2505);
			return true;
		case 145:// tan 5.
			tan(player, 5, 20, 1751, 2505);
			return true;
		case 137:// tan x.
			player.getInterfaceState().openEnterAmountInterface(324, 2505);
			/*
			 * player.getActionSender().sendAmountRequest();
			 * player.setTemporaryAttribute("tanXDeletedItem", 1751);
			 * player.setTemporaryAttribute("tanXAddedItem", 2505);
			 * player.setTemporaryAttribute("tanXPayment", 20);
			 */
			return true;
		case 129:// tan all.
			tan(player, player.getInventory().getCount(1751), 20, 1751, 2505);
			return true;
			/*
			 * Red d' leather.
			 */
		case 154:// tan 1.
			tan(player, 1, 20, 1749, 2507);
			return true;
		case 146:// tan 5.
			tan(player, 5, 20, 1749, 2507);
			return true;
		case 138:// tan x.
			player.getInterfaceState().openEnterAmountInterface(324, 2507);
			/*
			 * player.getActionSender().sendAmountRequest();
			 * player.setTemporaryAttribute("tanXDeletedItem", 1749);
			 * player.setTemporaryAttribute("tanXAddedItem", 2507);
			 * player.setTemporaryAttribute("tanXPayment", 20);
			 */
			return true;
		case 130:// tan all.
			tan(player, player.getInventory().getCount(1749), 20, 1749, 2507);
			return true;
			/*
			 * Black d' leather.
			 */
		case 155:// tan 1.
			tan(player, 1, 20, 1747, 2509);
			return true;
		case 147:// tan 5.
			tan(player, 5, 20, 1747, 2509);
			return true;
		case 139:// tan x.
			player.getInterfaceState().openEnterAmountInterface(324, 2509);
			/*
			 * player.getActionSender().sendAmountRequest();
			 * player.setTemporaryAttribute("tanXDeletedItem", 1747);
			 * player.setTemporaryAttribute("tanXAddedItem", 2509);
			 * player.setTemporaryAttribute("tanXPayment", 20);
			 */
			return true;
		case 131:// tan all.
			tan(player, player.getInventory().getCount(1747), 20, 1747, 2509);
			return true;
		}
		return false;
	}

	/**
	 * Call this event when you want to spin.
	 */
	private static class LeatherCraftingAction extends Action {
		public LeatherCraftingAction(Player player, int amount, int leatherId,
				int leatherAmount, int itemToRecieve, double xpToReceive) {
			super(player, 0);
			this.leather = new Item(leatherId, leatherAmount);
			this.itemToRecieve = new Item(itemToRecieve);
			this.exp = xpToReceive;
			this.amount = amount;
		}

		@Override
		public void execute() {
			if (getPlayer().isDead()) {
				this.stop();
				return;
			}
			if (!getPlayer().getInventory().contains(THREAD)) {
				getPlayer().getActionSender().sendMessage(
						"You ran out of thread.");
				this.stop();
				return;
			}
			if (!getPlayer().getInventory().contains(leather)) {
				getPlayer().getActionSender().sendMessage(
						"You don't have enough hides.");
				this.stop();
				return;
			}
			getPlayer().playAnimation(LEATHER_CRAFTING);
			/**
			 * Deletes one piece of thread randomly.
			 */
			if (r.nextInt(3) == 0) {
				getPlayer().getInventory().remove(THREAD);
			}
			getPlayer().getInventory().remove(leather);
			getPlayer().getInventory().add(itemToRecieve);
			getPlayer().getSkills().addExperience(Skills.CRAFTING, exp);
			this.setDelay(2200);
			if (--amount <= 0) {
				this.stop();
			}
		}

		private int amount;
		private final Item leather;
		private final Item itemToRecieve;
		private final double exp;

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
	 * Call this event when you want to spin.
	 */
	private static class SpinningAction extends Action {

		private static final Animation SPINNING_EMOTE = Animation.create(894);

		public SpinningAction(Player player, int amount, int itemToDelete,
				int itemToRecieve, double xpToReceive) {
			super(player, 0);
			this.player = player;
			this.itemToDelete = new Item(itemToDelete);
			this.itemToRecieve = new Item(itemToRecieve);
			this.exp = xpToReceive;
			this.amount = amount;
		}

		@Override
		public void execute() {
			if (player == null) {
				this.stop();
				return;
			}
			if (!player.getInventory().contains(itemToDelete)
					|| player.isDead() || amount <= 0) {
				this.stop();
				return;
			}
			/**
			 * If player is done with his crafting amount, then the Event will
			 * stop. Same if the player ran out of rawMaterial.
			 */
			if (!player.getInventory().contains(itemToDelete) || amount <= 0) {
				this.stop();
				return;
			}
			player.playAnimation(SPINNING_EMOTE);
			player.getInventory().remove(itemToDelete, 1);
			player.getInventory().add(itemToRecieve);
			player.getSkills().addExperience(12, exp);
			this.setDelay(2200);
			if (--amount <= 0) {
				this.stop();
			}

		}

		private final Player player;
		private int amount;
		private final Item itemToDelete;
		private final Item itemToRecieve;
		private final double exp;

		@Override
		public QueuePolicy getQueuePolicy() {
			return QueuePolicy.NEVER;
		}

		@Override
		public WalkablePolicy getWalkablePolicy() {
			return WalkablePolicy.NON_WALKABLE;
		}
	}

	private static int[][] GEM_INFORMATION = { { 1629, 1613, 16, 25, 892 },
			{ 1627, 1611, 13, 20, 891 }, { 1625, 1609, 1, 15, 890 },
			{ 1623, 1607, 20, 50, 889 }, { 1621, 1605, 27, 67, 888 },
			{ 2619, 1603, 34, 85, 887 }, { 2617, 1601, 43, 107, 886 },
			{ 2631, 1615, 55, 137, 885 }, { 6571, 6573, 67, 167, 2717 } };

	public static boolean handleItemOnItem(Player player, int itemUsed,
			int usedWith) {
		for (int h = 0; h < GEM_INFORMATION.length; h++) {
			if ((itemUsed == 1755 && usedWith == GEM_INFORMATION[h][0])
					|| (usedWith == 1755 && itemUsed == GEM_INFORMATION[h][0])) {
				if (player.getSkills().getLevel(Skills.CRAFTING) < GEM_INFORMATION[h][2]) {
					player.getActionSender().sendMessage(
							"You need a level of " + GEM_INFORMATION[h][2]
									+ " to craft that!");
					break;
				}
				player.getActionSender().sendMessage("You craft the gem.");
				player.playAnimation(Animation.create(GEM_INFORMATION[h][4]));
				player.getInventory().remove(new Item(GEM_INFORMATION[h][0])); // Deletes
																				// the
																				// neededgem
				player.getInventory().add(new Item(GEM_INFORMATION[h][1], 1)); // Gives
																				// item
				player.getSkills().addExperience(Skills.CRAFTING,
						GEM_INFORMATION[h][3]); // Gives EXP
				break;
			}
		}
		/*
		 * Soft leather.
		 */
		if (itemUsed == 1733 && usedWith == 1741 || usedWith == 1733
				&& itemUsed == 1741) {
			player.resetSkilling();
			player.getCraftingVariables().setCrafting(true);
			player.getActionSender().sendInterface(154);
			return true;
		}
		/*
		 * Hard leather.
		 */
		if (itemUsed == 1733 && usedWith == 1743 || usedWith == 1733
				&& itemUsed == 1743) {
			player.resetSkilling();
			player.getCraftingVariables().setCrafting(true);
			player.getActionSender().sendChatboxInterface(309);
			player.getActionSender().sendInterfaceModel(309, 2, 200, 1131);
			return true;
		}
		/*
		 * Green dhide leather.
		 */
		if (itemUsed == 1733 && usedWith == 1745 || usedWith == 1733
				&& itemUsed == 1745) {
			player.resetSkilling();
			player.getCraftingVariables().setCrafting(true);
			player.getActionSender().sendChatboxInterface(304);
			player.getActionSender().sendInterfaceModel(304, 2, 200, 1135);// Body
			player.getActionSender().sendInterfaceModel(304, 3, 200, 1065);// Vamps
			player.getActionSender().sendInterfaceModel(304, 4, 200, 1099);// Chaps
			player.getCraftingVariables().setLeatherType(
					DragonLeatherType.GREEN_DRAGON_LEATHER);
			return true;
		}
		/*
		 * Blue dhide leather.
		 */
		if (itemUsed == 1733 && usedWith == 2505 || usedWith == 1733
				&& itemUsed == 2505) {
			player.resetSkilling();
			player.getCraftingVariables().setCrafting(true);
			player.getActionSender().sendChatboxInterface(304);
			player.getActionSender().sendInterfaceModel(304, 2, 200, 2499);// Body
			player.getActionSender().sendInterfaceModel(304, 3, 200, 2487);// Vamps
			player.getActionSender().sendInterfaceModel(304, 4, 200, 2493);// Chaps
			player.getCraftingVariables().setLeatherType(
					DragonLeatherType.BLUE_DRAGON_LEATHER);
			return true;
		}
		/*
		 * Red dhide leather.
		 */
		if (itemUsed == 1733 && usedWith == 2507 || usedWith == 1733
				&& itemUsed == 2507) {
			player.resetSkilling();
			player.getCraftingVariables().setCrafting(true);
			player.getActionSender().sendChatboxInterface(304);
			player.getActionSender().sendInterfaceModel(304, 2, 200, 2501);// Body
			player.getActionSender().sendInterfaceModel(304, 3, 200, 2489);// Vamps
			player.getActionSender().sendInterfaceModel(304, 4, 200, 2495);// Chaps
			player.getCraftingVariables().setLeatherType(
					DragonLeatherType.RED_DRAGON_LEATHER);
			return true;
		}
		/*
		 * Black dhide leather.
		 */
		if (itemUsed == 1733 && usedWith == 2509 || usedWith == 1733
				&& itemUsed == 2509) {
			player.resetSkilling();
			player.getCraftingVariables().setCrafting(true);
			player.getActionSender().sendChatboxInterface(304);
			player.getActionSender().sendInterfaceModel(304, 2, 200, 2503);// Body
			player.getActionSender().sendInterfaceModel(304, 3, 200, 2491);// Vamps
			player.getActionSender().sendInterfaceModel(304, 4, 200, 2497);// Chaps
			player.getCraftingVariables().setLeatherType(
					DragonLeatherType.BLACK_DRAGON_LEATHER);
			return true;
		}
		return false;
	}

}
