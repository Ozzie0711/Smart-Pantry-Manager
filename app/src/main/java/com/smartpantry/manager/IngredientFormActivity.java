package com.smartpantry.manager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class IngredientFormActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText inputName;
    private EditText inputQuantity;
    private EditText inputUnit;

    private int ingredientId = -1;
    private String expiryDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_form);

        inputName = findViewById(R.id.inputIngredientName);
        inputQuantity = findViewById(R.id.inputIngredientQuantity);
        inputUnit = findViewById(R.id.inputIngredientUnit);

        Button btnSave = findViewById(R.id.btnSaveIngredient);
        Button btnCancel = findViewById(R.id.btnCancelIngredient);

        databaseHelper = new DatabaseHelper(this);

        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        if (ingredientId != -1) {
            setTitle("Edit Ingredient");
            loadIngredient();
        } else {
            setTitle("Add Ingredient");
        }

        btnSave.setOnClickListener(v -> saveIngredient());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadIngredient() {
        try (Cursor cursor = databaseHelper.getAllIngredients()) {
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                        == ingredientId) {

                    inputName.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("name")));

                    inputQuantity.setText(String.valueOf(cursor.getDouble(
                            cursor.getColumnIndexOrThrow("quantity"))));

                    inputUnit.setText(cursor.getString(
                            cursor.getColumnIndexOrThrow("unit")));

                    expiryDate = cursor.getString(
                            cursor.getColumnIndexOrThrow("expiry_date"));

                    return;
                }
            }
        }

        Toast.makeText(this, "Ingredient not found",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveIngredient() {
        String name = inputName.getText().toString().trim();
        String quantityText = inputQuantity.getText().toString().trim();
        String unit = inputUnit.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "Please complete all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid quantity",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Double.isFinite(quantity) || quantity <= 0) {
            Toast.makeText(this, "Quantity must be greater than zero",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success;

        if (ingredientId == -1) {
            success = databaseHelper.addIngredient(
                    name, quantity, unit, null);
        } else {
            success = databaseHelper.updateIngredient(
                    ingredientId, name, quantity, unit, expiryDate);
        }

        if (success) {
            Toast.makeText(this,
                    ingredientId == -1
                            ? "Ingredient added"
                            : "Ingredient updated",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save ingredient",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}