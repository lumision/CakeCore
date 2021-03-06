package cakecore.recipes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import cakecore.item.RItem;
import cakecore.main.CakeCore;
import cakecore.main.CakeLibrary;

/**
 * Individual data files in plugins/CakeCore/recipes
 * Item names are based on the item database (plugins/CakeCore/items)
 * RPGRecipe data example:
 * 
 * result: Ifritus
 * shape: A, A, A //one item A on first row, one item A on second row and one item A on the third row
 * ingredients:
 *  A: FragmentOfIfrit
 *  
 * sound: ENTITY_PLAYER_LEVELUP
 * volume: 0.2
 * pitch: 1.0
 * 
 * result: MixedWoodenStairs
 * shape: AXX, BCX, DEF
 * ingredients: //'X' is coded to be an empty slot
 *  A: OakWood
 *  B: SpruceWood
 *  C: BirchWood
 *  D: JungleWood
 *  E: ArcaciaWood
 *  F: DarkOakWood
 * 
 */

public class RPGRecipe 
{
	public static ArrayList<RPGRecipe> recipes = new ArrayList<RPGRecipe>();
	public static final File recipesFolder = new File("plugins/CakeCore/recipes");

	public boolean requiresUnlock;
	public boolean cannotUnlock;
	public RItem result;
	public int resultAmount;
	public String[] shape;
	public HashMap<RItem, Character> ingredients;

	public Sound sound;
	public float volume, pitch;

	int items = 0;
	boolean requiresCraftingTable = false;
	public RPGRecipe(RItem result, String[] shape, HashMap<RItem, Character> ingredients, Sound sound, float volume, float pitch)
	{
		this.result = result;
		this.resultAmount = 1;
		this.shape = shape;
		this.ingredients = ingredients;

		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;

		this.requiresCraftingTable = shape.length > 2
				|| shape[0].length() >= 3 
				|| (shape.length > 1 && shape[1].length() >= 3) 
				|| (shape.length > 2 && shape[2].length() >= 3);

				items = 0;
				for (int i = 0; i < shape.length; i++)
					for (char c: shape[i].toCharArray())
						if (c != 'X')
							items++;

				recipes.add(this);
	}

	public static void readRecipeData()
	{
		recipes.clear();
		for (File file: recipesFolder.listFiles())
		{
			if (!file.getName().endsWith(".yml"))
				continue;
			try
			{
				ArrayList<String> lines = CakeLibrary.readFile(file);

				RItem result = null;
				String[] shape = null;
				HashMap<RItem, Character> ingredients = new HashMap<RItem, Character>();

				Sound sound = null;
				float volume = 0;
				float pitch = 0;
				boolean requiresUnlock = false;
				boolean cannotUnlock = false;
				int resultAmount = 1;

				for (String line: lines)
				{
					if (line.startsWith(" "))
					{
						String[] split = line.substring(1).split(": ");
						if (split.length < 2)
							continue;
						RItem ingredient = CakeCore.getItemFromDatabase(split[1]);
						if (ingredient == null)
						{
							CakeCore.msgConsole("&4Error reading recipe data: " + file.getName() + "; ingredient &c" + split[1] + " &4not in item database.");
							continue;
						}
						ingredients.put(ingredient, split[0].charAt(0));
						continue;
					}

					String[] split = line.split(": ");
					if (split.length < 2)
						continue;

					if (split[0].equalsIgnoreCase("result"))
						result = CakeCore.getItemFromDatabase(split[1]);
					else if (split[0].equalsIgnoreCase("shape"))
						shape = split[1].split(", ");
					else if (split[0].equalsIgnoreCase("sound"))
						sound = Sound.valueOf(split[1]);
					else if (split[0].equalsIgnoreCase("volume"))
						volume = Float.valueOf(split[1]);
					else if (split[0].equalsIgnoreCase("pitch"))
						pitch = Float.valueOf(split[1]);
					else if (split[0].equalsIgnoreCase("requiresUnlock"))
						requiresUnlock = Boolean.valueOf(split[1]);
					else if (split[0].equalsIgnoreCase("cannotUnlock"))
						cannotUnlock = Boolean.valueOf(split[1]);
					else if (split[0].equalsIgnoreCase("amount"))
						resultAmount = Integer.valueOf(split[1]);
				}

				if (result != null && shape != null && ingredients.size() > 0)
				{
					RPGRecipe recipe = new RPGRecipe(result, shape, ingredients, sound, volume, pitch);
					recipe.cannotUnlock = cannotUnlock;
					recipe.requiresUnlock = requiresUnlock;
					recipe.resultAmount = resultAmount;
				}
				else
					CakeCore.msgConsole("&4Error reading recipe data: " + file.getName() + "; result, shape, or ingredients is null.");
			} catch (Exception e) {}
		}
	}
	
	public ItemStack getResult()
	{
		ItemStack item = result.createItem();
		item.setAmount(resultAmount);
		return item;
	}

	public boolean crafted(ItemStack[] craftingMatrix)
	{
		boolean craftingTable = craftingMatrix.length == 9;
		if (requiresCraftingTable && !craftingTable)
			return false;

		int inputItems = 0;
		for (int i = 0; i < craftingMatrix.length; i++)
			if (!CakeLibrary.isItemStackNull(craftingMatrix[i]))
				inputItems++;

		if (inputItems != items)
			return false;

		String[] matrixShape = { "", "", craftingTable ? "" : "XXX" };
		for (int i = 0; i < craftingMatrix.length; i++)
		{
			ItemStack item = craftingMatrix[i];
			if (item == null || item.getType() == Material.AIR)
			{
				matrixShape = appendMatrixShape(craftingTable, matrixShape, i, "X");
				continue;
			}
			char c = 0;
			RItem ri = new RItem(item);
			for (RItem check : ingredients.keySet())
				if (check.compare(ri))
					c = ingredients.get(check);
			if (c == 0)
			{
				matrixShape = appendMatrixShape(craftingTable, matrixShape, i, "X");
				continue;
			}
			matrixShape = appendMatrixShape(craftingTable, matrixShape, i, c + "");
		}

		int equate = 0;
		for (int rowSnip = 0; rowSnip < 4 - shape.length; rowSnip++)
		{
			for (int columnSnip = 0; columnSnip < (craftingTable ? 3 : 2); columnSnip++)
			{
				equate = 0;
				for (int i = 0; i < matrixShape.length; i++)
				{
					if (i + rowSnip >= matrixShape.length)
						break;
					if (shape.length < i + 1)
						break;
					String line = matrixShape[i + rowSnip];
					line = line.substring(columnSnip);
					if (line.length() == 0)
						continue;
					if (line.startsWith(shape[i]))
						equate++;
				}
				if (equate == shape.length)
					return true;
			}
		}

		return false;
	}

	String[] appendMatrixShape(boolean craftingTable, String[] matrixShape, int index, String character)
	{
		matrixShape[craftingTable ? (index <= 2 ? 0 : index <= 5 ? 1 : 2) : index <= 1 ? 0 : 1] += character;
		return matrixShape;
	}
}
