package org.bukkit.entity;

import org.bukkit.inventory.meta.FireworkMeta;

import java.util.UUID;

public interface Firework extends Entity {

    /**
     * Get a copy of the fireworks meta
     *
     * @return A copy of the current Firework meta
     */
    FireworkMeta getFireworkMeta();

    /**
     * Apply the provided meta to the fireworks
     *
     * @param meta The FireworkMeta to apply
     */
    void setFireworkMeta(FireworkMeta meta);

    /**
     * Cause this firework to explode at earliest opportunity, as if it has no
     * remaining fuse.
     */
    void detonate();

    // Paper start
    public UUID getSpawningEntity();
    /**
     * If this firework is boosting an entity, return it
     * @return The entity being boosted
     */
    public LivingEntity getBoostedEntity();
    // Paper end
}
