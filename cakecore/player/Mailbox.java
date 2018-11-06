package cakecore.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cakecore.item.RItem;
import cakecore.kits.RPGKit.TimeTrack;
import cakecore.main.CakeLibrary;

public class Mailbox 
{
	public RPlayer owner;
	public ArrayList<RItem> items;

	private Inventory mailboxInventory;
	public static final String mailboxDescriptionPrefix = "§a§a§a";
	static final String invTitle = CakeLibrary.recodeColorCodes("&2Mailbox");

	public Mailbox(RPlayer owner)
	{
		this.owner = owner;
		items = new ArrayList<RItem>();
	}

	public Inventory getMailboxInventory()
	{
		if (mailboxInventory != null)
			return mailboxInventory;
		mailboxInventory = Bukkit.createInventory(null, 27, invTitle);
		updateMailboxInventory();
		return mailboxInventory;
	}

	public void updateMailboxInventory()
	{
		if (mailboxInventory == null)
		{
			getMailboxInventory();
			return;
		}
		mailboxInventory.clear();
		if (items.size() > 0)
			for (int i = 0; i < Math.min(items.size(), mailboxInventory.getSize()); i++)
				mailboxInventory.setItem(i, items.get(i).createItem());
	}
	
	public static ItemStack updateForRetrieval(ItemStack item)
	{
		ItemMeta im = item.getItemMeta();
		if (im.getLore() == null)
			return item;
		List<String> lore = im.getLore();
		for (int i = 0; i < lore.size(); i++)
		{
			String line = lore.get(i);
			if (line.startsWith(mailboxDescriptionPrefix))
			{
				lore.remove(i);
				i--;
			}
			line = CakeLibrary.removeColorCodes(line);
			if (line.startsWith("Expiry: "))
			{
				line = line.toLowerCase();
				String length = line.split(": ")[1];
				int days = 0;
				int hours = 0;
				int minutes = 0;
				String[] split = length.split(" ");
				for (int i1 = 0; i1 < split.length - 1; i1++)
				{
					try
					{
						int n = Integer.parseInt(split[i1]);

						if (split[i1 + 1].startsWith("month"))
							days += n * 30;
						else if (split[i1 + 1].startsWith("week"))
							days += n * 7;
						else if (split[i1 + 1].startsWith("day"))
							days += n;
						else if (split[i1 + 1].startsWith("hour"))
							hours += n;
						else if (split[i1 + 1].startsWith("minute"))
							minutes += n;
					} catch (Exception e) {}
				}
				lore.set(i, RItem.getExpiryLine(new TimeTrack().getClonedOffset(0, days, hours, minutes)));
			}
		}
		im.setLore(lore);
		item.setItemMeta(im);
		return item;
	}
}
