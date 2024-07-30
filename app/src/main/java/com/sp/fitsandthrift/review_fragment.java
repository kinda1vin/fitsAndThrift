package com.sp.fitsandthrift;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.fitsandthrift.adapter.NotiRecycleAdapter;
import com.sp.fitsandthrift.adapter.ReviewAdapter;
import com.sp.fitsandthrift.model.Review;

import java.util.ArrayList;

public class review_fragment extends Fragment {

    private TextView averageRating;
    private RatingBar ratingBar;
    private TextView numberOfReviews;
    private ArrayList<Review> ReviewArrayList;
    private FirebaseFirestore db;
    private RecyclerView ReviewRecyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_fragment, container, false);

        // Initialize views
        averageRating = view.findViewById(R.id.averageRating);
        ratingBar = view.findViewById(R.id.ratingBar);
        numberOfReviews = view.findViewById(R.id.numberOfReviews);

        // Set data
        setReviewBarData(0.0f, 0);
        ReviewDataInitialize();
        ReviewRecyclerView= view.findViewById(R.id.reviewrecyclerview);
        ReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ReviewRecyclerView.setHasFixedSize(true);
        ReviewAdapter reviewAdapter=new ReviewAdapter(getContext(),ReviewArrayList);
        ReviewRecyclerView.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    private void setReviewBarData(float avgRating, int numReviews) {
        averageRating.setText(String.valueOf(avgRating));
        ratingBar.setRating(avgRating);
        numberOfReviews.setText("(" + numReviews + " reviews)");
    }

    private void ReviewDataInitialize() {
        ReviewArrayList = new ArrayList<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Get current user ID
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String reviewUser = documentSnapshot.getString("username");
                String reviewUserPic = documentSnapshot.getString("profilePicUrl");
                String reviewMessage = "Sample review message";

                Review review = new Review(reviewUser, reviewUserPic, reviewMessage);
                ReviewArrayList.add(review);

                if (ReviewRecyclerView.getAdapter() != null) {
                    ReviewRecyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    ReviewAdapter reviewAdapter = new ReviewAdapter(getContext(), ReviewArrayList);
                    ReviewRecyclerView.setAdapter(reviewAdapter);
                }
            } else {
                Log.d("Firestore", "No such document");
            }
        }).addOnFailureListener(e -> Log.d("Firestore", "Failed to fetch user data", e));
    }

    private void updateReviewUI() {
        // Update your UI elements or RecyclerView adapter here
        // This is just a placeholder function to illustrate where you might update the UI
    }
}