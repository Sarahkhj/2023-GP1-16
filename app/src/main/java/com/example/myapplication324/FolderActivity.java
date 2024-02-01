package com.example.myapplication324;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.myapplication324.databinding.ActivityFolderBinding;
import com.example.myapplication324.databinding.ActivityHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
                ShowDialog();

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


    private void callChooseWordFile() {

        Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent1.addCategory(Intent.CATEGORY_OPENABLE);
        intent1.setType("*/*");
        String[] mimetype = {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent1.putExtra(Intent.EXTRA_MIME_TYPES, mimetype);
        startActivityForResult(intent1, PICK_WORD_FILE);

    }


    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case CHOSE_PDF_FROM_DEVICE:
//                    // Handle the PDF file selected from device
//                    if (data != null) {
//                        // Get the URI of the selected PDF file
//                        Uri pdfUri = data.getData();
//
//                        // Perform the necessary actions with the selected PDF file
//                        // For example, you might want to upload it to Firebase Storage
//                        uploadFileToFirebase(pdfUri);
//                    }
//                    break;
//
//                case PICK_WORD_FILE:
//                    // Handle the Word file selected from device
//                    if (data != null) {
//                        // Get the URI of the selected Word file
//                        Uri wordUri = data.getData();
//
//                        // Perform the necessary actions with the selected Word file
//                        // For example, you might want to upload it to Firebase Storage
//                        uploadFileToFirebase(wordUri);
//                    }
//                    break;
//
//                // Add more cases if needed for other request codes
//            }
//        }
//    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String folderId = getFolderId(); // Replace this with the actual method to get the folderId

            switch (requestCode) {
                case CHOSE_PDF_FROM_DEVICE:
                    // Handle the PDF file selected from device
                    if (data != null) {
                        // Get the URI of the selected PDF file
                        Uri pdfUri = data.getData();

                        // Perform the necessary actions with the selected PDF file
                        // For example, you might want to upload it to Firebase Storage
                        uploadFileToFirebase(pdfUri, folderId);
                    }
                    break;

                case PICK_WORD_FILE:
                    // Handle the Word file selected from device
                    if (data != null) {
                        // Get the URI of the selected Word file
                        Uri wordUri = data.getData();

                        // Perform the necessary actions with the selected Word file
                        // For example, you might want to upload it to Firebase Storage
                        uploadFileToFirebase(wordUri, folderId);
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


    private void uploadFileToFirebase(Uri fileUri, String folderId) {
        // Sample logic for uploading a file to Firebase Storage within a specific folder
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to the folder in Firebase Storage
        StorageReference folderRef = storageRef.child("folders/" + folderId);

        // Create a reference to the file within the folder
        StorageReference fileRef = folderRef.child(UUID.randomUUID().toString());

        // Upload the file to Firebase Storage
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully
                    StyleableToast.makeText(FolderActivity.this, "File uploaded successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();
                })
                .addOnFailureListener(e -> {
                    // File upload failed
                    StyleableToast.makeText(FolderActivity.this, "File upload failed", Toast.LENGTH_SHORT, R.style.mytoast).show();
                });
    }

//    private void uploadFileToFirebase(Uri fileUri) {
//        // Sample logic for uploading a file to Firebase Storage
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//
//        // Create a reference to the file in Firebase Storage
//        StorageReference fileRef = storageRef.child("uploads/" + UUID.randomUUID().toString());
//
//        // Upload the file to Firebase Storage
//        fileRef.putFile(fileUri)
//                .addOnSuccessListener(taskSnapshot -> {
//                    // File uploaded successfully
//                    StyleableToast.makeText(FolderActivity.this, "File uploaded successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();
//                })
//                .addOnFailureListener(e -> {
//                    // File upload failed
//                    StyleableToast.makeText(FolderActivity.this, "File upload failed", Toast.LENGTH_SHORT, R.style.mytoast).show();
//                });
//    }

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
                DatabaseReference foldersRef = FirebaseDatabase.getInstance().getReference().child("folders").child(currentUserId);

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

    private void createSubfolder(String parentFolderId) {
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

            // Perform subfolder creation logic here
            if (!folderName.isEmpty()) {
                DatabaseReference foldersRef = FirebaseDatabase.getInstance().getReference().child("folders").child(currentUserId);

                // Generate a unique key for the subfolder
                String subfolderId = foldersRef.push().getKey();

                // Store subfolder metadata in the Realtime Database
                FolderMetadata subfolderMetadata = new FolderMetadata(subfolderId, folderName);

                // Save subfolder metadata using the unique key
                foldersRef.child(subfolderId).setValue(subfolderMetadata)
                        .addOnSuccessListener(aVoid -> {
                            // Subfolder created successfully
                            StyleableToast.makeText(FolderActivity.this, "Subfolder created successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();
                            dialog.dismiss(); // Dismiss the dialog after creating the subfolder

                            // Update the parent folder's metadata to include the subfolder
                            DatabaseReference parentFolderRef = foldersRef.child(parentFolderId);
                            parentFolderRef.child("subfolders").child(subfolderId).setValue(true);
                        })
                        .addOnFailureListener(e -> {
                            // Subfolder creation failed
                            StyleableToast.makeText(FolderActivity.this, "Subfolder creation failed", Toast.LENGTH_SHORT, R.style.mytoast).show();
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



    private void openFolder(int position) {
        if (position >= 0 && position < folderMetadataList.size()) {
            FolderMetadata clickedFolder = folderMetadataList.get(position);
            String folderId = clickedFolder.getFolderId();

            // Use the utility method from FolderUtils
            FolderUtils.openFolder(this, folderId);
        }
    }


//    private void openFolder(int position) {
//        if (position >= 0 && position < folderMetadataList.size()) {
//            FolderMetadata clickedFolder = folderMetadataList.get(position);
//            String folderId = clickedFolder.getFolderId();
//
//
//            // Start FolderActivity and pass the folderId
//            Intent folderIntent = new Intent(this, FolderActivity.class);
//            folderIntent.putExtra("folderId", folderId);
//            startActivity(folderIntent);
//        }
//    }



//    private class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//        private List<Object> itemsList = new ArrayList<>();
//
//
//        public void setItemsList(List<Object> itemsList) {
//            this.itemsList = itemsList;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            Object item = itemsList.get(position);
//
//            if (item instanceof FileMetadata) {
//                return 0;
//            } else if (item instanceof FolderMetadata) {
//                return 1;
//            }
//
//            return -1;
//        }
//
//
//
//        @NonNull
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//            RecyclerView.ViewHolder viewHolder;
//
//            switch (viewType) {
//                case 0:
//                    View fileView = inflater.inflate(R.layout.item_file, parent, false);
//                    viewHolder = new FileAdapter.FileViewHolder(fileView);
//                    break;
//                case 1:
//                    View folderView = inflater.inflate(R.layout.item_folder, parent, false);
//                    viewHolder = new FileAdapter.FolderViewHolder(folderView);
//                    break;
//                default:
//                    throw new IllegalStateException("Unexpected value: " + viewType);
//            }
//            return viewHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//            Object item = itemsList.get(position);
//
//            if (holder instanceof FileAdapter.FileViewHolder && item instanceof FileMetadata) {
//                FileMetadata fileMetadata = (FileMetadata) item;
//                ((FileAdapter.FileViewHolder) holder).fileNameTextView.setText(fileMetadata.getFileName());
//            } else if (holder instanceof FileAdapter.FolderViewHolder && item instanceof FolderMetadata) {
//                FolderMetadata folderMetadata = (FolderMetadata) item;
//                ((FileAdapter.FolderViewHolder) holder).folderNameTextView.setText(folderMetadata.getFolderName());
//            }
//        }
//
//
//
//        @Override
//        public int getItemCount() {
//            return itemsList.size();
//        }
//
//        class FileViewHolder extends RecyclerView.ViewHolder {
//            TextView fileNameTextView;
//
//            public FileViewHolder(@NonNull View itemView) {
//                super(itemView);
//                fileNameTextView = itemView.findViewById(R.id.fileNameTextView); // Replace with your file item view
//            }
//        }
//
//        class FolderViewHolder extends RecyclerView.ViewHolder {
//            TextView folderNameTextView;
//
//            public FolderViewHolder(@NonNull View itemView) {
//                super(itemView);
//                folderNameTextView = itemView.findViewById(R.id.folderNameTextView); // Replace with your folder item view
//                // Add click listener for the folder item
//                itemView.setOnClickListener(v -> openFolder(getAdapterPosition()));
//            }
//        }
}
