package com.example.connectify.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatRoomModel {
    private String chatroomId;
    private List<String> userIds;
    private Timestamp lastMessageTimestamp;
    private String lastMessageSenderId;
    private String lastMessage;
    private String type="text";

    public ChatRoomModel() {
    }

    public ChatRoomModel(String chatroomId, List<String> userIds, Timestamp lastMessageTimestamp, String lastMessageSenderId, String lastMessage, String type) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessage = lastMessage;
        this.type = type;

    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
