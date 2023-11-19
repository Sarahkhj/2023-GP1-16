package com.example.myapplication324;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
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
    public void changePassword(String oldPassword, String newPassword, final ChangePasswordCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    callback.onPasswordChanged(true, "Password updated successfully");
                                } else {
                                    callback.onPasswordChanged(false, "Failed to update password. Please try again");
                                }
                            }
                        });
                    } else {
                        callback.onPasswordChanged(false, "Incorrect old password. Please try again");
                    }
                }
            });
        }
    }

    public UserHelperClass (Sign_up signUp) {

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

