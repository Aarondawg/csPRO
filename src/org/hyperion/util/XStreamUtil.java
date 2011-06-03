package org.hyperion.util;

import com.thoughtworks.xstream.XStream;

/**
 * Util class to get the xstream object.
 * 
 * @author Graham
 * 
 */
public class XStreamUtil {

	private XStreamUtil() {
	}

	private static XStream xstream = null;

	public static XStream getXStream() {
		if (xstream == null) {
			xstream = new XStream();
			/*
			 * Set up our aliases.
			 */
			xstream.alias("player", org.hyperion.rs2.model.Player.class);
			xstream.alias("itemDefinition",
					org.hyperion.rs2.model.ItemDefinition.class);
			xstream.alias("item", org.hyperion.rs2.model.Item.class);
			xstream.alias("npcDefinition",
					org.hyperion.rs2.model.NPCDefinition.class);
			xstream.alias("objectDefinition",
					org.hyperion.rs2.model.GameObjectDefinition.class);
			xstream.alias("dialogue", org.hyperion.rs2.content.Dialogue.class);
			xstream.alias("dialogueLoader",
					org.hyperion.rs2.content.DialogueLoader.class);
			xstream.alias("door", org.hyperion.rs2.model.Door.class);
			xstream.alias("location", org.hyperion.rs2.model.Location.class);
			xstream.alias("dropController",
					org.hyperion.rs2.model.NPCDropController.class);
			xstream.alias("npcDrop", org.hyperion.rs2.content.NPCDropItem.class);
			xstream.alias("shop", org.hyperion.rs2.content.Shop.class);
			xstream.alias("npcSpawn", org.hyperion.rs2.model.NPCSpawn.class);
			xstream.alias(
					"equipmentType",
					org.hyperion.rs2.model.container.Equipment.EquipmentType.class);
			/*
			 * xstream.alias("npc", com.rs2hd.model.NPC.class);
			 * xstream.alias("npcDrop", com.rs2hd.model.NPCDrop.class);
			 * xstream.alias("drop", com.rs2hd.model.NPCDrop.Drop.class);
			 * xstream.alias("spawn", com.rs2hd.model.Spawn.class);
			 * xstream.alias("shop", com.rs2hd.model.Shop.class);
			 * xstream.alias("config", com.rs2hd.Configuration.class);
			 * xstream.alias("package", com.rs2hd.tools.Package.class);
			 * xstream.alias("bans", com.rs2hd.util.BanManager.class);
			 * xstream.alias("object", com.rs2hd.model.GlobalObject.class);
			 * xstream.alias("region", com.rs2hd.model.Region.class);
			 * xstream.alias("worldObject", com.rs2hd.io.WorldObject.class);
			 * xstream.alias("dialogue", com.rs2hd.model.Dialogue.class);
			 * xstream.alias("dialogues",
			 * com.rs2hd.content.DialogueLoader.class); xstream.alias("door",
			 * com.rs2hd.model.DoorManager.class);
			 */
		}
		return xstream;
	}

}
