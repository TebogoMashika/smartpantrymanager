package com.school.smartpantrymanager.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.school.smartpantrymanager.database.DatabaseHelper;
import com.school.smartpantrymanager.model.Recipe;
import com.school.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for recipes and recipe ingredients.
 */
public class RecipeDAO {

    private final DatabaseHelper databaseHelper;

    public RecipeDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    /**
     * Adds a recipe.
     *
     * @param name recipe name
     * @param instructions recipe instructions
     * @return ID of the newly created recipe
     */
    public long addRecipe(String name, String instructions) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECIPE_NAME, name);
        values.put(DatabaseHelper.COLUMN_RECIPE_INSTRUCTIONS, instructions);

        return database.insert(
                DatabaseHelper.TABLE_RECIPES,
                null,
                values
        );
    }

    /**
     * Adds an ingredient to a recipe.
     *
     * @param recipeId recipe ID
     * @param ingredientName ingredient name
     * @param quantity required quantity
     * @param unit ingredient unit
     * @return ID of the newly added ingredient
     */
    public long addIngredient(long recipeId, String ingredientName, double quantity, String unit) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_RECIPE_INGREDIENT_RECIPE_ID, recipeId);
        values.put(DatabaseHelper.COLUMN_RECIPE_INGREDIENT_NAME, ingredientName);
        values.put(DatabaseHelper.COLUMN_RECIPE_INGREDIENT_QUANTITY, quantity);
        values.put(DatabaseHelper.COLUMN_RECIPE_INGREDIENT_UNIT, unit);

        return database.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, values);
    }

    /**
     * Retrieves all recipes.
     *
     * @return list of recipes
     */
    public List<Recipe> getAllRecipes() {

        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_RECIPE_NAME + " ASC"
        );

        while (cursor.moveToNext()) {
            int recipeId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_ID));

            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_NAME));
            String instructions = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_INSTRUCTIONS));

            Recipe recipe = new Recipe(recipeId, name, instructions);
            recipe.setIngredients(getIngredients(recipeId));
            recipes.add(recipe);
        }

        cursor.close();
        return recipes;
    }

    /**
     * Retrieves ingredients belonging to a recipe.
     *
     * @param recipeId recipe ID
     * @return list of recipe ingredients
     */
    public List<RecipeIngredient> getIngredients(int recipeId) {

        List<RecipeIngredient> ingredients = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                null,
                DatabaseHelper.COLUMN_RECIPE_INGREDIENT_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                DatabaseHelper.COLUMN_RECIPE_INGREDIENT_NAME + " ASC"
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_INGREDIENT_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_INGREDIENT_NAME));
            double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_INGREDIENT_QUANTITY));

            String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPE_INGREDIENT_UNIT));
            ingredients.add(new RecipeIngredient(id, recipeId, name, quantity, unit));
        }

        cursor.close();
        return ingredients;
    }

    /**
     * Deletes a recipe and its ingredients.
     *
     * @param recipeId recipe ID
     * @return number of recipes deleted
     */
    public int deleteRecipe(int recipeId) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        return database.delete(
                DatabaseHelper.TABLE_RECIPES,
                DatabaseHelper.COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)}
        );
    }

    /**
     * Closes the database helper.
     */
    public void close() {
        databaseHelper.close();
    }

}