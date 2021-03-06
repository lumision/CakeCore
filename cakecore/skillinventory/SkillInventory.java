package cakecore.skillinventory;

public class SkillInventory 
{
	/*
	public static ItemStack locked = new SpawnEgg(EntityType.ENDERMAN).toItemStack(1);
	public static ItemStack levelSkills = CakeLibrary.editNameAndLore(new ItemStack(Material.WOOL, 1, (short) 5),
			"&a&nSpend skill points",
			"&7Level up or unlock skills by",
			"&7using your own skill points.");
	public static ItemStack delevelSkills = CakeLibrary.editNameAndLore(new ItemStack(Material.WOOL, 1, (short) 14), 
			"&c&nReclaim skill points",
			"&7Individually retract each skill's level",
			"&7to claim back skill points.");
	public static ItemStack returnToBook = CakeLibrary.renameItem(new ItemStack(Material.WOOL, 1, (short) 0), 
			"&f&nReturn to skill selection");
	public static ItemStack skillPoints = CakeLibrary.renameItem(new ItemStack(Material.PAPER), 
			"&6Skill Points: &e");
	public static ItemStack adminCommands = CakeLibrary.editNameAndLore(new ItemStack(Material.SIGN), 
			"&f&nYou have sufficient permissions to use these:",
			"&7/skills setsp <amt> [player]:",
			"  &7Sets the skill points of the active class.",
			"&7/skills addsp <(-)amt> [player]",
			"  &7Adds to the skill points of the active class.",
			"&cOr you can click this sign to add 10 skill points.");

	public static ItemStack modeLevel = CakeLibrary.renameItem(new ItemStack(Material.WOOL, 1, (short) 5), "&aMode: Spending Skillpoints");
	public static ItemStack modeDelevel = CakeLibrary.renameItem(new ItemStack(Material.WOOL, 1, (short) 14), "&cMode: Reclaiming Skillpoints");
	public static ItemStack modeSelect = CakeLibrary.renameItem(new ItemStack(Material.WOOL, 1, (short) 0), "&fMode: Selecting Skills");

	public static Inventory getSkillInventory(RPlayer player, int mode) //mode == 0: skillbook, 1: use skillPoints, 2: reclaim skillPoints
	{
		Inventory inv;
		int size = 9 * (player.currentClass.getTier() + 2);
		switch(player.currentClass)
		{
		//tier 1
		case ASSASSIN:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Thief"));
			setThiefSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case PRIEST:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Priest"));
			setPriestSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case WARRIOR:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Warrior"));
			setWarriorSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case MAGE:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Mage"));
			setMageSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;

		//tier 2
		case SORCERER:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Sorcerer"));
			setMageSkills(inv, player, 1);
			setSorcererSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case SHAMAN:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Shaman"));
			setMageSkills(inv, player, 1);
			setShamanSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case KNIGHT:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Knight"));
			setWarriorSkills(inv, player, 1);
			setKnightSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case PALADIN:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Paladin"));
			setWarriorSkills(inv, player, 1);
			setPaladinSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case FRIAR:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Friar"));
			setPriestSkills(inv, player, 1);
			setFriarSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case ASSASSIN:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Assassin"));
			setThiefSkills(inv, player, 1);
			setAssassinSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;

		//tier 3
		case ODIN:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Odin"));
			setWarriorSkills(inv, player, 2);
			setPaladinSkills(inv, player, 1);
			setOdinSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case HERO:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Hero"));
			setWarriorSkills(inv, player, 2);
			setPaladinSkills(inv, player, 1);
			setHeroSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case ARCHMAGE:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Archmage"));
			setMageSkills(inv, player, 2);
			setSorcererSkills(inv, player, 1);
			setArchmageSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case THAUMATURGE:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Thaumaturge"));
			setMageSkills(inv, player, 2);
			setSorcererSkills(inv, player, 1);
			setThaumaturgeSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case DUALIST:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Dualist"));
			setThiefSkills(inv, player, 2);
			setAssassinSkills(inv, player, 1);
			setDualistSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		case BISHOP:
			inv = Bukkit.createInventory(null, size, CakeLibrary.recodeColorCodes("&9Skillbook: Bishop"));
			setPriestSkills(inv, player, 2);
			setFriarSkills(inv, player, 1);
			setBishopSkills(inv, player, 0);
			setUI(inv, size / 9, player, mode);
			return inv;
		default: return null;
		}
	}

	public static void updateSkillInventory(Inventory inv, RPlayer player)
	{
		String invName = CakeLibrary.removeColorCodes(inv.getName());
		if (invName.contains("Warrior"))
			setWarriorSkills(inv, player, 0);
		if (invName.contains("Mage"))
			setMageSkills(inv, player, 0);
		if (invName.contains("Priest"))
			setPriestSkills(inv, player, 0);
		if (invName.contains("Thief"))
			setThiefSkills(inv, player, 0);
		if (invName.contains("Sorcerer"))
		{
			setSorcererSkills(inv, player, 0);
			setMageSkills(inv, player, 1);
		}
		if (invName.contains("Archmage"))
		{
			setArchmageSkills(inv, player, 0);
			setSorcererSkills(inv, player, 1);
			setMageSkills(inv, player, 2);
		}
		if (invName.contains("Shaman"))
		{
			setShamanSkills(inv, player, 0);
			setMageSkills(inv, player, 1);
		}
		if (invName.contains("Thaumaturge"))
		{
			setThaumaturgeSkills(inv, player, 0);
			setShamanSkills(inv, player, 1);
			setMageSkills(inv, player, 2);
		}
		if (invName.contains("Knight"))
		{
			setKnightSkills(inv, player, 0);
			setWarriorSkills(inv, player, 1);
		}
		if (invName.contains("Hero"))
		{
			setHeroSkills(inv, player, 0);
			setKnightSkills(inv, player, 1);
			setWarriorSkills(inv, player, 2);
		}
		if (invName.contains("Paladin"))
		{
			setPaladinSkills(inv, player, 0);
			setWarriorSkills(inv, player, 1);
		}
		if (invName.contains("Odin"))
		{
			setOdinSkills(inv, player, 0);
			setPaladinSkills(inv, player, 1);
			setWarriorSkills(inv, player, 2);
		}
		if (invName.contains("Friar"))
		{
			setFriarSkills(inv, player, 0);
			setPriestSkills(inv, player, 1);
		}
		if (invName.contains("Bishop"))
		{
			setBishopSkills(inv, player, 0);
			setFriarSkills(inv, player, 1);
			setPriestSkills(inv, player, 2);
		}
		if (invName.contains("Assassin"))
		{
			setAssassinSkills(inv, player, 0);
			setThiefSkills(inv, player, 1);
		}
		if (invName.contains("Dualist"))
		{
			setDualistSkills(inv, player, 0);
			setAssassinSkills(inv, player, 1);
			setThiefSkills(inv, player, 2);
		}
		updateSkillPointsIcon(inv, player);
	}
	
	public static void updateSkillPointsIcon(Inventory inv, RPlayer player)
	{
		for (int i = 0; i < inv.getSize(); i++)
		{
			ItemStack is = inv.getItem(i);
			if (CakeLibrary.isItemStackNull(is))
				continue;
			if (CakeLibrary.removeColorCodes(CakeLibrary.getItemName(is)).startsWith("Skill Points: "))
				setSkillPointsIcon(inv, i, player);
		}
	}

	public static ItemStack changeForInventory(ItemStack skillItem, String playerName)
	{
		if (skillItem == null)
			return null;
		skillItem.setAmount(1);
		skillItem = CakeLibrary.setUnbreakable(CakeLibrary.addLore(skillItem, "&cOwner: " + playerName));
		if (skillItem.getTypeId() == 383)
			skillItem = new ItemStack(Material.AIR);
		return skillItem;
	}

	public static void setSkillPointsIcon(Inventory inv, int slot, RPlayer player)
	{
		int points = player.getCurrentClass().skillPoints;
		ItemStack sp = CakeLibrary.renameItem(skillPoints.clone(), "&6Skill Points: &e" + points);
		sp.setAmount(points < 1 ? 1 : points > 64 ? 1 : points);
		inv.setItem(slot, sp);
	}

	public static void setUI(Inventory inv, int line, RPlayer player, int mode)
	{
		int offset = (line - 1) * 9;
		inv.setItem(offset + 5, levelSkills.clone());
		inv.setItem(offset + 3, delevelSkills.clone());
		inv.setItem(offset + 4, returnToBook.clone());
		//inv.setItem(offset, getClassIcon(player.currentClass));
		inv.setItem(offset, mode == 0 ? modeSelect.clone() : mode == 1 ? modeLevel.clone() : modeDelevel.clone());
		setSkillPointsIcon(inv, offset + 1, player);
		if (player.getPlayer().hasPermission("rpgcore.skills"))
			inv.setItem(offset + 8, adminCommands.clone());
	}

	public static ItemStack getClassIcon(ClassType classType)
	{
		switch (classType)
		{
		case WARRIOR:
			return CakeLibrary.renameItem(new ItemStack(Material.IRON_SWORD), "&fClass: " + getClassColor(classType) + "Warrior");
		case KNIGHT:
			return CakeLibrary.renameItem(new ItemStack(Material.IRON_CHESTPLATE), "&fClass: " + getClassColor(classType) + "Knight");
		case HERO:
			return CakeLibrary.renameItem(new ItemStack(Material.DIAMOND_CHESTPLATE), "&fClass: " + getClassColor(classType) + "Hero");
		case PALADIN:
			return CakeLibrary.renameItem(new ItemStack(Material.GOLD_SWORD), "&fClass: " + getClassColor(classType) + "Paladin");
		case ODIN:
			return CakeLibrary.renameItem(new ItemStack(Material.DIAMOND_SWORD), "&fClass: " + getClassColor(classType) + "Odin");
		case MAGE:
			return CakeLibrary.renameItem(new ItemStack(Material.STICK), "&fClass: " + getClassColor(classType) + "Mage");
		case SHAMAN:
			return CakeLibrary.renameItem(new ItemStack(Material.PAPER), "&fClass: " + getClassColor(classType) + "Shaman");
		case THAUMATURGE:
			return CakeLibrary.renameItem(new ItemStack(Material.BOOK), "&fClass: " + getClassColor(classType) + "Thaumaturge");
		case SORCERER:
			return CakeLibrary.renameItem(new ItemStack(Material.SUGAR), "&fClass: " + getClassColor(classType) + "Sorcerer");
		case ARCHMAGE:
			return CakeLibrary.renameItem(new ItemStack(Material.GLOWSTONE_DUST), "&fClass: " + getClassColor(classType) + "Archmage");
		case PRIEST:
			return CakeLibrary.renameItem(new ItemStack(38, 1, (short) 3), "&fClass: " + getClassColor(classType) + "Priest");
		case FRIAR:
			return CakeLibrary.renameItem(new ItemStack(38, 1, (short) 6), "&fClass: " + getClassColor(classType) + "Friar");
		case BISHOP:
			return CakeLibrary.renameItem(new ItemStack(38, 1, (short) 8), "&fClass: " + getClassColor(classType) + "Bishop");
		case ASSASSIN:
			return CakeLibrary.renameItem(new ItemStack(Material.ENDER_PEARL), "&fClass: " + getClassColor(classType) + "Thief");
		case ASSASSIN:
			return CakeLibrary.renameItem(new ItemStack(Material.EYE_OF_ENDER), "&fClass: " + getClassColor(classType) + "Assassin");
		case DUALIST:
			return CakeLibrary.renameItem(new ItemStack(Material.NETHER_STAR), "&fClass: " + getClassColor(classType) + "Dualist");
		}
		return null;
	}

	public static String getClassColor(ClassType classType)
	{
		switch (classType.getTier())
		{
		case 1:
			return "�c";
		case 2:
			return "�b";
		case 3:
			return "�e";
		default:
			return "";
		}
	}

	public static void updatePlayerInventorySkills(RPlayer player)
	{
		Player p = player.getPlayer();
		if (p == null)
			return;
		Inventory inv = p.getInventory();
		for (int i = 0; i < inv.getSize(); i++)
		{
			ItemStack is = inv.getItem(i);
			if (CakeLibrary.isItemStackNull(is))
				continue;
			String name = CakeLibrary.getItemName(is);
			if (!CakeLibrary.hasColor(name))
				continue;
			name = CakeLibrary.removeColorCodes(name);
			ItemStack change = null;

			for (RPGSkill skill: RPGSkill.skillList)
				if (name.equals(skill.skillName))
					change = skill.instanceGetSkillItem(player);

			if (change == null)
				continue;
			inv.setItem(i, changeForInventory(change, player.getPlayerName()));
		}
	}

	public static void setWarriorSkills(Inventory inv, RPlayer player, int row)
	{
		inv.setItem(0 + (row * 9), PowerPierce.getSkillItem(player));
		inv.setItem(1 + (row * 9), Leap.getSkillItem(player));
		inv.setItem(8 + (row * 9), IronBody.getSkillItem(player));
		inv.setItem(7 + (row * 9), Warcry.getSkillItem(player));
	}
	public static void setThiefSkills(Inventory inv, RPlayer player, int row)
	{
		inv.setItem(0 + (row * 9), ShadowStab.getSkillItem(player));
		inv.setItem(1 + (row * 9), Kunai.getSkillItem(player));
		inv.setItem(2 + (row * 9), Dash.getSkillItem(player));
		//inv.setItem(3 + (row * 9), Evade.getSkillItem(player));
		inv.setItem(8 + (row * 9), BladeMastery.getSkillItem(player));
		inv.setItem(7 + (row * 9), LightFeet.getSkillItem(player));
	}
	public static void setPriestSkills(Inventory inv, RPlayer player, int row)
	{
		inv.setItem(0 + (row * 9), HolyBolt.getSkillItem(player));
		inv.setItem(1 + (row * 9), Heal.getSkillItem(player));
		inv.setItem(2 + (row * 9), Enlightenment.getSkillItem(player));
	}
	public static void setMageSkills(Inventory inv, RPlayer player, int row)
	{
		inv.setItem(0 + (row * 9), IceBolt.getSkillItem(player));
		inv.setItem(1 + (row * 9), PoisonBolt.getSkillItem(player));
		inv.setItem(4 + (row * 9), Propulsion.getSkillItem(player));
		inv.setItem(8 + (row * 9), Wisdom.getSkillItem(player));
	}

	public static void setPaladinSkills(Inventory inv, RPlayer player, int row)
	{
	}
	public static void setKnightSkills(Inventory inv, RPlayer player, int row)
	{
	}
	public static void setAssassinSkills(Inventory inv, RPlayer player, int row)
	{
		inv.setItem(0 + (row * 9), TripleKunai.getSkillItem(player));
	}
	public static void setFriarSkills(Inventory inv, RPlayer player, int row)
	{
	}
	public static void setShamanSkills(Inventory inv, RPlayer player, int row)
	{
	}
	public static void setSorcererSkills(Inventory inv, RPlayer player, int row)
	{
	}

	public static void setOdinSkills(Inventory inv, RPlayer player, int row)
	{
	}
	public static void setHeroSkills(Inventory inv, RPlayer player, int row)
	{
	}
	public static void setDualistSkills(Inventory inv, RPlayer player, int row)
	{
		inv.setItem(4 + (row * 9), Heartspan.getSkillItem(player));
	}
	public static void setBishopSkills(Inventory inv, RPlayer player, int row)
	{
	}
	public static void setThaumaturgeSkills(Inventory inv, RPlayer player, int row)
	{
	}
	public static void setArchmageSkills(Inventory inv, RPlayer player, int row)
	{
		inv.setItem(0 + (row * 9), ArcaneBarrage.getSkillItem(player));
		inv.setItem(4 + (row * 9), Armageddon.getSkillItem(player));
	}
	*/
}

