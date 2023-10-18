package com.example.myapplication324;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "GP.db";
    private static String usersTable="usersTable";
    private static String username="username";
    private static String password="password";
    private static String email="email";
    private static String phone="phone";
    private static String fileID="fileID";
    private static String owner="owner";
    private static String viewer="viewer";
    private static String filesTable="filesTable";

    public DBHelper(Context context) {
        super(context, "GP.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create table " + usersTable + "(" +
                username + " text primary key, " +
                password + " varchar, " +
                email + " varchar, " +
                phone + " integer, " +
                fileID + " integer references " + filesTable + "(" + fileID + ")" + ")");

        MyDB.execSQL("create table " + filesTable + "(" +
                fileID + " integer primary key, " +
                owner + " varchar, " +
                viewer + " varchar " +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists " + usersTable);
        onCreate(MyDB);

    }


    public Boolean addUser(String username, String password , String email , String phone){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("email", email);
        contentValues.put("phone", phone);
        long result = MyDB.insert("usersTable", null, contentValues);
        if(result==-1) return false;
        else
            return true;
    }

    public Boolean addFile(int fileID, String owner , String viewer){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("fileID", fileID);
        contentValues.put("owner", owner);
        contentValues.put("viewer", viewer);
        long result = MyDB.insert("usersTable", null, contentValues);
        if(result==-1) return false;
        else
            return true;
    }


    public Boolean checkusername(String email) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        String sql="select * from " + usersTable + " where email =?";
       // Cursor cursor = MyDB.rawQuery("Select * from usersTable where username = ?", new String [] {username});
        Cursor cursor = MyDB.rawQuery(sql, new String[] {email});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public Boolean checkusernamepassword(String username, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        String sql="Select * from " + usersTable + " where username =? and password =? ";

        Cursor cursor = MyDB.rawQuery(sql, new String[] {username,password});
        if(cursor.getCount()>0)
            return true;
        else
            return false;
    }
    public Boolean checkPhoneNumber(String phoneNumber) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        String sql = "SELECT * FROM " + usersTable + " WHERE phone = ?";
        Cursor cursor = MyDB.rawQuery(sql, new String[] { phoneNumber });
        if (cursor.getCount() > 0) {
            return true; // Phone number already exists
        } else {
            return false; // Phone number does not exist
        }
    }
}