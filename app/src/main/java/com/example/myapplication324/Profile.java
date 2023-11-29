package com.example.myapplication324;

import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication324.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private EditText username, email, PhoneNum;
    private Button pass;
    private Button update;


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
        UserHelperClass userHelper = new UserHelperClass();

        userHelper.getUserProfile(this, username, email, PhoneNum);

        pass.setOnClickListener(new View.OnClickListener() { // open change password
            @Override
            public void onClick(View view) {
                openchangepass();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();


            }
        });
    }

    public void openchangepass() {
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }


    public void updateProfile() {
        String updatedUsername = username.getText().toString().trim();
        String updatedEmail = email.getText().toString().trim();
        String updatedPhoneNum = PhoneNum.getText().toString().trim();

        // Input validation
        if (updatedUsername.isEmpty()) {
            // Show error message to the user indicating missing fields
            StyleableToast.makeText(Profile.this, "Please enter a username", Toast.LENGTH_SHORT, R.style.mytoast).show();
            return;
        }

        // Firebase instances
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        if (user != null) {
            String userEmail = user.getEmail();

            mDatabase.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userKey = userSnapshot.getKey();

                        // Check if the username is actually changing
                        String currentUsername = userSnapshot.child("username").getValue(String.class);
                        if (!currentUsername.equals(updatedUsername)) {
                            // Update the username
                            mDatabase.child(userKey).child("username").setValue(updatedUsername);
                        }

                        // Check if the phone number is changing
                        String currentPhoneNum = userSnapshot.child("phoneNum").getValue(String.class);
                        if (!currentPhoneNum.equals(updatedPhoneNum)) {
                            // Check if the new phone number already exists
                            mDatabase.orderByChild("phoneNum").equalTo(updatedPhoneNum).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        StyleableToast.makeText(Profile.this, "Phone already exists!", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                    } else {
                                        // Update the phone number
                                        mDatabase.child(userKey).child("phoneNum").setValue(updatedPhoneNum);
                                        // After updating phone number, check and update email
                                        checkAndUpdateEmail(user, updatedEmail);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle error
                                }
                            });
                        } else {
                            // If phone number is not changing, check and update email
                            checkAndUpdateEmail(user, updatedEmail);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private void checkAndUpdateEmail(FirebaseUser user, String updatedEmail) {
        String userEmail = user.getEmail();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Check if the email is changing
        if (!userEmail.equals(updatedEmail)) {
            // Check if the new email already exists
            mDatabase.orderByChild("email").equalTo(updatedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        StyleableToast.makeText(Profile.this, "Email already exists!", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    } else {
                        // Update the email in Firebase Authentication
                        user.updateEmail(updatedEmail)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Profile update successful
                                        showCompletionMessage();
                                    } else {
                                        // Profile update failed
                                        String errorMessage = task.getException().getMessage();
                                        StyleableToast.makeText(Profile.this, "Failed to update profile: " + errorMessage, Toast.LENGTH_SHORT, R.style.mytoast).show();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        } else {
            // If email is not changing, show completion message
            showCompletionMessage();
        }
    }

    private void showCompletionMessage() {
        StyleableToast.makeText(Profile.this, "Profile updated successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();
    }
}

