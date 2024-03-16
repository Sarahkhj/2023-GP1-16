package com.example.myapplication324;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.myapplication324.databinding.ActivityFavoriteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class Favorite extends DrawerBaseActivity {
    protected final int home = 1;
    protected final int favo = 2;
    protected final int shared = 3;
    protected final int search = 4;
    private Intent intent;
    private FirebaseAuth auth;
    private String currentUserId; // This should be unique for each user

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    FirebaseFirestore db;
    private String rtvFullName;

    private List<FileMetadata> fileMetadataList = new ArrayList<>();
    private List<FileMetadata> folderMetadataList = new ArrayList<>();

    private List<Object> itemsList = new ArrayList<>(); // Combined list of files and folders



    ActivityFavoriteBinding activityFavoriteBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFavoriteBinding= ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(activityFavoriteBinding.getRoot());
        allocateActivityTitle("Favorite");
        SetUpRV();
        SetUpFB();

        //Bottom nav
        MeowBottomNavigation bottomNavigation = findViewById(R.id.meow);
        bottomNavigation.show(2,true);
        MeowBottomNavigationShow(bottomNavigation);
        bottomNavigation.setOnClickMenuListener(item -> {
            // chose which class to go
            MeowBottomNavigationClick(item.getId());
        });

        bottomNavigation.setOnShowListener(item -> {

        });

        auth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = auth.getCurrentUser().getUid(); // You should have a unique identifier for each user.


        if (auth.getCurrentUser() != null) {
            rtvFullName = auth.getCurrentUser().getEmail();
            //t1.setText(rtvFullName);

        } else {
            StyleableToast.makeText(Favorite.this, "No users Found !", Toast.LENGTH_SHORT, R.style.mytoast).show();

        }
        recyclerView = findViewById(R.id.recyclerView); // Add RecyclerView in your XML layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileAdapter = new FileAdapter(itemsList,this);
        recyclerView.setAdapter(fileAdapter);
        updateRecyclerView();

        // Call method to fetch files and folders from Firebase
        fetchFilesFromFirebase();


    }
    private void SetUpFB(){

        db=FirebaseFirestore.getInstance();

    }

    private void SetUpRV(){

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }
    private void fetchFilesFromFirebase() {
        DatabaseReference filesRef = FirebaseDatabase.getInstance().getReference().child("files").child(currentUserId);
        DatabaseReference filesRefinside = FirebaseDatabase.getInstance().getReference().child("filesinsideFolders").child(currentUserId);
        itemsList.clear();
        fileMetadataList.clear(); // Clear file metadata list
        folderMetadataList.clear(); // Clear folder metadata list

        // Attach a listener to read the data at our files reference
        filesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the list once before updating
                fileMetadataList.clear();

                // Iterate over all child nodes
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FileMetadata fileMetadata = dataSnapshot.getValue(FileMetadata.class);
                    if (fileMetadata != null && fileMetadata.isFavorite()) { // Check if the file is marked as favorite
                        fileMetadata.setKey(dataSnapshot.getKey());
                        fileMetadata.setPage("files");
                        fileMetadataList.add(fileMetadata); // Add file to file metadata list
                        itemsList.add(fileMetadata); // Add file to the list only if it's a favorite
                    }
                }

                // Here, no need to clear and re-add items to itemsList since we're directly adding the favorites
                // Also, no need for a separate fileMetadataList and folderMetadataList if we're only displaying favorites
                itemsList.clear();
                itemsList.addAll(fileMetadataList);
                itemsList.addAll(folderMetadataList);
                // Update RecyclerView
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(Favorite.this, "Failed to fetch files: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        filesRefinside.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the list once before updating
                folderMetadataList.clear();

                // Iterate over all child nodes
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FileMetadata fileMetadata = dataSnapshot.getValue(FileMetadata.class);
                    if (fileMetadata != null && fileMetadata.isFavorite()) { // Check if the file is marked as favorite
                        fileMetadata.setKey(dataSnapshot.getKey());
                        folderMetadataList.add(fileMetadata); // Add folder to folder metadata list
                        fileMetadata.setKey(dataSnapshot.getKey());
                        fileMetadata.setPage("filesinsideFolders");
                        itemsList.add(fileMetadata); // Add file to the list only if it's a favorite
                    }
                }

                // Here, no need to clear and re-add items to itemsList since we're directly adding the favorites
                // Also, no need for a separate fileMetadataList and folderMetadataList if we're only displaying favorites
                itemsList.clear();
                itemsList.addAll(fileMetadataList);
                itemsList.addAll(folderMetadataList);
                // Update RecyclerView
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(Favorite.this, "Failed to fetch files: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateRecyclerView() {
        // Notify the adapter about changes in the combined list
        fileAdapter.setItemsList(itemsList);
        fileAdapter.notifyDataSetChanged();
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