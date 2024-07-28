package com.sp.fitsandthrift;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Query;
import com.sp.fitsandthrift.Firebase.Util;
import com.sp.fitsandthrift.adapter.SearchUserRecyclerAdapter;
import com.sp.fitsandthrift.model.Usermodel;

public class SearchUser extends AppCompatActivity {
    TextInputEditText searchUser;
    ImageView backBtn;
    ImageButton searchChat;
    RecyclerView search_user_recycler_view;

    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchUser = findViewById(R.id.searchUserName);
        backBtn = findViewById(R.id.backBtn);
        searchChat = findViewById(R.id.searchChat);
        search_user_recycler_view = findViewById(R.id.search_user_recycler_view);

        searchUser.requestFocus();

        backBtn.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        searchChat.setOnClickListener(v -> {
            // Search for user
            String searchTerm = searchUser.getText().toString();
            if (searchTerm.isEmpty() || searchTerm.length()<3){
                searchUser.setError("Invalid username");
                return;
            }
            setupSearchUserRecycler(searchTerm);
        });

    }

    void setupSearchUserRecycler(String searchTerm){

        Query query = Util.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username", searchTerm);

        FirestoreRecyclerOptions<Usermodel> options = new FirestoreRecyclerOptions.Builder<Usermodel>()
                .setQuery(query, Usermodel.class).build();


        adapter = new SearchUserRecyclerAdapter( options, getApplicationContext());
        search_user_recycler_view.setLayoutManager(new LinearLayoutManager(this ));
        search_user_recycler_view.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null)
            adapter.startListening();
    }
}