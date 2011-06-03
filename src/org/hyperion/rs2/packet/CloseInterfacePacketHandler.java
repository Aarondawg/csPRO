package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

/**
 * A packet handler that is called when an interface is closed.
 * 
 * @author Graham Edgecombe
 * 
 */
public class CloseInterfacePacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		/*
		 * House options..
		 */
		if (!player.hasVisibleSidebarInterfaces()) {
			player.getActionSender().sendSidebarInterfaces();
			player.setVisibleSidebarInterfaces(true);
		}
		player.getInterfaceState().interfaceClosed(-1);
	}

}
