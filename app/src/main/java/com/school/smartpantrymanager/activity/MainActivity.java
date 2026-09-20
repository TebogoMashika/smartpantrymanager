package com.school.smartpantrymanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.school.smartpantrymanager.R;

/**
 * Main screen of the Smart Pantry Manager application.
 *
 * Provides navigation to the pantry, suggested recipes,
 * and application settings.
 */
public class MainActivity extends AppCompatActivity {

    private Button buttonPantry;
    private Button buttonRecipes;
    private Button buttonSettings;

    /**
     * Initializes the main activity and its navigation buttons.
     *
     * @param savedInstanceState previously saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPantry = findViewById(R.id.buttonPantry);
        buttonRecipes = findViewById(R.id.buttonRecipes);
        buttonSettings = findViewById(R.id.buttonSettings);

        buttonPantry.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PantryActivity.class);
            startActivity(intent);
        });

        buttonRecipes.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SuggestedRecipesActivity.class);
            startActivity(intent);
        });

        buttonSettings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}