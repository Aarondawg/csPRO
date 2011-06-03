package org.hyperion.rs2.packet;

import java.util.Random;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.Drinks;
import org.hyperion.rs2.content.Food;
import org.hyperion.rs2.content.Jewellery;
import org.hyperion.rs2.content.minigames.Barrows;
import org.hyperion.rs2.content.quest.impl.LostCity;
import org.hyperion.rs2.content.quest.impl.TutorialIsland;
import org.hyperion.rs2.content.skills.Cooking;
import org.hyperion.rs2.content.skills.Crafting;
import org.hyperion.rs2.content.skills.Firemaking;
import org.hyperion.rs2.content.skills.Fletching;
import org.hyperion.rs2.content.skills.Herblore;
import org.hyperion.rs2.content.skills.Prayer;
import org.hyperion.rs2.content.skills.Runecrafting;
import org.hyperion.rs2.content.skills.Slayer;
import org.hyperion.rs2.content.skills.Smithing;
import org.hyperion.rs2.content.skills.farming.Farming;
import org.hyperion.rs2.content.traveling.TeleTabs;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.GameObjectDefinition;
import org.hyperion.rs2.model.GroundItemController;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.net.Packet;

public class ItemPacketHandler implements PacketHandler {

	private static final int ITEM_CLICKING = 21;
	private static final int ITEM_ON_FLOORITEM = 22;
	private static final int ITEM_ON_OBJECT = 45;
	private static final int ITEM_ON_ITEM = 23;
	private static final int DROP_ITEM = 237;
	private static final int PICKUP_ITEM = 238;
	private static final int ITEM_RUBBING = 11;
	private static final int ITEM_ON_PLAYER = 30;

	@Override
	public void handle(Player player, Packet packet) {
		player.getActionSender().sendCloseInterface();
		switch (packet.getOpcode()) {
		case ITEM_CLICKING:
			handleItemClicking(player, packet);
			break;
		case ITEM_ON_FLOORITEM:// Light packet!
			player.resetInteractingEntity();
			// handleItemOnFloorItem(player, packet);
			break;
		case ITEM_ON_OBJECT:
			player.resetInteractingEntity();
			handleItemOnObject(player, packet);
			break;
		case ITEM_ON_ITEM:
			handleItemOnItem(player, packet);
			break;
		case DROP_ITEM:
			handleDropItem(player, packet);
			break;
		case PICKUP_ITEM:
			player.resetInteractingEntity();
			handlePickupItem(player, packet);
			break;
		case ITEM_RUBBING:
			handleItemRubbing(player, packet);
			break;
		case ITEM_ON_PLAYER:
			handleItemOnPlayer(player, packet);
			break;
		}

	}

	private static final Random r = new Random();

	private void handleItemOnPlayer(Player player, Packet packet) {
		final int itemId = packet.getLEShortA();
		final int unknown2 = packet.getInt1();
		final int playerIndex = packet.getShort();
		final int itemSlot = packet.getLEShortA();
		final Player target = (Player) World.getWorld().getPlayers()
				.get(playerIndex);
		final Item item = player.getInventory().get(itemSlot);
		if (item == null || item.getId() != itemId || target == null) {
			return;
		}
		switch (itemId) {
		case 962: // Christmas Cracker..
			final int[] partyhats = { 1038, 1040, 1042, 1044, 1046, 1048 };
			Item phat = new Item(partyhats[r.nextInt(partyhats.length)]);
			final int[] runes = { 1079, 1093, 1113, 1127, 1147, 1163, 1185,
					1201, 1213, 1247, 1275, 1289, 1303, 1319, 1333, 1347, 1359,
					1373, 1432 };
			Item rune = new Item(runes[r.nextInt(runes.length)]);
			player.getInventory().remove(item);
			if (r.nextBoolean()) {
				player.getInventory().add(phat);
				player.getActionSender().sendMessage(
						"Aww, better luck next time.");
				target.getInventory().add(rune);
				target.getActionSender().sendMessage("Happy times!");
			} else {
				target.getInventory().add(phat);
				target.getActionSender().sendMessage(
						"Aww, better luck next time.");
				player.getInventory().add(rune);
				player.getActionSender().sendMessage("Happy times!");
			}
			break;
		default:
			System.out.println("Handle item on player request: " + playerIndex
					+ " " + itemId + " " + unknown2 + " " + itemSlot);
		}

	}

