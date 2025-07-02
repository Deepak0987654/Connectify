package com.example.connectify.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.connectify.DirectChatActivity;
import com.example.connectify.R;
import com.example.connectify.Utils.AndroidUtil;
import com.example.connectify.Utils.FirebaseUtil;
import com.example.connectify.model.ChatRoomModel;
import com.example.connectify.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {
    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
        Log.e("seen", "RecentChatRecyclerAdapter: inside");
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        Log.e("seen", "RecentChatRecyclerAdapter: on bind");
        FirebaseUtil.getOtherUserFromChatRoom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                        assert otherUserModel != null;
                        holder.usernameText.setText(otherUserModel.getUsername());

                        String profileUrl = otherUserModel.getUrl();
                        if (profileUrl != null && !profileUrl.isEmpty()) {
                            Glide.with(holder.itemView.getContext())
                                    .load(profileUrl)
                                    .placeholder(R.drawable.man_3)
                                    .transform(new CircleCrop())
                                    .into(holder.profilePic);
                        } else {
                            holder.profilePic.setImageResource(R.drawable.man_3);
                        }
                        String type = model.getType().replace("\"", "").trim();
                        Log.e("see ", "model: "+type);


                        switch (type) {
                            case "image":
                                Log.e("seen", "onBindViewHolder: image" );
                                holder.lastMessageImage.setImageResource(R.drawable.image);
                                holder.lastMessageText.setText("photo");
                                if (lastMessageSentByMe)
                                    holder.imageText.setText("You : ");

                                break;
                            case "video":
                                Log.e("seen", "onBindViewHolder: video" );
                                holder.lastMessageImage.setImageResource(R.drawable.image);
                                holder.lastMessageText.setText("Video");
                                if (lastMessageSentByMe)
                                    holder.imageText.setText("You : ");
                                break;
                            case "text":
                                Log.e("seen", "onBindViewHolder: text" );
                                holder.lastMessageImage.setVisibility(View.GONE);
                                holder.imageText.setVisibility(View.GONE);
                                if (lastMessageSentByMe)
                                    holder.lastMessageText.setText("You : " + model.getLastMessage());
                                else
                                    holder.lastMessageText.setText(model.getLastMessage());
                                break;
                        }

                        holder.lastMessageTime.setText(FirebaseUtil.formatTimestamp(model.getLastMessageTimestamp()));
                        holder.itemView.setOnClickListener(v -> {
                            // navigate to chat activity
                            Intent intent = new Intent(context, DirectChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            context.startActivity(intent);

                        });
                    }

                });
                    Log.e("seen", "RecentChatRecyclerAdapter: outside + "+ model.getLastMessage());
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatRoomModelViewHolder(view);
    }


    public static class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, lastMessageText, lastMessageTime, imageText;
        ImageView profilePic, lastMessageImage;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_txt);
            lastMessageText = itemView.findViewById(R.id.last_message_txt);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_txt);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            lastMessageImage = itemView.findViewById(R.id.last_message_image);
            imageText = itemView.findViewById(R.id.image_description_txt);
        }
    }
}