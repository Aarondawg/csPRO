package org.hyperion.rs2.content;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.content.quest.QuestHandler;
import org.hyperion.rs2.content.quest.impl.LostCity;
import org.hyperion.rs2.content.skills.Slayer;
import org.hyperion.rs2.content.traveling.ShipTraveling;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.XStreamUtil;

public class DialogueLoader {

	private static final Logger logger = Logger.getLogger(DialogueLoader.class
			.getName());

	// You see, XML is smart.
	// It checks up this class.. Because of this class.
	private int npcId; // And it sees this guy, and it knows its an integer
	private int questId = -1;// And this one..
	private Dialogue[] dialouges; // And it sees this as a dialogue array..
	// Which means we have to put alot of dialogues in ths dialogue array.
	private static Map<Integer, DialogueLoader> dialougeSet = new HashMap<Integer, DialogueLoader>();

	@SuppressWarnings("unchecked")
	public static void init() throws FileNotFoundException {
		List<DialogueLoader> list = (List<DialogueLoader>) XStreamUtil
				.getXStream()
				.fromXML(new FileInputStream("data/dialouges.xml"));
		for (DialogueLoader dialouge : list) {
			dialougeSet.put(dialouge.getNpcId(), dialouge);
		}
		logger.info("Loaded " + dialougeSet.size() + " dialogue sets.");
	}

	public int getNpcId() {
		return npcId;
	}

	public static DialogueLoader forId(int id) {
		return dialougeSet.get(id);
	}

	/**
	 * Used to handle the dialogues when you click a quest related NPC. - Well
	 * the first one.
	 * 
	 */
	public static void handleQuestDialogue(Player player, DialogueLoader dl,
			Dialogue dialogue) {
		assert (dialogue != null);
		switch (dialogue.getType()) {
		case OPTION:
			options(player, dialogue.getLines());
			break;
		case PLAYER:
			dialogue(player, player, dialogue.getEmotion(), dialogue.getLines());
			break;
		case NPC:
			dialogue(player, new NPC(NPCDefinition.forId(dl.npcId)),
					dialogue.getEmotion(), dialogue.getLines());
			break;
		default:
			System.out.println("You fucked up somehow - dialogueloader.");
		}
		player.setNextDialogueIds(dialogue.getNextDialougeIds());
		player.setCurrentDialogueLoader(dl);
	}

