package org.hyperion.rs2.model.npc;

import java.util.Random;

import org.hyperion.rs2.content.skills.Prayer;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Equipment;

public class GreenDragon extends NPC {

	private static final Animation DRAGON_FIRE = Animation.create(81);
	private static final Random r = new Random();

	public GreenDragon(NPCDefinition definition, Location location,
			Location minLocation, Location maxLocation) {
		super(definition, location, minLocation, maxLocation);
	}

	@Override
	public boolean getSpecialAttack(final Entity victim) {
		int damage = r.nextInt(65);
		if (victim instanceof Player) {
			Player pVictim = ((Player) victim);
			if (r.nextInt(4) == 0) { // Dragon fire.
				playAnimation(DRAGON_FIRE);
				playGraphics(Graphic.create(1, (100 << 16)));
				boolean print = true;
				if (pVictim.getEquipment().get(Equipment.SLOT_SHIELD) != null) {
					// Check for anti-dragon shield, or dragon fire shield.
					if (pVictim.getEquipment().get(Equipment.SLOT_SHIELD)
							.getId() == 1540
							|| pVictim.getEquipment()
									.get(Equipment.SLOT_SHIELD).getId() == 11284) {
						damage /= 10;
						pVictim.getActionSender()
								.sendMessage(
										"Your anti-dragonfire shield protected you from the dragons fire.");
						print = false;
					}
				}
				if (print) {
					pVictim.getActionSender()
							.sendMessage(
									damage >= 40 ? "You were burnt terribly in the dragons fire!"
											: "You manage to resist some of the dragons fire.");
				}
				if (pVictim.getPrayer().isPrayerToggled(
						Prayer.PROTECT_FROM_MAGE)) {
					damage /= 2;
				}
				final NPC npc = this;
				final int fDamage = damage;
				World.getWorld().submit(new Event(900) {
					public void execute() {
						Combat.inflictDamage(victim, npc, fDamage);
						this.stop();
					}
				});
				return true;
			}
		}
		return false;
	}

}
