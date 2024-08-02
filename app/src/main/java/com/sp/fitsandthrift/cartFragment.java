package com.sp.fitsandthrift;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sp.fitsandthrift.adapter.CartAdapter;
import com.sp.fitsandthrift.model.CartItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class cartFragment extends Fragment {
    private ImageView backImageView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private RecyclerView recyclerViewCart;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userID;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        backImageView = rootView.findViewById(R.id.backIcon);
        recyclerViewCart = rootView.findViewById(R.id.recyclerViewCart);
        searchView = rootView.findViewById(R.id.searchView);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(getContext(), cartItems);
        recyclerViewCart.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerViewCart.setAdapter(cartAdapter);

        //Set up search View
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

        // Get userID from Firebase Authentication if not passed in arguments
        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            Log.e("cartFragment", "User not authenticated");
            return rootView;
        }

        // Load cart items for the user
        checkAndCreateCartDocument();

        // Navigate to Home Page
        backImageView.setOnClickListener(v -> getParentFragmentManager().popBackStack());



        return rootView;
    }

    private void filterItems(String newText) {
        List<CartItem> filteredList = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.getItemDescription().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(item);
            }
        }
        cartAdapter.updateList(filteredList);
    }

    private void checkAndCreateCartDocument() {
        DocumentReference cartDocRef = db.collection("carts").document(userID);

        cartDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Document exists, load cart items
                    loadCartItems();
                } else {
                    // Document does not exist, create it
                    Map<String, Object> emptyCart = new HashMap<>();
                    cartDocRef.set(emptyCart).addOnCompleteListener(createTask -> {
                        if (createTask.isSuccessful()) {
                            // Document created successfully
                            loadCartItems();
                        } else {
                            // Handle error
                            Toast.makeText(getContext(), "Error creating cart document", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                // Handle error
                Toast.makeText(getContext(), "Error checking cart document existence", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCartItems() {
        DocumentReference cartDocRef = db.collection("carts").document(userID);

        cartDocRef.collection("Items").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cartItems.clear();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    CartItem cartItem = document.toObject(CartItem.class);
                    if (cartItem != null) {
                        cartItems.add(cartItem);
                    }
                }
                cartAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error loading cart items: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}