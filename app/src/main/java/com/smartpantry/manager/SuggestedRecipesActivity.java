package com.smartpantry.manager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private final ArrayList<Integer> recipeIds = new ArrayList<>();
    private final ArrayList<String> recipeNames = new ArrayList<>();

    private ArrayAdapter<String> adapter;
    private ListView listRecipes;
    private TextView txtNoRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        listRecipes = findViewById(R.id.listSuggestedRecipes);
        txtNoRecipes = findViewById(R.id.txtNoRecipes);
        Button btnBack = findViewById(R.id.btnBackToPantry);

        databaseHelper = new DatabaseHelper(this);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                recipeNames
        );

        listRecipes.setAdapter(adapter);
        listRecipes.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < recipeIds.size()) {
                Intent intent = new Intent(
                        SuggestedRecipesActivity.this,
                        RecipeDetailActivity.class
                );
                intent.putExtra("recipe_id", recipeIds.get(position));
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(v -> finish());

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        recipeIds.clear();
        recipeNames.clear();

        try (Cursor recipes = databaseHelper.getAllRecipes()) {
            while (recipes.moveToNext()) {
                int recipeId = recipes.getInt(
                        recipes.getColumnIndexOrThrow("id"));
                String recipeName = recipes.getString(
                        recipes.getColumnIndexOrThrow("name"));

                if (canMakeRecipe(recipeId)) {
                    recipeIds.add(recipeId);
                    recipeNames.add(recipeName);
                }
            }
        }

        adapter.notifyDataSetChanged();

        boolean noMatches = recipeNames.isEmpty();
        txtNoRecipes.setVisibility(noMatches ? View.VISIBLE : View.GONE);
        listRecipes.setVisibility(noMatches ? View.GONE : View.VISIBLE);
    }

    private boolean canMakeRecipe(int recipeId) {
        boolean hasIngredients = false;

        try (Cursor required = databaseHelper.getRecipeIngredients(recipeId)) {
            if (!required.moveToFirst()) {
                return false;
            }

            do {
                hasIngredients = true;

                String requiredName = required.getString(
                        required.getColumnIndexOrThrow("ingredient_name"));
                double requiredQuantity = required.getDouble(
                        required.getColumnIndexOrThrow("required_quantity"));
                String requiredUnit = required.getString(
                        required.getColumnIndexOrThrow("unit"));

                boolean foundEnough = false;

                try (Cursor pantry = databaseHelper.getAllIngredients()) {
                    while (pantry.moveToNext()) {
                        String pantryName = pantry.getString(
                                pantry.getColumnIndexOrThrow("name"));
                        double pantryQuantity = pantry.getDouble(
                                pantry.getColumnIndexOrThrow("quantity"));
                        String pantryUnit = pantry.getString(
                                pantry.getColumnIndexOrThrow("unit"));

                        boolean sameName = pantryName.trim()
                                .equalsIgnoreCase(requiredName.trim());
                        boolean sameUnit = pantryUnit.trim()
                                .equalsIgnoreCase(requiredUnit.trim());

                        if (sameName && sameUnit
                                && pantryQuantity >= requiredQuantity) {
                            foundEnough = true;
                            break;
                        }
                    }
                }

                if (!foundEnough) {
                    return false;
                }

            } while (required.moveToNext());
        }

        return hasIngredients;
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}