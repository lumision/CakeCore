package cakecore.skills;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cakecore.buff.Buff;
import cakecore.buff.Stats;
import cakecore.classes.RPGClass.ClassType;
import cakecore.main.CakeLibrary;
import cakecore.main.RPGEvents;
import cakecore.player.RPGParty;
import cakecore.player.RPlayer;

public class Bless extends RPGSkill
{
	public final static String skillName = "Bless";
	public final static boolean passiveSkill = false;
	public final static int skillTier = 1;
	public final static int castDelay = 0;
	public final static ClassType classType = ClassType.PRIEST;
	public final static int cooldown = 60;
	public final static Stats buffStats = Stats.createStats("&eBless", new ItemStack(38, 1, (short) 7))
			.setMagicDamageMultiplier(1.2F)
			.setMeleeDamageMultiplier(1.2F)
			.setBuffDuration(5 * 60 * 20);
	public Bless()
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
		return CakeLibrary.addLore(CakeLibrary.renameItem(new ItemStack(38, 1, (short) 7), 
				"&eBless"),
				"&7Buff:",
				"&7 * Magic Damage: +" + CakeLibrary.convertMultiplierToAddedPercentage(buffStats.magicDamageMultiplier) + "%",
				"&7 * Melee Damage: +" + CakeLibrary.convertMultiplierToAddedPercentage(buffStats.meleeDamageMultiplier) + "%",
				"&7 * Buff Duration: " + CakeLibrary.convertTimeToString(buffStats.buffDuration / 20),
				"&7 * Party Buff",
				"&f",
				"&7Cooldown: " + CakeLibrary.convertTimeToString(cooldown),
				"&f",
				"&8&oBestows holy power to the",
				"&8&oaffected; increasing",
				"&8&ooverall attack efficiency.",
				"&f",
				"&7Skill Tier: " + RPGSkill.skillTierNames[skillTier],
				"&7Class: " + classType.getClassName());
	}

	public static void applyEffect(RPlayer player, Buff b)
	{
		Player p = player.getPlayer();
		if (p == null)
			return;
		RPGEvents.scheduleRunnable(new RPGEvents.PlayEffect(Effect.STEP_SOUND, p, 89), 0);
		b.applyBuff(player);
		player.updateScoreboard = true;
	}
}
