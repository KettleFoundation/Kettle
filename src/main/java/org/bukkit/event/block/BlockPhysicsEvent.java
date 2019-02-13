package org.bukkit.event.block;

import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Thrown when a block physics check is called.
 * <br>
 * This event is a high frequency event, it may be called thousands of times per
 * a second on a busy server. Plugins are advised to listen to the event with
 * caution and only perform lightweight checks when using it.
 * <br>
 * In addition to this, cancelling the event is liable to leave the world in an
 * inconsistent state. For example if you use the event to leave a block
 * floating in mid air when that block has a requirement to be attached to
 * something, there is no guarantee that the floating block will persist across
 * server restarts or map upgrades.
 * <br>
 * Plugins should also note that where possible this event may only called for
 * the "root" block of physics updates in order to limit event spam. Physics
 * updates that cause other blocks to change their state may not result in an
 * event for each of those blocks (usually adjacent). If you are concerned about
 * monitoring these changes then you should check adjacent blocks yourself.
 */
public class BlockPhysicsEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final BlockData changed;
    private boolean cancel = false;
    // Paper start - add source block
    private int sourceX;
    private int sourceY;
    private int sourceZ;
    private Block sourceBlock;

    public BlockPhysicsEvent(final Block block, final BlockData changed, final int sourceX, final int sourceY, final int sourceZ) {
        this(block, changed);
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.sourceZ = sourceZ;
        this.sourceBlock = null;
    }

    /**
     * Gets the source block, causing this event
     *
     * @return Source block
     */
    public Block getSourceBlock() {
        return sourceBlock == null ? (sourceBlock = block.getWorld().getBlockAt(sourceX, sourceY, sourceZ)) : sourceBlock;
    }
    // Paper end

    public BlockPhysicsEvent(final Block block, final BlockData changed) {
        super(block);
        this.changed = changed;
        this.sourceBlock = block; // Paper - add source block
    }

    /**
     * Gets the type of block that changed, causing this event
     *
     * @return Changed block's type
     */
    public Material getChangedType() {
        return changed.getMaterial();
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
