package com.sp.fitsandthrift;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
    private String imageUri;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userID;
    private boolean isCartDisplayed = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_details, container, false);

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
        db = FirebaseFirestore.getInstance();

        userID = auth.getCurrentUser().getUid();

        String itemID = getArguments().getString("itemID");

        if (itemID == null) {
            Toast.makeText(getContext(), "Item not found", Toast.LENGTH_SHORT).show();
            return rootView;
        } else {
            fetchItemDetails(itemID);
        }

        // Navigate to Back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    addToCart();

                    if (isCartDisplayed) {
                        // change icon
                        cartIcon.setImageResource(R.drawable.cart_filled);
                    } else {
                        cartIcon.setImageResource(R.drawable.cart_notfilled);
                    }
                }
            }
        });

        return rootView;
    }

    private void addToCart() {
        String itemID = getArguments().getString("itemID");
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
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Item added to cart", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchItemDetails(String itemID) {
        db.collection("items").whereEqualTo("itemID", itemID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    Item item = documentSnapshot.toObject(Item.class);
                    if (item != null) {
                        itemDescription.setText(item.getItemDescription());
                        color.setText("Color" + ": " + item.getColor());
                        gender.setText("Gender" + ": " + item.getItemGender());
                        itemCondition.setText("Item Condition" + ": " + item.getItemCondition());
                        itemType.setText("Type" + ": " + item.getItemType());
                        size.setText("Size" + ": " + item.getSize());
                        imageUri = item.getImageUri();
                        Glide.with(getContext()).load(item.getImageUri()).into(itemImage);
                        fetchUserDetails(item.getUserID());
                    }
                }
            }

        });
    }

    private void fetchUserDetails(String userID) {
        db.collection("users").whereEqualTo("currentUserId", userID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    Usermodel user = documentSnapshot.toObject(Usermodel.class);
                    if (user != null) {
                        userName.setText("Uploaded By" + ": " + user.getUsername());
                        Glide.with(getContext()).load(user.getProfilePicUrl()).circleCrop().into(profilePic);
                    }
                }
            }
        });
    }
}