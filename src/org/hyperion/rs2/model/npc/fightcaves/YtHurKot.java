package org.hyperion.rs2.model.npc.fightcaves;

import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.Following;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Damage.Hit;

public class YtHurKot extends FightCaveMonster {

	private boolean stop = false;

	private static final Random r = new Random();

	public YtHurKot(Player player, NPCDefinition definition, Location location) {
		super(player, definition, location);
	}

	@Override
	public void inflictDamage(Hit inc, Entity source) {
		super.inflictDamage(inc, source);
		stop = true;
	}

	public void heal(final TzTokJad jad) {
		final YtHurKot npc = this;
		this.getActionQueue().addAction(new Action(this, 0) {

			private int cycles = 0;

			@Override
			public void execute() {
				if (stop) {
					this.stop();
				}
				Following.handleCombatFollowing(npc, jad, 1);
				if (cycles % 3 == 0) {
					if (npc.getLocation().isWithinInteractingRange(npc, jad, 2)) {
						/*
						 * Animate
						 */
						npc.playAnimation(Animation.create(2639));
						/*
						 * By a random amount between 1 and 30 (inclusive both)
						 */
						jad.heal(r.nextInt(30) + 1, false);
						jad.playGraphics(Graphic.create(444, (250 << 16)));
					}
				}
				cycles++;
				this.setDelay(600);
			}

			@Override
			public QueuePolicy getQueuePolicy() {
				return QueuePolicy.NEVER;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.NON_WALKABLE;
			}

		});

	}

}
