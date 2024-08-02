package com.sp.fitsandthrift;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;

public class QuantityList extends AppCompatActivity implements QuantityListener{

    private Button selectBtn;
    RecyclerView quantityRecyclerView;
    QuantityAdapter quantityAdapter;
    private FirebaseFirestore db;
    private ArrayList<Item> quantityList = new ArrayList<>();
    private static final String PREFS_NAME = "trade_prefs";
    private static final String ITEM_ID_KEY = "item_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trade_request_item_list);

        quantityRecyclerView = findViewById(R.id.item_list_request);

        db = FirebaseFirestore.getInstance();

        selectBtn = findViewById(R.id.item_list_select);

        String itemID = getIntent().getStringExtra("itemID");
        if(itemID != null){
            saveItemID(itemID);
        }

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedItems = quantityAdapter.getSelectedItems();
                Intent intent = new Intent(QuantityList.this, TradeOfferMatch.class);
                intent.putStringArrayListExtra("selectedItems", selectedItems);
                startActivity(intent);
            }
        });

        setquantityRecyclerView();
        fetchItemList();
    }

    private void fetchItemList(){
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference collectionReference = db.collection("items");
        collectionReference.whereEqualTo("userID", currentUserId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Item item = document.toObject(Item.class);
                        quantityList.add(item);
                    }
                    quantityAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void setquantityRecyclerView() {

        quantityRecyclerView.setHasFixedSize(true);
        quantityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        quantityAdapter = new QuantityAdapter(this, quantityList, this);
        quantityRecyclerView.setAdapter(quantityAdapter);
    }

    private void saveItemID(String itemID){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ITEM_ID_KEY, itemID);
        editor.apply();
    }

    @Override
    public void onQuantityChanged(ArrayList<String> quantityList) {
        Toast.makeText(this, "Selected" , Toast.LENGTH_SHORT).show();
    }
}
