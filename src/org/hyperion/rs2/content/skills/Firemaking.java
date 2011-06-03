package org.hyperion.rs2.content.skills;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.GroundItem;
import org.hyperion.rs2.model.GroundItemController;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.region.Region;

public class Firemaking {

	private static final Animation FIREMAKING_ANIMATION = Animation.create(733);
	private static final Animation RESET = Animation.create(-1);
	public static Map<Location, Integer> fireContainer = new HashMap<Location, Integer>();

	public static void firemakingEvent(final Player player, final int logsId,
			final Location loc, int slot, final int minLvl, final double xp,
			final int delay, final int fireId) {
		// player.setBusy(true);
		player.resetSkilling();
		player.getFiremakingVariables().setFiremaking(true);
		if (delay > 500) {
			player.playAnimation(FIREMAKING_ANIMATION);
			player.getFiremakingVariables().setFiremaking(true);
			player.getActionSender().sendMessage(
					"You attempt to light the logs.");
		}
		if (slot != -1) {
			player.getInventory().set(slot, null);
			GroundItemController
					.createGroundItem(new Item(logsId), player, loc);
			// World.getInstance().getItemManager().createGroundItem(player, new
			// Item(logsId));
		}
		player.getActionQueue().addAction(new Action(player, delay) {
			public void execute() {
				if (player.getFiremakingVariables().isFiremaking()) {
					if (delay > 500) {
						if (fireLogs(player, minLvl)) {
							player.playAnimation(RESET);
							// player.getUpdateFlags().setAppearanceUpdateRequired(true);
							player.getWalkingQueue().reset();
							player.getWalkingQueue().addStep(loc.getX() - 1,
									loc.getY());
							player.getWalkingQueue().finish();
							player.face(loc);
							player.getActionQueue().addAction(
									new FireAction(player, logsId, loc, xp,
											fireId));
							this.stop();
						} else {
							if (player.getFiremakingVariables()
									.getAnimationDelay() >= 3) {
								player.playAnimation(FIREMAKING_ANIMATION);
								player.getFiremakingVariables()
										.setAnimationDelay(0);
							} else {
								player.getFiremakingVariables()
										.increaseAnimationDelay();
							}
						}
					} else {
						player.playAnimation(RESET);
						player.getWalkingQueue().reset();
						player.getWalkingQueue().addStep(loc.getX() - 1,
								loc.getY());
						player.getWalkingQueue().finish();
						player.face(loc);
						player.getActionQueue()
								.addAction(
										new FireAction(player, logsId, loc, xp,
												fireId));
						this.stop();
					}
				} else {
					// player.setBusy(false);
					this.stop();
				}
			}

			@Override
			public QueuePolicy getQueuePolicy() {
				return QueuePolicy.NEVER;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.NON_WALKABLE;
			}
		});
	}

	public static class FireAction extends Action {

		public FireAction(final Player player, final int logsId,
				final Location loc, final double xp, final int fireId) {
			super(player, 800);
			this.player = player;
			this.logsId = logsId;
			this.loc = loc;
			this.exp = xp;
			this.fireId = fireId;
		}

		public void execute() {
			GroundItemController.removeGroundItemForAll(new GroundItem(player
					.getName(), loc, logsId, 1));
			// World.getWorld().getItemManager().destroyGroundItem(loc, logsId);
			player.getActionSender().sendMessage(
					"The fire catches and the logs begin to burn.");
			createGlobalFire(player, fireId, loc, 0, 10);
			fireContainer.put(loc, fireId);
			player.getSkills().addExperience(Skills.FIREMAKING, exp);
			removeFireEvent(player, loc);
			// player.setBusy(false);
			this.stop();
		}

		@Override
		public QueuePolicy getQueuePolicy() {
			return QueuePolicy.ALWAYS;// No trouble here, as the other preparing
										// action is set to NEVER.
		}

		@Override
		public WalkablePolicy getWalkablePolicy() {
			return WalkablePolicy.NON_WALKABLE;
		}

		private final Player player;
		private final int logsId;
		private final Location loc;
		private final double exp;
		private final int fireId;

	}

