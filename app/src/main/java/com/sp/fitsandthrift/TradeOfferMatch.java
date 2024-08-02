package com.sp.fitsandthrift;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sp.fitsandthrift.model.Notification;

import java.util.ArrayList;

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
                sendNotificationToOwner(desiredItem.getUserID());
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
                setupSelectedItemsRecyclerView();
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
                setupDesiredItemsRecyclerView();
            }
        });
    }

    private void setupSelectedItemsRecyclerView() {
        selectedItemsRecyclerView.setHasFixedSize(true);
        selectedItemsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        selectedItemsAdapter = new SelectedItemsAdapter(this, selectedItems);
        selectedItemsRecyclerView.setAdapter(selectedItemsAdapter);
    }

    private void setupDesiredItemsRecyclerView() {
        desiredItemsRecyclerView.setHasFixedSize(true);
        desiredItemsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        desiredItemsAdapter = new DesiredItemsAdapter(this, desiredItems);
        desiredItemsRecyclerView.setAdapter(desiredItemsAdapter);
    }

    private void sendNotificationToOwner(String ownerId) {
        // Get the current user's details
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String senderUsername = task.getResult().getString("username");
                String senderProfilePicUrl = task.getResult().getString("profilePicUrl");

                // Create a notification with the sender's details
                String message = senderUsername + " sends you a trade request";
                Notification notification = new Notification(senderUsername, message, senderProfilePicUrl);

                // Add the notification to the owner's collection
                db.collection("users").document(ownerId).collection("notifications").add(notification)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d("TradeOfferMatch", "Notification added to owner's collection");
                            } else {
                                Log.e("TradeOfferMatch", "Failed to add notification", task1.getException());
                            }
                        });
            } else {
                Log.e("TradeOfferMatch", "Failed to get sender details", task.getException());
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
