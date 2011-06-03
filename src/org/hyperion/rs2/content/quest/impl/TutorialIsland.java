package org.hyperion.rs2.content.quest.impl;

import java.util.Random;

import org.hyperion.rs2.action.impl.MiningAction.Node;
import org.hyperion.rs2.content.Dialogue;
import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.content.DialogueLoader.Type;
import org.hyperion.rs2.content.quest.Quest;
import org.hyperion.rs2.content.quest.QuestHandler;
import org.hyperion.rs2.content.skills.Firemaking;
import org.hyperion.rs2.content.traveling.DoorManager;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.impl.EquipmentContainerListener;
import org.hyperion.rs2.model.container.impl.InterfaceContainerListener;
import org.hyperion.rs2.model.container.impl.WeaponContainerListener;
import org.hyperion.rs2.model.region.Region;

public class TutorialIsland implements Quest {

	public static final int MAX_STAGE = 200;
	public static final int QUEST_INFO_INDEX = 0;
	public static final int MAIN_QUEST_STAGE_INDEX = 0;

	@Override
	public int getConfigId() {
		return 0;
	}

	@Override
	public int getConfigValue() {
		return 0;
	}

	/*
	 * Theres no quest lines for this "quest".
	 */
	@Override
	public String[] getQuestLines(Player player) {
		return new String[] { "" };
	}

	@Override
	public String getQuestName() {
		return "Tutorial Island";
	}

	@Override
	public boolean isFinished(Player player) {
		int[][] stages = player.getQuestInfo();
		return stages[QUEST_INFO_INDEX][0] == MAX_STAGE;
	}

	@Override
	public boolean isStarted(Player player) {
		int[][] stages = player.getQuestInfo();
		return stages[QUEST_INFO_INDEX][0] != 0;
	}

	@Override
	public void dialogueEnded(Player player) {
		int[][] info = player.getQuestInfo();
		switch (info[QUEST_INFO_INDEX][0]) { // quest stage..
		/*
		 * We were just told to move through the door..
		 */
		case 1:
			player.editQuestInfo(TutorialIsland.QUEST_INFO_INDEX,
					TutorialIsland.MAIN_QUEST_STAGE_INDEX, 2); // Hopefully the
																// stage is
																// currently 0,
																// and we set it
																// to 1.
			player.getActionSender().sendObjectHints(
					Location.create(3098, 3107, 130), 3);
			break;
		}

	}

