//package com.example.myapplication324;
//
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    private List<Object> itemsList;
//
//    public FileAdapter(List<Object> itemsList) {
//        this.itemsList = itemsList;
//    }
//
//
//    public void setItemsList(List<Object> itemsList) {
//            this.itemsList = itemsList;
//      }
//    @Override
//    public int getItemViewType(int position) {
//        Object item = itemsList.get(position);
//
//        if (item instanceof FileMetadata) {
//            return 0;
//        } else if (item instanceof FolderMetadata) {
//            return 1;
//        }
//
//        return -1;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        RecyclerView.ViewHolder viewHolder;
//
//        switch (viewType) {
//            case 0:
//                View fileView = inflater.inflate(R.layout.item_file, parent, false);
//                viewHolder = new FileViewHolder(fileView);
//                break;
//            case 1:
//                View folderView = inflater.inflate(R.layout.item_folder, parent, false);
//                viewHolder = new FolderViewHolder(folderView);
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + viewType);
//        }
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Object item = itemsList.get(position);
//
//        if (holder instanceof FileViewHolder && item instanceof FileMetadata) {
//            FileMetadata fileMetadata = (FileMetadata) item;
//            ((FileViewHolder) holder).fileNameTextView.setText(fileMetadata.getFileName());
//        } else if (holder instanceof FolderViewHolder && item instanceof FolderMetadata) {
//            FolderMetadata folderMetadata = (FolderMetadata) item;
//            ((FolderViewHolder) holder).folderNameTextView.setText(folderMetadata.getFolderName());
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return itemsList.size();
//    }
//
//    // FileViewHolder and FolderViewHolder classes remain the same
//    // ...
//
//    static class FileViewHolder extends RecyclerView.ViewHolder {
//        TextView fileNameTextView;
//
//        public FileViewHolder(@NonNull View itemView) {
//            super(itemView);
//            fileNameTextView = itemView.findViewById(R.id.fileNameTextView); // Replace with your file item view
//        }
//    }
//
//    static class FolderViewHolder extends RecyclerView.ViewHolder {
//        TextView folderNameTextView;
//
//        public FolderViewHolder(@NonNull View itemView) {
//            super(itemView);
//            folderNameTextView = itemView.findViewById(R.id.folderNameTextView); // Replace with your folder item view
//            // Add click listener for the folder item
//            itemView.setOnClickListener(v -> FolderUtils.openFolder(getAdapterPosition()));
//        }
//    }
//
//
//}
package com.example.myapplication324;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.MoreObjects;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URL;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> itemsList;
    private Context context; // Add a reference to the context
    private int currentPosition = -1; // Initialize currentPosition to -1
    public FileAdapter(List<Object> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    public void setItemsList(List<Object> itemsList) {
        this.itemsList = itemsList;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = itemsList.get(position);

        if (item instanceof FileMetadata) {
            return 0;
        } else if (item instanceof FolderMetadata) {
            return 1;
        }

        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case 0:
                View fileView = inflater.inflate(R.layout.item_file, parent, false);
                viewHolder = new FileViewHolder(fileView);
                break;
            case 1:
                View folderView = inflater.inflate(R.layout.item_folder, parent, false);
                viewHolder = new FolderViewHolder(folderView);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = itemsList.get(position);

        if (holder instanceof FileViewHolder && item instanceof FileMetadata) {
            FileMetadata fileMetadata = (FileMetadata) item;
            ((FileViewHolder) holder).fileNameTextView.setText(fileMetadata.getFileName());
          // ((FileViewHolder) holder).fileLinkTextView.setText(fileMetadata.getFileDownloadUrl()); // شلته عشان ماينعرض  اسم الرابط بالصفحه
            ((FileViewHolder) holder).options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     currentPosition = holder.getAdapterPosition();
                    showPopupMenu(v);        }

                private void showPopupMenu(View itemView) {


                 //   AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                        PopupMenu popupMenu = new PopupMenu(itemView.getContext(), itemView);
                        MenuInflater inflater = popupMenu.getMenuInflater();
                        inflater.inflate(R.menu.file_popup_menu, popupMenu.getMenu());

                        // Set a click listener on the popup menu items
                        popupMenu.setOnMenuItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.menu_rename:
                                    showRenameDialog(itemView);
                                    break;
                                case R.id.menu_download:
                                    // Perform download action
                                    downLoadFile( ((FileViewHolder) holder).fileNameTextView.getContext(), fileMetadata.getFileName(), ".pdf",DIRECTORY_DOWNLOADS, fileMetadata.getFileDownloadUrl());
                                    break;
                                case R.id.menu_delete:
                                    // Perform delete action
                                    FileMetadata currentFileMetadata = (FileMetadata) itemsList.get(currentPosition); // Access the file metadata from the list
                                    String table = checkSubstring(context.toString());
                                    getParentKeyByChildKey(currentFileMetadata.getKey(),table);

                                    break;
                            }
                            return true;
                        });

                        popupMenu.show();

                }



                private void showRenameDialog(View itemView) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Rename File");
                    FileMetadata currentFileMetadata = (FileMetadata) itemsList.get(currentPosition); // Access the file metadata from the list
                    String currentFileName = currentFileMetadata.getFileName(); // Get the current file name

