package com.example.myapplication324;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.myapplication324.databinding.ActivityHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import io.github.muddz.styleabletoast.StyleableToast;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class Home extends DrawerBaseActivity { //i changed the extends class
    private SearchView searchView;
    private FirebaseAuth auth;
    private String rtvFullName;
    protected final int home = 1;
    protected final int favo = 2;
    protected final int shared = 3;
    protected final int search = 4;
    private Intent intent;
    private final int CHOSE_PDF_FROM_DEVICE = 1001;
    private final int PICK_WORD_FILE = 1002;
    private ActivityHomeBinding activityHomeBinding;
    private String password;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String currentUserId; // This should be unique for each user
    private FloatingActionButton fab_main, fab1_mail, fab2_share, pdf;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_mail, textview_share, text;
    Boolean isOpen = false;
    private EditText FolderName;
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;

    private List<FileMetadata> fileMetadataList = new ArrayList<>();
    private List<FolderMetadata> folderMetadataList = new ArrayList<>();

    private List<Object> itemsList = new ArrayList<>(); // Combined list of files and folders
    private ProgressBar progressBar;
    FirebaseFirestore db;

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

        progressBar = findViewById(R.id.par);
        progressBar.setVisibility(View.GONE);

        db=FirebaseFirestore.getInstance();


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
            //Toast.makeText(getApplicationContext(), "upload file", Toast.LENGTH_SHORT).show();
            callChoosePdfFile();

        });

        fab1_mail.setOnClickListener(view -> {
            // Toast.makeText(getApplicationContext(), "creat folder", Toast.LENGTH_SHORT).show();
            CreateFolder();//new name instead of (ShowDialog())

        });
        pdf.setOnClickListener(v -> callChooseWordFile());

        auth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = auth.getCurrentUser().getUid(); // You should have a unique identifier for each user.


        if (auth.getCurrentUser() != null) {
            rtvFullName = auth.getCurrentUser().getEmail();
            //t1.setText(rtvFullName);

        } else {
            StyleableToast.makeText(Home.this, "No users Found !", Toast.LENGTH_SHORT, R.style.mytoast).show();

        }

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchText = newText.toLowerCase();
                performSearch(searchText);
                return true;
            }
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

        recyclerView = findViewById(R.id.recyclerView); // Add RecyclerView in your XML layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileAdapter = new FileAdapter(itemsList,this);
        recyclerView.setAdapter(fileAdapter);

        // Call method to fetch files and folders from Firebase
        fetchFilesFromFirebase();

    }
    private void performSearch(String searchText) {
        List<Object> searchResults = new ArrayList<>();

        for (Object item : itemsList) {
            if (item instanceof FileMetadata) {
                FileMetadata fileMetadata = (FileMetadata) item;
                if (fileMetadata.getFileName().toLowerCase().contains(searchText)) {
                    searchResults.add(fileMetadata);
                }
            } else if (item instanceof FolderMetadata) {
                FolderMetadata folderMetadata = (FolderMetadata) item;
                if (folderMetadata.getFolderName().toLowerCase().contains(searchText)) {
                    searchResults.add(folderMetadata);
                }
            }
        }

        fileAdapter = new FileAdapter(searchResults,this ); // Create a new instance of FileAdapter with the search results
        recyclerView.setAdapter(fileAdapter);
    }

    private void fetchFilesFromFirebase() {
        DatabaseReference filesRef = FirebaseDatabase.getInstance().getReference().child("files").child(currentUserId);
        DatabaseReference foldersRef = FirebaseDatabase.getInstance().getReference().child("folders").child(currentUserId);

        // Clear the items list before populating it again
        itemsList.clear();
        fileMetadataList.clear(); // Clear file metadata list
        folderMetadataList.clear(); // Clear folder metadata list


        filesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fileMetadataList.clear(); // Clear file metadata list before populating
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FileMetadata fileMetadata = dataSnapshot.getValue(FileMetadata.class);
                    if (fileMetadata != null) {
                        fileMetadataList.add(fileMetadata);
                        fileMetadata.setKey(dataSnapshot.getKey());
                        fileMetadata.setPage("files");
                        itemsList.add(fileMetadata); // Add file to combined list

                    }
                }
                // Clear and re-add items to the combined list
                itemsList.clear();
                itemsList.addAll(fileMetadataList);
                itemsList.addAll(folderMetadataList);

                // Update RecyclerView
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(Home.this, "Failed to fetch files: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        foldersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                folderMetadataList.clear(); // Clear folder metadata list before populating
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FolderMetadata folderMetadata = dataSnapshot.getValue(FolderMetadata.class);
                    if (folderMetadata != null) {
                        folderMetadataList.add(folderMetadata);
                        folderMetadata.setKey(dataSnapshot.getKey());
                        itemsList.add(folderMetadata); // Add folder to combined list
                    }
                }

                // Clear and re-add items to the combined list
                itemsList.clear();
                itemsList.addAll(fileMetadataList);
                itemsList.addAll(folderMetadataList);

                // Update RecyclerView
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(Home.this, "Failed to fetch folders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateRecyclerView() {
        // Notify the adapter about changes in the combined list
        fileAdapter.setItemsList(itemsList);
        fileAdapter.notifyDataSetChanged();
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

//    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
//        super.onActivityResult(requestCode, resultCode, resultData);
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//
//        if (currentUser == null) {
//            // Handle the case where the user is not authenticated
//            return;
//        }
//        DatabaseReference mDatabase;
//        mDatabase = FirebaseDatabase.getInstance().getReference("users");
//        String userEmail = currentUser.getEmail();
//        mDatabase.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    password = dataSnapshot.child("password").getValue(String.class);
//                    if (requestCode == CHOSE_PDF_FROM_DEVICE && resultCode == Activity.RESULT_OK) {
//                        if (resultData != null) {
//                            Uri fileUri = resultData.getData();
//                            if (fileUri != null) {
//                                try {
//                                    String fileName = getFileNameFromUri(fileUri); // Get the original file name
//                                    // Encrypt the file
//                                    InputStream inputStream = getContentResolver().openInputStream(fileUri);
//                                    byte[] encryptedBytes = Crypto.encryptFile(inputStream, password);
//                                    if (encryptedBytes != null) {
//                                        // Upload the encrypted file to Firebase Storage
//                                        String encryptedFileName = fileName;
//                                        StorageReference fileReference = storageReference.child(currentUserId + "/" + encryptedFileName);
//
//                                        UploadTask uploadTask = fileReference.putBytes(encryptedBytes);
//                                        uploadTask.addOnSuccessListener(taskSnapshot -> {
//                                            progressBar.setVisibility(View.GONE);
//                                            StyleableToast.makeText(Home.this, "File uploaded successfully!", Toast.LENGTH_SHORT, R.style.mytoast).show();
//
//                                            // Get the download URL of the uploaded file
//                                            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
//                                                progressBar.setVisibility(View.GONE);
//                                                String fileDownloadUrl = uri.toString();
//
//                                                // Store file metadata in the Realtime Database
//                                                FileMetadata fileMetadata = new FileMetadata(encryptedFileName, fileDownloadUrl);
//                                                databaseReference.child("files").child(currentUserId).push().setValue(fileMetadata);
//                                            });
//                                        }).addOnFailureListener(e -> StyleableToast.makeText(Home.this, "Failed to upload encrypted file: " + e.getMessage(), Toast.LENGTH_SHORT, R.style.mytoast).show()).addOnProgressListener(snapshot12 -> progressBar.setVisibility(View.VISIBLE));
//                                    } else {
//                                        // Encryption failed
//                                        StyleableToast.makeText(Home.this, "Encryption failed.", Toast.LENGTH_SHORT, R.style.mytoast).show();
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    } else if (requestCode == PICK_WORD_FILE && resultCode == Activity.RESULT_OK) {
//                        if (resultData != null) {
//                            Uri fileUri = resultData.getData();
//                            if (fileUri != null) {
//                                try {
//                                    String fileName = getFileNameFromUri(fileUri); // Get the original file name
//
//                                    // Encrypt the file
//                                    InputStream inputStream = getContentResolver().openInputStream(fileUri);
//                                    byte[] encryptedBytes = Crypto.encryptFile(inputStream, password);
//                                    if (encryptedBytes != null) {
//                                        // Upload the encrypted file to Firebase Storage
//                                        String encryptedFileName = fileName;
//                                        StorageReference fileReference = storageReference.child(currentUserId + "/" + encryptedFileName);
//
//                                        UploadTask uploadTask = fileReference.putBytes(encryptedBytes);
//                                        uploadTask.addOnSuccessListener(taskSnapshot -> {
//                                            progressBar.setVisibility(View.GONE);
//                                            StyleableToast.makeText(Home.this, "File uploaded successfully!", Toast.LENGTH_SHORT, R.style.mytoast).show();
//
//                                            // Get the download URL of the uploaded file
//                                            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
//                                                String fileDownloadUrl = uri.toString();
//
//                                                // Store file metadata in the Realtime Database
//                                                FileMetadata fileMetadata = new FileMetadata(encryptedFileName, fileDownloadUrl);
//                                                databaseReference.child("files").child(currentUserId).push().setValue(fileMetadata);
//                                            });
//                                        }).addOnFailureListener(e -> StyleableToast.makeText(Home.this, "Failed to upload encrypted file: " + e.getMessage(), Toast.LENGTH_SHORT, R.style.mytoast).show()).addOnProgressListener(snapshot1 -> progressBar.setVisibility(View.VISIBLE));
//                                    } else {
//                                        // Encryption failed
//                                        StyleableToast.makeText(Home.this, "Encryption failed.", Toast.LENGTH_SHORT, R.style.mytoast).show();
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                StyleableToast.makeText(Home.this, "No password?.", Toast.LENGTH_SHORT, R.style.mytoast).show();
//
//            }
//        });
//
//    }


    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        String userEmail = currentUser.getEmail();
        mDatabase.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    password = dataSnapshot.child("password").getValue(String.class);
                    if (requestCode == CHOSE_PDF_FROM_DEVICE && resultCode == Activity.RESULT_OK) {
                        if (resultData != null) {
                            Uri fileUri = resultData.getData();
                            if (fileUri != null) {
                                try {
                                    String fileName = getFileNameFromUri(fileUri);
                                    InputStream inputStream = getContentResolver().openInputStream(fileUri);

                                    // Encrypt the file
                                    byte[] encryptedBytes = Crypto.encryptFile(inputStream, Home.this, fileName);////////////////////////
                                    if (encryptedBytes != null) {
                                        String encryptedFileName = fileName;

                                        // Upload the encrypted file
                                        StorageReference fileReference = storageReference.child(currentUserId + "/" + encryptedFileName);
                                        UploadTask uploadTask = fileReference.putBytes(encryptedBytes);
                                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                                            progressBar.setVisibility(View.GONE);
                                            StyleableToast.makeText(Home.this, "File uploaded successfully!", Toast.LENGTH_SHORT, R.style.mytoast).show();

                                            // Get the download URL of the uploaded file
                                            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                                String fileDownloadUrl = uri.toString();

                                                // Save the encrypted file metadata to the database
                                                FileMetadata fileMetadata = new FileMetadata(encryptedFileName, fileDownloadUrl);
                                                databaseReference.child("files").child(currentUserId).push().setValue(fileMetadata);
                                            });
                                        }).addOnFailureListener(e -> StyleableToast.makeText(Home.this, "Failed to upload encrypted file: " + e.getMessage(), Toast.LENGTH_SHORT, R.style.mytoast).show()).addOnProgressListener(snapshot12 -> progressBar.setVisibility(View.VISIBLE));
                                    } else {
                                        StyleableToast.makeText(Home.this, "Encryption failed.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if (requestCode == PICK_WORD_FILE && resultCode == Activity.RESULT_OK) {
                        if (resultData != null) {
                            Uri fileUri = resultData.getData();
                            if (fileUri != null) {
                                try {
                                    String fileName = getFileNameFromUri(fileUri);
                                    InputStream inputStream = getContentResolver().openInputStream(fileUri);

                                    // Encrypt the file
                                    byte[] encryptedBytes = Crypto.encryptFile(inputStream, Home.this, fileName); ///////////////////////////
                                    if (encryptedBytes != null) {
                                        String encryptedFileName = fileName;

                                        // Upload the encrypted file
                                        StorageReference fileReference = storageReference.child(currentUserId + "/" + encryptedFileName);
                                        UploadTask uploadTask = fileReference.putBytes(encryptedBytes);
                                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                                            progressBar.setVisibility(View.GONE);
                                            StyleableToast.makeText(Home.this, "File uploaded successfully!", Toast.LENGTH_SHORT, R.style.mytoast).show();

                                            // Get the download URL of the uploaded file
                                            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                                String fileDownloadUrl = uri.toString();

                                                // Save the encrypted file metadata to the database
                                                FileMetadata fileMetadata = new FileMetadata(encryptedFileName, fileDownloadUrl);
                                                databaseReference.child("files").child(currentUserId).push().setValue(fileMetadata);
                                            });
                                        }).addOnFailureListener(e -> StyleableToast.makeText(Home.this, "Failed to upload encrypted file: " + e.getMessage(), Toast.LENGTH_SHORT, R.style.mytoast).show()).addOnProgressListener(snapshot12 -> progressBar.setVisibility(View.VISIBLE));
                                    } else {
                                        StyleableToast.makeText(Home.this, "Encryption failed.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(Home.this, "No password?.", Toast.LENGTH_SHORT, R.style.mytoast).show();

            }
        });

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

//    private void createFolder() {
//        String folderName = FolderName.getText().toString().trim();
//
//        if (!folderName.isEmpty()) {
//            DatabaseReference foldersRef = FirebaseDatabase.getInstance().getReference().child("folders").child(currentUserId);
//
//            // Generate a unique key for the folder
//            String folderId = foldersRef.push().getKey();
//
//            // Store folder metadata in the Realtime Database
//            FolderMetadata folderMetadata = new FolderMetadata(folderId, folderName);
//
//            // Save folder metadata using the unique key
//            foldersRef.child(folderId).setValue(folderMetadata)
//                    .addOnSuccessListener(aVoid -> {
//                        // Folder created successfully
//                        StyleableToast.makeText(Home.this, "Folder created successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();
//                    })
//                    .addOnFailureListener(e -> {
//                        // Folder creation failed
//                        StyleableToast.makeText(Home.this, "Folder creation failed", Toast.LENGTH_SHORT, R.style.mytoast).show();
//                    });
//        } else {
//            StyleableToast.makeText(Home.this, "Please enter a folder name", Toast.LENGTH_SHORT, R.style.mytoast).show();
//        }
//    }

    private void CreateFolder() {
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

            // Perform folder name validation
            if (!folderName.isEmpty()) {
                // Check if the folder name contains invalid characters
                if (containsInvalidCharacters(folderName)) {
                    folderNameEditText.setError("Folder name contains invalid characters");
                } else {
                    DatabaseReference foldersRef = FirebaseDatabase.getInstance().getReference().child("folders").child(currentUserId);

                    // Generate a unique key for the folder
                    String folderId = foldersRef.push().getKey();

                    // Store folder metadata in the Realtime Database
                    FolderMetadata folderMetadata = new FolderMetadata(folderId, folderName);

                    // Save folder metadata using the unique key
                    foldersRef.child(folderId).setValue(folderMetadata)
                            .addOnSuccessListener(aVoid -> {
                                // Folder created successfully
                                StyleableToast.makeText(Home.this, "Folder created successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                dialog.dismiss(); // Dismiss the dialog after creating the folder
                            })
                            .addOnFailureListener(e -> {
                                // Folder creation failed
                                StyleableToast.makeText(Home.this, "Folder creation failed", Toast.LENGTH_SHORT, R.style.mytoast).show();
                            });
                }
            } else {
                folderNameEditText.setError("Please enter a folder name");
            }
        });

        // Show the dialog
        dialog.show();
    }

    // Function to check if the folder name contains invalid characters
    private boolean containsInvalidCharacters(String name) {
        String invalidCharacters = "[]{}()/\\:?\"<>|*";
        for (int i = 0; i < name.length(); i++) {
            if (invalidCharacters.contains(String.valueOf(name.charAt(i)))) {
                return true;
            }
        }
        return false;
    }


}