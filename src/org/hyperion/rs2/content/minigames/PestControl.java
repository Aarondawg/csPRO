package org.hyperion.rs2.content.minigames;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.DeathEvent;
import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.GameObjectDefinition;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.npc.pestcontrol.PestControlMob;
import org.hyperion.rs2.model.npc.pestcontrol.PestControlPortal;
import org.hyperion.rs2.model.npc.pestcontrol.VoidKnight;

public class PestControl {

	/**
	 * The java.util.Random instance for Pest Control.
	 */
	private static final Random r = new Random();

	/**
	 * Flag indicating if there's currently a game going on.
	 */
	private static boolean gameGoingOn = false;

	/**
	 * A list holding all the players we have in the boat at the moment.
	 */
	private static final List<Player> PLAYERS_IN_BOAT = new ArrayList<Player>();

	/**
	 * A list holding all the players we have in the game at the moment.
	 */
	private static final List<Player> PLAYERS_IN_GAME = new ArrayList<Player>();

	/**
	 * The ingame pest control time.
	 */
	private static int gameTime = 1200; // Twenty minutes.

	/**
	 * The purple Pest Control Portal.
	 */
	private static final PestControlPortal PURPLE_PORTAL = (PestControlPortal) NPC
			.create(NPCDefinition.forId(3777), Location.create(2628, 2591, 0),
					null, null);

	/**
	 * The blue Pest Control Portal.
	 */
	private static final PestControlPortal BLUE_PORTAL = (PestControlPortal) NPC
			.create(NPCDefinition.forId(3778), Location.create(2680, 2588, 0),
					null, null);

	/**
	 * The yellow Pest Control Portal.
	 */
	private static final PestControlPortal YELLOW_PORTAL = (PestControlPortal) NPC
			.create(NPCDefinition.forId(3779), Location.create(2669, 2570, 0),
					null, null);

	/**
	 * The "red" Pest Control Portal.
	 */
	private static final PestControlPortal RED_PORTAL = (PestControlPortal) NPC
			.create(NPCDefinition.forId(3780), Location.create(2645, 2569, 0),
					null, null);

	/**
	 * The Void Knight in the center.
	 */
	private static final VoidKnight VOID_KNIGHT = (VoidKnight) NPC.create(
			NPCDefinition.forId(3782), Location.create(2656, 2592, 0), null,
			null);

	/**
	 * All the monsters for a game.
	 */
	private static final List<PestControlMob> MONSTERS = new ArrayList<PestControlMob>();

	/**
	 * The array of the DEFAULT pest control objects.
	 */
	private static final GameObject[] DEFAULT_PEST_CONTROL_OBJECTS = {
			/* Western facing gate. */
			new GameObject(GameObjectDefinition.forId(14233), Location.create(
					2643, 2593, 0), 0, 0), // Gate
			new GameObject(GameObjectDefinition.forId(14235), Location.create(
					2643, 2592, 0), 0, 0), // Gate

			/* South facing gate.. */
			new GameObject(GameObjectDefinition.forId(14233), Location.create(
					2656, 2585, 0), 0, 3), // Gate
			new GameObject(GameObjectDefinition.forId(14235), Location.create(
					2657, 2585, 0), 0, 3), // Gate

			/* Eastern gates */
			new GameObject(GameObjectDefinition.forId(14233), Location.create(
					2670, 2592, 0), 0, 2), // Gate
			new GameObject(GameObjectDefinition.forId(14235), Location.create(
					2670, 2593, 0), 0, 2), // Gate

			new GameObject(GameObjectDefinition.forId(14224), Location.create(
					2648, 2578, 0), 0, 3), // Barricade
			new GameObject(GameObjectDefinition.forId(14224), Location.create(
					2649, 2578, 0), 0, 3), // Barricade
			new GameObject(GameObjectDefinition.forId(14224), Location.create(
					2667, 2578, 0), 0, 3), // Barricade
			new GameObject(GameObjectDefinition.forId(14224), Location.create(
					2668, 2578, 0), 0, 3), // Barricade
			new GameObject(GameObjectDefinition.forId(14224), Location.create(
					2673, 2591, 0), 0, 2), // Barricade
			new GameObject(GameObjectDefinition.forId(14224), Location.create(
					2673, 2592, 0), 0, 2), // Barricade
			new GameObject(GameObjectDefinition.forId(14225), Location.create(
					2650, 2578, 0), 9, 2), // Barricade
			new GameObject(GameObjectDefinition.forId(14225), Location.create(
					2669, 2578, 0), 9, 2), // Barricade
			new GameObject(GameObjectDefinition.forId(14225), Location.create(
					2673, 2593, 0), 9, 1), // Barricade
			new GameObject(GameObjectDefinition.forId(14226), Location.create(
					2647, 2578, 0), 9, 3), // Barricade
			new GameObject(GameObjectDefinition.forId(14226), Location.create(
					2666, 2578, 0), 9, 3), // Barricade
			new GameObject(GameObjectDefinition.forId(14226), Location.create(
					2673, 2590, 0), 9, 2), // Barricade
			new GameObject(GameObjectDefinition.forId(14227), Location.create(
					2636, 2589, 0), 0, 0), // Barricade
			new GameObject(GameObjectDefinition.forId(14227), Location.create(
					2636, 2590, 0), 0, 0), // Barricade
			new GameObject(GameObjectDefinition.forId(14227), Location.create(
					2657, 2575, 0), 0, 3), // Barricade
			new GameObject(GameObjectDefinition.forId(14227), Location.create(
					2658, 2575, 0), 0, 3), // Barricade
			new GameObject(GameObjectDefinition.forId(14227), Location.create(
					2676, 2585, 0), 0, 2), // Barricade
			new GameObject(GameObjectDefinition.forId(14227), Location.create(
					2676, 2586, 0), 0, 2), // Barricade
			new GameObject(GameObjectDefinition.forId(14228), Location.create(
					2636, 2588, 0), 9, 3), // Barricade
			new GameObject(GameObjectDefinition.forId(14228), Location.create(
					2659, 2575, 0), 9, 2), // Barricade
			new GameObject(GameObjectDefinition.forId(14228), Location.create(
					2676, 2587, 0), 9, 1), // Barricade
			new GameObject(GameObjectDefinition.forId(14229), Location.create(
					2636, 2591, 0), 9, 0), // Barricade
			new GameObject(GameObjectDefinition.forId(14229), Location.create(
					2656, 2575, 0), 9, 3), // Barricade
			new GameObject(GameObjectDefinition.forId(14229), Location.create(
					2676, 2584, 0), 9, 2), // Barricade
			new GameObject(GameObjectDefinition.forId(14230), Location.create(
					2637, 2572, 0), 0, 2), // Barricade
			new GameObject(GameObjectDefinition.forId(14230), Location.create(
					2637, 2573, 0), 0, 2), // Barricade
			new GameObject(GameObjectDefinition.forId(14230), Location.create(
					2676, 2572, 0), 0, 0), // Barricade
			new GameObject(GameObjectDefinition.forId(14230), Location.create(
					2676, 2573, 0), 0, 0), // Barricade
			new GameObject(GameObjectDefinition.forId(14231), Location.create(
					2637, 2571, 0), 9, 2), // Barricade
			new GameObject(GameObjectDefinition.forId(14231), Location.create(
					2676, 2574, 0), 9, 0), // Barricade
			new GameObject(GameObjectDefinition.forId(14232), Location.create(
					2637, 2574, 0), 9, 1), // Barricade
			new GameObject(GameObjectDefinition.forId(14232), Location.create(
					2676, 2571, 0), 9, 3), // Barricade
	};