// Create the input field for the new name
                    final EditText input = new EditText(itemView.getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setText(currentFileName); // Set the current file name as the initial text
                    input.setSelection(currentFileName.length()); // Set cursor position at the end of the text
                    builder.setView(input);

// Set the positive button and its click listener
                    builder.setPositiveButton("Rename", (dialog, which) -> {
                        String newName = input.getText().toString().trim();
                        if (!newName.equals(currentFileName)) {
                            // Handle the file rename logic here
                            String fileId = currentFileMetadata.getFileDownloadUrl(); // Get the file ID from the metadata

                            // Update the file name in the metadata
                            currentFileMetadata.setFileName(newName);

                            // Update the file name in the UI
                            ((FileViewHolder) holder).fileNameTextView.setText(newName);

                            // Update the file name in Firebase
                            DatabaseReference fileRef = FirebaseDatabase.getInstance().getReference().child("files");
                            fileRef.child("fileName").setValue(newName);
                        }
                    });

// Set the negative button and its click listener
                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                    builder.show();
                }
            });

        } else if (holder instanceof FolderViewHolder && item instanceof FolderMetadata) {
            FolderMetadata folderMetadata = (FolderMetadata) item;
            ((FolderViewHolder) holder).folderNameTextView.setText(folderMetadata.getFolderName());

            // Pass the context and folderId to FolderViewHolder
            ((FolderViewHolder) holder).setContextAndFolderId(context, folderMetadata.getFolderId());
            ((FolderViewHolder) holder).options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = holder.getAdapterPosition();
                    showPopupMenu(v);        }

                private void showPopupMenu(View itemView) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), itemView);
                    MenuInflater inflater = popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.folder_popup_menu, popupMenu.getMenu());

                    // Set a click listener on the popup menu items
                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.menu_rename:
                                showRenameDialog(itemView);
                                break;
                            case R.id.menu_delete:
                                // Perform delete action for folders
                                break;
                        }
                        return true;
                    });

                    popupMenu.show();
                }

                private void showRenameDialog(View itemView) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Rename Folder");
                    FolderMetadata currentFolderMetadata = (FolderMetadata) itemsList.get(currentPosition); // Access the folder metadata from the list
                    String currentFolderName = currentFolderMetadata.getFolderName(); // Get the current folder name

                    // Create the input field for the new name
                    final EditText input = new EditText(itemView.getContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setText(currentFolderName); // Set the current folder name as the initial text
                    input.setSelection(currentFolderName.length()); // Set cursor position at the end of the text
                    builder.setView(input);

                    // Set the positive button and its click listener
                    builder.setPositiveButton("Rename", (dialog, which) -> {
                        String newName = input.getText().toString().trim();
                        if (!newName.equals(currentFolderName)) {
                            // Handle the folder rename logic here
                            currentFolderMetadata.setFolderName(newName);

                            // Update the name in the UI
                            ((FolderViewHolder) holder).folderNameTextView.setText(newName);

                            // Update the name in Firebase or your data source
                            DatabaseReference folderRef = FirebaseDatabase.getInstance().getReference().child("folders").child(currentFolderMetadata.getFolderId());
                            folderRef.child("folderName").setValue(newName);
                        }
                    });

                    // Set the negative button and its click listener
                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                    builder.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    private void deleteFile(String fileKey) {
        // Create a storage reference from our app
         Toast.makeText(context, fileKey, Toast.LENGTH_SHORT).show();


    }
    private void deleteData(final String parentKey, final String username,String table) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this file?");

        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(table);
                reference.child(parentKey).child(username).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            StyleableToast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT,R.style.mytoast).show();
                        } else {
                            StyleableToast.makeText(context, "Failed", Toast.LENGTH_SHORT,R.style.mytoast).show();
                        }
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked No button

                dialog.dismiss();
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Show the dialog
        dialog.show();
    }


    private void getParentKeyByChildKey(String childKey,String table) { // take the parants id
        DatabaseReference filesRef = FirebaseDatabase.getInstance().getReference().child(table);

        filesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.child(childKey).exists()) {
                        String parentKey = userSnapshot.getKey();

                        // Now you have the parent key
                        Log.d("PARENT_KEY", parentKey);
                        deleteData(parentKey,childKey,table);

                        return; // Exit the loop after finding the first match
                    }
                }

                // Handle the case when the child key is not found
                Log.d("CHILD_KEY_NOT_FOUND", "Child key not found");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Log.e("ERROR", "Database Error: " + databaseError.getMessage());
            }
        });
    }

    public static String checkSubstring(String word) {
        String substring = "home"; // Specify your substring here
        String lowercaseWord = word.toLowerCase();

        if (lowercaseWord.contains(substring)) {
            Log.e("Yes", " file ");
            return "files";
        } else {
            Log.e("No", " filesinsideFolders ");
            return "filesinsideFolders";
        }
    }






    // FileViewHolder and FolderViewHolder classes remain the same
    // ...
    public void downLoadFile(Context context, String fileName,String fileExtension,String destinationDirectory,String url){

        DownloadManager downloadManager= (DownloadManager)context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName+ fileExtension);

        downloadManager.enqueue(request);

    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView,fileLinkTextView;
        Button buttonDownLoad;
        ImageButton options;


        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTextView); // Replace with your file item view
            fileLinkTextView = itemView.findViewById(R.id.fileLinkTextView);

            options=itemView.findViewById(R.id.optionsButton);

        }



    }



    static class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderNameTextView;

        private Context context;
        private String folderId;
        ImageButton options;
        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderNameTextView = itemView.findViewById(R.id.folderNameTextView); // Replace with your folder item view
            // Add click listener for the folder item
            itemView.setOnClickListener(v -> FolderUtils.openFolder(context, folderId));
            options=itemView.findViewById(R.id.optionsButton);
        }

        // Method to set the context and folderId
        public void setContextAndFolderId(Context context, String folderId) {
            this.context = context;
            this.folderId = folderId;
        }
    }
}
