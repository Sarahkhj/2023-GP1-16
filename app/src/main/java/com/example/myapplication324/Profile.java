package com.example.myapplication324;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication324.databinding.ActivityProfileBinding;
import com.example.myapplication324.databinding.ActivityShareBinding;
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

    private TextView t1;
    private TextView t2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(activityProfileBinding.getRoot());
        allocateActivityTitle("Profile");

        // Initializing views
        t1 = findViewById(R.id.amazonName); // Assuming this is the TextView for the email
        t2 = findViewById(R.id.amazonDesc); // Assuming this is the TextView to display the username

        auth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        mDatabase = rootNode.getReference("users"); // Replace "users" with your database reference

        if (auth.getCurrentUser() != null) {
            String userEmail = auth.getCurrentUser().getEmail();
            t2.setText(userEmail);

            // Querying the database for username using email
            mDatabase.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Retrieving the username associated with the email
                            String username = snapshot.child("username").getValue(String.class);
                            t1.setText(username); // Setting the username in the TextView
                        }
                    } else {
                        StyleableToast.makeText(Profile.this, "No username found for this email", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error in fetching data
                    StyleableToast.makeText(Profile.this, "Database Error", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            });
        } else {
            StyleableToast.makeText(Profile.this, "Error: No user found!", Toast.LENGTH_SHORT, R.style.mytoast).show();
        }
    }
}