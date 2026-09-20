package com.school.smartpantrymanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.school.smartpantrymanager.R;
import com.school.smartpantrymanager.adapter.RecipeAdapter;
import com.school.smartpantrymanager.dao.PantryDAO;
import com.school.smartpantrymanager.dao.RecipeDAO;
import com.school.smartpantrymanager.dao.RecipeMatcher;
import com.school.smartpantrymanager.model.PantryItem;
import com.school.smartpantrymanager.model.Recipe;

import java.util.List;

/**
 * Displays recipes that can be prepared using the available pantry ingredients.
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecipes;
    private TextView textViewNoRecipes;

    private PantryDAO pantryDAO;
    private RecipeDAO recipeDAO;
    private RecipeMatcher recipeMatcher;

    /**
     * Initializes the suggested recipes screen.
     *
     * @param savedInstanceState previously saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        textViewNoRecipes = findViewById(R.id.textViewNoRecipes);
        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));

        pantryDAO = new PantryDAO(this);
        recipeDAO = new RecipeDAO(this);
        recipeMatcher = new RecipeMatcher();

        loadSuggestedRecipes();
    }

    /**
     * Loads the pantry and recipe data and displays matching recipes.
     */
    private void loadSuggestedRecipes() {
        List<PantryItem> pantryItems = pantryDAO.getAllItems();
        List<Recipe> recipes = recipeDAO.getAllRecipes();

        List<Recipe> matchingRecipes = recipeMatcher.findMatchingRecipes(pantryItems, recipes);

        if (matchingRecipes.isEmpty()) {
            textViewNoRecipes.setVisibility(TextView.VISIBLE);
            recyclerViewRecipes.setVisibility(RecyclerView.GONE);
        } else {
            textViewNoRecipes.setVisibility(TextView.GONE);
            recyclerViewRecipes.setVisibility(RecyclerView.VISIBLE);

            RecipeAdapter recipeAdapter =
                    new RecipeAdapter(
                            matchingRecipes,
                            recipe -> {
                                Intent intent = new Intent(
                                        SuggestedRecipesActivity.this,
                                        RecipeDetailActivity.class
                                );

                                intent.putExtra(
                                        "recipe_id",
                                        recipe.getId()
                                );

                                startActivity(intent);
                            }
                    );

            recyclerViewRecipes.setAdapter(recipeAdapter);
        }
    }

    /**
     * Reloads recipes when returning to the screen.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (pantryDAO != null && recipeDAO != null) {
            loadSuggestedRecipes();
        }
    }

    /**
     * Closes database helpers.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryDAO != null) {
            pantryDAO.close();
        }

        if (recipeDAO != null) {
            recipeDAO.close();
        }
    }
}