package com.example.myapplication324;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.biometric.BiometricPrompt;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
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

//
//                if(!validateForm())
//                    return;
                //checkusernamepassword
                String emailAsText= email.getText().toString().trim();
                String pass=password.getText().toString().trim();
                isCredentialsValid();
                if (!db.checkusernamepassword(emailAsText,pass)){
                    Toast.makeText(Login.this, "Account does not exist", Toast.LENGTH_SHORT).show();
                }
                else{


                }




            }
        });



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

//    private boolean validateForm(){
//        email.setError(null);
//        password.setError(null);
//        String emailAsText= email.getText().toString().trim();  //retrieve the text entered in the email
//        String passwordAsText=password.getText().toString().trim();
//
//        if (TextUtils.isEmpty(emailAsText)) {
//            email.setError("Please enter your email");
//            email.requestFocus();
//            return false;
//        }
//        if (TextUtils.isEmpty(passwordAsText)) {
//            password.setError("Please enter your Password");
//            password.requestFocus();
//            return false;
//        }
//        return true;
//    }

    public void opensignup(){
        Intent intent = new Intent(this, Sign_up.class);
        startActivity(intent);
    }
    public void openforget(){
        Intent intent = new Intent(this, Forgetpassword.class);
        startActivity(intent);
    }

}