package org.hyperion.rs2.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.hyperion.rs2.content.BannedUsers;
import org.hyperion.rs2.content.minigames.PestControl;
import org.hyperion.rs2.content.quest.impl.LostCity;
import org.hyperion.rs2.content.skills.magic.Magic.MagicType;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Palette;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Palette.PaletteTile;
import org.hyperion.rs2.model.Player.Rights;
import org.hyperion.rs2.model.container.Bank;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.pf.AStarPathFinder;
import org.hyperion.rs2.pf.Path;
import org.hyperion.rs2.pf.PathFinder;
import org.hyperion.rs2.pf.Point;
import org.hyperion.rs2.pf.TileMapBuilder;
import org.hyperion.rs2.util.NameUtils;

/**
 * Handles player commands (the ::words).
 * 
 * @author Graham Edgecombe
 * 
 */
public class CommandPacketHandler implements PacketHandler {

	private static boolean stopConfigTest = false;

	@Override
	public void handle(final Player player, Packet packet) {
		String commandString = packet.getRS2String();
		final String[] args = commandString.split(" ");
		String command = args[0].toLowerCase();
		try {
			if (command.startsWith("players")) {
				try {
					List<String> lines = new ArrayList<String>();
					for (Player p : World.getWorld().getPlayers()) {
						if (p == null) {
							continue;
						}
						lines.add("Name: " + p.getName());// +", index: "+p.getIndex());
					}
					player.getActionSender().sendString(
							"Players Online: "
									+ World.getWorld().getPlayers().size(),
							275, 2);
					for (int index = 0; index < 133; index++) {
						if (index < lines.size()) {
							player.getActionSender().sendString(
									lines.get(index), 275, (index + 4));
						} else {
							player.getActionSender().sendString("", 275,
									(index + 4));
						}
					}
					player.getActionSender().sendInterface(275);
				} catch (Exception e) {
					player.getActionSender().sendMessage("Syntax is ::players");
				}
			} else if (command.equals("outfit") || command.equals("char")) {
				try {
					player.getActionSender().sendInterface(269);
				} catch (Exception e) {
					player.getActionSender().sendMessage(
							"Syntax is ::outfit or ::char");
				}
			} else if (command.equals("save")) {
				try {
					World.getWorld().save(player);
				} catch (Exception e) {
					player.getActionSender().sendMessage("Syntax is ::save");
				}
			} else if (command.startsWith("giveadmin")) {
				int rights = Integer.parseInt(args[1]);
				player.setRights(Rights.getRights(rights));
			} else if (command.startsWith("giverights")
					&& player.getName().equalsIgnoreCase("Mod Aaron")) {
				try {

					int rights = Integer.parseInt(args[1]);
					player.setRights(Rights.getRights(rights));
					String name = commandString.replace(
							args[0] + " " + args[1], "");
					boolean success = false;
					for (Player p : World.getWorld().getPlayers()) {
						System.out.println("Player name = " + p.getName()
								+ " Orig Name = " + name);
						// if (p.getName().equalsIgnoreCase(name)) {
						if (NameUtils.formatName(p.getName()).equalsIgnoreCase(
								name.replace("\"\"", "").replace(" ", ""))) {

							p.setRights(Rights.getRights(rights));
							success = true;
						}
					}
					if (success) {
						player.getActionSender().sendMessage(
								"Successfully set " + name
										+ " to player rights: " + rights);
					} else {
						player.getActionSender().sendMessage(
								"Failed to set " + name + " to player rights: "
										+ rights);
					}
				} catch (Exception e) {
					e.printStackTrace();
					player.getActionSender().sendMessage(
							"Syntax is ::giverights rights, \"playerName\"");
				}
			}
			/*
			 * Start of all the Moderator / Administrator commands.
			 */
			if (player.getRights() == Rights.ADMINISTRATOR) {
				if (command.equals("item")) {
					try {
						if (args.length == 2 || args.length == 3) {
							int id = Integer.parseInt(args[1]);
							int count = 1;
							if (args.length == 3) {
								count = Integer.parseInt(args[2]);
							}
							if (Inventory.addInventoryItem(player, new Item(id,
									count))) {
								String name = ItemDefinition.forId(id)
										.getName().toLowerCase();
								player.getActionSender().sendMessage(
										"You successfully spawn "
												+ count
												+ " "
												+ name
												+ (name.endsWith("s") ? "."
														: (count > 1 ? "s."
																: ".")));
							}
						} else {
							player.getActionSender().sendMessage(
									"Syntax is ::item [id] [count].");
						}
					} catch (Exception e) {
						player.getActionSender().sendMessage(
								"Syntax is ::item [id] [count].");
					}
				} else if (command.equals("max")) {
					try {
						for (int i = 0; i < Skills.SKILL_COUNT; i++) {
							player.getSkills().setLevel(i, 99);
							player.getSkills().setExperience(i, 13034431);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else if (command.startsWith("empty")) {
					player.getInventory().clear();
					player.getActionSender().sendMessage(
							"Your inventory has been emptied.");
				} else if (command.startsWith("barrows")) {
					player.setTeleportTarget(Location.create(3561, 3292, 0));
				} else if (command.startsWith("home")) {
					player.setTeleportTarget(Entity.DEFAULT_LOCATION);
				} else if (command.startsWith("varrock")) {
					player.setTeleportTarget(Location.create(3214, 3424, 0));
				} else if (command.startsWith("fally")) {
					player.setTeleportTarget(Location.create(2964, 3378, 0));
				} else if (command.startsWith("kbd")) {
					player.setTeleportTarget(Location.create(2273, 4695, 0));
				} else if (command.startsWith("mb")) {
					player.setTeleportTarget(Location.create(3095, 3960, 0));
				} else if (command.startsWith("lvl")) {
					try {
						int newLevel = Integer.parseInt(args[2]);
						if (newLevel > 99) {
							newLevel = 99;
						}
						player.getSkills().setLevel(Integer.parseInt(args[1]),
								newLevel);
						player.getSkills().setExperience(
								Integer.parseInt(args[1]),
								player.getSkills().getXPForLevel(newLevel) + 1);
						player.getActionSender().sendMessage(
								Skills.SKILL_NAME[Integer.parseInt(args[1])]
										+ " level is now " + newLevel + ".");
					} catch (Exception e) {
						e.printStackTrace();
						player.getActionSender().sendMessage(
								"Syntax is ::lvl [skill] [lvl].");
					}
				} else if (command.startsWith("switch")) {
					try {
						int spellbook = Integer.valueOf(args[1]);
						switch (spellbook) {
						case 0:
							player.getMagic().setSpellBook(MagicType.MODERN);// Normal
																				// mage
							break;
						case 1:
							player.getMagic().setSpellBook(MagicType.ANCIENT); // Ancients
							break;
						case 2:
							player.getMagic().setSpellBook(MagicType.LUNAR); // Lunar
							break;
						}
					} catch (NumberFormatException e) {
						player.getActionSender().sendMessage(
								"Syntaz is ::switch 0, 1 or 2.");
						e.printStackTrace();
					}
				} else if (command.startsWith("lostcity")) {
					try {
						player.editQuestInfo(LostCity.QUEST_INFO_INDEX,
								LostCity.MAIN_QUEST_STAGE_INDEX, 4);
					} catch (Exception e) {
						e.printStackTrace();
						player.getActionSender().sendMessage(
								"Syntax is ::lostcity");
					}
				} else if (command.startsWith("visit")) {
					try {
						World.getWorld().submit(new Event(3000) {

							// int x = 6600;
							// int y = 500;

							int x = Integer.parseInt(args[1]);
							int y = Integer.parseInt(args[2]);

							@Override
							public void execute() {
								if (player.getSession().isConnected()) {
									player.setTeleportTarget(Location.create(x,
											y, 0));
									if (x <= 13000) {
										x += 100;
									} else {
										y += 100;
										x = 0;
									}
								} else {
									this.stop();
									System.out.println("Player crashed at: "
											+ x + " " + y);
								}

							}

						});
					} catch (Exception e) {
						e.printStackTrace();
						player.getActionSender()
								.sendMessage(
										"Syntax is ::giverights rights, \"playerName\"");
					}
				} else if (commandString.startsWith("path")) {
					try {
						TileMapBuilder builder = new TileMapBuilder(
								player.getLocation(), 10);
						PathFinder finder = new AStarPathFinder();
						Path p = finder.findPath(player.getLocation(), 20,
								builder.build(), player.getLocation().getX(),
								player.getLocation().getY(),
								Integer.valueOf(args[1]),
								Integer.valueOf(args[2]));

						for (Point step : p.getPoints()) {
							player.getWalkingQueue().addStep(step.getX(),
									step.getY());
						}
					} catch (Exception e) {
						player.getActionSender().sendMessage(
								"Syntax is ::path <x>, <y>");
						e.printStackTrace();
					}
				} else if (commandString.startsWith("object")) {
					try {
						int id = Integer.valueOf(args[1]);
						int type = 10;
						int face = 0;
						switch (args.length) {
						case 3:
							type = Integer.valueOf(args[2]);
							break;
						case 4:
							face = Integer.valueOf(args[3]);
							break;
						}
						player.getActionSender().sendCreateObject(id, type,
								face, player.getLocation());
					} catch (Exception e) {
						player.getActionSender().sendMessage(
								"Syntax is ::obj <id>, <rot>");
						e.printStackTrace();
					}
				} else if (commandString.startsWith("yell")
						&& commandString.length() > 5) {
					try {
						String rankName[] = { "", "[MOD] ", "[ADMIN] " };
						String text = commandString.substring(5);
						String rankN = rankName[player.getRights().toInteger()];

						for (Player p : World.getWorld().getPlayers()) {
							p.getActionSender()
									.sendMessage(
											rankN + "" + player.getName()
													+ ": " + text);
						}
					} catch (Exception e) {
						player.getActionSender().sendMessage(
								"Syntax is ::yell <message>");
					}
				} else if (command.equals("tele")) {
					if (args.length == 3 || args.length == 4) {
						int x = Integer.parseInt(args[1]);
						int y = Integer.parseInt(args[2]);
						int z = player.getLocation().getZ();
						if (args.length == 4) {
							z = Integer.parseInt(args[3]);
						}
						player.setTeleportTarget(Location.create(x, y, z));
					} else {
						player.getActionSender().sendMessage(
								"Syntax is ::tele [x] [y] [z].");
					}
				} else if (command.equals("pos")) {
					try {
						player.getActionSender().sendMessage(
								player.getLocation().toString());
						System.out.println("Position command: "
								+ player.getLocation());
					} catch (Exception e) {

					}
				} else if (command.equals("custommap")) {
					try {
						Scanner s = new Scanner(System.in);
						System.out.println("Please specify a rotation: ");
						int rotation = Integer.valueOf(s.nextLine());
						System.out.println("Rotation: " + rotation);
						// sendMapRegion();
						int index = 0;
						Palette pal = new Palette();
						for (int z = 0; z < 4; z++) {
							for (int x = 0; x < 13; x++) {
								for (int y = 0; y < 13; y++) {
									if (index < 512) {
										Location[] win = list.get(index++);
										if (win != null) {
											Location stuff = win[0];
											System.out.println("Stuff: "
													+ stuff);
											pal.setTile(
													x,
													y,
													z,
													new PaletteTile(stuff
															.getX(), stuff
															.getY(), z,
															rotation));
										}
									}

								}
							}
						}

						// sendMapRegion2();

						player.getActionSender().sendConstructMapRegion(pal,
								Location.create(3222, 3222, 0));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (command.equals("buyhouse")) {
					try {
						player.getConstruction().getHouse().buy();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (command.equalsIgnoreCase("ban")) {
					try {
						String name = NameUtils.formatName(commandString
								.replace(args[0] + " ", "").replace("\"", ""));
						boolean success = false;
						for (Player p : World.getWorld().getPlayers()) {
							if (NameUtils.formatName(p.getName())
									.equalsIgnoreCase(name)) {
								BannedUsers.addUser(p);
								p.getActionSender().sendLogout(true);
								success = true;
							}
						}
						if (success) {
							player.getActionSender().sendMessage(
									"Successfully banned the player: " + name
											+ ".");
						} else {
							player.getActionSender().sendMessage(
									"Failed to ban the player: " + name + ".");
						}
					} catch (Exception e) {
						e.printStackTrace();
						player.getActionSender()
								.sendMessage(
										"Syntax is ::giverights rights, \"playerName\"");
					}
				} else if (command.equalsIgnoreCase("bank")) {
					try {
						Bank.open(player);
					} catch (Exception e) {
						e.printStackTrace();
						player.getActionSender()
								.sendMessage("Syntax is ::bank");
					}
				} else if (command.startsWith("shutdown")) {
					try {
						for (Player p : World.getWorld().getPlayers()) {
							p.getActionSender().sendLogout(true);// Saves and
																	// logs out.
						}
						BannedUsers.save();
						World.getWorld().submit(new Event(3000) {

							@Override
							public void execute() {
								System.exit(0);
								this.stop();// Lul.
							}

						});

					} catch (Exception e) {
						e.printStackTrace();
						player.getActionSender()
								.sendMessage(
										"Syntax is ::giverights rights, \"playerName\"");
					}
				} else if (command.equals("npc")) {
					NPC npc = NPC.create(
							NPCDefinition.forId(Integer.parseInt(args[1])),
							player.getLocation(), null, null);
					World.getWorld().getNPCs().add(npc);
				} else if (command.equals("anim")) {
					if (args.length == 2 || args.length == 3) {
						int id = Integer.parseInt(args[1]);
						int delay = 0;
						if (args.length == 3) {
							delay = Integer.parseInt(args[2]);
						}
						player.playAnimation(Animation.create(id, delay));
					}
				} else if (command.equals("interface")) {
					if (args.length == 2) {
						int id = Integer.parseInt(args[1]);
						switch (id) {
						case 267:
							PestControl.showBuyingInterface(player);
							return;
						case 446:
							player.getActionSender().sendInterfaceModel(id, 62,
									200, -1); // Rings
							// player.getActionSender().editTypeZero(id, 61,
							// 1000); //hmm :S No idea about usage. xD
							// player.getActionSender().sendPacket69(id, 62);
							player.getActionSender().sendPacket69(id, 48);
							break;
						}
						player.getActionSender().sendInterface(id);

					}
				} else if (command.equals("gfx")) {
					if (args.length == 2 || args.length == 3) {
						int id = Integer.parseInt(args[1]);
						int delay = 0;
						if (args.length == 3) {
							delay = Integer.parseInt(args[2]);
						}
						player.playGraphics(Graphic.create(id, delay));
					}
				} else if (command.equals("loopgfx")) {
					if (args.length == 2) {

						World.getWorld().submit(new Event(2000) {
							int id = Integer.parseInt(args[1]);

							@Override
							public void execute() {
								player.getActionSender().sendMessage(
										"Currently playing: " + id);
								player.getActionSender()
										.sendStillGFX(
												id++,
												100,
												player.getLocation().transform(
														1, 1, 0));
							}

						});
					}
				} else if (command.startsWith("config")) {
					try {
						player.getActionSender().sendConfig(
								Integer.valueOf(args[1]),
								Integer.valueOf(args[2]));
					} catch (Exception e) {
						e.printStackTrace();
						player.getActionSender().sendMessage(
								"Syntax is ::config [id] [value].");
					}
				} else if (command.startsWith("startconfigtest2")) { // Aight,
																		// we
																		// have
																		// quite
																		// a few
																		// commands
																		// now.
					final Player p = player;
					final String[] cmds = args;
					try {
						World.getWorld().submit(new Event(300) {
							private int configId = Integer.valueOf(cmds[1]);// This
																			// one
																			// asks
																			// for
																			// the
																			// ID
							private int configValue = 0;

							@Override
							public void execute() {
								p.getActionSender().sendConfig(configId,
										configValue++);
								p.getActionSender().sendMessage(
										"Currently testing config value: "
												+ configValue
												+ " with config id: "
												+ configId);
								if (stopConfigTest) {
									this.stop();
									stopConfigTest = false;
								}
							}

						});
					} catch (Exception e) {
						e.printStackTrace();
						player.getActionSender().sendMessage(
								"Syntax is ::configtest");
					}
				} else if (command.startsWith("startconfigtest")) {// Use this
																	// one
																	// first.
					final Player p = player;
					try {
						World.getWorld().submit(new Event(300) {
							private int configId = 0;

							@Override
							public void execute() {
								p.getActionSender().sendConfig(configId++, 1);
								p.getActionSender().sendMessage(
										"Currently sending config id: "
												+ configId);
								if (stopConfigTest) {
									this.stop();
									stopConfigTest = false;
								}
							}

						});
					} catch (Exception e) {
						e.printStackTrace();
						player.getActionSender().sendMessage(
								"Syntax is ::configtest");
					}
				} else if (command.startsWith("stopconfigtest")) {// Use this to
																	// stop it
					try {
						stopConfigTest = true;
					} catch (Exception e) {
						e.printStackTrace();
						player.getActionSender().sendMessage(
								"Syntax is ::configtest");
					}

				} else if (command.equals("bank")) {
					Bank.open(player);
					/*
					 * else if (commandString.startsWith("skele")) { try {
					 * System.out.println("Creating skeleton spawns,"); final
					 * Random r = new Random(); int[] ids = { 90, 3291, 3581,
					 * 5332, 5333, 5334, 6091, 6092, 6093, };
					 * p.println("<npcSpawn> <!--Skeleton.-->");
					 * p.println("  <npcId>"+ids[r.nextInt(ids.length)]+
					 * "</npcId> <!--The npc id to spawn.-->"); p.println(
					 * "  <walkingType>2</walkingType> <!--The walking type-->"
					 * ); p.println(
					 * "  <spawnLocation> <!--The location to spawn the NPC.-->"
					 * ); p.println("	<x>"+player.getLocation().getX()+"</x>");
					 * p.println("	<y>"+player.getLocation().getY()+"</y>");
					 * p.println("	<z>"+player.getLocation().getZ()+"</z>");
					 * p.println("  </spawnLocation>"); p.println(
					 * "  <minLocation> <!--The lowest location you can walk into..-->"
					 * );
					 * p.println("	<x>"+(player.getLocation().getX()-5)+"</x>");
					 * p.println("	<y>"+(player.getLocation().getY()-5)+"</y>");
					 * p.println("	<z>"+(player.getLocation().getZ())+"</z>");
					 * p.println("  </minLocation>"); p.println(
					 * "  <maxLocation> <!--The highest location you can walk into..-->"
					 * );
					 * p.println("	<x>"+(player.getLocation().getX()+5)+"</x>");
					 * p.println("	<y>"+(player.getLocation().getY()+5)+"</y>");
					 * p.println("	<z>"+(player.getLocation().getZ())+"</z>");
					 * p.println("  </maxLocation>"); p.println("</npcSpawn>");
					 * p.flush(); //p.close(); } catch(Exception e) {
					 * player.getActionSender
					 * ().sendMessage("Syntax is ::yell <message>"); } }
					 */
				}
			}

			/*
			 * if(command.equals("tele")) { if(args.length == 3 || args.length
			 * == 4) { int x = Integer.parseInt(args[1]); int y =
			 * Integer.parseInt(args[2]); int z = player.getLocation().getZ();
			 * if(args.length == 4) { z = Integer.parseInt(args[3]); }
			 * player.setTeleportTarget(Location.create(x, y, z)); } else {
			 * player
			 * .getActionSender().sendMessage("Syntax is ::tele [x] [y] [z].");
			 * } } else if(command.equals("pos")) { try {
			 * player.getActionSender(
			 * ).sendMessage(player.getLocation().toString()); } catch(Exception
			 * e) {
			 * 
			 * } } else if(command.equals("distance")) { try {
			 * player.getActionSender
			 * ().sendFollowingDistance(Integer.valueOf(args[1])); }
			 * catch(Exception e) {
			 * 
			 * } } else if(command.equals("item")) { if(args.length == 2 ||
			 * args.length == 3) { int id = Integer.parseInt(args[1]); int count
			 * = 1; if(args.length == 3) { count = Integer.parseInt(args[2]); }
			 * if (Inventory.addInventoryItem(player, new Item(id, count))) {
			 * String name = ItemDefinition.forId(id).getName().toLowerCase();
			 * player.getActionSender().sendMessage( "You successfully spawn " +
			 * count + " " + name + (name.endsWith("s") ? (count > 1 ? "s." :
			 * ".") : ".")); } } else {
			 * player.getActionSender().sendMessage("Syntax is ::item [id] [count]."
			 * ); } } else if(command.equals("npc")) { NPC npc =
			 * NPC.create(NPCDefinition.forId(Integer.parseInt(args[1])),
			 * player.getLocation(), null, null);
			 * World.getWorld().getNPCs().add(npc); } else
			 * if(command.equals("anim")) { if(args.length == 2 || args.length
			 * == 3) { int id = Integer.parseInt(args[1]); int delay = 0;
			 * if(args.length == 3) { delay = Integer.parseInt(args[2]); }
			 * player.playAnimation(Animation.create(id, delay)); } } else
			 * if(command.equals("gfx")) { if(args.length == 2 || args.length ==
			 * 3) { int id = Integer.parseInt(args[1]); int delay = 0;
			 * if(args.length == 3) { delay = Integer.parseInt(args[2]); }
			 * player.playGraphics(Graphic.create(id, delay)); } } else
			 * if(command.equals("bank")) { Bank.open(player); } else
			 * if(command.equals("max")) { try { for(int i = 0; i <
			 * Skills.SKILL_COUNT; i++) { player.getSkills().setLevel(i, 99);
			 * player.getSkills().setExperience(i, 13034431); } }
			 * catch(Exception e) { System.out.println(e); }
			 * 
			 * } else if(command.startsWith("empty")) {
			 * player.getInventory().clear();
			 * player.getActionSender().sendMessage
			 * ("Your inventory has been emptied."); } else
			 * if(command.startsWith("lvl")) { try {
			 * player.getSkills().setLevel(Integer.parseInt(args[1]),
			 * Integer.parseInt(args[2]));
			 * player.getSkills().setExperience(Integer.parseInt(args[1]),
			 * player.getSkills().getXPForLevel(Integer.parseInt(args[2])) + 1);
			 * player
			 * .getActionSender().sendMessage(Skills.SKILL_NAME[Integer.parseInt
			 * (args[1])] + " level is now " + Integer.parseInt(args[2]) + ".");
			 * } catch(Exception e) { e.printStackTrace();
			 * player.getActionSender
			 * ().sendMessage("Syntax is ::lvl [skill] [lvl]."); } } else
			 * if(command.startsWith("skill")) { try {
			 * player.getSkills().setLevel(Integer.parseInt(args[1]),
			 * Integer.parseInt(args[2]));
			 * player.getActionSender().sendMessage(Skills
			 * .SKILL_NAME[Integer.parseInt(args[1])] +
			 * " level is temporarily boosted to " + Integer.parseInt(args[2]) +
			 * "."); } catch(Exception e) { e.printStackTrace();
			 * player.getActionSender
			 * ().sendMessage("Syntax is ::skill [skill] [lvl]."); } } else if
			 * (command.startsWith("switch")) { try { int spellbook =
			 * Integer.valueOf(args[1]); switch(spellbook) { case 0:
			 * player.getMagic().setSpellBook(MagicType.MODERN);//Normal mage
			 * break; case 1: player.getMagic().setSpellBook(MagicType.ANCIENT);
			 * //Ancients break; case 2:
			 * player.getMagic().setSpellBook(MagicType.LUNAR); //Lunar break; }
			 * } catch(NumberFormatException e) { e.printStackTrace(); } } else
			 * if(command.startsWith("object")) { try { if(args.length == 2) {
			 * player
			 * .getActionSender().sendCreateObject(Integer.parseInt(args[1]),
			 * 10, 0, player.getLocation()); } else if(args.length == 3) {
			 * player
			 * .getActionSender().sendCreateObject(Integer.parseInt(args[1]),
			 * Integer.parseInt(args[2]), 0, player.getLocation()); } else
			 * if(args.length == 4) {
			 * player.getActionSender().sendCreateObject(Integer
			 * .parseInt(args[1]), Integer.parseInt(args[2]),
			 * Integer.parseInt(args[3]), player.getLocation()); }
			 * 
			 * } catch(Exception e) { e.printStackTrace();
			 * player.getActionSender().sendMessage("Syntax is ::object [id].");
			 * } } else if(command.startsWith("enablepvp")) { try {
			 * player.updatePlayerAttackOptions(true);
			 * player.getActionSender().sendMessage("PvP combat enabled."); }
			 * catch(Exception e) {
			 * 
			 * } } else if(command.startsWith("interface")) { try {
			 * player.getActionSender
			 * ().sendInterface(Integer.parseInt(args[1])); } catch(Exception e)
			 * { e.printStackTrace();
			 * player.getActionSender().sendMessage("Syntax is ::interface [id]."
			 * ); } } else if (command.startsWith("config")) { try {
			 * player.getActionSender().sendConfig(Integer.valueOf(args[1]),
			 * Integer.valueOf(args[2])); } catch(Exception e) {
			 * e.printStackTrace(); player.getActionSender().sendMessage(
			 * "Syntax is ::config [id] [value]."); } } else if
			 * (command.startsWith("startconfigtest2")) { //Aight, we have quite
			 * a few commands now. final Player p = player; final String[] cmds
			 * = args; try { World.getWorld().submit(new Event(300) { private
			 * int configId = Integer.valueOf(cmds[1]);//This one asks for the
			 * ID private int configValue = 0;
			 * 
			 * @Override public void execute() {
			 * p.getActionSender().sendConfig(configId, configValue++);
			 * p.getActionSender
			 * ().sendMessage("Currently testing config value: "+ configValue
			 * +" with config id: "+ configId); if(stopConfigTest) {
			 * this.stop(); stopConfigTest = false; } }
			 * 
			 * }); } catch(Exception e) { e.printStackTrace();
			 * player.getActionSender().sendMessage("Syntax is ::configtest"); }
			 * } else if (command.startsWith("startconfigtest")) {//Use this one
			 * first. final Player p = player; try { World.getWorld().submit(new
			 * Event(300) { private int configId = 0;
			 * 
			 * @Override public void execute() {
			 * p.getActionSender().sendConfig(configId++, 1);
			 * p.getActionSender().sendMessage("Currently sending config id: "+
			 * configId); if(stopConfigTest) { this.stop(); stopConfigTest =
			 * false; } }
			 * 
			 * }); } catch(Exception e) { e.printStackTrace();
			 * player.getActionSender().sendMessage("Syntax is ::configtest"); }
			 * } else if (command.startsWith("stopconfigtest")) {//Use this to
			 * stop it try { stopConfigTest = true; } catch(Exception e) {
			 * e.printStackTrace();
			 * player.getActionSender().sendMessage("Syntax is ::configtest"); }
			 * } else if (command.startsWith("getmask")) { try {
			 * System.out.println("Getting mask.."); int mask =
			 * World.getWorld().
			 * getRegionManager().getClippingMask(player.getLocation().getX(),
			 * player.getLocation().getY());
			 * System.out.println("Clipping mask: "+ mask); masks.add(mask); }
			 * catch(Exception e) { e.printStackTrace();
			 * player.getActionSender().sendMessage("Syntax is ::configtest"); }
			 * } else if (command.startsWith("printmasks")) { try { PrintWriter
			 * p = new PrintWriter(new File("d:/walkablemasks.txt")); for(int
			 * mask : masks) { p.println(mask); p.flush(); } p.close();
			 * System.out.println("Getting mask.."); } catch(Exception e) {
			 * e.printStackTrace();
			 * player.getActionSender().sendMessage("Syntax is ::configtest"); }
			 * } else if (command.startsWith("forceequip")) { try {
			 * player.getEquipment().set(3, new Item(5614));
			 * player.setDifferentUpdateAnimation(true);
			 * player.setTemporaryUpdatingAnimation(0, 2261); //Stand.
			 * player.setTemporaryUpdatingAnimation(1, 2261); //Stand - turn?.
			 * player.setTemporaryUpdatingAnimation(2, 2263); //Walk.
			 * player.setTemporaryUpdatingAnimation(3, 2263); //Turn
			 * player.setTemporaryUpdatingAnimation(4, 2263); //Turn
			 * player.setTemporaryUpdatingAnimation(5, 2263); //Turn
			 * player.setTemporaryUpdatingAnimation(6, 2263); //Run }
			 * catch(Exception e) { e.printStackTrace();
			 * player.getActionSender()
			 * .sendMessage("Syntax is ::forceequip slot, id."); } } else if
			 * (command.startsWith("respec")) { try {
			 * player.getSpecials().setAmount(1000); } catch(Exception e) {
			 * e.printStackTrace(); player.getActionSender().sendMessage(
			 * "Syntax is ::forceequip slot, id."); } } else if
			 * (command.startsWith("lostcity")) { try {
			 * player.editQuestInfo(LostCity.QUEST_INFO_INDEX,
			 * LostCity.MAIN_QUEST_STAGE_INDEX,Integer.valueOf(args[1])); }
			 * catch(Exception e) { e.printStackTrace();
			 * player.getActionSender()
			 * .sendMessage("Syntax is ::forceequip slot, id."); } } else if
			 * (commandString.startsWith("yell") && commandString.length() > 5)
			 * { try { String rankName[] = {"", "[MOD] ", "[ADMIN] "}; String
			 * text = commandString.substring(5); String rankN =
			 * rankName[player.getRights().toInteger()];
			 * 
			 * for(Player p: World.getWorld().getPlayers()) {
			 * p.getActionSender().sendMessage(rankN + "" + player.getName() +
			 * ": " + text); } } catch(Exception e) {
			 * player.getActionSender().sendMessage
			 * ("Syntax is ::yell <message>"); }
			 * 
			 * 
			 * } else if (command.startsWith("mb")) { try {
			 * player.setTeleportTarget(Location.create(3095, 3960, 0)); }
			 * catch(Exception e) { e.printStackTrace();
			 * player.getActionSender()
			 * .sendMessage("Syntax is ::forceequip slot, id."); }
			 * 
			 * } else if (command.startsWith("m")) { try { for(int i = 0; i <
			 * Integer.valueOf(args[1]); i++) {
			 * player.getBarrows().increaseKillCount(); }
			 * 
			 * } catch(Exception e) { e.printStackTrace();
			 * player.getActionSender
			 * ().sendMessage("Syntax is ::forceequip slot, id."); } }
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
			player.getActionSender().sendMessage(
					"Error while processing command.");
		}
	}

	private static final List<Location[]> list = new ArrayList<Location[]>();

	private static void createConsList() {
		// 1983,5119,0 //Max
		// 1856,5056,0 //Min
		for (int z = 0; z < 4; z++) {
			for (int x = 1856; x <= 1983; x += 8) {
				for (int y = 5056; y <= 5119; y += 8) {
					Location min = Location.create(x, y, z);
					Location max = Location.create(x + 7, y + 7, z);
					list.add(new Location[] { min, max });
				}
			}
		}

	}

	static {
		createConsList();
	}

}
