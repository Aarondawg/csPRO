package org.hyperion.rs2.content.traveling;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.content.Dialogue;
import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.content.DialogueLoader.Emotes;
import org.hyperion.rs2.content.DialogueLoader.Type;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

public class ShipTraveling {

	public static final int INTERFACE = 299;

	/*
	 * SendConfig configuration for interface 299. Config ID: 75 Value IDs: 0 =
	 * clear. 1 = Port Sarim - Entrana 2 = Entrana - Port Sarim 3 = Port Sarim -
	 * Crandor 4 = Crandor - Port Sarim 5 = Port Sarim - Karamja. 6 = Karamja -
	 * Port Sarim 7 = Sea(Maybe ardounge) - Entrana?? 8 = Entrana?? - Sea (Maybe
	 * ardounge) 9 = Stands still on top of Crandor. 10 = From the "sword" and
	 * out of the map(left side of the screen) 11 = Port Khazard - Ship yard. 12
	 * = Ship yard - Port Khazard. 13 = Between Port Khazard and "Sword" - Ship
	 * yard. 14 = Port Sarim - Ape Atoll. 15 = Ape atoll - Port Sarim. 16 =
	 * Feldip Hills - Next to Cairn Isle (On a Raft, for that Orge quest) 17 =
	 * Next to Cairn Isle (On a Raft, for that Orge quest) - Feldip Hills 18 =
	 * (Nothing) 19 = (Nothing) 20 = (Nothing) - Brown.
	 */

	public static void handleDialogueShipTraveling(Player player, int npcId,
			int nextDialogueId) {
		ShipLocation from = ShipLocation.getShipLocationByLocation(player
				.getLocation());
		if (from == null) {
			System.out.println("The ship take-off location was null!");
			return;
		}
		switch (npcId) {
		/*
		 * For now, we only spawned one monk.
		 */
		case 657:
			if (nextDialogueId == 4) {
				player.getActionSender().sendChatboxInterface(101);
				player.getActionSender().sendString(
						"The monk quickly searches you.", 101, 1);
				player.setTemporaryAttribute("EntranaShipTraveling",
						!hasCombatItems(player));
			}
			break;
		case 376:
			if (hasCoins(player, NPC.create(NPCDefinition.forId(npcId)), 30)) {
				travel(player, from, ShipLocation.SHIP_YARD);
			}
			break;
		}
	}

	public static boolean handleSecondNPCOption(Player player, NPC npc) {
		ShipLocation from = ShipLocation.getShipLocationByLocation(npc
				.getLocation());
		if (from == null) {
			System.out.println("The ship take-off location was null!");
			return false;
		}
		switch (from) {
		case SHIP_YARD:
			switch (npc.getDefinition().getId()) {
			/*
			 * Customs officer
			 */
			case 380:
				if (hasCoins(player, npc, 30)) {
					travel(player, from, ShipLocation.PORT_SARIM);
					return true;
				}
				break;
			}
			break;
		case PORT_SARIM:
			// If the NPC is is the karamja, then pay 30 coins.
			switch (npc.getDefinition().getId()) {
			/*
			 * For now, there's only one Monk of Entrana in Port Sarim.
			 */
			case 657:
				if (!hasCombatItems(player)) {
					travel(player, from, ShipLocation.ENTRANA);
				}
				return true;
				/*
				 * Captain Tobias - Towards Karamja.
				 */
			case 376:
				if (hasCoins(player, npc, 30)) {
					travel(player, from, ShipLocation.SHIP_YARD);
				}
				return true;
			}
			break;
		case ENTRANA:
			/*
			 * No need to check for weapons, or NPC IDs.
			 */
			travel(player, from, ShipLocation.PORT_SARIM);
			return true;
		}
		return false;

	}

	private static boolean hasCoins(Player player, NPC npc, int amount) {
		Item coins = new Item(995, amount);
		if (player.getInventory().contains(coins)) {
			player.getInventory().remove(coins);
			return true;
		} else {
			player.setNextDialogueIds(new int[] { -1 });
			DialogueLoader.dialogue(player, npc, Emotes.SAD, new String[] {
					"I'm affraid you'll need at least " + amount,
					"coins in order to board this ship. Fetch some",
					"and come back for the next take-off." });
			return false;
		}
	}

