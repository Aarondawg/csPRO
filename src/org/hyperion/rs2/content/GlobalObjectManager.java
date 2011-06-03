package org.hyperion.rs2.content;

import java.util.ArrayList;
import java.util.List;

import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.GameObjectDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.region.Region;

/**
 * ObjectManager.java
 * 
 * @author Aeque
 * @author Brown
 * 
 */
public class GlobalObjectManager {

	private static GlobalObjectManager instance = new GlobalObjectManager();
	private List<GameObject> objects;
	private GameObject ob;

	public GlobalObjectManager() {
		objects = new ArrayList<GameObject>();
	}

	/**
	 * Gets the global object instance.
	 */
	public static GlobalObjectManager getInstance() {
		return instance;
	}

	public void createGlobalObject(Player player, int id, Location location,
			int face, int type) {
		for (GameObject ob : objects) {
			if (ob.getLocation().equals(location)) {
				return;
			}
		}
		final GameObject obj = new GameObject(GameObjectDefinition.forId(id),
				location, face, type);
		objects.add(obj);
		refresh(player, obj);
	}

	private void refresh(Player player, GameObject obj) {
		for (Region reg : World.getWorld().getRegionManager()
				.getSurroundingRegions(obj.getLocation())) {
			for (final Player p : reg.getPlayers()) {
				if (p.getLocation().isWithinDistance(obj.getLocation())) {
					p.getActionSender().sendCreateObject(
							obj.getDefinition().getId(), obj.getType(),
							obj.getRotation(), obj.getLocation());
				}
			}
		}
	}

	public boolean globalObjectExists(Location l) {
		for (GameObject o : objects) {
			if (o.getLocation().equals(l)) {
				return true;
			}
		}
		return false;
	}

	public void replaceGlobalObject(Player player, Location l, GameObject obj) {
		for (final GameObject o : objects) {
			if (o.getLocation().equals(l)) {
				ob = o;
			}
		}
		if (objects.contains(ob)) {
			objects.remove(ob);
		}
		objects.add(obj);
		refresh(player, obj);
	}

	public void destroyGlobalObject(Player player, Location l) {
		for (final GameObject o : objects) {
			if (o.getLocation().equals(l)) {
				ob = o;
			}
		}
		if (objects.contains(ob)) {
			for (Region reg : World.getWorld().getRegionManager()
					.getSurroundingRegions(player.getLocation())) {
				for (final Player p : reg.getPlayers()) {
					if (p.getLocation().isWithinDistance(l)) {
						p.getActionSender().sendDestroyObject(ob.getType(),
								ob.getRotation(), l);
					}
				}
			}
			objects.remove(ob);
		}
		/*
		 * final GameObject obj = new
		 * GameObject(GameObjectDefinition.forId(6951), l); refresh(player,
		 * obj);
		 */
	}

	public void createSpawn(Player player, GameObject obj) {
		objects.add(obj);
		refresh(player, obj);
	}

	public void refresh(Player p) {
		for (GameObject o : objects) {
			if (p.getLocation().isWithinDistance(o.getLocation())) {
				p.getActionSender().sendCreateObject(o.getDefinition().getId(),
						o.getType(), o.getRotation(), o.getLocation());
			}
		}
	}

}