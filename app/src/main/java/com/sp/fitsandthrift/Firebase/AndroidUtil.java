package com.sp.fitsandthrift.Firebase;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.firestore.auth.User;
import com.sp.fitsandthrift.model.Usermodel;

public class AndroidUtil {
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void passUserModelAsIntent(Intent intent, Usermodel usermodel){
        intent.putExtra("userId", usermodel.getCurrentUserId());
        intent.putExtra("username", usermodel.getUsername());
        intent.putExtra("phoneNumber", usermodel.getPhoneNumber());
        intent.putExtra("profilePic", usermodel.getProfilePicUrl());
    }

    public static Usermodel getUserModelFromIntent(Intent intent){
        Usermodel usermodel = new Usermodel();
        usermodel.setCurrentUserId(intent.getStringExtra("userId"));
        usermodel.setUsername(intent.getStringExtra("username"));
        usermodel.setPhoneNumber(intent.getStringExtra("phoneNumber"));
        usermodel.setProfilePicUrl(intent.getStringExtra("profilePic"));
        return usermodel;
    }
}
