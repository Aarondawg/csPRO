package org.hyperion.rs2.packet;

import java.util.logging.Logger;

import org.hyperion.rs2.content.Emotes;
import org.hyperion.rs2.content.RandomEvents;
import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.content.quest.QuestHandler;
import org.hyperion.rs2.content.skills.Crafting;
import org.hyperion.rs2.content.skills.Smithing;
import org.hyperion.rs2.content.skills.magic.SpellMananger;
import org.hyperion.rs2.content.skills.magic.Magic.MagicType;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.container.Duel;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.net.Packet;

/**
 * Handles clicking on most buttons in the interface.
 * 
 * @author Graham Edgecombe
 * @author Brown.
 * @author Tommi.
 * 
 */
public class ActionButtonPacketHandler implements PacketHandler {

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger
			.getLogger(ActionButtonPacketHandler.class.getName());

	@Override
	public void handle(Player player, Packet packet) {
		if (player == null)
			return;
		int interfaceId = packet.getShort() & 0xFFFF;
		int button = packet.getShort() & 0xFFFF;
		System.out.println("InterfaceId " + interfaceId + " ButtonId : "
				+ button);
		int buttonId2 = 0;
		if (packet.getLength() >= 6) {
			buttonId2 = packet.getShort() & 0xFFFF;
		}
		if (buttonId2 == 65535) {
			buttonId2 = 0;
		}
		if (player.getConstruction().handleBuildingInterfaces(interfaceId,
				button)) {
			return;
		}
		switch (interfaceId) {
		case 411:
			player.getWarriorsGuild().setDefenceStyle(button);
			break;
		/*
		 * Magic.
		 */
		case 192: // Modern spells.
			SpellMananger.castButtonSpell(MagicType.MODERN, player, button);
			break;
		case 193:// Ancient spells.
			SpellMananger.castButtonSpell(MagicType.ANCIENT, player, button);
			break;
		case 430: // Lunar spells
			SpellMananger.castButtonSpell(MagicType.LUNAR, player, button);
			break;
		case 267:
			if (PestControl.handleVoidKnightRewardInterface(player, button)) {
				return;
			} else {
				System.out
						.println("Unhandled Void Knight Reward Interface Button: "
								+ button);
			}
			break;

		/**
		 * Logout.
		 */
		case 182:
			player.getActionSender().sendLogout(false);
			break;
		/**
		 * Genie lamp.
		 */
		case 134:
			RandomEvents.handleGenieLamp(player, button);
			break;
		/**
		 * Quest interface.
		 */
		case 274:
			QuestHandler.sendQuestInterface(player, button);
			break;
		/**
		 * Bank.
		 */
		case 12:
			switch (button) {
			/**
			 * Withdraw as note.
			 */
			case 92:
				player.getSettings().setWithdrawAsNotes(true);
				break;
			/**
			 * Withdraw as item.
			 */
			case 93:
				player.getSettings().setWithdrawAsNotes(false);
				break;
			/**
			 * Rearrange mode :s
			 */
			case 98:
				player.getSettings().setSwapping(true);
			case 99:
				player.getSettings().setSwapping(false);
				break;
			}
			break;
		/**
		 * Modern autocasting interface.
		 */
		case 319:
			// TODO: Find a way to make interface "type 8" show :s
			// player.getEquipment().refresh();
			player.getMagic().setModernAutoCasting(button);
			break;
		/**
		 * Ancients magic auto casting.
		 */
		case 388:
			// player.getEquipment().refresh();
			// player.setAttacking(false);
			player.getMagic().setAncientAutoCasting(button);
			break;
		/**
		 * Prayer interface.
		 */
		case 271:
			if (player.getRequestManager().isDueling()) {
				if (player.getRequestManager().getDuel()
						.isRuleToggled(Duel.NO_PRAYER)) {
					player.getActionSender().sendMessage(
							"You cannot use prayer in this duel!");
					player.getPrayer().reset();
					return;
				}
			}
			player.getPrayer().handleButtons(button);
			break;

		/**
		 * Second duel interface (accept)
		 */
		case 106:
			if (player.getRequestManager().isDueling()) {
				player.getRequestManager().getDuel().accept(player);
			} else {
				System.out.println("Duel thingy was null.");
			}

			break;
		/**
		 * First Duel interface.
		 */
		case 107:
			if (player.getRequestManager().isDueling()) {
				player.getRequestManager().getDuel()
						.handleFirstInterfaceButtons(player, button);
			} else {
				System.out.println("Duel thingy was null.");
			}
			break;
		/**
		 * Smelting interface 311.
		 */
		case 311:
			switch (button) {
			/*
			 * Rune bars.
			 */
			case 48:// 1
				Smithing.furnaceSmelting(player, 85, 451, 453, 8, 2363, 1, 50,
						false, true);
				break;
			case 47:// 5
				Smithing.furnaceSmelting(player, 85, 451, 453, 8, 2363, 5, 50,
						false, true);
				break;
			case 46:// 10
				Smithing.furnaceSmelting(player, 85, 451, 453, 8, 2363, 10, 50,
						false, true);
				break;
			case 45:// x
				player.getInterfaceState().openEnterAmountInterface(
						interfaceId, 2363);
				break;
			/*
			 * Adamant bars.
			 */
			case 44:// 1
				Smithing.furnaceSmelting(player, 70, 449, 453, 4, 2361, 1,
						37.5, false, true);
				break;
			case 43:// 5
				Smithing.furnaceSmelting(player, 70, 449, 453, 6, 2361, 5,
						37.5, false, true);
				break;
			case 42:// 10
				Smithing.furnaceSmelting(player, 70, 449, 453, 6, 2361, 10,
						37.5, false, true);
				break;
			case 41:// x
				player.getInterfaceState().openEnterAmountInterface(
						interfaceId, 2361);
				break;
			/*
			 * Mithril
			 */
			case 40:// 1
				Smithing.furnaceSmelting(player, 50, 447, 453, 4, 2359, 1, 30,
						false, true);
				break;
			case 39:// 5
				Smithing.furnaceSmelting(player, 50, 447, 453, 4, 2359, 5, 30,
						false, true);
				break;
			case 38:// 10
				Smithing.furnaceSmelting(player, 50, 447, 453, 4, 2359, 10, 30,
						false, true);
			case 37:// x
				player.getInterfaceState().openEnterAmountInterface(
						interfaceId, 2359);
				break;
			/*
			 * Gold
			 */
			case 36:// 1
				Smithing.furnaceSmelting(player, 40, 444, 444, 1, 2357, 1,
						22.5, false, false);
				break;
			case 35:// 5
				Smithing.furnaceSmelting(player, 40, 444, 444, 1, 2357, 5,
						22.5, false, false);
				break;
			case 34:// 10
				Smithing.furnaceSmelting(player, 40, 444, 444, 1, 2357, 10,
						22.5, false, false);
				break;
			case 33:// x (doing all for now)
				player.getInterfaceState().openEnterAmountInterface(
						interfaceId, 2357);
				break;
			/*
			 * Steel
			 */
			case 32:// 1
				Smithing.furnaceSmelting(player, 30, 440, 453, 2, 2353, 1,
						17.5, false, true);
				break;
			case 31:// 5
				Smithing.furnaceSmelting(player, 30, 440, 453, 2, 2353, 5,
						17.5, false, true);
				break;
			case 30:// 10
				Smithing.furnaceSmelting(player, 30, 440, 453, 2, 2353, 10,
						17.5, false, true);
				break;
			case 29:// x (doing all for now)
				player.getInterfaceState().openEnterAmountInterface(
						interfaceId, 2353);
				break;
			/*
			 * Silver
			 */
			case 28:// 1
				Smithing.furnaceSmelting(player, 20, 442, 442, 1, 2355, 1,
						13.7, false, false);
				break;
			case 27:// 5
				Smithing.furnaceSmelting(player, 20, 442, 442, 1, 2355, 5,
						13.7, false, false);
				break;
			case 26:// 10
				Smithing.furnaceSmelting(player, 20, 442, 442, 1, 2355, 10,
						13.7, false, false);
				break;
			case 25:// x (doing all for now)
				player.getInterfaceState().openEnterAmountInterface(
						interfaceId, 2355);
				break;
			/*
			 * Iron
			 */
			case 24:// 1
				Smithing.furnaceSmelting(player, 15, 440, 440, 1, 2351, 1,
						12.5, true, false);
				break;
			case 23:// 5
				Smithing.furnaceSmelting(player, 15, 440, 440, 1, 2351, 5,
						12.5, true, false);
				break;
			case 22:// 10
				Smithing.furnaceSmelting(player, 15, 440, 440, 1, 2351, 10,
						12.5, true, false);
				break;
			case 21:// x (doing all for now)
				player.getInterfaceState().openEnterAmountInterface(
						interfaceId, 2351);
				break;
			/*
			 * Blurite /skip.
			 */

			/*
			 * Bronze.
			 */
			case 16: // 1
				Smithing.furnaceSmelting(player, 1, 438, 436, 1, 2349, 1, 6.2,
						false, true);
				break;
			case 15: // 5
				Smithing.furnaceSmelting(player, 1, 438, 436, 1, 2349, 5, 6.2,
						false, true);
				break;
			case 14: // 10
				Smithing.furnaceSmelting(player, 1, 438, 436, 1, 2349, 10, 6.2,
						false, true);
				break;
			case 13: // x (doing all for now)
				player.getInterfaceState().openEnterAmountInterface(
						interfaceId, 2349);
				break;
			default:
				logger.info("Unhandled buttonId: " + button
						+ "on the smelting interface (probs just blurite)");
			}
			break;

		/**
		 * Leather crafting interface.
		 */
		case 154:
			if (Crafting.leatherCraftingInterface(player, button))
				return;
			break;
		/**
		 * Tanning interface.
		 */
		case 324:
			if (Crafting.tanningButtons(player, button)) {
				return;
			}
			break;
		/**
		 * Staff weapon interface?
		 */
		case 90:
			player.getWalkingQueue().reset();
			switch (button) {
			case 1:
			case 2:
			case 3:
				// player.getActionSender().sendString("Spell", 90, 105);
				// player.setAutoCasting(false);
				break;
			// TODO: Set like fighting types here.
			case 4:// TODO: Defence exp lol.
			case 5:
				/*
				 * We make sure the player isn't trying to AutoCast, in case
				 * we're dueling.
				 */
				if (player.getRequestManager().isDueling()) {
					if (player.getRequestManager().getDuel()
							.isRuleToggled(Duel.NO_MAGIC)) {
						player.getActionSender().sendMessage(
								"Magic have been disabled during this duel!");
						player.getPrayer().reset();
						return;
					}
				}
				if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
					if (player.getEquipment().get(Equipment.SLOT_WEAPON)
							.getId() == 4675) {
						if (player.getMagic().getSpellBook() != MagicType.ANCIENT) {
							player.getActionSender()
									.sendMessage(
											"You cannot autocast Ancient spells without having the Ancient spellbook!");
							return;
						}
						player.getActionSender().sendSidebarInterface(0, 388);
						return;
					} else {
						int[] MODERN_STAVES = { 1381, 1397, 1383, 1395, 1385,
								1399, 1387, 1393 };
						for (int i : MODERN_STAVES) {
							if (i == player.getEquipment()
									.get(Equipment.SLOT_WEAPON).getId()) {
								if (player.getMagic().getSpellBook() != MagicType.MODERN) {
									player.getActionSender()
											.sendMessage(
													"You cannot autocast Modern spells without having the Modern spellbook!");
									return;
								}
								player.getActionSender().sendSidebarInterface(
										0, 319);// Modern magic auto casting
								return;
							}
						}
					}
				}
				break;
			default:
				logger.fine("Unhandled staff button: " + button);
			}
			break;
		case 75: // battleaxe attack interface
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// Slash
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 5:
				// Slash
				player.setAttackButton(2);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 4:
				// Crush
				player.setAttackButton(3);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 3:
				// Slash
				player.setAttackButton(4);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			case 10:
				player.getSpecials()
						.setActive(!player.getSpecials().isActive());
				break;
			}
			break;
		case 76: // granite maul attack interface
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// Crush
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 4:
				// Crush
				player.setAttackButton(2);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 3:
				// Crush
				player.setAttackButton(4);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			case 8:
				player.getSpecials()
						.setActive(!player.getSpecials().isActive());
				break;
			}
			break;
		case 77: // bow attack interface
		case 79: // cross bow interface..
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// No others..
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 4:
				player.setAttackButton(2);
				player.setAttackStyle(Combat.RAPID);
				break;
			case 3:
				player.setAttackButton(3);
				player.setAttackStyle(Combat.LONGRANGE);
				break;
			case 8:
				player.getSpecials()
						.setActive(!player.getSpecials().isActive());
				break;
			}
			break;
		case 78: // claw attack interface
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// Slash
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 5:
				// Slash
				player.setAttackButton(2);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 4:
				// Stab
				player.setAttackButton(3);
				player.setAttackStyle(Combat.CONTROLLED);
				break;
			case 3:
				// Slash
				player.setAttackButton(4);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			case 10:
				player.getSpecials()
						.setActive(!player.getSpecials().isActive());
				break;
			}
			break;
		case 80: // frog cannon
			if (setAutoReliating(player, button)) {
				return;
			}
			// Say what?
			break;
		case 81: // sword and scimmy
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// Slash
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 3:
				// Slash
				player.setAttackButton(2);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 4:
				// Stab
				player.setAttackButton(3);
				player.setAttackStyle(Combat.CONTROLLED);
				break;
			case 5:
				// Slash
				player.setAttackButton(4);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			case 10:
				player.getSpecials()
						.setActive(!player.getSpecials().isActive());
				break;
			}
			break;
		case 82: // Looks like the regulair attacking interface..
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// Slash
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 3:
				// Slash
				player.setAttackButton(2);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 4:
				// Crush
				player.setAttackButton(3);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 5:
				// Slash
				player.setAttackButton(4);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			case 10:
				player.getSpecials()
						.setActive(!player.getSpecials().isActive());
				break;
			}
			break;
		case 83: // pickaxe attack interface
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// Stab
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 3:
				// Stab
				player.setAttackButton(2);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 4:
				// Crush
				player.setAttackButton(3);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 5:
				// Stab
				player.setAttackButton(4);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			}
			break;
		case 84: // halberd attack interface
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// Stab
				player.setAttackButton(1);
				player.setAttackStyle(Combat.CONTROLLED);
				break;
			case 3:
				// Slash
				player.setAttackButton(2);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 4:
				// Stab
				player.setAttackButton(3);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			case 8:
				player.getSpecials()
						.setActive(!player.getSpecials().isActive());
				break;
			}
			break;
		case 85: // Staff
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// Crush
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 3:
				// Crush
				player.setAttackButton(2);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 4:
				// Crush
				player.setAttackButton(3);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			case 8:
				player.getSpecials()
						.setActive(!player.getSpecials().isActive());
				break;
			}
			break;
		case 86: // scythe attack interface
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// Slash
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 3:
				// Stab
				player.setAttackButton(2);
				player.setAttackStyle(Combat.AGGRESSIVE);
			case 4:
				// Crush
				player.setAttackButton(3);
				player.setAttackStyle(Combat.AGGRESSIVE);
			case 5:
				// Slash
				player.setAttackButton(4);
				player.setAttackStyle(Combat.DEFENSIVE);
			}
			break;
		case 87: // spear attack interface
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// Stab
				player.setAttackButton(1);
				player.setAttackStyle(Combat.CONTROLLED);
				break;
			case 3:
				// Slash
				player.setAttackButton(2);
				player.setAttackStyle(Combat.CONTROLLED);
				break;
			case 4:
				// Crush
				player.setAttackButton(3);
				player.setAttackStyle(Combat.CONTROLLED);
				break;
			case 5:
				// Stab
				player.setAttackButton(4);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			case 10:
				player.getSpecials()
						.setActive(!player.getSpecials().isActive());
				break;
			}
			break;
		case 88: // mace attack interface
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// Crush
				player.setAttackButton(1);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			case 3:
				// Crush
				player.setAttackButton(2);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 4:
				// Stab
				player.setAttackButton(3);
				player.setAttackStyle(Combat.CONTROLLED);
				break;
			case 5:
				// Crush
				player.setAttackButton(4);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			case 10:
				player.getSpecials()
						.setActive(!player.getSpecials().isActive());
				break;
			}
			break;
		case 89: // dagger attack interface
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 3:
				player.setAttackButton(2);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 4:
				player.setAttackButton(3);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 5:
				player.setAttackButton(4);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			case 10:
				player.getSpecials()
						.setActive(!player.getSpecials().isActive());
				break;
			}
			break;
		// case 90: //staff interface with autocast options
		// if(setAutoReliating(player, button)) {
		// return;
		// }
		// break;
		case 91: // dart/knife attack interface
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 3:
				player.setAttackButton(2);
				player.setAttackStyle(Combat.RAPID);
				break;
			case 4:
				player.setAttackButton(3);
				player.setAttackStyle(Combat.LONGRANGE);
				break;
			}
			break;
		case 92: // unarmed attack interface
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				// Crush
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 3:
				// Crush
				player.setAttackButton(2);
				player.setAttackStyle(Combat.AGGRESSIVE);
				break;
			case 4:
				// Crush
				player.setAttackButton(3);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			}
			break;
		case 93: // some whip style attack interface
			if (setAutoReliating(player, button)) {
				return;
			}
			switch (button) {
			case 2:
				player.setAttackButton(1);
				player.setAttackStyle(Combat.ACCURATE);
				break;
			case 3:
				player.setAttackButton(2);
				player.setAttackStyle(Combat.CONTROLLED);
				break;
			case 4:
				player.setAttackButton(3);
				player.setAttackStyle(Combat.DEFENSIVE);
				break;
			case 8:
				player.getSpecials()
						.setActive(!player.getSpecials().isActive());
				break;
			}
			break;
		case Trade.SECONDARY_TRADING_INTERFACE:
			/*
			 * Accept the second trading screen.
			 */
			if (button == 101) {
				player.getRequestManager().getTrade().accept(player);
			}
			break;
		case Trade.FIRST_TRADING_INTERFACE:
			/*
			 * Accept the first trading screen.
			 */
			if (button == 96) {
				player.getRequestManager().getTrade().accept(player);
			}
			break;
		case 387:
			/*
			 * Equipment tab.
			 */
			switch (button) {
			case 24:
				player.getActionSender().sendInterface(465);
				player.getEquipment().fireItemsChanged();
				player.getBonuses().refresh();
			default:
				logger.config("Unhandled button id: 387, " + button + ".");
				break;
			}
			break;
		case 261:
			switch (button) {
			case 10:
				player.getSettings().setBrightness(4);
				break;
			case 9:
				player.getSettings().setBrightness(3);
				break;
			case 8:
				player.getSettings().setBrightness(2);
				break;
			case 7:
				player.getSettings().setBrightness(1);
				break;
			case 0:
				/*
				 * Toggle run.
				 */
				if (!player.getWalkingQueue().isRunningToggled()) {
					player.getWalkingQueue().setRunningToggled(true);
					player.getActionSender().sendConfig(173, 1);
				} else {
					player.getWalkingQueue().setRunningToggled(false);
					player.getActionSender().sendConfig(173, 0);
				}
				break;
			case 1:
				/*
				 * Chat effects config.
				 */
				if (!player.getSettings().isChatEffectsEnabled()) {
					player.getSettings().setChatEffectsEnabled(true);
					player.getActionSender().sendConfig(171, 0);
				} else {
					player.getSettings().setChatEffectsEnabled(false);
					player.getActionSender().sendConfig(171, 1);
				}
				break;
			case 2:
				/*
				 * Split private chat config.
				 */
				if (!player.getSettings().isPrivateChatSplit()) {
					player.getSettings().setPrivateChatSplit(true);
					player.getActionSender().sendConfig(287, 1);
				} else {
					player.getSettings().setPrivateChatSplit(false);
					player.getActionSender().sendConfig(287, 0);
				}
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				break;
			case 3:
				/*
				 * Mouse button config.
				 */
				if (!player.getSettings().isMouseTwoButtons()) {
					player.getSettings().setMouseTwoButtons(true);
					player.getActionSender().sendConfig(170, 0);
				} else {
					player.getSettings().setMouseTwoButtons(false);
					player.getActionSender().sendConfig(170, 1);
				}
				break;
			case 4:
				/*
				 * Accept aid config.
				 */
				if (!player.getSettings().isAcceptAidEnabled()) {
					player.getSettings().setAcceptAidEnabled(true);
					player.getActionSender().sendConfig(427, 1);
				} else {
					player.getSettings().setAcceptAidEnabled(false);
					player.getActionSender().sendConfig(427, 0);
				}
				break;
			/*
			 * House options.
			 */
			case 5:
				for (int i = 0; i < 14; i++) {
					player.getActionSender().sendSidebarInterface(i, -1);
				}
				player.getActionSender().sendSidebarInterface(11, 398);
				player.getActionSender().sendConfig(261,
						player.getConstruction().isInBuildingMode() ? 1 : 0);
				player.getActionSender().sendConfig(262,
						player.getConstruction().getHouse().getRooms().length);
				player.setVisibleSidebarInterfaces(false);
				break;
			default:
				logger.config("Unhandled button id: " + interfaceId + ", "
						+ button + ".");
				break;
			}
			break;
		/*
		 * House options interface.
		 */
		case 398:
			switch (button) {
			/*
			 * Building mode off.
			 */
			case 1:
				if (player.getConstruction().switchBuildingMode(false)) {
					player.getActionSender().sendConfig(261, 0);
				}
				break;
			/*
			 * Building mode on.
			 */
			case 14:
				if (player.getConstruction().switchBuildingMode(true)) {
					player.getActionSender().sendConfig(261, 1);
				}
				break;
			/*
			 * Leave house.
			 */
			case 13:
				// TODO:
				break;
			/*
			 * Expel guests.
			 */
			case 15:
				// TODO:
				break;
			}
			break;
		case 378:
			/*
			 * Welcome screen
			 */
			switch (button) {
			case 6:
				/*
				 * Close the welcome screen.
				 */
				// Quest.sendQuestLogin(player);
				player.getActionSender().sendCloseInterface();
				/*
				 * if (!player.getSettings().hasChoosenOutfit()) {
				 * //player.getActionSender().sendChatboxAndMainInterface(269,
				 * 372); player.getActionSender().sendInterface(269);
				 * /*player.getActionSender().sendChatboxInterface(372);
				 * player.getActionSender
				 * ().sendString(TutorialIsland.CHATBOX_MESSAGES[0][0], 372, 0);
				 * //Title. player.getActionSender().sendString(TutorialIsland.
				 * CHATBOX_MESSAGES[0][1], 372, 1); //Line 1.
				 * player.getActionSender
				 * ().sendString(TutorialIsland.CHATBOX_MESSAGES[0][2], 372, 2);
				 * //Line 2. player.getActionSender().sendString(TutorialIsland.
				 * CHATBOX_MESSAGES[0][3], 372, 3); //Line 3.
				 * player.getActionSender
				 * ().sendString(TutorialIsland.CHATBOX_MESSAGES[0][4], 372, 4);
				 * //Line 4. }
				 */
				break;
			default:
				logger.config("Unhandled button id: 378, " + button + ".");
			}
			break;
		case 464:
			/*
			 * Emote tab.
			 */
			if (!Emotes.emote(player, button)) {
				logger.config("Unhandled button id: 464, " + button + ".");
			}
			break;
		case 320:
			/*
			 * Skills tab.
			 */
			int skillMenu = -1;
			switch (button) {
			case 123:
				/*
				 * Attack
				 */
				skillMenu = 0;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 126:
				/*
				 * Strength.
				 */
				skillMenu = 1;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 129:
				/*
				 * Defence.
				 */
				skillMenu = 2;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 132:
				/*
				 * Ranged
				 */
				skillMenu = 3;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 135:
				/*
				 * Prayer
				 */
				skillMenu = 4;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 142:
				/*
				 * Magic
				 */
				skillMenu = 5;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 145:
				/*
				 * Runecrafting
				 */
				skillMenu = 18;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 148:
				/*
				 * Construction
				 */
				skillMenu = 21;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 124:
				/*
				 * Hitpoints
				 */
				skillMenu = 6;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 127:
				/*
				 * Agility
				 */
				skillMenu = 7;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 130:
				/*
				 * Herblore
				 */
				skillMenu = 8;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 133:
				/*
				 * Thieving
				 */
				skillMenu = 9;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 136:
				/*
				 * Crafting
				 */
				skillMenu = 10;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 143:
				/*
				 * Fletching
				 */
				skillMenu = 11;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 146:
				/*
				 * Slayer
				 */
				skillMenu = 19;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 149:
				/*
				 * Hunter
				 */
				skillMenu = 22;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 125:
				/*
				 * Mining
				 */
				skillMenu = 12;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 128:
				/*
				 * Smithing
				 */
				skillMenu = 13;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 131:
				/*
				 * Fishing
				 */
				skillMenu = 14;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 134:
				/*
				 * Cooking
				 */
				skillMenu = 15;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 137:
				/*
				 * Firemaking
				 */
				skillMenu = 16;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 144:
				/*
				 * Woodcutting
				 */
				skillMenu = 17;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 147:
				/*
				 * Farming
				 */
				skillMenu = 20;
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			}
			/*
			 * Skill information interface
			 */
			player.getActionSender().sendInterface(499);

			if (skillMenu != -1) {
				player.setTemporaryAttribute("skillMenu", skillMenu);
			}
			break;
		case 499:
			/*
			 * Skill information.
			 */
			skillMenu = -1;
			if (player.getTemporaryAttribute("skillMenu") != null) {
				skillMenu = (Integer) player.getTemporaryAttribute("skillMenu");
			}
			switch (button) {
			case 10:
				player.getActionSender().sendConfig(965, skillMenu);
				break;
			case 11:
				player.getActionSender().sendConfig(965, 1024 + skillMenu);
				break;
			case 12:
				player.getActionSender().sendConfig(965, 2048 + skillMenu);
				break;
			case 13:
				player.getActionSender().sendConfig(965, 3072 + skillMenu);
				break;
			case 14:
				player.getActionSender().sendConfig(965, 4096 + skillMenu);
				break;
			case 15:
				player.getActionSender().sendConfig(965, 5120 + skillMenu);
				break;
			case 16:
				player.getActionSender().sendConfig(965, 6144 + skillMenu);
				break;
			case 17:
				player.getActionSender().sendConfig(965, 7168 + skillMenu);
				break;
			case 18:
				player.getActionSender().sendConfig(965, 8192 + skillMenu);
				break;
			case 19:
				player.getActionSender().sendConfig(965, 9216 + skillMenu);
				break;
			case 20:
				player.getActionSender().sendConfig(965, 10240 + skillMenu);
				break;
			case 21:
				player.getActionSender().sendConfig(965, 11264 + skillMenu);
				break;
			case 22:
				player.getActionSender().sendConfig(965, 12288 + skillMenu);
				break;
			case 23:
				player.getActionSender().sendConfig(965, 13312 + skillMenu);
				break;
			}
			/*
			 * Spinning interface!
			 */
		case 459:
			switch (button) {
			/*
			 * Ropes. (Using Yak hair lols).
			 */
			case 128:// Spin 1.
				Crafting.spin(player, 30, 1, 10814, 954, 25);
				break;
			case 127:// Spin 5.
				Crafting.spin(player, 30, 5, 10814, 954, 25);
				break;
			case 126:// Spin 10.
				Crafting.spin(player, 30, 10, 10814, 954, 25);
				break;
			case 125:// Spin X.
				player.getInterfaceState().openEnterAmountInterface(459, 10814);
				break;
			/*
			 * Crossbow Strings (Using Sinew).
			 */
			case 114:// Spin 1.
				Crafting.spin(player, 10, 1, 9436, 9438, 15);
				break;
			case 113:// Spin 5.
				Crafting.spin(player, 10, 5, 9436, 9438, 15);
				break;
			case 112:// Spin 10.
				Crafting.spin(player, 10, 10, 9436, 9438, 15);
				break;
			case 111:// Spin X.
				player.getInterfaceState().openEnterAmountInterface(459, 9436);
				break;
			/*
			 * Crossbow Strings (Using tree roots). This is messy, because you
			 * can use different raw materials to create a crossbow string. :(
			 */
			case 121:// Spin 1.
				if (player.getInventory().contains(6043)) {
					Crafting.spin(player, 10, 1, 6043, 9438, 15);
					break;
				} else if (player.getInventory().contains(6045)) {
					Crafting.spin(player, 10, 1, 6045, 9438, 15);
					break;
				} else if (player.getInventory().contains(6047)) {
					Crafting.spin(player, 10, 1, 6047, 9438, 15);
					break;
				} else if (player.getInventory().contains(6049)) {
					Crafting.spin(player, 10, 1, 6049, 9438, 15);
					break;
				} else if (player.getInventory().contains(6053)) {
					Crafting.spin(player, 10, 1, 6053, 9438, 15);
					break;
				}
				break;
			case 120:// Spin 5.
				if (player.getInventory().contains(6043)) {
					Crafting.spin(player, 10, 5, 6043, 9438, 15);
					break;
				} else if (player.getInventory().contains(6045)) {
					Crafting.spin(player, 10, 5, 6045, 9438, 15);
					break;
				} else if (player.getInventory().contains(6047)) {
					Crafting.spin(player, 10, 5, 6047, 9438, 15);
					break;
				} else if (player.getInventory().contains(6049)) {
					Crafting.spin(player, 10, 5, 6049, 9438, 15);
					break;
				} else if (player.getInventory().contains(6053)) {
					Crafting.spin(player, 10, 5, 6053, 9438, 15);
					break;
				}
				break;
			case 119:// Spin 10.
				if (player.getInventory().contains(6043)) {
					Crafting.spin(player, 10, 10, 6043, 9438, 15);
					break;
				} else if (player.getInventory().contains(6045)) {
					Crafting.spin(player, 10, 10, 6045, 9438, 15);
					break;
				} else if (player.getInventory().contains(6047)) {
					Crafting.spin(player, 10, 10, 6047, 9438, 15);
					break;
				} else if (player.getInventory().contains(6049)) {
					Crafting.spin(player, 10, 10, 6049, 9438, 15);
					break;
				} else if (player.getInventory().contains(6053)) {
					Crafting.spin(player, 10, 10, 6053, 9438, 15);
					break;
				}
				break;
			case 118:// Spin X.
				if (player.getInventory().contains(6043)) {
					player.getInterfaceState().openEnterAmountInterface(459,
							6043);
					break;
				} else if (player.getInventory().contains(6045)) {
					player.getInterfaceState().openEnterAmountInterface(459,
							6045);
					break;
				} else if (player.getInventory().contains(6047)) {
					player.getInterfaceState().openEnterAmountInterface(459,
							6047);
					break;
				} else if (player.getInventory().contains(6049)) {
					player.getInterfaceState().openEnterAmountInterface(459,
							6049);
					break;
				} else if (player.getInventory().contains(6053)) {
					player.getInterfaceState().openEnterAmountInterface(459,
							6053);
					break;
				}
				player.getActionSender().sendMessage(
						"You don't have any tree roots to spin!");
				break;
			/*
			 * Magic strings.
			 */
			case 107: // Spin 1.
				Crafting.spin(player, 19, 1, 6051, 6038, 30);
				break;
			case 106: // Spin 5.
				Crafting.spin(player, 19, 5, 6051, 6038, 30);
				break;
			case 105: // Spin 10.
				Crafting.spin(player, 19, 10, 6051, 6038, 30);
				break;
			case 104: // Spin x.
				player.getInterfaceState().openEnterAmountInterface(459, 6051);
				break;
			/*
			 * Wool.
			 */
			case 100: // Spin 1.
				Crafting.spin(player, 1, 1, 1737, 1759, 2.5);
				break;
			case 99: // Spin 5.
				Crafting.spin(player, 1, 5, 1737, 1759, 2.5);
				break;
			case 98: // Spin 10.
				Crafting.spin(player, 1, 10, 1737, 1759, 2.5);
				break;
			case 97: // Spin X.
				player.getInterfaceState().openEnterAmountInterface(459, 1737);

				break;
			/*
			 * Flax.
			 */
			case 95: // Spin 1.
				Crafting.spin(player, 10, 1, 1779, 1777, 13);
				break;
			case 94: // Spin 5.
				Crafting.spin(player, 10, 5, 1779, 1777, 13);
				break;
			case 93: // Spin 10.
				Crafting.spin(player, 10, 10, 1779, 1777, 13);
				break;
			case 92: // Spin X.
				player.getInterfaceState().openEnterAmountInterface(459, 1779);
				break;
			}
		default:
			logger.info("Unhandled action button: " + button
					+ " on interface: " + interfaceId + ".");
			break;
		}

	}

	private static final boolean setAutoReliating(Player player, int button) {
		if (button >= 24) {
			player.setAutoRetaliating(!player.isAutoRetaliating());
			player.getSettings().refresh(player);
			return true;
		}
		return false;
	}

	/*
	 * private void handleWeaponInterfaceButton(Player player, int buttonId) {
	 * switch (buttonId) { case 2: player.setAttackStyle(1); break; case 3:
	 * player.setAttackStyle(2); break; case 4: player.setAttackStyle(4); break;
	 * case 5: player.setAttackStyle(3); break; case 24: case 25: case 26: case
	 * 27: player.setAutoRetaliating(!player.isAutoRetaliating());
	 * player.getSettings().refresh(player); break; } }
	 */

}
