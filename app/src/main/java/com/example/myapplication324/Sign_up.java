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
import com.google.firebase.database.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.github.muddz.styleabletoast.StyleableToast;

public class Sign_up extends AppCompatActivity {
    private TextView login;
    private EditText username, email, PhoneNum, password, rePassword;
    private Button sign;

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");

        login = findViewById(R.id.login);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        PhoneNum = findViewById(R.id.PhoneNum);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.rePassword);
        sign = findViewById(R.id.sign);

        // Checking biometric authentication availability
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
        } else {
            Log.e("MY_APP_TAG", "Biometric authentication is not available.");
        }

        // Initialize biometric prompt
        biometricPrompt = new BiometricPrompt(Sign_up.this, ContextCompat.getMainExecutor(this), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                StyleableToast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT, R.style.mytoast).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                handleAuthenticationSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                StyleableToast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        });

        // Configure biometric prompt
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Sign up")
                .setSubtitle("Register your fingerprint to sign up")
                .setNegativeButtonText("Cancel")
                .build();

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSignUp();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogin();
            }
        });
    }

    private void handleSignUp() {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        String rePass = rePassword.getText().toString();
        String mail = email.getText().toString();
        String Phone = PhoneNum.getText().toString();

        if (user.isEmpty() || pass.isEmpty() || rePass.isEmpty() || mail.isEmpty() || Phone.isEmpty()) {
            StyleableToast.makeText(Sign_up.this, "Please enter all the fields", Toast.LENGTH_LONG, R.style.mytoast).show();
        } else if (!pass.equals(rePass)) {
            rePassword.setError("Passwords do not match");
        } else if (mail.equals(Phone)) {
            StyleableToast.makeText(Sign_up.this, "Email should not be equal to the phone number", Toast.LENGTH_SHORT, R.style.mytoast).show();
        } else {
            if (isValidEmail(mail) && isValidPhoneNumber(Phone) && isValidPassword(pass)) {
                // Check if phone number already exists
                reference.orderByChild("phoneNum").equalTo(PhoneNum.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            StyleableToast.makeText(Sign_up.this, "Phone number already exists! Please use another number.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                        } else {
                            // Check if email already exists
                            reference.orderByChild("email").equalTo(email.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        StyleableToast.makeText(Sign_up.this, "Email already exists! Please use another email.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                    } else {
                                        // If phone number and email are unique, proceed with biometric authentication
                                        authenticateWithBiometrics();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle database error
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
            } else {
                // Handle validation errors
                StringBuilder errorMessage = new StringBuilder("Invalid format: ");
                if (!isValidEmail(mail)) {
                    email.setError("Enter a valid email address");
                }
                if (!isValidPhoneNumber(Phone)) {
                    PhoneNum.setError("Phone number must be 10 digits and start with 05");
                }
                if (!isValidPassword(pass)) {
                    password.setError("Password must be at least 8 characters and meet certain criteria");
                }
            }
        }
    }

    private void handleAuthenticationSuccess() {
        auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    UserHelperClass helperClass = new UserHelperClass(username.getText().toString(),password.getText().toString(), email.getText().toString(), PhoneNum.getText().toString());
                    reference.push().setValue(helperClass);
                    startActivity(new Intent(Sign_up.this, Home.class));
                } else {
                    StyleableToast.makeText(Sign_up.this, "SignUp Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "(?i)[a-z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.length() == 10 && phoneNumber.startsWith("05");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 && password.length() <= 15
                && !password.equals(password.toLowerCase()) && !password.equals(password.toUpperCase())
                && password.matches(".*\\d.*") && password.matches(".*[./].*");
    }

    private void authenticateWithBiometrics() {
        biometricPrompt.authenticate(promptInfo);
    }

    private void openLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}

