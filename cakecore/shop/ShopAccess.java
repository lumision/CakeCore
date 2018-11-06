package cakecore.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cakecore.main.CakeLibrary;

public class ShopAccess
{
	public static final String itemName = "Exchange Access";
	static final String[] shopList = {
			"&aBlocks",
			"&bMaterials",
	};
	static Inventory shopSelection;
	
	public static void handleShopAccessOpen(Player p)
	{
		if (shopSelection != null)
		{
			p.openInventory(shopSelection);
			return;
		}
		shopSelection = Bukkit.createInventory(null, 9, CakeLibrary.recodeColorCodes("&1" + itemName));
		for (int i = 0; i < shopList.length; i++)
		{
			//Bukkit.broadcastMessage(ShopManager.getShopWithDB(shopList[i].substring(2)).shopName);
			//Bukkit.broadcastMessage(ShopManager.getShopWithDB(shopList[i].substring(2)).shopItems.get(0).item.itemVanilla.getTypeId() + "");
			shopSelection.setItem(i, CakeLibrary.renameItem(
					new ItemStack(ShopManager.getShopWithDB(shopList[i].substring(2)).shopItems.get(0).item.itemVanilla.getType()),
					"&fShop&7: " + shopList[i]));
		}
		shopSelection.setItem(8, CakeLibrary.renameItem(new ItemStack(Material.GOLD_NUGGET), "&6Sell Items"));
		p.openInventory(shopSelection);
	}
	
	public static Inventory getShopSelection()
	{
		if (shopSelection != null)
			return shopSelection;
		return null;
	}
}
