package org.hyperion.rs2.model.npc.fightcaves;

import java.util.Random;

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
import org.hyperion.rs2.model.Damage.Hit;

public class TzTokJad extends FightCaveMonster {

	private static final Random r = new Random();

	public TzTokJad(Player player, NPCDefinition definition, Location location) {
		super(player, definition, location);
		this.getMagic().setAutoCasting(true);
		this.getMagic().setAutoCastingSpellId(0);
	}

	private boolean magic; // Always initialized before actions..

	private boolean check = false;

	@Override
	public boolean getSpecialAttack(final Entity victim) {
		System.out.println("Check: " + check + " AttackType: " + attack);
		if (check
				&& (attack.equals(AttackType.MELEE) || attack
						.equals(AttackType.RANGED))) {
			check = false;
			return false;
		} else {
			if (victim instanceof Player && victim.equals(getPlayer())) {
				/*
				 * We check if we're within melee distance..
				 */
				if (this.getLocation().isWithinInteractingRange(this,
						getPlayer(), 1)) {
					setAttackType(AttackType.MELEE);
					check = true;
					Combat.handleMeleeCombat(this, victim);
				} else {
					/*
					 * We randomly pick whenever we should attack with melee or
					 * ranged.
					 */
					magic = r.nextBoolean();
					setAttackType(magic ? AttackType.MAGIC : AttackType.RANGED);
					if (magic) {
						/*
						 * Magic attack.
						 */
						cast(victim);
					} else {
						/*
						 * Ranged attack
						 */
						check = true;
						Combat.handleRangedCombat(this, victim);
						this.increaseCombatDelay(this.getAttackSpeed());
					}
				}
			} else {
				this.getActionQueue().cancelQueuedActions();
				return false;
			}
		}
		return true;
	}

	private boolean spawnedHealers = false;

	@Override
	public void inflictDamage(Hit inc, Entity source) {
		super.inflictDamage(inc, source);
		if (this.getHitpoints() <= (this.getMaxHitpoints() / 2)
				&& !spawnedHealers) {
			getPlayer().getFightCaves().spawnHealers(this);
			spawnedHealers = true;
		}
	}

	public void cast(final Entity target) {
		this.setInteractingEntity(target);
		this.playAnimation(Animation.create(this.getAttackAnimation()));
		// this.playGraphics(casterGfx);
		ProjectileManager.fire(this, target, getProjectileId(), 50, 60, 26);
		final TzTokJad npc = this;
		World.getWorld().submit(
				new Event(ProjectileManager.magicHitDelay(this, target)) {
					public void execute() {
						if (SpellMananger.calculateNormalHit(npc, target,
								getMaxHit())) {
							target.playGraphics(getTargetGfx());
						}
						this.stop();
					}
				});
		this.increaseCombatDelay(this.getAttackSpeed());
	}

	private AttackType attack = r.nextBoolean() ? AttackType.MAGIC
			: AttackType.RANGED;

	public void setAttackType(AttackType attack) {
		this.attack = attack;
	}

	@Override
	public int getAttackingDistance() {
		return 8;
	}

	private static final Graphic RANGED_TARGET_GFX = Graphic.create(437);
	private static final Graphic MAGIC_TARGET_GFX = Graphic.create(449);

	public Graphic getTargetGfx() {
		if (magic) {
			return MAGIC_TARGET_GFX;
		} else {
			return RANGED_TARGET_GFX;
		}
	}

	@Override
	public AttackType getAttackType() {
		return attack;
	}

	@Override
	public int getAttackAnimation() {
		switch (attack) {
		case MAGIC:
			return 2656;
		case RANGED:
			return 2652;
		case MELEE:
			return getDefinition().getAttackAnimation();
		}
		return -1;
	}

	// 1625 = jad range gfx //On player sligthly after the attack..

	@Override
	public int getDrawBackGraphics() {
		if (magic) {
			return 447;
		} else {
			return -1; // None for ranged..
		}
	}

	@Override
	public int getProjectileId() {
		if (magic) {
			return 448;
		} else {
			return -1;
		}

	}

	@Override
	public boolean hasAmmo() {
		return true;
	}

}
