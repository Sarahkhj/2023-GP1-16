package com.example.myapplication324;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Sign_up extends AppCompatActivity {
    private TextView t1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        t1 = (TextView) findViewById(R.id.login);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openlogin();
            }
        });


    }

    public void openlogin(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}