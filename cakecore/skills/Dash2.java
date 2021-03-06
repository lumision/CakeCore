package cakecore.skills;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import cakecore.classes.RPGClass.ClassType;
import cakecore.main.CakeLibrary;
import cakecore.main.RPGEvents;
import cakecore.player.RPlayer;

public class Dash2 extends RPGSkill
{
	public final static String skillName = "Dash II";
	public final static boolean passiveSkill = false;
	public final static int skillTier = 2;
	public final static int castDelay = 10;
	public final static float damage = 1.8F;
	public final static ClassType classType = ClassType.ASSASSIN;
	public Dash2()
	{
		super(skillName, passiveSkill, castDelay, 0, classType, skillTier, "Dash");
	}

	@Override
	public void instantiate(RPlayer player)
	{
		Location b = null;
		int length = 8;
		if (player.getPlayer().isSneaking())
		{
			Vector vector = player.getPlayer().getLocation().getDirection().setY(0);
			vector.setX(-vector.getX()).setZ(-vector.getZ()).normalize();
			for (int i = 1; i <= length; i++)
			{
				Location point = player.getPlayer().getLocation().add(vector.clone().multiply(i)).add(0.5f, 0, 0.5f);
				Location b1 = point.clone().add(0.5f, 1, 0.5f);
				if (!CakeLibrary.getPassableBlocks().contains(point.getBlock().getType()) || !CakeLibrary.getPassableBlocks().contains(b1.getBlock().getType()))
					break;
				b = point.add(0, -1, 0);
			}
		} else {
			b = player.getPlayer().getTargetBlock(CakeLibrary.getPassableBlocks(), length).getLocation();
		}
		if (b == null)
			return;
		Location b1 = b.clone().add(0, 1, 0);
		Location b2 = b.clone().add(0, 2, 0);
		if (!CakeLibrary.getPassableBlocks().contains(b1.getBlock().getType()) || !CakeLibrary.getPassableBlocks().contains(b2.getBlock().getType()))
		{
			player.getPlayer().sendMessage(CakeLibrary.recodeColorCodes("&c* You cannot dash into a block!"));
			return;
		}
		int yDiff = 0;
		for (int y = b.getBlockY(); y > 0; y--)
		{
			b.setY(y);
			yDiff++;
			if (!CakeLibrary.getPassableBlocks().contains(b.getBlock().getType()))
				break;
		}
		if (yDiff > 5)
		{
			player.getPlayer().sendMessage(CakeLibrary.recodeColorCodes("&c* The dropdown is too huge to dash to"));
			return;
		}
		Location start = player.getPlayer().getLocation();
		
		ArrayList<LivingEntity> hit = new ArrayList<LivingEntity>();
		Location line = b1.clone().subtract(start);
		Vector vector = line.toVector().normalize().multiply(1.5D);
		int dist = (int) (line.getX() / vector.getX());
		int multiplier = 0;
		while (multiplier < dist)
		{
			multiplier++;
			new RPGEvents.AOEDetectionAttackWithBlockBreakEffect(hit, start.clone().add(vector.multiply(multiplier)), 
					0.75D, getUnvariedDamage(player), player.getPlayer(), 20).run();;
		}
		
		Location teleport = b.clone().add(0.5D, 1, 0.5D);
		teleport.setYaw(start.getYaw());
		teleport.setPitch(start.getPitch());
		teleport.getWorld().playEffect(teleport, Effect.STEP_SOUND, 20);
		teleport.getWorld().playEffect(teleport.clone().add(0, 1, 0), Effect.STEP_SOUND, 20);
		teleport.getWorld().playEffect(start, Effect.STEP_SOUND, 20);
		teleport.getWorld().playEffect(start.clone().add(0, 1, 0), Effect.STEP_SOUND, 20);
		player.getPlayer().teleport(teleport);
		player.castDelays.remove(ShadowStab1.skillName);
		player.invulnerabilityTicks = 20;
		super.applyCooldown(player, 3);
	}

	@Override
	public ItemStack getSkillItem()
	{
		return CakeLibrary.addLore(CakeLibrary.renameItem(new ItemStack(Material.ARROW, 1), 
				"&eDash II"),
				"&7Damage: " + (int) (damage * 100) + "%",
				"&7Distance: 8 blocks",
				"&7Invulnerability: 1s",
				"&7Cooldown: 3s",
				"&f",
				"&8&oDash forward in the direction",
				"&8&oyou face; damaging anything you",
				"&8&opass through. Hold down &7&o[SNEAK]",
				"&8&oto dash in the opposite direction.",
				"&f",
				"&7Skill Tier: " + RPGSkill.skillTierNames[skillTier],
				"&7Class: " + classType.getClassName());
	}
}
