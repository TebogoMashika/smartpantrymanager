package com.school.smartpantrymanager.model;

/**
 * Represents an ingredient required by a recipe.
 */
public class RecipeIngredient {

    private int id;
    private int recipeId;
    private String ingredientName;
    private double requiredQuantity;
    private String unit;

    /**
     * Creates a recipe ingredient without a database ID.
     *
     * @param recipeId recipe ID
     * @param ingredientName ingredient name
     * @param requiredQuantity required quantity
     * @param unit measurement unit
     */
    public RecipeIngredient(
            int recipeId,
            String ingredientName,
            double requiredQuantity,
            String unit) {

        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }

    /**
     * Creates a recipe ingredient with a database ID.
     *
     * @param id database ID
     * @param recipeId recipe ID
     * @param ingredientName ingredient name
     * @param requiredQuantity required quantity
     * @param unit measurement unit
     */
    public RecipeIngredient(
            int id,
            int recipeId,
            String ingredientName,
            double requiredQuantity,
            String unit) {

        this.id = id;
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getRequiredQuantity() {
        return requiredQuantity;
    }

    public void setRequiredQuantity(double requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}