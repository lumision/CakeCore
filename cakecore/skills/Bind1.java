package cakecore.skills;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import cakecore.classes.RPGClass.ClassType;
import cakecore.main.CakeLibrary;
import cakecore.main.RPGEvents.Bind;
import cakecore.player.RPlayer;

public class Bind1 extends RPGSkill
{
	public final static String skillName = "Bind I";
	public final static boolean passiveSkill = false;
	public final static int skillTier = 1;
	public final static int castDelay = 0;
	public final static ClassType classType = ClassType.ASSASSIN;
	public final static float damage = 0.0F;
	public final static int cooldown = 15;
	public final static int bindDuration = 3 * 20;
	public Bind1()
	{
		super(skillName, passiveSkill, castDelay, damage, classType, skillTier);
	}

	@Override
	public void instantiate(RPlayer player)
	{
		LivingEntity target = null;
		
		Vector direction = player.getPlayer().getLocation().getDirection().normalize();
		int check = 0;
		boolean b = false;
		while (check < 3)
		{
			check++;
			Location point1 = player.getPlayer().getEyeLocation().add(direction.clone().multiply(check));
			ArrayList<LivingEntity> nearby = CakeLibrary.getNearbyLivingEntities(point1, 0.5F);
			for (LivingEntity n: nearby)
				if (!(n instanceof Player))	
				{
					target = n;
					b = true;
					break;
				}
			if (b)
				break;
		}
		
		if (target == null)
		{
			player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENTITY_EGG_THROW, 0.2F, 0.5F);
			return;
		}
		
		Bind.bindTarget(target, bindDuration, true);
		super.applyCooldown(player, cooldown);
	}

	@Override
	public ItemStack getSkillItem()
	{
		return CakeLibrary.addLore(CakeLibrary.renameItem(new ItemStack(Material.STRING, 1), 
				"&bBind I"),
				"&7Bind Duration: " + (bindDuration / 20) + "s",
				"&7Cooldown: " + cooldown + "s",
				"&f",
				"&8&oBind a target within physical",
				"&8&oreach; immobilizing and disabling",
				"&8&otheir attacks for a short time.",
				"&f",
				"&7Skill Tier: " + RPGSkill.skillTierNames[skillTier],
				"&7Class: " + classType.getClassName());
	}
}
