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

public class TeleBlock implements Spell {

	private static final Animation casterAnim = Animation.create(1820);
	private static final int projectileId = 344;
	private final Graphic targetGfx = Graphic.create(345/* ,(100 << 16) */);
	private final Item[] runes = { new Item(563), new Item(562), new Item(560) };

	public TeleBlock() {

	}

	@Override
	public void cast(final Entity caster, final Entity target) {
		if (target instanceof Player) {
			final Player pTarget = (Player) target;
			if (pTarget.isTeleblocked()) {
				if (caster instanceof Player) {
					((Player) caster).getActionSender().sendMessage(
							"Your target is already teleblocked!");
				}
				return;
			}
			if (caster instanceof Player) {
				Player pCaster = (Player) caster;
				for (Item rune : getRuneReqs()) {// We loop through all runes
													// again.
					if (rune != null) { // Make sure the item is not a null.
						Inventory.removeRune(pCaster, -1, rune); // Remove it
																	// from the
																	// inventory.
					}
				}
				pCaster.getSkills()
						.addExperience(Skills.MAGIC, getExperience()); // Add
																		// experience.
			}

			caster.playAnimation(casterAnim);
			ProjectileManager.fire(caster, target, projectileId, 50, 42, 26);
			World.getWorld().submit(
					new Event(ProjectileManager.magicHitDelay(caster, target)) {
						public void execute() {
							if (SpellMananger.getRandom(10 + SpellMananger
									.calculateMagicDefence(target)) < SpellMananger
									.getRandom(10 + SpellMananger
											.calculateMagicAttack(caster))) {
								target.playGraphics(targetGfx);
								pTarget.setTeleblocked(true);
								World.getWorld().submit(new Event(1200000) {// 20
																			// minutes

											@Override
											public void execute() {
												if (pTarget != null) {
													pTarget.getActionSender()
															.sendMessage(
																	"Your teleblock faces away..");
													pTarget.setTeleblocked(false);
												}
												this.stop();
											}

										});
							}
							if (caster instanceof Player) {
								((Player) caster)
										.getActionSender()
										.sendMessage(
												"You cannot cast this spell on an NPC.");
							}
							// caster.resetInteractingEntity();
							this.stop();
						}
					});
		} else {
			if (caster instanceof Player) {
				((Player) caster).getActionSender().sendMessage(
						"You cannot cast this spell on an NPC.");
			}
		}

	}

	@Override
	public double getExperience() {
		return 80;
	}

	@Override
	public int getLevelReq() {
		return 85;
	}

	@Override
	public Item[] getRuneReqs() {
		return runes;
	}

}
