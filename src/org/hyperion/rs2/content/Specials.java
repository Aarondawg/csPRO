package org.hyperion.rs2.content;

import java.util.Random;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.GroundItemController;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Duel;
import org.hyperion.rs2.model.container.Equipment;

public class Specials {

	private transient Player player;
	private transient boolean active = false;
	private boolean specialWeapon;
	private int amount = 1000;

	public Specials(Player player) {
		this.player = player;
		this.active = false;
	}

	/**
	 * Sets the special amount.
	 * 
	 * @param amt
	 *            The new amount.
	 */
	public void setAmount(int amt) {
		amount = amt;
		refresh();
	}

	/**
	 * Called every 30 seconds.
	 */
	public void tick() {
		boolean update = false;
		if (amount < 1000) {
			amount += 100;
			update = true;
		}
		if (amount > 1000) {
			amount = 1000;
			update = true;
		}
		if (update) {
			refresh();
		}
	}

	/**
	 * Sends the special amount. Sends the active/inactive config, based on
	 * whenever or not our special is active.
	 */
	public void refresh() {
		player.getActionSender().sendConfig(300, amount);
		if (active) {
			player.getActionSender().sendConfig(301, 1);
			Item currentWeapon = player.getEquipment().get(
					Equipment.SLOT_WEAPON);
			/*
			 * This is where we hardcode all the instant specials.
			 */
			if (currentWeapon != null) {
				switch (currentWeapon.getId()) {
				case 4153: // Granite Maul.
					if (player.isInCombat()) {
						perform(player.getInteractingEntity());
					} else {
						player.setCombatDelay(0);
					}
					break;
				case 1377:// Dragon battleaxe.
					int req = getRequiredEnergy(1377);
					if (amount >= req) {// This is the case, or else we wouldn't
										// be here.
						amount -= req;
						refresh();
						player.playAnimation(Animation.create(1056));
						player.playGraphics(Graphic.create(246));
						player.forceChat("Raarrrrrgggggghhhhhhh!");
						int boost = (int) (player.getSkills()
								.getLevelForExperience(Skills.STRENGTH) * 0.20);
						int newStat = (player.getSkills().getLevel(
								Skills.STRENGTH) + boost);
						if (newStat > (player.getSkills()
								.getLevelForExperience(Skills.STRENGTH) + boost)) {
							newStat = (player.getSkills()
									.getLevelForExperience(Skills.STRENGTH) + boost);
						}
						player.getSkills().setLevel(Skills.STRENGTH, newStat);
						int[] stats = { Skills.ATTACK, Skills.DEFENCE,
								Skills.MAGIC, Skills.RANGE };
						for (int stat : stats) {
							int dec = (int) (player.getSkills()
									.getLevelForExperience(stat) * 0.20);
							newStat = (player.getSkills().getLevel(stat) - dec);
							if (newStat > (player.getSkills()
									.getLevelForExperience(stat) - dec)) {
								newStat = (player.getSkills()
										.getLevelForExperience(stat) - dec);
							}
							player.getSkills().setLevel(stat, newStat);
						}
						setActive(false);
					} else {
						return;
					}
					break;
				}
			}

		} else {
			player.getActionSender().sendConfig(301, 0);
		}
	}

	/**
	 * Defines if the special is active or not.
	 * 
	 * @return True if, false if not.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the current special weapon to active or inactive.
	 * 
	 * @param b
	 *            The active or not boolean.
	 */
	public void setActive(boolean b) {
		if (player.getRequestManager().isDueling()) {
			if (player.getRequestManager().getDuel()
					.isRuleToggled(Duel.NO_SPECIAL_ATTACKS)) {
				player.getActionSender().sendMessage(
						"Special attacks has been disabled for this duel!");
				return;
			}
		}
		if (b && !canPerform()) {
			player.getActionSender().sendMessage(
					"You don't have enough special energy left.");
			b = false;
		}
		active = b;
		refresh();
	}

