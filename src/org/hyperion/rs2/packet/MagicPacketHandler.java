package org.hyperion.rs2.packet;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.skills.magic.SpellMananger;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.net.Packet;

public class MagicPacketHandler implements PacketHandler {

	private static final int MAGIC_ON_NPC = 170;
	private static final int MAGIC_ON_ITEM = 188;
	private static final int MAGIC_ON_GROUNDITEM = 182;
	private static final int MAGIC_ON_PLAYER = 153;

	@Override
	public void handle(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case MAGIC_ON_NPC:
			handleMagicOnNPC(player, packet);
			break;
		case MAGIC_ON_ITEM:
			handleMagicOnItem(player, packet);
			break;
		case MAGIC_ON_GROUNDITEM:
			handleMagicOnGroundItem(player, packet);
			break;
		case MAGIC_ON_PLAYER:
			handleMagicOnPlayer(player, packet);
			break;
		}

	}

	private void handleMagicOnPlayer(final Player player, Packet packet) {
		packet.getShort();
		int playerIndex = packet.getLEShortA();
		int interfaceId = packet.getShort();
		final int spellId = packet.getShort();
		if (interfaceId != player.getMagic().getSpellBook().toInteger()) {
			System.out.println("OMFG HACKERS.");
			return;
		}
		final Player target = (Player) World.getWorld().getPlayers()
				.get(playerIndex);
		if (target == null) {
			return;
		}
		player.setInteractingEntity(target);
		player.getActionSender().sendFollowing(target);
		player.getActionSender().sendFollowingDistance(7);
		player.getActionQueue().cancelQueuedActions();
		player.getActionQueue().addAction(
				new MagicAction(player, target, spellId));
	}

	public class MagicAction extends Action {

		private MagicAction(Entity attacker, Entity target, int spellId) {
			super(attacker, 0);
			this.target = target;
			this.spellId = spellId;
		}

		@Override
		public QueuePolicy getQueuePolicy() {
			return QueuePolicy.NEVER;
		}

		@Override
		public WalkablePolicy getWalkablePolicy() {
			return WalkablePolicy.NON_WALKABLE;
		}

		@Override
		public void execute() {
			Entity caster = getEntity();
			if ((caster == null || target == null)
					&& (caster.isDead() || target.isDead())) {
				this.stop();
				return;
			}
			boolean bool = true; // Interface open..

			if (caster instanceof Player) {
				Player p = (Player) caster;
				bool = p.getInterfaceState().getCurrentInterface() == -1;
			}
			if (caster.getLocation().withinRange(target, 8) && bool) {
				caster.getWalkingQueue().reset();
				if (caster.getCombatDelay() <= 0) {
					SpellMananger.castSpell(caster, target, spellId);
					this.stop();
				}
			}
			this.setDelay(600);
		}

		private final Entity target;
		private final int spellId;
	}

	private void handleMagicOnGroundItem(Player player, Packet packet) {
		int itemX = packet.getShort();
		packet.getShort(); // Hopefully nothing.
		int spellId = packet.getShort();
		int interfaceId = packet.getShort();
		int itemY = packet.getShort();
		int itemId = packet.getShort();
	}

	private void handleMagicOnItem(Player player, Packet packet) {
		int interfaceSet = packet.getInt1();
		int toInterfaceId = interfaceSet >> 16;
		int itemSlot = packet.getShortA();
		packet.getLEShortA();
		int itemId = packet.getLEShort();
		int fromInterfaceId = packet.getShort();
		int spellId = packet.getShort();
		System.out.println("Spell ID: " + spellId);
		Item item = player.getInventory().get(itemSlot);
		if (toInterfaceId != Inventory.INTERFACE
				|| item.getId() != itemId
				|| fromInterfaceId != player.getMagic().getSpellBook()
						.toInteger()) {
			System.out.println("OMFG HACKERS.");
			return;
		}
		SpellMananger.handleMagicOnItem(player, item, spellId, itemSlot);
	}

	private void handleMagicOnNPC(final Player player, Packet packet) {
		int npcIndex = packet.getShortA();
		packet.getLEShort();
		final int spellId = packet.getByte() & 0xFF;
		int interfaceId = packet.getShort();
		packet.getByte();
		if (interfaceId != player.getMagic().getSpellBook().toInteger()) {
			System.out.println("OMFG HACKERS.");
			return;
		}
		final NPC target = (NPC) World.getWorld().getNPCs().get(npcIndex);
		if (!target.getDefinition().isAttackable()) {
			return;
		}
		if (!Combat.canAttackNPC(player, target)) {
			player.getActionSender().sendMessage("This is not mine to attack.");
			player.getWalkingQueue().reset();
			return;
		}
		player.getActionSender().sendFollowing(target);
		player.getActionSender().sendFollowingDistance(7);
		player.setInteractingEntity(target);
		player.getActionQueue().cancelQueuedActions();
		player.getActionQueue().addAction(
				new MagicAction(player, target, spellId));

	}

}
