package org.hyperion.rs2.content.skills.agility.impl;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

public class TeleportObstacle implements Obstacle {

	private static final Animation RESET = Animation.create(-1);

	private final int option;
	private final Location teleport;
	private final Animation animation;
	private final long delay;
	private final double exp;

	public TeleportObstacle(int option, Location teleport, Animation animation,
			long delay, double exp) {
		this.option = option;
		this.teleport = teleport;
		this.animation = animation;
		this.delay = delay;
		this.exp = exp;
	}

	public Location getTeleportLocation() {
		return teleport;
	}

	public Animation getAnimation() {
		return animation;
	}

	public long getDelay() {
		return delay;
	}

	@Override
	public int getObjectOption() {
		return option;
	}

	@Override
	public void climb(final Player player) {
		player.setCanWalk(false);
		player.playAnimation(animation);
		player.getActionQueue().addAction(new Action(player, delay) {

			@Override
			public void execute() {
				player.setTeleportTarget(teleport);
				player.getSkills().addExperience(Skills.AGILITY, exp);
				player.playAnimation(RESET);
				player.setCanWalk(true);
				this.stop();
			}

			@Override
			public QueuePolicy getQueuePolicy() {
				return QueuePolicy.NEVER;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.NON_WALKABLE;
			}

		});
	}

}
