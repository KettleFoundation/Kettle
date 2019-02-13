package org.bukkit.entity;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.EntityEffect;
import org.bukkit.Nameable;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.material.Directional;
import org.bukkit.metadata.Metadatable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * Represents a base entity in the world
 */
public interface Entity extends Metadatable, CommandSender, Nameable {

    /**
     * Gets the entity's current position
     *
     * @return a new copy of Location containing the position of this entity
     */
    public Location getLocation();

    /**
     * Stores the entity's current position in the provided Location object.
     * <p>
     * If the provided Location is null this method does nothing and returns
     * null.
     *
     * @param loc the location to copy into
     * @return The Location object provided or null
     */
    public Location getLocation(Location loc);

    /**
     * Sets this entity's velocity
     *
     * @param velocity New velocity to travel with
     */
    public void setVelocity(Vector velocity);

    /**
     * Gets this entity's current velocity
     *
     * @return Current traveling velocity of this entity
     */
    public Vector getVelocity();

    /**
     * Gets the entity's height
     *
     * @return height of entity
     */
    public double getHeight();

    /**
     * Gets the entity's width
     *
     * @return width of entity
     */
    public double getWidth();

    /**
     * Gets the entity's current bounding box.
     * <p>
     * The returned bounding box reflects the entity's current location and
     * size.
     *
     * @return the entity's current bounding box
     */
    public BoundingBox getBoundingBox();

    /**
     * Returns true if the entity is supported by a block. This value is a
     * state updated by the server and is not recalculated unless the entity
     * moves.
     *
     * @return True if entity is on ground.
     */
    public boolean isOnGround();

    /**
     * Gets the current world this entity resides in
     *
     * @return World
     */
    public World getWorld();

    /**
     * Teleports this entity to the given location. If this entity is riding a
     * vehicle, it will be dismounted prior to teleportation.
     *
     * @param location New location to teleport this entity to
     * @return <code>true</code> if the teleport was successful
     */
    public boolean teleport(Location location);

    /**
     * Teleports this entity to the given location. If this entity is riding a
     * vehicle, it will be dismounted prior to teleportation.
     *
     * @param location New location to teleport this entity to
     * @param cause The cause of this teleportation
     * @return <code>true</code> if the teleport was successful
     */
    public boolean teleport(Location location, TeleportCause cause);

    /**
     * Teleports this entity to the target Entity. If this entity is riding a
     * vehicle, it will be dismounted prior to teleportation.
     *
     * @param destination Entity to teleport this entity to
     * @return <code>true</code> if the teleport was successful
     */
    public boolean teleport(Entity destination);

    /**
     * Teleports this entity to the target Entity. If this entity is riding a
     * vehicle, it will be dismounted prior to teleportation.
     *
     * @param destination Entity to teleport this entity to
     * @param cause The cause of this teleportation
     * @return <code>true</code> if the teleport was successful
     */
    public boolean teleport(Entity destination, TeleportCause cause);

    // Paper start
    /**
     * Loads/Generates(in 1.13+) the Chunk asynchronously, and then teleports the entity when the chunk is ready.
     * @param loc Location to teleport to
     * @return A future that will be completed with the result of the teleport
     */
    public default java.util.concurrent.CompletableFuture<Boolean> teleportAsync(Location loc) {
        return teleportAsync(loc, TeleportCause.PLUGIN);
    }
    /**
     * Loads/Generates(in 1.13+) the Chunk asynchronously, and then teleports the entity when the chunk is ready.
     * @param loc Location to teleport to
     * @param cause Reason for teleport
     * @return A future that will be completed with the result of the teleport
     */
    public default java.util.concurrent.CompletableFuture<Boolean> teleportAsync(Location loc, TeleportCause cause) {
        java.util.concurrent.CompletableFuture<Boolean> future = new java.util.concurrent.CompletableFuture<>();
        loc.getWorld().getChunkAtAsync(loc).thenAccept((chunk) -> future.complete(teleport(loc, cause)));
        return future;
    }
    // Paper end

