package org.hyperion.rs2.model;

import org.hyperion.rs2.model.container.Duel;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.util.NameUtils;

/**
 * The request manager manages
 * 
 * @author Graham Edgecombe
 * 
 */
public class RequestManager {

	public void requestTrade(Player other) {
		/*
		 * if(World.getWorld().isSystemUpdating()) {
		 * player.getActionSender().sendMessage(
		 * "<col=ff0000>Server is currently updating! Make sure to complete your trade"
		 * );
		 * player.getActionSender().sendMessage("<col=ff0000>before time runs out!"
		 * ); }
		 */
		if (other.getRequestManager().tradeReq == player) {
			answerTrade(other);
		} else {
			player.getActionSender().sendMessage("Sending trade request...");
			other.getActionSender().sendMessage(
					NameUtils.formatName(player.getName()) + ":tradereq:");
			tradeReq = other;
		}
	}

	public void requestDuel(Player other) {
		/*
		 * if(World.getWorld().isSystemUpdating()) {
		 * player.getActionSender().sendMessage
		 * ("Access to minigames have been blocked while server is being updated."
		 * ); return; }
		 */
		if (other.getRequestManager().getDuelReq() == player) {
			answerDuel(other);
		} else {
			player.getActionSender().sendMessage("Sending duel request...");
			other.getActionSender().sendMessage(
					NameUtils.formatName(player.getName()) + ":duelreq:");
			setDuelReq(other);
		}
	}

	public void answerTrade(Player other) {
		if (trade != null) {
			if (trade.getPlayer1() == other && trade.getPlayer2() == player) {
				return;
			}
			if (trade.getPlayer2() == other && trade.getPlayer1() == player) {
				return;
			}
			trade.close();
		}
		if (other.getRequestManager().tradeReq == player) {
			// both players are trading each other so establish the trade
			establishTrade(player, other);
		} else {
			requestTrade(other);
		}
	}

	public void answerDuel(Player other) {
		if (duel != null) {
			if (duel.getPlayer1() == other && duel.getPlayer2() == player) {
				return;
			}
			if (duel.getPlayer2() == other && duel.getPlayer1() == player) {
				return;
			}
			duel.close();
			duel = null;
		}
		if (other.getRequestManager().duelReq == player) {
			// both players are trading each other so establish the trade
			establishDuel(player, other);
		} else {
			requestDuel(other);
		}
	}

	private static void establishTrade(Player player, Player other) {
		Trade trade = new Trade(player, other);
		player.getRequestManager().trade = trade;
		other.getRequestManager().trade = trade;
	}

	private static void establishDuel(Player player, Player other) {
		Duel duel = new Duel(player, other);
		player.getRequestManager().duel = duel;
		other.getRequestManager().duel = duel;
	}

	public boolean isDueling() {
		return getDuel() != null;
	}

	public boolean isTrading() {
		return trade != null;
	}

	public Trade getTrade() {
		return trade;
	}

	public Duel getDuel() {
		return duel;
	}

	/**
	 * Creates the request manager.
	 * 
	 * @param player
	 *            The player whose requests the manager is managing.
	 */
	public RequestManager(Player player) {
		this.player = player;
	}

	private Player player;

	private Trade trade = null;

	private Duel duel = null;

	private Player duelReq = null;

	private Player tradeReq = null;

	public void setTrade(Trade trade) {
		this.trade = trade;

	}

	public void setTradingWith(Player tradingWith) {
		this.tradeReq = tradingWith;
	}

	public void setDuelReq(Player duelReq) {
		this.duelReq = duelReq;
	}

	public Player getDuelReq() {
		return duelReq;
	}

	public void setDuel(Duel duel) {
		this.duel = duel;
	}

}