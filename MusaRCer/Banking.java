package MusaRCer;

import org.tbot.internal.handlers.LogHandler;
import org.tbot.methods.Bank;
import org.tbot.methods.GameObjects;
import org.tbot.methods.Mouse;
import org.tbot.methods.Npcs;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Time;
import org.tbot.methods.tabs.Inventory;
import org.tbot.methods.walking.Walking;
import org.tbot.wrappers.GameObject;
import org.tbot.wrappers.Item;
import org.tbot.wrappers.NPC;
import org.tbot.wrappers.Tile;

public class Banking {

	public static boolean IS_BAG_FILLED = false;

	static Tile[] ALTAR_TO_BANK = MusaRCer.reversePath(Craft.BANK_TO_ALTAR);

	public static void run() {

		if (Inventory.isFull()) {
			if (!IS_BAG_FILLED) {
				if (!Bank.isOpen()) {
					Item POUCH = Inventory.getFirst("Small pouch");
					if (POUCH != null) {
						POUCH.interact("Fill");
					}
				} else {
					Bank.close();
				}
			}
		} else {
			if (Bank.isOpen()) {
				if (!Inventory.contains("Small pouch")) {
					Bank.withdraw("Small pouch", 1);
				} else if (Inventory.getCount() > 1 && !Inventory.contains("Pure essence")) {
					Bank.depositAll();
				} else {
					Bank.withdrawAll("Pure essence");
				}
			} else {
				NPC BOOTH = Npcs.getNearest("Banker");
				if (BOOTH != null && BOOTH.isOnScreen()) {
					Mouse.move(BOOTH.getModel().getRandomPoint());
					Mouse.click(false);
					Mouse.move(Mouse.getX() + Random.nextInt(-20, 20), Mouse.getY() + Random.nextInt(36, 45));
					Mouse.click(true);
				} else {
					LogHandler.log("GEWRE");
					if (Craft.atAltar()) {
						GameObject PORTAL = GameObjects.getNearest("Portal");
						if(PORTAL != null && PORTAL.isOnScreen()){
							PORTAL.interact("Use");
						} else if(PORTAL != null && PORTAL.getLocation().isOnMiniMap()){
							Tile T = PORTAL.getLocation();
							int X = T.getX() + Random.nextInt(-2, 2);
							int Y = T.getY() + Random.nextInt(-2, 2);
							Walking.walkTileMM(new Tile(X, Y));
						} else {
							int X = Players.getLocal().getLocation().getX() + Random.nextInt(-2, 2);
							int Y = Players.getLocal().getLocation().getY() + Random.nextInt(4, 11);
							Walking.walkTileMM(new Tile(X, Y));
						}
					} else {
						MusaRCer.walkPath(ALTAR_TO_BANK);
					}
				}
			}
		}
		Time.sleep(300, 1200);
	}

	public static boolean isReady() {
		if (Inventory.contains("Cosmic rune")) {
			return Inventory.getCount("Cosmic rune") >= 30;
		}
		if(!Inventory.contains("Pure essence") && !IS_BAG_FILLED){
			return true;
		}
		return !Inventory.isFull() || !IS_BAG_FILLED;
	}

}
