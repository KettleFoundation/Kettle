package org.bukkit.inventory.meta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multimap;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;

/**
 * This type represents the storage mechanism for auxiliary item data.
 * <p>
 * An implementation will handle the creation and application for ItemMeta.
 * This class should not be implemented by a plugin in a live environment.
 */
public interface ItemMeta extends Cloneable, ConfigurationSerializable {

    /**
     * Checks for existence of a display name.
     *
     * @return true if this has a display name
     */
    boolean hasDisplayName();

    /**
     * Gets the display name that is set.
     * <p>
     * Plugins should check that hasDisplayName() returns <code>true</code>
     * before calling this method.
     *
     * @return the display name that is set
     */
    String getDisplayName();

    /**
     * Sets the display name.
     *
     * @param name the name to set
     */
    void setDisplayName(String name);

    /**
     * Checks for existence of a localized name.
     *
     * @return true if this has a localized name
     */
    boolean hasLocalizedName();

    /**
     * Gets the localized display name that is set.
     * <p>
     * Plugins should check that hasLocalizedName() returns <code>true</code>
     * before calling this method.
     *
     * @return the localized name that is set
     */
    String getLocalizedName();

    /**
     * Sets the localized name.
     *
     * @param name the name to set
     */
    void setLocalizedName(String name);

    /**
     * Checks for existence of lore.
     *
     * @return true if this has lore
     */
    boolean hasLore();

    /**
     * Gets the lore that is set.
     * <p>
     * Plugins should check if hasLore() returns <code>true</code> before
     * calling this method.
     *
     * @return a list of lore that is set
     */
    List<String> getLore();

    /**
     * Sets the lore for this item.
     * Removes lore when given null.
     *
     * @param lore the lore that will be set
     */
    void setLore(List<String> lore);

    /**
     * Checks for the existence of any enchantments.
     *
     * @return true if an enchantment exists on this meta
     */
    boolean hasEnchants();

    /**
     * Checks for existence of the specified enchantment.
     *
     * @param ench enchantment to check
     * @return true if this enchantment exists for this meta
     */
    boolean hasEnchant(Enchantment ench);

    /**
     * Checks for the level of the specified enchantment.
     *
     * @param ench enchantment to check
     * @return The level that the specified enchantment has, or 0 if none
     */
    int getEnchantLevel(Enchantment ench);

    /**
     * Returns a copy the enchantments in this ItemMeta. <br>
     * Returns an empty map if none.
     *
     * @return An immutable copy of the enchantments
     */
    Map<Enchantment, Integer> getEnchants();

