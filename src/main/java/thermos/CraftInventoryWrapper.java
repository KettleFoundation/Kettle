package thermos;

import java.util.Collections;
import java.util.List;

import net.minecraft.util.text.ITextComponent;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class CraftInventoryWrapper extends CraftInventory {
    public CraftInventoryWrapper(IInventory inventory) {
        super(new Inv(inventory));
        ((Inv) super.inventory).wrapper = this;
    }

    private static final class Inv implements IInventory, InventoryHolder {
        CraftInventoryWrapper wrapper;
        IInventory inventory;

        Inv(IInventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public Inventory getInventory() {
            return wrapper;
        }

        @Override
        public int getSizeInventory() {
            return inventory.getSizeInventory();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ItemStack getStackInSlot(int p_70301_1_) {
            return inventory.getStackInSlot(p_70301_1_);
        }

        @Override
        public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
            return inventory.decrStackSize(p_70298_1_, p_70298_2_);
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            return null;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
            return inventory.getStackInSlotOnClosing(p_70304_1_);
        }

        @Override
        public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
            inventory.setInventorySlotContents(p_70299_1_, p_70299_2_);
        }

        @Override
        public String getInventoryName() {
            return inventory.getInventoryName();
        }

        @Override
        public boolean hasCustomInventoryName() {
            return inventory.hasCustomInventoryName();
        }

        @Override
        public int getInventoryStackLimit() {
            return inventory.getInventoryStackLimit();
        }

        @Override
        public void markDirty() {
            inventory.markDirty();
        }

        @Override
        public boolean isUsableByPlayer(EntityPlayer player) {
            return false;
        }

        @Override
        public void openInventory(EntityPlayer player) {

        }

        @Override
        public void closeInventory(EntityPlayer player) {

        }

        @Override
        public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
            return inventory.isUseableByPlayer(p_70300_1_);
        }

        @Override
        public void openInventory() {
            inventory.openInventory();
        }

        @Override
        public void closeInventory() {
            inventory.closeInventory();

        }

        @Override
        public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
            return inventory.isItemValidForSlot(p_94041_1_, p_94041_2_);
        }

        @Override
        public int getField(int id) {
            return 0;
        }

        @Override
        public void setField(int id, int value) {

        }

        @Override
        public int getFieldCount() {
            return 0;
        }

        @Override
        public void clear() {

        }

        @Override
        public ItemStack[] getContents() {
            return inventory.getContents();
        }

        @Override
        public void onOpen(CraftHumanEntity who) {
            try {
                inventory.onOpen(who);
            } catch (AbstractMethodError ignored) {
            }
        }

        @Override
        public void onClose(CraftHumanEntity who) {
            try {
                inventory.onClose(who);
            } catch (AbstractMethodError ignored) {
            }
        }

        @Override
        public List<HumanEntity> getViewers() {
            try {
                return inventory.getViewers();
            } catch (AbstractMethodError ignored) {
                return Collections.emptyList();
            }
        }

        @Override
        public InventoryHolder getOwner() {
            return this;
        }

        @Override
        public void setMaxStackSize(int size) {
            try {
                inventory.setMaxStackSize(size);
            } catch (AbstractMethodError ignored) {
            }
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean hasCustomName() {
            return false;
        }

        @Override
        public ITextComponent getDisplayName() {
            return null;
        }
    }
}

