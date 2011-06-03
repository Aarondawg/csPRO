package org.hyperion.rs2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hyperion.rs2.content.minigames.FightCaves;
import org.hyperion.rs2.model.region.Region;

/**
 * Represents a single location in the game world.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Location {

	/**
	 * The x coordinate.
	 */
	private final int x;

	/**
	 * The y coordinate.
	 */
	private final int y;

	/**
	 * The z coordinate.
	 */
	private final int z;

	/**
	 * Creates a location.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param z
	 *            The z coordinate.
	 * @return The location.
	 */
	public static Location create(int x, int y, int z) {
		return new Location(x, y, z);
	}

	/**
	 * Creates a location.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param z
	 *            The z coordinate.
	 */
	private Location(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Gets the absolute x coordinate.
	 * 
	 * @return The absolute x coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the absolute y coordinate.
	 * 
	 * @return The absolute y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the z coordinate, or height.
	 * 
	 * @return The z coordinate.
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Gets the local x coordinate relative to this region.
	 * 
	 * @return The local x coordinate relative to this region.
	 */
	public int getLocalX() {
		return getLocalX(this);
	}

	/**
	 * Gets the local y coordinate relative to this region.
	 * 
	 * @return The local y coordinate relative to this region.
	 */
	public int getLocalY() {
		return getLocalY(this);
	}

	/**
	 * Gets the local x coordinate relative to a specific region.
	 * 
	 * @param l
	 *            The region the coordinate will be relative to.
	 * @return The local x coordinate.
	 */
	public int getLocalX(Location l) {
		return x - 8 * (l.getRegionX() - 6);
	}

	/**
	 * Gets the local y coordinate relative to a specific region.
	 * 
	 * @param l
	 *            The region the coordinate will be relative to.
	 * @return The local y coordinate.
	 */
	public int getLocalY(Location l) {
		return y - 8 * (l.getRegionY() - 6);
	}

	/**
	 * Gets the region x coordinate.
	 * 
	 * @return The region x coordinate.
	 */
	public int getRegionX() {
		return x >> 3;
	}

	/**
	 * Gets the region y coordinate.
	 * 
	 * @return The region y coordinate.
	 */
	public int getRegionY() {
		return y >> 3;
	}

	/**
	 * Checks if this location is within range of another.
	 * 
	 * @param other
	 *            The other location.
	 * @return <code>true</code> if the location is in range, <code>false</code>
	 *         if not.
	 */
	public boolean isWithinDistance(Location other) {
		if (z != other.z) {
			return false;
		}
		int deltaX = other.x - x, deltaY = other.y - y;
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
	}

	@Override
	public int hashCode() {
		return z << 30 | x << 15 | y;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Location)) {
			return false;
		}
		Location loc = (Location) other;
		return loc.x == x && loc.y == y && loc.z == z;
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "," + z + "]";
	}

	/**
	 * Creates a new location based on this location.
	 * 
	 * @param diffX
	 *            X difference.
	 * @param diffY
	 *            Y difference.
	 * @param diffZ
	 *            Z difference.
	 * @return The new location.
	 */
	public Location transform(int diffX, int diffY, int diffZ) {
		return Location.create(x + diffX, y + diffY, z + diffZ);
	}

	/**
	 * Gets the wilderness level of a specific location (l)
	 */
	public static int getWildernessLevel(Location l) {
		if (l.getX() >= 2944 && l.getX() < 3392 && l.getY() >= 3520
				&& l.getY() < 6400) {
			return 1 + (l.getY() - 3520) / 8;
		} else {
			return 0;
		}
	}

	/**
	 * Calculate the distance between a player and a point.
	 * 
	 * @param p
	 *            A point.
	 * @return The square distance.
	 */
	public double getDistance(Location other) {
		int xdiff = this.getX() - other.getX();
		int ydiff = this.getY() - other.getY();
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	/**
	 * Checks if a specific set of coordinates is in a multi area.
	 * 
	 * @return <code>true</code> if the coordinate set is in a multi area,
	 *         <code>false</code> if not.
	 */
	public boolean isInMulti() {
		if ((x >= 3029 && x <= 3374 && y >= 3759 && y <= 3903)
				|| (x >= 2250 && x <= 2280 && y >= 4670 && y <= 4720)
				|| (x >= 3198 && x <= 3380 && y >= 3904 && y <= 3970)
				|| (x >= 3191 && x <= 3326 && y >= 3510 && y <= 3759)
				|| (x >= 2987 && x <= 3006 && y >= 3912 && y <= 3937)
				|| (x >= 2245 && x <= 2295 && y >= 4675 && y <= 4720)
				|| (x >= 2450 && x <= 3520 && y >= 9450 && y <= 9550)
				|| (x >= 3006 && x <= 3071 && y >= 3602 && y <= 3710)
				|| (x >= 3134 && x <= 3192 && y >= 3519 && y <= 3646)) {
			return true;
		} else if (inPestControlGame()) {
			return true;
		} else if (isInFightCaves()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Cba remaking it. ;D
	 */
	public static boolean isInMulti(int x, int y, int z) {
		return create(x, y, z).isInMulti();
	}

	/**
	 * Checks if an entity is within a specific radius.
	 * 
	 * @param rad
	 *            The radius.
	 * @return True if we're within distance/range, false if not.
	 */
	public boolean withinRange(Entity e, int rad) {
		if (e == null)
			return false;
		return withinRange(e.getLocation(), rad);
	}

	/**
	 * Checks if we're within combat range.
	 * 
	 * @param dist
	 *            The distance.
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public boolean isWithinInteractingRange(Entity e, Entity target, int dist) {
		int eSize = e.getSize();
		int tSize = target.getSize();
		Location loc = getClosestSpot(e.getLocation(),
				getValidSpots(tSize, target.getLocation()));
		if (loc == null) {
			System.out.println("Location was null...");
			return false;
		} else {
			return withinRange(loc, dist + Math.round(eSize / 2) - 1);
		}
	}

	/**
	 * Checks if a specific location is within a specific radius.
	 * 
	 * @param rad
	 *            The radius.
	 * @return True if we're within distance/range, false if not.
	 */
	public boolean withinRange(Location p, int rad) {
		if (p == null) {
			return false;
		}
		int dX = Math.abs(x - p.x);
		int dY = Math.abs(y - p.y);
		return dX <= rad && dY <= rad;
	}

	/**
	 * Checks if a specific location is within a specific radius.
	 * 
	 * @param rad
	 *            The radius.
	 * @return True if we're within distance/range, false if not.
	 */
	public boolean withinRange(int x1, int y1, int rad) {
		int dX = Math.abs(x - x1);
		int dY = Math.abs(y - y1);
		return dX <= rad && dY <= rad;
	}

	/**
	 * Checks if we're in a specific arena based on location objects.
	 * 
	 * @param minLocation
	 *            The min location to check.
	 * @param maxLocation
	 *            The max location to check.
	 * @return True if we're in the area, false it not.
	 */
	public boolean isInArea(Location minLocation, Location maxLocation) {
		return isInArea(x, y, z, minLocation.getX(), minLocation.getY(),
				minLocation.getZ(), maxLocation.getX(), maxLocation.getY(),
				maxLocation.getZ());
	}

	/**
	 * Checks if we're in a specific arena based on simple coordinates.
	 * 
	 * @param minX
	 *            The minimum x coordinate.
	 * @param minY
	 *            The minimum y coordinate.
	 * @param minHeight
	 *            the minimum height.
	 * @param maxX
	 *            The maximum x coordinate.
	 * @param maxY
	 *            The maximum y coordinate.
	 * @param maxHeight
	 *            The maximum height.
	 * @return True if we're in the area, false it not.
	 */
	public static boolean isInArea(int x, int y, int z, int minX, int minY,
			int minHeight, int maxX, int maxY, int maxHeight) {
		if (z != minHeight || z != maxHeight) {
			return false;
		}
		return (x >= minX && y >= minY) && (x <= maxX && y <= maxY);
	}

	/**
	 * Checks if we're within distance of another coordinate set.
	 * 
	 * @param x2
	 *            The x coordinate
	 * @param y2
	 *            The y coordinate.
	 * @param z2
	 *            The height.
	 * @return True if, false if not.
	 */
	public boolean isWithinDistance(int x2, int y2, int z2) {
		if (z2 != z) {
			return false;
		}
		int deltaX = x2 - x, deltaY = y2 - y;
		return deltaX <= 14 && deltaX >= -15 && deltaY <= 14 && deltaY >= -15;
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
	public static Location getClosestSpot(Location target, Location[] steps) {
		Location closestStep = null;
		for (Location p : steps) {
			if (closestStep == null
					|| (getDistance(closestStep, target) > getDistance(p,
							target))) {
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
		Location[] list = new Location[size * 4];
		int index = 0;
		for (int i = 0; i < size; i++) {
			list[index++] = (new Location(location.getX() - 1, location.getY()
					+ i, location.getZ()));
			list[index++] = (new Location(location.getX() + i,
					location.getY() - 1, location.getZ()));
			list[index++] = (new Location(location.getX() + i, location.getY()
					+ size, location.getZ()));
			list[index++] = (new Location(location.getX() + size,
					location.getY() + i, location.getZ()));
		}
		return list;
	}

	public static double getDistance(Location p, Location p2) {
		return Math.sqrt((p2.getX() - p.getX()) * (p2.getX() - p.getX())
				+ (p2.getY() - p.getY()) * (p2.getY() - p.getY()));
	}

	/*
	 * public boolean isInWilderness() { if((x > 2941 && x < 3392 && y > 3518 &&
	 * y < 3966) || (x > 3009 && x < 3060 && y > 10303 && y < 10356)){ return
	 * true; } return false; }
	 */

	public boolean isInWilderness() {
		return x >= 2944 && x < 3392 && y >= 3520 && y < 6400;
	}

	/**
	 * The following methods are all found by Ryan.
	 * 
	 * @author Thing1.
	 */

	public boolean isInBarrows() {
		return x >= 3522 && x <= 3583 && y >= 9675 && y <= 9720/*
																 * || x >= 3546
																 * && x <= 3583
																 * && y >= 3269
																 * && y <= 3308
																 */;
	}

	public boolean dharoksMound() {
		return x >= 3570 && x <= 3576 && y >= 3293 && y <= 3299;
	}

	public boolean veracsMound() {
		return x >= 3554 && x <= 3559 && y >= 3294 && y <= 3300;
	}

	public boolean ahrimsMound() {
		return x >= 3561 && x <= 3567 && y >= 3286 && y <= 3292;
	}

	public boolean toragsMound() {
		return x >= 3550 && x <= 3555 && y >= 3280 && y <= 3285;
	}

	public boolean karilsMound() {
		return x >= 3563 && x <= 3568 && y >= 3273 && y <= 3279;
	}

	public boolean guthansMound() {
		return x >= 3574 && x <= 3579 && y >= 3279 && y <= 3284;
	}

	/**
	 * Gets a more proper location based on the size of an entity.
	 * 
	 * @param size
	 *            1 for players, npc.getDefinition().getSize() for npcs.
	 * @return A proper location.
	 */
	public Location getActualLocation(int size) {
		if (size == 1) {
			return this;
		}
		size = Math.round(size / 2);
		return transform(size, size, 0);
	}

	/**
	 * Checks if a player is in a duel arena, and has the "Challenge" option.
	 */
	public boolean isInDuelArena() {
		if (z != 0) {
			return false;
		}
		return (x >= 3322 && x < 3394 && y > 3195 && y < 3291)
				|| (x > 3311 && x < 3323 && y > 3223 && y < 3248);
	}

	/**
	 * Checks if a Player is inside the actual arena (fighting each other).
	 */
	public boolean isDueling() {
		/*
		 * Eastern normal arena.
		 */
		if (x >= 3363 && y >= 3225 && x <= 3389 && y <= 3239) {
			if ((x != 2263 && y != 3239) && (x != 3363 && y != 3225)) {
				return true;
			}
		}

		/*
		 * Western obstacle arena.
		 */
		if (x >= 3332 && y >= 3225 && x <= 3358 && y <= 3239) {
			if ((x != 3358 && y != 3239) & (x != 3358 && y != 3225)) {
				return true;
			}
		}

		// TODO: Get more arenas, only 4 to go!

		return false;
	}

	/**
	 * This is called on login, to check if you're in an illegal area. If so,
	 * it'll return a new location, which the players location is set to. Its
	 * also used to check for illegal places to teleport away from :):)
	 */
	public Location getNewArea(Player player) {
		// TODO: Small random factors for those?
		if (isDueling()) {
			return create(3356, 3268, 0);
		}
		if (inPestControlGame()) {
			return create(2658, 2649, 0);
		}
		/*
		 * Warriors guild ingame.. :)
		 */
		if (isInArea(x, y, z, 2837, 3533, 2, 2878, 3557, 2)
				&& !isInArea(x, y, z, 2838, 3537, 2, 2846, 3542, 2)) {
			return create(2844, 3539, 2);
		}
		if (isInFightCaves()) {
			return player.getFightCaves().getLoginLocation();
		}
		return null;
	}

	public boolean inPestControlBoat() {
		return (x >= 2660 && x <= 2663 && y >= 2638 && y <= 2643);
	}

	public boolean inPestControlGame() {
		return (x >= 2624 && x <= 2690 && y >= 2550 && y <= 2625);
	}

	public boolean multiZone() {
		return x >= 3287 && x <= 3298 && y >= 3167 && y <= 3178 || x >= 3070
				&& x <= 3095 && y >= 3405 && y <= 3448 || x >= 2961
				&& x <= 2981 && y >= 3330 && y <= 3354 || x >= 2510
				&& x <= 2537 && y >= 4632 && y <= 4660 || x >= 3012
				&& x <= 3066 && y >= 4805 && y <= 4858 || x >= 2794
				&& x <= 2813 && y >= 9281 && y <= 9305 || x >= 3546
				&& x <= 3557 && y >= 9689 && y <= 9700 || x >= 2708
				&& x <= 2729 && y >= 9801 && y <= 9829 || x >= 3450
				&& x <= 3525 && y >= 9470 && y <= 9535 || x >= 3207
				&& x <= 3395 && y >= 3904 && y <= 3903 || x >= 3006
				&& x <= 3072 && y >= 3611 && y <= 3712 || x >= 3149
				&& x <= 3395 && y >= 3520 && y <= 4000 || x >= 2365
				&& x <= 2420 && y >= 5065 && y <= 5120 || x >= 2890
				&& x <= 2935 && y >= 4425 && y <= 4470 || x >= 2250
				&& x <= 2290 && y >= 4675 && y <= 4715 || x >= 2690
				&& x <= 2825 && y >= 2680 && y <= 2810;
	}

	public boolean isInFightCaves() {
		return isInArea(x, y, z, FightCaves.minLocation.getX(),
				FightCaves.minLocation.getY(), z,
				FightCaves.maxLocation.getX(), FightCaves.maxLocation.getY(), z);
	}

	public boolean hasLineOfSight(Location location) {
		if (this.getZ() != location.getZ()) {
			return false;
		}
		int rand = new Random().nextInt(30);
		boolean bool = rand < 5;
		return bool;
		/*
		 * List<Region> regions = new ArrayList<Region>(); for(final Region r :
		 * World.getWorld().getRegionManager().getSurroundingRegions(this)) {
		 * regions.add(r); } for(final Region r :
		 * World.getWorld().getRegionManager().getSurroundingRegions(location))
		 * { if(!regions.contains(r)) { regions.add(r); } } if(this.getY() -
		 * location.getY() != 0 && (this.getX() - location.getX()) != 0) {
		 * double a = (this.getY() - location.getY() / (this.getX() -
		 * location.getX())); double b = location.getY() - (a *
		 * location.getX()); for(final Region r : regions) { for(final
		 * GameObject o : r.getGameObjects()) { if(o.getType() != 22 &&
		 * o.getLocation().getZ() == this.getZ()) { double calc =
		 * o.getLocation().getY() - (a * o.getLocation().getX() + b); if(calc >
		 * -0.5 && calc < 0.5) { return false; } } } } } else {
		 * 
		 * } return true;
		 */
	}

}
