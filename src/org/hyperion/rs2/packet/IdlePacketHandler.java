package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

public class IdlePacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		// player.getActionSender().sendLogout(true);
		player.forceChat("Idle");
	}

}
