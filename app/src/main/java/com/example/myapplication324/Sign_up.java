package com.example.myapplication324;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.github.muddz.styleabletoast.StyleableToast;

public class Sign_up extends AppCompatActivity  {
    private TextView login;
    private EditText username, email, PhoneNum, password, rePassword;
    private Button sign;
    private FingerPrintAuthenticator fingerprintAuthenticator;

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private Spinner spinner;
    private ArrayList<Custom_spinner> customList;
    private SpinnerAdapter spinnerAdapter;
    private Custom_spinner clickedcolor;
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
        spinner = findViewById(R.id.spinner2);

        // create spinnerItemlist for spinner
        customList=new ArrayList<>();
       // customList.add(new Custom_spinner("Black",R.drawable.black));
        customList.add(new Custom_spinner("Red",R.drawable.red));
        customList.add(new Custom_spinner("Blue",R.drawable.blue));
        customList.add(new Custom_spinner("Green",R.drawable.green));
        customList.add(new Custom_spinner("Dark gray",R.drawable.dark_gray));
        customList.add(new Custom_spinner("Purple",R.drawable.purple));
        customList.add(new Custom_spinner("Cyan",R.drawable.cyan));
        customList.add(new Custom_spinner("Magenta",R.drawable.magenta));
        customList.add(new Custom_spinner("Yellow",R.drawable.yellow));

        //create Adapter for spinner
          spinnerAdapter = new SpinnerAdapter(this,customList);

        if (spinner != null) {
            spinner.setAdapter(spinnerAdapter);


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                   clickedcolor= (Custom_spinner)adapterView.getSelectedItem();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }


        fingerprintAuthenticator = new FingerPrintAuthenticator(Sign_up.this, new FingerPrintAuthenticator.AuthenticationCallback() {
            @Override
            public void onAuthenticationSuccess() {
                handleAuthenticationSuccess();
            }
        });



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
                                        fingerprintAuthenticator.showSignUpBiometricPrompt();
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
        UserHelperClass helperClass = new UserHelperClass(username.getText().toString(), password.getText().toString(), email.getText().toString(), PhoneNum.getText().toString(), clickedcolor.getSpinnerText());
        helperClass.saveUserToDatabase(reference, auth, this);
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


    private void openLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }


}
