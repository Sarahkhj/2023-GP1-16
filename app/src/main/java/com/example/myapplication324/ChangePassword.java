package com.example.myapplication324;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication324.databinding.ActivityChangePasswordBinding;
import com.example.myapplication324.databinding.ActivitySearchBinding;
import com.example.myapplication324.databinding.ActivityShareBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

public class ChangePassword extends DrawerBaseActivity {
    ActivityChangePasswordBinding activityChangePasswordBinding;

    private Button update;
    private EditText old;
    private  EditText newpass;
    private EditText confirmpass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChangePasswordBinding= ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(activityChangePasswordBinding.getRoot());
        allocateActivityTitle("Change Password");
        old=findViewById(R.id.oldpass);
        newpass=findViewById(R.id.newpass);
        confirmpass=findViewById(R.id.conpass);

        update=findViewById(R.id.update_pass);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = old.getText().toString();
                String newPassword = newpass.getText().toString();
                String confirmPassword = confirmpass.getText().toString();

                // Check if new password meets the criteria
                if (isValidPassword(newPassword)) {
                    // Make sure the new password and confirm password fields match
                    if (newPassword.equals(confirmPassword)) {
                        // Get the current user
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

                        // Check if the user is logged in
                        if (user != null) {
                            // Get the user's email
                            String email = user.getEmail();

                            // Create the credential using the email and old password
                            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

                            // Reauthenticate the user with the credential
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // If reauthentication is successful, update the user's password
                                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Password update in Firebase Authentication successful
                                                    // Update the password in the Realtime Database
                                                    mDatabase.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                                                String userKey = userSnapshot.getKey();
                                                                mDatabase.child(userKey).child("password").setValue(newPassword);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            // Handle onCancelled
                                                        }
                                                    });

                                                    StyleableToast.makeText(ChangePassword.this, "Password updated successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                                    openhome();
                                                } else {
                                                    // Password update in Firebase Authentication failed
                                                    StyleableToast.makeText(ChangePassword.this, "Failed to update password. Please try again", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                                }
                                            }
                                        });
                                    } else {
                                        // Reauthentication failed, display incorrect old password message
                                        StyleableToast.makeText(ChangePassword.this, "Incorrect old password. Please try again", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                    }
                                }
                            });
                        }
                    } else {
                        // New password and confirm password fields do not match
                        StyleableToast.makeText(ChangePassword.this, "New password and confirm password do not match", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    }
                } else {
                    // New password does not meet the criteria
                    newpass.setError("Password must be at least 8 characters and meet certain criteria");
                }
            }
        });    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 && password.length() <= 15
                && !password.equals(password.toLowerCase()) && !password.equals(password.toUpperCase())
                && password.matches(".*\\d.*") && password.matches(".*[./].*");
    }

    private void openhome(){
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

}