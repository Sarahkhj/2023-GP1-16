package com.example.myapplication324;

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;


public class UserHelperClass {
    String username, email, PhoneNum;
    private static String usersTable="usersTable";

    public UserHelperClass (Sign_up signUp) {

    }
    public UserHelperClass(String username, String email,String phone) {
        this.username = username;
        this.email = email;
        this.PhoneNum = phone;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }

    /*public Boolean checkPhoneNumber(String phoneNumber) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        String sql = "SELECT * FROM " + usersTable + " WHERE phone = ?";
        Cursor cursor = MyDB.rawQuery(sql, new String[] { phoneNumber });
        if (cursor.getCount() > 0) {
            return true; // Phone number already exists
        } else {
            return false; // Phone number does not exist
        }
    }

    public Boolean checkEmail(String email) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        String sql = "SELECT * FROM " + usersTable + " WHERE email = ?";
        Cursor cursor = MyDB.rawQuery(sql, new String[] { email });
        if (cursor.getCount() > 0) {
            return true; // Email already exists
        } else {
            return false; // Email does not exist
        }
    }*/
}

