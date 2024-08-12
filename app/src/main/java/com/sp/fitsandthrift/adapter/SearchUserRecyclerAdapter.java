package com.sp.fitsandthrift.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.sp.fitsandthrift.ChatActivity;
import com.sp.fitsandthrift.Firebase.AndroidUtil;
import com.sp.fitsandthrift.Firebase.Util;
import com.sp.fitsandthrift.R;
import com.sp.fitsandthrift.model.Usermodel;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<Usermodel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Usermodel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull Usermodel model) {
        holder.username.setText(model.getUsername());
        holder.phoneNumber.setText(model.getPhoneNumber());

        // Display "(You)" if the current user is the one being displayed
        if (model.getCurrentUserId().equals(Util.currentUserId())) {
            holder.username.setText(model.getUsername() + " (You)");
        }

        // Load profile picture using Glide
        if (model.getProfilePicUrl() != null && !model.getProfilePicUrl().isEmpty()) {
            Glide.with(context)
                    .load(model.getProfilePicUrl())
                    .circleCrop()
                    .placeholder(R.drawable.profile1) // Placeholder image
                    .error(R.drawable.profile1) // Error image
                    .into(holder.profilePic);
        } else {
            holder.profilePic.setImageResource(R.drawable.profile1); // Default image
        }

        holder.itemView.setOnClickListener(v -> {
            // Intent to start ChatActivity and pass selected user's data
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, model); // Pass the user model using this method
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView phoneNumber;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            phoneNumber = itemView.findViewById(R.id.user_phone);
            profilePic = itemView.findViewById(R.id.profile_pic_view); // Ensure this is the correct ID
        }
    }
}