package org.hyperion.rs2.content.skills.magic;

import org.hyperion.rs2.content.skills.magic.impl.Spell;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.ContainerListener;
import org.hyperion.rs2.model.container.Duel;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.container.impl.WeaponContainerListener;

public class Magic {

	private Entity entity;

	private MagicType spellBook = MagicType.MODERN;

	private boolean vengeanceCasted = false;
	private boolean chargeCasted = false;
	private boolean autoCasting = false;

	private int autoCastingSpellId = -1;

	private long lastVengeanceOther = 0;
	private long lastVengeance = 0;
	private long lastTeleport = -3000;

	public Magic(Entity entity) {
		this.entity = entity;
	}

	public long getLastTeleport() {
		return System.currentTimeMillis() - lastTeleport;
	}

	public void setLastTeleport(long time) {
		lastTeleport = time;
	}

	public long getLastVengeance() {
		return lastVengeance;
	}

	private static final Item vengRune1 = new Item(9075, 4);
	private static final Item vengRune2 = new Item(560, 2);
	private static final Item vengRune3 = new Item(557, 10);
	private static final Graphic vengeanceGfx = Graphic
			.create(726, (100 << 16));
	private static final Animation vengeanceAnim = Animation.create(4410);

	public void vengence() {
		if (entity instanceof Player) {
			if (((Player) entity).getSkills().getLevel(6) < 94) {
				((Player) entity).getActionSender().sendMessage(
						"You need Magic level of 94 to cast this spell.");
				return;
			}
		}
		if (System.currentTimeMillis() - getLastVengeance() < 30000) {
			if (entity instanceof Player) {
				((Player) entity).getActionSender().sendMessage(
						"You can only cast vengeance spells every 30 seconds.");
			}
			return;
		}
		if (entity instanceof Player) {
			if (!Inventory.containsRune((Player) entity, vengRune1)
					|| !Inventory.containsRune((Player) entity, vengRune2)
					|| !Inventory.containsRune((Player) entity, vengRune3)) {
				((Player) entity)
						.getActionSender()
						.sendMessage(
								"You don't have the required runes to cast this spell.");
				return;
			}
		}
		entity.playAnimation(vengeanceAnim);
		entity.playGraphics(vengeanceGfx);
		if (entity instanceof Player) {
			Player player = (Player) entity;
			Inventory.removeRune(player, -1, vengRune1);
			Inventory.removeRune(player, -1, vengRune2);
			Inventory.removeRune(player, -1, vengRune3);
		}
		vengeanceEffect();
	}

	public void vengeanceEffect() {
		lastVengeance = System.currentTimeMillis();
		vengeanceCasted = true;
		World.getWorld().submit(new Event(60000) {
			public void execute() {
				// Vengeance fades away after 60 seconds/on logout, this
				// event does the timer
				if (vengeanceCasted)
					vengeanceCasted = false;
				this.stop();
			}
		});
	}

	private static final Item chargeRune1 = new Item(554, 3);
	private static final Item chargeRune2 = new Item(565, 3);
	private static final Item chargeRune3 = new Item(556, 3);
	private static final Animation chargeAnim = Animation.create(811);
	private static final Graphic chargeGfx = Graphic.create(301, (100 << 16));

	public void charge() {
		if (entity instanceof Player) {
			Player pEntity = (Player) entity;
			if (chargeCasted) {
				pEntity.getActionSender().sendMessage(
						"You already have charge casted!");
				return;
			}
			if (!Inventory.containsRune(pEntity, chargeRune1)
					|| !Inventory.containsRune(pEntity, chargeRune2)
					|| !Inventory.containsRune(pEntity, chargeRune3)) {
				pEntity.getActionSender()
						.sendMessage(
								"You don't have the required runes to cast this spell.");
				return;
			}
			Inventory.removeRune(pEntity, -1, chargeRune1);
			Inventory.removeRune(pEntity, -1, chargeRune2);
			Inventory.removeRune(pEntity, -1, chargeRune3);
		}

		entity.playAnimation(chargeAnim);
		entity.playGraphics(chargeGfx);
		chargeCasted = true;
		World.getWorld().submit(new Event(420000) {
			public void execute() {
				if (chargeCasted) {
					chargeCasted = false;
					if (entity instanceof Player) {
						((Player) entity).getActionSender().sendMessage(
								"You're charge spell faded away.");
					}
				}
				this.stop();
			}
		});

	}

