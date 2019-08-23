package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;

public class CraftCustomEntity extends CraftEntity {
    private String entityName;

    public CraftCustomEntity(CraftServer server, Entity entity) {
        super(server, entity);
        this.entityName = EntityRegistry.entityTypeMap.get(entity.getClass());
        if (entityName == null) {
            entityName = entity.getName();
        }
    }

    @Override
    public Entity getHandle() {
        return entity;
    }

    @Override
    public String toString() {
        return this.entityName;
    }

    @Override
    public EntityType getType() {
        EntityType type = EntityType.fromName(this.entityName);
        if (type != null) {
            return type;
        } else {
            return EntityType.UNKNOWN;
        }
    }

    @Override
    public String getCustomName() {
        final String name = getHandle().getCustomNameTag();
        if (name == null || name.length() == 0) {
            return entity.getName();
        }

        return name;
    }
}