	private static void removeFireEvent(final Player player, final Location loc) {
		World.getWorld().submit(new Event(60000) {
			public void execute() {
				createGlobalFire(player, 6951, loc, 0, 10);
				fireContainer.remove(loc);
				GroundItemController.createGroundItem(new Item(592), player,
						loc);
				// World.getWorld().getItemManager().createGroundItem(loc, new
				// Item(592));
				this.stop();
			}
		});
	}

	private static void createGlobalFire(Player player, int objId,
			Location objLoc, int objFace, int objType) {
		for (Region reg : World.getWorld().getRegionManager()
				.getSurroundingRegions(player.getLocation())) {
			for (final Player p : reg.getPlayers()) {
				p.getActionSender().sendCreateObject(objId, objType, objFace,
						objLoc);
			}
		}
	}

	private static void fm(Player player, int logsId, int logsSlot, int minLvl,
			double xp, int fireId) {
		/*
		 * if(player.isBusy()) { return; }
		 */
		if (player.getSkills().getLevel(Skills.FIREMAKING) < minLvl) {
			player.getActionSender().sendMessage(
					"You need a Firemaking level of " + minLvl
							+ " to burn these logs.");
			return;
		}
		Location loc = player.getLocation();
		if (fireContainer.containsKey(loc)) {
			player.getActionSender().sendMessage("You can't light logs here.");
			return;
		}
		int delay = 0;
		if (player.getFiremakingVariables().isFiremaking()) {
			delay = 500;
		} else {
			delay = 2000;
			// resetFiremakingEvent(player);
		}

		Item logs = player.getInventory().get(logsSlot);
		if (player.getInventory().contains(590) && logs.getId() == logsId
				&& logs.getCount() == 1) {
			firemakingEvent(player, logsId, loc, logsSlot, minLvl, xp, delay,
					fireId);
		}
	}

	private static void light(Player player, int logsId, int x, int y,
			int minLvl, double xp, int fireId) {
		/*
		 * if(player.isBusy()) { return; }
		 */
		if (player.getSkills().getLevelForExperience(11) < minLvl) {
			player.getActionSender().sendMessage(
					"You need a Firemaking level of " + minLvl
							+ " to burn these logs.");
			return;
		}
		Location loc = Location.create(x, y, player.getLocation().getZ());
		if (fireContainer.containsKey(loc)) {
			player.getActionSender()
					.sendMessage("You can't light a fire here.");
			return;
		}
		// if(World.getWorld().getItemManager().getItemAmount(loc, logsId) < 1)
		// {
		if (!GroundItemController.groundItemExists(loc, logsId)) {
			return;
		}
		int delay = 0;
		if (player.getFiremakingVariables().isFiremaking()) {
			delay = 500;
		} else {
			delay = 2000;
			// resetFiremakingEvent(player);
		}
		// if(player.getInventory().contains(590) &&
		// World.getWorld().getItemManager().getItemAmount(loc, logsId) > 0 &&
		// player.getLocation().equals(loc)) {
		if (player.getInventory().contains(590)
				&& GroundItemController.groundItemExists(loc, logsId)
				&& player.getLocation().equals(loc)) {
			firemakingEvent(player, logsId, loc, -1, minLvl, xp, delay, fireId);
		}
	}

	/*
	 * private static void resetFiremakingEvent(final Player player) { final
	 * Location loc = Location.location(0, 0, 0); World.getWorld().submit(new
	 * Event(1500) { public void execute() {
	 * if(player.getFiremakingVariables().isFiremaking()) { if(!player.isBusy())
	 * { if(loc == player.getLocation()) {
	 * player.getFiremakingVariables().setFiremaking(false); this.stop(); } else
	 * { //TODO: Check what the fuck this is lel //loc = player.getLocation();
	 * //:( } } } else { this.stop(); } } }); }
	 */

