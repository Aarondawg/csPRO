package org.hyperion.rs2.content.skills.agility.impl;

import org.hyperion.rs2.model.Player;

public interface Obstacle {

	public void climb(final Player player);

	public int getObjectOption();

}
