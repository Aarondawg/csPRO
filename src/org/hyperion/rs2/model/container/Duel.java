package org.hyperion.rs2.model.container;

import java.util.Random;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.DeathEvent;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Container.Type;
import org.hyperion.rs2.model.container.Equipment.EquipmentType;
import org.hyperion.rs2.model.container.impl.InterfaceContainerListener;
import org.hyperion.rs2.util.NameUtils;

public class Duel {

	private static final Random r = new Random();

	public enum State {
		FIRST_SCREEN, SECOND_SCREEN,
	}

	public static final int FIRST_DUELING_INTERFACE = 107;
	public static final int SECONDARY_DUELING_INTERFACE = 334;
	public static final int INVENTORY_INTERFACE = 109;

	// TODO: arrows shot in the duel player1Arena are given back?

	private State state = State.FIRST_SCREEN;
	private final Container stake1 = new Container(Type.STANDARD,
			Inventory.SIZE);
	private final Container stake2 = new Container(Type.STANDARD,
			Inventory.SIZE);
	private final Player player1, player2;
	private boolean accept1, accept2;

	private static final int[] DUELING_CONFIG_IDS = { 1, 2, 16, 32, 64, 128,
			256, 512, 1024, 4096, 8192, 16384, 32768, 65536, 131072, 262144,
			524288, 2097152, 8388608, 16777216, 67108864, 134217728, 268435456 };

	private static final int[] DUELING_BUTTON_IDS = { 124, 144, 132, 145, 126,
			148, 127, 149, 125, 150, 129, 151, 130, 152, 131, 153, 154, 155,
			156, 157, 159, 158, 200, 201, 202, 204, 205, 206, 207, 210, 209,
			208, 203 };