	private void handleItemRubbing(Player player, Packet packet) {
		int slot = packet.getLEShortA() & 0xFFFF;
		int interfaceSet = packet.getInt();
		int interfaceId = interfaceSet >> 16;
		int itemId = packet.getLEShort() & 0xFFFF;
		if (slot < 0 || slot >= Inventory.SIZE
				|| player.getInventory().get(slot) == null) {
			return;
		}
		if (player.getInventory().get(slot).getId() != itemId) {
			return;
		}
		if (interfaceId == Inventory.INTERFACE) {
			if (Jewellery.rubItem(player, slot, itemId, false)) {
				return;
			}
		}

	}

	private void handlePickupItem(Player player, Packet packet) {
		final int y = packet.getLEShortA() & 0xFFFF;
		final int x = packet.getLEShortA() & 0xFFFF;
		final int id = packet.getShortA() & 0xFFFF;
		final Location l = Location.create(x, y, player.getLocation().getZ());
		GroundItemController.pickupGroundItem(l, id, player);
	}

	private void handleDropItem(Player player, Packet packet) {
		player.getActionSender().sendCloseInterface();
		int id = packet.getShort();
		int slot = packet.getShortA();
		packet.getInt();
		if (slot < 0 || slot >= Inventory.SIZE) {
			return;
		}
		Item item = player.getInventory().get(slot);
		if (item.getId() != id) {
			return;
		}
		for (int destroyable : Constants.DESTROYABLE_ITEMS) {
			if (destroyable == id) {
				player.getActionSender().sendChatboxInterface(94);
				player.getActionSender().sendString(
						"Clicking yes will permanently delete this item.", 94,
						11);
				player.getActionSender().sendString("", 94, 10);
				player.getActionSender().sendString(
						"If you want to keep this item click no.", 94, 12);
				player.getActionSender().sendString(
						item.getDefinition().getName(), 94, 13);
				player.getActionSender().sendUpdateItem(94, 0, 93, 0, item);
				player.getActionSender().sendInterfaceModel(94, 0, 5, id);
				player.setDestroyingItem(id, slot);
				return;
			}
		}
		GroundItemController.createGroundItem(item, player,
				player.getLocation());
		player.getInventory().set(slot, null);
	}

	private void handleItemOnItem(Player player, Packet packet) {
		int usedWith = packet.getShort();
		int interfaceSet = packet.getLEInt();// InterfaceSet.
		int interfaceId = interfaceSet >> 16;
		int used = packet.getShortA();
		int withSlot = packet.getLEShort();
		packet.getInt1();// no idea
		int slot = packet.getShort();
		if (interfaceId != Inventory.INTERFACE) {
			return;
		}
		if (withSlot < 0 || withSlot >= Inventory.SIZE) {
			return;
		}
		if (slot < 0 || slot >= Inventory.SIZE) {
			return;
		}
		Item itemUsed = player.getInventory().get(slot);
		Item itemUsedWith = player.getInventory().get(withSlot);
		if (itemUsed.getId() != used || itemUsedWith.getId() != usedWith) {
			return;
		}
		if (usedWith == 590 || used == 590) { // Tinderbox.
			if (Firemaking.parseItemOnItemIds(player, used, usedWith, withSlot,
					slot)) {
				return;
			}
		}
		if (usedWith == 1511 || used == 1511) {// Normal log to be coated.
			if (Firemaking.parseCoatingIds(player, used, usedWith, withSlot,
					slot)) {
				return;
			}
		}
		/*
		 * Making a dramen staff.
		 */
		if (used == 771 && usedWith == 946 || usedWith == 771 && used == 946) {
			int questStage = player.getQuestInfo()[LostCity.QUEST_INFO_INDEX][LostCity.MAIN_QUEST_STAGE_INDEX];
			if (questStage >= 3) {
				player.getInventory().replace(771, 772);
				player.getActionSender().sendMessage(
						"You carve the branch into a staff.");
			} else {
				player.getActionSender().sendMessage("Why would I do that?");
			}
			return;
		}
		if (Cooking.checkForItemOnItem(player, itemUsed, itemUsedWith, slot,
				withSlot)) {
			return;
		}
		if (Fletching.handleItemOnItem(player, used, usedWith)) {
			return;
		}
		if (Crafting.handleItemOnItem(player, used, usedWith)) {
			return;
		}
		if (Herblore.handleItemOnItem(player, used, usedWith)) {
			return;
		}
		if (used == 7156 && usedWith == 1511 || usedWith == 7156
				&& used == 1511) {
			TutorialIsland.creaseFire(player);
		}

	}

