package com.example.myapplication324;

import android.content.Context;
import android.content.Intent;

public class FolderUtils {

    public static void openFolder(Context context, String folderId) {
        Intent folderIntent = new Intent(context, FolderActivity.class);
        folderIntent.putExtra("folderId", folderId);
        context.startActivity(folderIntent);
    }
}