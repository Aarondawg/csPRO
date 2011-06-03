package org.hyperion.rs2.content.skills.agility;

import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.content.skills.agility.impl.*;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;

public class Agility {

	private static final Map<Location, Obstacle> agilityMap = new HashMap<Location, Obstacle>();

	public static boolean handleObjectOption(Player player, Location location,
			int option) {
		Obstacle obs = agilityMap.get(location);
		if (obs == null || obs.getObjectOption() != option) {
			return false;
		} else {
			obs.climb(player);
			return true;
		}
	}

	static {
		/*
		 * Start of the Gnome Agility course..
		 */
		/*
		 * Net climbing..
		 */
		TeleportObstacle NET_CLIMB_1 = new TeleportObstacle(1, Location.create(
				2473, 3424, 1), Animation.create(740), 1800, 7.5);
		agilityMap.put(Location.create(2471, 3425, 0), NET_CLIMB_1);
		agilityMap.put(Location.create(2473, 3425, 0), NET_CLIMB_1);
		agilityMap.put(Location.create(2475, 3425, 0), NET_CLIMB_1);
		/*
		 * Branch climbing..
		 */
		TeleportObstacle BRANCH_CLIMB_1 = new TeleportObstacle(1,
				Location.create(2473, 3420, 2), Animation.create(740), 1200, 5); // TODO:
																					// Real
																					// animation
		agilityMap.put(Location.create(2473, 3422, 1), BRANCH_CLIMB_1);
		/*
		 * Rope in the tree upstairs..
		 */
		WalkingObstacle BALACING_ROPE = new WalkingObstacle(1,
				new Location[] { Location.create(2477, 3420, 2),
						Location.create(2483, 3420, 2) }, 762, 3000, 7.5);
		agilityMap.put(Location.create(2478, 3420, 2), BALACING_ROPE);
		agilityMap.put(Location.create(2479, 3420, 2), BALACING_ROPE);
		agilityMap.put(Location.create(2480, 3420, 2), BALACING_ROPE);
		agilityMap.put(Location.create(2481, 3420, 2), BALACING_ROPE);
		agilityMap.put(Location.create(2482, 3420, 2), BALACING_ROPE);
		/*
		 * Branch climbing..
		 */
		TeleportObstacle BRANCH_CLIMB_2 = new TeleportObstacle(1,
				Location.create(2487, 3420, 0), Animation.create(740), 600, 5);// TODO:
																				// Real
																				// animation
		agilityMap.put(Location.create(2486, 3419, 2), BRANCH_CLIMB_2);

		/*
		 * Net climbing 2
		 */
		WalkingObstacle NET_CLIMB_2_1 = new WalkingObstacle(1,
				new Location[] { Location.create(2484, 3425, 0),
						Location.create(2484, 3427, 0) }, 3063, 3000, 7.5); // TODO:
																			// Real
																			// animation
																			// ID?
		agilityMap.put(Location.create(2483, 3426, 0), NET_CLIMB_2_1);

		WalkingObstacle NET_CLIMB_2_2 = new WalkingObstacle(1,
				new Location[] { Location.create(2486, 3425, 0),
						Location.create(2486, 3427, 0) }, 3063, 3000, 7.5); // TODO:
																			// Real
																			// animation
																			// ID?
		agilityMap.put(Location.create(2485, 3426, 0), NET_CLIMB_2_2);

		WalkingObstacle NET_CLIMB_2_3 = new WalkingObstacle(1,
				new Location[] { Location.create(2488, 3425, 0),
						Location.create(2488, 3427, 0) }, 3063, 3000, 7.5); // TODO:
																			// Real
																			// animation
																			// ID?
		agilityMap.put(Location.create(2487, 3426, 0), NET_CLIMB_2_3);

		/*
		 * End of the Gnome Agility course..
		 */
	}

}
