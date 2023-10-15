package com.example.myapplication324;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sign_up extends AppCompatActivity {
    private TextView login;
    private EditText username, email, PhoneNum, password, rePassword;
    private Button sign;
    private DBHelper DB;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

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

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String rePass = rePassword.getText().toString();
                String mail = email.getText().toString();
                String Phone = PhoneNum.getText().toString();
                if (user.equals("") || pass.equals("") || rePass.equals("") || mail.equals("") || Phone.equals("")) {
                    Toast.makeText(Sign_up.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else if (!pass.equals(rePass)) {
                    // Set an error on the rePassword field
                    rePassword.setError("Passwords do not match");
                } else {
                    boolean validEmail = isValidEmail(mail);
                    boolean validPhone = isValidPhoneNumber(Phone);
                    boolean validPassword = isValidPassword(pass);

                    if (validEmail && validPhone && validPassword) {
                        auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Sign_up.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Sign_up.this, Home.class));
                                } else {
                                    Toast.makeText(Sign_up.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        StringBuilder errorMessage = new StringBuilder("Invalid format: ");
                        if (!validEmail) {
                            email.setError("Email, ");
                        }
                        if (!validPhone) {
                            PhoneNum.setError("Phone number, ");
                        }
                        if (!validPassword) {
                            password.setError("password minimum 8\natleast 1 uppercase\natleast 1 lowercase\natleast 1 numbers\natleast 1special charecter  ");
                        }
                    }
                }
                // Rest of your code
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("users");
                UserHelperClass helperClass = new UserHelperClass(user, mail, Phone);
                reference.push().setValue(helperClass);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openlogin();
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
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
}