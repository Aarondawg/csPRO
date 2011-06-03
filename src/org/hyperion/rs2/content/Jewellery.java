package org.hyperion.rs2.content;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Equipment;

public class Jewellery {

	private static final Animation GEM_PRE_CAST_ANIMATION = Animation
			.create(714);
	private static final Graphic GEM_PRE_CAST_GRAPHICS = Graphic.create(301,
			(100 << 16));
	private static final Animation TELEPORTING_ANIMATION = Animation
			.create(715);

	private static Object[][] GLORY_DATA = {
			{ 1706, 1704, "You use you're amulets last charge." },
			{ 1708, 1706, "Your amulet has one charge left." },
			{ 1710, 1708, "Your amulet has two charges left." },
			{ 1712, 1710, "Your amulet has three charges left." }, };

	private static Object[][] RING_OF_DUELING_DATA = {
			{ 2566, -1, "Your ring of dueling crumbles to dust." },
			{ 2564, 2566, "Your ring of dueling has one charge left." },
			{ 2562, 2564, "Your ring of dueling has two charges left." },
			{ 2560, 2562, "Your ring of dueling has three charge left." },
			{ 2558, 2560, "Your ring of dueling has four charge left." },
			{ 2556, 2558, "Your ring of dueling has five charge left." },
			{ 2554, 2556, "Your ring of dueling has six charge left." },
			{ 2552, 2554, "Your ring of dueling has seven charge left." }, };

	private static Object[][] GAMES_NECKLACE_DATA = {
			{ 3867, -1, "Your games necklace crumbles to dust." },
			{ 3865, 3867, "Your games necklace has one charge left." },
			{ 3863, 3865, "Your games necklace has two charges left." },
			{ 3861, 3863, "Your games necklace has three charges left." },
			{ 3859, 3861, "Your games necklace has four charges left." },
			{ 3857, 3859, "Your games necklace has five charges left." },
			{ 3855, 3857, "Your games necklace has six charges left." },
			{ 3853, 3855, "Your games necklace has seven charges left." }, };

	public static enum GemType {
		GLORY, RING_OF_DUELING, GAMES_NECKLACE
	}

