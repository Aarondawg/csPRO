package org.hyperion.rs2.model;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperion.rs2.event.impl.PestControlEvent;
import org.hyperion.util.XStreamUtil;

/**
 * Represents a type of NPC.
 * 
 * @author Graham
 * 
 */
public class NPCDefinition {

	private static NPCDefinition[] definitions = null;

	@SuppressWarnings("unchecked")
	public static void init() throws IOException {
		List<NPCDefinition> defs = (List<NPCDefinition>) XStreamUtil
				.getXStream().fromXML(
						new FileInputStream("data/npcDefinitions.xml"));
		definitions = new NPCDefinition[defs.size()];

		for (NPCDefinition def : defs) {
			definitions[def.getId()] = def;
		}
		/*
		 * final Map<Integer, NPCINFO> map = new HashMap<Integer, NPCINFO>();
		 * FileInputStream fis = null; BufferedInputStream bis = null;
		 * DataInputStream dis = null; File[] files = new
		 * File("d:/npcDefinitions/").listFiles(); for(File file : files) {
		 * //File file = new File("d:/NPCS/runescape_009.htm"); try { fis = new
		 * FileInputStream(file); // Here BufferedInputStream is added for fast
		 * reading. bis = new BufferedInputStream(fis); dis = new
		 * DataInputStream(bis); // dis.available() returns 0 if the file does
		 * not have more lines.
		 * 
		 * //while (dis.available() != 0) { String name = dis.readLine(); int
		 * level = Integer.valueOf(dis.readLine()); int hits =
		 * Integer.valueOf(dis.readLine()); boolean aggressive =
		 * Boolean.valueOf(dis.readLine()); boolean retreats =
		 * Boolean.valueOf(dis.readLine()); boolean poisonous =
		 * Boolean.valueOf(dis.readLine());
		 * map.put(Integer.valueOf(file.getName().replace(".txt", "")), new
		 * NPCINFO(name, level, hits, aggressive, retreats, poisonous)); //} }
		 * catch(IOException e) { System.out.println(
		 * "WARNING: Serious error while trying to read through the file: "+e);
		 * } }
		 */

		/*
		 * PrintWriter p = new PrintWriter(new
		 * FileWriter("d:/npcDefinitions.xml", true)); for (NPCDefinition def :
		 * definitions) {
		 * 
		 * p.println(" <npcDefinition>");
		 * p.println("   <id>"+def.getId()+"</id>");
		 * p.println("   <name>"+def.getName()+"</name>");
		 * p.println("   <examine>"+def.getExamine()+"</examine>"); if(info !=
		 * null) { p.println("   <combat>"+info.getLevel()+"</combat>"); } else
		 * { p.println("   <combat>"+def.getCombat()+"</combat>"); }
		 * p.println("   <size>"+def.getSize()+"</size>");
		 * p.println("   <attackable>"+def.isAttackable()+"</attackable>");
		 * if(info != null) {
		 * p.println("   <aggressive>"+info.isAggressive()+"</aggressive>");
		 * p.println("   <retreats>"+info.isRetreats()+"</retreats>");
		 * p.println("   <poisonous>"+info.isPoisonous()+"</poisonous>"); } else
		 * { p.println("   <aggressive>"+false+"</aggressive>");
		 * p.println("   <retreats>"+false+"</retreats>");
		 * p.println("   <poisonous>"+false+"</poisonous>"); }
		 * p.println("   <respawn>"+def.getRespawn()+"</respawn>");
		 * p.println("   <maxHit>"+def.getMaxHit()+"</maxHit>"); if(info !=
		 * null) { p.println("   <hitpoints>"+info.getHits()+"</hitpoints>"); }
		 * else { p.println("   <hitpoints>"+def.getHitpoints()+"</hitpoints>");
		 * } p.println("   <attackSpeed>"+(def.getAttackSpeed() *
		 * 500)+"</attackSpeed>");
		 * p.println("   <attackAnim>"+def.getAttackAnimation
		 * ()+"</attackAnim>");
		 * p.println("   <defenceAnim>"+def.getDefenceAnimation
		 * ()+"</defenceAnim>");
		 * p.println("   <deathAnim>"+def.getDeathAnimation()+"</deathAnim>");
		 * p.println("   <attackBonus>"+def.getAttackBonus()+"</attackBonus>");
		 * p
		 * .println("   <defenceMelee>"+def.getDefenceMelee()+"</defenceMelee>")
		 * ;
		 * p.println("   <defenceRange>"+def.getDefenceRange()+"</defenceRange>"
		 * );
		 * p.println("   <defenceMage>"+def.getDefenceMage()+"</defenceMage>");
		 * p.println("  </npcDefinition>"); p.flush(); }
		 */

		PrintWriter p = new PrintWriter(new FileWriter(
				"data/npcDefinitionsEPICSHIT1337.xml", true));
		p.println("<list>");
		for (NPCDefinition def : definitions) {
			p.println(" <npcDefinition>");
			p.println("   <id>" + def.getId() + "</id>");
			p.println("   <name>" + def.getName() + "</name>");
			p.println("   <examine>" + def.getExamine() + "</examine>");
			p.println("   <combat>" + def.getCombat() + "</combat>");
			p.println("   <size>" + def.getSize() + "</size>");
			p.println("   <attackable>" + def.isAttackable() + "</attackable>");
			p.println("   <aggressive>" + def.isAggressive() + "</aggressive>");
			p.println("   <retreats>" + def.retreats() + "</retreats>");
			p.println("   <poisonous>" + def.isPoisonous() + "</poisonous>");
			p.println("   <respawn>" + def.getRespawn() + "</respawn>");
			p.println("   <maxHit>" + def.getMaxHit() + "</maxHit>");
			p.println("   <hitpoints>" + def.getHitpoints() + "</hitpoints>");
			p.println("   <attackSpeed>" + def.getAttackSpeed()
					+ "</attackSpeed>");
			p.println("   <attackAnim>" + def.getAttackAnimation()
					+ "</attackAnim>");
			p.println("   <defenceAnim>" + def.getDefenceAnimation()
					+ "</defenceAnim>");
			p.println("   <deathAnim>" + def.getDeathAnimation()
					+ "</deathAnim>");
			if (def.getAttackBonus() == def.getHitpoints() * 2) {
				p.println("   <attackBonus>" + (int) (def.getHitpoints() * 1.3)
						+ "</attackBonus>");
			} else {
				p.println("   <attackBonus>" + def.getAttackBonus()
						+ "</attackBonus>");
			}
			if (def.getDefenceMelee() == (def.getHitpoints() * 2)) {
				p.println("   <defenceMelee>"
						+ (int) (def.getHitpoints() * 1.3) + "</defenceMelee>");
			} else {
				p.println("   <defenceMelee>" + def.getDefenceMelee()
						+ "</defenceMelee>");
			}
			if (def.getDefenceRange() == (def.getHitpoints() * 2)) {
				p.println("   <defenceRange>"
						+ (int) (def.getHitpoints() * 1.3) + "</defenceRange>");
			} else {
				p.println("   <defenceRange>" + def.getDefenceRange()
						+ "</defenceRange>");
			}
			if (def.getDefenceMage() == (def.getHitpoints() * 2)) {
				p.println("   <defenceMage>" + (0) + "</defenceMage>");
			} else {
				p.println("   <defenceMage>" + def.getDefenceMage()
						+ "</defenceMage>");
			}
			p.println("  </npcDefinition>");
			p.flush();
		}
		p.println("</list>");
		p.close();
		/*
		 * We start this event here, because its referring NPCs.
		 */
		World.getWorld().submit(new PestControlEvent());
	}

