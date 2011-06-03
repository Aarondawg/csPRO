package org.hyperion.rs2.content.skills.magic;

import java.util.Random;

import org.hyperion.rs2.content.skills.Prayer;
import org.hyperion.rs2.content.skills.magic.Magic.MagicType;
import org.hyperion.rs2.content.skills.magic.impl.Spell;
import org.hyperion.rs2.content.skills.magic.impl.ancient.BloodSpell;
import org.hyperion.rs2.content.skills.magic.impl.ancient.IceSpell;
import org.hyperion.rs2.content.skills.magic.impl.lunar.VengenceOther;
import org.hyperion.rs2.content.skills.magic.impl.modern.ElementalSpell;
import org.hyperion.rs2.content.skills.magic.impl.modern.FreezingSpell;
import org.hyperion.rs2.content.skills.magic.impl.modern.GodSpell;
import org.hyperion.rs2.content.skills.magic.impl.modern.ReducingSpell;
import org.hyperion.rs2.content.skills.magic.impl.modern.TeleBlock;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Bonuses;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.Damage.HitType;
import org.hyperion.rs2.model.container.Duel;
import org.hyperion.rs2.model.container.Inventory;

/**
 * All static spell methods and stuff should be handled in here. Non-static
 * stuff in Magic.java.
 * 
 * @author Brown.
 */

public class SpellMananger {

	private static final Graphic splash = Graphic.create(85, (100 << 16));

	private static final Random r = new Random();

	public static final Spell[] MODERN_SPELLS = new Spell[63];
	public static final Spell[] ANCIENT_SPELLS = new Spell[16];
	private static final Spell[] LUNAR_SPELLS = new Spell[100];

	/**
	 * An array holding all the modern non-combat spell ids. To check if we can
	 * cast a spell outside of the wilderness. //TODO: Find out if this list
	 * contains more of them.
	 */
	private static int[] MODERN_NON_COMBAT_SPELLS = { 54, // Teleother Lumbridge
			59, // Teleother Falador
			62, // Teleother Camelot.
	};

	/**
	 * An array holding all the lunar non-combat spell ids. To check if we can
	 * cast a spell outside of the wilderness. //TODO: Should veng other and
	 * stuff be in here..?
	 */
	private static int[] LUNAR_NON_COMBAT_SPELLS = { 19, // Vengeance other.
	};

	/**
	 * Makes a specific entity cast a specific spell on a specific target.
	 * 
	 * @param caster
	 *            The spell caster.
	 * @param target
	 *            The casters target.
	 * @param spellId
	 *            The spell id
	 */
	public static void castSpell(Entity caster, Entity target, int spellId) {
		caster.getWalkingQueue().reset();
		boolean wildernessCheck = true;
		if (caster instanceof Player && target instanceof Player) {
			Player pCaster = (Player) caster;
			if (pCaster.getRequestManager().isDueling()) {
				if (pCaster.getRequestManager().getDuel()
						.isRuleToggled(Duel.NO_MAGIC)) {
					pCaster.getActionSender().sendMessage(
							"Magic have been disabled during this duel!");
					caster.getMagic().resetAutoCasting();
					return;
				}
			}
			for (int id : MODERN_NON_COMBAT_SPELLS) {
				if (id == spellId) {
					wildernessCheck = false;
				}
			}
			for (int id : LUNAR_NON_COMBAT_SPELLS) {
				if (id == spellId) {
					wildernessCheck = false;
				}
			}
			if (wildernessCheck) {
				if (!Combat.canAttack(caster, target)) {
					caster.getMagic().setAutoCasting(false);
					return;
				}
				caster.setInCombat(true);
				caster.setInteractingEntity(target);
				caster.setAggressor(true);
				if (caster instanceof Player && target instanceof Player) {
					((Player) target).getSkulls()
							.checkForSkull((Player) caster);
				}
			}
		}
		MagicType book = caster.getMagic().getSpellBook();
		Spell spell = null;
		try {
			spell = book.equals(MagicType.MODERN) ? MODERN_SPELLS[spellId]
					: book.equals(MagicType.ANCIENT) ? ANCIENT_SPELLS[spellId]
							: LUNAR_SPELLS[spellId];
		} catch (Exception e) {
			System.out.println("Magic type: " + book);
			e.printStackTrace();
		}
		if (spell != null) {
			/*
			 * Okay well. The following part is going to look very messy..! Its
			 * all about rune and level checking, and its for players only,
			 * hence the "caster instanceof Player."
			 */
			if (caster instanceof Player) {
				Player pCaster = (Player) caster;
				if (pCaster.getSkills().getLevel(Skills.MAGIC) < spell
						.getLevelReq()) {
					pCaster.getActionSender().sendMessage(
							"You need to have a Magic of at least "
									+ spell.getLevelReq()
									+ " in order to cast this spell.");
					caster.getMagic().resetAutoCasting();
					return;
				}
				for (Item rune : spell.getRuneReqs()) { // We loop through all
														// runes.
					if (rune != null) { // Make sure the rune is not a null.
						if (!Inventory.containsRune(pCaster, rune)) {// Check if
																		// the
																		// players
																		// inventory
																		// contains
																		// it.
							pCaster.getActionSender()
									.sendMessage(
											"You don't have the required runes to cast this spell.");
							caster.getMagic().resetAutoCasting();
							return;
						}
					}
				}
				/*
				 * At this point, we're sure that the players inventory contains
				 * all the needed runes.
				 */
			}
			if (wildernessCheck) { // Means its a combat spell.
				target.setUnderAttack(caster); // Even if we splash.
			}
			spell.cast(caster, target);
			caster.increaseCombatDelay(2500);
		} else {
			caster.resetInteractingEntity();
			caster.getMagic().resetAutoCasting();
			if (caster instanceof Player) {
				((Player) caster).getActionSender().sendMessage(
						"Sorry, this spell is unavailable atm.");
			}
		}
	}

	public static int getRandom(int range) {
		return (int) (java.lang.Math.random() * (range + 1));
	}

