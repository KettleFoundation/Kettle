package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;

public class CraftCustomEntity extends CraftEntity {
    public Class<? extends Entity> entityClass;
    private String entityName;

    public CraftCustomEntity(CraftServer server, Entity entity) {
        super(server, entity);
        this.entityClass = entity.getClass();
        this.entityName = EntityRegistry.entityTypeMap.get(entityClass);
        if (entityName == null)
            entityName = entity.getCommandSenderEntity().getName();
    }

    @Override
    public Entity getHandle() {
        return (Entity) entity;
    }

    @Override
    public String toString() {
        return this.entityName;
    }

    public EntityType getType() {
        EntityType type = EntityType.fromName(this.entityName);
        if (type != null)
            return type;
        else return EntityType.UNKNOWN;
    }
}