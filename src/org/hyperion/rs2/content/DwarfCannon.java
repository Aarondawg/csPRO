package org.hyperion.rs2.content;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Damage.HitType;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.region.Region;

/**
 * 
 * @author Kelvin
 * 
 */
public class DwarfCannon {
	public static final int CANNONBAL_ID = 2; // TODO: change to real id
	public static final int DWARFCANNON_CYCLE = 1000; // 2 seconds.

	public static void start(Player player, int x, int y) {
		/*
		 * if(player.isCannonRunning()) { player.getActionSender().sendMessage(
		 * "Your cannon is still running, please wait."); return; } if
		 * (!player.getInventory().hasItem(CANNONBAL_ID)) {
		 * player.getActionSender
		 * ().sendMessage("You do not have any cannonbal's left."); return; }
		 * else { player.face(Location.create(x, y,
		 * player.getLocation().getZ())); int amount = 20; Item cannonBall =
		 * player.getInventory().getById(CANNONBAL_ID); if
		 * (!player.getInventory().hasItemAmount(CANNONBAL_ID, 20)) { amount =
		 * cannonBall.getCount(); } player.getInventory().remove(cannonBall,
		 * amount);
		 * 
		 * player.getActionSender().sendMessage("You loaded the cannon with " +
		 * amount + " cannonballs.");
		 * 
		 * World.getWorld().submit(new DwarfCannonEvent(player,
		 * DWARFCANNON_CYCLE, x, y, amount)); player.setIsCannonRunning(true);
		 * amount = 0; }
		 */
	}

	public static class DwarfCannonEvent extends Event {
		private Player player;
		private int x, y;
		private int rounds;
		private int animID = 515;

		public DwarfCannonEvent(Player player, long delay, int x, int y,
				int rounds) {
			super(delay);
			this.player = player;
			this.x = x;
			this.y = y;
			this.rounds = rounds;
		}

		@Override
		public void execute() {
			/*
			 * if (rounds <= 0) { player.getActionSender().sendMessage(
			 * "Your cannon is running out of cannonballs..");
			 * player.getActionSender
			 * ().sendMessage("Please refill your cannon.");
			 * player.setIsCannonRunning(false); super.stop(); return; }
			 * rounds--; if (animID == 521) { animID = 514; } else { animID++; }
			 * // Rotate packet. player.getActionSender().animateObject(x, y,
			 * animID, 10, -1); player.getActionSender().sendMessage("Ani: " +
			 * animID + " x: " + x + " y " + y); for(NPC n :
			 * player.getRegion().getNpcs()) { if(Location.create(x, y,
			 * player.getLocation
			 * ().getZ()).isWithinInteractionDistance(n.getLocation()))
			 * n.inflictDamage(20, HitType.NORMAL_DAMAGE); }
			 */
		}
	}
	/*
	 * private static void attackMultiNpcs(int maxDamage, int range) { for (int
	 * i = 0; i < 10000; i++) { if(server.npcHandler.npcs[i] != null) {
	 * if(server.npcHandler.npcs[i].MaxHP > 1) {
	 * if(distanceToPoint(server.npcHandler.npcs[i].absX,
	 * server.npcHandler.npcs[i].absY) <= range &&
	 * !server.npcHandler.npcs[i].IsDead && server.npcHandler.npcs[i].HP != 1000
	 * && npcId != 2475 && npcId != 2259) { int damage = misc.random(maxDamage);
	 * offsetY = (server.npcHandler.npcs[i].absX)-(CannonX+1) * - 1; offsetX =
	 * (server.npcHandler.npcs[i].absY)-(CannonY+1) * - 1;
	 * 
	 * if (server.npcHandler.npcs[i].HP - hitDiff < 0) damage =
	 * server.npcHandler.npcs[i].HP; hitDiff = damage;
	 * server.npcHandler.npcs[i].hitDiff = damage;
	 * server.npcHandler.npcs[i].updateRequired = true;
	 * server.npcHandler.npcs[i].hitUpdateRequired = true; } } } } }
	 * 
	 * public void CannonBomber(int gfx, int maxDamage) { for (int i = 0; i <=
	 * server.npcHandler.maxNPCs; i++) { if(server.npcHandler.npcs[i] != null) {
	 * if(!server.npcHandler.npcs[i].IsDead && server.npcHandler.npcs[i].HP != 0
	 * && cannonDistance(server.npcHandler.npcs[i].absX,
	 * server.npcHandler.npcs[i].absY) <= 6) { int Attacking =
	 * server.npcHandler.maxNPCs; int offsetX = (cannonX -
	 * server.npcHandler.npcs[i].absX) * -1; int offsetY = (cannonY -
	 * server.npcHandler.npcs[i].absY) * -1; int damage =
	 * misc.random(maxDamage); stillgfx(gfx, server.npcHandler.npcs[i].absY,
	 * server.npcHandler.npcs[i].absX); //ProjectileSpell(1, 1, 1, cannonY,
	 * cannonX, offsetY, offsetX, Attacking, server.npcHandler.npcs[i].absY,
	 * server.npcHandler.npcs[i].absX); if (server.npcHandler.npcs[i].HP -
	 * hitDiff < 0) damage = server.npcHandler.npcs[i].HP; hitDiff = damage;
	 * server.npcHandler.npcs[i].StartKilling = playerId;
	 * server.npcHandler.npcs[i].RandomWalk = false;
	 * server.npcHandler.npcs[i].IsUnderAttack = true;
	 * server.npcHandler.npcs[i].hitDiff = damage; server.npcHandler.npcs[i].HP
	 * -= hitDiff; server.npcHandler.npcs[i].updateRequired = true;
	 * server.npcHandler.npcs[i].hitUpdateRequired = true; } } } }
	 */

}