package org.hyperion.rs2.model;

/**
 * Represents a single chat message.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ChatMessage {

	/**
	 * The effects.
	 */
	private int effects;

	/**
	 * The numChars.
	 */
	private int numChars;

	/**
	 * The chat text.
	 */
	private String chatText;

	/**
	 * The packed chat text.
	 */

	/**
	 * Creates a new chat message.
	 * 
	 * @param colour
	 *            The message colour.
	 * @param effects
	 *            The message effects.
	 * @param text
	 *            The packed chat text.
	 */
	public ChatMessage(int effects, int numChars, String chatText) {
		this.effects = effects;
		this.numChars = numChars;
		this.chatText = chatText;
	}

	/**
	 * Gets the message effects.
	 * 
	 * @return The message effects.
	 */
	public int getEffects() {
		return effects;
	}

	/**
	 * Gets the number of chars.
	 * 
	 * @return The number of chars.
	 */
	public int getNumChars() {
		return numChars;
	}

	/**
	 * Gets the chat text.
	 * 
	 * @return The chat text.
	 */
	public String getChatText() {
		return chatText;
	}
}
