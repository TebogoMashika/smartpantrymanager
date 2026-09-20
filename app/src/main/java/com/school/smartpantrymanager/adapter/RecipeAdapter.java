package com.school.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.smartpantrymanager.R;
import com.school.smartpantrymanager.model.Recipe;

import java.util.List;

/**
 * Adapter used to display suggested recipes.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_recipe,
                parent,
                false
        );

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.recipeNameTextView.setText(recipe.getName());
        String ingredientCount = recipe.getIngredients().size() + " ingredients";
        holder.recipeIngredientsTextView.setText(ingredientCount);
        holder.itemView.setOnClickListener(view -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /**
     * Holds views for a recipe item.
     */
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        private final TextView recipeNameTextView;
        private final TextView recipeIngredientsTextView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.textViewRecipeName);
            recipeIngredientsTextView = itemView.findViewById(R.id.textViewRecipeIngredients);
        }
    }
}