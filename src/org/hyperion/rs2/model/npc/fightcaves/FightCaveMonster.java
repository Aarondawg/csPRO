package org.hyperion.rs2.model.npc.fightcaves;

import java.util.Random;

import org.hyperion.rs2.action.impl.AttackingAction;
import org.hyperion.rs2.content.minigames.FightCaves;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;

public abstract class FightCaveMonster extends NPC {

	private static final Random r = new Random();

	public FightCaveMonster(Player player, NPCDefinition definition,
			Location location) {
		super(definition, location, FightCaves.minLocation,
				FightCaves.maxLocation);
		this.player = player;
	}

	public static FightCaveMonster create(Player player, int id) {
		switch (id) {
		case 2627: // Tz-Kih
		case 2628: // Tz-Kih
		case 2734: // Tz-Kih
		case 2735: // Tz-Kih
			return new TzKid(player, NPCDefinition.forId(id),
					getRandomLocation(player, id));
		case 2629: // Tz-Kek
		case 2630: // Tz-Kek
		case 2736: // Tz-Kek
		case 2737: // Tz-Kek
		case 2738: // Tz-Kek
			return new TzKek(player, NPCDefinition.forId(id),
					getRandomLocation(player, id));
		case 2631: // Tok-Xil
		case 2632: // Tok-Xil
		case 2739: // Tok-Xil
		case 2740: // Tok-Xil
		case 3592: // Tok-Xil
			return new TokXil(player, NPCDefinition.forId(id),
					getRandomLocation(player, id));
		case 2741: // Yt-MejKot
		case 2742: // Yt-MejKot
			return new YtMejKot(player, NPCDefinition.forId(id),
					getRandomLocation(player, id));
		case 2743: // Ket-Zek
		case 2744: // Ket-Zek
			return new KetZek(player, NPCDefinition.forId(id),
					getRandomLocation(player, id));
		case 2745: // TzTokJad
			return new TzTokJad(player, NPCDefinition.forId(id),
					getRandomLocation(player, id));
		case 2746: // Yt-HurKot
			return new YtHurKot(player, NPCDefinition.forId(id),
					getRandomLocation(player, id));
		}
		System.out.println("WTF LOL!?" + id);
		return null;
	}

	/**
	 * Basicly contains the four corners as I havn't seen monsters spawn in the
	 * middle of anything. (This will make all monsters stuck, unless they're
	 * spawned in the opposite corner. {min x, min y, maxX, maxY}
	 */
	private static int[][] LOCATIONS = { { 2376, 5065, 2387, 5076 },
			{ 2376, 5099, 2387, 5112 }, { 2408, 5103, 2419, 5113 },
			{ 2412, 5078, 2422, 5086 }, };

	public static Location getRandomLocation(Player player, int id) {
		final int[] locationSet = LOCATIONS[r.nextInt(LOCATIONS.length)];
		final int x = r.nextInt(locationSet[2] - locationSet[0])
				+ locationSet[0];
		final int y = r.nextInt(locationSet[3] - locationSet[1])
				+ locationSet[1];
		final int z = player.getLocation().getZ();
		return Location.create(x, y, z);
	}

	@Override
	public void inflictDamage(Hit inc, Entity source) {
		if (source != null && source instanceof Player) {
			Player player = (Player) source;
			player.increasePestControlDamage(inc.getDamage());
			if (!this.isInCombat()) {
				this.getWalkingQueue().reset();
				this.setInCombat(true);
				this.setAggressorState(false);
				this.setInteractingEntity(source);
				this.setCombatDelay(getAttackSpeed() / 2); // Slight delay
															// before we attack
															// back.
				this.getActionQueue().cancelQueuedActions();
				this.getActionQueue().addAction(
						new AttackingAction(this, source));
			}
		}
		if (!getUpdateFlags().get(UpdateFlag.HIT)) {
			getDamage().setHit1(inc);
			getUpdateFlags().flag(UpdateFlag.HIT);
		} else {
			if (!getUpdateFlags().get(UpdateFlag.HIT_2)) {
				getDamage().setHit2(inc);
				getUpdateFlags().flag(UpdateFlag.HIT_2);
			}
		}
		setHitpoints(getHitpoints() - inc.getDamage());
		if (getHitpoints() <= 0) {
			if (!this.isDead()) {
				final FightCaveMonster npc = this;
				World.getWorld().submit(new Event(1800) {
					boolean first = true;

					public void execute() {
						if (first) {
							playAnimation(Animation.create(getDeathAnimation()));
							first = false;
						} else {
							this.stop();
							playAnimation(Animation.create(-1));
							setInvisible(true);
							player.getFightCaves().remove(npc);
						}
					}
				});
			}
			this.getPoison().setPoisonHit(0);
			this.setDead(true);
		}
	}

	public Player getPlayer() {
		return player;
	}

	private final Player player;

}
