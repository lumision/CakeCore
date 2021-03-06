package cakecore.shop;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.inventory.Inventory;

import cakecore.item.RItem;
import cakecore.main.CakeCore;
import cakecore.main.CakeLibrary;

public class ShopManager 
{
	public static ArrayList<Inventory> openShops = new ArrayList<Inventory>();
	public static ArrayList<Shop> shopDatabase = new ArrayList<Shop>();
	public static final File shopsFile = new File("plugins/CakeCore/shops");

	public ShopManager()
	{

	}
	
	public static Shop getShopWithDB(String dbName)
	{
		for (Shop shop: shopDatabase)
			if (shop.dbName.equalsIgnoreCase(dbName))
				return shop;
		return null;
	}

	public static void readShopDatabase()
	{
		shopDatabase.clear();
		for (File file: shopsFile.listFiles())
		{
			if (!file.getName().endsWith(".yml"))
				continue;
			String shopName = null;
			ArrayList<ShopItem> shopItems = new ArrayList<ShopItem>();
			try
			{
				ArrayList<String> lines = CakeLibrary.readFile(file);
				String header = "";
				for (String s: lines)
				{
					if (s.startsWith(" "))
					{
						s = s.substring(1);
						if (header.equals("items:"))
						{
							String[] split = s.split(", ");
							if (split.length < 3)
								continue;
							RItem ri = CakeCore.getItemFromDatabase(split[0]);
							if (ri == null)
							{
								CakeCore.msgConsole("&4Error while reading shop \"&c" + file.getName() + "&4\" - \"&c" + split[0] + "&4\" is not an item!");
								continue;
							}
							int cost = Integer.parseInt(split[1]);
							int slot = Integer.parseInt(split[2]);
							ShopItem si = new ShopItem(ri, cost, slot);
							shopItems.add(si);
						}
					} else
					{
						String[] split = s.split(": ");
						if (s.startsWith("shopname: "))
						{
							shopName = CakeLibrary.recodeColorCodes(split[1]);
							continue;
						}
						header = s;
					}
				}
			} catch (Exception e) {
				CakeCore.msgConsole("&4Unable to read shop file: " + file.getName());
			}
			Shop shop = new Shop(shopName, shopItems);
			shop.dbName = file.getName().substring(0, file.getName().length() - 4);
			shopDatabase.add(shop);
		}
	}
}
