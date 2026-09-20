package com.school.smartpantrymanager.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.school.smartpantrymanager.R;
import com.school.smartpantrymanager.database.DatabaseHelper;

/**
 * Allows the user to configure application preferences.
 */
public class SettingsActivity extends AppCompatActivity {

    private Switch switchExpiryAlerts;
    private Spinner spinnerUnits;
    private DatabaseHelper databaseHelper;
    private boolean loadingSettings = true;

    /**
     * Initializes the settings screen.
     *
     * @param savedInstanceState previously saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchExpiryAlerts =
                findViewById(R.id.switchExpiryAlerts);

        spinnerUnits =
                findViewById(R.id.spinnerUnits);

        databaseHelper =
                new DatabaseHelper(this);

        setupUnitSpinner();
        loadSettings();

        switchExpiryAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (!loadingSettings) {
                        saveSettings();
                    }
                }
        );

        spinnerUnits.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        if (!loadingSettings) {
                            saveSettings();
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );
    }

    /**
     * Creates the units spinner.
     */
    private void setupUnitSpinner() {
        String[] units = {
                "Metric",
                "Imperial"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        units
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnits.setAdapter(adapter);
    }

    /**
     * Loads saved settings from SQLite.
     */
    private void loadSettings() {
        SQLiteDatabase database =
                databaseHelper.getReadableDatabase();

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_SETTINGS,
                new String[]{
                        DatabaseHelper.COLUMN_SETTINGS_EXPIRY_ALERTS,
                        DatabaseHelper.COLUMN_SETTINGS_UNIT_PREFERENCE
                },
                DatabaseHelper.COLUMN_SETTINGS_ID + " = ?",
                new String[]{"1"},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            boolean expiryAlertsEnabled =
                    cursor.getInt(0) == 1;

            String unitPreference =
                    cursor.getString(1);

            switchExpiryAlerts.setChecked(
                    expiryAlertsEnabled
            );

            if ("Imperial".equals(unitPreference)) {
                spinnerUnits.setSelection(1);
            } else {
                spinnerUnits.setSelection(0);
            }
        }

        cursor.close();

        loadingSettings = false;
    }

    /**
     * Saves settings to SQLite.
     */
    private void saveSettings() {
        ContentValues values = new ContentValues();

        values.put(
                DatabaseHelper.COLUMN_SETTINGS_EXPIRY_ALERTS,
                switchExpiryAlerts.isChecked() ? 1 : 0
        );

        values.put(
                DatabaseHelper.COLUMN_SETTINGS_UNIT_PREFERENCE,
                spinnerUnits.getSelectedItem().toString()
        );

        databaseHelper.getWritableDatabase().update(
                DatabaseHelper.TABLE_SETTINGS,
                values,
                DatabaseHelper.COLUMN_SETTINGS_ID + " = ?",
                new String[]{"1"}
        );
    }

    /**
     * Closes the database helper.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}