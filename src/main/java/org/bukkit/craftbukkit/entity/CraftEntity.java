// 
// Decompiled by Procyon v0.5.30
// 

package org.bukkit.craftbukkit.entity;

import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.permissions.ServerOperator;
import net.minecraft.util.DamageSource;
import org.bukkit.permissions.PermissionAttachmentInfo;
import java.util.Set;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.EntityEffect;
import java.util.UUID;
import com.google.common.base.Preconditions;
import org.bukkit.Server;
import java.util.Iterator;
import java.util.ArrayList;
import com.google.common.base.Predicate;
import java.util.List;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.entity.Entity;

public abstract class CraftEntity implements Entity
{
    private static PermissibleBase perm;
    protected final CraftServer server;
    protected net.minecraft.entity.Entity entity;
    private EntityDamageEvent lastDamageEvent;
    
    public CraftEntity(final CraftServer server, final net.minecraft.entity.Entity entity) {
        this.server = server;
        this.entity = entity;
    }
    
    public static CraftEntity getEntity(final CraftServer server, final net.minecraft.entity.Entity entity) {
        if (entity instanceof EntityLivingBase) {
            if (entity instanceof EntityPlayer) {
                if (entity instanceof EntityPlayerMP) {
                    return new CraftPlayer(server, (EntityPlayerMP)entity);
                }
                return new CraftHumanEntity(server, (EntityPlayer)entity);
            }
            else if (entity instanceof EntityWaterMob) {
                if (entity instanceof EntitySquid) {
                    return new CraftSquid(server, (EntitySquid)entity);
                }
                return new CraftWaterMob(server, (EntityWaterMob)entity);
            }
            else if (entity instanceof EntityCreature) {
                if (entity instanceof EntityAnimal) {
                    if (entity instanceof EntityChicken) {
                        return new CraftChicken(server, (EntityChicken)entity);
                    }
                    if (entity instanceof EntityCow) {
                        if (entity instanceof EntityMooshroom) {
                            return new CraftMushroomCow(server, (EntityMooshroom)entity);
                        }
                        return new CraftCow(server, (EntityCow)entity);
                    }
                    else {
                        if (entity instanceof EntityPig) {
                            return new CraftPig(server, (EntityPig)entity);
                        }
                        if (entity instanceof EntityTameable) {
                            if (entity instanceof EntityWolf) {
                                return new CraftWolf(server, (EntityWolf)entity);
                            }
                            if (entity instanceof EntityOcelot) {
                                return new CraftOcelot(server, (EntityOcelot)entity);
                            }
                        }
                        else {
                            if (entity instanceof EntitySheep) {
                                return new CraftSheep(server, (EntitySheep)entity);
                            }
                            if (entity instanceof EntityHorse) {
                                return new CraftHorse(server, (EntityHorse)entity);
                            }
                            if (entity instanceof EntityRabbit) {
                                return new CraftRabbit(server, (EntityRabbit)entity);
                            }
                            if (entity instanceof EntityPolarBear) {
                                return new CraftPolarBear(server, (EntityPolarBear)entity);
                            }
                            return new CraftAnimals(server, (EntityAnimal)entity);
                        }
                    }
                }
                else if (entity instanceof EntityMob) {
                    if (entity instanceof EntityZombie) {
                        if (entity instanceof EntityPigZombie) {
                            return new CraftPigZombie(server, (EntityPigZombie)entity);
                        }
                        return new CraftZombie(server, (EntityZombie)entity);
                    }
                    else {
                        if (entity instanceof EntityCreeper) {
                            return new CraftCreeper(server, (EntityCreeper)entity);
                        }
                        if (entity instanceof EntityEnderman) {
                            return new CraftEnderman(server, (EntityEnderman)entity);
                        }
                        if (entity instanceof EntitySilverfish) {
                            return new CraftSilverfish(server, (EntitySilverfish)entity);
                        }
                        if (entity instanceof EntityGiantZombie) {
                            return new CraftGiant(server, (EntityGiantZombie)entity);
                        }
                        if (entity instanceof EntitySkeleton) {
                            return new CraftSkeleton(server, (EntitySkeleton)entity);
                        }
                        if (entity instanceof EntityBlaze) {
                            return new CraftBlaze(server, (EntityBlaze)entity);
                        }
                        if (entity instanceof EntityWitch) {
                            return new CraftWitch(server, (EntityWitch)entity);
                        }
                        if (entity instanceof EntityWither) {
                            return new CraftWither(server, (EntityWither)entity);
                        }
                        if (entity instanceof EntitySpider) {
                            if (entity instanceof EntityCaveSpider) {
                                return new CraftCaveSpider(server, (EntityCaveSpider)entity);
                            }
                            return new CraftSpider(server, (EntitySpider)entity);
                        }
                        else {
                            if (entity instanceof EntityEndermite) {
                                return new CraftEndermite(server, (EntityEndermite)entity);
                            }
                            if (entity instanceof EntityGuardian) {
                                return new CraftGuardian(server, (EntityGuardian)entity);
                            }
                            return new CraftMonster(server, (EntityMob)entity);
                        }
                    }
                }
                else if (entity instanceof EntityGolem) {
                    if (entity instanceof EntitySnowman) {
                        return new CraftSnowman(server, (EntitySnowman)entity);
                    }
                    if (entity instanceof EntityIronGolem) {
                        return new CraftIronGolem(server, (EntityIronGolem)entity);
                    }
                    if (entity instanceof EntityShulker) {
                        return new CraftShulker(server, (EntityShulker)entity);
                    }
                }
                else {
                    if (entity instanceof EntityVillager) {
                        return new CraftVillager(server, (EntityVillager)entity);
                    }
                    return new CraftCreature(server, (EntityCreature)entity);
                }
            }
            else if (entity instanceof EntitySlime) {
                if (entity instanceof EntityMagmaCube) {
                    return new CraftMagmaCube(server, (EntityMagmaCube)entity);
                }
                return new CraftSlime(server, (EntitySlime)entity);
            }
            else if (entity instanceof EntityFlying) {
                if (entity instanceof EntityGhast) {
                    return new CraftGhast(server, (EntityGhast)entity);
                }
                return new CraftFlying(server, (EntityFlying)entity);
            }
            else {
                if (entity instanceof EntityDragon) {
                    return new CraftEnderDragon(server, (EntityDragon)entity);
                }
                if (entity instanceof EntityAmbientCreature) {
                    if (entity instanceof EntityBat) {
                        return new CraftBat(server, (EntityBat)entity);
                    }
                    return new CraftAmbient(server, (EntityAmbientCreature)entity);
                }
                else {
                    if (entity instanceof EntityArmorStand) {
                        return new CraftArmorStand(server, (EntityArmorStand)entity);
                    }
                    return new CraftLivingEntity(server, (EntityLivingBase)entity);
                }
            }
        }
        else if (entity instanceof EntityDragonPart) {
            final EntityDragonPart part = (EntityDragonPart)entity;
            if (part.entityDragonObj instanceof EntityDragon) {
                return new CraftEnderDragonPart(server, (EntityDragonPart)entity);
            }
            return new CraftComplexPart(server, (EntityDragonPart)entity);
        }
        else {
            if (entity instanceof EntityXPOrb) {
                return new CraftExperienceOrb(server, (EntityXPOrb)entity);
            }
            if (entity instanceof EntityTippedArrow) {
                if (((EntityTippedArrow)entity).isTipped()) {
                    return new CraftTippedArrow(server, (EntityTippedArrow)entity);
                }
                return new CraftArrow(server, (EntityArrow)entity);
            }
            else {
                if (entity instanceof EntitySpectralArrow) {
                    return new CraftSpectralArrow(server, (EntitySpectralArrow)entity);
                }
                if (entity instanceof EntityArrow) {
                    return new CraftArrow(server, (EntityArrow)entity);
                }
                if (entity instanceof EntityBoat) {
                    return new CraftBoat(server, (EntityBoat)entity);
                }
                if (entity instanceof EntityThrowable) {
                    if (entity instanceof EntityEgg) {
                        return new CraftEgg(server, (EntityEgg)entity);
                    }
                    if (entity instanceof EntitySnowball) {
                        return new CraftSnowball(server, (EntitySnowball)entity);
                    }
                    if (entity instanceof EntityPotion) {
                        if (!((EntityPotion)entity).isLingering()) {
                            return new CraftSplashPotion(server, (EntityPotion)entity);
                        }
                        return new CraftLingeringPotion(server, (EntityPotion)entity);
                    }
                    else {
                        if (entity instanceof EntityEnderPearl) {
                            return new CraftEnderPearl(server, (EntityEnderPearl)entity);
                        }
                        if (entity instanceof EntityExpBottle) {
                            return new CraftThrownExpBottle(server, (EntityExpBottle)entity);
                        }
                    }
                }
                else {
                    if (entity instanceof EntityFallingBlock) {
                        return new CraftFallingSand(server, (EntityFallingBlock)entity);
                    }
                    if (entity instanceof EntityFireball) {
                        if (entity instanceof EntitySmallFireball) {
                            return new CraftSmallFireball(server, (EntitySmallFireball)entity);
                        }
                        if (entity instanceof EntityLargeFireball) {
                            return new CraftLargeFireball(server, (EntityLargeFireball)entity);
                        }
                        if (entity instanceof EntityWitherSkull) {
                            return new CraftWitherSkull(server, (EntityWitherSkull)entity);
                        }
                        if (entity instanceof EntityDragonFireball) {
                            return new CraftDragonFireball(server, (EntityDragonFireball)entity);
                        }
                        return new CraftFireball(server, (EntityFireball)entity);
                    }
                    else {
                        if (entity instanceof EntityEnderEye) {
                            return new CraftEnderSignal(server, (EntityEnderEye)entity);
                        }
                        if (entity instanceof EntityEnderCrystal) {
                            return new CraftEnderCrystal(server, (EntityEnderCrystal)entity);
                        }
                        if (entity instanceof EntityFishHook) {
                            return new CraftFish(server, (EntityFishHook)entity);
                        }
                        if (entity instanceof EntityItem) {
                            return new CraftItem(server, (EntityItem)entity);
                        }
                        if (entity instanceof EntityWeatherEffect) {
                            if (entity instanceof EntityLightningBolt) {
                                return new CraftLightningStrike(server, (EntityLightningBolt)entity);
                            }
                            return new CraftWeather(server, (EntityWeatherEffect)entity);
                        }
                        else if (entity instanceof EntityMinecart) {
                            if (entity instanceof EntityMinecartFurnace) {
                                return new CraftMinecartFurnace(server, (EntityMinecartFurnace)entity);
                            }
                            if (entity instanceof EntityMinecartChest) {
                                return new CraftMinecartChest(server, (EntityMinecartChest)entity);
                            }
                            if (entity instanceof EntityMinecartTNT) {
                                return new CraftMinecartTNT(server, (EntityMinecartTNT)entity);
                            }
                            if (entity instanceof EntityMinecartHopper) {
                                return new CraftMinecartHopper(server, (EntityMinecartHopper)entity);
                            }
                            if (entity instanceof EntityMinecartMobSpawner) {
                                return new CraftMinecartMobSpawner(server, (EntityMinecartMobSpawner)entity);
                            }
                            if (entity instanceof EntityMinecartEmpty) {
                                return new CraftMinecartRideable(server, (EntityMinecart)entity);
                            }
                            if (entity instanceof EntityMinecartCommandBlock) {
                                return new CraftMinecartCommand(server, (EntityMinecartCommandBlock)entity);
                            }
                        }
                        else if (entity instanceof EntityHanging) {
                            if (entity instanceof EntityPainting) {
                                return new CraftPainting(server, (EntityPainting)entity);
                            }
                            if (entity instanceof EntityItemFrame) {
                                return new CraftItemFrame(server, (EntityItemFrame)entity);
                            }
                            if (entity instanceof EntityLeashKnot) {
                                return new CraftLeash(server, (EntityLeashKnot)entity);
                            }
                            return new CraftHanging(server, (EntityHanging)entity);
                        }
                        else {
                            if (entity instanceof EntityTNTPrimed) {
                                return new CraftTNTPrimed(server, (EntityTNTPrimed)entity);
                            }
                            if (entity instanceof EntityFireworkRocket) {
                                return new CraftFirework(server, (EntityFireworkRocket)entity);
                            }
                            if (entity instanceof EntityShulkerBullet) {
                                return new CraftShulkerBullet(server, (EntityShulkerBullet)entity);
                            }
                            if (entity instanceof EntityAreaEffectCloud) {
                                return new CraftAreaEffectCloud(server, (EntityAreaEffectCloud)entity);
                            }
                        }
                    }
                }
            }
        }
        throw new AssertionError((Object)("Unknown entity " + ((entity == null) ? null : entity.getClass())));
    }
    
