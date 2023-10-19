package com.example.myapplication324;

public class UserHelperClass {
    String username, email, PhoneNum;

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


}