	private static int calculateHit(Entity caster, Entity target, int maxHit) {
		if (getRandom(10 + calculateMagicDefence(target)) > getRandom(10 + calculateMagicAttack(caster))) {
			maxHit = 0;
		}
		/*
		 * int mageAtk = getRandom(mageAtk(caster)); int mageDef =
		 * getRandom(mageDef(target)); if(mageAtk <= mageDef) maxHit = 0;
		 */
		int hit = getRandom(maxHit);
		if (hit == 0) {
			target.playGraphics(splash);
			return hit;
		} else {
			if (hit >= target.getHitpoints()) {
				hit = target.getHitpoints();
			}
			if (caster instanceof Player) {
				((Player) caster).getSkills().addExperience(Skills.MAGIC,
						(2 * hit));
				((Player) caster).getSkills().addExperience(Skills.HITPOINTS,
						(2 * hit));
			}
			if (target instanceof Player) {
				Player p = (Player) target;
				if (caster instanceof Player) {
					Player attacker = (Player) caster;
					if (attacker.getPrayer().isPrayerToggled(Prayer.SMITE)) {
						p.getSkills().setLevel(
								Skills.PRAYER,
								p.getSkills().getLevel(Skills.PRAYER)
										- (hit / 4));
						if (p.getSkills().getLevel(Skills.PRAYER) < 0) {
							p.getSkills().setLevel(Skills.PRAYER, 0);
						}
					}
				} else { // We're an NPC.
					if (p.getPrayer().isPrayerToggled(Prayer.PROTECT_FROM_MAGE)) {
						hit = 0;
					}
				}
			}
			Combat.inflictMagicDamage(target, caster, hit);
			if (target.getMagic().hasVengeanceCasted() && hit > 0) {
				int vengHit = (hit - (hit / 4));
				Combat.inflictDamage(caster, null, vengHit);
				target.forceChat("Taste vengeance!");
				target.getMagic().setVengeanceCasted(false);
			}
			return hit;
		}
	}

	public static boolean calculateNormalHit(Entity caster, Entity target,
			int maxHit) {
		return calculateHit(caster, target, maxHit) > 0;
	}

	public static boolean calculateBloodSpellHit(Entity caster, Entity target,
			int maxHit) {
		final int hit = calculateHit(caster, target, maxHit);
		if (hit == 0)
			return false;
		double heal = (hit * 0.25);
		System.out.println("Heal amount: " + heal + "Hit: " + hit);
		if (heal != 0) {
			if (caster instanceof Player) {
				final Player pCaster = (Player) caster;
				if (pCaster.getSkills().getLevel(Skills.HITPOINTS) < pCaster
						.getSkills().getLevelForExperience(Skills.HITPOINTS)) {
					pCaster.getActionSender().sendMessage(
							"You drain some of your opponments health!");
					pCaster.heal((int) Math.round(heal));
				}
			}

		}
		return true;
	}

