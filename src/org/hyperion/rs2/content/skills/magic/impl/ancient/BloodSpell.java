package org.hyperion.rs2.content.skills.magic.impl.ancient;

import org.hyperion.rs2.content.ProjectileManager;
import org.hyperion.rs2.content.skills.magic.SpellMananger;
import org.hyperion.rs2.content.skills.magic.impl.Spell;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.region.Region;

public class BloodSpell implements Spell {

	private final int level;
	private final Animation casterAnim;
	private final Graphic casterGfx;
	private final int projectileId;
	private final Graphic targetGfx;
	private final int maxHit;
	private final double experience;
	private final Item[] runes;
	private final boolean multiHitting;

	public BloodSpell(int level, int casterAnim, int casterGfx, int projectile,
			int targetGfx, int maxHit, double exp, Item rune1, Item rune2,
			Item rune3, boolean multiHitting) {
		this.level = level;
		this.casterAnim = Animation.create(casterAnim);
		this.casterGfx = Graphic.create(casterGfx, (100 << 16));
		this.projectileId = projectile;
		this.targetGfx = Graphic.create(targetGfx, (100 << 16));
		this.maxHit = maxHit;
		this.experience = exp;
		this.runes = new Item[] { rune1, rune2, rune3 };
		this.multiHitting = multiHitting;
	}

	@Override
	public void cast(final Entity caster, final Entity target) {
		if (caster instanceof Player) {
			Player pCaster = (Player) caster;
			for (Item rune : getRuneReqs()) {// We loop through all runes again.
				if (rune != null) { // Make sure the item is not a null.
					Inventory.removeRune(pCaster, -1, rune); // Remove it from
																// the
																// inventory.
				}
			}
			pCaster.getSkills().addExperience(Skills.MAGIC, getExperience()); // Add
																				// experience.
		}
		caster.playAnimation(casterAnim);
		if (casterGfx != null)
			caster.playGraphics(casterGfx);
		if (projectileId != -1)
			ProjectileManager.fire(caster, target, projectileId, 50, 42, 26);
		World.getWorld().submit(
				new Event(ProjectileManager.magicHitDelay(caster, target)) {
					public void execute() {
						if (SpellMananger.calculateBloodSpellHit(caster,
								target, maxHit)) {
							target.playGraphics(targetGfx);
						}
						if (multiHitting) {
							if (!SpellMananger.checkForMulti(target)) {
								this.stop();
								return;
							} else {
								for (Region reg : World
										.getWorld()
										.getRegionManager()
										.getSurroundingRegions(
												target.getLocation())) {
									for (final Entity e : reg.getEntities()) {
										if (e == null
												|| e.isDead()
												|| e.getClientIndex() == caster
														.getClientIndex()
												|| !e.getLocation()
														.withinRange(target, 1)
												|| e.getClientIndex() == target
														.getClientIndex()) {
											continue;
										}
										if (!Combat.canAttack(caster, e)) {
											continue;
										}
										if (SpellMananger
												.calculateBloodSpellHit(caster,
														e, maxHit)) {
											e.playGraphics(targetGfx);
										}
									}
								}
							}
						}
						this.stop();
						// caster.resetInteractingEntity();
					}
				});

	}

	@Override
	public double getExperience() {
		return experience;
	}

	@Override
	public int getLevelReq() {
		return level;
	}

	@Override
	public Item[] getRuneReqs() {
		return runes;
	}

}
