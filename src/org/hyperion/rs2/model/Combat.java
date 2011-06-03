package org.hyperion.rs2.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.hyperion.rs2.action.impl.AttackingAction;
import org.hyperion.rs2.content.ProjectileManager;
import org.hyperion.rs2.content.minigames.WarriorsGuild;
import org.hyperion.rs2.content.skills.Prayer;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.GroundItemController;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.Damage.HitType;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.npc.fightcaves.TzTokJad;

/**
 * Handles the combat system.
 * 
 * @author Graham
 * 
 */
public class Combat {

	/*
	 * AttackStyle constants..
	 */
	public static final int ACCURATE = 0;
	public static final int RAPID = 1;
	public static final int LONGRANGE = 2;

	public static final int AGGRESSIVE = 1;
	public static final int DEFENSIVE = 2;
	public static final int CONTROLLED = 3;

	private static final Random r = new Random();

	/**
	 * Attack types.
	 */
	public static enum AttackType {
		/**
		 * Melee-based attacks.
		 */
		MELEE,

		/**
		 * Projectile-based attacks.
		 */
		RANGED,

		/**
		 * Magic-based attacks.
		 */
		MAGIC,
	}

	/**
	 * Called when an entity attacks another entity.
	 * 
	 * @param source
	 *            The source entity (attacker).
	 * @param victim
	 *            The victim entity (attacked).
	 */
	public static void attack(final Entity source, final Entity victim) {
		source.setInteractingEntity(victim);
		if (!canAttack(source, victim)) {
			source.getWalkingQueue().reset();
			return;
		}
		source.setInCombat(true);
		source.setAggressor(true);
		int dist = source.getAttackingDistance();
		if (source instanceof Player) {
			Player pSource = (Player) source;
			pSource.getActionSender().sendFollowing(victim);
			pSource.getActionSender().sendFollowingDistance(dist);

		}
		if (source.getLocation().isWithinInteractingRange(source, victim, dist)) {
			source.getWalkingQueue().reset();
		}
		source.getActionQueue().cancelQueuedActions();
		source.getActionQueue().addAction(new AttackingAction(source, victim));
	}