	/**
	 * Checks if a player is having "combat items" in his inventory or wielded.
	 * 
	 * @param player
	 *            The player.
	 * @return True if, false if not.
	 */
	private static boolean hasCombatItems(Player player) {
		Item[] equip = player.getEquipment().toArray();
		Item[] inven = player.getInventory().toArray();
		for (Item item : equip) {
			for (int id : Constants.COMBAT_RELATED_ITEMS) {
				if (item != null) {
					if (item.getId() == id) {
						String[] lines = {
								"Grr! I see you brought some illegal items! Get",
								"out of my sight immediately!" };
						Entity interacting = player.getInteractingEntity();
						if (interacting == null) {
							DialogueLoader.dialogue(player, new NPC(
									NPCDefinition.forId(657)), Emotes.ANGER2,
									lines);
						} else {
							interacting.face(player.getLocation());
							DialogueLoader.dialogue(player, interacting,
									Emotes.ANGER2, lines);
						}
						player.setNextDialogueIds(null);
						return true;
					}
				}

			}
		}
		for (Item item : inven) {
			for (int id : Constants.COMBAT_RELATED_ITEMS) {
				if (item != null) {
					if (item.getId() == id) {
						String[] lines = {
								"Grr! I see you brought some illegal items! Get",
								"out of my sight immediately!" };
						DialogueLoader
								.dialogue(
										player,
										player.getInteractingEntity() == null ? new NPC(
												NPCDefinition.forId(657))
												: player.getInteractingEntity(),
										Emotes.ANGER2, lines);
						player.setNextDialogueIds(null);
						return true;
					}
				}

			}
		}
		return false;
	}

	private static final Location PORT_SARIM_SHIP_FOR_ENTRANA_GANGPLANK = Location
			.create(3048, 3232, 1);
	private static final Location ENTRANA_GANGPLANK = Location.create(2834,
			3333, 1);

	private static final Location PORT_SARIM_SHIP_FOR_SHIP_YARD_GANGPLANK = Location
			.create(3031, 3217, 1);
	private static final Location SHIP_YARD_GANGPLANK = Location.create(2956,
			3144, 1);

	public static boolean handleShipGangPlanks(Player player, Location loc,
			int id) {
		switch (id) {
		case 2413:// Entering gang plank from the Port Sarim ship - the one
					// going to Entrana.
			if (loc.equals(PORT_SARIM_SHIP_FOR_ENTRANA_GANGPLANK)) {
				player.setTeleportTarget(Location.create(3048, 3234, 0));
				return true;
			}
			break;
		case 2415:// Entering gang plank from the Entrana ship.
			if (loc.equals(ENTRANA_GANGPLANK)) {
				player.setTeleportTarget(Location.create(2834, 3335, 0));
				return true;
			}
			break;
		case 2084:
			if (loc.equals(PORT_SARIM_SHIP_FOR_SHIP_YARD_GANGPLANK)) {
				player.setTeleportTarget(Location.create(3029, 3217, 0));
				return true;
			}
			break;
		case 2082:
			if (loc.equals(SHIP_YARD_GANGPLANK)) {
				player.setTeleportTarget(Location.create(2956, 3146, 0));
				return true;
			}
			break;
		}
		return false;
	}

