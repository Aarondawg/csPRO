package org.hyperion.rs2.model.npc.fightcaves;

import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Combat.AttackType;

/**
 * Has high accuracy and strength, but low defence and hitpoints. Protect from
 * Missiles prayer is advised, but not necessary. In waves where both Tok-Xil
 * and Ket-Zek is present, players should kill the Tok-Xil first with Protect
 * from Missiles on (unless a Tz-Kih is nearby) before its damage begins
 * building up. You can also poison it and rest in the safespot.
 */
public class TokXil extends FightCaveMonster {

	public TokXil(Player player, NPCDefinition definition, Location location) {
		super(player, definition, location);
	}

	@Override
	public boolean getSpecialAttack(final Entity victim) {
		if (type.equals(AttackType.MELEE)) { // Prevents a nasty exception..
			setAttackType(AttackType.RANGED);
			return false;
		}
		/*
		 * We check if we're within melee distance..
		 */
		if (this.getLocation().isWithinInteractingRange(this, getPlayer(), 1)) {
			setAttackType(AttackType.MELEE);
			Combat.handleMeleeCombat(this, victim);
			this.increaseCombatDelay(this.getAttackSpeed());
			return true;
		} else {
			setAttackType(AttackType.RANGED);
			return false;
		}
	}

	@Override
	public boolean hasAmmo() {
		return true;
	}

	@Override
	public int getDrawBackGraphics() {
		return -1;
	}

	@Override
	public int getProjectileId() {
		return 442; // FIXME
	}

	public void setAttackType(AttackType type) {
		this.type = type;
	}

	private AttackType type = AttackType.RANGED;

	@Override
	public AttackType getAttackType() {
		return type;
	}

	@Override
	public int getAttackAnimation() {
		if (type.equals(AttackType.RANGED)) {
			return 2633;
		} else {
			return super.getAttackAnimation();
		}
	}

}