	public boolean hasChargeCasted() {
		return chargeCasted;
	}

	public boolean hasVengeanceCasted() {
		return vengeanceCasted;
	}

	public void setVengeanceCasted(boolean b) {
		this.vengeanceCasted = b;
	}

	public long getLastVengeanceOther() {
		return lastVengeanceOther;
	}

	public void setLastVengeanceOther(long time) {
		this.lastVengeanceOther = time;
	}

	public void setSpellBook(MagicType spellBook) {
		this.spellBook = spellBook;
		if (entity instanceof Player) {
			((Player) this.entity).getActionSender().sendSidebarInterface(6,
					spellBook.toInteger());
		}
	}

	public void setSpellBook(int interfaceId) {
		switch (interfaceId) {
		case 192:
			spellBook = MagicType.MODERN;
			break;
		case 193:
			spellBook = MagicType.ANCIENT;
			break;
		case 430:
			spellBook = MagicType.LUNAR;
			break;
		}
	}

	/**
	 * Gets the current autocasting spell id set.
	 * 
	 * @return The current autocasting spell id.
	 */
	public int getAutoCastingSpellId() {
		return autoCastingSpellId;
	}

	/**
	 * Sets the spell id to autocast for this entity.
	 */
	public void setAutoCastingSpellId(int set) {
		this.autoCastingSpellId = set;
	}

	/**
	 * Defining whenever or not we're autocasting.
	 * 
	 * @return True if we're autocasting, false if not.
	 */
	public boolean isAutoCasting() {
		return autoCasting;
	}

	/**
	 * Sets the autocasting boolean for this entity.
	 */
	public void setAutoCasting(boolean set) {
		this.autoCasting = set;
	}

	/**
	 * Resets the auto casting stuff.
	 */
	public void resetAutoCasting() {
		autoCasting = false;
		autoCastingSpellId = -1;
		if (entity instanceof Player) {
			Player pEntity = (Player) entity;
			pEntity.getActionSender().sendString("Spell", 90, 105);
			for (ContainerListener l : pEntity.getEquipment().getListeners()) {
				if (l instanceof WeaponContainerListener) {
					((WeaponContainerListener) l).sendWeapon();
				}
			}
		}
	}

	public MagicType getSpellBook() {
		return spellBook;
	}

	public static enum MagicType {
		MODERN(192), ANCIENT(193), LUNAR(430);

		int interfaceId;

		private MagicType(int interfaceId) {
			this.interfaceId = interfaceId;
		}

		public int toInteger() {
			return interfaceId;
		}

	}

	private static final int[] MODERN_AUTO_CASTING_SPELLS = { 1, 4, 6, 7, 10,
			14, 17, 20, 24, 27, 33, 38, 45, 48, 52, 55, };

	private static final String[] MODERN_AUTO_CASTING_SPELL_NAMES = {
			"Wind strike", "Water strike", "Earth strike", "Fire strike",
			"Wind bolt", "Water bolt", "Earth bolt", "Fire bolt", "Wind blast",
			"Water blast", "Earth blast", "Fire blast", "Wind wave",
			"Water wave", "Earth wave", "Fire wave", };

