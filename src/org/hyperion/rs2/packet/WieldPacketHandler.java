package org.hyperion.rs2.packet;

import org.hyperion.rs2.content.quest.QuestHandler;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.container.Equipment.EquipmentType;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.util.ItemConfiguration;

/**
 * Handles the 'wield' option on items.
 * 
 * @author Graham Edgecombe
 * 
 */
public class WieldPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		int interfaceSet = packet.getLEInt();
		int interfaceId = interfaceSet >> 16;
		int index = packet.getLEShort();
		int wearId = packet.getLEShortA();
		switch (interfaceId) {
		case Inventory.INTERFACE:
			if (index >= 0 && index < Inventory.SIZE) {
				Item item = player.getInventory().get(index);
				if (item != null && item.getId() == wearId) {
					EquipmentType type = ItemDefinition.forId(wearId)
							.getEquipmentType();
					if (type.equals(EquipmentType.NONE)) {
						player.getActionSender().sendMessage(
								"You can't wear this item!");
						return;
					}
					if (type == EquipmentType.WEAPON
							|| type == EquipmentType.SHIELD) {
						if (Equipment.isTwoHanded(item.getDefinition())
								&& player.getInventory().freeSlots() < 1
								&& player.getEquipment().get(5) != null) {
							player.getActionSender().sendMessage(
									"Not enough free space in your inventory.");
							return;
						}
					}
					if (QuestHandler.isQuestEquipItem(player, wearId)) {
						return;
					}
					int attLevel = player.getSkills().getLevelForExperience(0);
					int defLevel = player.getSkills().getLevelForExperience(1);
					int strLevel = player.getSkills().getLevelForExperience(2);
					int rangeLevel = player.getSkills()
							.getLevelForExperience(4);
					int magicLevel = player.getSkills()
							.getLevelForExperience(6);
					int targetAtt = ItemConfiguration.getCLAttack(item
							.getDefinition().getId());
					int targetDef = ItemConfiguration.getCLDefence(item
							.getDefinition().getId());
					int targetStr = ItemConfiguration.getCLStrength(item
							.getDefinition().getId());
					int targetRange = ItemConfiguration.getCLRanged(item
							.getDefinition().getId());
					int targetMagic = ItemConfiguration.getCLMagic(item
							.getDefinition().getId());
					boolean stop = false;
					if (attLevel < targetAtt) {
						stop = true;
						player.getActionSender().sendMessage(
								"You need an Attack level of " + targetAtt
										+ " to wield this item.");
					}
					if (strLevel < targetStr) {
						stop = true;
						player.getActionSender().sendMessage(
								"You need a Strength level of " + targetStr
										+ " to wield this item.");
					}
					if (defLevel < targetDef) {
						stop = true;
						player.getActionSender().sendMessage(
								"You need a Defence level of " + targetDef
										+ " to wield this item.");
					}
					if (rangeLevel < targetRange) {
						stop = true;
						player.getActionSender().sendMessage(
								"You need a Ranged level of " + targetRange
										+ " to wield this item.");
					}
					if (magicLevel < targetMagic) {
						stop = true;
						player.getActionSender().sendMessage(
								"You need a Magic level of " + targetMagic
										+ " to wield this item.");
					}
					if (stop) {
						return;
					}

					if (player.getRequestManager().isDueling()
							&& player.getLocation().isInDuelArena()) {
						if (!player.getRequestManager().getDuel()
								.canUseItem(player, type)) {
							return;
						}
					}

					if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null
							&& (type == EquipmentType.WEAPON || type == EquipmentType.SHIELD)) {
						if (player.getEquipment().get(Equipment.SLOT_WEAPON)
								.getId() != wearId) {
							player.getSpecials().setActive(false);
							player.getMagic().resetAutoCasting();
						}
					}

					Item oldEquip = null;
					boolean stackable = item.getDefinition().isStackable();
					if (player.getEquipment().isSlotUsed(type.getSlot())
							&& !stackable) {
						oldEquip = player.getEquipment().get(type.getSlot());
						player.getEquipment().set(type.getSlot(), null);
					} else if (stackable
							&& (player.getEquipment().get(type.getSlot()) == null || player
									.getEquipment().get(type.getSlot()).getId() != wearId)) {
						oldEquip = player.getEquipment().get(type.getSlot());
						player.getEquipment().set(type.getSlot(), null);
					}
					player.getInventory().set(index, null);
					/*
					 * The following fixes dual weapon wielding.
					 */
					if (type == EquipmentType.WEAPON) {
						if (Equipment.isTwoHanded(item.getDefinition())
								&& player.getEquipment().get(5) != null) {
							if (!player.getInventory().add(
									player.getEquipment().get(5))) {
								player.getInventory().add(item);
								return;
							}
							player.getEquipment().set(5, null);
						}
					} else if (type == EquipmentType.SHIELD) {
						if (player.getEquipment().get(3) != null
								&& Equipment.isTwoHanded(player.getEquipment()
										.get(3).getDefinition())) {
							if (!player.getInventory().add(
									player.getEquipment().get(3))) {
								player.getInventory().add(item);
								return;
							}
							player.getEquipment().set(3, null);
						}
					}
					/*
					 * Continuing Grahams code.
					 */
					if (oldEquip != null) {
						player.getInventory().add(oldEquip);
					}
					if (!stackable
							|| player.getEquipment().get(type.getSlot()) == null) {
						player.getEquipment().set(type.getSlot(), item);
					} else {
						player.getEquipment().add(item);

					}
					player.refreshCombatVariables(type.getSlot());
				}
			}
			break;
		}
	}

}