	/**
	 * Defines if we can perform a special attack.
	 * 
	 * @return True if, false if not.
	 */
	public boolean canPerform() {
		return player.getEquipment().get(3) != null
				&& specialWeapon
				&& amount >= getRequiredEnergy(player.getEquipment().get(3)
						.getId());
	}

	private static final Random r = new Random();

	/**
	 * Performs a special attack.
	 */
	public void perform(final Entity victim) {
		if (!(player.getEquipment().get(3) != null && specialWeapon)) {
			return;
		}
		int id = player.getEquipment().get(3).getId();
		int req = getRequiredEnergy(id);
		if (amount >= req) {
			amount -= req;
		} else {
			return;
		}
		int hitDelay = 350; // In milliseconds TODO: Calculate correct time
		active = false;
		refresh();
		double factor = 1;
		int customHit = -1;
		int customHit2 = -1;
		boolean dmg2 = false;
		switch (id) {

		case 7158: // Dragon 2h sword
			player.playAnimation(Animation.create(3157));
			player.playGraphics(Graphic.create(559));
			// TODO: Multi attack
			break;

		case 3101: // Rune claws
			player.playAnimation(Animation.create(435));
			player.playGraphics(Graphic.create(274, (100 << 16)));
			break;

		case 6739: // Dragon axe
			player.playGraphics(Graphic.create(479, (100 << 16)));
			player.playAnimation(Animation.create(2876));
			break;

		case 1249: // Dragon spear
		case 1263: // Dragon spear(p)
		case 3176: // Dragon spear(kp)
		case 5716: // Dragon spear(p+)
		case 5730: // Dragon spear(p++)
			player.playAnimation(Animation.create(1064));
			player.playGraphics(Graphic.create(253, (100 << 16)));

			victim.playGraphics(Graphic.create(254));
			// victim.playAnimation(Animation.create(victim.getDefenceAnimation());
			// TODO: Moving stuff
			break;
		case 4153: // Granite maul
			player.playGraphics(Graphic.create(340));
			player.playAnimation(Animation.create(1667));
			hitDelay = 0;
			// player.increaseCombatDelay(player.getAttackSpeed());
			// dmg2 = true;
			break;
		case 10887: // Barrelchest anchor
			player.playGraphics(Graphic.create(1027));
			player.playAnimation(Animation.create(5870));
			if (victim instanceof Player) {
				Player pVictim = (Player) victim;
				int[] skills = { Skills.ATTACK, Skills.DEFENCE, Skills.MAGIC,
						Skills.RANGE };
				int skill = r.nextInt(skills.length);
				int amount = (int) (pVictim.getSkills().getLevelForExperience(
						skill) * 0.10);
				pVictim.getSkills().detractLevel(skill, amount);
			}
			break;
		case 4151: // Abyssal whip
			factor = 0.9;
			player.playAnimation(Animation.create(1658));
			victim.playGraphics(Graphic.create(341, (100 << 16)));
			break;
		case 1305: // Dragon longsword
			factor = 1;
			player.playAnimation(Animation.create(1058));
			player.playGraphics(Graphic.create(248, (100 << 16)));
			break;
		case 4587: // Dragon scimitar
			factor = 1;
			player.playGraphics(Graphic.create(347, (100 << 16)));
			player.playAnimation(Animation.create(1872));
			// TODO: Injury stuff
			break;
		case 1434: // Dragon mace
			factor = 1.2;
			player.playAnimation(Animation.create(1060));
			player.playGraphics(Graphic.create(251, (100 << 16)));
			break;
		case 3204: // Dragon halberd
			factor = 1.1;
			player.playAnimation(Animation.create(1203));
			player.playGraphics(Graphic.create(282, (100 << 16)));

			/*
			 * if(victim instanceof NPC) { NPC npc = (NPC) victim;
			 * if(npc.getSize() > 1) dmg2 = true; }
			 */
			break;
		case 1215: // Dragon dagger
		case 1231: // Dragon dagger(p)
		case 5680: // Dragon dagger(p+)
		case 5698: // Drag dagger(p++)
			player.playAnimation(Animation.create(1062));
			player.playGraphics(Graphic.create(252, (100 << 16)));
			factor = 1.10;
			dmg2 = true;
			break;
		case 861: // Magic shortbow.
			player.playAnimation(Animation.create(1074));
			player.playGraphics(Graphic.create(250, (100 << 16)));
			World.getWorld().submit(new Event(0) {

				boolean firstRound = true;

				@Override
				public void execute() {
					ProjectileManager.fireRangedProjectile(player, victim, 249,
							50, 43, 31);
					World.getWorld().submit(
							new Event(ProjectileManager.rangedHitDelay(player,
									victim)) {

								@Override
								public void execute() {
									int hit = (int) (Math.ceil((double) Math
											.random()
											* (double) player.getMaxHit()));

									int rangeAtk = Combat.getRandom(Combat
											.rangeAtk(true, player));
									int rangeDef = Combat.getRandom(Combat
											.rangeDef(victim));
									if (rangeAtk <= rangeDef) {
										hit = 0;
									}

									victim.getPoison().checkForPoison(player);

									Combat.inflictDamage(victim, player, hit);
									Item ammoDrop = Equipment
											.removeAmmo(player);
									if (ammoDrop != null) {
										GroundItemController.createGroundItem(
												ammoDrop, player,
												victim.getLocation());
									}
									this.stop();
								}

							});
					if (firstRound
							&& player.getEquipment().get(Equipment.SLOT_ARROWS) != null) {
						firstRound = false;
						this.setDelay(300);
					} else {
						this.stop();
					}
				}
			});
			return;
		case 11235: // Dark bow.
			Combat.darkBow(player, victim, true);
			return;
		}

		final int customHitH = customHit;
		final int customHitH2 = customHit2;
		final double factorH = factor;
		final boolean dmg2H = dmg2;

		World.getWorld().submit(new Event(hitDelay) {
			public void execute() {
				int hit = 0;
				int hit2 = 0;
				if (customHitH != -1) {
					hit = customHitH;
				} else {
					hit = (int) (Math.ceil(Math.random() * player.getMaxHit()
							* factorH));
				}
				int meleeAtk = Combat.getRandom(Combat.meleeAtk(true, player));
				int meleeDef = Combat.getRandom(Combat.meleeDef(victim));
				if (meleeAtk <= meleeDef)
					hit = 0;
				Combat.inflictDamage(victim, player, hit);
				victim.addKillerHit(player, hit);
				if (dmg2H) {
					if (!victim.isDead()) {
						if (customHitH2 != -1) {
							hit2 = customHitH2;
						} else {
							hit2 = (int) (Math.ceil(Math.random()
									* player.getMaxHit() * factorH));
						}
						Combat.inflictDamage(victim, player, hit2);
					}
				}
				this.stop();
			}
		});
	}

