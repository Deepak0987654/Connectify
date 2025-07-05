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
import com.example.connectify.Utils.AESHelper;
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
        String decryptedMessage ="";
        try {
            decryptedMessage = AESHelper.decrypt(model.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String finalDecryptedMessage = decryptedMessage;
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);

            switch (model.getType()) {
                case "image":
                    holder.rightTextContainer.setVisibility(View.GONE);
                    holder.rightMediaContainer.setVisibility(View.VISIBLE);
                    holder.rightMediaImageView.setVisibility(View.VISIBLE);
                    holder.rightMediaIcon.setVisibility(View.GONE);

                    Glide.with(context)
                            .load(finalDecryptedMessage) // URL of image
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(holder.rightMediaImageView);

                    holder.rightMediaImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ImageViewerActivity.class);
                        intent.putExtra("imageUrl", finalDecryptedMessage);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // Use safe context
                        if (!(context instanceof android.app.Activity)) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }

                        context.startActivity(intent);
                    });

                    break;
                case "video":
                    holder.rightTextContainer.setVisibility(View.GONE);
                    holder.rightMediaContainer.setVisibility(View.VISIBLE);
                    holder.rightMediaImageView.setVisibility(View.VISIBLE);
                    holder.rightMediaIcon.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(finalDecryptedMessage)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .frame(1000000)
                            .into(holder.rightMediaImageView);

                    holder.rightMediaImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        intent.putExtra("videoUrl", finalDecryptedMessage);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });
                    break;
                case "text":
                default:
                    holder.rightMediaContainer.setVisibility(View.GONE);
                    holder.rightTextContainer.setVisibility(View.VISIBLE);
                    holder.rightChatTextView.setVisibility(View.VISIBLE);
                    holder.rightChatTextView.setText(finalDecryptedMessage);
                    break;
            }
        }
        else{
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatLayout.setVisibility(View.GONE);
            switch (model.getType()) {
                case "image":
                    holder.leftTextContainer.setVisibility(View.GONE);
                    holder.leftMediaContainer.setVisibility(View.VISIBLE);
                    holder.leftMediaImageView.setVisibility(View.VISIBLE);
                    holder.leftMediaIcon.setVisibility(View.GONE);

                    Glide.with(context)
                            .load(finalDecryptedMessage) // URL of image
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(holder.leftMediaImageView);

                    holder.leftMediaImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ImageViewerActivity.class);
                        intent.putExtra("imageUrl", finalDecryptedMessage);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });

                    break;
                case "video":
                    holder.leftTextContainer.setVisibility(View.GONE);
                    holder.leftMediaContainer.setVisibility(View.VISIBLE);
                    holder.leftMediaImageView.setVisibility(View.VISIBLE);
                    holder.leftMediaIcon.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(finalDecryptedMessage)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .frame(1000000)
                            .into(holder.leftMediaImageView);

                    holder.leftMediaImageView.setOnClickListener(v -> {
                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        intent.putExtra("videoUrl", finalDecryptedMessage);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    });
                    break;
                case "text":
                default:
                    holder.leftMediaContainer.setVisibility(View.GONE);
                    holder.leftTextContainer.setVisibility(View.VISIBLE);
                    holder.leftChatTextView.setVisibility(View.VISIBLE);
                    holder.leftChatTextView.setText(finalDecryptedMessage);
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
        LinearLayout leftTextContainer, rightTextContainer;
        TextView leftChatTextView, rightChatTextView;
        FrameLayout leftMediaContainer, rightMediaContainer;
        ImageView leftMediaImageView, rightMediaImageView;
        ImageView leftMediaIcon, rightMediaIcon;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
           leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
           rightChatLayout = itemView.findViewById(R.id.right_chat_layout);

           leftTextContainer = itemView.findViewById(R.id.left_text_container);
           rightTextContainer = itemView.findViewById(R.id.right_text_container);

           leftChatTextView = itemView.findViewById(R.id.left_text_textview);
           rightChatTextView = itemView.findViewById(R.id.right_text_textview);

           leftMediaContainer = itemView.findViewById(R.id.left_media_container);
           rightMediaContainer = itemView.findViewById(R.id.right_media_container);

           leftMediaImageView = itemView.findViewById(R.id.left_media_imageview);
           rightMediaImageView = itemView.findViewById(R.id.right_media_imageview);

           leftMediaIcon = itemView.findViewById(R.id.left_media_icon);
           rightMediaIcon = itemView.findViewById(R.id.right_media_icon);


        }
    }
}