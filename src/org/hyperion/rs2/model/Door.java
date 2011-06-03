package org.hyperion.rs2.model;

/**
 * Represents a door. Idea: Java´
 * 
 * @author Brown.
 */

public class Door {

	/**
	 * The door constructor.
	 */
	/*
	 * public Door(Location open, int openFace, Location closed, int closedFace,
	 * int openId, int closedId, boolean isOpen) { this.open = open;
	 * this.openFace = openFace; this.closed = closed; this.closedFace =
	 * closedFace; this.openId = openId; this.closedId = closedId; this.isOpen =
	 * isOpen; }
	 */

	/**
	 * Gets the open location.
	 * 
	 * @return The open door location.
	 */
	public Location getOpenLocation() {
		return locations[0];
	}

	/**
	 * Gets the closed location.
	 * 
	 * @return The closed door location.
	 */
	public Location getClosedLocation() {
		return locations[1];
	}

	/**
	 * Gets the secondary open location - if the door is a double door.
	 * 
	 * @return The secondary open door location.
	 */
	public Location getSecondOpenLocation() {
		return locations[2];
	}

	/**
	 * Gets the secondary closed location - if the door is a double door.
	 * 
	 * @return The secondary closed door location.
	 */
	public Location getSecondClosedLocation() {
		return locations[3];
	}

	/**
	 * Gets the walk to location.
	 * 
	 * @return The walk to location.
	 */
	public Location getWalkTo() {
		return locations[2];
	}

	/**
	 * Gets the secondary walk to location.
	 * 
	 * @return The secondary walk to location.
	 */
	public Location getSecondaryWalkTo() {
		return locations[3];
	}

	/**
	 * Gets all the locations used for this door.
	 * 
	 * @return All the locations used for this door.
	 */
	public Location[] getLocations() {
		return locations;
	}

	/**
	 * Gets the open face.
	 * 
	 * @return The open doors rotation.
	 */
	public int getOpenFace() {
		return faces[0];
	}

	/**
	 * Gets the closed face.
	 * 
	 * @return The closed doors rotation.
	 */
	public int getClosedFace() {
		return faces[1];
	}

	/**
	 * Gets the secondary open face.
	 * 
	 * @return The secondary open doors rotation.
	 */
	public int getSecondaryOpenFace() {
		return faces[2];
	}

	/**
	 * Gets the secondary closed face.
	 * 
	 * @return The secondary closed doors rotation.
	 */
	public int getSecondaryClosedFace() {
		return faces[3];
	}

	/**
	 * Gets the open id.
	 * 
	 * @return The open doors id.
	 */
	public int getOpenId() {
		return ids[0];
	}

	/**
	 * Gets the closed id.
	 * 
	 * @return The closed doors id.
	 */
	public int getClosedId() {
		return ids[1];
	}

	/**
	 * Gets the secondary open id.
	 * 
	 * @return The secondary open doors id.
	 */
	public int getSecondaryOpenId() {
		return ids[2];
	}

	/**
	 * Gets the secondary closed id.
	 * 
	 * @return The secondary closed doors id.
	 */
	public int getSecondaryClosedId() {
		return ids[3];
	}

	/**
	 * Gets the open object type.
	 * 
	 * @return The open doors object type.
	 */
	public int getOpenType() {
		return types[0];
	}

	/**
	 * Gets the closed object type.
	 * 
	 * @return The closed doors object type.
	 */
	public int getClosedType() {
		return types[1];
	}

	/**
	 * Gets the secondary open object type.
	 * 
	 * @return The secondary open doors object type.
	 */
	public int getSecondaryOpenType() {
		return types[2];
	}

	/**
	 * Gets the secondary closed object type.
	 * 
	 * @return The secondary closed doors object type.
	 */
	public int getSecondaryClosedType() {
		return types[3];
	}

	/**
	 * Determines if the door is open or closed..
	 * 
	 * @return True if the door is open, false if not.
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * If this door is set to open, its set to closed, and vice versa.
	 */
	public void redoOpenState() {
		isOpen = !isOpen;
	}

	/**
	 * Gets the type of this door.
	 */
	public DoorType getType() {
		return type;
	}

	private Location[] locations;

	private int[] faces;

	private int[] ids;

	private int[] types;

	private boolean isOpen;

	private DoorType type;

	public static enum DoorType {
		NORMAL, NORMALFORCE, DOUBLE, DOUBLEFORCE
	}

}