    /**
     * Adds the specified enchantment to this item meta.
     *
     * @param ench Enchantment to add
     * @param level Level for the enchantment
     * @param ignoreLevelRestriction this indicates the enchantment should be
     *     applied, ignoring the level limit
     * @return true if the item meta changed as a result of this call, false
     *     otherwise
     */
    boolean addEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction);

    /**
     * Removes the specified enchantment from this item meta.
     *
     * @param ench Enchantment to remove
     * @return true if the item meta changed as a result of this call, false
     *     otherwise
     */
    boolean removeEnchant(Enchantment ench);

    /**
     * Checks if the specified enchantment conflicts with any enchantments in
     * this ItemMeta.
     *
     * @param ench enchantment to test
     * @return true if the enchantment conflicts, false otherwise
     */
    boolean hasConflictingEnchant(Enchantment ench);

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client. This Method does silently ignore double set itemFlags.
     *
     * @param itemFlags The hideflags which shouldn't be rendered
     */
    void addItemFlags(ItemFlag... itemFlags);

    /**
     * Remove specific set of itemFlags. This tells the Client it should render it again. This Method does silently ignore double removed itemFlags.
     *
     * @param itemFlags Hideflags which should be removed
     */
    void removeItemFlags(ItemFlag... itemFlags);

    /**
     * Get current set itemFlags. The collection returned is unmodifiable.
     *
     * @return A set of all itemFlags set
     */
    Set<ItemFlag> getItemFlags();

    /**
     * Check if the specified flag is present on this item.
     *
     * @param flag the flag to check
     * @return if it is present
     */
    boolean hasItemFlag(ItemFlag flag);

    /**
     * Return if the unbreakable tag is true. An unbreakable item will not lose
     * durability.
     *
     * @return true if the unbreakable tag is true
     */
    boolean isUnbreakable();

    /**
     * Sets the unbreakable tag. An unbreakable item will not lose durability.
     *
     * @param unbreakable true if set unbreakable
     */
    void setUnbreakable(boolean unbreakable);

    /**
     * Checks for the existence of any AttributeModifiers.
     *
     * @return true if any AttributeModifiers exist
     */
    boolean hasAttributeModifiers();

    /**
     * Return an immutable copy of all Attributes and
     * their modifiers in this ItemMeta.<br>
     * Returns null if none exist.
     *
     * @return an immutable {@link Multimap} of Attributes
     *         and their AttributeModifiers, or null if none exist
     */
    Multimap<Attribute, AttributeModifier> getAttributeModifiers();

    /**
     * Return an immutable copy of all {@link Attribute}s and their
     * {@link AttributeModifier}s for a given {@link EquipmentSlot}.<br>
     * Any {@link AttributeModifier} that does have have a given
     * {@link EquipmentSlot} will be returned. This is because
     * AttributeModifiers without a slot are active in any slot.<br>
     * If there are no attributes set for the given slot, an empty map
     * will be returned.
     *
     * @param slot the {@link EquipmentSlot} to check
     * @return the immutable {@link Multimap} with the
     *         respective Attributes and modifiers, or an empty map
     *         if no attributes are set.
     */
    Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot);

    /**
     * Return an immutable copy of all {@link AttributeModifier}s
     * for a given {@link Attribute}
     *
     * @param attribute the {@link Attribute}
     * @return an immutable collection of {@link AttributeModifier}s
     *          or null if no AttributeModifiers exist for the Attribute.
     * @throws NullPointerException if Attribute is null
     */
    Collection<AttributeModifier> getAttributeModifiers(Attribute attribute);

    /**
     * Add an Attribute and it's Modifier.
     * AttributeModifiers can now support {@link EquipmentSlot}s.
     * If not set, the {@link AttributeModifier} will be active in ALL slots.
     * <br>
     * Two {@link AttributeModifier}s that have the same {@link java.util.UUID}
     * cannot exist on the same Attribute.
     *
     * @param attribute the {@link Attribute} to modify
     * @param modifier the {@link AttributeModifier} specifying the modification
     * @return true if the Attribute and AttributeModifier were
     *         successfully added
     * @throws NullPointerException if Attribute is null
     * @throws NullPointerException if AttributeModifier is null
     * @throws IllegalArgumentException if AttributeModifier already exists
     */
    boolean addAttributeModifier(Attribute attribute, AttributeModifier modifier);

    /**
     * Set all {@link Attribute}s and their {@link AttributeModifier}s.
     * To clear all currently set Attributes and AttributeModifiers use
     * null or an empty Multimap.
     * If not null nor empty, this will filter all entries that are not-null
     * and add them to the ItemStack.
     *
     * @param attributeModifiers the new Multimap containing the Attributes
     *                           and their AttributeModifiers
     */
    void setAttributeModifiers(Multimap<Attribute, AttributeModifier> attributeModifiers);

    /**
     * Remove all {@link AttributeModifier}s associated with the given
     * {@link Attribute}.
     * This will return false if nothing was removed.
     *
     * @param attribute attribute to remove
     * @return  true if all modifiers were removed from a given
     *                  Attribute. Returns false if no attributes were
     *                  removed.
     * @throws NullPointerException if Attribute is null
     */
    boolean removeAttributeModifier(Attribute attribute);

    /**
     * Remove all {@link Attribute}s and {@link AttributeModifier}s for a
     * given {@link EquipmentSlot}.<br>
     * If the given {@link EquipmentSlot} is null, this will remove all
     * {@link AttributeModifier}s that do not have an EquipmentSlot set.
     *
     * @param slot the {@link EquipmentSlot} to clear all Attributes and
     *             their modifiers for
     * @return true if all modifiers were removed that match the given
     *         EquipmentSlot.
     */
    boolean removeAttributeModifier(EquipmentSlot slot);

    /**
     * Remove a specific {@link Attribute} and {@link AttributeModifier}.
     * AttributeModifiers are matched according to their {@link java.util.UUID}.
     *
     * @param attribute the {@link Attribute} to remove
     * @param modifier the {@link AttributeModifier} to remove
     * @return if any attribute modifiers were remove
     *
     * @see AttributeModifier#getUniqueId()
     *
     * @throws NullPointerException if the Attribute is null
     * @throws NullPointerException if the AttributeModifier is null
     */
    boolean removeAttributeModifier(Attribute attribute, AttributeModifier modifier);

    /**
     * Returns a public custom tag container capable of storing tags on the
     * item.
     *
     * Those tags will be sent to the client with all of their content, so the
     * client is capable of reading them. This will result in the player seeing
     * a NBT Tag notification on the item.
     *
     * These tags can also be modified by the client once in creative mode
     *
     * @return the custom tag container
     */
    CustomItemTagContainer getCustomTagContainer();

    @SuppressWarnings("javadoc")
    ItemMeta clone();

    // Spigot start
    public class Spigot
    {

        /**
         * Sets the unbreakable tag
         *
         * @param unbreakable true if set unbreakable
         * @deprecated see {@link ItemMeta#setUnbreakable(boolean)}
         */
        @Deprecated
        public void setUnbreakable(boolean unbreakable)
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        /**
         * Return if the unbreakable tag is true
         *
         * @return true if the unbreakable tag is true
         * @deprecated see {@link ItemMeta#isUnbreakable()}
         */
        @Deprecated
        public boolean isUnbreakable()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }

    Spigot spigot();
    // Spigot end
    // Paper start - Add an API for CanPlaceOn and CanDestroy NBT values
    /**
     * Gets set of materials what given item can destroy in {@link org.bukkit.GameMode#ADVENTURE}
     *
     * @return Set of materials
     * @deprecated Minecraft does not limit this to the material enum, Use {@link #getDestroyableKeys()} as a replacement
     */
    @Deprecated
    Set<org.bukkit.Material> getCanDestroy();

    /**
     * Sets set of materials what given item can destroy in {@link org.bukkit.GameMode#ADVENTURE}
     *
     * @param canDestroy Set of materials
     * @deprecated Minecraft does not limit this to the material enum, Use {@link #setDestroyableKeys(Collection)} as a replacement
     */
    @Deprecated
    void setCanDestroy(Set<org.bukkit.Material> canDestroy);

    /**
     * Gets set of materials where given item can be placed on in {@link org.bukkit.GameMode#ADVENTURE}
     *
     * @return Set of materials
     * @deprecated Minecraft does not limit this to the material enum, Use {@link #getPlaceableKeys()} as a replacement
     */
    @Deprecated
    Set<org.bukkit.Material> getCanPlaceOn();

    /**
     * Sets set of materials where given item can be placed on in {@link org.bukkit.GameMode#ADVENTURE}
     *
     * @param canPlaceOn Set of materials
     * @deprecated Minecraft does not limit this to the material enum, Use {@link #setPlaceableKeys(Collection)} as a replacement
     */
    @Deprecated
    void setCanPlaceOn(Set<org.bukkit.Material> canPlaceOn);

    /**
     * Gets the collection of namespaced keys that the item can destroy in {@link org.bukkit.GameMode#ADVENTURE}
     *
     * @return Set of {@link com.destroystokyo.paper.Namespaced}
     */
    Set<com.destroystokyo.paper.Namespaced> getDestroyableKeys();

    /**
     * Sets the collection of namespaced keys that the item can destroy in {@link org.bukkit.GameMode#ADVENTURE}
     *
     * @param canDestroy Collection of {@link com.destroystokyo.paper.Namespaced}
     */
    void setDestroyableKeys(Collection<com.destroystokyo.paper.Namespaced> canDestroy);

    /**
     * Gets the collection of namespaced keys that the item can be placed on in {@link org.bukkit.GameMode#ADVENTURE}
     *
     * @return Set of {@link com.destroystokyo.paper.Namespaced}
     */
    Set<com.destroystokyo.paper.Namespaced> getPlaceableKeys();

    /**
     * Sets the set of namespaced keys that the item can be placed on in {@link org.bukkit.GameMode#ADVENTURE}
     *
     * @param canPlaceOn Collection of {@link com.destroystokyo.paper.Namespaced}
     */
    void setPlaceableKeys(Collection<com.destroystokyo.paper.Namespaced> canPlaceOn);

    /**
     * Checks for the existence of any keys that the item can be placed on
     *
     * @return true if this item has placeable keys
     */
    boolean hasPlaceableKeys();

    /**
     * Checks for the existence of any keys that the item can destroy
     *
     * @return true if this item has destroyable keys
     */
    boolean hasDestroyableKeys();
    // Paper end
}