	@Override
	public void handleDialogueActions(Player player,
			DialogueLoader dialogueLoader, int nextDialogueId) {
		System.out.println("HAI MAN " + nextDialogueId + " "
				+ dialogueLoader.getNpcId());
		Dialogue dialogue = null;
		int[][] info = player.getQuestInfo();
		int stage = info[QUEST_INFO_INDEX][0];
		switch (dialogueLoader.getNpcId()) {
		/*
		 * RuneScape guide..
		 */
		case 945:
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendFlashingTab(11);
			player.setCurrentDialogueLoader(dialogueLoader);
			dialogue = dialogueLoader.getDialouges()[6]; // We play dialogue
															// number 6.
			player.setNextDialogueIds(new int[] { -1 }); // We set the next
															// dialogue to
															// nothing...
			break;
		/*
		 * Survival expert
		 */
		case 943:
			switch (nextDialogueId) {
			case 1:
				player.getActionSender().sendCloseInterface();
				if (!player.getInventory().contains(new Item(7156))
						|| !player.getInventory().contains(new Item(1351))) {
					player.getActionSender()
							.sendMessage(
									"The Survival Guide gives you a tinderbox and a bronze axe.");
					player.getInventory().add(new Item(7156)); // Tinderbox
					player.getInventory().add(new Item(1351)); // Bronze axe
					if (stage == 3) {
						player.getActionSender().sendSidebarInterface(3, 149);
						player.getActionSender().sendFlashingTab(3);
						player.editQuestInfo(QUEST_INFO_INDEX,
								MAIN_QUEST_STAGE_INDEX, 4);
					}
				} else {
					player.getActionSender().sendMessage(
							"Use your axe to cut some logs..");
				}
				break;
			case 3:
				player.getActionSender().sendCloseInterface();
				if (!player.getInventory().contains(new Item(6209))) {
					player.getActionSender().sendMessage(
							"The Survival Guide gives you a net.");
					player.getInventory().add(new Item(6209));
					if (stage == 6) {
						player.getActionSender().sendObjectHints(
								Location.create(3102, 3093, 0), 2);
						player.editQuestInfo(QUEST_INFO_INDEX,
								MAIN_QUEST_STAGE_INDEX, 7);
					}
				} else {
					player.getActionSender().sendMessage(
							"Use your axe catch some fish.");
				}
				break;
			}
			break;
		case 942:
			switch (nextDialogueId) {
			case 4:
				player.getActionSender().sendCloseInterface();
				if (player.getInventory().add(new Item(2307))) {
					player.getActionSender()
							.sendMessage(
									"The Master Chef hands over a piece of bread dough.");
					player.getActionSender().sendMessage(
							"Use it with the stove in order to cook it.");
					player.getActionSender().sendObjectHints(
							Location.create(3076, 3081, 50), 5);
				} else {
					player.getActionSender().sendMessage(
							"You don't have enough space in your inventory.");
				}

				break;
			}
			break;
		/*
		 * Quest guide..
		 */
		case 949:
			switch (nextDialogueId) {
			case 1:
				player.getActionSender().sendCloseInterface();
				if (stage == 12) {
					player.getActionSender().sendSidebarInterface(2, 274);
					player.getActionSender().sendFlashingTab(2);
					player.getActionSender().sendMessage(
							"Click the flashing icon to continue..");
					player.getActionSender().sendPlayerHints(-1);
				}
				break;
			case 7:
				player.getActionSender().sendCloseInterface();
				if (stage == 13) {
					player.getActionSender().sendObjectHints(
							Location.create(3088, 3119, 50), 2);
					player.editQuestInfo(QUEST_INFO_INDEX,
							MAIN_QUEST_STAGE_INDEX, 14);
				}
				break;
			}
			break;
		/*
		 * Mining Instructor
		 */
		case 948:
			switch (nextDialogueId) {
			case 3: // Prospect stuff..
				player.getActionSender().sendCloseInterface();
				if (stage == 15) {
					player.getActionSender().sendObjectHints(
							Location.create(3076, 9506, 30), 2);
				}
				break;
			case 7:
				player.getActionSender().sendCloseInterface();
				Item pick = new Item(1265);
				if (!player.getInventory().contains(pick)) {
					player.getInventory().add(pick);
					player.getActionSender()
							.sendMessage(
									"The Mining Instructor hands you a bronze pickaxe.");
				}
				if (stage == 17) {
					player.editQuestInfo(QUEST_INFO_INDEX,
							MAIN_QUEST_STAGE_INDEX, 18);
					player.getActionSender().sendObjectHints(
							Location.create(3076, 9506, 30), 2);
				}
				break;
			case 11:
				player.getActionSender().sendCloseInterface();
				Item hammer = new Item(2347);
				if (!player.getInventory().contains(hammer)) {
					player.getInventory().add(hammer);
					player.getActionSender().sendMessage(
							"The Mining Instructor hands you a hammer.");
				}
				if (stage == 20) {
					player.editQuestInfo(QUEST_INFO_INDEX,
							MAIN_QUEST_STAGE_INDEX, 21);
					player.getActionSender().sendObjectHints(
							Location.create(3083, 9499, 30), 2);
				}
				break;
			}
			break;
		case 944:
			switch (nextDialogueId) {
			case 1:
				player.getActionSender().sendCloseInterface();
				if (stage == 22) {
					InterfaceContainerListener equipmentListener1 = new InterfaceContainerListener(
							player, Equipment.INTERFACE1, 25, 94);
					player.getEquipment().addListener(equipmentListener1);
					player.getEquipment().addListener(
							new EquipmentContainerListener(player));

					InterfaceContainerListener equipmentListener2 = new InterfaceContainerListener(
							player, Equipment.INTERFACE2, 103, 95);
					player.getEquipment().addListener(equipmentListener2);
					player.getEquipment().addListener(
							new EquipmentContainerListener(player));
					player.getEquipment().addListener(
							new WeaponContainerListener(player));

					player.getActionSender().sendFlashingTab(0);
					player.editQuestInfo(QUEST_INFO_INDEX,
							MAIN_QUEST_STAGE_INDEX, 23);
				}
				break;
			case 3:
				player.getActionSender().sendCloseInterface();
				player.getActionSender().sendObjectHints(
						Location.create(3111, 9526, 80), 2);
				break;
			}
			break;
		}
		if (dialogue != null) {
			DialogueLoader.dialogue(player,
					dialogue.getType() == Type.PLAYER ? player : new NPC(
							NPCDefinition.forId(dialogueLoader.getNpcId())),
					dialogue.getEmotion(), dialogue.getLines());
			player.setCurrentDialogueLoader(dialogueLoader);
		}
	}

