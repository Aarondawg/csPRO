package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

/**
 * Server-wide event to update special timers.
 * 
 * @author Graham
 * 
 */
public class SpecialUpdateEvent extends Event {

	/**
	 * Creates the event to run every 30 seconds.
	 */
	public SpecialUpdateEvent() {
		super(30000);
	}

	@Override
	public void execute() {
		for (Player p : World.getWorld().getPlayers()) {
			p.getSpecials().tick();
		}
	}

}
