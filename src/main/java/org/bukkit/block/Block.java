package org.bukkit.block;

import java.util.Collection;

import org.bukkit.Chunk;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.Metadatable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

/**
 * Represents a block. This is a live object, and only one Block may exist for
 * any given location in a world. The state of the block may change
 * concurrently to your own handling of it; use block.getState() to get a
 * snapshot state of a block which will not be modified.
 *
 * <br>
 * Note that parts of this class which require access to the world at large
 * (i.e. lighting and power) may not be able to be safely accessed during world
 * generation when used in cases like BlockPhysicsEvent!!!!
 */
public interface Block extends Metadatable {

    /**
     * Gets the metadata for this block
     *
     * @return block specific metadata
     * @deprecated Magic value
     */
    @Deprecated
    byte getData();

    /**
     * Gets the complete block data for this block
     *
     * @return block specific data
     */
    BlockData getBlockData();

    /**
     * Gets the block at the given offsets
     *
     * @param modX X-coordinate offset
     * @param modY Y-coordinate offset
     * @param modZ Z-coordinate offset
     * @return Block at the given offsets
     */
    Block getRelative(int modX, int modY, int modZ);

    /**
     * Gets the block at the given face
     * <p>
     * This method is equal to getRelative(face, 1)
     *
     * @param face Face of this block to return
     * @return Block at the given face
     * @see #getRelative(BlockFace, int)
     */
    Block getRelative(BlockFace face);

    /**
     * Gets the block at the given distance of the given face
     * <p>
     * For example, the following method places water at 100,102,100; two
     * blocks above 100,100,100.
     *
     * <pre>
     * Block block = world.getBlockAt(100, 100, 100);
     * Block shower = block.getRelative(BlockFace.UP, 2);
     * shower.setType(Material.WATER);
     * </pre>
     *
     * @param face Face of this block to return
     * @param distance Distance to get the block at
     * @return Block at the given face
     */
    Block getRelative(BlockFace face, int distance);

    /**
     * Gets the type of this block
     *
     * @return block type
     */
    Material getType();

    /**
     * Gets the light level between 0-15
     *
     * @return light level
     */
    byte getLightLevel();

    /**
     * Get the amount of light at this block from the sky.
     * <p>
     * Any light given from other sources (such as blocks like torches) will
     * be ignored.
     *
     * @return Sky light level
     */
    byte getLightFromSky();

    /**
     * Get the amount of light at this block from nearby blocks.
     * <p>
     * Any light given from other sources (such as the sun) will be ignored.
     *
     * @return Block light level
     */
    byte getLightFromBlocks();

    /**
     * Gets the world which contains this Block
     *
     * @return World containing this block
     */
    World getWorld();

    /**
     * Gets the x-coordinate of this block
     *
     * @return x-coordinate
     */
    int getX();

    /**
     * Gets the y-coordinate of this block
     *
     * @return y-coordinate
     */
    int getY();

    /**
     * Gets the z-coordinate of this block
     *
     * @return z-coordinate
     */
    int getZ();

    // Paper Start

    /**
     * Returns this block's coordinates packed into a long value
     * <p>
     * The return value can be computed as follows:
     * <br>
     * {@code long value = ((long)getX() & 0x7FFFFFF) | (((long)getZ() & 0x7FFFFFF) << 27) | ((long)getY() << 54);}
     * </p>
     *
     * <p>
     * And may be unpacked as follows:
     *<br>
     * {@code int x = (int) ((packed << 37) >> 37);}
     * <br>
     * {@code int y = (int) (packed >>> 54);}
     * <br>
     * {@code int z = (int) ((packed << 10) >> 37);}
     * </p>
     *
     * @return This block's x, y, and z coordinates packed into a long value
     */
    public default long getBlockKey() {
        return ((long)getX() & 0x7FFFFFF) | (((long)getZ() & 0x7FFFFFF) << 27) | ((long)getY() << 54);
    }
    // Paper End

