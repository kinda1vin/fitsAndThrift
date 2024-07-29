package com.sp.fitsandthrift;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Trade_fragment extends Fragment {

    private TabHost tabs;
    private ImageView upload;
    private RecyclerView recyclerView;
    private uploadItem.ItemAdapter ItemAdapter;
    public static ArrayList<Item> items;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trade_fragment, container, false);

        tabs = view.findViewById(R.id.tabs);
        tabs.setup();

        // Tab1
        TabHost.TabSpec spec = tabs.newTabSpec("History");
        spec.setContent(R.id.history);
        spec.setIndicator("History");
        tabs.addTab(spec);

        // Tab2
        spec = tabs.newTabSpec("My Item");
        spec.setContent(R.id.myitem);
        spec.setIndicator("My Item");
        tabs.addTab(spec);


        upload = view.findViewById(R.id.upload);
        upload.setOnClickListener(onUpload);

        recyclerView = view.findViewById(R.id.myitemView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        db = FirebaseFirestore.getInstance();
        items = new ArrayList<Item>();
        ItemAdapter = new uploadItem.ItemAdapter(getContext(), items);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        EventChangeListener();

        // Check if there are any new items from the upload activity
        Intent intent = getActivity().getIntent();
        String itemDescription = intent.getStringExtra("itemDescription");
        String imageUri = intent.getStringExtra("imageUri");
        //String gender = intent.getStringExtra("gender");
        //String itemType = intent.getStringExtra("itemType");
        //String color = intent.getStringExtra("color");
        //String size = intent.getStringExtra("size");
        //String itemCondition = intent.getStringExtra("itemCondition");

        if (itemDescription != null && imageUri != null) {
            Uri itemphoto = Uri.parse(imageUri);
            items.add(new Item());
            if (items.size()==1) {
                recyclerView.setVisibility(View.VISIBLE);
            }
            ItemAdapter.notifyItemInserted(items.size() - 1);
        }



        // Get the current user's ID
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "Current user ID: " + userID);
        // Create a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a query against the collection.
        Query query = db.collection("items").whereEqualTo("userID", userID);

        // Add a snapshot listener to the query
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                // Clear the items ArrayList
                items.clear();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Convert the document into an Item object
                    Item item = document.toObject(Item.class);
                    Log.d(TAG, "Item data: " + item.toString());
                    // Add the item to the items ArrayList
                    items.add(item);
                }
                Log.d(TAG, "Items ArrayList size: " + items.size());
                // Notify the adapter that the data has changed
                ItemAdapter.notifyDataSetChanged();
            }
        });

        // Existing code...
        recyclerView.setAdapter(ItemAdapter);
        recyclerView.setVisibility(View.INVISIBLE);
        return view;
    }

    private void EventChangeListener() {
        db.collection("items")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){

                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                            Log.e("Firestore error", Objects.requireNonNull(error.getMessage()));
                            return;
                        }
                        //assert value != null;
                        if(value != null){
                            for (DocumentChange dc : value.getDocumentChanges()){
                                if(dc.getType() == DocumentChange.Type.ADDED){
                                    String imageUri = dc.getDocument().getString("imageUri");
                                    Item item = dc.getDocument().toObject(Item.class);
                                    item.setImageUri(imageUri);
                                    if(item.getItemDescription() != null && item.getImageUri() != null){
                                        items.add(item);
                                    }
                                }
                            }
                            ItemAdapter.notifyDataSetChanged();
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }

                        // Set RecyclerView visibility to VISIBLE after data is loaded
                        if (!items.isEmpty()) {
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private View.OnClickListener onUpload = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), uploadItem.class);
            startActivityForResult(intent, 1); // Use requestCode 1
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            // Check if there are any new items from the upload activity
            if (data != null) {
                String itemDescription = data.getStringExtra("itemDescription");
                String imageUri = data.getStringExtra("imageUri");
                //String gender = data.getStringExtra("gender");
                //String itemType = data.getStringExtra("itemType");
                //String color = data.getStringExtra("color");
                //String size = data.getStringExtra("size");
                //String itemCondition = data.getStringExtra("itemCondition");

                if (itemDescription != null && imageUri != null) {
                    Item newItem = new Item(itemDescription, imageUri);
                    items.add(new Item());
                    if (items.size()==1) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    ItemAdapter.notifyItemInserted(items.size() - 1);
                }
            }
        }
    }
}