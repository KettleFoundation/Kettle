package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

import java.util.Collection;

/**
 * Fired when a DragonFireball collides with a block/entity and spawns an AreaEffectCloud
 */
public class EnderDragonFireballHitEvent extends EntityEvent implements Cancellable {
    private final Collection<LivingEntity> targets;
    private final AreaEffectCloud areaEffectCloud;

    public EnderDragonFireballHitEvent(DragonFireball fireball, Collection<LivingEntity> targets, AreaEffectCloud areaEffectCloud) {
        super(fireball);
        this.targets = targets;
        this.areaEffectCloud = areaEffectCloud;
    }

    /**
     * The fireball involved in this event
     */
    @Override
    public DragonFireball getEntity() {
        return (DragonFireball) super.getEntity();
    }

    /**
     * The living entities hit by fireball
     *
     * May be null if no entities were hit
     *
     * @return the targets
     */
    public Collection<LivingEntity> getTargets() {
        return targets;
    }

    /**
     * @return The area effect cloud spawned in this collision
     */
    public AreaEffectCloud getAreaEffectCloud() {
        return areaEffectCloud;
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
