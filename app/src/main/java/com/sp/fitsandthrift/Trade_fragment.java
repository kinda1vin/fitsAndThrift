package com.sp.fitsandthrift;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;

import java.util.ArrayList;

public class Trade_fragment extends Fragment {

    private TabHost tabs;
    private ImageView upload;
    private RecyclerView recyclerView;
    private uploadItem.ItemAdapter itemAdapter;
    private static ArrayList<Item> itemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trade_fragment, container, false);

        tabs = view.findViewById(R.id.tabs);
        tabs.setup();

        // Tab1
        TabHost.TabSpec spec = tabs.newTabSpec("History");
        spec.setContent(R.id.history);
        spec.setIndicator("History");
        tabs.addTab(spec);

        // Tab2
        spec = tabs.newTabSpec("My Item");
        spec.setContent(R.id.myitem);
        spec.setIndicator("My Item");
        tabs.addTab(spec);

        upload = view.findViewById(R.id.upload);
        upload.setOnClickListener(onUpload);

        recyclerView = view.findViewById(R.id.myitemView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        itemList = new ArrayList<>();
        itemAdapter = new uploadItem.ItemAdapter(getActivity(), itemList);


        // Check if there are any new items from the upload activity
        Intent intent = getActivity().getIntent();
        String description = intent.getStringExtra("itemdesc");
        String itemphoto = intent.getStringExtra("imageUri");

        if (description != null && itemphoto != null) {
            Uri imageUri = Uri.parse(itemphoto);
            itemList.add(new Item(description, imageUri));
            itemAdapter.notifyItemInserted(itemList.size() - 1);
        }

        recyclerView.setAdapter(itemAdapter);
        recyclerView.setVisibility(View.GONE);

        return view;
    }

    private View.OnClickListener onUpload = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), uploadItem.class);
            startActivityForResult(intent, 1); // Use requestCode 1
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            // Check if there are any new items from the upload activity
            if (data != null) {
                String description = data.getStringExtra("itemdesc");
                String itemphoto = data.getStringExtra("imageUri");

                if (description != null && itemphoto != null) {
                    Uri imageUri = Uri.parse(itemphoto);
                    itemList.add(new Item(description, imageUri));
                    if (itemList.size()==1) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    itemAdapter.notifyItemInserted(itemList.size() - 1);
                }
            }
        }
    }
}