	public static boolean rubItem(Player player, int slot, int itemId,
			boolean operating) {
		if (!operating || slot == Equipment.SLOT_AMULET) {
			if (itemId == 1704) {
				player.getActionSender().sendMessage(
						"The amulet has lost its charge.");
				player.getActionSender()
						.sendMessage(
								"It will need to be recharged before you can use it again.");
				return true;
			}
			if (itemId > 1704 && itemId <= 1712) {
				itemId -= 1704;
				if (itemId % 2 == 0) { // Its an equal number..
					int divided = itemId / 2;
					player.getActionSender().sendString("Edgeville", 238, 1);
					player.getActionSender().sendString("Karamja", 238, 2);
					player.getActionSender().sendString("Draynor Village", 238,
							3);
					player.getActionSender().sendString("Al-Kharid", 238, 4);
					player.getActionSender().sendString("Nowhere", 238, 5);
					player.getActionSender().sendChatboxInterface(238);
					player.getJewellery().setGem(GemType.GLORY, divided,
							operating);
					return true;
				}
			} else if (itemId >= 3852 && itemId <= 3867) {
				itemId -= 3851;
				if (itemId % 2 == 0) { // Its an equal number..
					int divided = itemId / 2;
					player.getActionSender().sendChatboxInterface(231);
					player.getActionSender().sendString("Burthorpe Games Room",
							231, 1);
					player.getActionSender().sendString("Barbarian Outpost",
							231, 2);
					player.getActionSender().sendString("Nowhere", 231, 3);
					player.getJewellery().setGem(GemType.GAMES_NECKLACE,
							9 - divided, operating);
					return true;
				}

			}
		}
		if (!operating || slot == Equipment.SLOT_RING) {
			if (itemId >= 2552 && itemId <= 2566) {
				itemId -= 2550;
				if (itemId % 2 == 0) { // Its an equal number..
					int divided = itemId / 2;
					player.getActionSender().sendChatboxInterface(231);
					player.getActionSender().sendString("Castle Wars Arena",
							231, 1);
					player.getActionSender().sendString("Al-Kharid Duel Arena",
							231, 2);
					player.getActionSender().sendString("Nowhere", 231, 3);
					player.getJewellery().setGem(GemType.RING_OF_DUELING,
							9 - divided, operating);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Notice: This may be set, without being used at all. (Eg. if the player
	 * clicks rub, but then clickes the minimap). Sets how many charges the
	 * glory have left, so we can display the correct message.
	 * 
	 * @param gemId
	 *            The id of the specific gem, Glory = 1, Ring of Dueling = 2,
	 *            Games Necklace = 3.
	 * @param charge
	 *            The charge our gem have left. :)
	 * @param isOperating
	 *            true if you're operating the gem, false if not.
	 * */
	public void setGem(GemType gem, int charge, boolean isOperating) {
		this.gem = gem;
		this.gemCharge = charge;
		this.operate = isOperating;
	}

	/**
	 * Gets the players currently rubbed GemType.
	 * 
	 * @return The players GemType.
	 */
	public GemType getGemType() {
		return gem;
	}

	/**
	 * Used for teleporting, while using a glory/ring of duelling/games
	 * necklage.
	 * 
	 * @param player
	 *            The player, who's using a gem for teleporting.
	 * @param location
	 *            The location of where to go. (Edgewille, Castle wars, Duel
	 *            arena).
	 * */
	public void gemTeleport(final Player player, final Location location) {
		if (gem == null || gemCharge == -1 || player.isDead()) {
			return;
		}
		/*
		 * Prevents mass clicking them.
		 */
		if (player.getMagic().getLastTeleport() < 3000) {
			return;
		}
		if (player.getRequestManager().isDueling()) {
			if (player.getLocation().isInDuelArena()) {
				player.getActionSender().sendMessage(
						"You can't teleport while you're dueling.");
				return;
			}
		}
		if (player.isTeleblocked()
				|| player.getLocation().getNewArea(player) != null) {
			player.getActionSender().sendMessage(
					"A magical force stops you from teleporting.");
			return;
		}
		Container con = operate ? player.getEquipment() : player.getInventory();
		switch (gem) {
		/*
		 * Player is using a glory.
		 */
		case GLORY:
			if (Location.getWildernessLevel(player.getLocation()) > 30) {
				player.getActionSender().sendMessage(
						"You cannot teleport above level 30 wilderness.");
				return;
			}
			Object[] data1 = GLORY_DATA[gemCharge - 1];
			if (!con.replace((Integer) data1[0], (Integer) data1[1])) {
				return;
			}
			player.getActionSender().sendMessage((String) data1[2]);
			break;
		/*
		 * Player is using a Ring of Dueling.
		 */
		case RING_OF_DUELING:
			if (Location.getWildernessLevel(player.getLocation()) > 20) {
				player.getActionSender().sendMessage(
						"You cannot teleport above level 20 wilderness.");
				return;
			}
			Object[] data2 = RING_OF_DUELING_DATA[gemCharge - 1];
			if (!con.replace((Integer) data2[0], (Integer) data2[1])) {
				return;
			}
			player.getActionSender().sendMessage((String) data2[2]);
			break;
		/*
		 * Player is using a Games Necklace.
		 */
		case GAMES_NECKLACE:
			if (Location.getWildernessLevel(player.getLocation()) > 20) {
				player.getActionSender().sendMessage(
						"You cannot teleport above level 20 wilderness.");
				return;
			}
			Object[] data3 = GAMES_NECKLACE_DATA[gemCharge - 1];
			if (!con.replace((Integer) data3[0], (Integer) data3[1])) {
				return;
			}
			player.getActionSender().sendMessage((String) data3[2]);
			break;
		}
		player.playAnimation(GEM_PRE_CAST_ANIMATION);
		player.playGraphics(GEM_PRE_CAST_GRAPHICS);
		World.getWorld().submit(new Event(1800) {
			public void execute() {
				player.setTeleportTarget(location);
				player.playAnimation(TELEPORTING_ANIMATION);
				this.stop();
			}
		});
		player.getMagic().setLastTeleport(System.currentTimeMillis());
	}

	public int getRecoilCount() {
		return recoilCount;
	}

	public void setRecoilCount(int recoilCount) {
		this.recoilCount = recoilCount;
	}

	/**
	 * Appends the ring of recoil effect - as far as the player is wearing a
	 * ring, and the target is alive.
	 * 
	 * @param user
	 *            The ring of recoil user.
	 * @param attacker
	 *            The one attacking the user.
	 * @param damage
	 *            The damage inflicted on the user.
	 */
	public void recoilingEffect(Player user, Entity attacker, int damage) {
		if (user.getEquipment().get(Equipment.SLOT_RING) != null
				&& user.getEquipment().get(Equipment.SLOT_RING).getId() == 2550) {
			int newHit = 0;
			/*
			 * The damage is 10, 20, 30, 40 or so.
			 */
			if (damage % 10 == 0) {
				newHit = damage / 10;
				/*
				 * The damage is something else.
				 */
			} else if (damage != 0) {
				int stuff = 100 - damage; // 100 is the biggest hit we can make.
											// (w/e)
				int count = 0;
				while (stuff > 10) {
					stuff -= 10;
					count++;
				}
				newHit = (10 - count);
			}
			if (!attacker.isDead()) {
				Combat.inflictDamage(attacker, null, newHit);
				if (--recoilCount == 0) {
					user.getActionSender().sendMessage(
							"Your ring of recoil crumbles to dust.");
					user.getEquipment().set(Equipment.SLOT_RING, null);
					recoilCount = 40;
				}
			} else {
				user.getActionSender()
						.sendMessage(
								"Your ring of recoil is useless against this creature.");
			}
		}

	}

	/**
	 * What is our gemtype?
	 */
	private GemType gem = null;
	/**
	 * What is the current gem charge?
	 */
	private int gemCharge = -1;
	/**
	 * Are we operating our glory/ring of dueling/games necklace?
	 */
	private boolean operate = false;
	/**
	 * The players ring of recoil count.
	 */
	private int recoilCount = 40;

}
