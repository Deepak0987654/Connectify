package com.example.connectify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.connectify.Utils.AndroidUtil;
import com.example.connectify.Utils.FirebaseUtil;
import com.example.connectify.model.UserModel;

public class SplashScreen extends AppCompatActivity {
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.i("seen", "onCreate: Splash Screen");
        progressBar = findViewById(R.id.progress_bar);
        // Simulate progress bar animation
        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 2;
                handler.post(() -> progressBar.setProgress(progressStatus));
                try {
                    Thread.sleep(30); // Adjust speed of progress
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Transition to MainActivity after progress completes
            if(FirebaseUtil.isLoggedIn()){
                Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else{
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            }
            finish();
        }).start();
    }
    @Override
    protected void onDestroy() {
        Log.i("seen", "onDestroy: Splash screen");
        super.onDestroy();
    }
}