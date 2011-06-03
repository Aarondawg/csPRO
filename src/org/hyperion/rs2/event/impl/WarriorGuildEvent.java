package org.hyperion.rs2.event.impl;

import java.util.Iterator;

import org.hyperion.rs2.content.minigames.WarriorsGuild;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

public class WarriorGuildEvent extends Event {

	public WarriorGuildEvent() {
		super(60000); // Once per minute..
		World.getWorld().submit(new WGCatapultEvent());
	}

	private static final Item REMOVE = new Item(WarriorsGuild.TOKENS, 10);

	@Override
	public void execute() {
		Iterator<Player> it = WarriorsGuild.IN_GAME.iterator();
		while (it.hasNext()) {
			Player p = it.next();
			p.getInventory().remove(REMOVE);
			p.getActionSender().sendMessage("Ten of your tokens crumble away.");
			Item item = p.getInventory().getById(WarriorsGuild.TOKENS);
			if (item == null || item.getCount() < 10) {
				p.getWarriorsGuild().outOfTokens(); // Registers a 25 seconds
													// event.. ;)
			}
		}

	}

}
