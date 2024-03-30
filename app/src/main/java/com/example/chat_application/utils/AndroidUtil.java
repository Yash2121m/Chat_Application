package com.example.chat_application.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chat_application.model.User_model;
import com.google.firebase.firestore.auth.User;

public class AndroidUtil {
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, User_model model){
        intent.putExtra("username",model.getUsername());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserid());

    }

    public static  User_model getUserModelFromIntent(Intent intent){
        User_model userModel = new User_model();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserid(intent.getStringExtra("userId"));
        return userModel;

    }

    public static void set_profile_pic(Context context, Uri image_uri, ImageView imageView){
        Glide.with(context).load(image_uri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
