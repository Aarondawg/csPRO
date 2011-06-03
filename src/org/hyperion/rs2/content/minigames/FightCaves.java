package org.hyperion.rs2.content.minigames;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.content.DialogueLoader;
import org.hyperion.rs2.content.DialogueLoader.Emotes;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Combat;
import org.hyperion.rs2.model.GroundItemController;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.npc.fightcaves.FightCaveMonster;
import org.hyperion.rs2.model.npc.fightcaves.TzKek;
import org.hyperion.rs2.model.npc.fightcaves.TzTokJad;
import org.hyperion.rs2.model.npc.fightcaves.YtHurKot;

public class FightCaves {

	public static final List<Player> IN_CAVES = new ArrayList<Player>();

	public static final Location maxLocation = Location.create(2431, 5119, 0);

	public static final Location minLocation = Location.create(2368, 5056, 0);

	public static Location OUTSIDE = Location.create(2438, 5168, 0);

	public FightCaves(Player player) {
		this.player = player;
	}

	private static final Random r = new Random();

	/**
	 * @Author NoobScape.
	 */
	public void decryptNextWave() {
		int waveId = wave;
		System.out.println("Wave: " + waveId);
		if (waveId == 63) {
			// Here comes TzTok Jad!
		}
		int[] mobs = new int[6];
		int mobsAdded = 0;
		if (waveId / 63 > 0) {
			for (int i = 0; i <= waveId / 63; i++) {
				mobs[mobsAdded++] = 2745;
				waveId -= 63;
			}
		}
		if (waveId / 31 > 0) {
			for (int i = 0; i <= waveId / 31; i++) {
				mobs[mobsAdded++] = 2743 + r.nextInt(2);
				waveId -= 31;
			}
		}
		if (waveId / 15 > 0) {
			for (int i = 0; i <= waveId / 15; i++) {
				mobs[mobsAdded++] = 2741 + r.nextInt(2);
				waveId -= 15;
			}
		}
		if (waveId / 7 > 0) {
			for (int i = 0; i <= waveId / 7; i++) {
				mobs[mobsAdded++] = 2739 + r.nextInt(2);
				waveId -= 7;
			}
		}
		if (waveId / 3 > 0) {
			for (int i = 0; i <= waveId / 3; i++) {
				mobs[mobsAdded++] = 2736 + r.nextInt(2);
				waveId -= 3;
			}
		}
		if (waveId > 0) {
			for (int i = 0; i <= waveId; i++) {
				mobs[mobsAdded++] = 2734 + r.nextInt(2);
				waveId--;
			}
		}
		for (int npc : mobs) {
			if (npc != 0) {
				FightCaveMonster monster = FightCaveMonster.create(player, npc);
				monsters.add(monster);
				World.getWorld().register(monster);
				Combat.attack(monster, player);
			}
		}
	}

	public void spawnHealers(TzTokJad jad) {
		for (int i = 0; i < 4; i++) {
			YtHurKot healer = new YtHurKot(player, NPCDefinition.forId(2746),
					FightCaveMonster.getRandomLocation(player, 2746));
			monsters.add(healer);
			World.getWorld().register(healer);
			healer.heal(jad);
		}

	}

	public List<FightCaveMonster> getMonsters() {
		return monsters;
	}

	public void destroy() {
		IN_CAVES.remove(player);
		for (NPC npc : monsters) {
			npc.setInvisible(true);
			World.getWorld().unregister(npc);
		}
	}

	public void remove(FightCaveMonster npc) {
		if (IN_CAVES.contains(player)) {
			if (monsters.remove(npc)) {
				World.getWorld().unregister(npc);
				/*
				 * We check if its a big Tz-Kek..
				 */
				if (npc.getDefinition().getCombat() == 45) {
					/*
					 * And spawn the two smaller ones..
					 */
					for (int i = 0; i < 2; i++) {
						TzKek monster = new TzKek(player,
								NPCDefinition.forId(2738), Location.create(npc
										.getLocation().getX() + i, npc
										.getLocation().getY(), npc
										.getLocation().getZ()));
						monsters.add(monster);
						World.getWorld().register(monster);
						Combat.attack(monster, player);
					}
				}
				if (npc instanceof TzTokJad) {
					finish();
				} else if (monsters.size() == 0) {
					wave++;
					if (logoutFlag) {
						player.getActionSender().sendLogout(true);
						return;
					}
					World.getWorld().submit(new Event(1200) {

						@Override
						public void execute() {
							decryptNextWave();
							this.stop();
						}

					});

				}
			}
		}
	}

	private static final Item FIRE_CAPE = new Item(6570);

	private void finish() {
		if (IN_CAVES.contains(player)) {
			player.setNextDialogueIds(new int[] { -1 });
			DialogueLoader
					.dialogue(
							player,
							new NPC(NPCDefinition.forId(2617)),
							Emotes.DEFAULT,
							new String[] {
									"You even defeated TzTok-Jad, I am most impressed!",
									"Please accept this gift as a reward." });
			Item tokkul = new Item(6529, 8032); // Correct amount according to
												// Brian
			if (!player.getInventory().add(tokkul)) {
				GroundItemController.createGroundItem(tokkul, player,
						player.getLocation());
			}
			if (!player.getInventory().add(FIRE_CAPE)) {
				GroundItemController.createGroundItem(FIRE_CAPE, player,
						player.getLocation());
			}
			player.setTeleportTarget(OUTSIDE);
			IN_CAVES.remove(player);
		}

	}