    /**
     * Gets the Location of the block
     *
     * @return Location of block
     */
    Location getLocation();

    /**
     * Stores the location of the block in the provided Location object.
     * <p>
     * If the provided Location is null this method does nothing and returns
     * null.
     *
     * @param loc the location to copy into
     * @return The Location object provided or null
     */
    Location getLocation(Location loc);

    /**
     * Gets the chunk which contains this block
     *
     * @return Containing Chunk
     */
    Chunk getChunk();

    /**
     * Sets the complete data for this block
     *
     * @param data new block specific data
     */
    void setBlockData(BlockData data);

    /**
     * Sets the complete data for this block
     *
     * <br>
     * Note that applyPhysics = false is not in general safe. It should only be
     * used when you need to avoid triggering a physics update of neighboring
     * blocks, for example when creating a {@link Bisected} block. If you are
     * using a custom populator, then this parameter may also be required to
     * prevent triggering infinite chunk loads on border blocks. This method
     * should NOT be used to "hack" physics by placing blocks in impossible
     * locations. Such blocks are liable to be removed on various events such as
     * world upgrades. Furthermore setting large amounts of such blocks in close
     * proximity may overload the server physics engine if an update is
     * triggered at a later point. If this occurs, the resulting behavior is
     * undefined.
     *
     * @param data new block specific data
     * @param applyPhysics false to cancel physics from the changed block
     */
    void setBlockData(BlockData data, boolean applyPhysics);

    /**
     * Sets the type of this block
     *
     * @param type Material to change this block to
     */
    void setType(Material type);

    /**
     * Sets the type of this block
     *
     * <br>
     * Note that applyPhysics = false is not in general safe. It should only be
     * used when you need to avoid triggering a physics update of neighboring
     * blocks, for example when creating a {@link Bisected} block. If you are
     * using a custom populator, then this parameter may also be required to
     * prevent triggering infinite chunk loads on border blocks. This method
     * should NOT be used to "hack" physics by placing blocks in impossible
     * locations. Such blocks are liable to be removed on various events such as
     * world upgrades. Furthermore setting large amounts of such blocks in close
     * proximity may overload the server physics engine if an update is
     * triggered at a later point. If this occurs, the resulting behavior is
     * undefined.
     *
     * @param type Material to change this block to
     * @param applyPhysics False to cancel physics on the changed block.
     */
    void setType(Material type, boolean applyPhysics);

    /**
     * Gets the face relation of this block compared to the given block.
     * <p>
     * For example: 
     * <pre>{@code
     * Block current = world.getBlockAt(100, 100, 100);
     * Block target = world.getBlockAt(100, 101, 100);
     *
     * current.getFace(target) == BlockFace.Up;
     * }</pre>
     * <br>
     * If the given block is not connected to this block, null may be returned
     *
     * @param block Block to compare against this block
     * @return BlockFace of this block which has the requested block, or null
     */
    BlockFace getFace(Block block);

    /**
     * Captures the current state of this block. You may then cast that state
     * into any accepted type, such as Furnace or Sign.
     * <p>
     * The returned object will never be updated, and you are not guaranteed
     * that (for example) a sign is still a sign after you capture its state.
     *
     * @return BlockState with the current state of this block.
     */
    BlockState getState();

    // Paper start
    /**
     * @see #getState() optionally disables use of snapshot, to operate on real block data
     * @param useSnapshot if this block is a TE, should we create a fully copy of the TileEntity
     * @return BlockState with the current state of this block
     */
    BlockState getState(boolean useSnapshot);
    // Paper end

    /**
     * Returns the biome that this block resides in
     *
     * @return Biome type containing this block
     */
    Biome getBiome();

    /**
     * Sets the biome that this block resides in
     *
     * @param bio new Biome type for this block
     */
    void setBiome(Biome bio);

    /**
     * Returns true if the block is being powered by Redstone.
     *
     * @return True if the block is powered.
     */
    boolean isBlockPowered();

