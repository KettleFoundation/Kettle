package net.minecraft.inventory;

import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerHorseInventory extends Container
{
    private final IInventory horseInventory;
    private final AbstractHorse horse;

    public ContainerHorseInventory(IInventory playerInventory, IInventory horseInventoryIn, final AbstractHorse horse, EntityPlayer player)
    {
        this.horseInventory = horseInventoryIn;
        this.horse = horse;
        int i = 3;
        horseInventoryIn.openInventory(player);
        int j = -18;
        this.addSlotToContainer(new Slot(horseInventoryIn, 0, 8, 18)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() == Items.SADDLE && !this.getHasStack() && horse.canBeSaddled();
            }
            public boolean isEnabled()
            {
                return horse.canBeSaddled();
            }
        });
        this.addSlotToContainer(new Slot(horseInventoryIn, 1, 8, 36)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return horse.isArmor(stack);
            }
            public boolean isEnabled()
            {
                return horse.wearsArmor();
            }
            public int getSlotStackLimit()
            {
                return 1;
            }
        });

        if (horse instanceof AbstractChestHorse && ((AbstractChestHorse)horse).hasChest())
        {
            for (int k = 0; k < 3; ++k)
            {
                for (int l = 0; l < ((AbstractChestHorse)horse).getInventoryColumns(); ++l)
                {
                    this.addSlotToContainer(new Slot(horseInventoryIn, 2 + l + k * ((AbstractChestHorse)horse).getInventoryColumns(), 80 + l * 18, 18 + k * 18));
                }
            }
        }

        for (int i1 = 0; i1 < 3; ++i1)
        {
            for (int k1 = 0; k1 < 9; ++k1)
            {
                this.addSlotToContainer(new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
            }
        }

        for (int j1 = 0; j1 < 9; ++j1)
        {
            this.addSlotToContainer(new Slot(playerInventory, j1, 8 + j1 * 18, 142));
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.horseInventory.isUsableByPlayer(playerIn) && this.horse.isEntityAlive() && this.horse.getDistance(playerIn) < 8.0F;
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.horseInventory.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, this.horseInventory.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(1).isItemValid(itemstack1) && !this.getSlot(1).getHasStack())
            {
                if (!this.mergeItemStack(itemstack1, 1, 2, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(0).isItemValid(itemstack1))
            {
                if (!this.mergeItemStack(itemstack1, 0, 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.horseInventory.getSizeInventory() <= 2 || !this.mergeItemStack(itemstack1, 2, this.horseInventory.getSizeInventory(), false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.horseInventory.closeInventory(playerIn);
    }
}
