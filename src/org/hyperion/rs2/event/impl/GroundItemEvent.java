package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.GroundItem;
import org.hyperion.rs2.model.GroundItemController;

/**
 * Updates the status of the GroundItem(s). This method is called by the
 * background loader, and the event will be running in the background forever.
 */

public class GroundItemEvent extends Event {

	public GroundItemEvent() {
		super(600);
	}

	@Override
	public void execute() {
		for (GroundItem g : GroundItemController.getGroundItems()) {
			// Decrease the timer for the ground item, so it loops
			// Through various stages and eventually, disappears.
			g.decreaseTime();

			/*
			 * Removes the ground item, if time is.
			 */
			if (g.getTime() == GroundItemController.DISAPPEAR) {
				GroundItemController.removeGroundItemForAll(g);
				return;
			}

			/*
			 * Makes sure that a player bound item doesn't appear for everyone..
			 */
			if (Constants.playerBoundItem(g.getId())) {
				continue;
			}

			/*
			 * Makes the ground item appear for everyone, if the player is null
			 * (or an npc killed an npc).
			 */
			if (g.getOwner() == null
					&& g.getTime() > GroundItemController.APPEAR_FOR_EVERYONE) {
				g.setTime(GroundItemController.APPEAR_FOR_EVERYONE);
			}

			/*
			 * Spawns the ground item for everyone, if time is.
			 */
			if (g.getTime() == GroundItemController.APPEAR_FOR_EVERYONE) {
				GroundItemController.spawnForEveryone(g);
			}

		}

	}

}
