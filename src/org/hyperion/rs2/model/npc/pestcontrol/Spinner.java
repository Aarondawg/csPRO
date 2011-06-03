package org.hyperion.rs2.model.npc.pestcontrol;

import java.util.Random;

import org.hyperion.rs2.content.Following;
import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;

public class Spinner extends PestControlMob {

	private static final Random r = new Random();

	private static final Animation SPINNER_HEAL = Animation.create(1232);
	private static final Graphic GRAPHIC = Graphic.create(658);

	private long lastHeal = 0;

	public Spinner(NPCDefinition definition, Location location) {
		super(definition, location, true);
	}

	@Override
	public void behave() {
		if (!this.isUnderAttack()) {
			this.getActionQueue().cancelQueuedActions();
			this.setHaveObjective(true);
			PestControlPortal closest = getClosestPortal();
			if (closest.getHitpoints() < closest.getMaxHitpoints()
					&& closest.getHitpoints() > 0) {
				if (!this.getLocation().isWithinInteractingRange(this, closest,
						1)) {
					Following.handleCombatFollowing(this, closest, 1);
				} else if (System.currentTimeMillis() - lastHeal > 3000) {
					this.playAnimation(Animation.create(this
							.getAttackAnimation()));
					this.playGraphics(GRAPHIC);
					this.face(closest.getLocation());
					closest.heal(r.nextInt(30), false);
					lastHeal = System.currentTimeMillis();
				}
			}
		}
	}

	private PestControlPortal getClosestPortal() {
		PestControlPortal closest = PestControl.getBluePortal();
		if (PestControl.getRedPortal().getLocation()
				.getDistance(this.getLocation()) < closest.getLocation()
				.getDistance(this.getLocation())) {
			closest = PestControl.getRedPortal();
		}
		if (PestControl.getPurplePortal().getLocation()
				.getDistance(this.getLocation()) < closest.getLocation()
				.getDistance(this.getLocation())) {
			closest = PestControl.getPurplePortal();
		}
		if (PestControl.getYellowPortal().getLocation()
				.getDistance(this.getLocation()) < closest.getLocation()
				.getDistance(this.getLocation())) {
			closest = PestControl.getYellowPortal();
		}
		return closest;
	};

}