    @Override
    public Location getLocation() {
        return new Location(this.getWorld(), this.entity.posX, this.entity.posY, this.entity.posZ, this.entity.rotationYaw, this.entity.rotationPitch);
    }
    
    @Override
    public Location getLocation(final Location loc) {
        if (loc != null) {
            loc.setWorld(this.getWorld());
            loc.setX(this.entity.posX);
            loc.setY(this.entity.posY);
            loc.setZ(this.entity.posZ);
            loc.setYaw(this.entity.rotationYaw);
            loc.setPitch(this.entity.rotationPitch);
        }
        return loc;
    }
    
    @Override
    public Vector getVelocity() {
        return new Vector(this.entity.motionX, this.entity.motionY, this.entity.motionZ);
    }
    
    @Override
    public void setVelocity(final Vector vel) {
        this.entity.motionX = vel.getX();
        this.entity.motionY = vel.getY();
        this.entity.motionZ = vel.getZ();
        this.entity.velocityChanged = true;
    }
    
    @Override
    public boolean isOnGround() {
        if (this.entity instanceof EntityArrow) {
            return ((EntityArrow)this.entity).isInGround();
        }
        return this.entity.onGround;
    }
    
    @Override
    public World getWorld() {
        return this.entity.worldObj.getWorld();
    }
    
