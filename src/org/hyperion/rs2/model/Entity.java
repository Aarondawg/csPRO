package org.hyperion.rs2.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hyperion.rs2.action.ActionQueue;
import org.hyperion.rs2.content.Poison;
import org.hyperion.rs2.model.Combat.AttackType;
import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.content.minigames.WarriorsGuild;
import org.hyperion.rs2.content.skills.magic.Magic;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.region.Region;

/**
 * Represents a character in the game world, i.e. a <code>Player</code> or an
 * <code>NPC</code>.
 * 
 * @author Graham Edgecombe
 * 
 */
public abstract class Entity {

	/**
	 * The default, i.e. spawn, location.
	 */
	public static final Location DEFAULT_LOCATION = Location.create(3222, 3222,
			0);

	/**
	 * The index in the <code>EntityList</code>.
	 */
	private int index;

	/**
	 * The current location.
	 */
	private Location location;

	/**
	 * The entity's first stored hit for updates.
	 */
	private transient Damage damage = new Damage();

	/**
	 * The entity's state of life.
	 */
	private boolean isDead = false;

	/**
	 * The entity's combat state.
	 */
	private boolean isInCombat = false;

	/**
	 * Auto-retaliation setting.
	 */
	private boolean isAutoRetaliating = true;

	/**
	 * The teleportation target.
	 */
	private Location teleportTarget = null;

	/**
	 * The update flags.
	 */
	private final UpdateFlags updateFlags = new UpdateFlags();

	/**
	 * The list of local players.
	 */
	private final List<Player> localPlayers = new LinkedList<Player>();

	/**
	 * The list of local npcs.
	 */
	private final List<NPC> localNpcs = new LinkedList<NPC>();

	/**
	 * The teleporting flag.
	 */
	private boolean teleporting = false;

	/**
	 * The walking queue.
	 */
	private final WalkingQueue walkingQueue = new WalkingQueue(this);

	/**
	 * A queue of actions.
	 */
	private final ActionQueue actionQueue = new ActionQueue();

	/**
	 * The sprites i.e. walk directions.
	 */
	private final Sprites sprites = new Sprites();

	/**
	 * The last known map region.
	 */
	private Location lastKnownRegion = this.getLocation();

	/**
	 * Map region changing flag.
	 */
	private boolean mapRegionChanging = false;

	/**
	 * The current animation.
	 */
	private Animation currentAnimation;

	/**
	 * The current graphic.
	 */
	private Graphic currentGraphic;

	/**
	 * The current region.
	 */
	private Region currentRegion;

	/**
	 * The interacting entity.
	 */
	private Entity interactingEntity;

	/**
	 * The face location.
	 */
	private Location face;

	/**
	 * The current forced ChatText.
	 */
	private String forcedChat;

	/**
	 * Entity's combat aggressor state.
	 */
	private boolean isAggressor = false;

	/**
	 * Entity's poison class.
	 */
	private final Poison poison = new Poison(this);

	/**
	 * Combat related stuff from rs2hd. Will document later if I care.
	 */
	private int attackStyle = 0;
	private int attackButton = 1;
	private Entity attacker = null;

	/**
	 * Creates the entity.
	 */
	public Entity() {
		// setLocation(DEFAULT_LOCATION);
		// this.lastKnownRegion = location;
	}

	/**
	 * Set the entity's combat state.
	 * 
	 * @param isInCombat
	 *            This entity's combat state.
	 */
	public void setInCombat(boolean isInCombat) {
		if (!isInCombat) {
			// this.resetInteractingEntity();
			this.setAggressor(false);
		}
		this.isInCombat = isInCombat;
	}

	/**
	 * Returns the combat state of this entity.
	 * 
	 * @return <code>boolean</code> The entity's combat state.
	 */
	public boolean isInCombat() {
		return isInCombat;
	}

	/**
	 * Gets the entity's aggressor state.
	 * 
	 * @return boolean The entity's aggressor state.
	 */
	public boolean getAggressorState() {
		return isAggressor;
	}

	/**
	 * Sets the aggressor state for this entity.
	 */
	public void setAggressorState(boolean b) {
		isAggressor = b;
	}

	/**
	 * Set the entity's autoretaliation setting.
	 * 
	 * @param b
	 *            <code>true/false</code> Whether or not this entity will
	 *            autoretaliate when attacked.
	 */
	public void setAutoRetaliating(boolean b) {
		this.isAutoRetaliating = b;
	}

