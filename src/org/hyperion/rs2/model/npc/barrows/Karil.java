package org.hyperion.rs2.model.npc.barrows;

import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.Combat.AttackType;

public class Karil extends NPC {

	public Karil(NPCDefinition definition, Location location,
			Location minLocation, Location maxLocation) {
		super(definition, location, minLocation, maxLocation);
	}

	@Override
	public boolean getSpecialAttack(final Entity victim) {
		// As this is called every time we're attacking the victim, we add
		// prayer reducing in here.
		if (victim instanceof Player) {
			Player pVictim = (Player) victim;
			pVictim.getSkills().detractLevel(Skills.PRAYER, 2);
		}
		return false;
	}

	@Override
	public AttackType getAttackType() {
		return AttackType.RANGED;
	}

	@Override
	public int getDrawBackGraphics() {
		return 28;
	}

	@Override
	public int getProjectileId() {
		return 27;
	}

	@Override
	public boolean hasAmmo() {
		return true;
	}

}
