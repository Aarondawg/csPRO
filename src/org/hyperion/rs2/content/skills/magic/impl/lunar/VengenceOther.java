package org.hyperion.rs2.content.skills.magic.impl.lunar;

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

public class VengenceOther implements Spell {

	private static final Item[] runes = { new Item(9075, 3), new Item(560, 2),
			new Item(557, 10) };

	private static final Graphic vengeanceOtherGfx = Graphic.create(725,
			(100 << 16));
	private static final Animation vengeanceOtherAnim = Animation.create(4411);

	@Override
	public void cast(final Entity caster, final Entity target) {
		if (System.currentTimeMillis()
				- caster.getMagic().getLastVengeanceOther() < 30000) {
			if (caster instanceof Player) {
				((Player) caster).getActionSender().sendMessage(
						"You can only cast vengeance other every 30 seconds.");
			}
			return;
		}
		if (target.getMagic().hasVengeanceCasted()) {
			if (caster instanceof Player) {
				((Player) caster).getActionSender().sendMessage(
						"Your target already has Vengeance casted!");
			}
			return;
		}
		if (caster instanceof Player) {
			Player p = (Player) caster;
			for (Item rune : runes) {
				Inventory.removeRune(p, -1, rune);
			}
			p.getSkills().addExperience(Skills.MAGIC, getExperience());
		}
		caster.getMagic().setLastVengeanceOther(System.currentTimeMillis());
		caster.playAnimation(vengeanceOtherAnim);
		World.getWorld().submit(
				new Event(ProjectileManager.magicHitDelay(caster, target)) {// 1000
					public void execute() {
						target.playGraphics(vengeanceOtherGfx);
						if (target instanceof Player) {
							Player pTarget = (Player) target;
							pTarget.getMagic().vengeanceEffect();
						}
						// caster.resetInteractingEntity();
						this.stop();
					}
				});
	}

	@Override
	public double getExperience() {
		return 108;
	}

	@Override
	public int getLevelReq() {
		return 92;
	}

	@Override
	public Item[] getRuneReqs() {
		return runes;
	}

}
