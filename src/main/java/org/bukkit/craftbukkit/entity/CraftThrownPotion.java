// 
// Decompiled by Procyon v0.5.30
// 

package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.projectile.EntityThrowable;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import java.util.Iterator;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import net.minecraft.potion.PotionUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.potion.PotionEffect;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityPotion;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.ThrownPotion;

public abstract class CraftThrownPotion extends CraftProjectile implements ThrownPotion
{
    public CraftThrownPotion(final CraftServer server, final EntityPotion entity) {
        super(server, entity);
    }
    
    @Override
    public Collection<PotionEffect> getEffects() {
        final ImmutableList.Builder<PotionEffect> builder = /*(ImmutableList.Builder<PotionEffect>)*/ImmutableList.builder();
        for (final net.minecraft.potion.PotionEffect effect : PotionUtils.getEffectsFromStack(this.getHandle().getPotion())) {
            builder.add(/*(Object)*/CraftPotionUtil.toBukkit(effect));
        }
        return (Collection<PotionEffect>)builder.build();
    }
    
    @Override
    public ItemStack getItem() {
        return CraftItemStack.asBukkitCopy(this.getHandle().getPotion());
    }
    
    @Override
    public EntityPotion getHandle() {
        return (EntityPotion)this.entity;
    }
}
