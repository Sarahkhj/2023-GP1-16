package com.example.myapplication324;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myapplication324.databinding.ActivityFavoriteBinding;

public class Favorite extends DrawerBaseActivity {

    ActivityFavoriteBinding activityFavoriteBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFavoriteBinding= ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(activityFavoriteBinding.getRoot());
        allocateActivityTitle("Favorite");

    }
}