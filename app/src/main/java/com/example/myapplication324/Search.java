package com.example.myapplication324;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.myapplication324.databinding.ActivityFavoriteBinding;
import com.example.myapplication324.databinding.ActivitySearchBinding;

public class Search extends DrawerBaseActivity {
    ActivitySearchBinding activitySearchBinding;
    protected final int home = 1;
    protected final int favo = 2;
    protected final int shared = 3;
    protected final int search = 4;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchBinding= ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(activitySearchBinding.getRoot());
        allocateActivityTitle("Search");
        //Bottom nav
        MeowBottomNavigation bottomNavigation = findViewById(R.id.meow);
        bottomNavigation.add(new MeowBottomNavigation.Model(home,R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(search,R.drawable.baseline_search_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(favo,R.drawable.baseline_favorite_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(shared,R.drawable.baseline_group_24));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                // StyleableToast.makeText(Home.this, "item"+item.getId(), Toast.LENGTH_SHORT,R.style.mytoast).show();
                // chose which class to go
                int num = item.getId();
                switch(num){
                    case 1:
                        intent = new Intent(Search.this, Home.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(Search.this, Favorite.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(Search.this, Share.class);
                        startActivity(intent);
                        break;
                    case 4:
                        break;                }
            }
        });

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                String name;
                switch(item.getId()){
                    case home:name="home";
                        break;
                    case favo:name="favo";
                        break;
                    case shared:name="shared";
                        //  Intent intent = new Intent(Home.this, Share.class);
                        //startActivity(intent);
                        break;
                    case search:name="search";
                        //  Intent intent = new Intent(Home.this, search.class);
                        // startActivity(intent);
                        break;                }
            }

        });

    }
}