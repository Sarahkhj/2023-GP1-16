package com.example.myapplication324;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import io.github.muddz.styleabletoast.StyleableToast;

public class Login extends AppCompatActivity {

    private TextView t1;
    private TextView t2;

    Button SignInButton;

    private EditText email, password;
    private FirebaseAuth auth;

    private DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        t1 = findViewById(R.id.signup);
       // t2 = findViewById(R.id.forget);
        reference = FirebaseDatabase.getInstance().getReference("users");


        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensignup();
            }
        });


        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();

        SignInButton = findViewById(R.id.sign);
        email = findViewById(R.id.email);
        //password = findViewById(R.id.password);

//        db = new DBHelper(this);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCredentialsValid()) {
                    doesEmailExist(new EmailCheckListener() {
                        @Override
                        public void onEmailCheckResult(boolean emailExists) {
                            if (emailExists) {
                                String emailAsText = email.getText().toString().trim();
                                // Create an Intent to start the OtherActivity
                                Intent intent = new Intent(Login.this, Login2.class);

                                // Put the user's email as an extra in the Intent
                                intent.putExtra("USER_EMAIL", emailAsText);

                                // Start the OtherActivity
                                startActivity(intent);
                            } else {
                                StyleableToast.makeText(Login.this, "Email does not exist! Please enter an existing email.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private boolean isCredentialsValid() {
        String emailAddress = email.getText().toString().trim();
       // String pass = password.getText().toString();
        if (TextUtils.isEmpty(emailAddress) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            email.setError("Enter a valid email address");
            return false;
        }
        else{
        return true;}
    }
    public void opensignup() {
        Intent intent = new Intent(this, Sign_up.class);
        startActivity(intent);
    }
    public void openforget() {
        Intent intent = new Intent(this, Forgetpassword.class);
        startActivity(intent);
    }

    private void doesEmailExist(final EmailCheckListener listener) {
        String userEmail = email.getText().toString().trim();

        reference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.onEmailCheckResult(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                listener.onEmailCheckResult(false);
            }
        });
    }

    // Define a listener interface
    interface EmailCheckListener {
        void onEmailCheckResult(boolean emailExists);
    }



}