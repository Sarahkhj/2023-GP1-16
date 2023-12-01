package com.example.myapplication324;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.github.muddz.styleabletoast.StyleableToast;

public class Login2 extends AppCompatActivity {
    private TextView userEmailTextView,pass, forget;
    private WheelView wheelView;
    private String password,userfinal;
    private Button login;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private int usertry=0;
    private FingerPrintAuthenticator fingerprintAuthenticator;



    private int scolor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        userEmailTextView = findViewById(R.id.email);
        forget = findViewById(R.id.forget);
        login = findViewById(R.id.sign);
        pass = findViewById(R.id.pass);
        wheelView = findViewById(R.id.wheelView);
        auth = FirebaseAuth.getInstance();
        wheelView.setTextView(pass); // Set the TextView in the WheelView
        reference = FirebaseDatabase.getInstance().getReference("users");
        fingerprintAuthenticator = new FingerPrintAuthenticator(Login2.this, () -> startActivity(new Intent(Login2.this, Home.class)));


        forget.setOnClickListener(view -> openforget());

        // Retrieve the user's email from the Intent
        Intent intent = getIntent();
        if (intent != null) {
            String userEmail = intent.getStringExtra("USER_EMAIL");

            if (userEmail != null) {
                // Display the user's email in a TextView or perform other actions
                userEmailTextView.setText(userEmail);
                /// password = "Ss123456.";
                //scolor = Color.RED;
                //
                getUserData(userEmail, (userName, userColor) -> {
                    if (userName != null&& userColor!=null) {
                        // Do something with the color
                        scolor = getColorInt(userColor);
                        password = userName;
                        //  StyleableToast.makeText(Login2.this, userName, Toast.LENGTH_SHORT, R.style.mytoast).show();
                        // StyleableToast.makeText(Login2.this, userColor, Toast.LENGTH_SHORT, R.style.mytoast).show();
                        // Set onTouchListener for the WheelView
                        wheelView.setOnTouchListener((v, event) -> {
                            wheelView.onTouchEvent(event, scolor,password);
                            return true; // Return true to indicate that the event has been handled
                        });


                        login.setOnClickListener(view -> {
                            // Check if the password is not empty
                            if (!TextUtils.isEmpty(pass.getText())) {
                                userfinal = pass.getText().toString().trim();
                                if (userfinal.equals(password)) {
                                    // Use Firebase Authentication to sign in
                                    auth.signInWithEmailAndPassword(userEmail, password)
                                            .addOnSuccessListener(authResult -> {
                                                // Handle successful login
                                                fingerprintAuthenticator.showSignInBiometricPrompt();
                                                //startActivity(new Intent(Login2.this, Home.class));

                                                // Show the biometric prompt on success
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle login failure
                                                usertry++;
                                                if (usertry == 3) {
                                                    wheelView.showAlertDialog("Max Attempts Exceeded", "You have exceeded the maximum number of login attempts. Do you want to reset your password?",Forgetpassword.class);

                                                } else {
                                                    pass.setText("");
                                                    userfinal = "";
                                                    StyleableToast.makeText(Login2.this, "Incorrect password", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                                }
                                            });
                                } else {
                                    usertry++;
                                    if (usertry == 3) {
                                        wheelView.showAlertDialog("Max Attempts Exceeded", "You have exceeded the maximum number of login attempts. Do you want to reset your password?",Forgetpassword.class);

                                    } else {
                                        pass.setText("");
                                        userfinal = "";
                                        StyleableToast.makeText(Login2.this, "Incorrect password", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                    }

                                }
                            } else {
                                // Handle the case where the password is empty
                                // You might want to show an error message or do something else
                                StyleableToast.makeText(Login2.this, "Please enter a password", Toast.LENGTH_SHORT, R.style.mytoast).show();
                            }
                        });



                    } else {
                        StyleableToast.makeText(Login2.this, "No color", Toast.LENGTH_SHORT, R.style.mytoast).show();


                    }
                });








            }

        }
    }
    public void rotateClockwise(View view) {
        wheelView.rotateClockwise();
    }

    public void rotateCounterClockwise(View view) {
        wheelView.rotateCounterClockwise();
    }

    public void openforget() {
        Intent intent = new Intent(this, Forgetpassword.class);
        startActivity(intent);
    }





    private void getColorAttribute(String userEmail, final ColorCallback callback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the user object
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Assuming there is a 'color' attribute in the user data
                        String color = userSnapshot.child("color").getValue(String.class);
                        callback.onColorReceived(color);
                        return;
                    }
                } else {
                    // User with the specified email not found
                    callback.onColorReceived(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                callback.onColorReceived(null);
            }
        });
    }
    private void getUserData(String userEmail, final UserDataCallback callback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the user object
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Assuming there are 'name' and 'color' attributes in the user data
                        String userName = userSnapshot.child("password").getValue(String.class);
                        String userColor = userSnapshot.child("color").getValue(String.class);
                        callback.onUserDataReceived(userName, userColor);
                        return;
                    }
                } else {
                    // User with the specified email not found
                    callback.onUserDataReceived(null, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                callback.onUserDataReceived(null, null);
            }
        });
    }

    public interface UserDataCallback {
        void onUserDataReceived(String userName, String userColor);
    }

    // Callback interface to handle the result asynchronously
    public interface ColorCallback {
        void onColorReceived(@Nullable String color);
    }

    public static int getColorInt(String colorName) {
        // Convert color name to integer representation
        int colorInt;

        switch (colorName.toLowerCase()) {
            case "blue":
                colorInt = Color.BLUE;
                break;
            case "green":
                colorInt = Color.GREEN;
                break;
            case "yellow":
                colorInt = Color.YELLOW;
                break;
            case "magenta":
                colorInt = Color.MAGENTA;
                break;
            case "cyan":
                colorInt = Color.CYAN;
                break;
            case "red":
                colorInt = Color.RED;
                break;
            case "purple":
                colorInt = Color.rgb(128, 0, 128); // RGB value for purple
                break;
            case "darkgray":
                colorInt = Color.DKGRAY;
                break;
            default:
                colorInt = Color.BLACK; // Default to black if the color name is not recognized
                break;
        }

        return colorInt;
    }




}