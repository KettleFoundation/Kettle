package org.bukkit.block;

import com.destroystokyo.paper.loottable.LootableBlockInventory;
import org.bukkit.DyeColor;
import org.bukkit.Nameable;
import org.bukkit.loot.Lootable;

/**
 * Represents a captured state of a ShulkerBox.
 */
public interface ShulkerBox extends Container, Nameable, LootableBlockInventory { // Paper

    /**
     * Get the {@link DyeColor} corresponding to this ShulkerBox
     *
     * @return the {@link DyeColor} of this ShulkerBox
     */
    public DyeColor getColor();
}
