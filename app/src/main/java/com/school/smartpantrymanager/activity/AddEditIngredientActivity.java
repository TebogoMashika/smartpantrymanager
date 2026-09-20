package com.school.smartpantrymanager.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.school.smartpantrymanager.R;
import com.school.smartpantrymanager.dao.PantryDAO;
import com.school.smartpantrymanager.model.PantryItem;

/**
 * Allows the user to add or edit a pantry ingredient.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextQuantity;
    private EditText editTextUnit;
    private EditText editTextExpiry;
    private Button buttonSave;
    private Button buttonBack;

    private PantryDAO pantryDAO;
    private int ingredientId = -1;

    /**
     * Initializes the add/edit ingredient activity.
     *
     * @param savedInstanceState previously saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        editTextName = findViewById(R.id.editTextName);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        editTextUnit = findViewById(R.id.editTextUnit);
        editTextExpiry = findViewById(R.id.editTextExpiry);
        buttonSave = findViewById(R.id.buttonSave);
        buttonBack = findViewById(R.id.buttonBack);

        pantryDAO = new PantryDAO(this);

        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        if (ingredientId != -1) {
            loadIngredient();
            buttonSave.setText("Update Ingredient");
        } else {
            buttonSave.setText("Add Ingredient");
        }

        buttonSave.setOnClickListener(view -> saveIngredient());
        buttonBack.setOnClickListener(view -> finish());
    }

    /**
     * Loads the existing ingredient into the edit fields.
     */
    private void loadIngredient() {
        PantryItem pantryItem = pantryDAO.getItemById(ingredientId);

        if (pantryItem != null) {
            editTextName.setText(pantryItem.getName());
            editTextQuantity.setText(String.valueOf(pantryItem.getQuantity()));
            editTextUnit.setText(pantryItem.getUnit());

            if (pantryItem.getExpiryDate() != null) {
                editTextExpiry.setText(pantryItem.getExpiryDate());
            }
        }
    }

    /**
     * Validates and saves the pantry ingredient.
     */
    private void saveIngredient() {
        String name = editTextName.getText().toString().trim();
        String quantityText = editTextQuantity.getText().toString().trim();
        String unit = editTextUnit.getText().toString().trim();
        String expiryDate = editTextExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Enter an ingredient name");
            editTextName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            editTextQuantity.setError("Enter a quantity");
            editTextQuantity.requestFocus();
            return;
        }

        if (unit.isEmpty()) {
            editTextUnit.setError("Enter a unit");
            editTextUnit.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException exception) {
            editTextQuantity.setError("Enter a valid quantity");
            editTextQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            editTextQuantity.setError("Quantity must be greater than zero");
            editTextQuantity.requestFocus();
            return;
        }

        if (ingredientId == -1) {
            addIngredient(name, quantity, unit, expiryDate);
        } else {
            updateIngredient(name, quantity, unit, expiryDate);
        }
    }

    /**
     * Adds a new ingredient to the database.
     *
     * @param name ingredient name
     * @param quantity ingredient quantity
     * @param unit ingredient unit
     * @param expiryDate optional expiry date
     */
    private void addIngredient(String name, double quantity, String unit, String expiryDate) {
        long result = pantryDAO.addItem(name, quantity, unit, expiryDate);

        if (result != -1) {
            Toast.makeText(this, "Ingredient added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add ingredient", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates an existing ingredient in the database.
     *
     * @param name ingredient name
     * @param quantity ingredient quantity
     * @param unit ingredient unit
     * @param expiryDate optional expiry date
     */
    private void updateIngredient(String name, double quantity, String unit, String expiryDate) {
        int result = pantryDAO.updateItem(ingredientId, name, quantity, unit, expiryDate);

        if (result > 0) {
            Toast.makeText(this, "Ingredient updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update ingredient", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Closes the database helper when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pantryDAO != null) {
            pantryDAO.close();
        }
    }
}