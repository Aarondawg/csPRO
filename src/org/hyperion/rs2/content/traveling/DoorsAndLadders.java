package org.hyperion.rs2.content.traveling;

import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.content.skills.Cooking;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

public class DoorsAndLadders {

	private static final Map<Location, Location[][]> APPROVED_LOCATIONS = new HashMap<Location, Location[][]>();
	private static final Animation CLIMB_UP_LADDERS = Animation.create(828);
	private static final Animation CLIMB_DOWN_LADDERS = Animation.create(827);

	private static boolean handleLadderClicking(Player player, GameObject o,
			int option) {
		switch (option) {
		/*
		 * Depending on the id, this is either climb up, climb down, or choose
		 * lol.
		 */
		case 1:
			switch (o.getDefinition().getId()) {
			/*
			 * Climb up ladders. Currently this just puts you one height level
			 * up.
			 */
			case 272: // Ship ladder.
			case 1747: // To the spinning wheel in Camelot
			case 12964: // Lumbridge mill.
			case 1738: // Warrior guild stairs.
			case 15638: // Warrior guild stairs.
				climbUp(player, o.getLocation(), 0);
				return true;
				/*
				 * Climb down ladders. Currently this just puts you one height
				 * level down.
				 */
			case 273: // Ship ladder.
			case 1746:
			case 12966: // Lumbridge mill.
			case 1765: // King Black Dragon entrance ladder.
				climbDown(player, o.getLocation(), 0);
				return true;
				/*
				 * Choose whenever to go up, down or nowhere. //TODO: Finish
				 * this eventually.
				 */
			case 12965:
			case 1739: // Warriors Guild
				player.getActionSender().sendMessage(
						"Nothing interesting happens.");
				break;
			case 11727: // White knights castle ones..
				player.playAnimation(CLIMB_UP_LADDERS);
				int xOffset = 0;
				int yOffset = 0;
				System.out.println(o.getRotation());
				switch (o.getRotation()) {
				case 0:
					yOffset = -1;
					break;
				case 1:
					xOffset = -1;
					break;
				case 2:
					yOffset = 1;
					break;
				case 3:
					xOffset = 1;
					break;
				}
				if (player.getLocation().getZ() <= 3) {
					player.setTeleportTarget(Location.create(o.getLocation()
							.getX() + xOffset,
							o.getLocation().getY() + yOffset, o.getLocation()
									.getZ() + 1));
				}
				break;
			case 11728:// White knights castle ones..
				player.playAnimation(CLIMB_DOWN_LADDERS);
				xOffset = 0;
				yOffset = 0;
				System.out.println(o.getRotation());
				switch (o.getRotation()) {
				case 0:
					yOffset = -1;
					break;
				case 1:
					xOffset = -1;
					break;
				case 2:
					yOffset = 1;
					break;
				case 3:
					xOffset = 1;
					break;
				}
				if (player.getLocation().getZ() > 0) {
					player.setTeleportTarget(Location.create(o.getLocation()
							.getX() + xOffset,
							o.getLocation().getY() + yOffset, o.getLocation()
									.getZ() - 1));
				}
				break;
			}
			break;
		/*
		 * Climb up ladders. Currently this just puts you one height level up.
		 */
		case 2:
			switch (o.getDefinition().getId()) {
			case 1739: // Warriors Guild
			case 12965:
				climbUp(player, o.getLocation(), 1);
				return true;
			}
			break;
		/*
		 * Climb down ladders. Currently this just puts you one height level
		 * down.
		 */
		case 3:
			switch (o.getDefinition().getId()) {
			case 12965:
			case 1739: // Warriors Guild
				climbDown(player, o.getLocation(), 0);
				return true;
			}
			break;
		}

		return false;
	}

	/**
	 * Simple method that will make the player climb down one height level.
	 */
	private static void climbDown(final Player player, Location loc, int index) {
		Location[][] locs = APPROVED_LOCATIONS.get(loc);
		if (locs != null) {
			boolean bad = true;
			for (Location l : locs[0]) {
				if (l.equals(player.getLocation())) {
					bad = false;
				}
			}
			if (bad) {
				player.getActionSender().sendMessage("I can't reach that.");
				return;
			}
			player.playAnimation(CLIMB_DOWN_LADDERS);
			player.setTeleportTarget(locs[1][index]);
		}

		if (loc.getX() == 3164 && loc.getY() == 3307) { // Lumbridge mill
														// things.
			World.getWorld().submit(new Event(300) {

				@Override
				public void execute() {
					Cooking.sendFlourBins(player);
					this.stop();

				}

			});

		}
	}

