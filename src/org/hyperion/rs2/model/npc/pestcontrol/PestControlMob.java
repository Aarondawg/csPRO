package org.hyperion.rs2.model.npc.pestcontrol;

import org.hyperion.rs2.action.impl.AttackingAction;
import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;

public abstract class PestControlMob extends NPC {

	public PestControlMob(NPCDefinition definition, Location location,
			boolean isWalkable) {
		super(definition, location, Location.create(2624, 2550, 0), Location
				.create(2690, 2625, 0));
		this.isWalkable = isWalkable;
		this.setWalkingType(0); // We're handling walking in the behave method.
	}

	/**
	 * Behaves as a this Pest Control Mob should. Example: Walking towards the
	 * knight.
	 */
	public abstract void behave();

	@Override
	public void inflictDamage(Hit inc, Entity source) {
		if (source != null && source instanceof Player) {
			Player player = (Player) source;
			player.increasePestControlDamage(inc.getDamage());
			closestDoorLocation = null;
			if (!this.isInCombat() || haveObjective) {
				this.getWalkingQueue().reset();
				haveObjective = false;
				this.setInCombat(true);
				this.setAggressorState(false);
				this.setInteractingEntity(source);
				this.setCombatDelay(getAttackSpeed() / 2); // Slight delay
															// before we attack
															// back.
				this.getActionQueue().cancelQueuedActions();
				this.getActionQueue().addAction(
						new AttackingAction(this, source));
			}
		}
		if (!getUpdateFlags().get(UpdateFlag.HIT)) {
			getDamage().setHit1(inc);
			getUpdateFlags().flag(UpdateFlag.HIT);
		} else {
			if (!getUpdateFlags().get(UpdateFlag.HIT_2)) {
				getDamage().setHit2(inc);
				getUpdateFlags().flag(UpdateFlag.HIT_2);
			}
		}
		setHitpoints(getHitpoints() - inc.getDamage());
		if (getHitpoints() <= 0) {
			if (!this.isDead()) {
				final PestControlMob npc = this;
				World.getWorld().submit(new Event(1800) {
					boolean first = true;

					public void execute() {
						if (first) {
							if (npc instanceof Splatter) {
								// TODO: Splatting and hitting surounding
								// people. :D
							}
							playAnimation(Animation.create(getDeathAnimation()));
							first = false;
						} else {
							this.stop();
							setInvisible(true);
							PestControl.removeNPC(npc);
						}
					}
				});
			}
			this.getPoison().setPoisonHit(0);
			this.setDead(true);
		}
	}

	/**
	 * 
	 */
	public void attackKnight() {
		if (!this.isInCombat() && !haveObjective) {
			if (canWalkToArea(PestControl.getVoidKnight().getLocation())) {
				if (this.getLocation().isInArea(min2, max2)) {
					Combat.attack(this, PestControl.getVoidKnight());
					haveObjective = true;
				} else {
					if (closestDoorLocation == null) {
						closestDoorLocation = PestControl
								.getClosestDoorWalktoLocation(this
										.getLocation());
					}
					if (this.getLocation().equals(closestDoorLocation)) {
						Combat.attack(this, PestControl.getVoidKnight());
						haveObjective = true;
					} else if (this.getWalkingQueue().isEmpty()) {
						this.getWalkingQueue().reset();
						this.getWalkingQueue().addStep(
								closestDoorLocation.getX(),
								closestDoorLocation.getY());
						this.getWalkingQueue().finish();
					}
				}
			}
		}
	}

	@Override
	public boolean canWalkToArea(Location loc) {
		if (loc.isInArea(getMinLocation(), getMaxLocation())) {
			if (!this.getLocation().isInArea(min2, max2)) {
				if (!PestControl.closestDoorIsOpen(this.getLocation())) {
					if (loc.isInArea(min2, max2)) {
						if (loc.equals(PestControl.getVoidKnight()
								.getLocation()) && haveObjective) {
							haveObjective = false;
							this.getWalkingQueue().reset();
						}
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	private static final Location min2 = Location.create(2642, 2585, 0);
	private static final Location max2 = Location.create(2669, 2606, 0);

	private Location closestDoorLocation = null;

	@Override
	public void handleAggressivity(Player player) {
		if (!this.isInCombat()
				&& !haveObjective
				&& (!canWalkToArea(PestControl.getVoidKnight().getLocation())
						|| this instanceof Brawler || this instanceof Spinner)) {
			if (canWalkToArea(player.getLocation())) {
				if ((this.getLocation().isInArea(min2, max2) && player
						.getLocation().isInArea(min2, max2))
						|| !this.getLocation().isInArea(min2, max2)
						&& !player.getLocation().isInArea(min2, max2)) {
					Combat.attack(this, player);
				} else {
					if (closestDoorLocation == null) {
						closestDoorLocation = PestControl
								.getClosestDoorWalktoLocation(this
										.getLocation());
					}
					if (this.getLocation().equals(closestDoorLocation)) {
						Combat.attack(this, player);
					} else if (this.getWalkingQueue().isEmpty()) {
						this.getWalkingQueue().reset();
						this.getWalkingQueue().addStep(
								closestDoorLocation.getX(),
								closestDoorLocation.getY());
						this.getWalkingQueue().finish();
					}
				}
			}
		}
	}

	public void setHaveObjective(boolean b) {
		haveObjective = b;
	}

	/**
	 * Defines if you can walk on this NPC.
	 * 
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public boolean isWalkable() {
		return isWalkable;
	}

	/**
	 * Defines if you can walk through this mob.
	 */
	private final boolean isWalkable;

	/**
	 * Flag defining if we we're currently attacking the void knight, or having
	 * another objective.
	 */
	private boolean haveObjective;

}
