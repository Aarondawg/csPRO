package org.hyperion.rs2.content.skills.magic.impl.modern;

import org.hyperion.rs2.content.ProjectileManager;
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

public class ReducingSpell implements Spell {

	private final int level;
	private final double experience;
	private final Animation casterAnim;
	private final Graphic casterGfx;
	private final int projectileId;
	private final Graphic targetGfx;
	private final int skill;
	private final double percentage;
	private final Item[] runes;

	public ReducingSpell(int level, double experience, int casterAnim,
			int casterGfx, int projectileId, int targetGfx, int skill,
			double percentage, Item rune, Item rune2, Item rune3) {
		this.level = level;
		this.experience = experience;
		this.casterAnim = Animation.create(casterAnim);
		this.casterGfx = Graphic.create(casterGfx, (100 << 16));
		this.projectileId = projectileId;
		this.targetGfx = Graphic.create(targetGfx, (100 << 16));
		this.skill = skill;
		this.percentage = percentage;
		this.runes = new Item[] { rune, rune2, rune3 };
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
						target.playGraphics(targetGfx);
						if (target instanceof Player) {
							Player pTarget = (Player) target;
							pTarget.getSkills()
									.detractLevel(
											skill,
											(int) (pTarget.getSkills()
													.getLevel(skill) * percentage));
							pTarget.getActionSender().sendMessage(
									"You feel weakened.");
						}
						// TODO: NPC's :(
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
