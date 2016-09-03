package MusaSlaughter;

import javax.swing.JOptionPane;

import org.tbot.bot.TBot;
import org.tbot.internal.handlers.LogHandler;
import org.tbot.methods.Camera;
import org.tbot.methods.GameObjects;
import org.tbot.methods.Npcs;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Skills;
import org.tbot.methods.Time;
import org.tbot.methods.Widgets;
import org.tbot.methods.Skills.Skill;
import org.tbot.methods.tabs.Inventory;
import org.tbot.methods.walking.Walking;
import org.tbot.util.Filter;
import org.tbot.wrappers.GameObject;
import org.tbot.wrappers.Item;
import org.tbot.wrappers.NPC;
import org.tbot.wrappers.Player;
import org.tbot.wrappers.Tile;
import org.tbot.wrappers.WidgetChild;

public class Fight {

	public static String NPC_NAME;

	static int[] DEATH_ANIMATIONS = { 90, 2704, 4932, 4932, 5491, 4914, 808, 5386, 357 };

	public static Tile MID_TILE = new Tile(3110, 9933);

	public static boolean IS_MULTIWAY = false;

	private static boolean isInteracting() {
		if (Players.getLocal().isInteractingWithLocalPlayer()) {
			for (int i : DEATH_ANIMATIONS) {
				if (i == Players.getLocal().getInteractingEntity().getAnimation2()) {
					return false;
				}
			}
			return true;
		}
		return Players.getLocal().getInteractingCharacterIndex() > 0;
	}

	public static void run() {
		
		if(Inventory.contains("Enchanted gem") && Random.nextInt(0, 1000) > 990){
			Inventory.getFirst("Enchanted gem").interact("Check");
		}

		if (Eat.isReady()) {
			Eat.run();
		}

		Item i = Inventory.getFirst("Iron knife");
		if (i != null && !Loot.isReady() && Random.nextInt(0, 100) > 77
				&& Players.getLocal().getInteractingCharacterIndex() < 0) {
			i.interact("Wield");
			Time.sleep(300, 900);
		}

		WidgetChild W = Widgets.getClickContinueWidget();
		if (W != null) {
			W.click();

		} else

		if (!isInteracting()) {

			NPC n = Npcs.getNearest(new Filter<NPC>() {
				public boolean accept(NPC n) {
					if (n.getCombatLevel() > 1 && n.getName().toLowerCase().contains(NPC_NAME.toLowerCase())) {
						if (n.isInteractingWithLocalPlayer()) {
							for (int i : DEATH_ANIMATIONS) {
								if (n.getAnimation2() == i) {
									return n.getAnimation() < 1;
								}
							}
							return true;
						}
					}
					return false;
				}
			});

			if (n == null) {
				try {
					n = Npcs.getNearest(new Filter<NPC>() {
						public boolean accept(NPC n) {
							if (n.getCombatLevel() > 0 && n.getName().toLowerCase().contains(NPC_NAME.toLowerCase())) {
								if (n.getInteractingCharacterIndex() < 0
										|| (n.getInteractingEntity() != null
												&& !n.getInteractingEntity().getInteractingEntity().equals(n))
										|| n.isInteractingWithLocalPlayer() || IS_MULTIWAY) {
									for (int i : DEATH_ANIMATIONS) {
										if (n.getAnimation2() == i) {
											return n.getAnimation() < 1;
										}
									}
									return true;
								}
							}
							return false;
						}
					});
				} catch (Exception e) {

				}
			}

			if (n != null) {
				if (n.isOnScreen()) {
					n.interact("Attack");
				} else if (n.getLocation().isOnMiniMap()) {
					Walking.walkTileMM(n.getLocation());
				} else if (!MID_TILE.isOnScreen()) {
					Walking.walkTileMM(MID_TILE);
				}
			} else {
				if("wall beast".contains(NPC_NAME.toLowerCase())){
					
					NPC HOLE = Npcs.getNearest(new Filter<NPC>(){
						public boolean accept(NPC n){
							return n.getName().equals("Hole in the wall") && n.distance() > 1;
						}
					});
					
					while(n == null && HOLE != null && HOLE.distance() > 1 && TBot.getBot().getScriptHandler().getScript().isRunning()){
						if(HOLE != null){
							if(HOLE.isOnScreen()){
								Walking.walkTileOnScreen(HOLE.getLocation());
							} else {
								if(Random.nextInt(0, 10) > 3){
									Camera.turnTo(HOLE);
								}
								if(!HOLE.isOnScreen()){
									Walking.walkTileMM(HOLE.getLocation());
								}
							}
							Time.sleep(300, 700);
						}
					}
				}
			}
			Time.sleep(Random.nextInt(300, 1600));
		} else {
			Antiban.run();
		}
	}

	public static boolean isReady() {
		int HP = 100 * Skills.getCurrentLevel(Skill.HITPOINTS) / Skills.getRealLevel(Skill.HITPOINTS);
		return Inventory.containsOneOf(Eat.FOOD) || HP > 50;
	}

}
