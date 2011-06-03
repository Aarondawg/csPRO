package org.hyperion.rs2.content.skills.magic.impl;

import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Item;

/**
 * A simple spell interface.
 * 
 * @Author Brown.
 */

public interface Spell {

	/**
	 * Casts a spell.
	 * 
	 * @param caster
	 *            The spell caster
	 * @param target
	 *            The target of the spell.
	 */
	public void cast(Entity caster, Entity target);

	/**
	 * Gets the level requirement of a specific spell.
	 */
	public int getLevelReq();

	/**
	 * Gets an array of all the runes neeeded to cast this spell.
	 */
	public Item[] getRuneReqs();

	/**
	 * Gets the experience reward of this spell.
	 */
	public double getExperience();

}