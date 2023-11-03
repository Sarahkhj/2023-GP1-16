package com.example.myapplication324;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.myapplication324.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class Home extends DrawerBaseActivity { //i changed the extends class
    private TextView t1;
    private Button b1;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private String rtvFullName;
    protected final int home = 1;
    protected final int favo = 2;
    protected final int shared = 3;
    protected final int search = 4;

    private Button chooseFile_btn;
    private TextView filePath;
    private Intent intent;

    private final int CHOSE_PDF_FROM_DEVICE=1001;
    private final int PICK_WORD_FILE=1002;

    private static final String TAG ="Home";
    ActivityHomeBinding activityHomeBinding;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String currentUserId; // This should be unique for each user


    private List<FileMetadata> fileMetadataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());
        allocateActivityTitle("Home");
        //The code above is to appear the side navigation
        //setContentView(R.layout.activity_home);
        t1 = findViewById(R.id.name);
        b1 = findViewById(R.id.logout);
        auth = FirebaseAuth.getInstance();
        rootNode =FirebaseDatabase.getInstance();

        chooseFile_btn= findViewById(R.id.choose_file_btn);
        filePath = findViewById(R.id.file_path);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = auth.getCurrentUser().getUid(); // You should have a unique identifier for each user.

        chooseFile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callChoosePdfFile();
            }
        });
        findViewById(R.id.choose_word_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callChooseWordFile();
            }
        });


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
                        break;
                    case 2:
                        intent = new Intent(Home.this, Favorite.class);
                        startActivity(intent);
                        break;
                    case 3:
                         intent = new Intent(Home.this, Share.class);
                         startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(Home.this, Search.class);
                        startActivity(intent);
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

    ////Uploading files////
    private void callChoosePdfFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, CHOSE_PDF_FROM_DEVICE);
    }
    private void callChooseWordFile(){

        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent1.addCategory(Intent.CATEGORY_OPENABLE);
        intent1.setType("*/*");
        String[] mimetype = {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent1.putExtra(Intent.EXTRA_MIME_TYPES,mimetype);
        startActivityForResult(intent1,PICK_WORD_FILE);

    }
    //    private void callChooseTXTfFile(){
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("text/plain");
//        startActivityForResult(intent,CHOSE_PDF_FROM_DEVICE);
//    }
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode ==CHOSE_PDF_FROM_DEVICE && resultCode == Activity.RESULT_OK){

            if (resultData != null) {
                Uri fileUri = resultData.getData();
                if (fileUri != null) {
                    String fileName =  getFileNameFromUri(fileUri); // Get the original file name
                    StorageReference fileReference = storageReference.child(currentUserId + "/" + fileName);

                    UploadTask uploadTask = fileReference.putFile(fileUri);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        StyleableToast.makeText(Home.this, "PDF uploaded successfully!", Toast.LENGTH_SHORT, R.style.mytoast).show();

                        // Get the download URL of the uploaded file
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String fileDownloadUrl = uri.toString();

                            // Store file metadata in the Realtime Database
                            FileMetadata fileMetadata = new FileMetadata(fileName, fileDownloadUrl);
                            databaseReference.child("files").child(currentUserId).push().setValue(fileMetadata);
                        });
                    }).addOnFailureListener(e -> {
                        StyleableToast.makeText(Home.this, "Failed to upload PDF: " + e.getMessage(), Toast.LENGTH_SHORT, R.style.mytoast).show();
                    });

                }
            }}else if (requestCode == PICK_WORD_FILE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri fileUri = resultData.getData();
                if (fileUri != null) {
                    String fileName =  getFileNameFromUri(fileUri); // Get the original file name
                    StorageReference fileReference = storageReference.child(currentUserId + "/" + fileName);

                    UploadTask uploadTask = fileReference.putFile(fileUri);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        StyleableToast.makeText(Home.this, "Word file uploaded successfully!", Toast.LENGTH_SHORT, R.style.mytoast).show();

                        // Get the download URL of the uploaded file
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String fileDownloadUrl = uri.toString();

                            // Store file metadata in the Realtime Database
                            FileMetadata fileMetadata = new FileMetadata(fileName, fileDownloadUrl);
                            databaseReference.child("files").child(currentUserId).push().setValue(fileMetadata);
                        });
                    }).addOnFailureListener(e -> {
                        StyleableToast.makeText(Home.this, "Failed to upload Word file: " + e.getMessage(), Toast.LENGTH_SHORT, R.style.mytoast).show();
                    });
                }
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = "unknown";
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (displayNameIndex != -1) {
                    fileName = cursor.getString(displayNameIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }

}