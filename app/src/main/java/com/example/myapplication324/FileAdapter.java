
package com.example.myapplication324;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.text.InputType;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> itemsList;
    private Context context; // Add a reference to the context
    private int currentPosition = -1; // Initialize currentPosition to -1
    String currentActivityName;

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
    private boolean containsInvalidCharacters(String name) {
        String invalidCharacters = "[]{}()/\\:?\"<>|*";
        for (int i = 0; i < name.length(); i++) {
            if (invalidCharacters.contains(String.valueOf(name.charAt(i)))) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = itemsList.get(position);
        currentActivityName =context.toString();

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
                    if (currentActivityName.toLowerCase().contains("favorite")) {
                        popupMenu.getMenu().removeItem(R.id.menu_favorite);
                        popupMenu.getMenu().removeItem(R.id.menu_download);

                    }
                    else{
                        popupMenu.getMenu().removeItem(R.id.menu_removefavorite);
                    }
                    try {
                        Field mPopupField = PopupMenu.class.getDeclaredField("mPopup");
                        mPopupField.setAccessible(true);
                        Object mPopup = mPopupField.get(popupMenu);
                        mPopup.getClass().getDeclaredMethod("setForceShowIcon", boolean.class).invoke(mPopup, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                        // Set a click listener on the popup menu items
                        popupMenu.setOnMenuItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.menu_rename:
                                    showRenameDialog(itemView);
                                    break;
                                case R.id.menu_download:
                                    // Perform download action
                                    downLoadFile( ((FileViewHolder) holder).fileNameTextView.getContext(), fileMetadata.getFileName(),DIRECTORY_DOWNLOADS, fileMetadata.getFileDownloadUrl());
                                    break;
                                case R.id.menu_favorite:
                                    // Perform add to favorite action
                                    FileMetadata currentFileMetadatafavo = (FileMetadata) itemsList.get(currentPosition); // Access the file metadata from the list
                                    String tablefavo = checkSubstring(context.toString());
                                    getParentKeyByChildKeyfavo(currentFileMetadatafavo.getKey(),tablefavo,true);
                                    break;
                                case R.id.menu_removefavorite:
                                    // Perform add to favorite action
                                    FileMetadata currentFileMetadatafremov = (FileMetadata) itemsList.get(currentPosition); // Access the file metadata from the list
                                    String tableremv = currentFileMetadatafremov.getPage();
                                    getParentKeyByChildKeyfavo(currentFileMetadatafremov.getKey(),tableremv,false);
                                    break;
                                case R.id.menu_delete:
                                    // Perform delete action
                                    FileMetadata currentFileMetadata = (FileMetadata) itemsList.get(currentPosition); // Access the file metadata from the list
                                    //String table = checkSubstring(context.toString());
                                    String table = currentFileMetadata.getPage();
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
                    builder.setPositiveButton("Rename", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    // Get the positive button from the dialog
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(view -> {
                        String newName = input.getText().toString().trim();
                        if (!newName.equals(currentFileName)) {
                            // Check if the new name contains invalid characters
                            if (containsInvalidCharacters(newName)) {
                                input.setError("File name contains invalid characters");
                            } else {
                                // Handle the file rename logic here
                                String fileId = currentFileMetadata.getFileDownloadUrl(); // Get the file ID from the metadata

                                // Update the file name in the metadata
                                currentFileMetadata.setFileName(newName);

                                // Update the file name in the UI
                                ((FileViewHolder) holder).fileNameTextView.setText(newName);

                                // Update the file name in Firebase
                                String table = currentFileMetadata.getPage();
                                getParentKeyByChildKeyRENAME(currentFileMetadata.getKey(), table, newName);

                                dialog.dismiss(); // Close the dialog
                            }
                        }
                    });

                    // Set the negative button and its click listener
                    builder.setNegativeButton("Cancel", (dialog1, which) -> dialog.cancel());
                }
            });

        }     //file renaming done


        else if (holder instanceof FolderViewHolder && item instanceof FolderMetadata) {
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
                    try {
                        Field mPopupField = PopupMenu.class.getDeclaredField("mPopup");
                        mPopupField.setAccessible(true);
                        Object mPopup = mPopupField.get(popupMenu);
                        mPopup.getClass().getDeclaredMethod("setForceShowIcon", boolean.class).invoke(mPopup, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Set a click listener on the popup menu items
                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.menu_rename:
                                FolderMetadata currentFolderMetadata1 = (FolderMetadata) itemsList.get(currentPosition); // Access the folder metadata from the list
                                //checkKey(currentFolderMetadata.getKey());
                                String table1 = "folders";
                                //getParentKeyByChildKeyfolder(currentFolderMetadata.getKey(),table);
                                String page1 = checkSubstringfolder(context.toString());
                                if(page1.equalsIgnoreCase("folders")){
                                    showRenameDialog(itemView);   }
                                else {
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                    String tableName = "Subfolders"; // Replace with your actual table name
                                    String childKey = currentFolderMetadata1.getKey(); // The child key you're looking for
                                    showRenameDialogSub(itemView,rootRef,tableName,childKey);
                                }
                                break;
                            case R.id.menu_delete:
                                // Perform delete action for folders
                                FolderMetadata currentFolderMetadata = (FolderMetadata) itemsList.get(currentPosition); // Access the folder metadata from the list
                                //checkKey(currentFolderMetadata.getKey());
                                String table = "folders";
                                //getParentKeyByChildKeyfolder(currentFolderMetadata.getKey(),table);
                                String page = checkSubstringfolder(context.toString());
                                if(page.equalsIgnoreCase("folders")){
                                showDeleteConfirmationDialog(currentFolderMetadata.getKey(),table);}
                                else {
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                    String tableName = "Subfolders"; // Replace with your actual table name
                                    String childKey = currentFolderMetadata.getKey(); // The child key you're looking for
                                    showDeleteConfirmationDialoginside(rootRef, tableName, childKey);
                                }
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
                    builder.setPositiveButton("Rename", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    // Get the positive button from the dialog
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(view -> {
                        String newName = input.getText().toString().trim();
                        if (!newName.equals(currentFolderName)) {
                            // Check if the new name contains invalid characters
                            if (containsInvalidCharacters(newName)) {
                                input.setError("Folder name contains invalid characters");
                                Toast.makeText(itemView.getContext(), "Folder name contains invalid characters", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the folder rename logic here
                                currentFolderMetadata.setFolderName(newName);

                                // Update the name in the UI
                                ((FolderViewHolder) holder).folderNameTextView.setText(newName);
                                String folderKey = currentFolderMetadata.getKey();


                                //for now folders then sub folder->>>getting from a method which table
                                getParentKeyByChildKeyFolder(folderKey, "folders", newName);

                                dialog.dismiss(); // Close the dialog
                            }
                        }
                    });

                    // Set the negative button and its click listener
                    builder.setNegativeButton("Cancel", (dialog1, which) -> dialog1.cancel());
                }
                private void showRenameDialogSub(View itemView, DatabaseReference rootRef, String tableName, String childKey) {
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
                    builder.setPositiveButton("Rename", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    // Get the positive button from the dialog
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(view -> {
                        String newName = input.getText().toString().trim();
                        if (!newName.equals(currentFolderName)) {
                            // Check if the new name contains invalid characters
                            if (containsInvalidCharacters(newName)) {
                                input.setError("Folder name contains invalid characters");
                                Toast.makeText(itemView.getContext(), "Folder name contains invalid characters", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the folder rename logic here
                                currentFolderMetadata.setFolderName(newName);

                                // Update the name in the UI
                                ((FolderViewHolder) holder).folderNameTextView.setText(newName);
                                String folderId = currentFolderMetadata.getFolderId();

                                // Call the updateSubFolderName method with appropriate parameters
                                findParentKeysSub(rootRef, tableName, childKey, newName, itemView);
                                dialog.dismiss(); // Close the dialog
                            }
                        }
                    });

                    // Set the negative button and its click listener
                    builder.setNegativeButton("Cancel", (dialog1, which) -> dialog1.cancel());
                }

                private void findParentKeysSub(DatabaseReference databaseRef, final String tableName, final String childKey, final String newName, final View itemView) {
                    // Get a reference to the specific table
                    DatabaseReference tableRef = databaseRef.child(tableName);

                    // Listen for a single snapshot of the data at this table path
                    tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Flag to check if the child key is found
                            boolean isChildKeyFound = false;

                            // Iterate over the top-level children (root parent keys)
                            for (DataSnapshot rootParentSnapshot : dataSnapshot.getChildren()) {
                                String rootParentKey = rootParentSnapshot.getKey();

                                // Iterate over the children of each root parent (sub parent keys)
                                for (DataSnapshot subParentSnapshot : rootParentSnapshot.getChildren()) {
                                    String subParentKey = subParentSnapshot.getKey();

                                    // Check if this is the immediate parent of the childKey
                                    if (subParentSnapshot.hasChild(childKey)) {
                                        // We've found the immediate parent and root parent of the childKey
                                        Log.d("PARENT_KEYS_FOUND", "Root Parent Key: " + rootParentKey + ", Immediate Parent Key: " + subParentKey);
                                        isChildKeyFound = true;
                                        // Rename the sub parent key
                                        updateSubFolderName(databaseRef, tableName, rootParentKey, subParentKey, childKey, newName, itemView);

                                        break;
                                    }
                                }

                                // If the child key has been found, break out of the loop
                                if (isChildKeyFound) {
                                    break;
                                }
                            }

                            // If we get here, it means the child key was not found under any sub parents
                            if (!isChildKeyFound) {
                                Log.d("PARENT_KEYS_NOT_FOUND", "Could not find parent keys for the child key: " + childKey);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle the database error
                        }
                    });
                }



                private void updateSubFolderName(DatabaseReference databaseRef, String tableName, String rootParentKey, String subParentKey, String childKey, String newName, View itemView) {
                    DatabaseReference folderRef = databaseRef.child(tableName).child(rootParentKey).child(subParentKey).child(childKey);
                    folderRef.child("folderName").setValue(newName)
                            .addOnSuccessListener(aVoid -> {
                                // Folder name update in Firebase Realtime Database successful
                                StyleableToast.makeText(itemView.getContext(), "Folder name updated successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();

                                // Perform any additional actions after updating the folder name
                            })
                            .addOnFailureListener(e -> {
                                // Folder name update in Firebase Realtime Database failed
                                StyleableToast.makeText(itemView.getContext(), "Failed to update folder name", Toast.LENGTH_SHORT, R.style.mytoast).show();
                                // Handle the failure case
                            });
                }
            });
        }
    }


    private void getParentKeyByChildKeyFolder(String childKey, String table, String newName) { ///folder renaming
        DatabaseReference foldersRef = FirebaseDatabase.getInstance().getReference().child(table);
        foldersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot folderSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot childSnapshot : folderSnapshot.getChildren()) {
                        if (childSnapshot.getKey().equals(childKey)) {
                            String parentKey = folderSnapshot.getKey();


                            // Now you have the parent key
                            Log.d("PARENT_KEY", parentKey);
                            updateFolderName(parentKey,childKey,newName,"folders");
                            // Perform any additional actions with the parent key
                            // ...

                            return; // Exit the loop after finding the first match
                        }
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
    private void updateFolderName(String parentKey, String childKey, String newName, String table) {
        DatabaseReference folderRef = FirebaseDatabase.getInstance().getReference().child(table).child(parentKey).child(childKey);
        folderRef.child("folderName").setValue(newName)
                .addOnSuccessListener(aVoid -> {
                    // Folder name update in Firebase Realtime Database successful
                    StyleableToast.makeText(context, "Folder name updated successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();


                    // Perform any additional actions after updating the folder name
                })
                .addOnFailureListener(e -> {
                    // Folder name update in Firebase Realtime Database failed
                    StyleableToast.makeText(context, "Failed to update folder name", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    // Handle the failure case
                });
    }

    private void getParentKeyByChildKeyRENAME(String childKey, String table, String newName) {
        DatabaseReference filesRef = FirebaseDatabase.getInstance().getReference().child(table);

        filesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.child(childKey).exists()) {
                        String parentKey = userSnapshot.getKey();

                        // Now you have the parent key
                        Log.d("PARENT_KEY", parentKey);
                        // Update the file name
                        checkIfFileNameExists(parentKey, childKey, newName, table);

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
    private void checkIfFileNameExists(String parentKey, String childKey, String newName, String table) {
        DatabaseReference filesRef = FirebaseDatabase.getInstance().getReference().child(table);

        filesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.getKey().equals(parentKey)) {
                        for (DataSnapshot fileSnapshot : userSnapshot.getChildren()) {
                            if (fileSnapshot.child("fileName").getValue(String.class).equals(newName)) {
                                // The file name already exists, so add a number to it
                                int count = 1;
                                String baseName = newName.substring(0, newName.lastIndexOf('.'));
                                String extension = newName.substring(newName.lastIndexOf('.'));
                                String modifiedName = baseName + " (" + count + ")" + extension;

                                // Keep incrementing the count until a unique name is found
                                while (fileSnapshot.child("fileName").getValue(String.class).equals(modifiedName)) {
                                    count++;
                                    modifiedName = baseName + " (" + count + ")" + extension;
                                }

                                // Update the file name in Firebase
                                updateFileName(parentKey, childKey, modifiedName, table);
                                return; // Exit the loop after finding the first match
                            }
                        }
                    }
                }

                // The file name is unique, so proceed with the rename
                updateFileName(parentKey, childKey, newName, table);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Log.e("ERROR", "Database Error: " + databaseError.getMessage());
            }
        });
    }
    private void updateFileName(String parentKey, String childKey, String newName, String table) {
        DatabaseReference fileRef = FirebaseDatabase.getInstance().getReference().child(table).child(parentKey).child(childKey);
        fileRef.child("fileName").setValue(newName)
                .addOnSuccessListener(aVoid -> {
                    // File name update in Firebase Realtime Database successful

                    StyleableToast.makeText(context, "File name updated successfully", Toast.LENGTH_SHORT,R.style.mytoast).show();

                    // Perform any additional actions after updating the file name
                })
                .addOnFailureListener(e -> {
                    // File name update in Firebase Realtime Database failed
                    StyleableToast.makeText(context, "Failed to update file name", Toast.LENGTH_SHORT,R.style.mytoast).show();


                    // Handle the failure case
                });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    private void checkKey(String fileKey) {
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
    //add to favorite

    private void getParentKeyByChildKeyfavo(String childKey,String table,Boolean F) { // take the parants id
        DatabaseReference filesRef = FirebaseDatabase.getInstance().getReference().child(table);

        filesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.child(childKey).exists()) {
                        String parentKey = userSnapshot.getKey();

                        // Now you have the parent key
                        Log.d("PARENT_KEY", parentKey);
                        if(F){
                        addFileToFavorites(parentKey,childKey,table);}
                        else{
                            removeFileFromFavorites(parentKey,childKey,table);
                        }

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
    private void addFileToFavorites(final String parentKey, final String username, String table) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(table);
        DatabaseReference favoriteRef = reference.child(parentKey).child(username).child("favorite");

        // Check if the file is already marked as favorite
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue(Boolean.class) == Boolean.FALSE) {
                    // If the favorites attribute exists and is not already true, set it as a favorite
                    favoriteRef.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                StyleableToast.makeText(context, "File added to favorites", Toast.LENGTH_SHORT, R.style.mytoastheart).show();
                            } else {
                                StyleableToast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT, R.style.mytoast).show();
                            }
                        }
                    });
                } else if (dataSnapshot.exists()) {
                    // If the favorites attribute exists and is already true, inform the user
                    StyleableToast.makeText(context, "File is already on favorites", Toast.LENGTH_SHORT, R.style.mytoastheart).show();
                } else {
                    // If the favorites attribute does not exist, inform the user it can't be added
                    StyleableToast.makeText(context, "Cannot add to favorites", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                StyleableToast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        });
    }
    private void removeFileFromFavorites(final String parentKey, final String username, String table) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(table);
        DatabaseReference favoriteRef = reference.child(parentKey).child(username).child("favorite");

        // Check if the file is marked as a favorite
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue(Boolean.class) == Boolean.TRUE) {
                    // If the favorites attribute exists and is true, remove it as a favorite
                    favoriteRef.setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                StyleableToast.makeText(context, "File removed from favorites", Toast.LENGTH_SHORT, R.style.mytoastheart).show();
                            } else {
                                StyleableToast.makeText(context, "Failed to remove from favorites", Toast.LENGTH_SHORT, R.style.mytoast).show();
                            }
                        }
                    });
                } else if (dataSnapshot.exists()) {
                    // If the favorites attribute exists and is already false, inform the user
                    StyleableToast.makeText(context, "File is not in favorites", Toast.LENGTH_SHORT, R.style.mytoast).show();
                } else {
                    // If the favorites attribute does not exist, inform the user it can't be removed
                    StyleableToast.makeText(context, "Cannot remove from favorites, file not found", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                StyleableToast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        });
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
    private void getParentKeyByChildKeyfolder(String childKey,String table) { // take the parants id
        DatabaseReference filesRef = FirebaseDatabase.getInstance().getReference().child(table);

        filesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.child(childKey).exists()) {
                        String parentKey = userSnapshot.getKey();

                        // Now you have the parent key
                        Log.d("PARENT_KEY", parentKey);
                      //  deleteDatafolder(parentKey,childKey,table);
                        removeChildrenAndFolder(parentKey,childKey);

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
    public void removeChildrenAndFolder(String parentKey, final String targetFolderId) {
        // Reference to the parent node
        String fileTable = "filesinsideFolders";
        DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference(fileTable).child(parentKey);
        // Attach a listener to read the data at the parent node
        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the parent node exists
                if (dataSnapshot.exists()) {
                    // Iterate through the children of the parent node
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        // Access the "folder id" attribute inside each child
                        String folderId = childSnapshot.child("folderId").getValue(String.class);

                        // Check if the folder id matches the target folder id
                        if (folderId != null && folderId.equals(targetFolderId)) {
                            // Remove the child node
                            childSnapshot.getRef().removeValue();
                        }
                    }
                    // remove folder that inside folder
                    removeChildAndItsChildren(parentKey,targetFolderId);
                    // Remove the folder
                    deleteFolderOnly(parentKey, targetFolderId);
                } else {
                    removeChildAndItsChildren(parentKey,targetFolderId);
                    // Remove the folder
                    deleteFolderOnly(parentKey, targetFolderId);
                    // Parent node with the given key does not exist
                    //System.out.println("Parent not found for key: " + parentKey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors, if any
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }
    public void removeChildAndItsChildren(String parentKey, final String childKey) {
        // Reference to the Subfolders table under the specified parent key
        String tableSubfolders = "Subfolders";
        DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference(tableSubfolders).child(parentKey);

        // Attach a listener to read the data at the specified child node
        parentRef.child(childKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the child node exists
                if (dataSnapshot.exists()) {
                    // Remove the child node and its children
                    dataSnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                // Handle the case where the delete operation failed
                                System.out.println("Error deleting child: " + databaseError.getMessage());
                            } else {
                                // Operation was successful
                                System.out.println("Child and its children successfully deleted");
                            }
                        }
                    });
                } else {
                    // Child node with the given key does not exist
                    System.out.println("Child not found for key: " + childKey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors, if any
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }
    private void deleteFolderOnly(final String parentKey, final String username) {
        String folderTable = "folders";

        // Delete the folder without confirmation
        DatabaseReference folderReference = FirebaseDatabase.getInstance().getReference(folderTable).child(parentKey).child(username);

        // Delete the folder itself
        folderReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    StyleableToast.makeText(context, "Successfully Deleted Folder", Toast.LENGTH_SHORT, R.style.mytoast).show();
                } else {
                    StyleableToast.makeText(context, "Failed to delete the folder", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            }
        });
    }
