// 
// Decompiled by Procyon v0.5.30
// 

package org.bukkit.craftbukkit.inventory;

import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import ru.svarka.crafting.ICBRecipe;

import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeIterator implements Iterator<Recipe>
{
    private final Iterator<ICBRecipe> recipes;
    private final Iterator<ItemStack> smeltingCustom;
    private final Iterator<ItemStack> smeltingVanilla;
    private Iterator<?> removeFrom;
    
    public RecipeIterator() {
        this.removeFrom = null;
        List<ICBRecipe> list = new ArrayList<ICBRecipe>();
        for(IRecipe recipe : CraftingManager.getInstance().getRecipeList()) {
        	if(recipe instanceof ICBRecipe) {
        		list.add((ICBRecipe) recipe);
        	}
        }
        this.recipes = list.iterator();
        this.smeltingCustom = FurnaceRecipes.instance().customRecipes.keySet().iterator();
        this.smeltingVanilla = FurnaceRecipes.instance().smeltingList.keySet().iterator();
    }
    
    @Override
    public boolean hasNext() {
        return this.recipes.hasNext() || this.smeltingCustom.hasNext() || this.smeltingVanilla.hasNext();
    }
    
    @Override
    public Recipe next() {
        if (this.recipes.hasNext()) {
            this.removeFrom = this.recipes;
            return this.recipes.next().toBukkitRecipe();
        }
        ItemStack item;
        if (this.smeltingCustom.hasNext()) {
            this.removeFrom = this.smeltingCustom;
            item = this.smeltingCustom.next();
        }
        else {
            this.removeFrom = this.smeltingVanilla;
            item = this.smeltingVanilla.next();
        }
        final CraftItemStack stack = CraftItemStack.asCraftMirror(FurnaceRecipes.instance().getSmeltingResult(item));
        return new CraftFurnaceRecipe(stack, CraftItemStack.asCraftMirror(item));
    }
    
    @Override
    public void remove() {
        if (this.removeFrom == null) {
            throw new IllegalStateException();
        }
        this.removeFrom.remove();
    }
}