	public static boolean fireLogs(Player player, int minLvl) {
		int lvl = player.getSkills().getLevelForExperience(Skills.FIREMAKING);
		double check = (lvl + 40 - Math.round(minLvl * 0.5));
		int rand = new Random().nextInt(100);
		if (check >= rand) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean parseItemOnItemIds(Player player, int itemUsed,
			int usedWith, int slot, int withSlot) {
		/**
		 * Normal logs.
		 */
		if (itemUsed == 590 && usedWith == 1511 || usedWith == 590
				&& itemUsed == 1511) {
			fm(player, 1511, itemUsed == 590 ? slot : withSlot, 1, 40, 2732);
			return true;
		}
		/**
		 * Normal logs (red).
		 */
		if (itemUsed == 590 && usedWith == 7404 || usedWith == 590
				&& itemUsed == 7404) {
			fm(player, 7404, itemUsed == 590 ? slot : withSlot, 1, 50, 11404);
			return true;
		}
		/**
		 * Normal logs (green).
		 */
		if (itemUsed == 590 && usedWith == 7405 || usedWith == 590
				&& itemUsed == 7405) {
			fm(player, 7405, itemUsed == 590 ? slot : withSlot, 1, 50, 11405);
			return true;
		}
		/**
		 * Normal logs (blue).
		 */
		if (itemUsed == 590 && usedWith == 7406 || usedWith == 590
				&& itemUsed == 7406) {
			fm(player, 7406, itemUsed == 590 ? slot : withSlot, 1, 50, 11406);
			return true;
		}
		/**
		 * Normal logs (white).
		 */
		if (itemUsed == 590 && usedWith == 10328 || usedWith == 590
				&& itemUsed == 10328) {
			fm(player, 10328, itemUsed == 590 ? slot : withSlot, 1, 50, 20000);
			return true;
		}
		/**
		 * Normal logs (purple).
		 */
		if (itemUsed == 590 && usedWith == 10329 || usedWith == 590
				&& itemUsed == 10329) {
			fm(player, 10329, itemUsed == 590 ? slot : withSlot, 1, 50, 20001);
			return true;
		}
		/**
		 * Achey.
		 */
		if (itemUsed == 590 && usedWith == 2862 || usedWith == 590
				&& itemUsed == 2862) {
			fm(player, 2862, itemUsed == 590 ? slot : withSlot, 1, 40, 2732);
			return true;
		}
		/**
		 * Oak.
		 */
		if (itemUsed == 590 && usedWith == 1521 || usedWith == 590
				&& itemUsed == 1521) {
			fm(player, 1521, itemUsed == 590 ? slot : withSlot, 15, 60, 2732);
			return true;
		}
		/**
		 * Willow.
		 */
		if (itemUsed == 590 && usedWith == 1519 || usedWith == 590
				&& itemUsed == 1519) {
			fm(player, 1519, itemUsed == 590 ? slot : withSlot, 30, 90, 2732);
			return true;
		}
		/**
		 * Teak.
		 */
		if (itemUsed == 590 && usedWith == 6333 || usedWith == 590
				&& itemUsed == 6333) {
			fm(player, 6333, itemUsed == 590 ? slot : withSlot, 35, 105, 2732);
			return true;
		}
		/**
		 * Maple.
		 */
		if (itemUsed == 590 && usedWith == 1517 || usedWith == 590
				&& itemUsed == 1517) {
			fm(player, 1517, itemUsed == 590 ? slot : withSlot, 45, 135, 2732);
			return true;
		}
		/**
		 * Yew
		 */
		if (itemUsed == 590 && usedWith == 1515 || usedWith == 590
				&& itemUsed == 1515) {
			fm(player, 1515, itemUsed == 590 ? slot : withSlot, 60, 202.5, 2732);
			return true;
		}
		/**
		 * Magic
		 */
		if (itemUsed == 590 && usedWith == 1513 || usedWith == 590
				&& itemUsed == 1513) {
			fm(player, 1513, itemUsed == 590 ? slot : withSlot, 75, 303.8, 2732);
			return true;
		}

		return false;
	}

	public static boolean parseLightIds(Player player, int itemId, int x, int y) {
		switch (itemId) {
		/**
		 * Normal logs
		 */
		case 1511:
			light(player, itemId, x, y, 1, 40, 2732);
			return true;
			/**
			 * Normal logs (red)
			 */
		case 7405:
			light(player, itemId, x, y, 1, 50, 11404);
			return true;
			/**
			 * Normal logs (green)
			 */
		case 7404:
			light(player, itemId, x, y, 1, 50, 11405);
			return true;
			/**
			 * Normal logs (blue)
			 */
		case 7406:
			light(player, itemId, x, y, 1, 50, 11406);
			return true;
			/**
			 * Normal logs (white)
			 */
		case 10328:
			light(player, itemId, x, y, 1, 50, 20000);
			return true;
			/**
			 * Normal logs (purple)
			 */
		case 10329:
			light(player, itemId, x, y, 1, 50, 20001);
			return true;
			/**
			 * Achey
			 */
		case 2862:
			light(player, itemId, x, y, 1, 40, 2732);
			return true;
			/**
			 * Oak
			 */
		case 1521:
			light(player, itemId, x, y, 15, 60, 2732);
			return true;
			/**
			 * Willow
			 */
		case 1519:
			light(player, itemId, x, y, 30, 90, 2732);
			return true;
			/**
			 * Teak
			 */
		case 6333:
			light(player, itemId, x, y, 35, 105, 2732);
			return true;
			/**
			 * Maple
			 */
		case 1517:
			light(player, itemId, x, y, 45, 135, 2732);
			return true;
			/**
			 * Mahogany
			 */
		case 6332:
			light(player, itemId, x, y, 50, 157.5, 2732);
			return true;
			/**
			 * Yew
			 */
		case 1515:
			light(player, itemId, x, y, 60, 202.5, 2732);
			return true;
			/**
			 * Magic
			 */
		case 1513:
			light(player, itemId, x, y, 75, 303.8, 2732);
			return true;
		}
		return false;
	}

	public static void coatLogsEvent(final Player player, final int lighterId,
			final int logsSlot, final String color, final int cLogsId) {
		// //player.setBusy(true);
		World.getWorld().submit(new Event(300) {
			public void execute() {
				player.getActionSender().sendMessage(
						"You coat the logs with the " + color + " chemicals.");
				player.getInventory().remove(new Item(lighterId));
				player.getInventory().set(logsSlot, null);
				player.getInventory().add(new Item(cLogsId, 1));
				// player.setBusy(false);
				this.stop();
			}
		});
	}

	public static void coat(Player player, int lighterId, int lighterSlot,
			int logsSlot, String color, int cLogsId) {
		/*
		 * if(player.isBusy()) { return; }
		 */
		Item lighter = player.getInventory().get(lighterSlot);
		Item logs = player.getInventory().get(logsSlot);
		if (lighter.getId() == lighterId && lighter.getCount() >= 1
				&& logs.getId() == 1511 && logs.getCount() == 1) {
			coatLogsEvent(player, lighterId, logsSlot, color, cLogsId);
		}
	}

	public static boolean parseCoatingIds(Player player, int itemUsed,
			int usedWith, int slot, int withSlot) {
		if (itemUsed == 7329 && usedWith == 1511 || usedWith == 7329
				&& itemUsed == 1511) {
			coat(player, 7329, itemUsed == 1511 ? slot : withSlot,
					itemUsed == 1511 ? withSlot : slot, "red", 7404);
			return true;
		}
		if (itemUsed == 7330 && usedWith == 1511 || usedWith == 7330
				&& itemUsed == 1511) {
			coat(player, 7330, itemUsed == 1511 ? slot : withSlot,
					itemUsed == 1511 ? withSlot : slot, "green", 7405);
			return true;
		}
		if (itemUsed == 7331 && usedWith == 1511 || usedWith == 7331
				&& itemUsed == 1511) {
			coat(player, 7331, itemUsed == 1511 ? slot : withSlot,
					itemUsed == 1511 ? withSlot : slot, "blue", 7406);
			return true;
		}
		if (itemUsed == 10326 && usedWith == 1511 || usedWith == 10326
				&& itemUsed == 1511) {
			coat(player, 10326, itemUsed == 1511 ? slot : withSlot,
					itemUsed == 1511 ? withSlot : slot, "purple", 10329);
			return true;
		}
		if (itemUsed == 10327 && usedWith == 1511 || usedWith == 10327
				&& itemUsed == 1511) {
			coat(player, 10327, itemUsed == 1511 ? slot : withSlot,
					itemUsed == 1511 ? withSlot : slot, "white", 10328);
			return true;
		}
		return false;
	}

}
