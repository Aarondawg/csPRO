package org.hyperion.rs2.content.minigames;

import java.util.Random;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.InterfaceState.InterfaceOption;
import org.hyperion.rs2.model.container.Inventory;

public class Barrows {

	/**
	 * The java.util.Random instance used for several things.
	 */
	private static final Random r = new Random();

	/**
	 * Mind, chaos, death and blood runes.
	 */
	public static final int[][] RUNES = { { 565, 100 } /* Blood Rune */,
			{ 558, 1000 } /* Mind Rune */, { 560, 300 } /* Death Rune */,
			{ 562, 526 } /* Chaos rune */
	};

	/**
	 * Barrow equipment.
	 */
	public static final Item[] BARROWS_EQUIPMENT = { new Item(4708),
			new Item(4710), new Item(4712), new Item(4714), new Item(4716),
			new Item(4718), new Item(4720), new Item(4722), new Item(4724),
			new Item(4726), new Item(4728), new Item(4730), new Item(4732),
			new Item(4734), new Item(4736), new Item(4738), new Item(4745),
			new Item(4747), new Item(4749), new Item(4751), new Item(4753),
			new Item(4755), new Item(4755), new Item(4759) };

	/**
	 * Holds the locations of all the tombs.
	 */
	public static final Location[] TOMB_LOCATIONS = {
			Location.create(3555, 9698, 3), // Ahrim tomb.
			Location.create(3554, 9714, 3), // Dharoks tomb.
			Location.create(3538, 9703, 3), // Guthan tomb.
			Location.create(3550, 9682, 3), // Karil tomb.
			Location.create(3569, 9685, 3), // Torag tomb.
			Location.create(3573, 9705, 3), // Veracs tomb.
	};

	/**
	 * Holds all the barrows brothers.
	 */
	public static final int[] BARROWS_BROTHERS = { 2025, // Ahrim.
			2026, // Dharock.
			2027, // Guthan.
			2028, // Karil.
			2029, // Torag.
			2030, // Verac.

	};

	/**
	 * This will contain the locations of all the doors in barrows. {room}
	 * {north, south, west, east}
	 * 
	 */
	private static final Location[][][] DOOR_MAP = {
	/* Start of the center room. */
	{ { Location.create(3551, 9701, 0), Location.create(3552, 9701, 0) }, // North
			{ Location.create(3551, 9688, 0), Location.create(3552, 9688, 0) }, // South
			{ Location.create(3545, 9694, 0), Location.create(3545, 9695, 0) }, // West
			{ Location.create(3558, 9694, 0), Location.create(3558, 9695, 0) }, // East
	},
	/* End of the center room. */
	};

	/**
	 * Constructs the barrows class.
	 */
	public Barrows() {
		setupGame();
	}

	/**
	 * Handles the players object clicking in Barrows.
	 * 
	 * @param player
	 *            The clicking player.
	 * @param location
	 *            The location of the object.
	 * @param objectId
	 *            The object id.
	 */
	public static boolean clickObject(final Player player, Location location,
			int objectId) {
		for (int index = 0; index < TOMB_LOCATIONS.length; index++) {
			if (TOMB_LOCATIONS[index].equals(location)) {
				int tombInfo = player.getBarrows().getTombInformation()[index];
				if (tombInfo == 2 || tombInfo == 3) {
					player.setTemporaryAttribute("InterfaceOption",
							InterfaceOption.BARROWS);
					player.getActionSender().sendString("Enter the Tunnels?",
							229, 0);
					player.getActionSender().sendString("Yes.", 229, 1);
					player.getActionSender().sendString(
							"I don't think I'm ready yet.", 229, 2);
					player.getActionSender().sendChatboxInterface(229);
				} else if (tombInfo == 1) {
					player.getActionSender().sendMessage(
							"You've already defeated "
									+ NPCDefinition.forId(
											BARROWS_BROTHERS[index]).getName()
									+ ".");
				} else {
					if (player.getBarrows().getSummoned() == null) {
						final NPC brother = NPC.create(
								NPCDefinition.forId(BARROWS_BROTHERS[index]),
								player.getLocation(), null, null);
						player.getBarrows().setSummoned(brother);
						player.addPrivateNPC(brother);
						Combat.attack(brother, player);
						World.getWorld().getNPCs().add(brother);
						brother.forceChat("You dare disturb my rest!");
						player.getActionSender().sendNPCHints(
								brother.getIndex());

						final int fIndex = index;
						World.getWorld().submit(new Event(600) {

							private int timeOut = 60000 * 5;

							@Override
							public void execute() {
								/*
								 * If 5 minutes passed by or the player is more
								 * than 10 squares away, we remove the NCP, stop
								 * the event, and return.
								 */
								if (--timeOut == 0
										|| brother.getLocation().getDistance(
												player.getLocation()) > 10
										|| brother.getLocation().getZ() != player
												.getLocation().getZ()) {
									brother.destroy();
									player.lastObjectClick = System
											.currentTimeMillis() + 2000;
									World.getWorld().getNPCs().remove(brother);
									player.getBarrows().setSummoned(null);
									player.removePrivateNPC(brother);
									player.getActionSender().sendNPCHints(-1);
									this.stop();
									return;
								}
								/*
								 * If the barrows brother is dead, we increase
								 * the tomb info, and the kill count.
								 */
								if (brother.isDead()) {
									World.getWorld().submit(new Event(4000) {

										@Override
										public void execute() {
											brother.destroy();
											player.lastObjectClick = System
													.currentTimeMillis() + 2000;
											World.getWorld().getNPCs()
													.remove(brother);
											player.getBarrows().setSummoned(
													null);
											player.getBarrows().setTombState(
													fIndex, 1);
											player.removePrivateNPC(brother);
											player.getActionSender()
													.sendNPCHints(-1);
											this.stop();

										}

									});
								}

							}

						});
					} else {
						player.getActionSender().sendMessage(
								"You are already fighting "
										+ NPCDefinition.forId(
												BARROWS_BROTHERS[index])
												.getName() + "!");
					}
				}
				return true;
			}
		}
		switch (objectId) {
		case 6703:
			player.setTeleportTarget(Location.create(3574, 3297, 0));
			return true;
		case 6704:
			player.setTeleportTarget(Location.create(3576, 3281, 0));
			return true;
		case 6702:
			player.setTeleportTarget(Location.create(3564, 3289, 0));
			return true;
		case 6707:
			player.setTeleportTarget(Location.create(3556, 3297, 0));
			return true;
		case 6706:
			player.setTeleportTarget(Location.create(3553, 3283, 0));
			return true;
		case 6705:
			player.setTeleportTarget(Location.create(3565, 3276, 0));
			return true;
			// The door is locked with a strange puzzle
			// You hear the doors' locking mechanism grind open.
			/*
			 * Barrows chest.
			 */
		case 10284:
			player.getBarrows().claim(player);
			return true;
		default:
			return false;
		}
	}

