package com.example.connectify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.connectify.Adapter.ChatRecyclerAdapter;
import com.example.connectify.Utils.AESHelper;
import com.example.connectify.Utils.AndroidUtil;
import com.example.connectify.Utils.FirebaseUtil;
import com.example.connectify.model.ChatMessageModel;
import com.example.connectify.model.ChatRoomModel;
import com.example.connectify.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.SecretKey;

public class DirectChatActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_VIDEO_PICK = 1002;
    UserModel otherUser;
    String chatRoomId;
    ChatRoomModel chatRoomModel;
    ChatRecyclerAdapter adapter;
    EditText messageInput;
    ImageButton backBtn, sendMessageBtn;
    TextView otherUsername, userStatus;
    RecyclerView recyclerView;
    ImageView profilePic;


    @SuppressLint({"MissingInflatedId", "SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_direct_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.i("seen", "onCreate: DirectChatActivity");
        // get User Model from Intent

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(), otherUser.getUserId());
        profilePic = findViewById(R.id.profile_pic);
        messageInput = findViewById(R.id.chat_message_input);
        backBtn = findViewById(R.id.back_arrow_icon_btn);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        otherUsername = findViewById(R.id.other_username);
        userStatus = findViewById(R.id.user_status);
        recyclerView = findViewById(R.id.direct_chat_recycle_view);

        loadOrGenerateAESKey();

        messageInput.setOnTouchListener((v, event) -> {
                    final int DRAWABLE_RIGHT = 2; // index 2 = end/right

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (messageInput.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                            int drawableWidth = messageInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();

                            if (event.getRawX() >= (messageInput.getRight() - drawableWidth - messageInput.getPaddingEnd())) {
                                // ✅ Icon tapped
                                showAttachmentOptions();
                                return true;
                            }
                        }
                    }
                    return false;
            });


        backBtn.setOnClickListener((v)->{
            onBackPressed();

        });
        otherUsername.setText(otherUser.getUsername());
        FirebaseUtil.allUserCollectionReference()
                .document(otherUser.getUserId())
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null || !snapshot.exists()) return;

                    Boolean online = snapshot.getBoolean("online");
                    Timestamp lastSeen = snapshot.getTimestamp("lastSeen");

                    if (online != null && online) {
                        userStatus.setText("Online");
                    }else{
                        assert lastSeen != null;
                        String lastSeenText = "Last seen: " + FirebaseUtil.formatTimestamp(lastSeen);
                        userStatus.setText(lastSeenText);
                    }
                });

        String profileUrl = otherUser.getUrl();
        if (profileUrl != null && !profileUrl.isEmpty()) {
            Glide.with(this)
                    .load(profileUrl)
                    .placeholder(R.drawable.man_3)
                    .transform(new CircleCrop())
                    .into(profilePic);
        }else{
            profilePic.setImageResource(R.drawable.man_3);
        }

        sendMessageBtn.setOnClickListener((v)-> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUser(message, "text");
        });

        getOrCreateChatRoomModel();



    }
    private void loadOrGenerateAESKey() {
        String senderId = FirebaseUtil.currentUserId();
        String receiverId = otherUser.getUserId();
        String keyDocId = getChatKeyDocId(senderId, receiverId);


        FirebaseFirestore.getInstance()
                .collection("chat_keys")
                .document(keyDocId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String base64Key = documentSnapshot.getString("aesKey");
                        if (base64Key != null && !base64Key.isEmpty()) {
                            SecretKey aesKey = AESHelper.decodeKeyFromBase64(base64Key);
                            AESHelper.setSharedSecretKey(aesKey);
                        }
                    } else {
                        // No key exists — generate one and store it
                        try {
                            SecretKey generatedKey = AESHelper.generateAESKey();
                            AESHelper.setSharedSecretKey(generatedKey);

                            String encodedKey = AESHelper.encodeKeyToBase64(generatedKey);
                            Map<String, Object> keyMap = new HashMap<>();
                            keyMap.put("senderId", senderId);
                            keyMap.put("receiverId", receiverId);
                            keyMap.put("aesKey", encodedKey);

                            FirebaseFirestore.getInstance()
                                    .collection("chat_keys")
                                    .document(keyDocId)
                                    .set(keyMap)
                                    .addOnSuccessListener(aVoid -> Log.d("AES", "Key stored successfully"))
                                    .addOnFailureListener(e -> Log.e("AES", "Key store failed: " + e.getMessage()));
                        } catch (Exception e) {
                            Log.e("AES", "Key generation failed: " + e.getMessage());
                        }
                    }
                });
    }

    void openCamera() {
        ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .start();
    }

    void openGallery() {
        ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .start();
    }
    void pickVideoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_VIDEO_PICK);
    }


    private void showAttachmentOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_attachment, null);
        bottomSheetDialog.setContentView(view);

        view.findViewById(R.id.option_camera).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            openCamera();
        });

        view.findViewById(R.id.option_gallery).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            openGallery();
        });
        view.findViewById(R.id.option_video).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            pickVideoFromGallery();
        });

        bottomSheetDialog.show();
    }


    void setupChatRecyclerView(){
        Log.i("seen", "inside: setupChatRecyclerView");
        Query query = FirebaseUtil.getChatRoomMessageReference(chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
        Log.i("seen", "outside: setupChatRecyclerView");
    }
    void sendMessageToUser(String message, String type){
        if (AESHelper.getSharedSecretKey() == null) {
            AndroidUtil.showToast(this, "Encryption key not loaded yet");
            return;
        }
        String encryptedMessage = "";
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        try {
            encryptedMessage = AESHelper.encrypt(message);
            chatRoomModel.setLastMessage(encryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtil.showToast(this, "Encryption failed");
            return;
        }
        chatRoomModel.setType(type);
        FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(encryptedMessage,FirebaseUtil.currentUserId(),Timestamp.now(), type);
        FirebaseUtil.getChatRoomMessageReference(chatRoomId).add(chatMessageModel)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                 messageInput.setText("");
                            }
                        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        Uri fileUri = data.getData();
        if (fileUri == null) {
            AndroidUtil.showToast(this, "File selection failed");
            return;
        }

        switch (requestCode) {
            case REQUEST_VIDEO_PICK:
                uploadToCloudinary(fileUri, "video", "video");
                break;

            case REQUEST_IMAGE_PICK:
            default:
                uploadToCloudinary(fileUri, "auto", "image"); // `auto` detects image automatically
                break;

        }
    }
    private void uploadToCloudinary(Uri fileUri, String resourceType, String messageType) {
        MediaManager.get().upload(fileUri)
                .option("resource_type", resourceType)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        AndroidUtil.showToast(getApplicationContext(), messageType + " uploading...");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String url = Objects.requireNonNull(resultData.get("secure_url")).toString();
                        sendMessageToUser(url, messageType);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        AndroidUtil.showToast(getApplicationContext(), messageType + " upload failed");
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }


    void getOrCreateChatRoomModel(){
        Log.i("seen", "inside: getOrCreateChatRoomModel");
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if(chatRoomModel==null){
                    //first time chat
                    chatRoomModel = new ChatRoomModel(
                            chatRoomId,
                            Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()),
                            Timestamp.now(),
                            "",
                            "",
                            "text"
                    );
                    FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);

                    // Generate AES Key
                    SecretKey aesKey = null;
                    try {
                        aesKey = AESHelper.generateAESKey();
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                    String base64Key = AESHelper.encodeKeyToBase64(aesKey);

                    // ✅ Store key in Firestore
                    Map<String, Object> keyMap = new HashMap<>();
                    keyMap.put("senderId", FirebaseUtil.currentUserId());
                    keyMap.put("receiverId", otherUser.getUserId());
                    keyMap.put("aesKey", base64Key);

                    // Store key globally if needed
                    AESHelper.setSharedSecretKey(aesKey);

                    FirebaseFirestore.getInstance()
                            .collection("chat_keys")
                            .document(FirebaseUtil.currentUserId() + "_" + otherUser.getUserId())
                            .set(keyMap);
                }
                //  After room is ready, now fetch key
                //  Now safe to set up adapter
                retrieveAESKey(this::setupChatRecyclerView);
            }
        });
        Log.i("seen", "outside: getOrCreateChatRoomModel");
    }
    private void retrieveAESKey(Runnable onSuccess) {
        String docId = getChatKeyDocId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        FirebaseFirestore.getInstance()
                .collection("chat_keys")
                .document(docId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String base64Key = documentSnapshot.getString("aesKey");
                        if (base64Key != null) {
                            AESHelper.setSharedSecretKey(AESHelper.decodeKeyFromBase64(base64Key));
                            onSuccess.run();
                        }
                    }
                });
    }

    public static String getChatKeyDocId(String uid1, String uid2) {
        List<String> ids = Arrays.asList(uid1, uid2);
        Collections.sort(ids);
        return ids.get(0) + "_" + ids.get(1);
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

    @Override
    protected void onDestroy() {
        Log.i("seen", "onDestroy:  DirectChatActivity");
        super.onDestroy();
    }
}