	@Override
	public void getDialogueForQuestStage(DialogueLoader dl, Player player,
			NPC npc) {
		int[][] info = player.getQuestInfo();
		Dialogue dia = QuestHandler.getDialougeForQuestStage(dl, 0,
				info[QUEST_INFO_INDEX][0]);
		if (dia != null) {
			DialogueLoader.handleQuestDialogue(player, dl, dia);
		} else {
			System.out.println("Stage: " + info[QUEST_INFO_INDEX][0]);
			player.getActionSender().sendMessage(
					"Please follow the tutorial island as you're supposed to."); // As
																					// a
																					// dialogue..,
																					// put
																					// chatbox
																					// interfaces
																					// with
																					// info.
		}

	}

	@Override
	public boolean handleObjectClicking(Player player, int objectId,
			Location loc, int option) {
		System.out.println("Tutorial island object click: " + objectId + " "
				+ option + " " + loc);
		int[][] info = player.getQuestInfo();
		int stage = info[QUEST_INFO_INDEX][0];
		if (option == 100 && stage == 4) { // Means we'd successfully cut a
											// tree..
			player.editQuestInfo(QUEST_INFO_INDEX, MAIN_QUEST_STAGE_INDEX, 5);
			player.getActionSender().sendMessage("Well done!");
			player.getActionSender().sendMessage(
					"Now use your tinderbox with your log to create a fire.");
			player.getActionSender().sendPlayerHints(-1); // Resets the hint
															// icons..
		}
		if (objectId == 3014 || objectId == 3018) {
			if (DoorManager.handleDoor(player, loc, objectId)) {
				System.out.println("Here.. " + stage);
				switch (stage) {
				case 2:
					/*
					 * First door in Tutorial island..
					 */
					if (loc.getX() == 3098 && loc.getY() == 3107) {
						System.out.println("Fail..");
						player.editQuestInfo(QUEST_INFO_INDEX,
								MAIN_QUEST_STAGE_INDEX, 3);
						sendHeadIcon(player, 943);
					}
					break;
				/*
				 * Leaving the chef..
				 */
				case 11:
					if (loc.getX() == 3072 && loc.getY() == 3090 && stage == 11) {
						player.getActionSender().sendSidebarInterface(12, 464);
						player.getActionSender().sendFlashingTab(12);
						player.editQuestInfo(QUEST_INFO_INDEX,
								MAIN_QUEST_STAGE_INDEX, 12);
						player.getActionSender().sendMessage(
								"Click the flashing icon to continue..");
						player.getActionSender().sendPlayerHints(-1);
					}
					break;
				}
				return true;
			}
		}
		return false;
	}

	public static void sendHeadIcon(Player player, int id) {
		for (Region r : World.getWorld().getRegionManager()
				.getSurroundingRegions(player.getLocation())) {
			for (NPC npc : r.getNpcs()) {
				if (npc.getDefinition().getId() == id) {
					player.getActionSender().sendNPCHints(npc.getIndex());
					return;
				}
			}
		}

	}

