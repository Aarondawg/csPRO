package org.hyperion.rs2.packet;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.action.impl.ProspectingAction;
import org.hyperion.rs2.action.impl.WoodcuttingAction;
import org.hyperion.rs2.action.impl.WoodcuttingAction.Tree;
import org.hyperion.rs2.action.impl.MiningAction;
import org.hyperion.rs2.action.impl.MiningAction.Node;
import org.hyperion.rs2.content.minigames.Barrows;
import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.content.quest.Quest;
import org.hyperion.rs2.content.quest.QuestHandler;
import org.hyperion.rs2.content.quest.impl.TutorialIsland;
import org.hyperion.rs2.content.skills.Cooking;
import org.hyperion.rs2.content.skills.Prayer;
import org.hyperion.rs2.content.skills.Runecrafting;
import org.hyperion.rs2.content.skills.Thieving;
import org.hyperion.rs2.content.skills.agility.Agility;
import org.hyperion.rs2.content.skills.construction.Construction;
import org.hyperion.rs2.content.skills.farming.Harvesting;
import org.hyperion.rs2.content.traveling.DoorsAndLadders;
import org.hyperion.rs2.content.traveling.Levers;
import org.hyperion.rs2.content.traveling.Obelisks;
import org.hyperion.rs2.content.traveling.ShipTraveling;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.GameObject;
import org.hyperion.rs2.model.GameObjectDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.model.region.Region;
import org.hyperion.rs2.net.Packet;

/**
 * Object option packet handler.
 * 
 * @author Graham Edgecombe
 * 
 */
public class ObjectOptionPacketHandler implements PacketHandler {

	/**
	 * Option 1 opcode.
	 */
	private static final int OPTION_1 = 64, OPTION_2 = 53, OPTION_3 = 58;

	@Override
	public void handle(Player player, Packet packet) {
		player.resetInteractingEntity();
		switch (packet.getOpcode()) {
		case OPTION_1:
			handleOption1(player, packet);
			break;
		case OPTION_2:
			handleOption2(player, packet);
			break;
		case OPTION_3:
			handleOption3(player, packet);
			break;
		}
	}

