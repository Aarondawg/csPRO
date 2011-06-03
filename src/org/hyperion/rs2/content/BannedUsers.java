package org.hyperion.rs2.content;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.hyperion.rs2.model.Player;

/**
 * Contains all the banned users.
 */
public class BannedUsers {

	private static final List<String> BANNED_USERS = new ArrayList<String>();

	@SuppressWarnings("deprecation")
	public static void init() {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		File file = new File("./data/bannedUsers.txt");
		try {
			fis = new FileInputStream(file);
			// Here BufferedInputStream is added for fast reading.
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			// dis.available() returns 0 if the file does not have more lines.

			while (dis.available() != 0) {
				String name = dis.readLine();
				BANNED_USERS.add(name);
			}
			dis.close();
		} catch (IOException e) {
			System.out
					.println("WARNING: Serious error while trying to read through the file: "
							+ e);
		}
	}

	public static void addUser(Player player) {
		BANNED_USERS.add(player.getName());
	}

	public static void save() {
		try {
			PrintWriter p = new PrintWriter(new FileWriter(
					"./data/bannedUsers.txt", true));
			for (String name : BANNED_USERS) {
				p.println(name);
				p.flush();
			}
		} catch (Exception e) {
			System.out.println("Banned users: " + e);
		}

	}

	public static boolean contains(String name) {
		return BANNED_USERS.contains(name);
	}

}
