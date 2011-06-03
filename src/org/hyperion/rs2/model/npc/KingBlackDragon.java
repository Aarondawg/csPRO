package org.hyperion.rs2.model.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.ProjectileManager;
import org.hyperion.rs2.content.skills.Prayer;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Equipment;

public class KingBlackDragon extends NPC {

	/**
	 * The animation when attacking with fire.
	 */
	private static final Animation DRAGON_FIRE = Animation.create(81);

	/**
	 * The array list of players we can attack..
	 */
	private List<Player> attackablePlayers = new ArrayList<Player>();

	/**
	 * The number of attacks.
	 */
	private static final int NUMBER_OF_ATTACKS = 4;

	/**
	 * Simple integers defining which attack that was randomly chosen.
	 */
	private static final int REGULAR_FIRE = 0;
	private static final int SHOCK_BREATH = 1;
	private static final int ICE_BREATH = 2;
	private static final int POISONOUS_FIRE = 3;
	private static final int MELEE_ATTACK = 4;

	/**
	 * Simple array holding the projectile IDs of the spells.
	 */
	private static final int[] PROJECTILE_IDS = { 393/* Red attack */, 396/*
																		 * Blue
																		 * attack
																		 */,
			395/* White attack */, 394 /* Green attack */};

	/**
	 * Our java.util.Random instance.
	 */
	private static final Random r = new Random();

	/**
	 * @see NPC
	 */
	public KingBlackDragon(NPCDefinition definition, Location location,
			Location minLocation, Location maxLocation) {
		super(definition, location, minLocation, maxLocation);
	}

	@Override
	public boolean getSpecialAttack(final Entity victim) {
		/*
		 * This have to return false, else the Melee attack wouldn't work.
		 */
		return false;
	}

