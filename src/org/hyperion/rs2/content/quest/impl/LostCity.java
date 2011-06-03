package org.hyperion.rs2.content.quest.impl;

import java.util.Random;

import org.hyperion.rs2.action.impl.WoodcuttingAction.Axe;
import org.hyperion.rs2.content.Dialogue;
import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.content.DialogueLoader.Type;
import org.hyperion.rs2.content.quest.Quest;
import org.hyperion.rs2.content.quest.QuestHandler;
import org.hyperion.rs2.content.traveling.DoorManager;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Inventory;

public class LostCity implements Quest {

	public static final int QUEST_INFO_INDEX = 4;
	public static final int MAIN_QUEST_STAGE_INDEX = 0;
	private static final int MAXIMUM_STAGE = 4;
	private static final int QUEST_ID = 83;

	public static final int[] LOST_CITY_WEARABLE_ITEMS = { 1215, 1231, 5680,
			5698, /* Dragon daggers */1305, /* Dragon longsword */
	};

	private static final String[][] QUEST_LINES = {
			{ "I can start this quest by speaking to the adventures in",
					"the swamp just south of Lumbridge.",
					"To complete this quest I need: ", "Level 31 Crafting",
					"Level 36 Woodcutting",
					"and be able to defeat a Level 101 Sprit without weapons." },
			{ "The big fat Warrior told me about some leprechaun hiding",
					"in a tree around the camp fire. I should look around,",
					"and see what I can find." },
			{
					"<str>The big fat Warrior told me about some leprechaun hiding",
					"<str>in a tree around the camp fire. I should look around,",
					"<str>and see what I can find.",
					"So, the leprechaun from the tree told me alot about Sanaris.",
					"He said it was hidden in the shed in the Lumbridge swamp,",
					"but the shed was like a portal. The key to the portal",
					"is a dramen staff, which can be made from a",
					"Dramen branch. I can cut those from a tree",
					"which is found in a cave on the island Entrana." },
			{
					"<str>The big fat Warrior told me about some leprechaun hiding",
					"<str>in a tree around the camp fire. I should look around,",
					"<str>and see what I can find.",
					"<str>So, the leprechaun from the tree told me alot about Sanaris.",
					"<str>He said it was hidden in the shed in the Lumbridge swamp,",
					"<str>but the shed was like a portal. The key to the portal",
					"<str>is a dramen staff, which can be made from a",
					"<str>Dramen branch. I can cut those from a tree",
					"<str>which is found in a cave on the island Entrana.",
					"After killing the tree sprit, I should fletch a Dramen staff",
					"from the branch of the tree, and enter",
					"the shed in the Lumbridge swamp!" },
			{
					"<str>The big fat Warrior told me about some leprechaun hiding",
					"<str>in a tree around the camp fire. I should look around,",
					"<str>and see what I can find.",
					"<str>So, the leprechaun from the tree told me alot about Sanaris.",
					"<str>He said it was hidden in the shed in the Lumbridge swamp,",
					"<str>but the shed was like a portal. The key to the portal",
					"<str>is a dramen staff, which can be made from a",
					"<str>Dramen branch. I can cut those from a tree",
					"<str>which is found in a cave on the island Entrana.",
					"<str>After killing the tree sprit, I should fletch a Dramen staff",
					"<str>from the branch of the tree, and enter",
					"<str>The shed in the lumbridge swamp!",
					"<col=00EE00>         QUEST COMPLETE!",
					"<col=00008B>	I gained 3 Quest point, access to Zanaris and",
					"<col=00008B>the ability to wear dragon longswords/daggers." },
			{ "" }, { "" }, { "" }, };

