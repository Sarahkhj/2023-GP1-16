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
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;
import java.util.List;

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
