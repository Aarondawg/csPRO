package org.hyperion.rs2.packet;

import org.hyperion.rs2.model.GameObjectDefinition;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.Packet;

public class ExaminePacketHandler implements PacketHandler {

	private final int ITEM_EXAMINE = 7;
	private final int OBJECT_EXAMINE = 4;
	private final int NPC_EXAMINE = 136;

	@Override
	public void handle(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case ITEM_EXAMINE:
			itemExamine(player, packet);
			break;
		case OBJECT_EXAMINE:
			objectExamine(player, packet);
			break;
		case NPC_EXAMINE:
			npcExamine(player, packet);
			break;
		}

	}

	private void npcExamine(Player player, Packet packet) {
		int npcId = packet.getLEShortA();
		player.getActionSender().sendMessage(
				NPCDefinition.forId(npcId).getExamine());
	}

	private void objectExamine(Player player, Packet packet) {
		int objectId = packet.getLEShortA();
		System.out.println("Examining object: " + objectId);
		player.getActionSender().sendMessage(
				GameObjectDefinition.forId(objectId).getDescription());
	}

	private void itemExamine(Player player, Packet packet) {
		int itemId = packet.getLEShortA();
		player.getActionSender().sendMessage(
				ItemDefinition.forId(itemId).getDescription());
	}

}
