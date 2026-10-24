package com.smartpantry.manager;

import android.content.Context;
import android.database.Cursor;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {


    @Test
    public void databaseContainsFifteenSeededRecipes() {
        Context context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        DatabaseHelper database = new DatabaseHelper(context);

        try (Cursor cursor = database.getAllRecipes()) {
            assertEquals("The database should contain 15 recipes",
                    15, cursor.getCount());
        } finally {
            database.close();
        }
    }
    @Test
    public void useAppContext() {
        Context context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        assertEquals("com.smartpantry.manager",
                context.getPackageName());
    }

    @Test
    public void databaseCanAddReadUpdateAndDeleteIngredient() {
        Context context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        DatabaseHelper database = new DatabaseHelper(context);
        String ingredientName = "Test Tomato";

        try {
            // Clean up any leftover test ingredient.
            try (Cursor cursor = database.getAllIngredients()) {
                while (cursor.moveToNext()) {
                    if (ingredientName.equals(
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow("name")))) {
                        database.deleteIngredient(
                                cursor.getInt(
                                        cursor.getColumnIndexOrThrow("id")));
                    }
                }
            }

            // Test CREATE.
            assertTrue(database.addIngredient(
                    ingredientName, 5, "pieces", null));

            int ingredientId = -1;

            // Test READ.
            try (Cursor cursor = database.getAllIngredients()) {
                while (cursor.moveToNext()) {
                    if (ingredientName.equals(
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow("name")))) {
                        ingredientId = cursor.getInt(
                                cursor.getColumnIndexOrThrow("id"));
                        assertEquals(5.0, cursor.getDouble(
                                        cursor.getColumnIndexOrThrow("quantity")),
                                0.001);
                        break;
                    }
                }
            }

            assertTrue("Ingredient should be saved", ingredientId > 0);

            // Test UPDATE.
            assertTrue(database.updateIngredient(
                    ingredientId, ingredientName, 10, "pieces", null));

            try (Cursor cursor = database.getAllIngredients()) {
                while (cursor.moveToNext()) {
                    if (cursor.getInt(
                            cursor.getColumnIndexOrThrow("id"))
                            == ingredientId) {
                        assertEquals(10.0, cursor.getDouble(
                                        cursor.getColumnIndexOrThrow("quantity")),
                                0.001);
                        break;
                    }
                }
            }

            // Test DELETE.
            assertTrue(database.deleteIngredient(ingredientId));

            boolean ingredientStillExists = false;

            try (Cursor cursor = database.getAllIngredients()) {
                while (cursor.moveToNext()) {
                    if (ingredientName.equals(
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow("name")))) {
                        ingredientStillExists = true;
                        break;
                    }
                }
            }

            assertFalse("Ingredient should be deleted",
                    ingredientStillExists);

        } finally {
            database.close();
        }
    }
}