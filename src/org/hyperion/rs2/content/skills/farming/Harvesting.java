package org.hyperion.rs2.content.skills.farming;

import java.util.Random;

import org.hyperion.rs2.content.GlobalObjectManager;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Inventory;

public class Harvesting {

	/**
	 * The random instance we're using.
	 */
	private static final Random r = new Random();

	/**
	 * Array of all the harvesting objects ingame.
	 */
	private static final int[] HARVESTING_OBJECT_IDS = { 1161, // Cabbage.
			3366, // Onion
			313, // Wheat
			5584, // Wheat
			5585, // Wheat
			312, // Potatoes.
	};

	/**
	 * Array of all the harvesting items to recieve.
	 */
	private static final Item[] HARVESTING_ITEMS = { new Item(1965), // Cabbage
			new Item(1957), // Onion
			new Item(1947), // Grain.
			new Item(1947), // Grain.
			new Item(1947), // Grain.
			new Item(1942), // Potato.
	};

	private static final Animation PICKING = Animation.create(827);

	private static final Item FLAX = new Item(1779);

	public static boolean checkForHarvesting(final Player player, int objectId,
			final Location loc) {
		for (int index = 0; index < HARVESTING_OBJECT_IDS.length; index++) {
			if (HARVESTING_OBJECT_IDS[index] == objectId) {
				// Destroy object, register event.
				if (Inventory.addInventoryItem(player, HARVESTING_ITEMS[index])) {
					player.playAnimation(PICKING);
					String name = HARVESTING_ITEMS[index].getDefinition()
							.getName().toLowerCase();
					player.getActionSender().sendMessage(
							"You pick "
									+ (name.equals("grain") ? "some " : name
											.equals("onion") ? "an " : "a ")
									+ name + ".");
					GlobalObjectManager.getInstance().destroyGlobalObject(
							player, loc);
					World.getWorld().submit(new Event(10000) {// Recreates the
																// object after
																// 10 seconds.
								public void execute() {
									GlobalObjectManager.getInstance()
											.createGlobalObject(player, 2646,
													loc, r.nextInt(4), 10);
									this.stop();
								}
							});
				}
			}
		}
		/*
		 * Some harvesting objects might have "more than one item" in it. We'll
		 * hardcode those in this switch.
		 */
		switch (objectId) {
		/*
		 * Flax picking.
		 */
		case 2646:
			if (Inventory.addInventoryItem(player, FLAX)) {
				player.playAnimation(PICKING);
				if (r.nextInt(9) == 0) {
					GlobalObjectManager.getInstance().destroyGlobalObject(
							player, loc);
					World.getWorld().submit(new Event(30000) {// Recreates the
																// object after
																// 30 seconds.
								public void execute() {
									GlobalObjectManager.getInstance()
											.createGlobalObject(player, 2646,
													loc, r.nextInt(4), 10);
									this.stop();
								}
							});
				}
			}
			return true;
			// case BUSHES:
			// break;

		}
		return false;
	}

}
