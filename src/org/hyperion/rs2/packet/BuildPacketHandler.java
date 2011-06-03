package org.hyperion.rs2.packet;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.model.GameObjectDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

/**
 * The packet used to build stuff in construction areas.
 * 
 * @author Brown
 */
public class BuildPacketHandler implements PacketHandler {

	@Override
	public void handle(final Player player, Packet packet) {
		final int x = packet.getShort();
		final int y = packet.getLEShortA();
		final int id = packet.getShort();
		System.out.println("Build packet handler: " + id);
		final Location location = Location.create(x, y, player.getLocation()
				.getZ());
		player.getActionQueue().addAction(new Action(player, 0) {

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
				if (player.getLocation().withinRange(location,
						GameObjectDefinition.forId(id).getBiggestSize())
						&& player.getInterfaceState().getCurrentInterface() == -1) {
					this.stop();
					Location loc = Location.create(x - 8
							* (player.getLastKnownRegion().getRegionX() - 6), y
							- 8
							* (player.getLastKnownRegion().getRegionY() - 6),
							player.getLocation().getZ());
					player.getConstruction().build(id, loc);
				}

			}

		});
	}

}
