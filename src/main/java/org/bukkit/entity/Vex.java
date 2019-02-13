package org.bukkit.entity;

/**
 * Represents a Vex.
 */
public interface Vex extends Monster {
    /**
     * @return What Entity (most likely an Evoker, but not guaranteed) summoned this Vex
     */
    // Paper start
    /**
     * Get the Mob that summoned this vex
     *
     * @return Mob that summoned this vex
     */
    Mob getSummoner();

    /**
     * Set the summoner of this vex
     *
     * @param summoner New summoner
     */
    void setSummoner(Mob summoner);
    // Paper end
}
