package com.example.myapplication324;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.github.muddz.styleabletoast.StyleableToast;


public class UserHelperClass {

    String username, email, PhoneNum, password,color;
    private static String usersTable="usersTable";
    FirebaseAuth auth;
    DatabaseReference mDatabase;
    public UserHelperClass() {
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    public void getUserProfile(Activity activity, TextView usernameTextView, TextView emailTextView,TextView P) {

        if(auth.getCurrentUser() != null) {

            String userEmail = auth.getCurrentUser().getEmail();

            mDatabase.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String phone=snapshot.child("phoneNum").getValue(String.class);


                            String username = snapshot.child("username").getValue(String.class);
                            usernameTextView.setText(username);
                            emailTextView.setText(userEmail);
                            P.setText(phone);

                        }

                    } else {

                        StyleableToast.makeText(activity, "No username found for this email", Toast.LENGTH_SHORT, R.style.mytoast).show();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    StyleableToast.makeText(activity, "Database Error", Toast.LENGTH_SHORT, R.style.mytoast).show();

                }
            });

        } else {

            StyleableToast.makeText(activity, "Error: No user found!", Toast.LENGTH_SHORT, R.style.mytoast).show();

        }

    }
    //change password
    public void updatePassword(String email, String oldPassword, String newPassword, Custom_spinner clickedcolor, final OnCompleteListener<Void> onCompleteListener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the user is logged in
        if (user != null) {
            // Get the user's email
            String userEmail = user.getEmail();

            // Create the credential using the email and old password
            AuthCredential credential = EmailAuthProvider.getCredential(userEmail, oldPassword);

            // Reauthenticate the user with the credential
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // If reauthentication is successful, update the user's password in Firebase Authentication
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
                                                mDatabase.child(userKey).child("color").setValue(clickedcolor.getSpinnerText());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle onCancelled
                                        }
                                    });

                                    onCompleteListener.onComplete(task);
                                } else {
                                    // Password update in Firebase Authentication failed
                                    onCompleteListener.onComplete(task);
                                }
                            }
                        });
                    } else {
                        // Reauthentication failed
                        onCompleteListener.onComplete(task);
                    }
                }
            });
        }
    }


    public void updateProfile(Activity activity, EditText username, EditText phoneNum) {
        String updatedUsername = username.getText().toString().trim();

        String updatedPhoneNum = phoneNum.getText().toString().trim();

        // Input validation
        if (updatedUsername.isEmpty()) {
            // Show error message to the user indicating missing fields
            StyleableToast.makeText(activity, "Please enter a username", Toast.LENGTH_SHORT, R.style.mytoast).show();
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
                            StyleableToast.makeText(activity, "Username updated successfully!", Toast.LENGTH_SHORT, R.style.mytoast).show();

                        }

                        // Check if the phone number is changing
                        String currentPhoneNum = userSnapshot.child("phoneNum").getValue(String.class);
                        if (!currentPhoneNum.equals(updatedPhoneNum)) {
                            // Check if the new phone number already exists
                            mDatabase.orderByChild("phoneNum").equalTo(updatedPhoneNum).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        StyleableToast.makeText(activity, "Phone already exists!", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                        // Revert to the original phone number
                                        phoneNum.setText(currentPhoneNum);
                                    } else {
                                        // Update the phone number
                                        mDatabase.child(userKey).child("phoneNum").setValue(updatedPhoneNum);
                                        StyleableToast.makeText(activity, "Phone number updated successfully!", Toast.LENGTH_SHORT, R.style.mytoast).show();

                                        // After updating phone number, check and update email

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle error
                                }
                            });
                        } else {
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


    public UserHelperClass (Sign_up signUp) {

    }

    public void saveUserToDatabase(DatabaseReference reference, FirebaseAuth auth, final Activity activity) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference userReference = reference.push();
                    userReference.child("username").setValue(username);
                    userReference.child("password").setValue(password);
                    userReference.child("email").setValue(email);
                    userReference.child("phoneNum").setValue(PhoneNum);
                    userReference.child("color").setValue(color);

                    activity.startActivity(new Intent(activity, Home.class));
                } else {
                    StyleableToast.makeText(activity, "SignUp Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            }
        });
    }



    public UserHelperClass(String username, String password, String email, String phone, String color) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.PhoneNum = phone;
        this.color = color;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

