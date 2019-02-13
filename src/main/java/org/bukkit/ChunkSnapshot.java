package org.bukkit;

import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;

/**
 * Represents a static, thread-safe snapshot of chunk of blocks.
 * <p>
 * Purpose is to allow clean, efficient copy of a chunk data to be made, and
 * then handed off for processing in another thread (e.g. map rendering)
 */
public interface ChunkSnapshot {

    /**
     * Gets the X-coordinate of this chunk
     *
     * @return X-coordinate
     */
    int getX();

    /**
     * Gets the Z-coordinate of this chunk
     *
     * @return Z-coordinate
     */
    int getZ();

    /**
     * Gets name of the world containing this chunk
     *
     * @return Parent World Name
     */
    String getWorldName();

    /**
     * Get block type for block at corresponding coordinate in the chunk
     *
     * @param x 0-15
     * @param y 0-255
     * @param z 0-15
     * @return block material type
     */
    Material getBlockType(int x, int y, int z);

    /**
     * Get block data for block at corresponding coordinate in the chunk
     *
     * @param x 0-15
     * @param y 0-255
     * @param z 0-15
     * @return block material type
     */
    BlockData getBlockData(int x, int y, int z);

    /**
     * Get block data for block at corresponding coordinate in the chunk
     *
     * @param x 0-15
     * @param y 0-255
     * @param z 0-15
     * @return 0-15
     * @deprecated Magic value
     */
    @Deprecated
    int getData(int x, int y, int z);

    /**
     * Get sky light level for block at corresponding coordinate in the chunk
     *
     * @param x 0-15
     * @param y 0-255
     * @param z 0-15
     * @return 0-15
     */
    int getBlockSkyLight(int x, int y, int z);

    /**
     * Get light level emitted by block at corresponding coordinate in the
     * chunk
     *
     * @param x 0-15
     * @param y 0-255
     * @param z 0-15
     * @return 0-15
     */
    int getBlockEmittedLight(int x, int y, int z);

    /**
     * Gets the highest non-air coordinate at the given coordinates
     *
     * @param x X-coordinate of the blocks (0-15)
     * @param z Z-coordinate of the blocks (0-15)
     * @return Y-coordinate of the highest non-air block
     */
    int getHighestBlockYAt(int x, int z);

    /**
     * Get biome at given coordinates
     *
     * @param x X-coordinate (0-15)
     * @param z Z-coordinate (0-15)
     * @return Biome at given coordinate
     */
    Biome getBiome(int x, int z);

    /**
     * Get raw biome temperature (0.0-1.0) at given coordinate
     *
     * @param x X-coordinate (0-15)
     * @param z Z-coordinate (0-15)
     * @return temperature at given coordinate
     */
    double getRawBiomeTemperature(int x, int z);

    /**
     * Get world full time when chunk snapshot was captured
     *
     * @return time in ticks
     */
    long getCaptureFullTime();

    /**
     * Test if section is empty
     *
     * @param sy - section Y coordinate (block Y / 16, 0-255)
     * @return true if empty, false if not
     */
    boolean isSectionEmpty(int sy);
}
