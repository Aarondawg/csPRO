package org.hyperion.rs2.content.skills;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Equipment;

public class Mining {

	public static void prepareMining(Player player, int objId, Location objLoc,
			int minLvl, int oreId, String oreName, double xp, int emptyId,
			int resDelay) {
		/*
		 * if(player.isBusy()) return; if(player.isMining()) return;
		 * if(player.getSkills().getLevelForXp(14) < minLvl) {
		 * player.getActionSender().sendMessage("You need a Mining level of " +
		 * minLvl + " to mine this rock"); return; }
		 * if(player.getInventory().getFreeSlots() < 1) {
		 * player.getActionSender(
		 * ).sendMessage("Your inventory is too full to hold any more ores.");
		 * return; } if(player.checkPick() < 1) {
		 * player.getActionSender().sendMessage
		 * ("You do not have a pickaxe that you can use."); return; }
		 * if(player.getLocation().withinRange(objLoc, 1)) miningEvent(player,
		 * objId, objLoc, minLvl, oreId, oreName, xp, emptyId, resDelay);
		 */
	}

	public static void miningEvent(final Player miner, final int objId,
			final Location objLoc, final int minLvl, final int oreId,
			final String oreName, final double xp, final int emptyId,
			final int resDelay) {
		/*
		 * miner.animationDelay = 0; miner.resetSkilling(); miner.setBusy(true);
		 * miner.setMining(true); miner.playAnimation(miner.getMiningAnim());
		 * miner
		 * .getActionSender().sendMessage("You swing your pick at the rock.");
		 * World.getInstance().registerEvent(new Event(2000) { public void
		 * execute() { if(ObjectList.containsObject(objLoc)) {
		 * miner.setMining(false); } if(miner.isMining()) {
		 * if(miner.getInventory().getFreeSlots() >= 1) {
		 * if(miner.animationDelay >= 1) {
		 * miner.playAnimation(getPickAnim(miner)); miner.animationDelay = 0; }
		 * else { miner.animationDelay++; } if(giveOre(miner, minLvl)) {
		 * miner.getInventory().add(oreId, 1); miner.getInventory().refresh();
		 * miner.getActionSender().sendMessage("You get some " + oreName + ".");
		 * miner.getSkills().addExperience(Skills.MINING, xp);
		 * miner.playAnimation(-1); miner.setMining(false);
		 * miner.setBusy(false); this.stop(); createGlobalRock(emptyId, objLoc,
		 * getFace(objLoc), 10); ObjectList.addToList(objLoc, objId);
		 * restoreRockEvent(objId, objLoc, getFace(objLoc), resDelay); } } else
		 * { miner.getActionSender().sendMessage(
		 * "Your inventory is too full to hold any more " + oreName + ".");
		 * miner.setBusy(false); miner.setMining(false); this.stop(); } } else {
		 * miner.setBusy(false); this.stop(); } } });
		 */
	}

	public static int getFace(Location location) {
		if (location == Location.create(2977, 3245, 0))
			return 2;
		else if (location == Location.create(2978, 3248, 0))
			return 0;
		else if (location == Location.create(2979, 3248, 0))
			return 1;
		else if (location == Location.create(2986, 3239, 0))
			return 1;
		else if (location == Location.create(2986, 3235, 0))
			return 0;
		else if (location == Location.create(2982, 3234, 0))
			return 2;
		else if (location == Location.create(2977, 3233, 0))
			return 2;
		else if (location == Location.create(2975, 3234, 0))
			return 2;
		else if (location == Location.create(2971, 3237, 0))
			return 1;
		else if (location == Location.create(2968, 3239, 0))
			return 2;
		else
			return -1;
	}

	private static int getPickAnim(Player player) {
		if (player.getInventory().contains(1275)
				|| player.getEquipment().contains(1275)
				&& player.getSkills().getLevelForExperience(Skills.MINING) >= 41)
			return 624;
		else if (player.getInventory().contains(1271)
				|| player.getEquipment().contains(1271)
				&& player.getSkills().getLevelForExperience(Skills.MINING) >= 31)
			return 628;
		else if (player.getInventory().contains(1273)
				|| player.getEquipment().contains(1273)
				&& player.getSkills().getLevelForExperience(Skills.MINING) >= 21)
			return 629;
		else if (player.getInventory().contains(1269)
				|| player.getEquipment().contains(1269)
				&& player.getSkills().getLevelForExperience(Skills.MINING) >= 6)
			return 627;
		else if (player.getInventory().contains(1267)
				|| player.getEquipment().contains(1267))
			return 626;
		else
			return 625;
	}

	private static void createGlobalRock(int objId, Location objLoc,
			int objFace, int objType) {
		for (Player p : World.getWorld().getPlayers()) {
			p.getActionSender().sendCreateObject(objId, objType, objFace,
					objLoc);
		}
	}

