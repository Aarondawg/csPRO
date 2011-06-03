package org.hyperion.rs2.action.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.ObjectList;
import org.hyperion.rs2.content.quest.Quest;
import org.hyperion.rs2.content.quest.QuestHandler;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.GameObjectDefinition;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.region.Region;

/**
 * An action for cutting down trees.
 * 
 * @author Graham Edgecombe
 * 
 */
public class WoodcuttingAction extends Action {

	/**
	 * Represents types of axes.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public static enum Axe {

		/**
		 * Dragon axe.
		 */
		DRAGON(6739, 61, 2846),

		/**
		 * Rune axe.
		 */
		RUNE(1359, 41, 867),

		/**
		 * Adamant axe.
		 */
		ADAMANT(1357, 31, 869),

		/**
		 * Mithril axe.
		 */
		MITHRIL(1355, 21, 871),

		/**
		 * Black axe.
		 */
		BLACK(1361, 6, 873),

		/**
		 * Steel axe.
		 */
		STEEL(1353, 6, 875),

		/**
		 * Iron axe.
		 */
		IRON(1349, 1, 877),

		/**
		 * Bronze axe.
		 */
		BRONZE(1351, 1, 879);

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
		 * A map of object ids to axes.
		 */
		private static Map<Integer, Axe> axes = new HashMap<Integer, Axe>();

		/**
		 * Gets a axe by an object id.
		 * 
		 * @param object
		 *            The object id.
		 * @return The axe, or <code>null</code> if the object is not a axe.
		 */
		public static Axe forId(int object) {
			return axes.get(object);
		}

