package com.sp.fitsandthrift.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class Util {
    private static final String USER_COLLECTION = "users"; // Replace with your actual collection name

    public static DocumentReference currentUserDetails() {
        String userID = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userID == null || userID.isEmpty()) {
            throw new IllegalArgumentException("User ID must not be null or empty");
        }
        return FirebaseFirestore.getInstance().collection(USER_COLLECTION).document(userID);
    }


}
