package org.hyperion.rs2.model.container;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container.Type;
import org.hyperion.rs2.model.container.impl.InterfaceContainerListener;
import org.hyperion.rs2.util.NameUtils;

/**
 * Represents a trade between two players.
 * 
 * @author Graham
 * 
 */
public class Trade {

	public enum State {
		FIRST_SCREEN, SECOND_SCREEN;
	}

	public static final int FIRST_TRADING_INTERFACE = 335;
	public static final int SECONDARY_TRADING_INTERFACE = 334;
	public static final int INVENTORY_INTERFACE = 336;

	private boolean exchanged = false;
	private State state = State.FIRST_SCREEN;
	private Container offer1 = new Container(Type.STANDARD, Inventory.SIZE);
	private Container offer2 = new Container(Type.STANDARD, Inventory.SIZE);
	private Player player1, player2;
	private boolean accept1, accept2;

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

	public Trade(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;

		listener11 = new InterfaceContainerListener(player1,
				FIRST_TRADING_INTERFACE, 91, -1);
		listener12 = new InterfaceContainerListener(player1,
				FIRST_TRADING_INTERFACE, 92, -1);

		listener21 = new InterfaceContainerListener(player2,
				FIRST_TRADING_INTERFACE, 91, -1);
		listener22 = new InterfaceContainerListener(player2,
				FIRST_TRADING_INTERFACE, 92, -1);

		openFirstInterface(player1);
		openFirstInterface(player2);
	}

	private void openFirstInterface(Player player) {
		player.getActionSender().sendInventoryInterface(
				FIRST_TRADING_INTERFACE, INVENTORY_INTERFACE);

		if (player == player1) {
			player.getInterfaceState().addListener(offer1, listener11);
			player.getInterfaceState().addListener(offer2, listener12);
		} else {
			player.getInterfaceState().addListener(offer2, listener21);
			player.getInterfaceState().addListener(offer1, listener22);
		}

		player.getInterfaceState().addListener(
				player.getInventory(),
				new InterfaceContainerListener(player, INVENTORY_INTERFACE, 0,
						93));

		player.getActionSender().sendString(
				"Trading with: "
						+ NameUtils.formatName(getOther(player).getName()),
				335, 93);
		player.getActionSender().sendString("", 335, 107); // Replaces the
															// "waiting for other player".

	}

	private void openSecondInterface(Player player) {
		player.getActionSender().sendInterface(SECONDARY_TRADING_INTERFACE);
		player.getActionSender().sendString(
				buildString(player == player1 ? offer1 : offer2), 334, 112);
		player.getActionSender().sendString(
				buildString(player == player1 ? offer2 : offer1), 334, 113);
		player.getActionSender().sendString(
				"<col=00FFFF>Trading with:<br><col=00FFFF>"
						+ NameUtils.formatName(getOther(player).getName()),
				334, 93);
		player.getActionSender().sendInterfaceConfig(
				SECONDARY_TRADING_INTERFACE, 37, false);
		player.getActionSender().sendInterfaceConfig(
				SECONDARY_TRADING_INTERFACE, 41, false);
		player.getActionSender().sendInterfaceConfig(
				SECONDARY_TRADING_INTERFACE, 45, true);
		player.getActionSender().sendInterfaceConfig(
				SECONDARY_TRADING_INTERFACE, 46, false);
	}

