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

public class Login extends AppCompatActivity {
    private TextView t1;

    Button SignInButton;

 private EditText email,password;

 DBHelper db;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        t1 = (TextView) findViewById(R.id.signup);
        t1.setOnClickListener(new View.OnClickListener() {//makes the signup text clickable
            @Override
            public void onClick(View view) {
                opensignup();
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

                if(db.checkusernamepassword((email.getText().toString()),
                        password.getText().toString()))
                {
                    Intent intent=new Intent(Login.this , Home.class);
                    startActivity(intent);

                }
                else
                {
                    Toast.makeText(Login.this, "Invalid inputs", Toast.LENGTH_SHORT).show();
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
            email.setError("Please enter a username");
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
}