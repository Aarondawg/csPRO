package org.hyperion.rs2.packet;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Duel;
import org.hyperion.rs2.net.Packet;

public class PlayerOptionPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case 119:
			/*
			 * Option 1.
			 */
			option1(player, packet);
			break;
		case 189:
			/*
			 * Option 2.
			 */
			option2(player, packet);
			break;
		case 24:
			/*
			 * Option 3.
			 */
			option3(player, packet);
			break;
		}
	}

	/**
	 * Handles the first option on a player option menu.
	 * 
	 * @param player
	 *            The player we want to handle a packet for.
	 * @param packet
	 *            The packet we're about to handle.
	 */
	private void option1(final Player player, Packet packet) {
		int id = packet.getLEShort() & 0xFFFF;
		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		final Player victim = (Player) World.getWorld().getPlayers().get(id);
		if (victim.isDead()) {
			return;
		}
		player.setFollowingEntityIndex(id);
		if (player.getLocation().isInDuelArena()) {
			if (player.getLocation().isDueling()
					&& player.getRequestManager().isDueling()) {
				switch (player.getAttackType()) {
				case MELEE:
					if (player.getRequestManager().getDuel()
							.isRuleToggled(Duel.NO_MELEE)) {
						player.getActionSender()
								.sendMessage(
										"Melee attacks have been disabled during this duel.");
						player.getWalkingQueue().reset();
						return;
					}
					break;
				case RANGED:
					if (player.getRequestManager().getDuel()
							.isRuleToggled(Duel.NO_RANGE)) {
						player.getActionSender()
								.sendMessage(
										"Ranged attacks have been disabled during this duel.");
						player.getWalkingQueue().reset();
						return;
					}
					break;
				case MAGIC:
					if (player.getRequestManager().getDuel()
							.isRuleToggled(Duel.NO_MAGIC)) {
						player.getActionSender()
								.sendMessage(
										"Magic attacks have been disabled during this duel.");
						player.getWalkingQueue().reset();
						return;
					}
					break;

				}
				Combat.attack(player, victim);
			} else {
				player.getActionSender().sendStopFollowing(false);
				player.getActionQueue().cancelQueuedActions();
				player.getActionQueue().addAction(new Action(player, 0) {

					@Override
					public QueuePolicy getQueuePolicy() {
						return QueuePolicy.NEVER;
					}

					@Override
					public WalkablePolicy getWalkablePolicy() {
						return WalkablePolicy.FOLLOW; // Its a player based
														// packet, without this
														// it would be
														// discarted.
					}

					@Override
					public void execute() {
						if (player.getLocation().withinRange(victim, 1)
								&& player.getInterfaceState()
										.getCurrentInterface() == -1) {
							this.stop();
							player.getRequestManager().requestDuel(victim);
						}
						this.setDelay(600);
					}

				});
			}
		} else {
			Combat.attack(player, victim);
		}
	}

	/**
	 * Handles the second option on a player option menu.
	 * 
	 * @param player
	 * @param packet
	 */
	private void option2(Player player, Packet packet) {
		int id = packet.getShort() & 0xFFFF;
		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		Player target = (Player) World.getWorld().getPlayers().get(id);
		if (target != null) {
			player.setFollowingEntityIndex(id);
			player.getActionSender().sendFollowingDistance(1);// Normal
																// following.
			player.setInteractingEntity(target);
			player.getActionSender().sendFollowing(target);
		}

	}

	/**
	 * Handles the third option on a player option menu.
	 * 
	 * @param player
	 * @param packet
	 */
	private void option3(final Player player, Packet packet) {
		int id = packet.getShort() & 0xFFFF;
		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		final Player target = (Player) World.getWorld().getPlayers().get(id);
		player.getActionSender().sendStopFollowing(false);
		player.getActionQueue().cancelQueuedActions();
		player.getActionQueue().addAction(new Action(player, 0) {

			@Override
			public QueuePolicy getQueuePolicy() {
				return QueuePolicy.NEVER;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.FOLLOW; // Its a walking based packet,
												// without this it would be
												// discarted.
			}

			@Override
			public void execute() {
				if (player.getLocation().withinRange(target, 1)
						&& player.getInterfaceState().getCurrentInterface() == -1) {
					player.getRequestManager().requestTrade(target);
					this.stop();
				}
				this.setDelay(600);
			}

		});
	}

}
