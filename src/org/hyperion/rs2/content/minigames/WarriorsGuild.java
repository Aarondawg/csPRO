package org.hyperion.rs2.content.minigames;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.content.traveling.DoorManager;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.WGCatapultEvent;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.GroundItemController;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.npc.MetalArmour;

/**
 * Handles the Warrior Guild MiniGame.
 * 
 * @author Brown.
 */
public class WarriorsGuild {

	/**
	 * The java.util.Random instance used for warious things.
	 */
	private static final Random r = new Random();

	/**
	 * A list of all the players inside the Cyclops arena.
	 */
	public static final List<Player> IN_GAME = new ArrayList<Player>();

	/**
	 * The tokens we're rewarded through all games in Warriors Guild.
	 */
	public static final int TOKENS = 8851;

	/**
	 * Set of all the Armour items used for the Animation Room. (Bronze - Rune)
	 * {helm, chest, legs}
	 */
	private static final int[][] ARMOUR_SET = { { 1155, 1117, 1075 }, // Bronze
			{ 1153, 1115, 1067 }, // Iron
			{ 1157, 1119, 1069 }, // Steel
			{ 1165, 1125, 1077 }, // Black
			{ 1159, 1121, 1071 }, // Mithril
			{ 1161, 1123, 1073 }, // Adamant
			{ 1163, 1127, 1079 }, // Rune
	};

	/**
	 * Set of all the animated Armour, with indexes corresponding with the
	 * indexes from the 2-d array above.
	 */
	private static final int[] ANIMATED_ARMOURS = { 4278, // Animated Bronze
															// Armour
			4279, // Animated Iron Armour
			4280, // Animated Steel Armour
			4281, // Animated Black Armour
			4282, // Animated Mithril Armour
			4283, // Animated Adamant Armour
			4284, // Animated Rune Armour
	};

	/**
	 * The locations of the two Animator objects, to prevent client hacks.
	 * (Simply editing the file ID)
	 */
	private static final Location ANIMATOR_1 = Location.create(2851, 3536, 0);
	private static final Location ANIMATOR_2 = Location.create(2857, 3536, 0);

	/**
	 * Locations to stand at before placing the Armour's on the animator..
	 */
	private static final Location ANIMATOR_1_STAND = Location.create(2851,
			3537, 0);
	private static final Location ANIMATOR_2_STAND = Location.create(2857,
			3537, 0);

	private static final Animation BONE_BURYING_ANIMATION = Animation
			.create(827);

	/**
	 * Checks if a specific NPC id is an Animated Armour.
	 * 
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public static boolean isMetalArmour(int id) {
		for (int armour : ANIMATED_ARMOURS) {
			if (armour == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Constructs the Warriors guild MiniGame for a specific player.
	 * 
	 * @param player
	 *            The player to construct the Warriors Guild MiniGame for.
	 */
	public WarriorsGuild(Player player) {
		this.player = player;
	}

	/**
	 * Called when our animated Armour dies. This will drop the items the Armour
	 * is build of, as well as the tokens needed.
	 */
	public void dropArmour() {
		assert currentArmour != null; // Makes sure our current Armour isen't
										// null (Really shouldn't be)
		boolean destroyed = false;
		for (int index = 0; index < ANIMATED_ARMOURS.length; index++) {
			if (ANIMATED_ARMOURS[index] == currentArmour.getDefinition()
					.getId()) {
				for (final int armour : ARMOUR_SET[index]) {
					/*
					 * Slight chance, based on how good the Armour is, for the
					 * Armour to be destroyed..
					 */
					if (r.nextInt((index + 1) * 30) == 0 && !destroyed) {
						player.getActionSender().sendMessage(
								"Unfortuantly your "
										+ ItemDefinition.forId(armour)
												.getName().toLowerCase()
										+ " were destroyed."); // FIXME real
																// message pls
						destroyed = true;
					} else {
						GroundItemController.createGroundItem(new Item(armour),
								player, currentArmour.getLocation());
					}
				}
				GroundItemController.createGroundItem(new Item(TOKENS,
						(index + 1) * 5) /* Lol hacks. */, player, currentArmour
						.getLocation());
			}
		}
		currentArmour = null;
	}

	private Item defenderDrop = null;

	/**
	 * Called everytime this player is killing a cyclop. This will randomly drop
	 * the next RuneDefender for us.
	 */
	public void killedCyclop(Location pos) {
		/* Make sure our player is actually in game.. */
		if (IN_GAME.contains(player)) {
			if (r.nextInt(30) == 0 && defenderDrop != null) {
				GroundItemController
						.createGroundItem(defenderDrop, player, pos);
			}
		}
	}

	/**
	 * Called once this player runs out of tokens..
	 */
	public void outOfTokens() {
		final DialogueLoader kamfreena = DialogueLoader.forId(4289);
		DialogueLoader.getNextDialogue(player, kamfreena, 18);
		World.getWorld().submit(new Event(25000) {

			@Override
			public void execute() {
				if (IN_GAME.contains(player)) {
					DialogueLoader.getNextDialogue(player, kamfreena, 19);
					IN_GAME.remove(player);
					player.setTeleportTarget(Location.create(2844, 3539, 2));
				}
				this.stop();
			}

		});

	}

