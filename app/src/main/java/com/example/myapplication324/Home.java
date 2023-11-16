package com.example.myapplication324;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.myapplication324.databinding.ActivityHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import io.github.muddz.styleabletoast.StyleableToast;


import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class Home extends DrawerBaseActivity { //i changed the extends class
    private TextView t1;
    private FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private String rtvFullName;
    protected final int home = 1;
    protected final int favo = 2;
    protected final int shared = 3;
    protected final int search = 4;

    private Button chooseFile_btn, CreateFolderBtn;
    private TextView filePath;
    private Intent intent;

    private final int CHOSE_PDF_FROM_DEVICE = 1001;
    private final int PICK_WORD_FILE = 1002;

    private static final String TAG = "Home";
    ActivityHomeBinding activityHomeBinding;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String currentUserId; // This should be unique for each user

    private FloatingActionButton fab_main, fab1_mail, fab2_share, pdf;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_mail, textview_share, text;
    Boolean isOpen = false;
    private EditText FolderName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());
        allocateActivityTitle("Home");
        //The code above is to appear the side navigation
        //setContentView(R.layout.activity_home);
        //FloatingActionButton
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

        FolderName = findViewById(R.id.FolderName);




        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

            }
        });


        fab2_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "upload file", Toast.LENGTH_SHORT).show();
                callChoosePdfFile();

            }
        });

        fab1_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "creat folder", Toast.LENGTH_SHORT).show();
                ShowDialog();  ///////////////////////////////////////////////////////////////////////////////////////

            }
        });
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callChooseWordFile();
            }
        });
        // end of FloatingActionButton
        t1 = findViewById(R.id.name);
        auth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();

       // chooseFile_btn = findViewById(R.id.choose_file_btn);
        //filePath = findViewById(R.id.file_path);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = auth.getCurrentUser().getUid(); // You should have a unique identifier for each user.

       /* chooseFile_btn.setOnClickListener(new View.OnClickListener() {
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
        });*/

        if (auth.getCurrentUser() != null) {
            rtvFullName = auth.getCurrentUser().getEmail();
            t1.setText(rtvFullName);

        } else {
            StyleableToast.makeText(Home.this, "Error = No users Found !", Toast.LENGTH_SHORT, R.style.mytoast).show();

        }


//Bottom nav
        MeowBottomNavigation bottomNavigation = findViewById(R.id.meow);
        bottomNavigation.show(1, true);
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

    ////Uploading files////
    private void callChoosePdfFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, CHOSE_PDF_FROM_DEVICE);
    }

    private void callChooseWordFile() {

        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent1.addCategory(Intent.CATEGORY_OPENABLE);
        intent1.setType("*/*");
        String[] mimetype = {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent1.putExtra(Intent.EXTRA_MIME_TYPES, mimetype);
        startActivityForResult(intent1, PICK_WORD_FILE);

    }

    //    private void callChooseTXTfFile(){
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("text/plain");
//        startActivityForResult(intent,CHOSE_PDF_FROM_DEVICE);
//    }
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == CHOSE_PDF_FROM_DEVICE && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                Uri fileUri = resultData.getData();
                if (fileUri != null) {
                    String fileName = getFileNameFromUri(fileUri); // Get the original file name
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
            }
        } else if (requestCode == PICK_WORD_FILE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri fileUri = resultData.getData();
                if (fileUri != null) {
                    String fileName = getFileNameFromUri(fileUri); // Get the original file name
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
                break;
        }

    }


    private void createFolder() {
        String folderName = FolderName.getText().toString().trim();

        if (!folderName.isEmpty()) {

            // Access the StorageReference
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            // Path for the new folder
            String folderPath = currentUserId + "/" + folderName + "/";

            // Reference to the folder path
            StorageReference folderRef = storageRef.child(folderPath);

            // This creates an empty file in the folder to signify its creation
            folderRef.child("New File").putBytes(new byte[0])
                    .addOnSuccessListener(taskSnapshot -> {
                        // Folder has been created
                        StyleableToast.makeText(Home.this, "Folder created successfully", Toast.LENGTH_SHORT,R.style.mytoast).show();
                    })
                    .addOnFailureListener(e -> {
                        // Folder creation failed
                        StyleableToast.makeText(Home.this, "Folder creation failed", Toast.LENGTH_SHORT,R.style.mytoast).show();
                    });
        } else {
            StyleableToast.makeText(Home.this, "Please enter a folder name", Toast.LENGTH_SHORT,R.style.mytoast).show();
        }
    }

    private void ShowDialog() {
        // Create a Dialog object
        Dialog dialog = new Dialog(this);

        // Set the content view of the dialog by inflating folder_dialog.xml
        View dialogView = LayoutInflater.from(this).inflate(R.layout.folder_dialog, null);
        dialog.setContentView(dialogView);

        // Now, you can find and use the views inside the dialogView
        EditText folderNameEditText = dialogView.findViewById(R.id.FolderName);
        Button createFolderBtn = dialogView.findViewById(R.id.CreateFolderBtn);

        // Set click listener for the create folder button
        createFolderBtn.setOnClickListener(v -> {
            String folderName = folderNameEditText.getText().toString().trim();

            // Perform folder creation logic here
            if (!folderName.isEmpty()) {
                // Access the StorageReference
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                // Path for the new folder
                String folderPath = currentUserId + "/" + folderName + "/";

                // Reference to the folder path
                StorageReference folderRef = storageRef.child(folderPath);

                // This creates an empty file in the folder to signify its creation
                folderRef.child("New File").putBytes(new byte[0])
                        .addOnSuccessListener(taskSnapshot -> {
                            // Folder has been created
                            StyleableToast.makeText(Home.this, "Folder created successfully", Toast.LENGTH_SHORT,R.style.mytoast).show();
                            dialog.dismiss(); // Dismiss the dialog after creating the folder
                        })
                        .addOnFailureListener(e -> {
                            // Folder creation failed
                            StyleableToast.makeText(Home.this, "Folder creation failed", Toast.LENGTH_SHORT,R.style.mytoast).show();
                        });
            } else {
                folderNameEditText.setError("Please enter a folder name");
            }
        });

        // Show the dialog
        dialog.show();
    }


}


