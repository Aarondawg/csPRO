package org.hyperion.rs2.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.hyperion.data.Persistable;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.action.impl.AttackingAction;
import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.content.Jewellery;
import org.hyperion.rs2.content.Shop;
import org.hyperion.rs2.content.Specials;
import org.hyperion.rs2.model.Combat.AttackType;
import org.hyperion.rs2.content.minigames.Barrows;
import org.hyperion.rs2.content.minigames.FightCaves;
import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.content.minigames.WarriorsGuild;
import org.hyperion.rs2.content.skills.CookingVariables;
import org.hyperion.rs2.content.skills.CraftingVariables;
import org.hyperion.rs2.content.skills.FiremakingVariables;
import org.hyperion.rs2.content.skills.FletchingVariables;
import org.hyperion.rs2.content.skills.Prayer;
import org.hyperion.rs2.content.skills.Slayer;
import org.hyperion.rs2.content.skills.SlayerTask;
import org.hyperion.rs2.content.skills.construction.Construction;
import org.hyperion.rs2.content.skills.construction.ConstructionObject;
import org.hyperion.rs2.content.skills.construction.Room;
import org.hyperion.rs2.content.skills.farming.CompostBin;
import org.hyperion.rs2.content.skills.farming.FarmingObject;
import org.hyperion.rs2.content.skills.farming.FarmingPatch;
import org.hyperion.rs2.content.skills.farming.FarmingPlant;
import org.hyperion.rs2.content.skills.magic.Magic;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.DeathEvent;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Duel;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.region.Region;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.net.ISAACCipher;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.util.FarmingList;
import org.hyperion.rs2.util.IoBufferUtils;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.rs2.model.npc.fightcaves.TzKid;

