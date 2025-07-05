package com.example.connectify.Utils;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FirebaseUtil {
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }
    public static boolean isLoggedIn(){
        return currentUserId() != null;
    }
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }
    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatRoomReference(String chatRoomId){
        return FirebaseFirestore.getInstance().collection("chatRooms").document(chatRoomId);
    }
    public static CollectionReference getChatRoomMessageReference(String chatRoomId){
        return getChatRoomReference(chatRoomId).collection("chats");
    }
    public static String getChatRoomId(String userId1, String userId2){
        if(userId1.hashCode() < userId2.hashCode()){
            return userId1 + "_" + userId2;
        }else{
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference allChatRoomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatRooms");
    }
    public static DocumentReference getOtherUserFromChatRoom(List<String> userIds){
        if(userIds.get(0).equals(currentUserId()))
            return allUserCollectionReference().document(userIds.get(1));
        else
            return allUserCollectionReference().document(userIds.get(0));

    }

    @SuppressLint("SimpleDateFormat")
    public static String formatTimestamp(Timestamp timestamp) {
        Date messageDate = timestamp.toDate();
        Calendar messageCal = Calendar.getInstance();
        messageCal.setTime(messageDate);

        Calendar now = Calendar.getInstance();

        // Today: show time like "12:34 PM"
        if (isSameDay(messageCal, now)) {
            return new SimpleDateFormat("hh:mm a").format(messageDate);
        }

        // Yesterday
        now.add(Calendar.DAY_OF_YEAR, -1);
        if (isSameDay(messageCal, now)) {
            return "Yesterday";
        }

        // Within last 7 days: show day like "Monday"
        now = Calendar.getInstance(); // reset
        int daysDiff = daysBetween(messageCal, now);
        if (daysDiff < 7) {
            return new SimpleDateFormat("EEEE").format(messageDate); // e.g., Monday
        }

        // Same year: "13 Jun"
        if (messageCal.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            return new SimpleDateFormat("dd MMM").format(messageDate);
        }

        // Older (different year): "13 Jun 2023"
        return new SimpleDateFormat("dd MMM yyyy").format(messageDate);
    }
    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private static int daysBetween(Calendar from, Calendar to) {
        Calendar start = (Calendar) from.clone();
        Calendar end = (Calendar) to.clone();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        long diffMillis = end.getTimeInMillis() - start.getTimeInMillis();
        return (int) TimeUnit.MILLISECONDS.toDays(diffMillis);
    }



    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static DocumentReference getChatKeyReference(String docId) {
        return FirebaseFirestore.getInstance().collection("chat_keys").document(docId);
    }



}
