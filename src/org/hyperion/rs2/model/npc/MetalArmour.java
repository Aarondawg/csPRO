package org.hyperion.rs2.model.npc;

import org.hyperion.rs2.action.impl.AttackingAction;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;

public class MetalArmour extends NPC {

	/**
	 * The minimum location this NPC can walk into.
	 */
	private static final Location minLocation = Location.create(2849, 3534, 0);
	/**
	 * The maximum location this NPC can walk into.
	 */
	private static final Location maxLocation = Location.create(2861, 3545, 0);

	/**
	 * An area the metal amours can't walk into, which is inside of the min/max
	 * location range.
	 */
	private static final Location min2 = Location.create(2860, 3534, 0);
	private static final Location max2 = Location.create(2861, 3536, 0);

	/**
	 * The Animation played when the MetalArmour is summoned.
	 */
	private static final Animation RISE = Animation.create(-1); // No clue on
																// this one.

	public MetalArmour(NPCDefinition definition, Location location, Player owner) {
		super(definition, location, minLocation, maxLocation);
		this.playAnimation(RISE);
		this.setWalkingType(0);
		this.owner = owner;
		this.forceChat("I'm ALIVE!");
	}

	@Override
	public boolean canWalkToArea(Location location) {
		if (location.isInArea(minLocation, maxLocation)) {
			if (!location.isInArea(min2, max2)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void inflictDamage(Hit inc, Entity source) {
		if (source != null) {
			if (source != owner) {
				source.getActionQueue().cancelQueuedActions(); // Should stop
																// unwanted
																// people from
																// attacking us,
																// although
																// it'll never
																// happen.
				return;
			}
			if (this.isAutoRetaliating() && !this.isInCombat()) {
				this.setInCombat(true);
				this.setAggressorState(false);
				this.setInteractingEntity(source);
				this.setCombatDelay(getAttackSpeed() / 2); // Slight delay
															// before we attack
															// back.
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
				this.getWalkingQueue().reset();
				final MetalArmour npc = this;
				World.getWorld().submit(new Event(2000) {
					boolean first = true;

					public void execute() {
						if (first) {
							playAnimation(Animation.create(getDeathAnimation()));
							first = false;
						} else {
							owner.getWarriorsGuild().dropArmour();
							npc.setInvisible(true); // No idea if setting this
													// flag is needed.
							World.getWorld().unregister(npc);
							this.stop();
						}
					}
				});
			}
			this.getActionQueue().cancelQueuedActions();
			this.setDead(true);
			this.setAggressorState(false);
			this.setInCombat(false);
			this.setAutoRetaliating(false);
			this.getPoison().setPoisonHit(0);
		}

	}

	@Override
	public void handleAggressivity(Player player) {
		/*
		 * We make sure that we're only aggressive towards the "owner" of this
		 * Metal Armour.
		 */
		if (player == owner) {
			super.handleAggressivity(player);
		}
	}

	/**
	 * The player who summoned this Metal Armour.
	 */
	private final Player owner;

}
