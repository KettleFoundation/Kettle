package org.bukkit.entity;

/**
 * Represents a phantom.
 */
public interface Phantom extends Flying {

    /**
     * @return The size of the phantom
     */
    public int getSize();

    /**
     * @param sz The new size of the phantom.
     */
    public void setSize(int sz);

    // Paper start
    /**
     * Get the UUID of the entity that caused this phantom to spawn
     *
     * @return UUID
     */
    public java.util.UUID getSpawningEntity();
    // Paper end
}
