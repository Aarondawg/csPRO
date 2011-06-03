package org.hyperion.rs2.model;

import org.hyperion.rs2.model.Combat.AttackType;
import org.hyperion.rs2.action.impl.AttackingAction;
import org.hyperion.rs2.content.skills.magic.Magic;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.DeathEvent;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.npc.*;
import org.hyperion.rs2.model.npc.barrows.*;
import org.hyperion.rs2.model.npc.pestcontrol.*;
import org.hyperion.rs2.model.region.Region;

/**
 * <p>
 * Represents a non-player character in the in-game world.
 * </p>
 * 
 * @author Graham Edgecombe
 * 
 */
public class NPC extends Entity {

	/**
	 * The definition.
	 */
	private final NPCDefinition definition;

	/**
	 * The magic instance.
	 */
	private Magic magic = null;

	/**
	 * The hitpoints of this NPC.
	 */
	private int hitpoints;

	/**
	 * Creates a new NPC which is never added to the game.
	 */
	public static NPC create(NPCDefinition definition) {
		return create(definition, null, null, null);
	}

	/**
	 * Creates a new NPC with a location, as well as a min/max location.
	 */
	public static NPC create(NPCDefinition definition, Location location,
			Location minLocation, Location maxLocation) {
		switch (definition.getId()) {
		/*
		 * King Black Dragon.
		 */
		case 50:
			return new KingBlackDragon(definition, location, minLocation,
					maxLocation);
			/*
			 * Green dragons.
			 */
		case 941:
		case 4677:
		case 4678:
		case 4679:
		case 4680:
			return new GreenDragon(definition, location, minLocation,
					maxLocation);
			/*
			 * Ahrim the Blighted
			 */
		case 2025:
			return new Ahrim(definition, location, minLocation, maxLocation);
			/*
			 * Dharok the Wretched
			 */
		case 2026:
			return new Dharok(definition, location, minLocation, maxLocation);
			/*
			 * Guthan the Infested
			 */
		case 2027:
			return new Guthan(definition, location, minLocation, maxLocation);
			/*
			 * Karil the Tainted
			 */
		case 2028:
			return new Karil(definition, location, minLocation, maxLocation);
			/*
			 * Torag the Corrupted
			 */
		case 2029:
			return new Torag(definition, location, minLocation, maxLocation);
			/*
			 * Verac the Defiled
			 */
		case 2030:
			return new Verac(definition, location, minLocation, maxLocation);
			/*
			 * Pest Control Portals.
			 */
		case 3777:
		case 3778:
		case 3779:
		case 3780:
			return new PestControlPortal(definition, location, minLocation,
					maxLocation);
			/*
			 * Void knight.
			 */
		case 3782:
		case 3783:
		case 3784:
		case 3785:
			return new VoidKnight(definition, location, minLocation,
					maxLocation);
			/*
			 * Shifters.
			 */
		case 3732: // shifter
		case 3733: // shifter
		case 3734: // shifter
		case 3735: // shifter
		case 3736: // shifter
		case 3737: // shifter
		case 3738: // shifter
		case 3739: // shifter
		case 3740: // shifter
		case 3741: // shifter
			return new Shifter(definition, location);
			/*
			 * Torchers.
			 */
		case 3752: // torcher
		case 3753: // torcher
		case 3754: // torcher
		case 3755: // torcher
		case 3756: // torcher
		case 3757: // torcher
		case 3758: // torcher
		case 3759: // torcher
		case 3760: // torcher
		case 3761: // torcher
			return new Torcher(definition, location);
			/*
			 * Spinners.
			 */
		case 3747: // spinner
		case 3748: // spinner
		case 3749: // spinner
		case 3750: // spinner
		case 3751: // spinner
			return new Spinner(definition, location);
			/*
			 * Ravagers
			 */
		case 3742: // ravager
		case 3743: // ravager
		case 3744: // ravager
		case 3745: // ravager
		case 3746: // ravager
			return new Ravager(definition, location);
			/*
			 * Defilers
			 */
		case 3762: // defiler
		case 3763: // defiler
		case 3764: // defiler
		case 3765: // defiler
		case 3766: // defiler
		case 3767: // defiler
		case 3768: // defiler
		case 3769: // defiler
		case 3770: // defiler
		case 3771: // defiler
			return new Defiler(definition, location);
			/*
			 * Brawlers.
			 */
		case 3772: // brawler
		case 3773: // brawler
		case 3774: // brawler
		case 3775: // brawler
		case 3776: // brawler
			return new Brawler(definition, location);
		}
		return location == null ? new NPC(definition) : new NPC(definition,
				location, minLocation, maxLocation);
	}

	/**
	 * Creates the NPC with the specified definition.
	 * 
	 * @param definition
	 *            The definition.
	 */
	public NPC(NPCDefinition definition) {
		super();
		this.definition = definition;
		this.hitpoints = definition.getHitpoints();
	}

	private Location minLocation = null;
	private Location maxLocation = null;

