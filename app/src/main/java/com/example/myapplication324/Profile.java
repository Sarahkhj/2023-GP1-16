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

    private EditText username , email,PhoneNum;
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
        PhoneNum=findViewById(R.id.editTextPhone);
        pass= findViewById(R.id.updatepassword);
        update=findViewById(R.id.update1);
        UserHelperClass userHelper = new UserHelperClass();

        userHelper.getUserProfile(this, username, email,PhoneNum);

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
        if (updatedUsername.isEmpty() || updatedEmail.isEmpty() || updatedPhoneNum.isEmpty()) {
            // Show error message to the user indicating missing fields
            StyleableToast.makeText(Profile.this, "Please fill in all fields", Toast.LENGTH_SHORT, R.style.mytoast).show();
            return;
        }

        // Email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(updatedEmail).matches()) {
            // Show error message to the user indicating invalid email format
            StyleableToast.makeText(Profile.this, "Invalid email format", Toast.LENGTH_SHORT, R.style.mytoast).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        if (user != null) {
          //  auth.signOut(); // Sign out the current user

            String userEmail = user.getEmail();

            mDatabase.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userKey = userSnapshot.getKey();

                        // Update the username
                        mDatabase.child(userKey).child("username").setValue(updatedUsername);

                        // Update the phone number
                        mDatabase.orderByChild("phoneNum").equalTo(PhoneNum.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                //    StyleableToast.makeText(Profile.this, "  phone already exist!", Toast.LENGTH_SHORT, R.style.mytoast).show();

                                }
                                else{
                                    mDatabase.child(userKey).child("phoneNum").setValue(updatedPhoneNum);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    // Update the email in Firebase Authentication
                    user.updateEmail(updatedEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Profile update successful
                                        StyleableToast.makeText(Profile.this, "Profile updated successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                    } else {
                                        // Profile update failed
                                        String errorMessage = task.getException().getMessage();
                                        StyleableToast.makeText(Profile.this, "Failed to update profile: " + errorMessage, Toast.LENGTH_SHORT, R.style.mytoast).show();
                                    }
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error occurred while fetching data
                    StyleableToast.makeText(Profile.this, "Failed to update profile", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            });
        }
    }
}


