package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Fired when an EnderDragon shoots a fireball
 */
public class EnderDragonShootFireballEvent extends EntityEvent implements Cancellable {
    private final DragonFireball fireball;

    public EnderDragonShootFireballEvent(EnderDragon entity, DragonFireball fireball) {
        super(entity);
        this.fireball = fireball;
    }

    /**
     * The enderdragon shooting the fireball
     */
    @Override
    public EnderDragon getEntity() {
        return (EnderDragon) super.getEntity();
    }

    /**
     * @return The fireball being shot
     */
    public DragonFireball getFireball() {
        return fireball;
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
