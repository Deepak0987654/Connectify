package com.example.connectify;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectify.Adapter.SearchUserRecyclerAdapter;
import com.example.connectify.Utils.FirebaseUtil;
import com.example.connectify.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageView searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.i("seen", "onCreate: SearchUserActivity");
        searchInput = findViewById(R.id.search_user_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_arrow_icon_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);
        searchInput.requestFocus();

        backButton.setOnClickListener(v -> {
            Log.i("seen", "Back button clicked: SearchUserActivity");
            onBackPressed();
        });
        searchButton.setOnClickListener(v -> {
            String username = searchInput.getText().toString();
            if (username.isEmpty() || username.length()<3) {
                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecycleView(username);
        });
    }
    void setupSearchRecycleView(String username) {
        try {
            Query query = FirebaseUtil.allUserCollectionReference()
                    .whereGreaterThanOrEqualTo("username", username)
                    .whereLessThanOrEqualTo("username", username + "\uf8ff");

            FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                    .setQuery(query, UserModel.class).build();
            adapter = new SearchUserRecyclerAdapter(options, SearchUserActivity.this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        } catch (Exception e) {
            Log.e("SearchUserActivity", "Error setting up recycler view: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("seen", "onStart: SearchUserActivity");
        if (adapter != null) {
            try {
                adapter.startListening();
            } catch (Exception e) {
                Log.e("SearchUserActivity", "Error in onStart: " + e.getMessage(), e);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("seen", "onStop: SearchUserActivity");
        if (adapter != null) {
            try {
                adapter.stopListening();
            } catch (Exception e) {
                Log.e("SearchUserActivity", "Error in onStop: " + e.getMessage(), e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("seen", "onResume: SearchUserActivity");
        if (adapter != null) {
            try {
                adapter.startListening();
            } catch (Exception e) {
                Log.e("SearchUserActivity", "Error in onResume: " + e.getMessage(), e);
            }
        }
        FirebaseUtil.currentUserDetails().get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                FirebaseUtil.currentUserDetails().update("online", true);
            } else {
                // Safe fallback - create user document
                Map<String, Object> data = new HashMap<>();
                data.put("online", true);
                FirebaseUtil.currentUserDetails().set(data, SetOptions.merge());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("seen", "onPause: SearchUserActivity");
        FirebaseUtil.currentUserDetails().get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                FirebaseUtil.currentUserDetails().update("online", false,"lastSeen", com.google.firebase.Timestamp.now());
            } else {
                // Safe fallback - create user document
                Map<String, Object> data = new HashMap<>();
                data.put("online", false);
                data.put("lastSeen", com.google.firebase.Timestamp.now());
                FirebaseUtil.currentUserDetails().set(data, SetOptions.merge());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("seen", "onDestroy: SearchUserActivity");
        // Ensure adapter is cleaned up
        if (adapter != null) {
            try {
                adapter.stopListening();
                adapter = null;
            } catch (Exception e) {
                Log.e("SearchUserActivity", "Error in onDestroy: " + e.getMessage(), e);
            }
        }
    }
}