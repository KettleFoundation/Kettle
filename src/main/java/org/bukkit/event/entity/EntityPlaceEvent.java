package org.bukkit.event.entity;

import org.bukkit.Warning;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Triggered when a entity is created in the world by a player "placing" an item
 * on a block.
 * <br>
 * Note that this event is currently only fired for three specific placements:
 * armor stands, minecarts, and end crystals.
 *
 * @deprecated draft API
 */
@Deprecated
@Warning(false)
public class EntityPlaceEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Player player;
    private final Block block;
    private final BlockFace blockFace;

    public EntityPlaceEvent(final Entity entity, final Player player, final Block block, final BlockFace blockFace) {
        super(entity);
        this.player = player;
        this.block = block;
        this.blockFace = blockFace;
    }

    /**
     * Returns the player placing the entity
     *
     * @return the player placing the entity
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the block that the entity was placed on
     *
     * @return the block that the entity was placed on
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Returns the face of the block that the entity was placed on
     *
     * @return the face of the block that the entity was placed on
     */
    public BlockFace getBlockFace() {
        return blockFace;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
