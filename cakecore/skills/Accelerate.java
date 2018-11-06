package cakecore.skills;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cakecore.buff.Buff;
import cakecore.buff.Stats;
import cakecore.classes.RPGClass.ClassType;
import cakecore.main.CakeLibrary;
import cakecore.main.RPGEvents;
import cakecore.player.RPGParty;
import cakecore.player.RPlayer;

public class Accelerate extends RPGSkill
{
	public final static String skillName = "Accelerate";
	public final static boolean passiveSkill = false;
	public final static int skillTier = 2;
	public final static int castDelay = 0;
	public final static ClassType classType = ClassType.MAGE;
	public final static int cooldown = 60;
	public final static Stats buffStats = Stats.createStats("&bAccelerate", new ItemStack(Material.FEATHER, 1))
			.setAttackSpeedMultiplier(1.2F)
			.setBuffDuration(5 * 60 * 20);
	public Accelerate()
	{
		super(skillName, passiveSkill, castDelay, 0, classType, skillTier);
	}

	@Override
	public void instantiate(RPlayer player)
	{
		super.applyCooldown(player, cooldown);
		Buff b = Buff.createBuff(buffStats);
		if (player.partyID == -1)
			applyEffect(player, b);
		else
			for (RPlayer partyMember: RPGParty.getParty(player.partyID).members)
				applyEffect(partyMember, b);
	}

	@Override
	public ItemStack getSkillItem()
	{
		return CakeLibrary.addLore(CakeLibrary.renameItem(new ItemStack(Material.SUGAR, 1), 
				"&bAccelerate"),
				"&7Buff:",
				"&7 * Attack Speed: +" + CakeLibrary.convertMultiplierToAddedPercentage(buffStats.attackSpeedMultiplier) + "%",
				"&7 * Buff Duration: " + CakeLibrary.convertTimeToString(buffStats.buffDuration / 20),
				"&7 * Party Buff",
				"&7Cooldown: " + cooldown + "s",
				"&f",
				"&8&oApplies an arcane buff that",
				"&8&oassists mobility; increasing",
				"&8&ooverall attack speed.",
				"&f",
				"&7Skill Tier: " + RPGSkill.skillTierNames[skillTier],
				"&7Class: " + classType.getClassName());
	}

	public static void applyEffect(RPlayer player, Buff b)
	{
		Player p = player.getPlayer();
		if (p == null)
			return;
		RPGEvents.scheduleRunnable(new RPGEvents.PlayEffect(Effect.STEP_SOUND, p, 20), 0);
		b.applyBuff(player);
		player.updateScoreboard = true;
	}
}