	/**
	 * Get this entity's autoretaliation setting.
	 * 
	 * @return <code>true</code> if autoretaliation is on, <code>false</code> if
	 *         not.
	 */
	public boolean isAutoRetaliating() {
		return isAutoRetaliating;
	}

	/**
	 * Set the entity's state of life.
	 * 
	 * @param isDead
	 *            Boolean
	 */
	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	/**
	 * Is the entity dead?
	 * 
	 * @return
	 */
	public boolean isDead() {
		return isDead;
	}

	/**
	 * Makes this entity speak with forcechat.
	 * 
	 * @param chat
	 *            The chattext.
	 */
	public void forceChat(String chat) {
		this.forcedChat = chat;
		this.updateFlags.flag(UpdateFlag.FORCED_CHAT);
	}

	/**
	 * Resets the forced chattext.
	 */
	public void resetForceChat() {
		this.forcedChat = null;
	}

	/**
	 * Gets the forced chat text.
	 * 
	 * @return The forced chat text.
	 */
	public String getForceChatText() {
		return forcedChat;
	}

	/**
	 * Makes this entity face a location.
	 * 
	 * @param location
	 *            The location to face.
	 */
	public void face(Location location) {
		this.face = location;
		this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
	}

	/**
	 * Checks if this entity is facing a location.
	 * 
	 * @return The entity face flag.
	 */
	public boolean isFacing() {
		return face != null;
	}

	/**
	 * Resets the facing location.
	 */
	public void resetFace() {
		this.face = null;
		this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
	}

	/**
	 * Gets the face location.
	 * 
	 * @return The face location, or <code>null</code> if the entity is not
	 *         facing.
	 */
	public Location getFaceLocation() {
		return face;
	}

	/**
	 * Checks if this entity is interacting with another entity.
	 * 
	 * @return The entity interaction flag.
	 */
	public boolean isInteracting() {
		return interactingEntity != null;
	}

	/**
	 * Sets the interacting entity.
	 * 
	 * @param entity
	 *            The new entity to interact with.
	 */
	public void setInteractingEntity(Entity entity) {
		this.interactingEntity = entity;
		this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
	}

	/**
	 * Resets the interacting entity.
	 */
	public void resetInteractingEntity() {
		this.interactingEntity = null;
		this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
	}

	/**
	 * Gets the interacting entity.
	 * 
	 * @return The entity to interact with.
	 */
	public Entity getInteractingEntity() {
		return interactingEntity;
	}

	/**
	 * Gets the current region.
	 * 
	 * @return The current region.
	 */
	public Region getRegion() {
		return currentRegion;
	}

	/**
	 * Gets the current animation.
	 * 
	 * @return The current animation;
	 */
	public Animation getCurrentAnimation() {
		return currentAnimation;
	}

	/**
	 * Checks if the entity is currently animating.
	 * 
	 * @return true if, false if not.
	 */
	public boolean isAnimating() {
		return currentAnimation != null;
	}

	/**
	 * Gets the current graphic.
	 * 
	 * @return The current graphic.
	 */
	public Graphic getCurrentGraphic() {
		return currentGraphic;
	}

	/**
	 * Resets attributes after an update cycle.
	 */
	public void reset() {
		this.currentAnimation = null;
		this.currentGraphic = null;
	}

	/**
	 * Animates the entity.
	 * 
	 * @param animation
	 *            The animation.
	 */
	public void playAnimation(Animation animation) {
		this.currentAnimation = animation;
		this.getUpdateFlags().flag(UpdateFlag.ANIMATION);
	}

	/**
	 * Plays graphics.
	 * 
	 * @param graphic
	 *            The graphics.
	 */
	public void playGraphics(Graphic graphic) {
		this.currentGraphic = graphic;
		this.getUpdateFlags().flag(UpdateFlag.GRAPHICS);
	}

	/**
	 * Gets the walking queue.
	 * 
	 * @return The walking queue.
	 */
	public WalkingQueue getWalkingQueue() {
		return walkingQueue;
	}

	/**
	 * Gets the action queue.
	 * 
	 * @return The action queue.
	 */
	public ActionQueue getActionQueue() {
		return actionQueue;
	}