	public static void castButtonSpell(MagicType book, Player caster,
			int spellId) {
		if (caster.getRequestManager().isDueling()) {
			if (caster.getRequestManager().getDuel()
					.isRuleToggled(Duel.NO_MAGIC)) {
				caster.getActionSender().sendMessage(
						"Magic have been disabled during this duel!");
				return;
			}
		}
		switch (book) {
		case MODERN:
			switch (spellId) {
			case 3: // Enchant crossbow modernSpells
			case 9: // Bones to bananas
			case 23: // Teleport to House.
				caster.getActionSender().sendMessage(
						"You cannot use this spell yet.");
				break;

			case 58: // Charge
				caster.getMagic().charge();
				break;
			case 0: // HomeTeleport
				homeTele(caster, 3220 + r.nextInt(4), 3218 + r.nextInt(4), 0);
				break;
			case 15: // Teleport to Varrock
				teleport(caster, MagicType.MODERN, 25, 3209, 3422, 0, 35, 554,
						1, 556, 3, 563, 1, -1, -1, 8, 4, 3500);
				break;
			case 18: // Teleport to Lumbridge
				teleport(caster, MagicType.MODERN, 31, 3221, 3219, 0, 41, 557,
						1, 556, 3, 563, 1, -1, -1, 4, 2, 3500);
				break;
			case 21: // Teleport to Falador
				teleport(caster, MagicType.MODERN, 37, 2963, 3377, 0, 47, 555,
						1, 556, 3, 563, 1, -1, -1, 5, 4, 3500);
				break;
			case 26: // Teleport to Camelot.
				teleport(caster, MagicType.MODERN, 45, 2756, 3476, 0, 55, 556,
						5, 563, 1, -1, -1, -1, -1, 3, 5, 3500);
				break;
			case 32: // Teleport to Ardounge.
				teleport(caster, MagicType.MODERN, 51, 2659, 3305, 0, 61, 555,
						2, 563, 2, -1, -1, -1, -1, 5, 5, 3500);
				break;
			case 37: // Teleport to Watchtower.
				teleport(caster, MagicType.MODERN, 58, 2931, 4712, 2, 68, 557,
						2, 563, 2, -1, -1, -1, -1, 2, 1, 3500);
				break;
			case 44: // Teleport to Trollheim.
				teleport(caster, MagicType.MODERN, 61, 2890, 3678, 0, 68, 554,
						2, 563, 2, -1, -1, -1, -1, 4, 3, 3500);
				break;
			case 47: // Teleport to Ape Atoll.
				teleport(caster, MagicType.MODERN, 64, 2795, 2797, 1, 74, 554,
						2, 555, 2, 563, 2, 1963, 1, 6, 2, 3500);
				break;
			default:
				System.out.println("Unhandled modern magic button spell: "
						+ spellId);
			}
			break;
		case ANCIENT:
			switch (spellId) {

			case 24: // Home tele
				homeTele(caster, 3086 + r.nextInt(3), 3501 + r.nextInt(3), 0);
				break;
			case 16: // Paddewwa tele
				teleport(caster, MagicType.ANCIENT, 54, 3097, 9882, 0, 64, 563,
						2, 554, 1, 556, 1, -1, -1, 4, 2, 3000);
				break;
			case 17: // Senntisten tele
				teleport(caster, MagicType.ANCIENT, 60, 3360, 3385, 0, 70, 566,
						1, 563, 2, -1, -1, -1, -1, 3, 2, 3000);
				break;
			case 18: // Kharyrll tele
				teleport(caster, MagicType.ANCIENT, 66, 3492, 3492, 0, 76, 565,
						1, 563, 2, -1, -1, -1, -1, 4, 2, 3000);
				break;
			case 19: // Lassar tele
				teleport(caster, MagicType.ANCIENT, 72, 3000, 3469, 0, 82, 563,
						2, 555, 4, -1, -1, -1, -1, 4, 2, 3000);
				break;
			case 20: // Dareeyak tele
				teleport(caster, MagicType.ANCIENT, 78, 2965, 3694, 0, 88, 563,
						2, 554, 3, 556, 2, -1, -1, 3, 2, 3000);
				break;
			case 21: // Carrallangar tele
				teleport(caster, MagicType.ANCIENT, 84, 3156, 3668, 0, 82, 566,
						2, 563, 2, -1, -1, -1, -1, 2, 2, 3000);
				break;
			case 22: // Annakarl tele
				teleport(caster, MagicType.ANCIENT, 90, 3288, 3886, 0, 100,
						565, 2, 563, 2, -1, -1, -1, -1, 1, 1, 3000);
				break;
			case 23: // Ghorrock tele
				teleport(caster, MagicType.ANCIENT, 96, 2951, 3895, 0, 106,
						563, 2, 555, 8, -1, -1, -1, -1, 3, 3, 3000);
				break;

			default:
				System.out.println("Unhandled modern magic button spell: "
						+ spellId);
			}
			break;
		case LUNAR:
			switch (spellId) {
			case 16: // Home teleport - 0
				homeTele(caster, 3086 + r.nextInt(3), 3501 + r.nextInt(3), 0);
				break;
			case 15: // Bake pie - 65
			case 4: // Npc contact - 67
			case 7: // Humidify - 68
			case 20: // Moonclan teleport - 69
				teleport(caster, MagicType.LUNAR, 0, 2951, 3895, 0, 106, 9075,
						2, 563, 1, 557, 2, -1, -1, 3, 3, 1871);
			case 32: // Tele group Moonclan - 70
			case 23: // Cure me - 71
			case 8: // Hunter kit - 71
			case 24: // Waterbirth teleport - 72
				teleport(caster, MagicType.LUNAR, 0, 2951, 3895, 0, 105, 563,
						2, 555, 8, -1, -1, -1, -1, 3, 3, 3000);
			case 33: // Tele group waterbirth - 73
			case 3: // Cure group - 74
			case 0: // Barbarian teleport - 75
				teleport(caster, MagicType.LUNAR, 96, 2951, 3895, 0, 106, 563,
						2, 555, 8, -1, -1, -1, -1, 3, 3, 3000);
			case 34: // Tele group barbarian - 76
			case 25: // Superglass make - 77
			case 18: // Khazard teleport - 78
				teleport(caster, MagicType.LUNAR, 96, 2951, 3895, 0, 106, 563,
						2, 555, 8, -1, -1, -1, -1, 3, 3, 3000);
			case 35: // Tele group khazard - 79
			case 10: // Dream - 79
			case 22: // string jewellery - 80
			case 13: // Magic imbue - 82
			case 17: // Fishing guild teleport - 85
				teleport(caster, MagicType.LUNAR, 96, 2951, 3895, 0, 106, 563,
						2, 555, 8, -1, -1, -1, -1, 3, 3, 3000);
			case 36: // Tele group fishing guild - 86
			case 21: // Catherby teleport - 87
				teleport(caster, MagicType.LUNAR, 96, 2951, 3895, 0, 106, 563,
						2, 555, 8, -1, -1, -1, -1, 3, 3, 3000);
			case 37: // tele group catherby - 88
			case 28: // Ice plateau teleport - 89
				teleport(caster, MagicType.LUNAR, 96, 2951, 3895, 0, 106, 563,
						2, 555, 8, -1, -1, -1, -1, 3, 3, 3000);
			case 38: // tele group ice plateau - 90
			case 30: // heal group - 95
			case 12: // spellbook swap
				caster.getActionSender().sendMessage(
						"This spell doesn't work yet.");
				break;

			case 14: // Vengeance - 94
				if (caster instanceof Player) {
					caster.getMagic().vengence();
				}
				break;
			}
		}

	}