	private static final int[] RULE_IDS = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5,
			6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
			20, 21 };

	private static final int[] DUEL_SLOT_IDS = { Equipment.SLOT_HELM,
			Equipment.SLOT_CAPE, Equipment.SLOT_AMULET, Equipment.SLOT_WEAPON,
			Equipment.SLOT_CHEST, Equipment.SLOT_SHIELD,
			Equipment.SLOT_BOTTOMS, Equipment.SLOT_GLOVES,
			Equipment.SLOT_BOOTS, Equipment.SLOT_RING, Equipment.SLOT_ARROWS, };

	/**
	 * All those messages has been copied straight from videos around the 459
	 * era. (Except for the "Fun weapons" as I couldn't find a video with the
	 * message). I'm not sure if there should be more than three messages in the
	 * "Before the duel starts" array. :(
	 */
	private static final String[] RULES = { "You cannot forfeit the duel",
			"You cannot move", "You cannot use ranged attacks",
			"You cannot use melee attacks", "You cannot use magic attacks",
			"You cannot use drinks", "You cannot use food",
			"You cannot use prayer", "There will be obstacles in the arena.",
			"There will be fun weapons", "You cannot use special attacks." };
	private static final String[] BEFORE_THE_DUEL_STARTS = {
			"Some user items will be taken off",
			"Boosted stats will be restored",
			"Existing prayers will be stopped", "", "" };

	private static final int[] BEFORE_THE_DUEL_STARTS_CHILD_IDS = { 138, 118,
			119, 126, 127 };
	private static final int[] DURING_THE_DUEL_CHILD_IDS = { 129, 130, 131,
			132, 134, 135, 136, 137, 139, 140, 141 };

	/**
	 * The is where we keep all our rule constants.
	 */
	public static final int NO_FORFEIT = 0;
	public static final int NO_MOVEMENT = 1;
	public static final int NO_RANGE = 2;
	public static final int NO_MELEE = 3;
	public static final int NO_MAGIC = 4;
	public static final int NO_DRINKS = 5;
	public static final int NO_FOOD = 6;
	public static final int NO_PRAYER = 7;
	public static final int OBSTACLES = 8;
	public static final int FUN_WEAPONS = 9;
	public static final int NO_SPECIAL_ATTACKS = 10;
	public static final int NO_HATS = 11;
	public static final int NO_CAPES = 12;
	public static final int NO_AMULETS = 13;
	public static final int NO_SWORDS = 14;
	public static final int NO_BODIES = 15;
	public static final int NO_SHIELDS = 16;
	public static final int NO_LEGS = 17;
	public static final int NO_GLOVES = 18;
	public static final int NO_BOOTS = 19;
	public static final int NO_RINGS = 20;
	public static final int NO_ARROWS = 21;

	/**
	 * We aren't making those static, causing them to chance every time we
	 * create a new duel.
	 */
	private Location player1Arena;
	private Location player2Arena;

	private Location obstracleArena1;
	private Location obstracleArena2;

	/**
	 * Hint Hint: Switch the buttons or make an array and then + or - depending
	 * if they are on or off.
	 */
	public void handleFirstInterfaceButtons(Player player, int buttonId) {
		int rule = -1;
		int slot = -1;
		/*
		 * We check if we clicked the accept button.
		 */
		if (buttonId == 103) {
			accept(player);
			return;
		}
		for (int index = 0; index < DUELING_BUTTON_IDS.length; index++) {
			if (DUELING_BUTTON_IDS[index] == buttonId) {
				rule = RULE_IDS[index];
				if (rule >= 11) {
					slot = DUEL_SLOT_IDS[rule - 11];
				}
				break;
			}
		}
		/*
		 * Make sure we actually changed the rule, this could cause a crash if
		 * not.
		 */
		if (rule == -1) {
			return;
		}
		accept1 = false;
		accept2 = false;
		acceptUpdate();
		if (rule >= 11 && slot != -1) {
			if (player1.getEquipment().get(slot) != null
					|| player2.getEquipment().get(slot) != null) {
				if (!rules[rule]) {
					duelSpaceReq++;
				} else {
					duelSpaceReq--;
				}

			}
		}
		if (rule >= 11) {
			System.out.println("DuelSpaceReq: " + duelSpaceReq);
			if (player1.getInventory().freeSlots() < duelSpaceReq
					|| player2.getInventory().freeSlots() < duelSpaceReq) {
				player.getActionSender()
						.sendMessage(
								"You or your opponent don't have the required space to set this rule.");
				return;
			}
		}

		if (rules[rule]) {
			totalDuelConfigs -= DUELING_CONFIG_IDS[rule];
			rules[rule] = false;
		} else {
			totalDuelConfigs += DUELING_CONFIG_IDS[rule];
			rules[rule] = true;
		}
		player1.getActionSender().sendConfig(286, totalDuelConfigs);
		player2.getActionSender().sendConfig(286, totalDuelConfigs);
	}

	public Duel(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
		// this.player1.setStaking(true);
		// this.player2.setStaking(true);
		listener11 = new InterfaceContainerListener(player1,
				FIRST_DUELING_INTERFACE, 98, -1);
		listener12 = new InterfaceContainerListener(player1,
				FIRST_DUELING_INTERFACE, 99, -1);

		listener21 = new InterfaceContainerListener(player2,
				FIRST_DUELING_INTERFACE, 98, -1);
		listener22 = new InterfaceContainerListener(player2,
				FIRST_DUELING_INTERFACE, 99, -1);

		openFirstInterface(player1);
		openFirstInterface(player2);
	}

	private void openFirstInterface(Player player) {
		player.getActionSender().sendInventoryInterface(
				FIRST_DUELING_INTERFACE, INVENTORY_INTERFACE);
		if (player == player1) {
			player.getInterfaceState().addListener(stake1, listener11);
			player.getInterfaceState().addListener(stake2, listener12);
		} else {
			player.getInterfaceState().addListener(stake2, listener21);
			player.getInterfaceState().addListener(stake1, listener22);
		}
		player.getInterfaceState().addListener(
				player.getInventory(),
				new InterfaceContainerListener(player, INVENTORY_INTERFACE, 0,
						93));
		player.getActionSender().sendString(
				NameUtils.formatName(getOther(player).getName()), 107, 248);
		player.getActionSender().sendString("", 107, 107); // Replaces the
															// "waiting for other player".
		refreshDuelRules();
	}

	private void openSecondInterface(Player player) {
		player.getActionSender().sendInterface(106);
		player.getActionSender().sendString(
				buildStakedItemsString(player == player1 ? stake1 : stake2),
				106, 102);
		player.getActionSender().sendString(
				buildStakedItemsString(player == player1 ? stake2 : stake1),
				106, 103);
		buildRulesString(player1, player2);
		player.getActionSender().sendString("", 106, 133);
	}

	private void buildRulesString(Player player1, Player player2) {
		boolean[] writeStrings = { true, true, true, true, true };
		/*
		 * Check the rules, and tell the array if we aren't going to write our
		 * strings on the interface.
		 */
		if (!(duelSpaceReq > 0))
			writeStrings[0] = false;
		if (!rules[NO_FOOD])
			writeStrings[1] = false;
		if (!rules[NO_PRAYER])
			writeStrings[2] = false;
		/*
		 * Makes sure the interface is clean before we write stuff on it (even
		 * if we don't).
		 */
		for (int i : BEFORE_THE_DUEL_STARTS_CHILD_IDS) {
			player1.getActionSender().sendString("", 106, i);
			player2.getActionSender().sendString("", 106, i);
		}
		int nextString = 0;
		int nextChild = 0;
		/*
		 * Write all the needed strings.
		 */
		for (boolean write : writeStrings) {
			if (write) {
				player1.getActionSender().sendString(
						BEFORE_THE_DUEL_STARTS[nextString], 106,
						BEFORE_THE_DUEL_STARTS_CHILD_IDS[nextChild]);
				player2.getActionSender().sendString(
						BEFORE_THE_DUEL_STARTS[nextString], 106,
						BEFORE_THE_DUEL_STARTS_CHILD_IDS[nextChild]);
				nextChild++;
			}
			nextString++;
		}
		/*
		 * Makes sure the interface is clean before we write stuff on it (even
		 * if we don't).
		 */
		for (int i : DURING_THE_DUEL_CHILD_IDS) {
			player1.getActionSender().sendString("", 106, i);
			player2.getActionSender().sendString("", 106, i);
		}
		/*
		 * This makes the correct rules(according to the rules array, go in the
		 * highest child available.
		 */
		nextString = 0;
		nextChild = 0;
		for (boolean rule : rules) {
			if (nextString == 11) {
				break;
			}
			if (rule) {
				player1.getActionSender().sendString(RULES[nextString], 106,
						DURING_THE_DUEL_CHILD_IDS[nextChild]);
				player2.getActionSender().sendString(RULES[nextString], 106,
						DURING_THE_DUEL_CHILD_IDS[nextChild]);
				nextChild++;
			}
			nextString++;
		}
	}

	private String buildStakedItemsString(Container container) {
		if (container.freeSlots() == container.size()) {
			return "<col=FFFFFF>Absolutely nothing!";
		} else {
			StringBuilder bldr = new StringBuilder();
			for (int i = 0; i < container.size(); i++) {
				Item item = container.get(i);
				if (item != null) {
					bldr.append("<col=FF9040>" + item.getDefinition().getName());
					if (item.getCount() > 1) {
						bldr.append(" <col=FFFFFF> x <col=FFFFFF>"
								+ item.getCount());
					}
					bldr.append("<br>");
				}
			}
			return bldr.toString();
		}
	}

	public Player getOther(Player player) {
		return player == player1 ? player2 : player1;
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void close() {
		if (!isDueling && !finished) {
			for (Item i : stake1.toArray()) {
				if (i != null) {
					player1.getInventory().add(i);
				}
			}
			for (Item i : stake2.toArray()) {
				if (i != null) {
					player2.getInventory().add(i);
				}
			}
			reset();
			player1.getActionSender().sendCloseInterface();
			player2.getActionSender().sendCloseInterface();
		}
		stake1.removeListener(listener11);
		stake1.removeListener(listener21);

		stake2.removeListener(listener21);
		stake2.removeListener(listener22);
	}

	public void reset() {
		refreshDuelRules();
		player1.getRequestManager().setDuelReq(null);
		player1.getRequestManager().setDuel(null);
		player2.getRequestManager().setDuelReq(null);
		player2.getRequestManager().setDuel(null);
	}

	public void accept(Player which) {
		if (rules[NO_MELEE] && rules[NO_MAGIC] && rules[NO_RANGE]) {
			which.getActionSender().sendMessage(
					"You cannot not have no forms of combat!");
			return;
		}
		if (rules[NO_MELEE] && rules[NO_FORFEIT]) {
			which.getActionSender()
					.sendMessage(
							"Without melee, you could get stuck without being able to forfeit.");
			return;
		}
		if (which == player1) {
			accept1 = true;
		} else {
			accept2 = true;
		}
		acceptUpdate();
	}

	public void acceptUpdate() {
		switch (state) {
		case FIRST_SCREEN:
			if (accept1 && accept2) {
				state = State.SECOND_SCREEN;
				accept1 = false;
				accept2 = false;
				openSecondInterface(player1);
				openSecondInterface(player2);
			} else if (accept1 && !accept2) {
				player1.getActionSender().sendString(
						"Waiting for other player...", 107, 113);
				player2.getActionSender().sendString(
						"The other player has accepted.", 107, 113);
			} else if (!accept1 && accept2) {
				player2.getActionSender().sendString(
						"Waiting for other player...", 107, 113);
				player1.getActionSender().sendString(
						"The other player has accepted.", 107, 113);
			} else {
				player2.getActionSender().sendString("", 107, 113);
				player1.getActionSender().sendString("", 107, 113);
			}
			break;
		case SECOND_SCREEN:
			if (accept1 && accept2) {
				state = State.SECOND_SCREEN;
				accept1 = false;
				accept2 = false;
				startDuel();
				close();
			} else if (accept1 && !accept2) {
				player1.getActionSender().sendString(
						"Waiting for other player...", 106, 133);
				player2.getActionSender().sendString(
						"The other player has accepted.", 106, 133);
			} else if (!accept1 && accept2) {
				player2.getActionSender().sendString(
						"Waiting for other player...", 106, 133);
				player1.getActionSender().sendString(
						"The other player has accepted.", 106, 133);
			} else {
				player2.getActionSender().sendString("", 106, 133);
				player1.getActionSender().sendString("", 106, 133);
			}
			break;
		}
	}

	private void configureRules(Player player) {
		/*
		 * There is a few parts we only have to do once.
		 */
		if (player == player1) {
			/*
			 * No movement.
			 */
			if (rules[NO_MOVEMENT]) {
				player2Arena = Location.create(player1Arena.getX() - 1,
						player1Arena.getY(), 0);
				player1.setCanWalk(false);
				player2.setCanWalk(false);
			}
			/*
			 * Obstacles.
			 */
			if (rules[OBSTACLES]) {
				/*
				 * Checks if there's movement or not.
				 */
				/*
				 * No need to set temporary attributes for this no-movement,
				 * because we already did that in rule 1. :)
				 */
				if (rules[NO_MOVEMENT]) {
					System.out.println("No-movement obstracle area");
					player1Arena = obstracleArena1;
					player2Arena = Location.create(obstracleArena1.getX() - 1,
							obstracleArena1.getY(), 0);
				} else {
					System.out.println("Normal obstracle area");
					player1Arena = obstracleArena1;
					player2Arena = obstracleArena2;
				}
			}
		}
		/*
		 * Special attacks.
		 */
		if (rules[NO_SPECIAL_ATTACKS]) {
			player.getSpecials().setActive(false);
		}

		/*
		 * Equipment. We loop through all equipment rules.
		 */
		for (int rule = 11; rule < rules.length; rule++) {
			/*
			 * Make sure the rule applies.
			 */
			if (rules[rule]) {
				/*
				 * If that is so, we get the equipment slot.
				 */
				int slot = DUEL_SLOT_IDS[rule - 11];
				/*
				 * Check if we're actually wearing an item in this slot..
				 */
				if (player.getEquipment().get(slot) != null) {
					/*
					 * If so, we check if we can add it to the inventory.
					 */
					if (player.getInventory().add(
							player.getEquipment().get(slot))) {
						/*
						 * If we can, (which we REALLY should be able to, unless
						 * I failed) we reset the equipment slot.
						 */
						player.getEquipment().set(slot, null);
						/*
						 * And print a warning if we can't.
						 */
					} else {
						System.out.println("Duel wearing bug@!!!!");
					}
				}
			}
		}
	}

	/**
	 * Checks if we can wear a specific item during this duel.
	 * 
	 * @param player
	 *            The player attempting to wear an item.
	 * @param type
	 *            The equipment type.
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public boolean canUseItem(Player player, EquipmentType type) {
		/*
		 * We loop through all equipment rules.
		 */
		for (int rule = 11; rule < rules.length; rule++) {
			/*
			 * Make sure the rule applies.
			 */
			if (rules[rule]) {
				/*
				 * If that is so, we get the equipment slot.
				 */
				int slot = DUEL_SLOT_IDS[rule - 11];
				/*
				 * Check if the item we're about to wear is heading for this
				 * slot..
				 */
				if (slot == type.getSlot()) {
					/*
					 * If so, we get the EquipmentType description..
					 */
					String desc = type.getDescription().toLowerCase();
					/*
					 * Modify it slightly..
					 */
					if (!desc.endsWith("s")) {
						desc += "s";
					}
					/*
					 * Notify the player, and return false.
					 */
					player.getActionSender().sendMessage(
							"Wearing " + desc
									+ " has been disabled in this duel!");
					return false;
				}
			}
		}
		return true;
	}

	private void startDuel() {
		isDueling = true;
		configureRules(player1);
		configureRules(player2);
		player1.getActionSender().sendMessage(
				"Accepted stake and duel options.");
		player2.getActionSender().sendMessage(
				"Accepted stake and duel options.");
		player1.getActionSender().sendCloseInterface();
		player2.getActionSender().sendCloseInterface();
		player1.setTeleportTarget(player1Arena);
		player2.setTeleportTarget(player2Arena);
		player1.getActionSender().sendPlayerHints(player2.getIndex());
		player2.getActionSender().sendPlayerHints(player1.getIndex());
		player1.getSpecials().setAmount(1000);
		player2.getSpecials().setAmount(1000);
		player1.getMagic().setVengeanceCasted(false);
		player2.getMagic().setVengeanceCasted(false);
		player1.getMagic().resetAutoCasting();
		player1.getSpecials().setActive(false);
		player2.getSpecials().setActive(false);
		World.getWorld().submit(new Event(1000) {
			private int duelCount = 3;

			public void execute() {
				if (duelCount != 0) {
					player1.forceChat("" + duelCount);
					player2.forceChat("" + duelCount);
					duelCount--;
				} else {
					player1.forceChat("FIGHT!");
					player2.forceChat("FIGHT!");
					duelCount = 0;
					canFight = true;
					this.stop();
				}
			}
		});
	}

	public void forfeit(final Player player, final Location loc) {
		if (rules[NO_MOVEMENT]) {
			/*
			 * If we can't move, we do actions without distance checking.
			 */
			if (rules[NO_FORFEIT]) {
				player.getActionSender().sendMessage(
						"Forfeiting the duel has been disabled!");
			} else {
				finish(player, false, false);
			}
		} else {
			/*
			 * Make sure we're all close to the forfeiting object, before we
			 * execute the action.
			 */
			player.getActionQueue().cancelQueuedActions();
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
					if (player.getLocation().withinRange(loc, 1)) {
						if (rules[NO_FORFEIT]) {
							player.getActionSender().sendMessage(
									"Forfeiting the duel has been disabled!");
						} else {
							finish(player, false, false);
						}
						this.stop();
					}
				}

			});
		}
	}

	public void destroy(Player p) {
		finish(p, false, true);
		close();
	}

	public void handleDeath(Player player) {
		finish(player, true, false);
	}

	/**
	 * This method is called when a player is dying/is forfeiting in the duel
	 * arena.
	 * 
	 * @param player
	 *            The player who recently died.
	 */
	private void finish(final Player player, final boolean died,
			final boolean disconnect) {
		final Player other = getOther(player);
		/*
		 * If the other player already died, we won, and everything is already
		 * taken care of.
		 */
		if (other.isDead() || finished) {
			return; // Nothing left to do.
		}
		/*
		 * <code>Player</code> other is now the winner, as he didn't die first.
		 */
		/*
		 * We set our finished flag to true.
		 */
		finished = true;
		/*
		 * We reset following and interacting..
		 */
		player.getActionSender().sendStopFollowing(false);
		player.resetInteractingEntity();
		other.getActionSender().sendStopFollowing(false);
		other.resetInteractingEntity();
		/*
		 * We create the usual delay on 5 seconds before he handle the death
		 * stuff.
		 */
		World.getWorld().submit(new Event(died ? 5000 : 0) {

			@Override
			public void execute() {
				/*
				 * Send a message to the loser, or the winner + loser if there
				 * was a forfeit.
				 */
				if (died) {
					player.getActionSender().sendMessage(
							"Better luck next time!");
				} else if (!disconnect) {
					player.getActionSender().sendMessage(
							"You forfeit the duel!");
					other.getActionSender().sendMessage(
							"The other player has forfeited the duel!");
				} else {
					other.getActionSender().sendMessage(
							"The other player has disconnected.");
				}

				/*
				 * This is not how its done, as the players is teleported to a
				 * random side of the arena, and you can end up next to each
				 * other in RS2. Its good for now though.
				 */
				Location duelArena1 = Location.create(3356 + r.nextInt(20),
						3268 + r.nextInt(9), 0);
				Location duelArena2 = Location.create(3379 - r.nextInt(20),
						3279 - r.nextInt(9), 0);
				if (r.nextBoolean()) {
					player.setTeleportTarget(duelArena1);
					other.setTeleportTarget(duelArena2);
				} else {
					player.setTeleportTarget(duelArena2);
					other.setTeleportTarget(duelArena1);
				}
				/*
				 * We remove the hint icons.
				 */
				player.getActionSender().sendPlayerHints(-1);
				other.getActionSender().sendPlayerHints(-1);
				/*
				 * We add all the items the winner staked, in the winners
				 * inventory.
				 */
				Container staked = getStake(other);
				Inventory.addInventoryItems(other, staked.toArray());
				/*
				 * We get the items won, and display them on the winning
				 * interface (interface 110). The player will receive those once
				 * he clicks the claim button.
				 */
				Container won = getStake(player);
				other.getActionSender().sendUpdateItems(110, 88, 93,
						won.shift().toArray());
				/*
				 * We write the losers name on the screen, as well as his combat
				 * level.
				 */
				other.getActionSender().sendString(
						"" + player.getSkills().getCombatLevel(), 110, 104);
				other.getActionSender().sendString(
						"" + NameUtils.formatName(player.getName()), 110, 105);
				/*
				 * We delay the interface displaying slightly, as well as the
				 * char resetting.
				 */
				World.getWorld().submit(new Event(600) {

					@Override
					public void execute() {
						if (player == null || other == null) {
							this.stop();
							return;
						}
						if (!player.getSession().isConnected()
								&& !other.getSession().isConnected()) {
							this.stop();
						}
						boolean bool = disconnect ? true : !player
								.getLocation().isDueling();
						if (!other.getLocation().isDueling() && bool) {
							/*
							 * We reset both players.
							 */
							DeathEvent.resetPlayer(player);
							DeathEvent.resetPlayer(other);
							refreshDuelRules();
							isDueling = false;
							other.getActionSender().sendInterface(110);
							this.stop();
						}
					}

				});
				this.stop();
			}

		});

	}

	public void removeItem(Player player, int slot, int amount) {
		if (state == State.FIRST_SCREEN) {
			if (player == player2) {
				Item item = stake2.get(slot);
				if (item != null) {
					int id = item.getId();
					int got = stake1.getCount(id);
					int trueAmount = amount > got ? got : amount;
					Item trueItem = new Item(id, trueAmount);

					if (!player.getInventory().hasRoomFor(trueItem,
							duelSpaceReq)) {
						player.getActionSender()
								.sendMessage(
										"You do not have enough space to withdraw that item!");
						return;
					}
					accept1 = false;
					accept2 = false;
					acceptUpdate();
					stake2.remove(slot, trueItem);
					player2.getInventory().add(trueItem);
				}
			}
			if (player == player1) {
				Item item = stake1.get(slot);
				if (item != null) {
					int id = item.getId();
					int got = stake1.getCount(id);
					int trueAmount = amount > got ? got : amount;
					Item trueItem = new Item(id, trueAmount);

					if (!player.getInventory().hasRoomFor(trueItem,
							duelSpaceReq)) {
						player.getActionSender()
								.sendMessage(
										"You do not have enough space to withdraw that item!");
						return;
					}

					accept1 = false;
					accept2 = false;
					acceptUpdate();
					stake1.remove(slot, trueItem);
					player1.getInventory().add(trueItem);
				}
			}
		}
	}

	public void stakeItem(Player player, int slot, int amount) {
		if (state == State.FIRST_SCREEN) {
			if (player == player1) {
				accept1 = false;
				accept2 = false;
				acceptUpdate();
				Item item = player1.getInventory().get(slot);
				if (item != null) {
					int id = item.getId();
					if (Constants.playerBoundItem(id)) {
						player.getActionSender().sendMessage(
								"You can't stake this item.");
					}
					int got = player1.getInventory().getCount(id);
					int trueAmount = amount > got ? got : amount;
					Item trueItem = new Item(id, trueAmount);
					player1.getInventory().remove(slot, trueItem);
					stake1.add(trueItem);
				}
			}
			if (player == player2) {
				accept1 = false;
				accept2 = false;
				acceptUpdate();
				Item item = player2.getInventory().get(slot);
				if (item != null) {
					int id = item.getId();
					if (Constants.playerBoundItem(id)) {
						player.getActionSender().sendMessage(
								"You can't stake this item.");
					}
					int got = player2.getInventory().getCount(id);
					int trueAmount = amount > got ? got : amount;
					Item trueItem = new Item(id, trueAmount);
					player2.getInventory().remove(slot, trueItem);
					stake2.add(trueItem);
				}
			}
		}
	}

	public boolean canFight() {
		return canFight;
	}

	/**
	 * Defines if a specific rule is toggled.
	 * 
	 * @param rule
	 *            The rule toggled.
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public boolean isRuleToggled(int rule) {
		return rules[rule];
	}

	public void refreshDuelRules() {
		/*
		 * Resets the actual buttons first.
		 */
		for (int i = 0; i < rules.length; i++) {
			if (rules[i]) {
				totalDuelConfigs -= DUELING_CONFIG_IDS[i];
				rules[i] = false;
			}
		}
		player1.getActionSender().sendConfig(286, totalDuelConfigs);
		player2.getActionSender().sendConfig(286, totalDuelConfigs);
		/*
		 * Then we reset the serversided configs for the buttons.
		 */
		for (int i = 0; i < rules.length; i++) {
			rules[i] = false;
		}
	}

	/**
	 * Gets the item container containing the items a specific player staked.
	 * 
	 * @param player
	 *            The player who's container we're getting.
	 * @return An item <code>Container</code>, with the stake that belongs to
	 *         the player.
	 */
	public Container getStake(Player player) {
		return player == player1 ? stake1 : stake2;
	}

	/**
	 * Defines if a rule is set to on or off.
	 */
	private boolean[] rules = new boolean[22];

	/**
	 * Defines if we've found a winner.
	 */
	private boolean finished = false;

	/**
	 * Defines if we're currently dueling (In the figthing arena)
	 */
	private boolean isDueling = false;

	private int totalDuelConfigs = 0;
	private boolean canFight = false;
	private int duelSpaceReq;

	/**
	 * Listeners for player1.
	 */
	private final InterfaceContainerListener listener11;
	private final InterfaceContainerListener listener12;

	/**
	 * Listeners for player2.
	 */
	private final InterfaceContainerListener listener21;
	private final InterfaceContainerListener listener22;

	{
		for (int index = 0; index < rules.length; index++) {
			rules[index] = false;
		}

		/*
		 * We pick the arenas randomly. There's 3 obstacle arenas, as well as 3
		 * normal arenas.
		 */
		switch (r.nextInt(3)) {
		case 0:
		case 1:
		case 2:
			Location nMin = Location.create(3366, 3227, 0);
			Location nMax = Location.create(3386, 3237, 0);

			player1Arena = Location.create(
					nMin.getX() + r.nextInt(nMax.getX() - nMin.getX()),
					nMin.getY() + r.nextInt(nMax.getY() - nMin.getY()), 0);
			player2Arena = Location.create(
					nMin.getX() + r.nextInt(nMax.getX() - nMin.getX()),
					nMin.getY() + r.nextInt(nMax.getY() - nMin.getY()), 0);

			Location oMin = Location.create(3335, 3227, 0);
			Location oMax = Location.create(3355, 3237, 0);

			obstracleArena1 = Location.create(
					oMin.getX() + r.nextInt(oMax.getX() - oMin.getX()),
					oMin.getY() + r.nextInt(oMax.getY() - oMin.getY()), 0);
			obstracleArena2 = Location.create(
					oMin.getX() + r.nextInt(oMax.getX() - oMin.getX()),
					oMin.getY() + r.nextInt(oMax.getY() - oMin.getY()), 0);
			break;
		}
		// TODO: Fill this out, as well as the "isInDuelArena" boolean in
		// Location.java.

	}

}