		/**
		 * Populates the tree map.
		 */
		static {
			for (Axe axe : Axe.values()) {
				axes.put(axe.id, axe);
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
		private Axe(int id, int level, int animation) {
			this.id = id;
			this.level = level;
			this.animation = Animation.create(animation);
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

	}

	/**
	 * Represents types of trees.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public static enum Tree {

		/**
		 * Normal tree.
		 */
		NORMAL(1511, 1, 50, new int[] { 1276, 1277, 1278, 1279, 1280, 1282,
				1283, 1284, 1285, 1286, 1289, 1290, 1291, 1315, 1316, 1318,
				1319, 1330, 1331, 1332, 1365, 1383, 1384, 2409, 3033, 3034,
				3035, 3036, 3881, 3882, 3883, 5902, 5903, 5904, 14308, 14309 },
				new int[] { 1342 /* Normal tree for now. */}, 1, 5000),

		/**
		 * Willow tree.
		 */
		WILLOW(1519, 30, 135, new int[] { 1308, 5551, 5552, 5553 },
				new int[] { 7399 }, 8, 10000),

		/**
		 * Oak tree.
		 */
		OAK(1521, 15, 75, new int[] { 1281, 3037 }, new int[] { 1349 }, 5, 8000),

		/**
		 * Magic tree.
		 */
		MAGIC(1513, 75, 500, new int[] { 1292, 1306 }, new int[] { 7402 }, 15,
				30000), // one of those are the Dramen tree. xD

		/**
		 * Maple tree.
		 */
		MAPLE(1517, 45, 200, new int[] { 1307, 4677 }, new int[] { 1356 }, 10,
				15000),

		/**
		 * Mahogany tree.
		 */
		MAHOGANY(6332, 50, 250, new int[] { 9034 }, new int[] { 1355 }, 5,
				20000),

		/**
		 * Teak tree.
		 */
		TEAK(6333, 35, 170, new int[] { 9036 }, new int[] { 1354 }, 5, 20000),

		/**
		 * Achey tree.
		 */
		ACHEY(2862, 1, 50, new int[] { 2023 }, new int[] { 3371 }, 1, 5000),

		/**
		 * Yew tree.
		 */
		YEW(1515, 60, 350, new int[] { 1309 }, new int[] { 1355 }, 13, 20000);

		/**
		 * A map of object ids to trees.
		 */
		private static Map<Integer, Tree> trees = new HashMap<Integer, Tree>();

		/**
		 * Gets a tree by an object id.
		 * 
		 * @param object
		 *            The object id.
		 * @return The tree, or <code>null</code> if the object is not a tree.
		 */
		public static Tree forId(int object) {
			return trees.get(object);
		}

		/**
		 * Populates the tree map.
		 */
		static {
			for (Tree tree : Tree.values()) {
				for (int object : tree.objects) {
					trees.put(object, tree);
				}
			}
		}

		/**
		 * The stumps of the tree.
		 */
		private int[] stumps;

		/**
		 * The object ids of this tree.
		 */
		private int[] objects;

		/**
		 * The minimum level to cut this tree down.
		 */
		private int level;

		/**
		 * The log of this tree.
		 */
		private Item log;

		/**
		 * The experience.
		 */
		private double experience;

		/**
		 * The chance of turning a tree into a stump.
		 */
		private int chance;

		/**
		 * The restore delay.
		 */
		private int restoreDelay;

		/**
		 * Creates the tree.
		 * 
		 * @param log
		 *            The log id.
		 * @param level
		 *            The required level.
		 * @param experience
		 *            The experience per log.
		 * @param objects
		 *            The object ids.
		 */
		private Tree(int log, int level, double experience, int[] objects,
				int[] stumps, int chance, int restoreDelay) {
			this.objects = objects;
			this.level = level;
			this.experience = experience;
			this.log = new Item(log);
			this.stumps = stumps;
			this.chance = chance;
			this.restoreDelay = restoreDelay;
		}

		/**
		 * Gets the chance.
		 * 
		 * @return The chance.
		 */
		public int getChance() {
			return chance;
		}

		/**
		 * Gets the log id.
		 * 
		 * @return The log id.
		 */
		public Item getLogId() {
			return log;
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
		 * Gets the stump ids.
		 * 
		 * @return The stump ids.
		 */
		public int[] getStumpIds() {
			return stumps;
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

		/**
		 * Gets the tree's restore delay.
		 * 
		 * @return The restore delay.
		 */
		public int getRestoreRelay() {
			return restoreDelay;
		}

	}

	/**
	 * The axe type.
	 */
	private Axe axe;

	/**
	 * The tree type.
	 */
	private Tree tree;

	/**
	 * Creates the <code>WoodcuttingAction</code>.
	 * 
	 * @param player
	 *            The player performing the action.#
	 * @param tree
	 *            The tree.
	 */
	public WoodcuttingAction(Player player, Location location, Tree tree,
			int objectId) {
		super(player, 1800);
		this.tree = tree;
		this.location = location;
		this.objectId = objectId;
		init();
	}

	private final int objectId;

	private final Location location;

	public Location getLocation() {
		return location;
	}

	public long getHarvestDelay() {
		int req = tree.getRequiredLevel();
		int level = getPlayer().getSkills().getLevel(Skills.WOODCUTTING);
		int diff = (int) ((level - req) / 10);
		int delay = (10 - diff);
		if (delay <= 0) {
			delay = 1;
		}
		if (delay > 5) {
			delay = 5;
		}
		return ((r.nextInt(delay) + 1) + r.nextInt(3)) * 1000;
	}

	public void init() {
		final Player player = getPlayer();
		player.face(location.getActualLocation(GameObjectDefinition.forId(
				objectId).getBiggestSize()));
		if (player.getInventory().freeSlots() == 0) {
			player.getActionSender().sendMessage(
					"There is not enough space in your inventory.");
			this.stop();
			return;
		}
		final int wc = player.getSkills().getLevel(Skills.WOODCUTTING);
		for (Axe axe : Axe.values()) {
			if ((player.getEquipment().contains(axe.getId()) || player
					.getInventory().contains(axe.getId()))
					&& wc >= axe.getRequiredLevel()) {
				this.axe = axe;
				break;
			}
		}
		if (axe == null) {
			player.getActionSender().sendMessage(
					"You do not have an axe that you can use.");
			this.stop();
			return;
		}
		if (wc < tree.getRequiredLevel()) {
			player.getActionSender()
					.sendMessage(
							"You do not have the required level to cut down that tree.");
			this.stop();
			return;
		}
		player.getActionSender().sendMessage(
				"You swing your axe at the tree...");
		player.playAnimation(getAnimation());
	}

	private static final Random r = new Random();

	public int getCycles() {
		if (tree == Tree.NORMAL || tree == Tree.ACHEY) {
			return 1;
		} else {
			return r.nextInt(tree.getChance()) + 5;
		}
	}

	public Item getHarvestedItem() {
		return tree.getLogId();
	}

	public double getExperience() {
		return tree.getExperience();
	}

	public Animation getAnimation() {
		return axe.getAnimation();
	}

	public int getSkill() {
		return Skills.WOODCUTTING;
	}

	public void removeTree() {
		createGlobalTree(getPlayer(), tree.getStumpIds()[0], getLocation(), 0,
				10);// FIXME
		ObjectList.addToList(getLocation(), tree.getStumpIds()[0]); // FIXME
		restoreTreeEvent(getPlayer(), getLocation(), 0, tree.getRestoreRelay());
		getPlayer().playAnimation(RESET);
	}

	private static void createGlobalTree(Player player, int objId,
			Location objLoc, int objFace, int objType) {
		for (Region reg : World.getWorld().getRegionManager()
				.getSurroundingRegions(objLoc)) {
			for (final Player p : reg.getPlayers()) {
				p.getActionSender().sendCreateObject(objId, objType, objFace,
						objLoc);
			}
		}
	}

	private void restoreTreeEvent(final Player player, final Location objLoc,
			final int objFace, int resDelay) {
		World.getWorld().submit(new Event(resDelay) {
			public void execute() {
				createGlobalTree(player, objectId, objLoc, objFace, 10);
				ObjectList.remFromList(objLoc);
				this.stop();
			}
		});
	}

	@Override
	public QueuePolicy getQueuePolicy() {
		return QueuePolicy.NEVER;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

	public int cycles = 1;

	private static boolean giveLog(Player player, int requiredLevel) {
		int playerLevel = player.getSkills().getLevelForExperience(
				Skills.WOODCUTTING);
		int axe = checkHatchet(player);
		double check = axe + Math.round(playerLevel * 0.6)
				- Math.round(requiredLevel * 0.8);
		Random r = new Random();
		int rand = r.nextInt(100);
		if (check >= rand) {
			return true;
		} else {
			return false;
		}
	}

	public static int checkHatchet(Player player) {
		if ((player.getInventory().contains(6739) || Equipment.containsWeapon(
				player, 6739))
				&& player.getSkills().getLevelForExperience(Skills.WOODCUTTING) >= 61)// Rune
																						// axe
			return 35;
		else if ((player.getInventory().contains(1359) || Equipment
				.containsWeapon(player, 1359))
				&& player.getSkills().getLevelForExperience(Skills.WOODCUTTING) >= 41)// Rune
																						// axe
			return 25;
		else if ((player.getInventory().contains(1357) || Equipment
				.containsWeapon(player, 1357))
				&& player.getSkills().getLevelForExperience(Skills.WOODCUTTING) >= 31)// Adamant
																						// axe
			return 15;
		else if ((player.getInventory().contains(1355) || Equipment
				.containsWeapon(player, 1355))
				&& player.getSkills().getLevelForExperience(Skills.WOODCUTTING) >= 21)// Mithril
																						// axe
			return 13;
		else if ((player.getInventory().contains(1361) || Equipment
				.containsWeapon(player, 1361))
				&& player.getSkills().getLevelForExperience(Skills.WOODCUTTING) >= 11)// Black
																						// axe
			return 10;
		else if ((player.getInventory().contains(1353) || Equipment
				.containsWeapon(player, 1353))
				&& player.getSkills().getLevelForExperience(Skills.WOODCUTTING) >= 6)// Steel
																						// axe
			return 7;
		else if (player.getInventory().contains(1349)
				|| Equipment.containsWeapon(player, 1349))// Iron axe
			return 4;
		else if (player.getInventory().contains(1351)
				|| Equipment.containsWeapon(player, 1351))// Bronze axe
			return 3;
		else
			return -1;
	}

	private static final Animation RESET = Animation.create(-1);

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
		boolean haveSpace = player.getInventory().hasRoomFor(item);
		if (haveSpace) {
			if (cycles % 2 == 0) {
				player.playAnimation(getAnimation());
			}
			if (giveLog(player, tree.getRequiredLevel())) {
				giveRewards(player, item);
				if (removeTree(item.getId(), tree.getChance())) {
					removeTree();
					this.stop();
				} else if (!player.getInventory().hasRoomFor(item)) {
					haveSpace = false;
				}
			}
		}
		if (!haveSpace) {
			this.stop();
			player.playAnimation(RESET);
			player.getActionSender().sendMessage(
					"There is not enough space in your inventory.");
			return;
		}
		cycles++;
	}

	private static boolean removeTree(int itemId, int chance) {
		if (itemId == 2862 || itemId == 1511) {// Normal and Achey logs.
			return true;
		}
		if (r.nextInt(chance) == 0) {
			return true;
		} else {
			return false;
		}
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
		if (Inventory.addInventoryItem(player, reward)) {
			ItemDefinition def = reward.getDefinition();
			player.getActionSender().sendMessage(
					"You get some " + def.getName().toLowerCase() + ".");
			player.getSkills().addExperience(getSkill(), getExperience());
			Quest quest = QuestHandler.getQuest(0);
			if (!quest.isFinished(player)) {
				quest.handleObjectClicking(player, objectId, location, 100);
			}
		}
	}

}
