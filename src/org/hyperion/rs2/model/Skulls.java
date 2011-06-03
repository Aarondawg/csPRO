package org.hyperion.rs2.model;

import java.util.ArrayList;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;

/**
 * A class handling the skulls of a specific player.
 * 
 * @author Brown
 */

public class Skulls {

	/**
	 * ArrayList containing all the players we've been in combat with.
	 */
	private ArrayList<String> combatInteractions = new ArrayList<String>();

	/**
	 * The player we're dealing with.
	 */
	private Player player;

	/**
	 * Simple boolean to indicate if the player is skulled or not.
	 */
	private int skullId = -1;

	/**
	 * This is set to 20 everytime we're attacked. This is decreased by 1 every
	 * single minute.
	 */
	private int skullTimer = 0;

	/**
	 * This should be called through the "targets" object instance. Checks if
	 * the ArrayList contains the attacker, if not, it gives him a skull and
	 * starts an event to remove it.
	 * 
	 * @param attacker
	 *            The attacking player.
	 */
	public boolean checkForSkull(Player attacker) {
		// Checks our targets attackedPlayers list.
		// If the attacker is in here, we simply return false.
		if (combatInteractions.contains(attacker.getName())) {
			return false;
		} else {
			attacker.getSkulls().combatInteractions.add(player.getName());
			combatInteractions.add(attacker.getName());
			if (attacker.getLocation().isInWilderness()) {
				attacker.getSkulls().appendSkull(player, 0);
			}
			return true;
		}
	}

	/**
	 * Appends a skull for a specific player, and calls the UpdateFlags for
	 * instant appearance.
	 * 
	 * @param player
	 *            The player to skull
	 * @param skull
	 *            The skull id to show above the players head. 0 = Wilderness
	 *            one, 1 = Fight pits.
	 */
	private void appendSkull(Player target, int skull) {
		skullId = skull;
		skullTimer = 20;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		World.getWorld().submit(new CombatInteractionsRemoverEvent(target));
	}

	/**
	 * Gets the ArrayList of players we've been in combat with.
	 * 
	 * @return The combatInteractions ArrayList.
	 */
	public ArrayList<String> getCombatInteractions() {
		return combatInteractions;
	}

	/**
	 * Tells us if a player is skulled. Example usage: items kept on death.
	 * 
	 * @return true of our player is skulled, false if not.
	 */
	public boolean isSkulled() {
		return skullId == 0;
	}

	/**
	 * Sets the skull icon above the player. Example usage: Fight Pits.
	 */
	public void setSkullId(int skullId) {
		this.skullId = skullId;
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/**
	 * Gets the current skullId.
	 * 
	 * @return The current skullId.
	 */
	public int getSkullId() {
		return skullId;
	}

	/**
	 * Gets the skull timer. Example usage: Saving/Loading.
	 */
	public int getSkullTimer() {
		return skullTimer;
	}

	/**
	 * Setup the players skulls.
	 * 
	 * @param skullTimer
	 *            The skull timer saved/loaded from the serialize method in
	 *            Player.java
	 */
	public void setup(int skullTimer) {
		this.skullTimer = skullTimer;
		if (skullTimer > 0) {
			this.skullId = 0;
			player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		}
	}

	/**
	 * Resets everything related with skulls. Example usage: Death.
	 */
	public void reset() {
		combatInteractions.clear();
		skullId = -1;
	}

	/**
	 * Event called every time we've attacked a new player. The skull will only
	 * disappear if we're dealing with the last attacked player.
	 */
	private class CombatInteractionsRemoverEvent extends Event {

		private static final int DELAY = 1200000; // 20 minutes.

		public CombatInteractionsRemoverEvent(Player target) {
			super(DELAY);
			this.target = target;
		}

		@Override
		public void execute() {
			try {
				combatInteractions.remove(target.getName());
				target.getSkulls().getCombatInteractions()
						.remove(player.getName());
			} catch (Exception e) {
				System.out
						.println("WARNING!! Nulled player/target in the SkullEvent");
			}
			this.stop();
		}

		/**
		 * The player we were attacking.
		 */
		private Player target;

	}

	/**
	 * This method is called once every minute.
	 */
	public void tick() {
		if (skullTimer > 0) {
			skullTimer--;
			if (skullTimer == 0) {
				skullId = -1;
				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
			}
		}
	}

	/**
	 * Class constructor.
	 */
	public Skulls(Player player) {
		this.player = player;
	}

}
