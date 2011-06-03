package org.hyperion.rs2.content;

import java.io.FileInputStream;
import java.util.List;
import java.util.logging.Logger;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Container.Type;
import org.hyperion.rs2.model.container.impl.InterfaceContainerListener;
import org.hyperion.util.XStreamUtil;

/**
 * A simple shopping class.
 * 
 * @author Brown.
 */
public class Shop {

	/**
	 * The shop container size.
	 */
	public static final int SIZE = 40;

	/**
	 * The shop interface.
	 */
	public static final int SHOP_INTERFACE = 300;

	/**
	 * The inventory interface.
	 */
	public static final int SHOP_INVENTORY_INTERFACE = 301;

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Logger.getLogger(Shop.class.getName());

	/**
	 * The shopping list.
	 */
	private static List<Shop> shops;

	/**
	 * Array holding all the general stores, so we don't have to load them in
	 * the xml file.
	 */
	private static final Shop[] GENERAL_STORES;

	/**
	 * Array holding all the general store locations. {minLocation, maxLocation}
	 */
	private static final Location[][] GENERAL_STORE_LOCATIONS = {
			{ Location.create(3208, 3242, 0), Location.create(3214, 3251, 0) }, // Lumbridge
																				// General
																				// Store.
			{ Location.create(3214, 3410, 0), Location.create(3220, 3420, 0) }, // Varrock
																				// General
																				// Store.

	};

	/**
	 * Array holding all the general store names.
	 */
	private static final String[] GENERAL_STORE_NAMES = {
			"Lumbridge General Store", "Varrock General Store" };

	/**
	 * The name of this shop.
	 */
	private final String name;

	/**
	 * The currency id.
	 */
	private final int currency;

	/**
	 * The NPC id's of this shop.
	 */
	private final int[] npcIds;

	/**
	 * The item array, when we get stuff from XML. We'll also use this to see if
	 * the container contains the correct items, When we update it once every
	 * minute or something like that.
	 */
	private final Item[] defaultItems;

	/**
	 * The items of this shop.
	 */
	private Container items;

	/**
	 * The min location of this shop.
	 */
	private final Location minLocation;

	/**
	 * The max location of this shop.
	 */
	private final Location maxLocation;

	/**
	 * Sets up a new shop.
	 * 
	 * @param name
	 *            The name of the shop.
	 * @param items
	 *            The item container.
	 * @param minLocation
	 *            The minimum location of the shop.
	 * @param maxLocation
	 *            The maximum location of the shop.
	 * @param currency
	 *            The item id of which we pay with.
	 */
	public Shop(String name, Container items, Location minLocation,
			Location maxLocation, int currency, int[] npcIds) {
		this.name = name;
		this.items = items;
		this.minLocation = minLocation;
		this.maxLocation = maxLocation;
		this.currency = currency;

		/*
		 * We have to do this, do make sure the items aren't changing by them
		 * selfs.
		 */
		Item[] arrayItems = items.toArray();
		Item[] copy = new Item[items.size()]; // Copy without the nulls in.
		for (int i = 0; i < copy.length; i++) {
			copy[i] = arrayItems[i];
		}

		this.defaultItems = copy;
		this.npcIds = npcIds;
	}

