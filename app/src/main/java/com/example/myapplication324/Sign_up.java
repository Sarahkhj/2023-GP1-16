package com.example.myapplication324;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Sign_up extends AppCompatActivity {
    private TextView login;
    private EditText username, email, PhoneNum, password;
    private Button sign;
    private DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        login = findViewById(R.id.login);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        PhoneNum = findViewById(R.id.PhoneNum);
        password = findViewById(R.id.password);
        sign = findViewById(R.id.sign);
        DB = new DBHelper(this); // Initialize your DBHelper

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openlogin();
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String mail = email.getText().toString();
                String Phone = PhoneNum.getText().toString();
                if (user.equals("") || pass.equals("") || mail.equals("") || Phone.equals("")) {
                    Toast.makeText(Sign_up.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkuser = DB.checkusername(user);
                    if (checkuser) {
                        Toast.makeText(Sign_up.this, "User already exists! Please sign in", Toast.LENGTH_SHORT).show();
                    } else {
                        Boolean insert = DB.addUser(user, pass, mail, Phone);
                        if (insert) {
                            Toast.makeText(Sign_up.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Sign_up.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    public void openlogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}
