package com.smartpantry.manager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class IngredientAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<String> ingredientNames = new ArrayList<>();
    private final ArrayList<Double> quantities = new ArrayList<>();
    private final ArrayList<String> units = new ArrayList<>();

    public IngredientAdapter(Context context) {
        this.context = context;
    }

    public void updateData(Cursor cursor) {
        ingredientNames.clear();
        quantities.clear();
        units.clear();

        while (cursor.moveToNext()) {
            ingredientNames.add(
                    cursor.getString(cursor.getColumnIndexOrThrow("name"))
            );
            quantities.add(
                    cursor.getDouble(cursor.getColumnIndexOrThrow("quantity"))
            );
            units.add(
                    cursor.getString(cursor.getColumnIndexOrThrow("unit"))
            );
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ingredientNames.size();
    }

    @Override
    public Object getItem(int position) {
        return ingredientNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.item_ingredient, parent, false
            );
        }

        TextView txtName = view.findViewById(R.id.txtIngredientName);
        TextView txtQuantity = view.findViewById(R.id.txtIngredientQuantity);

        txtName.setText(ingredientNames.get(position));
        txtQuantity.setText(
                quantities.get(position) + " " + units.get(position)
        );

        return view;
    }
}
