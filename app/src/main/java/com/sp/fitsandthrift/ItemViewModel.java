package com.sp.fitsandthrift;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sp.fitsandthrift.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<List<Item>> items = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public void retrieveItems(String itemType, String category, String currentUserId) {
        CollectionReference itemsCollection = db.collection("items");
        com.google.firebase.firestore.Query query = itemsCollection.whereEqualTo("itemType", itemType)
                .whereEqualTo("trade_status", false);

        if (!category.equals("All")) {
            query = query.whereEqualTo("gender", category);
        }
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Item> itemList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Item item = document.toObject(Item.class);
                    item.setItemID(document.getId());
                    if (!item.getUserID().equals(currentUserId)) {
                        itemList.add(item);
                    }
                }
                items.setValue(itemList);
            } else {
                items.setValue(null);
            }
        });
    }

}