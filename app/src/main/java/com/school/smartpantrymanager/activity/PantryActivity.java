package com.school.smartpantrymanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.school.smartpantrymanager.R;
import com.school.smartpantrymanager.adapter.PantryAdapter;
import com.school.smartpantrymanager.dao.PantryDAO;
import com.school.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays all ingredients currently stored in the pantry.
 */
public class PantryActivity extends AppCompatActivity implements PantryAdapter.OnPantryItemClickListener {

    private RecyclerView recyclerViewPantry;
    private TextView textViewEmptyPantry;
    private FloatingActionButton fabAddIngredient;

    private PantryDAO pantryDAO;
    private PantryAdapter pantryAdapter;
    private List<PantryItem> pantryItems;

    /**
     * Initializes the pantry activity.
     *
     * @param savedInstanceState previously saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        recyclerViewPantry = findViewById(R.id.recyclerViewPantry);
        textViewEmptyPantry = findViewById(R.id.textViewEmptyPantry);
        fabAddIngredient = findViewById(R.id.fabAddIngredient);

        pantryDAO = new PantryDAO(this);
        pantryItems = new ArrayList<>();

        recyclerViewPantry.setLayoutManager(new LinearLayoutManager(this));

        pantryAdapter = new PantryAdapter(pantryItems, this);
        recyclerViewPantry.setAdapter(pantryAdapter);

        fabAddIngredient.setOnClickListener(view -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });
    }

    /**
     * Reloads pantry data whenever the activity becomes visible.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    /**
     * Loads all pantry items from SQLite and displays them.
     */
    private void loadPantryItems() {

        List<PantryItem> databaseItems = pantryDAO.getAllItems();

        pantryItems.clear();
        pantryItems.addAll(databaseItems);

        pantryAdapter.notifyDataSetChanged();

        if (pantryItems.isEmpty()) {
            recyclerViewPantry.setVisibility(RecyclerView.GONE);
            textViewEmptyPantry.setVisibility(TextView.VISIBLE);
        } else {
            recyclerViewPantry.setVisibility(RecyclerView.VISIBLE);
            textViewEmptyPantry.setVisibility(TextView.GONE);
        }
    }

    /**
     * Opens the ingredient screen for editing.
     *
     * @param pantryItem selected pantry item
     */
    @Override
    public void onEdit(PantryItem pantryItem) {

        Intent intent = new Intent(
                PantryActivity.this,
                AddEditIngredientActivity.class
        );

        intent.putExtra("pantry_id", pantryItem.getId());
        startActivity(intent);
    }

    /**
     * Displays a confirmation dialog before deleting an ingredient.
     *
     * @param pantryItem pantry item to delete
     */
    @Override
    public void onDelete(PantryItem pantryItem) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Delete " + pantryItem.getName() + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> deletePantryItem(pantryItem))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes a pantry item from SQLite.
     *
     * @param pantryItem pantry item to delete
     */
    private void deletePantryItem(PantryItem pantryItem) {

        int rowsDeleted = pantryDAO.deleteItem(pantryItem.getId());

        if (rowsDeleted > 0) {
            Toast.makeText(
                    this,
                    "Ingredient deleted",
                    Toast.LENGTH_SHORT
            ).show();

            loadPantryItems();
        } else {
            Toast.makeText(
                    this,
                    "Unable to delete ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * Closes the database when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryDAO != null) {
            pantryDAO.close();
        }
    }
}