package com.example.myapplication324;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.myapplication324.databinding.ActivityProfileBinding;


public class Profile extends  DrawerBaseActivity {
    ActivityProfileBinding activityProfileBinding;


    private EditText username, PhoneNum;
    private TextView email;
    private Button pass;
    private Button update;
    private UserHelperClass userHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(activityProfileBinding.getRoot());
        allocateActivityTitle("Profile");

        // Initializing views
        username = findViewById(R.id.profileName); // Assuming this is the TextView for the email
        email = findViewById(R.id.profile_email); // Assuming this is the TextView to display the username
        PhoneNum = findViewById(R.id.editTextPhone);
        pass = findViewById(R.id.updatepassword);
        update = findViewById(R.id.update1);
        userHelper = new UserHelperClass();

        userHelper.getUserProfile(this, username, email, PhoneNum);

        pass.setOnClickListener(new View.OnClickListener() { // open change password
            @Override
            public void onClick(View view) {
                openChangePass();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userHelper.updateProfile(Profile.this, username, PhoneNum);
            }
        });
    }

    public void openChangePass() {
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }
}

