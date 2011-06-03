package org.hyperion.rs2.content.skills;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.container.Inventory;

public class Fletching {

	private static class FletchingAction extends Action {

		private static final Animation FLETCHING_ANIMATION = Animation
				.create(1248);

		/**
		 * Log - unstrung bow event.
		 * 
		 * @param xpToReceive
		 * @param itemToRecieve
		 * @param itemToDelete
		 * @param amount
		 */
		public FletchingAction(Player player, int amount, int itemToDelete,
				int itemToRecieve, double exp) {
			super(player, 0);
			this.itemToDelete = new Item(itemToDelete);
			this.itemToRecieve = new Item(itemToRecieve);
			this.exp = exp;
			this.amount = amount;
		}

		@Override
		public void execute() {
			System.out.println("Executing");
			if (getPlayer() == null) {
				this.stop();
				return;
			}
			if (!getPlayer().getInventory().contains(itemToDelete)
					|| getPlayer().isDead() || amount <= 0) {
				this.stop();
				return;
			}
			getPlayer().playAnimation(FLETCHING_ANIMATION);
			getPlayer().getInventory().remove(itemToDelete);
			getPlayer()
					.getInventory()
					.add(itemToRecieve.setCount(itemToRecieve.getId() == 52 ? 15
							: 1));
			getPlayer().getSkills().addExperience(9, exp);
			this.setDelay(2200);
			if (--amount <= 0) {
				this.stop();
			}

		}

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

	/**
	 * Should make the ChatboxInterfacePacketHandler ALOT cleaner.
	 * 
	 * @Author Brown.
	 * */
	public static void fletchLogs(Player player, int levelReq, int amount,
			int itemToDelete, int itemToRecieve, double xpToReceive) {
		player.getActionSender().sendCloseInterface();
		if (player.getSkills().getLevel(Skills.FLETCHING) >= levelReq) {
			player.resetSkilling();
			player.getFletchingVariables().setFletching(true);
			player.getActionQueue().addAction(
					new FletchingAction(player, amount, itemToDelete,
							itemToRecieve, xpToReceive));
			// appendLogFletchingEngine(player, amount, itemToDelete,
			// itemToRecieve, xpToReceive);
		} else {
			Item item = new Item(itemToRecieve);
			player.getActionSender().sendMessage(
					"You need a Fletching level of " + levelReq
							+ " in order to fletch a "
							+ item.getDefinition().getName().toLowerCase()
							+ ".");
		}
	}

	public static void stringBows(Player player, int levelReq,
			int itemToDelete, int itemToRecieve, double xpToReceive) {
		player.getActionSender().sendCloseInterface();
		if (player.getSkills().getLevel(Skills.FLETCHING) >= levelReq) {
			player.getInventory().remove(new Item(1777));
			player.getInventory().remove(new Item(itemToDelete));
			player.getInventory().add(new Item(itemToRecieve));
			player.getSkills().addExperience(9, xpToReceive);
			player.getActionSender().sendMessage(
					"You successfully string the "
							+ ItemDefinition.forId(itemToRecieve).getName()
							+ ".");
		} else {
			Item item = new Item(itemToRecieve);
			player.getActionSender().sendMessage(
					"You need a Fletching level of " + levelReq
							+ " in order to string a "
							+ item.getDefinition().getName().toLowerCase()
							+ ".");
		}
	}

	public static void fletchArrows(Player player, int levelReq,
			int itemToDelete, int itemToReceive, double xpToGain) {
		player.getActionSender().sendCloseInterface();
		if (player.getSkills().getLevel(Skills.FLETCHING) >= levelReq) {
			int amount = 15;
			int count1 = player.getInventory().getCount(53);// Headless arrows.
			int count2 = player.getInventory().getCount(itemToDelete); // Arrow
																		// tips.
			if (count1 < amount) {
				amount = count1;
			}
			if (count2 < amount) {
				amount = count2;
			}
			if (Inventory.addInventoryItem(player, new Item(itemToReceive,
					amount))) {
				player.getInventory().remove(new Item(53, amount));
				player.getInventory().remove(new Item(itemToDelete, amount));
				player.getSkills().addExperience(9, xpToGain);
			}

		} else {
			player.getActionSender().sendMessage(
					"You need a Fletching level of "
							+ levelReq
							+ " in order to fletch "
							+ new Item(itemToReceive).getDefinition().getName()
									.toLowerCase() + "'s.");
		}
	}