	/**
	 * Creates the NPC with the specified definition, minimum and maximum
	 * Locations.
	 * 
	 * @param definition
	 *            The definition.
	 * @param minLocation
	 *            The lowest location the NPC can walk in.
	 * @param maxLocation
	 *            The highest location the NPC can walk in.
	 */
	public NPC(NPCDefinition definition, Location location,
			Location minLocation, Location maxLocation) {
		super();
		this.definition = definition;
		this.hitpoints = definition.getHitpoints();
		this.minLocation = minLocation;
		this.maxLocation = maxLocation;
		this.setLocation(location);
		this.setLastKnownRegion(location);
	}

	/**
	 * Gets the NPC definition.
	 * 
	 * @return The NPC definition.
	 */
	public NPCDefinition getDefinition() {
		return definition;
	}

	@Override
	public void addToRegion(Region region) {
		region.addNpc(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removeNpc(this);
	}

	@Override
	public int getClientIndex() {
		return this.getIndex();
	}

	@Override
	public void heal(int hitDiff) {
		hitpoints += hitDiff;
	}

	@Override
	public void heal(int hitDiff, boolean canGoAboveMax) {
		heal(hitDiff);
		if (!canGoAboveMax) {
			if (hitpoints > getMaxHitpoints()) {
				hitpoints = getMaxHitpoints();
			}
		}
	}

	@Override
	public Magic getMagic() {
		if (magic == null) {
			magic = new Magic(this);
		}
		return magic;
	}

	@Override
	public int getAttackAnimation() {
		return definition.getAttackAnimation();
	}

	@Override
	public int getAttackSpeed() {
		return definition.getAttackSpeed();
	}

	@Override
	public int getDeathAnimation() {
		return definition.getDeathAnimation();
	}

	@Override
	public int getDefenceAnimation() {
		return definition.getDefenceAnimation();
	}

	@Override
	public int getMaxHit() {
		return definition.getMaxHit();
	}

	@Override
	public AttackType getAttackType() {
		return AttackType.MELEE;
	}

	@Override
	public int getDrawBackGraphics() {
		return 0;
	}

	@Override
	public int getProjectileId() {
		return 0;
	}

	@Override
	public boolean hasAmmo() {
		return false;
	}

	@Override
	public int getHitpoints() {
		return hitpoints;
	}

	public int getMaxHitpoints() {
		return definition.getHitpoints();
	}

	public void setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
	}

	@Override
	public void inflictDamage(Hit inc, Entity source) {
		if (source != null) {
			Combat.vengeance(this, source, inc.getDamage());
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
		hitpoints -= inc.getDamage();
		if (hitpoints <= 0) {
			if (!this.isDead()) {
				this.getWalkingQueue().reset();
				World.getWorld().submit(new Event(2000) {
					public void execute() {
						playAnimation(Animation.create(getDeathAnimation()));
						this.stop();
					}
				});
				World.getWorld().submit(new DeathEvent(this));
			}
			this.getActionQueue().cancelQueuedActions();
			this.setDead(true);
			this.setAggressorState(false);
			this.setInCombat(false);
			this.setAutoRetaliating(false);
			this.getPoison().setPoisonHit(0);
		}

	}

	private boolean isInvisible = false;

	public void setInvisible(boolean b) {
		this.isInvisible = b;
	}

	public boolean isInvisible() {
		return isInvisible;
	}

	@Override
	public boolean getSpecialAttack(Entity victim) {
		return false;
	}

	/**
	 * Gets the minimum location this NPC can walk in.
	 * 
	 * @return The minLocation.
	 */
	public Location getMinLocation() {
		return minLocation;
	}

	/**
	 * Gets the maximum location this NPC can walk in.
	 * 
	 * @return The maxLocation.
	 */
	public Location getMaxLocation() {
		return maxLocation;
	}

	private int walkingType = 0;

	/**
	 * Gets the walking type. 1 & 0 = can't walk. 2 = can walk.
	 */
	public int getWalkingType() {
		return walkingType;
	}

	public void setWalkingType(int i) {
		walkingType = i;
	}

	public boolean canWalkToArea(Location loc) {
		if (minLocation != null && maxLocation != null) {
			return loc.isInArea(minLocation, maxLocation);
		} else {
			return false;
		}
	}

	/**
	 * Handles the aggressivity of this NPC. This method is simply left with
	 * nothing but an attacking method, and all the NPC's extending this class
	 * (The aggressive ones) should override the method with content.
	 * 
	 * @param player
	 *            The player we want to be aggressive against.
	 */
	public void handleAggressivity(Player player) {
		if (player.getLocation().isInMulti() || !player.isInCombat()) {
			if (this.getDefinition().getCombat() * 2 >= player.getSkills()
					.getCombatLevel()) {
				if (canWalkToArea(player.getLocation()) && !this.isInCombat()) {
					Combat.attack(this, player);
				}
			}
		}
	}

	@Override
	public void setInteractingEntity(Entity entity) {
		super.setInteractingEntity(entity);
		lastInteract = System.currentTimeMillis();
	}

	/**
	 * The last time we were interacted.
	 */
	private long lastInteract = 0;

	/**
	 * Gets the last time we were interacted.
	 * 
	 * @return The last time we were interacted.
	 */
	public long getLastInteract() {
		return lastInteract;
	}

	@Override
	public int getSize() {
		return definition.getSize();
	}

}
