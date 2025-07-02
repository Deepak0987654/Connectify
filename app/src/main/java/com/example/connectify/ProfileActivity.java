package com.example.connectify;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.connectify.Utils.AndroidUtil;
import com.example.connectify.Utils.FirebaseUtil;
import com.example.connectify.model.UserModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    ImageView profilePic;
    ImageButton backButton;
    EditText usernameInput, phoneInput;
    Button updateButton;
    UserModel currentUserModel;
    ProgressBar progressBar;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri imageUri;
    private static final String TAG = "updateIssue";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        profilePic = findViewById(R.id.profile_pic);
        usernameInput = findViewById(R.id.profile_username);
        phoneInput = findViewById(R.id.profile_phone);
        updateButton = findViewById(R.id.profile_update_button);
        backButton = findViewById(R.id.back_arrow_icon_btn);
        progressBar = findViewById(R.id.profile_progress_bar);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
        getUserData();
        updateButton.setOnClickListener(v -> {
            updateBtnClick();
        });
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                if(data != null && data.getData() != null){
                    imageUri = data.getData();
                    Glide.with(this)
                            .load(imageUri)
                            .transform(new CircleCrop())  // applies circular transformation
                            .into(profilePic);
                }
            }
        });
        profilePic.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(intent -> {
                        imagePickLauncher.launch(intent);
                        return null;
                    });
        });

    }
    void updateBtnClick(){
        String newUsername = usernameInput.getText().toString();
        if(newUsername.isEmpty() || newUsername.length() < 3){
            usernameInput.setError("Username length should be at least 3 characters");
            return;
        }
        currentUserModel.setUsername(newUsername);
        setInProgress(true);
        uploadImageToCloudinary(imageUri);
    }
    void updateToFirestore(){
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if(task.isSuccessful()){
                        AndroidUtil.showToast(profilePic.getContext(),"Profile updated successfully");
                    }else{
                        AndroidUtil.showToast(profilePic.getContext(),"Failed to update profile");
                    }
                });
    }

    void getUserData(){
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            assert currentUserModel != null;
            usernameInput.setText(currentUserModel.getUsername());
            phoneInput.setText(currentUserModel.getPhone());
            if (currentUserModel.getUrl() != null && !currentUserModel.getUrl().isEmpty()) {
                Glide.with(ProfileActivity.this)
                        .load(currentUserModel.getUrl())
                        .placeholder(R.drawable.man_3)
                        .transform(new CircleCrop())
                        .into(profilePic);
            }

        });
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            updateButton.setVisibility(View.VISIBLE);
        }
    }
    private void uploadImageToCloudinary(Uri uri) {
        if (uri == null) {
            Log.d(TAG, "No image selected");
            updateToFirestore();
            return;
        }

        MediaManager.get().upload(uri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Upload started");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String url = Objects.requireNonNull(resultData.get("secure_url")).toString();
                        Log.d("Cloudinary", "Uploaded URL: " + url);

                        currentUserModel.setUrl(url);
                        updateToFirestore();
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.d(TAG,"Upload failed: " + error.getDescription());
                        updateToFirestore();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }
    @Override
    protected void onResume() {
        super.onResume();
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

}