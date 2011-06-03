package org.hyperion.rs2.content.skills;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;

public class Prayer {

	private Player player;
	private int prayerDrain = 100;
	private int drainRate = 0;
	private int prayerIcon = -1;
	private boolean prayers[] = new boolean[24];
	private boolean eventIsRunning = false;

	public Prayer(Player player) {
		this.player = player;
	}

	public static final int THICK_SKIN = 0;
	public static final int BURST_OF_STRENGTH = 1;
	public static final int ATTACK_LOW = 2;
	public static final int SHARP_EYE = 3;
	public static final int MAGE_LOW = 4;
	public static final int ROCK_SKIN = 5;
	public static final int SUPERHUMAN_STRENGTH = 6;
	public static final int ATTACK_MID = 7;
	public static final int RAPID_RESTORE = 8;
	public static final int RAPID_HEAL = 9;
	public static final int PROTECT_ITEMS = 10;
	public static final int HAWK_EYE = 11;
	public static final int MAGE_MID = 12;
	public static final int STEEL_SKIN = 13;
	public static final int ULTIMATE_STRENGTH = 14;
	public static final int ATTACK_HIGH = 15;
	public static final int PROTECT_FROM_MAGE = 16;
	public static final int PROTECT_FROM_RANGE = 17;
	public static final int PROTECT_FROM_MELEE = 18;
	public static final int EAGLE_EYE = 19;
	public static final int MAGE_HIGH = 20;
	public static final int RETRIBUTION = 21;
	public static final int REDEMPTION = 22;
	public static final int SMITE = 23;

	/**
	 * Tells us whenever or not this player have a specific prayer toggled.
	 * 
	 * @param prayer
	 *            The index of the Prayer to check.
	 * @return <code>true</code> if the prayer is activated, <code>false</code>
	 *         if not.
	 */
	public boolean isPrayerToggled(int prayer) {
		return prayers[prayer];
	}

	/* Method to switch prayers */
	public void switchPrayers(Player player, int i) {
		System.out.println("switch prayer: " + i);
		switch (i) {
		/* i represents which prayer is being used */
		case 0:// Thick skin
			/* Array created for prayers to switch off */
			int[] off = { 5, 13 };// , 3, 4, 11, 12, 19, 20};
			/* Switches off all prayers in the array */
			prayerOff(off);
			break;
		case 1:// Burst of Strength
			int[] off2 = { 6, 14, 3, 4, 11, 12, 19, 20 };
			prayerOff(off2);
			break;
		case 2:// Clarity of Thought
			int[] off3 = { 7, 15, 3, 4, 11, 12, 19, 20 };
			prayerOff(off3);
			break;
		case 3:// Sharp eye
			int[] off4 = { 1, 2, 6, 7, 14, 15, 4, 11, 12, 19, 20 };
			prayerOff(off4);
			break;
		case 4:// Mystic will
			int[] off5 = { 1, 2, 6, 7, 14, 15, 3, 11, 12, 19, 20 };
			prayerOff(off5);
			break;
		case 5:// Rock skin
			int[] off6 = { 0, 13 };// , 3, 4, 11, 12, 19, 20};
			prayerOff(off6);
			break;
		case 6:// Super human strength
			int[] off7 = { 1, 14, 3, 4, 11, 12, 19, 20 };
			prayerOff(off7);
			break;
		case 7:// Improved reflects
			int[] off8 = { 2, 15, 3, 4, 11, 12, 19, 20 };
			prayerOff(off8);
			break;
		case 8:// Rapid restore
			int[] off9 = { 9 };
			prayerOff(off9);
			break;
		case 9:// Rapid health
			int[] off10 = { 8 };
			prayerOff(off10);
			break;
		case 10:// Protect items
			int[] off11 = {};
			prayerOff(off11);
			break;
		case 11:// Hawk eye
			int[] off12 = { 1, 2, 6, 7, 14, 15, 3, 4, 12, 19, 20 };
			prayerOff(off12);
			break;
		case 12:// Mystic lore
			int[] off13 = { 1, 2, 6, 7, 14, 15, 3, 4, 11, 19, 20 };
			prayerOff(off13);
			break;
		case 13:// Steel skin
			int[] off14 = { 5, 1 };
			prayerOff(off14);
			break;
		case 14:// Ultimate strength
			int[] off15 = { 6, 2, 3, 4, 11, 12, 19, 20 };
			prayerOff(off15);
			break;
		case 15:// Incredible reflexes
			int[] off16 = { 7, 2, 3, 4, 11, 12, 19, 20 };
			prayerOff(off16);
			break;
		case 16:// Protect from Magic
			int[] off17 = { 17, 18, 21, 22, 23 };
			prayerOff(off17);
			break;
		case 17:// Protect from Range
			int[] off18 = { 16, 18, 21, 22, 23 };
			prayerOff(off18);
			break;
		case 18:// Protect from Melee
			int[] off19 = { 16, 17, 21, 22, 23 };
			prayerOff(off19);
			break;
		case 19:// Eagle eye
			int[] off20 = { 1, 2, 6, 7, 14, 15, 3, 4, 11, 12, 20 };
			prayerOff(off20);
			break;
		case 20:// Mystic might
			int[] off21 = { 1, 2, 6, 7, 14, 15, 3, 4, 11, 12, 19 };
			prayerOff(off21);
			break;
		case 21:// Retribution
			int[] off22 = { 16, 17, 18, 22, 23 };
			prayerOff(off22);
			break;
		case 22:// Redemption
			int[] off23 = { 16, 17, 18, 21, 23 };
			prayerOff(off23);
			break;
		case 23:// Smite
			int[] off24 = { 16, 17, 18, 21, 22 };
			prayerOff(off24);
			break;
		}
	}