	/**
	 * Called on login.
	 */
	public static void start(Player player) {
		int[][] info = player.getQuestInfo();
		int stage = info[QUEST_INFO_INDEX][0];
		switch (stage) {
		case 0:
			/*
			 * Means we logged in the first time.
			 */
			if (player.getLocation().equals(Entity.DEFAULT_LOCATION)) {
				player.getActionSender().sendInterface(269);
			}
			sendHeadIcon(player, 945);
			player.getActionSender().sendSidebarInterface(10, 182);
			break;
		case 1:
			sendHeadIcon(player, 945);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			break;
		case 2:
			player.getActionSender().sendObjectHints(
					Location.create(3098, 3107, 130), 3);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			break;
		case 3:
			sendHeadIcon(player, 943); // Survival expert..
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
		case 4:
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendObjectHints(
					Location.create(3100, 3096, 140), 2);
			player.getActionSender().sendMessage(
					"Use your axe to cut down a tree..");
		case 5:
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendMessage("Well done!");
			player.getActionSender().sendMessage(
					"Now use your tinderbox with your log to create a fire.");
			break;
		case 6:
			sendHeadIcon(player, 943); // Survival expert..
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320); // Skills
			break;
		case 7:
		case 8:
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320); // Skills
			if (!player.getInventory().contains(new Item(2514))
					&& player.getInventory().contains(new Item(6209))) {
				player.getActionSender().sendObjectHints(
						Location.create(3102, 3093, 0), 2);
				player.getActionSender().sendMessage(
						"You should fish and cook some shrimp from the pond.");
			} else {
				sendHeadIcon(player, 943); // Survival expert..
			}
			break;
		case 9:
			player.getActionSender().sendObjectHints(
					Location.create(3089, 3091, 50), 2);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320); // Skills
			break;
		case 10:
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320); // Skills
			TutorialIsland.sendHeadIcon(player, 942);
			break;
		case 11:
			player.getActionSender().sendObjectHints(
					Location.create(3073, 3090, 130), 6);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320); // Skills
			player.getActionSender().sendSidebarInterface(13, 239);
			break;
		case 12:
			sendHeadIcon(player, 949); // Quest guide..
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320); // Skills
			player.getActionSender().sendSidebarInterface(13, 239);
			break;
		case 13:
			sendHeadIcon(player, 949); // Quest guide..
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320); // Skills
			player.getActionSender().sendSidebarInterface(13, 239);
			player.getActionSender().sendSidebarInterface(2, 274);
			break;
		case 14:
			player.getActionSender().sendObjectHints(
					Location.create(3088, 3119, 50), 2);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320); // Skills
			player.getActionSender().sendSidebarInterface(13, 239);
			player.getActionSender().sendSidebarInterface(2, 274);
			break;
		case 15:
		case 17:
		case 20:
			sendHeadIcon(player, 948);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320);
			player.getActionSender().sendSidebarInterface(13, 239);
			player.getActionSender().sendSidebarInterface(2, 274);
			break;
		case 16:
		case 18:
			player.getActionSender().sendObjectHints(
					Location.create(3085, 9501, 30), 2);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320);
			player.getActionSender().sendSidebarInterface(13, 239);
			player.getActionSender().sendSidebarInterface(2, 274);
			break;
		case 19:
			player.getActionSender().sendObjectHints(
					Location.create(3080, 9496, 30), 2);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320);
			player.getActionSender().sendSidebarInterface(13, 239);
			player.getActionSender().sendSidebarInterface(2, 274);
			break;
		case 21:
			player.getActionSender().sendObjectHints(
					Location.create(3083, 9499, 30), 2);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320);
			player.getActionSender().sendSidebarInterface(13, 239);
			player.getActionSender().sendSidebarInterface(2, 274);
			break;
		case 22:
			sendHeadIcon(player, 944);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320);
			player.getActionSender().sendSidebarInterface(13, 239);
			player.getActionSender().sendSidebarInterface(2, 274);
			break;
		case 23:
		case 24:
			player.editQuestInfo(QUEST_INFO_INDEX, MAIN_QUEST_STAGE_INDEX, 24);
			player.getActionSender().sendObjectHints(
					Location.create(3111, 9526, 80), 2);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320);
			player.getActionSender().sendSidebarInterface(13, 239);
			player.getActionSender().sendSidebarInterface(2, 274);
			player.getActionSender().sendSidebarInterface(1, 320);
			player.getActionSender().sendSidebarInterface(4, 387);
			break;
		case 25:
			player.getActionSender().sendObjectHints(
					Location.create(3122, 3124, 50), 2);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320);
			player.getActionSender().sendSidebarInterface(13, 239);
			player.getActionSender().sendSidebarInterface(2, 274);
			player.getActionSender().sendSidebarInterface(1, 320);
			player.getActionSender().sendSidebarInterface(4, 387);
			break;
		case 26:
			TutorialIsland.sendHeadIcon(player, 947);
			player.getActionSender().sendSidebarInterface(10, 182);
			player.getActionSender().sendSidebarInterface(11, 261);
			player.getActionSender().sendSidebarInterface(3, 149);
			player.getActionSender().sendSidebarInterface(1, 320);
			player.getActionSender().sendSidebarInterface(13, 239);
			player.getActionSender().sendSidebarInterface(2, 274);
			player.getActionSender().sendSidebarInterface(1, 320);
			player.getActionSender().sendSidebarInterface(4, 387);
			break;
		}

	}

	public static void generateMessage(Player player, String[] strings) {

	}

	public static void creaseFire(final Player player) {
		int[][] info = player.getQuestInfo();
		final int stage = info[QUEST_INFO_INDEX][0];
		Location loc = player.getLocation();
		player.setCanWalk(false);
		player.getWalkingQueue().reset();
		player.getWalkingQueue().addStep(loc.getX() - 1, loc.getY());
		player.getWalkingQueue().finish();
		player.face(loc);
		player.getActionQueue().addAction(
				new Firemaking.FireAction(player, 1511, loc, 40, 2732));
		World.getWorld().submit(new Event(800) {

			@Override
			public void execute() {
				player.setCanWalk(true);
				if (stage == 5) {
					player.getActionSender().sendSidebarInterface(1, 320); // Skills
					player.getActionSender().sendFlashingTab(1);
					player.editQuestInfo(QUEST_INFO_INDEX,
							MAIN_QUEST_STAGE_INDEX, 6);
				}
				this.stop();
			}

		});
	}

	public static void fish(final Player player, NPC npc) {
		int[][] info = player.getQuestInfo();
		final int stage = info[QUEST_INFO_INDEX][0];
		player.face(npc.getLocation());
		/*
		 * If we somehow lost our fishing tool, we stop the action as well. is
		 * moving around, or he walks away.
		 */
		if (!player.getInventory().contains(new Item(6209))) { // The tutorial
																// island net..
			player.getActionSender().sendMessage(
					"You need a fishing net in order to fish at this spot.");
			player.playAnimation(Animation.create(-1));
			return;
		}

		final Item fish = new Item(2514);

		/*
		 * We have sure the player have space for this fish..
		 */
		if (!player.getInventory().hasRoomFor(fish)) {
			player.playAnimation(Animation.create(-1));
			player.getActionSender().sendMessage(
					"You don't have enough space in your inventory.");
			return;
		}
		player.setCanWalk(false);
		player.playAnimation(Animation.create(621));
		World.getWorld().submit(new Event(5000) {

			@Override
			public void execute() {
				player.setCanWalk(true);
				player.getActionSender().sendMessage("You catch some shrimps.");
				player.getInventory().add(fish);
				player.getSkills().addExperience(Skills.FISHING, 10);
				if (stage == 7) {
					player.getActionSender().sendMessage(
							"You should try cooking the shrimps on a fire..");
					player.getActionSender().sendPlayerHints(-1);
				}
				player.playAnimation(Animation.create(-1));
				this.stop();
			}

		});

	}

	public static void cook(final Player player, final boolean fire) {
		int[][] info = player.getQuestInfo();
		final int stage = info[QUEST_INFO_INDEX][0];
		Animation anim = Animation.create(fire ? 897 : 896);
		player.playAnimation(anim);
		World.getWorld().submit(new Event(2400) {

			@Override
			public void execute() {
				if (fire) {
					player.getInventory().remove(new Item(2514));
					if (stage == 7 || new Random().nextBoolean()) {
						if (stage == 7) {
							player.editQuestInfo(QUEST_INFO_INDEX,
									MAIN_QUEST_STAGE_INDEX, 8);
						}
						player.getActionSender().sendMessage(
								"You accidently burn your shrimps.");
						player.getInventory().add(new Item(323));
					} else {
						player.getInventory().add(new Item(315));
						player.getActionSender().sendMessage(
								"You successfully cook some shrimp.");
						if (stage == 8) {
							player.getActionSender().sendObjectHints(
									Location.create(3089, 3091, 50), 5);
							player.editQuestInfo(QUEST_INFO_INDEX,
									MAIN_QUEST_STAGE_INDEX, 9);
						}
					}
				} else {
					// Stove
				}
				this.stop();
			}

		});

	}

	public static void finishedBread(Player player) {
		int[][] info = player.getQuestInfo();
		final int stage = info[QUEST_INFO_INDEX][0];
		if (stage == 10) {
			player.editQuestInfo(QUEST_INFO_INDEX, MAIN_QUEST_STAGE_INDEX, 11);
			player.getActionSender().sendSidebarInterface(13, 239);
			player.getActionSender().sendFlashingTab(13);
			player.getActionSender().sendMessage(
					"Click the flashing icon to continue..");
			player.getActionSender().sendPlayerHints(-1);
		}

	}

	public static void examinedOre(Player player, Node node) {
		int[][] info = player.getQuestInfo();
		final int stage = info[QUEST_INFO_INDEX][0];
		if (stage == 15) {
			if (node.equals(Node.TIN)) {
				player.editQuestInfo(QUEST_INFO_INDEX, MAIN_QUEST_STAGE_INDEX,
						16);
				player.getActionSender().sendObjectHints(
						Location.create(3085, 9501, 30), 2);
			} else {
				player.getActionSender().sendMessage(
						".. I should do it in the order I was told to though.");
			}
		} else if (stage == 16) {
			if (node.equals(Node.COPPER)) {
				player.editQuestInfo(QUEST_INFO_INDEX, MAIN_QUEST_STAGE_INDEX,
						17);
				sendHeadIcon(player, 948);
			} else {
				player.getActionSender().sendMessage(
						".. I should do it in the order I was told to though.");
			}

		}

	}

	public static void mining(Player player, Node node) {
		int[][] info = player.getQuestInfo();
		final int stage = info[QUEST_INFO_INDEX][0];
		if (node.equals(Node.TIN) || node.equals(Node.COPPER) && stage == 18) {
			Item tin = new Item(438);
			Item copper = new Item(436);
			if (player.getInventory().contains(tin)
					&& !player.getInventory().contains(copper)) {
				player.getActionSender().sendObjectHints(
						Location.create(3085, 9501, 30), 2);
			} else if (!player.getInventory().contains(tin)
					&& player.getInventory().contains(copper)) {
				player.getActionSender().sendObjectHints(
						Location.create(3076, 9506, 30), 2); // Its already
																// there in the
																// theory..
			} else {// Contains both
				player.editQuestInfo(QUEST_INFO_INDEX, MAIN_QUEST_STAGE_INDEX,
						19);
				player.getActionSender().sendObjectHints(
						Location.create(3080, 9496, 30), 2);
			}
		}
	}

	public static void smeltedBar(Player player, Item resultBar) {
		Item bronze = new Item(2349); // bar
		int[][] info = player.getQuestInfo();
		final int stage = info[QUEST_INFO_INDEX][0];
		if (player.getInventory().contains(bronze) && stage == 19) {
			player.editQuestInfo(QUEST_INFO_INDEX, MAIN_QUEST_STAGE_INDEX, 20);
			sendHeadIcon(player, 948);
		}

	}

	public static void itemSmith(Player player, Item result) {
		int[][] info = player.getQuestInfo();
		final int stage = info[QUEST_INFO_INDEX][0];
		Item dagger = new Item(1205);
		if (player.getInventory().contains(dagger) && stage == 21) {
			player.editQuestInfo(QUEST_INFO_INDEX, MAIN_QUEST_STAGE_INDEX, 22);
			sendHeadIcon(player, 944); // Hopefully combat instructor
		}

	}

}
