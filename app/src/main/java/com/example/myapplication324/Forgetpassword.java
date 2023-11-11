package com.example.myapplication324;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import io.github.muddz.styleabletoast.StyleableToast;

public class Forgetpassword extends AppCompatActivity {
    private Button rest;
    private EditText email;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        rest = findViewById(R.id.update_pass);
        email = findViewById(R.id.email);
        rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textemail = email.getText().toString();
                if (TextUtils.isEmpty(textemail)) {
                    email.setError("Enter email!");
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(textemail).matches()){
                    email.setError("Enter a valid email address");


                }
                else {
                    forgetpassword(textemail);
                }

            }
        });
    }

    private void forgetpassword(String email) {
        auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    StyleableToast.makeText(Forgetpassword.this, "Please Check you inbox for password reset link", Toast.LENGTH_SHORT,R.style.mytoast).show();
                    Intent intent = new Intent ( Forgetpassword. this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // on below line calling a method to start the activity
                    startActivity(intent);

                    // on below line calling finish to close the current activity.
                    finish();

                } else {
                    Toast.makeText(Forgetpassword.this, "Something went wrong"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                }

            }
        });
    }
}