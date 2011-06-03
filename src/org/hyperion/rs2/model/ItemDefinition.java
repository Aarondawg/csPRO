package org.hyperion.rs2.model;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.container.Equipment.EquipmentType;
import org.hyperion.util.XStreamUtil;

/**
 * The item definition manager.
 * 
 * @author Vastico
 * @author Graham Edgecombe
 * 
 */
public class ItemDefinition {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(ItemDefinition.class
			.getName());

	/**
	 * The definition array.
	 */
	private static ItemDefinition[] definitions;

	/**
	 * Gets a definition for the specified id.
	 * 
	 * @param id
	 *            The id.
	 * @return The definition.
	 */
	public static ItemDefinition forId(int id) {
		ItemDefinition def = definitions[id];
		if (def == null) {
			def = new ItemDefinition(id, "# + id", "It's an item!", false,
					false, false, -1, -1, true, 0, 0, 0, 0, new int[] { 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
		}
		return def;
	}

	/**
	 * Loads the item definitions.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws IllegalStateException
	 *             if the definitions have been loaded already.
	 */
	@SuppressWarnings("unchecked")
	public static void init() throws IOException {
		logger.info("Loading definitions...");
		try {
			List<ItemDefinition> defs = (List<ItemDefinition>) XStreamUtil
					.getXStream().fromXML(
							new FileInputStream("data/itemDefinitions.xml"));
			definitions = new ItemDefinition[defs.size()];
			for (ItemDefinition def : defs) {
				definitions[def.getId()] = def;
			}
			logger.info("Loaded " + defs.size() + " definitions");
		} catch (IOException e) {
			logger.warning("Failed to initialize the item definitions: " + e);
		}
		/*
		 * PrintWriter printwriter = new PrintWriter(new
		 * FileWriter("d:/ItemDefinitionsEquipmentFINAL.xml", true)); for(int id
		 * = 0; id < definitions.length; id++) { try {
		 * printwriter.println("  <itemDefinition>");
		 * printwriter.println("    <id>"+id+"</id>");
		 * printwriter.println("    <name>"+forId(id).getName()+"</name>");
		 * printwriter
		 * .println("    <examine>"+forId(id).getDescription()+"</examine>");
		 * printwriter
		 * .println("    <equipmentType>"+getEquipmentType(id)+"</equipmentType>"
		 * ); printwriter.println("    <noted>"+forId(id).isNoted()+"</noted>");
		 * printwriter
		 * .println("    <noteable>"+forId(id).isNoteable()+"</noteable>");
		 * printwriter
		 * .println("    <stackable>"+forId(id).isStackable()+"</stackable>");
		 * printwriter
		 * .println("    <parentId>"+forId(id).getNormalId()+"</parentId>");
		 * printwriter
		 * .println("    <notedId>"+forId(id).getNotedId()+"</notedId>");
		 * printwriter
		 * .println("    <members>"+forId(id).isMembersOnly()+"</members>");
		 * printwriter
		 * .println("    <specialStorePrice>"+forId(id).getSpecialStorePrice
		 * ()+"</specialStorePrice>");
		 * printwriter.println("    <generalStorePrice>"
		 * +forId(id).getGeneralStorePrice()+"</generalStorePrice>");
		 * printwriter.println("    <highAlcValue>"+forId(id).getHighAlcValue()+
		 * "</highAlcValue>");
		 * printwriter.println("    <lowAlcValue>"+forId(id).
		 * getLowAlcValue()+"</lowAlcValue>");
		 * printwriter.println("    <bonus>"); for(int i = 0; i < 12; i++) {
		 * printwriter.println("      <int>" + forId(id).getBonus(i)+"</int>");
		 * } printwriter.println("    </bonus>");
		 * printwriter.println("  </itemDefinition>"); printwriter.flush();
		 * System.gc(); } catch(Exception e) { e.printStackTrace(); } }
		 * printwriter.close();
		 */
	}

	/**
	 * Equipment type
	 */
	private final EquipmentType equipmentType;

	/**
	 * Id.
	 */
	private final int id;

	/**
	 * Name.
	 */
	private final String name;

	/**
	 * Description.
	 */
	private final String examine;

	/**
	 * Noted flag.
	 */
	private final boolean noted;

	/**
	 * Noteable flag.
	 */
	private final boolean noteable;

	/**
	 * Stackable flag.
	 */
	private final boolean stackable;

	/**
	 * Non-noted id.
	 */
	private final int parentId;

	/**
	 * Noted id.
	 */
	private final int notedId;

	/**
	 * Members flag.
	 */
	private final boolean members;

	/**
	 * The special store price.
	 */
	private final int specialStorePrice;

	/**
	 * Shop value.
	 */
	private final int generalStorePrice;

	/**
	 * High alc value.
	 */
	private final int highAlcValue;

	/**
	 * Low alc value.
	 */
	private final int lowAlcValue;

	/**
	 * Bonus values.
	 */
	private final int[] bonus;

	/**
	 * Creates the item definition.
	 * 
	 * @param id
	 *            The id.
	 * @param name
	 *            The name.
	 * @param examine
	 *            The description.
	 * @param noted
	 *            The noted flag.
	 * @param noteable
	 *            The noteable flag.
	 * @param stackable
	 *            The stackable flag.
	 * @param parentId
	 *            The non-noted id.
	 * @param notedId
	 *            The noted id.
	 * @param members
	 *            The members flag.
	 * @param shopValue
	 *            The shop price.
	 * @param highAlcValue
	 *            The high alc value.
	 * @param lowAlcValue
	 *            The low alc value.
	 */
	private ItemDefinition(int id, String name, String examine, boolean noted,
			boolean noteable, boolean stackable, int parentId, int notedId,
			boolean members, int specialStorePrice, int generalStorePrice,
			int highAlcValue, int lowAlcValue, int[] bonus) {
		this.id = id;
		this.name = name;
		this.examine = examine;
		this.noted = noted;
		this.noteable = noteable;
		this.stackable = stackable;
		this.parentId = parentId;
		this.notedId = notedId;
		this.members = members;
		this.specialStorePrice = specialStorePrice;
		this.generalStorePrice = generalStorePrice;
		this.highAlcValue = highAlcValue;
		this.lowAlcValue = lowAlcValue;
		this.bonus = bonus;
		this.equipmentType = null;
	}

	/**
	 * Gets the id.
	 * 
	 * @return The id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the description.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return examine;
	}

	/**
	 * Gets the noted flag.
	 * 
	 * @return The noted flag.
	 */
	public boolean isNoted() {
		return noted;
	}

	/**
	 * Gets the noteable flag.
	 * 
	 * @return The noteable flag.
	 */
	public boolean isNoteable() {
		return noteable;
	}

	/**
	 * Gets the stackable flag.
	 * 
	 * @return The stackable flag.
	 */
	public boolean isStackable() {
		return stackable || noted;
	}

	/**
	 * Gets the normal id.
	 * 
	 * @return The normal id.
	 */
	public int getNormalId() {
		return parentId;
	}

	/**
	 * Gets the noted id.
	 * 
	 * @return The noted id.
	 */
	public int getNotedId() {
		return notedId;
	}

	/**
	 * Gets the members only flag.
	 * 
	 * @return The members only flag.
	 */
	public boolean isMembersOnly() {
		return members;
	}

	/**
	 * Gets the value.
	 * 
	 * @return The value.
	 */
	public int getGeneralStorePrice() {
		int price = generalStorePrice;
		if (this.isNoted() && parentId != -1 && parentId != id) {
			price = forId(parentId).getGeneralStorePrice();
		}
		if (price == 0) {
			return 1;
		}
		return price;
	}

	public int getSpecialStorePrice() {
		int price = specialStorePrice;
		if (this.isNoted() && parentId != -1 && parentId != id) {
			if (price < forId(parentId).getSpecialStorePrice()) {
				price = forId(parentId).getSpecialStorePrice();
			}
		}
		if (price == 0) {
			return 1;
		}
		return price;
	}

	/**
	 * Gets the low alc value.
	 * 
	 * @return The low alc value.
	 */
	public int getLowAlcValue() {
		if (lowAlcValue == 0) {
			if (noted && parentId != -1 && parentId != id) {
				return forId(parentId).getLowAlcValue();
			}
		}
		return lowAlcValue;
	}

	/**
	 * Gets the high alc value.
	 * 
	 * @return The high alc value.
	 */
	public int getHighAlcValue() {
		if (highAlcValue == 0) {
			if (noted && parentId != -1 && parentId != id) {
				return forId(parentId).getHighAlcValue();
			}
		}
		return highAlcValue;
	}

	/**
	 * Gets the array of bonuses.
	 * 
	 * @return The array of item bonuses.
	 */
	public int[] getBonuses() {
		return bonus;
	}

	/**
	 * Gets a specific bonus based on this item definition..
	 * 
	 * @return The bonus value
	 */
	public int getBonus(int id) {
		return bonus[id];
	}

	public EquipmentType getEquipmentType() {
		return equipmentType;
	}

}
