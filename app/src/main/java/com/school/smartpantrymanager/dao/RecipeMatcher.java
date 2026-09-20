package com.school.smartpantrymanager.dao;

import com.school.smartpantrymanager.model.PantryItem;
import com.school.smartpantrymanager.model.Recipe;
import com.school.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Matches recipes against the ingredients currently available in the pantry.
 *
 * A recipe is a strict match only when every required ingredient is present
 * in sufficient quantity.
 */
public class RecipeMatcher {

    /**
     * Finds recipes that can be completely prepared using the pantry contents.
     *
     * @param pantryItems ingredients currently stored in the pantry
     * @param recipes recipes to check
     * @return recipes for which every required ingredient is available
     */
    public List<Recipe> findMatchingRecipes(List<PantryItem> pantryItems, List<Recipe> recipes) {
        List<Recipe> matchingRecipes = new ArrayList<>();
        Map<String, PantryQuantity> pantryQuantityMap = createPantryQuantityMap(pantryItems);

        for (Recipe recipe : recipes) {
            if (canPrepareRecipe(recipe, pantryQuantityMap)) {
                matchingRecipes.add(recipe);
            }
        }

        return matchingRecipes;
    }

    /**
     * Determines whether every ingredient required by a recipe is available
     * in sufficient quantity.
     *
     * @param recipe recipe to check
     * @param pantryQuantityMap normalized pantry quantities
     * @return true only when the complete recipe can be prepared
     */
    private boolean canPrepareRecipe(Recipe recipe, Map<String, PantryQuantity> pantryQuantityMap) {
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            String ingredientName = normalizeName(ingredient.getIngredientName());

            if (!pantryQuantityMap.containsKey(ingredientName)) {
                return false;
            }

            PantryQuantity pantryQuantity = pantryQuantityMap.get(ingredientName);

            if (!hasEnoughQuantity(pantryQuantity, ingredient.getRequiredQuantity(), ingredient.getUnit())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a normalized map of pantry ingredients and their quantities.
     *
     * @param pantryItems pantry ingredients
     * @return normalized pantry ingredient quantities
     */
    private Map<String, PantryQuantity> createPantryQuantityMap(List<PantryItem> pantryItems) {
        Map<String, PantryQuantity> pantryQuantityMap = new HashMap<>();

        for (PantryItem pantryItem : pantryItems) {
            String ingredientName = normalizeName(pantryItem.getName());
            String unit = normalizeUnit(pantryItem.getUnit());

            if (!isSupportedUnit(unit)) {
                pantryQuantityMap.putIfAbsent(ingredientName, new PantryQuantity(pantryItem.getQuantity(), unit));
                continue;
            }

            double quantity = convertToBaseUnit(pantryItem.getQuantity(), unit);
            String baseUnit = getBaseUnit(unit);

            if (pantryQuantityMap.containsKey(ingredientName)) {
                PantryQuantity existingQuantity = pantryQuantityMap.get(ingredientName);

                if (existingQuantity.getUnit().equals(baseUnit)) {
                    quantity += existingQuantity.getQuantity();
                }
            }

            pantryQuantityMap.put(ingredientName, new PantryQuantity(quantity, baseUnit));
        }

        return pantryQuantityMap;
    }

    /**
     * Checks whether the pantry contains enough of an ingredient.
     *
     * @param pantryQuantity available pantry quantity
     * @param requiredQuantity required recipe quantity
     * @param requiredUnit required recipe unit
     * @return true when sufficient quantity is available
     */
    private boolean hasEnoughQuantity(PantryQuantity pantryQuantity, double requiredQuantity, String requiredUnit) {
        String normalizedRequiredUnit = normalizeUnit(requiredUnit);

        if (!canConvertUnits(pantryQuantity.getUnit(), normalizedRequiredUnit)) {
            return false;
        }

        double requiredBaseQuantity = convertToBaseUnit(requiredQuantity, normalizedRequiredUnit);

        return pantryQuantity.getQuantity() >= requiredBaseQuantity;
    }

    /**
     * Normalizes ingredient names to handle simple differences such as
     * uppercase letters, extra spaces and singular/plural forms.
     *
     * @param name ingredient name
     * @return normalized ingredient name
     */
    private String normalizeName(String name) {
        if (name == null) {
            return "";
        }

        String normalizedName = name.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
        normalizedName = normalizedName.replaceAll("[^a-z0-9 ]", "");

        if (normalizedName.endsWith("ies") && normalizedName.length() > 3) {
            normalizedName = normalizedName.substring(0, normalizedName.length() - 3) + "y";
        } else if (normalizedName.endsWith("oes") && normalizedName.length() > 3) {
            normalizedName = normalizedName.substring(0, normalizedName.length() - 2);
        } else if (normalizedName.endsWith("ses") && normalizedName.length() > 3) {
            normalizedName = normalizedName.substring(0, normalizedName.length() - 2);
        } else if (normalizedName.endsWith("xes") && normalizedName.length() > 3) {
            normalizedName = normalizedName.substring(0, normalizedName.length() - 2);
        } else if (normalizedName.endsWith("ches") && normalizedName.length() > 4) {
            normalizedName = normalizedName.substring(0, normalizedName.length() - 2);
        } else if (normalizedName.endsWith("shes") && normalizedName.length() > 4) {
            normalizedName = normalizedName.substring(0, normalizedName.length() - 2);
        } else if (normalizedName.endsWith("s") && !normalizedName.endsWith("ss") && normalizedName.length() > 2) {
            normalizedName = normalizedName.substring(0, normalizedName.length() - 1);
        }

        return normalizedName;
    }

    /**
     * Normalizes common measurement units.
     *
     * @param unit unit entered by the user
     * @return normalized unit
     */
    private String normalizeUnit(String unit) {
        if (unit == null) {
            return "";
        }

        String normalizedUnit = unit.toLowerCase(Locale.ROOT).trim();

        switch (normalizedUnit) {
            case "kilogram":
            case "kilograms":
            case "kg":
                return "kg";
            case "gram":
            case "grams":
            case "g":
                return "g";
            case "liter":
            case "liters":
            case "litre":
            case "litres":
            case "l":
                return "l";
            case "milliliter":
            case "milliliters":
            case "millilitre":
            case "millilitres":
            case "ml":
                return "ml";
            case "tablespoon":
            case "tablespoons":
            case "tbsp":
            case "tbs":
                return "tbsp";
            case "teaspoon":
            case "teaspoons":
            case "tsp":
                return "tsp";
            case "cup":
            case "cups":
                return "cup";
            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
            case "item":
            case "items":
                return "piece";
            default:
                return normalizedUnit;
        }
    }

    /**
     * Converts supported units into their base units.
     *
     * @param quantity quantity to convert
     * @param unit normalized unit
     * @return converted quantity
     */
    private double convertToBaseUnit(double quantity, String unit) {
        switch (unit) {
            case "kg":
                return quantity * 1000;
            case "g":
                return quantity;
            case "l":
                return quantity * 1000;
            case "ml":
                return quantity;
            case "tbsp":
                return quantity * 3;
            case "tsp":
                return quantity;
            case "cup":
                return quantity * 48;
            case "piece":
                return quantity;
            default:
                return quantity;
        }
    }

    /**
     * Returns the base unit used for comparison.
     *
     * @param unit normalized unit
     * @return base unit
     */
    private String getBaseUnit(String unit) {
        switch (unit) {
            case "kg":
            case "g":
                return "g";
            case "l":
            case "ml":
                return "ml";
            case "tbsp":
            case "tsp":
            case "cup":
                return "tsp";
            case "piece":
                return "piece";
            default:
                return unit;
        }
    }

    /**
     * Determines whether two units can be converted to each other.
     *
     * @param firstUnit first unit
     * @param secondUnit second unit
     * @return true when the units belong to the same measurement group
     */
    private boolean canConvertUnits(String firstUnit, String secondUnit) {
        return getBaseUnit(firstUnit).equals(getBaseUnit(secondUnit));
    }

    /**
     * Determines whether a unit is supported by the matcher.
     *
     * @param unit normalized unit
     * @return true when the unit is supported
     */
    private boolean isSupportedUnit(String unit) {
        return unit.equals("kg") || unit.equals("g") || unit.equals("l") || unit.equals("ml") ||
                unit.equals("tbsp") || unit.equals("tsp") || unit.equals("cup") || unit.equals("piece");
    }

    /**
     * Stores a normalized pantry quantity and its base unit.
     */
    private static class PantryQuantity {

        private final double quantity;
        private final String unit;

        /**
         * Creates a pantry quantity.
         *
         * @param quantity quantity in the base unit
         * @param unit base unit
         */
        public PantryQuantity(double quantity, String unit) {
            this.quantity = quantity;
            this.unit = unit;
        }

        /**
         * Returns the available quantity.
         *
         * @return quantity
         */
        public double getQuantity() {
            return quantity;
        }

        /**
         * Returns the base unit.
         *
         * @return base unit
         */
        public String getUnit() {
            return unit;
        }
    }
}