package com.example.myapplication324;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import io.github.muddz.styleabletoast.StyleableToast;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;


    @Override
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base,null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar = drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.menu_drawer_open,R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //user log out
        auth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
 // switch for side navigation items
        switch (item.getItemId()){
            case R.id.favo:
              startActivity(new Intent(this, Favorite.class));
              overridePendingTransition(0,0);
              break;

            case R.id.home:
                startActivity(new Intent(this, Home.class));
                overridePendingTransition(0,0);
                break;
            case R.id.Share:
                startActivity(new Intent(this, Share.class));
                overridePendingTransition(0,0);
                break;
            case R.id.logout:
                showConfirmationDialog();
                break;
            case R.id.about:
                startActivity(new Intent(this, Profile.class));
                overridePendingTransition(0,0);
                break;
            case R.id.help:
                startActivity(new Intent(this, HelpGuide.class));
                overridePendingTransition(0,0);
                break;

        }
        return false;
    }

    protected void allocateActivityTitle(String titleString){
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(titleString);
        }
    }
    private void logout(){
        auth.signOut();
        Intent intent = new Intent(DrawerBaseActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
       // StyleableToast.makeText(DrawerBaseActivity.this, "Logout Successful !", Toast.LENGTH_SHORT,R.style.mytoast).show();

    }
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log Out");
        builder.setMessage("Are you sure you want to log out?"); // Set the message for confirmation

        // Set a positive button and its click listener
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });

        // Set a negative button and its click listener
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the action when the user cancels (clicks 'No') or dismisses the dialog
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();


    }
}