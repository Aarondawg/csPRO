package org.hyperion.rs2.model;

import org.hyperion.rs2.model.region.Region;

public class GroundItem extends Item {

	/**
	 * The location of this item.
	 */
	private final Location location;

	/**
	 * The region of the item location.
	 */
	private final Region[] regions;

	/**
	 * The name of the ground item owner.
	 */
	private final String owner;

	/**
	 * A ground item have two stages, each stage is 2 minutes. Thats 240
	 * seconds.
	 */
	private int time = 240;

	/**
	 * Creates a new ground item.
	 * 
	 * @param owner
	 *            The ground item owner.
	 * @param location
	 *            The location of the ground item.
	 * @param id
	 *            The id of the item.
	 * @param amount
	 *            The amount of the item.
	 */
	public GroundItem(String owner, Location location, int id, int amount) {
		super(id, amount);
		this.location = location;
		this.owner = owner;
		this.regions = World.getWorld().getRegionManager()
				.getSurroundingRegions(location);
	}

	/**
	 * Gets the ground item location.
	 * 
	 * @return The ground item's location.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Gets the name of the item owner.
	 * 
	 * @return The item owner's name.
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Sets the ground item time.
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * Gets the ground item time.
	 * 
	 * @return The ground item time.
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Decreases the ground items time.
	 */
	public void decreaseTime() {
		this.time--;
	}

	/**
	 * Gets the ground item region array.
	 * 
	 * @return The regions of the ground item.
	 */
	public Region[] getRegions() {
		return regions;
	}

}
