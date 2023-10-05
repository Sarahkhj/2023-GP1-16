package com.example.myapplication324;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private TextView t1;
    private TextView t2;


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

        db = new DBHelper(this);//to gain access to the local database and perform various operations

        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateForm())
                    return;
                //checkusernamepassword
                String emailAsText= email.getText().toString();
                if(!isValidEmail(emailAsText)){
                    Toast.makeText(Login.this, "invalid email format", Toast.LENGTH_SHORT).show();
                }


                if (!db.checkusernamepassword((email.getText().toString()),
                        password.getText().toString())) {
                            Toast.makeText(Login.this, "Account does not exist", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent=new Intent(Login.this , Home.class);
                            startActivity(intent);

                        }


            }
        });


    }

    private boolean validateForm(){
        email.setError(null);
        password.setError(null);
        String emailAsText= email.getText().toString().trim();  //retrieve the text entered in the email
        String passwordAsText=password.getText().toString().trim();

        if (TextUtils.isEmpty(emailAsText)) {
            email.setError("Please enter your email");
            email.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(passwordAsText)) {
            password.setError("Please enter your Password");
            password.requestFocus();
            return false;
        }
        return true;
    }

    public void opensignup(){
        Intent intent = new Intent(this, Sign_up.class);
        startActivity(intent);
    }
    public void openforget(){
        Intent intent = new Intent(this, Forgetpassword.class);
        startActivity(intent);
    }
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}