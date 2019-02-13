package org.bukkit.inventory;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Keyed;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.material.MaterialData;

/**
 * Represents a shapeless recipe, where the arrangement of the ingredients on
 * the crafting grid does not matter.
 */
public class ShapelessRecipe implements Recipe, Keyed {
    private final NamespacedKey key;
    private final ItemStack output;
    private final List<RecipeChoice> ingredients = new ArrayList<>();
    private String group = "";

    @Deprecated
    public ShapelessRecipe(ItemStack result) {
        this.key = NamespacedKey.randomKey();
        new Throwable("Warning: A plugin is creating a recipe using a Deprecated method. This will cause you to receive warnings stating 'Tried to load unrecognized recipe: bukkit:<ID>'. Please ask the author to give their recipe a static key using NamespacedKey.").printStackTrace();
        this.output = new ItemStack(result);
    }

    /**
     * Create a shapeless recipe to craft the specified ItemStack. The
     * constructor merely determines the result and type; to set the actual
     * recipe, you'll need to call the appropriate methods.
     *
     * @param key the unique recipe key
     * @param result The item you want the recipe to create.
     * @see ShapelessRecipe#addIngredient(Material)
     * @see ShapelessRecipe#addIngredient(MaterialData)
     * @see ShapelessRecipe#addIngredient(Material,int)
     * @see ShapelessRecipe#addIngredient(int,Material)
     * @see ShapelessRecipe#addIngredient(int,MaterialData)
     * @see ShapelessRecipe#addIngredient(int,Material,int)
     */
    public ShapelessRecipe(NamespacedKey key, ItemStack result) {
        this.key = key;
        this.output = new ItemStack(result);
    }

    /**
     * Adds the specified ingredient.
     *
     * @param ingredient The ingredient to add.
     * @return The changed recipe, so you can chain calls.
     */
    public ShapelessRecipe addIngredient(MaterialData ingredient) {
        return addIngredient(1, ingredient);
    }

    /**
     * Adds the specified ingredient.
     *
     * @param ingredient The ingredient to add.
     * @return The changed recipe, so you can chain calls.
     */
    public ShapelessRecipe addIngredient(Material ingredient) {
        return addIngredient(1, ingredient, 0);
    }

    /**
     * Adds the specified ingredient.
     *
     * @param ingredient The ingredient to add.
     * @param rawdata The data value, or -1 to allow any data value.
     * @return The changed recipe, so you can chain calls.
     * @deprecated Magic value
     */
    @Deprecated
    public ShapelessRecipe addIngredient(Material ingredient, int rawdata) {
        return addIngredient(1, ingredient, rawdata);
    }

    /**
     * Adds multiples of the specified ingredient.
     *
     * @param count How many to add (can't be more than 9!)
     * @param ingredient The ingredient to add.
     * @return The changed recipe, so you can chain calls.
     */
    public ShapelessRecipe addIngredient(int count, MaterialData ingredient) {
        return addIngredient(count, ingredient.getItemType(), ingredient.getData());
    }

    /**
     * Adds multiples of the specified ingredient.
     *
     * @param count How many to add (can't be more than 9!)
     * @param ingredient The ingredient to add.
     * @return The changed recipe, so you can chain calls.
     */
    public ShapelessRecipe addIngredient(int count, Material ingredient) {
        return addIngredient(count, ingredient, 0);
    }

    /**
     * Adds multiples of the specified ingredient.
     *
     * @param count How many to add (can't be more than 9!)
     * @param ingredient The ingredient to add.
     * @param rawdata The data value, or -1 to allow any data value.
     * @return The changed recipe, so you can chain calls.
     * @deprecated Magic value
     */
    @Deprecated
    public ShapelessRecipe addIngredient(int count, Material ingredient, int rawdata) {
        Validate.isTrue(ingredients.size() + count <= 9, "Shapeless recipes cannot have more than 9 ingredients");

        // -1 is the old wildcard, map to Short.MAX_VALUE as the new one
        if (rawdata == -1) {
            rawdata = Short.MAX_VALUE;
        }

        while (count-- > 0) {
            ingredients.add(new RecipeChoice.MaterialChoice(Collections.singletonList(ingredient)));
        }
        return this;
    }

    public ShapelessRecipe addIngredient(RecipeChoice ingredient) {
        Validate.isTrue(ingredients.size() + 1 <= 9, "Shapeless recipes cannot have more than 9 ingredients");

        ingredients.add(ingredient);
        return this;
    }

