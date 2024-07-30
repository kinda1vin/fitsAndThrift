package com.sp.fitsandthrift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sp.fitsandthrift.R;
import com.sp.fitsandthrift.model.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyReviewViewHolder> {
    private Context context;
    private ArrayList<Review> reviewArrayList;

    public ReviewAdapter(Context context, ArrayList<Review> reviewArrayList) {
        this.context = context;
        this.reviewArrayList = reviewArrayList;
    }

    @NonNull
    @Override
    public MyReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.reviewlayout, parent, false);
        return new MyReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyReviewViewHolder holder, int position) {
        Review review = reviewArrayList.get(position);
        // Assuming getReviewuserpic() returns a drawable resource ID
        Glide.with(context)
                .load(review.getReviewuserpic()) // Assume getReviewuserpic() returns a URL
                .placeholder(R.drawable.activeprofile) // Optional: a default image to display while loading
                .into(holder.reviewuserpic);
        holder.reviewusername.setText(review.getUser());
        holder.reviewmessage.setText(review.getReviewtext());
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    public static class MyReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView reviewuserpic;
        TextView reviewusername;
        TextView reviewmessage;

        public MyReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewuserpic = itemView.findViewById(R.id.reviewuserpic);
            reviewusername = itemView.findViewById(R.id.reviewusername);
            reviewmessage = itemView.findViewById(R.id.reviewmesssage);
        }
    }
}