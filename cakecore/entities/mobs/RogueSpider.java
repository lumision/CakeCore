package cakecore.entities.mobs;

import org.bukkit.entity.Monster;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RogueSpider extends RPGMonster
{
	public static double maxHealth = 450.0D;
	public static String name = "§2Rogue Spider §7Lv. 20";

	public RogueSpider(Monster entity)
	{
		super(entity, false);
		entity.setMaxHealth(maxHealth);
		entity.setHealth(maxHealth);
		entity.setCustomName(name);
		entity.setCustomNameVisible(true);

		entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4));
		entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2));
		entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
	}

	@Override
	public boolean tick()
	{
		super.tick();
		if (isDead())
			return true;
		if (castDelay > 0 || target == null)
			return false;
		double distanceSq = target.getLocation().distanceSquared(entity.getLocation());

		int i = rand.nextInt(10) + 1;
		if (i <= 5 && distanceSq <= 25)
			castShadowStab(3, 16);
		else if (i <= 8 && distanceSq > 25)
			castDash(8);
		else
			castKunai(6, 24);
		return false;
	}
}