	/**
	 * Sets the last known map region.
	 * 
	 * @param lastKnownRegion
	 *            The last known map region.
	 */
	public void setLastKnownRegion(Location lastKnownRegion) {
		this.lastKnownRegion = lastKnownRegion;
	}

	/**
	 * Gets the last known map region.
	 * 
	 * @return The last known map region.
	 */
	public Location getLastKnownRegion() {
		return lastKnownRegion;
	}

	/**
	 * Checks if the map region has changed in this cycle.
	 * 
	 * @return The map region changed flag.
	 */
	public boolean isMapRegionChanging() {
		return mapRegionChanging;
	}

	/**
	 * Sets the map region changing flag.
	 * 
	 * @param mapRegionChanging
	 *            The map region changing flag.
	 */
	public void setMapRegionChanging(boolean mapRegionChanging) {
		this.mapRegionChanging = mapRegionChanging;
	}

	/**
	 * Checks if this entity has a target to teleport to.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasTeleportTarget() {
		return teleportTarget != null;
	}

	/**
	 * Gets the teleport target.
	 * 
	 * @return The teleport target.
	 */
	public Location getTeleportTarget() {
		return teleportTarget;
	}

	/**
	 * Sets the teleport target.
	 * 
	 * @param teleportTarget
	 *            The target location.
	 */
	public void setTeleportTarget(Location teleportTarget) {
		if (this instanceof Player) {
			((Player) this).getActionSender().sendStopFollowing(false);
		}
		this.teleportTarget = teleportTarget;
	}

	/**
	 * Resets the teleport target.
	 */
	public void resetTeleportTarget() {
		this.teleportTarget = null;
	}

	/**
	 * Gets the sprites.
	 * 
	 * @return The sprites.
	 */
	public Sprites getSprites() {
		return sprites;
	}

	/**
	 * Checks if this player is teleporting.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isTeleporting() {
		return teleporting;
	}

	/**
	 * Sets the teleporting flag.
	 * 
	 * @param teleporting
	 *            The teleporting flag.
	 */
	public void setTeleporting(boolean teleporting) {
		this.teleporting = teleporting;
	}

	/**
	 * Gets the list of local players.
	 * 
	 * @return The list of local players.
	 */
	public List<Player> getLocalPlayers() {
		return localPlayers;
	}

	/**
	 * Gets the list of local npcs.
	 * 
	 * @return The list of local npcs.
	 */
	public List<NPC> getLocalNPCs() {
		return localNpcs;
	}

	/**
	 * Sets the entity's index.
	 * 
	 * @param index
	 *            The index.
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Gets the entity's index.
	 * 
	 * @return The index.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the current location.
	 * 
	 * @param location
	 *            The current location.
	 */
	public void setLocation(Location location) {
		this.location = location;

		Region newRegion = World.getWorld().getRegionManager()
				.getRegionByLocation(location);
		if (newRegion != currentRegion) {
			if (currentRegion != null) {
				removeFromRegion(currentRegion);
			}
			currentRegion = newRegion;
			addToRegion(currentRegion);
		}
	}

	/**
	 * Destroys this entity.
	 */
	public void destroy() {
		if (this instanceof Player) {
			Player p = (Player) this;
			if (p.getRequestManager().isTrading()) {
				p.getRequestManager().getTrade().close();
			}
			if (p.getRequestManager().isDueling()) {
				p.getRequestManager().getDuel().destroy(p);
			}
			PestControl.destroy(p);
			WarriorsGuild.IN_GAME.remove(p);
			p.getFightCaves().destroy();
		}
		removeFromRegion(currentRegion);
		reset();
	}

	/**
	 * Deal a hit to the entity.
	 * 
	 * @param damage
	 *            The damage to be done.
	 * @param aggressor
	 *            The type of damage we are inflicting.
	 */
	public abstract void inflictDamage(Hit damage, Entity aggressor);

	/**
	 * Heals the player.
	 * 
	 * @param hitDiff
	 *            How much to heal the player.
	 */
	public abstract void heal(int hitDiff);

	/**
	 * Heals the player.
	 * 
	 * @param hitDiff
	 *            How much to heal the player.
	 * @param canGoAboveMax
	 *            Goes over max if true, doesn't if false.
	 */
	public abstract void heal(int hitDiff, boolean canGoAboveMax);

