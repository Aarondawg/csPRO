package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.region.Region;

/**
 * This class should change as the time of the years change.
 * 
 * @author Brown
 */
public class HolidayEvent extends Event {

	public HolidayEvent() {
		super(960000); // Every 16th minute
	}

	@Override
	public void execute() {
		/*
		 * Region mostPlayers = null; for(final Region region :
		 * World.getWorld().getRegionManager().getRegions()) { if(mostPlayers ==
		 * null || mostPlayers.getPlayers().size() < region.getPlayers().size())
		 * { mostPlayers = region; } } if(mostPlayers != null &&
		 * mostPlayers.getPlayers().size() != 0) { final Player p =
		 * mostPlayers.getPlayers().toArray(new Player[0])[0]; final Location
		 * loc = Location.create(p.getLocation().getX() + 1,
		 * p.getLocation().getY(), p.getLocation().getZ()); final NPC santa =
		 * new NPC(NPCDefinition.forId(1552), loc, Location.create(loc.getX() -
		 * 3, loc.getY() - 3, loc.getY()), Location.create(loc.getX() + 3,
		 * loc.getY() + 3, loc.getY())); World.getWorld().register(santa);
		 * santa.setInvisible(false); santa.setWalkingType(2); santa.forceChat(
		 * "Ho ho ho! Well hello boy and boys pretending to be girls!");
		 * World.getWorld().submit(new Event(30000) {
		 * 
		 * @Override public void execute() { World.getWorld().unregister(santa);
		 * this.stop(); }
		 * 
		 * }); }
		 */
	}

}
