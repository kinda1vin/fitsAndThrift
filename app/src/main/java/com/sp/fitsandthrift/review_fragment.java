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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    private ReviewAdapter reviewAdapter;
    private FirebaseAuth auth;

    private static final int WRITE_REVIEW_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_fragment, container, false);

        averageRating = view.findViewById(R.id.otheruseravgrating);
        ratingBar = view.findViewById(R.id.otherratingbar);
        numberOfReviews = view.findViewById(R.id.othertotalreview);

        setReviewBarData(0.0f, 0);

        ReviewArrayList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(getContext(), ReviewArrayList);

        ReviewRecyclerView = view.findViewById(R.id.other_reviewrecycle);
        ReviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ReviewRecyclerView.setHasFixedSize(true);
        ReviewRecyclerView.setAdapter(reviewAdapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        loadReviews();

        return view;
    }

    private void setReviewBarData(float avgRating, int numReviews) {
        // Use String.format to format the average rating
        String formattedRating = String.format("%.2f", avgRating);

        averageRating.setText(formattedRating);  // Set formatted average rating
        ratingBar.setRating(avgRating);
        numberOfReviews.setText("(" + numReviews + " reviews)");
    }
    private void loadReviews() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference reviewsRef = db.collection("reviews");

        reviewsRef.whereEqualTo("receiverId", userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("Firestore", "Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots == null) {
                    Log.e("Firestore", "No data found.");
                    return;
                }

                ReviewArrayList.clear();
                int totalRating = 0;

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    try {
                        Review review = document.toObject(Review.class);
                        ReviewArrayList.add(review);
                        totalRating += review.getRating();
                    } catch (Exception ex) {
                        Log.e("Firestore", "Error deserializing review", ex);
                    }
                }

                int numReviews = ReviewArrayList.size();
                float avgRating = numReviews > 0 ? (float) totalRating / numReviews : 0.0f;
                setReviewBarData(avgRating, numReviews);
                reviewAdapter.notifyDataSetChanged();
            }
        });
    }
}
