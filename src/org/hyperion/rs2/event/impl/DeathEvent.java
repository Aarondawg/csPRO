package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.content.minigames.FightCaves;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.GroundItemController;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDropController;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;

/**
 * The death event handles player and npc deaths. Drops loot, does animation,
 * teleportation, etc.
 * 
 * @author Graham
 * @author Brown
 */
public class DeathEvent extends Event {

	private Entity entity;
	private static final Animation RESET = Animation.create(-1);

	/**
	 * Creates the death event for the specified entity.
	 * 
	 * @param entity
	 *            The player or npc whose death has just happened.
	 */
	public DeathEvent(Entity entity) {
		super(4000);
		this.entity = entity;
	}

	@Override
	public void execute() {
		if (entity instanceof Player) {
			Player p = (Player) entity;
			resetPlayer(p);
			p.getActionSender().sendMessage("Oh dear, you are dead!");
			if (!p.getFightCaves().exitCaves()) {
				p.setTeleportTarget(Location.create(
						(int) (Entity.DEFAULT_LOCATION.getX() + (Math.random() * 7)),
						(int) (Entity.DEFAULT_LOCATION.getY() + (Math.random() * 7)),
						Entity.DEFAULT_LOCATION.getZ()));
				p.dropLoot();
			}
			p.clearKillersHits();
		} else {
			final NPC n = (NPC) entity;
			final Location goodPos = n.getLocation().getActualLocation(
					n.getDefinition().getSize());
			final Entity killer = n.getKiller();
			n.clearKillersHits();
			if (killer != null && killer instanceof Player) {
				Player pKiller = (Player) killer;
				if (n.getLocation().isInBarrows()) {
					pKiller.getBarrows().increaseKillCount();
				}
				if (pKiller.getSlayerTask().hasTask()) {
					for (int task : pKiller.getSlayerTask().getSlayerNpcs()) {
						if (task == n.getDefinition().getId()) {
							pKiller.getSlayerTask().decrementCount();
							pKiller.getSkills().addExperience(Skills.SLAYER,
									n.getMaxHitpoints());
						}
					}
				}
				switch (n.getDefinition().getId()) {
				/*
				 * Cyclopes from Warriors Guild.
				 */
				case 4291:
				case 4292:
					pKiller.getWarriorsGuild().killedCyclop(goodPos);
					break;
				}
			}
			n.setInvisible(true);
			NPCDropController drops = NPCDropController.forId(n.getDefinition()
					.getId());
			if (drops != null) {
				for (Item item : drops.getDrops()) {
					if (item != null) {
						GroundItemController.createGroundItem(item,
								n.getKiller(), goodPos);
					}
				}
			}
			World.getWorld().submit(
					new Event(n.getDefinition().getRespawn() * 1000) {

						@Override
						public void execute() {
							n.setInvisible(false);
							n.setAutoRetaliating(true);
							n.setHitpoints(n.getMaxHitpoints());
							entity.setTeleportTarget(n.getLocation());
							entity.setDead(false);
							entity.playAnimation(RESET);
							entity.setCanWalk(true);
							this.stop();
						}

					});
		}
		this.stop();
	}

	/**
	 * Everything but teleporting and message sending is done in this method,
	 * making it possible to call it from other places as well. (Example:
	 * MiniGames)
	 * 
	 * @param player
	 *            The player who just died/needs a reset.
	 */
	public static void resetPlayer(Player player) {
		player.setTeleblocked(false);
		player.getSkills().setLevel(Skills.HITPOINTS,
				player.getSkills().getLevelForExperience(Skills.HITPOINTS));
		player.getPrayer().reset();
		for (int skill = 0; skill < Skills.SKILL_COUNT; skill++) {
			player.getSkills().setLevel(skill,
					player.getSkills().getLevelForExperience(skill));
		}
		player.getSpecials().setAmount(1000);
		player.setFrozen(false);
		player.setDead(false);
		player.playAnimation(RESET);
		player.setCanWalk(true);
		player.getActionSender().sendStopFollowing(false);
		player.getPoison().setPoisonHit(0);
	}

}