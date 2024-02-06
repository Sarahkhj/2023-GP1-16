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

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> itemsList;
    private Context context; // Add a reference to the context

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
            ((FileViewHolder) holder).buttonDownLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downLoadFile( ((FileViewHolder) holder).fileNameTextView.getContext(), fileMetadata.getFileName(), ".pdf",DIRECTORY_DOWNLOADS, fileMetadata.getFileDownloadUrl());
                }
            });

        } else if (holder instanceof FolderViewHolder && item instanceof FolderMetadata) {
            FolderMetadata folderMetadata = (FolderMetadata) item;
            ((FolderViewHolder) holder).folderNameTextView.setText(folderMetadata.getFolderName());

            // Pass the context and folderId to FolderViewHolder
            ((FolderViewHolder) holder).setContextAndFolderId(context, folderMetadata.getFolderId());
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


        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTextView); // Replace with your file item view
            fileLinkTextView = itemView.findViewById(R.id.fileLinkTextView);
            buttonDownLoad = itemView.findViewById(R.id.buttonDownLoad);


        }
    }



    static class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderNameTextView;

        private Context context;
        private String folderId;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderNameTextView = itemView.findViewById(R.id.folderNameTextView); // Replace with your folder item view
            // Add click listener for the folder item
            itemView.setOnClickListener(v -> FolderUtils.openFolder(context, folderId));
        }

        // Method to set the context and folderId
        public void setContextAndFolderId(Context context, String folderId) {
            this.context = context;
            this.folderId = folderId;
        }
    }
}
