package cakecore.skills;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import cakecore.classes.RPGClass.ClassType;
import cakecore.main.CakeLibrary;
import cakecore.main.RPGEvents;
import cakecore.player.RPlayer;

public class PowerSlash1 extends RPGSkill
{
	public final static String skillName = "Power Slash I";
	public final static boolean passiveSkill = false;
	public final static int skillTier = 1;
	public final static int castDelay = 20;
	public final static ClassType classType = ClassType.WARRIOR;
	public final static float damage = 2.6F;
	public final static int cooldown = 2;
	public final static int size = 7;
	public PowerSlash1()
	{
		super(skillName, passiveSkill, castDelay, damage, classType, skillTier);
	}

	@Override
	public void instantiate(RPlayer player)
	{
		super.applyCooldown(player, 2);
		ArrayList<LivingEntity> hit = new ArrayList<LivingEntity>();
		
		Location horizon = player.getPlayer().getLocation();
		horizon.setYaw(horizon.getYaw() - 100F);
		horizon.setPitch(25F);
		
		Vector slashDirection = horizon.getDirection().normalize();
		Vector direction = player.getPlayer().getLocation().getDirection().normalize();
		Location startPointCenter = player.getPlayer().getEyeLocation();
		
		int multiplier = 1;
		while (multiplier < 6)
		{
			startPointCenter = startPointCenter.add(direction);
			if (CakeLibrary.getNearbyLivingEntitiesExcludePlayers(startPointCenter, 1.0D).size() > 0)
				break;
			multiplier++;
		}
		
		Location startPoint = startPointCenter.clone().add(slashDirection.clone().multiply(-size));
		
		multiplier = 1;
		int delay = 0;
        player.getPlayer().getWorld().playSound(player.getPlayer().getEyeLocation(), Sound.ENTITY_GHAST_SHOOT, 0.1F, 1.5F);
		while (multiplier < size * 2)
		{
			multiplier++;
			delay = multiplier / 2;
			Location point = startPoint.clone().add(slashDirection.clone().multiply(multiplier));
			RPGEvents.scheduleRunnable(new RPGEvents.PlayEffect(Effect.STEP_SOUND, point, 20), delay);
			if (multiplier % 2 == 0)
			RPGEvents.scheduleRunnable(new RPGEvents.AOEDetectionAttackWithBlockBreakEffect(hit, point, 1.25D, getUnvariedDamage(player), player.getPlayer(), 20), delay);
		}
	}

	@Override
	public ItemStack getSkillItem()
	{
		return CakeLibrary.addLore(CakeLibrary.renameItem(new ItemStack(Material.IRON_SWORD, 1), 
				"&cPower Slash I"),
				"&7Damage: " + (int) (damage * 100) + "%",
				"&7Cooldown: " + cooldown + "s",
				"&f",
				"&8&oUnleash an almighty slash",
				"&f",
				"&7Skill Tier: " + RPGSkill.skillTierNames[skillTier],
				"&7Class: " + classType.getClassName());
	}
}
