package cakecore.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.DataException;

import cakecore.advancement.AdvancementAPI;
import cakecore.advancement.Trigger;
import cakecore.advancement.Trigger.TriggerType;
import cakecore.areas.Area;
import cakecore.areas.Arena;
import cakecore.areas.ArenaInstance;
import cakecore.classes.ClassInventory;
import cakecore.classes.RPGClass;
import cakecore.entities.mobs.RPGMonster;
import cakecore.entities.mobs.RPGMonsterSpawn;
import cakecore.item.BonusStat.BonusStatCrystal;
import cakecore.item.EnhancementInventory;
import cakecore.item.GlobalGift;
import cakecore.item.ItemPouch;
import cakecore.item.ItemSwap;
import cakecore.item.RItem;
import cakecore.kits.RPGKit;
import cakecore.kits.RPGKit.TimeTrack;
import cakecore.npc.ConversationData;
import cakecore.npc.CustomNPC;
import cakecore.npc.NPCManager;
import cakecore.player.RPGParty;
import cakecore.player.RPlayer;
import cakecore.player.RPlayerManager;
import cakecore.previewchests.PreviewChestManager;
import cakecore.recipes.RPGRecipe;
import cakecore.shop.GuildShop;
import cakecore.shop.Shop;
import cakecore.shop.ShopManager;
import cakecore.sideclasses.RPGSideClass;
import cakecore.skillinventory2.SkillInventory2;
import cakecore.skills.RPGSkill;
import cakecore.songs.RPGSong;
import cakecore.songs.RSongManager;
import cakecore.songs.RunningTrack;

public class CakeCore extends JavaPlugin
{
	public static final File pluginFolder = new File("plugins/CakeCore");
	public static final File itemsFolder = new File("plugins/CakeCore/items");
	public static ArrayList<RItem> itemDatabase = new ArrayList<RItem>();
	public static RPGEvents events;
	public static RPGListener listener;
	public static RPlayerManager playerManager;
	public static RSongManager songManager;
	public static PreviewChestManager previewChestManager;
	public static CakeCore instance;
	public static Random rand = new Random();
	public static Timer timer = new Timer();
	public static NPCManager npcManager;
	public static long serverAliveTicks;
	public static int arenaSpawnTestIndex = 0;
	public static int maxMobsPer64Radius = 48;
	public static boolean inventoryButtons = true;
	public static boolean bonusStatEnabled = true;

	public File configFile = new File("plugins/CakeCore/config.yml");
	public static String areaInstanceWorld;

	public File headsFile = new File("plugins/CakeCore/Heads.yml");
	public static HashMap<String, String> heads;

	static final String[] helpGold = new String[] { 
			"&6===[&e /gold Help &6]===", 
			"&6/gold withdraw/w <amt>: &eWithdraws Gold into item form"
	};

	static final String[] helpGoldAdmin = new String[] { 
			"&6/gold add/a <amt> [player]: &eAdds Gold to a player",  
			"&6/gold remove/r <amt> [player]: &eRemoves Gold from a player"
	};

	static final String[] helpParty = new String[] { 
			"&5===[&d /party (/p) Help &5]===",  
			"&5/party info/i: &dGives info about the party you're in",
			"&5/party join/j <host>: &dJoins a party if you were invited",
			"&5/party leave/l: &dLeaves the party as a member",
			"&5/party create/c: &dCreates a party with you as the host",
			"&5/party invite/inv <player>: &dInvites a player to your party (Host)",
			"&5/party kick/k <player>: &dKicks a player from your party (Host)",
			"&5/party sethost/sh <player>: &dPasses host to a member (Host)",
			"&5/party disband/d: &dDisbands your party (Host)",
			"&dBegin a message with '\\' to chat in the party."
	};

	static final String[] helpItem = new String[] { 
			"&6===[&e /item Help &6]===",
			"&6/item tier <tier>",
			"&6/item lvrequirement <level>",
			"&6/item magicDamage <damage>",
			"&6/item meleeDamage <damage>",
			"&6/item attackSpeed <multiplier>",
			"&6/item critChance <percentage>",
			"&6/item critDamage <percentage>",
			"&6/item recoverySpeed <percentage>",
			"&6/item cooldownReduction <percentage>",
			"&6/item damageReduction <percentage>",
			"&6/item xpMultiplier <multiplier>",
			"&6/item unbreakable",
			"&6/item accessory",
			"&6/item d/desc <l/list / d/del / lineNumber> <lineNumber/newDesc>",
			"&eTip: You can use TAB to auto-complete"
	};

	static final String[] helpArea = new String[] { 
			"&4===[&c /area Help &4]===",  
			"&4/area pos1/pos2: &cSets the pos1 or pos2 of a creating area",
			"&4/area create <name>: &cCreates an area",
			"&4/area del <areaName>: &cDeletes an area",
			"&4/area setpos <areaName>: &cSets the new position of an area",
			"&4/area list: &cLists all areas",
			"&4/area editable <areaName>: &cToggles the editability of an area",
			"&4/area bgm <areaName> <bgmName>: &cSets the BGM of an area"
	};

	static final String[] helpArena = new String[] { 
			"&6===[&e /arena Help &6]===",  
			"&6/arena list: &eLists all the arenas",
			"&6/arena create <schematicName>: &eCreates an arena based on an existing WorldEdit schematic",
			"&6/arena del <arenaName>: &eDelets an arena",
			"&6/arena setspawnrotation <arenaName>: &eSets the spawn rotation of an arena to where you're facing",
			"&6/arena tpspawntest <arenaName>: &eTeleports you to the test location for adding mob spawns",
			"&6/arena addmobspawn <arenaName> <mobName>: &eAdds a mob spawn based on your current location",
			"&6/arena enter <arenaName>: &eEnters an instance of an arena if it's been created",
			"&6/arena leave: &eLeaves your current arena",
			"&6Tip: You can use TAB to auto-complete"
	};

	static final String[] helpNPC = new String[] { 
			"&6===[&e /npc Help &6]===",  
			"&6/npc create <npcName>: &eCreates an NPC",
			"&6/npc skin <skinName>: &eSets the skin of an NPC (tab-completable)",
			"&6/npc rename <npcName>: &eRenames an NPC",
			"&6/npc del: &eDeletes an NPC",
			"&6/npc lockRotation: &eToggles head rotation lock on an NPC",
			"&6/npc chatRange <blocks>: &eSets the chat range for an NPC",
			"&6/npc databaseName <databaseName>: &eSets the databaseName for an NPC",
			"&6Tip: You can use TAB to auto-complete"
	};

	public static NamespacedKey key;

	public static void main(String[] args) {}

	@Override
	public void onEnable()
	{
		CakeCore.instance = this;
		key = new NamespacedKey(this, "CakeCore");
		pluginFolder.mkdirs();
		playerManager = new RPlayerManager(this);
		
		RPGClass.setXPTable();
		RPGSideClass.setXPTable();
		readItemDatabase();
		readConfig();
		readHeads();
		RPGMonsterSpawn.onEnable();
		ShopManager.readShopDatabase();
		ConversationData.readConversationData();
		RPGRecipe.readRecipeData();
		Arena.readArenaData();
		ArenaInstance.readData();
		GuildShop.readItemPrices();
		ItemSwap.readData();
		GlobalGift.readData();
		RPGKit.readData();
		Area.readData();
		ItemPouch.readData();
		RPGParty.readData();

		events = new RPGEvents(this);
		previewChestManager = new PreviewChestManager(this);
		songManager = new RSongManager(this);
		listener = new RPGListener(this);
		getServer().getPluginManager().registerEvents(listener, this);
		RPGEvents.stopped = false;
		
		if (heads.containsKey("mailbox"))
			RPlayer.invMailbox = CakeLibrary.renameItem(CakeLibrary.getSkullWithTexture(heads.get("mailbox")), "&a&nMailbox");

		if (areaInstanceWorld != null && areaInstanceWorld.length() > 0)
			Bukkit.getServer().createWorld(new WorldCreator(areaInstanceWorld));

		for (File file: new File(".").listFiles())
		{
			String name = file.getName();
			if (name.startsWith("world_") && !name.contains("."))
			{
				if (Bukkit.getWorld(name.substring(6)) != null)
					continue;
				Bukkit.getServer().createWorld(new WorldCreator(name));
			}
		}

		npcManager = new NPCManager(this);

		for (World world: Bukkit.getWorlds())
		{
			world.setGameRuleValue("keepInventory", "true");
			world.setGameRuleValue("doFireTick", "false");
			world.setGameRuleValue("doWeatherCycle", "true");
			world.setGameRuleValue("mobGriefing", "false");
			world.setGameRuleValue("sendCommandFeedback", "false");
			world.setGameRuleValue("maxEntityCramming", "0");
		}

		if (Bukkit.getMonsterSpawnLimit() < 512)
			RPGEvents.scheduleRunnable(new RPGEvents.Message(Bukkit.getConsoleSender(), 
					"&c[NOTE] &4spawn-limit&c: &4monsters&c in &4bukkit.yml&c is set to &4" + Bukkit.getMonsterSpawnLimit() + "&c; recommended value is &41024&c or higher."), 10);
		if (Bukkit.getTicksPerMonsterSpawns() < 3 || Bukkit.getTicksPerMonsterSpawns() > 10)
			RPGEvents.scheduleRunnable(new RPGEvents.Message(Bukkit.getConsoleSender(), 
					"&c[NOTE] &4ticks-per&c: &4monster-spawns&c in &4bukkit.yml&c is set to &4" + Bukkit.getTicksPerMonsterSpawns() + "&c; recommended value is &45&c."), 11);
		try
		{
			File file = new File("spigot.yml");
			for (String line: CakeLibrary.readFile(file))
			{
				line = line.replace(" ", "");
				if (line.startsWith("max:"))
				{
					String[] split = line.split(":");
					if (Float.valueOf(split[1]) < 999999999)
					{
						RPGEvents.scheduleRunnable(new RPGEvents.Message(Bukkit.getConsoleSender(), 
								"&c[NOTE] &4maxHealth&c, &4movementSpeed&c, or &4attackDamage&c variable in &4spigot.yml&c is not optimally set; recommended value is &4999999999&c (nine 9s) for all 3."), 12);
						break;
					}
				}
			}
		} catch (Exception e) {}

		RPGEvents.scheduleRunnable(new RPGEvents.Message(Bukkit.getConsoleSender(), "&cCakeCraft has finished launching."), 20);

	}

