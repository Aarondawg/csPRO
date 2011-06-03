package org.hyperion.rs2.model.npc.fightcaves;

import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Damage.Hit;

/**
 * Inflicts 1 HP of recoil damage when hit by any Melee attack. These are the
 * least dangerous monsters in the cave, and should be a player's lowest
 * priority to kill. They can also be safe spotted. However, if you are trying
 * to conserve Hitpoints, try not to melee these as they will inflict 1 recoil
 * damage and can hit up to 7, but not often. When it dies, 2 level 22 Tz-Keks
 * take its place.
 * 
 * 
 * 2 of these appear when a level 45 Tz-Kek is killed; they do not have the
 * recoil effect. These are harmless to any player with a chance of acquiring
 * the cape and are good source of health while using Guthan's.
 */
public class TzKek extends FightCaveMonster {

	public TzKek(Player player, NPCDefinition definition, Location location) {
		super(player, definition, location);
	}

	@Override
	public void inflictDamage(Hit inc, Entity source) {
		super.inflictDamage(inc, source);
		if (this.getDefinition().getCombat() == 45) {
			/*
			 * Recoiling effect..
			 */
			int newHit = 0;
			int damage = inc.getDamage();
			/*
			 * The damage is 10, 20, 30, 40 or so.
			 */
			if (damage % 10 == 0) {
				newHit = damage / 10;
				/*
				 * The damage is something else.
				 */
			} else if (damage != 0) {
				int stuff = 100 - damage; // 100 is the biggest hit we can make.
											// (w/e)
				int count = 0;
				while (stuff > 10) {
					stuff -= 10;
					count++;
				}
				newHit = (10 - count);
			}
			if (!source.isDead()) {
				Combat.inflictDamage(source, null, newHit);
			}
		}
	}

}
