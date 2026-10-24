package com.smartpantry.manager;

import android.content.Context;
import android.database.Cursor;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        assertEquals("com.smartpantry.manager",
                appContext.getPackageName());
    }

    @Test
    public void databaseCanAddReadUpdateAndDeleteIngredient() {
        Context context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        DatabaseHelper database = new DatabaseHelper(context);

        String originalName = "Test Tomato";
        String updatedName = "Updated Tomato";

        // Clean up leftover test data.
        Cursor oldItems = database.getReadableDatabase().rawQuery(
                "SELECT id FROM pantry_items WHERE name = ?",
                new String[]{originalName});

        try {
            while (oldItems.moveToNext()) {
                database.deleteIngredient(oldItems.getInt(0));
            }
        } finally {
            oldItems.close();
        }

        // ADD
        assertTrue("Ingredient should be added",
                database.addIngredient(
                        originalName, 5, "pieces", null));

        int ingredientId = -1;

        // READ
        Cursor cursor = database.getReadableDatabase().rawQuery(
                "SELECT id, name, quantity, unit FROM pantry_items WHERE name = ?",
                new String[]{originalName});

        try {
            assertTrue("Ingredient should exist", cursor.moveToFirst());

            ingredientId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id"));

            assertEquals(originalName,
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("name")));

            assertEquals(5,
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("quantity")));
        } finally {
            cursor.close();
        }

        assertTrue("Ingredient ID should be valid", ingredientId > 0);

        // UPDATE
        assertTrue("Ingredient update should succeed",
                database.updateIngredient(
                        ingredientId, updatedName, 10, "kg", null));

        // VERIFY UPDATED VALUES
        cursor = database.getReadableDatabase().rawQuery(
                "SELECT name, quantity, unit FROM pantry_items WHERE id = ?",
                new String[]{String.valueOf(ingredientId)});

        try {
            assertTrue("Updated ingredient should exist",
                    cursor.moveToFirst());

            assertEquals(updatedName,
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("name")));

            assertEquals(10,
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("quantity")));

            assertEquals("kg",
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("unit")));
        } finally {
            cursor.close();
        }

        // DELETE
        assertTrue("Ingredient deletion should succeed",
                database.deleteIngredient(ingredientId));

        cursor = database.getReadableDatabase().rawQuery(
                "SELECT id FROM pantry_items WHERE id = ?",
                new String[]{String.valueOf(ingredientId)});

        try {
            assertFalse("Deleted ingredient should not exist",
                    cursor.moveToFirst());
        } finally {
            cursor.close();
        }

        database.close();
    }

    @Test
    public void databaseContainsFifteenSeededRecipes() {
        Context context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        DatabaseHelper database = new DatabaseHelper(context);
        Cursor recipes = database.getAllRecipes();

        try {
            assertEquals(15, recipes.getCount());
        } finally {
            recipes.close();
            database.close();
        }
    }

    @Test
    public void addIngredientRejectsInvalidData() {
        Context context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        DatabaseHelper database = new DatabaseHelper(context);

        try {
            assertFalse(database.addIngredient(
                    "", 5, "pieces", null));

            assertFalse(database.addIngredient(
                    "Test Invalid", 0, "pieces", null));

            assertFalse(database.addIngredient(
                    "Test Invalid", -5, "pieces", null));

            assertFalse(database.addIngredient(
                    "Test Invalid", 5, "", null));
        } finally {
            database.close();
        }
    }

    @Test
    public void seededRecipesHaveIngredients() {
        Context context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();

        DatabaseHelper database = new DatabaseHelper(context);
        Cursor recipes = database.getAllRecipes();

        try {
            assertTrue("Recipes should exist",
                    recipes.moveToFirst());

            do {
                int recipeId = recipes.getInt(
                        recipes.getColumnIndexOrThrow("id"));

                Cursor ingredients =
                        database.getRecipeIngredients(recipeId);

                try {
                    assertTrue(
                            "Each seeded recipe should have ingredients",
                            ingredients.getCount() > 0);
                } finally {
                    ingredients.close();
                }

            } while (recipes.moveToNext());

        } finally {
            recipes.close();
            database.close();
        }
    }
}