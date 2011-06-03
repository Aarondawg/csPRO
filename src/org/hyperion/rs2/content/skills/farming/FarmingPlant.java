package org.hyperion.rs2.content.skills.farming;

import java.util.Random;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;

/**
 * Represents a simple farming path ingame.
 * 
 * @author Brown
 */

public class FarmingPlant implements FarmingObject {

	private static final int MAX_GROWING_STAGES = 5;// Completely unsure of this
													// one.
	private static final Random RANDOM = new Random();

	/**
	 * //TODO: WARNING!!! REDO THE CURRENTOBJECTID READING THING FROM THE
	 * ARRAYS!!!
	 */

	/**
	 * Constructor to set up a new farming path.
	 */
	public FarmingPlant(Player player, Location loc, int pIndex, int sIndex,
			int time, boolean supCompost) {
		health = HealthStage.NORMAL;
		minutesRemaining = time;
		superCompost = supCompost;
		nextStageTime = time / MAX_GROWING_STAGES;
		currentStage = 0;
		patchIndex = pIndex;
		location = loc;
		diseasingTick = 0;
		water = false;
		seedIndex = sIndex;
		spawnMe(player);
	}

	/**
	 * Constructor to set up a farming plant when we load up the player files.
	 */
	public FarmingPlant(Location create, byte aIndex, byte sIndex,
			byte healthState, byte time, boolean superCompost2,
			byte currentStage2, byte diseasingTick2, boolean w) {

		location = create;
		patchIndex = aIndex;
		switch (healthState) {
		case 0:
			health = HealthStage.NORMAL;
			break;
		case 1:
			health = HealthStage.DISEASED;
			break;
		case 2:
			health = HealthStage.DEAD;
			break;
		}
		minutesRemaining = time;
		superCompost = superCompost2;
		currentStage = currentStage2;
		diseasingTick = diseasingTick2;
		nextStageTime = time / MAX_GROWING_STAGES;
		water = w;
		seedIndex = sIndex;
	}

