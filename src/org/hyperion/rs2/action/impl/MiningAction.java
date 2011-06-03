package org.hyperion.rs2.action.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.ObjectList;
import org.hyperion.rs2.content.quest.impl.TutorialIsland;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.region.Region;

/**
 * An action for cutting down trees.
 * 
 * @author Graham Edgecombe
 * 
 */
public class MiningAction extends Action {

	/**
	 * Represents types of axes.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public static enum Pickaxe {

		/**
		 * Rune pickaxe.
		 */
		RUNE(1275, 41, 624, 21),

		/**
		 * Adamant pickaxe.
		 */
		ADAMANT(1271, 31, 628, 15),

		/**
		 * Mithril pickaxe.
		 */
		MITHRIL(1273, 21, 629, 10),

		/**
		 * Steel pickaxe.
		 */
		STEEL(1269, 11, 627, 7),

		/**
		 * Iron pickaxe.
		 */
		IRON(1267, 1, 626, 4),

		/**
		 * Bronze pickaxe.
		 */
		BRONZE(1265, 1, 625, 2);

		/**
		 * The id.
		 */
		private int id;

		/**
		 * The level.
		 */
		private int level;

		/**
		 * The animation.
		 */
		private Animation animation;

		/**
		 * The chance.
		 */
		private final int chance;

		/**
		 * A map of object ids to axes.
		 */
		private static Map<Integer, Pickaxe> pickaxes = new HashMap<Integer, Pickaxe>();

		/**
		 * Gets a axe by an object id.
		 * 
		 * @param object
		 *            The object id.
		 * @return The axe, or <code>null</code> if the object is not a axe.
		 */
		public static Pickaxe forId(int object) {
			return pickaxes.get(object);
		}

		/**
		 * Populates the tree map.
		 */
		static {
			for (Pickaxe pickaxe : Pickaxe.values()) {
				pickaxes.put(pickaxe.id, pickaxe);
			}
		}

		/**
		 * Creates the axe.
		 * 
		 * @param id
		 *            The id.
		 * @param level
		 *            The required level.
		 * @param animation
		 *            The animation id.
		 */
		private Pickaxe(int id, int level, int animation, int chance) {
			this.id = id;
			this.level = level;
			this.animation = Animation.create(animation);
			this.chance = chance;
		}

		/**
		 * Gets the id.
		 * 
		 * @return The id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the required level.
		 * 
		 * @return The required level.
		 */
		public int getRequiredLevel() {
			return level;
		}

		/**
		 * Gets the animation id.
		 * 
		 * @return The animation id.
		 */
		public Animation getAnimation() {
			return animation;
		}

