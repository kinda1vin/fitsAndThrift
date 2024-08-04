package com.sp.fitsandthrift;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sp.fitsandthrift.adapter.itemAdapter;

import java.util.ArrayList;
import java.util.List;

public class Trade_fragment extends Fragment {

    private TabHost tabs;
    private ImageView upload;
    private ImageView carticon;
    private RecyclerView myItemRecyclerView;
    private RecyclerView tradedItemsRecyclerView;
    private itemAdapter myItemAdapter;
    private itemAdapter tradedItemAdapter;
    private List<Item> myItems;
    private List<Item> tradedItems;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trade_fragment, container, false);

        tabs = view.findViewById(R.id.tabs);
        tabs.setup();

        // Tab1
        TabHost.TabSpec spec = tabs.newTabSpec("History");
        spec.setContent(R.id.history);
        spec.setIndicator(createTabIndicator("History"));
        tabs.addTab(spec);

        // Tab2
        spec = tabs.newTabSpec("My Item");
        spec.setContent(R.id.myitem);
        spec.setIndicator(createTabIndicator("My Item"));
        tabs.addTab(spec);


        upload = view.findViewById(R.id.upload);
        upload.setOnClickListener(onUpload);

        carticon = view.findViewById(R.id.carticon);
        carticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.tradeframe, new cartFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        myItemRecyclerView = view.findViewById(R.id.myitemView);
        tradedItemsRecyclerView = view.findViewById(R.id.traded_items_view);

        myItemRecyclerView.setHasFixedSize(true);
        myItemRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        tradedItemsRecyclerView.setHasFixedSize(true);
        tradedItemsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        db = FirebaseFirestore.getInstance();
        myItems = new ArrayList<>();
        tradedItems = new ArrayList<>();

        myItemAdapter = new itemAdapter(getContext(), myItems, new selectListener() {
            @Override
            public void onItemClick(Item item) {
                MyItemDetails_Fragment myItemDetails_fragment = MyItemDetails_Fragment.newInstance(item);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.tradeframe, myItemDetails_fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        tradedItemAdapter = new itemAdapter(getContext(), tradedItems, new selectListener() {
            @Override
            public void onItemClick(Item item) {
                // Handle item click if needed
            }
        });

        myItemRecyclerView.setAdapter(myItemAdapter);
        tradedItemsRecyclerView.setAdapter(tradedItemAdapter);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        fetchMyItems();
        fetchTradedItems();

        return view;
    }
    private View createTabIndicator(String title) {
        TextView tabIndicator = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.tab_item, null);
        tabIndicator.setText(title);
        return tabIndicator;
    }

    private void fetchMyItems() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "Current user ID: " + userID);

        Query query = db.collection("items")
                .whereEqualTo("userID", userID)
                .whereEqualTo("trade_status", false);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                myItems.clear();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Item item = document.toObject(Item.class);
                    Log.d(TAG, "Item data: " + item.toString());
                    myItems.add(item);
                }
                Log.d(TAG, "Items ArrayList size: " + myItems.size());
                myItemAdapter.notifyDataSetChanged();

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

    private void fetchTradedItems() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "Current user ID: " + userID);

        Query query = db.collection("items")
                .whereEqualTo("userID", userID)
                .whereEqualTo("trade_status", true);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                tradedItems.clear();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Item item = document.toObject(Item.class);
                    Log.d(TAG, "Item data: " + item.toString());
                    tradedItems.add(item);
                }
                Log.d(TAG, "Items ArrayList size: " + tradedItems.size());
                tradedItemAdapter.notifyDataSetChanged();

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
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
            if (data != null) {
                String itemDescription = data.getStringExtra("itemDescription");
                String imageUri = data.getStringExtra("imageUri");

                if (itemDescription != null && imageUri != null) {
                    Item newItem = new Item(itemDescription, imageUri);
                    myItems.add(newItem);
                    if (myItems.size() == 1) {
                        myItemRecyclerView.setVisibility(View.VISIBLE);
                    }
                    myItemAdapter.notifyItemInserted(myItems.size() - 1);
                }
            }
        }
    }
}