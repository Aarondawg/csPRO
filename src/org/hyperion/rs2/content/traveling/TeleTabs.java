package org.hyperion.rs2.content.traveling;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

public class TeleTabs {

	private static final Animation TELE_TAB = Animation.create(4071);
	private static final Graphic TELE_TAB_GFX = Graphic.create(678);

	// private static final Random r = new Random();

	public static boolean handle(final Player player, Item item) {
		Location location = null;
		switch (item.getId()) {
		case 8007: // Varrock teleport
			location = Location.create(3209, 3422, 0);
			break;
		case 8008: // Lumbridge teleport
			location = Location.create(3221, 3219, 0);
			break;
		case 8009: // Falador teleport
			location = Location.create(2963, 3377, 0);
			break;
		case 8010: // Camelot teleport
			location = Location.create(2756, 3476, 0);
			break;
		case 8011: // Ardougne teleport
			location = Location.create(2659, 3305, 0);
			break;
		}
		if (location != null) {
			/*
			 * Prevents mass clicking them.
			 */
			if (player.getMagic().getLastTeleport() < 3000 || player.isDead()) {
				return true;
			}

			if (player.getRequestManager().isDueling()) {
				if (player.getLocation().isInDuelArena()) {
					player.getActionSender().sendMessage(
							"You can't teleport while you're dueling.");
					return true;
				}
			}

			if (player.isTeleblocked()
					|| player.getLocation().getNewArea(player) != null) {
				player.getActionSender().sendMessage(
						"A magical force stops you from teleporting.");
				return true;
			}

			/*
			 * Makes sure we're below level 20 wilderness.
			 */
			if (Location.getWildernessLevel(player.getLocation()) > 20) {
				player.getActionSender().sendMessage(
						"You cannot teleport above level 20 wilderness.");
				return true;
			}
			player.playAnimation(TELE_TAB);
			player.playGraphics(TELE_TAB_GFX);
			player.getInventory().remove(item, 1);
			player.getMagic().setLastTeleport(System.currentTimeMillis());
			final Location fLocation = location;
			World.getWorld().submit(new Event(1600) {

				@Override
				public void execute() {
					player.setTeleportTarget(fLocation);
					player.playAnimation(Animation.create(-1));
					this.stop();

				}

			});

			return true;
		}
		return false;
	}

}
