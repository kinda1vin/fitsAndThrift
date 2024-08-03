package com.sp.fitsandthrift;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.sp.fitsandthrift.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TradeRequestReview extends AppCompatActivity {

    private RecyclerView selectedItemsRecyclerView;
    private RecyclerView desiredItemsRecyclerView;
    private SelectedItemsAdapter selectedItemsAdapter;
    private DesiredItemsAdapter desiredItemsAdapter;
    private FirebaseFirestore db;
    private ArrayList<Item> selectedItems = new ArrayList<>();
    private ArrayList<Item> desiredItems = new ArrayList<>();
    private String desiredItemId;
    private static final String TAG = "TradeRequestReview";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trade_request_accept_decline);

        selectedItemsRecyclerView = findViewById(R.id.selected_items_recycler_view);
        desiredItemsRecyclerView = findViewById(R.id.desired_items_recycler_view);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        ArrayList<String> selectedItemIds = intent.getStringArrayListExtra("selectedItems");
        desiredItemId = intent.getStringExtra("desiredItemId");

        Log.d(TAG, "Selected Item IDs: " + selectedItemIds);
        Log.d(TAG, "Desired Item ID: " + desiredItemId);

        if (selectedItemIds != null && !selectedItemIds.isEmpty()) {
            fetchSelectedItems(selectedItemIds);
        } else {
            Log.w(TAG, "No selected items received");
        }

        if (desiredItemId != null) {
            fetchDesiredItem(desiredItemId);
        } else {
            Log.w(TAG, "No desired item ID received");
        }

        Button acceptButton = findViewById(R.id.accept);
        Button declineButton = findViewById(R.id.decline);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle accept action
                acceptTrade(selectedItemIds);
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle decline action
                declineTrade();
            }
        });
    }

    private void fetchSelectedItems(ArrayList<String> selectedItemIds) {
        db.collection("items").whereIn("itemID", selectedItemIds).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Item item = document.toObject(Item.class);
                        selectedItems.add(item);
                        Log.d(TAG, "Selected Item: " + item.getItemDescription());
                    }
                    setupSelectedItemsRecyclerView();
                } else {
                    Log.e(TAG, "Error fetching selected items", task.getException());
                }
            }
        });
    }

    private void fetchDesiredItem(String itemID) {
        db.collection("items").whereEqualTo("itemID", itemID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Item item = document.toObject(Item.class);
                        desiredItems.add(item);
                        Log.d(TAG, "Desired Item: " + item.getItemDescription());
                    }
                    setupDesiredItemsRecyclerView();
                } else {
                    Log.e(TAG, "Error fetching desired item", task.getException());
                }
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

    private void acceptTrade(ArrayList<String> selectedItemIds) {
        if (desiredItemId != null) {
            WriteBatch batch = db.batch();

            // Update desired item
            DocumentReference desiredItemRef = db.collection("items").document(desiredItemId);
            batch.update(desiredItemRef, "trade_status", true);

            // Update each selected item
            for (String itemId : selectedItemIds) {
                DocumentReference selectedItemRef = db.collection("items").document(itemId);
                batch.update(selectedItemRef, "trade_status", true);
            }

            // Commit the batch
            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(TradeRequestReview.this, "Trade Accepted", Toast.LENGTH_SHORT).show();
                        // Redirect or update the trade status in the database
                    } else {
                        Toast.makeText(TradeRequestReview.this, "Failed to accept trade", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void declineTrade() {
        Toast.makeText(this, "Trade Declined", Toast.LENGTH_SHORT).show();
        // No need to update the trade status as it remains false
        finish();
    }
}
