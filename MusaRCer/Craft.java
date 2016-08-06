package MusaRCer;

import org.tbot.internal.handlers.LogHandler;
import org.tbot.methods.GameObjects;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Time;
import org.tbot.methods.tabs.Inventory;
import org.tbot.methods.walking.Walking;
import org.tbot.wrappers.GameObject;
import org.tbot.wrappers.Item;
import org.tbot.wrappers.Tile;

public class Craft {

	/** FIRE ALTAR **/

	/**
	 * static Tile[] BANK_TO_ALTAR = { new Tile(3381, 3268), new Tile(3374,
	 * 3265), new Tile(3364, 3264), new Tile(3351, 3265), new Tile(3341, 3266),
	 * new Tile(3328, 3265), new Tile(3325, 3254), new Tile(3319, 3245), new
	 * Tile(3309, 3248), new Tile(3312, 3253)};
	 **/

	static Tile[] BANK_TO_ALTAR = { new Tile(2383, 4457), new Tile(2392, 4452), new Tile(2397, 4445),
			new Tile(2405, 4445), new Tile(2412, 4439), new Tile(2415, 4430), new Tile(2419, 4423),
			new Tile(2419, 4414), new Tile(2413, 4408), new Tile(2403, 4408), new Tile(2393, 4407),
			new Tile(2385, 4410), new Tile(2381, 4401), new Tile(2387, 4397), new Tile(2392, 4395),
			new Tile(2399, 4394), new Tile(2405, 4390), new Tile(2406, 4382), new Tile(2406, 4378) };

	public static void run() {

		if (atAltar()) {
			if (Inventory.contains("Pure essence")) {
				GameObject ALTAR = GameObjects.getNearest("Altar");
				if (ALTAR.isOnScreen()) {
					ALTAR.interact("Craft-rune");
				} else if (ALTAR.getLocation().isOnMiniMap()) {
					Walking.walkTileMM(ALTAR.getLocation());
				} else if(ALTAR != null){
				Tile TILE = ALTAR.getLocation();
				int X = TILE.getX();
				int Y = TILE.getY();
				int DIFF = Random.nextInt(8, 12);
				Tile N = new Tile(X, Y + DIFF);
				Tile W = new Tile(X - DIFF, Y);
				Tile E = new Tile(X + DIFF, Y);
				Tile S = new Tile(X, Y - DIFF);
				Tile[] TILES = {N, W, E, S};
				for(Tile T : TILES){
					if(T.isOnMiniMap()){
						Walking.walkTileMM(T);
					}
				}
				}
				Time.sleep(300, 1200);
			} else {
				if (Inventory.getCount("Cosmic rune") < 30) {
					Item i = Inventory.getFirst("Small pouch");
					if (i != null) {
						i.interact("Empty");
						Time.sleep(300, 900);
					}
				}
			}
		} else {
			GameObject RUINS = GameObjects.getNearest("Mysterious ruins");
			if (RUINS != null && RUINS.isOnScreen()) {
				RUINS.interact("Enter");
			} else {
				MusaRCer.walkPath(BANK_TO_ALTAR);
			}
			Time.sleep(300, 1200);
		}
	}

	public static boolean atAltar() {
		return Players.getLocal().getLocation().getY() > 4800;
	}

}
