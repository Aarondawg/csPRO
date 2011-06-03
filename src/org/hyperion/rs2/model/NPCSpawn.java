package org.hyperion.rs2.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Logger;

import org.hyperion.rs2.content.skills.Fishing;
import org.hyperion.util.XStreamUtil;

/**
 * Represents an NPC spawn. The loading and spawning is handled in here as well.
 * 
 * @author Brown.
 */

public class NPCSpawn {

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(NPCSpawn.class
			.getName());

	/**
	 * The ID of the NPC this spawn represents.
	 */
	private int npcId;

	/**
	 * The walking type.
	 */
	private int walkingType;

	/**
	 * The spawn location.
	 */
	private Location spawnLocation;

	/**
	 * The minimum location of this NPCSpawn.
	 */
	private Location minLocation;

	/**
	 * The maximum location of this NPCSpawn.
	 */
	private Location maxLocation;

	/**
	 * Loads the NPC spawn list from xml, and spawns all the NPC's.
	 * 
	 * @throws FileNotFoundException
	 *             If it couldn't find npcSpawns.xml.
	 */
	@SuppressWarnings("unchecked")
	public static void init() throws FileNotFoundException {
		logger.info("Loading NPC spawns...");
		List<NPCSpawn> spawns = (List<NPCSpawn>) XStreamUtil.getXStream()
				.fromXML(new FileInputStream("data/npcSpawns.xml"));
		for (NPCSpawn spawn : spawns) {
			NPC npc = NPC.create(NPCDefinition.forId(spawn.getId()),
					spawn.getLocation(), spawn.getMinimumLocation(),
					spawn.getMaximumLocation());
			npc.setWalkingType(spawn.getWalkingType());
			World.getWorld().getNPCs().add(npc);
		}
		logger.info("Loaded " + spawns.size() + " NPC spawns.");
		Fishing.spawnFishingSpots();
	}

	/**
	 * Gets the location associated with this NPCSpawn.
	 * 
	 * @return The spawn location.
	 */
	private Location getLocation() {
		return spawnLocation;
	}

	/**
	 * Gets the id of this NPCSpawn.
	 * 
	 * @return The NPC id.
	 */
	private int getId() {
		return npcId;
	}

	/**
	 * Gets the lowest location the NPC can walk into.
	 * 
	 * @return The minLocation.
	 */
	public Location getMinimumLocation() {
		return minLocation;
	}

	/**
	 * Gets the highest location the NPC can walk into.
	 * 
	 * @return The maxLocation.
	 */
	public Location getMaximumLocation() {
		return maxLocation;
	}

	/**
	 * Gets the walking type for this npc.
	 * 
	 * @return The walking type.
	 */
	public int getWalkingType() {
		return walkingType;
	}

}
