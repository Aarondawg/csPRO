package org.hyperion.rs2.model;

/**
 * Contains client-side settings.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Settings {

	/**
	 * Received starter items.
	 */
	private boolean hasReceivedtarterItems = false;

	/**
	 * Withdraw as notes flag.
	 */
	private boolean withdrawAsNotes = false;

	/**
	 * Swapping flag.
	 */
	private boolean swapping = true;

	/**
	 * Sets the withdraw as notes flag.
	 * 
	 * @param withdrawAsNotes
	 *            The flag.
	 */
	public void setWithdrawAsNotes(boolean withdrawAsNotes) {
		this.withdrawAsNotes = withdrawAsNotes;
	}

	/**
	 * Sets the swapping flag.
	 * 
	 * @param swapping
	 *            The swapping flag.
	 */
	public void setSwapping(boolean swapping) {
		this.swapping = swapping;
	}

	/**
	 * Checks if the player is withdrawing as notes.
	 * 
	 * @return The withdrawing as notes flag.
	 */
	public boolean isWithdrawingAsNotes() {
		return withdrawAsNotes;
	}

	/**
	 * Checks if the player is swapping.
	 * 
	 * @return The swapping flag.
	 */
	public boolean isSwapping() {
		return swapping;
	}

	private boolean chat = true, split = false, mouse = true, aid = false;

	private int brightness = 3;

	public void refresh(Player player) {
		player.getActionSender().sendConfig(171, !chat ? 1 : 0);
		player.getActionSender().sendConfig(287, split ? 1 : 0);
		player.getActionSender().sendConfig(170, !mouse ? 1 : 0);
		player.getActionSender().sendConfig(427, aid ? 1 : 0);
		player.getActionSender().sendConfig(172,
				!player.isAutoRetaliating() ? 1 : 0);
		player.getActionSender().sendConfig(166, brightness);
		player.getActionSender().sendConfig(173, 0); // Run
	}

	public boolean isMouseTwoButtons() {
		return mouse;
	}

	public boolean isChatEffectsEnabled() {
		return chat;
	}

	public boolean isPrivateChatSplit() {
		return split;
	}

	public boolean isAcceptAidEnabled() {
		return aid;
	}

	public void setMouseTwoButtons(boolean mouse) {
		this.mouse = mouse;
	}

	public void setChatEffectsEnabled(boolean chat) {
		this.chat = chat;
	}

	public void setPrivateChatSplit(boolean split) {
		this.split = split;
	}

	public void setAcceptAidEnabled(boolean aid) {
		this.aid = aid;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getBrightness() {
		return brightness;
	}

	public void setHasReceivedtarterItems(boolean hasReceivedtarterItems) {
		this.hasReceivedtarterItems = hasReceivedtarterItems;
	}

	public boolean hasReceivedtarterItems() {
		return hasReceivedtarterItems;
	}

}
