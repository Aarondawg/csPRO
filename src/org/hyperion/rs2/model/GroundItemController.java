package org.hyperion.rs2.model;

import java.util.ArrayList;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.region.Region;

/**
 * Controls all GroundItems world-wide.
 * 
 * @author Martin
 * @author Brown
 */
public class GroundItemController {

	/**
	 * This ArrayList contains all ground items.
	 */
	private static final ArrayList<GroundItem> items = new ArrayList<GroundItem>();

	/**
	 * Gets the ArrayList that contains all ground items.
	 * 
	 * @return The ArrayList that contains all ground items.
	 */
	public static ArrayList<GroundItem> getGroundItems() {
		return items;
	}

	/**
	 * Checks if a specific ground item exists.
	 * 
	 * @return True if, false if not.
	 */
	public static boolean groundItemExists(Location l, int id) {
		for (GroundItem g : items) {
			if (g.getLocation().equals(l) && g.getId() == id
					&& g.getTime() != DISAPPEAR) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Spawns a ground item for everyone to see.
	 * 
	 * @param g
	 *            The ground item object to spawn.
	 */
	public static void spawnForEveryone(GroundItem g) {
		for (final Region r : g.getRegions()) {
			for (final Player player : r.getPlayers()) {
				if (player.getName().equals(g.getOwner()))
					// The owner has the item spawned as he drops. If this
					// Was removed, it would spawn another item, as this
					// Creates an item for EVERYONE.
					continue;

				player.getActionSender().sendCreateGroundItem(g);
			}
		}
	}

	/**
	 * Remove a ground item for all players.
	 * 
	 * @param g
	 *            The ground item object to remove.
	 */
	public static void removeGroundItemForAll(GroundItem g) {
		synchronized (items) {
			// Remove the GroundItem from the ArrayList.
			if (items.remove(g)) {
				for (final Region r : g.getRegions()) {
					for (final Player player : r.getPlayers()) {
						player.getActionSender().sendDestroyGroundItem(g);
					}
				}
			}
		}
	}

	/**
	 * Gets a GroundItem according to the data provided.
	 * 
	 * @param l
	 *            The location.
	 * @param id
	 *            The ID to look for.
	 * @return The GroundItem object, if any.
	 */
	public static GroundItem getGroundItem(Location l, int id) {
		for (GroundItem g : items) {
			if (g.getId() == id && g.getLocation().equals(l)) {
				return g;
			}
		}
		return null;
	}

	/**
	 * Creates a ground item.
	 * 
	 * @param item
	 *            The item we want to put on the floor.
	 * @param entity
	 *            The name of the person who dropped it / who it belongs to.
	 * @param location
	 *            Where we want it to be put.
	 */
	public static void createGroundItem(Item item, Entity entity,
			Location location) {
		if (entity instanceof Player) {
			Player player = (Player) entity;

			if (item.getDefinition().isStackable()) {
				// Check if theres already an item on the floor, with that id.
				GroundItem orig = getGroundItem(location, item.getId());
				if (orig != null) {
					// If there is, we check if the owner is the same.
					if (orig.getOwner() != null
							&& orig.getOwner().equals(player.getName())) {
						// We simply increase the delay, I don't know how to
						// handle it other ways.
						orig.increaseCount(item.getCount());
						// TODO: Do this a proper way :(
						// Make the GroundItem update.
						player.getActionSender().sendDestroyGroundItem(orig);
						player.getActionSender().sendCreateGroundItem(orig);
						return; // Nothing else to do in here.
					}
				}

			}

			// Create the object for the GroundItem.
			GroundItem g = new GroundItem(player.getName(), location,
					item.getId(), item.getCount());

			// Add the GroundItem to the ArrayList, so it gets processed.
			items.add(g);
			// Make the GroundItem appear.
			player.getActionSender().sendCreateGroundItem(g);
		} else {
			// null or NPC.
			if (item.getDefinition().isStackable()) {
				// Check if theres already an item on the floor, with that id.
				GroundItem orig = getGroundItem(location, item.getId());
				if (orig != null) {
					// If there is, we check if the owner is the same.
					if (orig.getOwner() == null) {
						// We simply increase the delay, I don't know how to
						// handle it other ways.
						orig.increaseCount(item.getCount());
						// TODO: Do this a proper way :(
					}
					// Make the GroundItem update.
					return; // Nothing else to do in here.
				}

			}
			// Create the object for the GroundItem.
			GroundItem g = new GroundItem(null, location, item.getId(),
					item.getCount());

			// Add the GroundItem to the ArrayList, so it gets processed.
			items.add(g);
		}

	}

	private static final Animation BONE_BURYING_ANIMATION = Animation
			.create(827);

	/**
	 * Picks up a GroundItem.
	 * 
	 * @param l
	 *            The items location.
	 * @param id
	 *            The id of the ground item clicked.
	 * @param player
	 *            The player picking up the GroundItem.
	 */
	public static void pickupGroundItem(final Location l, final int id,
			Player player) {
		player.getActionQueue().cancelQueuedActions();
		/*
		 * Else, we wait till we're at the actual location.
		 */
		player.getActionQueue().addAction(new Action(player, 0) {

			@Override
			public QueuePolicy getQueuePolicy() {
				return QueuePolicy.NEVER;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.NON_WALKABLE;
			}

			@Override
			public void execute() {
				if (getPlayer().getLocation().withinRange(l, 0)) {
					GroundItem g = getGroundItem(l, id);
					if (g != null) {
						if (Inventory.addInventoryItem(getPlayer(), g)) {
							removeGroundItemForAll(g);
						}
					}
					this.stop();
				} else if (getPlayer().getLocation().withinRange(l, 1)
						&& getPlayer().getWalkingQueue().isEmpty()) {
					GroundItem g = getGroundItem(l, id);
					if (g != null) {
						if (Inventory.addInventoryItem(getPlayer(), g)) {
							getPlayer().face(l);
							getPlayer().playAnimation(BONE_BURYING_ANIMATION); // Couldn't
																				// find
																				// a
																				// decent
																				// one,
																				// lol.
							removeGroundItemForAll(g);
						}
					}
					this.stop();
				}
				this.setDelay(600);
			}

		});

	}

	public static void refresh(Player p) {
		for (GroundItem g : items) {
			if (p.getLocation().isWithinDistance(g.getLocation())
					&& (g.getOwner() == null
							|| g.getTime() <= APPEAR_FOR_EVERYONE || p
							.getName().equals(g.getOwner()))) {
				p.getActionSender().sendCreateGroundItem(g);
			}
		}
	}

	/*
	 * Constants, the stages of the ground item. Total span: 4 minutes.
	 */
	public static final int APPEAR_FOR_EVERYONE = 120;
	public static final int DISAPPEAR = 0;
}