package com.school.smartpantrymanager.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.school.smartpantrymanager.R;
import com.school.smartpantrymanager.dao.RecipeDAO;
import com.school.smartpantrymanager.model.Recipe;
import com.school.smartpantrymanager.model.RecipeIngredient;

import java.util.List;

/**
 * Displays the details of a selected recipe.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private TextView textViewRecipeName;
    private TextView textViewIngredients;
    private TextView textViewInstructions;

    private RecipeDAO recipeDAO;

    /**
     * Initializes the recipe detail screen.
     *
     * @param savedInstanceState previously saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        textViewRecipeName =
                findViewById(R.id.textViewRecipeName);

        textViewIngredients =
                findViewById(R.id.textViewIngredients);

        textViewInstructions =
                findViewById(R.id.textViewInstructions);

        recipeDAO = new RecipeDAO(this);

        int recipeId =
                getIntent().getIntExtra("recipe_id", -1);

        if (recipeId != -1) {
            loadRecipeDetails(recipeId);
        }
    }

    /**
     * Loads and displays the selected recipe.
     *
     * @param recipeId recipe ID
     */
    private void loadRecipeDetails(int recipeId) {
        List<Recipe> recipes =
                recipeDAO.getAllRecipes();

        for (Recipe recipe : recipes) {
            if (recipe.getId() == recipeId) {
                displayRecipe(recipe);
                return;
            }
        }

        textViewRecipeName.setText("Recipe not found");
        textViewIngredients.setText("");
        textViewInstructions.setText("");
    }

    /**
     * Displays recipe information.
     *
     * @param recipe recipe to display
     */
    private void displayRecipe(Recipe recipe) {
        textViewRecipeName.setText(recipe.getName());

        StringBuilder ingredients =
                new StringBuilder();

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            ingredients.append("• ")
                    .append(ingredient.getIngredientName())
                    .append(" - ")
                    .append(ingredient.getRequiredQuantity())
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append("\n");
        }

        textViewIngredients.setText(
                ingredients.toString()
        );

        textViewInstructions.setText(
                recipe.getInstructions()
        );
    }

    /**
     * Closes the recipe database.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (recipeDAO != null) {
            recipeDAO.close();
        }
    }
}