	@Override
	public void onDisable()
	{
		for (Player p: Bukkit.getOnlinePlayers())
			if (CakeLibrary.hasColor(p.getOpenInventory().getTitle()))
			{
				listener.handleInventoryClose(new InventoryCloseEvent(p.getOpenInventory()));
				p.closeInventory();
			}
		RPGEvents.stopped = true;
		writeConfig();
		npcManager.onDisable();
		RPlayerManager.writeData();
		previewChestManager.writeData();
		for (RPGMonster ce: RPGMonster.entities)
			ce.entity.remove();
	}

	public static ItemStack getGoldItem(int amount)
	{
		ItemStack gold = new ItemStack(Material.GOLD_NUGGET);
		return CakeLibrary.editNameAndLore(gold, "&6Gold &e(" + CakeLibrary.seperateNumberWithCommas(amount, false) + ")",
				"&7&oInteract with to store");
	}

	public void readHeads()
	{
		heads = new HashMap<String, String>();
		if (!headsFile.exists())
		{
			writeHeads();
			return;
		}
		ArrayList<String> lines = CakeLibrary.readFile(headsFile);
		try
		{
			for (String line: lines)
			{
				String[] split = line.split(": ");
				if (split.length < 2)
					continue;
				heads.put(split[0].toLowerCase(), split[1]);
			}
		} catch (Exception e) 
		{
			CakeCore.msgConsole("Error reading heads file.");
		}
	}

	public void writeHeads()
	{
		try {
			headsFile.createNewFile();
		} catch (IOException e) {}
	}

	public void readConfig()
	{
		if (!configFile.exists())
		{
			writeConfig();
			return;
		}
		ArrayList<String> lines = CakeLibrary.readFile(configFile);
		try
		{
			for (String line: lines)
			{
				String[] split = line.split(": ");
				if (split[0].equalsIgnoreCase("areaInstanceWorld"))
					areaInstanceWorld = split[1];
				if (split[0].equalsIgnoreCase("arenaSpawnTestIndex"))
					arenaSpawnTestIndex = Integer.valueOf(split[1]);
				if (split[0].equalsIgnoreCase("inventoryButtons"))
					inventoryButtons = Boolean.valueOf(split[1]);
				if (split[0].equalsIgnoreCase("maxMobsPer64Radius"))
					maxMobsPer64Radius = Integer.valueOf(split[1]);
				if (split[0].equalsIgnoreCase("bonusStatEnabled"))
					bonusStatEnabled = Boolean.valueOf(split[1]);
			}
		} catch (Exception e) 
		{
			CakeCore.msgConsole("Error reading config file.");
		}
	}

	public void writeConfig()
	{
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("areaInstanceWorld: world_flat");
		lines.add("arenaSpawnTestIndex: " + arenaSpawnTestIndex);
		lines.add("inventoryButtons: " + inventoryButtons);
		lines.add("maxMobsPer64Radius: " + maxMobsPer64Radius);
		lines.add("bonusStatEnabled: " + bonusStatEnabled);
		CakeLibrary.writeFile(lines, configFile);
		readConfig();
	}

	public void readItemFolder(File folder)
	{
		if (folder == null)
			return;
		if (folder.listFiles() == null)
			return;
		for (File file: folder.listFiles())
		{
			if (!file.getName().contains("."))
			{
				readItemFolder(file);
				continue;
			}
			if (!file.getName().endsWith(".yml"))
				continue;
			itemDatabase.add(RItem.readFromFile(file));
		}
	}

	public void readItemDatabase()
	{
		itemDatabase.clear();
		readItemFolder(itemsFolder);
	}

