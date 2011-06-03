package org.hyperion.rs2.content.skills.magic.impl.modern;

import org.hyperion.rs2.content.ProjectileManager;
import org.hyperion.rs2.content.skills.magic.SpellMananger;
import org.hyperion.rs2.content.skills.magic.impl.Spell;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Inventory;

/**
 * Class handling all elemental spells. Wind, Earth, Water, Fire.
 * 
 * @author Brown.
 */

public class ElementalSpell implements Spell {

	private static final Animation casterAnim = Animation.create(711);

	private final int level;
	private final Graphic casterGfx;
	private final int projectileId;
	private final Graphic targetGfx;
	private final int maxHit;
	private final double experience;
	private final Item[] runes;

	public ElementalSpell(int level, int casterGfx, int projectile,
			int targetGfx, int maxHit, double exp, Item rune1, Item rune2,
			Item rune3) {
		this.level = level;
		this.casterGfx = Graphic.create(casterGfx, (100 << 16));
		this.projectileId = projectile;
		this.targetGfx = Graphic.create(targetGfx, (100 << 16));
		this.maxHit = maxHit;
		this.experience = exp;
		this.runes = new Item[] { rune1, rune2, rune3 };
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
		caster.playGraphics(casterGfx);
		ProjectileManager.fire(caster, target, projectileId, 50, 42, 26);
		World.getWorld().submit(
				new Event(ProjectileManager.magicHitDelay(caster, target)) {
					public void execute() {
						if (SpellMananger.calculateNormalHit(caster, target,
								maxHit)) {
							target.playGraphics(targetGfx);
						}
						// caster.resetInteractingEntity();
						this.stop();
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
