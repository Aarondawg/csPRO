package org.hyperion.rs2.model.npc.pestcontrol;

import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;

public class PestControlPortal extends NPC {

	public PestControlPortal(NPCDefinition definition, Location location,
			Location minLocation, Location maxLocation) {
		super(definition, location, minLocation, maxLocation);
		this.setAutoRetaliating(false);
	}

	@Override
	public void inflictDamage(Hit inc, Entity source) {
		if (source != null && source instanceof Player) {
			Player player = (Player) source;
			player.increasePestControlDamage(inc.getDamage());
		}
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
				// TODO: Animation??
				World.getWorld().submit(new Event(2000) {
					boolean first = true;

					public void execute() {
						if (first) {
							PestControl.portalDied();
							first = false;
						} else {
							setInvisible(true);
							this.stop();
						}
					}
				});
			}
			this.getPoison().setPoisonHit(0);
			this.setDead(true);
		}

	}

}
