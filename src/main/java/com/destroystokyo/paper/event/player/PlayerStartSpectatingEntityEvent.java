package com.destroystokyo.paper.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Triggered when a player starts spectating an entity in spectator mode.
 */
public class PlayerStartSpectatingEntityEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Entity currentSpectatorTarget;
    private final Entity newSpectatorTarget;

    public PlayerStartSpectatingEntityEvent(Player player, Entity currentSpectatorTarget, Entity newSpectatorTarget) {
        super(player);
        this.currentSpectatorTarget = currentSpectatorTarget;
        this.newSpectatorTarget = newSpectatorTarget;
    }

    /**
     * Gets the entity that the player is currently spectating or themselves if they weren't spectating anything
     *
     * @return The entity the player is currently spectating (before they start spectating the new target).
     */
    public Entity getCurrentSpectatorTarget() {
        return currentSpectatorTarget;
    }

    /**
     * Gets the new entity that the player will now be spectating
     *
     * @return The entity the player is now going to be spectating.
     */
    public Entity getNewSpectatorTarget() {
        return newSpectatorTarget;
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

