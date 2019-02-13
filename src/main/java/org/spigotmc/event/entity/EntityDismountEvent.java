package org.spigotmc.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Called when an entity stops riding another entity.
 *
 */
public class EntityDismountEvent extends EntityEvent implements Cancellable
{

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Entity dismounted;
    private final boolean isCancellable; // Paper

    public EntityDismountEvent(Entity what, Entity dismounted)
    {
        // Paper start
        this(what, dismounted, true);
    }

    public EntityDismountEvent(Entity what, Entity dismounted, boolean isCancellable)
    {
        // Paper end
        super( what );
        this.dismounted = dismounted;
        this.isCancellable = isCancellable; // Paper
    }

    public Entity getDismounted()
    {
        return dismounted;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        // Paper start
        if (cancel && !isCancellable) {
            return;
        }
        this.cancelled = cancel;
    }

    public boolean isCancellable() {
        return isCancellable;
        // Paper end
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
