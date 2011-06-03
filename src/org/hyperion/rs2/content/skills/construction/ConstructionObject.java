package org.hyperion.rs2.content.skills.construction;

import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.GameObjectDefinition;
import org.hyperion.rs2.model.Location;

public class ConstructionObject extends GameObject {

	public ConstructionObject(GameObjectDefinition definition,
			Location location, int type, int rotation,
			boolean buildingModeObject) {
		super(definition, location, type, rotation);
		this.buildingModeObject = buildingModeObject;
	}

	public boolean isBuildingModeObject() {
		return buildingModeObject;
	}

	private final boolean buildingModeObject;

}
