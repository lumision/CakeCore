package cakecore.skills;

import org.bukkit.inventory.ItemStack;

import cakecore.classes.RPGClass.ClassType;
import cakecore.main.CakeLibrary;

public class Tenacity3 extends RPGSkill
{
	public final static String skillName = "Tenacity III";
	public final static boolean passiveSkill = true;
	public final static int skillTier = 5;
	public final static int castDelay = 0;
	public final static ClassType classType = ClassType.WARRIOR;
	
	public Tenacity3()
	{
		super(skillName, passiveSkill, castDelay, 0, classType, skillTier);
	}

	@Override
	public ItemStack getSkillItem()
	{
		return CakeLibrary.addLore(CakeLibrary.renameItem(new ItemStack(351, 1, (short) 6), 
				"&3Tenacity III"),
				"&7Passive Skill:",
				"&7 * Debuff Duration: -50%",
				"&f",
				"&8&oMakes received debuffs count",
				"&8&odown significantly faster.",
				"&f",
				"&7Skill Tier: " + RPGSkill.skillTierNames[skillTier],
				"&7Class: " + classType.getClassName());
	}
}
