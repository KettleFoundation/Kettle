package org.bukkit.entity;

/**
 * Represents a Creeper
 */
public interface Creeper extends Monster {

    /**
     * Checks if this Creeper is powered (Electrocuted)
     *
     * @return true if this creeper is powered
     */
    public boolean isPowered();

    /**
     * Sets the Powered status of this Creeper
     *
     * @param value New Powered status
     */
    public void setPowered(boolean value);

    /**
     * Set the maximum fuse ticks for this Creeper, where the maximum ticks 
     * is the amount of time in which a creeper is allowed to be in the 
     * primed state before exploding.
     *
     * @param ticks the new maximum fuse ticks
     */
    public void setMaxFuseTicks(int ticks);

    /**
     * Get the maximum fuse ticks for this Creeper, where the maximum ticks 
     * is the amount of time in which a creeper is allowed to be in the 
     * primed state before exploding.
     *
     * @return the maximum fuse ticks
     */
    public int getMaxFuseTicks();

    /**
     * Set the explosion radius in which this Creeper's explosion will affect.
     *
     * @param radius the new explosion radius
     */
    public void setExplosionRadius(int radius);

    /**
     * Get the explosion radius in which this Creeper's explosion will affect.
     *
     * @return the explosion radius
     */
    public int getExplosionRadius();

    // Paper start
    /**
     * Set whether creeper is ignited or not (armed to explode)
     *
     * @param ignited New ignited state
     */
    public void setIgnited(boolean ignited);

    /**
     * Check if creeper is ignited or not (armed to explode)
     *
     * @return Ignited state
     */
    public boolean isIgnited();

    /**
     * Get the number of ticks this creeper has been ignited (armed to explode)
     *
     * @return Ticks creeper has been ignited
     */
    public int getFuseTicks();

    /**
     * Make the creeper explode (no waiting for fuse)
     */
    public void explode();
    // Paper end
}
