package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

public class SavingEvent extends Event {

	public SavingEvent() {
		super(30000);
	}

	@Override
	public void execute() {
		for (final Player p : World.getWorld().getPlayers()) {
			World.getWorld().save(p);
		}

	}

}