		public int getChance() {
			return chance;
		}
	}

	/**
	 * Represents types of nodes.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public static enum Node {

		/**
		 * Copper ore.
		 */
		COPPER(436, 1, 17.5, new int[] { 2090, 2091, 3042 }, 450, 3000),

		/**
		 * Tin ore.
		 */
		TIN(438, 1, 17.5, new int[] { 2094, 2095, 3043, 11933, 11934, 11935 },
				450, 3000),

		/**
		 * Blurite ore.
		 */
		BLURITE(668, 10, 17.5, new int[] { 2110 }, 450, 5000),

		/**
		 * Iron ore.
		 */
		IRON(440, 15, 35, new int[] { 2092, 2093 }, 450, 6000),

		/**
		 * Silver ore.
		 */
		SILVER(442, 20, 40, new int[] { 2100, 2101 }, 450, 9000),

		/**
		 * Gold ore.
		 */
		GOLD(444, 40, 65, new int[] { 2098, 2099 }, 450, 15000),

		/**
		 * Coal ore.
		 */
		COAL(453, 30, 50, new int[] { 2096, 2097, 11930, 11931, 11932 }, 451,
				10000),

		/**
		 * Mithril ore.
		 */
		MITHRIL(447, 55, 80, new int[] { 2102, 2103 }, 451, 12000),

		/**
		 * Adamantite ore.
		 */
		ADAMANTITE(449, 70, 95, new int[] { 2104, 2105 }, 450, 15000),

		/**
		 * Rune ore.
		 */
		RUNE(451, 85, 125, new int[] { 2106, 2107, 14860 }, 450, 20000),

		/**
		 * Clay ore.
		 */
		CLAY(434, 1, 5, new int[] { 2108, 2109 }, 450, 1200),

		/**
		 * Rune Essence..
		 */
		RUNE_ESSENCE(1436, 1, 5, new int[] { 2491 }, -1, -1);

		/**
		 * A map of object ids to nodes.
		 */
		private static Map<Integer, Node> nodes = new HashMap<Integer, Node>();

		/**
		 * Gets a node by an object id.
		 * 
		 * @param object
		 *            The object id.
		 * @return The node, or <code>null</code> if the object is not a node.
		 */
		public static Node forId(int object) {
			return nodes.get(object);
		}

		/**
		 * Populates the node map.
		 */
		static {
			for (Node node : Node.values()) {
				for (int object : node.objects) {
					nodes.put(object, node);
				}
			}
		}

		/**
		 * The object ids of this node.
		 */
		private int[] objects;

		/**
		 * The minimum level to mine this node.
		 */
		private int level;

		/**
		 * The ore this node contains.
		 */
		private int ore;

		/**
		 * The experience.
		 */
		private double experience;

		private final int emptyId;

		private final long restoreDelay;

		/**
		 * Creates the node.
		 * 
		 * @param ore
		 *            The ore id.
		 * @param level
		 *            The required level.
		 * @param experience
		 *            The experience per ore.
		 * @param objects
		 *            The object ids.
		 */
		private Node(int ore, int level, double experience, int[] objects,
				int empty, long restoreDelay) {
			this.objects = objects;
			this.level = level;
			this.experience = experience;
			this.ore = ore;
			this.emptyId = empty;
			this.restoreDelay = restoreDelay;
		}

		/**
		 * Gets the ore id.
		 * 
		 * @return The ore id.
		 */
		public int getOreId() {
			return ore;
		}

		/**
		 * Gets the object ids.
		 * 
		 * @return The object ids.
		 */
		public int[] getObjectIds() {
			return objects;
		}

		/**
		 * Gets the required level.
		 * 
		 * @return The required level.
		 */
		public int getRequiredLevel() {
			return level;
		}

		/**
		 * Gets the experience.
		 * 
		 * @return The experience.
		 */
		public double getExperience() {
			return experience;
		}

		public int getEmptyId() {
			return emptyId;
		}

		public long getRestoreRelay() {
			return restoreDelay;
		}

	}

	/**
	 * The delay.
	 */
	private int delay = 3000;

	/**
	 * The axe type.
	 */
	private Pickaxe pickaxe;

	/**
	 * The node type.
	 */
	private Node node;

	/**
	 * The rock location
	 */
	private final Location location;

	public final int objectId;

	/**
	 * Creates the <code>WoodcuttingAction</code>.
	 * 
	 * @param player
	 *            The player performing the action.#
	 * @param tree
	 *            The tree.
	 */
	public MiningAction(Player player, Location location, Node node,
			int objectId) {
		super(player, 1500);
		this.node = node;
		this.location = location;
		this.objectId = objectId;
		init();
	}

	public long getHarvestDelay() {
		return delay;
	}

	public void init() {
		final Player player = getPlayer();
		player.face(location);
		if (player.getInventory().freeSlots() == 0) {
			player.getActionSender().sendMessage(
					"There is not enough space in your inventory.");
			this.stop();
			return;
		}
		final int mining = player.getSkills().getLevel(Skills.MINING);
		for (Pickaxe pickaxe : Pickaxe.values()) {
			if ((player.getEquipment().contains(pickaxe.getId()) || player
					.getInventory().contains(pickaxe.getId()))
					&& mining >= pickaxe.getRequiredLevel()) {
				this.pickaxe = pickaxe;
				break;
			}
		}
		if (pickaxe == null) {
			player.getActionSender()
					.sendMessage(
							"You do not have a pickaxe for which you have the level to use.");
			stop();
			return;
		}
		if (mining < node.getRequiredLevel()) {
			player.getActionSender().sendMessage(
					"You do not have the required level to mine this rock.");
			stop();
			return;
		}
		player.getActionSender().sendMessage(
				"You swing your pick at the rock...");
		player.playAnimation(pickaxe.getAnimation());

	}

	/**
	 * Attempts to calculate the number of cycles to mine the ore based on
	 * mining level, ore level and axe speed modifier. Needs heavy work. It's
	 * only an approximation.
	 */
	private int calculateCycles(Player player, Node node, Pickaxe pickaxe) {
		final int mining = player.getSkills().getLevel(Skills.MINING);
		final int difficulty = node.getRequiredLevel();
		final int modifier = pickaxe.getRequiredLevel();
		final int random = new Random().nextInt(3);
		double cycleCount = 1;
		cycleCount = Math.ceil((difficulty * 60 - mining * 20) / modifier
				* 0.25 - random * 4);
		if (cycleCount < 1) {
			cycleCount = 1;
		}
		// player.getActionSender().sendMessage("You must wait " + cycleCount +
		// " cycles to mine this ore.");
		return (int) cycleCount;
	}

	public Item getHarvestedItem() {
		return new Item(node.getOreId(), 1);
	}

	public double getExperience() {
		return node.getExperience();
	}

	public int getSkill() {
		return Skills.MINING;
	}

	@Override
	public QueuePolicy getQueuePolicy() {
		return QueuePolicy.NEVER;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

	private static final Animation RESET = Animation.create(-1);

	private int cycles = 0;

	@Override
	public void execute() {
		final Player player = getPlayer();
		/*
		 * We check if there's an empty rock/tree in the location.
		 */
		if (ObjectList.containsObject(location)) {
			this.stop();
			return;
		}
		Item item = getHarvestedItem();
		if (player.getInventory().hasRoomFor(item)) {
			if (cycles % 2 == 0) {
				player.playAnimation(pickaxe.getAnimation());
			}
			if (giveOre(player, node.getRequiredLevel())) {
				giveRewards(player, item);
				if (removeRock()) {
					this.stop();
				}
			}
		} else {
			this.stop();
			player.playAnimation(RESET);
			player.getActionSender().sendMessage(
					"There is not enough space in your inventory.");
			return;
		}
		cycles++;

	}

	/**
	 * Grants the player his or her reward.
	 * 
	 * @param player
	 *            The player object.
	 * @param reward
	 *            The item reward object.
	 */
	private void giveRewards(Player player, Item reward) {
		if (node.equals(Node.RUNE_ESSENCE)
				&& player.getSkills().getLevel(Skills.MINING) >= 30) {
			reward = new Item(7936);
		}
		if (Inventory.addInventoryItem(player, reward)) {
			ItemDefinition def = reward.getDefinition();
			player.getActionSender().sendMessage(
					"You get some " + def.getName().toLowerCase() + ".");
			player.getSkills().addExperience(getSkill(), getExperience());
			TutorialIsland.mining(player, node);
		}
	}

	private boolean removeRock() {
		if (!node.equals(Node.RUNE_ESSENCE)) {
			createGlobalRock(getPlayer(), node.getEmptyId(), location, 0, 10);
			ObjectList.addToList(location, node.getEmptyId()); // FIXME
			restoreRockEvent(getPlayer(), objectId, location, 0,
					node.getRestoreRelay());
			getPlayer().playAnimation(RESET);
			return true;
		} else {
			return false;
		}
	}

	private static void createGlobalRock(Player player, int objId,
			Location objLoc, int objFace, int objType) {
		for (Region reg : World.getWorld().getRegionManager()
				.getSurroundingRegions(objLoc)) {
			for (final Player p : reg.getPlayers()) {
				p.getActionSender().sendCreateObject(objId, objType, objFace,
						objLoc);
			}
		}
	}

	private boolean giveOre(Player player, int miningLevel) {
		int playerLevel = player.getSkills().getLevelForExperience(
				Skills.MINING);
		int pickaxe = this.pickaxe.getChance();
		double check = pickaxe + Math.round(playerLevel * 0.6)
				- Math.round(miningLevel * 0.8);
		Random r = new Random();
		int rand = r.nextInt(100);
		if (check >= rand) {
			return true;
		} else {
			return false;
		}
	}

	private static void restoreRockEvent(final Player player,
			final int objectId, final Location objLoc, final int objFace,
			long resDelay) {
		World.getWorld().submit(new Event(resDelay) {
			public void execute() {
				createGlobalRock(player, objectId, objLoc, objFace, 10);
				ObjectList.remFromList(objLoc);
				this.stop();
			}
		});

	}

}