	/* Method to switch prayers off */
	public void prayerOff(int[] prayers) {
		for (int z = 0; z < prayers.length; z++) {
			if (this.prayers[prayers[z]]) {
				this.prayers[prayers[z]] = false;
				player.getActionSender().sendConfig(PRAYER_CONFIGS[prayers[z]],
						0);
				drainRate -= DRAIN_RATES[prayers[z]];
			}
		}
	}

	public void handleButtons(int buttonId) {
		System.out.println("ButtonId: " + buttonId);
		int x = 0;
		/* Prayer button id's range from 5-57, increasing by 2 each time. */
		for (int i = 5; i <= 51; i += 2) {
			/* Checks if the loop mathces the button pressed */
			/* Checks if the player has more than 0 prayer points */
			if (buttonId == i && player.getSkills().getLevel(5) > 0) {
				/* Checks if the players prayer level is high enough */
				if (player.getSkills().getLevelForExperience(5) >= PRAYER_LEVEL_REQS[x]) {
					/*
					 * Switches off other prayers affected by the prayer
					 * selected
					 */
					switchPrayers(player, x);
					/* Changes prayer status on/off */
					this.prayers[x] = !this.prayers[x];
					/* Changes prayer config status, 0 for off or 1 for on */
					player.getActionSender().sendConfig(PRAYER_CONFIGS[x],
							prayers[x] ? 1 : 0);
					/* Checks if the prayer requires a headicon */
					if (x >= 16 && x <= 18 || x >= 21 && x <= 24) {
						if (this.prayers[x]) {
							/*
							 * Gets the required pray icon if prayer is being
							 * switched on
							 */
							getPrayerIcon(player, x);
						} else {
							/* Removes head icon if prayer is being switched off */
							setPrayerIcon(-1);
						}
					}
					if (this.prayers[x]) {
						/*
						 * Increases the rate at which prayer drains if switched
						 * on
						 */
						this.drainRate += DRAIN_RATES[x];
					} else {
						/*
						 * Decreases the rate at which prayer drains if switched
						 * off
						 */
						this.drainRate -= DRAIN_RATES[x];
					}
					// player.appearanceUpdateReq = true;
					player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				} else {
					player.getActionSender().sendChatboxInterface(210);
					player.getActionSender().sendString(
							"<col=00008B>You need a Prayer level of "
									+ PRAYER_LEVEL_REQS[x]
									+ " to use this Prayer.", 210, 0);
				}
			}
			/* Counts which prayer is being used */
			x++;
		}
		// Best way I could think of was putting redemption here, and in the hit
		// method in player.
		// redemption(player);
		if (!eventIsRunning) {
			World.getWorld().submit(new PrayerEvent());
			eventIsRunning = true;
		}
	}

