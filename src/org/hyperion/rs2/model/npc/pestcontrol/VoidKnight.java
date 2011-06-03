package org.hyperion.rs2.model.npc.pestcontrol;

import org.hyperion.rs2.action.impl.AttackingAction;
import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.DeathEvent;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;

public class VoidKnight extends NPC {

	public VoidKnight(NPCDefinition definition, Location location,
			Location minLocation, Location maxLocation) {
		super(definition, location, minLocation, maxLocation);
	}

	@Override
	public void inflictDamage(Hit inc, Entity source) {
		if (!getUpdateFlags().get(UpdateFlag.HIT)) {
			getDamage().setHit1(inc);
			getUpdateFlags().flag(UpdateFlag.HIT);
		} else {
			if (!getUpdateFlags().get(UpdateFlag.HIT_2)) {
				getDamage().setHit2(inc);
				getUpdateFlags().flag(UpdateFlag.HIT_2);
			}
		}
		setHitpoints(getHitpoints() - inc.getDamage());
		if (getHitpoints() <= 0) {
			if (!this.isDead()) {
				World.getWorld().submit(new Event(2000) {
					public void execute() {
						playAnimation(Animation.create(getDeathAnimation()));
						this.stop();
					}
				});
				PestControl.finishGame(false);
			}
			this.getActionQueue().cancelQueuedActions();
			this.setDead(true);
			this.setAggressorState(false);
			this.setInCombat(false);
			this.setAutoRetaliating(false);
			this.getPoison().setPoisonHit(0);
		}

	}

}
