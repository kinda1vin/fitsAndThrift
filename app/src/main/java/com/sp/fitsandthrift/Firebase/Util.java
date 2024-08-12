package com.sp.fitsandthrift.Firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sp.fitsandthrift.model.Usermodel;

import java.text.SimpleDateFormat;
import java.util.List;

public class Util {

    // Retrieve current user's ID
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    // Retrieve current user's Firestore document reference
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    // Asynchronously fetch current user data
    public static Task<DocumentSnapshot> getCurrentUserData() {
        return currentUserDetails().get();
    }

    // Retrieve current user's profile picture URL
    public static String currentUserProfilePicUrl() {
        try {
            Task<DocumentSnapshot> task = getCurrentUserData();
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    Usermodel usermodel = snapshot.toObject(Usermodel.class);
                    if (usermodel != null) {
                        return usermodel.getProfilePicUrl();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ""; // Return an empty string if no URL is found
    }

    // Get Storage Reference for current user's profile picture
    public static StorageReference getCurrentProfilePicStorageRef() {
        return FirebaseStorage.getInstance().getReference()
                .child("profile_pic")
                .child(currentUserId());
    }

    // Get reference to all users collection
    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    // Get chatroom document reference
    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    // Get collection reference for chat messages
    public static CollectionReference getChatMessagesReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    // Generate chatroom ID based on user IDs
    // Generate chatroom ID based on user IDs
    public static String getChatroomId(String userId1, String userId2) {
        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException("User IDs must not be null");
        }

        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }


    // Get reference to all chatrooms collection
    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    // Retrieve the other user's document reference from a chatroom
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("User IDs list cannot be null or empty");
        }
        if (userIds.size() != 2) {
            throw new IllegalArgumentException("User IDs list must contain exactly two user IDs");
        }

        String otherUserId = userIds.get(0).equals(currentUserId()) ? userIds.get(1) : userIds.get(0);
        return allUserCollectionReference().document(otherUserId);
    }

    // Convert Timestamp to string format (e.g., HH:mm)
    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }
}