package org.hyperion.rs2.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.hyperion.rs2.content.NPCDropItem;
import org.hyperion.util.XStreamUtil;

/**
 * Controls all the NPC drops in game.
 * 
 * @author Brown
 */

public class NPCDropController {

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger
			.getLogger(NPCDropController.class.getName());

	/**
	 * The random instance we use to get drops with.
	 */
	private static final Random RANDOM = new Random();

	/**
	 * The id's of the NPC's that "owns" this class.
	 */
	private int npcIds[];

	/**
	 * All the drops that belongs to this class.
	 */
	private NPCDropItem[] drops;

	/**
	 * The map containing all the npc drops. ;)
	 */
	private static Map<Integer, NPCDropController> dropControllers = new HashMap<Integer, NPCDropController>();

	@SuppressWarnings("unchecked")
	public static void init() throws FileNotFoundException {
		List<NPCDropController> list = (List<NPCDropController>) XStreamUtil
				.getXStream().fromXML(new FileInputStream("data/npcDrops.xml"));
		for (NPCDropController npcDrop : list) {
			for (int id : npcDrop.getNpcIds()) {
				dropControllers.put(id, npcDrop);
			}
		}
		logger.info("Loaded " + dropControllers.size() + " npc drops.");
	}

	/**
	 * Gets the NPC drop controller by an id.
	 * 
	 * @return The NPC drops associated with this id.
	 */
	public static NPCDropController forId(int id) {
		return dropControllers.get(id);
	}

	/**
	 * Gets an array of all the items an NPC should drop. This will get 100%
	 * drops as well.
	 * 
	 * @return An array of the items this NPC should drop.
	 */
	public Item[] getDrops() {
		if (drops == null) {
			return null;
		}
		int random = RANDOM.nextInt(600); // Was 1000 before, waay too rare
											// drops.
		Item toDrop = null;
		int length = 1;
		for (NPCDropItem item : drops) {
			if (item.shouldAlwaysDrop()) {
				length++;
			} else {
				if (random < item.getHighestChance()
						&& random >= item.getLowestChance()) {
					toDrop = item.getItem();
				}
			}
		}
		Item[] toReturn = new Item[length];
		int index = 0;
		for (NPCDropItem item : drops) {
			if (item.shouldAlwaysDrop()) {
				toReturn[index++] = item.getItem();
			}
		}
		toReturn[index] = toDrop;
		return toReturn;
	}

	/**
	 * Gets the id's of the NPC's this class belongs to.
	 * 
	 * @return The id's of the NPC's.
	 */
	public int[] getNpcIds() {
		return npcIds;
	}

}
