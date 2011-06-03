package org.hyperion.rs2.model.npc.barrows;

import java.util.Random;

import org.hyperion.rs2.content.skills.magic.SpellMananger;
import org.hyperion.rs2.content.skills.magic.impl.Spell;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.Combat.AttackType;

public class Ahrim extends NPC {

	/**
	 * Ahrim's random instance.
	 */
	private static final Random r = new Random();

	/**
	 * The NPC constructor.
	 */
	public Ahrim(NPCDefinition definition, Location location,
			Location minLocation, Location maxLocation) {
		super(definition, location, minLocation, maxLocation);
		getMagic().setAutoCasting(true);
		getMagic().setAutoCastingSpellId(55);
	}

	@Override
	public AttackType getAttackType() {
		return AttackType.MAGIC;
	}

	/**
	 * An array holding all the Reducing Spells that Ahrim's cast.
	 */
	private static final Spell[] REDUCING_SPELLS = {
			SpellMananger.MODERN_SPELLS[2] /* Confuse */,
			SpellMananger.MODERN_SPELLS[7] /* Weaken */,
			SpellMananger.MODERN_SPELLS[11] /* Curse */,
			SpellMananger.MODERN_SPELLS[50] /* Vulnerability */,
			SpellMananger.MODERN_SPELLS[53] /* Enfeeble */,
			SpellMananger.MODERN_SPELLS[57] /* Stun */, };

	@Override
	public boolean getSpecialAttack(final Entity victim) {
		// As this is called every time we're attacking the victim, we add
		// prayer reducing in here.
		if (victim instanceof Player) {
			Player pVictim = (Player) victim;
			pVictim.getSkills().detractLevel(Skills.PRAYER, 2);
		}
		if (r.nextInt(5) == 0) {
			REDUCING_SPELLS[r.nextInt(REDUCING_SPELLS.length)].cast(this,
					victim);
			increaseCombatDelay(2500);
			return true;
		}
		return false;
	}

}
