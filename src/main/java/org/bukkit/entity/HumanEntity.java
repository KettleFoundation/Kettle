package org.bukkit.entity;

import java.util.Collection;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Represents a human entity, such as an NPC or a player
 */
public interface HumanEntity extends LivingEntity, AnimalTamer, InventoryHolder {

    /**
     * Returns the name of this player
     *
     * @return Player name
     */
    public String getName();

    /**
     * Get the player's inventory.
     *
     * @return The inventory of the player, this also contains the armor
     *     slots.
     */
    public PlayerInventory getInventory();

    /**
     * Get the player's EnderChest inventory
     *
     * @return The EnderChest of the player
     */
    public Inventory getEnderChest();

    /**
     * Gets the player's selected main hand
     *
     * @return the players main hand
     */
    public MainHand getMainHand();

    /**
     * If the player currently has an inventory window open, this method will
     * set a property of that window, such as the state of a progress bar.
     *
     * @param prop The property.
     * @param value The value to set the property to.
     * @return True if the property was successfully set.
     */
    public boolean setWindowProperty(InventoryView.Property prop, int value);

    /**
     * Gets the inventory view the player is currently viewing. If they do not
     * have an inventory window open, it returns their internal crafting view.
     *
     * @return The inventory view.
     */
    public InventoryView getOpenInventory();

    /**
     * Opens an inventory window with the specified inventory on the top and
     * the player's inventory on the bottom.
     *
     * @param inventory The inventory to open
     * @return The newly opened inventory view
     */
    public InventoryView openInventory(Inventory inventory);

    /**
     * Opens an empty workbench inventory window with the player's inventory
     * on the bottom.
     *
     * @param location The location to attach it to. If null, the player's
     *     location is used.
     * @param force If false, and there is no workbench block at the location,
     *     no inventory will be opened and null will be returned.
     * @return The newly opened inventory view, or null if it could not be
     *     opened.
     */
    public InventoryView openWorkbench(Location location, boolean force);

    /**
     * Opens an empty enchanting inventory window with the player's inventory
     * on the bottom.
     *
     * @param location The location to attach it to. If null, the player's
     *     location is used.
     * @param force If false, and there is no enchanting table at the
     *     location, no inventory will be opened and null will be returned.
     * @return The newly opened inventory view, or null if it could not be
     *     opened.
     */
    public InventoryView openEnchanting(Location location, boolean force);

    /**
     * Opens an inventory window to the specified inventory view.
     *
     * @param inventory The view to open
     */
    public void openInventory(InventoryView inventory);

    /**
     * Starts a trade between the player and the villager.
     *
     * Note that only one player may trade with a villager at once. You must use
     * the force parameter for this.
     *
     * @param trader The merchant to trade with. Cannot be null.
     * @param force whether to force the trade even if another player is trading
     * @return The newly opened inventory view, or null if it could not be
     * opened.
     */
    public InventoryView openMerchant(Villager trader, boolean force);

    /**
     * Starts a trade between the player and the merchant.
     *
     * Note that only one player may trade with a merchant at once. You must use
     * the force parameter for this.
     *
     * @param merchant The merchant to trade with. Cannot be null.
     * @param force whether to force the trade even if another player is trading
     * @return The newly opened inventory view, or null if it could not be
     * opened.
     */
    public InventoryView openMerchant(Merchant merchant, boolean force);

    /**
     * Force-closes the currently open inventory view for this player, if any.
     */
    public void closeInventory();

