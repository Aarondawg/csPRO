package org.hyperion.rs2.content.skills;

/**
 * Holding all the players cooking variables.
 */

public class CookingVariables {

	private boolean grainHopper = false;

	/**
	 * Defines if the grain hopper is full or not.
	 */
	public boolean isUsingGrainHopper() {
		return grainHopper;
	}

	/**
	 * Sets whenever or not theres grain in the grain hopper.
	 */
	public void setUsingGrainHopper(boolean b) {
		this.grainHopper = b;
	}

	private int flourBinAmount = 0;

	/**
	 * Gets the amount of floor in the flour bin.
	 * 
	 * @return the flour bin amount.
	 */
	public int getFlourBinAmount() {
		return flourBinAmount;
	}

	/**
	 * Increases the flour bin count.
	 */
	public void increaseFlourInFlourBin() {
		flourBinAmount++;
	}

	/**
	 * Decreases the flour bin count.
	 */
	public void decreaseFlourInFlourBin() {
		flourBinAmount--;
	}

	/**
	 * Resets the flour bin amount.
	 */
	public void resetFloorInFlourBin() {
		flourBinAmount = 0;
	}

	public void setCookingItem(int cookingItem) {
		this.cookingItem = cookingItem;
	}

	public int getCookingItem() {
		return cookingItem;
	}

	/**
	 * The cooking id we put on the interface.
	 */
	private int cookingItem = -1;

	private boolean isCookingOnFire = false;

	public boolean isCookingOnFire() {
		return isCookingOnFire;
	}

	public void setCookingOnFire(boolean cookingOnFire) {
		this.isCookingOnFire = cookingOnFire;
	}

}