	/**
	 * 
	 * @param caster
	 *            Player who casts the spell
	 * @param lvlreq
	 *            Level required to use this teleport
	 * @param Xcoord
	 *            X coordinate where the spell teleports you
	 * @param Ycoord
	 *            Y coordinate where the spell teleports you
	 * @param height
	 *            Height level where the spell teleports you
	 * @param xp
	 *            XP the spell gives you
	 * @param normalTele
	 *            Determines if the spell is modern tele or ancient tele
	 * @param rune1
	 *            First required rune
	 * @param rune1am
	 *            First required rune amount
	 * @param rune2
	 *            Second required rune
	 * @param rune2am
	 *            Second required rune amount
	 * @param rune3
	 *            Third required rune
	 * @param rune3am
	 *            Third required rune amount
	 * @param item
	 *            Item needed for teleport
	 * @param itemam
	 *            Item amount needed for teleport
	 */
	public static void teleport(final Player caster, final MagicType teleType,
			int lvlReq, final int x, final int y, final int height, int xp,
			int rune1, int rune1am, int rune2, int rune2am, int rune3,
			int rune3am, int item, int itemam, final int xOffset,
			final int yOffset, int clickDelay) {

		if (caster.getRequestManager().isDueling()) {
			if (caster.getLocation().isInDuelArena()) {
				caster.getActionSender().sendMessage(
						"You can't teleport while you're dueling.");
				return;
			}
		}
		if (Location.getWildernessLevel(caster.getLocation()) > 20) {
			caster.getActionSender().sendMessage(
					"You cannot teleport above level 20 wilderness.");
			return;
		}

		if (caster.isTeleblocked()
				|| caster.getLocation().getNewArea(caster) != null) {
			caster.getActionSender().sendMessage(
					"A magical force stops you from teleporting.");
			return;
		}

		if (caster.getMagic().getLastTeleport() < clickDelay || caster.isDead()) {
			return;
		}

		int emote = -1;
		int gfx = -1;
		int delay = 2000;

		switch (teleType) {
		case MODERN:
			emote = 714;
			gfx = 301;
			delay = 1800;
			break;
		case ANCIENT:
			emote = 1979;
			gfx = 392;
			delay = 2300;
			break;
		case LUNAR:
			emote = 1816;
			gfx = 747;
			delay = 1800;
			break;
		}
		final int finalEmote = emote;

		Item itemName = new Item(item);
		Item itemName1 = new Item(rune1);
		Item itemName2 = new Item(rune2);
		Item itemName3 = new Item(rune3);

		if (!hasMagicLevel(caster, lvlReq)) {
			caster.getActionSender().sendMessage(
					"You need a Magic level of " + lvlReq
							+ " to cast this spell");
			return;
		}
		if (!hasRequiredItem(caster, rune1, rune1am)) {
			caster.getActionSender().sendMessage(
					"You don't have enough "
							+ itemName1.getDefinition().getName().toLowerCase()
							+ "s to cast this spell.");
			return;
		}
		if (!hasRequiredItem(caster, rune2, rune2am)) {
			caster.getActionSender().sendMessage(
					"You don't have enough " + ""
							+ itemName2.getDefinition().getName().toLowerCase()
							+ "s to cast this spell.");
			return;
		}
		if (!hasRequiredItem(caster, rune3, rune3am)) {
			caster.getActionSender().sendMessage(
					"You don't have enough " + ""
							+ itemName3.getDefinition().getName().toLowerCase()
							+ "s to cast this spell.");
			return;
		}
		if (item != -1 && itemam != -1) {
			if (hasRequiredItem(caster, item, itemam)) {
				caster.getActionSender().sendMessage(
						"You don't have enough "
								+ ""
								+ itemName.getDefinition().getName()
										.toLowerCase()
								+ "s to cast this spell.");
				return;
			}
		}

		caster.playAnimation(Animation.create(emote));
		caster.playGraphics(Graphic.create(gfx,
				teleType == MagicType.ANCIENT ? 0 : (100 << 16)));
		caster.getSkills().addExperience(6, xp);
		Inventory.removeRune(caster, -1, new Item(rune1, rune1am));
		Inventory.removeRune(caster, -1, new Item(rune2, rune2));
		if (rune3am != -1)
			Inventory.removeRune(caster, -1, new Item(rune3, rune3));

		caster.getMagic().setLastTeleport(System.currentTimeMillis());

		World.getWorld().submit(new Event(delay) {
			public void execute() {
				caster.setTeleportTarget(Location.create(
						x + r.nextInt(xOffset), y + r.nextInt(yOffset), height));
				caster.playAnimation(Animation
						.create(teleType == MagicType.MODERN ? finalEmote + 1
								: -1));
				this.stop();
			}
		});
	}

	private static Animation animHome = Animation.create(4847);
	private static Animation animHome1 = Animation.create(4850);
	private static Animation animHome2 = Animation.create(4853);
	private static Animation animHome3 = Animation.create(4855);
	private static Animation animHome4 = Animation.create(4857);
	private static Animation animReset = Animation.create(-1);

	private static Graphic gfxHome = Graphic.create(800);
	private static Graphic gfxHome1 = Graphic.create(802);
	private static Graphic gfxHome2 = Graphic.create(803);
	private static Graphic gfxHome3 = Graphic.create(804);
	private static Graphic gfxReset = Graphic.create(800);

	public static void homeTele(final Player caster, final int x, final int y,
			final int z) {
		if (caster.getRequestManager().isDueling()) {
			if (caster.getLocation().isInDuelArena()) {
				caster.getActionSender().sendMessage(
						"You can't teleport while you're dueling.");
				return;
			}
		}
		caster.getWalkingQueue().reset();
		if (Location.getWildernessLevel(caster.getLocation()) > 20) {
			caster.getActionSender().sendMessage(
					"You cannot teleport above level 20 wilderness.");
			return;
		}
		if (caster.isTeleblocked()
				|| caster.getLocation().getNewArea(caster) != null) {
			caster.getActionSender().sendMessage(
					"A magical force stops you from teleporting.");
			return;
		}
		caster.playAnimation(animHome);
		caster.playGraphics(gfxHome);
		final Location firstLocaition = caster.getLocation();
		World.getWorld().submit(new Event(3000) {
			int stage = 0;

			public void execute() {
				if (firstLocaition != caster.getLocation()) {
					this.stop();
					caster.playGraphics(gfxReset);
					return;
				}
				switch (stage) {
				case 0:
					stage = 1;
					caster.playAnimation(animHome1);
					this.setDelay(2700);
					return;
				case 1:
					stage = 2;
					caster.playAnimation(animHome2);
					caster.playGraphics(gfxHome1);
					return;
				case 2:
					stage = 3;
					caster.playAnimation(animHome3);
					caster.playGraphics(gfxHome2);
					this.setDelay(2500);
					return;
				case 3:
					stage = 4;
					caster.playAnimation(animHome4);
					caster.playGraphics(gfxHome3);
					this.setDelay(2000);
					return;
				case 4:
					caster.playAnimation(animReset);
					caster.setTeleportTarget(Location.create(x, y, z));
					this.stop();
					return;
				}
			}
		});
	}

	/**
	 * Checks if the player's magic level is high enough.
	 * 
	 * @param player
	 *            The player, which interface we're checking.
	 * @param level
	 *            The level needed.
	 */
	private static boolean hasMagicLevel(Player player, int level) {
		return (player.getSkills().getLevel(6) >= level);
	}

	/**
	 * Checks if the player has an item amount of item id.
	 * 
	 * @param player
	 *            The player, which interface we're checking.
	 * @param itemId
	 *            The item id.
	 * @param amt
	 *            The amount.
	 */
	private static boolean hasRequiredItem(Player player, int itemId, int amt) {
		return (player.getInventory().getCount(itemId) >= amt);
	}

