package com.smartpantry.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Preserve existing pantry data while creating new tables.
        createTables(db);
        seedRecipes(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS pantry_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "expiry_date TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "instructions TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, " +
                "required_quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id))");
    }

    // Insert 15 sample recipes without creating duplicates.
    private void seedRecipes(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM recipes", null);

        int recipeCount = 0;
        try {
            if (cursor.moveToFirst()) {
                recipeCount = cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }

        if (recipeCount > 0) {
            return;
        }

        db.beginTransaction();

        try {
            addRecipe(db, "Tomato Pasta",
                    "Boil pasta. Cook tomatoes with garlic, then mix together.",
                    new String[]{"pasta", "tomato", "garlic"},
                    new double[]{200, 150, 1},
                    new String[]{"g", "g", "cloves"});

            addRecipe(db, "Vegetable Omelette",
                    "Beat eggs, add chopped vegetables, and cook in a pan.",
                    new String[]{"eggs", "onion", "tomato"},
                    new double[]{2, 50, 100},
                    new String[]{"pieces", "g", "g"});

            addRecipe(db, "Banana Pancakes",
                    "Mash banana, mix with egg and flour, then fry.",
                    new String[]{"banana", "eggs", "flour"},
                    new double[]{1, 1, 100},
                    new String[]{"pieces", "pieces", "g"});

            addRecipe(db, "Garlic Fried Rice",
                    "Fry garlic, add cooked rice, and stir until heated.",
                    new String[]{"rice", "garlic", "oil"},
                    new double[]{200, 2, 15},
                    new String[]{"g", "cloves", "ml"});

            addRecipe(db, "Cheese Sandwich",
                    "Place cheese between bread slices and toast.",
                    new String[]{"bread", "cheese", "butter"},
                    new double[]{2, 40, 10},
                    new String[]{"slices", "g", "g"});

            addRecipe(db, "Chicken Stir-Fry",
                    "Cook chicken and vegetables in a pan until done.",
                    new String[]{"chicken", "onion", "carrot"},
                    new double[]{200, 50, 100},
                    new String[]{"g", "g", "g"});

            addRecipe(db, "Tomato Soup",
                    "Cook tomatoes and onion, add water, then blend.",
                    new String[]{"tomato", "onion", "water"},
                    new double[]{300, 50, 250},
                    new String[]{"g", "g", "ml"});

            addRecipe(db, "Mashed Potatoes",
                    "Boil potatoes, mash with butter and milk.",
                    new String[]{"potato", "butter", "milk"},
                    new double[]{300, 20, 50},
                    new String[]{"g", "g", "ml"});

            addRecipe(db, "Simple Salad",
                    "Chop cucumber and tomato, then mix with olive oil.",
                    new String[]{"cucumber", "tomato", "olive oil"},
                    new double[]{100, 100, 15},
                    new String[]{"g", "g", "ml"});

            addRecipe(db, "Garlic Bread",
                    "Spread garlic butter on bread and toast.",
                    new String[]{"bread", "garlic", "butter"},
                    new double[]{2, 1, 20},
                    new String[]{"slices", "cloves", "g"});

            addRecipe(db, "Apple Oatmeal",
                    "Cook oats with milk and top with chopped apple.",
                    new String[]{"oats", "milk", "apple"},
                    new double[]{50, 200, 1},
                    new String[]{"g", "ml", "pieces"});

            addRecipe(db, "Tuna Pasta",
                    "Boil pasta and mix with tuna and tomato.",
                    new String[]{"pasta", "tuna", "tomato"},
                    new double[]{200, 100, 100},
                    new String[]{"g", "g", "g"});

            addRecipe(db, "Bean Curry",
                    "Cook beans with onion and curry powder.",
                    new String[]{"beans", "onion", "curry powder"},
                    new double[]{200, 50, 5},
                    new String[]{"g", "g", "g"});

            addRecipe(db, "French Toast",
                    "Dip bread in beaten egg and milk, then fry.",
                    new String[]{"bread", "eggs", "milk"},
                    new double[]{2, 1, 50},
                    new String[]{"slices", "pieces", "ml"});

            addRecipe(db, "Carrot Soup",
                    "Boil carrots and onion until soft, then blend.",
                    new String[]{"carrot", "onion", "water"},
                    new double[]{250, 50, 300},
                    new String[]{"g", "g", "ml"});

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    private void addRecipe(
            SQLiteDatabase db,
            String name,
            String instructions,
            String[] ingredientNames,
            double[] quantities,
            String[] units) {

        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("instructions", instructions);

        long recipeId = db.insert("recipes", null, recipeValues);

        if (recipeId == -1) {
            throw new IllegalStateException(
                    "Could not insert recipe: " + name);
        }

        for (int i = 0; i < ingredientNames.length; i++) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("recipe_id", recipeId);
            ingredientValues.put("ingredient_name", ingredientNames[i]);
            ingredientValues.put("required_quantity", quantities[i]);
            ingredientValues.put("unit", units[i]);

            long result = db.insert(
                    "recipe_ingredients", null, ingredientValues);

            if (result == -1) {
                throw new IllegalStateException(
                        "Could not insert recipe ingredient: "
                                + ingredientNames[i]);
            }
        }
    }

    // CREATE: Add a pantry ingredient.
    public boolean addIngredient(
            String name, double quantity, String unit, String expiryDate) {

        if (name == null || name.trim().isEmpty()
                || !Double.isFinite(quantity) || quantity <= 0
                || unit == null || unit.trim().isEmpty()) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());
        values.put("expiry_date", expiryDate);

        return getWritableDatabase().insert(
                "pantry_items", null, values) != -1;
    }

    // READ: Retrieve all pantry ingredients.
    public Cursor getAllIngredients() {
        return getReadableDatabase().query(
                "pantry_items", null, null, null,
                null, null, "name ASC");
    }

    // UPDATE: Edit an existing pantry ingredient.
    public boolean updateIngredient(
            int id, String name, double quantity,
            String unit, String expiryDate) {

        if (id <= 0 || name == null || name.trim().isEmpty()
                || !Double.isFinite(quantity) || quantity <= 0
                || unit == null || unit.trim().isEmpty()) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());
        values.put("expiry_date", expiryDate);

        return getWritableDatabase().update(
                "pantry_items", values, "id = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    // DELETE: Remove a pantry ingredient.
    public boolean deleteIngredient(int id) {
        if (id <= 0) {
            return false;
        }

        return getWritableDatabase().delete(
                "pantry_items", "id = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    // READ: Retrieve all seeded recipes.
    public Cursor getAllRecipes() {
        return getReadableDatabase().query(
                "recipes", null, null, null,
                null, null, "name ASC");
    }

    // READ: Retrieve ingredients belonging to one recipe.
    public Cursor getRecipeIngredients(int recipeId) {
        return getReadableDatabase().query(
                "recipe_ingredients", null,
                "recipe_id = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, "ingredient_name ASC");
    }
}