	/**
	 * Removes this entity from the specified region.
	 * 
	 * @param region
	 *            The region.
	 */
	public abstract void removeFromRegion(Region region);

	/**
	 * Adds this entity to the specified region.
	 * 
	 * @param region
	 *            The region.
	 */
	public abstract void addToRegion(Region region);

	/**
	 * Gets the current location.
	 * 
	 * @return The current location.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Gets the update flags.
	 * 
	 * @return The update flags.
	 */
	public UpdateFlags getUpdateFlags() {
		return updateFlags;
	}

	/**
	 * Get this entity's hit1.
	 * 
	 * @return The entity's hits as <code>Hit</code> type.
	 */
	public Damage getDamage() {
		return damage;
	}

	/**
	 * Gets the client-side index of an entity.
	 * 
	 * @return The client-side index.
	 */
	public abstract int getClientIndex();

	/**
	 * Gets the poison class which belongs to this entity.
	 * 
	 * @return The poison class.
	 */
	public Poison getPoison() {
		return poison;
	}

	/**
	 * Gets the magic instance of this entity.
	 * 
	 * @return The magic instance.
	 */
	public abstract Magic getMagic();

	public Entity getAttacker() {
		return attacker;
	}

	public void setUnderAttack(Entity attacker) {
		setLastAttack();
		this.attacker = attacker;
	}