	public static int mageDef(Entity e) {
		int chance = 0;
		if (e instanceof Player) {
			Player p = (Player) e;
			int mageDefBonus = p.getBonuses().getBonus(Bonuses.MAGIC_DEFENCE);
			int mageDef = p.getSkills().getLevel(Skills.DEFENCE) / 2
					+ p.getSkills().getLevel(Skills.MAGIC) / 2;
			int maxMage = (p.getSkills().getLevelForExperience(Skills.DEFENCE) / 2 + p
					.getSkills().getLevelForExperience(Skills.MAGIC) / 2);
			/*
			 * if (p.getPrayer().prayOn[Prayer.DEF_LOW]) mageDef += (double)
			 * maxMage * 0.05; else if (p.getPrayer().prayOn[Prayer.DEF_MID])
			 * mageDef += (double) maxMage * 0.10; else if
			 * (p.getPrayer().prayOn[Prayer.defHigh]) mageDef += (double)
			 * maxMage * 0.15;
			 */
			if (mageDefBonus < 0) {
				chance += (int) ((double) mageDef + (double) mageDef * 0.15 + ((double) mageDef + (double) mageDefBonus * 0.35));
				return chance;
			}
			chance += (int) ((double) mageDef + (double) mageDef * 0.15 + ((double) mageDefBonus + (double) mageDefBonus * 0.05));
			/*
			 * if(p.getPrayer().prayOn[Prayer.PRAY_MAGE]) { chance *= 2; }
			 */
			return chance;
		} else {

		}
		return chance;
	}

	public static int mageAtk(Entity e) {
		int chance = 0;
		if (e instanceof Player) {
			Player p = (Player) e;
			int mageBonus = p.getBonuses().getBonus(Bonuses.MAGIC_ATTACK);
			int mageLvl = p.getSkills().getLevel(Skills.MAGIC);
			int maxMage = p.getSkills().getLevelForExperience(Skills.MAGIC);

			/*
			 * if (fullAhrimsEquipped()) mageLvl += (double) maxMage * 0.05;
			 */
			/*
			 * if (p.getPrayer().prayOn[Prayer.MAGE_LOW]) mageLvl += (double)
			 * maxMage * 0.05; if (p.getPrayer().prayOn[Prayer.MAGE_MID])
			 * mageLvl += (double) maxMage * 0.10;
			 * if(p.getPrayer().prayOn[Prayer.MAGE_HIGH]) mageLvl += (double)
			 * maxMage * 0.15; /*if (fullMaVoidEquipped()) mageLvl += (double)
			 * maxMage * 0.2; if (spellCastedId == 12037) chance += (int)
			 * ((double) mageLvl + (double) mageLvl * 0.15 + ((double) mageLvl +
			 * (double) mageLvl * 0.05)); else
			 */
			if (mageBonus < 0) {
				chance += (int) ((double) mageLvl + (double) mageLvl * 0.15 + ((double) mageBonus + (double) mageBonus * 0.40));
				return chance;
			}
			chance += (int) ((double) mageLvl + (double) mageLvl * 0.15 + ((double) mageBonus + (double) mageBonus * 0.07));
			return chance;
		}
		return chance;
	}

	/**
	 * Calculates the magic attack for an entity.
	 */
	public static int calculateMagicAttack(Entity e) {
		if (e instanceof Player) {
			Player p = (Player) e;
			int magicalAttackBonus = (p.getBonuses().getBonus(3) * 3);
			int magicalAttackLevel = (p.getSkills().getLevel(Skills.MAGIC) / 2);

			if (p.getPrayer().isPrayerToggled(Prayer.MAGE_LOW))
				magicalAttackLevel *= 1.05;
			if (p.getPrayer().isPrayerToggled(Prayer.MAGE_MID))
				magicalAttackLevel *= 1.10;
			if (p.getPrayer().isPrayerToggled(Prayer.MAGE_HIGH))
				magicalAttackLevel *= 1.15;

			return magicalAttackLevel + magicalAttackBonus;
		} else {
			NPC n = (NPC) e;
			return n.getDefinition().getAttackBonus();
		}
	}

	/**
	 * Calculates the magic defense for an entity.
	 */
	public static int calculateMagicDefence(Entity e) {
		if (e instanceof Player) {
			Player p = (Player) e;
			int defBonus = p.getBonuses().getBonus(8);
			int deflvl = p.getSkills().getLevel(Skills.DEFENCE);
			defBonus = defBonus / 2;
			deflvl = deflvl / 2;

			if (p.getPrayer().isPrayerToggled(Prayer.THICK_SKIN))
				deflvl *= 1.05;
			else if (p.getPrayer().isPrayerToggled(Prayer.ROCK_SKIN))
				deflvl *= 1.10;
			else if (p.getPrayer().isPrayerToggled(Prayer.STEEL_SKIN))
				deflvl *= 1.15;

			return defBonus + deflvl;
		} else {
			NPC n = (NPC) e;
			return n.getDefinition().getDefenceMage();
		}
	}

	/**
	 * Checks if the target is in a multi area, and if the targets coordinates
	 * just around is.
	 */
	public static boolean checkForMulti(Entity target) {
		int z = target.getLocation().getZ();
		if (!Location.isInMulti(target.getLocation().getX(), target
				.getLocation().getY(), z)
				|| !Location.isInMulti(target.getLocation().getX() - 1, target
						.getLocation().getY(), z)
				|| !Location.isInMulti(target.getLocation().getX(), target
						.getLocation().getY() - 1, z)
				|| !Location.isInMulti(target.getLocation().getX() + 1, target
						.getLocation().getY(), z)
				|| !Location.isInMulti(target.getLocation().getX(), target
						.getLocation().getY() + 1, z)
				// DONE North, South, East, West.
				|| !Location.isInMulti(target.getLocation().getX() + 1, target
						.getLocation().getY() + 1, z)
				|| !Location.isInMulti(target.getLocation().getX() - 1, target
						.getLocation().getY() + 1, z)
				|| !Location.isInMulti(target.getLocation().getX() + 1, target
						.getLocation().getY() - 1, z)
				|| !Location.isInMulti(target.getLocation().getX() - 1, target
						.getLocation().getY() - 1, z)) {
			// DONE NE, SE, NW, SW
			return false;
		}
		return true;
	}

