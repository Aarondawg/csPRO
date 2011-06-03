package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.util.TextUtils;

public class FriendsPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		System.out.println("Friends opcode: " + packet.getOpcode());
		long name = packet.getLong();
		System.out.println("Name: " + name);
		if (packet.getOpcode() == 118) {
			player.getFriends().addFriend(name);
		} else if (packet.getOpcode() == 39) {
			player.getFriends().addIgnore(name);
		} else if (packet.getOpcode() == 93) {
			player.getFriends().removeFriend(name);
		} else if (packet.getOpcode() == 80) {
			player.getFriends().removeIgnore(name);
		} else if (packet.getOpcode() == 59) {
			sendMessage(player, packet, name);
		}
	}

	private void sendMessage(Player player, Packet packet, long name) {
		int numChars = packet.get() & 0xFF;
		String text = TextUtils.decryptPlayerChat(packet, numChars);
		player.getFriends().sendMessage(name, text);
	}

}
