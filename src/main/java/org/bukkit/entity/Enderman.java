package org.bukkit.entity;

import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;

/**
 * Represents an Enderman.
 */
public interface Enderman extends Monster {

    // Paper start
    /**
     * Try to teleport the enderman to a random nearby location.
     *
     * May conditionally fail if the random location was not valid
     * @return If the enderman teleported successfully or not
     */

    public boolean teleportRandomly();
    // Paper end

    /**
     * Gets the id and data of the block that the Enderman is carrying.
     *
     * @return MaterialData containing the id and data of the block
     */
    public MaterialData getCarriedMaterial();

    /**
     * Sets the id and data of the block that the Enderman is carrying.
     *
     * @param material data to set the carried block to
     */
    public void setCarriedMaterial(MaterialData material);

    /**
     * Gets the data of the block that the Enderman is carrying.
     *
     * @return BlockData containing the carried block, or null if none
     */
    public BlockData getCarriedBlock();

    /**
     * Sets the data of the block that the Enderman is carrying.
     *
     * @param blockData data to set the carried block to, or null to remove
     */
    public void setCarriedBlock(BlockData blockData);
}
