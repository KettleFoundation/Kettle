package com.destroystokyo.paper.event.entity;

import org.bukkit.Location;
import org.bukkit.entity.Turtle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Fired when a Turtle starts digging to lay eggs
 */
public class TurtleStartDiggingEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final Location location;

    public TurtleStartDiggingEvent(Turtle turtle, Location location) {
        super(turtle);
        this.location = location;
    }

    /**
     * The turtle digging
     *
     * @return The turtle
     */
    public Turtle getEntity() {
        return (Turtle) entity;
    }

    /**
     * Get the location where the turtle is digging
     *
     * @return Location where digging
     */
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
