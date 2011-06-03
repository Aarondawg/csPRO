package org.hyperion.rs2.model.npc.pestcontrol;

import org.hyperion.rs2.content.Following;
import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;

public class Ravager extends PestControlMob {

	private GameObject objective = null;

	private long lastDestroy = 0;

	public Ravager(NPCDefinition definition, Location location) {
		super(definition, location, true);
	}

	@Override
	public void behave() {
		if (!this.isInCombat() && !this.isDead()) {
			if (objective == null) {
				GameObject closest = null;
				for (GameObject destroyable : PestControl
						.getCurrentPestControlObjects()) {
					double dist = destroyable.getLocation().getDistance(
							this.getLocation());
					if (closest == null
							|| dist < closest.getLocation().getDistance(
									this.getLocation())) {
						if (/* dist <= 16 && */PestControl
								.gameObjectIsDestroyable(destroyable)) {
							closest = destroyable;
						}
					}
				}
				if (closest != null) {
					Following.walkTowards(this, closest.getLocation());
					objective = closest;
					this.setHaveObjective(true);
					this.face(closest.getLocation());
				}
			} else if (objective != null) {
				if (this.getLocation().withinRange(objective.getLocation(), 1)
						&& System.currentTimeMillis() - lastDestroy > 4000) {
					this.playAnimation(Animation.create(getAttackAnimation()));
					PestControl.destroyObject(objective);
					lastDestroy = System.currentTimeMillis();
					objective = null;
					this.setHaveObjective(false);
				} else {
					Following.walkTowards(this, objective.getLocation());
				}
			}
		}

	}

}
