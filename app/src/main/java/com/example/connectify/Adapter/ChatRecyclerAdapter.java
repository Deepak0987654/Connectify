package com.example.connectify.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connectify.ImageViewerActivity;
import com.example.connectify.R;
import com.example.connectify.Utils.FirebaseUtil;
import com.example.connectify.VideoPlayerActivity;
import com.example.connectify.model.ChatMessageModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatViewHolder> {
    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull ChatMessageModel model) {
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            switch (model.getType()) {
                case "image":
                    holder.rightChatTextView.setVisibility(View.GONE);
                    holder.rightChatImageView.setVisibility(View.VISIBLE);
                    holder.rightMediaContainer.setVisibility(View.VISIBLE);

                    Glide.with(holder.itemView.getContext())
                            .load(model.getMessage()) // URL of image
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(holder.rightChatImageView);

                    holder.rightChatImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ImageViewerActivity.class);
                        intent.putExtra("imageUrl", model.getMessage());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });

                    break;
                case "video":
                    holder.rightChatTextView.setVisibility(View.GONE);
                    holder.rightChatImageView.setVisibility(View.VISIBLE);
                    holder.rightMediaContainer.setVisibility(View.VISIBLE);
                    holder.rightPlayIcon.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(model.getMessage())
                            .frame(1000000)
                            .into(holder.rightChatImageView);

                    holder.rightChatImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        intent.putExtra("videoUrl", model.getMessage());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });
                    break;
                case "text":
                    holder.rightChatImageView.setVisibility(View.GONE);
                    holder.rightChatTextView.setVisibility(View.VISIBLE);
                    holder.rightChatTextView.setText(model.getMessage());
                    break;
            }
        }
        else{
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatLayout.setVisibility(View.GONE);
            switch (model.getType()) {
                case "image":
                    holder.leftChatTextView.setVisibility(View.GONE);
                    holder.leftChatImageView.setVisibility(View.VISIBLE);
                    holder.rightMediaContainer.setVisibility(View.VISIBLE);

                    Glide.with(holder.itemView.getContext())
                            .load(model.getMessage()) // URL of image
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(holder.leftChatImageView);

                    holder.rightChatImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ImageViewerActivity.class);
                        intent.putExtra("imageUrl", model.getMessage());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });
                    break;
                case "video":
                    holder.leftChatTextView.setVisibility(View.GONE);
                    holder.leftChatImageView.setVisibility(View.VISIBLE);
                    holder.rightMediaContainer.setVisibility(View.VISIBLE);
                    holder.leftPlayIcon.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(model.getMessage())
                            .frame(1000000)
                            .placeholder(R.drawable.ic_video_placeholder_24)
                            .into(holder.leftChatImageView);

                    holder.rightChatImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        intent.putExtra("videoUrl", model.getMessage());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });
                    break;
                case "text":
                    holder.leftChatImageView.setVisibility(View.GONE);
                    holder.leftChatTextView.setVisibility(View.VISIBLE);
                    holder.leftChatTextView.setText(model.getMessage());
                    break;
            }
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatViewHolder(view);
    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextView, rightChatTextView;
        ImageView leftChatImageView, rightChatImageView;
        ImageView leftPlayIcon, rightPlayIcon;
        FrameLayout leftMediaContainer, rightMediaContainer;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
           leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
           rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
           leftChatTextView = itemView.findViewById(R.id.left_chat_textview);
           rightChatTextView = itemView.findViewById(R.id.right_chat_textview);
           leftChatImageView = itemView.findViewById(R.id.left_chat_imageview);
           rightChatImageView = itemView.findViewById(R.id.right_chat_imageview);
           leftPlayIcon = itemView.findViewById(R.id.left_play_icon);
           rightPlayIcon = itemView.findViewById(R.id.right_play_icon);
           leftMediaContainer = itemView.findViewById(R.id.left_media_container);
           rightMediaContainer = itemView.findViewById(R.id.right_media_container);
        }
    }
}