	public static class NPCINFO {
		private final String name;
		private final int level;
		private final int hits;
		private final boolean aggressive;
		private final boolean retreats;
		private final boolean poisonous;

		public NPCINFO(String name, int level, int hits, boolean aggressive,
				boolean retreats, boolean poisonous) {
			this.name = name;
			this.level = level;
			this.hits = hits;
			this.aggressive = aggressive;
			this.retreats = retreats;
			this.poisonous = poisonous;
		}

		public String getName() {
			return name;
		}

		public int getLevel() {
			return level;
		}

		public int getHits() {
			return hits;
		}

		public boolean isAggressive() {
			return aggressive;
		}

		public boolean isRetreats() {
			return retreats;
		}

		public boolean isPoisonous() {
			return poisonous;
		}

	}

	public static NPCDefinition forId(int id) {
		NPCDefinition d = definitions[id];
		if (d == null) {
			d = produceDefinition(id);
		}
		return d;
	}

	private int id;
	private String name, examine;
	private int respawn = 0, combat = 0, hitpoints = 1, maxHit = 0, size = 1,
			attackSpeed = 4000, attackAnim = 422, defenceAnim = 404,
			deathAnim = 2304, attackBonus = 20, defenceMelee = 20,
			defenceRange = 20, defenceMage = 20;

	private boolean attackable = false;
	private boolean aggressive = false;
	private boolean retreats = false;
	private boolean poisonous = false;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getExamine() {
		return examine;
	}

	public int getRespawn() {
		return respawn;
	}

	public int getCombat() {
		return combat;
	}

	public int getHitpoints() {
		return hitpoints;
	}

	public int getMaxHit() {
		return maxHit;
	}

	public int getSize() {
		return size;
	}

	public boolean isAggressive() {
		return aggressive;
	}

	public boolean retreats() {
		return retreats;
	}

	public boolean isPoisonous() {
		return poisonous;
	}

	public static NPCDefinition produceDefinition(int id) {
		NPCDefinition def = new NPCDefinition();
		def.id = id;
		def.name = "NPC #" + def.id;
		def.examine = "It's an NPC.";
		return def;
	}

	public int getAttackSpeed() {
		return attackSpeed;
	}

	public int getAttackAnimation() {
		return attackAnim;
	}

	public int getDefenceAnimation() {
		return defenceAnim;
	}

	public int getDeathAnimation() {
		return deathAnim;
	}

	public boolean isAttackable() {
		return attackable;
	}

	public int getAttackBonus() {
		return attackBonus;
	}

	public int getDefenceRange() {
		return defenceRange;
	}

	public int getDefenceMelee() {
		return defenceMelee;
	}

	public int getDefenceMage() {
		return defenceMage;
	}

}