	private static boolean giveOre(Player player, int miningLevel) {
		/*
		 * int playerLevel =
		 * player.getSkills().getLevelForExperience(Skills.MINING); int pickaxe
		 * = player.checkPick(); double check = pickaxe +
		 * Math.round(playerLevel*0.6) - Math.round(miningLevel*0.8); Random r =
		 * new Random(); int rand = r.nextInt(100); if(check >= rand) { return
		 * true; } else { return false; }
		 */
		return false;
	}

	private static void restoreRockEvent(final int objectId,
			final Location objLoc, final int objFace, int resDelay) {
		/*
		 * World.getInstance().registerEvent(new Event(resDelay) { public void
		 * execute() { createGlobalRock(objectId, objLoc, objFace, 10);
		 * ObjectList.remFromList(objLoc); this.stop(); } });
		 */

	}

	public static boolean parseIds(Player player, Location location, int id) {
		switch (id) {
		/**
		 * Clay.
		 */
		case 2108:
		case 2109:
			prepareMining(player, id, location, 1, 434, "clay", 5, 450, 120);
			return true;
			/**
			 * Copper.
			 */
		case 2090:
		case 2091:
			prepareMining(player, id, location, 1, 436, "copper", 17.5, 450,
					300);
			return true;
			/**
			 * Tin.
			 */
		case 2094:
		case 2095:
		case 11933:
		case 11934:
		case 11935:
			prepareMining(player, id, location, 1, 438, "tin", 17.5, 450, 300);
			return true;
			/**
			 * Iron
			 */
		case 2092:
		case 2093:
			prepareMining(player, id, location, 15, 440, "iron", 35, 450, 800);
			return true;

			/**
			 * Silver
			 */
		case 2100:
		case 2101:
			prepareMining(player, id, location, 20, 442, "silver", 40, 450,
					9000);
			return true;
			/**
			 * Coal.
			 */
		case 2096:
		case 2097:
		case 11930:
		case 11931:
		case 11932:
			prepareMining(player, id, location, 30, 453, "coal", 50, 451, 4400);
			return true;
			/**
			 * Gold
			 */
		case 2098:
		case 2099:
			prepareMining(player, id, location, 40, 444, "gold", 65, 450, 9000);
			return true;
			/**
			 * Mithril
			 */
		case 2102:
		case 2103:
			prepareMining(player, id, location, 55, 447, "mithril", 80, 451,
					6000);// 16100);
			return true;
			/**
			 * Adamantite
			 */
		case 2104:
		case 2105:
			prepareMining(player, id, location, 70, 449, "adamantite", 95, 450,
					20000);// 5000);
			return true;
		}
		// TODO: Rune.
		return false;
	}

	public static int checkForPick(Player player) {
		if ((player.getInventory().contains(1275) || Equipment.containsWeapon(
				player, 1275))
				&& player.getSkills().getLevelForExperience(14) >= 41)
			return 21;
		else if ((player.getInventory().contains(1271) || Equipment
				.containsWeapon(player, 1271))
				&& player.getSkills().getLevelForExperience(14) >= 31)
			return 15;
		else if ((player.getInventory().contains(1273) || Equipment
				.containsWeapon(player, 1273))
				&& player.getSkills().getLevelForExperience(14) >= 21)
			return 10;
		else if ((player.getInventory().contains(1269) || Equipment
				.containsWeapon(player, 1269))
				&& player.getSkills().getLevelForExperience(14) >= 6)
			return 7;
		else if (player.getInventory().contains(1267)
				|| Equipment.containsWeapon(player, 1267))
			return 4;
		else if (player.getInventory().contains(1265)
				|| Equipment.containsWeapon(player, 1265))
			return 2;
		else
			return -1;
	}

	public static Animation getMiningAnimation(Player player) {
		int id = 625;
		if ((player.getInventory().contains(1275) || Equipment.containsWeapon(
				player, 1275))
				&& player.getSkills().getLevelForExperience(14) >= 41)
			id = 624;
		else if ((player.getInventory().contains(1271) || Equipment
				.containsWeapon(player, 1271))
				&& player.getSkills().getLevelForExperience(14) >= 31)
			id = 628;
		else if ((player.getInventory().contains(1273) || Equipment
				.containsWeapon(player, 1273))
				&& player.getSkills().getLevelForExperience(14) >= 21)
			id = 629;
		else if ((player.getInventory().contains(1269) || Equipment
				.containsWeapon(player, 1269))
				&& player.getSkills().getLevelForExperience(14) >= 6)
			id = 627;
		else if (player.getInventory().contains(1267)
				|| Equipment.containsWeapon(player, 1267))
			id = 626;
		return Animation.create(id);
	}

}