	/**
	 * Checks if an entity can attack another.
	 * 
	 * @param source
	 *            The source entity.
	 * @param victim
	 *            The target entity.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public static boolean canAttack(Entity source, Entity victim) {
		if ((source instanceof Player) && (victim instanceof Player)) {
			Player pSource = (Player) source;
			Player pVictim = (Player) victim;
			int wildy = Math.min(
					Location.getWildernessLevel(pSource.getLocation()),
					Location.getWildernessLevel(pVictim.getLocation()));
			int levelDiff = Math.abs(pSource.getSkills().getCombatLevel()
					- pVictim.getSkills().getCombatLevel());
			if (pSource.getLocation().isDueling()
					&& (pVictim.getLocation().isDueling())) {
				if (pSource.getRequestManager().isDueling()) {
					if (!pSource.getRequestManager().getDuel().canFight()) {
						pSource.getActionSender().sendMessage(
								"The duel hasn't started yet!");
						return false;
					}
					if (pSource.getRequestManager().getDuel().getOther(pSource) != pVictim) {
						pSource.getActionSender().sendMessage(
								"You can only attack your own opponment!");
						return false;
					}
					return true;
				} else {
					pSource.setTeleportTarget(Entity.DEFAULT_LOCATION);
					pSource.getActionSender().sendMessage(
							"Please contact Brown if this happened.");
				}

			}
			if (wildy < levelDiff) {
				pSource.getActionSender()
						.sendMessage(
								"You need to move deeper into the wilderness to attack this player.");
				return false;
			}
			if (!pSource.getLocation().isInWilderness()) {
				pSource.getActionSender().sendMessage(
						"You are not in the wilderness.");
				return false;
			}
			if (!pVictim.getLocation().isInWilderness()) {
				pSource.getActionSender().sendMessage(
						"The other player is not in wilderness.");
				return false;
			}
			if (!checkForAttackable(pSource, pVictim)) {
				return false;
			}
		} else {
			if (!checkForAttackable(source, victim)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the target or the attacker is in a multi-attacking area.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	private static boolean checkForAttackable(Entity source, Entity victim) {
		/*
		 * Is the victim in a multi area?
		 */
		if (!victim.getLocation().isInMulti()) {
			/*
			 * If not, we check if the victim has been hit recently..
			 */
			if (victim.isUnderAttack() && victim.getAttacker() != source) {
				if (source instanceof Player) {
					((Player) source)
							.getActionSender()
							.sendMessage(
									victim instanceof Player ? "That player is already under attack!"
											: "Someone else is already fighting that.");
				}
				return false;
			}
		}
		/*
		 * Is the attacker in a multi area?
		 */
		if (!source.getLocation().isInMulti()) {
			/*
			 * If not, we check if the attacker has been hit recently..
			 */
			if (source.isUnderAttack() && source.getAttacker() != victim) {
				if (source instanceof Player) {
					((Player) source).getActionSender().sendMessage(
							"I'm already under attack!");
				}
				return false;
			}

		}
		return true;
	}

	public static void handleRangedCombat(final Entity entity,
			final Entity victim) {
		int hitDelay = ProjectileManager.rangedHitDelay(entity, victim);
		entity.setInteractingEntity(victim);
		victim.setUnderAttack(entity);
		if (entity.getSpecialAttack(victim)) {
			return;
		}
		if (entity instanceof Player) {
			final Player p = (Player) entity;
			if (p.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
				if (p.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 11235) {
					darkBow(p, victim, false);
					return;
				}
			} else {
				entity.setInCombat(false);
				return;
			}
		}
		entity.playAnimation(Animation.create(entity.getAttackAnimation()));
		if (entity.getDrawBackGraphics() != -1) {
			entity.playGraphics(Graphic.create(entity.getDrawBackGraphics(),
					(100 << 16)));
		}
		ProjectileManager.fireRangedProjectile(entity, victim,
				entity.getProjectileId(), 50, 43, 31);
		World.getWorld().submit(new Event(hitDelay) {
			public void execute() {
				int hit = (int) (Math.ceil((double) Math.random()
						* (double) entity.getMaxHit()));
				int rangeAtk = getRandom(rangeAtk(false, entity));
				int rangeDef = getRandom(rangeDef(victim));
				if (rangeAtk <= rangeDef) {
					hit = 0;
				}
				if (entity instanceof Player) {
					Player p = (Player) entity;
					/*
					 * Random crossbow special effect
					 */
					System.out.println(p.getEquipment()
							.get(Equipment.SLOT_ARROWS).getDefinition()
							.getName());
					if (p.getEquipment().get(Equipment.SLOT_ARROWS)
							.getDefinition().getName().endsWith("(e)")
							&& r.nextInt(1) == 0) {
						switch (p.getEquipment().get(Equipment.SLOT_ARROWS)
								.getId()) {
						case 9236: // Opal bolts (e)
							hit *= 1.25;
							victim.playGraphics(Graphic.create(749));
							break;
						case 9237: // Jade bolts (e)
							victim.playGraphics(Graphic.create(755));
							// TODO: Knock the target to the ground? Lol.
							break;
						case 9238: // Pearl bolts (e)
							hit *= 1.15;
							victim.playGraphics(Graphic.create(750));
							// Hits harder on people made of fire.
							break;
						case 9239: // Topaz bolts (e)
							victim.playGraphics(Graphic.create(757));
							if (victim instanceof Player) {
								Player pVictim = (Player) victim;
								int amount = r.nextInt(5) + 1;
								pVictim.getSkills().detractLevel(Skills.MAGIC,
										amount);
							}
							break;
						case 9240: // Sapphire bolts (e)
							victim.playGraphics(Graphic.create(751));
							if (victim instanceof Player) {
								Player pVictim = (Player) victim;
								int amount = r.nextInt(5) + 1;
								pVictim.getSkills().detractLevel(Skills.PRAYER,
										amount);
								for (int i = 0; i < (int) (amount * 0.5); i++) {
									p.getSkills().incrementLevel(Skills.PRAYER);
								}
							}
							break;
						case 9241: // Emerald bolts (e)
							victim.playGraphics(Graphic.create(752));
							victim.getPoison().startPoison(5);
							break;
						case 9242: // Ruby bolts (e)
							hit = (int) (victim.getHitpoints() * 0.20);
							Combat.inflictDamage(entity, null,
									(int) (entity.getHitpoints() * 0.10));
							victim.playGraphics(Graphic.create(754));
							break;
						case 9243: // Diamond bolts (e)
							hit *= 1.15;
							victim.playGraphics(Graphic.create(758));
							break;
						case 9244: // Dragon bolts (e)
							// TODO: Actual dragon effect lul.
							hit *= 1.45;
							victim.playGraphics(Graphic.create(756));
							break;
						case 9245: // Onyx bolts (e)
							hit *= 1.15; // (Perhaps more?);
							victim.playGraphics(Graphic.create(753));
							entity.heal((int) (hit * 0.25), false);
							break;
						}
					}
				} else {
					/*
					 * At this stage, the entity can only be an NPC. Makes your
					 * char immune to NPC range, if you're using the prayer.
					 */
					if (victim instanceof Player) {
						Player p = (Player) victim;
						if (p.getPrayer().isPrayerToggled(
								Prayer.PROTECT_FROM_RANGE)) {
							hit = 0;
						}
					}
					if (entity instanceof TzTokJad) {
						TzTokJad jad = (TzTokJad) entity;
						victim.playGraphics(jad.getTargetGfx());
					}
				}
				victim.getPoison().checkForPoison(entity);
				inflictDamage(victim, entity, hit);

				if (victim instanceof Player) {
					Player p = (Player) victim;
					if (entity instanceof Player) {
						Player attacker = (Player) entity;
						if (attacker.getPrayer().isPrayerToggled(Prayer.SMITE)) {
							p.getSkills().setLevel(
									Skills.PRAYER,
									p.getSkills().getLevel(Skills.PRAYER)
											- (hit / 4));
							if (p.getSkills().getLevel(Skills.PRAYER) < 0) {
								p.getSkills().setLevel(Skills.PRAYER, 0);
							}
							p.getActionSender().sendSkill(Skills.PRAYER);
						}
					}
				}
				Item ammo = Equipment.removeAmmo(entity);
				if (ammo != null) {
					GroundItemController.createGroundItem(ammo, entity,
							victim.getLocation());
				}
				this.stop();
			}
		});

	}

	public static void vengeance(Entity victim, Entity entity, int hit) {
		if (victim.getMagic().hasVengeanceCasted() && hit > 0) {
			hit -= hit / 4;
			inflictDamage(entity, null, hit);
			victim.forceChat("Taste vengeance!");
			victim.getMagic().setVengeanceCasted(false);
		}
	}

	private static final Animation RESET = Animation.create(-1);

	/**
	 * Called every tick. Handles the combat.
	 * 
	 * @param entity
	 *            The entity whose combat we are handling.
	 */
	public static void handleMeleeCombat(final Entity entity,
			final Entity victim) {
		entity.setInteractingEntity(victim);
		victim.setUnderAttack(entity);
		if (entity.getSpecialAttack(victim)) {
			return;
		}
		if (entity.isAnimating()) {
			entity.playAnimation(RESET);
		}
		entity.playAnimation(Animation.create(entity.getAttackAnimation()));
		World.getWorld().submit(new Event(350) { // Slight delay before the hit.
					public void execute() {
						int hit = (int) (Math.ceil(Math.random()
								* entity.getMaxHit()));
						int meleeAtk = getRandom(meleeAtk(false, entity));
						int meleeDef = getRandom(meleeDef(victim));
						if (meleeAtk <= meleeDef) {
							hit = 0;
						}
						entity.getPoison().checkForPoison(victim);
						/*
						 * Makes your char immune to NPC melee, if you're using
						 * the prayer.
						 */
						if (entity instanceof NPC) {
							NPC n = (NPC) entity;
							if (n.getDefinition().getId() != 2030) { // Verac
																		// the
																		// Defiled
								if (victim instanceof Player) {
									Player p = (Player) victim;
									if (p.getPrayer().isPrayerToggled(
											Prayer.PROTECT_FROM_MELEE)) {
										hit = 0;
									}
								}
							}

						}
						inflictDamage(victim, entity, hit);

						if (victim instanceof Player) {
							Player p = (Player) victim;
							if (p.getPrayer().isPrayerToggled(
									Prayer.RETRIBUTION)) {
								// p.getPrayer().setInCombatTargetIndex(entity.getIndex());
							}
							if (entity instanceof Player) {
								Player attacker = (Player) entity;
								if (attacker.getPrayer().isPrayerToggled(
										Prayer.SMITE)) {
									p.getSkills().setLevel(
											Skills.PRAYER,
											p.getSkills().getLevel(
													Skills.PRAYER)
													- (hit / 4));
									if (p.getSkills().getLevel(Skills.PRAYER) < 0) {
										p.getSkills()
												.setLevel(Skills.PRAYER, 0);
									}
									p.getActionSender()
											.sendSkill(Skills.PRAYER);
								}
							}
						}
						this.stop();
					}
				});
	}

	public static int meleeDef(Entity e) {
		int chance = 0;
		if (e instanceof Player) {
			Player p = (Player) e;
			int defBonus = p.getBonuses().getBonus(bestmeleeDef(p));
			int playerDef = p.getSkills().getLevel(Skills.DEFENCE);
			int maxDef = p.getSkills().getLevelForExperience(Skills.DEFENCE);
			if (p.getPrayer().isPrayerToggled(Prayer.THICK_SKIN))
				playerDef += (double) maxDef * 0.05;
			else if (p.getPrayer().isPrayerToggled(Prayer.ROCK_SKIN))
				playerDef += (double) maxDef * 0.10;
			else if (p.getPrayer().isPrayerToggled(Prayer.STEEL_SKIN))
				playerDef += (double) maxDef * 0.15;
			chance += (int) ((double) playerDef + (double) playerDef * 0.15 + ((double) defBonus + (double) defBonus * 0.05));
			if (p.getPrayer().isPrayerToggled(Prayer.PROTECT_FROM_MELEE)) {
				chance *= 2;
			}
			return chance;
		} else {
			NPC n = (NPC) e;
			chance = n.getDefinition().getDefenceMelee();
		}
		return chance;
	}

	public static int rangeDef(Entity e) {
		int chance = 0;
		if (e instanceof Player) {
			Player p = (Player) e;
			int rangeBonus = p.getBonuses().getBonus(9);
			int rangeLevel = p.getSkills().getLevel(Skills.DEFENCE);
			int maxDef = p.getSkills().getLevelForExperience(Skills.DEFENCE);

			if (p.getPrayer().isPrayerToggled(Prayer.THICK_SKIN))
				rangeLevel += (double) maxDef * 0.05;
			else if (p.getPrayer().isPrayerToggled(Prayer.ROCK_SKIN))
				rangeLevel += (double) maxDef * 0.10;
			else if (p.getPrayer().isPrayerToggled(Prayer.STEEL_SKIN))
				rangeLevel += (double) maxDef * 0.15;
			chance += (int) ((double) rangeLevel + (double) rangeLevel * 0.15 + ((double) rangeBonus + (double) rangeBonus * 0.05));
			if (p.getPrayer().isPrayerToggled(Prayer.PROTECT_FROM_RANGE)) {
				chance *= 2;
			}
			return chance;
		} else {
			NPC n = (NPC) e;
			chance = n.getDefinition().getDefenceRange();
		}
		return chance;
	}

	public static int mageDef(Entity e) {
		int chance = 0;
		if (e instanceof Player) {
			Player p = (Player) e;
			int mageDefBonus = p.getBonuses().getBonus(8);
			int mageDef = p.getSkills().getLevel(Skills.DEFENCE) / 2
					+ p.getSkills().getLevel(Skills.MAGIC) / 2;
			int maxMage = (p.getSkills().getLevelForExperience(Skills.DEFENCE) / 2 + p
					.getSkills().getLevelForExperience(Skills.MAGIC) / 2);
			if (p.getPrayer().isPrayerToggled(Prayer.THICK_SKIN))
				mageDef += (double) maxMage * 0.05;
			else if (p.getPrayer().isPrayerToggled(Prayer.ROCK_SKIN))
				mageDef += (double) maxMage * 0.10;
			else if (p.getPrayer().isPrayerToggled(Prayer.STEEL_SKIN))
				mageDef += (double) maxMage * 0.15;
			chance += (int) ((double) mageDef + (double) mageDef * 0.15 + ((double) mageDefBonus + (double) mageDefBonus * 0.05));
			if (p.getPrayer().isPrayerToggled(Prayer.PROTECT_FROM_MAGE)) {
				chance *= 2;
			}
			return chance;
		} else {
			NPC n = (NPC) e;
			chance = n.getDefinition().getDefenceMage();
		}
		return chance;
	}

	public static int meleeAtk(boolean usingSpecial, Entity e) {
		int chance = 0;
		if (e instanceof Player) {
			Player p = (Player) e;
			int attBonus = p.getBonuses().getBonus(bestmeleeAtk(p));
			int attLevel = p.getSkills().getLevel(Skills.ATTACK);
			int maxAtt = p.getSkills().getLevelForExperience(Skills.ATTACK);
			if (p.getPrayer().isPrayerToggled(Prayer.ATTACK_LOW))
				attLevel += (double) maxAtt * 0.05;
			else if (p.getPrayer().isPrayerToggled(Prayer.ATTACK_MID))
				attLevel += (double) maxAtt * 0.10;
			else if (p.getPrayer().isPrayerToggled(Prayer.ATTACK_HIGH))
				attLevel += (double) maxAtt * 0.15;
			/*
			 * if (fullMeVoidEquipped()) attLevel += (double) maxAtt * 0.1;
			 */
			if (usingSpecial) {
				switch (p.getEquipment().get(Equipment.SLOT_WEAPON).getId()) {
				case 1215: // Dragon dagger
				case 1231: // Dragon dagger(p)
				case 5680: // Dragon dagger(p+)
				case 5698: // Drag dagger(p++)
					attLevel += (double) maxAtt * 0.10;
					break;
				case 4151: // Whip
					attLevel += (double) maxAtt * 0.10;
					break;
				case 4587: // Dragon scimitar
					attLevel += (double) maxAtt * 0.08;
					break;
				case 1434:// Dragon mace
					attLevel -= (double) maxAtt * 0.20;
					break;
				}
			}
			chance += (int) ((double) attLevel + (double) attLevel * 0.15 + ((double) attBonus + (double) attBonus * 0.05));
		} else {
			NPC n = (NPC) e;
			chance = n.getDefinition().getAttackBonus();
		}
		return chance;
	}

	public static int rangeAtk(boolean specialAttack, Entity e) {
		int chance = 0;
		if (e instanceof Player) {
			Player p = (Player) e;
			int rangeBonus = p.getBonuses().getBonus(4);
			int rangeLvl = p.getSkills().getLevel(Skills.RANGE);
			int maxRange = p.getSkills().getLevelForExperience(Skills.RANGE);
			if (p.getPrayer().isPrayerToggled(Prayer.SHARP_EYE)) {
				rangeLvl += (double) maxRange * 0.05;
			} else if (p.getPrayer().isPrayerToggled(Prayer.HAWK_EYE)) {
				rangeLvl += (double) maxRange * 0.10;
			} else if (p.getPrayer().isPrayerToggled(Prayer.EAGLE_EYE)) {
				rangeLvl += (double) maxRange * 0.15;
			}
			if (specialAttack) {
				switch (p.getEquipment().get(Equipment.SLOT_WEAPON).getId()) {
				case 859:
					rangeLvl += (double) maxRange * 0.1;
					break;
				case 861:
					rangeLvl -= (double) maxRange * 0.05;
					break;
				}
			}
			chance += (int) ((double) rangeLvl + (double) rangeLvl * 0.15 + ((double) rangeBonus + (double) rangeBonus * 0.05));
			return chance;
		} else {
			NPC n = (NPC) e;
			chance = n.getDefinition().getAttackBonus();
		}
		return chance;
	}

	public static int mageAtk(Entity e) {
		int chance = 0;
		if (e instanceof Player) {
			Player p = (Player) e;
			int mageBonus = p.getBonuses().getBonus(3);
			int mageLvl = p.getSkills().getLevel(Skills.MAGIC);
			int maxMage = p.getSkills().getLevelForExperience(Skills.MAGIC);

			/*
			 * if (fullAhrimsEquipped()) mageLvl += (double) maxMage * 0.05;
			 */
			if (p.getPrayer().isPrayerToggled(Prayer.MAGE_LOW))
				mageLvl += (double) maxMage * 0.05;
			if (p.getPrayer().isPrayerToggled(Prayer.MAGE_MID))
				mageLvl += (double) maxMage * 0.10;
			if (p.getPrayer().isPrayerToggled(Prayer.MAGE_HIGH))
				mageLvl += (double) maxMage * 0.15;
			/*
			 * if (fullMaVoidEquipped()) mageLvl += (double) maxMage * 0.2; if
			 * (spellCastedId == 12037) chance += (int) ((double) mageLvl +
			 * (double) mageLvl * 0.15 + ((double) mageLvl + (double) mageLvl *
			 * 0.05)); else
			 */
			if (mageBonus < 0) {
				chance += (int) ((double) mageLvl + (double) mageLvl * 0.15 + ((double) mageBonus + (double) mageBonus * 0.40));
				return chance;
			}
			chance += (int) ((double) mageLvl + (double) mageLvl * 0.15 + ((double) mageBonus + (double) mageBonus * 0.07));
			return chance;
		} else {
			NPC n = (NPC) e;
			chance = n.getDefinition().getAttackBonus();
		}
		return chance;
	}

	public static int bestmeleeAtk(Entity e) {
		if (e instanceof Player) {
			Player p = (Player) e;
			if (p.getBonuses().getBonus(0) > p.getBonuses().getBonus(1)
					&& p.getBonuses().getBonus(0) > p.getBonuses().getBonus(2))
				return 0;
			if (p.getBonuses().getBonus(1) > p.getBonuses().getBonus(0)
					&& p.getBonuses().getBonus(1) > p.getBonuses().getBonus(2))
				return 1;
			if (p.getBonuses().getBonus(2) > p.getBonuses().getBonus(1)
					&& p.getBonuses().getBonus(2) > p.getBonuses().getBonus(0))
				return 2;
		}
		return 0;
	}

	public static int bestmeleeDef(Entity e) {
		if (e instanceof Player) {
			Player p = (Player) e;
			if (p.getBonuses().getBonus(5) > p.getBonuses().getBonus(6)
					&& p.getBonuses().getBonus(5) > p.getBonuses().getBonus(7))
				return 5;
			if (p.getBonuses().getBonus(6) > p.getBonuses().getBonus(5)
					&& p.getBonuses().getBonus(6) > p.getBonuses().getBonus(7))
				return 6;
			if (p.getBonuses().getBonus(7) > p.getBonuses().getBonus(5)
					&& p.getBonuses().getBonus(7) > p.getBonuses().getBonus(6))
				return 7;
		}
		return 5;
	}

	/**
	 * Inflicts damage on the recipient.
	 * 
	 * @param recipient
	 *            The entity taking the damage.
	 * @param damage
	 *            The damage to be done.
	 */
	public static void inflictDamage(Entity recipient, Entity aggressor,
			Hit damage) {
		recipient.inflictDamage(damage, aggressor);
		if (!recipient.isAnimating()) {
			recipient.playAnimation(Animation.create(recipient
					.getDefenceAnimation()));
		}
	}

	/**
	 * Inflicts damage on the recipient. (Use this for Ranged and Melee)
	 * 
	 * @param recipient
	 *            The entity taking the damage.
	 * @param damage
	 *            The damage to be done.
	 */
	public static void inflictDamage(Entity recipient, Entity aggressor,
			int damage) {
		if (damage >= recipient.getHitpoints()) {
			damage = recipient.getHitpoints();
		}
		if (aggressor != null) {
			recipient.addKillerHit(aggressor, damage);
		}
		inflictDamage(recipient, aggressor, new Hit(damage,
				damage == 0 ? HitType.NO_DAMAGE : HitType.NORMAL_DAMAGE));
	}

	/**
	 * Inflicts damage on the recipient. (Use this for Magic)
	 * 
	 * @param recipient
	 *            The entity taking the damage.
	 * @param damage
	 *            The damage to be done.
	 */
	public static void inflictMagicDamage(Entity recipient, Entity aggressor,
			int damage) {
		if (damage >= recipient.getHitpoints()) {
			damage = recipient.getHitpoints();
		}
		if (damage == 0) {
			return;
		}
		inflictDamage(recipient, aggressor, new Hit(damage,
				damage == 0 ? HitType.NO_DAMAGE : HitType.NORMAL_DAMAGE));
	}

	public static int getRandom(int range) {
		return (int) (java.lang.Math.random() * (range + 1));
	}

	public static class CombatSession {
		private int damage = 0;
		private long timestamp = 0;

		public CombatSession() {

		}

		public int getDamage() {
			return this.damage;
		}
	}

	/**
	 * Represents an instance of combat, where Entity is an assailant and
	 * Integer is the sum of their damage done. This is mapped to every victim
	 * in combat, and used to determine drops.
	 * 
	 * @author Brett Russell
	 */
	public static class CollectiveCombatSession {
		private long stamp;
		private Map<Entity, CombatSession> damageMap;
		private Set<Entity> names = damageMap.keySet();
		private boolean isActive;
		private Entity victim;

		public CollectiveCombatSession(Entity victim) {
			java.util.Date date = new java.util.Date();
			this.stamp = date.getTime();
			this.isActive = true;
			this.victim = victim;
		}

		/**
		 * Gets the timestamp for this object (when the session began).
		 * 
		 * @return The timestamp.
		 */
		public long getStamp() {
			return stamp;
		}

		/**
		 * Gets the entity with the highest damage count this session.
		 * 
		 * @return The entity with the highest damage count.
		 */
		public Entity getTopDamage() {
			Entity top = null;
			int damageDone = 0;
			int currentHighest = 0;

			Iterator<Entity> itr = names.iterator();

			while (itr.hasNext()) {
				Entity currentEntity = itr.next();
				damageDone = damageMap.get(currentEntity).getDamage();
				if (damageDone > currentHighest) {
					currentHighest = damageDone;
					top = currentEntity;
				}
			}
			return top;
		}

		/**
		 * Returns the Map of this session's participants. If you would want it,
		 * that is...
		 * 
		 * @return A Map of the participants and their damage done.
		 */
		public Map<Entity, CombatSession> getDamageCharts() {
			return damageMap;
		}

		/**
		 * Adds a participant to this session.
		 * 
		 * @param participant
		 *            The participant to add.
		 */
		public void addParticipant(Entity participant) {
			// TODO CombatSession
			damageMap.put(participant, null);
		}

		/**
		 * Remove a participant.
		 * 
		 * @param participant
		 *            The participant to remove.
		 */
		public void removeParticipant(Entity participant) {
			damageMap.remove(participant);
		}

		/**
		 * Sets this sessions active state.
		 * 
		 * @param state
		 *            A <code>boolean</code> value representing the state.
		 */
		public void setState(boolean b) {
			this.isActive = b;
		}

		/**
		 * Determine the active state of this session.
		 * 
		 * @return The active state as a <code>boolean</code> value.
		 */
		public boolean getIsActive() {
			return this.isActive;
		}
	}

	/**
	 * Get the attackers' weapon speed.
	 * 
	 * @param player
	 *            The player for whose weapon we are getting the speed value.
	 * @return A <code>double</code>-type value of the weapon speed.
	 */
	public static int getAttackSpeed(Item weapon) {
		if (weapon == null) {
			return 2400;// fist or foot
		}
		int amount = 0; // 6000 if nothing was found.
		String name = weapon.getDefinition().getName();
		if (name.contains("dagger")) { // Contains, because they might be
										// poisonous.
			amount = 6;
		} else if (name.endsWith("longsword")) {
			amount = 5;
		} else if (name.endsWith("2h sword") || weapon.getId() == 4153 /*
																		 * Granite
																		 * maul.
																		 */
				|| weapon.getId() == 10887 /* Barrelchest anchor */) {
			amount = 3;
		} else if (name.endsWith("sword")) {// This has to be the last one, else
											// the other ones will return as
											// swords.
			amount = 6;
		} else if (name.endsWith("scimitar")) {
			amount = 6;
		} else if (name.endsWith("mace")) {
			amount = 5;
		} else if (name.startsWith("Dharoks")) {// Dharoks great axe, all of
												// them. Before the axes.
			amount = 3;
		} else if (name.endsWith("battleaxe")) {// Before the axe, else the axe
												// would return.
			amount = 4;
		} else if (name.endsWith("axe")) { // Also pickaxes and thrown axes. ;)
			amount = 5;
		} else if (name.endsWith("warhammer")) {
			amount = 4;
		} else if (name.contains("spear")) { // Contains, because they might be
												// poisonous. Guthans spear
												// aswell.
			amount = 5;
		} else if (name.endsWith("claw")) {
			amount = 6;
		} else if (name.endsWith("halberd")) {
			amount = 3;
			/*
			 * Start of TzHaar items.
			 */
		} else if (name.equals("Toktz-xil-ak") || name.equals("TokTz-Xil-Ek")) {// Sword
																				// and
																				// knife.
			amount = 6;
		} else if (name.equals("Tzhaar-ket-em")) {// Mace
			amount = 5;
		} else if (name.equals("Toktz-mej-tal")) {// Staff
			amount = 4;
		} else if (name.equals("Tzhaar-ket-om")) {// Maul
			amount = 3;
		} else if (name.equals("Toktz-xil-ul")) {// Rings

			/*
			 * End of them.
			 */
		} else if (name.equals("Abyssal whip")) {
			amount = 6;
		} else if (name.startsWith("Torags hammer")
				|| name.startsWith("Veracs flail")) {
			amount = 5;
		} else if (name.contains("staff")
				&& ((weapon.getId() <= 1410)
						|| (weapon.getId() >= 3053 && weapon.getId() <= 3056) || (weapon
						.getId() >= 6562 && weapon.getId() <= 6727))) { // With
																		// this
																		// attack
																		// delay.
			amount = 5;
		} else if (weapon.getId() == 2415 || weapon.getId() == 2416
				|| weapon.getId() == 2417 || weapon.getId() == 4170
				|| weapon.getId() == 4675) { // God staves, slayer and ahrims
												// staff.
			amount = 6;
		} else if (name.startsWith("Ahrims staff")) {
			amount = 4;
		} else if (name.contains("ongbow")) {
			amount = 4;
		} else if (name.contains("hortbow")) {
			amount = 6;
		} else if (name.contains("comp bow") || name.contains("Seercull")
				|| name.contains("rystal bow") /*
												 * Left out the c, as its both
												 * upper and lower cased.
												 */) {
			amount = 5;
		} else if (name.contains("ogre")) {
			if (name.startsWith("Comp")) {
				amount = 5;
			} else {
				amount = 2;
			}
		} else if (name.equals("Dark bow")) {
			amount = 1;
		} else if (name.contains("c'bow")) {// Also Dorgeshuun.
			amount = 4;
		} else if ((name.equals("Hunters' crossbow"))
				|| (name.startsWith("Karils") && name.contains("bow"))) {
			amount = 6;
		} else if (name.contains("knife") || name.contains("dart")) {
			amount = 7;
		} else if (name.contains("javelin")) {
			amount = 4;
		}
		if (amount == 0) {
			System.out
					.println("Missing weapon speed for the following weapon: "
							+ name + ".");
		}
		return ((10 - amount) * 600);
	}

	public static void darkBow(final Player player, final Entity victim,
			final boolean specialAttack) {
		final boolean dragonArrows = (player.getEquipment()
				.get(Equipment.SLOT_ARROWS).getId() == 11212);
		player.playAnimation(Animation.create(426));
		if (specialAttack) {
			player.playGraphics(Graphic.create(dragonArrows ? 1114 : 1112,
					(102 << 16)));
		} else {
			player.playGraphics(Graphic.create(
					Equipment.getDarkbowDrawbackGraphics(player), (102 << 16)));
		}
		final int projectile = specialAttack ? dragonArrows ? 1099 : 1102
				: player.getProjectileId();
		ProjectileManager.fireDBowArrows(player, victim, projectile,
				dragonArrows);
		World.getWorld().submit(
				new Event(ProjectileManager.rangedHitDelay(player, victim)) {

					boolean firstRound = true;

					@Override
					public void execute() {
						int hit = (int) (Math.ceil((double) Math.random()
								* (double) player.getMaxHit()));
						if (specialAttack) {
							double increaseWith = 1 + (dragonArrows ? 0.5 : 0.3);
							hit *= increaseWith;
						}
						int rangeAtk = getRandom(rangeAtk(true, player));
						int rangeDef = getRandom(rangeDef(victim));
						if (rangeAtk <= rangeDef) {
							hit = 0;
						}
						if (specialAttack) {
							if (dragonArrows) {
								if (hit < 8) {
									hit = 8;
								}
							} else {
								if (hit < 5) {
									hit = 5;
								}
							}
							// hit += dragonArrows ? 8 : 5;
							victim.playGraphics(Graphic.create(1100,
									(102 << 16)));
						}

						victim.getPoison().checkForPoison(player);

						Combat.inflictDamage(victim, player, hit);
						Item ammoDrop = Equipment.removeAmmo(player);
						if (ammoDrop != null) {
							GroundItemController.createGroundItem(ammoDrop,
									player, victim.getLocation());
						}
						if (firstRound
								&& player.getEquipment().get(
										Equipment.SLOT_ARROWS) != null) {
							firstRound = false;
							this.setDelay(600);
						} else {
							this.stop();
						}

					}

				});

	}

	public static boolean canAttackNPC(Player player, NPC npc) {
		if (WarriorsGuild.isMetalArmour(npc.getDefinition().getId())) {
			if (player.getWarriorsGuild().getCurrentArmour() != npc) {
				return false;
			}
		}
		switch (npc.getDefinition().getId()) {
		/*
		 * Cyclopes from Warriors Guild.
		 */
		case 4291:
		case 4292:
			if (!WarriorsGuild.IN_GAME.contains(player)) {
				return false;
			}
			break;
		/*
		 * Barrows brothers.
		 */
		case 2025:
		case 2026:
		case 2027:
		case 2028:
		case 2029:
		case 2030:
			if (!player.containsPrivateNPC(npc)) {
				return false;
			}
			break;
		/*
		 * Sprit tree.
		 */
		case 655:
			Object temp = player.getTemporaryAttribute("Sprit Tree");
			if (temp == null || temp != npc) {
				return false;
			}
			break;
		}
		return true;
	}

}
