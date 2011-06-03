package org.hyperion.rs2.content.skills;

public class SlayerTask {

	/**
	 * Array of npc id's we can attack to decrease the count variable.
	 */
	private final int[] tasks;

	/**
	 * The count of how many npc's we need before we can get a new task.
	 */
	private int count;

	/**
	 * The name of the current/previous assignment. Eg Birds/Bears.
	 */
	private final String name;

	/**
	 * The task index.
	 */
	private final int taskIndex;

	/**
	 * The players current slayer master.
	 */
	private final int slayerMaster;

	/**
	 * Gets the array of npcs we're slaying.
	 * 
	 * @return the tasks array.
	 */
	public int[] getSlayerNpcs() {
		return tasks;
	}

	/**
	 * Gets the amount of needed npc's to kill.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Indicates whenever or not we have a task.
	 */
	public boolean hasTask() {
		return count > 0;
	}

	/**
	 * Gets the name of the target we're supposed to kill.
	 */
	public String getName() {
		return name;
	}

	public SlayerTask(String name, int count, int taskIndex, int slayerMaster) {
		this.name = name;
		this.count = count;
		int[] tasks = null;
		switch (slayerMaster) {
		case Slayer.TURAEL:
			tasks = Slayer.TURAEL_MONSTERS[taskIndex];
			break;
		case Slayer.MAZCHNA:
			tasks = Slayer.MAZCHNA_MONSTERS[taskIndex];
			break;
		case Slayer.VANNAKA:
			tasks = Slayer.VANNAKA_MONSTERS[taskIndex];
			break;
		case Slayer.CHAELDAR:
			tasks = Slayer.CHAELDAR_MONSTERS[taskIndex];
			break;
		case Slayer.DURADEL:
			tasks = Slayer.DURADEL_MONSTERS[taskIndex];
			break;
		}
		this.tasks = tasks;
		this.taskIndex = taskIndex;
		this.slayerMaster = slayerMaster;
	}

	public SlayerTask(int count, int taskIndex, int slayerMaster) {
		this.count = count;
		int[] tasks = null;
		String name = null;
		switch (slayerMaster) {
		case Slayer.TURAEL:
			tasks = Slayer.TURAEL_MONSTERS[taskIndex];
			name = Slayer.TURAEL_NAMES[taskIndex];
			break;
		case Slayer.MAZCHNA:
			tasks = Slayer.MAZCHNA_MONSTERS[taskIndex];
			name = Slayer.MAZCHNA_NAMES[taskIndex];
			break;
		case Slayer.VANNAKA:
			tasks = Slayer.VANNAKA_MONSTERS[taskIndex];
			name = Slayer.VANNAKA_NAMES[taskIndex];
			break;
		case Slayer.CHAELDAR:
			tasks = Slayer.CHAELDAR_MONSTERS[taskIndex];
			name = Slayer.CHAELDAR_NAMES[taskIndex];
			break;
		case Slayer.DURADEL:
			tasks = Slayer.DURADEL_MONSTERS[taskIndex];
			name = Slayer.DURADEL_NAMES[taskIndex];
			break;
		}
		this.tasks = tasks;
		this.name = name;
		this.taskIndex = taskIndex;
		this.slayerMaster = slayerMaster;
	}

	/**
	 * Gets the players current slayer master.
	 */
	public int getSlayerMaster() {
		return slayerMaster;
	}

	/**
	 * Gets the SlayerTask index.
	 * 
	 * @return The slayers task index.
	 */
	public int getTaskIndex() {
		return taskIndex;
	}

	/**
	 * Decrements the slayer task count.
	 */
	public void decrementCount() {
		count--;
	}

}