	/* Resets all prayer configs */
	/* Turns off all prayers */
	/* Removes headicons */
	public void reset() {
		eventIsRunning = false;
		for (int i = 0; i < prayers.length; i++) {
			if (prayers[i]) {
				prayers[i] = false;
			}
		}
		for (int config : PRAYER_CONFIGS) {
			player.getActionSender().sendConfig(config, 0);
		}
		drainRate = 0;
		setPrayerIcon(-1);
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	public void setPrayerIcon(int icon) {
		this.prayerIcon = icon;
	}

	public int getPrayerIcon() {
		return prayerIcon;
	}

	/* Method to check for head icon */
	public void getPrayerIcon(Player p, int i) {
		switch (i) {
		case 16:
			prayerIcon = 2;
			break;
		case 17:
			prayerIcon = 1;
			break;
		case 18:
			prayerIcon = 0;
			break;
		case 21:
			prayerIcon = 3;
			break;
		case 22:
			prayerIcon = 5;
			break;
		case 23:
			prayerIcon = 4;
			break;
		case 24:
			prayerIcon = 7;
			break;
		}
	}

	/* Configs for prayers */
	private static final int PRAYER_CONFIGS[] = { 83, 84, 85, 862, 863, 86, 87,
			88, 89, 90, 91, 864, 865, 92, 93, 94, 95, 96, 97, 866, 867, 98, 99,
			100 };

	/* Prayer levels required */
	private static final int PRAYER_LEVEL_REQS[] = { 1, 4, 7, 8, 9, 10, 13, 16,
			19, 22, 25, 26, 27, 28, 31, 34, 37, 40, 43, 44, 45, 46, 49, 52 };

	/* Drain rate of each prayer */
	private static final int DRAIN_RATES[] = { 3, 4, 5, 6, 7, 8, 9, 10, 6, 7,
			6, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 };

	private static final Animation alterAnim = Animation.create(645);

	/**
	 * The special boolean is used if you're using an altar which boosts your
	 * prayer by 2. Eg. the one in Prayer guild, and the Nature alter in the
	 * Montuana(spelled?) swamp.
	 */
	public static void altar(Player player, boolean special) {
		if ((special ? player.getSkills().getLevelForExperience(5) : player
				.getSkills().getLevelForExperience(5) + 2) <= player
				.getSkills().getLevel(5)) {
			player.getActionSender().sendMessage(
					"You already have full Prayer.");
			return;
		}
		player.getSkills().setLevel(
				5,
				(special ? player.getSkills().getLevelForExperience(5) + 2
						: player.getSkills().getLevelForExperience(5)));
		player.getActionSender().sendMessage(
				"You recharge your Prayer at the altar"
						+ (special ? "and feel an extra boost." : "."));
		player.playAnimation(alterAnim);
	}

	private static final Animation BONE_BURYING_ANIMATION = Animation
			.create(827);

	public static boolean bury(final Player player, final int itemId,
			final int slot) {
		if (System.currentTimeMillis() - player.lastBury < 1500)
			return false;
		player.lastBury = System.currentTimeMillis();
		final double exp = calculateXp(itemId);
		if (exp != -1) {
			player.getWalkingQueue().reset();
			player.playAnimation(BONE_BURYING_ANIMATION);
			player.getActionSender().sendMessage(
					"You dig a hole in the ground...");
			World.getWorld().submit(new Event(1000) {
				public void execute() {
					player.getSkills().addExperience(5, exp);
					player.getInventory().remove(slot, new Item(itemId));
					player.getActionSender().sendMessage("You bury the bones.");
					this.stop();
				}
			});
			return true;
		}
		return false;
	}

	// Norm, burnt, bat, big, bbydrag, drag, wolf, shaikahn, jogre, zogre,
	// fayrg, raurg,
	// Ourgh, Daganoth, Wyvern
	private static final int[] BONES = { 526, 528, 530, 532, 534, 536, 2859,
			3123, 3125, 4812, 4830, 4832, 4834, 6729, 6812 };
	private static final double[] EXP = { 4.5, 5.0, 5.25, 15, 30, 50, 4.4, 25,
			15, 22.5, 84, 96, 150, 150, 125, 50 };

	// TODO: Monkey bones.
	private static double calculateXp(int itemId) {
		for (int i = 0; i < BONES.length; i++) {
			if (BONES[i] == itemId) {
				return EXP[i];
			}
		}
		return -1;
	}

	/*
	 * switch(itemId) { case 526://Regular bones return 4.5; case 528://Burnt
	 * Bones return 5.0; case 530://Bat bones return 5.25; case 532://Big bones.
	 * return 15; case 534://Baby dragon bones. return 30; case 536://Dragon
	 * bones return 50; case 2530://Regular
	 * bones????????????????????????????????
	 * ??????????????????????????????????????? return 4.5; case 2859://Wolf
	 * bones return 4.4; case 3123://Shaikahan bones (from The Shaikahan, east
	 * of Tai Bwo Wannai.) return 25; case 3125://Jorge bones. /*Those are
	 * probably for some quest or w/e.
	 */
	// I'll give them all 15 experience, as I couldn't find the real thing.
	/*
	 * case 3127://Burnt Jorge bones. case 3128://Pasty Jorge bones. case
	 * 3129://Pasty Jorge bones. case 3130://Marinated j' bones case
	 * 3131://Pasty jogre bones case 3132://Pasty jogre bones case
	 * 3133://Marinated j' bones return 15; /*End of those for a quest or w/e.
	 */
	/* Start of monkey bones (also for Monkey Madness) */
	// I'll give them all 5 experience, as I couldn't find the real thing.
	// (Probably is the correct thing though).
	/*
	 * case 3179://Monkey bones case 3180://Monkey bones case 3181://Monkey
	 * bones BIG ONE case 3182://Monkey bones BIG ONE case 3183://Monkey bones
	 * NORMAL ONE //case 3184://Monkey bones NOTED NORMAL ONE case 3185://Monkey
	 * bones case 3186://Monkey bones SMALL ONE return 5; /*End of monkey bones
	 * (also for Monkey Madness)
	 */
	/*
	 * case 3187://Regular
	 * bones?????????????????????????????????????????????????
	 * ??????????????????????????? return 4.5; case 4812://Zogre bones return
	 * 22.5; case 4830://Fayrg bones return 84; case 4832://Raurg bones return
	 * 96; case 4834://Ourg bones return 140; case 6729://Dagannoth bones return
	 * 125; case 6812://Wyvern bones return 50; } return 0; }
	 */

	/**
	 * This event is running every time a player is praying.
	 */
	private class PrayerEvent extends Event {

		/**
		 * Constructs the Prayer event, and makes it to run every 1800 Ms.
		 */
		public PrayerEvent() {
			super(600);
		}

		@Override
		public void execute() {
			if (!eventIsRunning) {
				reset();
				this.stop();
			}
			if (player.getInterfaceState().getCurrentInterface() == -1) {
				/*
				 * prayerDrain is set at 100, lowers by drainRate of all prayers
				 * being used
				 */
				prayerDrain -= drainRate;
				/*
				 * When prayerDrain becomes 0 or less, players prayer level is
				 * decreased by 1.
				 */
				if (prayerDrain <= 0
						&& player.getSkills().getLevel(Skills.PRAYER) > 0) {
					player.getSkills().decrementLevel(Skills.PRAYER);
					/*
					 * If the players prayer level becomes 0, it resets all
					 * prayers in use.
					 */
					if (player.getSkills().getLevel(5) <= 0) {
						reset();
						player.getActionSender()
								.sendMessage(
										"You have run out of prayer points; recharge your prayer points at an altar");
					}
					prayerDrain = 100;
				}
			}
			boolean stop = true;
			for (boolean bool : prayers) {
				if (bool) {
					stop = false;
				}
			}
			if (stop) {
				reset();
				this.stop();
			}
		}

	}
}
