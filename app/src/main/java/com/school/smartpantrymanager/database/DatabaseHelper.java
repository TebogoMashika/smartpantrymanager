package com.school.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates and manages the SQLite database used by Smart Pantry Manager.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COLUMN_PANTRY_ID = "id";
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QUANTITY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";
    public static final String COLUMN_PANTRY_EXPIRY_DATE = "expiry_date";

    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_RECIPE_ID = "id";
    public static final String COLUMN_RECIPE_NAME = "name";
    public static final String COLUMN_RECIPE_INSTRUCTIONS = "instructions";

    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COLUMN_RECIPE_INGREDIENT_ID = "id";
    public static final String COLUMN_RECIPE_INGREDIENT_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RECIPE_INGREDIENT_NAME = "ingredient_name";
    public static final String COLUMN_RECIPE_INGREDIENT_QUANTITY = "required_quantity";
    public static final String COLUMN_RECIPE_INGREDIENT_UNIT = "unit";

    public static final String TABLE_SETTINGS = "settings";
    public static final String COLUMN_SETTINGS_ID = "id";
    public static final String COLUMN_SETTINGS_EXPIRY_ALERTS = "expiry_alerts";
    public static final String COLUMN_SETTINGS_UNIT_PREFERENCE = "unit_preference";

    /**
     * Creates the database helper.
     *
     * @param context application context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates all database tables and seeds the recipe collection.
     *
     * @param database SQLite database
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(
                "CREATE TABLE " + TABLE_PANTRY + " (" +
                        COLUMN_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_PANTRY_NAME + " TEXT NOT NULL, " +
                        COLUMN_PANTRY_QUANTITY + " REAL NOT NULL, " +
                        COLUMN_PANTRY_UNIT + " TEXT NOT NULL, " +
                        COLUMN_PANTRY_EXPIRY_DATE + " TEXT)"
        );

        database.execSQL(
                "CREATE TABLE " + TABLE_RECIPES + " (" +
                        COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                        COLUMN_RECIPE_INSTRUCTIONS + " TEXT NOT NULL)"
        );

        database.execSQL(
                "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                        COLUMN_RECIPE_INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_RECIPE_INGREDIENT_RECIPE_ID + " INTEGER NOT NULL, " +
                        COLUMN_RECIPE_INGREDIENT_NAME + " TEXT NOT NULL, " +
                        COLUMN_RECIPE_INGREDIENT_QUANTITY + " REAL NOT NULL, " +
                        COLUMN_RECIPE_INGREDIENT_UNIT + " TEXT NOT NULL, " +
                        "FOREIGN KEY (" + COLUMN_RECIPE_INGREDIENT_RECIPE_ID + ") REFERENCES " +
                        TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ") ON DELETE CASCADE)"
        );

        database.execSQL(
                "CREATE TABLE " + TABLE_SETTINGS + " (" +
                        COLUMN_SETTINGS_ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_SETTINGS_EXPIRY_ALERTS + " INTEGER NOT NULL, " +
                        COLUMN_SETTINGS_UNIT_PREFERENCE + " TEXT NOT NULL)"
        );

        database.execSQL(
                "INSERT INTO " + TABLE_SETTINGS + " (" +
                        COLUMN_SETTINGS_ID + ", " +
                        COLUMN_SETTINGS_EXPIRY_ALERTS + ", " +
                        COLUMN_SETTINGS_UNIT_PREFERENCE + ") VALUES (1, 1, 'Metric')"
        );

        seedPantryItems(database);
        seedRecipes(database);
    }

    /**
     * Enables SQLite foreign key support.
     *
     * @param database SQLite database
     */
    @Override
    public void onOpen(SQLiteDatabase database) {
        super.onOpen(database);

        if (!database.isReadOnly()) {
            database.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    /**
     * Seeds the pantry with test ingredients used to verify strict recipe matching.
     *
     * @param database SQLite database
     */
    private void seedPantryItems(SQLiteDatabase database) {
        addPantryItem(database, "Chicken", 1, "kg");
        addPantryItem(database, "Rice", 500, "g");
        addPantryItem(database, "Tomatoes", 4, "pieces");
        addPantryItem(database, "Onion", 2, "pieces");
        addPantryItem(database, "Cheese", 200, "g");
        addPantryItem(database, "Eggs", 6, "pieces");
        addPantryItem(database, "Milk", 1, "L");
        addPantryItem(database, "Flour", 500, "g");
        addPantryItem(database, "Butter", 250, "g");
    }

    /**
     * Inserts one pantry ingredient into the database.
     *
     * @param database SQLite database
     * @param name ingredient name
     * @param quantity ingredient quantity
     * @param unit measurement unit
     */
    private void addPantryItem(SQLiteDatabase database, String name, double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PANTRY_NAME, name);
        values.put(COLUMN_PANTRY_QUANTITY, quantity);
        values.put(COLUMN_PANTRY_UNIT, unit);
        database.insert(TABLE_PANTRY, null, values);
    }

    /**
     * Seeds the database with the default recipe collection.
     *
     * @param database SQLite database
     */
    private void seedRecipes(SQLiteDatabase database) {
        database.beginTransaction();

        try {
            addRecipe(database, "Chicken Stir Fry",
                    "Heat oil in a pan. Cook the chicken until browned. Add vegetables and stir fry until tender. Add soy sauce and serve with rice.",
                    new String[][]{
                            {"Chicken", "500", "g"},
                            {"Rice", "2", "cup"},
                            {"Carrot", "1", "unit"},
                            {"Bell Pepper", "1", "unit"},
                            {"Soy Sauce", "2", "tbsp"},
                            {"Cooking Oil", "1", "tbsp"}
                    });

            addRecipe(database, "Spaghetti Bolognese",
                    "Cook spaghetti according to the package instructions. Brown the mince with onion and garlic. Add tomatoes and simmer. Serve over spaghetti.",
                    new String[][]{
                            {"Spaghetti", "200", "g"},
                            {"Beef Mince", "300", "g"},
                            {"Tomato", "2", "unit"},
                            {"Onion", "1", "unit"},
                            {"Garlic", "2", "clove"},
                            {"Cooking Oil", "1", "tbsp"}
                    });

            addRecipe(database, "Chicken Curry",
                    "Cook onion and garlic in oil. Add chicken and curry powder. Add tomatoes and simmer until the chicken is cooked. Serve with rice.",
                    new String[][]{
                            {"Chicken", "500", "g"},
                            {"Rice", "2", "cup"},
                            {"Onion", "1", "unit"},
                            {"Garlic", "2", "clove"},
                            {"Tomato", "2", "unit"},
                            {"Curry Powder", "2", "tbsp"},
                            {"Cooking Oil", "1", "tbsp"}
                    });

            addRecipe(database, "Vegetable Omelette",
                    "Beat the eggs. Fry the vegetables in a pan. Add the eggs and cook until set. Fold and serve.",
                    new String[][]{
                            {"Eggs", "3", "unit"},
                            {"Onion", "1", "unit"},
                            {"Bell Pepper", "1", "unit"},
                            {"Tomato", "1", "unit"},
                            {"Cheese", "50", "g"},
                            {"Cooking Oil", "1", "tbsp"}
                    });

            addRecipe(database, "Pancakes",
                    "Mix flour, milk, eggs and sugar into a smooth batter. Heat a pan and cook pancakes on both sides until golden.",
                    new String[][]{
                            {"Flour", "2", "cup"},
                            {"Milk", "2", "cup"},
                            {"Eggs", "2", "unit"},
                            {"Sugar", "2", "tbsp"},
                            {"Butter", "2", "tbsp"}
                    });

            addRecipe(database, "Grilled Cheese Sandwich",
                    "Butter the bread. Add cheese between two slices. Grill in a pan until the bread is golden and the cheese melts.",
                    new String[][]{
                            {"Bread", "2", "slice"},
                            {"Cheese", "50", "g"},
                            {"Butter", "1", "tbsp"}
                    });

            addRecipe(database, "Tomato Pasta",
                    "Cook pasta until tender. Cook tomatoes and garlic in oil. Add the pasta and mix well. Season and serve.",
                    new String[][]{
                            {"Pasta", "200", "g"},
                            {"Tomato", "3", "unit"},
                            {"Garlic", "2", "clove"},
                            {"Cooking Oil", "1", "tbsp"}
                    });

            addRecipe(database, "Chicken Rice",
                    "Cook the chicken in a pan. Add rice and water and simmer until the rice is cooked. Season and serve.",
                    new String[][]{
                            {"Chicken", "500", "g"},
                            {"Rice", "2", "cup"},
                            {"Onion", "1", "unit"},
                            {"Carrot", "1", "unit"},
                            {"Cooking Oil", "1", "tbsp"}
                    });

            addRecipe(database, "Beef Burgers",
                    "Shape the beef mince into patties. Cook until fully done. Place the patties in burger buns and add tomato and onion.",
                    new String[][]{
                            {"Beef Mince", "400", "g"},
                            {"Burger Buns", "4", "unit"},
                            {"Tomato", "1", "unit"},
                            {"Onion", "1", "unit"},
                            {"Cheese", "50", "g"}
                    });

            addRecipe(database, "French Toast",
                    "Whisk eggs and milk together. Dip bread into the mixture. Fry in butter until golden on both sides.",
                    new String[][]{
                            {"Bread", "4", "slice"},
                            {"Eggs", "2", "unit"},
                            {"Milk", "1", "cup"},
                            {"Butter", "1", "tbsp"}
                    });

            addRecipe(database, "Chicken Sandwich",
                    "Cook the chicken until fully cooked. Slice the chicken and place it on bread with lettuce, tomato and mayonnaise.",
                    new String[][]{
                            {"Chicken", "300", "g"},
                            {"Bread", "4", "slice"},
                            {"Lettuce", "1", "cup"},
                            {"Tomato", "1", "unit"},
                            {"Mayonnaise", "2", "tbsp"}
                    });

            addRecipe(database, "Fried Rice",
                    "Cook the vegetables in oil. Add cooked rice and soy sauce. Stir fry until heated through.",
                    new String[][]{
                            {"Rice", "2", "cup"},
                            {"Eggs", "2", "unit"},
                            {"Carrot", "1", "unit"},
                            {"Peas", "1", "cup"},
                            {"Soy Sauce", "2", "tbsp"},
                            {"Cooking Oil", "1", "tbsp"}
                    });

            addRecipe(database, "Macaroni and Cheese",
                    "Cook macaroni. Prepare a simple cheese sauce using milk, butter and cheese. Mix with macaroni and serve.",
                    new String[][]{
                            {"Macaroni", "200", "g"},
                            {"Milk", "2", "cup"},
                            {"Cheese", "150", "g"},
                            {"Butter", "2", "tbsp"},
                            {"Flour", "2", "tbsp"}
                    });

            addRecipe(database, "Tuna Sandwich",
                    "Mix tuna with mayonnaise. Spread the mixture over bread and add lettuce and tomato.",
                    new String[][]{
                            {"Tuna", "1", "can"},
                            {"Bread", "4", "slice"},
                            {"Mayonnaise", "2", "tbsp"},
                            {"Lettuce", "1", "cup"},
                            {"Tomato", "1", "unit"}
                    });

            addRecipe(database, "Chicken Wrap",
                    "Cook chicken and slice it. Place chicken, lettuce, tomato and cheese in a wrap. Add mayonnaise and roll.",
                    new String[][]{
                            {"Chicken", "300", "g"},
                            {"Wrap", "2", "unit"},
                            {"Lettuce", "1", "cup"},
                            {"Tomato", "1", "unit"},
                            {"Cheese", "50", "g"},
                            {"Mayonnaise", "2", "tbsp"}
                    });

            addRecipe(database, "Vegetable Fried Rice",
                    "Stir fry the vegetables in oil. Add cooked rice and soy sauce. Cook until everything is hot.",
                    new String[][]{
                            {"Rice", "2", "cup"},
                            {"Carrot", "1", "unit"},
                            {"Peas", "1", "cup"},
                            {"Bell Pepper", "1", "unit"},
                            {"Soy Sauce", "2", "tbsp"},
                            {"Cooking Oil", "1", "tbsp"}
                    });

            addRecipe(database, "Egg Fried Rice",
                    "Scramble the eggs. Add cooked rice and soy sauce. Stir fry together until hot.",
                    new String[][]{
                            {"Rice", "2", "cup"},
                            {"Eggs", "2", "unit"},
                            {"Soy Sauce", "2", "tbsp"},
                            {"Cooking Oil", "1", "tbsp"}
                    });

            addRecipe(database, "Garlic Butter Pasta",
                    "Cook pasta until tender. Melt butter and cook garlic briefly. Add pasta and toss together.",
                    new String[][]{
                            {"Pasta", "200", "g"},
                            {"Garlic", "3", "clove"},
                            {"Butter", "2", "tbsp"},
                            {"Cheese", "30", "g"}
                    });

            addRecipe(database, "Chicken Pasta",
                    "Cook pasta. Cook chicken with garlic and oil. Combine the chicken and pasta and serve.",
                    new String[][]{
                            {"Pasta", "200", "g"},
                            {"Chicken", "300", "g"},
                            {"Garlic", "2", "clove"},
                            {"Cooking Oil", "1", "tbsp"},
                            {"Cheese", "30", "g"}
                    });

            addRecipe(database, "Beef Pasta",
                    "Cook pasta. Brown the beef mince with onion and tomato. Add the cooked pasta and combine.",
                    new String[][]{
                            {"Pasta", "200", "g"},
                            {"Beef Mince", "300", "g"},
                            {"Onion", "1", "unit"},
                            {"Tomato", "2", "unit"},
                            {"Cooking Oil", "1", "tbsp"}
                    });

            addRecipe(database, "Strict Chicken Rice",
                    "Cook the chicken and onion. Add rice and tomatoes and cook until ready.",
                    new String[][]{
                            {"Chicken", "500", "g"},
                            {"Rice", "300", "g"},
                            {"Tomato", "2", "pieces"},
                            {"Onion", "1", "piece"}
                    });

            addRecipe(database, "Missing Pasta Test",
                    "Cook the chicken, rice, tomatoes, onion and pasta together.",
                    new String[][]{
                            {"Chicken", "500", "g"},
                            {"Rice", "300", "g"},
                            {"Tomato", "2", "pieces"},
                            {"Onion", "1", "piece"},
                            {"Pasta", "200", "g"}
                    });

            addRecipe(database, "Insufficient Chicken Test",
                    "Cook the chicken with rice until ready.",
                    new String[][]{
                            {"Chicken", "2", "kg"},
                            {"Rice", "300", "g"}
                    });

            addRecipe(database, "Unit Conversion Pancakes",
                    "Mix flour, milk, eggs and butter. Cook the pancakes in a pan.",
                    new String[][]{
                            {"Flour", "300", "g"},
                            {"Milk", "500", "ml"},
                            {"Eggs", "2", "pieces"},
                            {"Butter", "50", "g"}
                    });

            addRecipe(database, "Missing Garlic Test",
                    "Cook the chicken, cheese, onion and garlic together.",
                    new String[][]{
                            {"Chicken", "300", "g"},
                            {"Cheese", "50", "g"},
                            {"Onion", "1", "piece"},
                            {"Garlic", "2", "pieces"}
                    });

            database.setTransactionSuccessful();

        } finally {
            database.endTransaction();
        }
    }

    /**
     * Inserts one recipe and all of its required ingredients.
     *
     * @param database SQLite database
     * @param name recipe name
     * @param instructions preparation instructions
     * @param ingredients recipe ingredients
     */
    private void addRecipe(SQLiteDatabase database, String name, String instructions, String[][] ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COLUMN_RECIPE_NAME, name);
        recipeValues.put(COLUMN_RECIPE_INSTRUCTIONS, instructions);

        long recipeId = database.insert(TABLE_RECIPES, null, recipeValues);

        for (String[] ingredient : ingredients) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put(COLUMN_RECIPE_INGREDIENT_RECIPE_ID, recipeId);
            ingredientValues.put(COLUMN_RECIPE_INGREDIENT_NAME, ingredient[0]);
            ingredientValues.put(COLUMN_RECIPE_INGREDIENT_QUANTITY, Double.parseDouble(ingredient[1]));
            ingredientValues.put(COLUMN_RECIPE_INGREDIENT_UNIT, ingredient[2]);

            database.insert(TABLE_RECIPE_INGREDIENTS, null, ingredientValues);
        }
    }

    /**
     * Upgrades the database.
     *
     * @param database SQLite database
     * @param oldVersion previous database version
     * @param newVersion new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(database);
    }
}