    /**
     * Returns true if the block is being indirectly powered by Redstone.
     *
     * @return True if the block is indirectly powered.
     */
    boolean isBlockIndirectlyPowered();

    /**
     * Returns true if the block face is being powered by Redstone.
     *
     * @param face The block face
     * @return True if the block face is powered.
     */
    boolean isBlockFacePowered(BlockFace face);

    /**
     * Returns true if the block face is being indirectly powered by Redstone.
     *
     * @param face The block face
     * @return True if the block face is indirectly powered.
     */
    boolean isBlockFaceIndirectlyPowered(BlockFace face);

    /**
     * Returns the redstone power being provided to this block face
     *
     * @param face the face of the block to query or BlockFace.SELF for the
     *     block itself
     * @return The power level.
     */
    int getBlockPower(BlockFace face);

    /**
     * Returns the redstone power being provided to this block
     *
     * @return The power level.
     */
    int getBlockPower();

    /**
     * Checks if this block is empty.
     * <p>
     * A block is considered empty when {@link #getType()} returns {@link
     * Material#AIR}.
     *
     * @return true if this block is empty
     */
    boolean isEmpty();

    /**
     * Checks if this block is liquid.
     * <p>
     * A block is considered liquid when {@link #getType()} returns {@link
     * Material#WATER} or {@link Material#LAVA}.
     *
     * @return true if this block is liquid
     */
    boolean isLiquid();

    /**
     * Gets the temperature of this block.
     * <p>
     * If the raw biome temperature without adjusting for height effects is
     * required then please use {@link World#getTemperature(int, int)}.
     *
     * @return Temperature of this block
     */
    double getTemperature();

    /**
     * Gets the humidity of the biome of this block
     *
     * @return Humidity of this block
     */
    double getHumidity();

    /**
     * Returns the reaction of the block when moved by a piston
     *
     * @return reaction
     */
    PistonMoveReaction getPistonMoveReaction();

    /**
     * Breaks the block and spawns items as if a player had digged it
     *
     * @return true if the block was destroyed
     */
    boolean breakNaturally();

    /**
     * Breaks the block and spawns items as if a player had digged it with a
     * specific tool
     *
     * @param tool The tool or item in hand used for digging
     * @return true if the block was destroyed
     */
    boolean breakNaturally(ItemStack tool);

    /**
     * Returns a list of items which would drop by destroying this block
     *
     * @return a list of dropped items for this type of block
     */
    Collection<ItemStack> getDrops();

    /**
     * Returns a list of items which would drop by destroying this block with
     * a specific tool
     *
     * @param tool The tool or item in hand used for digging
     * @return a list of dropped items for this type of block
     */
    Collection<ItemStack> getDrops(ItemStack tool);

    /**
     * Checks if this block is passable.
     * <p>
     * A block is passable if it has no colliding parts that would prevent
     * players from moving through it.
     * <p>
     * Examples: Tall grass, flowers, signs, etc. are passable, but open doors,
     * fence gates, trap doors, etc. are not because they still have parts that
     * can be collided with.
     *
     * @return <code>true</code> if passable
     */
    boolean isPassable();

    /**
     * Performs a ray trace that checks for collision with this specific block
     * in its current state using its precise collision shape.
     *
     * @param start the start location
     * @param direction the ray direction
     * @param maxDistance the maximum distance
     * @param fluidCollisionMode the fluid collision mode
     * @return the ray trace hit result, or <code>null</code> if there is no hit
     */
    RayTraceResult rayTrace(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode);

    /**
     * Gets the approximate bounding box for this block.
     * <p>
     * This isn't exact as some blocks {@link org.bukkit.block.data.type.Stairs}
     * contain many bounding boxes to establish their complete form.
     *
     * Also, the box may not be exactly the same as the collision shape (such as
     * cactus, which is 16/16 of a block with 15/16 collisional bounds).
     *
     * This method will return an empty bounding box if the geometric shape of
     * the block is empty (such as air blocks).
     *
     * @return the approximate bounding box of the block
     */
    BoundingBox getBoundingBox();
}
