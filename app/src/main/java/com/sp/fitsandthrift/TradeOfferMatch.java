package com.sp.fitsandthrift;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TradeOfferMatch extends AppCompatActivity {

    private RecyclerView selectedItemsRecyclerView;
    private RecyclerView desiredItemsRecyclerView;
    private SelectedItemsAdapter selectedItemsAdapter;
    private DesiredItemsAdapter desiredItemsAdapter;
    private FirebaseFirestore db;
    private ArrayList<Item> selectedItems = new ArrayList<>();
    private ArrayList<Item> desiredItems = new ArrayList<>();
    private static final String PREFS_NAME = "trade_prefs";
    private static final String ITEM_ID_KEY = "item_id";
    private static final String SERVER_KEY = "YOUR_SERVER_KEY"; // Replace with your FCM server key
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trade_offer_match);

        selectedItemsRecyclerView = findViewById(R.id.selected_items_recycler_view);
        desiredItemsRecyclerView = findViewById(R.id.desired_items_recycler_view);
        Button confirmButton = findViewById(R.id.comfirmtotrade);
        db = FirebaseFirestore.getInstance();

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }

        ArrayList<String> selectedItemIds = getIntent().getStringArrayListExtra("selectedItems");
        String itemID = getItemIDFromPreferences();

        if (itemID != null) {
            fetchDesiredItem(itemID);
        }

        fetchSelectedItems(selectedItemIds);

        confirmButton.setOnClickListener(v -> {
            if (!desiredItems.isEmpty()) {
                Item desiredItem = desiredItems.get(0);  // Assuming there's only one desired item
                sendNotificationToOwner(desiredItem.getUserID(), selectedItemIds, desiredItem.getItemID());
                Toast.makeText(TradeOfferMatch.this, "Notification sent to owner", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TradeOfferMatch.this, "No desired item selected", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private String getItemIDFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(ITEM_ID_KEY, null);
    }

    private void fetchSelectedItems(ArrayList<String> selectedItemIds) {
        db.collection("items").whereIn("itemID", selectedItemIds).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Item item = document.toObject(Item.class);
                    selectedItems.add(item);
                }
                setupselectedItemsRecyclerView();
            }
        });
    }

    private void fetchDesiredItem(String itemID) {
        db.collection("items").whereEqualTo("itemID", itemID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Item item = document.toObject(Item.class);
                    desiredItems.add(item);
                }
                setupdesiredItemsRecyclerView();
            }
        });
    }

    private void setupselectedItemsRecyclerView() {
        selectedItemsRecyclerView.setHasFixedSize(true);
        selectedItemsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        selectedItemsAdapter = new SelectedItemsAdapter(this, selectedItems);
        selectedItemsRecyclerView.setAdapter(selectedItemsAdapter);
    }

    private void setupdesiredItemsRecyclerView() {
        desiredItemsRecyclerView.setHasFixedSize(true);
        desiredItemsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        desiredItemsAdapter = new DesiredItemsAdapter(this, desiredItems);
        desiredItemsRecyclerView.setAdapter(desiredItemsAdapter);
    }

    private void sendNotificationToOwner(String ownerId, ArrayList<String> selectedItemIds, String desiredItemId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch current user's details
        db.collection("users").document(currentUserId).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful() && userTask.getResult() != null) {
                String senderUsername = userTask.getResult().getString("username");
                String senderProfilePicUrl = userTask.getResult().getString("profilePicUrl");
                String message = senderUsername + " has sent you a trade request";

                // Fetch the owner's FCM token and create the notification
                db.collection("users").document(ownerId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult().getString("fcmToken");
                        if (token != null) {
                            sendFCMNotification(token);
                        } else {
                            Log.e("TradeOfferMatch", "FCM token is null for owner: " + ownerId);
                        }

                        // Create a notification entry in the Firestore database
                        Map<String, Object> notification = new HashMap<>();
                        notification.put("senderUsername", senderUsername);
                        notification.put("message", message);
                        notification.put("senderProfilePicUrl", senderProfilePicUrl);
                        notification.put("ownerId", ownerId);
                        notification.put("selectedItems", selectedItemIds);
                        notification.put("desiredItemId", desiredItemId);

                        db.collection("users").document(ownerId).collection("notifications")
                                .add(notification)
                                .addOnSuccessListener(documentReference -> Log.d("TradeOfferMatch", "Notification stored successfully"))
                                .addOnFailureListener(e -> Log.e("TradeOfferMatch", "Error storing notification", e));
                    } else {
                        Log.e("TradeOfferMatch", "Failed to fetch FCM token for owner: " + ownerId, task.getException());
                    }
                });
            } else {
                Log.e("TradeOfferMatch", "Failed to fetch sender details", userTask.getException());
            }
        });
    }


    private void sendFCMNotification(String token) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("title", "Trade Offer");
            notification.put("body", "You have a new trade offer!");
            json.put("notification", notification);
        } catch (Exception e) {
            Log.e("TradeOfferMatch", "Failed to create JSON payload", e);
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("Authorization", "key=" + SERVER_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.e("TradeOfferMatch", "Failed to send notification", e);
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                Log.d("TradeOfferMatch", "Notification sent: " + response.body().string());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
