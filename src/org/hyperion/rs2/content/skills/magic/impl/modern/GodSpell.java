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
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Inventory;

public class GodSpell implements Spell {

	private static final int LEVEL_REQ = 60;
	private static final double EXP = 35;
	private static final Animation casterAnim = Animation.create(811);

	private final Graphic targetGfx;
	private final Item[] runes;
	private final Item staff;

	public static final Item SARA_STAFF = new Item(2415);
	public static final Item GUTHIX_STAFF = new Item(2416);
	public static final Item ZAMORAK_STAFF = new Item(2417);

	public GodSpell(Graphic targetGfx, Item rune1, Item rune2, Item rune3,
			Item staff) {
		this.targetGfx = targetGfx;
		this.runes = new Item[] { rune1, rune2, rune3 };
		this.staff = staff;
	}

	@Override
	public void cast(final Entity caster, final Entity target) {
		if (caster instanceof Player) {
			Player p = (Player) caster;
			if (p.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
				if (!(p.getEquipment().get(Equipment.SLOT_WEAPON).getId() == staff
						.getId())) {
					p.getActionSender().sendMessage(
							"You have to be wielding a "
									+ staff.getDefinition().getName()
									+ " in order to cast this spell.");
					return;
				}
			} else {
				p.getActionSender().sendMessage(
						"You have to be wielding a "
								+ staff.getDefinition().getName()
								+ " in order to cast this spell.");
				return;
			}
			for (Item rune : runes) {
				Inventory.removeRune(p, -1, rune);
			}
			p.getSkills().addExperience(Skills.MAGIC, EXP);
		}
		caster.playAnimation(casterAnim);
		World.getWorld().submit(
				new Event(ProjectileManager.magicHitDelay(caster, target)) {
					public void execute() {
						if (caster instanceof Player) {
							Player pCaster = (Player) caster;
							if (SpellMananger.calculateNormalHit(caster,
									target, pCaster.getMagic()
											.hasChargeCasted() ? 30 : 20)) {
								target.playGraphics(targetGfx);
								appendEffect(target);
							}
						}
						// caster.resetInteractingEntity();
						this.stop();
					}
				});

	}

	private void appendEffect(Entity target) {
		if (target instanceof Player) {
			Player pTarget = (Player) target;
			switch (staff.getId()) {
			case 2415:// Sara
				pTarget.getSkills().decrementLevel(Skills.PRAYER);
				break;
			case 2416:// Guthix
				pTarget.getSkills()
						.setLevel(
								Skills.DEFENCE,
								(int) (pTarget.getSkills().getLevel(
										Skills.DEFENCE) * 0.95));
				break;
			case 2417:// Zamorak
				pTarget.getSkills().setLevel(Skills.MAGIC,
						pTarget.getSkills().getLevel(Skills.MAGIC) - 5);
				break;
			}
		}

	}

	@Override
	public double getExperience() {
		return EXP;
	}

	@Override
	public int getLevelReq() {
		return LEVEL_REQ;
	}

	@Override
	public Item[] getRuneReqs() {
		return runes;
	}

}