    @Override
    public boolean teleport(final Location location) {
        return this.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
    
    @Override
    public boolean teleport(final Location location, final PlayerTeleportEvent.TeleportCause cause) {
        if (this.entity.isBeingRidden() || this.entity.isDead) {
            return false;
        }
        this.entity.dismountRidingEntity();
        this.entity.worldObj = ((CraftWorld)location.getWorld()).getHandle();
        this.entity.setPositionAndRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return true;
    }
    
    @Override
    public boolean teleport(final Entity destination) {
        return this.teleport(destination.getLocation());
    }
    
    @Override
    public boolean teleport(final Entity destination, final PlayerTeleportEvent.TeleportCause cause) {
        return this.teleport(destination.getLocation(), cause);
    }
    
    @Override
    public List<Entity> getNearbyEntities(final double x, final double y, final double z) {
        final List<net.minecraft.entity.Entity> notchEntityList = this.entity.worldObj.getEntitiesInAABBexcluding(this.entity, this.entity.getEntityBoundingBox().expand(x, y, z), null);
        final List<Entity> bukkitEntityList = new ArrayList<Entity>(notchEntityList.size());
        for (final net.minecraft.entity.Entity e : notchEntityList) {
            bukkitEntityList.add(e.getBukkitEntity());
        }
        return bukkitEntityList;
    }
    
    @Override
    public int getEntityId() {
        return this.entity.getEntityId();
    }
    
    @Override
    public int getFireTicks() {
        return this.entity.fire;
    }
    
    @Override
    public int getMaxFireTicks() {
        return this.entity.fireResistance;
    }
    
    @Override
    public void setFireTicks(final int ticks) {
        this.entity.fire = ticks;
    }
    
    @Override
    public void remove() {
        this.entity.setDead();
    }
    
    @Override
    public boolean isDead() {
        return !this.entity.isEntityAlive();
    }
    
    @Override
    public boolean isValid() {
        return this.entity.isEntityAlive() && this.entity.valid;
    }
    
    @Override
    public Server getServer() {
        return this.server;
    }
    
    public Vector getMomentum() {
        return this.getVelocity();
    }
    
    public void setMomentum(final Vector value) {
        this.setVelocity(value);
    }
    
    @Override
    public Entity getPassenger() {
        return this.isEmpty() ? null : this.getHandle().riddenByEntities.get(0).getBukkitEntity();
    }
    
    @Override
    public boolean setPassenger(final Entity passenger) {
        Preconditions.checkArgument(!this.equals(passenger), (Object)"Entity cannot ride itself.");
        if (passenger instanceof CraftEntity) {
            this.eject();
            ((CraftEntity)passenger).getHandle().startRiding(this.getHandle());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isEmpty() {
        return !this.getHandle().isBeingRidden();
    }
    
    @Override
    public boolean eject() {
        if (this.isEmpty()) {
            return false;
        }
        this.getPassenger().leaveVehicle();
        return true;
    }
    
    @Override
    public float getFallDistance() {
        return this.getHandle().fallDistance;
    }
    
    @Override
    public void setFallDistance(final float distance) {
        this.getHandle().fallDistance = distance;
    }
    
    @Override
    public void setLastDamageCause(final EntityDamageEvent event) {
        this.lastDamageEvent = event;
    }
    
    @Override
    public EntityDamageEvent getLastDamageCause() {
        return this.lastDamageEvent;
    }
    
    @Override
    public UUID getUniqueId() {
        return this.getHandle().getUniqueID();
    }
    
    @Override
    public int getTicksLived() {
        return this.getHandle().ticksExisted;
    }
    
    @Override
    public void setTicksLived(final int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Age must be at least 1 tick");
        }
        this.getHandle().ticksExisted = value;
    }
    
    public net.minecraft.entity.Entity getHandle() {
        return this.entity;
    }
    
    @Override
    public void playEffect(final EntityEffect type) {
        this.getHandle().worldObj.setEntityState(this.getHandle(), type.getData());
    }
    
    public void setHandle(final net.minecraft.entity.Entity entity) {
        this.entity = entity;
    }
    
    @Override
    public String toString() {
        return "CraftEntity{id=" + this.getEntityId() + '}';
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final CraftEntity other = (CraftEntity)obj;
        return this.getEntityId() == other.getEntityId();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.getEntityId();
        return hash;
    }
    
    @Override
    public void setMetadata(final String metadataKey, final MetadataValue newMetadataValue) {
        (/*(MetadataStoreBase<CraftEntity>)*/this.server.getEntityMetadata()).setMetadata(this, metadataKey, newMetadataValue);
    }
    
    @Override
    public List<MetadataValue> getMetadata(final String metadataKey) {
        return (/*(MetadataStoreBase<CraftEntity>)*/this.server.getEntityMetadata()).getMetadata(this, metadataKey);
    }
    
    @Override
    public boolean hasMetadata(final String metadataKey) {
        return (/*(MetadataStoreBase<CraftEntity>)*/this.server.getEntityMetadata()).hasMetadata(this, metadataKey);
    }
    
    @Override
    public void removeMetadata(final String metadataKey, final Plugin owningPlugin) {
        (/*(MetadataStoreBase<CraftEntity>)*/this.server.getEntityMetadata()).removeMetadata(this, metadataKey, owningPlugin);
    }
    
    @Override
    public boolean isInsideVehicle() {
        return this.getHandle().isRiding();
    }
    
    @Override
    public boolean leaveVehicle() {
        if (!this.isInsideVehicle()) {
            return false;
        }
        this.getHandle().dismountRidingEntity();
        return true;
    }
    
    @Override
    public Entity getVehicle() {
        if (!this.isInsideVehicle()) {
            return null;
        }
        return this.getHandle().getLowestRidingEntity().getBukkitEntity();
    }
    
    @Override
    public void setCustomName(String name) {
        if (name == null) {
            name = "";
        }
        this.getHandle().setCustomNameTag(name);
    }
    
    @Override
    public String getCustomName() {
        final String name = this.getHandle().getCustomNameTag();
        if (name == null || name.length() == 0) {
            return null;
        }
        return name;
    }
    
    @Override
    public void setCustomNameVisible(final boolean flag) {
        this.getHandle().setAlwaysRenderNameTag(flag);
    }
    
    @Override
    public boolean isCustomNameVisible() {
        return this.getHandle().getAlwaysRenderNameTag();
    }
    
    @Override
    public void sendMessage(final String message) {
    }
    
    @Override
    public void sendMessage(final String[] messages) {
    }
    
    @Override
    public String getName() {
        return this.getHandle().getName();
    }
    
    @Override
    public boolean isPermissionSet(final String name) {
        return getPermissibleBase().isPermissionSet(name);
    }
    
    @Override
    public boolean isPermissionSet(final Permission perm) {
        return getPermissibleBase().isPermissionSet(perm);
    }
    
    @Override
    public boolean hasPermission(final String name) {
        return getPermissibleBase().hasPermission(name);
    }
    
    @Override
    public boolean hasPermission(final Permission perm) {
        return getPermissibleBase().hasPermission(perm);
    }
    
    @Override
    public PermissionAttachment addAttachment(final Plugin plugin, final String name, final boolean value) {
        return getPermissibleBase().addAttachment(plugin, name, value);
    }
    
    @Override
    public PermissionAttachment addAttachment(final Plugin plugin) {
        return getPermissibleBase().addAttachment(plugin);
    }
    
    @Override
    public PermissionAttachment addAttachment(final Plugin plugin, final String name, final boolean value, final int ticks) {
        return getPermissibleBase().addAttachment(plugin, name, value, ticks);
    }
    
    @Override
    public PermissionAttachment addAttachment(final Plugin plugin, final int ticks) {
        return getPermissibleBase().addAttachment(plugin, ticks);
    }
    
    @Override
    public void removeAttachment(final PermissionAttachment attachment) {
        getPermissibleBase().removeAttachment(attachment);
    }
    
    @Override
    public void recalculatePermissions() {
        getPermissibleBase().recalculatePermissions();
    }
    
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return getPermissibleBase().getEffectivePermissions();
    }
    
    @Override
    public boolean isOp() {
        return getPermissibleBase().isOp();
    }
    
    @Override
    public void setOp(final boolean value) {
        getPermissibleBase().setOp(value);
    }
    
    @Override
    public void setGlowing(final boolean flag) {
        this.getHandle().glowing = flag;
        final net.minecraft.entity.Entity e = this.getHandle();
        if (e.getFlag(6) != flag) {
            e.setFlag(6, flag);
        }
    }
    
    @Override
    public boolean isGlowing() {
        return this.getHandle().glowing;
    }
    
    @Override
    public void setInvulnerable(final boolean flag) {
        this.getHandle().setEntityInvulnerable(flag);
    }
    
    @Override
    public boolean isInvulnerable() {
        return this.getHandle().isEntityInvulnerable(DamageSource.generic);
    }
    
    @Override
    public boolean isSilent() {
        return this.getHandle().isSilent();
    }
    
    @Override
    public void setSilent(final boolean flag) {
        this.getHandle().setSilent(flag);
    }
    
    @Override
    public boolean hasGravity() {
        return !this.getHandle().func_189652_ae();
    }
    
    @Override
    public void setGravity(final boolean gravity) {
        this.getHandle().func_189654_d(!gravity);
    }
    
    private static PermissibleBase getPermissibleBase() {
        if (CraftEntity.perm == null) {
            CraftEntity.perm = new PermissibleBase(new ServerOperator() {
                @Override
                public boolean isOp() {
                    return false;
                }
                
                @Override
                public void setOp(final boolean value) {
                }
            });
        }
        return CraftEntity.perm;
    }
}
