package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;

/**
 * Restores player stats.
 * 
 * @author Graham
 * 
 */
public class RestoreEvent extends Event {

	public RestoreEvent() {
		super(30000); // Runs every 30th second.
	}

	@Override
	public void execute() {
		for (Player player : World.getWorld().getPlayers()) {
			if (player.getInterfaceState().getCurrentInterface() == -1) {
				for (int skill = 0; skill < Skills.SKILL_COUNT; skill++) {
					if (/* i == Skills.HITPOINTS || */skill == Skills.PRAYER) {
						continue;
					}
					player.getSkills().normalizeLevel(skill);
				}
			}
			;
		}
	}

}
