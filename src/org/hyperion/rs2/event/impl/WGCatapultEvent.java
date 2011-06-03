package org.hyperion.rs2.event.impl;

import java.util.Random;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.content.ProjectileManager;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.Damage.HitType;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.region.Region;

/**
 * The warrior guild catapult event.
 */
public class WGCatapultEvent extends Event {

	private int cycle = 0;

	private static final Random r = new Random();

	private static final Location catapult = Location.create(2842, 3552, 1);

	private static final Location target = Location.create(2842, 3545, 1);

	private static final Region[] regions = World.getWorld().getRegionManager()
			.getSurroundingRegions(target);

	private static final int[] PROJECTILE_IDS = { 679, // Stab defence
			680, // Blunt defence
			681, // Slash defence
			682, // Magic defence
	};

	public static final String[] STYLES = { "Use stab defence",
			"Use blunt defence", "Use slash defence", "Use magic defence", };

	public WGCatapultEvent() {
		super(600);
	}

	private static final int distSpeed = 130;
	private static final int offsetX = (catapult.getX() - target.getX()) * -1;
	private static final int offsetY = (catapult.getY() - target.getY()) * -1;

	private static int projectileId = PROJECTILE_IDS[0];

	public static final String[] NAMES = { "spiky ball.", "rotating anvil.",
			"slashing blades.", "magic missile.", };

	private static final int SHIELD_ID = 8856;

	@Override
	public void execute() {
		for (Region region : regions) {
			for (Player player : region.getPlayers()) {
				if (player.getLocation().getZ() == 1) {
					if (cycle % 3 == 0) { // 1800 ms
						// Wait 4 cycles, hit..
						player.getActionSender().sendProjectile(catapult,
								target, offsetX, offsetY, projectileId,
								59/* Angle */, 43 /* Start height */, 41 /*
																		 * end
																		 * height
																		 */,
								distSpeed, null);
					}
					if (player.getLocation().equals(target)) {
						if (player.hasVisibleSidebarInterfaces()) {
							if (player.getEquipment()
									.get(Equipment.SLOT_SHIELD) == null
									|| player.getEquipment()
											.get(Equipment.SLOT_SHIELD).getId() != SHIELD_ID) {
								player.getWalkingQueue().reset();
								player.getWalkingQueue().addStep(2842, 3543);
								player.getWalkingQueue().finish();
								player.getActionSender()
										.sendMessage(
												"Woah, I'd better get a Defensive Shield from Gamfred first!");
							} else {
								for (int child = 9; child <= 12; child++) {
									int style = child - 9;
									player.getActionSender()
											.sendString(
													(style == player
															.getWarriorsGuild()
															.getDefenceStyle() ? "<col=000000>"
															: "<col=FF8040>")
															+ STYLES[style],
													411, child);
								}
								for (int icon = 0; icon <= 13; icon++) {
									player.getActionSender()
											.sendSidebarInterface(icon, -1);
								}
								player.getActionSender().sendSidebarInterface(
										4, 411);
								player.getActionSender().sendSwitchTabs(4);
								player.setVisibleSidebarInterfaces(false);
							}
						}
					}
				}
			}
		}
		if (cycle % 3 == 0) {
			final int attack = r.nextInt(PROJECTILE_IDS.length);
			projectileId = PROJECTILE_IDS[attack];
			// attack = fAttack;
			World.getWorld().submit(new Event(4400) {

				@Override
				public void execute() {
					for (Region region : regions) {
						for (Player player : region.getPlayers()) {
							if (player.getLocation().getZ() == 1
									&& player.getLocation().equals(target)
									&& player.getEquipment().get(
											Equipment.SLOT_SHIELD) != null
									&& player.getEquipment()
											.get(Equipment.SLOT_SHIELD).getId() == SHIELD_ID) {
								// Animate!
								// player.getActionSender().sendMessage("HIT ME");
								if (player.getWarriorsGuild().getDefenceStyle() != attack) {
									player.inflictDamage(new Hit(
											r.nextInt(3) + 1,
											HitType.NORMAL_DAMAGE), null);
									player.playAnimation(Animation.create(1232)); // TODO:
																					// Real
																					// animation..
									player.getActionSender().sendMessage(
											"You failed to defend yourself against the "
													+ NAMES[attack]);
								} else {
									player.getWarriorsGuild()
											.increaseCatapultTokens();
									player.getActionSender().sendMessage(
											"You defend yourself against the "
													+ NAMES[attack]);
								}
							}
						}
					}
					this.stop();
				}

			});
		}
		cycle++;
	}

}
