package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.ChatMessage;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.util.TextUtils;

/**
 * Handles public chat messages.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ChatPacketHandler implements PacketHandler {

	private static final int CHAT_QUEUE_SIZE = 3;

	@Override
	public void handle(Player player, Packet packet) {
		if (player.getChatMessageQueue().size() >= CHAT_QUEUE_SIZE) {
			return;
		}
		int effects = packet.getShort();
		int numChars = packet.get() & 0xFF;
		String text = TextUtils.decryptPlayerChat(packet, numChars);
		player.getChatMessageQueue().add(
				new ChatMessage(effects, numChars, text));
		player.getUpdateFlags().flag(UpdateFlag.CHAT);
	}

}