	@Override
	public void handleAggressivity(final Player player) {
		/*
		 * Make sure we aren't adding a null to the list.
		 */
		if (player == null) {
			return;
		}
		/*
		 * We add the player to the list of the players we're attacking if he's
		 * not already in there.
		 */
		if (!attackablePlayers.contains(player)) {
			attackablePlayers.add(player);
			/*
			 * Moves the player again when 5 seconds passed away.
			 */
			World.getWorld().submit(new Event(5000) {

				@Override
				public void execute() {
					attackablePlayers.remove(player);
					this.stop();
				}

			});
			/*
			 * We start the attacking action (this will NOT run if there is
			 * already an action queuing) This is running once every 3000 ms,
			 * where all players are being looped through.
			 */
			this.getActionQueue().addAction(new Action(this, 0) {

				@Override
				public QueuePolicy getQueuePolicy() {
					return QueuePolicy.NEVER;
				}

				@Override
				public WalkablePolicy getWalkablePolicy() {
					return WalkablePolicy.NON_WALKABLE;
				}

				@Override
				public void execute() {
					final Player[] list = attackablePlayers
							.toArray(new Player[0]); // Randomize the list.
					final NPC npc = (NPC) getEntity();
					/*
					 * Make sure we have a min and a max location.
					 */
					if (getMinLocation() != null && getMaxLocation() != null) {
						/*
						 * We loop through the list of players..
						 */
						for (int i = 0; i < list.length; i++) {
							final int index = i;
							World.getWorld().submit(new Event(200 * i) {

								@Override
								public void execute() {
									final Player p = list[index];
									/*
									 * Check if we or the player is either a
									 * null or dead.
									 */
									if (npc == null || npc.isDead()
											|| p == null || p.isDead()) {
										if (p != null) {
											attackablePlayers.remove(p);
										} else {
											attackablePlayers.clear(); // Just
																		// incase..
										}
										this.stop();
										return;
									}
									/*
									 * Make sure the player is connected.
									 */
									if (!player.getSession().isConnected()) {
										attackablePlayers.remove(p);
										this.stop();
										return;
									}
									/*
									 * Makes sure the player is within our
									 * "walking area" (Even though the KBD is
									 * not walking)
									 */
									if (p.getLocation().isInArea(
											getMinLocation(), getMaxLocation())) {
										/*
										 * Check if the attacking delay is good.
										 */
										if (npc.getCombatDelay() <= 600) {
											System.out.println("Attacking: "
													+ player);
											/*
											 * If so, we face the player.
											 */
											npc.setInteractingEntity(p);
											/*
											 * If the player is within attacking
											 * distance, theres a slight chance
											 * of a melee attack..
											 */
											final int attacks = p.getLocation()
													.isWithinInteractingRange(
															p, npc, 1) ? NUMBER_OF_ATTACKS + 1
													: NUMBER_OF_ATTACKS;
											/*
											 * And we randomly choose our next
											 * attack..
											 */
											final int attack = r
													.nextInt(attacks);
											/*
											 * If its a regular melee attack,
											 * which can be handled through the
											 * normal attacking action, we do it
											 * before we send the projectile,
											 * add a delay, and do the action.
											 */
											if (attack == MELEE_ATTACK) {
												// npc.getActionQueue().addAction(new
												// AttackingAction(this, p));
												Combat.handleMeleeCombat(npc, p);
											} else {
												/*
												 * We make the dragon animate.
												 */
												playAnimation(DRAGON_FIRE);
												/*
												 * We get the projectile ID from
												 * an array and send the
												 * projectile.
												 */
												ProjectileManager.fire(npc, p,
														PROJECTILE_IDS[attack],
														50, 42, 26);
												/*
												 * Based on the projectile
												 * delay, we start an event and
												 * do the action the fire
												 * provides.
												 */
												World.getWorld()
														.submit(new Event(
																ProjectileManager
																		.magicHitDelay(
																				npc,
																				p)) {

															@Override
															public void execute() {
																int damage = 80;
																if (p.getPrayer()
																		.isPrayerToggled(
																				Prayer.PROTECT_FROM_MAGE)) {
																	damage /= 2;
																}
																boolean print = true;
																if (p.getEquipment()
																		.get(Equipment.SLOT_SHIELD) != null) {
																	// Check for
																	// anti-dragon
																	// shield,
																	// or dragon
																	// fire
																	// shield.
																	if (p.getEquipment()
																			.get(Equipment.SLOT_SHIELD)
																			.getId() == 1540
																			|| p.getEquipment()
																					.get(Equipment.SLOT_SHIELD)
																					.getId() == 11284) {
																		damage /= 7;
																		player.getActionSender()
																				.sendMessage(
																						"Your anti-dragonfire shield protected you from the dragons fire.");
																		print = false;
																	}
																}
																if (print) {
																	player.getActionSender()
																			.sendMessage(
																					damage >= 40 ? "You were burnt terribly in the dragons fire!"
																							: "You manage to resist some of the dragons fire.");
																}
																// TODO: Anti
																// fire potion
																// support.
																damage = r
																		.nextInt(damage);
																Combat.inflictDamage(
																		p, npc,
																		damage);
																/*
																 * If the damage
																 * is above 0,
																 * there is 50%
																 * chance of
																 * some effect
																 * to append.
																 */
																if (damage > 0
																		&& r.nextBoolean()) {
																	switch (attack) {
																	/*
																	 * Just
																	 * normal
																	 * dragon
																	 * fire..
																	 */
																	case REGULAR_FIRE:
																		// Nothing
																		// happens
																		// here?
																		// ;)
																		break;
																	/*
																	 * Makes the
																	 * players
																	 * stat
																	 * decrease?
																	 */
																	case SHOCK_BREATH:
																		/*
																		 * No
																		 * idea
																		 * about
																		 * the
																		 * actual
																		 * stats
																		 * ..
																		 */
																		int[] skills = {
																				Skills.ATTACK,
																				Skills.STRENGTH,
																				Skills.DEFENCE,
																				Skills.MAGIC,
																				Skills.RANGE };
																		/*
																		 * Picks
																		 * a
																		 * random
																		 * ..
																		 */
																		int skill = skills[r
																				.nextInt(skills.length)];
																		/*
																		 * Gets
																		 * 10%
																		 * of
																		 * the
																		 * players
																		 * actual
																		 * level
																		 * ..
																		 */
																		int amount = (int) (p
																				.getSkills()
																				.getLevelForExperience(
																						skill) * 0.10);
																		/*
																		 * Decreases
																		 * the
																		 * players
																		 * level
																		 * by
																		 * this
																		 * amount
																		 * .
																		 */
																		p.getSkills()
																				.detractLevel(
																						skill,
																						amount);
																		p.getActionSender()
																				.sendMessage(
																						"You feel like loosing some of your "
																								+ Skills.SKILL_NAME[skill]
																										.toLowerCase()
																								+ " ability.");
																		break;
																	/*
																	 * Makes the
																	 * player
																	 * unable to
																	 * move and
																	 * attack
																	 * for a
																	 * while.
																	 */
																	case ICE_BREATH:
																		if (!p.isFrozen()) {
																			/*
																			 * We
																			 * increase
																			 * the
																			 * players
																			 * combat
																			 * delay
																			 * with
																			 * 7
																			 * seconds
																			 * .
																			 */
																			player.increaseCombatDelay(7000);
																			/*
																			 * We
																			 * set
																			 * the
																			 * player
																			 * to
																			 * frozen
																			 * .
																			 * .
																			 */
																			p.setFrozen(true);
																			p.getActionSender()
																					.sendMessage(
																							"You have been frozen and feel unavailable to attack for a while.");
																			/*
																			 * We
																			 * make
																			 * him
																			 * able
																			 * to
																			 * move
																			 * again
																			 * when
																			 * 10
																			 * seconds
																			 * passed
																			 * .
																			 */
																			World.getWorld()
																					.submit(new Event(
																							10000) {

																						@Override
																						public void execute() {
																							p.setFrozen(false);
																							this.stop();
																						}

																					});
																		}
																		break;
																	/*
																	 * Chance to
																	 * poison
																	 * with 8.
																	 */
																	case POISONOUS_FIRE:
																		if (p.getPoison()
																				.getPoisonHit() == 0) {
																			p.getPoison()
																					.startPoison(
																							8);
																		}
																		break;
																	}

																}
																this.stop();
															}

														});

												/*
												 * We increase the combat delay
												 * slightly. This is
												 * un-noticeable unless we have
												 * more players fighting the
												 * dragon.
												 */
												npc.increaseCombatDelay(100 + r
														.nextInt(200));
											}
										}
										/*
										 * The player was not in the
										 * "walking area" and we remove the
										 * player from the ArrayList.
										 */
									} else {
										attackablePlayers.remove(p);
									}
									this.stop();
								}

							});

						}

					}
					if (attackablePlayers.isEmpty()) {
						this.stop();
					}
					this.setDelay(3000);
				}

			});

		}

	}

}
