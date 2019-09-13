package org.bukkit.block;

import net.minecraft.inventory.IInventory;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * CraftCustomContainer
 *
 * @author Hexeption admin@hexeption.co.uk - Cauldron
 * @since 30/06/2019 - 01:56 PM
 */
public class CraftCustomContainer extends CraftBlockState implements InventoryHolder {

    private final CraftWorld world;
    private final IInventory container;

    public CraftCustomContainer(Block block) {
        super(block);
        world = (CraftWorld) block.getWorld();
        container = (IInventory) world.getTileEntityAt(getX(), getY(), getZ());
    }

    @Override
    public Inventory getInventory() {
        CraftInventory inventory = new CraftInventory(container);
        return inventory;
    }
}
