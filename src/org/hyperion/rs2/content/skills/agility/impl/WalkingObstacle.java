package org.hyperion.rs2.content.skills.agility.impl;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;

public class WalkingObstacle implements Obstacle {

	private final int option;
	private final Location[] walking;
	private final int[] animations = Player.STANDARD_UPDATING_ANIMATIONS;
	private final long delay;
	private final double exp;

	public WalkingObstacle(int option, Location[] walking, int animation,
			long delay, double exp) {
		this.option = option;
		this.walking = walking;
		this.animations[2] = animation;
		this.delay = delay;
		this.exp = exp;
	}

	@Override
	public int getObjectOption() {
		return option;
	}

	@Override
	public void climb(final Player player) {
		player.setCanWalk(false);
		player.setTemporaryUpdatingAnimation(animations);
		player.setDifferentUpdateAnimation(true);
		Location furthest = walking[0];
		for (int index = 1; index < walking.length; index++) {
			Location other = walking[index];
			if (other.getDistance(player.getLocation()) > furthest
					.getDistance(player.getLocation())) {
				furthest = other;
			}
		}
		player.getWalkingQueue().reset();
		player.getWalkingQueue().addStep(furthest.getX(), furthest.getY());
		player.getWalkingQueue().finish();
		player.getActionQueue().addAction(new Action(player, delay) {

			@Override
			public QueuePolicy getQueuePolicy() {
				return QueuePolicy.NEVER;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.NON_WALKABLE;
			}

			@Override
			public void execute() {
				player.setCanWalk(true);
				player.setDifferentUpdateAnimation(false);
				this.stop();
			}

		});

	}

}
