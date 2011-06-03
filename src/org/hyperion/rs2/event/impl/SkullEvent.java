package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

public class SkullEvent extends Event {

	public SkullEvent() {
		super(60000);// Runs once a minute.
	}

	@Override
	public void execute() {
		for (Player p : World.getWorld().getPlayers()) {
			p.getSkulls().tick();
		}

	}

}
