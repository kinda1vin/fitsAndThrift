package com.sp.fitsandthrift.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sp.fitsandthrift.R;
import com.sp.fitsandthrift.model.Notification;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class NotiRecycleAdapter extends RecyclerView.Adapter<NotiRecycleAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Notification> NotiArrayList;
    private OnNotificationClickListener onNotificationClickListener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }
    public NotiRecycleAdapter(Context context, ArrayList<Notification> NotiArrayList, OnNotificationClickListener listener) {
        this.context = context;
        this.NotiArrayList = NotiArrayList;
        this.onNotificationClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notii_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notification notification = NotiArrayList.get(position);
        holder.tvMessage.setText(notification.getMessage());

        // Log data to debug
        Log.d("NotiRecycleAdapter", "Username: " + notification.getSenderUsername());
        Log.d("NotiRecycleAdapter", "Profile Pic URL: " + notification.getSenderProfilePicUrl());

        // Load the sender's profile picture using Glide
        Glide.with(context)
                .load(notification.getSenderProfilePicUrl())
                .circleCrop()  // Circular crop the profile picture
                .placeholder(R.drawable.baseline_account_circle_24)  // Placeholder image
                .error(R.drawable.profile)  // Error image
                .into(holder.profileImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification != null) {
                    onNotificationClickListener.onNotificationClick(notification);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return NotiArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvMessage;
        ShapeableImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.title_image); // Ensure this matches the ImageView ID in XML
            tvMessage = itemView.findViewById(R.id.tvMessage); // Ensure this matches the TextView ID in XML
        }
    }
}