	public static void handleMagicOnItem(Player player, Item item, int spellId,
			int slot) {
		player.lastMagicOnItem = System.currentTimeMillis();
		if (System.currentTimeMillis() - player.lastMagicOnItem > 3000) {
			return;
		}
		switch (player.getMagic().getSpellBook()) {// Could use that enum, but
													// w/e.
		case MODERN:// Modern magic
			switch (spellId) {
			/*
			 * Low level alching..
			 */
			case 13:
				if (player.getSkills().getLevel(Skills.MAGIC) < 21) {
					player.getActionSender().sendMessage(
							"You need a Magic level of 21 to cast this spell.");
					return;
				}
				if (!Inventory.containsRune(player, new Item(561))
						|| !Inventory.containsRune(player, new Item(554, 3))) {
					player.getActionSender()
							.sendMessage(
									"You do not have the required runes to cast this spell.");
					return;
				}
				if (item.getId() == 995) {
					player.getActionSender().sendMessage(
							"You cannot convert gold into gold.");
					return;
				}
				player.playAnimation(Animation.create(712));
				player.playGraphics(Graphic.create(112, (100 << 16)));
				player.getSkills().addExperience(Skills.MAGIC, 31);

				player.getInventory().remove(slot, new Item(item.getId()));
				player.getInventory().add(
						new Item(995, item.getDefinition().getLowAlcValue()));
				player.getActionSender().sendSwitchTabs(6);
				Inventory.removeRune(player, -1, new Item(561));
				Inventory.removeRune(player, -1, new Item(554, 3));
				break;
			/*
			 * High level alching..
			 */
			case 34:
				if (player.getSkills().getLevel(Skills.MAGIC) < 55) {
					player.getActionSender().sendMessage(
							"You need a Magic level of 55 to cast this spell.");
					return;
				}
				if (!Inventory.containsRune(player, new Item(561))
						|| !Inventory.containsRune(player, new Item(554, 5))) {
					player.getActionSender()
							.sendMessage(
									"You do not have the required runes to cast this spell.");
					return;
				}
				if (item.getId() == 995) {
					player.getActionSender().sendMessage(
							"You cannot convert gold into gold.");
					return;
				}
				player.playAnimation(Animation.create(713));
				player.playGraphics(Graphic.create(113, (100 << 16)));
				player.getSkills().addExperience(Skills.MAGIC, 65);
				player.getInventory().remove(slot, new Item(item.getId()));
				player.getInventory().add(
						new Item(995, item.getDefinition().getHighAlcValue()));
				player.getActionSender().sendSwitchTabs(6);
				Inventory.removeRune(player, -1, new Item(561));
				Inventory.removeRune(player, -1, new Item(554, 5));
				break;
			/*
			 * Level 1 Enchanting.
			 */
			case 5:
				if (player.getSkills().getLevel(Skills.MAGIC) < 7) {
					player.getActionSender().sendMessage(
							"You need a Magic level of 7 to cast this spell.");
					return;
				}
				if (!Inventory.containsRune(player, new Item(555))
						|| !Inventory.containsRune(player, new Item(564))) {
					player.getActionSender()
							.sendMessage(
									"You do not have the required runes to cast this spell.");
					return;
				}
				if (item.getId() == 1637) { // Sapphire ring
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(238, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 17);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(2550));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(555));
					Inventory.removeRune(player, -1, new Item(564));
				}
				if (item.getId() == 1656) { // Sapphire necklace
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(114, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 17);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(3853));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(555));
					Inventory.removeRune(player, -1, new Item(564));
				}
				if (item.getId() == 1694) { // Sapphire ammy
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(114, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 17);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(1727));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(555));
					Inventory.removeRune(player, -1, new Item(564));
				}
				break;
			/*
			 * Level 2 Enchanting.
			 */
			case 16:
				if (player.getSkills().getLevel(Skills.MAGIC) < 27) {
					player.getActionSender().sendMessage(
							"You need a Magic level of 27 to cast this spell.");
					return;
				}
				if (!Inventory.containsRune(player, new Item(556, 3))
						|| !Inventory.containsRune(player, new Item(564))) {
					player.getActionSender()
							.sendMessage(
									"You do not have the required runes to cast this spell.");
					return;
				}
				if (item.getId() == 1639) { // Emerald ring
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(238, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 37);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(2552));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(556, 3));
					Inventory.removeRune(player, -1, new Item(564));
				}
				if (item.getId() == 1656) { // Emerald necklace
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(114, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 37);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(5521));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(556, 3));
					Inventory.removeRune(player, -1, new Item(564));
				}
				if (item.getId() == 1696) { // Emerald ammy
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(114, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 37);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(1729));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(556, 3));
					Inventory.removeRune(player, -1, new Item(564));
				}
				break;
			/*
			 * Level 3 Enchanting.
			 */
			case 28:
				if (player.getSkills().getLevel(Skills.MAGIC) < 49) {
					player.getActionSender().sendMessage(
							"You need a Magic level of 49 to cast this spell.");
					return;
				}
				if (!Inventory.containsRune(player, new Item(554, 5))
						|| !Inventory.containsRune(player, new Item(564))) {
					player.getActionSender()
							.sendMessage(
									"You do not have the required runes to cast this spell.");
					return;
				}
				if (item.getId() == 1641) { // Ruby ring
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(238, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 59);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(2568));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(554, 5));
					Inventory.removeRune(player, -1, new Item(564));
				}
				if (item.getId() == 1660) { // Ruby necklace
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(115, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 59);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(11195));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(554, 5));
					Inventory.removeRune(player, -1, new Item(564));
				}
				if (item.getId() == 1698) { // Ruby ammy
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(114, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 59);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(1725));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(554, 5));
					Inventory.removeRune(player, -1, new Item(564));
				}
				break;
			/*
			 * Level 4 Enchanting.
			 */
			case 36:
				if (player.getSkills().getLevel(Skills.MAGIC) < 57) {
					player.getActionSender().sendMessage(
							"You need a Magic level of 57 to cast this spell.");
					return;
				}
				if (!Inventory.containsRune(player, new Item(557, 10))
						|| !Inventory.containsRune(player, new Item(564))) {
					player.getActionSender()
							.sendMessage(
									"You do not have the required runes to cast this spell.");
					return;
				}
				if (item.getId() == 1643) { // Diamond ring
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(238, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 67);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(2570));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(557, 10));
					Inventory.removeRune(player, -1, new Item(564));
				}
				if (item.getId() == 1662) { // Diamond necklace
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(115, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 67);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(11090));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(557, 10));
					Inventory.removeRune(player, -1, new Item(564));
				}
				if (item.getId() == 1700) { // Diamond ammy
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(114, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 67);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(1731));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(557, 10));
					Inventory.removeRune(player, -1, new Item(564));
				}
				break;
			/*
			 * Level 5 Enchanting.
			 */
			case 51:
				if (player.getSkills().getLevel(Skills.MAGIC) < 57) {
					player.getActionSender().sendMessage(
							"You need a Magic level of 57 to cast this spell.");
					return;
				}
				if (!Inventory.containsRune(player, new Item(555, 15))
						|| !Inventory.containsRune(player, new Item(564))
						|| !Inventory.containsRune(player, new Item(557, 15))) {
					player.getActionSender()
							.sendMessage(
									"You do not have the required runes to cast this spell.");
					return;
				}
				if (item.getId() == 1645) { // Dragonstone ring
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(238, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 78);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(2572));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(555, 15));
					Inventory.removeRune(player, -1, new Item(557, 15));
					Inventory.removeRune(player, -1, new Item(564));
				}
				if (item.getId() == 1664) { // Dragonstone necklace
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(115, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 78);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(11113));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(555, 15));
					Inventory.removeRune(player, -1, new Item(557, 15));
					Inventory.removeRune(player, -1, new Item(564));
				}
				if (item.getId() == 1702) { // Dragonstone ammy
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(114, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 78);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(1712));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(555, 15));
					Inventory.removeRune(player, -1, new Item(557, 15));
					Inventory.removeRune(player, -1, new Item(564));
				}
				break;
			/*
			 * Level 6 Enchanting.
			 */
			case 61:
				if (player.getSkills().getLevel(Skills.MAGIC) < 87) {
					player.getActionSender().sendMessage(
							"You need a Magic level of 87 to cast this spell.");
					return;
				}
				if (!Inventory.containsRune(player, new Item(554, 20))
						|| !Inventory.containsRune(player, new Item(564))
						|| !Inventory.containsRune(player, new Item(557, 20))) {
					player.getActionSender()
							.sendMessage(
									"You do not have the required runes to cast this spell.");
					return;
				}
				if (item.getId() == 6581) { // Fury Ammy
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(238, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 97);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(6585));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(554, 20));
					Inventory.removeRune(player, -1, new Item(557, 20));
					Inventory.removeRune(player, -1, new Item(564));
				}
				if (item.getId() == 6577) { // Onyx necklace
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(115, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 97);

					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(11128));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(554, 20));
					Inventory.removeRune(player, -1, new Item(557, 20));
					Inventory.removeRune(player, -1, new Item(564));
				}
				if (item.getId() == 6575) { // Onyx ring
					player.playAnimation(Animation.create(712)); // SET CORRECT
																	// ONE
					player.playGraphics(Graphic.create(114, (100 << 16)));
					player.getSkills().addExperience(Skills.MAGIC, 97);
					player.getInventory().remove(slot, new Item(item.getId()));
					player.getInventory().add(new Item(6583));
					player.getActionSender().sendSwitchTabs(6);
					Inventory.removeRune(player, -1, new Item(554, 20));
					Inventory.removeRune(player, -1, new Item(557, 20));
					Inventory.removeRune(player, -1, new Item(564));
				}
				break;
			default:
				System.out.println("Unhandled magic on item spell: " + spellId
						+ ".");
			}
			break;
		}

	}

	private static void loadModernSpells() {
		MODERN_SPELLS[1] = new ElementalSpell(1, 90, 91, 92, 2, 5,
				new Item(556), new Item(558), null);// Wind strike.
		MODERN_SPELLS[4] = new ElementalSpell(5, 93, 94, 95, 4, 7,
				new Item(555), new Item(556), new Item(558));// Water strike.
		MODERN_SPELLS[6] = new ElementalSpell(9, 96, 97, 98, 6, 9, new Item(
				557, 2), new Item(556), new Item(558));// Earth strike.
		MODERN_SPELLS[8] = new ElementalSpell(13, 99, 100, 101, 8, 11,
				new Item(554, 3), new Item(556, 2), new Item(558));// Fire
																	// strike.

		MODERN_SPELLS[10] = new ElementalSpell(17, 117, 118, 119, 9, 13,
				new Item(556, 2), new Item(562), null);// Wind bolt.
		MODERN_SPELLS[14] = new ElementalSpell(23, 120, 121, 122, 10, 16,
				new Item(556, 2), new Item(555, 2), new Item(562));// Water
																	// bolt.
		MODERN_SPELLS[17] = new ElementalSpell(29, 123, 124, 125, 11, 20,
				new Item(556, 2), new Item(557, 4), new Item(562));// Earth
																	// bolt.
		MODERN_SPELLS[20] = new ElementalSpell(35, 126, 127, 128, 12, 22,
				new Item(556, 3), new Item(554, 4), new Item(562));// Fire bolt.

		MODERN_SPELLS[24] = new ElementalSpell(41, 132, 133, 134, 13, 25,
				new Item(556, 3), new Item(560), null);// Wind blast.
		MODERN_SPELLS[27] = new ElementalSpell(47, 135, 136, 137, 14, 28,
				new Item(556, 3), new Item(555, 3), new Item(560));// Water
																	// blast.
		MODERN_SPELLS[33] = new ElementalSpell(53, 138, 139, 140, 15, 31,
				new Item(556, 3), new Item(557, 4), new Item(560));// Earth
																	// blast.
		MODERN_SPELLS[38] = new ElementalSpell(59, 129, 130, 131, 16, 35,
				new Item(556, 4), new Item(554, 5), new Item(560));// Fire
																	// blast.

		MODERN_SPELLS[45] = new ElementalSpell(62, 158, 159, 160, 17, 36,
				new Item(556, 5), new Item(565), null);// Wind wave.
		MODERN_SPELLS[48] = new ElementalSpell(65, 161, 162, 163, 18, 37,
				new Item(556, 5), new Item(555, 7), new Item(565));// Water
																	// wave.
		MODERN_SPELLS[52] = new ElementalSpell(70, 164, 165, 166, 19, 40,
				new Item(556, 5), new Item(557, 7), new Item(565));// Earth
																	// wave.
		MODERN_SPELLS[55] = new ElementalSpell(75, 155, 156, 157, 20, 42,
				new Item(556, 5), new Item(554, 7), new Item(565));// Fire wave.

		MODERN_SPELLS[2] = new ReducingSpell(3, 13, 716, 102, 103, 104,
				Skills.ATTACK, 0.05, new Item(555, 3), new Item(557, 2),
				new Item(559));// Confuse
		MODERN_SPELLS[7] = new ReducingSpell(11, 21, 716, 105, 106, 107,
				Skills.STRENGTH, 0.05, new Item(555, 3), new Item(557, 2),
				new Item(559));// Weaken
		MODERN_SPELLS[11] = new ReducingSpell(19, 29, 729, 108, 109, 110,
				Skills.DEFENCE, 0.05, new Item(555, 2), new Item(557, 3),
				new Item(559));// Curse

		MODERN_SPELLS[50] = new ReducingSpell(66, 76, 729, 167, 168, 169,
				Skills.ATTACK, 0.10, new Item(555, 5), new Item(557, 5),
				new Item(566));// Vulnerability
		MODERN_SPELLS[53] = new ReducingSpell(73, 83, 729, 170, 171, 172,
				Skills.STRENGTH, 0.10, new Item(555, 8), new Item(557, 8),
				new Item(566));// Enfeeble
		MODERN_SPELLS[57] = new ReducingSpell(80, 90, 716, 173, 174, 175,
				Skills.DEFENCE, 0.10, new Item(555, 12), new Item(557, 12),
				new Item(566));// Stun

		MODERN_SPELLS[12] = new FreezingSpell(20, 5000, 179, 2, 30, new Item(
				557, 3), new Item(555, 3), new Item(561));// Bind
		MODERN_SPELLS[30] = new FreezingSpell(50, 10000, 180, 3, 60.5,
				new Item(557, 4), new Item(555, 4), new Item(561, 3));// Snare
		MODERN_SPELLS[56] = new FreezingSpell(79, 15000, 181, 5, 91, new Item(
				557, 5), new Item(555, 5), new Item(561, 4));// Entangle

		/*
		 * MODERN_SPELLS[54] = new TeleotherSpell();//Teleother Lumbridge
		 * MODERN_SPELLS[59] = new TeleotherSpell();//Teleother Falador
		 * MODERN_SPELLS[62] = new TeleotherSpell();//Teleother Camelot
		 */

		MODERN_SPELLS[41] = new GodSpell(Graphic.create(76, (180 << 16)),
				new Item(554, 2), new Item(565, 2), new Item(556, 4),
				GodSpell.SARA_STAFF);// Saradomin Strike
		MODERN_SPELLS[42] = new GodSpell(Graphic.create(77, (100 << 16)),
				new Item(554, 1), new Item(565, 2), new Item(556, 4),
				GodSpell.GUTHIX_STAFF);// Claws of Guthix
		MODERN_SPELLS[43] = new GodSpell(Graphic.create(78), new Item(554, 4),
				new Item(565, 2), new Item(556, 1), GodSpell.ZAMORAK_STAFF);// Flames
																			// of
																			// Zamorak

		// MODERN_SPELLS[22] = new CrumbleUndead();

		// MODERN_SPELLS[29] = new IbanBlast();

		// MODERN_SPELLS[31] = new MagicDart();

		MODERN_SPELLS[60] = new TeleBlock();

	}

	private static void loadAncientSpells() {
		ANCIENT_SPELLS[0] = new IceSpell(58, 5000, Animation.create(1978),
				Graphic.create(367), 360, Graphic.create(361), 17, 34,
				new Item(562, 2), new Item(560, 2), new Item(555, 2), false);// Ice
																				// rush.
		ANCIENT_SPELLS[1] = new IceSpell(82, 15000, Animation.create(1978),
				Graphic.create(367), -1, Graphic.create(367), 28, 46, new Item(
						560, 2), new Item(565, 2), new Item(555, 3), false);// Ice
																			// blitz
		ANCIENT_SPELLS[2] = new IceSpell(70, 10000, Animation.create(1979),
				null, -1, Graphic.create(363), 22, 40, new Item(562, 4),
				new Item(560, 2), new Item(555, 4), true);// Ice burst
		ANCIENT_SPELLS[3] = new IceSpell(94, 20000, Animation.create(1979),
				null, -1, Graphic.create(369), 30, 52, new Item(560, 4),
				new Item(565, 2), new Item(555, 6), true);// Ice barrage

		ANCIENT_SPELLS[4] = new BloodSpell(56, 1978, -1, -1, 373, 15, 33,
				new Item(562, 2), new Item(560, 2), new Item(565), false);// Blood
																			// rush.
		ANCIENT_SPELLS[5] = new BloodSpell(80, 1978, -1, 374, 375, 25, 45,
				new Item(560, 2), new Item(565, 4), null, false);// Blood blitz.
		ANCIENT_SPELLS[6] = new BloodSpell(68, 1979, -1, -1, 376, 21, 39,
				new Item(562, 4), new Item(560, 2), new Item(565, 2), true);// Blood
																			// burst.
		ANCIENT_SPELLS[7] = new BloodSpell(92, 1979, -1, -1, 377, 29, 51,
				new Item(560, 4), new Item(565, 4), new Item(566), true);// Blood
																			// barrage.
		/*
		 * ANCIENT_SPELLS[8] = new SmokeSpell();//Smoke rush. ANCIENT_SPELLS[9]
		 * = new SmokeSpell();//Smoke blitz. ANCIENT_SPELLS[10] = new
		 * SmokeSpell();//Smoke burst. ANCIENT_SPELLS[11] = new
		 * SmokeSpell();//Smoke barrage.
		 */

		/*
		 * ANCIENT_SPELLS[12] = new ShadowSpell();//Shadow rush.
		 * ANCIENT_SPELLS[13] = new ShadowSpell();//Shadow blitz.
		 * ANCIENT_SPELLS[14] = new ShadowSpell();//Shadow burst.
		 * ANCIENT_SPELLS[15] = new ShadowSpell();//Shadow barrage.
		 */

	}

	private static void loadLunarSpells() {
		LUNAR_SPELLS[19] = new VengenceOther();

	}

	static {
		loadModernSpells();
		loadAncientSpells();
		loadLunarSpells();
	}
}
