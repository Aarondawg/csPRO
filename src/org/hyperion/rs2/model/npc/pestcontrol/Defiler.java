package org.hyperion.rs2.model.npc.pestcontrol;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Combat.AttackType;

public class Defiler extends PestControlMob {

	public Defiler(NPCDefinition definition, Location location) {
		super(definition, location, false);

	}

	@Override
	public AttackType getAttackType() {
		return AttackType.RANGED;
	}

	@Override
	public void behave() {
		super.attackKnight();
	}

	@Override
	public int getDrawBackGraphics() {
		return 656;
	}

	@Override
	public int getProjectileId() {
		return 657;
	}

	@Override
	public boolean hasAmmo() {
		return true;
	}

}