	@Override
	public void dialogueEnded(Player player) {
		if (player.getTemporaryAttribute("Shamus") != null) { // The kid from
																// lost city..
			/*
			 * When we're done talking to shamus, we increase the stage to 2,
			 * when we click continue, shamus will be removed.
			 */
			if (player.getQuestInfo()[LostCity.QUEST_INFO_INDEX][LostCity.MAIN_QUEST_STAGE_INDEX] == 1) {
				NPC shamus = (NPC) player.getTemporaryAttribute("Shamus");
				shamus.destroy();
				World.getWorld().getNPCs().remove(shamus);
				player.removeTemporaryAttribute("Shamus");
				player.getActionSender().sendChatboxInterface(101);
				player.getActionSender().sendString(
						"The leprechaun magically disappears.", 101, 1);
				player.editQuestInfo(QUEST_INFO_INDEX, MAIN_QUEST_STAGE_INDEX,
						2);// we increase the stage.
			}
		}
	}

	@Override
	public int getConfigId() {
		return 147;
	}

	@Override
	public int getConfigValue() {
		return 6;
	}

	@Override
	public void getDialogueForQuestStage(DialogueLoader dl, Player player,
			NPC npc) {
		if (npc.getDefinition().getId() == 654) {
			if (player.getTemporaryAttribute("Shamus") == null) {
				player.getActionSender().sendMessage(
						"He doesn't seem to be interested in you.");
				return;
			}
			NPC shamus = (NPC) player.getTemporaryAttribute("Shamus");
			if (npc != shamus) {
				player.getActionSender().sendMessage(
						"He doesn't seem to be interested in you.");
				return;
			}
		}
		int[][] info = player.getQuestInfo();
		DialogueLoader.handleQuestDialogue(player, dl, QuestHandler
				.getDialougeForQuestStage(dl, QUEST_ID,
						info[QUEST_INFO_INDEX][0]));
	}

	@Override
	public String[] getQuestLines(Player player) {
		return QUEST_LINES[player.getQuestInfo()[QUEST_INFO_INDEX][MAIN_QUEST_STAGE_INDEX]];
	}

	@Override
	public String getQuestName() {
		return "Lost City";
	}

