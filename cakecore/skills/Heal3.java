package cakecore.skills;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cakecore.classes.RPGClass.ClassType;
import cakecore.main.CakeLibrary;
import cakecore.main.RPGEvents;
import cakecore.player.RPGParty;
import cakecore.player.RPlayer;
import net.minecraft.server.v1_12_R1.EnumParticle;

public class Heal3 extends RPGSkill
{
	public final static String skillName = "Heal III";
	public final static boolean passiveSkill = false;
	public final static int skillTier = 4;
	public final static int castDelay = 10;
	public final static ClassType classType = ClassType.PRIEST;
	public final static float healAmount = 4;
	public Heal3()
	{
		super(skillName, passiveSkill, castDelay, 0, classType, skillTier, "Heal");
	}

	@Override
	public void instantiate(RPlayer player)
	{
		super.applyCooldown(player, 3);
		if (player.partyID == -1)
			applyEffect(player, player.getPlayer(), healAmount);
		else
			for (RPlayer partyMember: RPGParty.getParty(player.partyID).members)
				applyEffect(partyMember, player.getPlayer(), healAmount);
	}

	@Override
	public ItemStack getSkillItem()
	{
		return CakeLibrary.addLore(CakeLibrary.renameItem(new ItemStack(38), 
				"&cHeal III"),
				"&7Heal: " + (healAmount / 2.0F) + " hearts",
				"&7Cooldown: 3s",
				"&f",
				"&8&oHeals the user and all party",
				"&8&omembers within 16 blocks.",
				"&f",
				"&7Skill Tier: " + RPGSkill.skillTierNames[skillTier],
				"&7Class: " + classType.getClassName());
	}

	public static void applyEffect(RPlayer player, Player caster, double healAmount)
	{
		Location l = player.getPlayer().getLocation();
		if (l.distance(caster.getLocation()) > 16.0D)
			return;
		for (int i = 0; i < 3; i++)
			RPGEvents.scheduleRunnable(new RPGEvents.PlaySoundEffect(l, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.2F, 0.8F + (i * 0.2F)), i * 2);
		new RPGEvents.ParticleEffect(EnumParticle.BLOCK_CRACK, l.add(0, 1.25D, 0), 0.5F, 32, 0, 35).run();
		double health = player.getPlayer().getHealth();
		health += healAmount;
		player.getPlayer().setHealth(health > player.getPlayer().getMaxHealth() ? player.getPlayer().getMaxHealth() : health);
	}
}
