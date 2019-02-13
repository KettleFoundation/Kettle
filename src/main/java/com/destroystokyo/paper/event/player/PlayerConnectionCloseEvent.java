package com.destroystokyo.paper.event.player;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.net.InetAddress;
import java.util.UUID;

/**
 * <p>
 * This event is invoked when a player has disconnected. It is guaranteed that,
 * if the server is in online-mode, that the provided uuid and username have been
 * validated.
 * </p>
 *
 * <p>
 * The event is invoked for players who have not yet logged into the world, whereas
 * {@link org.bukkit.event.player.PlayerQuitEvent} is only invoked on players who have logged into the world.
 * </p>
 *
 * <p>
 * The event is invoked for players who have already logged into the world,
 * although whether or not the player exists in the world at the time of
 * firing is undefined. (That is, whether the plugin can retrieve a Player object
 * using the event parameters is undefined). However, it is guaranteed that this
 * event is invoked AFTER {@link org.bukkit.event.player.PlayerQuitEvent}, if the player has already logged into the world.
 * </p>
 *
 * <p>
 * This event is guaranteed to never fire unless {@link org.bukkit.event.player.AsyncPlayerPreLoginEvent} has
 * been fired beforehand, and this event may not be called in parallel with
 * {@link org.bukkit.event.player.AsyncPlayerPreLoginEvent} for the same connection.
 * </p>
 *
 * <p>
 * Cancelling the {@link org.bukkit.event.player.AsyncPlayerPreLoginEvent} guarantees the corresponding
 * {@code PlayerConnectionCloseEvent} is never called.
 * </p>
 *
 * <p>
 * The event may be invoked asynchronously or synchronously. Plugins should check
 * {@link Event#isAsynchronous()} and handle accordingly.
 * </p>
 */
public class PlayerConnectionCloseEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID playerUniqueId;
    private final String playerName;
    private final InetAddress ipAddress;

    public PlayerConnectionCloseEvent(final UUID playerUniqueId, final String playerName, final InetAddress ipAddress, final boolean async) {
        super(async);
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.ipAddress = ipAddress;
    }

    /**
     * Returns the {@code UUID} of the player disconnecting.
     */
    public UUID getPlayerUniqueId() {
        return this.playerUniqueId;
    }

    /**
     * Returns the name of the player disconnecting.
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * Returns the player's IP address.
     */
    public InetAddress getIpAddress() {
        return this.ipAddress;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
