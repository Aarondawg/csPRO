package org.hyperion.rs2.net;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.content.quest.QuestHandler;
import org.hyperion.rs2.content.quest.impl.TutorialIsland;
import org.hyperion.rs2.content.skills.Cooking;
import org.hyperion.rs2.event.impl.DeathEvent;
import org.hyperion.rs2.model.Bonuses;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.GroundItem;
import org.hyperion.rs2.model.GroundItemController;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Palette;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Palette.PaletteTile;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.container.impl.EquipmentContainerListener;
import org.hyperion.rs2.model.container.impl.InterfaceContainerListener;
import org.hyperion.rs2.model.container.impl.WeaponContainerListener;
import org.hyperion.rs2.net.Packet.Type;
import org.hyperion.rs2.util.TextUtils;

/**
 * A utility class for sending packets.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ActionSender {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Creates an action sender for the specified player.
	 * 
	 * @param player
	 *            The player to create the action sender for.
	 */
	public ActionSender(Player player) {
		this.player = player;
	}

	/**
	 * Sends an inventory interface.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param inventoryInterfaceId
	 *            The inventory interface id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInventoryInterface(int interfaceId,
			int inventoryInterfaceId) {
		player.getInterfaceState().interfaceOpened(interfaceId);
		player.write(new PacketBuilder(171).putShortA(interfaceId).putShortA(
				inventoryInterfaceId).toPacket());
		return this;
	}

	/**
	 * Sends a message to the client with the distance we want to follow from in
	 * ;).
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendFollowingDistance(int distance) {
		sendMessage("FOLLOWING_HACK: " + distance);
		return this;
	}

	public ActionSender sendStopFollowing(boolean force) {
		sendMessage("FOLLOWING_HACK: STOP " + (force ? "FORCE" : ""));
		return this;
	}

	public ActionSender sendFollowing(Entity target) {
		String type = target instanceof Player ? "P" : "N";
		sendMessage("FOLLOWING_HACK: " + type + " " + target.getIndex());
		return this;
	}

	/**
	 * 2 = Compass 3 = Map 5 = both
	 */
	public void sendMinimapBlackout(int int1) {// With 1, nothing happends, with
		// 500, the compass is black.
		PacketBuilder spb = new PacketBuilder(238).put((byte) int1);
		player.getSession().write(spb.toPacket());
	}

	/**
	 * Starts a rather big earth quake. (Change the 10's to make it less bouncy)
	 */
	public ActionSender sendEarthQuake() {
		PacketBuilder spb = new PacketBuilder(76).put((byte) 1).put((byte) 10)
				.put((byte) 10).put((byte) 10);
		player.getSession().write(spb.toPacket());
		return this;

	}

	/**
	 * Stops the current earth quake.
	 */
	public ActionSender sendStopEarthQuake() {
		player.getSession().write(new PacketBuilder(202).toPacket());
		return this;

	}

	/**
	 * Sends all the login packets.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendLogin() {
		player.setActive(true);

		/*
		 * If we're in an area we can't really be in, such as a certain mini
		 * game or a quest area, we get a new area.
		 */
		Location newArea = player.getLocation().getNewArea(player);

		if (newArea != null) {
			player.setLocation(newArea);
		}

		if (player.getHitpoints() == 0) {
			player.setDead(true);
			World.getWorld().submit(new DeathEvent(player));
		}
		sendMapRegion();

		/*
		 * Palette pal = new Palette(); for(int z = 0; z < 4; z++) { for(int x =
		 * 0; x < 13; x++) { for(int y = 0; y < 13; y++) { PaletteTile tile =
		 * new PaletteTile(3222 / 8, 3222 / 8); pal.setTile(x, y, z, tile); } }
		 * } sendConstructMapRegion(pal, Location.create(4000, 4000, 0));
		 */
		//Quest tutorialIsland = QuestHandler.getQuest(0);

		sendMessage("Welcome to csPRO.");
		//if (tutorialIsland.isFinished(player)) {
			sendSidebarInterfaces();
		//}

		sendWelcomeScreen(378, 20);

		sendInteractionOption("Trade with", 3, false);
		sendInteractionOption("Follow", 2, false);
		sendSkills();
		PestControl.checkBoatLogin(player);
		player.getSpecials().refresh();
		player.getFriends().refresh();
		player.getSettings().refresh(player);

		if (!player.getSettings().hasReceivedtarterItems()) {
			sendInterface(269);
			player.getInventory().add(new Item(995, 10000)); // 10k coins

			player.getInventory().add(new Item(1277)); // Bronze sword.
			player.getInventory().add(new Item(841)); // Shortbow
			player.getInventory().add(new Item(882, 200)); // Bronze arrows

			player.getInventory().add(new Item(554, 100)); // Fire runes
			player.getInventory().add(new Item(555, 100)); // Water runes
			player.getInventory().add(new Item(556, 100)); // Air runes
			player.getInventory().add(new Item(557, 100)); // Earth runes
			player.getInventory().add(new Item(558, 100)); // Mind runes

			player.getInventory().add(new Item(2309, 5)); // Bread.

			player.getInventory().add(new Item(8007, 5)); // Varrock teletab.
			player.getInventory().add(new Item(8008, 5)); // Lumbridge teletab.
			player.getInventory().add(new Item(8009, 5)); // Falador teletab.
			player.getInventory().add(new Item(8010, 5)); // Camelot teletab.
			player.getInventory().add(new Item(8011, 5)); // Ardougne teletab.

			player.getSettings().setHasReceivedtarterItems(true);
		}

		QuestHandler.sendQuestLogin(player);

		InterfaceContainerListener inventoryListener = new InterfaceContainerListener(
				player, Inventory.INTERFACE, 0, 93);
		player.getInventory().addListener(inventoryListener);
		player.editQuestInfo(TutorialIsland.QUEST_INFO_INDEX, 0,
				TutorialIsland.MAX_STAGE);
		//int[][] info = player.getQuestInfo();
		//final int stage = info[TutorialIsland.QUEST_INFO_INDEX][0];
		//if (stage > 22) {
			InterfaceContainerListener equipmentListener1 = new InterfaceContainerListener(
					player, Equipment.INTERFACE1, 25, 94);
			player.getEquipment().addListener(equipmentListener1);
			player.getEquipment().addListener(
					new EquipmentContainerListener(player));

			InterfaceContainerListener equipmentListener2 = new InterfaceContainerListener(
					player, Equipment.INTERFACE2, 103, 95);
			player.getEquipment().addListener(equipmentListener2);
			player.getEquipment().addListener(
					new EquipmentContainerListener(player));
			player.getEquipment().addListener(
					new WeaponContainerListener(player));
		//}

		//TutorialIsland.start(player);
		return this;
	}

	public ActionSender sendFlashingTab(int tab) {
		final PacketBuilder pb = new PacketBuilder(80);
		pb.putByteA((byte) tab);
		player.getSession().write(pb.toPacket());
		return this;
	}

	/**
	 * Sends still graphics.
	 * 
	 * @param id
	 *            Graphics id.
	 * @param x
	 *            X coordinate (global).
	 * @param y
	 *            Y coordinate (global).
	 */
	public void sendStillGFX(final int id, final int height, final Location loc) {
		sendCoords(loc);
		final PacketBuilder p2 = new PacketBuilder(119);
		p2.put((byte) 0);
		p2.putShort(id);
		p2.put((byte) height);
		p2.putShort(0);
		player.getSession().write(p2.toPacket());
	}

	/**
	 * Sends the welcome screen.
	 * 
	 * @param topInterface
	 *            The top interface
	 * @param buttonInterface
	 *            The button interface.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendWelcomeScreen(int topInterface, int buttonInterface) {
		player.getInterfaceState().interfaceOpened(topInterface);
		player.getSession().write(
				new PacketBuilder(129).putShort(buttonInterface).putShortA(
						topInterface).toPacket());
		sendString(
				"<col=010101>Welcome to csPRO! <br> <col=010101>- a RuneScape Private Server based on the #459 Protocol.",
				topInterface, 12);
		sendString(
				"You have not yet set any recovery questions. It is <col=DD7500>strongly <col=FFFF00>reccommended that you do so. If you don't you will be <col=DD7500>unable to recover your password <col=FFFF00>if you forget it, or it is stolen.",
				topInterface, 14);
		sendString("You have 0 unread messages in your message centre",
				topInterface, 16);
		sendString(
				"You do not have a bank PIN. Please visit a bank if you would like to set one.",
				topInterface, 17);
		sendString(
				"You have <col=00EE00>unlimited <col=FFFF00>days of member credit remaining.",
				topInterface, 19);
		sendString(
				"Message of the week <br><br> <col=010101>Moderators will never ask for your password - <br><col=010101>EVER!",
				buttonInterface, 2);
		return this;
	}

	/**
	 * Replaces an ingame string with some server chosen value.
	 * 
	 * @param String
	 *            The string to write.
	 * @param interfaceId
	 *            The interfaceId to write the string on.
	 * @param childId
	 *            Where on the interface to write the string.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendString(String string, int interfaceId, int childId) {
		PacketBuilder bldr = new PacketBuilder(22, Type.VARIABLE_SHORT)
				.putInt2(interfaceId << 16 | childId).putString(string);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends the packet to construct a map region.
	 * 
	 * @param palette
	 *            The palette of map regions.
	 * @param newLocation
	 *            The new location of the player.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendConstructMapRegion(Palette palette,
			Location newLocation) {
		player.setLastKnownRegion(player.getLocation());
		player.setLocation(newLocation);
		PacketBuilder bldr = new PacketBuilder(27, Type.VARIABLE_SHORT);
		bldr.putLEShort(player.getLocation().getLocalY());
		bldr.putShortA(player.getLocation().getRegionX());
		bldr.putLEShort(player.getLocation().getRegionY());
		bldr.putByteS((byte) (player.getLocation().getZ()));
		bldr.putShort(player.getLocation().getLocalX());
		bldr.startBitAccess();
		for (int height = 0; height < 4; height++) {
			for (int xCalc = 0; xCalc < 13; xCalc++) {
				for (int yCalc = 0; yCalc < 13; yCalc++) {
					PaletteTile tile = palette.getTile(xCalc, yCalc, height);
					// System.out.println(tile);
					if (tile != null) {
						bldr.putBits(1, 1);
						bldr.putBits(26, tile.getX() << 14 | tile.getY() << 3
								| tile.getZ() << 24 | tile.getRotation() << 1);
						// bldr.putBits(26, tile.getRotation() << 1 | 0 | (z <<
						// 24) | (x << 14) | (y << 3));
					} else {
						bldr.putBits(1, 0);
					}
				}
			}
		}
		bldr.finishBitAccess();
		int[] sent = new int[4 * 13 * 13];
		int sentIndex = 0;
		for (int height = 0; height < 4; height++) {
			for (int xCalc = 0; xCalc < 13; xCalc++) {
				outer: for (int yCalc = 0; yCalc < 13; yCalc++) {
					PaletteTile tile = palette.getTile(xCalc, yCalc, height);
					if (tile != null) {
						// if (zPallete[height][xCalc][yCalc] != -1 &&
						// xPallete[height][xCalc][yCalc] != -1 &&
						// yPallete[height][xCalc][yCalc] != -1) {
						int x = tile.getX() / 8;
						int y = tile.getY() / 8;
						int region = y + (x << 8);
						for (int i = 0; i < sentIndex; i++) {
							if (sent[i] == region) {
								break outer;
							}
						}
						sent[sentIndex] = region;
						sentIndex++;
						bldr.putInt1(0);
						bldr.putInt1(0);
						bldr.putInt1(0);
						bldr.putInt1(0);
					}
				}
			}
		}
		player.getSession().write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends the player's skills.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSkills() {
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			sendSkill(i);
		}
		return this;
	}

	/**
	 * Sends a specific skill.
	 * 
	 * @param skill
	 *            The skill to send.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSkill(int skill) {
		PacketBuilder bldr = new PacketBuilder(181);
		bldr.putByteS((byte) player.getSkills().getLevel(skill));
		bldr.put((byte) skill);
		bldr.putLEInt((int) player.getSkills().getExperience(skill));
		player.getSession().write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends all the sidebar interfaces.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSidebarInterfaces() {
		final int[] icons = Constants.SIDEBAR_INTERFACES[0];
		final int[] interfaces = Constants.SIDEBAR_INTERFACES[1];
		for (int i = 0; i < icons.length; i++) {
			sendSidebarInterface(icons[i], interfaces[i]);
		}
		sendSidebarInterface(6, player.getMagic().getSpellBook().toInteger());
		return this;
	}

	/**
	 * Makes the client play a specific piece of music.
	 * 
	 * @param music
	 *            The music id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendMusic(int music) {
		player.getSession().write(
				new PacketBuilder(251).putShort(music).toPacket());
		return this;
	}

	/**
	 * Sends a specific sidebar interface.
	 * 
	 * @param icon
	 *            The sidebar icon.
	 * @param interfaceId
	 *            The interface id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSidebarInterface(int icon, int interfaceId) {
		player.getSession().write(
				new PacketBuilder(102).putLEShortA(interfaceId).putByteC(icon)
						.toPacket());
		return this;
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            The message to send.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendMessage(String message) {
		player.getSession().write(
				new PacketBuilder(158, Type.VARIABLE).putString(message)
						.toPacket());
		return this;
	}

	/**
	 * Sends the map region load command.
	 * 
	 * @return The action sender instance, for chaining.
	 */

	public ActionSender sendMapRegion() {
		PacketBuilder bldr = new PacketBuilder(49, Type.VARIABLE_SHORT);
		boolean forceSend = true;
		if ((((player.getLocation().getRegionX() / 8) == 48) || ((player
				.getLocation().getRegionX() / 8) == 49))
				&& ((player.getLocation().getRegionY() / 8) == 48)) {
			forceSend = false;
		}
		if (((player.getLocation().getRegionX() / 8) == 48)
				&& ((player.getLocation().getRegionY() / 8) == 148)) {
			forceSend = false;
		}
		bldr.putShortA(player.getLocation().getLocalY());
		bldr.putLEShortA(player.getLocation().getLocalX());
		for (int xCalc = (player.getLocation().getRegionX() - 6) / 8; xCalc <= ((player
				.getLocation().getRegionX() + 6) / 8); xCalc++) {
			for (int yCalc = (player.getLocation().getRegionY() - 6) / 8; yCalc <= ((player
					.getLocation().getRegionY() + 6) / 8); yCalc++) {
				@SuppressWarnings("unused")
				int region = yCalc + (xCalc << 8); // 1786653352
				if (forceSend
						|| ((yCalc != 49) && (yCalc != 149) && (yCalc != 147)
								&& (xCalc != 50) && ((xCalc != 49) || (yCalc != 47)))) {
					bldr.putInt2(0);
					bldr.putInt2(0);
					bldr.putInt2(0);
					bldr.putInt2(0);
				}
			}
		}
		bldr.putShortA(player.getLocation().getRegionX());
		bldr.putByteA(player.getLocation().getZ());
		bldr.putLEShortA((player.getLocation().getRegionY()));
		player.getSession().write(bldr.toPacket());
		Cooking.sendFlourBins(player);
		GroundItemController.refresh(player);
		player.setLastKnownRegion(player.getLocation());
		return this;
	}

	// 55

	public ActionSender sendWalkTo(int entityIndex) {
		System.out.println("Entity index: " + entityIndex);
		player.write(new PacketBuilder(65).putLEShort(entityIndex).toPacket());
		return this;
	}

	/**
	 * Sends the logout packet.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendLogout(boolean forced) {
		if (!forced) {
			if (!player.canLogout() && player.isActive()) {
				sendMessage("You cannot logout until 10 seconds after combat.");
				return this;
			}
			if (player.isDead()) {
				return this;
			}
			if (!player.getFightCaves().logoutAttempt()) {
				return this;
			}
		}
		player.write(new PacketBuilder(254).toPacket()); // TODO IoFuture
		return this;
	}

	/**
	 * Sends a packet to update a group of items.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param items
	 *            The items.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItems(int interfaceId, int childId, int type,
			Item[] items) {
		PacketBuilder bldr = new PacketBuilder(3, Type.VARIABLE_SHORT);
		bldr.putInt(interfaceId << 16 | childId);
		bldr.putShort(type);
		bldr.putShort(items.length);
		for (Item item : items) {
			if (item != null) {
				int count = item.getCount();
				bldr.putLEShortA(item.getId() + 1);
				if (count > 254) {
					bldr.putByteA((byte) 255);
					bldr.putInt(count);
				} else {
					bldr.putByteA((byte) count);
				}
			} else {
				bldr.putLEShortA(0);
				bldr.putByteA((byte) 0);
			}
		}
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends a packet to update a single item.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param slot
	 *            The slot.
	 * @param item
	 *            The item.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItem(int interfaceId, int childId, int type,
			int slot, Item item) {
		PacketBuilder bldr = new PacketBuilder(38, Type.VARIABLE_SHORT);
		bldr.putInt(interfaceId << 16 | childId);
		bldr.putShort(type);
		bldr.putSmart(slot);
		if (item != null) {
			int id = item.getId();
			bldr.putShort(id + 1);
			if (id + 1 != 0) {
				int count = item.getCount();
				if (count > 254) {
					bldr.put((byte) 255);
					bldr.putInt(count);
				} else {
					bldr.put((byte) count);
				}
			}
		} else {
			bldr.putShort(0);
			// bldr.put((byte) 0);
		}
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends a packet to update multiple (but not all) items.
	 * 
	 * @param interfaceId
	 *            The interface id.
	 * @param slots
	 *            The slots.
	 * @param items
	 *            The item array.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItems(int interfaceId, int childId, int type,
			int[] slots, Item[] items) {
		PacketBuilder bldr = new PacketBuilder(38, Type.VARIABLE_SHORT);
		bldr.putInt(interfaceId << 16 | childId);
		bldr.putShort(type);
		for (int i = 0; i < slots.length; i++) {
			Item item = items[slots[i]];
			bldr.putSmart(slots[i]);
			if (item != null) {
				int id = item.getId();
				bldr.putShort(id + 1);
				if (id + 1 != 0) {
					int count = item.getCount();
					if (count > 254) {
						bldr.put((byte) 255);
						bldr.putInt(count);
					} else {
						bldr.put((byte) count);
					}
				}
			} else {
				bldr.putShort(0);
				// bldr.put((byte) 0);
			}
		}
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Decides which of the two packets below to use, based on the value's
	 * value.
	 */
	public void sendConfig(int id, int value) {
		if (value < 128 && value > -128) {
			sendConfig1(id, value);
		} else {
			sendConfig2(id, value);
		}
	}

	/**
	 * Sends a configuration id along with a value #1.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendConfig1(int id, int value) {
		player.write(new PacketBuilder(244).putByteS((byte) value).putLEShort(
				id).toPacket());
		return this;
	}

	/**
	 * Sends a configuration id along with a value #2.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendConfig2(int id, int value) {
		player.write(new PacketBuilder(71).putLEShortA(id).putInt(value)
				.toPacket());
		return this;
	}

	/**
	 * Sends the enter amount interface.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendEnterAmountInterface() {
		player.write(new PacketBuilder(179).toPacket());
		return this;
	}

	public void sendNameRequest() {
		player.getSession().write(new PacketBuilder(197).toPacket());
	}

	/**
	 * Sends a packet that closes all interfaces the client has open.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendCloseInterface() {
		player.getInterfaceState().interfaceClosed(-1);
		player.write(new PacketBuilder(93).toPacket());
		return this;
	}

	/**
	 * Sends a normal interface for the client.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInterface(int interfaceId) {
		player.getInterfaceState().interfaceOpened(interfaceId);
		player.write(new PacketBuilder(192).putLEShort(interfaceId).toPacket());
		return this;
	}

	/**
	 * Sends a ChatBox interface for the client.
	 * 
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendChatboxInterface(int interfaceId) {
		player.getInterfaceState().interfaceOpened(interfaceId);
		player.write(new PacketBuilder(79).putShortA(interfaceId).toPacket());
		return this;
	}

	/**
	 * Sends the player an option.
	 * 
	 * @param slot
	 *            The slot to place the option in the menu.
	 * @param top
	 *            Place the option directly at the top.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInteractionOption(String option, int slot,
			boolean top) {
		PacketBuilder bldr = new PacketBuilder(151, Type.VARIABLE);
		bldr.putString(option);
		bldr.putByteA(top ? (byte) 1 : (byte) 0);
		bldr.put((byte) slot);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Animates an object.
	 * 
	 * @param objx
	 *            The x coordinate.
	 * @param objy
	 *            The y coordinate
	 * @param animationID
	 *            The animation id.
	 * @param tileObjectType
	 *            The object type.
	 * @param orientation
	 *            The facing dir.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender animateObject(int objx, int objy, int animationID,
			int tileObjectType, int orientation) {
		for (Player plr : World.getWorld().getRegionManager()
				.getRegionByLocation(player.getLocation()).getPlayers()) {
			plr.getActionSender().sendCoords(
					Location.create(objx, objy, plr.getLocation().getZ()));
			plr
					.getSession()
					.write(
							new PacketBuilder(209)
									.putShort(animationID)
									.put((byte) 0)
									.putByteS(
											(byte) ((tileObjectType << 2) + (orientation & 3)))
									.toPacket());
		}
		return this;
	}

	/**
	 * @param objectId
	 *            The id of the object we want to spawn.
	 * @param objectType
	 *            10 for normal objects.
	 * @param objectFace
	 *            The objects orientation.
	 * @param position
	 *            Where to spawn the object
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendCreateObject(int objectId, int objectType,
			int objectFace, Location position) {
		sendCoords(position);
		PacketBuilder bldr = new PacketBuilder(236);
		bldr.putLEShortA(objectId).putByteC(0).putByteS(
				(byte) ((objectType << 2) + (objectFace & 3)));
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendCreateLocalObject(int objectId, int objectType,
			int objectFace, Location position) {
		sendLocalCoords(position);
		PacketBuilder bldr = new PacketBuilder(236);
		bldr.putLEShortA(objectId).putByteC(0).putByteS(
				(byte) ((objectType << 2) + (objectFace & 3)));
		player.getSession().write(bldr.toPacket());
		return this;
	}

	/**
	 * This packet is needed to make alot of the other packets work. Eg
	 * Projectiles etc.
	 * 
	 * @param location
	 *            Where to send those coords.
	 * @return The action sender instance, for chaining.
	 * */
	public ActionSender sendLocalCoords(Location location) {
		PacketBuilder bldr = new PacketBuilder(97);
		bldr.putByteS((byte) (location.getY()));
		bldr.putByteC((byte) (location.getX()));
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendDestroyObject(int objectType, int objectFace,
			Location position) {
		sendCoords(position);
		PacketBuilder bldr2 = new PacketBuilder(64);
		bldr2.putByteC((objectType << 2) + (objectFace & 3)).put((byte) 0);
		player.getSession().write(bldr2.toPacket());
		return this;
	}

	public ActionSender sendDestroyLocalObject(int type, int rotation,
			Location location) {
		sendLocalCoords(location);
		PacketBuilder bldr2 = new PacketBuilder(64);
		bldr2.putByteC((type << 2) + (rotation & 3)).put((byte) 0);
		player.getSession().write(bldr2.toPacket());
		return this;
	}

	/**
	 * This packet is needed to make alot of the other packets work. Eg
	 * Projectiles etc.
	 * 
	 * @param location
	 *            Where to send those coords.
	 * @return The action sender instance, for chaining.
	 * */
	public ActionSender sendCoords(Location location) {
		PacketBuilder bldr = new PacketBuilder(97);
		int regionX = player.getLastKnownRegion().getRegionX(), regionY = player
				.getLastKnownRegion().getRegionY();
		bldr.putByteS((byte) (location.getY() - ((regionY - 6) * 8)));
		bldr.putByteC((byte) (location.getX() - ((regionX - 6) * 8)));
		player.getSession().write(bldr.toPacket());
		return this;
	}

	/**
	 * This packet is needed to make alot of the other packets work. Eg
	 * Projectiles etc.
	 * 
	 * @param location
	 *            Where to send those coords.
	 * @param xoff
	 *            The x offset.
	 * @param yoff
	 *            The y offset.
	 * @return The action sender instance, for chaining.
	 * */
	public ActionSender sendCoords(Location location, int xoff, int yoff) {
		PacketBuilder bldr = new PacketBuilder(97);
		int regionX = player.getLastKnownRegion().getRegionX(), regionY = player
				.getLastKnownRegion().getRegionY();
		bldr.putByteS((byte) ((location.getY() - ((regionY - 6) * 8)) + yoff));
		bldr.putByteC((byte) ((location.getX() - ((regionX - 6) * 8)) + xoff));
		player.getSession().write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends a specific sound to the client.
	 * 
	 * @param sound
	 *            The sound id.
	 * @param j
	 *            The volume of the sound????? - UNSURE.
	 * @param delay
	 *            The delay before the sound should be played.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSound(int sound, int j, int delay) {
		PacketBuilder bldr = new PacketBuilder(230);
		bldr.putShort(sound);
		bldr.put((byte) j);
		bldr.putShort(delay);
		player.getSession().write(bldr.toPacket());
		return this;

	}

	public ActionSender sendPlayerHints(int playerIndex) {
		PacketBuilder bldr = new PacketBuilder(60);
		bldr.put((byte) 10);// 10 for Player's.
		bldr.putShort(playerIndex);
		bldr.putShort(0);
		bldr.put((byte) 0);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendNPCHints(int npcIndex) {
		PacketBuilder bldr = new PacketBuilder(60);
		bldr.put((byte) 1);// 1 for NPC's.
		bldr.putShort(npcIndex);
		bldr.putShort(0);
		bldr.put((byte) 0);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	/**
	 * Notice: The z coordinate from the position parameter is the height to
	 * display the arrow..
	 * 
	 * @param rotation
	 *            : Center = 2, East: 3.
	 */
	public ActionSender sendObjectHints(Location position, int rotation) {
		PacketBuilder bldr = new PacketBuilder(60);
		bldr.put((byte) 3);// 2 for well yeah some specific coordinates.
		bldr.putShort(position.getX());
		bldr.putShort(position.getY());
		bldr.put((byte) position.getZ());
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendChatboxString(String string) {
		PacketBuilder bldr = new PacketBuilder(177, Type.VARIABLE);
		bldr.putString(string);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendMultiIcon(int id) {
		PacketBuilder bldr = new PacketBuilder(203);
		bldr.put((byte) id);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendInterfaceModel(int interfaceid, int child,
			int itemsize, int itemid) {
		PacketBuilder bldr = new PacketBuilder(225);
		int inter = ((interfaceid * 65536) + child);
		bldr.putShortA(itemid);
		bldr.putLEInt(inter);
		bldr.putLEInt(itemsize);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendLevelUp(String firstMessage, String secondMessage,
			int interfaceId) {
		sendChatboxInterface(interfaceId);
		sendString("<col=00008B>" + firstMessage, interfaceId, 0);
		sendString(secondMessage, interfaceId, 1);
		return this;
	}

	public ActionSender sendSwitchTabs(int sidebarId) {
		PacketBuilder bldr = new PacketBuilder(136).putByteC((byte) sidebarId);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendWalkableInterface(int interfaceId) {
		PacketBuilder bldr = new PacketBuilder(201);
		bldr.putShort(interfaceId);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendFriendsStatus(int i) {
		PacketBuilder bldr = new PacketBuilder(70).put((byte) i);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendFriend(long name, int world) {
		PacketBuilder bldr = new PacketBuilder(224).putLong(name).putShort(
				world).put((byte) 1);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendIgnores(long[] names) {
		PacketBuilder bldr = new PacketBuilder(35, Type.VARIABLE_SHORT);
		for (long name : names) {
			bldr.putLong(name);
		}
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendSentPrivateMessage(long name, String message) {
		byte[] bytes = new byte[message.length()];
		TextUtils.encryptPlayerChat(bytes, 0, 0, message.length(), message
				.getBytes());
		player.getSession().write(
				new PacketBuilder(170, Type.VARIABLE).putLong(name).put(
						(byte) message.length()).putBytes(bytes).toPacket());
		return this;
	}

	public ActionSender sendReceivedPrivateMessage(long name, int rights,
			String message) {
		int messageCounter = player.getFriends().getNextUniqueId();
		byte[] bytes = new byte[message.length() + 1];
		bytes[0] = (byte) message.length();
		TextUtils.encryptPlayerChat(bytes, 0, 1, message.length(), message
				.getBytes());
		PacketBuilder gen = new PacketBuilder(67, Type.VARIABLE);
		gen.putLong(name).putShort(1).putBytes(
				new byte[] { (byte) ((messageCounter << 16) & 0xFF),
						(byte) ((messageCounter << 8) & 0xFF),
						(byte) (messageCounter & 0xFF) }).put((byte) rights)
				.putBytes(bytes);
		player.getSession().write(gen.toPacket());
		return this;
	}

	/**
	 * @param time
	 *            This one is a little tricky. The value is seconds /2 and
	 *            alittle more. (cba doing more research).
	 * */
	public ActionSender sendSystemUpdate(int time) {
		player.getSession().write(
				new PacketBuilder(255).putShortA(time).toPacket());
		return this;
	}

	/**
	 * @param publicChat
	 *            0 = On, 1 = Friends, 2 = Off
	 * @param privateChat
	 *            0 = On, 1 = Friends, 2 = Off
	 * @param trade
	 *            0 = On, 1 = Friends, 2 = Off
	 * */
	public ActionSender sendChatOptions(int publicChat, int privateChat,
			int trade) {
		PacketBuilder bldr = new PacketBuilder(200).put((byte) publicChat).put(
				(byte) privateChat).put((byte) trade);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendRunEnergy() {
		player.getSession().write(
				new PacketBuilder(94).put((byte) player.getRunEnergy())
						.toPacket());
		return this;
	}

	public ActionSender sendWeight() {
		player.getSession()
				.write(new PacketBuilder(180).putShort(0).toPacket());
		return this;
	}

	public ActionSender sendDestroyGroundItem(GroundItem item) {
		if (item.getLocation().getZ() != player.getLocation().getZ()) {
			return this;
		}
		sendCoords(item.getLocation());
		PacketBuilder bldr = new PacketBuilder(48);
		bldr.put((byte) 0);
		bldr.putLEShortA(item.getId());
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendCreateGroundItem(GroundItem item) {
		if (item.getLocation().getZ() != player.getLocation().getZ()) {
			return this;
		}
		sendCoords(item.getLocation());
		PacketBuilder bldr = new PacketBuilder(33);
		bldr.putLEShortA(item.getCount());
		bldr.putLEShortA(item.getId());
		bldr.put((byte) 0);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	/**
	 * No other sidebar interfaces are displayed with this packet..
	 */
	public ActionSender sendLonleySidebarInterface(int interfaceId) {
		PacketBuilder bldr = new PacketBuilder(2);
		bldr.putShortA(interfaceId);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendProjectile(Location source, Location dest,
			int offsetX, int offsetY, int gfx, int angle, int startHeight,
			int endHeight, int speed, Entity lockon) {
		sendCoords(source, -3, -2);
		PacketBuilder bldr = new PacketBuilder(39).put((byte) angle).put(
				(byte) offsetX).put((byte) offsetY).putShort(
				lockon == null ? -1 : (lockon instanceof Player ? -lockon
						.getIndex() - 1 : lockon.getIndex() + 1)).putShort(gfx)
				.put((byte) startHeight).put((byte) endHeight).putShort(53)
				.putShort(speed).put((byte) 16).put((byte) 64);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendBonus(int[] bonuses) {
		int id = 108;
		for (int i = 0; i < (bonuses.length - 1); i++) {
			sendString((Bonuses.BONUS_NAMES[i] + ": "
					+ (bonuses[i] > 0 ? "+" : "") + bonuses[i]), 465, id++);
			if (i == 9) {
				id++;
				sendString((Bonuses.BONUS_NAMES[11] + ": "
						+ (bonuses[11] > 0 ? "+" : "") + bonuses[11]), 465,
						id++);
			}
			if (i == 10) {
				id++;
				sendString((Bonuses.BONUS_NAMES[11] + ": "
						+ (bonuses[11] > 0 ? "+" : "") + bonuses[11]), 465,
						id++);
				id = 120;
			}
		}
		return this;
	}

	/**
	 * Sends the players head for dialogues.
	 * 
	 * @return the ActionSender instance for chaining.
	 */
	public ActionSender sendPlayerHead(int interfaceId, int childId) {
		player.getSession().write(
				new PacketBuilder(21).putInt(interfaceId << 16 | childId)
						.toPacket());
		return this;
	}

	/**
	 * Sends a specific npc head for dialogues. Those two packets are 90%
	 * identical (different MediaType ID's), but sendNPCHead1 is writing wrong
	 * NPC id's for some reason..?
	 * 
	 * @return the ActionSender instance for chaining.
	 */
	public ActionSender sendNPCHead(int npcId, int interfaceId, int childId) {
		player.getSession().write(
				new PacketBuilder(188).putLEShort(npcId).putShort(interfaceId)
						.putShort(childId).toPacket());
		return this;
	}

	public ActionSender sendNPCHead1(int npcId, int interfaceId, int childId) {
		player.getSession().write(
				new PacketBuilder(62).putLEInt(interfaceId << 16 | childId)
						.putShort(npcId).toPacket());
		return this;
	}

	public ActionSender editTypeZero(int interfaceId, int childId, int info) {
		PacketBuilder bldr = new PacketBuilder(189).putLEShortA(info).putInt1(
				interfaceId << 16 | childId);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	public ActionSender sendMoveInterfaceString(int interfaceId, int childId,
			int offset1, int offset2) {
		PacketBuilder bldr = new PacketBuilder(252).putShort(offset1)
				.putLEShortA(offset2).putInt1(interfaceId << 16 | childId);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	// Something to do with model rotation..
	public ActionSender sendPacket125(int interfaceId, int childId, int info1,
			int info2) {
		PacketBuilder bldr = new PacketBuilder(125).putLEShort(info1).putInt2(
				interfaceId << 16 | childId).putShort(info2);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	// Clears an interface for items I suppose.
	public ActionSender sendPacket69(int interfaceId, int childId) {
		PacketBuilder bldr = new PacketBuilder(69).putLEInt(interfaceId << 16
				| childId);
		player.getSession().write(bldr.toPacket());
		return this;
	}

	/**
	 * Animates a specific interface child. Example usage: Welcome screen,
	 * dialogues.
	 */
	public ActionSender animateInterfaceId(int emoteId, int interfaceId,
			int childId) {
		player.getSession().write(
				new PacketBuilder(155).putLEShortA(emoteId).putInt2(
						interfaceId << 16 | childId).toPacket());
		return this;
	}

	public ActionSender sendInterfaceConfig(int interfaceId, int childId,
			boolean active) {
		PacketBuilder bldr = new PacketBuilder(148);
		bldr.putByteS((byte) (active ? 1 : 0)).putLEInt(
				interfaceId << 16 | childId);
		player.getSession().write(bldr.toPacket());
		return this;
	}

}