    // Paper start
    /**
     * Force-closes the currently open inventory view for this player, if any.
     *
     * @param reason why the inventory is closing
     */
    public void closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason reason);
    // Paper end

    /**
     * Returns the ItemStack currently in your hand, can be empty.
     *
     * @return The ItemStack of the item you are currently holding.
     * @deprecated Humans may now dual wield in their off hand, use explicit
     * methods in {@link PlayerInventory}.
     */
    @Deprecated
    public ItemStack getItemInHand();

    /**
     * Sets the item to the given ItemStack, this will replace whatever the
     * user was holding.
     *
     * @param item The ItemStack which will end up in the hand
     * @deprecated Humans may now dual wield in their off hand, use explicit
     * methods in {@link PlayerInventory}.
     */
    @Deprecated
    public void setItemInHand(ItemStack item);

    /**
     * Returns the ItemStack currently on your cursor, can be empty. Will
     * always be empty if the player currently has no open window.
     *
     * @return The ItemStack of the item you are currently moving around.
     */
    public ItemStack getItemOnCursor();

    /**
     * Sets the item to the given ItemStack, this will replace whatever the
     * user was moving. Will always be empty if the player currently has no
     * open window.
     *
     * @param item The ItemStack which will end up in the hand
     */
    public void setItemOnCursor(ItemStack item);

    /**
     * Check whether a cooldown is active on the specified material.
     *
     * @param material the material to check
     * @return if a cooldown is active on the material
     */
    public boolean hasCooldown(Material material);

    /**
     * Get the cooldown time in ticks remaining for the specified material.
     *
     * @param material the material to check
     * @return the remaining cooldown time in ticks
     */
    public int getCooldown(Material material);

    /**
     * Set a cooldown on the specified material for a certain amount of ticks.
     * ticks. 0 ticks will result in the removal of the cooldown.
     * <p>
     * Cooldowns are used by the server for items such as ender pearls and
     * shields to prevent them from being used repeatedly.
     * <p>
     * Note that cooldowns will not by themselves stop an item from being used
     * for attacking.
     *
     * @param material the material to set the cooldown for
     * @param ticks the amount of ticks to set or 0 to remove
     */
    public void setCooldown(Material material, int ticks);

    /**
     * Returns whether this player is slumbering.
     *
     * @return slumber state
     */
    public boolean isSleeping();

    /**
     * Get the sleep ticks of the player. This value may be capped.
     *
     * @return slumber ticks
     */
    public int getSleepTicks();

    /**
     * Gets the Location where the player will spawn at their bed, null if
     * they have not slept in one or their current bed spawn is invalid.
     *
     * @return Bed Spawn Location if bed exists, otherwise null.
     */
    public Location getBedSpawnLocation();

    /**
     * Sets the Location where the player will spawn at their bed.
     *
     * @param location where to set the respawn location
     */
    public void setBedSpawnLocation(Location location);

    /**
     * Sets the Location where the player will spawn at their bed.
     *
     * @param location where to set the respawn location
     * @param force whether to forcefully set the respawn location even if a
     *     valid bed is not present
     */
    public void setBedSpawnLocation(Location location, boolean force);

    /**
     * Attempts to make the entity sleep at the given location.
     * <br>
     * The location must be in the current world and have a bed placed at the
     * location. The game may also enforce other requirements such as proximity
     * to bed, monsters, and dimension type if force is not set.
     *
     * @param location the location of the bed
     * @param force whether to try and sleep at the location even if not
     * normally possible
     * @return whether the sleep was successful
     */
    public boolean sleep(Location location, boolean force);

    /**
     * Causes the player to wakeup if they are currently sleeping.
     *
     * @param setSpawnLocation whether to set their spawn location to the bed
     * they are currently sleeping in
     * @throws IllegalStateException if not sleeping
     */
    public void wakeup(boolean setSpawnLocation);

    /**
     * Gets the location of the bed the player is currently sleeping in
     *
     * @return location
     * @throws IllegalStateException if not sleeping
     */
    public Location getBedLocation();

    /**
     * Gets this human's current {@link GameMode}
     *
     * @return Current game mode
     */
    public GameMode getGameMode();

    /**
     * Sets this human's current {@link GameMode}
     *
     * @param mode New game mode
     */
    public void setGameMode(GameMode mode);

    /**
     * Check if the player is currently blocking (ie with a shield).
     *
     * @return Whether they are blocking.
     */
    public boolean isBlocking();

    /**
     * Check if the player currently has their hand raised (ie about to begin
     * blocking).
     *
     * @return Whether their hand is raised
     */
    public boolean isHandRaised();

    /**
     * Get the total amount of experience required for the player to level
     *
     * @return Experience required to level up
     */
    public int getExpToLevel();

    // Paper start
    /**
     * If there is an Entity on this entities left shoulder, it will be released to the world and returned.
     * If no Entity is released, null will be returned.
     *
     * @return The released entity, or null
     */
    public Entity releaseLeftShoulderEntity();

    /**
     * If there is an Entity on this entities left shoulder, it will be released to the world and returned.
     * If no Entity is released, null will be returned.
     *
     * @return The released entity, or null
     */
    public Entity releaseRightShoulderEntity();
    // Paper end

    /**
     * Discover a recipe for this player such that it has not already been
     * discovered. This method will add the key's associated recipe to the
     * player's recipe book.
     *
     * @param recipe the key of the recipe to discover
     *
     * @return whether or not the recipe was newly discovered
     */
    public boolean discoverRecipe(NamespacedKey recipe);

    /**
     * Discover a collection of recipes for this player such that they have not
     * already been discovered. This method will add the keys' associated
     * recipes to the player's recipe book. If a recipe in the provided
     * collection has already been discovered, it will be silently ignored.
     *
     * @param recipes the keys of the recipes to discover
     *
     * @return the amount of newly discovered recipes where 0 indicates that
     * none were newly discovered and a number equal to {@code recipes.size()}
     * indicates that all were new
     */
    public int discoverRecipes(Collection<NamespacedKey> recipes);

    /**
     * Undiscover a recipe for this player such that it has already been
     * discovered. This method will remove the key's associated recipe from the
     * player's recipe book.
     *
     * @param recipe the key of the recipe to undiscover
     *
     * @return whether or not the recipe was successfully undiscovered (i.e. it
     * was previously discovered)
     */
    public boolean undiscoverRecipe(NamespacedKey recipe);

    /**
     * Undiscover a collection of recipes for this player such that they have
     * already been discovered. This method will remove the keys' associated
     * recipes from the player's recipe book. If a recipe in the provided
     * collection has not yet been discovered, it will be silently ignored.
     *
     * @param recipes the keys of the recipes to undiscover
     *
     * @return the amount of undiscovered recipes where 0 indicates that none
     * were undiscovered and a number equal to {@code recipes.size()} indicates
     * that all were undiscovered
     */
    public int undiscoverRecipes(Collection<NamespacedKey> recipes);

    /**
     * Gets the entity currently perched on the left shoulder or null if no
     * entity.
     * <br>
     * The returned entity will not be spawned within the world, so most
     * operations are invalid unless the entity is first spawned in.
     *
     * @return left shoulder entity
     * @deprecated There are currently no well defined semantics regarding
     * serialized entities in Bukkit. Use with care.
     */
    @Deprecated
    public Entity getShoulderEntityLeft();

    /**
     * Sets the entity currently perched on the left shoulder, or null to
     * remove. This method will remove the entity from the world.
     * <br>
     * Note that only a copy of the entity will be set to display on the
     * shoulder.
     * <br>
     * Also note that the client will currently only render {@link Parrot}
     * entities.
     *
     * @param entity left shoulder entity
     * @deprecated There are currently no well defined semantics regarding
     * serialized entities in Bukkit. Use with care.
     */
    @Deprecated
    public void setShoulderEntityLeft(Entity entity);

    /**
     * Gets the entity currently perched on the right shoulder or null if no
     * entity.
     * <br>
     * The returned entity will not be spawned within the world, so most
     * operations are invalid unless the entity is first spawned in.
     *
     * @return right shoulder entity
     * @deprecated There are currently no well defined semantics regarding
     * serialized entities in Bukkit. Use with care.
     */
    @Deprecated
    public Entity getShoulderEntityRight();

    /**
     * Sets the entity currently perched on the right shoulder, or null to
     * remove. This method will remove the entity from the world.
     * <br>
     * Note that only a copy of the entity will be set to display on the
     * shoulder.
     * <br>
     * Also note that the client will currently only render {@link Parrot}
     * entities.
     *
     * @param entity right shoulder entity
     * @deprecated There are currently no well defined semantics regarding
     * serialized entities in Bukkit. Use with care.
     */
    @Deprecated
    public void setShoulderEntityRight(Entity entity);

    // Paper start - Add method to open already placed sign
    /**
     * Opens an editor window for the specified sign
     *
     * @param sign The sign to open
     */
    void openSign(org.bukkit.block.Sign sign);
    // Paper end
}