	@SuppressWarnings("unchecked")
	public static void init() {
		try {
			/*
			 * We load the shops from xml.
			 */
			shops = (List<Shop>) XStreamUtil.getXStream().fromXML(
					new FileInputStream("data/shops.xml"));
			for (Shop s : shops) {
				s.setContainer(new Container(Type.ALWAYS_STACK, SIZE));
				for (final Item item : s.getDefaultItems()) { // TODO: Check if
																// we need to
																// create an
																// array copy
																// here.
					s.addContainerItem(item);
				}
			}
			/*
			 * We add all the general stores to the list.
			 */
			for (Shop s : GENERAL_STORES) {
				shops.add(s);
			}

			logger.info("Loaded " + shops.size() + " shops.");

			/*
			 * We start an event, which will run in the back ground forever. ;)
			 */
			World.getWorld().submit(new Event(60000) { // Once every minute,
														// every single shop
														// will update its
														// items. ;)

						@Override
						public void execute() {
							for (Shop s : shops) {
								boolean fireEvents = false;
								for (Item containerItem : s.getItems()
										.toArray()) {
									for (Item defaultItem : s.getDefaultItems()) {
										if (containerItem != null) {
											if (defaultItem.getId() == containerItem
													.getId()) {
												if (defaultItem.getCount() > containerItem
														.getCount()) {
													containerItem
															.increaseCount();
													fireEvents = true;
												}
											}
										}
									}
								}
								if (fireEvents) {
									s.getItems().fireItemsChanged();
								}
							}

						}

					});
			/*
			 * PrintWriter p = new PrintWriter(new FileWriter("shops.xml",
			 * true)); p.println("<list>"); for (Shop shop : shops) {
			 * p.println(" <shop>");
			 * p.println("   <name>"+(shop.getName().endsWith(" ") ?
			 * shop.getName().substring(0, shop.getName().length() - 1) :
			 * shop.getName())+"</name>");
			 * p.println("   <currency>"+shop.getCurrency()+"</currency>");
			 * p.println("   <npcIds>"); for(int id : shop.getNPCIds()) {
			 * p.println("	  <int>"+id+"</int>"); } p.println("   </npcIds>");
			 * p.println("   <defaultItems>"); for(Item item :
			 * shop.getDefaultItems()) { p.println("	  <item>");
			 * p.println("	    <id>"+(item.getId() - 1)+"</id>");
			 * p.println("	    <count>"+item.getCount()+"</count>");
			 * p.println("	  </item>"); } p.println("   </defaultItems>");
			 * p.println(" </shop>"); p.flush(); } p.println("</list>");
			 * p.close();
			 */

		} catch (Exception e) {
			logger.warning("Failed to load the shops for some reason, check the data dir for shops.xml"
					+ e);
		}
	}

	/**
	 * Sets the item container of this shop.
	 */
	private void setContainer(Container container) {
		this.items = container;
	}

	/**
	 * Adds a single item to the container.
	 * 
	 * @param item
	 *            The item we want to add.
	 */
	private void addContainerItem(Item item) {
		items.add(item);
	}

	/**
	 * Gets the name of this shop.
	 * 
	 * @return The name of this shop.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the items for this shop.
	 * 
	 * @return The item container.
	 */
	public Container getItems() {
		return items;
	}

	/**
	 * Gets the array of items to put in the container.
	 * 
	 * @return The item array.
	 */
	public Item[] getDefaultItems() {
		return defaultItems;
	}

	/**
	 * Gets the maximum location of this shop.
	 * 
	 * @return The maximum location for this shop.
	 */
	public Location getMaximumLocation() {
		return maxLocation;
	}

	/**
	 * Gets the minimum location of this shop.
	 * 
	 * @return The minimum location for this shop.
	 */
	public Location getMinimumLocation() {
		return minLocation;
	}

	/**
	 * Gets the currency used to pay with in this shop
	 */
	public int getCurrency() {
		return currency;
	}

