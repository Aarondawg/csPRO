package org.hyperion.rs2.model;

import java.util.ArrayList;
import java.util.List;

import org.hyperion.rs2.util.NameUtils;

/**
 * Manages friends and ignores.
 * 
 * @author Graham
 * 
 */
public class Friends {

	private final Player player;

	private final List<Long> friends;
	private final List<Long> ignores;

	private int messageCounter;

	public Friends(Player player) {
		this.messageCounter = 1;
		this.player = player;
		this.friends = new ArrayList<Long>(200);
		this.ignores = new ArrayList<Long>(200);
	}

	public int getNextUniqueId() {
		if (messageCounter >= 16000000) {
			messageCounter = 0;
		}
		return messageCounter++;
	}

	public void refresh() {
		player.getActionSender().sendFriendsStatus(2);
		for (Long friend : friends) {
			player.getActionSender().sendFriend(friend, getWorld(friend));
		}
		long[] array = new long[ignores.size()];
		int i = 0;
		for (Long ignore : ignores) {
			if (ignore != null) {
				array[i++] = ignore;
			}
		}
		player.getActionSender().sendIgnores(array);
	}

	private int getWorld(Long friend) {
		for (Player p : World.getWorld().getPlayers()) {
			if (p != null) {
				if (p.getNameAsLong() == friend) {
					return p.getWorld();
				}
			}
		}
		return 0;
	}

	public void addFriend(long name) {
		if (friends.size() >= 200) {
			player.getActionSender().sendMessage("Your friends list is full.");
			return;
		}
		if (friends.contains(name)) {
			player.getActionSender().sendMessage(
					NameUtils.formatName(player.getName())
							+ " is already on your friends list.");
			return;
		}
		friends.add(name);
		player.getActionSender().sendFriend(name, getWorld(name));

	}

	public void addIgnore(long name) {
		if (ignores.size() >= 200) {
			player.getActionSender().sendMessage("Your ignore list is full.");
			return;
		}
		if (ignores.contains((Long) name)) {
			player.getActionSender().sendMessage(
					NameUtils.formatName(player.getName())
							+ " is already on your ignore list.");
			return;
		}
		ignores.add((Long) name);

	}

	public void removeFriend(long name) {
		friends.remove((Long) name);
	}

	public void removeIgnore(long name) {
		ignores.remove((Long) name);
	}

	public void registered() {
		for (Player p : World.getWorld().getPlayers()) {
			if (p != null) {
				p.getFriends().registered(player);
			}
		}
	}

	private void registered(Player p) {
		long n = p.getNameAsLong();
		if (friends.contains(n)) {
			player.getActionSender().sendFriend(n, getWorld(n));
		}
	}

	public void unregistered() {
		for (Player p : World.getWorld().getPlayers()) {
			if (p != null) {
				p.getFriends().unregistered(player);
			}
		}
	}

	private void unregistered(Player p) {
		long n = p.getNameAsLong();
		if (friends.contains(n)) {
			player.getActionSender().sendFriend(n, 0);
		}
	}

	public void sendMessage(long name, String text) {
		for (Player p : World.getWorld().getPlayers()) {
			if (p != null) {
				// System.out.println("Long names: " + p.getNameAsLong() + " " +
				// name);
				if (p.getNameAsLong() == name) {
					p.getActionSender().sendReceivedPrivateMessage(
							player.getNameAsLong(),
							player.getRights().toInteger(), text);
					player.getActionSender().sendSentPrivateMessage(name, text);
					return;
				}
			}
		}
		player.getActionSender().sendMessage(
				NameUtils.formatName(player.getName())
						+ " is currently unavailable.");
	}

	public List<Long> getFriends() {
		return friends;
	}

	public List<Long> getIgnores() {
		return ignores;
	}

}