	public enum LogFletchingType {
		LOGS, OAK_LOGS, WILLOW_LOGS, MAPLE_LOGS, YEW_LOGS, MAGIC_LOGS,
	}

	/**
	 * Redo this using arrays whenever you feel like making the world a better
	 * place.
	 */
	public static boolean handleItemOnItem(Player player, int itemUsed,
			int usedWith) {
		/**
		 * Initializes which type of fletching we're dealing with, opens the
		 * correct interface + Sends the correct items on interfaces. Fletching
		 * type 1: Logs. Fletching type 2: Oak logs. Fletching type 3: Willow
		 * logs. Fletching type 4: Maple logs. Fletching type 5: Yew logs.
		 * Fletching type 6: Magic logs.
		 * */
		if (itemUsed == 946 && usedWith == 1511 || itemUsed == 1511
				&& usedWith == 946) {
			if (player.getSkills().getLevel(9) >= 1) {
				player.resetSkilling();
				player.getFletchingVariables().setFletching(true);
				player.getFletchingVariables().setFletchingType(
						LogFletchingType.LOGS);
				player.getActionSender().sendCloseInterface();
				player.getActionSender().sendChatboxInterface(304);
				player.getActionSender().sendInterfaceModel(304, 2, 175, 52);
				player.getActionSender().sendInterfaceModel(304, 3, 175, 50);
				player.getActionSender().sendInterfaceModel(304, 4, 175, 48);
				return true;
			} else {
				player.getActionSender()
						.sendMessage(
								"You need a Fletching level of at least 1, in order to fletch logs.");
				return true;
			}
		}
		if (itemUsed == 946 && usedWith == 1521 || itemUsed == 1521
				&& usedWith == 946) {
			if (player.getSkills().getLevel(9) >= 20) {
				player.resetSkilling();
				player.getFletchingVariables().setFletching(true);
				player.getFletchingVariables().setFletchingType(
						LogFletchingType.OAK_LOGS);
				player.getActionSender().sendCloseInterface();
				player.getActionSender().sendChatboxInterface(303);
				player.getActionSender().sendInterfaceModel(303, 2, 175, 54);
				player.getActionSender().sendInterfaceModel(303, 3, 175, 56);
				return true;
			} else {
				player.getActionSender()
						.sendMessage(
								"You need a Fletching level of at least 20, in order to fletch oak logs.");
				return true;
			}
		}
		if (itemUsed == 946 && usedWith == 1519 || itemUsed == 1519
				&& usedWith == 946) {
			if (player.getSkills().getLevel(9) >= 35) {
				player.resetSkilling();
				player.getFletchingVariables().setFletching(true);
				player.getFletchingVariables().setFletchingType(
						LogFletchingType.WILLOW_LOGS);
				player.getActionSender().sendCloseInterface();
				player.getActionSender().sendChatboxInterface(303);
				player.getActionSender().sendInterfaceModel(303, 2, 175, 60);
				player.getActionSender().sendInterfaceModel(303, 3, 175, 58);
				return true;
			} else {
				player.getActionSender()
						.sendMessage(
								"You need a Fletching level of at least 35, in order to fletch willow logs.");
				return true;
			}
		}
		if (itemUsed == 946 && usedWith == 1517 || itemUsed == 1517
				&& usedWith == 946) {
			if (player.getSkills().getLevel(9) >= 50) {
				player.resetSkilling();
				player.getFletchingVariables().setFletching(true);
				player.getFletchingVariables().setFletchingType(
						LogFletchingType.MAPLE_LOGS);
				player.getActionSender().sendCloseInterface();
				player.getActionSender().sendChatboxInterface(303);
				player.getActionSender().sendInterfaceModel(303, 2, 175, 64);
				player.getActionSender().sendInterfaceModel(303, 3, 175, 62);
				return true;
			} else {
				player.getActionSender()
						.sendMessage(
								"You need a Fletching level of at least 50, in order to fletch maple logs.");
				return true;
			}
		}
		if (itemUsed == 946 && usedWith == 1515 || itemUsed == 1515
				&& usedWith == 946) {
			if (player.getSkills().getLevel(9) >= 65) {
				player.resetSkilling();
				player.getFletchingVariables().setFletching(true);
				player.getFletchingVariables().setFletchingType(
						LogFletchingType.YEW_LOGS);
				player.getActionSender().sendCloseInterface();
				player.getActionSender().sendChatboxInterface(303);
				player.getActionSender().sendInterfaceModel(303, 2, 175, 68);
				player.getActionSender().sendInterfaceModel(303, 3, 175, 66);
				return true;
			} else {
				player.getActionSender()
						.sendMessage(
								"You need a Fletching level of at least 65, in order to fletch yew logs.");
				return true;
			}
		}
		if (itemUsed == 946 && usedWith == 1513 || itemUsed == 1513
				&& usedWith == 946) {
			if (player.getSkills().getLevel(9) >= 80) {
				player.resetSkilling();
				player.getFletchingVariables().setFletching(true);
				player.getFletchingVariables().setFletchingType(
						LogFletchingType.MAGIC_LOGS);
				player.getActionSender().sendCloseInterface();
				player.getActionSender().sendChatboxInterface(303);
				player.getActionSender().sendInterfaceModel(303, 2, 175, 72);
				player.getActionSender().sendInterfaceModel(303, 3, 175, 70);
				return true;
			} else {
				player.getActionSender()
						.sendMessage(
								"You need a Fletching level of at least 80, in order to fletch magic logs.");
				return true;
			}
		}
		/**
		 * Bow stringing.
		 */
		/*
		 * Normal log short bows.
		 */
		if (itemUsed == 1777 && usedWith == 50 || usedWith == 1777
				&& itemUsed == 50) {
			Fletching.stringBows(player, 5, 50, 841, 5);
			return true;
		}
		/*
		 * Normal log long bows.
		 */
		if (itemUsed == 1777 && usedWith == 48 || usedWith == 1777
				&& itemUsed == 48) {
			Fletching.stringBows(player, 10, 48, 839, 10);
			return true;
		}
		/*
		 * Oak short bows.
		 */
		if (itemUsed == 1777 && usedWith == 54 || usedWith == 1777
				&& itemUsed == 54) {
			Fletching.stringBows(player, 20, 54, 843, 16.5);
			return true;
		}
		/*
		 * Oak long bows.
		 */
		if (itemUsed == 1777 && usedWith == 56 || usedWith == 1777
				&& itemUsed == 56) {
			Fletching.stringBows(player, 25, 56, 845, 25);
			return true;
		}
		/*
		 * Willow short bows.
		 */
		if (itemUsed == 1777 && usedWith == 60 || usedWith == 1777
				&& itemUsed == 60) {
			Fletching.stringBows(player, 35, 60, 849, 33.25);
			return true;
		}
		/*
		 * Willow long bows.
		 */
		if (itemUsed == 1777 && usedWith == 58 || usedWith == 1777
				&& itemUsed == 58) {
			Fletching.stringBows(player, 40, 58, 847, 41.5);
			return true;
		}
		/*
		 * Maple short bows.
		 */
		if (itemUsed == 1777 && usedWith == 62 || usedWith == 1777
				&& itemUsed == 62) {
			Fletching.stringBows(player, 50, 62, 853, 50);
			return true;
		}
		/*
		 * Maple long bows.
		 */
		if (itemUsed == 1777 && usedWith == 64 || usedWith == 1777
				&& itemUsed == 64) {
			Fletching.stringBows(player, 55, 64, 851, 58.25);
			return true;
		}
		/*
		 * Yew short bows.
		 */
		if (itemUsed == 1777 && usedWith == 68 || usedWith == 1777
				&& itemUsed == 68) {
			Fletching.stringBows(player, 65, 68, 857, 67.5);
			return true;
		}
		/*
		 * Yew long bows.
		 */
		if (itemUsed == 1777 && usedWith == 66 || usedWith == 1777
				&& itemUsed == 66) {
			Fletching.stringBows(player, 70, 66, 855, 75);
			return true;
		}
		/*
		 * Magic short bows.
		 */
		if (itemUsed == 1777 && usedWith == 72 || usedWith == 1777
				&& itemUsed == 72) {
			Fletching.stringBows(player, 80, 72, 861, 83.25);
			return true;
		}
		/*
		 * Magic long bows.
		 */
		if (itemUsed == 1777 && usedWith == 70 || usedWith == 1777
				&& itemUsed == 70) {
			Fletching.stringBows(player, 85, 70, 859, 91.5);
			return true;
		}
		/**
		 * Arrow fletching..
		 */
		/*
		 * Headless arrows.
		 */
		if (itemUsed == 52 && usedWith == 314 || usedWith == 52
				&& itemUsed == 314) {
			if (player.getSkills().getLevel(Skills.FLETCHING) >= 1) {
				int amount = 15;
				int count1 = player.getInventory().getCount(52);
				int count2 = player.getInventory().getCount(314);
				if (count1 < amount) {
					amount = count1;
				}
				if (count2 < amount) {
					amount = count2;
				}
				if (Inventory.addInventoryItem(player, new Item(53, amount))) {
					player.getInventory().remove(new Item(52, amount));
					player.getInventory().remove(new Item(314, amount));
					player.getSkills().addExperience(9, amount);
				}
				return true;
			} else {
				player.getActionSender()
						.sendMessage(
								"You need a Fletching level of at least 1 in order to fletch headless arrows.");
				return true;
			}
		}
		/*
		 * Bronze arrows.
		 */
		if (itemUsed == 53 && usedWith == 39 || usedWith == 53
				&& itemUsed == 39) {
			Fletching.fletchArrows(player, 1, 39, 882, 39.5);
			return true;
		}
		/*
		 * Iron arrows.
		 */
		if (itemUsed == 53 && usedWith == 40 || usedWith == 53
				&& itemUsed == 40) {
			Fletching.fletchArrows(player, 15, 40, 884, 57.5);
			return true;
		}
		/*
		 * Steel arrows.
		 */
		if (itemUsed == 53 && usedWith == 41 || usedWith == 53
				&& itemUsed == 41) {
			Fletching.fletchArrows(player, 30, 41, 886, 95);
			return true;
		}
		/*
		 * Mithril arrows.
		 */
		if (itemUsed == 53 && usedWith == 42 || usedWith == 53
				&& itemUsed == 42) {
			Fletching.fletchArrows(player, 45, 42, 888, 132);
			return true;
		}
		/*
		 * Adamant arrows.
		 */
		if (itemUsed == 53 && usedWith == 43 || usedWith == 53
				&& itemUsed == 43) {
			Fletching.fletchArrows(player, 60, 43, 890, 169.5);
			return true;
		}
		/*
		 * Rune arrows.
		 */
		if (itemUsed == 53 && usedWith == 44 || usedWith == 53
				&& itemUsed == 44) {
			Fletching.fletchArrows(player, 75, 44, 892, 207.4);
			return true;
		}
		// TODO: Dragon arrows.
		return false;
	}

