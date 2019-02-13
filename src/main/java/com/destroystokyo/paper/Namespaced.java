package com.destroystokyo.paper;

/**
 * Represents a namespaced resource, see {@link org.bukkit.NamespacedKey} for single elements
 * or {@link com.destroystokyo.paper.NamespacedTag} for a collection of elements
 *
 * Namespaces may only contain lowercase alphanumeric characters, periods,
 * underscores, and hyphens.
 * <p>
 * Keys may only contain lowercase alphanumeric characters, periods,
 * underscores, hyphens, and forward slashes.
 * <p>
 * You should not be implementing this interface yourself, use {@link org.bukkit.NamespacedKey}
 * or {@link com.destroystokyo.paper.NamespacedTag} as needed instead.
 */
public interface Namespaced {
    /**
     * Gets the namespace this resource is a part of
     * <p>
     * This is contractually obligated to only contain lowercase alphanumeric characters,
     * periods, underscores, and hyphens.
     *
     * @return resource namespace
     */
    String getNamespace();

    /**
     * Gets the key corresponding to this resource
     * <p>
     * This is contractually obligated to only contain lowercase alphanumeric characters,
     * periods, underscores, hyphens, and forward slashes.
     *
     * @return resource key
     */
    String getKey();
}
