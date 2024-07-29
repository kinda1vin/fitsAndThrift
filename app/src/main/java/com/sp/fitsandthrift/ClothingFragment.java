package com.sp.fitsandthrift;

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
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import com.sp.fitsandthrift.adapter.itemAdapter;



public class ClothingFragment extends Fragment{
    private ImageView backImageView;
    private ImageView cartButton;
    private TextView clothingTitle;
    private boolean isCartDisplayed = true;
    private RecyclerView recyclerView;
    private itemAdapter itemAdapter;
    private List<Item> clothingItemList;
    private FirebaseFirestore db;
    private String category;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_clothing, container, false);
        backImageView = rootView.findViewById(R.id.backIcon);
        cartButton = rootView.findViewById(R.id.cart);
        clothingTitle = rootView.findViewById(R.id.footwearTitle);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        db = FirebaseFirestore.getInstance();
        clothingItemList = new ArrayList<>();
        itemAdapter = new itemAdapter(getContext(), clothingItemList);
        recyclerView.setAdapter(itemAdapter);

        if (getArguments() != null) {
            category = getArguments().getString("category", "ALL");
        }
        retrieveClothingItems();

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

        // Inflate the layout for this fragment
        return rootView;
    }

    //filter clothing items according to gender
    private void retrieveClothingItems() {
        CollectionReference itemsCollection = db.collection("items");
        com.google.firebase.firestore.Query query = itemsCollection.whereEqualTo("itemType", "Clothing");

        if (!category.equals("All")) {
            query = query.whereEqualTo("gender", category);
        }

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        clothingItemList.clear();

                        for(QueryDocumentSnapshot document : task.getResult()) {
                            Item item = document.toObject(Item.class);
                            clothingItemList.add(item);
                        }
                        itemAdapter.notifyDataSetChanged();
                        updateClothingItems(category);
                    }
                });
    }

    //update the title of the clothing fragment according to gender
    public void updateClothingItems(String category) {
        switch (category) {
            case "Female":
                clothingTitle.setText("WOMEN / CLOTHING");
                break;
            case "Male":
                clothingTitle.setText("MEN / CLOTHING");
                break;
            default:
                clothingTitle.setText("CLOTHING");
                break;
        }
    }

}