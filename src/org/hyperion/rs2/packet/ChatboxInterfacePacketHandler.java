package org.hyperion.rs2.packet;

import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.content.DialogueLoader.Emotes;
import org.hyperion.rs2.content.minigames.Barrows;
import org.hyperion.rs2.content.skills.Cooking;
import org.hyperion.rs2.content.skills.Crafting;
import org.hyperion.rs2.content.skills.Fletching;
import org.hyperion.rs2.content.traveling.ShipTraveling;
import org.hyperion.rs2.content.traveling.ShipTraveling.ShipLocation;
import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.InterfaceState.InterfaceOption;
import org.hyperion.rs2.net.Packet;

public class ChatboxInterfacePacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int button = packet.getShort();
		int interfaceId = packet.getShort();
		packet.getLEShortA();
		if (interfaceId != player.getInterfaceState().getCurrentInterface()) {
			player.getActionSender().sendCloseInterface();
			System.out.println("AMFG HACKERS.");
			return;
		}
		DialogueLoader dl = player.getCurrentDialogueLoader();
		switch (interfaceId) {
		/**
		 * DialougeLoader.
		 */
		case 64:
		case 65:
		case 66:
		case 67:
		case 241:
		case 242:
		case 243:
		case 244:
			if (dl == null || player.getNextDialogueIds() == null) {
				/*
				 * If the next dialogue ID's are null, we've most likely set
				 * them null.
				 */
				/*
				 * We close all interfaces.
				 */
				player.getActionSender().sendCloseInterface();
				/*
				 * Check if the Monk of Entrana just searched us.
				 */
				Object entranaTraveling = player
						.getTemporaryAttribute("EntranaShipTraveling");
				if (entranaTraveling != null) {
					if ((Boolean) entranaTraveling) {
						ShipTraveling.travel(player, ShipLocation.PORT_SARIM,
								ShipLocation.ENTRANA);
					}
					player.removeTemporaryAttribute("EntranaShipTraveling");
				}
				break;
			}
			int nextDialogueId = player.getNextDialogueIds()[0];
			DialogueLoader.getNextDialogue(player, dl, nextDialogueId);
			break;
		/**
		 * Options.
		 */
		case 228: // Two options interface.
			if (dl == null) {
				player.getActionSender().sendCloseInterface();
				break;
			}
			switch (button) {
			case 1:// Yes.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[0]);
				return;
			case 2:// NO.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[1]);
				return;
			}
			break;
		case 230: // Three options interface.
			if (dl == null) {
				player.getActionSender().sendCloseInterface();
				break;
			}
			switch (button) {
			case 1:// Option 1.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[0]);
				return;
			case 2:// Option 2.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[1]);
				return;
			case 3:// Option 3.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[2]);
				return;
			}
			break;
		case 232:// Four options interface.
			if (dl == null) {
				player.getActionSender().sendCloseInterface();
				break;
			}
			switch (button) {
			case 1:// Option 1.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[0]);
				return;
			case 2:// Option 2.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[1]);
				return;
			case 3:// Option 3.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[2]);
				return;
			case 4:// Option 4.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[3]);
				return;
			}
			break;
		case 234:// Five options interface.
			if (dl == null) {
				player.getActionSender().sendCloseInterface();
				break;
			}
			switch (button) {
			case 1:// Option 1.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[0]);
				return;
			case 2:// Option 2.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[1]);
				return;
			case 3:// Option 3.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[2]);
				return;
			case 4:// Option 4.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[3]);
				return;
			case 5:// Option 5.
				DialogueLoader.getNextDialogue(player, dl,
						player.getNextDialogueIds()[4]);
				return;
			}
			break;
		/*
		 * Interface with one line of info.
		 */
		case 101:
			Object entranaTraveling1 = player
					.getTemporaryAttribute("EntranaShipTraveling");
			if (entranaTraveling1 != null) {
				boolean canTravel = (Boolean) entranaTraveling1;
				if (canTravel) {
					String[] lines = { "All is satisfactory. You may board the boat now." };
					DialogueLoader.dialogue(
							player,
							player.getInteractingEntity() == null ? new NPC(
									NPCDefinition.forId(657)) : player
									.getInteractingEntity(), Emotes.DEFAULT,
							lines);
					player.setNextDialogueIds(null);
				} else {
					String[] lines = {
							"Grr! I see you brought some illegal items! Get",
							"out of my sight immediately!" };
					DialogueLoader.dialogue(
							player,
							player.getInteractingEntity() == null ? new NPC(
									NPCDefinition.forId(657)) : player
									.getInteractingEntity(), Emotes.ANGER2,
							lines);
					player.setNextDialogueIds(null);
				}
				return;
			}
			player.getActionSender().sendCloseInterface();
			break;
		/*
		 * Only used for Construction at the moment..
		 */
		case 233:
			if (player.getConstruction().getBuildingRoom() == null) {
				switch (button) {
				/*
				 * Enter your house.
				 */
				case 1:
					player.getConstruction().enterHouse(false);
					break;
				/*
				 * Enter your house (building mode)
				 */
				case 2:
					player.getConstruction().enterHouse(true);
					break;
				/*
				 * Enter friends house.
				 */
				case 3:
					player.getInterfaceState().openEnterNameInterface(
							interfaceId);
					break;
				}
			} else {
				switch (button) {
				/*
				 * Rotate clockwise
				 */
				case 1:
					player.getActionSender()
							.sendMessage(
									"I almost had this, but spend 2 days fixing it. Its a delayed feature for now.");
					player.getActionSender().sendCloseInterface();
					// player.getConstruction().spawnRoomForRotation(true);
					break;
				/*
				 * Rotate anti clockwise
				 */
				case 2:
					player.getActionSender()
							.sendMessage(
									"I almost had this, but spend 2 days fixing it. Its a delayed feature for now.");
					player.getActionSender().sendCloseInterface();
					// player.getConstruction().spawnRoomForRotation(false);
					break;
				/*
				 * Build.
				 */
				case 3:
					for (GameObject object : player.getConstruction()
							.getBuildingRoom().getHotspots()) {
						Location local = Location.create(object.getLocation()
								.getX()
								+ player.getConstruction().getBuildingRoom()
										.getX(), object.getLocation().getY()
								+ player.getConstruction().getBuildingRoom()
										.getY(), object.getLocation().getZ());
						player.getActionSender().sendDestroyLocalObject(
								object.getType(), object.getRotation(), local);
					}
					player.getConstruction()
							.getHouse()
							.addRoomAndReload(
									player.getConstruction().getBuildingRoom());
					player.getInventory().remove(
							new Item(995, player.getConstruction()
									.getRoomPrice()));
					player.getConstruction().setBuildingRoom(null, -1);
					break;
				/*
				 * Cancel..
				 */
				case 4:
					for (GameObject object : player.getConstruction()
							.getBuildingRoom().getHotspots()) {
						Location local = Location.create(object.getLocation()
								.getX()
								+ player.getConstruction().getBuildingRoom()
										.getX(), object.getLocation().getY()
								+ player.getConstruction().getBuildingRoom()
										.getY(), object.getLocation().getZ());
						player.getActionSender().sendDestroyLocalObject(
								object.getType(), object.getRotation(), local);
					}
					player.getConstruction().setBuildingRoom(null, -1);
					player.getActionSender().sendCloseInterface();
					break;
				}
			}
			break;
		case Cooking.COOKING_INTERFACE:
			Cooking.handleInterfaceButtons(player, button);
			break;
		/*
		 * Entering the barrows tunnels.
		 */
		case 229:
			Object obj = player.getTemporaryAttribute("InterfaceOption");
			if (obj != null) {
				InterfaceOption option = (InterfaceOption) obj;
				switch (option) {
				case BARROWS:
					switch (button) {
					case 1:
						Barrows.teleportToTunnels(player);
						player.getActionSender().sendCloseInterface();
						break;
					case 2:
						player.getActionSender().sendCloseInterface();
						break;
					}
					break;
				case CONSTRUCTION:
					switch (button) {
					/*
					 * Yes
					 */
					case 1:
						player.getConstruction().remove();
						player.getActionSender().sendCloseInterface();
						break;
					/*
					 * No
					 */
					case 2:
						player.getActionSender().sendCloseInterface();
						break;
					}
					break;
				}
			}

			break;
		case 231:// Three options.
			switch (player.getJewellery().getGemType()) {
			case RING_OF_DUELING:
				/*
				 * Ring of dueling rubbing.
				 */
				switch (button) {
				case 1:// Teleport to Castle Wars.
					player.getJewellery().gemTeleport(player,
							Location.create(2440, 3089, 0));
					player.getActionSender().sendCloseInterface();
					break;
				case 2:// Teleport to Duel Arena.
					player.getJewellery().gemTeleport(player,
							Location.create(3316, 3235, 0));
					player.getActionSender().sendCloseInterface();
					break;
				case 3:// Nowhere.
					player.getActionSender().sendCloseInterface();
					break;
				}
				break;
			case GAMES_NECKLACE:
				/*
				 * Games necklace rubbing.
				 */
				switch (button) {
				case 1:// Burthope games room (Burthope for now)
					player.getJewellery().gemTeleport(player,
							Location.create(2926, 3559, 0));
					player.getActionSender().sendCloseInterface();
					break;
				case 2:// Barbarian outpost.
					player.getJewellery().gemTeleport(player,
							Location.create(2541, 3546, 0));
					player.getActionSender().sendCloseInterface();
					break;
				case 3:// Nowhere.
					player.getActionSender().sendCloseInterface();
					break;
				}
				break;
			}

			break;

		/*
		 * Glory teleporting.
		 */
		case 238:
			switch (button) {
			/*
			 * Edgewille.
			 */
			case 1:
				player.getJewellery().gemTeleport(player,
						Location.create(3089, 3496, 0));
				player.getActionSender().sendCloseInterface();
				break;
			/*
			 * Karamja.
			 */
			case 2:
				player.getJewellery().gemTeleport(player,
						Location.create(2918, 3176, 0));
				player.getActionSender().sendCloseInterface();
				break;
			/*
			 * Draynor Village
			 */
			case 3:
				player.getJewellery().gemTeleport(player,
						Location.create(3105, 3249, 0));
				player.getActionSender().sendCloseInterface();
				break;
			/*
			 * Al-Kharid
			 */
			case 4:
				player.getJewellery().gemTeleport(player,
						Location.create(3293, 3163, 0));
				player.getActionSender().sendCloseInterface();
				break;
			case 5:
				player.getActionSender().sendCloseInterface();
				break;
			}
			break;
		/*
		 * Hard leather body interface.
		 */
		case 309:
			if (Crafting.hardLeatherBodies(player, button)) {
				return;
			}
			break;
		/*
		 * All Dragon leathers are configured in this interface. Fletching is
		 * done in this interface as well.
		 */
		case 304:
			if (player.getCraftingVariables().isCrafting()) {
				Crafting.dragonLeatherCrafting(player, button);
			} else if (player.getFletchingVariables().isFletching()) {
				Fletching.handleChatboxInterface(304, player, button);
			}
			break;
		case 303:
			Fletching.handleChatboxInterface(303, player, button);
			break;
		/*
		 * Destroy items.
		 */
		case 94:
			switch (button) {
			case 4:
				player.getInventory().remove(player.getDestroyingItem()[1],
						new Item(player.getDestroyingItem()[0]));
				player.getActionSender().sendCloseInterface();
				break;
			case 5:
				player.getActionSender().sendCloseInterface();
				break;
			}
			break;
		default:
			System.out.println("Unhandled chatbox interface: " + interfaceId
					+ " " + button);
			player.getActionSender().sendCloseInterface();
		}
	}
}
