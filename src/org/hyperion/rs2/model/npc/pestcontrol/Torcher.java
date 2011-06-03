package org.hyperion.rs2.model.npc.pestcontrol;

import org.hyperion.rs2.content.ProjectileManager;
import org.hyperion.rs2.content.skills.magic.SpellMananger;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Combat.AttackType;

public class Torcher extends PestControlMob {

	private static final Graphic casterGfx = Graphic.create(630);
	private static final Graphic targetGfx = Graphic.create(633);
	private static final int projectileId = 631;

	public Torcher(NPCDefinition definition, Location location) {
		super(definition, location, false);
		this.getMagic().setAutoCasting(true);
		this.getMagic().setAutoCastingSpellId(0); // Can't be -1 :s
	}

	@Override
	public boolean getSpecialAttack(final Entity victim) {
		cast(victim);
		this.increaseCombatDelay(2500);
		return true;
	}

	public void cast(final Entity target) {
		this.playAnimation(Animation.create(this.getAttackAnimation()));
		this.playGraphics(casterGfx);
		ProjectileManager.fire(this, target, projectileId, 50, 42, 26);
		final PestControlMob npc = this;
		World.getWorld().submit(
				new Event(ProjectileManager.magicHitDelay(this, target)) {
					public void execute() {
						if (SpellMananger.calculateNormalHit(npc, target,
								getMaxHit())) {
							target.playGraphics(targetGfx);
						}
						this.stop();
					}
				});
	}

	@Override
	public void behave() {
		super.attackKnight();

	}

	@Override
	public AttackType getAttackType() {
		return AttackType.MAGIC;
	}

}
