package org.bukkit.entity;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

/**
 * Represents a falling block
 */
public interface FallingBlock extends Entity {

    /**
     * Get the Material of the falling block
     *
     * @return Material of the block
     * @deprecated use {@link #getBlockData()}
     */
    @Deprecated
    Material getMaterial();

    /**
     * Get the data for the falling block
     *
     * @return data of the block
     */
    BlockData getBlockData();

    /**
     * Get if the falling block will break into an item if it cannot be placed
     *
     * @return true if the block will break into an item when obstructed
     */
    boolean getDropItem();

    /**
     * Set if the falling block will break into an item if it cannot be placed
     *
     * @param drop true to break into an item when obstructed
     */
    void setDropItem(boolean drop);

    /**
     * Get the HurtEntities state of this block.
     *
     * @return whether entities will be damaged by this block.
     */
    boolean canHurtEntities();

    /**
     * Set the HurtEntities state of this block.
     *
     * @param hurtEntities whether entities will be damaged by this block.
     */
    void setHurtEntities(boolean hurtEntities);

    /**
     * Gets the source block location of the FallingBlock
     *
     * @return the source block location the FallingBlock was spawned from
     * @deprecated replaced by {@link Entity#getOrigin()}
     */
    @Deprecated
    default org.bukkit.Location getSourceLoc() {
        return this.getOrigin();
    }
}