	/**
	 * All the current pest control objects (that are destroyable).
	 */
	private static GameObject[] currentPestControlGameObjects = new GameObject[DEFAULT_PEST_CONTROL_OBJECTS.length];

	/**
	 * This array holds all the pest control object ids.. {Perfect one, part
	 * destroyed, more destroyed, more destroyed, etc.}
	 */
	private static final int[][] OBJECT_IDS = {
			{ 14233 /* Normal door */, 14238, 14242, 14244, 14246 }, // Gate
			{ 14235, 14238, 14242, 14244, 14246 }, // Gate

			{ 14233, 14238, 14242, 14244, 14246 }, // Gate
			{ 14235, 14238, 14242, 14244, 14246 }, // Gate

			{ 14233, 14238, 14242, 14244, 14246 }, // Gate //FIXME Few bugs on
													// those..
			{ 14235, 14238, 14242, 14244, 14246 }, // Gate

			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade

			{ 14225 /* Good object. */, 14228 /* Partly destroyed */, 14232 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14225 /* Good object. */, 14228 /* Partly destroyed */, 14232 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14225 /* Good object. */, 14228 /* Partly destroyed */, 14232 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade

			{ 14226 /* Good object */, 14229 /* Partly destroyed */, 14231 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14226 /* Good object */, 14229 /* Partly destroyed */, 14231 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14226 /* Good object */, 14229 /* Partly destroyed */, 14231 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade

			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade

			{ 14225 /* Good object. */, 14228 /* Partly destroyed */, 14232 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14225 /* Good object. */, 14228 /* Partly destroyed */, 14232 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14225 /* Good object. */, 14228 /* Partly destroyed */, 14232 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade

			{ 14226 /* Good object */, 14229 /* Partly destroyed */, 14231 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14226 /* Good object */, 14229 /* Partly destroyed */, 14231 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14226 /* Good object */, 14229 /* Partly destroyed */, 14231 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade

			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14224 /* Good object. */, 14227 /* Partly destroyed */, 14230 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade

			{ 14226 /* Good object */, 14229 /* Partly destroyed */, 14231 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14226 /* Good object */, 14229 /* Partly destroyed */, 14231 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade

			{ 14225 /* Good object. */, 14228 /* Partly destroyed */, 14232 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade
			{ 14225 /* Good object. */, 14228 /* Partly destroyed */, 14232 /*
																		 * Fully
																		 * destroyed
																		 */}, // Barricade

	};

	/**
	 * Gets the non-destroyed object for a specific GameObject.
	 * 
	 * @param GameObject
	 *            the object to get a the non-destroyed ID for.
	 * @return -1 If nothing found (This should never happen) or a non-destroyed
	 *         object.
	 */
	public static int getPerfectObject(GameObject go) {
		for (int index = 0; index < currentPestControlGameObjects.length; index++) {
			if (currentPestControlGameObjects[index] == go) {
				return OBJECT_IDS[index][0];
			}
		}
		return -1;
	}

	/**
	 * Gets an object which is more destroyed than the object we're using as
	 * parameter (if there is any)
	 * 
	 * @param GameObject
	 *            the object to get a the more destroyed object for.
	 * @return -1 If nothing found (This should never happen) or a non-destroyed
	 *         object.
	 */
	public static int getMoreDestroyedObject(GameObject go) {
		for (int index = 0; index < currentPestControlGameObjects.length; index++) {
			if (currentPestControlGameObjects[index] == go) {
				for (int i = 0; i < OBJECT_IDS[index].length; i++) {
					if (OBJECT_IDS[index][i] == go.getDefinition().getId()
							&& i != OBJECT_IDS[index].length - 1) {
						return OBJECT_IDS[index][i + 1];
					}
				}
			}
		}
		return -1;
	}

	public static boolean gameObjectIsDestroyable(GameObject destroyable) {
		return getMoreDestroyedObject(destroyable) != -1;
	}

	public static void destroyObject(GameObject objective) {
		for (int index = 0; index < currentPestControlGameObjects.length; index++) {
			if (currentPestControlGameObjects[index].getLocation().equals(
					objective.getLocation())) {
				int newId = getMoreDestroyedObject(currentPestControlGameObjects[index]);
				if (newId != -1) {
					for (Player player : PLAYERS_IN_GAME) {
						// player.getActionSender().sendDestroyObject(objective.getType(),
						// objective.getRotation(), objective.getLocation());
						player.getActionSender().sendCreateObject(newId,
								objective.getType(), objective.getRotation(),
								objective.getLocation());
					}
					if (newId == 14246) { // Means a door have been destroyed..
						if (index <= 5) {// It really should be! :p
							int coolIndex = -1;
							if (index % 2 == 0) {
								coolIndex = index == 0 ? 0 : index / 2;
							} else {
								coolIndex = (index - 1) == 0 ? 0
										: (index - 1) / 2;
							}
							openStages[coolIndex] = true;
						}
					}
					currentPestControlGameObjects[index] = new GameObject(
							GameObjectDefinition.forId(newId),
							objective.getLocation(), objective.getType(),
							objective.getRotation());
				}
			}
		}

	}

	/* Western facing gate. */
	// new GameObject(GameObjectDefinition.forId(14233),
	// Location.create(2643,2593,0), 0, 0), // Gate
	// new GameObject(GameObjectDefinition.forId(14235),
	// Location.create(2643,2592,0), 0, 0), // Gate

	/* South facing gate.. */
	// new GameObject(GameObjectDefinition.forId(14233),
	// Location.create(2656,2585,0), 0, 3), // Gate
	// new GameObject(GameObjectDefinition.forId(14235),
	// Location.create(2657,2585,0), 0, 3), // Gate

	/* Eastern gates */
	// new GameObject(GameObjectDefinition.forId(14233),
	// Location.create(2670,2592,0), 0, 2), // Gate
	// new GameObject(GameObjectDefinition.forId(14235),
	// Location.create(2670,2593,0), 0, 2), // Gate

	private static boolean[] openStages = { false, false, false, };

	public static boolean closestDoorIsOpen(Location thisLoc) {
		int bestIndex = 0;
		Location closest = currentPestControlGameObjects[0].getLocation();
		for (int index = 1; index <= 5; index++) {
			if (closest.getDistance(thisLoc) > currentPestControlGameObjects[index]
					.getLocation().getDistance(thisLoc)) {
				closest = currentPestControlGameObjects[index].getLocation();
				bestIndex = index;
			}
		}
		int coolIndex = -1;
		if (bestIndex % 2 == 0) {
			coolIndex = bestIndex == 0 ? 0 : bestIndex / 2;
		} else {
			coolIndex = (bestIndex - 1) == 0 ? 0 : (bestIndex - 1) / 2;
		}
		return openStages[coolIndex];
	}

	private static final Location[] DOOR_WALKTO_LOCATIONS = {
			Location.create(2642, 2593, 0), Location.create(2642, 2592, 0),
			Location.create(2656, 2584, 0), Location.create(2657, 2584, 0),
			Location.create(2671, 2593, 0), Location.create(2671, 2592, 0) };

	public static Location getClosestDoorWalktoLocation(Location loc) {
		int bestIndex = 0;
		Location closest = currentPestControlGameObjects[0].getLocation();
		for (int index = 1; index <= 5; index++) {
			if (closest.getDistance(loc) > currentPestControlGameObjects[index]
					.getLocation().getDistance(loc)) {
				closest = currentPestControlGameObjects[index].getLocation();
				bestIndex = index;
			}
		}
		return DOOR_WALKTO_LOCATIONS[bestIndex];
	}

	public static boolean openDoorSet(Location location) {
		for (int index = 0; index <= 5; index++) {
			GameObject go = currentPestControlGameObjects[index];
			if (go.getLocation().equals(location)) {
				System.out.println("Index was: " + index);
				int coolIndex = -1;
				if (index % 2 == 0) {
					coolIndex = index == 0 ? 0 : index / 2;
				} else {
					coolIndex = (index - 1) == 0 ? 0 : (index - 1) / 2;
				}
				if (!openStages[coolIndex]) {
					if (index <= 1) { // Western gate..
						currentPestControlGameObjects[0] = new GameObject(
								GameObjectDefinition.forId(currentPestControlGameObjects[0]
										.getDefinition().getId()),
								Location.create(2643, 2593, 0), 0, 1);
						currentPestControlGameObjects[1] = new GameObject(
								GameObjectDefinition.forId(currentPestControlGameObjects[1]
										.getDefinition().getId()),
								Location.create(2643, 2592, 0), 0, 3);
						for (Player player : PLAYERS_IN_GAME) {
							player.getActionSender().sendCreateObject(
									currentPestControlGameObjects[0]
											.getDefinition().getId(),
									currentPestControlGameObjects[0].getType(),
									currentPestControlGameObjects[0]
											.getRotation(),
									currentPestControlGameObjects[0]
											.getLocation());
							player.getActionSender().sendCreateObject(
									currentPestControlGameObjects[1]
											.getDefinition().getId(),
									currentPestControlGameObjects[1].getType(),
									currentPestControlGameObjects[1]
											.getRotation(),
									currentPestControlGameObjects[1]
											.getLocation());
						}
					} else if (index <= 3) { // South facing gate
						System.out.println("Southern..");
						currentPestControlGameObjects[2] = new GameObject(
								GameObjectDefinition.forId(currentPestControlGameObjects[2]
										.getDefinition().getId()),
								Location.create(2656, 2585, 0), 0, 0);
						currentPestControlGameObjects[3] = new GameObject(
								GameObjectDefinition.forId(currentPestControlGameObjects[3]
										.getDefinition().getId()),
								Location.create(2657, 2585, 0), 0, 2);
						for (Player player : PLAYERS_IN_GAME) {
							player.getActionSender().sendCreateObject(
									currentPestControlGameObjects[2]
											.getDefinition().getId(),
									currentPestControlGameObjects[2].getType(),
									currentPestControlGameObjects[2]
											.getRotation(),
									currentPestControlGameObjects[2]
											.getLocation());
							player.getActionSender().sendCreateObject(
									currentPestControlGameObjects[3]
											.getDefinition().getId(),
									currentPestControlGameObjects[3].getType(),
									currentPestControlGameObjects[3]
											.getRotation(),
									currentPestControlGameObjects[3]
											.getLocation());
						}
					} else { // Eastern gate
						System.out.println("Eastern..");
						currentPestControlGameObjects[4] = new GameObject(
								GameObjectDefinition.forId(currentPestControlGameObjects[4]
										.getDefinition().getId()),
								Location.create(2670, 2592, 0), 0, 3);
						currentPestControlGameObjects[5] = new GameObject(
								GameObjectDefinition.forId(currentPestControlGameObjects[5]
										.getDefinition().getId()),
								Location.create(2670, 2593, 0), 0, 1);
						for (Player player : PLAYERS_IN_GAME) {
							player.getActionSender().sendCreateObject(
									currentPestControlGameObjects[4]
											.getDefinition().getId(),
									currentPestControlGameObjects[2].getType(),
									currentPestControlGameObjects[4]
											.getRotation(),
									currentPestControlGameObjects[4]
											.getLocation());
							player.getActionSender().sendCreateObject(
									currentPestControlGameObjects[5]
											.getDefinition().getId(),
									currentPestControlGameObjects[3].getType(),
									currentPestControlGameObjects[5]
											.getRotation(),
									currentPestControlGameObjects[5]
											.getLocation());
						}
					}
					openStages[coolIndex] = true;
					return true;
				}

			}
		}
		return false;
	}

	public static boolean closeDoorSet(Location location) {
		for (int index = 0; index <= 5; index++) {
			GameObject go = currentPestControlGameObjects[index];
			if (go.getLocation().equals(location)
					&& go.getDefinition().getId() != 14246) { // TODO: Other
																// totally
																// broken door
																// id.
				int coolIndex = -1;
				if (index % 2 == 0) {
					coolIndex = index == 0 ? 0 : index / 2;
				} else {
					coolIndex = (index - 1) == 0 ? 0 : (index - 1) / 2;
				}
				if (openStages[coolIndex]) {
					if (index <= 1) { // Western gate..
						currentPestControlGameObjects[0] = new GameObject(
								GameObjectDefinition.forId(currentPestControlGameObjects[0]
										.getDefinition().getId()),
								Location.create(2643, 2593, 0), 0, 0);
						currentPestControlGameObjects[1] = new GameObject(
								GameObjectDefinition.forId(currentPestControlGameObjects[1]
										.getDefinition().getId()),
								Location.create(2643, 2592, 0), 0, 0);
						for (Player player : PLAYERS_IN_GAME) {
							player.getActionSender().sendCreateObject(
									currentPestControlGameObjects[0]
											.getDefinition().getId(),
									currentPestControlGameObjects[0].getType(),
									currentPestControlGameObjects[0]
											.getRotation(),
									currentPestControlGameObjects[0]
											.getLocation());
							player.getActionSender().sendCreateObject(
									currentPestControlGameObjects[1]
											.getDefinition().getId(),
									currentPestControlGameObjects[1].getType(),
									currentPestControlGameObjects[1]
											.getRotation(),
									currentPestControlGameObjects[1]
											.getLocation());
						}
					} else if (index <= 3) { // South facing gate
						currentPestControlGameObjects[2] = new GameObject(
								GameObjectDefinition.forId(currentPestControlGameObjects[2]
										.getDefinition().getId()),
								Location.create(2656, 2585, 0), 0, 3);
						currentPestControlGameObjects[3] = new GameObject(
								GameObjectDefinition.forId(currentPestControlGameObjects[3]
										.getDefinition().getId()),
								Location.create(2657, 2585, 0), 0, 3);
						for (Player player : PLAYERS_IN_GAME) {
							player.getActionSender().sendCreateObject(
									currentPestControlGameObjects[2]
											.getDefinition().getId(),
									currentPestControlGameObjects[2].getType(),
									currentPestControlGameObjects[2]
											.getRotation(),
									currentPestControlGameObjects[2]
											.getLocation());
							player.getActionSender().sendCreateObject(
									currentPestControlGameObjects[3]
											.getDefinition().getId(),
									currentPestControlGameObjects[3].getType(),
									currentPestControlGameObjects[3]
											.getRotation(),
									currentPestControlGameObjects[3]
											.getLocation());
						}
					} else { // Eastern gate
						currentPestControlGameObjects[4] = new GameObject(
								GameObjectDefinition.forId(currentPestControlGameObjects[4]
										.getDefinition().getId()),
								Location.create(2670, 2592, 0), 0, 2);
						currentPestControlGameObjects[5] = new GameObject(
								GameObjectDefinition.forId(currentPestControlGameObjects[5]
										.getDefinition().getId()),
								Location.create(2670, 2593, 0), 0, 2);
						for (Player player : PLAYERS_IN_GAME) {
							player.getActionSender().sendCreateObject(
									currentPestControlGameObjects[4]
											.getDefinition().getId(),
									currentPestControlGameObjects[4].getType(),
									currentPestControlGameObjects[4]
											.getRotation(),
									currentPestControlGameObjects[4]
											.getLocation());
							player.getActionSender().sendCreateObject(
									currentPestControlGameObjects[5]
											.getDefinition().getId(),
									currentPestControlGameObjects[5].getType(),
									currentPestControlGameObjects[5]
											.getRotation(),
									currentPestControlGameObjects[5]
											.getLocation());
						}
					}
					openStages[coolIndex] = false;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * The count of death portals at the moment.
	 */
	private static int deathPortals;

	/**
	 * Simple array holding all the NPCs randomly spawned in the game.
	 * 
	 * @author GodSentDeath
	 */
	public static int PEST_CONTROL_NPCS[] = { 3732, // shifter
			3733, // shifter
			3734, // shifter
			3735, // shifter
			3736, // shifter
			3737, // shifter
			3738, // shifter
			3739, // shifter
			3740, // shifter
			3741, // shifter

			3752, // torcher
			3753, // torcher
			3754, // torcher
			3755, // torcher
			3756, // torcher
			3757, // torcher
			3758, // torcher
			3759, // torcher
			3760, // torcher
			3761, // torcher

			3747, // spinner
			3748, // spinner
			3749, // spinner
			3750, // spinner
			3751, // spinner

			3742, // ravager
			3743, // ravager
			3744, // ravager
			3745, // ravager
			3746, // ravager

			3762, // defiler
			3763, // defiler
			3764, // defiler
			3765, // defiler
			3766, // defiler
			3767, // defiler
			3768, // defiler
			3769, // defiler
			3770, // defiler
			3771, // defiler

			3772, // brawler
			3773, // brawler
			3774, // brawler
			3775, // brawler
			3776, // brawler
	};

	/**
	 * This is where all the NPC's can spawn.
	 */
	private static final Location[][] SPAWNING_POSITIONS = new Location[][] {
			{ Location.create(2630, 2594, 0), Location.create(2631, 2594, 0),
					Location.create(2631, 2593, 0),
					Location.create(2631, 2592, 0),
					Location.create(2631, 2591, 0),
					Location.create(2631, 2590, 0),
					Location.create(2630, 2590, 0), }, // Purple portal.
			{ Location.create(2644, 2571, 0), Location.create(2644, 2572, 0),
					Location.create(2645, 2572, 0),
					Location.create(2646, 2572, 0),
					Location.create(2647, 2572, 0),
					Location.create(2648, 2572, 0),
					Location.create(2648, 2571, 0), },
			{ Location.create(2668, 2572, 0), Location.create(2668, 2573, 0),
					Location.create(2669, 2573, 0),
					Location.create(2670, 2573, 0),
					Location.create(2671, 2573, 0),
					Location.create(2672, 2573, 0),
					Location.create(2672, 2572, 0), },
			{ Location.create(2680, 2587, 0), Location.create(2679, 2587, 0),
					Location.create(2679, 2588, 0),
					Location.create(2679, 2589, 0),
					Location.create(2679, 2590, 0),
					Location.create(2679, 2591, 0),
					Location.create(2680, 2591, 0), }, };

	private static final Location PEST_CONTROL_BOAT_PLANK = Location.create(
			2658, 2639, 0);
	private static final Location PEST_CONTROL_BOAT_LADDER = Location.create(
			2660, 2639, 0);

	/**
	 * Handles all the Pest-Control related object clicking.
	 * 
	 * @param player
	 *            The clicking player.
	 * @param loc
	 *            The location of the object.
	 * @param objectId
	 *            The object id clicked.
	 */
	public static boolean handleObjectClicking(Player player, Location loc,
			int objectId, int option) {
		switch (objectId) {
		/*
		 * The pest control boat ladder.
		 */
		case 14314:
			/*
			 * We make sure we the location is correct, to prevent fake objects.
			 */
			if (loc.equals(PEST_CONTROL_BOAT_LADDER)) {
				if (player.getLocation().inPestControlBoat()
						&& PLAYERS_IN_BOAT.contains(player)) {
					/*
					 * Means we're leaving the boat.
					 */
					PLAYERS_IN_BOAT.remove(player);
					player.setTeleportTarget(Location.create(2657, 2639, 0));
					player.getActionSender().sendWalkableInterface(-1);
				} else {
					if (player.getSkills().getCombatLevel() < 40) {
						player.getActionSender()
								.sendMessage(
										"You need a Combat of level 40 in order to enter the Pest Control game.");
					} else {
						if (PLAYERS_IN_BOAT.size() < 25) {
							/*
							 * We're entering the boat.
							 */
							player.setTeleportTarget(Location.create(2660,
									2639, 0));
							PLAYERS_IN_BOAT.add(player);
							player.getActionSender().sendMessage(
									"You enter the boat.");
						} else {
							player.getActionSender().sendMessage(
									"The boat is currently full.");
						}
					}
				}
			}
			break;
		/*
		 * The pest control boat plank.
		 */
		case 14315:
			/*
			 * We make sure we the location is correct, to prevent fake objects.
			 */
			if (loc.equals(PEST_CONTROL_BOAT_PLANK)) {
				if (player.getLocation().inPestControlBoat()
						&& PLAYERS_IN_BOAT.contains(player)) {
					/*
					 * Means we're leaving the boat.
					 */
					PLAYERS_IN_BOAT.remove(player);
					player.setTeleportTarget(Location.create(2657, 2639, 0));
					player.getActionSender().sendWalkableInterface(-1);
				} else {
					if (PLAYERS_IN_BOAT.size() < 25) {
						/*
						 * We're entering the boat.
						 */
						player.setTeleportTarget(Location.create(2660, 2639, 0));
						PLAYERS_IN_BOAT.add(player);
						player.getActionSender().sendMessage(
								"You enter the boat.");
					} else {
						player.getActionSender().sendMessage(
								"The boat is currently full.");
					}

				}
			}
			break;
		}
		if (option == 1) {
			if (openDoorSet(loc)) {
				return true;
			}
			if (closeDoorSet(loc)) {
				return true;
			}
		} else { // Its two..

		}

		return false;
	}

	/**
	 * Checks if a given player is in the Pest Control boat on login, and
	 * handles the actions needed if so.
	 */
	public static boolean checkBoatLogin(Player player) {
		if (player.getLocation().inPestControlBoat()) {
			if (PLAYERS_IN_BOAT.size() < 25) {
				PLAYERS_IN_BOAT.add(player);
			} else {
				player.setTeleportTarget(Location.create(2658, 2649, 0));
			}
			return true;
		}
		return false;
	}

	/**
	 * This method is called once a player logs out, and it checks if his in the
	 * boat, and removes him from it if so.
	 * 
	 * @param player
	 *            The player logging out.
	 */
	public static boolean destroy(Player player) {
		if (player.getLocation().inPestControlBoat()) {
			return PLAYERS_IN_BOAT.remove(player);
		} else {
			return false;
		}
	}

	/**
	 * Gets the list of players for use in the PestControlEvent.
	 * 
	 * @return The list of all players in the boat.
	 */
	public static List<Player> getBoatList() {
		return PLAYERS_IN_BOAT;
	}

	/**
	 * Gets the list of players for use in the PestControlEvent.
	 * 
	 * @return The list of all players in the boat.
	 */
	public static List<Player> getGameList() {
		return PLAYERS_IN_GAME;
	}

	/**
	 * Used to remove all players from the boat list. (Just after we teleported
	 * them into the game)
	 */
	private static void emptyBoatList() {
		PLAYERS_IN_BOAT.clear();
	}

	/**
	 * Sets the "game going on" flag.
	 * 
	 * @param gameGoingOn
	 *            The new value.
	 */
	public static void setGameGoingOn(boolean gameGoingOn) {
		PestControl.gameGoingOn = gameGoingOn;
	}

	/**
	 * Defines if the Pest Control game is currently running.
	 * 
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public static boolean isGameGoingOn() {
		return gameGoingOn;
	}

	/**
	 * Checks if a specific coordinate is walkable, based on our Pest Control
	 * mobs.
	 */
	public static boolean canWalkForMobs(Player player, int x0, int y0) {
		System.out.println("Checking coordinates: " + x0 + " " + y0);
		/**
		 * This is called on every walking packet, so we should be careful about
		 * lag.
		 */
		if (PLAYERS_IN_GAME.contains(player)) {
			for (PestControlMob npc : MONSTERS) {
				if (!npc.isWalkable()) {
					for (int x = npc.getLocation().getX(); x < npc
							.getLocation().getX()
							+ npc.getDefinition().getSize(); x++) {
						for (int y = npc.getLocation().getY(); y < npc
								.getLocation().getY()
								+ npc.getDefinition().getSize(); y++) {
							System.out.println("Looping??");
							if (x == x0 && y == y0) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	public static int getGameTime() {
		return gameTime;
	}

	public static int decreaseGameTime() {
		return gameTime--;
	}

	/*
	 * We get the dialogue loader from the hashmap (only needs to be done once.
	 * ;))
	 */
	private static DialogueLoader voidKnightSquire = DialogueLoader.forId(3781);

	private static final Location ARRIVAL_BOAT_MIN_LOCATION = Location.create(
			2656, 2609, 0);
	private static final Location ARRIVAL_BOAT_MAX_LOCATION = Location.create(
			2659, 2614, 0);

	public static void startGame() {
		/*
		 * We setup the new pest control in game objects with the real ones (in
		 * case they changed in the last game)
		 */
		setupGameObjects();

		/*
		 * We start the game, and set the time.
		 */
		gameGoingOn = true;
		gameTime = 1200; // Twenty minutes.
		/*
		 * We set the players in game to the players in the boat, obviously. And
		 * we teleport them to the boat in game right away.
		 */
		for (Player player : PLAYERS_IN_BOAT) {
			PLAYERS_IN_GAME.add(player);
			player.setTeleportTarget(Location.create(
					ARRIVAL_BOAT_MIN_LOCATION.getX()
							+ r.nextInt(ARRIVAL_BOAT_MAX_LOCATION.getX()
									- ARRIVAL_BOAT_MIN_LOCATION.getX()),
					ARRIVAL_BOAT_MIN_LOCATION.getY()
							+ r.nextInt(ARRIVAL_BOAT_MAX_LOCATION.getY()
									- ARRIVAL_BOAT_MIN_LOCATION.getY()), 0));
			DialogueLoader.getNextDialogue(player, voidKnightSquire, 0);
			for (GameObject o : currentPestControlGameObjects) {
				player.getActionSender().sendCreateObject(
						o.getDefinition().getId(), o.getType(),
						o.getRotation(), o.getLocation());
			}
		}
		/*
		 * We empty the boat list.
		 */
		emptyBoatList();

		/*
		 * We setup all the portals.
		 */

		/*
		 * In case the portals died, we set them to visible.
		 */
		PURPLE_PORTAL.setInvisible(false);
		BLUE_PORTAL.setInvisible(false);
		YELLOW_PORTAL.setInvisible(false);
		RED_PORTAL.setInvisible(false);

		/*
		 * We make sure all of them have 250 Hitpoints.
		 */
		PURPLE_PORTAL.setHitpoints(250);
		BLUE_PORTAL.setHitpoints(250);
		YELLOW_PORTAL.setHitpoints(250);
		RED_PORTAL.setHitpoints(250);

		/*
		 * And that they aren't dead.
		 */
		PURPLE_PORTAL.setDead(false);
		BLUE_PORTAL.setDead(false);
		YELLOW_PORTAL.setDead(false);
		RED_PORTAL.setDead(false);

		/*
		 * We setup the void knight using the same procedure.
		 */
		VOID_KNIGHT.setInvisible(false);
		VOID_KNIGHT.setHitpoints(200);
		VOID_KNIGHT.setDead(false);

		/*
		 * We spawn an NPC at each portal to start off with, and then one for
		 * each portal, where the minutes are converted to seconds.
		 */
		World.getWorld().submit(new Event(5000) { // 5 seconds delay for the
													// first NPC's.

					@Override
					public void execute() {
						if (!gameGoingOn) {
							this.stop();
						}
						// Purple, blue, yellow, red
						if (PURPLE_PORTAL.getHitpoints() > 0) {
							PestControlMob mob = (PestControlMob) NPC.create(
									NPCDefinition
											.forId(PEST_CONTROL_NPCS[r
													.nextInt(PEST_CONTROL_NPCS.length)]),
									SPAWNING_POSITIONS[0][r
											.nextInt(SPAWNING_POSITIONS[0].length)],
									null, null);
							World.getWorld().getNPCs().add(mob);
							MONSTERS.add(mob);
						}
						if (BLUE_PORTAL.getHitpoints() > 0) {
							PestControlMob mob = (PestControlMob) NPC.create(
									NPCDefinition
											.forId(PEST_CONTROL_NPCS[r
													.nextInt(PEST_CONTROL_NPCS.length)]),
									SPAWNING_POSITIONS[1][r
											.nextInt(SPAWNING_POSITIONS[1].length)],
									null, null);
							World.getWorld().getNPCs().add(mob);
							MONSTERS.add(mob);
						}
						if (YELLOW_PORTAL.getHitpoints() > 0) {
							PestControlMob mob = (PestControlMob) NPC.create(
									NPCDefinition
											.forId(PEST_CONTROL_NPCS[r
													.nextInt(PEST_CONTROL_NPCS.length)]),
									SPAWNING_POSITIONS[2][r
											.nextInt(SPAWNING_POSITIONS[2].length)],
									null, null);
							World.getWorld().getNPCs().add(mob);
							MONSTERS.add(mob);
						}
						if (RED_PORTAL.getHitpoints() > 0) {
							PestControlMob mob = (PestControlMob) NPC.create(
									NPCDefinition
											.forId(PEST_CONTROL_NPCS[r
													.nextInt(PEST_CONTROL_NPCS.length)]),
									SPAWNING_POSITIONS[3][r
											.nextInt(SPAWNING_POSITIONS[3].length)],
									null, null);
							World.getWorld().getNPCs().add(mob);
							MONSTERS.add(mob);
						}
						this.setDelay(gameTime / 60 * 1000);
					}

				});
	}

	public static void portalDied() {
		if (++deathPortals == 4) {
			finishGame(true);
		}
	}

	public static void removeNPC(PestControlMob mob) {
		World.getWorld().unregister(mob);
		// mob.destroy();
		// World.getWorld().getNPCs().remove(mob);
		MONSTERS.remove(mob);
	}

	public static void finishGame(boolean successfullGame) {
		for (Player player : PLAYERS_IN_GAME) {
			DeathEvent.resetPlayer(player);
			player.setTeleportTarget(Location.create(2657, 2639, 0));
			player.getActionSender().sendWalkableInterface(-1);
			if (successfullGame) {
				if (player.getPestControlDamage() >= 50) { // TODO: Interfaces.
					player.increasePestControlPoints();
					Inventory.addInventoryItems(player, new Item[] { new Item(
							995, player.getSkills().getCombatLevel() * 10) });
					DialogueLoader.getNextDialogue(player, voidKnightSquire, 8);
				} else {
					DialogueLoader.getNextDialogue(player, voidKnightSquire, 9);
				}
			} else {
				DialogueLoader.getNextDialogue(player, voidKnightSquire, 7);
			}
			player.resetPestControlDamage();
		}
		deathPortals = 0;
		gameGoingOn = false;
		PLAYERS_IN_GAME.clear();
		for (PestControlMob mob : MONSTERS) {
			World.getWorld().unregister(mob);
		}
		MONSTERS.clear();
		setupGameObjects();
		for (int index = 0; index < openStages.length; index++) {
			openStages[index] = false;
		}
	}

	/**
	 * Called when a specific player dies.
	 * 
	 * @param player
	 *            The player who just died..
	 * @return <code>true</code>if, <code>false</code> if not.
	 */
	public static boolean handleDeath(final Player player) {
		if (PLAYERS_IN_GAME.contains(player)
				&& player.getLocation().inPestControlGame()) {
			World.getWorld().submit(new Event(5000) {

				@Override
				public void execute() {
					if (PestControl.gameGoingOn) {
						player.setTeleportTarget(Location.create(
								ARRIVAL_BOAT_MIN_LOCATION.getX()
										+ r.nextInt(ARRIVAL_BOAT_MAX_LOCATION
												.getX()
												- ARRIVAL_BOAT_MIN_LOCATION
														.getX()),
								ARRIVAL_BOAT_MIN_LOCATION.getY()
										+ r.nextInt(ARRIVAL_BOAT_MAX_LOCATION
												.getY()
												- ARRIVAL_BOAT_MIN_LOCATION
														.getY()), 0));
						player.getActionSender().sendMessage(
								"Oh dear, you are dead!");
						DeathEvent.resetPlayer(player);
					}
					this.stop();
				}

			});
			return true;
		}
		return false;
	}

	/**
	 * Called when a certain player decides to leave the game.
	 * 
	 * @param player
	 *            The player leaving.
	 */
	public static void exit(Player player) {
		player.getActionSender().sendCloseInterface();
		player.getActionSender().sendWalkableInterface(-1);
		player.getActionSender().sendMessage("You decided to leave the game.");
		player.setTeleportTarget(Location.create(2657, 2639, 0));
		PLAYERS_IN_GAME.remove(player);
	}

	/**
	 * Called once every second to make sure the NPC's are behaving as they
	 * should.
	 */
	public static void behave() {
		for (PestControlMob monster : MONSTERS) {
			monster.behave();
		}
	}

	private static void setupGameObjects() {
		for (int index = 0; index < currentPestControlGameObjects.length; index++) {
			currentPestControlGameObjects[index] = DEFAULT_PEST_CONTROL_OBJECTS[index];
		}
	}

	public static PestControlPortal getPurplePortal() {
		return PURPLE_PORTAL;
	}

	public static PestControlPortal getBluePortal() {
		return BLUE_PORTAL;
	}

	public static PestControlPortal getYellowPortal() {
		return YELLOW_PORTAL;
	}

	public static PestControlPortal getRedPortal() {
		return RED_PORTAL;
	}

	public static VoidKnight getVoidKnight() {
		return VOID_KNIGHT;
	}

	public static GameObject[] getCurrentPestControlObjects() {
		return currentPestControlGameObjects;
	}

	public static void showBuyingInterface(Player player) {
		player.removeTemporaryAttribute("PCReward");
		player.removeTemporaryAttribute("PCAmount");
		for (int index = 0; index < STRING_CHILD_IDS.length; index++) {
			if (index < SKILLS.length) {
				int skill = SKILLS[index];
				int level = player.getSkills().getLevel(skill);
				double exp = getVoidKnightRewardExperience(skill, level);
				player.getActionSender().sendString(
						"<col=FFFF00>" + Skills.SKILL_NAME[skill] + " - "
								+ (int) exp + " xp", 267,
						STRING_CHILD_IDS[index]);
			} else {
				int index2 = index - SKILLS.length;
				int pointReq = OTHER_NEEDED_POINTS[index2];
				if (pointReq > player.getPestControlPoints()) {
					player.getActionSender().sendString(
							"<col=FF0000>" + OTHER_STRINGS[index2], 267,
							STRING_CHILD_IDS[index]);
				} else {
					player.getActionSender().sendString(
							"<col=FFFF00>" + OTHER_STRINGS[index2], 267,
							STRING_CHILD_IDS[index]);
				}
			}
		}
		player.getActionSender().sendString(
				"Points: " + player.getPestControlPoints(), 267, 126);
		player.getActionSender().sendInterface(267);
	}

	public static final int[][] BUTTONS = { { 103, 139, 155 }, // Attack
			{ 104, 140, 156 }, // Strength
			{ 105, 141, 157 }, // Defence
			{ 106, 142, 158 }, // Ranged
			{ 107, 143, 159 }, // Magic
			{ 108, 144, 160 }, // Hitpoints
			{ 109, 145, 161 }, // Prayer
			{ 146 }, // Mace
			{ 147 }, // Top
			{ 148 }, // Robe
			{ 149 }, // Gloves
			{ 150 }, // Herb
			{ 154 }, // Seed
			{ 151 }, // Mineral
	};

	public static final int[] SKILLS = { Skills.ATTACK, Skills.STRENGTH,
			Skills.DEFENCE, Skills.RANGE, Skills.MAGIC, Skills.HITPOINTS,
			Skills.PRAYER, };

	public static final int[] STRING_CHILD_IDS = { 110, // Attack
			111, // Strength
			112, // Defence
			113, // Ranged
			114, // Magic
			115, // Hitpoints
			116, // Prayer
			133, // Void Knight Mace, Enabled:
			134, // Void Knight Top, Enabled:
			135, // Void Knight Robes, Enabled:
			136, // Void Knight Gloves, Enabled:
			137, // Herb Pack, Enabled:
			138, // Seed Pack, Enabled:
			153, // Mineral Pack, Enabled:
	};

	public static final String[] OTHER_STRINGS = { "Void Knight Mace",
			"Void Knight Top", "Void Knight Robes", "Void Knight Gloves",
			"Herb Pack", "Seed Pack", "Mineral Pack", };

	private static final Item[][] REWARDS = {
			{ new Item(8841) }, // Mace
			{ new Item(8839) }, // Top
			{ new Item(8840) }, // Robe
			{ new Item(8842) }, // Gloves
			{ new Item(199), new Item(201), new Item(203), new Item(205),
					new Item(207), new Item(209), new Item(211), new Item(213),
					new Item(215), new Item(217), new Item(219),
					new Item(2485/* Unsure if id is corrent. :( */),
					new Item(3049/* Unsure if id is corrent. :( */) }, // Herbs
			{ new Item(5096), /* Marigold seed */new Item(5097), /* Rosemary seed */
					new Item(5098), /* Nasturtium seed */new Item(5099), /*
																		 * Woad
																		 * seed
																		 */
					new Item(5100), /* Limpwurt seed */new Item(5101), /*
																		 * Redberry
																		 * seed
																		 */
					new Item(5102), /* Cadavaberry seed */new Item(5103), /*
																		 * Dwellberry
																		 * seed
																		 */
					new Item(5104), /* Jangerberry seed */new Item(5105), /*
																		 * Whiteberry
																		 * seed
																		 */
					new Item(5106), /* Poison ivy seed */}, // FIXME 100%
															// randomly picked
			{ new Item(454, 25), new Item(441, 18) }, // Minerals..
	};

	public static final int[] OTHER_NEEDED_POINTS = { 250, 250, 250, 150, 10,
			5, 5, };

	private static double getVoidKnightRewardExperience(int skill, int level) {
		double exp;
		if (skill == Skills.PRAYER) {
			exp = (level * level) / 12;
		} else {
			exp = (level * level) / 6;
		}
		return exp;
	}

	public static boolean handleVoidKnightRewardInterface(Player player,
			int button) {
		if (button == 122) { // Claim
			Object r = player.getTemporaryAttribute("PCReward");
			Object p = player.getTemporaryAttribute("PCAmount");
			if (r != null && p != null) {
				int points = (Integer) p;
				player.decreasePestControlPoints(points);
				if (r instanceof Integer) {
					int reward = (Integer) r;
					for (int skill : SKILLS) {
						if (skill == reward) { // Means its a skill :s
							int level = player.getSkills().getLevel(skill);
							double exp = getVoidKnightRewardExperience(skill,
									level) * points;
							player.getSkills().addExperience(skill, exp);
							player.getActionSender()
									.sendString(
											"The Void Knight has granted you "
													+ (int) exp + " "
													+ Skills.SKILL_NAME[skill]
													+ " xp.", 211, 0);
							player.getActionSender().sendString(
									"<col=FF0000>Remaining Void Knight Commendation Points: "
											+ player.getPestControlPoints(),
									211, 1);
							player.getActionSender().sendChatboxInterface(211);
						}
					}
				} else {
					Item[] reward = (Item[]) r;
					Inventory.addInventoryItems(player, reward);
					player.getActionSender().sendString(
							"The Void Knight handed over your items.", 211, 0);
					player.getActionSender().sendString(
							"<col=FF0000>Remaining Void Knight Commendation Points: "
									+ player.getPestControlPoints(), 211, 1);
					player.getActionSender().sendChatboxInterface(211);
				}
			} else {
				player.getActionSender().sendMessage(
						"You need to pick a reward first.");
			}
			player.removeTemporaryAttribute("PCReward");
			player.removeTemporaryAttribute("PCAmount");
			return true;
		}
		for (int index = 0; index < BUTTONS.length; index++) {
			int[] buttons = BUTTONS[index];
			for (int buttonIndex = 0; buttonIndex < buttons.length; buttonIndex++) {
				int b = buttons[buttonIndex];
				if (b == button) {
					/*
					 * Skill stuff..
					 */
					if (index < SKILLS.length) {
						if (buttonIndex == 0) {
							// Choose amount of points to spend by hand??
						} else {
							int skill = SKILLS[index];
							int multiplyer = buttonIndex == 1 ? 1 : 100;
							if (multiplyer > player.getPestControlPoints()) {
								player.getActionSender()
										.sendMessage(
												"You don't have enough Void Knight Commendation Points.");
								return true;
							}
							player.getActionSender()
									.sendString(
											"<col=FFFFFF>"
													+ Skills.SKILL_NAME[skill]
													+ " - "
													+ (int) getVoidKnightRewardExperience(
															skill,
															player.getSkills()
																	.getLevel(
																			skill))
													+ " xp", 267,
											STRING_CHILD_IDS[index]);
							reset(player);
							player.setTemporaryAttribute("PCReward", skill);
							player.setTemporaryAttribute("PCAmount", multiplyer);
						}
						/*
						 * Other stuff..
						 */
					} else {
						int rIndex = index - SKILLS.length;
						Item[] reward = REWARDS[rIndex];
						int amount = 1;
						int points = OTHER_NEEDED_POINTS[rIndex];
						if (points > player.getPestControlPoints()) {
							player.getActionSender()
									.sendMessage(
											"You don't have enough Void Knight Commendation Points.");
							return true;
						}
						player.getActionSender().sendString(
								"<col=FFFFFF>" + OTHER_STRINGS[rIndex], 267,
								STRING_CHILD_IDS[index]);
						reset(player);
						player.setTemporaryAttribute("PCReward", reward);
						player.setTemporaryAttribute("PCAmount", amount);
					}
					return true;
				}
			}
		}
		return false;
	}

	public static void reset(Player player) {
		/*
		 * Reset the previously chosen thing.
		 */
		Object r = player.getTemporaryAttribute("PCReward");
		if (r != null) {
			if (r instanceof Integer) {
				int reward = (Integer) r;
				for (int i = 0; i < SKILLS.length; i++) {
					if (SKILLS[i] == reward) {
						player.getActionSender().sendString(
								"<col=FFFF00>"
										+ Skills.SKILL_NAME[reward]
										+ " - "
										+ (int) getVoidKnightRewardExperience(
												reward, player.getSkills()
														.getLevel(reward))
										+ " xp", 267, STRING_CHILD_IDS[i]);
					}
				}
			} else {
				Item[] reward = (Item[]) r;
				for (int i = 0; i < REWARDS.length; i++) {
					if (REWARDS[i] == reward) {
						int child = STRING_CHILD_IDS[i + SKILLS.length];
						player.getActionSender().sendString(
								"<col=FFFF00>" + OTHER_STRINGS[i], 267, child);
					}
				}
			}

		}
	}

	static {
		PURPLE_PORTAL.setWalkingType(0);
		World.getWorld().getNPCs().add(PURPLE_PORTAL);

		BLUE_PORTAL.setWalkingType(0);
		World.getWorld().getNPCs().add(BLUE_PORTAL);

		YELLOW_PORTAL.setWalkingType(0);
		World.getWorld().getNPCs().add(YELLOW_PORTAL);

		RED_PORTAL.setWalkingType(0);
		World.getWorld().getNPCs().add(RED_PORTAL);

		VOID_KNIGHT.setWalkingType(0);
		World.getWorld().getNPCs().add(VOID_KNIGHT);

		setupGameObjects();
	}

}