    /**
     * Returns a list of entities within a bounding box centered around this
     * entity
     *
     * @param x 1/2 the size of the box along x axis
     * @param y 1/2 the size of the box along y axis
     * @param z 1/2 the size of the box along z axis
     * @return {@code List<Entity>} List of entities nearby
     */
    public List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z);

    /**
     * Returns a unique id for this entity
     *
     * @return Entity id
     */
    public int getEntityId();

    /**
     * Returns the entity's current fire ticks (ticks before the entity stops
     * being on fire).
     *
     * @return int fireTicks
     */
    public int getFireTicks();

    /**
     * Returns the entity's maximum fire ticks.
     *
     * @return int maxFireTicks
     */
    public int getMaxFireTicks();

    /**
     * Sets the entity's current fire ticks (ticks before the entity stops
     * being on fire).
     *
     * @param ticks Current ticks remaining
     */
    public void setFireTicks(int ticks);

    /**
     * Mark the entity's removal.
     */
    public void remove();

    /**
     * Returns true if this entity has been marked for removal.
     *
     * @return True if it is dead.
     */
    public boolean isDead();

    /**
     * Returns false if the entity has died or been despawned for some other
     * reason.
     *
     * @return True if valid.
     */
    public boolean isValid();

    /**
     * Gets the {@link Server} that contains this Entity
     *
     * @return Server instance running this Entity
     */
    public Server getServer();

    /**
     * Returns true if the entity gets persisted.
     * <p>
     * By default all entities are persistent. An entity will also not get
     * persisted, if it is riding an entity that is not persistent.
     * <p>
     * The persistent flag on players controls whether or not to save their
     * playerdata file when they quit. If a player is directly or indirectly
     * riding a non-persistent entity, the vehicle at the root and all its
     * passengers won't get persisted.
     * <p>
     * <b>This should not be confused with
     * {@link LivingEntity#setRemoveWhenFarAway(boolean)} which controls
     * despawning of living entities. </b>
     *
     * @return true if this entity is persistent
     * @deprecated draft API
     */
    @Deprecated
    public boolean isPersistent();

    /**
     * Sets whether or not the entity gets persisted.
     *
     * @param persistent the persistence status
     * @see #isPersistent()
     * @deprecated draft API
     */
    @Deprecated
    public void setPersistent(boolean persistent);

    /**
     * Gets the primary passenger of a vehicle. For vehicles that could have
     * multiple passengers, this will only return the primary passenger.
     *
     * @return an entity
     * @deprecated entities may have multiple passengers, use
     * {@link #getPassengers()}
     */
    @Deprecated
    public Entity getPassenger();

    /**
     * Set the passenger of a vehicle.
     *
     * @param passenger The new passenger.
     * @return false if it could not be done for whatever reason
     * @deprecated entities may have multiple passengers, use
     * {@link #getPassengers()}
     */
    @Deprecated
    public boolean setPassenger(Entity passenger);

    /**
     * Gets a list of passengers of this vehicle.
     * <p>
     * The returned list will not be directly linked to the entity's current
     * passengers, and no guarantees are made as to its mutability.
     *
     * @return list of entities corresponding to current passengers.
     */
    public List<Entity> getPassengers();

    /**
     * Add a passenger to the vehicle.
     *
     * @param passenger The passenger to add
     * @return false if it could not be done for whatever reason
     */
    public boolean addPassenger(Entity passenger);

    /**
     * Remove a passenger from the vehicle.
     *
     * @param passenger The passenger to remove
     * @return false if it could not be done for whatever reason
     */
    public boolean removePassenger(Entity passenger);

    /**
     * Check if a vehicle has passengers.
     *
     * @return True if the vehicle has no passengers.
     */
    public boolean isEmpty();

    /**
     * Eject any passenger.
     *
     * @return True if there was a passenger.
     */
    public boolean eject();

    /**
     * Returns the distance this entity has fallen
     *
     * @return The distance.
     */
    public float getFallDistance();

    /**
     * Sets the fall distance for this entity
     *
     * @param distance The new distance.
     */
    public void setFallDistance(float distance);

    /**
     * Record the last {@link EntityDamageEvent} inflicted on this entity
     *
     * @param event a {@link EntityDamageEvent}
     */
    public void setLastDamageCause(EntityDamageEvent event);

    /**
     * Retrieve the last {@link EntityDamageEvent} inflicted on this entity.
     * This event may have been cancelled.
     *
     * @return the last known {@link EntityDamageEvent} or null if hitherto
     *     unharmed
     */
    public EntityDamageEvent getLastDamageCause();

    /**
     * Returns a unique and persistent id for this entity
     *
     * @return unique id
     */
    public UUID getUniqueId();

    /**
     * Gets the amount of ticks this entity has lived for.
     * <p>
     * This is the equivalent to "age" in entities.
     *
     * @return Age of entity
     */
    public int getTicksLived();

    /**
     * Sets the amount of ticks this entity has lived for.
     * <p>
     * This is the equivalent to "age" in entities. May not be less than one
     * tick.
     *
     * @param value Age of entity
     */
    public void setTicksLived(int value);

    /**
     * Performs the specified {@link EntityEffect} for this entity.
     * <p>
     * This will be viewable to all players near the entity.
     * <p>
     * If the effect is not applicable to this class of entity, it will not play.
     *
     * @param type Effect to play.
     */
    public void playEffect(EntityEffect type);

    /**
     * Get the type of the entity.
     *
     * @return The entity type.
     */
    public EntityType getType();

    /**
     * Returns whether this entity is inside a vehicle.
     *
     * @return True if the entity is in a vehicle.
     */
    public boolean isInsideVehicle();

    /**
     * Leave the current vehicle. If the entity is currently in a vehicle (and
     * is removed from it), true will be returned, otherwise false will be
     * returned.
     *
     * @return True if the entity was in a vehicle.
     */
    public boolean leaveVehicle();

    /**
     * Get the vehicle that this player is inside. If there is no vehicle,
     * null will be returned.
     *
     * @return The current vehicle.
     */
    public Entity getVehicle();

    /**
     * Sets whether or not to display the mob's custom name client side. The
     * name will be displayed above the mob similarly to a player.
     * <p>
     * This value has no effect on players, they will always display their
     * name.
     *
     * @param flag custom name or not
     */
    public void setCustomNameVisible(boolean flag);

    /**
     * Gets whether or not the mob's custom name is displayed client side.
     * <p>
     * This value has no effect on players, they will always display their
     * name.
     *
     * @return if the custom name is displayed
     */
    public boolean isCustomNameVisible();

    /**
     * Sets whether the entity has a team colored (default: white) glow.
     *
     * @param flag if the entity is glowing
     */
    void setGlowing(boolean flag);

    /**
     * Gets whether the entity is glowing or not.
     *
     * @return whether the entity is glowing
     */
    boolean isGlowing();

    /**
     * Sets whether the entity is invulnerable or not.
     * <p>
     * When an entity is invulnerable it can only be damaged by players in
     * creative mode.
     *
     * @param flag if the entity is invulnerable
     */
    public void setInvulnerable(boolean flag);

    /**
     * Gets whether the entity is invulnerable or not.
     *
     * @return whether the entity is
     */
    public boolean isInvulnerable();

    /**
     * Gets whether the entity is silent or not.
     *
     * @return whether the entity is silent.
     */
    public boolean isSilent();

    /**
     * Sets whether the entity is silent or not.
     * <p>
     * When an entity is silent it will not produce any sound.
     *
     * @param flag if the entity is silent
     */
    public void setSilent(boolean flag);

    /**
     * Returns whether gravity applies to this entity.
     *
     * @return whether gravity applies
     */
    boolean hasGravity();

    /**
     * Sets whether gravity applies to this entity.
     *
     * @param gravity whether gravity should apply
     */
    void setGravity(boolean gravity);

    /**
     * Gets the period of time (in ticks) before this entity can use a portal.
     *
     * @return portal cooldown ticks
     */
    int getPortalCooldown();

    /**
     * Sets the period of time (in ticks) before this entity can use a portal.
     *
     * @param cooldown portal cooldown ticks
     */
    void setPortalCooldown(int cooldown);

    /**
     * Returns a set of tags for this entity.
     * <br>
     * Entities can have no more than 1024 tags.
     *
     * @return a set of tags for this entity
     */
    Set<String> getScoreboardTags();

    /**
     * Add a tag to this entity.
     * <br>
     * Entities can have no more than 1024 tags.
     *
     * @param tag the tag to add
     * @return true if the tag was successfully added
     */
    boolean addScoreboardTag(String tag);

    /**
     * Removes a given tag from this entity.
     *
     * @param tag the tag to remove
     * @return true if the tag was successfully removed
     */
    boolean removeScoreboardTag(String tag);

    /**
     * Returns the reaction of the entity when moved by a piston.
     *
     * @return reaction
     */
    PistonMoveReaction getPistonMoveReaction();

    /**
     * Get the closest cardinal {@link BlockFace} direction an entity is
     * currently facing.
     * <br>
     * This will not return any non-cardinal directions such as
     * {@link BlockFace#UP} or {@link BlockFace#DOWN}.
     * <br>
     * {@link Hanging} entities will override this call and thus their behavior
     * may be different.
     *
     * @return the entity's current cardinal facing.
     * @see Hanging
     * @see Directional#getFacing()
     */
    BlockFace getFacing();

    // Spigot start
    public class Spigot extends CommandSender.Spigot
    {

        /**
         * Returns whether this entity is invulnerable.
         *
         * @return True if the entity is invulnerable.
         * @deprecated {@link Entity#isInvulnerable()}
         */
        @Deprecated
        public boolean isInvulnerable()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }

    @Override
    Spigot spigot();
    // Spigot end

    // Paper start
    /**
     * Gets the location where this entity originates from.
     * <p>
     * This value can be null if the entity hasn't yet been added to the world.
     *
     * @return Location where entity originates or null if not yet added
     */
    Location getOrigin();

    /**
     * Returns whether this entity was spawned from a mob spawner.
     *
     * @return True if entity spawned from a mob spawner
     */
    boolean fromMobSpawner();

    /**
     * Gets the latest chunk an entity is currently or was in.
     *
     * @return The current, or most recent chunk if the entity is invalid (which may load the chunk)
     */
    Chunk getChunk();
    // Paper end
}
