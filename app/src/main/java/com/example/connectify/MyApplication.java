package com.example.connectify;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "*****");  // cloud name
        config.put("api_key", "***********"); // api key
        config.put("api_secret", "*******************"); // api secret

        MediaManager.init(this, config);
    }
}
