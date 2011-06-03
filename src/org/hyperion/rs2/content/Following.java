package org.hyperion.rs2.content;

import java.util.ArrayList;
import java.util.List;

import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.region.Region;

public class Following {

	public static boolean handleCombatFollowing(NPC npc, Entity target,
			int attackingDistance) {
		// System.out.println("Handle combat following.." + npc);
		if (target == null) {
			return false;
		}
		if (!npc.canWalk()) {
			return true; // We're still in combat I guess?
		}
		final double dist = target.getLocation().getDistance(npc.getLocation());
		if (dist <= target.getSize() + npc.getDefinition().getSize()
				+ attackingDistance
				&& !npc.getWalkingQueue().isEmpty()) {
			return true;
		}
		Location closest = Location.getClosestSpot(npc.getLocation(),
				Location.getValidSpots(target.getSize(), target.getLocation()));

		return walkTowards(npc, closest == null ? target.getLocation()
				: closest);
	}

	public static boolean walkTowards(NPC npc, Location target) {
		if (!npc.canWalkToArea(target)) {
			return false;
		}
		/*
		 * Check if we're already standing at our target.
		 */
		if (npc.getLocation().equals(target)) {
			return true;
		}

		/*
		 * We get the closest of our own surrounding coordinates, to the target.
		 */
		Location[] validSpots = getValidSpots(npc.getSize(), npc.getLocation());
		List<Location> ignore = new ArrayList<Location>();
		Location closest = null;
		for (int i = 0; i < 3/* Three closest spots.. */; i++) {
			closest = getClosestSpot(target, validSpots, ignore);
			ignore.add(closest);
			if (closest != null) {
				Region r = World.getWorld().getRegionManager()
						.getRegionByLocation(closest);
				boolean clip = false;
				/*
				 * We make sure the next step is clear of other NPC's / Players.
				 */
				for (Entity e : r.getEntities()) {
					if (e.getLocation().getZ() == closest.getZ()) {
						for (int xOffset1 = 1; xOffset1 < npc.getSize(); xOffset1++) {
							for (int yOffset1 = 1; yOffset1 < npc.getSize(); yOffset1++) {
								for (int xOffset2 = 1; xOffset2 < e.getSize(); xOffset2++) {
									for (int yOffset2 = 1; yOffset2 < e
											.getSize(); yOffset2++) {
										if (e.getLocation().getX() + xOffset2 == closest
												.getX() + xOffset1
												&& e.getLocation().getY()
														+ yOffset2 == closest
														.getY() + yOffset1) {
											// System.out.println("Attempting to clip: "
											// + npc + " " + o);
											clip = true;
											break;
										}
									}
								}
							}
						}

					}
				}
				if (!clip) {
					/*
					 * Then we check if there is an object in the road..
					 */
					for (GameObject o : r.getGameObjects()) {
						if (o.getType() != 22) {
							/*
							 * This works for all NPCs with the size of one..
							 * (Basicly checks the actual coordinate of the NPC)
							 */
							if (o.getLocation().equals(closest)) {
								// System.out.println("Attempting to clip: " +
								// npc + " " + o);
								clip = true;
								break;
							}
							/*
							 * If the size is bigger than one (TzTokJad, Hill
							 * Giants etc) we do a few nasty for loops..
							 */
							if (o.getLocation().getZ() == npc.getLocation()
									.getZ()) {
								for (int xOffset = 1; xOffset < npc.getSize(); xOffset++) {
									for (int yOffset = 1; yOffset < npc
											.getSize(); yOffset++) {
										if (o.getLocation().getX() == closest
												.getX() + xOffset
												&& o.getLocation().getY() == closest
														.getY() + yOffset) {
											// System.out.println("Attempting to clip: "
											// + npc + " " + o);
											clip = true;
											break;
										}
									}
								}
							}
						}
					}
				}
				if (clip) {
					closest = null;// Makes sure we aren't walking towards it..
				} else {
					break; // We have the closest, walkable target and we break
							// the loop.
				}
			}
		}
		/*
		 * Which is the coordinate we want to walk towards the next time.
		 */
		if (closest != null
				&& closest.getDistance(target) < npc.getLocation().getDistance(
						target)) {
			/*
			 * Discard previous walking queue
			 */
			npc.getWalkingQueue().reset();

			/*
			 * Add the path
			 */
			npc.getWalkingQueue().addStep(closest.getX(), closest.getY());

			/*
			 * Finish the walking queue.
			 */
			npc.getWalkingQueue().finish();
		} else {
			System.out.println("NPC never walked.");
		}
		return true;
	}

	/**
	 * Gets the closest spot from a list of locations.
	 * 
	 * @param steps
	 *            The list of steps.
	 * @param location
	 *            The location we want to be close to.
	 * @return The closest location.
	 */
	public static Location getClosestSpot(Location target, Location[] steps,
			List<Location> ignore) {
		Location closestStep = null;
		for (Location p : steps) {
			if (closestStep == null
					|| (Location.getDistance(closestStep, target) > Location
							.getDistance(p, target)) && !ignore.contains(p)) {
				// if (RS2RegionLoader.positionIsWalkalble(e, p.getX(),
				// p.getY())) {
				// System.out.println("Setting walkable pos..");
				closestStep = p;
				// }
			}
		}
		return closestStep;
	}

	/**
	 * Gets a list of all the valid spots around another location, within a
	 * specific "size/range".
	 * 
	 * @param size
	 *            The size/range.
	 * @param location
	 *            The location we want to get locations within range from.
	 */
	public static Location[] getValidSpots(int size, Location location) {
		Location[] list = new Location[(size * 4) + 4];
		int index = 0;
		list[index++] = (Location.create(location.getX() - 1,
				location.getY() - 1, location.getZ()));
		list[index++] = (Location.create(location.getX() + size,
				location.getY() - 1, location.getZ()));
		list[index++] = (Location.create(location.getX() + size,
				location.getY() + size, location.getZ()));
		list[index++] = (Location.create(location.getX() - 1, location.getY()
				+ size, location.getZ()));
		for (int i = 0; i < size; i++) {
			list[index++] = (Location.create(location.getX() - 1,
					location.getY() + i, location.getZ()));
			list[index++] = (Location.create(location.getX() + i,
					location.getY() - 1, location.getZ()));
			list[index++] = (Location.create(location.getX() + i,
					location.getY() + size, location.getZ()));
			list[index++] = (Location.create(location.getX() + size,
					location.getY() + i, location.getZ()));
		}
		return list;
	}

}
