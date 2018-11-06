package cakecore.item;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cakecore.main.CakeCore;
import cakecore.main.CakeLibrary;

public class ItemPouch 
{
	public static ArrayList<ItemPouch> itemPouches = new ArrayList<ItemPouch>();
	public static final File pouchesFolder = new File("plugins/CakeCore/pouches");

	public int size;
	public int pouchID;
	String idInColorCode;
	Inventory pouchInventory;

	public ItemPouch(int size)
	{
		this.size = size;
		this.pouchID = getNextPouchID();
	}

	public ItemPouch(int pouchID, int size)
	{
		this.pouchID = pouchID;
		this.size = size;
	}

	public Inventory getPouchInventory()
	{
		return pouchInventory != null ? pouchInventory :
			(pouchInventory = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&eItem Pouch" + getIDInColorCode())));
	}

	public int getNextPouchID()
	{
		int highest = -1;
		for (ItemPouch pouch: itemPouches)
			if (pouch.pouchID > highest)
				highest = pouch.pouchID;
		return highest + 1;
	}

	public String getIDInColorCode()
	{
		return idInColorCode != null ? idInColorCode : (idInColorCode = getIDInColorCode(pouchID));
	}

	public static String getIDInColorCode(int pouchID)
	{
		String build = "";
		for (char c: ("" + pouchID).toCharArray())
			build += "§" + c;
		return build;
	}

	public static ItemPouch getPouch(int id)
	{
		for (ItemPouch pouch: itemPouches)
			if (pouch.pouchID == id)
				return pouch;
		return null;
	}

	public static int getIDFromName(String name)
	{
		String build = "";
		char[] name1 = name.toCharArray();
		for (int i = 0; i < name1.length; i++)
			if (name1[i] == '§')
			{
				if (i + 1 < name1.length)
				{
					if (Character.isDigit(name1[i + 1]))
					{
						build += name1[i + 1];
						i++;
					} else
						build = "";
				}

			} else
				build = "";

		if (build.length() == 0)
			return -1;
		try
		{
			return Integer.valueOf(build);
		} catch (Exception e)
		{
			return -1;
		}
	}

	public static void readData()
	{
		if (!pouchesFolder.exists())
			return;
		itemPouches.clear();
		String[] split;
		ItemPouch pouch;
		Inventory inv;
		String itemFileName;
		for (File pouchFolder: pouchesFolder.listFiles())
		{
			try
			{
				split = pouchFolder.getName().split("-");
				pouch = new ItemPouch(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
				inv = pouch.getPouchInventory();
				for (File itemFile: pouchFolder.listFiles())
				{
					itemFileName = itemFile.getName();
					if (itemFileName.endsWith(".yml"))
						inv.setItem(Integer.valueOf(itemFileName.substring(0, itemFileName.length() - 4)), 
								RItem.readFromFile(itemFile).createItem());
				}
				itemPouches.add(pouch);
			} catch (Exception e)
			{
				CakeCore.msgConsole("Error reading pouch folder: " + pouchFolder.getName());
				e.printStackTrace();
			}
		}
	}

	public void writeData()
	{
		File pouchFolder = new File(pouchesFolder.getPath() + "/" + this.pouchID + "-" + this.size);
		if (pouchFolder.exists())
			for (File item: pouchFolder.listFiles())
				item.delete();
		pouchFolder.mkdirs();
		if (pouchInventory != null)
			for (int i = 0; i < pouchInventory.getSize(); i++)
			{
				ItemStack is = pouchInventory.getItem(i);
				if (!CakeLibrary.isItemStackNull(is))
					(new RItem(is)).saveToFile(new File(pouchFolder.getPath() + "/" + i + ".yml"));
			}
	}
}
