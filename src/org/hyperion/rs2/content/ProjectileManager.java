package org.hyperion.rs2.content;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.region.Region;

public class ProjectileManager {

	public static void fire(final Entity caster, final Entity target,
			final int projectileId, final int angle, final int startHeight,
			final int endHeight) {
		int cSize = 1;
		if (caster instanceof NPC) {
			cSize = ((NPC) caster).getDefinition().getSize();
		}
		final Location cLocation = caster.getLocation()
				.getActualLocation(cSize);
		int tSize = 1;
		if (target instanceof NPC) {
			tSize = ((NPC) target).getDefinition().getSize();
		}
		final Location tLocation = target.getLocation()
				.getActualLocation(tSize);
		final int offsetX = (cLocation.getX() - tLocation.getX()) * -1;
		final int offsetY = (cLocation.getY() - tLocation.getY()) * -1;
		World.getWorld().submit(new Event(300) {

			@Override
			public void execute() {
				for (Region reg : World.getWorld().getRegionManager()
						.getSurroundingRegions(target.getLocation())) {
					for (final Player p : reg.getPlayers()) {
						p.getActionSender()
								.sendProjectile(
										cLocation,
										tLocation,
										offsetX,
										offsetY,
										projectileId,
										angle,
										startHeight,
										endHeight,
										calcDistanceSpeed(cLocation, tLocation),
										target);
					}
				}
				this.stop();
			}
		});

	}

	public static int magicHitDelay(Entity caster, Entity target) {
		return hitDelay(caster.getLocation(), target.getLocation());
	}

	public static int hitDelay(Location from, Location to) {
		int hitDelay = 0;
		int dist = (int) (Math.round(from.getDistance(to)));
		if (dist == 0) {
		} else if (dist == 1) {
			hitDelay = 2;
		} else if (dist == 2 || dist == 3) {
			hitDelay = 3;
		} else {
			hitDelay = 4;
		}
		return (hitDelay * 550);
	}

	public static void fireRangedProjectile(final Entity caster,
			final Entity target, final int projectileId, final int angle,
			final int startHeight, final int endHeight) {
		final Location cLocation = caster.getLocation();
		final Location tLocation = target.getLocation();
		final int offsetX = (cLocation.getX() - tLocation.getX()) * -1;
		final int offsetY = (cLocation.getY() - tLocation.getY()) * -1;
		for (Region reg : World.getWorld().getRegionManager()
				.getSurroundingRegions(target.getLocation())) {
			for (final Player p : reg.getPlayers()) {
				p.getActionSender().sendProjectile(cLocation, tLocation,
						offsetX, offsetY, projectileId, angle, startHeight,
						endHeight, calcDistanceSpeed(cLocation, tLocation),
						target);
			}
		}

	}

	public static void fireDBowArrows(final Player caster, final Entity target,
			final int projectileId, final boolean dragonArrows) {
		final Location cLocation = caster.getLocation();
		final Location tLocation = target.getLocation();
		final int offsetX = (cLocation.getX() - tLocation.getX()) * -1;
		final int offsetY = (cLocation.getY() - tLocation.getY()) * -1;
		int speed = calcDistanceSpeed(cLocation, tLocation);
		for (Region reg : World.getWorld().getRegionManager()
				.getSurroundingRegions(target.getLocation())) {
			for (final Player p : reg.getPlayers()) {
				p.getActionSender().sendProjectile(cLocation, tLocation,
						offsetX, offsetY, projectileId, 50, 42, 29, speed,
						target);
				p.getActionSender().sendProjectile(cLocation, tLocation,
						offsetX, offsetY, projectileId, 50, 45, 32, speed + 25,
						target);
				p.getActionSender().sendProjectile(cLocation, tLocation,
						offsetX, offsetY, caster.getProjectileId(), 50, 42, 29,
						speed, target);
				p.getActionSender().sendProjectile(cLocation, tLocation,
						offsetX, offsetY, caster.getProjectileId(), 50, 45, 32,
						speed + 25, target);
			}
		}
	}

	public static int rangedHitDelay(Entity caster, Entity target) {
		int hitDelay = 0;
		int dist = (int) (Math.round(caster.getLocation().getDistance(
				target.getLocation())));
		if (dist == 0) {
		} else if (dist == 1) {
			hitDelay = 2;
		} else if (dist == 2 || dist == 3) {
			hitDelay = 3;
		} else {
			hitDelay = 4;
		}
		return (hitDelay * 450);
	}

	public static int calcDistanceSpeed(Location from, Location to) {
		int dist = (int) (Math.round(from.getDistance(to)));
		if (dist <= 1)
			return 60;
		if (dist <= 2)
			return 70;
		if (dist <= 3)
			return 80;
		if (dist <= 4)
			return 90;
		if (dist <= 5)
			return 90;
		if (dist <= 6)
			return 95;
		if (dist <= 7)
			return 100;
		return 110;
	}

}