	private int getRequiredEnergy(int id) {
		switch (id) {
		case 1377: // Dragon battleaxe.
			return 1000;
		case 7158: // Dragon 2h sword
			return 600;
		case 3101: // Rune claws
			return 250;
		case 6739: // Dragon axe
			return 1000;
		case 1249: // Dragon spear
		case 1263: // Dragon spear(p)
		case 3176: // Dragon spear(kp)
		case 5716: // Dragon spear(p+)
		case 5730: // Dragon spear(p++)
			return 250;
		case 4153: // Granite maul
			return 500;
		case 10887: // Barrelchest anchor
			return 500;
		case 4151: // Abyssal whip
			return 500;
		case 1305: // Dragon longsword
			return 250;
		case 4587: // Dragon scimitar
			return 600;
		case 1434: // Dragon mace
			return 250;
		case 3204: // Dragon halberd
			return 330;
		case 1215: // Dragon dagger
		case 1231: // Dragon dagger(p)
		case 5680: // Dragon dagger(p+)
		case 5698: // Drag dagger(p++)
			return 250;
		case 861: // Mage shortbow
			return 550;
		case 11235: // Dark bow.
			return 650;
		default:
			return 1000;
		}
	}

	public void setSpecialWeapon(boolean b) {
		this.specialWeapon = b;
	}

}
