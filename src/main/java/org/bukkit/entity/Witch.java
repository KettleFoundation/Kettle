package org.bukkit.entity;

import com.destroystokyo.paper.entity.RangedEntity;

// Paper start
import org.bukkit.inventory.ItemStack;
import javax.annotation.Nullable;
// Paper end

/**
 * Represents a Witch
 */
// Paper start
public interface Witch extends Monster, RangedEntity {
    /**
     * Check if Witch is drinking a potion
     *
     * @return True if drinking a potion
     */
    boolean isDrinkingPotion();

    /**
     * Get time remaining (in ticks) the Witch is drinking a potion
     *
     * @return Time remaining (in ticks)
     */
    int getPotionUseTimeLeft();

    /**
     * Get the potion the Witch is drinking
     *
     * @return The potion the witch is drinking
     */
    ItemStack getDrinkingPotion();

    /**
     * Set the potion the Witch should drink
     *
     * @param potion Potion to drink
     */
    void setDrinkingPotion(@Nullable ItemStack potion);
    // Paper end
}
