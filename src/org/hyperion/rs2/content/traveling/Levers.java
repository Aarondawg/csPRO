package org.hyperion.rs2.content.traveling;

import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

public class Levers {

	private static Map<Location, Lever> LEVERS = new HashMap<Location, Lever>();
	private static final Animation LEVER_ANIMATION = Animation.create(2140);

	public static boolean handle(final Player player, final Location loc,
			final int objectId) {
		final Lever lever = LEVERS.get(loc);
		if (lever == null || objectId == 5961 /* You've already clicked the lever. */) {
			return false;
		}
		/*
		 * Prevents mass clicking them.
		 */
		if (player.getMagic().getLastTeleport() < 3000) {
			return true;
		}
		player.playAnimation(LEVER_ANIMATION);
		player.getActionSender().sendCreateObject(5961, 4,
				lever.getDirection1(), loc);
		player.setCanWalk(false);
		player.getMagic().setLastTeleport(System.currentTimeMillis());
		World.getWorld().submit(new Event(1500) {

			@Override
			public void execute() {
				player.getActionSender().sendCreateObject(objectId, 4,
						lever.getDirection2(), loc);
				player.playAnimation(Animation.create(714));
				player.playGraphics(Graphic.create(301, (100 << 16)));
				World.getWorld().submit(new Event(1800) {

					@Override
					public void execute() {
						player.setTeleportTarget(lever.getTargetLocation());
						player.playAnimation(Animation.create(-1));
						player.setCanWalk(true);
						this.stop();
					}

				});
				this.stop();
			}

		});
		return true;
	}

	private static class Lever {

		private final Location targetLocation;
		private final int direction1;
		private final int direction2;

		public Location getTargetLocation() {
			return targetLocation;
		}

		public int getDirection1() {
			return direction1;
		}

		public int getDirection2() {
			return direction2;
		}

		public Lever(Location target, int direction1, int direction2) {
			this.targetLocation = target;
			this.direction1 = direction1;
			this.direction2 = direction2;
		}
	}

	/**
	 * This populates the map.
	 */
	static {
		/*
		 * King Black Dragon levers.
		 */
		LEVERS.put(Location.create(3067, 10253, 0),
				new Lever(Location.create(2271, 4680, 0), 3, 3));
		LEVERS.put(Location.create(2271, 4680, 0),
				new Lever(Location.create(3067, 10253, 0), 3, 3));

		// edgville to magebank
		// player.getActionAssistant().pullLever(player, x, y, 5961, 0, 3, 3153,
		// 3923, 0);

		// magebank back to edgeville
		// player.getActionAssistant().pullLever(player, x, y, 5961, 0, 4, 3079,
		// 3489, 0);
	}

}
