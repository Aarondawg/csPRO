package org.hyperion.rs2.model;

/**
 * Represents a single item.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Item {

	/**
	 * The id.
	 */
	private int id;

	/**
	 * The number of items.
	 */
	private int count;

	/**
	 * Creates a single item.
	 * 
	 * @param id
	 *            The id.
	 */
	public Item(int id) {
		this(id, 1);
	}

	/**
	 * Creates a stacked item.
	 * 
	 * @param id
	 *            The id.
	 * @param count
	 *            The number of items.
	 * @throws IllegalArgumentException
	 *             if count is negative.
	 */
	public Item(int id, int count) {
		if (count < 0) {
			throw new IllegalArgumentException("Count cannot be negative.");
		}
		this.id = id;
		this.count = count;
	}

	/**
	 * Gets the definition of this item.
	 * 
	 * @return The definition.
	 */
	public ItemDefinition getDefinition() {
		return ItemDefinition.forId(id);
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
	 * Gets the count.
	 * 
	 * @return The count.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Sets the item count.
	 */
	public Item setCount(int am) {
		this.count = am;
		if (this.count == 0) {
			return null;
		}
		return this;
	}

	/**
	 * Increases the count of this item by a certain amount.
	 * 
	 * @param amount
	 *            The amount to increase the original count with.
	 */
	public void increaseCount(int amount) {
		count += amount;
	}

	/**
	 * Increases the count of this item.
	 */
	public void increaseCount() {
		count++;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Item)) {
			return false;
		}
		Item item = (Item) other;
		return item.getId() == id && item.getCount() == count;
	}

	@Override
	public String toString() {
		return Item.class.getName() + " [id=" + id + ", count=" + count + "]";
	}

}
