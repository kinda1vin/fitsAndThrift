package com.sp.fitsandthrift.Firebase;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.List;



public class Util {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static StorageReference  getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(Util.currentUserId());
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }
     public static DocumentReference getChatroomReference(String chatroomId) {
         if (chatroomId == null) {
             // The chat room ID is null
             // Handle this case as appropriate for your applica    tion
             return null;
         }
         return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
     }

     public static CollectionReference getChatMessagesReference(String chatroomId) {
         return getChatroomReference(chatroomId).collection("chats");
     }
     public static String getChatroomId(String userId1, String userId2) {

         if (userId1.hashCode() < userId2.hashCode()) {
             return userId1 + "_" + userId2;
         } else {
             return userId2 + "_" + userId1;
         }
     }

     public static CollectionReference allChatroomCollectionReference(){
         return FirebaseFirestore.getInstance().collection("chatrooms");
     }

     public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
         if(userIds.get(0).equals(Util.currentUserId()))
             return allUserCollectionReference().document(userIds.get(1));
         else
             return allUserCollectionReference().document(userIds.get(0));
     }

     public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
     }
}