	public static void travel(final Player player, ShipLocation from,
			final ShipLocation where) {
		Location teleportTarget = null;
		int configId = 0;
		int delay = 0;
		switch (from) {
		case PORT_SARIM:
			switch (where) {
			case ENTRANA:
				teleportTarget = Location.create(2834, 3332, 1);
				configId = 1; // Travel from Port Sarim - Entrana.
				delay = 8300;
				break;
			case SHIP_YARD:
				teleportTarget = Location.create(2956, 3143, 1);
				configId = 5; // Travel from Port Sarim - Karamja.
				delay = 5000;
				break;
			case PEST_CONTROL:

				break;
			}
			break;
		/*
		 * In Entrana, there's only one way to go: Port Sarim.
		 */
		case ENTRANA:
			teleportTarget = Location.create(3048, 3231, 1);
			configId = 2;
			delay = 8300;
			break;
		/*
		 * Only one way to go: Port Sarim..
		 */
		case SHIP_YARD:
			teleportTarget = Location.create(3032, 3217, 1);
			configId = 6;
			delay = 5000;
			break;
		}
		player.getActionSender().sendInterface(INTERFACE);
		player.getActionSender().sendConfig(75, configId); // Travel from
															// Entrana - Port
															// Sarim.
		player.setCanWalk(false);
		player.setTeleportTarget(teleportTarget);
		World.getWorld().submit(new Event(delay) {

			@Override
			public void execute() {
				player.setCanWalk(true);
				player.getActionSender().sendConfig(75, 0); // resets the
															// interface for
															// further journey.
				player.getActionSender().sendMessage(
						"You arrive at " + where.getName() + ".");
				player.getActionSender().sendCloseInterface();
				this.stop();
			}

		});
	}

	public enum ShipLocation {
		PORT_SARIM(3040, 3224, "Port Sarim"), // In the middle of the water, all
												// take off locations can be
												// seen.
		SHIP_YARD(2956, 3146, "Karamja Ship Yard"), // Just when you get off the
													// ship.
		ENTRANA(2834, 3335, "Entrana"), // Just when you get off the ship.
		CRANDOR(0, 0, "Crandor"), BRIMHAVEN(0, 0, "Brimhaven"), CAIRN_ISLE(0,
				0, "Cairn Isle"), ARDOUNGE(0, 0, "Ardounge"), PORT_KHAZARD(0,
				0, "Port Khazard"), FELDIP_HILLS(0, 0, "Feldip Hills"), APE_ATOLL(
				0, 0, "Ape Atoll"), PEST_CONTROL(0, 0, "Pest Control");
		/**
		 * The flying location.
		 */
		private ShipLocation(int x, int y, String name) {
			this.x = x;
			this.y = y;
			this.name = name;
		}

		/**
		 * The coordinates.
		 */
		private final int x, y;

		/**
		 * The locations name.
		 */
		private String name;

		/**
		 * Gets the locations name.
		 * 
		 * @return The locations name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Checks if the ship location is within distance of a location.
		 * 
		 * @return True if, false if not.
		 */
		public boolean isWithinDistance(Location loc, int distance) {
			return (loc.withinRange(x, y, distance));
		}

		/**
		 * Gets the take off location by the players location. (Region based,
		 * should probably redo)
		 * 
		 * @param loc
		 *            The players location.
		 * @return The take-off location, null if none.
		 */
		public static ShipLocation getShipLocationByLocation(Location loc) {
			if (PORT_SARIM.isWithinDistance(loc, 40)) {
				return PORT_SARIM;
			}
			if (SHIP_YARD.isWithinDistance(loc, 10)) {
				return SHIP_YARD;
			}
			if (ENTRANA.isWithinDistance(loc, 10)) {
				return ENTRANA;
			}
			if (CRANDOR.isWithinDistance(loc, 10)) {
				return CRANDOR;
			}
			if (BRIMHAVEN.isWithinDistance(loc, 10)) {
				return BRIMHAVEN;
			}
			if (CAIRN_ISLE.isWithinDistance(loc, 10)) {
				return CAIRN_ISLE;
			}
			if (ARDOUNGE.isWithinDistance(loc, 10)) {
				return ARDOUNGE;
			}
			if (PORT_KHAZARD.isWithinDistance(loc, 10)) {
				return PORT_KHAZARD;
			}
			if (FELDIP_HILLS.isWithinDistance(loc, 10)) {
				return FELDIP_HILLS;
			}
			if (APE_ATOLL.isWithinDistance(loc, 10)) {
				return APE_ATOLL;
			}
			return null;
		}
	}

}