	// 13 = 364 //338 - Missing 24
	// 63 = 8032 // 7938 - Missing 94

	public boolean exitCaves() {
		if (IN_CAVES.contains(player)) {
			if (monsters.size() > 0) {
				for (NPC npc : monsters) {
					npc.setInvisible(true);
					World.getWorld().unregister(npc);
				}
				monsters.clear();
			}
			Item tokens = new Item(6529, getTokenAmount());
			if (!player.getInventory().add(tokens)) {
				GroundItemController.createGroundItem(tokens, player,
						player.getLocation());
			}
			player.setNextDialogueIds(new int[] { -1 });
			DialogueLoader
					.dialogue(
							player,
							new NPC(NPCDefinition.forId(2617)),
							Emotes.DEFAULT,
							new String[] { "Well done in the cave, here, take TokKul as reward." });
			wave = 1;
			player.setTeleportTarget(FightCaves.OUTSIDE);
			IN_CAVES.remove(player);
			return true;
		} else {
			return false;
		}
	}

	public int getTokenAmount() {
		return (int) Math.round(wave * wave * 2); // Almost perfect.
	}

	/**
	 * This method is called if the player logs out (and back in) while being in
	 * the fight caves..
	 * 
	 * @return The players current location, but with a different height level.
	 */
	public Location getLoginLocation() {
		/*
		 * We wait sligthly, to make sure the sendMapRegion packet have been
		 * transfered, and wait till the players currently open interfaceId is
		 * -1. (The Welcome Screen interface should be closed before we do
		 * anything)
		 */
		if (!IN_CAVES.contains(player)) { // This method is also called if we
											// try to teleport. xD
			IN_CAVES.add(player);
			World.getWorld().submit(new Event(300) {

				@Override
				public void execute() {
					if (player.getInterfaceState().getCurrentInterface() == -1) {
						player.getActionSender().animateInterfaceId(
								Emotes.DEFAULT.getId(), 246, 0);
						player.getActionSender().sendNPCHead(2617, 246, 0);
						player.getActionSender().sendString(
								NPCDefinition.forId(2617).getName(), 246, 1);
						player.getActionSender()
								.sendString(
										"You're on your own JalYt, prepare to fight for",
										246, 2);
						player.getActionSender().sendString("your life!", 246,
								3);
						player.getActionSender().sendChatboxInterface(246);
						World.getWorld().submit(new Event(1200) {

							@Override
							public void execute() {
								decryptNextWave();
								this.stop();
							}

						});
						this.stop();
					}
				}

			});
		}
		return player.getLocation(); // ;)
	}

	/**
	 * Called when a player attempts to logout.. (Whenever or not its in the
	 * fight caves)
	 */
	public boolean logoutAttempt() {
		if (IN_CAVES.contains(player)) {
			if (monsters.isEmpty()) {
				return true;
			} else {
				if (logoutFlag) {
					return true;
				} else {
					player.getActionSender()
							.sendMessage(
									"<col=FF0000>You will be logged out automaticly at the end of this wave.");
					player.getActionSender()
							.sendMessage(
									"<col=FF0000>If you logout sooner, you will have to repeat this wave.");
					logoutFlag = true;
					return false;
				}
			}
		} else {
			return true;
		}
	}

	public void enterFightCaves() {
		player.setTeleportTarget(Location.create(2413, 5117, 0));
		player.setCanWalk(false);
		/*
		 * We make sure we actually teleported (takes between 0 and 600 ms)
		 * before we send the Dialogue and walk towards the center etc.
		 */
		player.getActionQueue().addAction(new Action(player, 0) {

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
				if (player.getLocation().isInFightCaves()) {
					this.stop();
					wave = 1;
					logoutFlag = false;
					IN_CAVES.add(player);
					player.getActionSender().sendNPCHead(2617, 246, 0);
					player.getActionSender().animateInterfaceId(
							Emotes.DEFAULT.getId(), 246, 0);
					player.getActionSender().sendString(
							NPCDefinition.forId(2617).getName(), 246, 1);
					player.getActionSender().sendString(
							"You're on your own JalYt, prepare to fight for",
							246, 2);
					player.getActionSender().sendString("your life!", 246, 3);
					player.getActionSender().sendChatboxInterface(246);
					player.getWalkingQueue().reset();
					player.getWalkingQueue().addStep(2404, 5106); // Approximatly
																	// the
																	// center?
					player.getWalkingQueue().addStep(2404, 5104);
					player.getWalkingQueue().addStep(2398, 5088);
					player.getWalkingQueue().finish();
					decryptNextWave();
					World.getWorld().submit(new Event(4000) {

						@Override
						public void execute() {
							player.setCanWalk(true);
							this.stop();
						}

					});
				}
				this.setDelay(600);
			}

		});
	}

	private final List<FightCaveMonster> monsters = new ArrayList<FightCaveMonster>();
	private final Player player;
	private int wave = 1;
	private boolean logoutFlag;

}
