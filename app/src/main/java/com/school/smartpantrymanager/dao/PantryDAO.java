package com.school.smartpantrymanager.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.school.smartpantrymanager.database.DatabaseHelper;
import com.school.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for pantry items.
 */
public class PantryDAO {

    private final DatabaseHelper databaseHelper;

    /**
     * Creates a PantryDAO.
     *
     * @param context application context
     */
    public PantryDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    /**
     * Adds a new pantry item.
     *
     * @param name item name
     * @param quantity item quantity
     * @param unit item unit
     * @param expiryDate expiry date
     * @return ID of the newly added item, or -1 if unsuccessful
     */
    public long addItem(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PANTRY_NAME, name);
        values.put(DatabaseHelper.COLUMN_PANTRY_QUANTITY, quantity);
        values.put(DatabaseHelper.COLUMN_PANTRY_UNIT, unit);
        values.put(DatabaseHelper.COLUMN_PANTRY_EXPIRY_DATE, expiryDate);
        return database.insert(DatabaseHelper.TABLE_PANTRY, null, values);
    }

    /**
     * Retrieves all pantry items.
     *
     * @return list containing all pantry items
     */
    public List<PantryItem> getAllItems() {
        List<PantryItem> pantryItems = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_PANTRY_NAME + " ASC");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_NAME));
            double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_QUANTITY));
            String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_UNIT));
            String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_EXPIRY_DATE));

            pantryItems.add(new PantryItem(id, name, quantity, unit, expiryDate));
        }

        cursor.close();
        return pantryItems;
    }

    /**
     * Retrieves one pantry item by its ID.
     *
     * @param id pantry item ID
     * @return pantry item or null if it does not exist
     */
    public PantryItem getItemById(int id) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_PANTRY,
                null,
                DatabaseHelper.COLUMN_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        PantryItem pantryItem = null;

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_NAME));
            double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_QUANTITY));
            String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_UNIT));
            String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PANTRY_EXPIRY_DATE));

            pantryItem = new PantryItem(id, name, quantity, unit, expiryDate);
        }

        cursor.close();

        return pantryItem;
    }

    /**
     * Updates an existing pantry item.
     *
     * @param id item ID
     * @param name item name
     * @param quantity item quantity
     * @param unit item unit
     * @param expiryDate expiry date
     * @return number of rows updated
     */
    public int updateItem(int id, String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PANTRY_NAME, name);
        values.put(DatabaseHelper.COLUMN_PANTRY_QUANTITY, quantity);
        values.put(DatabaseHelper.COLUMN_PANTRY_UNIT, unit);
        values.put(DatabaseHelper.COLUMN_PANTRY_EXPIRY_DATE, expiryDate);
        return database.update(DatabaseHelper.TABLE_PANTRY, values, DatabaseHelper.COLUMN_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Deletes a pantry item.
     *
     * @param id item ID
     * @return number of rows deleted
     */
    public int deleteItem(int id) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        return database.delete(DatabaseHelper.TABLE_PANTRY,
                DatabaseHelper.COLUMN_PANTRY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Closes the database helper.
     */
    public void close() {
        databaseHelper.close();
    }
}