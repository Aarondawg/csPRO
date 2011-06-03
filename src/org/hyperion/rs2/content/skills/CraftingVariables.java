package org.hyperion.rs2.content.skills;

import org.hyperion.rs2.content.skills.Crafting.DragonLeatherType;

public class CraftingVariables {

	private boolean isCrafting = false;
	private DragonLeatherType leatherType;

	public void setCrafting(boolean isCrafting) {
		this.isCrafting = isCrafting;
	}

	public boolean isCrafting() {
		return isCrafting;
	}

	public void setLeatherType(DragonLeatherType leatherType) {
		this.leatherType = leatherType;
	}

	public DragonLeatherType getLeatherType() {
		return leatherType;
	}

}
