package com.sp.fitsandthrift.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.sp.fitsandthrift.ChatActivity;
import com.sp.fitsandthrift.Firebase.Util;
import com.sp.fitsandthrift.R;
import com.sp.fitsandthrift.model.ChatroomModel;
import com.sp.fitsandthrift.model.Usermodel;
import com.sp.fitsandthrift.Firebase.AndroidUtil;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        Util.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        if (documentSnapshot.exists()) {  // Check if the document exists
                            Usermodel otherUserModel = documentSnapshot.toObject(Usermodel.class);

                            if (otherUserModel != null) {
                                boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(Util.currentUserId());

                                // Load the profile picture using Glide
                                if (otherUserModel.getProfilePicUrl() != null && !otherUserModel.getProfilePicUrl().isEmpty()) {
                                    Glide.with(context)
                                            .load(otherUserModel.getProfilePicUrl())
                                            .circleCrop()
                                            .placeholder(R.drawable.profile1) // Placeholder image
                                            .error(R.drawable.profile1) // Error image
                                            .into(holder.profilePic);
                                } else {
                                    holder.profilePic.setImageResource(R.drawable.profile1); // Default image
                                }

                                // Set username and last message details
                                holder.usernameText.setText(otherUserModel.getUsername());
                                if (lastMessageSentByMe) {
                                    holder.lastMessageText.setText("You: " + model.getLastMessage());
                                } else {
                                    holder.lastMessageText.setText(model.getLastMessage());
                                }
                                holder.lastMessageTime.setText(Util.timestampToString(model.getLastMessageTimestamp()));

                                // Set click listener to navigate to the chat activity
                                holder.itemView.setOnClickListener(v -> {
                                    Intent intent = new Intent(context, ChatActivity.class);
                                    // Use AndroidUtil or direct putExtra
                                    intent.putExtra("username", otherUserModel.getUsername());
                                    intent.putExtra("email", otherUserModel.getEmail());
                                    intent.putExtra("profilePicUrl", otherUserModel.getProfilePicUrl());
                                    intent.putExtra("userId", otherUserModel.getCurrentUserId());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                });
                            } else {
                                // Log if Usermodel could not be created
                                Log.e("RecentChatAdapter", "Failed to convert document to Usermodel");
                            }
                        } else {
                            Log.e("RecentChatAdapter", "User document does not exist");
                        }
                    } else {
                        Log.e("RecentChatAdapter", "Failed to get user data", task.getException());
                    }
                });
    }


    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_view);
        }
    }
}