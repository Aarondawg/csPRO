package org.hyperion.rs2.action.impl;

import java.util.ArrayList;
import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.Following;
import org.hyperion.rs2.content.skills.magic.SpellMananger;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Combat.AttackType;
import org.hyperion.rs2.model.container.Duel;

public class AttackingAction extends Action {

	/**
	 * The victim of this attack action.
	 */
	private Entity victim;

	/**
	 * The attempts of walking towards an entity outside of an NPCs min/max
	 * location. Please notice, this is only used for NPCs.
	 */
	private int attempts = 0;

	/**
	 * Constructor method for this action.
	 * 
	 * @param player
	 *            The attacker.
	 * @param victim
	 *            The attacked.
	 */
	public AttackingAction(Entity attacker, Entity victim) {
		super(attacker, 0);
		this.victim = victim;
		this.attackType = attacker.getAttackType();
	}

	@Override
	public QueuePolicy getQueuePolicy() {
		return QueuePolicy.NEVER;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.FOLLOW;
	}

	/*
	 * The attacking type.
	 */
	private final AttackType attackType;

	private static final Random r = new Random();

	@Override
	public void execute() {
		/*
		 * The attacking entity.
		 */
		final Entity source = this.getEntity();

		/*
		 * Make sure we're neither the source or the victim is null or dead.
		 */
		if (source == null || source.isDead() || victim == null
				|| victim.isDead()) {
			if (source != null) { // Could give a null-pointer exception.
				source.setInCombat(false);
				source.resetInteractingEntity();
				if (source instanceof Player) {
					((Player) source).getActionSender()
							.sendStopFollowing(false);
				}
			}
			this.stop();
			return;
		}

		if (victim instanceof Player) {
			if (!((Player) victim).getSession().isConnected()) {
				this.stop();
				return;
			}
		}

		/*
		 * Make sure we're still in combat.
		 */
		if (!source.isInCombat()) {
			this.stop();
			return;
		}
		/*
		 * Make sure we can attack the victim.
		 */
		if (!Combat.canAttack(source, victim)) {
			this.stop();
			source.setInCombat(false);
			return;
		}
		/*
		 * The distance we can attack from..
		 */
		int range = source.getAttackingDistance();

		/*
		 * Makes it possible to attack from a further distance, if we're walking
		 * towards the target.
		 */
		if (!source.getWalkingQueue().isEmpty() && source.canWalk()) {
			if (!victim.getWalkingQueue().isEmpty()) {
				range += 2;
			} else {
				range++;
			}
		}
		/*
		 * Checks if we're standing at the same spot, and moves the character if
		 * so.
		 */
		if (source.getLocation().equals(victim.getLocation())
				&& victim.getWalkingQueue().isEmpty()) {
			/*
			 * An array of the coordinates north/south/east/west of the player.
			 */
			final int[][] coordinates = {
					{ source.getLocation().getX(),
							source.getLocation().getY() + 1 }, // North.
					{ source.getLocation().getX(),
							source.getLocation().getY() - 1 }, // South.
					{ source.getLocation().getX() + 1,
							source.getLocation().getY() }, // East.
					{ source.getLocation().getX() - 1,
							source.getLocation().getY() }, // West.
			};
			/*
			 * We loop through all the GameObjects in our region, check if one
			 * any of the coordinates are blocked. If they are, we make them
			 * non-walkable using the boolean array above..
			 */
			/*
			 * for(GameObject object : source.getRegion().getGameObjects()) {
			 * if((!object.isWalkable(null)) && object.getLocation().getZ() ==
			 * source.getLocation().getZ()) {//Walls, diag walls, world objects.
			 * for(int x = object.getMinimumLocation().getX(); x <=
			 * object.getMaximumLocation().getX(); x++) { for(int y =
			 * object.getMinimumLocation().getY(); y <=
			 * object.getMaximumLocation().getY(); y++) { for(int index = 0;
			 * index < coordinates.length; index++) { if(coordinates[index][0]
			 * == x && coordinates[index][1] == y) { walkable[index] = false; }
			 * } } } } }
			 */
			final ArrayList<int[]> walkableSpots = new ArrayList<int[]>();
			for (int index = 0; index < coordinates.length; index++) {
				// if(RS2RegionLoader.positionIsWalkalble(source,
				// coordinates[index][0], coordinates[index][1])) {
				walkableSpots.add(coordinates[index]);
				// }
			}
			if (walkableSpots.size() == 0) {
				System.out
						.println("Theres a bug in the don't stand at the same spot thing in the AttackingAction class.");
			} else {
				source.getWalkingQueue().reset();
				int[] steps = walkableSpots
						.get(r.nextInt(walkableSpots.size()));
				source.getWalkingQueue().reset();
				source.getWalkingQueue().addStep(steps[0], steps[1]);
				source.getWalkingQueue().finish();
			}
		}

		boolean bool = source instanceof NPC ? true : ((Player) source)
				.getInterfaceState().getCurrentInterface() == -1; // Interface
																	// open..

		/*
		 * Makes sure we're within the correct distance.
		 */
		if (source.getLocation()
				.isWithinInteractingRange(source, victim, range) && bool) {
			/*
			 * Reset the walking queue..
			 */
			if (attackType != AttackType.MELEE) {
				source.getWalkingQueue().reset();
			}
			/*
			 * Check if the attacking delay is good.
			 */
			if (source.getCombatDelay() <= 0) {
				/*
				 * We're ready to fight!
				 */
				switch (attackType) {
				case MELEE:
					Combat.handleMeleeCombat(source, victim);
					source.increaseCombatDelay(source.getAttackSpeed());
					break;
				case RANGED:
					if (!source.hasAmmo()) {
						source.getWalkingQueue().reset();
						source.setInCombat(false);
						this.stop();
						return;
					}
					Combat.handleRangedCombat(source, victim);
					int attackSpeed = source.getAttackSpeed();
					if (source.getAttackStyle() == 4) { // RAPID.
						attackSpeed -= 600;
					}
					source.increaseCombatDelay(attackSpeed);
					break;
				case MAGIC:
					if (!source.getMagic().isAutoCasting()
							|| source.getMagic().getAutoCastingSpellId() == -1) {
						this.stop();
						return;
					}
					if (!source.getSpecialAttack(victim)) {
						SpellMananger.castSpell(source, victim, source
								.getMagic().getAutoCastingSpellId());
					}
					break;
				}
			}
			/*
			 * In case the attacker is an NPC, we make it go out of combat, if
			 * the victim is out of range for more than 10 ticks.
			 */
		} else if (source instanceof NPC) {
			if (Following.handleCombatFollowing((NPC) source, victim, range)) {
				attempts = 0;
			} else {
				if (attempts++ > 10
						&& (!source.isUnderAttack() || source.getAttacker() != victim)) {
					this.stop();
					source.setInCombat(false);
					source.resetInteractingEntity();
				}
			}
		}
		this.setDelay(600);
	}

}
