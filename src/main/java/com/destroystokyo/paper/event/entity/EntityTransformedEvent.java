package com.destroystokyo.paper.event.entity;


import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityTransformEvent;

/**
 * Fired when an entity transforms into another entity
 * <p>
 * If the event is cancelled, the entity will not transform
 * @deprecated Bukkit has added {@link EntityTransformEvent}, you should start using that
 */
@Deprecated
public class EntityTransformedEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Entity transformed;
    private final TransformedReason reason;

    public EntityTransformedEvent(Entity entity, Entity transformed, TransformedReason reason) {
        super(entity);
        this.transformed = transformed;
        this.reason = reason;
    }

    /**
     * The entity after it has transformed
     *
     * @return Transformed entity
     * @deprecated see {@link EntityTransformEvent#getTransformedEntity()}
     */
    @Deprecated
    public Entity getTransformed() {
        return transformed;
    }

    /**
     * @return The reason for the transformation
     * @deprecated see {@link EntityTransformEvent#getTransformReason()}
     */
    @Deprecated
    public TransformedReason getReason() {
        return reason;
    }


    @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public boolean isCancelled(){
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel){
        cancelled = cancel;
    }

    public enum TransformedReason {
        /**
         * When a zombie drowns
         */
        DROWNED,
        /**
         * When a zombie villager is cured
         */
        CURED,
        /**
         * When a villager turns to a zombie villager
         */
        INFECTED,
        /**
         * When a mooshroom turns to a cow
         */
        SHEARED,
        /**
         * When a pig turns to a zombiepigman
         */
        LIGHTNING

    }
}
