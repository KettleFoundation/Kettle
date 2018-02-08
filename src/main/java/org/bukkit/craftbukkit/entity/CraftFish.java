package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.bukkit.projectiles.ProjectileSource;

public class CraftFish extends AbstractProjectile implements Fish {
    private double biteChance = -1;

    public CraftFish(CraftServer server, EntityFishHook entity) {
        super(server, entity);
    }

    public ProjectileSource getShooter() {
        if (getHandle().field_146042_b != null) {
            return getHandle().field_146042_b.getBukkitEntity();
        }

        return null;
    }

    public void setShooter(ProjectileSource shooter) {
        if (shooter instanceof CraftHumanEntity) {
            getHandle().field_146042_b = (EntityPlayer) ((CraftHumanEntity) shooter).entity;
        }
    }

    @Override
    public EntityFishHook getHandle() {
        return (EntityFishHook) entity;
    }

    @Override
    public String toString() {
        return "CraftFish";
    }

    public EntityType getType() {
        return EntityType.FISHING_HOOK;
    }

    public double getBiteChance() {
        EntityFishHook hook = getHandle();

        if (this.biteChance == -1) {
            if (hook.worldObj.canLightningStrikeAt(MathHelper.floor_double(hook.posX), net.minecraft.util.MathHelper.floor_double(hook.posY) + 1, net.minecraft.util.MathHelper.floor_double(hook.posZ))) {
                return 1/300.0;
            }
            return 1/500.0;
        }
        return this.biteChance;
    }

    public void setBiteChance(double chance) {
        Validate.isTrue(chance >= 0 && chance <= 1, "The bite chance must be between 0 and 1.");
        this.biteChance = chance;
    }

    @Deprecated
    public LivingEntity _INVALID_getShooter() {
        return (LivingEntity) getShooter();
    }

    @Deprecated
    public void _INVALID_setShooter(LivingEntity shooter) {
        setShooter(shooter);
    }
}
