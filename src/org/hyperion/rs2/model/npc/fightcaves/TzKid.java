package org.hyperion.rs2.model.npc.fightcaves;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;

/**
 * The small bat. Drains prayer, when in Melee range, by the amount of damage
 * done +1 (e.g. If it hits 0 HP, it will drain 1 Prayer). They hit up to 4
 * damage and therefore drain up to 5 prayer points at a time. Should always be
 * a player's first priority as Prayer is your most valuable resource in the
 * caves.
 */
public class TzKid extends FightCaveMonster {

	public TzKid(Player player, NPCDefinition definition, Location location) {
		super(player, definition, location);
		// Those guys are handled in the players "inflict damage" method.
	}

}
