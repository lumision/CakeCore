package cakecore.player;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cakecore.buff.BuffInventory;
import cakecore.main.CakeCore;
import cakecore.main.CakeLibrary;

public class RPGParty 
{
	public static ArrayList<RPGParty> parties = new ArrayList<RPGParty>();
	public static final File partiesFile = new File("plugins/CakeCore/parties.yml");
	public RPlayer host;
	public ArrayList<RPlayer> members;
	public ArrayList<UUID> invites;
	public int partyID;
	public Inventory partyInventory;
	public RPGParty(int partyID, RPlayer host)
	{
		this.members = new ArrayList<RPlayer>();
		this.invites = new ArrayList<UUID>();
		this.host = host;
		host.partyID = partyID;
		this.members.add(host);
		this.partyID = partyID;
		setPartyInventory();
	}

	public static RPGParty getParty(int id)
	{
		for (RPGParty p: parties)
			if (p.partyID == id)
				return p;
		return null;
	}

	public static RPGParty createNewParty(RPlayer host)
	{
		int id = 0;
		while(getParty(id) != null)
			id++;
		RPGParty p = new RPGParty(id, host);
		parties.add(p);
		writeData();
		return p;
	}

	public void setPartyInventory()
	{
		this.partyInventory = Bukkit.createInventory(null, 9, CakeLibrary.recodeColorCodes("&5Party Info"));
		updatePartyInventory();
	}

	public void updatePartyInventory()
	{
		if (partyID == -1)
			return;
		for (int i = 0; i < members.size(); i++)
		{
			RPlayer rp = members.get(i);
			ItemStack is = BuffInventory.getPlayerStatsIcon(rp);
			if (rp == host)
				is = CakeLibrary.renameItem(is, CakeLibrary.getItemName(is) + "&7 (Host)");
			partyInventory.setItem(i, is);
		}
	}

	public void addPlayer(RPlayer rp)
	{
		members.add(rp);
		rp.partyID = this.partyID;;
		RPlayerManager.writeData(rp);
		writeData();
	}

	public void removePlayer(RPlayer rp)
	{
		members.remove(rp);
		rp.partyID = -1;
		RPlayerManager.writeData(rp);
		writeData();
	}

	public void broadcastMessage(String msg)
	{
		for (RPlayer rp: members)
		{
			Player p = rp.getPlayer();
			if (p != null)
				msg(p, msg);
		}
	}

	public static void msg(Player p, String msg)
	{
		p.sendMessage(CakeLibrary.recodeColorCodes("&5[&dParties&5] &d" + msg));
	}

	public void disbandParty()
	{
		for (RPlayer rp: members)
		{
			rp.partyID = -1;
			Player p = rp.getPlayer();
			if (p != null)
				msg(p, "Party has been disbanded.");
			RPlayerManager.writeData(rp);
		}
		this.partyID = -1;
		this.members.clear();
		parties.remove(this);
	}

	public static void readData()
	{
		parties.clear();
		ArrayList<String> lines = CakeLibrary.readFile(partiesFile);
		for (String line: lines)
		{
			try
			{
				String[] split = line.split("::");
				RPGParty party = new RPGParty(Integer.valueOf(split[0]), RPlayerManager.getRPlayer(UUID.fromString(split[2])));
				String[] split1 = split[1].split(", ");
				for (String member: split1)
					if (!member.equals(split[2]))
						party.members.add(RPlayerManager.getRPlayer(UUID.fromString(member)));
				parties.add(party);
			} catch (Exception e)
			{
				CakeCore.msgConsole("Error reading party data line: &4" + line);
				e.printStackTrace();
			}
		}
	}

	public static void writeData()
	{
		ArrayList<String> lines = new ArrayList<String>();
		for (RPGParty party: parties)
		{
			String line = party.partyID + "::";
			for (RPlayer member: party.members)
				line += member.getUniqueID() + ", ";
			line = line.substring(0, line.length() - 2);
			line += "::" + party.host.getUniqueID();
			lines.add(line);
		}
		CakeLibrary.writeFile(lines, partiesFile);
	}
}