	/**
	 * After one dialouge is send, and you click the "Click here to continue"
	 * The next possible dialouge should be handled here.
	 */
	public static void getNextDialogue(Player player, DialogueLoader dl,
			int nextDialogueId) {
		System.out.println("nextDialogueId: " + nextDialogueId);
		if (nextDialogueId == -1) {// Means close the window.
			player.getActionSender().sendCloseInterface();
			return;
		}
		if (nextDialogueId == -2) {// Means close the window, and do a quest
									// action.
			player.getActionSender().sendCloseInterface();
			if (dl.isQuestRelated()) {
				QuestHandler.getQuest(dl.getQuestId()).dialogueEnded(player);
			} else {
				System.out
						.println("(-2) is used wrong in the dialogue system.");
			}
			return;
		}
		Dialogue dialogue = dl.getDialouges()[nextDialogueId];
		assert (dialogue != null);
		switch (dialogue.getType()) {
		case OPTION:
			options(player, dialogue.getLines());
			break;
		case PLAYER:
			// If its just a regulair player or npc type, it will go in here.
			// And everything is done automaticly, by the shit from the object.
			dialogue(player, player, dialogue.getEmotion(), dialogue.getLines());
			break;
		case NPC:
			dialogue(player, new NPC(NPCDefinition.forId(dl.npcId)),
					dialogue.getEmotion(), dialogue.getLines());
			break; // We break, and get to the button..
		case QUEST:
			QuestHandler.getQuest(dl.getQuestId()/*
												 * The id from the top of the
												 * dialogue
												 */).handleDialogueActions(
					player, dl, nextDialogueId);
			return; // We return, and get the fuck out of the method, nothing
					// else should be done in here.
		case SLAYER:
			switch (dl.getNpcId()) {
			/*
			 * This first part handles all Slayer dialogues.
			 */
			case Slayer.TURAEL:
			case Slayer.CHAELDAR:
			case Slayer.MAZCHNA:
			case Slayer.VANNAKA:
				switch (nextDialogueId) {
				case 2:// I need another assignment.
					if (player.getSlayerTask().hasTask()) {
						dialogue(
								player,
								new NPC(NPCDefinition.forId(dl.npcId)),
								Emotes.DEFAULT,
								new String[] {
										"You are still hunting "
												+ player.getSlayerTask()
														.getName() + ".",
										"Come back when you've finished your task." });
						player.setNextDialogueIds(new int[] { -1 });
					} else {
						dialogue(player,
								new NPC(NPCDefinition.forId(dl.npcId)),
								Emotes.DEFAULT,
								Slayer.getTaskDialogue(player, dl.getNpcId()));
						player.setNextDialogueIds(new int[] { 3 });
					}
					break;
				case 5:// Do you have any tips for me?
					dialogue(player, dialogue.getType() == Type.PLAYER ? player
							: new NPC(NPCDefinition.forId(dl.npcId)),
							Emotes.DEFAULT, Slayer.getTips(player));
					player.setNextDialogueIds(new int[] { 6 });
					break;
				case 8:// Do you have anything for trade?
					Shop.openShop(player,
							new NPC(NPCDefinition.forId(dl.npcId)), false);
					break;
				}
				player.setCurrentDialogueLoader(dl);
				return;
			}
			return;
		case SHOP:
			Shop.openShop(player, new NPC(NPCDefinition.forId(dl.getNpcId())),
					true);
			return;
		case BANK:
			Bank.open(player);
			return;
		case SHIP:
			ShipTraveling.handleDialogueShipTraveling(player, dl.getNpcId(),
					nextDialogueId);
			return;
		case WARRIORS_GUILD:
			player.getWarriorsGuild().dialogueFinished();
			return;
		case OTHER:
			switch (dl.getNpcId()) {
			/*
			 * Entrana monk for Lost City quest.
			 */
			case 656:
				int stage = player.getQuestInfo()[LostCity.QUEST_INFO_INDEX][LostCity.MAIN_QUEST_STAGE_INDEX];
				if (stage >= 2) {
					player.setTeleportTarget(Location.create(2822, 9772, 0));
					player.getActionSender().sendMessage(
							"You climb down the ladder.");
				} else {
					player.getActionSender().sendMessage(
							"I don't have any reason to enter this dungeon.");
				}
				player.getActionSender().sendCloseInterface();
				break;
			/*
			 * Santa Clause
			 */
			case 1552:
				int total = player.getSkills().getTotalLevel();
				if (total >= 300) {
					Dialogue d = dl.getDialouges()[5];
					dialogue(player, new NPC(NPCDefinition.forId(dl.npcId)),
							d.getEmotion(), d.getLines());
					player.setNextDialogueIds(new int[] { -1 });
					player.getInventory().add(new Item(962));
					player.setHasRecievedHolidayItems(true);
				} else {
					Dialogue d = dl.getDialouges()[4];
					dialogue(player, new NPC(NPCDefinition.forId(dl.npcId)),
							d.getEmotion(), d.getLines());
					player.setNextDialogueIds(new int[] { -1 });
				}
				player.setCurrentDialogueLoader(dl);
				return;
				/*
				 * The squire in Pest Control.
				 */
			case 3781:
				PestControl.exit(player);
				break;
			}
			return;
		default:
			System.out.println("You fucked up somehow - dialogueloader.");
		}
		player.setNextDialogueIds(dialogue.getNextDialougeIds()); // We set the
																	// next
																	// dialogue
																	// ids..
		player.setCurrentDialogueLoader(dl);
	}