	/**
	 * Gets a shop based on the location the npc is in.
	 * 
	 * @param from
	 *            The location of the npc we want to shop at.
	 * @param npcId
	 *            The id of the NPC we want to shop at.
	 * @return A shop, based on the location of the "shop owner".
	 */
	private static Shop getShop(Location from, int npcId) {
		for (Shop s : shops) {
			if (s.getMinimumLocation() != null
					&& s.getMaximumLocation() != null) {
				if (from.isInArea(s.getMinimumLocation(),
						s.getMaximumLocation())) {
					for (int npc : s.getNPCIds()) {
						if (npc == npcId) {
							return s;
						}
					}
				}
			} else {
				for (int npc : s.getNPCIds()) {
					if (npc == npcId) {
						return s;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets an array of all the NPCs this shop can be opened at.
	 * 
	 * @return The NPC id's.
	 */
	private int[] getNPCIds() {
		return npcIds;
	}

	/**
	 * Checks if the npc have a shop, and opens it.
	 * 
	 * @param player
	 *            The player trying to access a shop.
	 * @param npc
	 *            The npc with the option 2.
	 * @param usePlayersLocation
	 *            Defines if we should use the location of the player, rather
	 *            than the NPC.
	 * @return false if not.
	 */
	public static boolean openShop(Player player, NPC npc,
			boolean usePlayersLocation) {
		Shop shop = getShop(
				usePlayersLocation ? player.getLocation() : npc.getLocation(),
				npc.getDefinition().getId());
		if (shop == null) {
			// logger.warning("The npc: "+npc.getDefinition().getName() + " " +
			// npc.getDefinition().getId()
			// +" should have a shop, but doesn't.");
			return false;
		} else {
			player.getActionSender().sendInventoryInterface(SHOP_INTERFACE,
					SHOP_INVENTORY_INTERFACE);
			player.getInterfaceState().addListener(
					shop.getItems(),
					new InterfaceContainerListener(player, SHOP_INTERFACE, 75,
							93));
			player.getInterfaceState().addListener(
					player.getInventory(),
					new InterfaceContainerListener(player,
							SHOP_INVENTORY_INTERFACE, 0, 93));
			player.getActionSender().sendString(shop.getName(), SHOP_INTERFACE,
					76);
			player.setCurrentShop(shop);
			return true;
		}

	}

	public static void buy(Player player, int slot, int itemId, int amount) {
		Shop shop = player.getCurrentShop();
		if (shop == null) {
			System.out.println("Shop was null.");
			return;
		}
		Item item = shop.getItems().get(slot);
		if (item == null) {
			return; // invalid packet, or client out of sync
		}
		if (item.getId() != itemId) {
			return; // invalid packet, or client out of sync
		}
		int buyAmount = item.getCount();
		if (buyAmount >= amount) {
			buyAmount = amount;
		} else if (buyAmount == 0) {
			return; // invalid packet, or client out of sync
		}
		int newId = item.getId();
		boolean usingAllPayment = false;
		ItemDefinition def = ItemDefinition.forId(newId);

		if (!def.isStackable()) {
			int free = player.getInventory().freeSlots();
			if (usingAllPayment) {
				free++;
			}
			if (buyAmount > free) {
				player.getActionSender()
						.sendMessage(
								"You don't have enough inventory space to buy that many.");
				buyAmount = free;
				// return; //Maybe you shouldn't return here, but hey.
			}
		}

		int price = (shop.getName().contains("General Store") ? def
				.getGeneralStorePrice() : def.getSpecialStorePrice())
				* buyAmount;
		if (price <= 0) {
			price = 1;
		}
		Item payment = new Item(shop.getCurrency(), price);
		Item actualCurrency = player.getInventory().getById(
				player.getCurrentShop().getCurrency());

		if (!player.getInventory().contains(payment) || actualCurrency == null) {
			player.getActionSender().sendMessage(
					"You don't have enough "
							+ payment.getDefinition().getName().toLowerCase()
							+ " to buy this item.");
			return;
		}

		if (actualCurrency.getCount() == payment.getCount()) {
			usingAllPayment = true;
		}

		if (def.isStackable()) {
			if (player.getInventory().freeSlots() <= (usingAllPayment ? 1 : 0)
					&& player.getInventory().getById(newId) == null) {
				player.getActionSender()
						.sendMessage(
								"You don't have enough inventory space to buy this item.");
				return; // Maybe you shouldn't return here, but hey.
			}
		}

		// now add it to inv
		// if(player.getInventory().add(new Item(newId, transferAmount))) {
		player.getInventory().remove(payment);
		player.getInventory().add(new Item(newId, buyAmount));
		// all items in the bank are stacked, makes it very easy!
		int newAmount = item.getCount() - buyAmount;
		if (newAmount <= 0) {
			for (Item defaultItem : shop.getDefaultItems()) {
				if (defaultItem.getId() == item.getId()) {
					shop.getItems().set(slot, new Item(item.getId(), 0));
					return; // Nothing else to do ;)
				}
			}
			shop.getItems().set(slot, null);
		} else {
			shop.getItems().set(slot, new Item(item.getId(), newAmount));
		}
		// } else {
		// player.getActionSender().sendMessage("You don't have enough inventory space to buy that many.");
		// }

	}

	public static void sell(Player player, int slot, int itemId, int amount) {
		if (Constants.playerBoundItem(itemId)) {
			player.getActionSender().sendMessage("You can't sell this item!");
		}
		Shop shop = player.getCurrentShop();
		boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
		player.getInventory().setFiringEvents(false);
		try {
			Item item = player.getInventory().get(slot);
			if (item == null) {
				return; // invalid packet, or client out of sync
			}
			if (item.getId() != itemId) {
				return; // invalid packet, or client out of sync
			}
			int sellAmount = player.getInventory().getCount(itemId);
			if (sellAmount >= amount) {
				sellAmount = amount;
			} else if (sellAmount == 0) {
				return; // invalid packet, or client out of sync
			}
			ItemDefinition def = item.getDefinition();
			boolean generalStore = shop.getName().contains("General Store");
			if (!generalStore) {// We check if the items is in the default
								// stock.
				boolean returnTime = true;
				for (Item defaultItem : shop.getDefaultItems()) {
					if (defaultItem.getId() == item.getId()) {
						returnTime = false;
						break;
					}
				}
				if (returnTime) {
					player.getActionSender().sendMessage(
							"You can't sell that item in this shop.");
					return; // We return.
				}
			}
			int buyingPrice = def.getLowAlcValue() * sellAmount;// generalStore
																// ?
																// def.getLowAlcValue()
																// :
																// def.getHighAlcValue();
			Item payment = new Item(shop.getCurrency(), buyingPrice);
			if (!player.getInventory().hasRoomFor(payment)) {
				player.getActionSender().sendMessage(
						"You don't have enough space in your inventory.");
				return;
			}
			if (item.getDefinition().isStackable()) {
				int shopId = item.getId();
				if (shop.getItems().freeSlots() < 1
						&& shop.getItems().getById(shopId) == null) {
					player.getActionSender().sendMessage(
							"The shop doesn't have enough space.");
				}
				// we only need to remove from one stack
				int newInventoryAmount = item.getCount() - sellAmount;
				Item newItem;
				if (newInventoryAmount <= 0) {
					newItem = null;
				} else {
					newItem = new Item(item.getId(), newInventoryAmount);
				}
				Item sellingItem = new Item(item.getId(), sellAmount);
				if (shop.getItems().add(sellingItem)) {
					player.getInventory().add(payment);
					player.getInventory().set(slot, newItem);
					player.getInventory().fireItemsChanged();
					shop.getItems().fireItemsChanged();
				} else {
					player.getActionSender().sendMessage(
							"The shop doesn't have enough space.");
				}
			} else {
				if (shop.getItems().freeSlots() < sellAmount) {
					player.getActionSender().sendMessage(
							"The shop doesn't have enough space.");
				}
				Item sellingItem = new Item(item.getId(), sellAmount);
				if (!shop.getItems().add(sellingItem)) {
					player.getActionSender().sendMessage(
							"The shop doesn't have enough space.");
				} else {
					player.getInventory().add(payment);
					// we need to remove multiple items
					for (int i = 0; i < sellAmount; i++) {
						/*
						 * if(i == 0) { player.getInventory().set(slot, null); }
						 * else {
						 */
						player.getInventory()
								.set(player.getInventory().getSlotById(
										item.getId()), null);
						// }
					}
					player.getInventory().fireItemsChanged();
				}
			}
		} finally {
			player.getInventory().setFiringEvents(inventoryFiringEvents);
		}

	}

	static {
		GENERAL_STORES = new Shop[GENERAL_STORE_LOCATIONS.length];
		final int[] npcIds = { 520/* Gen. store owner */, 521/*
															 * Gen.store
															 * assistant
															 */, 522 /*
																	 * Gen.
																	 * store
																	 * owner
																	 */, 523 /*
																			 * Gen.
																			 * store
																			 * assistant
																			 */};
		for (int index = 0; index < GENERAL_STORES.length; index++) {
			/*
			 * We start by setting up the general store container.
			 */
			Container generalStore = new Container(Type.ALWAYS_STACK, SIZE);
			generalStore.add(new Item(1931, 30));// Pot
			generalStore.add(new Item(1935, 10));// Jug
			generalStore.add(new Item(5603, 10));// Shears
			generalStore.add(new Item(1925, 30));// Bucket
			generalStore.add(new Item(1923, 10));// Bowl
			generalStore.add(new Item(1887, 10));// Cake tin
			generalStore.add(new Item(590, 10));// Tinderbox
			generalStore.add(new Item(1755, 10));// Chisel
			generalStore.add(new Item(2347, 10));// Hammer
			generalStore.add(new Item(550, 10));// Newcomer map
			generalStore.add(new Item(9003, 10));// Security book
			generalStore.add(new Item(946));// Knife
			/*
			 * Then we add all the actual general stores, with locations, names,
			 * containers and everything.
			 */
			GENERAL_STORES[index] = new Shop(GENERAL_STORE_NAMES[index],
					generalStore, GENERAL_STORE_LOCATIONS[index][0],
					GENERAL_STORE_LOCATIONS[index][1], 995, npcIds);
		}
	}

}
