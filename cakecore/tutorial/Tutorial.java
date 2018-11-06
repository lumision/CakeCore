package cakecore.tutorial;

import org.bukkit.entity.Player;

import cakecore.classes.ClassInventory;
import cakecore.classes.RPGClass.ClassType;
import cakecore.external.Title;
import cakecore.item.RItem;
import cakecore.main.CakeCore;
import cakecore.main.CakeLibrary;
import cakecore.player.RPlayer;
import cakecore.player.RPlayerManager;

public class Tutorial 
{
	public RPlayer player;
	public boolean welcomeMessage, classMessage, classSelect, classResponse, 
	weaponGive, weaponEquip, weaponExplain, equipmentExplain, 
	equipmentExplain11, equipmentExplain12, equipmentExplain13, equipmentExplain14, equipmentExplain15, equipmentExplain2, equipmentExplain3, equipmentExplain4, equipmentExplain5,
	skillsExplain, skillsExplain1, conclusion, conclusion1;
	public static Title titleWelcome = new Title("&bWelcome", "", 20, 80, 20);
	public static Title titleClass = new Title("", "&bYou will now pick a class...", 20, 60, 20);
	public static Title titleWeapon = new Title("", "&eYou have been gifted an appropriate &nweapon&e.", 20, 60, 20);
	public static Title titleEquip = new Title("", "&a&nPlace it in your &2&nOff-Hand&a&n (Shield) slot to equip it.", 20, 32767, 20);
	public static Title titleEquipped = new Title("", "&aThe stats of that &nweapon&a have now been applied.", 20, 100, 20);
	public static Title titleSkills = new Title("&cInventory -> &b&nSkills&c", "&b(bottom right)&c click on some skill icons.", 20, 32767, 20);
	public static Title titleSkills1 = new Title("&4Left/Right Click", "&cwith the skill in your &nmain hand&c to cast it.", 20, 140, 20);
	public static Title titleConclude = new Title("", "&fThat concludes this small tutorial.", 20, 80, 20);
	public static Title titleConclude1 = new Title("", "&f&nWe hope you enjoy the game.", 20, 120, 20);
	public long ticks;
	public long equipTicks = Long.MAX_VALUE;
	public long skillsTicks = Long.MAX_VALUE;
	public Tutorial(RPlayer player)
	{
		this.player = player;
	}

	public void check()
	{
		if (player.tutorialCompleted)
			return;
		Player p = player.getPlayer();
		if (p == null)
			return;
		String name = p.getOpenInventory().getTitle();
		if (CakeLibrary.hasColor(name))
		{
			if (ticks - equipTicks >= 140 && name.contains("Learnt Skills"))
				skillsTicks = ticks;
			return;
		}
		ticks += 10;
		if (ticks > 20 && !welcomeMessage)
		{
			titleWelcome.sendPlayer(p);
			welcomeMessage = true;
		} else if (ticks >= 180 && !classMessage)
		{
			titleClass.sendPlayer(p);
			classMessage = true;
		} else if (ticks >= 300 && !classSelect)
		{
			p.openInventory(ClassInventory.getClassInventory1(player));
			classSelect = true;
		} else if (ticks >= 300 && !classResponse && !player.currentClass.equals(ClassType.ALL))
		{
			Title title = new Title("&4<&c " + player.currentClass.getClassName() + " &4>", "&e...is the class you have chosen.", 20, 60, 20);
			title.sendPlayer(p);
			classResponse = true;
		} else if (ticks >= 420 && !weaponGive)
		{
			titleWeapon.sendPlayer(p);
			RItem ri = CakeCore.getItemFromDatabase(player.currentClass.getDamageType() == 1 ? "BeginnerWand" : "BeginnerSword");
			player.giveItem(ri);
			weaponGive = true;
		} else if (ticks >= 540 && !weaponEquip)
		{
			titleEquip.sendPlayer(p);
			weaponEquip = true;
		} else if (ticks >= 560 && !CakeLibrary.isItemStackNull(p.getEquipment().getItemInOffHand()) && !weaponExplain)
		{
			titleEquipped.sendPlayer(p);
			weaponExplain = true;
			equipTicks = ticks;
		} else if (ticks - equipTicks >= 140 && !skillsExplain)
		{
			titleSkills.sendPlayer(p);
			skillsExplain = true;
		} else if (ticks - skillsTicks >= 20 && !skillsExplain1)
		{
			titleSkills1.sendPlayer(p);
			skillsExplain1 = true;
		} else if (ticks - skillsTicks >= 200 && !conclusion)
		{
			titleConclude.sendPlayer(p);
			conclusion = true;
		} else if (ticks - skillsTicks >= 320 && !conclusion1)
		{
			titleConclude1.sendPlayer(p);
			conclusion1 = true;
			player.tutorialCompleted = true;
			RPlayerManager.writeData(player);
		}
	}
}