	/**
	 * Use this for only one line of code to make a dialouge.
	 * 
	 * @param player
	 *            The player the interfaces/configurations is send for.
	 * @param talker
	 *            The actual talker, this could be the player or an npc.
	 * @param emote
	 *            The emotion we want the talker to perform.
	 * @param lines
	 *            The lines we want our talker to preach.
	 */
	public static void dialogue(Player player, Entity talker, Emotes emote,
			String[] lines) {
		boolean talkerIsPlayer = (talker instanceof Player);
		String name = NameUtils.formatName(talkerIsPlayer ? player.getName()
				: ((NPC) talker).getDefinition().getName());

		/*
		 * This looks amazingly funky, but it actually works rofl.
		 */
		int interfaceId = interfaces[talkerIsPlayer ? 1 : 2][lines.length - 1];
		player.getActionSender().sendChatboxInterface(interfaceId);
		player.getActionSender().animateInterfaceId(emote.getId(), interfaceId,
				0);
		if (talkerIsPlayer) {
			player.getActionSender().sendPlayerHead(interfaceId, 0);
		} else {
			player.getActionSender().sendNPCHead(
					((NPC) talker).getDefinition().getId(), interfaceId, 0);
		}
		player.getActionSender().sendString(name, interfaceId, 1);
		for (int i = 0; i < lines.length; i++) {
			lines[i] = lines[i].replace("playerName",
					NameUtils.formatName(player.getName()));
			player.getActionSender().sendString(lines[i], interfaceId, i + 2);
		}
	}

	public static void options(Player player, String[] lines) {
		/*
		 * This looks amazingly funky, but it actually works rofl.
		 */
		int interfaceId = interfaces[0][lines.length - 2];
		player.getActionSender().sendChatboxInterface(interfaceId);
		for (int i = 0; i < lines.length; i++) {
			player.getActionSender().sendString(lines[i], interfaceId, i + 1);
		}
	}

	public Dialogue[] getDialouges() {
		return dialouges;
	}

	public int getQuestId() {
		return questId;
	}

	public boolean isQuestRelated() {
		return questId != -1;
	}

	/**
	 * Thanks Dust Rip.
	 */
	public static enum Emotes {
		HAPPY(588), // - Joyful/happy
		CALM1(589), // - Speakingly calmly
		CALM2(590), // - Calm talk
		DEFAULT(591), // - Default speech
		EVIL(592), // - Evil
		EVILCONTINUED(593), // - Evil continued
		DELIGHTEDEVIL(594), // - Delighted evil
		ANNOYED(595), // - Annoyed
		DISTRESSED(596), // - Distressed
		DISTRESSEDCONTINUED(597), // - Distressed continued
		ALMOSTCRYING(598), // - Almost crying
		BOWSHEADWHILESAD(599), // - Bows head while sad
		DRUNKTOLEFT(600), // - Talks and looks sleepy/drunk to left
		DRUNKTORIGHT(601), // - Talks and looks sleepy/drunk to right
		DISINTERESTED(602), // - Sleepy or disinterested
		SPEELY(603), // - Tipping head as if sleepy.
		PLAINEVIL(604), // - Plain evil (Grits teeth and moves eyebrows)
		LAUGH1(605), // - Laughing or yawning
		LAUGH2(606), // - Laughing or yawning for longer
		LAUGH3(607), // - Laughing or yawning for longer
		LAUGH4(608), // - Laughing or yawning
		EVILLAUGH(609), // - Evil laugh then plain evil
		SAD(610), // - Slightly sad
		MORESAD(611), // - Quite sad
		ONONEHAND(612), // - On one hand...
		NEARLYCRYING(613), // - Close to crying
		ANGER1(614), // - Angry
		ANGER2(615), // - Angry
		ANGER3(616), // - Angry
		ANGER4(617); // - Angry

		private int id;

		private Emotes(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}
	}

	public static enum Type {
		NPC, PLAYER, OPTION, QUEST, SHOP, BANK, SLAYER, SHIP, WARRIORS_GUILD, OTHER,
	}

	private static int[][] interfaces = { { 228, 230, 232, 234 }, // Options
			{ 64, 65, 66, 67 }, // Players
			{ 241, 242, 243, 244 }, // Npcs
	};

}
