package org.hyperion.rs2.content.skills.farming;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Location;

public interface FarmingObject {

	/**
	 * This method is called once every 2.5 minute, and it handles different
	 * types of farming objects, such as farming plants, compost bins, farming
	 * paths.
	 */
	public void tick(Player player);

	/**
	 * Gets the location of this specific farming object.
	 * 
	 * @return The farming objects location.
	 */
	public Location getLocation();

	/**
	 * Gets the Array index.
	 * 
	 * @return The array index.
	 */
	public int getArrayIndex();

	/*
	 * /** Gets the object index.
	 * 
	 * @return The object index.
	 */
	// public int getObjectIndex();

	/**
	 * Spawns the farming object if its within distance.
	 * 
	 * @return True if within distance, false if not.
	 */
	public boolean spawnMe(Player player);

}
