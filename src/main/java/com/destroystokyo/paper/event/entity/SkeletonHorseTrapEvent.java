package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.SkeletonHorse;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Event called when a player gets close to a skeleton horse and triggers the lightning trap
 */
public class SkeletonHorseTrapEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public SkeletonHorseTrapEvent(SkeletonHorse horse) {
        super(horse);
    }

    @Override
    public SkeletonHorse getEntity() {
        return (SkeletonHorse) super.getEntity();
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

