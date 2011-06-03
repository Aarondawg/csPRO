package org.hyperion.rs2.content;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Player;

/**
 * Emotes helper class.
 * 
 * @author Graham
 * 
 */
public class Emotes {

	public final static Animation YES_EMOTE = Animation.create(855);
	public final static Animation NO_EMOTE = Animation.create(856);
	public final static Animation THINKING = Animation.create(857);
	public final static Animation BOW = Animation.create(858);
	public final static Animation ANGRY = Animation.create(859);
	public final static Animation CRY = Animation.create(860);
	public final static Animation LAUGH = Animation.create(861);
	public final static Animation CHEER = Animation.create(862);
	public final static Animation WAVE = Animation.create(863);
	public final static Animation BECKON = Animation.create(864);
	public final static Animation CLAP = Animation.create(865);
	public final static Animation DANCE = Animation.create(866);

	public final static Animation PANIC = Animation.create(2105);
	public final static Animation JIG = Animation.create(2106);
	public final static Animation SPIN = Animation.create(2107);
	public final static Animation HEADBANG = Animation.create(2108);
	public final static Animation JOYJUMP = Animation.create(2109);
	public final static Animation RASPBERRY = Animation.create(2110);
	public final static Animation YAWN = Animation.create(2111);
	public final static Animation SALUTE = Animation.create(2112);
	public final static Animation SHRUG = Animation.create(2113);
	public final static Animation BLOW_KISS = Animation.create(1368);
	public final static Graphic BLOW_KISS_GFX = Graphic.create(574);

	public final static Animation GLASS_WALL = Animation.create(1128);
	public final static Animation LEAN = Animation.create(1129);
	public final static Animation CLIMB_ROPE = Animation.create(1130);
	public final static Animation GLASS_BOX = Animation.create(1131);

	public final static Animation GOBLIN_BOW = Animation.create(2127);
	public final static Animation GOBLIN_DANCE = Animation.create(2128);

	public final static Animation SLAP_HEAD = Animation.create(4275);
	public final static Animation STAMP = Animation.create(1745);
	public final static Animation FLAP = Animation.create(4280);
	public final static Animation IDEA = Animation.create(4276);

	public final static Animation ZOMBIE_WALK = Animation.create(3544);
	public final static Animation ZOMBIE_DANCE = Animation.create(3543);

	public final static Animation SCARED = Animation.create(2836);
	public final static Animation RABIT_HOP = Animation.create(6111);

	/**
	 * Handles a player emote: does the appropriate animation.
	 * 
	 * @param player
	 * @param buttonId
	 * @return
	 */
	public static boolean emote(Player player, int buttonId) {
		switch (buttonId) {
		case 1:
			player.playAnimation(YES_EMOTE);
			break;
		case 2:
			player.playAnimation(NO_EMOTE);
			break;
		case 3:
			player.playAnimation(BOW);
			break;
		case 4:
			player.playAnimation(ANGRY);
			break;
		case 5:
			player.playAnimation(THINKING);
			break;
		case 6:
			player.playAnimation(WAVE);
			break;
		case 7:
			player.playAnimation(SHRUG);
			break;
		case 8:
			player.playAnimation(CHEER);
			break;
		case 9:
			player.playAnimation(BECKON);
			break;
		case 10:
			player.playAnimation(LAUGH);
			break;
		case 11:
			player.playAnimation(JOYJUMP);
			break;
		case 12:
			player.playAnimation(YAWN);
			break;
		case 13:
			player.playAnimation(DANCE);
			break;
		case 14:
			player.playAnimation(JIG);
			break;
		case 15:
			player.playAnimation(SPIN);
			break;
		case 16:
			player.playAnimation(HEADBANG);
			break;
		case 17:
			player.playAnimation(CRY);
			break;
		case 18:
			player.playAnimation(BLOW_KISS);
			player.playGraphics(BLOW_KISS_GFX);
			break;
		case 19:
			player.playAnimation(PANIC);
			break;
		case 20:
			player.playAnimation(RASPBERRY);
			break;
		case 21:
			player.playAnimation(CLAP);
			break;
		case 22:
			player.playAnimation(SALUTE);
			break;
		case 23:
			player.playAnimation(GOBLIN_BOW);
			break;
		case 24:
			player.playAnimation(GOBLIN_DANCE);
			break;
		case 25:
			player.playAnimation(GLASS_BOX);
			break;
		case 26:
			player.playAnimation(CLIMB_ROPE);
			break;
		case 27:
			player.playAnimation(LEAN);
			break;
		case 28:
			player.playAnimation(GLASS_WALL);
			break;
		case 29:
			player.playAnimation(SLAP_HEAD);
			break;
		case 30:
			player.playAnimation(STAMP);
			break;
		case 31:
			player.playAnimation(FLAP);
			break;
		case 32:
			player.playAnimation(IDEA);
			break;
		case 33:
			player.playAnimation(ZOMBIE_WALK);
			break;
		case 34:
			player.playAnimation(ZOMBIE_DANCE);
			break;
		case 35:
			player.playAnimation(SCARED);
			break;
		case 36:
			player.playAnimation(RABIT_HOP);
			break;
		case 37:
			Skillcape.performEmote(player);
			break;
		default:
			return false;
		}
		return true;
	}

}