	public void setModernAutoCasting(int buttonId) {
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (player.getRequestManager().isDueling()) {
				if (player.getRequestManager().getDuel()
						.isRuleToggled(Duel.NO_MAGIC)) {
					player.getActionSender().sendMessage(
							"Magic have been disabled during this duel!");
					return;
				}
			}
			// player.setAttacking(false);
			if (buttonId <= 15) {
				Spell spell = SpellMananger.MODERN_SPELLS[MODERN_AUTO_CASTING_SPELLS[buttonId]];
				if (player.getSkills().getLevel(Skills.MAGIC) < spell
						.getLevelReq()) {
					player.getActionSender().sendMessage(
							"You need to have a Magic of at least "
									+ spell.getLevelReq()
									+ " in order to cast this spell.");
					resetAutoCasting();
					return;
				}
				for (Item rune : spell.getRuneReqs()) { // We loop through all
														// runes.
					if (rune != null) { // Make sure the rune is not a null.
						if (!Inventory.containsRune(player, rune)) {// Check if
																	// the
																	// players
																	// inventory
																	// contains
																	// it.
							player.getActionSender()
									.sendMessage(
											"You don't have the required runes to cast this spell.");
							resetAutoCasting();
							return;
						}
					}
				}
				player.getMagic().setAutoCastingSpellId(
						MODERN_AUTO_CASTING_SPELLS[buttonId]);
				player.getActionSender().sendString(
						MODERN_AUTO_CASTING_SPELL_NAMES[buttonId], 90, 105);
				setAutoCasting(true);
				setAutoCastingSpellId(MODERN_AUTO_CASTING_SPELLS[buttonId]);
			}
			for (ContainerListener l : player.getEquipment().getListeners()) {
				if (l instanceof WeaponContainerListener) {
					((WeaponContainerListener) l).sendWeapon();
				}
			}

		}

	}

	private static final int[] ANCIENT_AUTO_CASTING_BUTTON_IDS = { 93, 145, 51,
			7, 119, 171, 71, 29, 106, 158, 62, 18, 132, 184, 82, 40 };

	private static final int[] ANCIENT_AUTO_CASTING_SPELLS = { 8, 12, 4, 0, 9,
			13, 5, 1, 10, 14, 6, 2, 11, 15, 7, 3, };

	private static final String[] ANCIENT_AUTO_CASTING_SPELL_NAMES = {
			"Smoke rush", "Shadow rush", "Blood rush", "Ice rush",
			"Smoke burst", "Shadow burst", "Blood burst", "Ice burst",
			"Smoke blitz", "Shadow blitz", "Blood blitz", "Ice blitz",
			"Smoke barrage", "Shadow barrage", "Blood barrage", "Ice barrage", };

	public void setAncientAutoCasting(int buttonId) {
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (player.getRequestManager().isDueling()) {
				if (player.getRequestManager().getDuel()
						.isRuleToggled(Duel.NO_MAGIC)) {
					player.getActionSender().sendMessage(
							"Magic have been disabled during this duel!");
					return;
				}
			}
			// player.setAttacking(false);
			if (buttonId != 6) {
				int index = -1;
				for (int i = 0; i < ANCIENT_AUTO_CASTING_BUTTON_IDS.length; i++) {
					if (ANCIENT_AUTO_CASTING_BUTTON_IDS[i] == buttonId) {
						index = i;
						break;
					}
				}
				if (index == -1) {
					resetAutoCasting();
					return;
				}
				Spell spell = SpellMananger.ANCIENT_SPELLS[ANCIENT_AUTO_CASTING_SPELLS[index]];
				if (player.getSkills().getLevel(Skills.MAGIC) < spell
						.getLevelReq()) {
					player.getActionSender().sendMessage(
							"You need to have a Magic of at least "
									+ spell.getLevelReq()
									+ " in order to cast this spell.");
					resetAutoCasting();
					return;
				}
				for (Item rune : spell.getRuneReqs()) { // We loop through all
														// runes.
					if (rune != null) { // Make sure the rune is not a null.
						if (!Inventory.containsRune(player, rune)) {// Check if
																	// the
																	// players
																	// inventory
																	// contains
																	// it.
							player.getActionSender()
									.sendMessage(
											"You don't have the required runes to cast this spell.");
							resetAutoCasting();
							return;
						}
					}
				}
				player.getMagic().setAutoCastingSpellId(
						ANCIENT_AUTO_CASTING_SPELLS[index]);
				player.getActionSender().sendString(
						ANCIENT_AUTO_CASTING_SPELL_NAMES[index], 90, 105);
				setAutoCasting(true);
				setAutoCastingSpellId(ANCIENT_AUTO_CASTING_SPELLS[index]);
			}
			for (ContainerListener l : player.getEquipment().getListeners()) {
				if (l instanceof WeaponContainerListener) {
					((WeaponContainerListener) l).sendWeapon();
				}
			}

		}

	}
}
