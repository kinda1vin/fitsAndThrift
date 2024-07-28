package com.sp.fitsandthrift;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.sp.fitsandthrift.model.Usermodel;
import com.sp.fitsandthrift.Firebase.AndroidUtil;

public class ChatActivity extends AppCompatActivity {

    Usermodel otherUser;
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

        otherUsernameTextView = findViewById(R.id.other_user_name);
        msgInput = findViewById(R.id.chatBox);
        sendMsgButton = findViewById(R.id.msg_send_btn);
        backToChatListButton = findViewById(R.id.backBtn);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);

        backToChatListButton.setOnClickListener(v->{
            onBackPressed();
        });

        otherUsernameTextView.setText(otherUser.getUsername());



    }
}