	/**
	 * Digs with a spade to get into the barrows' crypts.
	 * 
	 * @param player
	 *            - The player that this method is applied to.
	 */
	public static boolean digMound(Player player) {
		Location location = null;
		if (player.getLocation().dharoksMound()) {
			location = Location.create(3556, 9718, 3);
		}
		if (player.getLocation().guthansMound()) {
			location = Location.create(3534, 9704, 3);
		}
		if (player.getLocation().ahrimsMound()) {
			location = Location.create(3557, 9703, 3);
		}
		if (player.getLocation().veracsMound()) {
			location = Location.create(3578, 9706, 3);
		}
		if (player.getLocation().toragsMound()) {
			location = Location.create(3568, 9683, 3);
		}
		if (player.getLocation().karilsMound()) {
			location = Location.create(3546, 9684, 3);
		}
		if (location != null) {
			player.setTeleportTarget(location);
			player.getActionSender().sendMessage("You've broken into a crypt!");
			return true;
		}
		return false;
	}

	private static final Graphic HUGE_ROCK_FALLING = Graphic.create(406,
			(100 << 16));
	private static final Graphic ROCKS_FALLING = Graphic.create(405,
			(100 << 16));

	/**
	 * Every 5 second, you'd get hit by either a big or some small rocks.
	 * 
	 * @param player
	 *            The player being hit.
	 */
	private void rocks(final Player player) {
		player.getActionSender().sendEarthQuake();
		World.getWorld().submit(new Event(5000) {

			@Override
			public void execute() {
				if (!gameFinished) {
					player.getActionSender().sendStopEarthQuake();
					this.stop();
				} else {
					if (r.nextInt(4) == 0) {
						// Big ass rock.
						player.playGraphics(HUGE_ROCK_FALLING);
						Combat.inflictDamage(player, null, r.nextInt(12) + 1);
					} else {
						// Normal rocks.
						player.playGraphics(ROCKS_FALLING);
						Combat.inflictDamage(player, null, r.nextInt(3) + 1);
					}
				}
			}

		});

	}

	/**
	 * Gets an array of the players tomb information.
	 * 
	 * @return The players tomb information.
	 */
	public int[] getTombInformation() {
		return tombInformation;
	}

	/**
	 * Increases the barrows kill count.
	 */
	public void increaseKillCount() {
		killCount++;
	}

	/**
	 * Gets the kill count integer.
	 * 
	 * @return The players kill count.
	 */
	public int getKillCount() {
		return killCount;
	}

