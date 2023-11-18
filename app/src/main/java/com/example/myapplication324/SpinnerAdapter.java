package com.example.myapplication324;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<Custom_spinner> {
    public SpinnerAdapter(@NonNull Context context, ArrayList<Custom_spinner>customList) {
        super(context, 0, customList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customView(position,convertView,parent);
    }

    public View customView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.custom_spinner_layout,parent,false
            );
        }
        Custom_spinner items=getItem(position);
        ImageView spinnerImage = convertView.findViewById(R.id.image_view_colors);
        TextView spinnername = convertView.findViewById(R.id.textspinner);
        if (items != null) {
            spinnerImage.setImageResource(items.getSpinnerImage());
            spinnername.setText(items.getSpinnerText());
        }
        return convertView;
    }
}
