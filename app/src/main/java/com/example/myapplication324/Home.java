package com.example.myapplication324;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
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
import com.google.firebase.storage.UploadTask;

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

    private FloatingActionButton fab_main, fab1_mail, fab2_share;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_mail, textview_share;
    Boolean isOpen = false;

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;

   private List<FileMetadata> fileMetadataList = new ArrayList<>();

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
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);

        textview_mail = (TextView) findViewById(R.id.textview_mail);
        textview_share = (TextView) findViewById(R.id.textview_share);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOpen) {

                    textview_mail.setVisibility(View.INVISIBLE);
                    textview_share.setVisibility(View.INVISIBLE);
                    fab2_share.startAnimation(fab_close);
                    fab1_mail.startAnimation(fab_close);
                    fab_main.startAnimation(fab_anticlock);
                    fab2_share.setClickable(false);
                    fab1_mail.setClickable(false);
                    isOpen = false;
                } else {
                    textview_mail.setVisibility(View.VISIBLE);
                    textview_share.setVisibility(View.VISIBLE);
                    fab2_share.startAnimation(fab_open);
                    fab1_mail.startAnimation(fab_open);
                    fab_main.startAnimation(fab_clock);
                    fab2_share.setClickable(true);
                    fab1_mail.setClickable(true);
                    isOpen = true;
                }

            }
        });


        fab2_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "upload file", Toast.LENGTH_SHORT).show();

            }
        });

        fab1_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "creat folder", Toast.LENGTH_SHORT).show();

            }
        });
        // end of FloatingActionButton
        t1 = findViewById(R.id.name);
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

        if (auth.getCurrentUser() != null) {
            rtvFullName = auth.getCurrentUser().getEmail();
            t1.setText(rtvFullName);

        } else {
            StyleableToast.makeText(Home.this, "Error = No users Found !", Toast.LENGTH_SHORT,R.style.mytoast).show();
        }
//Bottom nav
        MeowBottomNavigation bottomNavigation = findViewById(R.id.meow);
        bottomNavigation.show(1,true);
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

        recyclerView = findViewById(R.id.recyclerView); // Add RecyclerView in your XML layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileAdapter = new FileAdapter();
        recyclerView.setAdapter(fileAdapter);

        // Call method to fetch files from Firebase
        fetchFilesFromFirebase();

    }


    private void fetchFilesFromFirebase() {
        DatabaseReference filesRef = FirebaseDatabase.getInstance().getReference().child("files").child(currentUserId);

        filesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fileMetadataList.clear(); // Clear existing data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FileMetadata fileMetadata = dataSnapshot.getValue(FileMetadata.class);
                    fileMetadataList.add(fileMetadata);
                }
                fileAdapter.setFileList(fileMetadataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(Home.this, "Failed to fetch files: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
    private static class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

        private List<FileMetadata> fileList = new ArrayList<>();

        public void setFileList(List<FileMetadata> fileList) {
            this.fileList = fileList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FileMetadata fileMetadata = fileList.get(position);
            holder.fileNameTextView.setText(fileMetadata.getFileName());
            // You can handle file download or other actions here if needed
        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView fileNameTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                fileNameTextView = itemView.findViewById(R.id.fileNameTextView); // Add TextView in your item layout
            }
        }
    }
}