package com.example.connectify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.connectify.Adapter.ViewPagerMessageAdapter;
import com.example.connectify.Utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeScreen extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerMessageAdapter viewPagerMessageAdapter;
    ImageView search;
    ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        Log.i("seen", "onCreate: Home screen");

        try {
            tabLayout = findViewById(R.id.tab_layout);
            viewPager = findViewById(R.id.view_pager);
            viewPagerMessageAdapter = new ViewPagerMessageAdapter(getSupportFragmentManager());
            viewPager.setAdapter(viewPagerMessageAdapter);
            tabLayout.setupWithViewPager(viewPager);
            menuIcon = findViewById(R.id.dots_icon);
            search = findViewById(R.id.search_icon);
            search.setOnClickListener(v -> {
                startActivity(new Intent(HomeScreen.this, SearchUserActivity.class));
            });
            // Set up PopupMenu for dot icon
            menuIcon.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(HomeScreen.this, menuIcon);
                popup.getMenu().add(0, 1, 0, "Profile");
                popup.getMenu().add(0, 2, 1, "Logout");
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case 1:
                            // Navigate to ProfileActivity
                            startActivity(new Intent(HomeScreen.this, ProfileActivity.class));
                            return true;
                        case 2:
                            // Show confirmation dialog for logout
                            new AlertDialog.Builder(HomeScreen.this)
                                    .setTitle("Logout")
                                    .setMessage("Are you sure you want to logout?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        FirebaseMessaging.getInstance().deleteToken()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Sign out and navigate to SignInActivity
                                                            FirebaseUtil.logout();
                                                            Intent intent = new Intent(HomeScreen.this, SignUp.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                            return true;
                        default:
                            return false;
                    }
                });
                popup.show();
            });
        } catch (Exception e) {
            Log.e("HomeScreen", "Error in onCreate: " + e.getMessage(), e);
        }


    }

    @Override
    public void onBackPressed() {
        try {
            // Check if on Chat fragment (position 0)
            if (viewPager != null && viewPagerMessageAdapter != null && viewPager.getCurrentItem() == 0) {
                // On Chat fragment, show exit confirmation dialog
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> super.onBackPressed())
                        .setNegativeButton("No", null)
                        .show();
                Log.i("seen", "onBackPressed: HomeScreen (Chat fragment, showing exit dialog)");
            } else if (viewPager != null) {
                // On other fragments, navigate to Chat fragment (position 0)
                viewPager.setCurrentItem(0);
                Log.i("seen", "onBackPressed: HomeScreen (Navigated to Chat fragment)");
            } else {
                // Fallback: show exit dialog if ViewPager is null
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> super.onBackPressed())
                        .setNegativeButton("No", null)
                        .show();
                Log.i("seen", "onBackPressed: HomeScreen (Fallback, showing exit dialog)");
            }
        } catch (Exception e) {
            Log.e("HomeScreen", "Error in onBackPressed: " + e.getMessage(), e);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("seen", "onStart: HomeScreen");
        try {
            if (viewPagerMessageAdapter != null) {
                viewPagerMessageAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e("HomeScreen", "Error in onStart: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("seen", "onResume: HomeScreen");
        try {
            if (viewPager != null && viewPagerMessageAdapter != null) {
                viewPager.setAdapter(viewPagerMessageAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        } catch (Exception e) {
            Log.e("HomeScreen", "Error in onResume: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("seen", "onPause: HomeScreen");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("seen", "onStop: HomeScreen");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("seen", "onDestroy: HomeScreen");
        try {
            if (viewPager != null) {
                viewPager.setAdapter(null);
            }
            viewPagerMessageAdapter = null;
        } catch (Exception e) {
            Log.e("HomeScreen", "Error in onDestroy: " + e.getMessage(), e);
        }
    }
}