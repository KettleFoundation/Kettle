package com.destroystokyo.paper;

import com.google.common.collect.Lists;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Helps prepare a particle to be sent to players.
 *
 * Usage of the builder is preferred over the super long {@link World#spawnParticle(Particle, Location, int, double, double, double, double, Object)} API
 */
public class ParticleBuilder {
    private Particle particle;
    private List<Player> receivers;
    private Player source;
    private Location location;
    private int count = 1;
    private double offsetX = 0, offsetY = 0, offsetZ = 0;
    private double extra = 1;
    private Object data;
    private boolean force = true;

    public ParticleBuilder(Particle particle) {
        this.particle = particle;
    }

    /**
     * Sends the particle to all receiving players (or all).
     * This method is safe to use Asynchronously
     *
     * @return a reference to this object.
     */
    public ParticleBuilder spawn() {
        if (this.location == null) {
            throw new IllegalStateException("Please specify location for this particle");
        }
        location.getWorld().spawnParticle(particle, receivers, source,
            location.getX(), location.getY(), location.getZ(),
            count, offsetX, offsetY, offsetZ, extra, data, force
        );
        return this;
    }

    /**
     * @return The particle going to be sent
     */
    public Particle particle() {
        return particle;
    }

    /**
     * Changes what particle will be sent
     * @param particle The particle
     * @return a reference to this object.
     */
    public ParticleBuilder particle(Particle particle) {
        this.particle = particle;
        return this;
    }

    /**
     * @return List of players who will receive the particle, or null for all in world
     */
    @Nullable
    public List<Player> receivers() {
        return receivers;
    }

    /**
     * Example use:
     *
     * builder.receivers(16);
     * if (builder.hasReceivers()) {
     *     sendParticleAsync(builder);
     * }
     * @return If this particle is going to be sent to someone
     */
    public boolean hasReceivers() {
        return (receivers == null && !location.getWorld().getPlayers().isEmpty()) || (receivers != null && !receivers.isEmpty());
    }

    /**
     * Sends this particle to all players in the world. This is rather silly and you should likely not be doing this.
     *
     * Just be a logical person and use receivers by radius or collection.
     * @return a reference to this object.
     */
    public ParticleBuilder allPlayers() {
       this.receivers = null;
       return this;
    }

    /**
     * @param receivers List of players to receive this particle, or null for all players in the world
     * @return a reference to this object.
     */
    public ParticleBuilder receivers(@Nullable List<Player> receivers) {
        // Had to keep this as we first made API List<> and not Collection, but removing this may break plugins compiled on older jars
        // TODO: deprecate?
        this.receivers = receivers != null ? Lists.newArrayList(receivers) : null;
        return this;
    }

    /**
     * @param receivers List of players to receive this particle, or null for all players in the world
     * @return a reference to this object.
     */
    public ParticleBuilder receivers(@Nullable Collection<Player> receivers) {
        this.receivers = receivers != null ? Lists.newArrayList(receivers) : null;
        return this;
    }

    /**
     * @param receivers List of players to be receive this particle, or null for all players in the world
     * @return a reference to this object.
     */
    public ParticleBuilder receivers(Player... receivers) {
        this.receivers = receivers != null ? Lists.newArrayList(receivers) : null;
        return this;
    }

    /**
     * Selects all players within a cuboid selection around the particle location, within the specified bounding box.
     * If you want a more spherical check, see {@link #receivers(int, boolean)}
     *
     * @param radius amount to add on all axis
     * @return a reference to this object.
     */
    public ParticleBuilder receivers(int radius) {
        return receivers(radius, radius);
    }

    /**
     * Selects all players within the specified radius around the particle location.
     * If byDistance is false, behavior uses cuboid selection the same as {@link #receivers(int, int)}
     * If byDistance is true, radius is tested by distance in a spherical shape
     * @param radius  amount to add on each axis
     * @param byDistance true to use a spherical radius, false to use a cuboid
     * @return a reference to this object.
     */
    public ParticleBuilder receivers(int radius, boolean byDistance) {
        if (!byDistance) {
            return receivers(radius, radius, radius);
        } else {
            this.receivers = Lists.newArrayList();
            for (Player nearbyPlayer : location.getWorld().getNearbyPlayers(location, radius, radius, radius)) {
                Location loc = nearbyPlayer.getLocation();
                double x = NumberConversions.square(location.getX() - loc.getX());
                double y = NumberConversions.square(location.getY() - loc.getY());
                double z = NumberConversions.square(location.getZ() - loc.getZ());
                if (Math.sqrt(x + y + z) > radius) {
                    continue;
                }
                this.receivers.add(nearbyPlayer);
            }
            return this;
        }
    }

    /**
     * Selects all players within a cuboid selection around the particle location, within the specified bounding box.
     * Allows specifying a different Y size than X and Z
     * If you want a more cylinder check, see {@link #receivers(int, int, boolean)}
     * If you want a more spherical check, see {@link #receivers(int, boolean)}
     *
     * @param xzRadius amount to add on the x/z axis
     * @param yRadius amount to add on the y axis
     * @return a reference to this object.
     */
    public ParticleBuilder receivers(int xzRadius, int yRadius) {
        return receivers(xzRadius, yRadius, xzRadius);
    }

    /**
     * Selects all players within the specified radius around the particle location.
     * If byDistance is false, behavior uses cuboid selection the same as {@link #receivers(int, int)}
     * If byDistance is true, radius is tested by distance on the y plane and on the x/z plane, in a cylinder shape.
     * @param xzRadius amount to add on the x/z axis
     * @param yRadius amount to add on the y axis
     * @param byDistance true to use a cylinder shape, false to use cuboid
     * @return a reference to this object.
     */
    public ParticleBuilder receivers(int xzRadius, int yRadius, boolean byDistance) {
        if (!byDistance) {
            return receivers(xzRadius, yRadius, xzRadius);
        } else {
            this.receivers = Lists.newArrayList();
            for (Player nearbyPlayer : location.getWorld().getNearbyPlayers(location, xzRadius, yRadius, xzRadius)) {
                Location loc = nearbyPlayer.getLocation();
                if (Math.abs(loc.getY() - this.location.getY()) > yRadius) {
                    continue;
                }
                double x = NumberConversions.square(location.getX() - loc.getX());
                double z = NumberConversions.square(location.getZ() - loc.getZ());
                if (x + z > NumberConversions.square(xzRadius)) {
                    continue;
                }
                this.receivers.add(nearbyPlayer);
            }
            return this;
        }
    }

    /**
     * Selects all players within a cuboid selection around the particle location, within the specified bounding box.
     * If you want a more cylinder check, see {@link #receivers(int, int, boolean)}
     * If you want a more spherical check, see {@link #receivers(int, boolean)}
     *
     * @param xRadius amount to add on the x axis
     * @param yRadius amount to add on the y axis
     * @param zRadius amount to add on the z axis
     * @return a reference to this object.
     */
    public ParticleBuilder receivers(int xRadius, int yRadius, int zRadius) {
        if (location == null) {
            throw new IllegalStateException("Please set location first");
        }
        return receivers(location.getWorld().getNearbyPlayers(location, xRadius, yRadius, zRadius));
    }

    /**
     * @return The player considered the source of this particle (for Visibility concerns), or null
     */
    public Player source() {
        return source;
    }

    /**
     * Sets the source of this particle for visibility concerns (Vanish API)
     * @param source The player who is considered the source
     * @return a reference to this object.
     */
    public ParticleBuilder source(Player source) {
        this.source = source;
        return this;
    }

    /**
     * @return Location of where the particle will spawn
     */
    public Location location() {
        return location;
    }

    /**
     * Sets the location of where to spawn the particle
     * @param location The location of the particle
     * @return a reference to this object.
     */
    public ParticleBuilder location(Location location) {
        this.location = location.clone();
        return this;
    }

    /**
     * Sets the location of where to spawn the particle
     * @param world World to spawn particle in
     * @param x X location
     * @param y Y location
     * @param z Z location
     * @return a reference to this object.
     */
    public ParticleBuilder location(World world, double x, double y, double z) {
        this.location = new Location(world, x, y, z);
        return this;
    }

    /**
     * @return Number of particles to spawn
     */
    public int count() {
        return count;
    }

    /**
     * Sets the number of particles to spawn
     * @param count Number of particles
     * @return a reference to this object.
     */
    public ParticleBuilder count(int count) {
        this.count = count;
        return this;
    }

    /**
     * Particle offset X. Varies by particle on how this is used
     * @return Particle offset X.
     */
    public double offsetX() {
        return offsetX;
    }
    /**
     * Particle offset Y. Varies by particle on how this is used
     * @return Particle offset Y.
     */
    public double offsetY() {
        return offsetY;
    }
    /**
     * Particle offset Z. Varies by particle on how this is used
     * @return Particle offset Z.
     */
    public double offsetZ() {
        return offsetZ;
    }

    /**
     * Sets the particle offset. Varies by particle on how this is used
     * @param offsetX Particle offset X
     * @param offsetY Particle offset Y
     * @param offsetZ Particle offset Z
     * @return a reference to this object.
     */
    public ParticleBuilder offset(double offsetX, double offsetY, double offsetZ) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        return this;
    }

    /**
     * Gets the Particle extra data. Varies by particle on how this is used
     * @return the extra particle data
     */
    public double extra() {
        return extra;
    }

    /**
     * Sets the particle extra data. Varies by particle on how this is used
     * @param extra the extra particle data
     * @return a reference to this object.
     */
    public ParticleBuilder extra(double extra) {
        this.extra = extra;
        return this;
    }

    /**
     * Gets the particle custom data. Varies by particle on how this is used
     * @param <T> The Particle data type
     * @return the ParticleData for this particle
     */
    public <T> T data() {
        //noinspection unchecked
        return (T) data;
    }

    /**
     * Sets the particle custom data. Varies by particle on how this is used
     * @param data The new particle data
     * @param <T> The Particle data type
     * @return a reference to this object.
     */
    public <T> ParticleBuilder data(T data) {
        this.data = data;
        return this;
    }

    /**
     * Sets whether the particle is forcefully shown to the player.
     * If forced, the particle will show faraway, as far as the player's view distance allows.
     * If false, the particle will show according to the client's particle settings.
     *
     * @param force true to force, false for normal
     * @return a reference to this object.
     */
    public ParticleBuilder force(boolean force) {
        this.force = force;
        return this;
    }

    /**
     * Sets the particle Color.
     * Only valid for REDSTONE.
     * @param color the new particle color
     * @return a reference to this object.
     */
    public ParticleBuilder color(Color color) {
        return color(color, 1);
    }

    /**
     * Sets the particle Color and size.
     * Only valid for REDSTONE.
     * @param color the new particle color
     * @param size the size of the particle
     * @return a reference to this object.
     */
    public ParticleBuilder color(Color color, float size) {
        if (particle != Particle.REDSTONE) {
            throw new IllegalStateException("Color may only be set on REDSTONE");
        }
        return data(new Particle.DustOptions(color, size));
    }

    /**
     * Sets the particle Color.
     * Only valid for REDSTONE.
     * @param r red color component
     * @param g green color component
     * @param b blue color component
     * @return a reference to this object.
     */
    public ParticleBuilder color(int r, int g, int b) {
        return color(Color.fromRGB(r, g, b));
    }
}
