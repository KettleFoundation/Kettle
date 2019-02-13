package com.destroystokyo.paper.loottable;

import org.bukkit.block.Block;

/**
 * Represents an Inventory that can generate loot, such as Chests inside of Fortresses and Mineshafts
 */
public interface LootableBlockInventory extends LootableInventory {

    /**
     * Gets the block that is lootable
     * @return The Block
     */
    Block getBlock();
}