	/**
	 * Redo this using arrays whenever you feel like making the world a better
	 * place.
	 */
	public static boolean handleChatboxInterface(int interfaceId,
			Player player, int childClicked) {
		switch (interfaceId) {
		case 303:
			switch (childClicked) {
			/*
			 * Create 1 longbow.
			 */
			case 11:
				System.out.println(player.getFletchingVariables()
						.getFletchingType());
				switch (player.getFletchingVariables().getFletchingType()) {
				case OAK_LOGS:
					Fletching.fletchLogs(player, 20, 1, 1521, 56, 25);
					return true;
				case WILLOW_LOGS:
					Fletching.fletchLogs(player, 35, 1, 1519, 58, 41.5);
					return true;
				case MAPLE_LOGS:
					Fletching.fletchLogs(player, 50, 1, 1517, 62, 58.25);
					return true;
				case YEW_LOGS:
					Fletching.fletchLogs(player, 65, 1, 1515, 66, 75);
					return true;
				case MAGIC_LOGS:
					Fletching.fletchLogs(player, 80, 1, 1513, 70, 91.5);
					return true;
				}
				break;
			/*
			 * Create 5 longbows.
			 */
			case 10:
				switch (player.getFletchingVariables().getFletchingType()) {
				case OAK_LOGS:
					Fletching.fletchLogs(player, 20, 5, 1521, 56, 25);
					return true;
				case WILLOW_LOGS:
					Fletching.fletchLogs(player, 35, 5, 1519, 58, 41.5);
					return true;
				case MAPLE_LOGS:
					Fletching.fletchLogs(player, 50, 5, 1517, 62, 58.25);
					return true;
				case YEW_LOGS:
					Fletching.fletchLogs(player, 65, 5, 1515, 66, 75);
					return true;
				case MAGIC_LOGS:
					Fletching.fletchLogs(player, 80, 5, 1513, 70, 91.5);
					return true;
				}
				break;
			/*
			 * Create 10 longbows.
			 */
			case 9:
				switch (player.getFletchingVariables().getFletchingType()) {
				case OAK_LOGS:
					Fletching.fletchLogs(player, 20, 10, 1521, 56, 25);
					return true;
				case WILLOW_LOGS:
					Fletching.fletchLogs(player, 35, 10, 1519, 58, 41.5);
					return true;
				case MAPLE_LOGS:
					Fletching.fletchLogs(player, 50, 10, 1517, 62, 58.25);
					return true;
				case YEW_LOGS:
					Fletching.fletchLogs(player, 65, 10, 1515, 66, 75);
					return true;
				case MAGIC_LOGS:
					Fletching.fletchLogs(player, 80, 10, 1513, 70, 91.5);
					return true;
				}
				break;
			/*
			 * Create x longbows (doing 27 for now).
			 */
			case 8:
				player.getInterfaceState().openEnterAmountInterface(303, 1);
				return true;
				/*
				 * Create one shortbow.
				 */
			case 7:
				switch (player.getFletchingVariables().getFletchingType()) {
				case OAK_LOGS:
					Fletching.fletchLogs(player, 20, 1, 1521, 54, 16.5);
					return true;
				case WILLOW_LOGS:
					Fletching.fletchLogs(player, 35, 1, 1519, 60, 33.25);
					return true;
				case MAPLE_LOGS:
					Fletching.fletchLogs(player, 50, 1, 1517, 64, 50);
					return true;
				case YEW_LOGS:
					Fletching.fletchLogs(player, 65, 1, 1515, 68, 67.5);
					return true;
				case MAGIC_LOGS:
					Fletching.fletchLogs(player, 80, 1, 1513, 72, 83.25);
					return true;
				}
				break;
			/*
			 * Create 5 shortbows.
			 */
			case 6:
				switch (player.getFletchingVariables().getFletchingType()) {
				case OAK_LOGS:
					Fletching.fletchLogs(player, 20, 5, 1521, 54, 16.5);
					return true;
				case WILLOW_LOGS:
					Fletching.fletchLogs(player, 35, 5, 1519, 60, 33.25);
					return true;
				case MAPLE_LOGS:
					Fletching.fletchLogs(player, 50, 5, 1517, 64, 50);
					return true;
				case YEW_LOGS:
					Fletching.fletchLogs(player, 65, 5, 1515, 68, 67.5);
					return true;
				case MAGIC_LOGS:
					Fletching.fletchLogs(player, 80, 5, 1513, 72, 83.25);
					return true;
				}
				break;
			/*
			 * Create 10 shortbows.
			 */
			case 5:
				switch (player.getFletchingVariables().getFletchingType()) {
				case OAK_LOGS:
					Fletching.fletchLogs(player, 20, 10, 1521, 54, 16.5);
					return true;
				case WILLOW_LOGS:
					Fletching.fletchLogs(player, 35, 10, 1519, 60, 33.25);
					return true;
				case MAPLE_LOGS:
					Fletching.fletchLogs(player, 50, 10, 1517, 64, 50);
					return true;
				case YEW_LOGS:
					Fletching.fletchLogs(player, 65, 10, 1515, 68, 67.5);
					return true;
				case MAGIC_LOGS:
					Fletching.fletchLogs(player, 80, 10, 1513, 72, 83.25);
					return true;
				}
				break;
			/*
			 * Create x shortbows.
			 */
			case 4:
				player.getInterfaceState().openEnterAmountInterface(303, 2);
				break;
			}
			break;
		case 304:
			if (player.getFletchingVariables().getFletchingType() == LogFletchingType.LOGS) {
				switch (childClicked) {
				/*
				 * Fletch 1 x 15 arrow shafts.
				 */
				case 8:
					Fletching.fletchLogs(player, 1, 1, 1511, 52, 5);
					break;
				/*
				 * Fletch 5 x 15 arrow shafts.
				 */
				case 7:

					Fletching.fletchLogs(player, 1, 5, 1511, 52, 5);
					break;
				/*
				 * Fletch 10 x 15 arrow shafts.
				 */
				case 6:
					Fletching.fletchLogs(player, 1, 10, 1511, 52, 5);
					break;
				/*
				 * Fletch X x 15 arrow shafts
				 */
				case 5: // Option X
					player.getInterfaceState().openEnterAmountInterface(304, 5);
					break;
				/*
				 * Fletch 1 shortbow(u).
				 */
				case 12:
					Fletching.fletchLogs(player, 5, 1, 1511, 50, 5);
					break;
				/*
				 * Fletch 5 shortbow(u).
				 */
				case 11:
					Fletching.fletchLogs(player, 5, 5, 1511, 50, 5);
					break;
				/*
				 * Fletch 10 shortbow(u).
				 */
				case 10:
					Fletching.fletchLogs(player, 5, 10, 1511, 50, 5);
					break;
				/*
				 * Fletch X shortbow(u). Set to 27 for now.
				 */
				case 9:
					player.getInterfaceState().openEnterAmountInterface(304, 9);
					break;
				/*
				 * Fletch 1 longbow(u).
				 */
				case 16:
					Fletching.fletchLogs(player, 10, 1, 1511, 48, 10);
					break;
				/*
				 * Fletch 5 longbows(u).
				 */
				case 15:
					Fletching.fletchLogs(player, 10, 5, 1511, 48, 10);
					break;
				/*
				 * Fletch 10 longbows(u).
				 */
				case 14:
					Fletching.fletchLogs(player, 10, 10, 1511, 48, 10);
					break;
				/*
				 * Fletch x longbows. This will be 27 for now. :)
				 */
				case 13:
					player.getInterfaceState()
							.openEnterAmountInterface(304, 13);
					break;
				default:
					System.out.println("Unhandled option: " + childClicked
							+ " on interface: " + interfaceId + ".");
				}
			}
			break;
		}
		return false;
	}
}
