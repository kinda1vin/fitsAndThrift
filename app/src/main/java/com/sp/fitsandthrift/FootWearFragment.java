package com.sp.fitsandthrift;

import android.os.Bundle;

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
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import com.sp.fitsandthrift.adapter.itemAdapter;
import com.sp.fitsandthrift.model.Usermodel;


public class FootWearFragment extends Fragment implements selectListener {
    private ImageView backImageView;
    private ImageView cartButton;
    private TextView footwearTitle;
    private boolean isCartDisplayed = true;
    private RecyclerView recyclerView;
    private itemAdapter itemAdapter;
    private List<Item> footwearItemList;
    private FirebaseFirestore db;
    private SearchView searchView;

    private String category;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_foot_wear, container, false);
        backImageView = rootView.findViewById(R.id.backIcon);
        cartButton = rootView.findViewById(R.id.cart);
        footwearTitle = rootView.findViewById(R.id.footwearTitle);
        searchView = rootView.findViewById(R.id.searchView);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        db = FirebaseFirestore.getInstance();
        footwearItemList = new ArrayList<>();
        itemAdapter = new itemAdapter(getContext(), footwearItemList, this);
        recyclerView.setAdapter(itemAdapter);

        if (getArguments() != null) {
            category = getArguments().getString("category", "ALL");
        }

        retrieveFootwearItems();

        // Navigate to Home
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new Home_Fragment());
                fragmentTransaction.addToBackStack(null); // Add to back stack to allow back navigation
                fragmentTransaction.commit();
            }
        });

        // Navigate to Cart
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCartDisplayed) {
                    // change icon
                    cartButton.setImageResource(R.drawable.cart_filled);
                } else {
                    cartButton.setImageResource(R.drawable.cart_notfilled);
                }
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new cartFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //Set up SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterItems(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems(newText);
                return false;
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    private void filterItems(String newText) {
        List<Item> filteredList = new ArrayList<>();
        for(Item item : footwearItemList) {
            if(item.getItemDescription().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(item);
            }
        }
        itemAdapter.updateList(filteredList);
    }

    //filter footwear items according to gender
    private void retrieveFootwearItems() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        CollectionReference itemsCollection = db.collection("items");
        com.google.firebase.firestore.Query query = itemsCollection.whereEqualTo("itemType", "Footwear");



        if(!category.equals("All")) {
            query = query.whereEqualTo("gender", category);
        }

        query.get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        footwearItemList.clear();
                        for(QueryDocumentSnapshot doc : task.getResult()) {
                            Item item = doc.toObject(Item.class);
                            item.setItemID(doc.getId());
                            if(!item.getUserID().equals(currentUserId)) {
                                footwearItemList.add(item);
                            }
                        }
                        itemAdapter.notifyDataSetChanged();
                        updateFootwearItems(category);
                    } else {
                        Log.d("FootwearFragment", "Error getting documents: ", task.getException());
                    }
                });
    }

    //update the title of the footwear fragment according to gender
    public void updateFootwearItems (String category) {
        switch (category) {
            case "Female":
                footwearTitle.setText("WOMEN / FOOTWEAR");
                break;
            case "Male":
                footwearTitle.setText("MEN / FOOTWEAR");
                break;
            default:
                footwearTitle.setText("FOOTWEAR");
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        Item item = footwearItemList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("itemID", item.getItemID());
        bundle.putString("itemDescription", item.getItemDescription());
        bundle.putString("color", item.getColor());
        bundle.putString("gender", item.getGender());
        bundle.putString("imageUri", item.getImageUri());
        bundle.putString("itemCondition", item.getItemCondition());
        bundle.putString("itemType", item.getItemType());
        bundle.putString("size", item.getSize());

        ItemDetailsFragment itemDetailsFragment = new ItemDetailsFragment();
        itemDetailsFragment.setArguments(bundle);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, itemDetailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}