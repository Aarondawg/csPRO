package org.hyperion.rs2.packet;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.action.Action.WalkablePolicy;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.net.Packet;

public class RequestPacketHandler implements PacketHandler {

	@Override
	public void handle(final Player player, Packet packet) {
		int id = packet.getLEShortA() & 0xFFFF;

		if (id < 0 || id >= Constants.MAX_PLAYERS) {
			return;
		}
		final Player other = (Player) World.getWorld().getPlayers().get(id);
		if (other == null) {
			return;
		}
		player.getActionSender().sendStopFollowing(false);
		player.getActionQueue().cancelQueuedActions();
		player.getActionQueue().addAction(new Action(player, 0) {

			@Override
			public QueuePolicy getQueuePolicy() {
				return QueuePolicy.NEVER;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.FOLLOW; // Its a player based packet,
												// without this it would be
												// discarted.
			}

			@Override
			public void execute() {
				if (player.getLocation().withinRange(other, 1)) {
					player.getRequestManager().answerTrade(other);
					this.stop();
				}
				this.setDelay(600);
			}

		});

	}

}