	private String buildString(Container container) {
		if (container.freeSlots() == container.capacity()) {
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

	private Player getOther(Player player) {
		return player == player1 ? player2 : player1;
	}

	public Player getPlayer1() {
		return player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void exchange() {
		for (Item i : offer2.toArray()) {
			if (i != null) {
				player1.getInventory().add(i);
			}
		}
		for (Item i : offer1.toArray()) {
			if (i != null) {
				player2.getInventory().add(i);
			}

		}
		exchanged = true;
	}

	public void close() {
		if (!exchanged) {
			for (Item i : offer1.toArray()) {
				if (i != null) {
					player1.getInventory().add(i);
				}
			}
			for (Item i : offer2.toArray()) {
				if (i != null) {
					player2.getInventory().add(i);
				}
			}
		}
		offer1.removeListener(listener11);
		offer1.removeListener(listener21);

		offer2.removeListener(listener21);
		offer2.removeListener(listener22);

		player1.getRequestManager().setTradingWith(null);
		player1.getRequestManager().setTrade(null);
		player2.getRequestManager().setTradingWith(null);
		player2.getRequestManager().setTrade(null);

		player1.getActionSender().sendCloseInterface();
		player2.getActionSender().sendCloseInterface();

		player1.getActionSender().sendStopFollowing(false);
		player2.getActionSender().sendStopFollowing(false);
	}

	public void accept(Player which) {
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
				if (!player1.getInventory().hasRoomFor(offer2, 0)) { // This
																		// doesn't
																		// work
																		// as
																		// the
																		// hasSpaceFor
																		// method
																		// only
																		// checks
																		// for
																		// one
																		// item
																		// at
																		// the
																		// time
																		// lel.
					player2.getActionSender()
							.sendMessage(
									"Other player does not have enough space in their inventory.");
					player1.getActionSender().sendMessage(
							"You do not have enough space in your inventory.");
					accept1 = false;
					accept2 = false;
					player2.getActionSender().sendString("", 335, 107);
					player1.getActionSender().sendString("", 335, 107);
					return;
				}
				if (!player2.getInventory().hasRoomFor(offer1, 0)) {
					player1.getActionSender()
							.sendMessage(
									"Other player does not have enough space in their inventory.");
					player2.getActionSender().sendMessage(
							"You do not have enough space in your inventory.");
					accept1 = false;
					accept2 = false;
					player2.getActionSender().sendString("", 335, 107);
					player1.getActionSender().sendString("", 335, 107);
					return;
				}
				state = State.SECOND_SCREEN;
				accept1 = false;
				accept2 = false;
				openSecondInterface(player1);
				openSecondInterface(player2);
			} else if (accept1 && !accept2) {
				player1.getActionSender().sendString(
						"Waiting for other player...", 335, 107);
				player2.getActionSender().sendString(
						"The other player has accepted.", 335, 107);
			} else if (!accept1 && accept2) {
				player2.getActionSender().sendString(
						"Waiting for other player...", 335, 107);
				player1.getActionSender().sendString(
						"The other player has accepted.", 335, 107);
			} else {
				player2.getActionSender().sendString("", 335, 107);
				player1.getActionSender().sendString("", 335, 107);
			}
			break;
		case SECOND_SCREEN:
			if (accept1 && accept2) {
				state = State.SECOND_SCREEN;
				accept1 = false;
				accept2 = false;
				exchange();
				close();
			} else if (accept1 && !accept2) {
				player1.getActionSender().sendString(
						"Waiting for other player...", 334, 91);
				player2.getActionSender().sendString(
						"The other player has accepted.", 334, 91);
			} else if (!accept1 && accept2) {
				player2.getActionSender().sendString(
						"Waiting for other player...", 334, 91);
				player1.getActionSender().sendString(
						"The other player has accepted.", 334, 91);
			} else {
				player2.getActionSender().sendString("", 335, 107);
				player1.getActionSender().sendString("", 335, 107);
			}
			break;
		}
	}

	public void removeItem(Player player, int slot, int amount) {
		if (state == State.FIRST_SCREEN) {
			if (player == player2) {
				accept1 = false;
				accept2 = false;
				acceptUpdate();
				Item item = offer2.get(slot);
				if (item != null) {
					int id = item.getId();
					int got = offer2.getCount(id);
					int trueAmount = amount > got ? got : amount;
					Item realItem = new Item(id, trueAmount);
					offer2.remove(slot, realItem);
					player2.getInventory().add(realItem);
				}
			}
			if (player == player1) {
				accept1 = false;
				accept2 = false;
				acceptUpdate();
				Item item = offer1.get(slot);
				if (item != null) {
					int id = item.getId();
					int got = offer1.getCount(id);
					int trueAmount = amount > got ? got : amount;
					Item realItem = new Item(id, trueAmount);
					offer1.remove(slot, realItem);
					player1.getInventory().add(realItem);
				}
			}

		}
	}

	public void offerItem(Player player, int slot, int amount) {
		if (state == State.FIRST_SCREEN) {
			if (player == player1) {
				accept1 = false;
				accept2 = false;
				acceptUpdate();
				Item item = player1.getInventory().get(slot);
				if (item != null) {
					int id = item.getId();
					int got = player1.getInventory().getCount(id);
					int trueAmount = amount > got ? got : amount;
					if (Constants.playerBoundItem(id)) {
						player.getActionSender().sendMessage(
								"You cannot trade this item!");
						return;
					}
					Item realItem = new Item(id, trueAmount);
					player1.getInventory().remove(slot, realItem);
					offer1.add(realItem);
				}
			}
			if (player == player2) {
				accept1 = false;
				accept2 = false;
				acceptUpdate();
				Item item = player2.getInventory().get(slot);
				if (item != null) {
					int id = item.getId();
					int got = player2.getInventory().getCount(id);
					int trueAmount = amount > got ? got : amount;
					if (Constants.playerBoundItem(id)) {
						player.getActionSender().sendMessage(
								"You cannot trade this item!");
						return;
					}
					Item realItem = new Item(id, trueAmount);
					player2.getInventory().remove(slot, realItem);
					offer2.add(realItem);
				}
			}
		}
	}

	public Container getOffer(Player player) {
		return player == player1 ? offer1 : offer2;
	}

}
