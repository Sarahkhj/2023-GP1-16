package com.example.myapplication324;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.muddz.styleabletoast.StyleableToast;

public class Sign_up extends AppCompatActivity {
    private TextView login;
    private EditText username, email, PhoneNum, password, rePassword;
    private Button sign;
    private DBHelper DB;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private UserHelperClass helperClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        login = findViewById(R.id.login);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        PhoneNum = findViewById(R.id.PhoneNum);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);
        sign = findViewById(R.id.sign);
        DB = new DBHelper(this);
        helperClass = new UserHelperClass(this);

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

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String rePass = rePassword.getText().toString();
                String mail = email.getText().toString();
                String Phone = PhoneNum.getText().toString();

                if (user.equals("") || pass.equals("") || rePass.equals("") || mail.equals("") || Phone.equals("")) {
                    //Toast.makeText(Sign_up.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                    StyleableToast.makeText(Sign_up.this, "Please enter all the fields", Toast.LENGTH_LONG, R.style.mytoast).show();

                } else if (!pass.equals(rePass)) {
                    // Set an error on the rePassword field
                    rePassword.setError("Passwords do not match");
                } else {
                    boolean validEmail = isValidEmail(mail);
                    boolean validPhone = isValidPhoneNumber(Phone);
                    boolean validPassword = isValidPassword(pass);

                    // Check if the user already exists
                    boolean checkEmail = DB.checkEmail(mail);
                    boolean checkPhone = DB.checkPhoneNumber(Phone);

                    if (checkEmail) {
                        StyleableToast.makeText(Sign_up.this, "User already exists! Please sign in", Toast.LENGTH_SHORT,R.style.mytoast).show();
                    } else if (checkPhone) {
                        StyleableToast.makeText(Sign_up.this, "Phone number already exists! Please use another number.", Toast.LENGTH_SHORT,R.style.mytoast).show();
                    } else if (validEmail && validPhone && validPassword) {
                        biometricPrompt = new BiometricPrompt(Sign_up.this, executor, new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);
                                StyleableToast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT,R.style.mytoast).show();
                            }

                            @Override
                            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                StyleableToast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT,R.style.mytoast).show();
                                auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            StyleableToast.makeText(Sign_up.this, "Signup Successful", Toast.LENGTH_SHORT,R.style.mytoast).show();
                                            rootNode = FirebaseDatabase.getInstance();
                                            reference = rootNode.getReference("users");
                                            UserHelperClass helperClass = new UserHelperClass(user, mail, Phone);
                                            reference.push().setValue(helperClass);
                                            startActivity(new Intent(Sign_up.this, Home.class));
                                        } else {
                                            StyleableToast.makeText(Sign_up.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT,R.style.mytoast).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                StyleableToast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT,R.style.mytoast).show();
                            }
                        });

                        biometricPrompt.authenticate(promptInfo);
                    } else {
                        StringBuilder errorMessage = new StringBuilder("Invalid format: ");
                        if (!validEmail) {
                            email.setError("Email ");
                        }
                        if (!validPhone) {
                            PhoneNum.setError("Phone number ");
                        }
                        if (!validPassword) {
                            password.setError("Password must be at least 8 characters, contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character.");
                        }
                    }
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openlogin();
            }
        });
        // Configure biometric prompt
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Sign up")
                .setSubtitle("Register your fingerprint to sign up")
                .setNegativeButtonText("cancel")
                .build();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "(?i)[a-z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Check if the phone number is exactly 10 digits long and starts with "05"
        return phoneNumber.length() == 10 && phoneNumber.startsWith("05");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.length() <= 15 &&
                !password.equals(password.toLowerCase()) &&
                !password.equals(password.toUpperCase()) &&
                password.matches(".*\\d.*") &&
                password.matches(".*[./].*");
    }

    public void openlogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void showBiometricPrompt() {
        // Show the biometric authentication prompt
        biometricPrompt.authenticate(promptInfo);
    }

}
