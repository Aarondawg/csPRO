package org.hyperion.rs2.content;

import org.hyperion.rs2.content.DialogueLoader.Emotes;
import org.hyperion.rs2.content.DialogueLoader.Type;

/**
 * Represents a dialogue.
 * 
 * @author Brown.
 */

public class Dialogue {

	public Dialogue(int id, Type type, String[] lines, Emotes emote,
			int[] dialogueIds, int questStage) {
		this.id = id;
		this.type = type;
		this.lines = lines;
		this.emotion = emote;
		this.nextDialougeId = dialogueIds;
		this.questStage = questStage;
	}

	/**
	 * The dialogue id.
	 */
	private final int id; // Its more smart, becaue it checks this aswell..

	/**
	 * The Dialogue type, PLAYER, NPC or OPTION.
	 */
	private final Type type;

	/**
	 * The lines we want to send.
	 */
	private final String[] lines;

	/**
	 * The emotion of the talker.
	 */
	private final Emotes emotion;

	/**
	 * If you're type is option, you don't have to use the "emotion", but you
	 * have to use this, to get the next dialouge id.
	 */
	private final int[] nextDialougeId;

	/**
	 * Used to get the first dialogue of a dialogue set for a quest.
	 */
	private final int questStage;

	/**
	 * The length of this is 1, unless we're dealing with Type OPTION.
	 * 
	 * @return the nextDialougeId array..
	 */
	public int[] getNextDialougeIds() {
		return nextDialougeId;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the lines
	 */
	public String[] getLines() {
		return lines;
	}

	public Emotes getEmotion() {
		return emotion;
	}

	public int getQuestStage() {
		return questStage;
	}

}