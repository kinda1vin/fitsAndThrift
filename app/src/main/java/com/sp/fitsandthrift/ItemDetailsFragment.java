package com.sp.fitsandthrift;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.circleCrop;

import android.content.Context;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.EventLogTags;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sp.fitsandthrift.model.Usermodel;

public class ItemDetailsFragment extends Fragment {
    private ImageView cartButton;
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
    private FirebaseFirestore db;
    private Button requestButton;
    private String itemID;
    private String userID;  // Add this line to store userID
    private Usermodel user;  // Add this line to store user details

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_details, container, false);

        cartButton = rootView.findViewById(R.id.cartButton);
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
        db = FirebaseFirestore.getInstance();

        String Description = getArguments().getString("itemDescription");
        if (Description == null) {
            Toast.makeText(getContext(), "Item not found", Toast.LENGTH_SHORT).show();
            return rootView;
        } else {
            fetchItemDetails(Description);
        }

        // Navigate to Back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QuantityList.class);
                intent.putExtra("itemID", itemID);
                startActivity(intent);
            }
        });

        // Navigate to other user profile
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("otherusername", user.getUsername());
                bundle.putString("otherusergmail", user.getEmail());
                bundle.putString("otheruserid", userID);

                otheruser_fragment otherUserFragment = new otheruser_fragment();
                otherUserFragment.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, otherUserFragment);  // Use replace instead of add
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return rootView;
    }

    private void fetchItemDetails(String Description) {
        db.collection("items").whereEqualTo("itemDescription", Description).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    Item item = documentSnapshot.toObject(Item.class);
                    if (item != null) {
                        itemID = item.getItemID();
                        itemDescription.setText(item.getItemDescription());
                        color.setText("Color" + ": " + item.getColor());
                        gender.setText("Gender" + ": " + item.getItemGender());
                        itemCondition.setText("Item Condition" + ": " + item.getItemCondition());
                        itemType.setText("Type" + ": " + item.getItemType());
                        size.setText("Size" + ": " + item.getSize());
                        Glide.with(getContext()).load(item.getImageUri()).into(itemImage);
                        fetchUserDetails(item.getUserID());
                    }
                }
            }
        });
    }

    private void fetchUserDetails(String userID) {
        this.userID = userID;  // Store userID for later use
        db.collection("users").whereEqualTo("currentUserId", userID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    user = documentSnapshot.toObject(Usermodel.class);
                    if (user != null) {
                        userName.setText(user.getUsername());
                        Glide.with(getContext()).load(user.getProfilePicUrl()).circleCrop().into(profilePic);
                    }
                }
            }
        });
    }
}
