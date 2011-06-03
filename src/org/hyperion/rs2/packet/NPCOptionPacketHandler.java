package org.hyperion.rs2.packet;

import java.util.Random;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.Dialogue;
import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.content.Shop;
import org.hyperion.rs2.content.DialogueLoader.Emotes;
import org.hyperion.rs2.content.minigames.WarriorsGuild;
import org.hyperion.rs2.content.quest.Quest;
import org.hyperion.rs2.content.quest.QuestHandler;
import org.hyperion.rs2.content.quest.impl.TutorialIsland;
import org.hyperion.rs2.content.skills.Crafting;
import org.hyperion.rs2.content.skills.Fishing;
import org.hyperion.rs2.content.skills.Runecrafting;
import org.hyperion.rs2.content.skills.Slayer;
import org.hyperion.rs2.content.skills.Thieving;
import org.hyperion.rs2.content.skills.Fishing.FishingSpot;
import org.hyperion.rs2.content.traveling.ShipTraveling;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.net.Packet;

public class NPCOptionPacketHandler implements PacketHandler {

	private final int ATTACK_NPC = 241;
	private final int OPTION1 = 81;
	private final int OPTION2 = 138;
	private final int OPTION3 = 114;

	public void handle(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case OPTION1:
			option1(player, packet);
			break;
		case OPTION2:
			option2(player, packet);
			break;
		case OPTION3:
			option3(player, packet);
			break;
		case ATTACK_NPC:
			attackNPC(player, packet);
			break;
		}

	}

	private void option3(final Player player, Packet packet) {
		final int id = packet.getLEShortA() & 0xFFFF;
		if (id < 0 || id >= Constants.MAX_NPCS) {
			return;
		}
		final NPC npc = (NPC) World.getWorld().getNPCs().get(id);
		if (npc == null) {
			return;
		}
		player.getActionSender().sendCloseInterface();
		player.setInteractingEntity(npc);
		npc.setInteractingEntity(player);
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
				if (player.getLocation().isWithinInteractingRange(player, npc,
						1)
						&& player.getInterfaceState().getCurrentInterface() == -1) {
					this.stop();
					switch (npc.getDefinition().getId()) {
					case 2259:// Mage of Zamorak - teleporting to abyss
						Runecrafting.teleToAbyss(player, npc);
						break;
					case 553: // Aubury - teleporting to the Rune Essence..
						Runecrafting.teleportToRuneEssenceMiningArea(player,
								npc);
						break;
					}
				}
				this.setDelay(600);
			}

		});
	}

	private void option2(final Player player, Packet packet) {
		int id = packet.getLEShortA();
		if (id < 0 || id >= Constants.MAX_NPCS) {
			return;
		}
		final NPC npc = (NPC) World.getWorld().getNPCs().get(id);
		if (npc == null) {
			return;
		}
		player.getActionSender().sendCloseInterface();
		System.out.println("NPC Option 2: " + npc.getDefinition().getName()
				+ " " + npc.getDefinition().getId());
		final FishingSpot spot = FishingSpot.getSpot(npc.getDefinition()
				.getId(), 2);
		if (spot == null) {
			player.setInteractingEntity(npc);
		} else {
			player.face(npc.getLocation());
		}
		final int offset = npc.getDefinition().getName().contains("Banker")
				|| npc.getDefinition().getName().contains("banker") ? 2 : 1;
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
				if (player.getLocation().isWithinInteractingRange(player, npc,
						offset)
						&& player.getInterfaceState().getCurrentInterface() == -1) {
					this.stop();
					if (spot != null
							&& System.currentTimeMillis() - player.lastNPCClick > 3000) {
						player.getActionQueue().cancelQueuedActions();
						player.getActionQueue().addAction(
								new Fishing.FishingAction(player, spot, npc
										.getLocation()));
						player.lastNPCClick = System.currentTimeMillis();
						return;
					}
					if (npc.getDefinition().getId() == 804) { // Tanner in
																// Al-Kharid.
						Crafting.sendTanningInterface(player);
					}
					if (ShipTraveling.handleSecondNPCOption(player, npc)) {
						return;
					}
					if (Shop.openShop(player, npc, false)) {
						return;
					}
					if (Thieving.checkForThieving(player, npc)) {
						return;
					}
					if (offset == 2) {// Means it a banker..
						Bank.open(player);
					}
				}
				this.setDelay(600);
			}

		});

	}

	private static final Random r = new Random();

	private static final String[][] RANDOM_DIALOGUES = {
			{ "Hello sir. How are you today?" },
			{ "Great weather, huh?" },
			{ "Aah. Another great day in the wonderfull world of",
					"Project Annihilation." },
			{ "Oh god. What a terrible day!" }, };

	private static final Emotes[] RANDOM_EMOTES = { Emotes.HAPPY, Emotes.HAPPY,
			Emotes.HAPPY, Emotes.SAD };

	private void option1(final Player player, Packet packet) {
		final int index = packet.getShort();
		if (index < 0 || index >= Constants.MAX_NPCS) {
			return;
		}
		final NPC npc = (NPC) World.getWorld().getNPCs().get(index);
		if (npc == null) {
			return;
		}
		player.getActionSender().sendCloseInterface();
		System.out.println("NPC Option 1: " + npc.getDefinition().getName()
				+ " " + npc.getDefinition().getId());
		player.removeTemporaryAttribute("EntranaShipTraveling");
		player.setInteractingEntity(npc);
		npc.setInteractingEntity(player);
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
				if (player.getLocation().isWithinInteractingRange(player, npc,
						1)
						&& player.getInterfaceState().getCurrentInterface() == -1) {
					this.stop();
					FishingSpot spot = FishingSpot.getSpot(npc.getDefinition()
							.getId(), 1);
					if (spot != null) {
						player.getActionQueue().cancelQueuedActions();
						player.getActionQueue().addAction(
								new Fishing.FishingAction(player, spot, npc
										.getLocation()));
						return;
					}
					int id = npc.getDefinition().getId();
					if (id == 952) {
						TutorialIsland.fish(player, npc);
						return;
					}
					if (id == Slayer.CHAELDAR || id == Slayer.DURADEL
							|| id == Slayer.MAZCHNA || id == Slayer.VANNAKA) {
						id = Slayer.TURAEL; // Since the dialogues are the same,
											// this should be fine. ;)
					}
					final DialogueLoader dl = DialogueLoader.forId(id);
					if (dl == null) {
						// TODO: Create random dialogues here..
						switch (id) {
						case 605:
							Shop.openShop(player, npc, false);
							return;
						default:
							int random = r.nextInt(RANDOM_DIALOGUES.length);
							String[] lines = RANDOM_DIALOGUES[random];
							Emotes emote = RANDOM_EMOTES[random];
							DialogueLoader.dialogue(player, npc, emote, lines);
							player.setNextDialogueIds(new int[] { -1 });
							break;
						}
						return;
					}
					switch (id) {
					case 1552:
						if (player.isHasRecievedHolidayItems()) {
							Dialogue d = dl.getDialouges()[6];
							DialogueLoader.dialogue(player, npc,
									d.getEmotion(), d.getLines());
							player.setNextDialogueIds(new int[] { -1 });
							return; // Don't continue the code..
						}
						break;

					}
					if (dl.isQuestRelated()) {
						Quest quest = QuestHandler.getQuest(dl.getQuestId());
						if (quest != null) {
							quest.getDialogueForQuestStage(dl, player, npc);
							return;
						}
						System.out.println("Quest for dialouges were null.");
					} else {
						DialogueLoader.getNextDialogue(player, dl, 0);
					}
					this.stop();
				}
				this.setDelay(600);
			}

		});
	}

	public void attackNPC(final Player player, Packet packet) {
		final int id = packet.getShortA();
		if (id < 0 || id >= Constants.MAX_NPCS) {
			return;
		}
		final NPC npc = (NPC) World.getWorld().getNPCs().get(id);
		if (npc == null) {
			System.out.println("NPC IS NULL!!");
			return;
		}
		if (npc.isDead()) {
			System.out.println("Npc is dead.");
			return;
		}
		player.getActionSender().sendCloseInterface();
		if (npc.getDefinition().isAttackable()) {
			if (!Combat.canAttackNPC(player, npc)) {
				player.getActionSender().sendMessage(
						"This is not mine to attack.");
				player.getWalkingQueue().reset();
				return;
			}
			player.setFollowingEntityIndex(npc.getIndex());
			/*
			 * Check if we're within the correct distance, and stop following.
			 */
			Combat.attack(player, npc);
		} else {
			player.getActionSender().sendMessage("You can't attack this NPC!");
		}
	}

}