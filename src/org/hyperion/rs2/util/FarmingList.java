package org.hyperion.rs2.util;

import org.hyperion.rs2.content.skills.farming.FarmingObject;
import org.hyperion.rs2.model.Player;

/**
 * A simple auto increasing array for Farming objects only.
 * 
 * @author Brown.
 */

public class FarmingList {

	private FarmingObject[] list = new FarmingObject[0];

	/**
	 * Adds an object to the AList.
	 * 
	 * @param farmingObject
	 *            The element we want to add to the AList.
	 */
	public void put(FarmingObject farmingObject) {
		FarmingObject[] list = new FarmingObject[this.list.length + 1];
		for (int i = 0; i < this.list.length; i++) {
			list[i] = this.list[i];
		}
		list[this.list.length] = farmingObject;
		this.list = list;
	}

	/**
	 * Removes an FarmingPath from the AList.
	 * 
	 * @param element
	 *            The element we want to add to the AList.
	 * @return Returns true if we sucessfully added the element, false if not.
	 */
	public boolean delete(FarmingObject element) {
		FarmingObject[] list = new FarmingObject[this.list.length - 1];
		FarmingObject[] save = this.list;
		int index = 0;
		try {
			for (FarmingObject o : this.list) {
				if (o != element) {
					list[index] = this.list[index];
					index++;
				}
			}
			return true;
		} catch (IndexOutOfBoundsException e) { // Happends if we none of the
												// FarmingPaths in the list
												// matches the element.
			this.list = save;
			return false;
		}
	}

	/**
	 * Replaces a specific farming object with a new one.
	 * 
	 * @param oldOne
	 *            The old farming object.
	 * @param newOne
	 *            The new farming object.
	 * @return True if we successfully replaced the object, false if not.
	 */
	public boolean replace(FarmingObject oldOne, FarmingObject newOne) {
		for (int i = 0; i < list.length; i++) {
			if (list[i] == oldOne) {
				list[i] = newOne;
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets a single FarmingPath from the list.
	 * 
	 * @param index
	 *            The index we want to get.
	 * @return The FarmingPath based on the index.
	 */
	public FarmingObject get(int index) {
		return list[index];
	}

	/**
	 * Sets the length of the path array. This should be used when we readd all
	 * farming paths to the array on start up. WARNING: This will delete all
	 * content from the array aswell.
	 */
	public void setSize(int size) {
		this.list = new FarmingObject[size];
	}

	/**
	 * Sets a specific index with some value.
	 */
	public void set(int index, FarmingObject value) {
		list[index] = value;
		// TODO Auto-generated method stub

	}

	/**
	 * Gets the length of the AList
	 * 
	 * @return The FarmingPathList length
	 */
	public int getSize() {
		return list.length;
	}

	/**
	 * Gets a copy of the FarmingPathList as an array.
	 * 
	 * @return A copy of the AList.
	 */
	public FarmingObject[] getArray() {
		return list;
	}

	/**
	 * Loops through the entire list of farming objects, and calls the tick
	 * method.
	 * 
	 * @param player
	 *            The player we're ticking the farming list for.
	 */
	public void tick(Player player) {
		for (FarmingObject fo : list) {
			fo.tick(player);
		}
	}

}
