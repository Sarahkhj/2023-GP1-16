package com.example.myapplication324;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import io.github.muddz.styleabletoast.StyleableToast;

public class Home extends AppCompatActivity {
    private TextView t1;
    private Button b1;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private String rtvFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        t1 = findViewById(R.id.name);
        b1 = findViewById(R.id.logout);
        auth = FirebaseAuth.getInstance();
        rootNode =FirebaseDatabase.getInstance();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                auth.signOut();
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
                finish();
                StyleableToast.makeText(Home.this, "Logout Successful !", Toast.LENGTH_SHORT,R.style.mytoast).show();

            }
        });
        if (auth.getCurrentUser() != null) {
            rtvFullName = auth.getCurrentUser().getEmail();
            t1.setText(rtvFullName);

        } else {
            StyleableToast.makeText(Home.this, "Error = No users Found !", Toast.LENGTH_SHORT,R.style.mytoast).show();
        }

    }
}