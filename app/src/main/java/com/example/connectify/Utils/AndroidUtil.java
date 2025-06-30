package com.example.connectify.Utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.connectify.model.UserModel;

public class AndroidUtil {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void passUserModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra("username", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("url", model.getUrl());
    }
    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel model = new UserModel();
        model.setUsername(intent.getStringExtra("username"));
        model.setPhone(intent.getStringExtra("phone"));
        model.setUserId(intent.getStringExtra("userId"));
        model.setUrl(intent.getStringExtra("url"));
        return model;
    }


}
