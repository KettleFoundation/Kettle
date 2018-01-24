package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public interface IRecipe
{
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    boolean matches(InventoryCrafting inv, World worldIn);

    /**
     * Returns an Item that is the result of this recipe
     */
    ItemStack getCraftingResult(InventoryCrafting inv);

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    boolean canFit(int width, int height);

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    ItemStack getRecipeOutput();

    NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv);

default NonNullList<Ingredient> getIngredients()
    {
        return NonNullList.<Ingredient>create();
    }

default boolean isDynamic()
    {
        return false;
    }

default String getGroup()
    {
        return "";
    }
}
