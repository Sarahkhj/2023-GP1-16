package com.example.myapplication324;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

import io.github.muddz.styleabletoast.StyleableToast;

public class Login extends AppCompatActivity {

    private TextView t1;
    private TextView t2;

    private Executor executor;

    private FingerPrintAuthenticator fingerprintAuthenticator;


    Button SignInButton;

    private EditText email, password;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        t1 = findViewById(R.id.signup);
        t2 = findViewById(R.id.forget);
        fingerprintAuthenticator = new FingerPrintAuthenticator(Login.this, new FingerPrintAuthenticator.AuthenticationCallback() {
            @Override
            public void onAuthenticationSuccess() {

            }
        });
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensignup();
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openforget();
            }
        });

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();

        SignInButton = findViewById(R.id.sign);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

//        db = new DBHelper(this);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAsText = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                // Validate credentials
                if (isCredentialsValid()) {
                    // Use Firebase Authentication to sign in
                    auth.signInWithEmailAndPassword(emailAsText, pass)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                 //   StyleableToast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT,R.style.mytoast).show();
                                    fingerprintAuthenticator.showSignInBiometricPrompt();
                                    // Show the biometric prompt on success

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    StyleableToast.makeText(Login.this, "Incorrect email or password", Toast.LENGTH_SHORT,R.style.mytoast).show();
                                }
                            });
                }
            }
        });



    }

    private boolean isCredentialsValid() {
        String emailAddress = email.getText().toString().trim();
        String pass = password.getText().toString();
        if (TextUtils.isEmpty(emailAddress) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            email.setError("Enter a valid email address");
            return false;
        } else if (TextUtils.isEmpty(pass)) {
            password.setError("Enter your password");
            return false;
        }
        return true;
    }

    public void opensignup() {
        Intent intent = new Intent(this, Sign_up.class);
        startActivity(intent);
    }

    public void openforget() {
        Intent intent = new Intent(this, Forgetpassword.class);
        startActivity(intent);
    }

//    private void showBiometricPrompt() {
//        // Show the biometric authentication prompt
//        biometricPrompt.authenticate(promptInfo);
//    }


}