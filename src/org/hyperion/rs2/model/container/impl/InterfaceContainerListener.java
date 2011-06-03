package org.hyperion.rs2.model.container.impl;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.ContainerListener;

/**
 * A ContainerListener which updates a client-side interface to match the
 * server-side copy of the container.
 * 
 * @author Graham Edgecombe
 * 
 */
public class InterfaceContainerListener implements ContainerListener {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * The interface id.
	 */
	private int interfaceId;

	/**
	 * The child id.
	 */
	private int child;

	/**
	 * The type id.
	 */
	private int type;

	/**
	 * Creates the container listener.
	 * 
	 * @param player
	 *            The player.
	 * @param interfaceId
	 *            The interface id.
	 * @param child
	 *            The child id of where the items should be placed.
	 * @param type
	 *            The item type.
	 */
	public InterfaceContainerListener(Player player, int interfaceId,
			int child, int type) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.child = child;
		this.type = type;
	}

	@Override
	public void itemChanged(Container container, int slot) {
		Item item = container.get(slot);
		player.getActionSender().sendUpdateItem(interfaceId, child, type, slot,
				item);
	}

	@Override
	public void itemsChanged(Container container) {
		player.getActionSender().sendUpdateItems(interfaceId, child, type,
				container.toArray());
	}

	@Override
	public void itemsChanged(Container container, int[] slots) {
		player.getActionSender().sendUpdateItems(interfaceId, child, type,
				slots, container.toArray());
	}

}
