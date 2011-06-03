package org.hyperion.rs2.task.impl;

import java.util.Random;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.task.Task;

/**
 * A task which performs pre-update tasks for an NPC.
 * 
 * @author Graham Edgecombe
 * 
 */
public class NPCTickTask implements Task {

	/**
	 * The random instance telling if we should walk or not.
	 */
	private static final Random r = new Random();

	/**
	 * The npc who we are performing pre-update tasks for.
	 */
	private NPC npc;

	/**
	 * Creates the tick task.
	 * 
	 * @param npc
	 *            The npc.
	 */
	public NPCTickTask(NPC npc) {
		this.npc = npc;
	}

	@Override
	public void execute(GameEngine context) {
		/*
		 * If the map region changed set the last known region.
		 */
		if (npc.isMapRegionChanging()) {
			npc.setLastKnownRegion(npc.getLocation());
		}

		/*
		 * Decrease our combat delay.
		 */
		npc.decreaseCombatDelay(600);

		/*
		 * Resets the NPC's interacting state.
		 */
		if (!npc.isUnderAttack() && !npc.isInCombat()
				&& System.currentTimeMillis() - npc.getLastInteract() > 5000) {
			npc.resetInteractingEntity();
		}

		/*
		 * If we have a min and a max location, we either randomly walk (if we
		 * aren't interacting) Or we follow the entity we're fighting with etc.
		 */
		if (npc.getMinLocation() != null && npc.getMaxLocation() != null
				&& npc.getWalkingType() == 2 && !npc.isInvisible()) {
			/*
			 * If the NPC doesn't interact (is in combat/speaking with a player)
			 */
			if (!npc.isInteracting()) {
				/*
				 * We make NPC's walk randomly.. (Maybe this should be tweaked?)
				 */
				if (r.nextInt(7) == 0) {
					/*
					 * We create two random movements..
					 */
					int moveX = (int) (Math.floor((Math.random() * 3)) - 1);
					int moveY = (int) (Math.floor((Math.random() * 3)) - 1);
					/*
					 * We add those movements to our actual coordinates.
					 */
					int tgtX = npc.getLocation().getX() + moveX;
					int tgtY = npc.getLocation().getY() + moveY;
					/*
					 * We check is those new coordinates is outside of the NPCs
					 * max range, and we modify them slightly if so.
					 */
					if (tgtX > npc.getMaxLocation().getX()
							|| tgtX < npc.getMinLocation().getX()
							|| tgtY > npc.getMaxLocation().getY()
							|| tgtY < npc.getMinLocation().getY()) {
						if (npc.getLocation().getX() > npc.getMaxLocation()
								.getX()) {
							tgtX = npc.getLocation().getX() - 1;
						} else if (npc.getLocation().getX() < npc
								.getMinLocation().getX()) {
							tgtX = npc.getLocation().getX() + 1;
						}
						if (npc.getLocation().getY() > npc.getMaxLocation()
								.getY()) {
							tgtY = npc.getLocation().getY() - 1;
						} else if (npc.getLocation().getY() < npc
								.getMinLocation().getY()) {
							tgtY = npc.getLocation().getY() + 1;
						}
					}
					/*
					 * We check if the new coordinate set is walkable before we
					 * walk towards it.
					 */
					for (GameObject o : npc.getRegion().getGameObjects()) {
						if (o.getType() != 22) {

							for (int xOffset1 = 0; xOffset1 < o.getDefinition()
									.getSizeX(o.getRotation()); xOffset1++) {
								for (int yOffset1 = 0; yOffset1 < o
										.getDefinition().getSizeX(
												o.getRotation()); yOffset1++) {
									int x = o.getLocation().getX() + xOffset1;
									int y = o.getLocation().getY() + yOffset1;
									/*
									 * This works for all NPCs with the size of
									 * one.. (Basicly checks the actual
									 * coordinate of the NPC)
									 */
									if (x == tgtX && y == tgtY) {
										return;
									}
									/*
									 * If the size is bigger than one (TzTokJad,
									 * Hill Giants etc) we do a few nasty for
									 * loops..
									 */
									if (o.getLocation().getZ() == npc
											.getLocation().getZ()) {
										for (int xOffset = 1; xOffset < npc
												.getSize(); xOffset++) {
											for (int yOffset = 1; yOffset < npc
													.getSize(); yOffset++) {
												if (x == tgtX + xOffset
														&& y == tgtY + yOffset) {
													return;
												}
											}
										}
									}
								}

							}

						}
					}
					/*
					 * We add the steps to the walking queue.
					 */
					npc.getWalkingQueue().reset();
					npc.getWalkingQueue().addStep(tgtX, tgtY);
					npc.getWalkingQueue().finish();

				}
			}
		}

		/*
		 * Process the next movement in the NPC's walking queue.
		 */
		npc.getWalkingQueue().processNextMovement();
	}

	// gets the direction between the two given points
	// valid directions are N:0, NE:2, E:4, SE:6, S:8, SW:10, W:12, NW:14
	// the invalid (inbetween) direction are 1,3,5,7,9,11,13,15 i.e. odd
	// integers
	// returns -1, if src and dest are the same
	public static int direction(int srcX, int srcY, int destX, int destY) {
		int dx = destX - srcX, dy = destY - srcY;
		// a lot of cases that have to be considered here ... is there a more
		// sophisticated (and quick!) way?
		if (dx < 0) {
			if (dy < 0) {
				if (dx < dy)
					return 11;
				else if (dx > dy)
					return 9;
				else
					return 10; // dx == dy
			} else if (dy > 0) {
				if (-dx < dy)
					return 15;
				else if (-dx > dy)
					return 13;
				else
					return 14; // -dx == dy
			} else { // dy == 0
				return 12;
			}
		} else if (dx > 0) {
			if (dy < 0) {
				if (dx < -dy)
					return 7;
				else if (dx > -dy)
					return 5;
				else
					return 6; // dx == -dy
			} else if (dy > 0) {
				if (dx < dy)
					return 1;
				else if (dx > dy)
					return 3;
				else
					return 2; // dx == dy
			} else { // dy == 0
				return 4;
			}
		} else { // dx == 0
			if (dy < 0) {
				return 8;
			} else if (dy > 0) {
				return 0;
			} else { // dy == 0
				return -1; // src and dest are the same
			}
		}
	}

}
