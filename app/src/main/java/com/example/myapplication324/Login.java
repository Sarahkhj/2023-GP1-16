package com.example.myapplication324;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.core.content.ContextCompat;
import androidx.biometric.BiometricPrompt;
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

import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private TextView t1;
    private TextView t2;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    Button SignInButton;

 private EditText email,password;

 DBHelper db;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        t1 = (TextView) findViewById(R.id.signup);
        t2 = (TextView) findViewById(R.id.forget);

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opensignup();
            }
        });
        t2.setOnClickListener(new View.OnClickListener() {//makes the signup text clickable
            @Override
            public void onClick(View view) {
                openforget();
            }
        });

        /////////////

        SignInButton = findViewById(R.id.sign);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);

        db = new DBHelper(this);
        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String emailAsText= email.getText().toString().trim();
                String pass=password.getText().toString().trim();
                isCredentialsValid();
                if (!db.checkusername(emailAsText)){
                    Toast.makeText(Login.this, "Account does not exist", Toast.LENGTH_SHORT).show();
                }
                else{
                    // Prompt appears when user clicks "Log in".
                    // Consider integrating with the keystore to unlock cryptographic operations,
                    // if needed by your app.
                    biometricPrompt.authenticate(promptInfo);





                }





            }

        });

//checking biometric authentication is available
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
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
                Toast.makeText(Login.this, "no fingerprint assigned go to settings ", Toast.LENGTH_SHORT).show();
                break;
        }
////Display the login prompt

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(Login.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getApplicationContext(), Home.class);
                startActivity(intent1);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();








    }
    private boolean isCredentialsValid() {
        String emailAddress = email.getText().toString().trim();
        String pass = password.getText().toString();
        if (TextUtils.isEmpty(emailAddress)
                || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            email.setError("Enter valid email!");
            return false;
        } else if (TextUtils.isEmpty(pass)) {
            password.setError("Enter your password");
            return false;
        }
        return true;
    }
  //  @alanoudnasser



    public void opensignup(){
        Intent intent = new Intent(this, Sign_up.class);
        startActivity(intent);
    }
    public void openforget(){
        Intent intent = new Intent(this, Forgetpassword.class);
        startActivity(intent);
    }


}