package MusaRCer;

import org.tbot.internal.AbstractScript;
import org.tbot.internal.Manifest;
import org.tbot.internal.event.events.MessageEvent;
import org.tbot.internal.event.listeners.MessageListener;
import org.tbot.internal.handlers.LogHandler;
import org.tbot.methods.Camera;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Time;
import org.tbot.methods.walking.Walking;
import org.tbot.wrappers.GameObject;
import org.tbot.wrappers.Tile;

@Manifest(name = "MusaRCer", authors = "MansaMusa")
public class MusaRCer extends AbstractScript implements MessageListener {

	public int loop() {

		try {
			
			if(Walking.getRunEnergy() > Random.nextInt(70, 80)){
				Walking.setRun(true);
			}

			if (Banking.isReady()) {
				Banking.run();
			} else {
				Craft.run();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Random.nextInt(300, 900);
	}

	public static void walkPath(Tile[] PATH) {

		int INDEX = getTileIndex(PATH);

		Tile DESTINATION = PATH[INDEX];

		LogHandler.log(DESTINATION);

		if (!DESTINATION.isOnMiniMap()) {
			DESTINATION = PATH[INDEX - 1];
		}

		LogHandler.log(DESTINATION);

		int X = DESTINATION.getX() + Random.nextInt(-1, 1);
		int Y = DESTINATION.getY() + Random.nextInt(-1, 1);

		DESTINATION = new Tile(X, Y);

		LogHandler.log(DESTINATION + "::");

		if (!Players.getLocal().isMoving() || Walking.getDestination().distance() < Random.nextInt(3, 8)) {

			if (Random.nextInt(0, 10) > 8) {
				Camera.turnTo(DESTINATION);
			}
			Walking.walkTileMM(DESTINATION);
		}
		
		Time.sleep(300, 700);

	}

	public static int getTileIndex(Tile[] PATH) {

		int OUT = PATH.length - 1;

		for (int i = OUT - 1; i >= 0; i--) {
			if ((PATH[i].distance() < PATH[OUT].distance())) {
				if (PATH[i].distance() < 5 || PATH[i + 1].isOnMiniMap()) {
					OUT = i + 1;
				}
				OUT = i;
			}
		}
		return OUT + 1;
	}

	public static Tile[] reversePath(Tile[] PATH) {
		Tile[] OUT = new Tile[PATH.length];
		for (int i = 0; i < PATH.length; i++) {
			Tile T = PATH[i];
			LogHandler.log(PATH.length - 1 - i + ":" + T);
			OUT[PATH.length - 1 - i] = T;
		}
		return OUT;
	}

	public void messageReceived(MessageEvent e) {
		if (e.getMessage().toString().contains("Your pouch is full")) {
			Banking.IS_BAG_FILLED = true;
		} else if (e.getMessage().toString().contains("Your pouch has no essence")) {
			Banking.IS_BAG_FILLED = false;
		}
	}

}
