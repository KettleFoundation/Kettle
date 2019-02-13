package com.destroystokyo.paper.loottable;

import org.bukkit.entity.Entity;

/**
 * Represents an Inventory that can generate loot, such as Minecarts inside of Mineshafts
 */
public interface LootableEntityInventory extends LootableInventory {

    /**
     * Gets the entity that is lootable
     * @return The Entity
     */
    Entity getEntity();
}
