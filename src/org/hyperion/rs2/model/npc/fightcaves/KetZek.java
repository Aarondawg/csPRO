package org.hyperion.rs2.model.npc.fightcaves;

import org.hyperion.rs2.content.ProjectileManager;
import org.hyperion.rs2.content.skills.magic.SpellMananger;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Combat.AttackType;

/**
 * Uses Magic and melee attacks. Range these while using Protect from magic.
 * Both types of attacks are known to hit well over 50 damage at best, so it is
 * advised to use protection prayers at all times. It is possible to use melee
 * or magic against the Ket-Zek, however it is highly recommended to use
 * ranged-based attacks. Also defeating a Ket-Zek is a Hard task for Karamja
 * Diary. The Ket-Zek hits incredibly hard and consistently with melee (Constant
 * 50+s) so if you must melee it, use protection prayers!
 */
public class KetZek extends FightCaveMonster {

	public KetZek(Player player, NPCDefinition definition, Location location) {
		super(player, definition, location);
		this.getMagic().setAutoCasting(true);
		this.getMagic().setAutoCastingSpellId(0);
	}

	@Override
	public boolean getSpecialAttack(final Entity victim) {
		if (type.equals(AttackType.MELEE)) { // Prevents a nasty exception..
			setAttackType(AttackType.MAGIC);
			return false;
		}
		/*
		 * We check if we're within melee distance..
		 */
		if (this.getLocation().isWithinInteractingRange(this, getPlayer(), 1)) {
			setAttackType(AttackType.MELEE);
			Combat.handleMeleeCombat(this, victim);
			this.increaseCombatDelay(this.getAttackSpeed());
		} else {
			cast(victim);
		}
		return true;
	}

	public void cast(final Entity target) {
		this.setInteractingEntity(target);
		setAttackType(AttackType.MAGIC);
		this.playAnimation(Animation.create(this.getAttackAnimation()));
		// this.playGraphics(casterGfx);
		ProjectileManager.fire(this, target, 445, 50, 42, 26);
		final KetZek npc = this;
		World.getWorld().submit(
				new Event(ProjectileManager.magicHitDelay(this, target)) {
					public void execute() {
						if (SpellMananger.calculateNormalHit(npc, target,
								getMaxHit())) {
							target.playGraphics(Graphic.create(446));
						}
						this.stop();
					}
				});
		this.increaseCombatDelay(this.getAttackSpeed());
	}

	public void setAttackType(AttackType type) {
		this.type = type;
	}

	private AttackType type = AttackType.MAGIC;

	@Override
	public AttackType getAttackType() {
		return type;
	}

	@Override
	public int getAttackAnimation() {
		if (type.equals(AttackType.MAGIC)) {
			return 2647;
		} else {
			return super.getAttackAnimation();
		}
	}

}
