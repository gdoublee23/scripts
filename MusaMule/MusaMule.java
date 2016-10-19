package MusaMule;

import org.tbot.bot.TBot;
import org.tbot.internal.AbstractScript;
import org.tbot.internal.Manifest;
import org.tbot.internal.handlers.RandomHandler;
import org.tbot.methods.Bank;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Widgets;
import org.tbot.methods.tabs.Inventory;
import org.tbot.methods.walking.Walking;
import org.tbot.wrappers.Player;
import org.tbot.wrappers.WidgetChild;

@Manifest(name = "MusaMule", authors = "MansaMusa")
public class MusaMule extends AbstractScript {

	String NAME = "B3NYAK00V";
	int COINS_AMOUNT = -1;

	public int loop() {

		try {

			TBot.getBot().getScriptHandler().getRandomHandler().get(RandomHandler.TOGGLE_ROOF).disable();

			if (COINS_AMOUNT < 0 || !Inventory.contains("Coins")) {
				withdraw();
			} else {
				trade();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Random.nextInt(100, 700);
	}

	private void trade() {
		if (Bank.isOpen()) {
			Bank.close();
		} else {
			WidgetChild TRADE = Widgets.getWidget(335, 31);
			WidgetChild TRADE2 = Widgets.getWidget(334, 30);
			if ((TRADE != null && TRADE.isVisible()) || (TRADE2 != null && TRADE2.isVisible())) {
				if (TRADE.getText().contains(NAME) || TRADE2.getText().contains(NAME)) {
					if (Inventory.contains("Coins")) {
						Inventory.getFirst("Coins").interact("Offer-all");
					} else {
						WidgetChild ACCEPT = Widgets.getWidget(335, 12);
						WidgetChild ACCEPT2 = Widgets.getWidget(334, 13);
						if (ACCEPT != null && ACCEPT.isVisible()) {
							ACCEPT.click();
						} else if (ACCEPT2 != null && ACCEPT2.isVisible()) {
							ACCEPT2.click();
						}
					}
				} else {
					Walking.walkTileMM(Players.getLocal().getLocation());
				}
			} else {
				Player PLAYER = Players.getLoaded(NAME);
				if (PLAYER != null && PLAYER.getLocation().isOnMiniMap()) {
					if (PLAYER.isOnScreen()) {
						PLAYER.interact("Trade with");
					} else {
						Walking.walkTileMM(PLAYER.getLocation());
					}
				} else {
					TBot.getBot().getScriptHandler().stopScript();
				}
			}
		}
	}

	private void withdraw() {
		if (Bank.isOpen()) {
			if (Bank.getCount("Coins") <= 1 && Inventory.getCount("Coins") <= 1) {
				TBot.getBot().getScriptHandler().stopScript();
			} else if (Inventory.contains("Coins")) {
				if (!Bank.contains("Coins")) {
					int AMOUNT = Random.nextInt(1, Inventory.getCount("Coins") / Random.nextInt(10, 20));
					Bank.deposit("Coins", AMOUNT);
				} else {
					COINS_AMOUNT = Bank.getCount("Coins");
				}
			} else {
				Bank.withdraw("Coins", Bank.getCount("Coins") - 1);
			}
		} else {
			Bank.open();
		}
	}

}