	private boolean readyToEnter = false;

	/**
	 * Called every time a player got its approval of entering the door.
	 */
	public void dialogueFinished() {
		player.getActionSender().sendCloseInterface();
		readyToEnter = true;
	}

	/**
	 * Gets the next defender drop, based on the players currently worn
	 * defender.
	 * 
	 * @return The next defender to drop.
	 */
	private Item getNextDefender(Item currentDefender) {
		if (currentDefender == null) {
			return new Item(8844);
		} else if (currentDefender.getId() == 8850) {
			return currentDefender;
		} else {
			return new Item(currentDefender.getId() + 1);
		}
	}

	public boolean handleDoorClick(Location loc) {
		final DialogueLoader kamfreena = DialogueLoader.forId(4289);
		System.out.println(kamfreena);
		/*
		 * Game Door entrance thing ;)
		 */
		if (loc.equals(GAME_DOOR_1) || loc.equals(GAME_DOOR_2)) {
			if (IN_GAME.contains(player)) {
				if (DoorManager.handleDoor(player, loc, -1)) {
					IN_GAME.remove(player);
				}
			} else {
				if (!readyToEnter) {
					// Make sure we have at least 100 tokens..
					if (!player.getInventory().contains(new Item(TOKENS, 100))) {
						DialogueLoader.getNextDialogue(player, kamfreena, 17);
					} else {
						Item defender = getCurrentDefender();
						if (defender == null) { // You can now have a bronze
												// defender. :o
							DialogueLoader
									.getNextDialogue(player, kamfreena, 9);
						} else if (defender.getId() == 8844) {// Bronze
							DialogueLoader.getNextDialogue(player, kamfreena,
									10);
						} else if (defender.getId() == 8850) {// Rune
							DialogueLoader.getNextDialogue(player, kamfreena,
									14);
						} else { // Anything else.. :s
							DialogueLoader.getNextDialogue(player, kamfreena,
									12);
						}
					}
				} else {
					if (DoorManager.handleDoor(player, loc, -1)) {
						IN_GAME.add(player);
						readyToEnter = false;
						defenderDrop = getNextDefender(getCurrentDefender());
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Gets the players currently worm Defender.
	 * 
	 * @return null if nothing found, else the players highest worn Defender.
	 */
	private Item getCurrentDefender() {
		Item defender = null; // The best defender so far.
		Item shield = player.getEquipment().get(Equipment.SLOT_SHIELD);
		if (shield != null
				&& shield.getDefinition().getName().contains("defender")) {
			defender = shield;
		}
		Item[] inv = player.getInventory().toArray();

		for (final Item item : inv) {
			if (item != null) {
				if (item.getDefinition().getName().contains("defender")) {
					// The one with the highest ID is the best one. xD
					if (defender == null || defender.getId() < item.getId()) {
						defender = item;
					}
				}
			}
		}
		return defender;
	}

	private static final Location GAME_DOOR_1 = Location.create(2847, 3540, 2);
	private static final Location GAME_DOOR_2 = Location.create(2847, 3541, 2);

	/**
	 * Handles any item on object actions to do with Warriors Guild.
	 * 
	 * @param item
	 *            The item used on an object.
	 * @param objectId
	 *            The object id.
	 * @param loc
	 *            The location of the object.
	 * @return <code>true</code> if there was an action to handle,
	 *         <code>false</code> if not.
	 */
	public boolean handleItemOnObject(final Item item, int objectId,
			final Location loc) {
		/*
		 * Magical Animator
		 */
		if (objectId == 15621
				&& (loc.equals(ANIMATOR_1) || loc.equals(ANIMATOR_2))) {
			/*
			 * The client will automatically walk to a specific point, lets wait
			 * for it. (Normal within distance thing doesn't work..)
			 */
			player.getActionQueue().cancelQueuedActions();
			final Location walkTo = loc.equals(ANIMATOR_1) ? ANIMATOR_1_STAND
					: ANIMATOR_2_STAND;
			player.getActionQueue().addAction(new Action(player, 0) {

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
					if (player.getLocation().equals(walkTo)) {
						/*
						 * We loop through all Armour types..
						 */
						for (int index = 0; index < ARMOUR_SET.length; index++) {
							/*
							 * We loop through the ID's for all the Armour
							 * types.
							 */
							for (int armour : ARMOUR_SET[index]) {
								/*
								 * We check if the Armour set contains the ID of
								 * the item used on an object..
								 */
								if (armour == item.getId()) {
									/*
									 * We make sure our inventory contains all
									 * the needed items..
									 */
									boolean stop = false;
									for (int armour1 : ARMOUR_SET[index]) {
										if (!player.getInventory().contains(
												armour1)) {
											String name = ItemDefinition
													.forId(armour1).getName()
													.toLowerCase();
											player.getActionSender()
													.sendMessage(
															"You're missing a"
																	+ (name.startsWith("A") ? "n "
																			: " ")
																	+ name
																	+ " in order to summon this Armour."); // FIXME
											stop = true;
										}
									}
									if (!stop) {
										/*
										 * We make sure this is the only Armour
										 * we have spawned..
										 */
										if (currentArmour == null) {
											/*
											 * Remove the Armour set from our
											 * inventory.
											 */
											for (int armour1 : ARMOUR_SET[index]) {
												player.getInventory().remove(
														new Item(armour1));
											}

											/*
											 * We bend down, send a message..
											 * //Should be an interface. :S
											 */
											player.playAnimation(BONE_BURYING_ANIMATION);
											player.setCanWalk(false);
											player.getActionSender()
													.sendString(
															"You place your armour on the platform where it",
															216, 0);
											player.getActionSender()
													.sendString(
															"disappears....",
															216, 1);
											player.getActionSender()
													.sendChatboxInterface(216);
											final int fIndex = index;
											World.getWorld().submit(
													new Event(2400) {

														@Override
														public void execute() {
															player.getActionSender()
																	.sendString(
																			"The animator hums, something appears to be working.",
																			216,
																			0);
															player.getActionSender()
																	.sendString(
																			"You stand back...",
																			216,
																			1);
															World.getWorld()
																	.submit(new Event(
																			1200) {

																		@Override
																		public void execute() {
																			/*
																			 * We
																			 * walk
																			 * a
																			 * few
																			 * steps
																			 * back
																			 * .
																			 * .
																			 */
																			player.getWalkingQueue()
																					.reset();
																			player.getWalkingQueue()
																					.setRunningQueue(
																							false);
																			player.getWalkingQueue()
																					.addStep(
																							player.getLocation()
																									.getX(),
																							player.getLocation()
																									.getY() + 3);
																			player.getWalkingQueue()
																					.finish();

																			World.getWorld()
																					.submit(new Event(
																							2400) {

																						@Override
																						public void execute() {
																							player.setCanWalk(true);
																							player.getActionSender()
																									.sendCloseInterface();
																							/*
																							 * Set
																							 * our
																							 * currently
																							 * spawned
																							 * Armour
																							 * to
																							 * the
																							 * Animated
																							 * Armour
																							 * matching
																							 * our
																							 * armour
																							 * set
																							 * .
																							 */
																							currentArmour = new MetalArmour(
																									NPCDefinition
																											.forId(ANIMATED_ARMOURS[fIndex]),
																									loc,
																									player);

																							/*
																							 * We
																							 * face
																							 * the
																							 * armour
																							 * .
																							 * .
																							 */
																							player.setInteractingEntity(currentArmour);

																							/*
																							 * And
																							 * add
																							 * it
																							 * in
																							 * the
																							 * actual
																							 * world
																							 * as
																							 * well
																							 * .
																							 */
																							World.getWorld()
																									.getNPCs()
																									.add(currentArmour);

																							/*
																							 * Place
																							 * a
																							 * hint
																							 * icon
																							 * above
																							 * the
																							 * NPC
																							 * .
																							 * .
																							 */
																							player.getActionSender()
																									.sendNPCHints(
																											currentArmour
																													.getIndex());
																							this.stop();
																						}
																					});
																			this.stop();

																		}

																	});
															this.stop();
														}

													});
										} else {
											player.getActionSender()
													.sendMessage(
															"You've already spawned an Armour."); // FIXME
										}
									}
								}
							}
						}
						this.stop();
					}
					this.setDelay(600);
				}

			});
			/*
			 * We handled what stuff related to the Animator, and every thing is
			 * cool. (Missing the "nothing interesting happens" though, but I
			 * couldn't think of other ways. *Shitty ActionSystem*
			 */
			return true;
		}
		return false;
	}

	/**
	 * Gets the players Capapult defence style..
	 */
	public int getDefenceStyle() {
		return defenceStyle;
	}

	public void setDefenceStyle(int button) {
		int oldButton = this.defenceStyle + 9;
		int style = button - 9;
		player.getActionSender().sendString(
				"<col=FF8040>" + WGCatapultEvent.STYLES[this.defenceStyle],
				411, oldButton);
		player.getActionSender().sendString(
				"<col=000000>" + WGCatapultEvent.STYLES[style], 411, button);
		this.defenceStyle = style;
	}

	private int defenceStyle = 0; // Magic defence by default..

	public void increaseCatapultTokens() {
		player.getInventory().add(new Item(TOKENS));
		// catapultTokens++;
	}

	private int catapultTokens = 0; // Should this be saved?

	/**
	 * Gets the current animated Armour spawned.
	 * 
	 * @return The currently spawned animation Armour.
	 */
	public MetalArmour getCurrentArmour() {
		return currentArmour;
	}

	/**
	 * The currently summoned Metal Armour.
	 */
	private MetalArmour currentArmour = null;

	/**
	 * The player we're going to handle Warriors Guild for.
	 */
	private final Player player;

}
