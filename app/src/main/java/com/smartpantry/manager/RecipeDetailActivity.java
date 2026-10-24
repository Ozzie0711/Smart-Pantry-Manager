package com.smartpantry.manager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView txtRecipeName = findViewById(R.id.txtRecipeName);
        TextView txtRecipeIngredients = findViewById(R.id.txtRecipeIngredients);
        TextView txtRecipeInstructions = findViewById(R.id.txtRecipeInstructions);
        Button btnBack = findViewById(R.id.btnBackToRecipes);

        databaseHelper = new DatabaseHelper(this);

        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        if (recipeId == -1) {
            Toast.makeText(this, "Recipe could not be opened", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String recipeName = "";
        String instructions = "";

        try (Cursor recipes = databaseHelper.getAllRecipes()) {
            while (recipes.moveToNext()) {
                if (recipes.getInt(recipes.getColumnIndexOrThrow("id")) == recipeId) {
                    recipeName = recipes.getString(
                            recipes.getColumnIndexOrThrow("name"));
                    instructions = recipes.getString(
                            recipes.getColumnIndexOrThrow("instructions"));
                    break;
                }
            }
        }

        StringBuilder ingredientsText = new StringBuilder();

        try (Cursor ingredients = databaseHelper.getRecipeIngredients(recipeId)) {
            while (ingredients.moveToNext()) {
                String name = ingredients.getString(
                        ingredients.getColumnIndexOrThrow("ingredient_name"));
                double quantity = ingredients.getDouble(
                        ingredients.getColumnIndexOrThrow("required_quantity"));
                String unit = ingredients.getString(
                        ingredients.getColumnIndexOrThrow("unit"));

                ingredientsText.append("• ")
                        .append(name)
                        .append(": ")
                        .append(quantity)
                        .append(" ")
                        .append(unit)
                        .append("\n");
            }
        }

        txtRecipeName.setText(recipeName);
        txtRecipeIngredients.setText(ingredientsText.toString());
        txtRecipeInstructions.setText(instructions);

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}