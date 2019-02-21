package org.bukkit.craftbukkit.inventory;

import net.minecraft.item.crafting.IRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * Bukkit API wrapper for non-vanilla IRecipe classes
 */
public class CraftCustomModRecipe implements Recipe {
    private IRecipe iRecipe;

    public CraftCustomModRecipe(IRecipe iRecipe) {
        this.iRecipe = iRecipe;
    }

    @Override
    public ItemStack getResult() {
        return CraftItemStack.asCraftMirror(iRecipe.getRecipeOutput());
    }

    public IRecipe getHandle() {
        return iRecipe;
    }
}
