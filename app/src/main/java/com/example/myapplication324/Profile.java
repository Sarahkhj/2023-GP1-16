package com.example.myapplication324;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication324.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.github.muddz.styleabletoast.StyleableToast;

public class Profile extends  DrawerBaseActivity {
    ActivityProfileBinding activityProfileBinding;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private String rtvFullName;
    private DatabaseReference mDatabase;

    private EditText username , email,PhoneNum;
    private Button pass;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(activityProfileBinding.getRoot());
        allocateActivityTitle("Profile");

        // Initializing views
        username = findViewById(R.id.profileName); // Assuming this is the TextView for the email
        email = findViewById(R.id.profile_email); // Assuming this is the TextView to display the username
        PhoneNum=findViewById(R.id.editTextPhone);
        pass= findViewById(R.id.updatepassword);
        UserHelperClass userHelper = new UserHelperClass();

        userHelper.getUserProfile(this, username, email,PhoneNum);

        pass.setOnClickListener(new View.OnClickListener() { // open change password
            @Override
            public void onClick(View view) {
                openchangepass();
            }
        });

    }

    public void openchangepass() {
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }}