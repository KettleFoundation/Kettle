package org.bukkit.inventory.meta;

import org.bukkit.DyeColor;
import org.bukkit.entity.TropicalFish;

/**
 * Represents a bucket of tropical fish.
 */
public interface TropicalFishBucketMeta extends ItemMeta {

    /**
     * Gets the color of the fish's pattern.
     * <p>
     * Plugins should check that hasVariant() returns <code>true</code> before
     * calling this method.
     *
     * @return pattern color
     */
    DyeColor getPatternColor();

    /**
     * Sets the color of the fish's pattern.
     * <p>
     * Setting this when hasVariant() returns <code>false</code> will initialize
     * all other values to unspecified defaults.
     *
     * @param color pattern color
     */
    void setPatternColor(DyeColor color);

    /**
     * Gets the color of the fish's body.
     * <p>
     * Plugins should check that hasVariant() returns <code>true</code> before
     * calling this method.
     *
     * @return pattern color
     */
    DyeColor getBodyColor();

    /**
     * Sets the color of the fish's body.
     * <p>
     * Setting this when hasVariant() returns <code>false</code> will initialize
     * all other values to unspecified defaults.
     *
     * @param color body color
     */
    void setBodyColor(DyeColor color);

    /**
     * Gets the fish's pattern.
     * <p>
     * Plugins should check that hasVariant() returns <code>true</code> before
     * calling this method.
     *
     * @return pattern
     */
    TropicalFish.Pattern getPattern();

    /**
     * Sets the fish's pattern.
     * <p>
     * Setting this when hasVariant() returns <code>false</code> will initialize
     * all other values to unspecified defaults.
     *
     * @param pattern new pattern
     */
    void setPattern(TropicalFish.Pattern pattern);

    /**
     * Checks for existence of a variant tag indicating a specific fish will be
     * spawned.
     *
     * @return if there is a variant
     */
    boolean hasVariant();

    TropicalFishBucketMeta clone();
}
