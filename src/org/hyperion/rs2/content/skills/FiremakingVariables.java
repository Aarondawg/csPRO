package org.hyperion.rs2.content.skills;

public class FiremakingVariables {

	private boolean isFiremaking = false;
	private int animationDelay;

	public void setFiremaking(boolean isFiremaking) {
		this.isFiremaking = isFiremaking;
	}

	public boolean isFiremaking() {
		return isFiremaking;
	}

	public void setAnimationDelay(int animationDelay) {
		this.animationDelay = animationDelay;
	}

	public int getAnimationDelay() {
		return animationDelay;
	}

	public void increaseAnimationDelay() {
		animationDelay++;

	}

}
