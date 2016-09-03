package MusaSlaughter;

import java.awt.ItemSelectable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.tbot.internal.handlers.LogHandler;
import org.tbot.methods.GroundItems;
import org.tbot.methods.Players;
import org.tbot.methods.Random;
import org.tbot.methods.Time;
import org.tbot.methods.Widgets;
import org.tbot.methods.tabs.Inventory;
import org.tbot.methods.walking.Walking;
import org.tbot.util.Filter;
import org.tbot.wrappers.GroundItem;
import org.tbot.wrappers.Item;
import org.tbot.wrappers.Widget;
import org.tbot.wrappers.WidgetChild;

public class Loot {

	static boolean IS_BURYING = false;
	static boolean IS_LOOTING = true;

	private static Filter<Item> INV_LOOT_FILTER = new Filter<Item>() {
		public boolean accept(Item i) {
			for (String s : NAMES) {
				if (i.getName().equals("Looting bag") && !Inventory.contains("Looting bag")) {
					return true;
				}
				if (i.getName().equals(s) && !i.getName().equals("Iron knife")) {
					return true;
				}
			}
			return false;
		}
	};

	public static String[] NAMES = {  "Clue scroll(easy)", "Clue scroll(medium)", "Clue scroll(hard)",
			"Clue scroll(elite)", "Mystic boots (light)", "Monkfish", "Dragon bones", "Green dragonhide", "Cosmic rune",
			"Law rune", "Black kiteshield", "Adamant arrow", "Rune arrow", "Torstol seed", "Lantadyme seed",
			"Dwarf weed seed", "Kwuarm seed", "Snapdragon seed", "Ranarr seed", "Avantoe seed", "Limpwurt seed",
			"Looting bag", "Rune med helm", "Law rune", "Grimy avantoe", "Nature rune", "Grimy kwuarm",
			"Grimy ranarr weed", "Grimy cadantine", "Grimy lantadyme", "Champion scroll (lesser demon)",
			"Grimy dwarf weed", "Mithril bolts" };

	public static class Bag {

		public static void run() {

			Item INV_ITEM = Inventory.getFirst(new Filter<Item>() {
				public boolean accept(Item i) {
					if (i.getName().toLowerCase().contains("bones") && IS_BURYING) {
						return true;
					}
					for (String s : NAMES) {
						if (i.getName().equals(s) && !i.getName().equals("Iron knife")) {
							return true;
						}
					}
					return false;
				}
			});

			if (INV_ITEM != null) {
				WidgetChild ITEM = Widgets.getWidget(81, 5);
				if (ITEM.isVisible()) {
					for (WidgetChild w : ITEM.getChildren()) {
						int ID = getId(INV_ITEM.getName());
						if (w != null && w.isValid() && w.getItemID() == ID) {
							LogHandler.log(w.getItemID() + ":" + INV_ITEM.getID());
							w.interact("Store-All");
							Time.sleep(300, 900);
							break;
						}
					}

				} else {
					Item BAG = Inventory.getFirst("Looting bag");
					if (BAG != null) {
						BAG.interact("Deposit");
					} else {
						LogHandler.log("NO BAG! :O");
					}
				}
			}
			Time.sleep(300, 700);
		}

		public static boolean isReady() {
			return Inventory.getFirst(INV_LOOT_FILTER) != null && Players.getLocal().getInteractingCharacterIndex() < 0
					&& Players.getLocal().getAnimation() == -1 && Inventory.contains("Looting bag") && !Loot.isReady();
		}

	}

	private static Filter<GroundItem> LOOT_FILTER = new Filter<GroundItem>() {
		public boolean accept(GroundItem i) {
			if (i.getName().toLowerCase().contains("bones") && IS_BURYING && i.getLocation().distance() <= 8) {
				return true;
			}
			for (String s : NAMES) {
				if (i.getName().equals(s) && i.getLocation().isOnMiniMap()) {
					return true;
				}
			}
			return false;
		}
	};

	public static boolean isReady() {
		if (!IS_LOOTING) {
			return false;
		}
		GroundItem loot = GroundItems.getNearest(LOOT_FILTER);
		if (Bag.isReady()) {
			return true;
		}
		if (Inventory.contains("Bones") || Inventory.contains("Big bones")) {
			return true;
		}
		return loot != null && (loot.isOnScreen() || loot.getLocation().isOnMiniMap());
	}

	public static void run() {

		if (Bag.isReady()) {
			Bag.run();
		} else {

			WidgetChild WIDGET = Widgets.getWidget(81, 2);

			if (!Bag.isReady() && WIDGET.isVisible()) {
				WIDGET.click();
			}

			GroundItem LOOT = GroundItems.getNearest(LOOT_FILTER);
			if (!Inventory.isFull()) {
				if (LOOT != null && LOOT.isOnScreen()) {
					LOOT.interact("Take " + LOOT.getName());
				} else {
					if (Inventory.contains("Bones") || Inventory.contains("Big bones")) {
						String STRING = "Bones";
						if (Inventory.getCount("Big bones") > Inventory.getCount("Bones")) {
							STRING = "Big bones";
						}
						Item BONES = Inventory.getItemClosestToMouse(STRING);
						if (IS_BURYING) {
							BONES.interact("Bury");
						} else {
							BONES.interact("Drop");
						}
						Time.sleep(300, 700);
					}

				}
			} else {
				if (Inventory.contains("Vial")) {
					Inventory.getItemClosestToMouse("Vial").interact("Drop");
				} else if (Inventory.containsOneOf(Eat.FOOD)) {
					Inventory.getFirst(Eat.FOOD).interact("Eat");
				}
			}

		}
		Time.sleep(300, 1600);
	}

	public static int getPrice(String ITEM_NAME) {

		BufferedReader READER;
		URL LINK;
		URLConnection CONNECTION;

		try {
			String SUFFIX = ITEM_NAME.replace(" ", "_");
			LINK = new URL("http://2007.runescape.wikia.com/wiki/Exchange:" + SUFFIX);
			CONNECTION = LINK.openConnection();
			READER = new BufferedReader(new InputStreamReader(CONNECTION.getInputStream()));
			String in;
			while ((in = READER.readLine()) != null) {
				if (in.contains("GEPrice")) {
					int START = in.indexOf("GEPrice") + 9;
					int END = in.indexOf("<", START);
					int PRICE = Integer.parseInt(in.substring(START, END).replace(",", ""));
					return PRICE;
				}
			}
			READER.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static int getId(String ITEM_NAME) {

		BufferedReader READER;
		URL LINK;
		URLConnection CONNECTION;

		try {
			String SUFFIX = ITEM_NAME.replace(" ", "_");
			LINK = new URL("http://2007.runescape.wikia.com/wiki/Exchange:" + SUFFIX);
			CONNECTION = LINK.openConnection();
			READER = new BufferedReader(new InputStreamReader(CONNECTION.getInputStream()));
			String in;
			while ((in = READER.readLine()) != null) {
				if (in.contains("GEDBID")) {
					int START = in.indexOf("GEDBID") + 8;
					int END = in.indexOf("<", START);
					int ID = Integer.parseInt(in.substring(START, END));
					LogHandler.log("id: " + ID);
					return ID;
				}
			}
			READER.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

}
