package com.example.connectify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.connectify.R;
import com.example.connectify.Utils.FirebaseUtil;
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
            if (model.isImage()) {
                holder.rightChatTextView.setVisibility(View.GONE);
                holder.rightChatImageView.setVisibility(View.VISIBLE);

                Glide.with(holder.itemView.getContext())
                        .load(model.getMessage()) // URL of image
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.rightChatImageView);
            } else {
                holder.rightChatImageView.setVisibility(View.GONE);
                holder.rightChatTextView.setVisibility(View.VISIBLE);
                holder.rightChatTextView.setText(model.getMessage());
            }
            holder.rightChatTextView.setText(model.getMessage());
        }else{
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatLayout.setVisibility(View.GONE);
            if (model.isImage()) {
                holder.leftChatTextView.setVisibility(View.GONE);
                holder.leftChatImageView.setVisibility(View.VISIBLE);

                Glide.with(holder.itemView.getContext())
                        .load(model.getMessage()) // URL of image
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(holder.leftChatImageView);
            } else {
                holder.leftChatImageView.setVisibility(View.GONE);
                holder.leftChatTextView.setVisibility(View.VISIBLE);
                holder.leftChatTextView.setText(model.getMessage());
            }
            holder.leftChatTextView.setText(model.getMessage());
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
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
           leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
           rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
           leftChatTextView = itemView.findViewById(R.id.left_chat_textview);
           rightChatTextView = itemView.findViewById(R.id.right_chat_textview);
           leftChatImageView = itemView.findViewById(R.id.left_chat_imageview);
           rightChatImageView = itemView.findViewById(R.id.right_chat_imageview);
        }
    }
}