package com.sp.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class review_fragment extends Fragment {

    private TextView averageRating;
    private RatingBar ratingBar;
    private TextView numberOfReviews;

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

        return view;
    }

    private void setReviewBarData(float avgRating, int numReviews) {
        averageRating.setText(String.valueOf(avgRating));
        ratingBar.setRating(avgRating);
        numberOfReviews.setText("(" + numReviews + " reviews)");
    }
}