	private void handleItemOnObject(final Player player, Packet packet) {
		final int objectId = packet.getLEShortA();
		final int objectY = packet.getShortA() & 0xFFFF;
		final int itemId = packet.getShortA();
		final int slot = packet.getShortA();
		final int objectX = packet.getShort();
		packet.getInt2(); // Hopefully junk.
		final Location loc = Location.create(objectX, objectY,
				player.getLocation().getZ()).getActualLocation(
				GameObjectDefinition.forId(objectId).getBiggestSize());
		final Item item = player.getInventory().get(slot);
		if (item.getId() != itemId) {
			return;
		}
		System.out.println("Using item " + itemId + " on object id: "
				+ objectId + loc);
		player.face(loc);
		player.getActionQueue().addAction(new Action(player, 0) {

			@Override
			public QueuePolicy getQueuePolicy() {
				return QueuePolicy.NEVER;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.NON_WALKABLE;
			}

			@Override
			public void execute() {
				if (player.getLocation().withinRange(loc,
						GameObjectDefinition.forId(objectId).getBiggestSize())
						&& player.getInterfaceState().getCurrentInterface() == -1) {
					this.stop();
					if (Cooking.checkForItemOnObject(player, itemId, slot,
							objectId)) {
						return;
					}
					if (Runecrafting.talismanTele(player, itemId, objectId,
							objectX, objectY)) {
						return;
					}
					if (Farming.handleItemOnObject(player, loc, objectId, item)) {
						return;
					}
					if (player.getWarriorsGuild().handleItemOnObject(item,
							objectId, loc)) {
						return;
					}
					if (objectId == 2732 && itemId == 2514) {
						TutorialIsland.cook(player, true);
					}
					switch (objectId) {
					/*
					 * Anvils..
					 */
					case 2782:
					case 2783:
					case 4306:
					case 6150:
						switch (itemId) {
						case 2349:// Bronze.
							player.getActionSender().sendInterface(312);
							Smithing.initializeBronzeItems(player);
							break;
						case 2351:// Iron
							player.getActionSender().sendInterface(312);
							Smithing.initializeIronItems(player);
							break;
						case 2353:// Steel
							player.getActionSender().sendInterface(312);
							Smithing.initializeSteelItems(player);
							break;
						case 2359:// Mithril
							player.getActionSender().sendInterface(312);
							Smithing.initializeMithrilItems(player);
							break;
						case 2361:// Adamant
							player.getActionSender().sendInterface(312);
							Smithing.initializeAdamantItems(player);
							break;
						case 2363:// Rune
							player.getActionSender().sendInterface(312);
							Smithing.initializeRuneItems(player);
							break;
						}
						break;
					default:
						player.getActionSender().sendMessage(
								"Nothing interesting happens.");
						break;
					}
					this.stop();
				}
				this.setDelay(600);
			}

		});

	}

	private void handleItemOnFloorItem(Player player, Packet packet) {
		int invItemId = packet.getLEShort();
		int floItemY = packet.getShort();
		packet.getInt2(); // Couldn't get anything usefull out of this one.
		int floItemX = packet.getShortA();
		int floItemId = packet.getLEShort();
		int invItemSlot = packet.getLEShort();

	}

	private static final Animation SPADE = Animation.create(831);
	private static final Animation RESET = Animation.create(-1);

	private void handleItemClicking(final Player player, Packet packet) {
		int itemId = packet.getLEShort() & 0xFFFF;
		int interfaceSet = packet.getLEInt();
		int interfaceId = interfaceSet >> 16;
		int slot = packet.getShort() & 0xFFFF;
		if (slot < 0 || slot >= Inventory.SIZE
				|| player.getInventory().get(slot) == null) {
			return;
		}
		Item item = player.getInventory().get(slot);
		if (item.getId() != itemId)
			return;
		switch (interfaceId) {
		case Inventory.INTERFACE:// Regular inventory interface id.
			if (itemId == 4155) { // Slayer gem.
				Slayer.handleSlayerGem(player);
				return;
			}
			if (itemId == 952 || itemId == 953) { // Space.
				player.playAnimation(SPADE);
				World.getWorld().submit(new Event(600) {

					@Override
					public void execute() {
						player.playAnimation(RESET);
						if (Barrows.digMound(player)) {
							this.stop();
							return;
						}
						// TODO: Treasure trails.
						player.getActionSender().sendMessage(
								"Nothing interesting happens.");
						this.stop();
					}

				});
			}

			if (Food.eat(player, item, slot)) {
				return;
			}
			if (Herblore.handleItemSelect(player, item, slot)) {
				return;
			}
			if (Drinks.getPotions(player, itemId, slot)) {
				return;
			}
			if (Prayer.bury(player, itemId, slot)) {
				return;
			}
			if (TeleTabs.handle(player, item)) {
				return;
			}
			break;
		}
	}

}
