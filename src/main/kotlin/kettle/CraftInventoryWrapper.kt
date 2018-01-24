package kettle

import java.util.Collections

import org.bukkit.craftbukkit.entity.CraftHumanEntity
import org.bukkit.craftbukkit.inventory.CraftInventory
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

class CraftInventoryWrapper(inventory: IInventory) : CraftInventory(Inv(inventory)) {
    init {
        (super.inventory as Inv).wrapper = this
    }

    private class Inv internal constructor(internal var inventory: IInventory) : IInventory, InventoryHolder {
        internal var wrapper: CraftInventoryWrapper? = null

        val inventoryName: String
            get() = inventory.getInventoryName()

        val contents: Array<ItemStack>
            get() = inventory.getContents()

        val viewers: List<HumanEntity>
            get() {
                try {
                    return inventory.getViewers()
                } catch (ignored: AbstractMethodError) {
                    return emptyList()
                }

            }

        val owner: InventoryHolder
            get() = this

        override fun getInventory(): Inventory? {
            return wrapper
        }

        override fun getSizeInventory(): Int {
            return inventory.sizeInventory
        }

        override fun getStackInSlot(p_70301_1_: Int): ItemStack {
            return inventory.getStackInSlot(p_70301_1_)
        }

        override fun decrStackSize(p_70298_1_: Int, p_70298_2_: Int): ItemStack {
            return inventory.decrStackSize(p_70298_1_, p_70298_2_)
        }

        fun getStackInSlotOnClosing(p_70304_1_: Int): ItemStack {
            return inventory.getStackInSlotOnClosing(p_70304_1_)
        }

        override fun setInventorySlotContents(p_70299_1_: Int, p_70299_2_: ItemStack) {
            inventory.setInventorySlotContents(p_70299_1_, p_70299_2_)
        }

        fun hasCustomInventoryName(): Boolean {
            return inventory.hasCustomInventoryName()
        }

        override fun getInventoryStackLimit(): Int {
            return inventory.inventoryStackLimit
        }

        override fun markDirty() {
            inventory.markDirty()
        }

        fun isUseableByPlayer(p_70300_1_: EntityPlayer): Boolean {
            return inventory.isUseableByPlayer(p_70300_1_)
        }

        fun openInventory() {
            inventory.openInventory()
        }

        fun closeInventory() {
            inventory.closeInventory()

        }

        override fun isItemValidForSlot(p_94041_1_: Int, p_94041_2_: ItemStack): Boolean {
            return inventory.isItemValidForSlot(p_94041_1_, p_94041_2_)
        }

        fun onOpen(who: CraftHumanEntity) {
            try {
                inventory.onOpen(who)
            } catch (ignored: AbstractMethodError) {
            }

        }

        fun onClose(who: CraftHumanEntity) {
            try {
                inventory.onClose(who)
            } catch (ignored: AbstractMethodError) {
            }

        }

        fun setMaxStackSize(size: Int) {
            try {
                inventory.setMaxStackSize(size)
            } catch (ignored: AbstractMethodError) {
            }

        }
    }
}