	/**
	 * Spawns the missing barrows brother.
	 * 
	 * @param player
	 *            The player.
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public boolean spawnMissingBrother(final Player player) {
		int index;
		for (index = 0; index < tombInformation.length; index++) {
			if (tombInformation[index] == 2) {
				final NPC brother = NPC.create(
						NPCDefinition.forId(BARROWS_BROTHERS[index]),
						player.getLocation(), null, null);
				player.getBarrows().setSummoned(brother);
				player.addPrivateNPC(brother);
				Combat.attack(brother, player);
				World.getWorld().getNPCs().add(brother);
				player.getActionSender().sendNPCHints(brother.getIndex());
				final int fIndex = index;
				World.getWorld().submit(new Event(600) {

					private int timeOut = 60000 * 5;

					@Override
					public void execute() {
						/*
						 * If 5 minutes passed by or the player is more than 10
						 * squares away, we remove the NCP, stop the event, and
						 * return.
						 */
						if (--timeOut == 0
								|| brother.getLocation().getDistance(
										player.getLocation()) > 10
								|| brother.getLocation().getZ() != player
										.getLocation().getZ()) {
							brother.destroy();
							World.getWorld().getNPCs().remove(brother);
							player.getBarrows().setSummoned(null);
							player.removePrivateNPC(brother);
							player.getActionSender().sendNPCHints(-1);
							this.stop();
							return;
						}
						/*
						 * If the barrows brother is dead, we increase the tomb
						 * info,
						 */
						if (brother.isDead()) {
							World.getWorld().submit(new Event(4000) {

								@Override
								public void execute() {
									brother.destroy();
									World.getWorld().getNPCs().remove(brother);
									player.getBarrows().setSummoned(null);
									player.getBarrows().setTombState(fIndex, 3);
									player.removePrivateNPC(brother);
									player.getActionSender().sendNPCHints(-1);
									this.stop();

								}

							});
						}

					}

				});
				return true;
			}
		}
		return false;
	}

	/**
	 * Claims the reward, at chest in the middle.
	 * 
	 * @param player
	 *            The player claiming his reward.
	 */
	public void claim(final Player player) {
		if (!spawnMissingBrother(player) && !gameFinished) {
			gameFinished = true;
			rocks(player);
			Item[] reward = new Item[r.nextInt(3) + 2]; // You get between 2 and
														// 5 items.

			for (int index = 0; index < reward.length; index++) {
				if (r.nextInt(r.nextInt(10000)) <= r.nextInt((killCount))) {
					if (r.nextInt(10) == 0) { // D med helm xD
						reward[index] = new Item(1149);
					} else { // Barrows equipment.
						reward[index] = BARROWS_EQUIPMENT[r
								.nextInt(BARROWS_EQUIPMENT.length)];
					}
				} else if (r.nextInt(5) == 0) { // Bolt racks or a half key..
					if (r.nextInt(3) == 0) { // Half keys.
						reward[index] = new Item(r.nextBoolean() ? 985 : 987);
					} else { // Bolt racks.
						reward[index] = new Item(4740, r.nextInt(200) + 1);
					}
				} else { // Runes or coins.
					if (r.nextInt(5) == 0) { // Coins
						reward[index] = new Item(995, r.nextInt(5000) + 1);
					} else {
						int index1 = r.nextInt(RUNES.length);
						reward[index] = new Item(RUNES[index1][0],
								r.nextInt(RUNES[index1][1]));
					}

				}
			}
			Inventory.addInventoryItems(player, reward);
		}
	}

	/**
	 * Setup a new barrows game.
	 */
	public void setupGame() {
		tombInformation[r.nextInt(tombInformation.length)] = 2; // The tunnel
																// tomb.
		killCount = 0;
	}

	/**
	 * Used when we get info from the save game file.
	 */
	public void setTombState(int index, int info) {
		tombInformation[index] = info;
	}

	/**
	 * We set the summoned barrows NPC.
	 * 
	 * @param summoned
	 *            The new NPC.
	 */
	public void setSummoned(NPC summoned) {
		this.summoned = summoned;
	}

	/**
	 * Get the current NPC summoned.
	 * 
	 * @return The currently summoned NPC.
	 */
	public NPC getSummoned() {
		return summoned;
	}

	public void setGameFinished(boolean b) {
		this.gameFinished = b;
	}

	public boolean isGameFinished() {
		return gameFinished;
	}

	/**
	 * The tunnels corners.
	 */
	private static final Location[] TUNNEL_CORNERS = {
			Location.create(3535, 9711, 0), // North West corner.
			Location.create(3568, 9711, 0), // North East corner.

	};

	/**
	 * Teleports you to a random spot in the Barrows tunnels.
	 * 
	 * @param player
	 *            The teleporting player.
	 */
	public static void teleportToTunnels(Player player) {
		player.setTeleportTarget(TUNNEL_CORNERS[r
				.nextInt(TUNNEL_CORNERS.length)]);
	}

	/**
	 * Sets up the Barrows minigame when we load up the player.
	 */
	public void load(boolean finished, int kc, int[] tomb) {
		this.gameFinished = finished;
		this.killCount = kc;
		this.tombInformation = tomb;

	}

	/**
	 * Defines how many kills we've made in the crypts.
	 */
	private int killCount = 0;

	/**
	 * The currently summoned NPC.
	 */
	private NPC summoned = null;

	/**
	 * Holds information about the tomb states. 0 = Nothing happend. 1 =
	 * Defeated. 2 = Tunnel. 3 = Tunnel spirit defeated.
	 */
	private int[] tombInformation = { 0, // Ahrims tomb.
			0, // Dharocks tomb.
			0, // Guthans tomb.
			0, // Karils tomb.
			0, // Torags tomb.
			0, // Veracs tomb.
	};

	/**
	 * Defines if we're in the tunnels after claiming the reward, and we want
	 * rocks in the head + an earth quake.
	 */
	private boolean gameFinished = false;

}