	public boolean isUnderAttack() {
		if (getAttacker() == null || getAttacker().isDead()) {
			return false;
		}
		if (System.currentTimeMillis() - getLastAttack() > 5000) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isAggressor() {
		return isAggressor;
	}

	public void setAggressor(boolean b) {
		isAggressor = b;
	}

	public void setAttackStyle(int i) {
		this.attackStyle = i;
	}

	public int getAttackStyle() {
		return attackStyle;
	}

	public abstract int getAttackAnimation();

	public abstract int getAttackSpeed();

	public abstract int getMaxHit();

	public abstract int getDefenceAnimation();

	public abstract int getDeathAnimation();

	public abstract AttackType getAttackType();

	public abstract int getDrawBackGraphics();

	public abstract int getProjectileId();

	public abstract boolean hasAmmo();

	public abstract int getHitpoints();

	private Map<Entity, Byte> killers = new HashMap<Entity, Byte>();

	/**
	 * Adds a killer hit, and grants the killer experience.
	 * 
	 * @param killer
	 *            The attacker.
	 * @param hit
	 *            The hit made upon us.
	 */
	public void addKillerHit(Entity killer, int hit) {
		if (!killers.containsKey(killer)) {
			killers.put(killer, (byte) hit);
		} else {
			killers.put(killer, (byte) (killers.get(killer) + hit));
		}
		if (killer instanceof Player) {
			Player p = (Player) killer;
			AttackType type = killer.getAttackType();
			int attackStyle = p.getAttackStyle();
			p.getSkills().addExperience(Skills.HITPOINTS, 1.33 * hit);
			if (type == AttackType.MELEE) {
				switch (attackStyle) {
				case Combat.ACCURATE:
					p.getSkills().addExperience(Skills.ATTACK, 4 * hit);
					break;
				case Combat.AGGRESSIVE:
					p.getSkills().addExperience(Skills.STRENGTH, 4 * hit);
					break;
				case Combat.CONTROLLED:
					p.getSkills().addExperience(Skills.ATTACK, 1.33 * hit);
					p.getSkills().addExperience(Skills.STRENGTH, 1.33 * hit);
					p.getSkills().addExperience(Skills.DEFENCE, 1.33 * hit);
					break;
				case Combat.DEFENSIVE:
					p.getSkills().addExperience(Skills.DEFENCE, 4 * hit);
					break;
				}
			} else {
				if (attackStyle == Combat.LONGRANGE) {
					p.getSkills().addExperience(Skills.RANGE, 2 * hit);
					p.getSkills().addExperience(Skills.DEFENCE, 2 * hit);
				} else {
					p.getSkills().addExperience(Skills.RANGE, 4 * hit);
				}
			}
		}
	}

	/**
	 * Clears all the hits made on this entity.
	 */
	public void clearKillersHits() {
		killers.clear();
	}

	/**
	 * Gets the Entity that killed us.
	 * 
	 * @return The entity that killed us.
	 */
	public Entity getKiller() {
		Entity highestHitter = null;
		int highestHit = -1;
		for (Map.Entry<Entity, Byte> entry : killers.entrySet()) {
			if (entry.getValue() == highestHit) {
				if (entry.getKey() == this.getAttacker()) {
					highestHitter = this.getAttacker();
				}
			} else if (entry.getValue() > highestHit) {
				highestHitter = entry.getKey();
			}
		}
		return highestHitter;
	}

	/**
	 * The last time we were attacked.
	 */
	private long lastAttack = 0;

	/**
	 * Gets the long defining the last time we were attacked.
	 * 
	 * @return The last time we were attacked.
	 */
	public long getLastAttack() {
		return lastAttack;
	}

	/**
	 * Should be called everytime this entity is attacked by someone.
	 */
	public void setLastAttack() {
		lastAttack = System.currentTimeMillis();
	}

	/**
	 * Defines if this entity can walk.
	 */
	private boolean canWalk = true;

	/**
	 * Sets the can walk boolean.
	 * 
	 * @param b
	 *            <code>true</code> if we can walk, <code>false</code> if not.
	 */
	public void setCanWalk(boolean b) {
		if (!b) {
			this.getWalkingQueue().reset();
		}
		canWalk = b;
	}

	/**
	 * Defines if the player can walk.
	 * 
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public boolean canWalk() {
		return canWalk;
	}

	/**
	 * The delay in ms, of when we can attack again.
	 */
	private long combatDelay = 0;

	/**
	 * Gets the combat delay.
	 * 
	 * @return The current combat delay.
	 */
	public long getCombatDelay() {
		return combatDelay;
	}

	/**
	 * Sets the combat delay.
	 * 
	 * @param delay
	 *            The new delay.
	 */
	public void setCombatDelay(long delay) {
		this.combatDelay = delay;
	}

	/**
	 * Increases the combat delay.
	 * 
	 * @param delay
	 *            The amount we want to increase.
	 * @return The new combat delay.
	 */
	public long increaseCombatDelay(long delay) {
		if (combatDelay < 0) {
			combatDelay = 0;
		}
		return this.combatDelay += delay;
	}

	/**
	 * Decreases the combat delay.
	 * 
	 * @param delay
	 *            The amount we want to decrease.
	 * @return The new combat delay.
	 */
	public long decreaseCombatDelay(long delay) {
		if ((this.combatDelay -= delay) < 0) {
			this.combatDelay = 0;
		}
		return this.combatDelay;
	}

	/**
	 * Checks if this entity is ready to perform a special attack. This is also
	 * used for special NPC's, which have different attacks etc.
	 * 
	 * @param victim
	 *            The victim we're currently in combat with.
	 */
	public abstract boolean getSpecialAttack(Entity victim);

	/**
	 * Gets the size of this entity.
	 * 
	 * @return The size of this entity.
	 */
	public abstract int getSize();

	/**
	 * Gets the approximate distance this entity can attack from.
	 */
	public int getAttackingDistance() {
		AttackType type = getAttackType();
		if (type.equals(AttackType.MELEE)) {
			return 1;
		} else if (type.equals(AttackType.MAGIC)) {
			return 8;
		} else { // Ranged.
			boolean longrange = getAttackStyle() == Combat.LONGRANGE;
			if (this instanceof Player) {
				Player p = (Player) this;
				if (p.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
					String name = p.getEquipment().get(Equipment.SLOT_WEAPON)
							.getDefinition().getName();
					if (name.contains("shortbow")) {
						return longrange ? 9 : 7;
					} else if (name.contains("longbow")) {
						return 10;
					} else if (name.contains("c'bow")
							|| name.contains("crossbow")) {
						return longrange ? 9 : 7;
					} else if (name.contains("comp bow")) {
						return longrange ? 11 : 10;
					}
				}
			}
			/*
			 * knives:4:6 darts:3:5 crystal:10:10 dbow:12:13 chins:9:10 javs:5:7
			 * t'axe:4:6 salamander:1:1 seercull:8:10 dorgbow:6:8 comp.ogre:5:7
			 * dfs:1 recoil,veng:ANY RANGE
			 */
			return longrange ? 8 : 6;
		}
	}

	public void setAttackButton(int attackButton) {
		this.attackButton = attackButton;
	}

	public int getAttackButton() {
		return attackButton;
	}

}
