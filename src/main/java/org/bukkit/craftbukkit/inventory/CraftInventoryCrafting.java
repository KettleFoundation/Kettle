// 
// Decompiled by Procyon v0.5.30
// 

package org.bukkit.craftbukkit.inventory;

import net.minecraft.item.crafting.IRecipe;
import ru.svarka.crafting.ICBRecipe;
import ru.svarka.inventory.ICBInventory;

import org.bukkit.inventory.Recipe;
import java.util.Arrays;
import org.bukkit.inventory.ItemStack;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.IInventory;
import org.bukkit.inventory.CraftingInventory;

public class CraftInventoryCrafting extends CraftInventory implements CraftingInventory
{
    private final ICBInventory resultInventory;
    
    public CraftInventoryCrafting(final InventoryCrafting inventory, final ICBInventory resultInventory) {
        super(inventory);
        this.resultInventory = resultInventory;
    }
    
    public ICBInventory getResultInventory() {
        return this.resultInventory;
    }
    
    public ICBInventory getMatrixInventory() {
        return this.inventory;
    }
    
    @Override
    public int getSize() {
        return this.getResultInventory().getSizeInventory() + this.getMatrixInventory().getSizeInventory();
    }
    
    @Override
    public void setContents(final ItemStack[] items) {
        final int resultLen = this.getResultInventory().getContents().length;
        final int len = this.getMatrixInventory().getContents().length + resultLen;
        if (len > items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + len + " or less");
        }
        this.setContents(items[0], Arrays.copyOfRange(items, 1, items.length));
    }
    
    @Override
    public ItemStack[] getContents() {
        final ItemStack[] items = new ItemStack[this.getSize()];
        net.minecraft.item.ItemStack[] mcResultItems;
        int i;
        for (mcResultItems = this.getResultInventory().getContents(), i = 0, i = 0; i < mcResultItems.length; ++i) {
            items[i] = CraftItemStack.asCraftMirror(mcResultItems[i]);
        }
        final net.minecraft.item.ItemStack[] mcItems = this.getMatrixInventory().getContents();
        for (int j = 0; j < mcItems.length; ++j) {
            items[i + j] = CraftItemStack.asCraftMirror(mcItems[j]);
        }
        return items;
    }
    
    public void setContents(final ItemStack result, final ItemStack[] contents) {
        this.setResult(result);
        this.setMatrix(contents);
    }
    
    @Override
    public CraftItemStack getItem(final int index) {
        if (index < this.getResultInventory().getSizeInventory()) {
            final net.minecraft.item.ItemStack item = this.getResultInventory().getStackInSlot(index);
            return (item == null) ? null : CraftItemStack.asCraftMirror(item);
        }
        final net.minecraft.item.ItemStack item = this.getMatrixInventory().getStackInSlot(index - this.getResultInventory().getSizeInventory());
        return (item == null) ? null : CraftItemStack.asCraftMirror(item);
    }
    
    @Override
    public void setItem(final int index, final ItemStack item) {
        if (index < this.getResultInventory().getSizeInventory()) {
            this.getResultInventory().setInventorySlotContents(index, (item == null) ? null : CraftItemStack.asNMSCopy(item));
        }
        else {
            this.getMatrixInventory().setInventorySlotContents(index - this.getResultInventory().getSizeInventory(), (item == null) ? null : CraftItemStack.asNMSCopy(item));
        }
    }
    
    @Override
    public ItemStack[] getMatrix() {
        final net.minecraft.item.ItemStack[] matrix = this.getMatrixInventory().getContents();
        final ItemStack[] items = new ItemStack[matrix.length];
        for (int i = 0; i < matrix.length; ++i) {
            items[i] = CraftItemStack.asCraftMirror(matrix[i]);
        }
        return items;
    }
    
    @Override
    public ItemStack getResult() {
        final net.minecraft.item.ItemStack item = this.getResultInventory().getStackInSlot(0);
        if (item != null) {
            return CraftItemStack.asCraftMirror(item);
        }
        return null;
    }
    
    @Override
    public void setMatrix(final ItemStack[] contents) {
        if (this.getMatrixInventory().getContents().length > contents.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + this.getMatrixInventory().getContents().length + " or less");
        }
        final net.minecraft.item.ItemStack[] mcItems = this.getMatrixInventory().getContents();
        for (int i = 0; i < mcItems.length; ++i) {
            if (i < contents.length) {
                final ItemStack item = contents[i];
                if (item == null || item.getTypeId() <= 0) {
                    this.getMatrixInventory().setInventorySlotContents(i, null);
                }
                else {
                    this.getMatrixInventory().setInventorySlotContents(i, CraftItemStack.asNMSCopy(item));
                }
            }
            else {
                this.getMatrixInventory().setInventorySlotContents(i, null);
            }
        }
    }
    
    @Override
    public void setResult(final ItemStack item) {
        final net.minecraft.item.ItemStack[] contents = this.getResultInventory().getContents();
        if (item == null || item.getTypeId() <= 0) {
            contents[0] = null;
        }
        else {
            contents[0] = CraftItemStack.asNMSCopy(item);
        }
    }
    
    @Override
    public Recipe getRecipe() {
        final IRecipe recipe = ((InventoryCrafting)this.getInventory()).currentRecipe;
        if(recipe instanceof ICBRecipe) {
        	return (recipe == null) ? null : ((ICBRecipe)recipe).toBukkitRecipe();
        }
        return null;
    }
}