	/**
	 * Handles the option 1 packet.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleOption1(final Player player, Packet packet) {
		if (System.currentTimeMillis() - player.lastObjectClick < 800) {
			return;
		}
		final int y = packet.getLEShort() & 0xFFFF;
		final int x = packet.getShort() & 0xFFFF;
		final int id = packet.getShort() & 0xFFFF;
		final Location loc = Location.create(x, y, player.getLocation().getZ());
		System.out.println(loc + " " + id);
		player.face(loc.getActualLocation(GameObjectDefinition.forId(id)
				.getBiggestSize()));
		/*
		 * Duel forfeiting. Delay is done in the dueling class, because if
		 * there's no movement toggled, then there's no delay.
		 */
		if (id == 3203) {
			if (player.getRequestManager().isDueling()) {
				player.getRequestManager().getDuel().forfeit(player, loc);
			}
		}
		Region r = World.getWorld().getRegionManager().getRegionByLocation(loc);
		GameObject g = null;
		for (GameObject go : r.getGameObjects()) {
			if (go.getLocation().equals(loc)
					&& go.getDefinition().getId() == id) {
				g = go;
				break;
			}
		}
		final GameObject o = g == null ? new GameObject(
				GameObjectDefinition.forId(id), loc, 10, 0) : g;
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
				if (player.getLocation().withinRange(loc,
						GameObjectDefinition.forId(id).getBiggestSize())
						&& player.getInterfaceState().getCurrentInterface() == -1) {
					this.stop();
					for (Quest quest : QuestHandler.getQuests().values()) {
						boolean handle = quest.handleObjectClicking(player, id,
								loc, 1);
						if (handle) {
							return;
						}
					}
					if (Agility.handleObjectOption(player, loc, 1)) {
						return;
					}
					if (player.getConstruction()
							.handleObjectPackets(loc, id, 1)) {
						return;
					}
					if (Construction.handleConstructionObjects(player, loc, id)) {
						return;
					}
					if (player.getWarriorsGuild().handleDoorClick(loc)) {
						return;
					}
					if (PestControl.handleObjectClicking(player, loc, id, 1)) {
						return;
					}
					if (Barrows.clickObject(player, loc, id)) {
						return;
					}
					if (ShipTraveling.handleShipGangPlanks(player, loc, id)) {
						return;
					}
					if (Obelisks.handle(player, id, loc)) {
						return;
					}
					if (Levers.handle(player, loc, id)) {
						return;
					}
					if (Runecrafting.portalTele(player, id, x, y)) {
						return;
					}
					if (Runecrafting.handleAbyssObject(player, id)) {
						return;
					}
					// woodcutting
					Tree tree = Tree.forId(id);
					if (tree != null) {
						player.getActionQueue().addAction(
								new WoodcuttingAction(player, loc, tree, id));
						// player.lastObjectClick = System.currentTimeMillis();
						return;
					}
					// mining
					Node node = Node.forId(id);
					if (node != null) {
						player.getActionQueue().addAction(
								new MiningAction(player, loc, node, id));
						// player.lastObjectClick = System.currentTimeMillis();
						return;
					}
					/*
					 * Check for Zanaris door entrance..
					 */
					if (Location.isInArea(loc.getX(), loc.getY(), loc.getY(),
							2463, 4433, 0, 2470, 4440, 0)) {
						// TODO: If you're in the area, just walk out,
						// If not, pay, walk in.
					}
					if (DoorsAndLadders.handle(player, o, 1)) {
						return;
					}
					if (Cooking.checkForObjectClicking(player, id, loc)) {
						return;
					}
					/*
					 * Tutorial island gate
					 */
					if (loc.equals(Location.create(3089, 3091, 0))
							|| loc.equals(Location.create(3089, 3092, 0))) {
						player.setTeleportTarget(Location.create(3089, 3092, 0));
						int[][] info = player.getQuestInfo();
						final int stage = info[TutorialIsland.QUEST_INFO_INDEX][0];
						if (stage == 9) {
							player.editQuestInfo(
									TutorialIsland.QUEST_INFO_INDEX,
									TutorialIsland.MAIN_QUEST_STAGE_INDEX, 10);
							TutorialIsland.sendHeadIcon(player, 942);
						}
					}
					if (loc.getX() == 3088 && loc.getY() == 3119) {
						int[][] info = player.getQuestInfo();
						final int stage = info[TutorialIsland.QUEST_INFO_INDEX][0];
						if (stage >= 14) {
							player.setTeleportTarget(Location.create(3088,
									9520, 0));
							if (stage == 14) {
								World.getWorld().submit(new Event(1200) {

									@Override
									public void execute() {
										TutorialIsland
												.sendHeadIcon(player, 948);
										this.stop();
									}

								});
								player.editQuestInfo(
										TutorialIsland.QUEST_INFO_INDEX,
										TutorialIsland.MAIN_QUEST_STAGE_INDEX,
										15);
							}
						} else {
							player.getActionSender()
									.sendMessage(
											"Please follow the Tutorial Island as you're supposed to.");
						}
					}
					if (loc.getX() == 3094
							&& (loc.getY() == 9502 || loc.getY() == 9503)) {
						int[][] info = player.getQuestInfo();
						final int stage = info[TutorialIsland.QUEST_INFO_INDEX][0];
						if (stage == 22) {
							player.setTeleportTarget(Location.create(3095,
									loc.getY(), 0));
						}
					}
					switch (id) {
					case 3024: // Tutorial island door..
						if (player.getLocation().getX() == 3124
								&& player.getLocation().getY() == 3124) {
							player.setTeleportTarget(Location.create(3125,
									3124, 0));
						} else {
							player.setTeleportTarget(Location.create(3124,
									3124, 0));
						}
						break;
					case 3030: // Tutorial island ladder
						int[][] info = player.getQuestInfo();
						final int stage = info[TutorialIsland.QUEST_INFO_INDEX][0];
						if (loc.getX() == 3111 && loc.getY() == 9526
								&& stage == 24) {
							player.setTeleportTarget(Location.create(3111,
									3125, 0));
							World.getWorld().submit(new Event(1200) {

								@Override
								public void execute() {
									player.getActionSender().sendObjectHints(
											Location.create(3122, 3124, 50), 2);
									player.getActionSender()
											.sendMessage(
													"Check out your bank inside this building..");
									player.editQuestInfo(
											TutorialIsland.QUEST_INFO_INDEX,
											TutorialIsland.MAIN_QUEST_STAGE_INDEX,
											25);
									this.stop();

								}

							});
						}
						break;
					case 3044: // Tutorial Island furnace..
						player.getActionSender().sendChatboxInterface(311);
						player.getActionSender().sendInterfaceModel(311, 4,
								125, 2349);// Bronze bar.
						player.getActionSender().sendInterfaceModel(311, 5,
								125, 9467);// Blurite bar.
						player.getActionSender().sendInterfaceModel(311, 6,
								125, 2351);// Iron bar.
						player.getActionSender().sendInterfaceModel(311, 7,
								125, 2355);// Silver
						player.getActionSender().sendInterfaceModel(311, 8,
								125, 2353);// Steel bar.
						player.getActionSender().sendInterfaceModel(311, 9,
								125, 2357);// Gold bar.
						player.getActionSender().sendInterfaceModel(311, 10,
								125, 2359);// Mithril bar.
						player.getActionSender().sendInterfaceModel(311, 11,
								125, 2361);// Adamant bar.
						player.getActionSender().sendInterfaceModel(311, 12,
								125, 2363);// Rune bar.
						break;
					// Bank Chests
					case 4483:
					case 10562:
					case 14382:
					case 16695:
					case 16696:
					case 21301:
					case 3045: // Tutorial island bank.
						Bank.open(player);
						break;
					/*
					 * Duel arena bank.
					 */
					case 3193:
						Bank.open(player);
						// TODO: Object replacing (open/close chest).
						break;
					/*
					 * Volcano entrance
					 */
					case 492:
						player.setTeleportTarget(Location.create(2856, 9570, 0));
						player.getActionSender()
								.sendMessage(
										"You slide down the rope, and enter the Volcano.");
						break;
					/*
					 * Volcano exit..
					 */
					case 1764:
						player.setTeleportTarget(Location.create(2856, 3170, 0));
						player.getActionSender().sendMessage(
								"You climb the rope and exit the Volcano.");
						break;
					/*
					 * TzHaar cave entrance..
					 */
					case 9358:
						player.setTeleportTarget(Location.create(2480, 5175, 0));
						player.getActionSender().sendMessage(
								"You crawl into the cave..");
						break;
					/*
					 * TzHaar cave exit..
					 */
					case 9359:
						player.setTeleportTarget(Location.create(2862, 9572, 0));
						player.getActionSender().sendMessage(
								"You crawl exit the cave..");
						break;
					/*
					 * Fight cave entrance..
					 */
					case 9356:
						player.getFightCaves().enterFightCaves();
						break;
					/*
					 * Fight cave exit
					 */
					case 2:
						player.getFightCaves().exitCaves();
						break;

					/*
					 * Dwarven Mines Falador
					 */
					case 11867:
						player.setTeleportTarget(Location.create(3020, 9850, 0));
						break;
					case 1734:
						player.setTeleportTarget(Location.create(3061, 3376, 0));
						break;
					case 1733:
						player.setTeleportTarget(Location.create(3058, 9776, 0));
						break;

					case 1755:
						if (player.getLocation().getX() == 3097
								&& player.getLocation().getY() == 9868) {
							player.setTeleportTarget(Location.create(3096,
									3468, 0));// edgeville dungeon
						} else if (player.getLocation().getX() == 3020
								&& player.getLocation().getY() == 9850) {
							player.setTeleportTarget(Location.create(3018,
									3450, 0));// dwarven mines ladder
						} else if (player.getLocation().getX() == 3088
								&& player.getLocation().getY() == 9971) {
							player.setTeleportTarget(Location.create(3087,
									3571, 0));// ladder to wildy hill out of
												// edge
						} else if (player.getLocation().getX() == 3236
								&& player.getLocation().getY() == 9858) {
							player.setTeleportTarget(Location.create(3237,
									3458, 0)); // [3237,9858,0
						}
						break;
					/*
					 * Edgeville Dungeon
					 */
					case 1568:
						player.setTeleportTarget(Location.create(3097, 9868, 0));
						break;
					case 1567:
						player.setTeleportTarget(Location.create(3099, 3468, 0));
						break;
					case 1759:
						player.setTeleportTarget(Location.create(3087, 9971, 0));

						/*
						 * Varrock Sewer
						 */
					case 881:
						if (player.getLocation().getX() == 3236
								&& player.getLocation().getY() == 3458) {
							player.setTeleportTarget(Location.create(3236,
									9859, 0));// varock sewer
							// } else if (player.getLocation().getX() == 3088 &&
							// player.getLocation().getY() == 9971){
							// player.setTeleportTarget(Location.create(3019,3450,0));//dwarven
							// mines ladder
							// } else if(player.getLocation().getX() == 3088 &&
							// player.getLocation().getY() == 9971){
							// player.setTeleportTarget(Location.create(3087,3571,0));//ladder
							// to wildy hill out of edge
						}
						break;
					/*
					 * Prayer altars
					 */
					case 61:
					case 409:
					case 410:
					case 411:
					case 412:
					case 2640:
					case 4008:
					case 4090:
					case 4091:
					case 4092:
					case 4141:
					case 6552:
					case 8749:
					case 10639:
					case 10640:
					case 13179:
					case 13180:
					case 13181:
					case 13182:
					case 13184:
					case 13185:
					case 13186:
					case 13187:
					case 13188:
					case 13189:
					case 13190:
					case 13191:
					case 13192:
					case 13193:
					case 13194:
					case 13195:
					case 13196:
					case 13197:
					case 13198:
					case 13199:
						Prayer.altar(player, false);
						break;
					/*
					 * Special Alters: Please notice: The Prayer Guild alter is
					 * probably above.
					 */
					case 3521: // Alter of nature.
						Prayer.altar(player, true);
						break;
					default:
						System.out
								.println("Unhandled object option 1 with id: "
										+ id + " at loc: " + loc);
					}

					// Cannon.
					/*
					 * if(id == 6) { DwarfCannon.start(player, x, y); }
					 */
				}
				this.setDelay(600);
			}

		});
		player.lastObjectClick = System.currentTimeMillis();
	}

	/**
	 * Handles the option 2 packet.
	 * 
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleOption2(final Player player, Packet packet) {
		if (System.currentTimeMillis() - player.lastObjectClick < 800) {
			return;
		}
		final int id = packet.getLEShort() & 0xFFFF;
		System.out.println("objectOPtion2 " + id);
		final int y = packet.getShortA() & 0xFFFF;
		final int x = packet.getShortA() & 0xFFFF;
		final Location loc = Location.create(x, y, player.getLocation().getZ());
		player.face(loc);
		Region r = World.getWorld().getRegionManager().getRegionByLocation(loc);
		GameObject g = null;
		for (GameObject go : r.getGameObjects()) {
			if (go.getLocation().equals(loc)
					&& go.getDefinition().getId() == id) {
				g = go;
				break;
			}
		}
		final GameObject o = g == null ? new GameObject(
				GameObjectDefinition.forId(id), loc, 10, 0) : g;
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
				if (player.getLocation().withinRange(loc,
						GameObjectDefinition.forId(id).getBiggestSize())
						&& player.getInterfaceState().getCurrentInterface() == -1) {
					this.stop();
					for (Quest quest : QuestHandler.getQuests().values()) {
						boolean handle = quest.handleObjectClicking(player, id,
								loc, 2);
						if (handle) {
							return;
						}
					}
					if (Thieving.handleObjectClick(player, o)) {
						return;
					}
					if (Agility.handleObjectOption(player, loc, 2)) {
						return;
					}
					Node node = Node.forId(id);
					if (node != null) {
						player.getActionQueue().addAction(
								new ProspectingAction(player, loc, node));
						return;
					}
					if (Harvesting.checkForHarvesting(player, id, loc)) {
						return;
					}
					if (DoorsAndLadders.handle(player, o, 2)) {
						return;
					}
					switch (id) {
					/*
					 * POH portal.
					 */
					case 13405:
						player.getConstruction().getHouse().switchLockedStage();
						break;
					/*
					 * Bank booths.
					 */
					case 2213:
					case 5276:
					case 6084:
					case 10517:
					case 11338:
					case 11402:
					case 11758:
					case 12798:
					case 12799:
					case 12800:
					case 12801:
					case 14367:
					case 14368:
					case 16700:
					case 18491:
					case 19230:
					case 20325:
					case 20326:
					case 20327:
					case 20328:
					case 22819:
					case 24914:
					case 25808:
					case 26972:
						Bank.open(player);
						break;
					/*
					 * Spinning wheel.
					 */
					case 2644:
						player.getActionSender().sendInterface(459);
						break;

					/*
					 * Furnaces.
					 */
					case 2781:
					case 2785:
					case 2966:
					case 3044:
					case 3294:
					case 3413:
					case 4304:
					case 4305:
					case 6189:
					case 6190:
					case 11009:
					case 11010:
					case 11666:
					case 12100:
					case 12809:
						player.getActionSender().sendChatboxInterface(311);
						player.getActionSender().sendInterfaceModel(311, 4,
								125, 2349);// Bronze bar.
						player.getActionSender().sendInterfaceModel(311, 5,
								125, 9467);// Blurite bar.
						player.getActionSender().sendInterfaceModel(311, 6,
								125, 2351);// Iron bar.
						player.getActionSender().sendInterfaceModel(311, 7,
								125, 2355);// Silver
						player.getActionSender().sendInterfaceModel(311, 8,
								125, 2353);// Steel bar.
						player.getActionSender().sendInterfaceModel(311, 9,
								125, 2357);// Gold bar.
						player.getActionSender().sendInterfaceModel(311, 10,
								125, 2359);// Mithril bar.
						player.getActionSender().sendInterfaceModel(311, 11,
								125, 2361);// Adamant bar.
						player.getActionSender().sendInterfaceModel(311, 12,
								125, 2363);// Rune bar.
						break;
					default:
						System.out.println("Unhandled Object Option2: " + id
								+ ", x: " + x + ", y: " + y);
						break;
					}
					// Cannon.
					/*
					 * if(id == 6) { DwarfCannon.start(player, x, y); }
					 */
					this.stop();

				}
				this.setDelay(600);
			}

		});

		player.lastObjectClick = System.currentTimeMillis();
	}

	private void handleOption3(final Player player, Packet packet) {
		if (System.currentTimeMillis() - player.lastObjectClick < 800) {
			return;
		}
		player.lastObjectClick = System.currentTimeMillis();
		final int id = packet.getShort() & 0xFFFF;
		final int y = packet.getLEShort() & 0xFFFF;
		final int x = packet.getLEShort() & 0xFFFF;
		final Location loc = Location.create(x, y, player.getLocation().getZ());
		Region r = World.getWorld().getRegionManager().getRegionByLocation(loc);
		GameObject g = null;
		for (GameObject go : r.getGameObjects()) {
			if (go.getLocation().equals(loc)
					&& go.getDefinition().getId() == id) {
				g = go;
				break;
			}
		}
		final GameObject o = g == null ? new GameObject(
				GameObjectDefinition.forId(id), loc, 10, 0) : g;
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
				if (player.getLocation().withinRange(loc,
						GameObjectDefinition.forId(id).getBiggestSize())
						&& player.getInterfaceState().getCurrentInterface() == -1) {
					this.stop();
					for (Quest quest : QuestHandler.getQuests().values()) {
						boolean handle = quest.handleObjectClicking(player, id,
								loc, 3);
						if (handle) {
							return;
						}
					}
					if (Agility.handleObjectOption(player, loc, 3)) {
						return;
					}
					if (DoorsAndLadders.handle(player, o, 3)) {
						return;
					}
					System.out.println("Unhandled object option 3 with id: "
							+ id + " at: " + loc);
				}
				this.setDelay(600);
			}

		});
		player.lastObjectClick = System.currentTimeMillis();
	}

}
