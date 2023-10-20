package com.example.myapplication324;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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

public class Login extends AppCompatActivity {

    private TextView t1;
    private TextView t2;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    Button SignInButton;

    private EditText email, password;
    private FirebaseAuth auth;

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        t1 = findViewById(R.id.signup);
        t2 = findViewById(R.id.forget);

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
                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    showBiometricPrompt(); // Show the biometric prompt on success
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        // Checking biometric authentication availability
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                Log.e("MY_APP_TAG", "please enroll your fingerprint in the settings");
                break;
        }

        // Initialize biometric prompt
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(Login.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getApplicationContext(), Home.class);
                startActivity(intent1);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Configure biometric prompt
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Sign in")
                .setSubtitle("Touch the fingerprint sensor to continue")
                .setNegativeButtonText("Cancel")
                .build();
    }

    private boolean isCredentialsValid() {
        String emailAddress = email.getText().toString().trim();
        String pass = password.getText().toString();
        if (TextUtils.isEmpty(emailAddress) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            email.setError("Enter valid email!");
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

    private void showBiometricPrompt() {
        // Show the biometric authentication prompt
        biometricPrompt.authenticate(promptInfo);
    }
}