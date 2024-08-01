package com.sp.fitsandthrift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.fitsandthrift.R;
import com.sp.fitsandthrift.model.Review;
import com.sp.fitsandthrift.model.Usermodel;
import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private ArrayList<Review> reviewList;
    private FirebaseFirestore db;

    public ReviewAdapter(Context context, ArrayList<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reviewlayout, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        String senderId = review.getSenderId();
        if (senderId != null) {
            db.collection("users").document(senderId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Usermodel usermodel = documentSnapshot.toObject(Usermodel.class);
                    if (usermodel != null) {
                        holder.username.setText(usermodel.getUsername());
                        Glide.with(context).load(usermodel.getProfilePicUrl()).circleCrop().placeholder(R.drawable.profile).error(R.drawable.profile).into(holder.profilePic);
                    }
                }
            });
        } else {
            // Handle case where senderId is null
            holder.username.setText("Unknown User");
            holder.profilePic.setImageResource(R.drawable.profile1); // Placeholder image
        }

        holder.reviewText.setText(review.getReviewText());
        holder.ratingBar.setRating(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView username, reviewText;
        RatingBar ratingBar;
        ImageView profilePic;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.reviewusername);
            reviewText = itemView.findViewById(R.id.review_text);
            ratingBar = itemView.findViewById(R.id.reviewratingbar);
            profilePic = itemView.findViewById(R.id.reviewuserpic);
        }
    }
}
