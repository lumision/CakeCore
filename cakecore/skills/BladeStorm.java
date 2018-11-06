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
import net.minecraft.server.v1_12_R1.EnumParticle;

public class BladeStorm extends RPGSkill
{
	public final static String skillName = "Blade Storm";
	public final static boolean passiveSkill = false;
	public final static int skillTier = 2;
	public final static int castDelay = 10;
	public final static ClassType classType = ClassType.ASSASSIN;
	public final static float damage = 1.2F;
	public final static int hits = 16;
	public final static int cooldown = 6;
	public BladeStorm()
	{
		super(skillName, passiveSkill, castDelay, damage, classType, skillTier);
	}

	@Override
	public void instantiate(RPlayer player)
	{
		super.applyCooldown(player, cooldown);
		Location target = player.getPlayer().getLocation();
		player.getPlayer().getWorld().playSound(player.getPlayer().getEyeLocation(), Sound.ENTITY_CREEPER_DEATH, 1.0F, 0.7F);
		Vector vector = new Vector(0, -1, 0).normalize().multiply(1.0F);
		int delay;	
		for (int i = 0; i < hits; i++)
		{
			ArrayList<LivingEntity> hit = new ArrayList<LivingEntity>();
			Vector offset = new Vector(3 - rand.nextInt(7), 8 + rand.nextInt(3), 3 - rand.nextInt(7));
			Location start = target.clone().add(offset);
			
			int multiplier = 0;
	        
			while (multiplier < 16)
			{
				multiplier++;
				delay = multiplier + i;
				Location point = start.clone().add(vector.clone().multiply(multiplier));
				if (!CakeLibrary.getPassableBlocks().contains(point.getBlock().getType()))
				{
					RPGEvents.scheduleRunnable(new RPGEvents.PlaySoundEffect(point, Sound.BLOCK_FIRE_EXTINGUISH, 0.1F, 1.0F), delay);
					RPGEvents.scheduleRunnable(new RPGEvents.PlayEffect(Effect.STEP_SOUND, point, point.getBlock().getTypeId()), delay);
					break;
				}
				RPGEvents.scheduleRunnable(new RPGEvents.ParticleEffect(EnumParticle.CRIT_MAGIC, point, 0.1F, 3), delay);
				RPGEvents.scheduleRunnable(new RPGEvents.AOEDetectionAttackWithBlockBreakEffect(hit, point, 1.25D, getUnvariedDamage(player), player.getPlayer(), 57), delay);
			}
		}
	}

	@Override
	public ItemStack getSkillItem()
	{
		return CakeLibrary.addLore(CakeLibrary.renameItem(new ItemStack(Material.GHAST_TEAR), 
				"&4Blade Storm"),
				"&7Damage: " + (int) (damage * 100.0F) + "% x " + hits + " Hits",
				"&7Radius: 2 blocks",
				"&7Cooldown: " + cooldown + "s",
				"&f",
				"&8&oSummon a set of blades",
				"&8&oto fall around you.",
				"&f",
				"&7Skill Tier: " + RPGSkill.skillTierNames[skillTier],
				"&7Class: " + classType.getClassName());
	}
}
