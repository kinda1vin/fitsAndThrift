package com.sp.fitsandthrift;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.fitsandthrift.model.Review;

public class WriteReviewActivity extends AppCompatActivity {

    private EditText reviewEditText;
    private RatingBar ratingBar;
    private Button saveButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String receiverId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        reviewEditText = findViewById(R.id.review_edit_text);
        ratingBar = findViewById(R.id.rating_bar);
        saveButton = findViewById(R.id.save_button);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        receiverId = getIntent().getStringExtra("receiverId");

        saveButton.setOnClickListener(v -> saveReview());
    }

    private void saveReview() {
        String reviewText = reviewEditText.getText().toString();
        int rating = (int) ratingBar.getRating();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String senderId = currentUser.getUid();
            Review review = new Review(senderId, receiverId, reviewText, rating);

            db.collection("reviews").add(review).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    db.collection("user_reviews").document(receiverId).collection("reviews").add(review);
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    // Handle failure
                }
            });
        } else {
            // Handle case where user is not logged in
        }
    }
}