	public static RItem getItemFromDatabase(String databaseName)
	{
		for (RItem run: itemDatabase)
			if (run.databaseName.equalsIgnoreCase(databaseName))
				return run;
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args)
	{
		if (command.getName().equalsIgnoreCase("colors"))
		{
			CakeCore.msgNoTag(sender, "&11&22&33&44&55&66&77&88&99&00&aa&bb&cc&dd&ee&ff k&kk&f &ll&f&mm&f&nn&f&oo");
			return true;
		}
		if (command.getName().equalsIgnoreCase("rr"))
		{
			if (!sender.hasPermission("rpgcore.rr"))
			{
				msg(sender, "You do not have permissions to do this.");
				return true;
			}
			readConfig();
			readHeads();
			readItemDatabase();
			npcManager.readSkinDatas();
			ShopManager.readShopDatabase();
			songManager.readSongs();
			for (CustomNPC npc: NPCManager.npcs)
				npc.conversationData = null;
			ConversationData.readConversationData();
			RPGRecipe.readRecipeData();
			msg(sender, "Reloaded CakeCore v" + getDescription().getVersion());
			return true;
		}
		if (command.getName().equalsIgnoreCase("getitem"))
		{
			if (!sender.hasPermission("rpgcore.item"))
			{
				msg(sender, "You do not have permissions to do this.");
				return true;
			}
			if (args.length < 1)
			{
				msg(sender, "Usage: /getitem <item> [player]");
				return true;
			}
			if (args.length < 2 && !(sender instanceof Player))
			{
				msg(sender, "Usage: /getitem <item> <player>");
				return true;
			}
			Player p = args.length > 1 ? Bukkit.getPlayer(args[1]) : (Player) sender;
			if (p == null)
			{
				msg(sender, "That player is not online or does not exist.");
				return true;
			}
			RItem ri = getItemFromDatabase(args[0]);
			if (ri == null)
			{
				msg(sender, "This item does not exist in the database");
				return true;
			}
			p.getInventory().addItem(ri.createItem());	
			msg(sender, "Item sent.");
			return true;
		}
		if (sender instanceof Player)
		{
			Player p = (Player) sender;
			if (CakeLibrary.hasColor(p.getOpenInventory().getTitle()))
			{
				msg(p, "You're currently in an inventory and commands cannot be executed.");
				return true;
			}
			RPlayer rp = RPlayerManager.getRPlayer(p.getUniqueId());
			if (rp == null)
				return false;
			if (command.getName().equalsIgnoreCase("mobdrops"))
			{
				if (!p.hasPermission("rpgcore.mobdrops"))
				{
					msg(p, "No permissions.");
					return true;
				}
				if (args.length < 1)
				{
					msg(p, "Usage: /mobdrops <mobName> (tab-completable)");
					return true;
				}
				RPGMonsterSpawn spawn = RPGMonsterSpawn.getRPGMonsterSpawn(args[0]);
				if (spawn == null)
				{
					msg(p, "That mob does not exist");
					return true;
				}
				p.openInventory(spawn.getDropsInventory());
				return true;
			}
			if (command.getName().equalsIgnoreCase("enhance"))
			{
				p.openInventory(EnhancementInventory.getNewInventory());
				return true;
			}
			if (command.getName().equalsIgnoreCase("eq"))
			{
				if (!p.hasPermission("rpgcore.eq"))
				{
					msg(p, "You don't have permissions to use this!");
					return true;
				}
				if (args.length < 1)
				{
					msg(p, "Usage: /eq <player>");
					return true;
				}
				RPlayer target = RPlayerManager.getRPlayer(args[0]);
				if (target == null)
				{
					msg(p, "That player does not exist or is not online.");
					return true;
				}
				if (target.getPlayer() == null)
				{
					msg(p, "That player does not exist or is not online.");
					return true;
				}
				Inventory inv = Bukkit.createInventory(null, 9, CakeLibrary.recodeColorCodes("&4Equipment - " + target.getPlayerName()));
				inv.setItem(0, target.getPlayer().getEquipment().getItemInOffHand());
				inv.setItem(1, target.getPlayer().getEquipment().getHelmet());
				inv.setItem(2, target.getPlayer().getEquipment().getChestplate());
				inv.setItem(3, target.getPlayer().getEquipment().getLeggings());
				inv.setItem(4, target.getPlayer().getEquipment().getBoots());

				for (int i = 0; i < 3; i++)
					if (target.accessoryInventory.slots[i] != null)
						inv.setItem(8 - i, target.accessoryInventory.slots[i].createItem());
				p.openInventory(inv);
				return true;
			}
			if (command.getName().equalsIgnoreCase("buffs"))
			{
				Inventory inv = rp.buffInventory.getInventory();
				if (args.length > 0)
				{
					RPlayer target = RPlayerManager.getRPlayer(args[0]);
					if (target == null)
					{
						msg(p, "That player does not exist.");
						return true;
					}
					if (target.getPlayer() == null)
					{
						msg(p, "That player is not online.");
						return true;
					}
					inv = target.buffInventory.getInventory();
					target.buffInventory.updateInventory();
				} else
					rp.buffInventory.updateInventory();
				p.openInventory(inv);
				return true;
			}
			if (command.getName().equalsIgnoreCase("testadv"))
			{
				if (!p.isOp())
				{
					msg(p, "No permissions.");
					return true;
				}
				AdvancementAPI api = AdvancementAPI.builder(key).announce(true).description(args[0]).title(args[0]).trigger(Trigger.builder(TriggerType.TICK, args[0])).build();
				for (String s: p.getAdvancementProgress(api.getAdvancement()).getRemainingCriteria())
					msgNoTag(p, s);
				msg(p, "Attempted");
				return true;
			}
			if (command.getName().equalsIgnoreCase("skull"))
			{
				if (!p.hasPermission("rpgcore.skull"))
				{
					msg(p, "No permissions.");
					return true;
				}
				if (args.length < 1)
				{
					msg(p, "Usage: /skull <headName>");
					return true;
				}
				if (!heads.containsKey(args[0]))
				{
					msg(p, "That head is not a part of the database.");
					return true;
				}
				String value = heads.get(args[0]);
				p.getInventory().addItem(CakeLibrary.getSkullWithTexture(value));
				msg(p, "Skull obtained.");
				return true;
			}
			if (command.getName().equalsIgnoreCase("area"))
			{
				if (!p.hasPermission("rpgcore.area"))
				{
					msg(p, "No permissions.");
					return true;
				}
				if (args.length == 0)
				{
					msgNoTag(p, helpArea);
					return true;
				}
				if (args[0].equalsIgnoreCase("pos1"))
				{
					rp.pos1 = p.getLocation();
					msg(p, "Position 1 set.");
					return true;
				}
				if (args[0].equalsIgnoreCase("pos2"))
				{
					rp.pos2 = p.getLocation();
					msg(p, "Position 2 set.");
					return true;
				}
				if (args[0].equalsIgnoreCase("create"))
				{
					if (args.length <  2)
					{
						msg(p, "Usage: /area create <areaName>");
						return true;
					}
					if (rp.pos1 == null || rp.pos2 == null)
					{
						msg(p, "Set pos1 and pos2 with \"/area pos1/pos2\" first");
						return true;
					}
					Area.areas.add(new Area(args[1], p.getWorld().getName(), 
							(int) Math.min(rp.pos1.getX(), rp.pos2.getX()),
							(int) Math.max(rp.pos1.getX(), rp.pos2.getX()),
							(int) Math.min(rp.pos1.getZ(), rp.pos2.getZ()),
							(int) Math.max(rp.pos1.getZ(), rp.pos2.getZ())));
					Area.writeData();
					msg(p, "Area \"" + args[1] + "\" created.");
					return true;
				}
				if (args[0].equalsIgnoreCase("editable"))
				{
					if (args.length <  2)
					{
						msg(p, "Usage: /area editable <areaName>");
						return true;
					}
					Area area = null;
					for (Area check: Area.areas)
						if (check.name.equalsIgnoreCase(args[1]))
							area = check;
					if (area == null)
					{
						msg(p, "Area \"" + args[1] + "\" does not exist.");
						return true;
					}
					area.editable = !area.editable;
					msg(p, "Area editability: " + area.editable);
					return true;
				}
				if (args[0].equalsIgnoreCase("bgm"))
				{
					if (args.length <  3)
					{
						msg(p, "Usage: /area bgm <areaName> <bgmName>");
						return true;
					}
					Area area = null;
					for (Area check: Area.areas)
						if (check.name.equalsIgnoreCase(args[1]))
							area = check;
					if (area == null)
					{
						msg(p, "Area \"" + args[1] + "\" does not exist.");
						return true;
					}
					area.bgm = args[2];
					msg(p, "Area BGM set to: " + area.bgm);
					Area.writeData();
					return true;
				}
				if (args[0].equalsIgnoreCase("setpos"))
				{
					if (args.length <  2)
					{
						msg(p, "Usage: /area setpos <areaName>");
						return true;
					}
					if (rp.pos1 == null || rp.pos2 == null)
					{
						msg(p, "Set pos1 and pos2 with \"/area pos1/pos2\" first");
						return true;
					}
					Area area = null;
					for (Area check: Area.areas)
						if (check.name.equalsIgnoreCase(args[1]))
							area = check;
					area.world = p.getWorld().getName();
					area.minX = (int) Math.min(rp.pos1.getX(), rp.pos2.getX());
					area.maxX = (int) Math.max(rp.pos1.getX(), rp.pos2.getX());
					area.minZ = (int) Math.min(rp.pos1.getZ(), rp.pos2.getZ());
					area.maxZ = (int) Math.max(rp.pos1.getZ(), rp.pos2.getZ());
					Area.writeData();
					msg(p, "New area position set.");
					return true;
				}
				if (args[0].equalsIgnoreCase("del"))
				{
					if (args.length <  2)
					{
						msg(p, "Usage: /area del <areaName>");
						return true;
					}
					Area area = null;
					for (Area check: Area.areas)
						if (check.name.equalsIgnoreCase(args[1]))
							area = check;
					Area.areas.remove(area);
					Area.writeData();
					msg(p, "Area deleted.");
					return true;
				}
				if (args[0].equalsIgnoreCase("list"))
				{
					msgNoTag(p, "&4===[ &cAreas &4]===");
					for (Area area: Area.areas)
						msgNoTag(p, "&4 - &c" + area.name + "");
					return true;
				}
				msgNoTag(p, helpArea);
				return true;
			}
			if (command.getName().equalsIgnoreCase("gold"))
			{
				if (args.length == 0)
				{
					msgNoTag(p, helpGold);
					if (p.hasPermission("rpgcore.gold"))
						msgNoTag(p, helpGoldAdmin);
					return true;
				}
				if (args[0].equalsIgnoreCase("withdraw") || args[0].equalsIgnoreCase("w"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /gold withdraw/w <amt>");
						return true;
					}
					if (!CakeLibrary.playerHasVacantSlots(p))
					{
						msg(p, "You need inventory slots to withdraw Gold");
						return true;
					}
					int amount = 0;
					try
					{
						amount = Integer.parseInt(args[1]);
					} catch (Exception e)
					{
						msg(p, "Enter a number.");
						return true;
					}
					if (amount > rp.getGold())
					{
						msg(p, "You do not have that amount of money.");
						return true;
					}
					p.getInventory().addItem(getGoldItem(amount));
					rp.addGold(-amount);
					msg(p, "You've withdrawn &6" + amount + " &6Gold&e.");
					return true;
				}
				if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("a"))
				{
					if (!p.hasPermission("rpgcore.gold"))
					{
						msg(p, "No permissions.");
						return true;
					}
					if (args.length < 2)
					{
						msg(p, "Usage: /gold add/a <amt> [player]");
						return true;
					}
					int amount = 0;
					try
					{
						amount = Integer.parseInt(args[1]);
					} catch (Exception e)
					{
						msg(p, "Enter a number.");
						return true;
					}
					RPlayer target = rp;
					if (args.length > 2)
					{
						target = RPlayerManager.getRPlayer(args[2]);
						if (target == null)
						{
							msg(p, "That player does not exist");
							return true;
						}
					}
					target.addGold(amount);
					msg(p, "You've added &6" + amount + " Gold &eto &6" + target.getPlayerName() + "&e's account.");
					return true;
				}
				if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("r"))
				{
					if (!p.hasPermission("rpgcore.gold"))
					{
						msg(p, "No permissions.");
						return true;
					}
					if (args.length < 2)
					{
						msg(p, "Usage: /gold remove/r <amt> [player]");
						return true;
					}
					int amount = 0;
					try
					{
						amount = Integer.parseInt(args[1]);
					} catch (Exception e)
					{
						msg(p, "Enter a number.");
						return true;
					}
					RPlayer target = rp;
					if (args.length > 2)
					{
						target = RPlayerManager.getRPlayer(args[2]);
						if (target == null)
						{
							msg(p, "That player does not exist");
							return true;
						}
					}
					target.addGold(-amount);
					msg(p, "You've removed &6" + amount + " Gold &efrom &6" + target.getPlayerName() + "&e's account.");
					return true;
				}
				msgNoTag(p, helpGold);
				if (p.hasPermission("rpgcore.gold"))
					msgNoTag(p, helpGoldAdmin);
				return true;
			}
			if (command.getName().equalsIgnoreCase("shop"))
			{
				if (!p.hasPermission("rpgcore.shop"))
				{
					msg(p, "You do not have permissions to do this.");
					return true;
				}
				if (args.length == 0)
				{
					msgNoTag(p, "&cShop list:");
					for (Shop shop: ShopManager.shopDatabase)
						msgNoTag(p, "&c - " + shop.dbName);
					return true;
				}
				Shop shop = ShopManager.getShopWithDB(args[0]);
				if (shop == null)
				{
					msgNoTag(p, "&cShop list:");
					for (Shop shop1: ShopManager.shopDatabase)
						msgNoTag(p, "&c - " + shop1.dbName);
					return true;
				}
				p.openInventory(shop.getShopInventory());
				return true;
			}
			if (command.getName().equalsIgnoreCase("skillbook"))
			{
				if (!p.hasPermission("rpgcore.skillbook"))
				{
					msg(p, "You do not have permissions to do this.");
					return true;
				}
				if (args.length < 1)
				{
					msg(p, "Usage: /skillbook <skillname>");
					return true;
				}
				RPGSkill skill = null;
				for (RPGSkill check: RPGSkill.skillList)
					if (check.skillName.toLowerCase().replace(" ", "").equals(args[0].toLowerCase()))
						skill = check;
				if (skill == null)
				{
					msg(p, "Skill does not exist");
					return true;
				}
				p.getInventory().addItem(skill.getSkillbook());
				msg(p, "Skillbook for \"" + skill.skillName + "\" received.");
				return true;
			}
			if (command.getName().equalsIgnoreCase("pc"))
			{
				if (!p.hasPermission("rpgcore.previewchest"))
				{
					msg(p, "You do not have permissions to do this.");
					return true;
				}
				Block b = p.getTargetBlock(CakeLibrary.getPassableBlocks(), 6);
				Location l = b.getLocation();
				if (!(b.getState() instanceof Chest))
				{
					msg(p, "The target block is not a chest (" + b.getType() + ")");
					return true;
				}

				Chest chest = (Chest) b.getState();
				InventoryHolder ih = chest.getInventory().getHolder();

				if (ih instanceof DoubleChest)
				{
					DoubleChest dchest = (DoubleChest) ih;
					Location left = ((Chest) dchest.getLeftSide()).getBlock().getLocation();
					Location right = ((Chest) dchest.getRightSide()).getBlock().getLocation();
					Location getLeft = previewChestManager.getPreviewChest(left);
					Location getRight = previewChestManager.getPreviewChest(right);
					if (getLeft == null && getRight == null)
					{
						previewChestManager.previewChests.add(left);
						msg(p, "Preview chest added.");
					} else
					{
						if (getLeft != null)
							previewChestManager.previewChests.remove(getLeft);
						if (getRight != null)
							previewChestManager.previewChests.remove(getRight);
						msg(p, "Preview chest removed.");
					}
				} else
				{
					Location get = previewChestManager.getPreviewChest(l);
					if (get == null)
					{
						previewChestManager.previewChests.add(l);
						msg(p, "Preview chest added.");
					} else 
					{
						previewChestManager.previewChests.remove(get);
						msg(p, "Preview chest removed.");
					}
					previewChestManager.writeData();
				}
				return true;
			}
			if (command.getName().equalsIgnoreCase("saveitem"))
			{
				if (!p.hasPermission("rpgcore.item"))
				{
					msg(p, "You do not have permissions to do this.");
					return true;
				}
				ItemStack is = p.getItemInHand();
				if (CakeLibrary.isItemStackNull(is))
				{
					msg(p, "Hold an item to save.");
					return true;
				}
				if (args.length < 1)
				{
					msg(p, "Usage: /saveitem <filename>");
					return true;
				}
				if (args[0].equalsIgnoreCase("retardlumi"))
				{
					for (RItem item: itemDatabase)
						if (item != null)
							if (item.file != null)
								item.saveToFile(item.file);
					msg(p, "All items fixed, retarded lumi.");
					return true;
				}
				RItem ri = new RItem(is, args[0]);
				ri.saveToFile(args[0]);
				RItem get = getItemFromDatabase(args[0]);
				if (get != null)
					itemDatabase.remove(get);
				itemDatabase.add(ri);
				msg(p, "Item saved");
				return true;
			}
			if (command.getName().equalsIgnoreCase("npcflag"))
			{
				if (!p.hasPermission("rpgcore.npc"))
				{
					msg(p, "You do not have permissions to do this.");
					return true;
				}
				if (args.length == 0)
				{
					msg(p, "Usage: /npcflag <list/del/set> [flagName] [newValue]");
					return true;
				}
				if (args[0].equalsIgnoreCase("list"))
				{
					Object[] keys = rp.npcFlags.keySet().toArray();
					Object[] values = rp.npcFlags.values().toArray();
					for (int i = 0; i < keys.length; i++)
						msgNoTag(p, (String) keys[i] + ": " + (String) values[i]);
					return true;
				}
				if (args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /npcflag del <flagName>");
						return true;
					}
					if (rp.npcFlags.remove(args[1]) == null)
					{
						msg(p, "You do not have a \"" + args[1] + "\" NPC Flag (case-sensitive)");
						return true;
					}
					msg(p, "NPC Flag \"" + args[1] + "\" removed.");
					RPlayerManager.writeData(rp);
					return true;
				}
				if (args[0].equalsIgnoreCase("set"))
				{
					if (args.length < 3)
					{
						msg(p, "Usage: /npcflag set <flagName> <newValue>");
						return true;
					}
					if (rp.npcFlags.containsKey(args[1]))
						rp.npcFlags.remove(args[1]);
					rp.npcFlags.put(args[1], args[2]);
					msg(p, "NPC Flag\"" + args[1] + "\" set to \"" + args[2] + "\".");
					RPlayerManager.writeData(rp);
					return true;
				}
				msg(p, "Usage: /npcflag <del/set> <flagName> [newValue]");
				return true;
			}
			if (command.getName().equalsIgnoreCase("npc"))
			{
				if (!p.hasPermission("rpgcore.npc"))
				{
					msg(p, "You do not have permissions to do this.");
					return true;
				}
				if (args.length == 0)
				{
					msg(p, helpNPC);
					return true;
				}
				if (args[0].equalsIgnoreCase("create"))
				{
					String name = args[1];
					for (int i = 2; i < args.length; i++)
						name += " " + args[i];
					String npcName = CakeLibrary.recodeColorCodes(name);
					if (npcName.length() > 16)
						npcName = npcName.substring(0, 16);
					rp.selectedNPC = npcManager.createNPC(p.getLocation(), npcName);
					rp.selectedNPC.saveNPC();
					msg(p, "NPC Created and selected.");
					return true;
				}
				if (args[0].equalsIgnoreCase("lockrotation"))
				{
					if (rp.selectedNPC == null)
					{
						msg(p, "Select an NPC by sneak-clicking it first");
						return true;
					}
					rp.selectedNPC.lockRotation = !rp.selectedNPC.lockRotation;
					rp.selectedNPC.saveNPC();
					msg(p, "NPC lockRotation: " + rp.selectedNPC.lockRotation);
					return true;
				}
				if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del"))
				{
					if (rp.selectedNPC == null)
					{
						msg(p, "Select an NPC by sneak-clicking it first");
						return true;
					}
					rp.selectedNPC.deleteNPC();
					rp.selectedNPC = null;
					msg(p, "NPC Deleted.");
					return true;
				}
				if (args[0].equalsIgnoreCase("rename"))
				{
					if (rp.selectedNPC == null)
					{
						msg(p, "Select an NPC by sneak-clicking it first");
						return true;
					}
					if (args.length < 2)
					{
						msg(p, "Usage: /npc rename [newName]");
						return true;
					}
					String name = args[1];
					for (int i = 2; i < args.length; i++)
						name += " " + args[i];
					CustomNPC prev = rp.selectedNPC;
					rp.selectedNPC = prev.skinData != null ? npcManager.createNPCUsernameSkin(prev.getBukkitLocation(), CakeLibrary.recodeColorCodes(name), prev.skinData.skinName)
							: npcManager.createNPC(prev.getBukkitLocation(), CakeLibrary.recodeColorCodes(name));
					rp.selectedNPC.applyNonConstructorVariables(prev);
					prev.deleteNPC();
					rp.selectedNPC.saveNPC();
					msg(p, "NPC Renamed.");
					return true;
				}
				if (args[0].equalsIgnoreCase("move"))
				{
					if (rp.selectedNPC == null)
					{
						msg(p, "Select an NPC by sneak-clicking it first");
						return true;
					}
					Location l = p.getLocation();
					rp.selectedNPC.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
					rp.selectedNPC.updatePosition();
					rp.selectedNPC.updateRotation();
					rp.selectedNPC.saveNPC();
					msg(p, "NPC moved.");
					return true;
				}
				if (args[0].equalsIgnoreCase("databasename"))
				{
					if (rp.selectedNPC == null)
					{
						msg(p, "Select an NPC by sneak-clicking it first");
						return true;
					}
					if (args.length < 2)
					{
						msg(p, "Usage: /npc databasename <databaseName>");
						return true;
					}
					rp.selectedNPC.changeDatabaseName(args[1]);
					msg(p, "NPC databaseName set to " + rp.selectedNPC.databaseName + ".");
					return true;
				}
				if (args[0].equalsIgnoreCase("chatrange"))
				{
					if (rp.selectedNPC == null)
					{
						msg(p, "Select an NPC by sneak-clicking it first");
						return true;
					}
					if (args.length < 2)
					{
						msg(p, "Usage: /npc chatrange <blocks>");
						return true;
					}
					float distance = -1;
					try
					{
						distance = Float.parseFloat(args[1]);
					} catch (Exception e) 
					{
						msg(p, "That is not a number");
						return true;
					}
					if (distance > 32)
					{
						msg (p, "That chat distance is too great!");
						return true;
					}
					if (distance < 0)
					{
						msg(p, ":thinking:");
						return true;
					}
					rp.selectedNPC.chatRangeDistance = distance;
					rp.selectedNPC.saveNPC();
					msg(p, "NPC chatDistanceRange set to " + distance + ".");
					return true;
				}
				if (args[0].equalsIgnoreCase("skin"))
				{
					if (rp.selectedNPC == null)
					{
						msg(p, "Select an NPC by sneak-clicking it first");
						return true;
					}
					if (args.length < 2)
					{
						msg(p, "Usage: /npc skin [newSkinName]");
						return true;
					}
					CustomNPC prev = rp.selectedNPC;
					rp.selectedNPC = npcManager.createNPCUsernameSkin(prev.getBukkitLocation(), prev.getName(), args[1]);
					rp.selectedNPC.applyNonConstructorVariables(prev);
					prev.deleteNPC();
					rp.selectedNPC.saveNPC();
					msg(p, "NPC Skin changed.");
					return true;
				}
				msg(p, helpNPC);
				return true;
			}
			if (command.getName().equalsIgnoreCase("crystal"))
			{
				if (!p.hasPermission("rpgcore.crystal"))
				{
					msg(p, "You do not have permissions to do this.");
					return true;
				}
				for (BonusStatCrystal crystal: BonusStatCrystal.values())
				{
					ItemStack item = crystal.getItemStack();
					item.setAmount(64);
					p.getInventory().addItem(item);
				}
				return true;
			}
			if (command.getName().equalsIgnoreCase("party"))
			{
				if (args.length < 1)
				{
					msgNoTag(p, helpParty);
					return true;
				}
				if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i"))
				{
					if (rp.partyID == -1)
					{
						RPGParty.msg(p, "You are not in a party.");
						return true;
					}
					RPGParty party = RPGParty.getParty(rp.partyID);
					party.updatePartyInventory();

					/**msgNoTag(p, "&5---[&d Party Info &5]---");
					msgNoTag(p, "&5 * Players:");
					for (RPlayer member: party.players)
					{
						Player player = member.getPlayer();
							msgNoTag(p, "&5   -> &d" + member.getPlayerName() + " &5- " +
									(player == null ? "&dOffline" : "") +
									(player != null ? "&dClass: " + member.currentClass.toString() : "") + 
									(player != null ? "&5, &dDmg: " + member.getDamageOfClass() : "") + 
									(player != null ? "&5, &dCs: " + (1 / member.getCastDelayMultiplier()) : "") + 
									(member.getPlayerName().equalsIgnoreCase(party.host.getPlayerName()) ? "&5, &dHOST" : ""));
					}*/

					p.openInventory(party.partyInventory);
					return true;
				}
				if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c"))
				{
					if (rp.partyID != -1)
					{
						RPGParty.msg(p, "You are already in a party.");
						RPGParty.msg(p, "Use &5/party disband &dto disband it.");
						return true;
					}
					RPGParty.createNewParty(rp);
					RPGParty.msg(p, "Party created.");
					RPGParty.msg(p, "&dUse &5/party <invite/kick/disband> [player] &dto manage.");
					return true;
				}
				if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("j"))
				{
					if (args.length < 2)
					{
						RPGParty.msg(p, "Usage: /party join <host>");
						return true;
					}
					if (rp.partyID != -1)
					{
						RPGParty.msg(p, "You are already in a party.");
						RPGParty.msg(p, "Use &5/party leave &dto leave.");
						return true;
					}
					RPlayer target = RPlayerManager.getRPlayer(CakeLibrary.completeName(args[1]));
					if (target == null)
					{
						RPGParty.msg(p, "No player by that name was found.");
						return true;
					}
					if (target.partyID == -1)
					{
						RPGParty.msg(p, "That player is not even in a party.");
						return true;
					}
					RPGParty party = RPGParty.getParty(target.partyID);
					if (party.host != target)
					{
						RPGParty.msg(p, "That player is not the host of his party.");
						return true;
					}
					if (!party.invites.contains(p.getUniqueId()))
					{
						RPGParty.msg(p, "You aren't invited to this party.");
						return true;
					}
					if (party.members.size() >= 9)
					{
						RPGParty.msg(p, "This party is full (9 players).");
						return true;
					}
					party.invites.remove(p.getUniqueId());
					party.addPlayer(rp);
					party.updatePartyInventory();
					party.broadcastMessage("&5" + p.getName() + " &dhas joined the party.");
					RPGParty.writeData();
					return true;
				}
				if (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("l"))
				{
					if (rp.partyID == -1)
					{
						RPGParty.msg(p, "You aren't even in a party.");
						return true;
					}
					RPGParty party = RPGParty.getParty(rp.partyID);
					if (party.host == rp)
					{
						RPGParty.msg(p, "You can't leave the party as a host.");
						RPGParty.msg(p, "Either disband the party or pass host to someone else.");
						return true;
					}
					party.removePlayer(rp);
					party.updatePartyInventory();
					party.broadcastMessage("&5" + p.getName() + " &dhas left the party.");
					RPGParty.msg(p, "You have left the party.");
					RPGParty.writeData();
					return true;
				}
				if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("inv"))
				{
					if (args.length < 2)
					{
						RPGParty.msg(p, "Usage: /party invite <player>");
						return true;
					}
					if (rp.partyID == -1)
					{
						RPGParty.msg(p, "You are not in a party.");
						return true;
					}
					RPGParty party = RPGParty.getParty(rp.partyID);
					if (party.host != rp)
					{
						RPGParty.msg(p, "You are not the host, try asking &5" + party.host.getPlayerName() + " &dto do it.");
						return true;
					}
					RPlayer target = RPlayerManager.getRPlayer(CakeLibrary.completeName(args[1]));
					if (target == null)
					{
						RPGParty.msg(p, "No player by that name was found.");
						return true;
					}
					if (party.members.contains(target))
					{
						RPGParty.msg(p, "That player is already in the party.");
						return true;
					}
					Player t = target.getPlayer();
					if (t == null)
					{
						RPGParty.msg(p, "That player is not online.");
						return true;
					}
					if (party.invites.contains(target.getUniqueID()))
					{
						RPGParty.msg(p, "You have already invited the player.");
						return true;
					}
					party.invites.add(target.getUniqueID());
					RPGParty.msg(t, "You have been invited to join &5" + p.getName() + "&d's party.");
					RPGParty.msg(t, "Type &5/party join " + p.getName() + " &dto join.");
					party.broadcastMessage("&5" + target.getPlayerName() + " &dhas been invited to the party.");
					return true;
				}
				if (args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("k"))
				{
					if (args.length < 2)
					{
						RPGParty.msg(p, "Usage: /party kick <player>");
						return true;
					}
					if (rp.partyID == -1)
					{
						RPGParty.msg(p, "You are not in a party.");
						return true;
					}
					RPGParty party = RPGParty.getParty(rp.partyID);
					if (party.host != rp)
					{
						RPGParty.msg(p, "You are not the host; try asking &5" + party.host.getPlayerName() + " &dto do it.");
						return true;
					}
					RPlayer target = RPlayerManager.getRPlayer(CakeLibrary.completeName(args[1]));
					if (target == null)
					{
						RPGParty.msg(p, "No player by that name was found.");
						return true;
					}
					if (party.host == target)
					{
						RPGParty.msg(p, "You can't kick yourself from your party.");
						return true;
					}
					if (!party.members.contains(target))
					{
						RPGParty.msg(p, "That player is not in the party.");
						return true;
					}
					Player t = target.getPlayer();
					party.removePlayer(target);
					party.updatePartyInventory();
					party.broadcastMessage("&5" + target.getPlayerName() + " &dhas been kicked from the party.");
					if (t != null)
						RPGParty.msg(t, "You have been kicked from the party.");
					RPGParty.writeData();
					return true;
				}
				if (args[0].equalsIgnoreCase("sethost") || args[0].equalsIgnoreCase("sh"))
				{
					if (args.length < 2)
					{
						RPGParty.msg(p, "Usage: /party sethost <player>");
						return true;
					}
					if (rp.partyID == -1)
					{
						RPGParty.msg(p, "You are not in a party.");
						return true;
					}
					RPGParty party = RPGParty.getParty(rp.partyID);
					if (party.host != rp)
					{
						RPGParty.msg(p, "You are not the host, try asking &5" + party.host.getPlayerName() + " &dto do it.");
						return true;
					}
					RPlayer target = RPlayerManager.getRPlayer(CakeLibrary.completeName(args[1]));
					if (target == null)
					{
						RPGParty.msg(p, "No player by that name was found.");
						return true;
					}
					if (!party.members.contains(target))
					{
						RPGParty.msg(p, "That player is not in the party.");
						return true;
					}
					if (party.host == target)
					{
						RPGParty.msg(p, "That player is already the host.");
						return true;
					}
					party.host = target;
					party.updatePartyInventory();
					party.broadcastMessage("&5" + target.getPlayerName() + " &dhas been set as party host.");
					RPGParty.writeData();
					return true;
				}
				if (args[0].equalsIgnoreCase("disband") || args[0].equalsIgnoreCase("d"))
				{
					if (rp.partyID == -1)
					{
						RPGParty.msg(p, "You are not in a party.");
						return true;
					}
					RPGParty party = RPGParty.getParty(rp.partyID);
					if (party.host != rp)
					{
						RPGParty.msg(p, "You are not the host, try asking &5" + party.host.getPlayerName() + " &dto do it.");
						return true;
					}
					party.disbandParty();
					return true;
				}
				msgNoTag(p, helpParty);
				return true;
			}
			if (command.getName().equalsIgnoreCase("itemprice"))
			{
				if (!p.hasPermission("rpgcore.itemprice"))
				{
					msg(p, "You do not have permissions to use this command.");
					return true;
				}

				if (args.length < 1)
				{
					msg(p, "Usage: /itemprice <priceToSet/check/list/customList/vanillaList/del> <listPageNumber>");
					return true;
				}

				if (args[0].equalsIgnoreCase("list") || args[0].equals("l"))
				{
					int pageNumber = 0;
					if (args.length > 1)
					{
						try
						{
							pageNumber = Integer.parseInt(args[1]) - 1;
						} catch (Exception e)
						{
							msg(p, "That is not a number.");
							return true;
						}
					}
					pageNumber = Math.min(pageNumber < 0 ? 0 : pageNumber, GuildShop.getItemPriceListPages() - 1);
					p.openInventory(GuildShop.getItemPriceList(pageNumber));
					return true;
				}

				if (args[0].equalsIgnoreCase("customlist") || args[0].equals("cl"))
				{
					int pageNumber = 0;
					if (args.length > 1)
					{
						try
						{
							pageNumber = Integer.parseInt(args[1]) - 1;
						} catch (Exception e)
						{
							msg(p, "That is not a number.");
							return true;
						}
					}
					pageNumber = Math.min(pageNumber < 0 ? 0 : pageNumber, GuildShop.getItemPriceListPages() - 1);
					p.openInventory(GuildShop.getCustomItemPriceList(pageNumber));
					return true;
				}

				if (args[0].equalsIgnoreCase("vanillalist") || args[0].equals("vl"))
				{
					int pageNumber = 0;
					if (args.length > 1)
					{
						try
						{
							pageNumber = Integer.parseInt(args[1]) - 1;
						} catch (Exception e)
						{
							msg(p, "That is not a number.");
							return true;
						}
					}
					pageNumber = Math.min(pageNumber < 0 ? 0 : pageNumber, GuildShop.getItemPriceListPages() - 1);
					p.openInventory(GuildShop.getVanillaItemPriceList(pageNumber));
					return true;
				}

				ItemStack is = p.getItemInHand();
				if (CakeLibrary.isItemStackNull(is))
				{
					msg(p, "Hold the item you want to edit.");
					return true;
				}
				RItem ri = new RItem(is);
				ri.itemVanilla.setAmount(1);

				RItem check = null;
				for (RItem key: GuildShop.itemPrices.keySet())
					if (key.compare(ri))
						check = key;

				if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del") || args[0].equalsIgnoreCase("d"))
				{
					if (check == null)
					{
						msg(p, "That item does not exist in the price list.");
						return true;
					}
					GuildShop.itemPrices.remove(check);
					GuildShop.saveItemPrices();
					msg(p, "Item cost of &6" + CakeLibrary.getItemName(is) + "&e removed.");
					return true;
				}

				if (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("c"))
				{
					if (check == null)
					{
						msg(p, "That item does not exist in the price list.");
						return true;
					}
					msg(p, "Item cost of &6" + CakeLibrary.getItemName(is) + "&e: &6" + GuildShop.itemPrices.get(check) + " Gold");
					return true;
				}

				int cost = 0;
				try
				{
					cost = Integer.parseInt(args[0]);
				} catch (Exception e)
				{
					msg(p, "That is not a number.");
					return true;
				}

				GuildShop.itemPrices.put(check == null ? ri : check, cost);
				GuildShop.saveItemPrices();
				msg(p, "Item cost of &6" + CakeLibrary.getItemName(is) + "&e set to &6" + cost + "&e.");

				return true;
			}
			if (command.getName().equalsIgnoreCase("itemfood"))
			{
				if (!p.hasPermission("rpgcore.item"))
				{
					msg(p, "You do not have permissions to use this command.");
					return true;
				}
				if (args.length < 1)
					return true;

				ItemStack is = p.getItemInHand();
				if (CakeLibrary.isItemStackNull(is))
				{
					msg(p, "Hold the item you want to edit.");
					return true;
				}
				RItem ri = new RItem(is);

				if (args[0].equalsIgnoreCase("satiate"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.satiate = (int) (Float.valueOf(args[1]) * 2.0F);
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("magicDamageAdd"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.magicDamageAdd = Integer.parseInt(args[1]);
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("meleeDamageAdd"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.meleeDamageAdd = Integer.parseInt(args[1]);
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("magicDamageMultiplier"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.magicDamageMultiplier = Float.parseFloat(args[1]);
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("meleeDamageMultiplier"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.meleeDamageMultiplier = Float.parseFloat(args[1]);
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("attackSpeedMultiplier"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.attackSpeedMultiplier = Float.parseFloat(args[1]);
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("critChanceAdd"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.critChanceAdd = Integer.parseInt(args[1]);
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("critDamageAdd"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.critDamageAdd = Integer.parseInt(args[1]);
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("damageReductionAdd"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.damageReductionAdd = Integer.parseInt(args[1]);
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("cooldownReductionAdd"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.cooldownReductionAdd = Integer.parseInt(args[1]);
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("recoverySpeedAdd"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.recoverySpeedAdd = Integer.parseInt(args[1]);
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("xpMultiplier"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.xpMultiplierFood = Float.parseFloat(args[1]);
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("buffDuration"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.buffDuration = 0;
					for (int i = 1; i < args.length; i++)
					{
						if (args[i].endsWith("h"))
							ri.buffDuration += Integer.parseInt(args[i].substring(0, args[i].length() - 1)) * 60 * 60 * 20;
						else if (args[i].endsWith("m"))
							ri.buffDuration += Integer.parseInt(args[i].substring(0, args[i].length() - 1)) * 60 * 20;
						else if (args[i].endsWith("s"))
							ri.buffDuration += Integer.parseInt(args[i].substring(0, args[i].length() - 1)) * 20;
						else 
							ri.buffDuration += Integer.parseInt(args[i]) * 20;
					}
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				if (args[0].equalsIgnoreCase("consumableCooldown"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemfood " + args[0] + " <amount>");
						return true;
					}
					ri.consumableCooldown = 0;
					for (int i = 1; i < args.length; i++)
					{
						if (args[i].endsWith("m"))
							ri.consumableCooldown += Integer.parseInt(args[i].substring(0, args[i].length() - 1)) * 60 * 20;
						else if (args[i].endsWith("s"))
							ri.consumableCooldown += Integer.parseInt(args[i].substring(0, args[i].length() - 1)) * 20;
						else 
							ri.consumableCooldown += Integer.parseInt(args[i]) * 20;
					}
					ri.consumable = true;
					p.setItemInHand(is = ri.createItem());
					msgNoTag(p, CakeLibrary.getItemName(is));
					for (String lore: CakeLibrary.getItemLore(is))
						msgNoTag(p, lore);
					return true;
				}

				msg(p, "Unrecognized argument. Use tab maybe?");
				return true;
			}
			if (command.getName().equalsIgnoreCase("setrgb"))
			{
				if (!p.hasPermission("rpgcore.item"))
				{
					msg(p, "You do not have permissions to use this command.");
					return true;
				}
				if (args.length < 3)
				{
					msg(p, "Usage: /setrgb <red> <green> <blue>");
					msg(p, "Values range from 0 to 255");
					return true;
				}
				ItemStack is = p.getItemInHand();
				if (CakeLibrary.isItemStackNull(is))
				{
					msg(p, "Hold the item you want to edit.");
					return true;
				}
				if (!(is.getItemMeta() instanceof LeatherArmorMeta))
				{
					msg(p, "That is not a piece of leather equipment");
					return true;
				}
				LeatherArmorMeta lim = (LeatherArmorMeta) is.getItemMeta();
				try
				{
					lim.setColor(Color.fromRGB(Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2])));
				} catch (Exception e)
				{
					msg(p, "Error; did you enter values between 0 to 255?");
					return true;
				}
				is.setItemMeta(lim);
				p.setItemInHand(is);
				msg(p, "RGB color set.");
				return true;
			}
			if (command.getName().equalsIgnoreCase("item"))
			{
				if (!p.hasPermission("rpgcore.item"))
				{
					msg(p, "You do not have permissions to use this command.");
					return true;
				}
				if (args.length < 1)
				{
					msgNoTag(p, helpItem);
					return true;
				}
				ItemStack is = p.getItemInHand();
				if (CakeLibrary.isItemStackNull(is))
				{
					msg(p, "Hold the item you want to edit.");
					return true;
				}
				RItem ri = new RItem(is);
				if (args[0].equalsIgnoreCase("expiry"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item expiry <dayOffset> [hourOffset] [minuteOffset]");
						return true;
					}
					ri.expiry = new TimeTrack().getClonedOffset(0, Integer.parseInt(args[1]), 
							args.length > 2 ? Integer.parseInt(args[2]) : 0, args.length > 3 ? Integer.parseInt(args[3]) : 0);
					p.setItemInHand(ri.createItem());
					msg(p, "Expiry attribute edited.");
					return true;
				}
				if (args[0].equalsIgnoreCase("tier"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item tier <tier>");
						return true;
					}
					int tier = -1;
					try
					{
						tier = Integer.parseInt(args[1]);
					} catch (Exception e) {
						msg(p, "Enter a number.");
						return true;
					}
					if (tier < 0)
					{
						msg(p, "The minimum tier is 0.");
						return true;
					}
					if (tier > RItem.tiers.length)
					{
						msg(p, "The maximum tier is " + RItem.tiers.length);
						return true;
					}
					ri.setTier(tier);
					p.setItemInHand(ri.createItem());
					msg(p, "Tier attribute edited.");
					return true;
				}
				if (args[0].equalsIgnoreCase("lvrequirement"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item lvrequirement <damage>");
						return true;
					}
					int dmg = -1;
					try
					{
						dmg = Integer.parseInt(args[1]);
					} catch (Exception e) {
						msg(p, "Enter a number.");
						return true;
					}
					ri.levelRequirement = dmg;
					p.setItemInHand(ri.createItem());
					msg(p, "Lv requirement attribute edited.");
					return true;
				} else if (args[0].equalsIgnoreCase("xpmultiplier"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item xpmultiplier <multiplier>");
						return true;
					}
					double multiplier = -1;
					try
					{
						multiplier = Double.parseDouble(args[1]);
					} catch (Exception e) {
						msg(p, "Enter a number.");
						return true;
					}
					ri.xpMultiplier = multiplier;
					p.setItemInHand(ri.createItem());
					msg(p, "XP Multiplier attribute edited.");
					return true;
				} else if (args[0].equalsIgnoreCase("magicdamage"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item magicdamage <damage>");
						return true;
					}
					int dmg = -1;
					try
					{
						dmg = Integer.parseInt(args[1]);
					} catch (Exception e) {
						msg(p, "Enter a number.");
						return true;
					}
					ri.magicDamage = dmg;
					p.setItemInHand(ri.createItem());
					msg(p, "Magic damage attribute edited.");
					return true;
				} else if (args[0].equalsIgnoreCase("meleedamage"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item meleedamage <damage>");
						return true;
					}
					int dmg = -1;
					try
					{
						dmg = Integer.parseInt(args[1]);
					} catch (Exception e) {
						msg(p, "Enter a number.");
						return true;
					}
					ri.meleeDamage = dmg;
					p.setItemInHand(ri.createItem());
					msg(p, "Melee damage attribute edited.");
					return true;
				} else if (args[0].equalsIgnoreCase("critchance"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item critchance <percentage>");
						return true;
					}
					int dmg = -1;
					try
					{
						dmg = Integer.parseInt(args[1]);
					} catch (Exception e) {
						msg(p, "Enter a number.");
						return true;
					}
					ri.critChance = dmg;
					p.setItemInHand(ri.createItem());
					msg(p, "Crit chance attribute edited.");
					return true;
				} else if (args[0].equalsIgnoreCase("critdamage"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item critchance <percentage>");
						return true;
					}
					int dmg = -1;
					try
					{
						dmg = Integer.parseInt(args[1]);
					} catch (Exception e) {
						msg(p, "Enter a number.");
						return true;
					}
					ri.critDamage = dmg;
					p.setItemInHand(ri.createItem());
					msg(p, "Crit damage attribute edited.");
					return true;
				} else if (args[0].equalsIgnoreCase("attackspeed"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item attackspeed <damage>");
						return true;
					}
					double amt = -1;
					try
					{
						amt = Double.parseDouble(args[1]);
					} catch (Exception e) {
						msg(p, "Enter a number.");
						return true;
					}
					ri.attackSpeed = amt;
					p.setItemInHand(ri.createItem());
					msg(p, "Attack Speed attribute edited.");
					return true;
				} else if (args[0].equalsIgnoreCase("recoveryspeed"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item recoveryspeed <percentage>");
						return true;
					}
					int amt = -1;
					try
					{
						amt = Integer.parseInt(args[1]);
					} catch (Exception e) {
						msg(p, "Enter a number.");
						return true;
					}
					ri.recoverySpeed = amt;
					p.setItemInHand(ri.createItem());
					msg(p, "Recovery speed attribute edited.");
					return true;
				} else if (args[0].equalsIgnoreCase("cooldownreduction"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item cooldownreduction <percentage>");
						return true;
					}
					int amt = -1;
					try
					{
						amt = Integer.parseInt(args[1]);
					} catch (Exception e) {
						msg(p, "Enter a number.");
						return true;
					}
					ri.cooldownReduction = amt;
					p.setItemInHand(ri.createItem());
					msg(p, "Cooldown reduction attribute edited.");
					return true;
				} else if (args[0].equalsIgnoreCase("damagereduction"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item damagereduction <percentage>");
						return true;
					}
					int amt = -1;
					try
					{
						amt = Integer.parseInt(args[1]);
					} catch (Exception e) {
						msg(p, "Enter a number.");
						return true;
					}
					ri.damageReduction = amt;
					p.setItemInHand(ri.createItem());
					msg(p, "Damage reduction attribute edited.");
					return true;
				} else if (args[0].equalsIgnoreCase("unbreakable"))
				{
					ItemMeta im = is.getItemMeta();
					boolean u = im.spigot().isUnbreakable();	
					im.spigot().setUnbreakable(!u);
					is.setItemMeta(im);
					p.setItemInHand(is);
					msg(p, "Unbreakable attribute toggled to: " + !u);
					return true;
				} else if (args[0].equalsIgnoreCase("accessory"))
				{
					ri.accessory = !ri.accessory;
					p.setItemInHand(ri.createItem());
					msg(p, "Accessory attribute toggled to: " + ri.accessory);
					return true;
				} else if (args[0].equalsIgnoreCase("desc") || args[0].equalsIgnoreCase("description") || args[0].equalsIgnoreCase("d"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /item d/desc <l/list / d/del / lineNumber> <lineNumber/newDesc>");
						return true;
					}
					ArrayList<String> lore = CakeLibrary.getItemLore(is);
					int descriptionIndex = -1;
					int lastDescriptionIndex = -1;
					for (int i = 0; i < lore.size(); i++)
						if (lore.get(i).startsWith("�7�o"))
						{
							if (descriptionIndex == -1)
								descriptionIndex = i;
							lastDescriptionIndex = i;
						}
					if (args[1].equalsIgnoreCase("l") || args[1].equalsIgnoreCase("list"))
					{
						if (descriptionIndex == -1)
						{
							msg(p, "This item does not have a description");
							return true;
						}
						for (int i = 0; i < lastDescriptionIndex + 1 - descriptionIndex; i++)
							msgNoTag(p, "&f#" + (i + 1) + "&7: " + lore.get(descriptionIndex + i));
						return true;
					}
					if (args.length < 3)
					{
						msg(p, "Usage: /item d/desc <l/list / d/del / lineNumber> <lineNumber/newDesc>");
						return true;
					}
					if (args[1].equalsIgnoreCase("d") || args[1].equalsIgnoreCase("del"))
					{
						int lineNumber = -1;
						try
						{
							lineNumber = Integer.valueOf(args[2]);
						} catch (Exception e)
						{
							msg(p, "Usage: /item desc del <lineNumber>");
							return true;
						}
						if (descriptionIndex == -1)
						{
							msg(p, "This item does not have a description");
							return true;
						}
						if (lastDescriptionIndex < descriptionIndex + lineNumber - 1)
						{
							for (int i = 0; i < lastDescriptionIndex + 1 - descriptionIndex; i++)
								msgNoTag(p, "&f#" + (i + 1) + "&7: " + lore.get(descriptionIndex + i));
							msg(p, "Usage: /item desc del <lineNumber>");
							return true;
						}
						String line = lore.get(descriptionIndex + lineNumber - 1);
						lore.remove(descriptionIndex + lineNumber - 1);
						is = CakeLibrary.setItemLore(is, lore);
						p.setItemInHand(is);
						msg(p, "Removed description line &f#" + lineNumber + "&c '" + line + "�c'");
						return true;
					}
					int lineNumber = -1;
					try
					{
						lineNumber = Integer.valueOf(args[1]);
					} catch (Exception e)
					{
						msg(p, "Usage: /item d/desc <l/list / d/del / lineNumber> <lineNumber/newDesc>");
						return true;
					}
					String desc = "�7�o" + args[2];
					if (args.length > 3)
						for (int i = 3; i < args.length; i++)
							desc += " " + args[i];
					if (descriptionIndex == -1)
					{
						lineNumber = 1;
						lore.add("�f");
						lore.add(desc);
					} else
					{
						if (lastDescriptionIndex < descriptionIndex + lineNumber - 1)
						{
							lore.add(desc);
							lineNumber = lastDescriptionIndex - descriptionIndex + 2;
						} else
							lore.set(descriptionIndex + lineNumber - 1, desc);
					}
					is = CakeLibrary.setItemLore(is, lore);
					p.setItemInHand(is);
					msg(p, "Description line &f#" + lineNumber + "&c set to '" + desc + "�c'");
					return true;
				}
				msgNoTag(p, helpItem);
				return true;
			}
			if (command.getName().equalsIgnoreCase("mob"))
			{
				if (!p.hasPermission("rpgcore.mob"))
				{
					msg(p, "No access to this command lul");
					return true;
				}
				if (args.length < 1)
				{
					msg(p, "Usage: /mob <mobname> [count]");
					return true;
				}
				int count = 1;
				if (args.length > 1)
					count = Math.min(10, Integer.parseInt(args[1]));
				RPGMonsterSpawn mob = RPGMonsterSpawn.getRPGMonsterSpawn(args[0]);
				if (mob == null)
				{
					msg(p, "No such mob available for spawning.");
					return true;
				}
				listener.allowSpawn = count;
				for (int i = 0; i < count; i++)
					mob.spawnMonster(p.getLocation());
				msg(p, "Spawned.");
				return true;
			}
			if (command.getName().equalsIgnoreCase("itemswap"))
			{
				if (!p.isOp())
				{
					msg(p, "Op is required for this command.");
					return true;
				}
				if (args.length < 1)
				{
					msg(p, "Usage: /itemswap <list/add/del/+version> <from> [to]");
					return true;
				}
				if (args[0].equalsIgnoreCase("+version"))
				{
					ItemSwap.itemSwapVersion++;
					msg(p, "Item swap version increased by 1; now " + ItemSwap.itemSwapVersion + ".");
					ItemSwap.writeData();
					return true;
				}
				if (args[0].equalsIgnoreCase("add"))
				{
					if (args.length < 3)
					{
						msg(p, "Usage: /itemswap add <from> <to>");
						return true;
					}
					RItem from = getItemFromDatabase(args[1]);
					RItem to = getItemFromDatabase(args[2]);
					if (from == null)
					{
						msg(p, "\"" + args[1] + "\" does not exist as an item");
						return true;
					}
					if (to == null)
					{
						msg(p, "\"" + args[2] + "\" does not exist as an item");
						return true;
					}
					ItemSwap.addItemSwap(from, to);
					msg(p, "Item swap added.");
					return true;
				}
				if (args[0].equalsIgnoreCase("del"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /itemswap del <from>");
						return true;
					}
					int index = -1;
					for (int i = 0; i < ItemSwap.itemSwaps.size(); i++)
						if (ItemSwap.itemSwaps.get(i).from.databaseName.equalsIgnoreCase(args[1]))
							index = i;
					if (index == -1)
					{
						msg(p, "That item swap with \"" + args[1] + "\" (from) does not exist");
						return true;
					}
					ItemSwap.itemSwaps.remove(index);
					ItemSwap.writeData();
					msg(p, "Item swap from \"" + args[1] + "\" deleted.");
					return true;
				}
				if (args[0].equalsIgnoreCase("list"))
				{
					msgNoTag(p, "&6ItemSwap List:");
					for (ItemSwap swap: ItemSwap.itemSwaps)
						msgNoTag(p, "&eFrom \"" + swap.from.databaseName + "\" to \"" + swap.to.databaseName + "\"");
					return true;
				}
				msg(p, "Usage: /itemswap <list/add/del/+version> <from> [to]");
				return true;
			}
			if (command.getName().equalsIgnoreCase("globalgift"))
			{
				if (!p.isOp())
				{
					msg(p, "This command requires op");
					return true;
				}
				if (args.length < 1)
				{
					msg(p, "Usage: /globalgift <list/add/del> <itemName/delIndex> [expiryInDays]");
					return true;
				}
				if (args[0].equalsIgnoreCase("list"))
				{
					msgNoTag(p, "&6GlobalGift List; Current day: " + Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
					for (GlobalGift gift: GlobalGift.gifts)
						msgNoTag(p, "&e#" + gift.giftIndex + ", \"" + gift.item.databaseName + "\", expire: " + gift.expireDay + "D " + gift.expireYear + "Y");
					return true;
				}
				if (args[0].equalsIgnoreCase("add"))
				{
					if (args.length < 3)
					{
						msg(p, "Usage: /globalgift add <itemName> <expiryInDays>");
						return true;
					}
					RItem item = getItemFromDatabase(args[1]);
					if (item == null)
					{
						msg(p, "This item does not exist in the database");
						return true;
					}
					try
					{
						Calendar calendar = Calendar.getInstance();
						int day = calendar.get(Calendar.DAY_OF_YEAR) + Integer.valueOf(args[2]);
						int year = calendar.get(Calendar.YEAR);
						while (day > 365)
						{
							year++;
							day -= 365;
						}
						GlobalGift.createGlobalGift(item, day, year);
						GlobalGift.writeData();
						msg(p, "Global gift \"" + item.databaseName + "\" created; expires on " + day + "D " + year + "Y");
					} catch (Exception e)
					{
						msg(p, "Usage: /globalgift add <itemName> <expiryInDays>");
						return true;
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("del"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /globalgift del <delIndex>");
						return true;
					}
					try
					{
						GlobalGift gift = GlobalGift.getGiftByIndex(Integer.valueOf(args[1]));
						if (gift == null)
						{
							msg(p, "That gift index does not exist.");
							return true;
						}
						GlobalGift.gifts.remove(gift);
						GlobalGift.writeData();
						msg(p, "Global gift \"" + gift.item.databaseName + "\" removed.");
					} catch (Exception e)
					{
						msg(p, "Usage: /globalgift del <delIndex>");
						return true;
					}
					return true;
				}
				msg(p, "Usage: /globalgift <list/add/del> <itemName/delIndex> [expiryInDays]");
				return true;
			}
			if (command.getName().equalsIgnoreCase("rkit"))
			{
				if (!p.hasPermission("rpgcore.kit"))
				{
					msg(p, "You do not have permissions to use this command.");
					return true;
				}
				if (args.length < 1)
				{
					msg(p, "Usage: /rkit <get/create/edit/del/reload> <kitName> [intervalDays] [intervalHours] [intervalMinutes]");
					return true;
				}
				if (args[0].equalsIgnoreCase("reload"))
				{
					RPGKit.readData();
					msg(p, "Data successfully reloaded.");
					return true;
				}
				if (args[0].equalsIgnoreCase("get"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /rkit get <kitName>");
						return true;
					}
					RPGKit kit = RPGKit.getKit(args[1]);
					if (kit == null)
					{
						msg(p, "That kit does not exist; try using tab");
						return true;
					}
					kit.sendToPlayer(rp);
					msg(p, "Kit obtained.");
					return true;
				}
				if (args[0].equalsIgnoreCase("create"))
				{
					if (RPGKit.getKit(args[1]) != null)
					{
						msg(p, "A kit by that name already exists");
						return true;
					}
					try
					{
						p.openInventory(RPGKit.createKit(args[1], Integer.valueOf(args[2]), Integer.valueOf(args[3]), Integer.valueOf(args[4])).getKitInventory());
						msg(p, "Kit successfully created.");
						return true;
					} catch (Exception e)
					{
						msg(p, "Usage: /rkit create <kitName> <intervalDays> <intervalHours> <intervalMinutes>");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("edit"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /rkit edit <kitName>");
						return true;
					}
					RPGKit kit = RPGKit.getKit(args[1]);
					if (kit == null)
					{
						msg(p, "That kit does not exist; try using tab");
						return true;
					}
					p.openInventory(kit.getKitInventory());
					return true;
				}
				if (args[0].equalsIgnoreCase("del"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /rkit edit <kitName>");
						return true;
					}
					RPGKit kit = RPGKit.getKit(args[1]);
					if (kit == null)
					{
						msg(p, "That kit does not exist; try using tab");
						return true;
					}
					kit.delete();
					msg(p, "Kit successfully deleted.");
					return true;
				}
				return true;
			}
			if (command.getName().equalsIgnoreCase("arena"))
			{
				if (!p.hasPermission("rpgcore.arena"))
				{
					msg(p, "You do not have permissions to use this command.");
					return true;
				}
				if (args.length < 1)
				{
					msgNoTag(p, helpArena);
					return true;
				}
				if (args[0].equalsIgnoreCase("list"))
				{
					msgNoTag(p, "&4Arena List:");
					for (Arena a: Arena.arenaList)
						msgNoTag(p, "&4 - &c" + a.schematicName);
					return true;
				}
				if (args[0].equalsIgnoreCase("create"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena create <schematicName>");
						return true;
					}
					if (Arena.getArena(args[1]) != null)
					{
						msg(p, "An arena by that name already exists");
						return true;
					}
					File file = new File("plugins/WorldEdit/schematics/" + args[1] + ".schematic");
					if (!file.exists())
					{
						msg(p, "That schematic does not exist.");
						return true;
					}
					new Arena(args[1]);
					Arena.writeArenaData();
					msg(p, "Arena \"" + args[1] + "\" created.");
					return true;
				}
				if (args[0].equalsIgnoreCase("del"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena del <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					for (int i = 0; i < ArenaInstance.arenaInstanceList.size(); i++)
					{
						ArenaInstance ai = ArenaInstance.arenaInstanceList.get(i);
						if (ai.arena == a)
						{
							for (Player check: Bukkit.getOnlinePlayers())
							{
								RPlayer rcheck = RPlayerManager.getRPlayer(check.getUniqueId());
								if (rcheck.arenaInstanceID == ai.arenaInstanceID)
									rcheck.leaveArena(true);
							}
							ArenaInstance.arenaInstanceList.remove(i);
							i--;
						}
					}
					Arena.arenaList.remove(a);
					ArenaInstance.writeData();
					Arena.writeArenaData();
					msg(p, "Arena removed.");
					return true;
				}
				if (args[0].equalsIgnoreCase("setspawnrotation"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena setspawnrotation <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					Location l = p.getLocation();
					a.yaw = l.getYaw();
					a.pitch = l.getPitch();
					Arena.writeArenaData();
					msg(p, "Arena spawn rotation set.");
					return true;
				}
				if (args[0].equalsIgnoreCase("tpspawntest"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena tpspawntest <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					arenaSpawnTestIndex++;
					try {
						File file = new File("plugins/WorldEdit/schematics/" + a.schematicName + ".schematic");
						EditSession es = WorldEdit.getInstance().getEditSessionFactory().getEditSession(ArenaInstance.getArenaInstanceWorld()
								, WorldEdit.getInstance().getConfiguration().maxChangeLimit);
						CuboidClipboard clip = SchematicFormat.MCEDIT.load(file);
						try {
							clip.paste(es, new Vector(arenaSpawnTestIndex * -256, 64, 0), true);
						} catch (MaxChangedBlocksException e) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (DataException e) {
						e.printStackTrace();
					}
					p.teleport(new Location(Bukkit.getWorld(areaInstanceWorld), (arenaSpawnTestIndex * -256) + 0.5F, 64.5F, 0.5F));
					writeConfig();
					return true;
				}
				if (args[0].equalsIgnoreCase("addmobspawn"))
				{
					if (args.length < 3)
					{
						msg(p, "Usage: /arena addmobspawn <arenaName> <mobName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					a.mobSpawns.add(args[2]);
					a.mobSpawnOffsets.add(p.getLocation().add((arenaSpawnTestIndex * 256), -64, 0).toVector());
					msg(p, "Mob spawn for \"" + args[2] + "\" added to \"" + a.schematicName + "\".");
					Arena.writeArenaData();
					return true;
				}
				if (args[0].equalsIgnoreCase("setentrance"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena setentrance <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					a.entrances.add(p.getLocation());
					msg(p, "Entrance for \"" + a.schematicName + "\" has been added.");
					Arena.writeArenaData();
					return true;
				}
				if (args[0].equalsIgnoreCase("setinternalexit"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena setinternalexit <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					a.exitInternals.add(p.getLocation().add((arenaSpawnTestIndex * 256), -64, 0).toVector());
					msg(p, "Internal exit for \"" + a.schematicName + "\" has been added.");
					Arena.writeArenaData();
					return true;
				}
				if (args[0].equalsIgnoreCase("clearentrances"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena clearentrances <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					a.entrances.clear();
					msg(p, "Entrance for \"" + a.schematicName + "\" has been added.");
					Arena.writeArenaData();
					return true;
				}
				if (args[0].equalsIgnoreCase("enable"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena enable <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					a.enabled = true;
					if (a.exitExternal == null)
						msgNoTag(p, "&4Warning: This arena does not have externalExit set");
					if (a.exitInternals.size() == 0)
						msgNoTag(p, "&4Warning: This arena does not have any internalExits set");
					if (a.entrances.size() == 0)
						msgNoTag(p, "&4Warning: This arena does not have any entrances set");
					if (a.yaw == 0 || a.pitch == 0)
						msgNoTag(p, "&4Warning: This arena does not have a spawnRotation set");
					msg(p, "Arena \"" + a.schematicName + "\" has been enabled.");
					Arena.writeArenaData();
					return true;
				}
				if (args[0].equalsIgnoreCase("disable"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena disable <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					a.enabled = false;
					msg(p, "Arena \"" + a.schematicName + "\" has been disabled.");
					Arena.writeArenaData();
					return true;
				}
				if (args[0].equalsIgnoreCase("clearinternalexits"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena clearinternalexits <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					a.exitInternals.clear();
					msg(p, "Internal exit for \"" + a.schematicName + "\" has been added.");
					Arena.writeArenaData();
					return true;
				}
				if (args[0].equalsIgnoreCase("setexternalexit"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena setexternalexit <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					a.exitExternal = p.getLocation();
					msg(p, "External exit for \"" + a.schematicName + "\" has been set.");
					Arena.writeArenaData();
					return true;
				}
				if (args[0].equalsIgnoreCase("clearmobspawns"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena clearmobspawns <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					a.mobSpawns.clear();
					msg(p, "Mob spawns for \"" + a.schematicName + "\" cleared.");
					return true;
				}
				if (args[0].equalsIgnoreCase("enter"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena enter <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					rp.enterArena(a);
					msg(p, "Entering arena...");
					return true;
				}
				if (args[0].equalsIgnoreCase("leave"))
				{
					if (rp.arenaInstanceID == -1)
					{
						msg(p, "You are currently not in an arena.");
						return true;
					}
					rp.leaveArena(true);
					msg(p, "Leaving arena...");
					return true;
				}
				if (args[0].equalsIgnoreCase("createinstance"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /arena createInstance <arenaName>");
						return true;
					}
					Arena a = Arena.getArena(args[1]);
					if (a == null)
					{
						msg(p, "That arena does not exist.");
						return true;
					}
					ArenaInstance ai = ArenaInstance.createArenaInstance(a);
					ArenaInstance.writeData();
					msg(p, "Creating arena instance in world \"" + areaInstanceWorld + "\", at (" + (ai.arenaInstanceID * 256) + ", 64, 0).");
					return true;
				}
				msgNoTag(p, helpArena);
				return true;
			}
			if (command.getName().equalsIgnoreCase("bgm"))
			{
				if (args.length < 1)
				{
					msg(p, "Usage: /bgm <list/reload/play/stop> [song]");
					return true;
				}
				if (args[0].equalsIgnoreCase("list"))
				{
					msgNoTag(p, "&6===[ &eSongs &6]===");
					for (RPGSong song: songManager.songs)
						msgNoTag(p, "&e\"" + song.name + "\" &6- &e" + song.BPM + "BPM, " + song.tracks.size() + " tracks");
					return true;
				}
				if (args[0].equalsIgnoreCase("reload"))
				{
					songManager.readSongs();
					msg(p, "Songs reloaded - found " + songManager.songs.size() + ".");
					return true;
				}
				if (args[0].equalsIgnoreCase("rp"))
				{
					songManager.readSongs();
					if (args.length < 2)
					{
						msg(p, "Usage: /bgm rp <song>");
						return true;
					}
					String songName = args[1];
					RPGSong song = songManager.getSong(songName);
					if (song == null)
					{
						msg(p, "Unable to find song by name provided.");
						return true;
					}
					for (RunningTrack rt: RSongManager.runningTracks)
						if (rt.player.getName().equalsIgnoreCase(p.getName()))
							rt.stop();
					int offset = 0;
					if (args.length > 2)
					{
						try
						{
							offset = Integer.parseInt(args[2]);
						} catch (Exception e) {}
					}
					song.play(p, offset);
					return true;
				}
				if (args[0].equalsIgnoreCase("play") || args[0].equalsIgnoreCase("p"))
				{
					if (args.length < 2)
					{
						msg(p, "Usage: /bgm play <song>");
						return true;
					}
					String songName = args[1];
					RPGSong song = songManager.getSong(songName);
					if (song == null)
					{
						msg(p, "Unable to find song by name provided.");
						return true;
					}
					for (RunningTrack rt: RSongManager.runningTracks)
						if (rt.player.getName().equalsIgnoreCase(p.getName()))
							if (rt.player.getName().equalsIgnoreCase(p.getName()))
							{
								rt.stop();
								break;
							}
					int offset = 0;
					if (args.length > 2)
					{
						try
						{
							offset = Integer.parseInt(args[2]);
						} catch (Exception e) {}
					}
					song.play(p, offset);
					return true;
				}
				if (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("s"))
				{
					for (RunningTrack rt: RSongManager.runningTracks)
						if (rt.player.getName().equalsIgnoreCase(p.getName()))
							rt.stop();
					return true;
				}
				msg(p, "Usage: /bgm <list/reload/play/stop> [song]");
				return true;
			}
			if (command.getName().equalsIgnoreCase("testsound"))
			{
				RPGEvents.scheduleRunnable(new RPGEvents.PlaySoundEffect(((Player) sender).getLocation(), Sound.valueOf(args[0]), args.length > 1 ? Float.parseFloat(args[1]) : 1.0F, args.length > 2 ? Float.parseFloat(args[2]) : 1.0F), 0);
				return true;
			}
			if (command.getName().equalsIgnoreCase("class"))
			{
				if (!p.hasPermission("rpgcore.class"))
				{
					msg(p, "You do not have permissions to use this.");
					return true;
				}
				if (args.length > 0)
				{
					if (args[0].equalsIgnoreCase("setlevel"))
					{
						if (args.length < 2)
						{
							msg(p, "Usage: /class setlevel <level>");
							return true;
						}
						int level = -1;
						try
						{
							level = Integer.parseInt(args[1]);
						} catch (Exception e)
						{
							msg(p, "Enter a number.");
							return true;
						}
						if (level < 0)
						{
							msg(p, "Enter a positive number.");
							return true;
						}
						if (level > RPGClass.xpTable.size())
						{
							msg(p, "The maximum level is " + (RPGClass.xpTable.size() - 1) + ".");
							return true;
						}
						rp.getCurrentClass().xp = RPGClass.getXPRequiredForLevel(level);
						rp.checkLevel = true;
						return true;
					}
				}
				p.openInventory(ClassInventory.getClassInventory1(rp));
				return true;
			}
			if (command.getName().equalsIgnoreCase("skills"))
			{
				if (p.hasPermission("rpgcore.skills") && args.length > 0)
				{
					if (args[0].equalsIgnoreCase("unlockall") || args[0].equalsIgnoreCase("learnall"))
					{
						for (RPGSkill skill: RPGSkill.skillList)
							if (!rp.skills.contains(skill.skillName))
								rp.skills.add(skill.skillName);
						msg(p, "Learnt all skills.");
						return true;
					}
					if (args[0].equalsIgnoreCase("lockall") || args[0].equalsIgnoreCase("unlearnall"))
					{
						rp.skills.clear();
						msg(p, "Unlearnt all skills.");
						return true;
					}
					return true;
				}
				p.openInventory(SkillInventory2.getSkillInventory(rp, rp.lastSkillbookTier));
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.1F, 0.8F);
				return true;
			}
		}
		return false;
	}

	public static void msgNoTag(CommandSender p, String... msgs)
	{
		for (String msg: msgs)
			p.sendMessage(CakeLibrary.recodeColorCodes(msg));
	}

	public static void msgNoTag(CommandSender p, String msg)
	{
		p.sendMessage(CakeLibrary.recodeColorCodes(msg));
	}

	public static void msg(CommandSender p, String... msgs)
	{
		for (String msg: msgs)
			p.sendMessage(CakeLibrary.recodeColorCodes("&6[&eCakeCore&6] &e" + msg));
	}

	public static void msg(CommandSender p, String msg)
	{
		p.sendMessage(CakeLibrary.recodeColorCodes("&6[&eCakeCore&6] &e" + msg));
	}

	public static void msgConsole(String msg)
	{
		Bukkit.getConsoleSender().sendMessage(CakeLibrary.recodeColorCodes("&6[&eCakeCore&6] &e" + msg));
	}
}
