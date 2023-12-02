package com.example.myapplication324;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.myapplication324.databinding.ActivityFolderBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class FolderActivity extends DrawerBaseActivity {
    // Define your views and variables
    private FloatingActionButton fab_main, fab1_mail, fab2_share, pdf;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    private Boolean isOpen = false;
    private TextView textview_mail, textview_share, text;
    protected final int home = 1;
    protected final int favo = 2;
    protected final int shared = 3;
    protected final int search = 4;
    private ActivityFolderBinding activityFolderBinding;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityFolderBinding = ActivityFolderBinding.inflate(getLayoutInflater());
        setContentView(activityFolderBinding.getRoot());
        allocateActivityTitle("Home");
        fab_main = findViewById(R.id.fab);
        fab1_mail = findViewById(R.id.fab1);
        fab2_share = findViewById(R.id.fab2);
        pdf = findViewById(R.id.fab3);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);

        textview_mail = (TextView) findViewById(R.id.textview_mail);
        textview_share = (TextView) findViewById(R.id.textview_share);
        text = findViewById(R.id.pdf);
       Home home=new Home();

        fab_main.setOnClickListener(view -> {

            if (isOpen) {

                textview_mail.setVisibility(View.INVISIBLE);
                textview_share.setVisibility(View.INVISIBLE);
                text.setVisibility(View.INVISIBLE);
                fab2_share.startAnimation(fab_close);
                fab1_mail.startAnimation(fab_close);
                pdf.startAnimation(fab_close);
                fab_main.startAnimation(fab_anticlock);
                fab2_share.setClickable(false);
                fab1_mail.setClickable(false);
                pdf.setClickable(false);
                isOpen = false;

            } else {
                textview_mail.setVisibility(View.VISIBLE);
                textview_share.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                fab2_share.startAnimation(fab_open);
                fab1_mail.startAnimation(fab_open);
                pdf.startAnimation(fab_open);
                fab_main.startAnimation(fab_clock);
                fab2_share.setClickable(true);
                pdf.setClickable(true);
                fab1_mail.setClickable(true);
                isOpen = true;
            }

        });


        fab2_share.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "upload file", Toast.LENGTH_SHORT).show();
           home.callChoosePdfFile();

        });

        fab1_mail.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "creat folder", Toast.LENGTH_SHORT).show();
            home.createFolder();

        });

        //Bottom nav
        MeowBottomNavigation bottomNavigation = findViewById(R.id.meow);
        bottomNavigation.show(1, true);
        MeowBottomNavigationShow(bottomNavigation);

        bottomNavigation.setOnClickMenuListener(item -> {
            // chose which class to go
            MeowBottomNavigationClick(item.getId());
        });

        bottomNavigation.setOnShowListener(item -> {

        });



        // Fetch folder information from the intent
        FolderMetadata folderMetadata = getIntent().getParcelableExtra("folder");

        // Use folderMetadata to fetch files associated with the folder and display them
        // You can use a RecyclerView and another adapter similar to FileAdapter for this purpose
    }

    //bottom nav method
    private void MeowBottomNavigationShow(MeowBottomNavigation bottomNavigation) {
        bottomNavigation.add(new MeowBottomNavigation.Model(home, R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(search, R.drawable.baseline_search_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(favo, R.drawable.baseline_favorite_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(shared, R.drawable.baseline_group_24));

    }

    private void MeowBottomNavigationClick(int num) {
        int number = num;
        switch (number) {
            case 1:
                break;
            case 2:
                intent = new Intent(FolderActivity.this, Favorite.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(FolderActivity.this, Share.class);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(FolderActivity.this, Search.class);
                startActivity(intent);
                break;
        }

    }


}
