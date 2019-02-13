package com.destroystokyo.paper.loottable;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class LootableInventoryReplenishEvent extends PlayerEvent implements Cancellable {
    private final LootableInventory inventory;

    public LootableInventoryReplenishEvent(Player player, LootableInventory inventory) {
        super(player);
        this.inventory = inventory;
    }

    public LootableInventory getInventory() {
        return inventory;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
