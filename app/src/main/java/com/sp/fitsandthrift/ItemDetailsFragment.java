package com.sp.fitsandthrift;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.fitsandthrift.model.CartItem;
import com.sp.fitsandthrift.model.Usermodel;

public class ItemDetailsFragment extends Fragment {
    private ImageView cartIcon;
    private ImageView backButton;
    private ImageView itemImage;
    private TextView itemDescription;
    private TextView color;
    private TextView gender;
    private TextView itemCondition;
    private TextView itemType;
    private TextView size;
    private TextView userName;
    private ImageView profilePic;
    private Button requestButton;
    private String imageUri;
    private Usermodel user;
    private String uploaderID;  // Store the uploader's userID here

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userID;
    private boolean isCartDisplayed = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_details, container, false);

        // Initialize UI components
        cartIcon = rootView.findViewById(R.id.cartButton);
        itemImage = rootView.findViewById(R.id.itemImageView);
        itemDescription = rootView.findViewById(R.id.DescriptionTextView);
        color = rootView.findViewById(R.id.colorTextView);
        gender = rootView.findViewById(R.id.genderTextView);
        itemCondition = rootView.findViewById(R.id.conditionTextView);
        itemType = rootView.findViewById(R.id.typeTextView);
        userName = rootView.findViewById(R.id.userName);
        profilePic = rootView.findViewById(R.id.profilePic);
        size = rootView.findViewById(R.id.sizeTextView);
        backButton = rootView.findViewById(R.id.backButton);
        requestButton = rootView.findViewById(R.id.requestButton);

        // Retrieve arguments passed from the activity
        final String itemID;
        final String userID;

        if (getArguments() != null) {
            itemID = getArguments().getString("itemID");
            userID = getArguments().getString("userID");
        } else {
            itemID = null;
            userID = auth.getCurrentUser().getUid(); // Fallback to the current user ID
        }

        // Check if itemID is null and notify the user
        if (itemID == null) {
            Toast.makeText(getContext(), "Item not found", Toast.LENGTH_SHORT).show();
            return rootView;
        } else {
            fetchItemDetails(itemID);
        }

        // Navigate Back to MainActivity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBackToMainActivity();
            }
        });

        // Handle request button click
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QuantityList.class);
                intent.putExtra("itemID", itemID);
                startActivity(intent);
            }
        });

        // Handle profile picture click to navigate to another user fragment
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("otherusername", user.getUsername());
                    bundle.putString("otherusergmail", user.getEmail());
                    bundle.putString("otheruserid", uploaderID);  // Use uploaderID here

                    otheruser_fragment otherUserFragment = new otheruser_fragment();
                    otherUserFragment.setArguments(bundle);

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, otherUserFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(getContext(), "User information is not available yet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle cart icon click to add item to cart
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(itemID);

                if (isCartDisplayed) {
                    // change icon
                    cartIcon.setImageResource(R.drawable.cart_filled);
                } else {
                    cartIcon.setImageResource(R.drawable.cart_notfilled);
                }
            }
        });

        return rootView;
    }

    private void addToCart(String itemID) {
        if (itemID == null) {
            Toast.makeText(getContext(), "Item not found", Toast.LENGTH_SHORT).show();
            return;
        }

        CartItem cartItem = new CartItem(
                imageUri,
                itemDescription.getText().toString(),
                itemID,
                userID
        );

        db.collection("carts")
                .document(userID)
                .collection("Items")
                .document(itemID)
                .set(cartItem)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Item added to cart", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add item to cart", Toast.LENGTH_SHORT).show());
    }

    private void fetchItemDetails(String itemID) {
        db.collection("items").document(itemID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Item item = documentSnapshot.toObject(Item.class);
                if (item != null) {
                    itemDescription.setText(item.getItemDescription());
                    color.setText("Color: " + item.getColor());
                    gender.setText("Gender: " + item.getGender());
                    itemCondition.setText("Item Condition: " + item.getItemCondition());
                    itemType.setText("Type: " + item.getItemType());
                    size.setText("Size: " + item.getSize());
                    imageUri = item.getImageUri();
                    Glide.with(getContext()).load(item.getImageUri()).into(itemImage);
                    uploaderID = item.getUserID(); // Store uploader's userID
                    fetchUserDetails(uploaderID);
                }
            } else {
                Toast.makeText(getContext(), "Item not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserDetails(String userID) {
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                user = documentSnapshot.toObject(Usermodel.class);
                if (user != null) {
                    userName.setText("Uploaded By: " + user.getUsername());
                    Glide.with(getContext()).load(user.getProfilePicUrl()).circleCrop().into(profilePic);
                } else {
                    Toast.makeText(getContext(), "User data is not available", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show());
    }

    private void navigateBackToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        // Optionally, add flags to clear the back stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // Finish current activity to remove it from the back stack
        requireActivity().finish();
    }
}
