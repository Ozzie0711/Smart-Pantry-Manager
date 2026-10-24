package com.smartpantry.manager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private IngredientAdapter adapter;
    private ListView listPantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        listPantry = findViewById(R.id.listPantry);
        Button btnAdd = findViewById(R.id.btnAddIngredient);
        Button btnRecipes = findViewById(R.id.btnSuggestedRecipes);
        Button btnSettings = findViewById(R.id.btnSettings);

        adapter = new IngredientAdapter(this);
        listPantry.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> openIngredientForm(-1));

        btnRecipes.setOnClickListener(v -> openSuggestedRecipes());

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );
            startActivity(intent);
        });

        listPantry.setOnItemClickListener((parent, view, position, id) -> {
            showIngredientOptions((int) id);
        });

        loadIngredients();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && adapter != null) {
            loadIngredients();
        }
    }

    private void loadIngredients() {
        try (Cursor cursor = databaseHelper.getAllIngredients()) {
            adapter.updateData(cursor);
        }
    }

    private void openIngredientForm(int itemId) {
        Intent intent = new Intent(
                this,
                IngredientFormActivity.class
        );

        if (itemId != -1) {
            intent.putExtra("ingredient_id", itemId);
        }

        startActivity(intent);
    }

    private void showIngredientOptions(int itemId) {
        new AlertDialog.Builder(this)
                .setTitle("Ingredient Options")
                .setItems(
                        new String[]{"Edit", "Delete", "Cancel"},
                        (dialog, which) -> {
                            if (which == 0) {
                                openIngredientForm(itemId);
                            } else if (which == 1) {
                                confirmDelete(itemId);
                            }
                        }
                )
                .show();
    }

    private void confirmDelete(int itemId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete this ingredient?"
                )
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (databaseHelper.deleteIngredient(itemId)) {
                        loadIngredients();
                        showMessage("Ingredient deleted");
                    } else {
                        showMessage("Could not delete ingredient");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openSuggestedRecipes() {
        Intent intent = new Intent(
                this,
                SuggestedRecipesActivity.class
        );
        startActivity(intent);
    }

    private void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}