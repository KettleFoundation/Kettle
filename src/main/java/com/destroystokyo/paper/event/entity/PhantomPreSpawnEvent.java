package com.destroystokyo.paper.event.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

import javax.annotation.Nullable;

/**
 * Called when a phantom is spawned for an exhausted player
 */
public class PhantomPreSpawnEvent extends PreCreatureSpawnEvent {
    private final Entity entity;

    public PhantomPreSpawnEvent(Location location, Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        super(location, EntityType.PHANTOM, reason);
        this.entity = entity;
    }

    /**
     * Get the entity this phantom is spawning for
     *
     * @return Entity
     */
    @Nullable
    public Entity getSpawningEntity() {
        return entity;
    }
}
