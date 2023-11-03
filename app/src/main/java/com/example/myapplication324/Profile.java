package com.example.myapplication324;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication324.databinding.ActivityProfileBinding;
import com.example.myapplication324.databinding.ActivityShareBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import io.github.muddz.styleabletoast.StyleableToast;

public class Profile extends  DrawerBaseActivity {
    ActivityProfileBinding activityProfileBinding;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private String rtvFullName;
    private TextView t1;
    private TextView t2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding= ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(activityProfileBinding.getRoot());
        allocateActivityTitle("Profile");
        // User name
        t1 = findViewById(R.id.amazonName);
        auth = FirebaseAuth.getInstance();
        rootNode =FirebaseDatabase.getInstance();
        if (auth.getCurrentUser() != null) {
            rtvFullName = auth.getCurrentUser().getEmail();
            t1.setText(rtvFullName);

        } else {
            StyleableToast.makeText(Profile.this, "Error = No users Found !", Toast.LENGTH_SHORT,R.style.mytoast).show();
        }




    }
}