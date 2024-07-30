package com.sp.fitsandthrift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.sp.fitsandthrift.adapter.ChatRecyclerAdapter;
import com.sp.fitsandthrift.model.ChatMessageModel;
import com.sp.fitsandthrift.model.ChatroomModel;
import com.sp.fitsandthrift.model.Usermodel;
import com.sp.fitsandthrift.Firebase.AndroidUtil;
import com.sp.fitsandthrift.Firebase.Util;

import java.sql.Time;
import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    Usermodel otherUser;

    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    TextView otherUsernameTextView;
    EditText msgInput;
    ImageButton sendMsgButton;
    ImageView backToChatListButton;
    RecyclerView chatRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = Util.getChatroomId(Util.currentUserId(), otherUser.getCurrentUserId());

        otherUsernameTextView = findViewById(R.id.other_user_name);
        msgInput = findViewById(R.id.chatBox);
        sendMsgButton = findViewById(R.id.msg_send_btn);
        backToChatListButton = findViewById(R.id.backBtn);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);

        backToChatListButton.setOnClickListener(v->{
            onBackPressed();
        });

        otherUsernameTextView.setText(otherUser.getUsername());

        sendMsgButton.setOnClickListener((v->{
            String msg = msgInput.getText().toString().trim();
            if(msg.isEmpty()){
                return;
            }
            sendMessageToUser(msg);
        }));

        getOrCreateChatroomModel();

        setUpChatRecyclerView();
    }

    void setUpChatRecyclerView(){
        Query query = Util.getChatMessagesReference(chatroomId).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        chatRecyclerView.setLayoutManager(manager);
        chatRecyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String msg){

        chatroomModel.setLastMessageSenderId(Util.currentUserId());
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessage(msg);
        Util.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(
                msg,
                Util.currentUserId(),
                Timestamp.now()
        );
        Util.getChatMessagesReference(chatroomId).add(chatMessageModel).
                addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            msgInput.setText("");
                        }
                    }
                });
    }
    void getOrCreateChatroomModel(){
        Util.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel==null){
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(Util.currentUserId(),otherUser.getCurrentUserId()),
                            Timestamp.now(),
                            ""
                    );
                    Util.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("loadChatFragment", true);
        startActivity(intent);
        finish();
    }
}
