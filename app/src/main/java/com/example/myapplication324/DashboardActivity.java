package com.example.myapplication324;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myapplication324.databinding.ActivityDashboardBinding;

public class DashboardActivity extends DrawerBaseActivity {


    ActivityDashboardBinding activityDashboardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());
        allocateActivityTitle("Dashboard");


    }


}