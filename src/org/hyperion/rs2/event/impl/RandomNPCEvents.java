package org.hyperion.rs2.event.impl;

import java.util.Iterator;
import java.util.Random;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

/*
 * If there's one player ONLY online, he'll get a random event every 30 minutes.
 * If there's two, there would be 50% chance of getting a random event every 15 minutes, and so forth.
 */
public class RandomNPCEvents extends Event {

	private static final int[] RANDOM_NPCS = { 409, // Genie
	};

	private static final Random r = new Random();

	public RandomNPCEvents() {
		super(60000);
	}

	@Override
	public void execute() {
		if (!World.getWorld().getPlayers().isEmpty()) {
			// Iterator<Player> fun = World.getWorld().getPlayers().iterator();
			final Player p = (Player) World.getWorld().getPlayers()
					.toArray(new Player[0])[(r.nextInt(World.getWorld()
					.getPlayers().size()))];
			if (p != null) {
				final Location loc = Location.create(
						p.getLocation().getX() + 1, p.getLocation().getY(), p
								.getLocation().getZ());
				final NPC random = new NPC(NPCDefinition.forId(RANDOM_NPCS[r
						.nextInt(RANDOM_NPCS.length)]), loc, Location.create(
						loc.getX() - 3, loc.getY() - 3, loc.getY()),
						Location.create(loc.getX() + 3, loc.getY() + 3,
								loc.getY()));
				World.getWorld().register(random);
			}
			long delay = 1800000 /* 30 minutes *// World.getWorld().getPlayers()
					.size();
			this.setDelay(delay);
		}

	}

}
