package com.destroystokyo.paper.event.player;

import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Fired when a player boosts elytra flight with a firework
 */
public class PlayerElytraBoostEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final ItemStack itemStack;
    private Firework firework;
    private boolean consume = true;

    public PlayerElytraBoostEvent(Player player, ItemStack itemStack, Firework firework) {
        super(player);
        this.itemStack = itemStack;
        this.firework = firework;
    }

    /**
     * Get the firework itemstack used
     *
     * @return ItemStack of firework
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get the firework entity that was spawned
     *
     * @return Firework entity
     */
    public Firework getFirework() {
        return firework;
    }

    /**
     * Get whether to consume the firework or not
     *
     * @return True to consume
     */
    public boolean shouldConsume() {
        return consume;
    }

    /**
     * Set whether to consume the firework or not
     *
     * @param consume True to consume
     */
    public void setShouldConsume(boolean consume) {
        this.consume = consume;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