	/**
	 * Simple method that will make the player climb up one height level.
	 */
	private static void climbUp(Player player, Location loc, int index) {
		Location[][] locs = APPROVED_LOCATIONS.get(loc);
		if (locs != null) {
			boolean bad = true;
			for (Location l : locs[0]) {
				if (l.equals(player.getLocation())) {
					bad = false;
				}
			}
			if (bad) {
				player.getActionSender().sendMessage("I can't reach that.");
				return;
			}
			player.playAnimation(CLIMB_UP_LADDERS);
			player.setTeleportTarget(locs[1][index]);

		}
	}

	public static boolean handle(Player player, GameObject o, int option) {
		if (handleLadderClicking(player, o, option)) {
			return true;
		}
		if (DoorManager.handleDoor(player, o.getLocation(), o.getDefinition()
				.getId())) {
			return true;
		}
		return false;
	}

	/**
	 * This populates the map.
	 */
	static {
		/*
		 * Seers village - upstairs with the spinning wheel.
		 */
		APPROVED_LOCATIONS.put(
				Location.create(2715, 3470, 0),
				new Location[][] {
						/*
						 * Those are the valid take off's.
						 */
						new Location[] { Location.create(2714, 3470, 0),
								Location.create(2715, 3471, 0) },

						/*
						 * This is where we end up.
						 */
						new Location[] { Location.create(2715, 3471, 1) } });
		APPROVED_LOCATIONS.put(
				Location.create(2715, 3470, 1),
				new Location[][] {
						/*
						 * Those are the valid take off's.
						 */
						new Location[] { Location.create(2714, 3470, 1),
								Location.create(2715, 3471, 1) },

						/*
						 * This is where we end up.
						 */
						new Location[] { Location.create(2715, 3471, 0) } });

		/*
		 * King black dragon entrance.
		 */
		APPROVED_LOCATIONS.put(Location.create(3017, 3849, 0),
				new Location[][] {
				/*
				 * Those are the valid take off's.
				 */
				new Location[] { Location.create(3017, 3850, 0), },

				/*
				 * This is where we end up.
				 */

				new Location[] { Location.create(3069, 10255, 0) } });
		/*
		 * Warrior guild ladder one.
		 */
		APPROVED_LOCATIONS.put(
				Location.create(2839, 3537, 0),
				new Location[][] {
						/*
						 * Those are the valid take off's.
						 */
						new Location[] {
						/*
						 * Buttom floor
						 */
						Location.create(2841, 3537, 0),
								Location.create(2841, 3538, 0),
								Location.create(2840, 3539, 0),
								Location.create(2839, 3539, 0),
								Location.create(2838, 3538, 0),
								Location.create(2838, 3537, 0), },

						/*
						 * This is where we end up.
						 */

						new Location[] { Location.create(2840, 3538, 1), } });
		APPROVED_LOCATIONS.put(
				Location.create(2839, 3537, 1),
				new Location[][] {
						/*
						 * Those are the valid take off's.
						 */
						new Location[] {
						/*
						 * Second floor
						 */
						Location.create(2840, 3539, 1),
								Location.create(2841, 3538, 1),
								Location.create(2841, 3539, 1),
								Location.create(2840, 3538, 1), },

						/*
						 * This is where we end up.
						 */
						new Location[] { Location.create(2840, 3538, 0),
								Location.create(2840, 3539, 2), } });
		APPROVED_LOCATIONS.put(Location.create(2840, 3538, 2),
				new Location[][] {
				/*
				 * Those are the valid take off's.
				 */
				new Location[] {
				/*
				 * Third floor
				 */
				Location.create(2840, 3539, 2), },

				/*
				 * This is where we end up.
				 */

				new Location[] { Location.create(2840, 3538, 1), } });
	}

}
