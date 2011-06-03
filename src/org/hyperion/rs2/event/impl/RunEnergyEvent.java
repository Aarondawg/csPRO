package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

/**
 * Handles the regaining of player energy.
 * 
 * @author Graham
 * 
 */
public class RunEnergyEvent extends Event {

	/**
	 * Creates the server-wide run event to run every 2 seconds.
	 */
	public RunEnergyEvent() {
		super(2000);
	}

	@Override
	public void execute() {
		for (Player p : World.getWorld().getPlayers()) {
			if ((p.getWalkingQueue().isRunningToggled() || p.getWalkingQueue()
					.isRunning()) && p.getSprites().getSecondarySprite() != -1) {
				continue;
			} else {
				if (p.getRunEnergy() < 100) {
					p.setRunEnergy(p.getRunEnergy() + 1);
				}
			}
		}
	}

}
