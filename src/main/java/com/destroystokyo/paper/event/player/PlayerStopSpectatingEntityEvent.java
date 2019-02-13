package com.destroystokyo.paper.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Triggered when a player stops spectating an entity in spectator mode.
 */
public class PlayerStopSpectatingEntityEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Entity spectatorTarget;

    public PlayerStopSpectatingEntityEvent(Player player, Entity spectatorTarget) {
        super(player);
        this.spectatorTarget = spectatorTarget;
    }

    /**
     * Gets the entity that the player is spectating
     *
     * @return The entity the player is currently spectating (before they will stop).
     */
    public Entity getSpectatorTarget() {
        return spectatorTarget;
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