    /**
     * Removes an ingredient from the list.
     *
     * @param ingredient The ingredient to remove
     * @return The changed recipe.
     */
    public ShapelessRecipe removeIngredient(RecipeChoice ingredient) {
        ingredients.remove(ingredient);

        return this;
    }

    /**
     * Removes an ingredient from the list. If the ingredient occurs multiple
     * times, only one instance of it is removed. Only removes exact matches,
     * with a data value of 0.
     *
     * @param ingredient The ingredient to remove
     * @return The changed recipe.
     */
    public ShapelessRecipe removeIngredient(Material ingredient) {
        return removeIngredient(ingredient, 0);
    }

    /**
     * Removes an ingredient from the list. If the ingredient occurs multiple
     * times, only one instance of it is removed. If the data value is -1,
     * only ingredients with a -1 data value will be removed.
     *
     * @param ingredient The ingredient to remove
     * @return The changed recipe.
     */
    public ShapelessRecipe removeIngredient(MaterialData ingredient) {
        return removeIngredient(ingredient.getItemType(), ingredient.getData());
    }

    /**
     * Removes multiple instances of an ingredient from the list. If there are
     * less instances then specified, all will be removed. Only removes exact
     * matches, with a data value of 0.
     *
     * @param count The number of copies to remove.
     * @param ingredient The ingredient to remove
     * @return The changed recipe.
     */
    public ShapelessRecipe removeIngredient(int count, Material ingredient) {
        return removeIngredient(count, ingredient, 0);
    }

    /**
     * Removes multiple instances of an ingredient from the list. If there are
     * less instances then specified, all will be removed. If the data value
     * is -1, only ingredients with a -1 data value will be removed.
     *
     * @param count The number of copies to remove.
     * @param ingredient The ingredient to remove.
     * @return The changed recipe.
     */
    public ShapelessRecipe removeIngredient(int count, MaterialData ingredient) {
        return removeIngredient(count, ingredient.getItemType(), ingredient.getData());
    }

    /**
     * Removes an ingredient from the list. If the ingredient occurs multiple
     * times, only one instance of it is removed. If the data value is -1,
     * only ingredients with a -1 data value will be removed.
     *
     * @param ingredient The ingredient to remove
     * @param rawdata The data value;
     * @return The changed recipe.
     * @deprecated Magic value
     */
    @Deprecated
    public ShapelessRecipe removeIngredient(Material ingredient, int rawdata) {
        return removeIngredient(1, ingredient, rawdata);
    }

    /**
     * Removes multiple instances of an ingredient from the list. If there are
     * less instances then specified, all will be removed. If the data value
     * is -1, only ingredients with a -1 data value will be removed.
     *
     * @param count The number of copies to remove.
     * @param ingredient The ingredient to remove.
     * @param rawdata The data value.
     * @return The changed recipe.
     * @deprecated Magic value
     */
    @Deprecated
    public ShapelessRecipe removeIngredient(int count, Material ingredient, int rawdata) {
        Iterator<RecipeChoice> iterator = ingredients.iterator();
        while (count > 0 && iterator.hasNext()) {
            ItemStack stack = iterator.next().getItemStack();
            if (stack.getType() == ingredient && stack.getDurability() == rawdata) {
                iterator.remove();
                count--;
            }
        }
        return this;
    }

    /**
     * Get the result of this recipe.
     *
     * @return The result stack.
     */
    public ItemStack getResult() {
        return output.clone();
    }

    /**
     * Get the list of ingredients used for this recipe.
     *
     * @return The input list
     */
    public List<ItemStack> getIngredientList() {
        ArrayList<ItemStack> result = new ArrayList<ItemStack>(ingredients.size());
        for (RecipeChoice ingredient : ingredients) {
            result.add(ingredient.getItemStack().clone());
        }
        return result;
    }

    public List<RecipeChoice> getChoiceList() {
        List<RecipeChoice> result = new ArrayList<>(ingredients.size());
        for (RecipeChoice ingredient : ingredients) {
            result.add(ingredient.clone());
        }
        return result;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    /**
     * Get the group of this recipe. Recipes with the same group may be grouped
     * together when displayed in the client.
     *
     * @return recipe group. An empty string denotes no group. May not be null.
     */
    public String getGroup() {
        return group;
    }

    /**
     * Set the group of this recipe. Recipes with the same group may be grouped
     * together when displayed in the client.
     *
     * @param group recipe group. An empty string denotes no group. May not be
     * null.
     */
    public void setGroup(String group) {
        Preconditions.checkArgument(group != null, "group");
        this.group = group;
    }
}
