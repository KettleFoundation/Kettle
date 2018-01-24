// 
// Decompiled by Procyon v0.5.30
// 

package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.monster.SkeletonType;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.EntityType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Skeleton;

public class CraftSkeleton extends CraftMonster implements Skeleton
{
    public CraftSkeleton(final CraftServer server, final EntitySkeleton entity) {
        super(server, entity);
    }
    
    @Override
    public EntitySkeleton getHandle() {
        return (EntitySkeleton)this.entity;
    }
    
    @Override
    public String toString() {
        return "CraftSkeleton";
    }
    
    @Override
    public EntityType getType() {
        return EntityType.SKELETON;
    }
    
    @Override
    public SkeletonType getSkeletonType() {
        return SkeletonType.values()[this.getHandle().func_189771_df().ordinal()];
    }
    
    @Override
    public void setSkeletonType(final SkeletonType type) {
        Validate.notNull((Object)type);
        this.getHandle().func_189768_a(net.minecraft.entity.monster.SkeletonType.func_190134_a(type.ordinal()));
    }
}
