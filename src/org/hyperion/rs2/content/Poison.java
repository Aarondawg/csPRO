package org.hyperion.rs2.content;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.Damage.HitType;
import org.hyperion.rs2.model.container.Equipment;

public class Poison {

	/**
	 * The entity.
	 */
	private final Entity entity;

	/**
	 * The variables used for poison.
	 */
	private int poisonTicks, poisonHit, firstPoison;

	/**
	 * The class constructor.
	 * 
	 * @param entity
	 *            The owner of this class.
	 */
	public Poison(Entity entity) {
		this.entity = entity;
	}

	/**
	 * Weapons which may poison you. {id, damage}
	 */
	private static final int[][] POISON_WEPS = { { 5698, 6 }, };

	/**
	 * Gets a random value.
	 * 
	 * @return A random value.
	 */
	public static int getRandom(int range) {
		return (int) (java.lang.Math.random() * (range + 1));
	}

	/**
	 * Checks if we're going to poison the victim. This should be called from
	 * the attackers class.
	 * 
	 * @param victim
	 *            The victim we are trying to poison.
	 */
	public void checkForPoison(Entity victim) {
		if (getRandom(9) == 0) {
			if (entity instanceof Player) {
				Player p = (Player) entity;
				for (int i = 0; i < POISON_WEPS.length; i++) {
					if (p.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
						if (p.getEquipment().get(Equipment.SLOT_WEAPON).getId() == POISON_WEPS[i][0]) {
							victim.getPoison().startPoison(POISON_WEPS[i][1]);
						}
					}
				}
			} else if (entity instanceof NPC) {
				NPC n = (NPC) entity;
				if (n.getDefinition().isPoisonous()) {
					victim.getPoison().startPoison(6); // All poisoning damage
														// for NPC's will be 6
														// for now.
				}
			}
		}

	}

	/**
	 * Starts a poison event for the entity.
	 * 
	 * @param strength
	 *            The poisons strength.
	 */
	public void startPoison(int strength) {
		if (getPoisonHit() > 0) { // Already poisoned
			return;
		}
		firstPoison = strength;
		poisonTicks = strength;
		setPoisonHit(strength);
		if (entity instanceof Player) {
			Player p = (Player) entity;
			p.getActionSender().sendMessage("You have been poisoned.");
		}
		startPoisonEvent();
	}

	/**
	 * Starts the poison event.
	 */
	public void startPoisonEvent() {
		World.getWorld().submit(new Event(15000) {
			public void execute() {
				if (getPoisonHit() == 0) {
					this.stop();
					return;
				}
				int hit = getPoisonHit();
				if (hit > entity.getHitpoints()) {
					hit = entity.getHitpoints();
				}
				entity.inflictDamage(new Hit(hit, HitType.POISON_DAMAGE), null);
				poisonTicks--;
				if (poisonTicks == 0) {
					setPoisonHit(getPoisonHit() - 1);
					poisonTicks = firstPoison;
				}
			}
		});
	}

	/**
	 * Gets all the poison related variables for this entity in an int[3]
	 * 
	 * @return An array of the variables for this entity.
	 */
	public int[] getVariables() {
		return new int[] { poisonTicks, getPoisonHit(), firstPoison };
	}

	/**
	 * Sets up the variables when the char is loaded. Starts the poison event.
	 */
	public void setup(int poisonTicks, int poisonHit, int firstPoison) {
		this.poisonTicks = poisonTicks;
		this.setPoisonHit(poisonHit);
		this.firstPoison = firstPoison;
		startPoisonEvent();
	}

	/**
	 * Sets the current poison hit.
	 * 
	 * @param poisonHit
	 *            The new poison hit.
	 */
	public void setPoisonHit(int poisonHit) {
		this.poisonHit = poisonHit;
	}

	/**
	 * Gets the poison hit.
	 * 
	 * @return the poison hit.s
	 */
	public int getPoisonHit() {
		return poisonHit;
	}

}
