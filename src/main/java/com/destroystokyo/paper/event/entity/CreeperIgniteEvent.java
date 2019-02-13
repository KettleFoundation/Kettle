package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.Creeper;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Called when a Creeper is ignite flag is changed (armed/disarmed to explode).
 */
public class CreeperIgniteEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean canceled;
    private boolean ignited;

    public CreeperIgniteEvent(Creeper creeper, boolean ignited) {
        super(creeper);
        this.ignited = ignited;
    }

    @Override
    public Creeper getEntity() {
        return (Creeper) entity;
    }

    public boolean isIgnited() {
        return ignited;
    }

    public void setIgnited(boolean ignited) {
        this.ignited = ignited;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
