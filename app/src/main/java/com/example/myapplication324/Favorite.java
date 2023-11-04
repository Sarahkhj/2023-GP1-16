package com.example.myapplication324;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.myapplication324.databinding.ActivityFavoriteBinding;

public class Favorite extends DrawerBaseActivity {
    protected final int home = 1;
    protected final int favo = 2;
    protected final int shared = 3;
    protected final int search = 4;
    private Intent intent;


    ActivityFavoriteBinding activityFavoriteBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFavoriteBinding= ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(activityFavoriteBinding.getRoot());
        allocateActivityTitle("Favorite");
        //Bottom nav
        MeowBottomNavigation bottomNavigation = findViewById(R.id.meow);
        bottomNavigation.show(2,true);
        MeowBottomNavigationShow(bottomNavigation);
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                // chose which class to go
                MeowBottomNavigationClick(item.getId());
            }
        });

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {

            }

        });


    }
    //bottom nav method
    private void MeowBottomNavigationShow(MeowBottomNavigation bottomNavigation){
        bottomNavigation.add(new MeowBottomNavigation.Model(home,R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(search,R.drawable.baseline_search_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(favo,R.drawable.baseline_favorite_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(shared,R.drawable.baseline_group_24));

    }
    private void MeowBottomNavigationClick(int num){
        int number = num;
        switch(number){
            case 1:
                intent = new Intent(Favorite.this, Home.class);
                startActivity(intent);
                break;
            case 2:
                break;
            case 3:
                intent = new Intent(Favorite.this, Share.class);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(Favorite.this, Search.class);
                startActivity(intent);
                break;                }

    }
}