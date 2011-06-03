package org.hyperion.rs2.content;

import java.util.Random;

import org.hyperion.rs2.model.Item;

/**
 * Represents a single npc drop.
 * 
 * @author Brown.
 */

public class NPCDropItem {

	/**
	 * The random instance to get the item count.
	 */
	private static final Random r = new Random();

	/**
	 * The id.
	 */
	private final int id;

	/**
	 * Indicates if this NPCDrop should always be dropped.
	 */
	private final boolean shouldAlwaysDrop;

	/**
	 * Array holding all the amounts of this item.
	 */
	private final int[] count;

	/**
	 * The highest chance of getting this item.
	 */
	private final int highestChance;

	/**
	 * The lowest chance of getting this item.
	 */
	private final int lowestChance;

	public NPCDropItem(int i, int[] js) {
		this.id = i;
		this.count = js;
		this.shouldAlwaysDrop = false;
		this.lowestChance = 0;
		this.highestChance = 0;
	}

	public NPCDropItem(int id2, int[] is, boolean b) {
		this.id = id2;
		this.count = is;
		this.shouldAlwaysDrop = b;
		this.lowestChance = 0;
		this.highestChance = 0;
	}

	/**
	 * Gets the highest chance for this item.
	 * 
	 * @return The highest chance for this item.
	 */
	public int getHighestChance() {
		return highestChance;
	}

	/**
	 * Gets the lowest chance for this item.
	 * 
	 * @return The lowest chance for this item.
	 */
	public int getLowestChance() {
		return lowestChance;
	}

	/**
	 * Gets an item, with a random amount based on the array.
	 * 
	 * @return An item, with a random amount and the id of this class.
	 */
	public Item getItem() {
		return new Item(id, count[r.nextInt(count.length)]);
	}

	/**
	 * Gets the item id.
	 * 
	 * @return The item id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Indicates if this item always drops.
	 * 
	 * @return true if, false if not.
	 */
	public boolean shouldAlwaysDrop() {
		return shouldAlwaysDrop;
	}

}
