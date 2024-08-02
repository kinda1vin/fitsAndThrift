package com.sp.fitsandthrift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.sp.fitsandthrift.adapter.ChatRecyclerAdapter;
import com.sp.fitsandthrift.model.ChatMessageModel;
import com.sp.fitsandthrift.model.ChatroomModel;
import com.sp.fitsandthrift.model.Usermodel;
import com.sp.fitsandthrift.Firebase.AndroidUtil;
import com.sp.fitsandthrift.Firebase.Util;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    Usermodel otherUser; // User model for the other user
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    TextView otherUsernameTextView;
    EditText msgInput;
    ImageButton sendMsgButton;
    ImageView backToChatListButton;
    RecyclerView chatRecyclerView;
    ImageView profilePic; // ImageView for profile picture

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Retrieve the other user's information from the intent
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = Util.getChatroomId(Util.currentUserId(), otherUser.getCurrentUserId());

        // Initialize UI components
        otherUsernameTextView = findViewById(R.id.other_user_name);
        msgInput = findViewById(R.id.chatBox);
        sendMsgButton = findViewById(R.id.msg_send_btn);
        backToChatListButton = findViewById(R.id.backBtn);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        profilePic = findViewById(R.id.profilePic); // Ensure this is the correct ID for your ImageView

        // Load the profile picture of the other user
        loadUserProfilePicture(otherUser.getProfilePicUrl());

        // Set up the back button to go to the previous screen
        backToChatListButton.setOnClickListener(v -> onBackPressed());

        // Set the other user's username in the TextView
        otherUsernameTextView.setText(otherUser.getUsername());

        // Set up the send button to send a message
        sendMsgButton.setOnClickListener(v -> {
            String msg = msgInput.getText().toString().trim();
            if (!msg.isEmpty()) {
                sendMessageToUser(msg);
            }
        });

        // Set up the chat room and recycler view
        getOrCreateChatroomModel();
        setUpChatRecyclerView();
    }

    // Method to load the user's profile picture using Glide
    private void loadUserProfilePicture(String profilePicUrl) {
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Glide.with(this)
                    .load(profilePicUrl)
                    .circleCrop()
                    .placeholder(R.drawable.profile1) // Placeholder image
                    .error(R.drawable.profile1) // Error image
                    .into(profilePic);
        } else {
            profilePic.setImageResource(R.drawable.profile1); // Default image if URL is null
        }
    }

    // Set up the recycler view for chat messages
    void setUpChatRecyclerView() {
        Query query = Util.getChatMessagesReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class)
                .build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        chatRecyclerView.setLayoutManager(manager);
        chatRecyclerView.setAdapter(adapter);
        adapter.startListening();

        // Smooth scroll to the bottom when a new message is added
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (positionStart == 0) {
                    chatRecyclerView.smoothScrollToPosition(0);
                }
            }
        });
    }

    // Method to send a message to the other user
    void sendMessageToUser(String msg) {
        // Update chatroom model with the last message details
        chatroomModel.setLastMessageSenderId(Util.currentUserId());
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessage(msg);
        Util.getChatroomReference(chatroomId).set(chatroomModel);

        // Create a new chat message model
        ChatMessageModel chatMessageModel = new ChatMessageModel(
                msg,
                Util.currentUserId(),
                Timestamp.now()
        );

        // Add the message to the database
        Util.getChatMessagesReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            msgInput.setText(""); // Clear the input field on successful message send
                        }
                    }
                });
    }

    // Get or create the chatroom model
    void getOrCreateChatroomModel() {
        Util.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    // First time chat, create a new chatroom
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(Util.currentUserId(), otherUser.getCurrentUserId()),
                            Timestamp.now(),
                            ""
                    );
                    Util.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    // Override the onBackPressed method to handle back button action
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("loadChatFragment", true);
        startActivity(intent);
        finish();
    }
}
