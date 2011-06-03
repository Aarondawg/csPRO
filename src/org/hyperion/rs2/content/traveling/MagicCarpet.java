package org.hyperion.rs2.content.traveling;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

public class MagicCarpet {

	/**
	 * Takes off with the magic carpet, and gets you to the provided location.
	 * 
	 * @param where
	 *            Where the Magic carpet should fly.
	 */
	public static void fly(final Player player, FlyingLocation where) {
		/*
		 * This array will contain all the coordinates we're going to walk. {{x,
		 * y}, {x,y}, {x,y}}
		 */
		int[][] coordinates = null;
		switch (FlyingLocation
				.getFlyingLocationByLocation(player.getLocation())) {
		/*
		 * The take off location is the Ruins of Uzer.
		 */
		case RUINS_OF_UZER:
			switch (where) {
			/*
			 * The landing location is the Ruins of Uzer.
			 */
			case RUINS_OF_UZER:
				// TODO: Get a new length, do the correct coordinates.
				int index = 0;
				coordinates = new int[100][2];
				coordinates[index++] = new int[] { 3232, 3232 };
				coordinates[index++] = new int[] { 3232, 3232 };
				coordinates[index++] = new int[] { 3232, 3232 };
				coordinates[index++] = new int[] { 3232, 3232 };
				coordinates[index++] = new int[] { 3232, 3232 };
				coordinates[index++] = new int[] { 3232, 3232 };
				coordinates[index++] = new int[] { 3232, 3232 };
				coordinates[index++] = new int[] { 3232, 3232 };
				coordinates[index++] = new int[] { 3232, 3232 };
				coordinates[index++] = new int[] { 3232, 3232 };
				break;
			}
			break;
		/*
		 * The take off location is Pollnivneach
		 */
		case POLLNIVNEACH:
			break;
		/*
		 * The take off location is the Bedabin camp.
		 */
		case BEDABIN_CAMP:
			break;
		}
		/*
		 * We have to make it final to work in the event.
		 */
		final int[][] finalCoordinates = coordinates;
		World.getWorld().submit(new Event(10000) {

			int index = 0;

			@Override
			public void execute() {
				/*
				 * We get the coordinates from the array.
				 */
				int x = finalCoordinates[index][0];
				int y = finalCoordinates[index][1];
				/*
				 * Add the steps to the walking queue.
				 */
				player.getWalkingQueue().addStep(x, y);
				/*
				 * Increase the index.
				 */
				index++;
				/*
				 * We're done, we land, and stop the event.
				 */
				if (index == finalCoordinates.length) {
					// TODO: Landing.
					this.stop();
				}
			}

		});
	}

	private static final int[] FLYING_ANIMATIONS = { 2261, 2261, 2263, 2263,
			2263, 2263, 2263, };

	public enum FlyingLocation {
		RUINS_OF_UZER(3535, 3535), // TODO: Finish those!
		POLLNIVNEACH(2323, 2323), BEDABIN_CAMP(2323, 2323);

		/**
		 * The flying location.
		 */
		private FlyingLocation(int x, int y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * The coordinates.
		 */
		final int x, y;

		/**
		 * Checks if the flying location is within distance of a location.
		 * 
		 * @return True if, false if not.
		 */
		public boolean isWithinDistance(Location loc) {
			return (loc.isWithinDistance(x, y, 0));
		}

		/**
		 * Gets the take off location by the players location. (Region based,
		 * should probably redo)
		 * 
		 * @param loc
		 *            The players location.
		 * @return The take-off location, null if none.
		 */
		public static FlyingLocation getFlyingLocationByLocation(Location loc) {
			if (RUINS_OF_UZER.isWithinDistance(loc)) {
				return RUINS_OF_UZER;
			}
			if (POLLNIVNEACH.isWithinDistance(loc)) {
				return POLLNIVNEACH;
			}
			if (BEDABIN_CAMP.isWithinDistance(loc)) {
				return BEDABIN_CAMP;
			}
			return null;
		}
	}

}
