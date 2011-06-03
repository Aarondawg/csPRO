package org.hyperion.rs2.model.npc.pestcontrol;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;

public class Shifter extends PestControlMob {

	public Shifter(NPCDefinition definition, Location location) {
		super(definition, location, false);
	}

	@Override
	public void behave() {
		super.attackKnight();
	}

}
