package org.hyperion.rs2.content;

/**
 * ObjectList.java
 * 
 * @author : Aeque
 * @date : 2.06.2009
 */

import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.model.Location;

public class ObjectList {

	private static Map<Location, Integer> objectList = new HashMap<Location, Integer>();

	public static void addToList(Location location, int objectId) {
		objectList.put(location, objectId);
	}

	public static void remFromList(Location location) {
		objectList.remove(location);
	}

	public static boolean containsObject(Location location) {
		return objectList.containsKey(location);
	}
}