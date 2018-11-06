package cakecore.entities.bosses;

import org.bukkit.Location;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import cakecore.entities.mobs.RPGMonster;
import cakecore.entities.mobs.RPGMonsterSpawn;
import cakecore.item.RItem;
import cakecore.main.CakeCore;
import cakecore.main.CakeLibrary;
import cakecore.main.RPGEvents;
import cakecore.main.RPGEvents.Bind;
import net.minecraft.server.v1_12_R1.EnumParticle;

public class CorruptedMage extends RPGMonster
{
	public static double maxHealth = 70000.0D;
	public static String name = "§3Corrupted Mage §7Lv. 42";

	public static final int arcaneBeamDamage = 6;
	public static final int arcaneBeamDelay = 12;

	public static final int minionSpawnDelay = 24;

	public static final String skullName = "mageblue";

	public int phase = 0;
	public int phaseTicks = 0;
	public Location down;

	public CorruptedMage(Monster entity)
	{
		super(entity, true);
		entity.setMaxHealth(maxHealth);
		entity.setHealth(maxHealth);
		entity.setCustomName(name);
		entity.setCustomNameVisible(true);

		entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
		entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));

		EntityEquipment eq = entity.getEquipment();

		if (CakeCore.heads.containsKey(skullName))
			eq.setHelmet(CakeLibrary.getSkullWithTexture(CakeCore.heads.get(skullName)));
		RItem chestplate = CakeCore.getItemFromDatabase("RobesOfCorruption");
		RItem leggings = CakeCore.getItemFromDatabase("PantsOfCorruption");
		RItem boots = CakeCore.getItemFromDatabase("BootsOfCorruption");
		RItem hand = CakeCore.getItemFromDatabase("RodOfCorruption");

		if (chestplate != null)
			eq.setChestplate(chestplate.createItem());
		if (leggings != null)
			eq.setLeggings(leggings.createItem());
		if (boots != null)
			eq.setBoots(boots.createItem());
		if (hand != null)
			eq.setItemInMainHand(hand.createItem());

		eq.setHelmetDropChance(0.0F);
		eq.setItemInMainHandDropChance(0);
		eq.setChestplateDropChance(0);
		eq.setLeggingsDropChance(0);
		eq.setBootsDropChance(0);
	}

	@Override
	public boolean tick()
	{
		super.tick();
		if (isDead())
			return true;
		switch (phase)
		{
		case 0:
			phaseTicks = 0;
			if (entity.getHealth() < entity.getMaxHealth() * 0.75D)
			{
				Bind.bindTarget(entity, 10 * 20, false);
				down = entity.getLocation();
				down.setPitch(75);
				phase++;
			}
			break;
		case 1:
			phaseTicks++;
			entity.teleport(down);
			new RPGEvents.ParticleEffect(EnumParticle.BLOCK_DUST, entity.getEyeLocation(), 0.4F, 16, 0, 152).run();
			if (phaseTicks > 10 * 20)
				phase++;
			break;
		case 2:
			phaseTicks = 0;
			if (entity.getHealth() < entity.getMaxHealth() * 0.25D)
			{
				Bind.bindTarget(entity, 10 * 20, false);
				down = entity.getLocation();
				down.setPitch(75);
				phase++;
			}
			break;
		case 3:
			phaseTicks++;
			entity.teleport(down);
			new RPGEvents.ParticleEffect(EnumParticle.BLOCK_DUST, entity.getEyeLocation(), 0.4F, 16, 0, 152).run();
			if (phaseTicks > 10 * 20)
				phase++;
			break;
		case 4:
			break;
		}
		if (castDelay > 0 || target == null)
			return false;

		int i = rand.nextInt(20) + 1;
		if (i <= 8)
			castArcaneBeam(6, 12);
		else if (i <= 14)
			castFireball(8, 12, 3 * 20);
		else if (i <= 17)
			new MobArcaneStormE(this, 6, 24, 16);
		else if (i <= 19)
			new MobSunfireE(this, 16, 72, 5 * 20);
		else
			castMinionSpawn();
		return false;
	}

	public void castMinionSpawn()
	{
		castDelay = minionSpawnDelay;

		for (int i = 0; i < 4 + rand.nextInt(4); i++)
		{
			int type = rand.nextInt(6);
			RPGMonsterSpawn spawn = RPGMonsterSpawn.getRPGMonsterSpawn(type <= 2 ? "ReinforcedSkeleton" : type <= 4 ? "MageZombie" : "SorcererZombie");
			spawn.spawnMonster(entity.getLocation().add(rand.nextInt(5) - rand.nextInt(5), 2, rand.nextInt(5) - rand.nextInt(5)));
		}
	}
}
