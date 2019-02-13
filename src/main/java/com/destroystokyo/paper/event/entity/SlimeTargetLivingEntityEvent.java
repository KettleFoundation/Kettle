package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.event.Cancellable;

/**
 * Fired when a Slime decides to change direction to target a LivingEntity.
 * <p>
 * This event does not fire for the entity's actual movement. Only when it
 * is choosing to start moving.
 */
public class SlimeTargetLivingEntityEvent extends SlimePathfindEvent implements Cancellable {
    private final LivingEntity target;

    public SlimeTargetLivingEntityEvent(Slime slime, LivingEntity target) {
        super(slime);
        this.target = target;
    }

    /**
     * Get the targeted entity
     *
     * @return Targeted entity
     */
    public LivingEntity getTarget() {
        return target;
    }
}