/**
 * Represents a player-controller character.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Player extends Entity implements Persistable {

	/**
	 * Represents the rights of a player.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public enum Rights {

		/**
		 * A standard account.
		 */
		PLAYER(0),

		/**
		 * A player-moderator account.
		 */
		MODERATOR(1),

		/**
		 * An administrator account.
		 */
		ADMINISTRATOR(2);

		/**
		 * The integer representing this rights level.
		 */
		private int value;

		/**
		 * Creates a rights level.
		 * 
		 * @param value
		 *            The integer representing this rights level.
		 */
		private Rights(int value) {
			this.value = value;
		}

		/**
		 * Gets an integer representing this rights level.
		 * 
		 * @return An integer representing this rights level.
		 */
		public int toInteger() {
			return value;
		}

		/**
		 * Gets rights by a specific integer.
		 * 
		 * @param value
		 *            The integer returned by {@link #toInteger()}.
		 * @return The rights level.
		 */
		public static Rights getRights(int value) {
			if (value == 1) {
				return MODERATOR;
			} else if (value == 2) {
				return ADMINISTRATOR;
			} else {
				return PLAYER;
			}
		}
	}

	/*
	 * Attributes specific to our session.
	 */

	/**
	 * The <code>IoSession</code>.
	 */
	private final IoSession session;

	/**
	 * The ISAAC cipher for incoming data.
	 */
	private final ISAACCipher inCipher;

	/**
	 * The ISAAC cipher for outgoing data.
	 */
	private final ISAACCipher outCipher;

	/**
	 * The action sender.
	 */
	private final ActionSender actionSender = new ActionSender(this);

	/**
	 * A queue of pending chat messages.
	 */
	private final Queue<ChatMessage> chatMessages = new LinkedList<ChatMessage>();

	/**
	 * The current chat message.
	 */
	private ChatMessage currentChatMessage;

	/**
	 * Active flag: if the player is not active certain changes (e.g. items)
	 * should not send packets as that indicates the player is still loading.
	 */
	private boolean active = false;

	/**
	 * The interface state.
	 */
	private final InterfaceState interfaceState = new InterfaceState(this);

	/**
	 * A queue of packets that are pending.
	 */
	private final Queue<Packet> pendingPackets = new LinkedList<Packet>();

	/**
	 * The request manager which manages trading and duelling requests.
	 */
	private final RequestManager requestManager = new RequestManager(this);

	/*
	 * Core login details.
	 */

	/**
	 * The name.
	 */
	private String name;

	/**
	 * The name expressed as a long.
	 */
	private long nameLong;

	/**
	 * The UID, i.e. number in <code>random.dat</code>.
	 */
	private final int uid;

	/**
	 * The password.
	 */
	private String password;

	/**
	 * The rights level.
	 */
	private Rights rights = Rights.PLAYER;

	/**
	 * The members flag.
	 */
	private boolean members = true;

	/*
	 * Attributes.
	 */

	/**
	 * The player's appearance information.
	 */
	private final Appearance appearance = new Appearance();

	/**
	 * The player's equipment.
	 */
	private final Container equipment = new Container(Container.Type.STANDARD,
			Equipment.SIZE);

	/**
	 * The player's skill levels.
	 */
	private final Skills skills = new Skills(this);

	/**
	 * The player's inventory.
	 */
	private final Container inventory = new Container(Container.Type.STANDARD,
			Inventory.SIZE);

	/**
	 * The player's bank.
	 */
	private final Container bank = new Container(Container.Type.ALWAYS_STACK,
			Bank.SIZE);

	/**
	 * The player's settings.
	 */
	private final Settings settings = new Settings();

	/*
	 * Cached details.
	 */
	/**
	 * The cached update block.
	 */
	private Packet cachedUpdateBlock;

	/**
	 * The players current dialougeLoader.
	 */
	private DialogueLoader dialougeLoader;

	/**
	 * The players specials.
	 */
	private final Specials specials = new Specials(this);

	/**
	 * The players magic instance.
	 */
	private final Magic magic = new Magic(this);

	/**
	 * The players skulls.
	 */
	private final Skulls skulls = new Skulls(this);

	/**
	 * The players bonuses.
	 */
	private final Bonuses bonuses = new Bonuses(this);

	/**
	 * The players prayer class.
	 */
	private final Prayer prayer = new Prayer(this);

	/**
	 * The players construction class, containing the players house etc.
	 */
	private final Construction construction = new Construction(this);

	/**
	 * The players slayer assignment.
	 */
	private SlayerTask slayerTask = new SlayerTask("", 0, 0, Slayer.TURAEL);

	/**
	 * The players farming list.
	 */
	private FarmingList farmingList = new FarmingList();

	/**
	 * All the variables and ugly methods we didnt want in here.
	 */
	private final CookingVariables cookingVariables = new CookingVariables();

	/**
	 * All the crafting variables and ugly methods we didnt want in here.
	 */
	private final CraftingVariables craftingVariables = new CraftingVariables();

	/**
	 * All the firemaking variables and ugly methods we didn't want in here.
	 */
	private final FiremakingVariables firemakingVariables = new FiremakingVariables();

	/**
	 * All the fletching variables and ugly methods we didn't want in here.
	 */
	private final FletchingVariables fletchingVariables = new FletchingVariables();

	/**
	 * The class holding the players current friends as well as ignores.
	 */
	private final Friends friends = new Friends(this);

	/**
	 * The class holding the players jevelery variables.
	 */
	private final Jewellery jewellery = new Jewellery();

	/**
	 * The players Barrows minigame.
	 */
	private final Barrows barrows = new Barrows();

	/**
	 * The players Warriors Guild minigame.
	 */
	private final WarriorsGuild warriorsGuild = new WarriorsGuild(this);

	/**
	 * The players fight caves minigame.
	 */
	private final FightCaves fightcaves = new FightCaves(this);

	/**
	 * The players current shop.
	 */
	private Shop currentShop = null;

	/**
	 * The player's quest points.
	 */
	private int questPoints = 0;

	/**
	 * Flag to indicate whenever or not we're teleblocked.
	 */
	private boolean isTeleBlocked;

	/**
	 * Flag to indicate whenever or not we're frozen.
	 */
	private boolean frozen = false;

	private boolean hasRecievedHolidayItems = false;

	/**
	 * A simple hashmap of temporary attributes.
	 */
	private transient Map<String, Object> temporaryAttributes = new HashMap<String, Object>();;

	/**
	 * Longs used as timers.
	 */
	public long lastBury = 0;
	private long lastEat = 0;
	private long lastDrink = 0;
	public long lastObjectClick = 0;
	public long lastNPCClick = 0;
	public long lastMagicOnItem = 0;

	/**
	 * Creates a player based on the details object.
	 * 
	 * @param details
	 *            The details object.
	 */
	public Player(PlayerDetails details) {
		super();
		this.setLocation(DEFAULT_LOCATION);
		this.setLastKnownRegion(Entity.DEFAULT_LOCATION);
		this.session = details.getSession();
		this.inCipher = details.getInCipher();
		this.outCipher = details.getOutCipher();
		this.name = details.getName();
		this.nameLong = NameUtils.nameToLong(this.name);
		this.password = details.getPassword();
		this.uid = details.getUID();
		this.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		this.setTeleporting(true);
	}

	/**
	 * Updates the players barrows state.
	 */
	public boolean updateBarrowsState() {
		if (getLocation().isInBarrows()) {
			actionSender.sendWalkableInterface(24);
			actionSender.sendString("Kill Count: " + barrows.getKillCount(),
					24, 0);
			actionSender.sendMinimapBlackout(5);
			return true;
		} else {
			barrows.setGameFinished(false);
			// actionSender.sendWalkableInterface(-1); //Done by the wilderness
			// thing.
			if (interfaceState.getCurrentInterface() != 399) { // Player owned
																// house.
				actionSender.sendMinimapBlackout(0);
			}
			return false;
		}

	}

	private int lastKnownWildernessLevel = -1;

	public void updateWildernessState() {
		if (this.getLocation().isInDuelArena()) {
			actionSender.sendWalkableInterface(389);
			actionSender.sendInteractionOption("Challenge", 1, false);
			if (this.getLocation().isDueling()) {
				actionSender.sendInteractionOption("Attack", 1, false);
			}
			return;
		}
		boolean update = false;
		if (lastKnownWildernessLevel == -1) {
			update = true;
		}
		int currentLevel = Location.getWildernessLevel(this.getLocation());
		if (currentLevel != lastKnownWildernessLevel) {
			update = true;
		}
		if (getLocation().isInMulti()) {
			actionSender.sendMultiIcon(1);
		} else {
			actionSender.sendMultiIcon(0);
		}
		if (update) {
			lastKnownWildernessLevel = currentLevel;
			if (currentLevel == 0) {
				actionSender.sendInteractionOption("null", 1, false);
				actionSender.sendWalkableInterface(-1);
			} else {
				actionSender.sendInteractionOption("Attack", 1, false);
				actionSender.sendWalkableInterface(381);
			}
		}
	}

	/**
	 * Gets the request manager.
	 * 
	 * @return The request manager.
	 */
	public RequestManager getRequestManager() {
		return requestManager;
	}

	/**
	 * Gets the player's name expressed as a long.
	 * 
	 * @return The player's name expressed as a long.
	 */
	public long getNameAsLong() {
		return nameLong;
	}

	/**
	 * Gets the player's settings.
	 * 
	 * @return The player's settings.
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * Writes a packet to the <code>IoSession</code>. If the player is not yet
	 * active, the packets are queued.
	 * 
	 * @param packet
	 *            The packet.
	 */
	public void write(Packet packet) {
		synchronized (this) {
			if (!active) {
				pendingPackets.add(packet);
			} else {
				for (Packet pendingPacket : pendingPackets) {
					session.write(pendingPacket);
				}
				pendingPackets.clear();
				session.write(packet);
			}
		}
	}

	/**
	 * Gets the player's bank.
	 * 
	 * @return The player's bank.
	 */
	public Container getBank() {
		return bank;
	}

	/**
	 * Gets the interface state.
	 * 
	 * @return The interface state.
	 */
	public InterfaceState getInterfaceState() {
		return interfaceState;
	}

	/**
	 * Checks if there is a cached update block for this cycle.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasCachedUpdateBlock() {
		return cachedUpdateBlock != null;
	}

	/**
	 * Sets the cached update block for this cycle.
	 * 
	 * @param cachedUpdateBlock
	 *            The cached update block.
	 */
	public void setCachedUpdateBlock(Packet cachedUpdateBlock) {
		this.cachedUpdateBlock = cachedUpdateBlock;
	}

	/**
	 * Gets the cached update block.
	 * 
	 * @return The cached update block.
	 */
	public Packet getCachedUpdateBlock() {
		return cachedUpdateBlock;
	}

	/**
	 * Resets the cached update block.
	 */
	public void resetCachedUpdateBlock() {
		cachedUpdateBlock = null;
	}

	/**
	 * Gets the current chat message.
	 * 
	 * @return The current chat message.
	 */
	public ChatMessage getCurrentChatMessage() {
		return currentChatMessage;
	}

	/**
	 * Sets the current chat message.
	 * 
	 * @param currentChatMessage
	 *            The current chat message to set.
	 */
	public void setCurrentChatMessage(ChatMessage currentChatMessage) {
		this.currentChatMessage = currentChatMessage;
	}

	/**
	 * Gets the queue of pending chat messages.
	 * 
	 * @return The queue of pending chat messages.
	 */
	public Queue<ChatMessage> getChatMessageQueue() {
		return chatMessages;
	}

	/**
	 * Gets the player's appearance.
	 * 
	 * @return The player's appearance.
	 */
	public Appearance getAppearance() {
		return appearance;
	}

	/**
	 * Gets the player's equipment.
	 * 
	 * @return The player's equipment.
	 */
	public Container getEquipment() {
		return equipment;
	}

	/**
	 * Gets the player's skills.
	 * 
	 * @return The player's skills.
	 */
	public Skills getSkills() {
		return skills;
	}

	/**
	 * Gets the action sender.
	 * 
	 * @return The action sender.
	 */
	public ActionSender getActionSender() {
		return actionSender;
	}

	/**
	 * Gets the incoming ISAAC cipher.
	 * 
	 * @return The incoming ISAAC cipher.
	 */
	public ISAACCipher getInCipher() {
		return inCipher;
	}

	/**
	 * Gets the outgoing ISAAC cipher.
	 * 
	 * @return The outgoing ISAAC cipher.
	 */
	public ISAACCipher getOutCipher() {
		return outCipher;
	}

	/**
	 * Gets the player's name.
	 * 
	 * @return The player's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the player's password.
	 * 
	 * @return The player's password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the player's password.
	 * 
	 * @param pass
	 *            The password.
	 */
	public void setPassword(String pass) {
		this.password = pass;
	}

	/**
	 * Gets the player's UID.
	 * 
	 * @return The player's UID.
	 */
	public int getUID() {
		return uid;
	}

	/**
	 * Gets the <code>IoSession</code>.
	 * 
	 * @return The player's <code>IoSession</code>.
	 */
	public IoSession getSession() {
		return session;
	}

	/**
	 * Sets the rights.
	 * 
	 * @param rights
	 *            The rights level to set.
	 */
	public void setRights(Rights rights) {
		this.rights = rights;
	}

	/**
	 * Gets the rights.
	 * 
	 * @return The player's rights.
	 */
	public Rights getRights() {
		return rights;
	}

	/**
	 * Checks if this player has a member's account.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isMembers() {
		return members;
	}

	/**
	 * Sets the members flag.
	 * 
	 * @param members
	 *            The members flag.
	 */
	public void setMembers(boolean members) {
		this.members = members;
	}

	@Override
	public String toString() {
		return Player.class.getName() + " [name=" + name + " rights=" + rights
				+ " members=" + members + " index=" + this.getIndex() + "]";
	}

	/**
	 * Sets the active flag.
	 * 
	 * @param active
	 *            The active flag.
	 */
	public void setActive(boolean active) {
		synchronized (this) {
			this.active = active;
		}
	}

	/**
	 * Gets the active flag.
	 * 
	 * @return The active flag.
	 */
	public boolean isActive() {
		synchronized (this) {
			return active;
		}
	}

	/**
	 * Gets the inventory.
	 * 
	 * @return The inventory.
	 */
	public Container getInventory() {
		return inventory;
	}

	/**
	 * Updates the players' options when in a PvP area.
	 */
	public void updatePlayerAttackOptions(boolean enable) {
		if (enable) {
			actionSender.sendInteractionOption("Attack", 1, true);
			// actionSender.sendOverlay(381);
		} else {

		}
	}

	/**
	 * Manages updateflags and HP modification when a hit occurs.
	 * 
	 * @param source
	 *            The Entity dealing the blow.
	 */
	@Override
	public void inflictDamage(Hit inc, Entity source) {
		/**
		 * Ignores the hit if we recently teleported.
		 */
		if (magic.getLastTeleport() < 3000) {
			return;
		}
		actionSender.sendCloseInterface();
		if (source != null) {
			jewellery.recoilingEffect(this, source, inc.getDamage());
			Combat.vengeance(this, source, inc.getDamage());
			if (source instanceof Player) {
				skulls.checkForSkull((Player) source);
			} else {
				/*
				 * We check if the attacker is one of the fight cave bats, and
				 * we reduce the players prayer..
				 */
				if (source instanceof TzKid) {
					skills.detractLevel(Skills.PRAYER, 1 + inc.getDamage());
				}
			}
			if (this.isAutoRetaliating() && !this.isInCombat()
					&& this.getWalkingQueue().isEmpty()) {
				this.setInCombat(true);
				this.setAggressorState(false);
				this.setInteractingEntity(source);
				this.setCombatDelay(getAttackSpeed() / 2); // Slight delay
															// before we attack
															// back.
				this.getActionQueue().addAction(
						new AttackingAction(this, source));
			}
		}
		if (!getUpdateFlags().get(UpdateFlag.HIT)) {
			getDamage().setHit1(inc);
			getUpdateFlags().flag(UpdateFlag.HIT);
		} else {
			if (!getUpdateFlags().get(UpdateFlag.HIT_2)) {
				getDamage().setHit2(inc);
				getUpdateFlags().flag(UpdateFlag.HIT_2);
			}
		}
		skills.detractLevel(Skills.HITPOINTS, inc.getDamage());
		if (skills.getLevel(Skills.HITPOINTS) <= 0) {
			if (!this.isDead()) {
				this.getWalkingQueue().reset();
				World.getWorld().submit(new Event(2000) {
					public void execute() {
						playAnimation(Animation.create(getDeathAnimation()));
						this.stop();
					}
				});

				/*
				 * We fire a death event in the Dueling class if we died in the
				 * dueling arena.
				 */
				if (this.getRequestManager().isDueling()
						&& this.getLocation().isInDuelArena()) {
					this.getRequestManager().getDuel().handleDeath(this);
					/*
					 * If we die in PestControl, we make sure to get back into
					 * the boat.
					 */
				} else if (!PestControl.handleDeath(this)) {
					/*
					 * If not, we go on as usual.
					 */
					if (source instanceof Player) {
						Player pSource = (Player) source;
						Entity killer = getKiller();
						if (killer == source) {
							pSource.getActionSender().sendMessage(
									"You have defeated "
											+ NameUtils.formatName(name) + ".");
						}
					}
					World.getWorld().submit(new DeathEvent(this));
				}

			}
			this.getActionQueue().cancelQueuedActions();
			this.setDead(true);
			this.setInCombat(false);
		}
	}

	@Override
	public void deserialize(IoBuffer buf) {
		this.name = IoBufferUtils.getString(buf);
		this.nameLong = NameUtils.nameToLong(this.name);
		this.password = IoBufferUtils.getString(buf);
		this.rights = Player.Rights.getRights(buf.getUnsigned());
		this.magic.setSpellBook(buf.getUnsignedShort());
		this.members = buf.getUnsigned() == 1;
		setLocation(Location.create(buf.getUnsignedShort(),
				buf.getUnsignedShort(), buf.getUnsigned()));
		int[] look = new int[13];
		for (int i = 0; i < 13; i++) {
			look[i] = buf.getUnsigned();
		}
		appearance.setLook(look);
		for (int i = 0; i < Equipment.SIZE; i++) {
			int id = buf.getUnsignedShort();
			if (id != 65535) {
				int amt = buf.getInt();
				Item item = new Item(id, amt);
				equipment.set(i, item);
			}
		}
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			skills.setSkill(i, buf.getUnsigned(), buf.getDouble());
		}
		for (int i = 0; i < Inventory.SIZE; i++) {
			int id = buf.getUnsignedShort();
			if (id != 65535) {
				int amt = buf.getInt();
				Item item = new Item(id, amt);
				inventory.set(i, item);
			}
		}
		for (int i = 0; i < Bank.SIZE; i++) {
			int id = buf.getUnsignedShort();
			if (id != 65535) {
				int amt = buf.getInt();
				Item item = new Item(id, amt);
				bank.set(i, item);
			}
		}
		for (int j = 0; j < questInfo.length; j++) {
			for (int i = 0; i < questInfo[j].length; i++) {
				questInfo[j][i] = buf.getUnsignedShort();
			}
		}
		questPoints = buf.getUnsignedShort();
		int master = buf.getUnsignedShort();
		int count = buf.getUnsignedShort();
		int index = buf.getUnsignedShort();
		slayerTask = new SlayerTask(count, index, master);
		skulls.setup(buf.get());
		getPoison().setup(buf.get(), buf.get(), buf.get());
		int listLength = buf.getUnsignedShort();
		farmingList.setSize(listLength);
		for (int i = 0; i < listLength; i++) {
			int x = buf.getUnsignedShort();
			int y = buf.getUnsignedShort();
			byte aIndex = buf.get();
			byte a = buf.get();
			switch (a) {
			case 0:// Farming plant.
				byte seedIndex = buf.get();
				byte healthState = buf.get();
				byte time = buf.get();
				boolean superCompost = buf.get() == 1;
				byte currentStage = buf.get();
				byte diseasingTick = buf.get();
				boolean water = false; // TODO: THIS SHOULD JUST BE FALSE AM I
										// RIGHT??
				farmingList.set(i, new FarmingPlant(Location.create(x, y, 0),
						aIndex, seedIndex, healthState, time, superCompost,
						currentStage, diseasingTick, water));
				break;
			case 1:// Compost bin.
				break;
			case 2:// Farming path.
				break;
			}
		}
		int fLength = buf.get();
		for (int i = 0; i < fLength; i++) {
			friends.getFriends().add(buf.getLong());
		}
		int iLength = buf.get();
		for (int i = 0; i < iLength; i++) {
			friends.getIgnores().add(buf.getLong());
		}
		settings.setAcceptAidEnabled(buf.get() == 1);
		settings.setBrightness(buf.get());
		settings.setChatEffectsEnabled(buf.get() == 1);
		settings.setMouseTwoButtons(buf.get() == 1);
		settings.setPrivateChatSplit(buf.get() == 1);
		settings.setHasReceivedtarterItems(buf.get() == 1);
		setAutoRetaliating(buf.get() == 1);
		jewellery.setRecoilCount(buf.get());
		int kc = buf.getInt();
		boolean finished = buf.get() == 1;
		int[] tomb = new int[6];
		for (int i = 0; i < tomb.length; i++) {
			tomb[i] = buf.get();
		}
		barrows.load(finished, kc, tomb);
		pestControlPoints = buf.get();
		org.hyperion.rs2.content.skills.construction.House.HouseDesign design = org.hyperion.rs2.content.skills.construction.House.HouseDesign
				.getDesign(buf.get());
		construction.getHouse().setDesign(design);
		int length = buf.get();
		for (int i = 0; i < length; i++) {
			int x = buf.get();
			int y = buf.get();
			int z = buf.get();
			Room.RoomType type = Room.RoomType.getType(buf.getUnsignedShort(),
					buf.getUnsignedShort());
			int l = buf.getUnsignedShort();
			// System.out.println("Length: " + l);
			ConstructionObject[] objects = new ConstructionObject[l];
			for (int j = 0; j < l; j++) {
				int id = buf.getUnsignedShort();
				// System.out.println("ID: " + id);
				if (id != 65535) {
					int x1 = buf.getUnsignedShort();
					int y1 = buf.getUnsignedShort();
					int plane = buf.get();
					int type1 = buf.get();
					int rotation = buf.get();
					ConstructionObject obj = new ConstructionObject(
							GameObjectDefinition.forId(id), Location.create(x1,
									y1, plane), type1, rotation, false);
					objects[j] = obj;
				}
			}
			Room room = new Room(type, objects, this, x, y, z);
			construction.getHouse().addRoom(room);
		}
		hasRecievedHolidayItems = buf.get() == 1;
		while (buf.hasRemaining()) {
			System.out.println("WARNING: Didnt read the entire player buffer!");
			buf.get();
		}
	}

	@Override
	public void serialize(IoBuffer buf) {
		IoBufferUtils.putString(buf, NameUtils.formatName(name));
		IoBufferUtils.putString(buf, password);
		buf.put((byte) rights.toInteger());
		buf.putShort((short) magic.getSpellBook().toInteger());
		buf.put((byte) (members ? 1 : 0));
		buf.putShort((short) getLocation().getX());
		buf.putShort((short) getLocation().getY());
		buf.put((byte) getLocation().getZ());
		int[] look = appearance.getLook();
		for (int i = 0; i < 13; i++) {
			buf.put((byte) look[i]);
		}
		for (int i = 0; i < Equipment.SIZE; i++) {
			Item item = equipment.get(i);
			if (item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			buf.put((byte) skills.getLevel(i));
			buf.putDouble((double) skills.getExperience(i));
		}
		for (int i = 0; i < Inventory.SIZE; i++) {
			Item item = inventory.get(i);
			if (item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		for (int i = 0; i < Bank.SIZE; i++) {
			Item item = bank.get(i);
			if (item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		for (int j = 0; j < questInfo.length; j++) {
			for (int i = 0; i < questInfo[j].length; i++) {
				// Always two, except for a few exceptions
				buf.putShort((short) questInfo[j][i]);
			}
		}
		buf.putShort((short) questPoints);
		buf.putShort((short) slayerTask.getSlayerMaster());
		buf.putShort((short) slayerTask.getCount());
		buf.putShort((short) slayerTask.getTaskIndex());
		buf.put((byte) skulls.getSkullTimer());
		final int[] skullVariables = getPoison().getVariables();
		buf.put((byte) skullVariables[0]);
		buf.put((byte) skullVariables[1]);
		buf.put((byte) skullVariables[2]);
		FarmingObject[] list = farmingList.getArray();
		buf.putShort((short) list.length);
		for (FarmingObject fo : list) {
			buf.putShort((short) fo.getLocation().getX());
			buf.putShort((short) fo.getLocation().getY());
			// No need to do height, they're all at height 0.
			buf.put((byte) fo.getArrayIndex());
			if (fo instanceof FarmingPlant) {
				buf.put((byte) 0);// Means its a farming plant.
				FarmingPlant fp = (FarmingPlant) fo;
				buf.put((byte) fp.getSeedIndex());
				buf.put((byte) fp.getHealthState());
				buf.put((byte) fp.getTime());
				buf.put((byte) (fp.hasSuperCompostSoil() ? 1 : 0));
				buf.put((byte) fp.getCurrentStage());
				buf.put((byte) fp.getDiseasingTick());
			} else if (fo instanceof CompostBin) {
				buf.put((byte) 1);// Means its a compost bin.
			} else if (fo instanceof FarmingPatch) {
				buf.put((byte) 2);// Means its a farming path.

			}
		}
		buf.put((byte) friends.getFriends().size());
		for (long friend : friends.getFriends()) {
			buf.putLong(friend);
		}
		buf.put((byte) friends.getIgnores().size());
		for (long ignore : friends.getIgnores()) {
			buf.putLong(ignore);
		}
		buf.put((byte) (settings.isAcceptAidEnabled() ? 1 : 0));
		buf.put((byte) settings.getBrightness());
		buf.put((byte) (settings.isChatEffectsEnabled() ? 1 : 0));
		buf.put((byte) (settings.isMouseTwoButtons() ? 1 : 0));
		buf.put((byte) (settings.isPrivateChatSplit() ? 1 : 0));
		buf.put((byte) (settings.hasReceivedtarterItems() ? 1 : 0));
		buf.put((byte) (isAutoRetaliating() ? 1 : 0));
		buf.put((byte) (jewellery.getRecoilCount()));
		buf.putInt(barrows.getKillCount());
		buf.put((byte) (barrows.isGameFinished() ? 1 : 0));
		for (int info : barrows.getTombInformation()) {
			buf.put((byte) info);
		}
		buf.put(pestControlPoints);
		buf.put((byte) construction.getHouse().getHouseDesign().getLevel());
		buf.put((byte) construction.getHouse().getRooms().length);
		for (Room room : construction.getHouse().getRooms()) {
			buf.put((byte) (room.getX() / 8));
			buf.put((byte) (room.getY() / 8));
			buf.put((byte) room.getHeight());
			buf.putShort((short) room.getRoomType()
					.getPaletteTile(construction.getHouse().getHouseDesign())
					.getActualX());
			buf.putShort((short) room.getRoomType()
					.getPaletteTile(construction.getHouse().getHouseDesign())
					.getActualY());
			buf.putShort((short) room.getObjects().length);
			for (ConstructionObject obj : room.getObjects()) {
				if (obj == null) {
					buf.putShort((short) 65535);
				} else {
					// System.out.println("Here lul");
					buf.putShort((short) obj.getDefinition().getId());
					buf.putShort((short) obj.getLocation().getX());
					buf.putShort((short) obj.getLocation().getY());
					buf.put((byte) obj.getLocation().getZ());
					buf.put((byte) obj.getType());
					buf.put((byte) obj.getRotation());
				}
			}
		}
		buf.put((byte) (hasRecievedHolidayItems ? 1 : 0));
	}

	@Override
	public void addToRegion(Region region) {
		region.addPlayer(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removePlayer(this);
	}

	@Override
	public int getClientIndex() {
		return this.getIndex() + 32768;
	}

	@Override
	public void heal(int amount) {
		skills.heal(amount, false);
	}

	@Override
	public void heal(int amount, boolean canGoOverMax) {
		skills.heal(amount, canGoOverMax);
	}

	/**
	 * Sets the current DialogueLoader for the ChatboxInterface packet handler.
	 */
	public void setCurrentDialogueLoader(DialogueLoader dialougeLoader) {
		this.dialougeLoader = dialougeLoader;
	}

	/**
	 * Gets the current DialogueLoader for the ChatboxInterface packet handler.
	 */
	public DialogueLoader getCurrentDialogueLoader() {
		return dialougeLoader;
	}

	/**
	 * Gets the players specials.
	 */
	public Specials getSpecials() {
		return specials;
	}

	/**
	 * Gets the players bonuses.
	 */
	public Bonuses getBonuses() {
		return bonuses;
	}

	/**
	 * Gets the players current slayerTask.
	 */
	public SlayerTask getSlayerTask() {
		return slayerTask;
	}

	/**
	 * Sets the players next slayerTask.
	 */
	public void setSlayerTask(SlayerTask task) {
		this.slayerTask = task;
	}

	/**
	 * The player's quest stage array. {QuestStae, Amount of some item needed,
	 * Some other shit, some other shit 2 ;)}
	 */
	private int[][] questInfo = { { 0 },// Tutorial island
			{ 0 },// Black Knights Fortress.
			{ 0 },// Desert Treasure.
			{ 0 },// Legends Quest.
			{ 0 },// Lost City.
			{ 0 },// Lunar Diplomacy.
			{ 0 },// Monkey Madness..
			{ 0 },// Between a Rock.
	};

	/**
	 * Gets the players quest points.
	 */
	public int getQuestPoints() {
		return questPoints;
	}

	/**
	 * Sets the players quest points.
	 */
	public void setQuestPoints(int questPoints) {
		this.questPoints = questPoints;
	}

	/**
	 * Gets the quest info array.
	 * 
	 * @return The quest stage array.
	 */
	public int[][] getQuestInfo() {
		return questInfo;
	}

	/**
	 * Edits a piece of quest info, with the two indexes provided,
	 * 
	 * @param questInfoIndex1
	 *            - The index of the actual quest.
	 * @param questInfoIndex2
	 *            - The index of the information we want to edit.
	 * @param newInfo
	 *            - The new info.
	 */
	public void editQuestInfo(int questInfoIndex1, int questInfoIndex2,
			int newInfo) {
		questInfo[questInfoIndex1][questInfoIndex2] = newInfo;
	}

	/**
	 * Sets a temporary attribute.
	 */
	public void setTemporaryAttribute(String attribute, Object value) {
		temporaryAttributes.put(attribute, value);
	}

	/**
	 * Gets a temporary attribute by a string.
	 */
	public Object getTemporaryAttribute(String attribute) {
		return temporaryAttributes.get(attribute);
	}

	/**
	 * Removes a temporary attribute by a string.
	 */
	public void removeTemporaryAttribute(String attribute) {
		temporaryAttributes.remove(attribute);
	}

	/**
	 * Sets the players teleblocked state.
	 */
	public void setTeleblocked(boolean teleBlocked) {
		this.isTeleBlocked = teleBlocked;
	}

	/**
	 * Tells us if the player is teleblocked.
	 */
	public boolean isTeleblocked() {
		return isTeleBlocked;
	}

	@Override
	public Magic getMagic() {
		return magic;
	}

	/**
	 * Indicates if we can walk or not. Used for teleporting, POH, duel arena
	 * etc.
	 */
	@Override
	public boolean canWalk() {
		if (frozen) {
			actionSender.sendMessage("A magical force stops you from moving!");
		}
		return super.canWalk();
	}

	/**
	 * Sets the players frozen state. (Bind, snare, Ice barrage etc)
	 */
	public void setFrozen(boolean b) {
		this.setCanWalk(!b);// They're opposites lol.
		this.frozen = b;
	}

	@Override
	public void setCanWalk(boolean b) {
		if (b) {
			if (this.getRequestManager().isDueling()
					&& this.getLocation().isDueling()) {
				if (this.getRequestManager().getDuel()
						.isRuleToggled(Duel.NO_MOVEMENT)) {
					return;
				}
			}
		} else {
			this.getActionSender().sendStopFollowing(true);
		}
		super.setCanWalk(b);
	}

	public boolean isFrozen() {
		return frozen;
	}

	public Skulls getSkulls() {
		return skulls;
	}

	/**
	 * Adds a farming object to the farming list.
	 */
	public void addToFarmingList(FarmingObject farmingObject) {
		farmingList.put(farmingObject);
	}

	/**
	 * Removes a single farming object from the farming list.
	 */
	public boolean replaceFarmingObjects(FarmingObject oldOne,
			FarmingObject newOne) {
		return farmingList.replace(oldOne, newOne);

	}

	/**
	 * Gets the entire farming list as an array.
	 * 
	 * @return The farming list as an array.
	 */
	public FarmingObject[] getFarmingObjectArray() {
		return farmingList.getArray();
	}

	/**
	 * Returns the shop we're currently shopping in.
	 * 
	 * @return The shop we're currently shopping in, or null.
	 */
	public Shop getCurrentShop() {
		return currentShop;
	}

	/**
	 * Sets the players current shop.
	 */
	public void setCurrentShop(Shop shop) {
		this.currentShop = shop;
	}

	/**
	 * Index handling the genie lamp. //TODO: Put those in some player only
	 * random event thing.
	 */
	private int genieLampIndex = -1;

	public void setGenieLampIndex(int genieLampIndex) {
		this.genieLampIndex = genieLampIndex;
	}

	public int getGenieLampIndex() {
		return genieLampIndex;
	}

	/**
	 * Gets the players cooking variables.
	 * 
	 * @return A class holding variables and method we didn't want in here.
	 */
	public CookingVariables getCookingVariables() {
		return cookingVariables;
	}

	/**
	 * Gets the players Crafting variables.
	 * 
	 * @return A class holding variables and method we didn't want in here.
	 */
	public CraftingVariables getCraftingVariables() {
		return craftingVariables;
	}

	/**
	 * Gets the players Firemaking variables.
	 * 
	 * @return A class holding variables and method we didn't want in here.
	 */
	public FiremakingVariables getFiremakingVariables() {
		return firemakingVariables;
	}

	/**
	 * Gets the players Fletching variables.
	 * 
	 * @return A class holding variables and method we didn't want in here.
	 */
	public FletchingVariables getFletchingVariables() {
		return fletchingVariables;
	}

	/**
	 * Resets all the skilling booleans. We do this, because some skills uses
	 * the same interfaces, and we want to make sure its the correct thing we're
	 * handling.
	 */
	public void resetSkilling() {
		cookingVariables.setCookingItem(-1);
		craftingVariables.setCrafting(false);
		firemakingVariables.setFiremaking(false);
		fletchingVariables.setFletching(false);
	}

	private int[] nextDialogueIds = null;

	public void setNextDialogueIds(int[] nextDialogueIds) {
		this.nextDialogueIds = nextDialogueIds;
	}

	public int[] getNextDialogueIds() {
		return nextDialogueIds;
	}

	/**
	 * Temporary walking animations for Agility, Magic Carpet and others.
	 */
	private int[] temporaryUpdatingAnimations = STANDARD_UPDATING_ANIMATIONS;

	/**
	 * The standard updating animations.
	 */
	public static int[] STANDARD_UPDATING_ANIMATIONS = { 0x328, // Stand,
			0x337, // Stand - turn.
			0x333, // Walk
			0x334, // turn 180
			0x335, // turn 90 cw
			0x336, // turn 90 ccw
			0x338, // run
	};

	/**
	 * Defines if we should set the updating animations on a different way.
	 */
	private boolean differentUpdateAnimation = false;

	/**
	 * Gets a temporary player updating animation by an index.
	 * 
	 * @param index
	 *            The array index of the animation.
	 * @return A temporary updating animation.
	 */
	public int getTemporaryUpdatingAnimation(int index) {
		return temporaryUpdatingAnimations[index];
	}

	/**
	 * Sets a temporary walking animation by an index and an animation.
	 * 
	 * @param index
	 *            The array index we want to set.
	 * @param animation
	 *            The new animation performed.
	 */
	public void setTemporaryUpdatingAnimation(int index, int animation) {
		this.temporaryUpdatingAnimations[index] = animation;
	}

	/**
	 * Sets all the temporary walking animation by a new int[].
	 * 
	 * @param animations
	 *            The new animation set.
	 */
	public void setTemporaryUpdatingAnimation(int[] animations) {
		this.temporaryUpdatingAnimations = animations;
	}

	/**
	 * Defines if we should update the players animations a different way.
	 * 
	 * @return true if, false if not.
	 */
	public boolean differentUpdateAnimations() {
		return differentUpdateAnimation;
	}

	/**
	 * Sets the players animations to update a different way.
	 */
	public void setDifferentUpdateAnimation(boolean differentUpdateAnimation) {
		this.differentUpdateAnimation = differentUpdateAnimation;
		if (!differentUpdateAnimation) {
			temporaryUpdatingAnimations = STANDARD_UPDATING_ANIMATIONS;
		}
	}

	/**
	 * Gets the players prayer class.
	 * 
	 * @return The players prayer class.
	 */
	public Prayer getPrayer() {
		return prayer;
	}

	@Override
	public int getAttackAnimation() {
		return Equipment.getAttackAnimation(this);
	}

	@Override
	public int getAttackSpeed() {
		return Combat.getAttackSpeed(equipment.get(Equipment.SLOT_WEAPON));
	}

	@Override
	public int getDeathAnimation() {
		return 2304;
	}

	@Override
	public int getDefenceAnimation() {
		return Equipment.getDefenceAnimation(this);
	}

	@Override
	public int getMaxHit() {
		AttackType type = this.getAttackType();
		int fightType = this.getAttackStyle();
		double maxHit = 0;
		if (type.equals(AttackType.MELEE)) {
			int bonus = bonuses.getBonus(Bonuses.STRENGTH);
			double boost = 1.0;
			if (prayer.isPrayerToggled(Prayer.BURST_OF_STRENGTH)) {
				boost *= 1.05;
			}
			if (prayer.isPrayerToggled(Prayer.SUPERHUMAN_STRENGTH)) {
				boost *= 1.10;
			}
			if (prayer.isPrayerToggled(Prayer.ULTIMATE_STRENGTH)) {
				boost *= 1.15;
			}
			/*
			 * + Burst of Strength Prayer: 1.05 + Superhuman Strength Prayer:
			 * 1.10 + Ultimate Strength Prayer: 1.15 + Chivalry Prayer: 1.18 +
			 * Piety Prayer: 1.23 + Void Knight, Full Melee: 1.20 + Black Mask
			 * (Slayer Missions): 1.15 + Salve Amulet (Versus Undead): 1.15 +
			 * Salve Amulet(e) (Versus Undead): 1.20
			 */
			int strength = skills.getLevel(Skills.STRENGTH);
			if (Equipment.wearingDharock(this)) {
				double nb = bonus /= 100; // This is now 1.05 with nothing but
											// DH.
				boost *= 0.60 + (nb - 1.05);
				return (int) (((skills.getLevelForExperience(Skills.HITPOINTS) + strength) / 2 - getHitpoints() / 2) * boost);
			}

			double effectiveStrength = Math.round(strength * boost);

			if (fightType == 1) {// Agressive
				effectiveStrength += 3;
			}
			if (fightType == 2) { // Controlled.
				effectiveStrength += 1;
			}

			maxHit = 1.3 + effectiveStrength / 10 + bonus / 80
					+ effectiveStrength * bonus / 640;
		} else if (type.equals(AttackType.RANGED)) {
			int range = skills.getLevel(Skills.RANGE);

			double b = 1.0;

			if (prayer.isPrayerToggled(Prayer.SHARP_EYE)) {
				b *= 1.05;
			}
			if (prayer.isPrayerToggled(Prayer.HAWK_EYE)) {
				b *= 1.10;
			}
			if (prayer.isPrayerToggled(Prayer.EAGLE_EYE)) {
				b *= 1.15;
			}

			// TODO: Void knight/full ranger: *= 1.20

			double effectiveStrength = Math.round(range * b);

			if (fightType == 1) { // Accurate..
				effectiveStrength += 3;
			}
			// fightType 4 = rapid
			// fightType 2 = longrange.
			int rangedStrength = Equipment.getRangedStrength(this);
			System.out.println("Ranged Strength: " + rangedStrength);

			maxHit = 1.3 + effectiveStrength / 10 + rangedStrength / 80
					+ effectiveStrength * rangedStrength / 640;
		}
		System.out.println("Max: " + maxHit);
		return (int) Math.ceil(maxHit);
	}

	@Override
	public AttackType getAttackType() {
		if (Equipment.isWieldindBow(this)
				|| Equipment.isWieldingThrowables(this)) {
			return AttackType.RANGED;
		}
		if (magic.getAutoCastingSpellId() != -1) {
			return AttackType.MAGIC;
		}
		return AttackType.MELEE;
	}

	@Override
	public int getDrawBackGraphics() {
		return Equipment.getDrawbackGraphics(this);
	}

	@Override
	public int getProjectileId() {
		return Equipment.getProjectileId(this);
	}

	@Override
	public boolean hasAmmo() {
		/*
		 * First, we check if we're wearing throwable ranged items.
		 */
		if (Equipment.isWieldingThrowables(this)) {
			return true;
		}
		Item weapon = equipment.get(Equipment.SLOT_WEAPON);
		Item arrows = equipment.get(Equipment.SLOT_ARROWS);
		if (weapon == null) {
			return false;
		}
		boolean crossbow = weapon.getDefinition().getName().toLowerCase()
				.endsWith("crossbow")
				|| weapon.getDefinition().getName().endsWith("c'bow");
		if (arrows == null) {
			actionSender
					.sendMessage("You have no "
							+ (crossbow ? "bolts" : "arrows")
							+ " left in your quiver.");
			return false;
		}
		/*
		 * Then we check for regular bows..
		 */
		if (!crossbow) {
			/*
			 * Crystal bow..
			 */
			if (weapon.getId() >= 4212 && weapon.getId() <= 4223) {
				return true;// Doesn't need any ammo.
			}
			/*
			 * Check if we're wearing regular arrows, And check if the bow is
			 * compatible with them..
			 */
			for (int index = 0; index < Equipment.ARROWS_SORTED_BY_LEVEL.length; index++) {
				for (int arrow : Equipment.ARROWS_SORTED_BY_LEVEL[index]) {
					if (arrow == arrows.getId()) {
						/*
						 * Normal longbow + shortbow.
						 */
						if (weapon.getId() == 839 || weapon.getId() == 841) {
							if (index <= 1) { // Bronze and iron arrows.
								return true;
							}
							/*
							 * Oak longbow + shortbow..
							 */
						} else if (weapon.getId() == 843
								|| weapon.getId() == 845) {
							if (index <= 2) { // Up to steel..
								return true;
							}
							/*
							 * Willow longbow + shortbow..
							 */
						} else if (weapon.getId() == 847
								|| weapon.getId() == 849) {
							if (index <= 3) {
								return true;
							}
							/*
							 * Maple longbow + shortbow.
							 */
						} else if (weapon.getId() == 851
								|| weapon.getId() == 853) {
							if (index <= 4) {
								return true;
							}
							/*
							 * Yew/Magic longbows + shortbows..
							 */
						} else if (weapon.getId() == 855
								|| weapon.getId() == 857
								|| weapon.getId() == 859
								|| weapon.getId() == 861) {
							if (index <= 5) {
								return true;
							}
							/*
							 * Dark bow..
							 */
						} else if (weapon.getId() == 11235) {
							if (index <= 6) {
								return true;
							}
						}
					}
				}
			}
			/*
			 * If not, we check for crossbows.
			 */
		} else {
			/*
			 * Karils crossbow.
			 */
			if (weapon.getId() == 4734 && arrows.getId() == 4740) {
				return true;
			}
			/*
			 * Check if we're wearing bolts..
			 */
			for (int index = 0; index < Equipment.BOLTS_SORTED_BY_LEVEL.length; index++) {
				for (int bolt : Equipment.BOLTS_SORTED_BY_LEVEL[index]) {
					System.out.println("The fuck? " + bolt + "vs "
							+ arrows.getId());
					if (bolt == arrows.getId()) {
						System.out.println("Win: " + index);
						/*
						 * Regular crossbow, phoenix crossbow and bronze
						 * crossbows.
						 */
						if (weapon.getId() == 767 || weapon.getId() == 837
								|| weapon.getId() == 11165
								|| weapon.getId() == 11167
								|| weapon.getId() == 9174) {
							if (index == 0) {
								return true;
							}
							/*
							 * Blurite crossbow.
							 */
						} else if (weapon.getId() == 9176) {
							if (index <= 1) {
								return true;
							}
							/*
							 * Iron crossbow and Dorgeshuun crossbow..
							 */
						} else if (weapon.getId() == 9177
								|| weapon.getId() == 8880) {
							if (index <= 2) {
								return true;
							}
							/*
							 * Steel crossbow.
							 */
						} else if (weapon.getId() == 9179) {
							if (index <= 3) {
								return true;
							}
							/*
							 * Mith crossbow.
							 */
						} else if (weapon.getId() == 9181) {
							if (index <= 4) {
								return true;
							}
							/*
							 * Adamant crossbow.
							 */
						} else if (weapon.getId() == 9183) {
							if (index <= 5) {
								return true;
							}
							/*
							 * Rune crossbow.
							 */
						} else if (weapon.getId() == 9185) {
							if (index <= 6) {
								return true;
							}
						}
					}
				}
			}
		}
		/*
		 * If we reach this stage, it means that we're wearing arrows, but we
		 * don't have a "bow" that fits it..
		 */
		actionSender
				.sendMessage("You can't use this type of ammo with that type of bow!"); // FIXME
		actionSender.sendStopFollowing(false);
		return false;
	}

	@Override
	public int getHitpoints() {
		return skills.getLevel(Skills.HITPOINTS);
	}

	public boolean canLogout() {
		return System.currentTimeMillis() - getLastAttack() > 10000;
	}

	private int runEnergy = 100;

	public int followThisPlayer = -1;

	public int getRunEnergy() {
		return runEnergy;
	}

	public void setRunEnergy(int runEnergy) {
		this.runEnergy = runEnergy;
		actionSender.sendRunEnergy();
	}

	private int followingEntityIndex = -1;

	/**
	 * Do this with an Entity object instead, or use the "interacting with".
	 */
	public void setFollowingEntityIndex(int id) {
		followingEntityIndex = id;
	}

	public int getFollowingPlayerIndex() {
		return followingEntityIndex;
	}

	public boolean isFollowing() {
		return followingEntityIndex != -1;
	}

	/**
	 * Used to reset all our combat variables when we equip a new item.
	 */
	public void refreshCombatVariables(int slot) {
		bonuses.refresh();
		actionSender.sendFollowingDistance(getAttackingDistance());
		setInCombat(false);
	}

	/**
	 * Sets the last drinking delay, as well as the combat delay.
	 * 
	 * @param lastEat
	 *            The last time we drank.
	 */
	public void setLastDrink(long lastDrink) {
		if (lastDrink - lastEat > 1500) {
			increaseCombatDelay(1200);
		}
		this.lastDrink = lastDrink;
	}

	/**
	 * Gets the last time we drank.
	 * 
	 * @return The last time we drank.
	 */
	public long getLastDrink() {
		return lastDrink;
	}

	/**
	 * Sets the last eating delay, as well as the combat delay.
	 * 
	 * @param lastEat
	 *            The last time we ate.
	 */
	public void setLastEat(long lastEat) {
		if (lastEat - lastDrink > 1500) {
			increaseCombatDelay(1200);
		}
		this.lastEat = lastEat;
	}

	/**
	 * Gets the last time we ate.
	 * 
	 * @return The last time we ate.
	 */
	public long getLastEat() {
		return lastEat;
	}

	@Override
	public boolean getSpecialAttack(Entity victim) {
		if (specials.isActive() && specials.canPerform()) {
			specials.perform(victim);
			return true;
		}
		return false;
	}

	public Barrows getBarrows() {
		return barrows;
	}

	/**
	 * A list of NPCs only this player can attack.
	 */
	private List<NPC> privateNPCs = new ArrayList<NPC>();

	/**
	 * Adds an NPC to the players list of private NPCs.
	 * 
	 * @param npc
	 *            The NPC to add.
	 */
	public void addPrivateNPC(NPC npc) {
		privateNPCs.add(npc);
	}

	/**
	 * Adds an NPC from the players list of private NPCs.
	 * 
	 * @param npc
	 *            The NPC to remove.
	 */
	public void removePrivateNPC(NPC npc) {
		privateNPCs.remove(npc);
	}

	/**
	 * Checks if the players private NPC list contains a following NPC.
	 * 
	 * @param npc
	 *            The NPC we want to check if the list contains.
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public boolean containsPrivateNPC(NPC npc) {
		return privateNPCs.contains(npc);
	}

	/**
	 * The "world" the player is currently in. For Private Messaging.
	 */
	public int getWorld() {
		return 1;
	}

	/**
	 * Gets the players friends/ignores.
	 * 
	 * @return The players friends/ignores.
	 */
	public Friends getFriends() {
		return friends;
	}

	public Jewellery getJewellery() {
		return jewellery;
	}

	/**
	 * The items on the "Are you sure you want to destroy this item" interface.
	 */
	private int[] destroyingItem = new int[2];

	/**
	 * Sets the items to destroy.
	 * 
	 * @param id
	 *            The item id.
	 * @param slot
	 *            The item slot.
	 */
	public void setDestroyingItem(int id, int slot) {
		destroyingItem[0] = id;
		destroyingItem[1] = slot;
	}

	/**
	 * Gets the item we're currently trying to destroy.
	 * 
	 * @return The item we want to destroy.
	 */
	public int[] getDestroyingItem() {
		return destroyingItem;
	}

	private int pestControlDamage = 0;
	private byte pestControlPoints = 0;

	public void increasePestControlDamage(int damage) {
		pestControlDamage += damage;
	}

	public void resetPestControlDamage() {
		this.pestControlDamage = 0;
	}

	public int getPestControlDamage() {
		return pestControlDamage;
	}

	/**
	 * Increases the players Pest Control points by one..
	 */
	public void increasePestControlPoints() {
		if (pestControlPoints + 1 < 250) {
			this.pestControlPoints += 1;
		}
	}

	public void decreasePestControlPoints(int points) {
		this.pestControlPoints -= points;
	}

	public byte getPestControlPoints() {
		return pestControlPoints;
	}

	@Override
	public int getSize() {
		return 1;
	}

	public WarriorsGuild getWarriorsGuild() {
		return warriorsGuild;
	}

	/**
	 * Gets an array of all the items this player will drop. This will remove
	 * items from the inventory / equipment as well.
	 * 
	 * @return An array of items to put on the floor.
	 */
	public Item[] getDropItems() {
		Item[] mostValueable = new Item[prayer
				.isPrayerToggled(Prayer.PROTECT_ITEMS) ? 4 : 3];
		Item[] inventory = this.inventory.toArray();
		for (int index = 0; index < mostValueable.length; index++) {
			Item best = null;
			for (int i = 0; i < inventory.length; i++) {
				Item item = inventory[i];
				if (item != null) {
					if (best == null
							|| best.getDefinition().getGeneralStorePrice() < item
									.getDefinition().getGeneralStorePrice()) {
						best = item;
						inventory[i] = null;
					}
				}
			}
			for (int i = 0; i < inventory.length; i++) {
				Item item = inventory[i];
				if (item != null) {
					if (best == null
							|| best.getDefinition().getGeneralStorePrice() < item
									.getDefinition().getGeneralStorePrice()) {
						best = item;
						inventory[i] = null;
					}
				}
			}
			mostValueable[index] = best;
		}
		return inventory;
	}

	public void dropLoot() {
		List<Item> items = new ArrayList<Item>();
		for (Item item : inventory.toArray()) {
			if (item != null) {
				items.add(item);
			}
		}
		for (Item item : equipment.toArray()) {
			if (item != null) {
				items.add(item);
			}
		}
		inventory.clear();
		equipment.clear();
		int keep = skulls.isSkulled() ? 0 : 3;
		if (prayer.isPrayerToggled(Prayer.PROTECT_ITEMS)) {
			keep++;
		}
		if (keep > 0) {
			Collections.sort(items, new Comparator<Item>() {
				@Override
				public int compare(Item arg0, Item arg1) {
					int a0 = arg0.getDefinition().getGeneralStorePrice();
					int a1 = arg1.getDefinition().getGeneralStorePrice();
					return a1 - a0;
				}
			});
			List<Item> toKeep = new ArrayList<Item>();
			for (Item item : items) {
				if (keep > 0) {
					if (item.getCount() == 1) {
						toKeep.add(item);
						keep--;
					} else {
						int smallest = keep < item.getCount() ? keep : item
								.getCount();
						toKeep.add(new Item(item.getId(), smallest));
						keep -= smallest;

					}
				} else {
					break;
				}
			}
			for (Item k : toKeep) {
				inventory.add(k);
			}
			for (int i = 0; i < items.size(); i++) {
				Item item = items.get(i);
				for (Item k : toKeep) {
					if (k.getId() == item.getId()) {
						items.set(i, item.setCount(item.getCount() - 1));
						break;
					}
				}
			}
		}
		Entity killer = this.getKiller();
		killer = killer instanceof Player ? killer : null;
		items.add(new Item(526)); // Bones..
		for (Item i : items) {
			System.out.println(i);
			if (i != null) {
				if (Constants.playerBoundItem(i.getId())) { // Makes sure items
															// such as Torso,
															// Fire cape is only
															// visible to the
															// player which the
															// item belongs to.
					GroundItemController.createGroundItem(i, this,
							this.getLocation());
				} else {
					GroundItemController.createGroundItem(i, killer,
							this.getLocation());
				}
			}
		}
	}

	public Construction getConstruction() {
		return construction;
	}

	private boolean visibleSidebarInterfaces = true;

	public void setVisibleSidebarInterfaces(boolean visibleSidebarInterfaces) {
		this.visibleSidebarInterfaces = visibleSidebarInterfaces;
	}

	public boolean hasVisibleSidebarInterfaces() {
		return visibleSidebarInterfaces;
	}

	public FightCaves getFightCaves() {
		return fightcaves;
	}

	public void setHasRecievedHolidayItems(boolean hasRecievedHolidayItems) {
		this.hasRecievedHolidayItems = hasRecievedHolidayItems;
	}

	public boolean isHasRecievedHolidayItems() {
		return hasRecievedHolidayItems;
	}

}
