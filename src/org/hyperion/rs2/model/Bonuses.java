package org.hyperion.rs2.model;

import org.hyperion.rs2.model.container.Equipment;

public class Bonuses {

	public static final String[] BONUS_NAMES = new String[] { "Stab", "Slash",
			"Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Magic",
			"Range", "Strength", "Prayer", };

	public static int ATKSTAB = 0, ATKSLASH = 1, ATKCRUSH = 2,
			MAGIC_ATTACK = 3, ATKRANGE = 4, DEFSTAB = 5, DEFSLASH = 6,
			DEFCRUSH = 7, MAGIC_DEFENCE = 8, DEFRANGE = 9, STRENGTH = 10,
			PRAYER = 11;

	public static final int SIZE = 12;

	private Player player;
	private int[] bonuses = new int[SIZE];

	public Bonuses(Player player) {
		this.player = player;
	}

	public void refresh() {
		for (int i = 0; i < SIZE; i++) {
			bonuses[i] = 0;
		}
		for (int i = 0; i < Equipment.SIZE; i++) {
			Item item = player.getEquipment().get(i);
			if (item != null) {
				for (int j = 0; j < SIZE; j++) {
					bonuses[j] += item.getDefinition().getBonus(j);
				}
			}
		}
		player.getActionSender().sendBonus(bonuses);
	}

	/*
	 * public void sendBonus() { int id = 108; for(int i = 0; i <
	 * (bonuses.length-1); i++) {
	 * player.getActionSender().sendString((Bonuses.BONUS_NAMES[i] + ": " +
	 * (bonuses[i] > 0 ? "+" : "") + bonuses[i]), 465, id++); if(i == 9) { id++;
	 * player.getActionSender().sendString((Bonuses.BONUS_NAMES[11] + ": " +
	 * (bonuses[11] > 0 ? "+" : "") + bonuses[11]), 465, id++); } if(i == 10) {
	 * id++; player.getActionSender().sendString((Bonuses.BONUS_NAMES[11] + ": "
	 * + (bonuses[11] > 0 ? "+" : "") + bonuses[11]), 465, id++); id = 120; } }
	 * }
	 */
	public int[] getBonuses() {
		return bonuses;
	}

	public int getBonus(int i) {
		return bonuses[i];
	}

}