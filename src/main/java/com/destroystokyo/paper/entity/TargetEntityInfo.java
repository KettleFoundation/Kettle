package com.destroystokyo.paper.entity;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Represents information about a targeted entity
 */
public class TargetEntityInfo {
    private final Entity entity;
    private final Vector hitVec;

    public TargetEntityInfo(Entity entity, Vector hitVec) {
        this.entity = entity;
        this.hitVec = hitVec;
    }

    /**
     * Get the entity that is targeted
     *
     * @return Targeted entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Get the position the entity is targeted at
     *
     * @return Targeted position
     */
    public Vector getHitVector() {
        return hitVec;
    }
}