	@Override
	public void tick(Player player) {
		if (minutesRemaining == 0) {
			if (currentStage != MAX_GROWING_STAGES) {
				currentStage = MAX_GROWING_STAGES;
				spawnMe(player);
			}
			// Check for final stage, eventually set to final stage.
			return;// Nothing else to do here really.
		} else {
			if (currentStage == MAX_GROWING_STAGES) {
				System.out
						.println("This should REALLY not happend, FUCK FUCK FUCK FUCK, (FARMING)");
				minutesRemaining = 0;
				return;
			}
			if (health == HealthStage.NORMAL) {
				minutesRemaining--;
				/*
				 * Makes a ~1% chance of diseasing if we're using super compost,
				 * 1.7% if we're using regular compost. As the average plant is
				 * growing for like 45 minutes, and the global event is running
				 * Every 2.5 minutes, we have to multiply the chance with ~18.
				 * Which makes the chances of diseasing ~18% and ~30.6%.
				 */
				if (!water && currentStage != 0) { // First stage cant disease.
													// (I hope lol).
					if (RANDOM.nextInt(superCompost ? 100 : 60) == 0) {
						health = HealthStage.DISEASED;
						diseasingTick = 5; // Gives you 5 * 2.5 minutes = 12.5
											// minutes to fix your plant.
						return;
					}
				}
				if (minutesRemaining >= nextStageTime
						* (MAX_GROWING_STAGES - currentStage)) {
					water = false;
					currentStage++;
					spawnMe(player);
				}

			} else if (health == HealthStage.DISEASED) {
				if (diseasingTick == 0) {
					health = HealthStage.DEAD;
					return;
				}
				diseasingTick--;
			}
		}

	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public int getArrayIndex() {
		return patchIndex;
	}

	public int getSeedIndex() {
		return seedIndex;
	}

	/**
	 * Gets the total time needed to grow this plant.
	 * 
	 * @return The total time needed to grow this plant.
	 */
	public int getTime() {
		return minutesRemaining;
	}

	/**
	 * Gets the health stage of this plant, as an int. Usage: Saving.
	 */
	public int getHealthState() {
		return health.toInteger();
	}

	/**
	 * Determines whenever or not this plant is using super compost.
	 * 
	 * @return True if using super compost, false if not.
	 */
	public boolean hasSuperCompostSoil() {
		return superCompost;
	}

	/**
	 * Gets the current stage of this plant.
	 * 
	 * @return The current stage.
	 */
	public int getCurrentStage() {
		return currentStage;
	}

	/**
	 * Gets the diseasing tick.
	 * 
	 * @return The diseasing tick.
	 */
	public int getDiseasingTick() {
		return diseasingTick;
	}

	/**
	 * Cures this specific plant.
	 * 
	 * @param player
	 *            The player trying to cure this plant.
	 */
	public void cure(Player player) {
		switch (health) {
		case NORMAL:
			player.getActionSender().sendMessage(
					"You're plant is perfectly healthy.");
			break;
		case DISEASED:
			health = HealthStage.NORMAL;
			player.getActionSender().sendMessage(
					"You cured your plant in time.");
			diseasingTick = 0;
			break;
		case DEAD:
			player.getActionSender().sendMessage(
					"You were too late, your plant is already dead.");
			break;
		}
	}

	private final int seedIndex;

	private boolean water;

	private int diseasingTick;

	private final double nextStageTime;

	private int minutesRemaining;

	private final boolean superCompost;

	private final int patchIndex;

	private final Location location;

	private int currentStage;

	private HealthStage health;

	private static enum HealthStage {
		NORMAL(0), DISEASED(1), DEAD(2);

		private HealthStage(int id) {
			this.id = id;
		}

		public int toInteger() {
			return id;
		}

		private int id;
	}

	private static final int[][][][] OBJECT_SPAWN_LOCATION_OFFSETS = { { {// Allotments
																			// {x,
																			// y}
			{}, {}, {}, {}, {}, {}, {}, // Potatoes.
			{}, {}, {}, {}, {}, {}, {}, // Onions.
			{}, {}, {}, {}, {}, {}, {}, // Cabbages
			{}, {}, {}, {}, {}, {}, {}, // Tomatoes.
			{}, {}, {}, {}, {}, {}, {}, // Sweetcorn
			{}, {}, {}, {}, {}, {}, {}, // Strawberries.
			{}, {}, {}, {}, {}, {}, {}, // Watermelons
	}, },

	};

	@Override
	public boolean spawnMe(Player player) {
		int[][] xAndYs = OBJECT_SPAWN_LOCATION_OFFSETS[patchIndex][seedIndex];
		int health = 0;
		int stage = currentStage;
		switch (this.health) {
		case NORMAL:
			if (water) {
				health = Farming.GOOD_SHAPE_AND_WATER;
			} else {
				health = Farming.GOOD_SHAPE;
			}
			break;
		case DISEASED:
			health = Farming.DISEASED_SHAPE;
			if (stage == MAX_GROWING_STAGES) {
				System.out.println("This is really bad. #1");
				return false;
			}
			stage = currentStage - 1;// Because we never use seed and the
										// finished plant.
			break;
		case DEAD:
			health = Farming.DEAD_SHAPE;
			if (stage == MAX_GROWING_STAGES) {
				System.out.println("This is really bad. #2");
				return false;
			}
			stage = currentStage - 1;// Because we never use seed and the
										// finished plant.
			break;
		}
		int currentObjectId = Farming.PLANT_OBJECTS[patchIndex][health][stage];
		if (player.getLocation().withinRange(location, 50)) {// TODO: Redo that
																// part thing.
			for (int[] xAndY : xAndYs) {
				player.getActionSender().sendCreateObject(
						currentObjectId,
						10,
						RANDOM.nextInt(4),
						Location.create(location.getX() + xAndY[0],
								location.getY() + xAndY[1], location.getZ()));
			}
			return true;
		}
		return false;
	}

}