	@Override
	public void handleDialogueActions(Player player,
			DialogueLoader dialogueLoader, int nextDialogueId) {
		Dialogue dialogue = null;
		switch (nextDialogueId) {
		/*
		 * We were just told about the leprechaun hiding in a tree, and we start
		 * the quest.
		 */
		case 13:
			player.editQuestInfo(QUEST_INFO_INDEX, MAIN_QUEST_STAGE_INDEX, 1); // Hopefully
																				// the
																				// stage
																				// is
																				// currently
																				// 0,
																				// and
																				// we
																				// set
																				// it
																				// to
																				// 1.
			dialogue = dialogueLoader.getDialouges()[14]; // We play dialogue
															// number 14.
			player.setNextDialogueIds(new int[] { 15 }); // We set the next
															// dialogue to 15.
			// We're using arrays, and you have to do new int[]{ID}, because of
			// the Option support.
			player.getActionSender().sendConfig(getConfigId(), 1);// Makes the
																	// quest tab
																	// light
																	// yellow.
			break;
		// When you handle actions like that, no other dialogue is send, and you
		// have to configure that manually..
		//
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
	public boolean isFinished(Player player) {
		int[][] stages = player.getQuestInfo();
		return stages[QUEST_INFO_INDEX][MAIN_QUEST_STAGE_INDEX] == MAXIMUM_STAGE;
	}

	@Override
	public boolean isStarted(Player player) {
		int[][] stages = player.getQuestInfo();
		return stages[QUEST_INFO_INDEX][MAIN_QUEST_STAGE_INDEX] != 0;
	}

	/**
	 * Called every time you click the tree with the leprechaun in.
	 * 
	 * @param player
	 *            The player who's clicking it.
	 */
	private static void handleLeprechaunTree(final Player player) {
		if (player.getTemporaryAttribute("Shamus") == null) {
			int stage = player.getQuestInfo()[QUEST_INFO_INDEX][MAIN_QUEST_STAGE_INDEX];
			if (stage == 1) {// We were just told about the tree.
				final NPC shamus = new NPC(NPCDefinition.forId(654));
				shamus.setLocation(player.getLocation());
				World.getWorld().getNPCs().add(shamus);
				player.setTemporaryAttribute("Shamus", shamus);
				World.getWorld().submit(new Event(120000) {// Removes Shamus
															// after two
															// minutes, if the
															// player just
															// walked away, and
															// didn't finish the
															// dialogue.

							@Override
							public void execute() {
								if (shamus != null) {
									shamus.destroy();
									World.getWorld().getNPCs().remove(shamus);
									player.removeTemporaryAttribute("Shamus");
								}
								this.stop();
							}

						});
				// TODO: Randomly walk around before they start talking..
			} else { // Nothing else should happen.
				player.getActionSender().sendMessage(
						"Nothing interesting happens.");
			}
		}

	}

	private static void enterEntranaDungeon(Player player) {
		int stage = player.getQuestInfo()[QUEST_INFO_INDEX][MAIN_QUEST_STAGE_INDEX];
		if (stage >= 2) {
			for (NPC npc : player.getRegion().getNpcs()) {
				if (npc.getDefinition().getId() == 656) { // Cavemonk.
					npc.face(player.getLocation());
					final DialogueLoader dl = DialogueLoader.forId(656);
					DialogueLoader.getNextDialogue(player, dl, 0);
					break;
				}
			}
		} else {
			player.getActionSender().sendMessage(
					"I don't have any reason to enter this dungeon.");
		}

	}

	private static final Random RANDOM = new Random();
	private static final Animation RESET = Animation.create(-1);

	private static void handleDramenTree(final Player player) {
		Axe a = null;
		final int wc = player.getSkills().getLevel(Skills.WOODCUTTING);
		for (Axe axe : Axe.values()) {
			if ((player.getEquipment().contains(axe.getId()) || player
					.getInventory().contains(axe.getId()))
					&& wc >= axe.getRequiredLevel()) {
				a = axe;
				break;
			}
		}
		if (a == null) {
			player.getActionSender().sendMessage(
					"You do not have an axe that you can use.");
			return;
		}
		int stage = player.getQuestInfo()[QUEST_INFO_INDEX][MAIN_QUEST_STAGE_INDEX];
		if (stage < 2) {
			player.getActionSender().sendMessage(
					"I have no reason what so ever to harm this tree.");
			return;
		}
		if (stage == 2) {
			if (player.getTemporaryAttribute("Sprit Tree") == null) {
				final NPC treeSprit = new NPC(NPCDefinition.forId(655));
				treeSprit.setLocation(player.getLocation());
				World.getWorld().getNPCs().add(treeSprit);
				player.setTemporaryAttribute("Sprit Tree", treeSprit);
				World.getWorld().submit(new Event(600) {

					private int timeOut = 60000 * 5;

					@Override
					public void execute() {
						/*
						 * If 5 minutes passed by or the player is more than 10
						 * squares away, we remove the NCP, the attribute, stop
						 * the event, and return.
						 */
						if (--timeOut == 0
								|| treeSprit.getLocation().getDistance(
										player.getLocation()) > 10) {
							player.removeTemporaryAttribute("Sprit Tree");
							treeSprit.destroy();
							World.getWorld().getNPCs().remove(treeSprit);
							this.stop();
							return;
						}
						/*
						 * If the Sprit tree is dead, we remove the temporary
						 * attribute, Send a message, and increase our quest
						 * stage.
						 */
						if (treeSprit.isDead()) {
							player.editQuestInfo(QUEST_INFO_INDEX,
									MAIN_QUEST_STAGE_INDEX, 3); // We set the
																// players quest
																// stage to 3.
							player.removeTemporaryAttribute("Sprit Tree");
							player.getActionSender().sendChatboxInterface(101);
							player.getActionSender()
									.sendString(
											"With the Tree Sprit defeated, you can now chop the tree.",
											101, 1);
							treeSprit.destroy();
							World.getWorld().getNPCs().remove(treeSprit);
							this.stop();
							return;
						}
						if (RANDOM.nextInt(4) == 0) {
							treeSprit.playAnimation(Animation.create(5535));
							// ProjectileManager.f
						}
						/*
						 * If timeOut is 10, 20, 30, 40 etc, we send the force
						 * text.
						 */
						if (timeOut % 10 == 0) {
							treeSprit
									.forceChat("You must defeat me before touching the tree!");
						}

					}

				});
			} else {
				player.getActionSender().sendMessage(
						"You should defeat the Tree Sprit first.");
			}
			return;
		}
		player.playAnimation(a.getAnimation());
		World.getWorld().submit(new Event(RANDOM.nextInt(3) * 1000) {

			@Override
			public void execute() {
				if (Inventory.addInventoryItem(player, new Item(771))) {
					player.getActionSender().sendMessage(
							"You cut a branch from the Dramen tree.");
				}
				player.playAnimation(RESET);
				this.stop();
			}

		});

	}

	private static final Location LOST_CITY_TREE = Location.create(3138, 3212,
			0);
	private static final Location LOST_CITY_ENTRANA_LADDER = Location.create(
			2820, 3374, 0);
	private static final Location LOST_CITY_DRAMEN_TREE = Location.create(2860,
			9734, 0);
	private static final Location LOST_CITY_SHED_ENTRANCE = Location.create(
			3202, 3169, 0);
	private static final Location LOST_CITY_SHED_ENTRANCE2 = Location.create(
			3201, 3169, 0);
	private static final Location LOST_CITY_MAGIC_DOOR = Location.create(2874,
			9750, 0);

	/*
	 * Every time you click an object, this method is called for all quests.
	 */
	@Override
	public boolean handleObjectClicking(final Player player, int objectId,
			Location loc, int option) {
		// System.out.println("Handle object clicking.");
		if (loc.equals(LOST_CITY_SHED_ENTRANCE) && objectId == 2406) {
			DoorManager.handleDoor(player, loc, objectId);
			final int stage = player.getQuestInfo()[QUEST_INFO_INDEX][MAIN_QUEST_STAGE_INDEX];
			if (stage >= 3 && player.getEquipment().contains(772)) {
				player.lastObjectClick = System.currentTimeMillis() + 2000; // Sets
																			// it
																			// so
																			// you
																			// can't
																			// click
																			// an
																			// object
																			// for
																			// the
																			// next
																			// two
																			// seconds.
				World.getWorld().submit(new Event(4000) {
					@Override
					public void execute() {
						player.getActionSender().sendMessage(
								"The world starts to shimmer..");
						player.setTeleportTarget(Location.create(2452, 4473, 0));
						if (stage == 3) {
							player.getActionSender().sendConfig(getConfigId(),
									getConfigValue());
							player.editQuestInfo(QUEST_INFO_INDEX,
									MAIN_QUEST_STAGE_INDEX, 4); // We set the
																// players quest
																// stage to 4,
																// and the quest
																// is finished.
							player.getActionSender().sendMessage(
									"Congratulations! Quest complete!");
							player.getActionSender().sendInterface(277);
							player.getActionSender()
									.sendString(
											"You have completed the Lost City of Zanaris Quest!",
											277, 2);
							player.getActionSender().sendString(
									"3 Quest Points", 277, 8);
							player.getActionSender().sendString(
									"Access to Zanaris", 277, 9);
							player.setQuestPoints(player.getQuestPoints() + 3);
							for (int i = 10; i < 15; i++) { // Clears the rest
															// of the interface.
								player.getActionSender().sendString("", 277, i);
							}
							player.getActionSender().sendString(
									"" + player.getQuestPoints(), 277, 5);
							player.getActionSender().sendInterfaceModel(277,
									17, 400, 772);
						}
						this.stop();
					}

				});
			}
			return true;
		}
		if (loc.equals(LOST_CITY_MAGIC_DOOR) && objectId == 2407) {
			player.getActionSender().sendMessage("The door seems to be stuck.");
			// Lmao ownt kids.
			return true;
		}
		if (loc.equals(LOST_CITY_TREE) && objectId == 2409) {
			handleLeprechaunTree(player);
			return true;
		}
		if (loc.equals(LOST_CITY_ENTRANA_LADDER) && objectId == 2408) {
			enterEntranaDungeon(player);
			return true;
		}
		if (loc.equals(LOST_CITY_DRAMEN_TREE) && objectId == 1292) {
			handleDramenTree(player);
			return true;
		}
		return false;
	}

}