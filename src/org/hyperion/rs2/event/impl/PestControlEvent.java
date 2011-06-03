package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.region.Region;

public class PestControlEvent extends Event {

	private static final Region BOAT_REGION = World.getWorld()
			.getRegionManager().getRegion(2662, 2641);

	private static final int WAITING_TIME = 10; // 5 minutes.

	private static final int MINIMUM_PLAYERS_PER_GAME = 1; // 5
	private static int time = WAITING_TIME;

	public PestControlEvent() {
		super(1000); // Once every second to make stuff easy xD.
	}

	@Override
	public void execute() {
		/*
		 * First, we handle the boat part.
		 */
		final Player[] list = PestControl.getBoatList().toArray(new Player[0]);
		String departure = "";
		if (list.length < MINIMUM_PLAYERS_PER_GAME) {
			departure = "Need more players!";
			/*
			 * If there's not enough players playing, we set the time to 300
			 * seconds (5 minutes)
			 */
			time = WAITING_TIME;
		} else {
			/*
			 * We decrease the time based on whenever or not a game is currently
			 * going on.
			 */
			time = PestControl.isGameGoingOn() ? PestControl.getGameTime() + 60
					: time - 1;

			departure = secondsToMinutesAndSeconds(time);
		}
		for (Player player : list) {
			player.getActionSender().sendWalkableInterface(407);
			player.getActionSender().sendString(
					"Players Ready: " + list.length, 407, 1);
			// player.getActionSender().sendString("(Need 5 to 25 players)",
			// 407, 2); The interface will already have this string. ;)
			player.getActionSender().sendString(
					"Points: " + player.getPestControlPoints(), 407, 3);
			player.getActionSender().sendString("Next Departure: " + departure,
					407, 0);
		}
		/*
		 * If the time is 0, we start the game.
		 */
		if (time == 0) {
			PestControl.startGame();
		}
		/*
		 * Then we handle the in game part.
		 */
		if (PestControl.isGameGoingOn()) {
			String time = secondsToMinutesAndSeconds(PestControl.getGameTime());
			for (Player player : PestControl.getGameList().toArray(
					new Player[0])) {
				player.getActionSender().sendWalkableInterface(408);
				player.getActionSender().sendString(time, 408, 0);
				player.getActionSender()
						.sendString(
								"" + PestControl.getVoidKnight().getHitpoints(),
								408, 1); // Void knight health
				player.getActionSender().sendString(
						"" + player.getPestControlDamage(), 408, 11); // Damage
																		// dealth
				player.getActionSender().sendString(
						"" + PestControl.getPurplePortal().getHitpoints(), 408,
						13); // Purple
				player.getActionSender().sendString(
						"" + PestControl.getBluePortal().getHitpoints(), 408,
						14); // Blue
				player.getActionSender().sendString(
						"" + PestControl.getYellowPortal().getHitpoints(), 408,
						15); // Yellow
				player.getActionSender()
						.sendString(
								"" + PestControl.getRedPortal().getHitpoints(),
								408, 16); // "Red"
			}
			if (PestControl.decreaseGameTime() == 0) {
				PestControl.finishGame(false);
			}
			PestControl.behave();
			/*
			 * This event will result in the behaving method being called once
			 * every 500 ms instead of every 1000 ms. ;)
			 */
			World.getWorld().submit(new Event(500) {

				@Override
				public void execute() {
					PestControl.behave();
					this.stop();
				}

			});
		}
	}

	private String secondsToMinutesAndSeconds(int seconds) {
		int minutes = (int) (seconds / 60);
		String time = "" + minutes + " min " + (seconds - (minutes * 60))
				+ " seconds";
		return time;
	}

}