////////////////
    private void showDeleteConfirmationDialog(String childKey,String table) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this folder?");

        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                getParentKeyByChildKeyfolder(childKey, table);
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
    private void updateSubFolderName(DatabaseReference rootRef, String parentKey, String childKey, String newName, String table) {
        DatabaseReference folderRef = rootRef.child(table).child(parentKey).child(childKey);
        folderRef.child("folderName").setValue(newName)
                .addOnSuccessListener(aVoid -> {
                    // Folder name update in Firebase Realtime Database successful
                    StyleableToast.makeText(context, "Folder name updated successfully", Toast.LENGTH_SHORT, R.style.mytoast).show();

                    // Perform any additional actions after updating the folder name
                })
                .addOnFailureListener(e -> {
                    // Folder name update in Firebase Realtime Database failed
                    StyleableToast.makeText(context, "Failed to update folder name", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    // Handle the failure case
                });
    }

    //folderinsidemfolder
    private void findParentKeys(DatabaseReference databaseRef, final String tableName, final String childKey) {
        // Get a reference to the specific table
        DatabaseReference tableRef = databaseRef.child(tableName);

        // Listen for a single snapshot of the data at this table path
        tableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Flag to check if the child key is found
                boolean isChildKeyFound = false;

                // Iterate over the top-level children (root parent keys)
                for (DataSnapshot rootParentSnapshot : dataSnapshot.getChildren()) {
                    String rootParentKey = rootParentSnapshot.getKey();

                    // Iterate over the children of each root parent (sub parent keys)
                    for (DataSnapshot subParentSnapshot : rootParentSnapshot.getChildren()) {
                        String subParentKey = subParentSnapshot.getKey();

                        // Check if this is the immediate parent of the childKey
                        if (subParentSnapshot.hasChild(childKey)) {
                            // We've found the immediate parent and root parent of the childKey
                            Log.d("PARENT_KEYS_FOUND", "Root Parent Key: " + rootParentKey + ", Immediate Parent Key: " + subParentKey);
                            isChildKeyFound = true;
                            removeChildrenAndFolderinside(rootParentKey,childKey);
                            deleteFolderOnlyinsidefolder(rootParentKey,subParentKey,childKey);
                            break;
                        }
                    }

                    // If the child key has been found, break out of the loop
                    if (isChildKeyFound) {
                        break;
                    }
                }

                // If we get here, it means the child key was not found under any sub parents
                if (!isChildKeyFound) {
                    Log.d("PARENT_KEYS_NOT_FOUND", "Could not find parent keys for the child key: " + childKey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("DB_ERROR", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
    private void deleteFolderOnlyinsidefolder(final String rootparentKey, final String parentKey,final String username) {
        String folderTable = "Subfolders";

        // Delete the folder without confirmation
        DatabaseReference folderReference = FirebaseDatabase.getInstance().getReference(folderTable).child(rootparentKey).child(parentKey).child(username);

        // Delete the folder itself
        folderReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    StyleableToast.makeText(context, "Successfully Deleted Folder", Toast.LENGTH_SHORT, R.style.mytoast).show();
                } else {
                    StyleableToast.makeText(context, "Failed to delete the folder", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }
            }
        });
    }
    public static String checkSubstringfolder(String word) {
        String substring = "home"; // Specify your substring here
        String lowercaseWord = word.toLowerCase();

        if (lowercaseWord.contains(substring)) {
            Log.e("Yes", " file ");
            return "folders";
        } else {
            Log.e("No", " filesinsideFolders ");
            return "Subfolders";
        }
    }
    public void removeChildrenAndFolderinside(String parentKey, final String targetFolderId) {
        // Reference to the parent node
        String fileTable = "filesinsideFolders";
        DatabaseReference parentRef = FirebaseDatabase.getInstance().getReference(fileTable).child(parentKey);
        // Attach a listener to read the data at the parent node
        parentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the parent node exists
                if (dataSnapshot.exists()) {
                    // Iterate through the children of the parent node
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        // Access the "folder id" attribute inside each child
                        String folderId = childSnapshot.child("folderId").getValue(String.class);

                        // Check if the folder id matches the target folder id
                        if (folderId != null && folderId.equals(targetFolderId)) {
                            // Remove the child node
                            childSnapshot.getRef().removeValue();
                        }
                    }
                    // remove folder that inside folder
                    removeChildAndItsChildren(parentKey,targetFolderId);
                    // Remove the folder
                } else {
                    removeChildAndItsChildren(parentKey,targetFolderId);
                    // Remove the folder
                    // Parent node with the given key does not exist
                    //System.out.println("Parent not found for key: " + parentKey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors, if any
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }
    private void showDeleteConfirmationDialoginside(DatabaseReference rootRef,String tableName,String childKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this folder?");


        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes button
                findParentKeys(rootRef, tableName, childKey);
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





    // FileViewHolder and FolderViewHolder classes remain the same
    // ...
//    public void downLoadFile(Context context, String fileName,String fileExtension,String destinationDirectory,String url){
//
//        DownloadManager downloadManager= (DownloadManager)context.
//                getSystemService(Context.DOWNLOAD_SERVICE);
//        Uri uri=Uri.parse(url);
//        DownloadManager.Request request = new DownloadManager.Request(uri);
//
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName+ fileExtension);
//
//        downloadManager.enqueue(request);
//
//    }







// ميثود مافيه ايررور لكن مدري وين يودي الفايلات
   /* public void downLoadFile(Context context, String fileName, String destinationDirectory, String url) {
        // Start the download
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName);
        long downloadId = downloadManager.enqueue(request);

        // Define the BroadcastReceiver to handle download completion
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                long completedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (completedDownloadId == downloadId) {
                    // Get the downloaded file's input stream
                    InputStream inputStream = getDownloadedFileInputStream(context, downloadId);
                    if (inputStream != null) {
                        // Encrypt the downloaded file and save it
                        byte[] encryptedData = Crypto.encryptFile(inputStream, context, fileName);
                        if (encryptedData != null) {
                            // Save the encrypted data to a file (if needed)
                            // For example:
                            // saveEncryptedDataToFile(encryptedData, fileName);
                            // Decrypt the file and do something with it
                            byte[] decryptedData = Crypto.decryptFile(encryptedData, context, fileName);
                            if (decryptedData != null) {
                                // Do something with the decrypted data
                            }
                        }
                    }
                }
                // Unregister the BroadcastReceiver
                context.unregisterReceiver(this);
            }
        };

        // Register the BroadcastReceiver to handle download completion
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    // نفس الي تحت مكرر نسخته
    private InputStream getDownloadedFileInputStream(Context context, long downloadId) {
        // Retrieve the downloaded file's URI
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        try (Cursor cursor = downloadManager.query(query)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(columnIndex);
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    @SuppressLint("Range") String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)); // عدلت عليه من راسي  وهذا الكود قبل التعديل                     String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                    if (uriString != null) {
                        Uri uri = Uri.parse(uriString);
                        return context.getContentResolver().openInputStream(uri);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }





*/










/*

    //ميثوديالجديد
    public void downLoadFile(Context context, String fileName, String destinationDirectory, String url) {
        // Start the download
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName);
        long downloadId = downloadManager.enqueue(request);

        // Define the BroadcastReceiver to handle download completion
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                long completedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (completedDownloadId == downloadId) {
                    // Get the downloaded file's input stream
                    InputStream inputStream = getDownloadedFileInputStream(context, downloadId);
                    if (inputStream != null) {
                        // Encrypt the downloaded file and save it
                        byte[] encryptedData = Crypto.encryptFile(inputStream, context, fileName);
                        if (encryptedData != null) {
                            // Save the encrypted data to a file (if needed)
                            // For example:
                            // saveEncryptedDataToFile(encryptedData, fileName);
                        }
                    }
                }
                // Unregister the BroadcastReceiver
                context.unregisterReceiver(this);
            }
        };

        // Register the BroadcastReceiver to handle download completion
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }




    private InputStream getDownloadedFileInputStream(Context context, long downloadId) {
        // Retrieve the downloaded file's URI
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        try (Cursor cursor = downloadManager.query(query)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(columnIndex);
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (uriString != null) {
                        Uri uri = Uri.parse(uriString);
                        return context.getContentResolver().openInputStream(uri);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }





*/







    // الي يشتغل

    public void downLoadFile(Context context, String fileName, String destinationDirectory, String url) {
        // Define the BroadcastReceiver to handle download completion
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId != -1) {
                    // Decrypt the downloaded file and save it
                    String encryptedFilePath = context.getExternalFilesDir(destinationDirectory) + "/" + fileName;
                    decryptAndSaveFile(context, encryptedFilePath,fileName);
                }
            }
        };

        // Register the BroadcastReceiver
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        // Start the download
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName);
        downloadManager.enqueue(request);
    }


    private void decryptAndSaveFile(Context context, String encryptedFilePath, String decryptedFileName) {
        try {
            File encryptedFile = new File(encryptedFilePath);
            FileInputStream fis = new FileInputStream(encryptedFile);
            byte[] encryptedBytes = new byte[(int) encryptedFile.length()];
            fis.read(encryptedBytes);
            fis.close();

            // Decrypt the file
            byte[] decryptedBytes = Crypto.decryptFile(encryptedBytes, context,decryptedFileName);

            // Save the decrypted data to another file
            File decryptedFile = new File(context.getExternalFilesDir(null), decryptedFileName);
            FileOutputStream fos = new FileOutputStream(decryptedFile);
            fos.write(decryptedBytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


















    /*
    private void storeKeyInSharedPreferences(Context context, String fileName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FileKeys", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(fileName, key);
        editor.apply();
    }




    private void decryptAndSaveFile(Context context, String encryptedFilePath, String decryptedFileName) {
        try {
            File encryptedFile = new File(encryptedFilePath);
            FileInputStream fis = new FileInputStream(encryptedFile);
            byte[] encryptedBytes = new byte[(int) encryptedFile.length()];
            fis.read(encryptedBytes);
            fis.close();

            // Generate a new unique key for decryption
            String uniqueKey = Crypto.generateUniqueKey();

            // Decrypt the file using the generated unique key
            byte[] decryptedBytes = Crypto.decryptFile(encryptedBytes, uniqueKey);

            // Save the decrypted data to another file
            File decryptedFile = new File(context.getExternalFilesDir(null), decryptedFileName);
            FileOutputStream fos = new FileOutputStream(decryptedFile);
            fos.write(decryptedBytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // Method to decrypt the file using the stored key
    private byte[] decryptFileWithKey(byte[] encryptedFile, String fileName, Context context) {
        // Retrieve the key from SharedPreferences
        String storedKey = getKeyFromSharedPreferences(context, fileName);
        // Use the stored key for decryption
        return Crypto.decryptFile(encryptedFile, storedKey);
    }

    // Method to retrieve the key from SharedPreferences
    private String getKeyFromSharedPreferences(Context context, String fileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FileKeys", Context.MODE_PRIVATE);
        return sharedPreferences.getString(fileName, null);
    }



*/
















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
