package org.hyperion.rs2.packet;

import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

/**
 * A packet which handles walking requests.
 * 
 * @author Graham Edgecombe
 * @author Brown.
 * @author Laz.
 * @author Vastico.
 */
public class WalkingPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		if (!player.canWalk()) {
			return;
		}
		int size = packet.getLength();
		if (packet.getOpcode() == 191) {
			size -= 14;
		}
		player.getWalkingQueue().reset();
		if (player.isFollowing() && packet.getOpcode() == 186
				&& player.isInteracting()) {
			player.getActionQueue().clearNonFollowableActions();
		} else {
			if (!player.hasVisibleSidebarInterfaces()) {
				player.getActionSender().sendSidebarInterfaces();
				player.setVisibleSidebarInterfaces(true);
			}
			player.setInCombat(false);
			player.setFollowingEntityIndex(-1);
			player.getActionSender().sendCloseInterface();
			player.getActionQueue().clearNonWalkableActions();
			player.resetInteractingEntity();
		}

		final int steps = (size - 5) / 2;
		final int[][] path = new int[steps][2];
		final int firstX = packet.getShortA();// -
												// (player.getLocation().getRegionX()
												// - 6) * 8;
		final boolean runSteps = packet.getByte() == 1;
		for (int i = 0; i < steps; i++) {
			path[i][0] = packet.getByteS();
			path[i][1] = packet.getByteC();
		}
		final int firstY = packet.getShort();// -
												// (player.getLocation().getRegionY()
												// - 6) * 8;
		player.getWalkingQueue().setRunningQueue(runSteps);
		player.getWalkingQueue().addStep(firstX, firstY);
		for (int i = 0; i < steps; i++) {
			path[i][0] += firstX;
			path[i][1] += firstY;
			player.getWalkingQueue().addStep(path[i][0], path[i][1]);
		}
		player.getWalkingQueue().finish();
	}

	/*
	 * player.getWalkingQueue().setRunningQueue(runSteps);
	 * //player.getWalkingQueue().addStep(firstX, firstY); for (int i = 0; i <
	 * steps; i++) { path[i][0] += firstX; path[i][1] += firstY;
	 * //player.getWalkingQueue().addStep(path[i][0], path[i][1]); }
	 * //player.getWalkingQueue().finish(); if(steps > 0) {
	 * PathFinder.calculateWalkingRoute(player, path[steps - 1][0], path[steps -
	 * 1][1], false); } else { PathFinder.calculateWalkingRoute(player, firstX,
	 * firstY, false); }
	 */

}
