package org.hyperion.rs2.packet;

import org.hyperion.rs2.content.skills.construction.Construction;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

public class EnterNamePacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		long name = packet.getLong();
		Construction.enterFriendsHouse(player, name);
	}

}
