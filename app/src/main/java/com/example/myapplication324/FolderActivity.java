package com.example.myapplication324;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.myapplication324.databinding.ActivityFolderBinding;
import com.example.myapplication324.databinding.ActivityHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.github.muddz.styleabletoast.StyleableToast;

public class FolderActivity extends DrawerBaseActivity {
    private String folderId;
    private Intent intent;
    ActivityFolderBinding activityFolderBinding;


    protected final int home = 1;
    protected final int favo = 2;
    protected final int shared = 3;
    protected final int search = 4;
    private final int CHOSE_PDF_FROM_DEVICE = 1001;
    private final int PICK_WORD_FILE = 1002;
    private String currentUserId;
    private FloatingActionButton fab_main, fab1_mail, fab2_share, pdf;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_mail, textview_share, text;
    Boolean isOpen = false;

    private List<FolderMetadata> folderMetadataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<Object> itemsList = new ArrayList<>(); // Combined list of files and folders
    private List<FileMetadata> fileMetadataList = new ArrayList<>();
    private FirebaseAuth auth;
    private ProgressBar progressBar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFolderBinding = ActivityFolderBinding.inflate(getLayoutInflater());
        setContentView(activityFolderBinding.getRoot());
        allocateActivityTitle("Folder");

        //  setContentView(R.layout.activity_folder);
        fab_main = findViewById(R.id.fab);
        fab1_mail = findViewById(R.id.fab1);
        fab2_share = findViewById(R.id.fab2);
        pdf = findViewById(R.id.fab3);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);
        pdf = findViewById(R.id.fab3);

        textview_mail = (TextView) findViewById(R.id.textview_mail);
        textview_share = (TextView) findViewById(R.id.textview_share);
        text = findViewById(R.id.pdf);
        auth = FirebaseAuth.getInstance();

        currentUserId = auth.getCurrentUser().getUid(); // You should have a unique identifier for each user.
        progressBar = findViewById(R.id.par);
        progressBar.setVisibility(View.GONE);





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




        // Retrieve folderId from the Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("folderId")) {
            folderId = intent.getStringExtra("folderId");

            // Fetch and display files for the clicked folder
            fetchFilesFromFirebase(folderId);


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
                CreateSubFolder();

            });
            pdf.setOnClickListener(v -> callChooseWordFile());

            // You can add more buttons or customize the logic as needed
        } else {
            // Handle the case where folderId is not provided in the Intent
            Toast.makeText(this, "Invalid folderId", Toast.LENGTH_SHORT).show();
            finish(); // Finish the activity if folderId is not available
        }



        recyclerView = findViewById(R.id.recyclerView); // Add RecyclerView in your XML layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileAdapter = new FileAdapter(itemsList,this);
        recyclerView.setAdapter(fileAdapter);
    }

    private void callChoosePdfFile() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, CHOSE_PDF_FROM_DEVICE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
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

        return true;
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

    private void callChooseWordFile() {

        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent1.addCategory(Intent.CATEGORY_OPENABLE);
        intent1.setType("*/*");
        String[] mimetype = {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent1.putExtra(Intent.EXTRA_MIME_TYPES, mimetype);
        startActivityForResult(intent1, PICK_WORD_FILE);

    }



    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String folderId = getFolderId(); // Replace this with the actual method to get the folderId
            String fileName = ""; // Initialize file name variable

            switch (requestCode) {
                case CHOSE_PDF_FROM_DEVICE:
                    // Handle the PDF file selected from device
                    if (data != null) {
                        // Get the URI of the selected PDF file
                        Uri pdfUri = data.getData();
                        fileName = getFileNameFromUri(pdfUri);

                        // Encrypt the file
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(pdfUri);
                            byte[] encryptedBytes = Crypto.encryptFile(inputStream, FolderActivity.this, fileName);
                            if (encryptedBytes != null) {
                                // Proceed with uploading the encrypted file
                                uploadFileToFirebase(encryptedBytes, folderId, fileName);
                            } else {
                                StyleableToast.makeText(FolderActivity.this, "Encryption failed.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case PICK_WORD_FILE:
                    // Handle the Word file selected from device
                    if (data != null) {
                        // Get the URI of the selected Word file
                        Uri wordUri = data.getData();
                        fileName = getFileNameFromUri(wordUri);

                        // Encrypt the file
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(wordUri);
                            byte[] encryptedBytes = Crypto.encryptFile(inputStream, FolderActivity.this, fileName);
                            if (encryptedBytes != null) {
                                // Proceed with uploading the encrypted file
                                uploadFileToFirebase(encryptedBytes, folderId, fileName);
                            } else {
                                StyleableToast.makeText(FolderActivity.this, "Encryption failed.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                // Add more cases if needed for other request codes
            }
        }
    }
    // Replace this method with the actual method to get the current folderId
    private String getFolderId() {
        return folderId;
    }




    private void uploadFileToFirebase(byte[] encryptedBytes, String folderId, String fileName) {
        // Sample logic for uploading a file to Firebase Storage within a specific folder
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to the folder in Firebase Storage
        StorageReference folderRef = storageRef.child("folders/" + folderId);

        // Create a reference to the file within the folder
        StorageReference fileRef = folderRef.child(fileName); // Use the provided file name
        progressBar.setVisibility(View.VISIBLE); // Show progressBar when upload starts


        // Upload the file to Firebase Storage
        fileRef.putBytes(encryptedBytes)
                .addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully
                    progressBar.setVisibility(View.GONE); // Hide progressBar on success

                    // Get the download URL of the uploaded file
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save file metadata to Firebase Realtime Database
                        saveFileMetadataToDatabase( fileName,uri.toString() , folderId);
                    }).addOnFailureListener(e -> {
                        // Handle failure to get download URL
                        StyleableToast.makeText(FolderActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    });

                    StyleableToast.makeText(FolderActivity.this, "File uploaded successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();
                })
                .addOnFailureListener(e -> {
                    // File upload failed
                    progressBar.setVisibility(View.GONE); // Hide progressBar on failure
                    StyleableToast.makeText(FolderActivity.this, "File upload failed", Toast.LENGTH_SHORT, R.style.mytoast).show();
                })
                .addOnProgressListener(snapshot -> {
                    // Update progress bar
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                });

        fetchFilesFromFirebase(folderId);

    }

//    private void saveFileMetadataToDatabase( String fileName,String fileDownloadUrl) {
//        DatabaseReference filesMetadataRef = FirebaseDatabase.getInstance().getReference().child("filesinsideFolders").child(currentUserId);
//
//        // Generate a unique key for the file
//        String fileId = filesMetadataRef.push().getKey();
//
//        // Create a FileMetadata object
//        FileMetadata fileMetadata = new FileMetadata( fileName, fileDownloadUrl);
//
//        // Save file metadata using the unique key
//        filesMetadataRef.child(fileId).setValue(fileMetadata)
//                .addOnSuccessListener(aVoid -> {
//                    // File metadata saved successfully
//                    StyleableToast.makeText(FolderActivity.this, "File metadata saved successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();
//
//                    // Fetch and display updated files and folders
//                    fetchFilesAndFoldersFromFirebase();
//                })
//                .addOnFailureListener(e -> {
//                    // File metadata saving failed
//                    StyleableToast.makeText(FolderActivity.this, "File metadata saving failed", Toast.LENGTH_SHORT, R.style.mytoast).show();
//                });
//    }

    private void saveFileMetadataToDatabase(String fileName, String fileDownloadUrl, String folderId) {
        DatabaseReference filesMetadataRef = FirebaseDatabase.getInstance().getReference().child("filesinsideFolders").child(currentUserId);

        // Generate a unique key for the file
        String fileId = filesMetadataRef.push().getKey();

        // Create a FileMetadata object with folder ID
        FileMetadata fileMetadata = new FileMetadata(fileName, fileDownloadUrl, folderId);

        // Save file metadata using the unique key
        filesMetadataRef.child(fileId).setValue(fileMetadata)
                .addOnSuccessListener(aVoid -> {
                    // File metadata saved successfully
                 //   StyleableToast.makeText(FolderActivity.this, "File metadata saved successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();

                    // Fetch and display updated files and folders
                    fetchFilesFromFirebase(folderId);  // Pass the folderId when fetching files
                })
                .addOnFailureListener(e -> {
                    // File metadata saving failed
                   // StyleableToast.makeText(FolderActivity.this, "File metadata saving failed", Toast.LENGTH_SHORT, R.style.mytoast).show();
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


    private void CreateSubFolder() {
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
                DatabaseReference foldersRef = FirebaseDatabase.getInstance().getReference().child("Subfolders").child(currentUserId).child(folderId);

                // Generate a unique key for the folder
                String folderId = foldersRef.push().getKey();

                // Store folder metadata in the Realtime Database
                FolderMetadata folderMetadata = new FolderMetadata(folderId, folderName);

                // Save folder metadata using the unique key
                foldersRef.child(folderId).setValue(folderMetadata)
                        .addOnSuccessListener(aVoid -> {
                            // Folder created successfully
                            StyleableToast.makeText(FolderActivity.this, "Folder created successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();
                            dialog.dismiss(); // Dismiss the dialog after creating the folder

//
                        })
                        .addOnFailureListener(e -> {
                            // Folder creation failed
                            StyleableToast.makeText(FolderActivity.this, "Folder creation failed", Toast.LENGTH_SHORT, R.style.mytoast).show();
                        });
            } else {
                folderNameEditText.setError("Please enter a folder name");
            }
        });

        // Show the dialog
        dialog.show();
    }



    private void MeowBottomNavigationShow(MeowBottomNavigation bottomNavigation) {
        bottomNavigation.add(new MeowBottomNavigation.Model(home, R.drawable.baseline_home_24));
        //    bottomNavigation.add(new MeowBottomNavigation.Model(search, R.drawable.baseline_search_24));
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

        }

    }



    protected void fetchFilesFromFirebase(String currentFolderId) {
        DatabaseReference foldersRef = FirebaseDatabase.getInstance().getReference().child("Subfolders").child(currentUserId).child(folderId);
        DatabaseReference filesRef = FirebaseDatabase.getInstance().getReference().child("filesinsideFolders").child(currentUserId);

        itemsList.clear();
        fileMetadataList.clear(); // Clear file metadata list
        folderMetadataList.clear(); // Clear folder metadata list

        foldersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                folderMetadataList.clear(); // Clear folder metadata list before populating
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FolderMetadata folderMetadata = dataSnapshot.getValue(FolderMetadata.class);
                    if (folderMetadata != null) {
                        folderMetadata.setFolderId(dataSnapshot.getKey());
                        folderMetadataList.add(folderMetadata); // Add folder to folder metadata list
                        folderMetadata.setKey(dataSnapshot.getKey());
                        itemsList.add(folderMetadata); // Add folder to combined list
                    }
                }
                itemsList.clear();
                itemsList.addAll(fileMetadataList);
                itemsList.addAll(folderMetadataList);
                // Update RecyclerView after processing folders and files
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(FolderActivity.this, "Failed to fetch folders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add an event listener for files
        filesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fileMetadataList.clear(); // Clear file metadata list before populating
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FileMetadata fileMetadata = dataSnapshot.getValue(FileMetadata.class);
                    if (fileMetadata != null && fileMetadata.getFolderId().equals(currentFolderId)) {
                        fileMetadata.setKey(dataSnapshot.getKey());
                        fileMetadataList.add(fileMetadata); // Add file to file metadata list
                        fileMetadata.setPage("filesinsideFolders");
                        itemsList.add(fileMetadata); // Add file to combined list
                    }
                }
                itemsList.clear();
                itemsList.addAll(fileMetadataList);
                itemsList.addAll(folderMetadataList);
                // Update RecyclerView after processing folders and files
                updateRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(FolderActivity.this, "Failed to fetch files: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateRecyclerView() {
        // Notify the adapter about changes in the combined list
        fileAdapter.setItemsList(itemsList);
        fileAdapter.notifyDataSetChanged();
    }



}
