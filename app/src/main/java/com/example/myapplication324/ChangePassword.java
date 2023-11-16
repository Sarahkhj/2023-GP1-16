package com.example.myapplication324;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myapplication324.databinding.ActivityChangePasswordBinding;
import com.example.myapplication324.databinding.ActivitySearchBinding;
import com.example.myapplication324.databinding.ActivityShareBinding;

public class ChangePassword extends DrawerBaseActivity {
    ActivityChangePasswordBinding activityChangePasswordBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChangePasswordBinding= ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(activityChangePasswordBinding.getRoot());
        allocateActivityTitle("Change Password");
    }
}