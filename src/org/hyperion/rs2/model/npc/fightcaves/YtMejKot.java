package org.hyperion.rs2.model.npc.fightcaves;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;

/**
 * Can heal itself and other monsters, but only when in its attacking you.
 * Trapping it behind a rock (such as the Italy rock) and safe-spotting it is
 * recommended. When there is a Yt-MejKot and a Ket-Zek on the same wave, try to
 * run around the Ket-Zek so the Yt-MejKot will get stuck. Avoid getting in
 * melee range, as it can hit up to 28.
 */
public class YtMejKot extends FightCaveMonster {

	private static final Random r = new Random();

	public YtMejKot(Player player, NPCDefinition definition, Location location) {
		super(player, definition, location);
	}

	@Override
	public boolean getSpecialAttack(final Entity victim) {
		/*
		 * To be honest, I have no idea how they work in RS2, but here goes! We
		 * make a list of all the monsters which is less than two squares away
		 * from us.
		 */
		List<FightCaveMonster> withinDistance = new ArrayList<FightCaveMonster>();
		for (FightCaveMonster npc : getPlayer().getFightCaves().getMonsters()) {
			if (this.getLocation().isWithinInteractingRange(this, npc, 2)) {
				withinDistance.add(npc);
			}
		}
		/*
		 * We add our self to the list as well.
		 */
		withinDistance.add(this);
		/*
		 * Animate
		 */
		this.playAnimation(Animation.create(2639));
		/*
		 * We heal a random NPC from the list..
		 */
		FightCaveMonster toHeal = withinDistance.get(r.nextInt(withinDistance
				.size()));
		/*
		 * By a random amount between 1 and 30 (inclusive both)
		 */
		toHeal.heal(r.nextInt(30) + 1, false);
		toHeal.playGraphics(Graphic.create(444, (100 << 16)));
		return false;
	}

}
