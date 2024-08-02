package com.sp.fitsandthrift;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sp.fitsandthrift.adapter.NotiRecycleAdapter;
import com.sp.fitsandthrift.model.Notification;

import java.util.ArrayList;

public class notification_fragment extends Fragment {
    private ArrayList<Notification> NotiArrayList;
    private RecyclerView recycleview;
    private NotiRecycleAdapter notiRecycleAdapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        NotiArrayList = new ArrayList<>();
        recycleview = view.findViewById(R.id.notirecycle);
        recycleview.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleview.setHasFixedSize(true);
        notiRecycleAdapter = new NotiRecycleAdapter(getContext(), NotiArrayList);
        recycleview.setAdapter(notiRecycleAdapter);

        fetchNotifications();
    }

    private void fetchNotifications() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).collection("notifications")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        NotiArrayList.clear(); // Clear the list before adding updated notifications
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    NotiArrayList.add(dc.getDocument().toObject(Notification.class));
                                    break;
                                case MODIFIED:
                                    // Handle modification if needed
                                    break;
                                case REMOVED:
                                    // Handle removal if needed
                                    break;
                            }
                        }

                        notiRecycleAdapter.notifyDataSetChanged();
                    }
                });
    }
}
