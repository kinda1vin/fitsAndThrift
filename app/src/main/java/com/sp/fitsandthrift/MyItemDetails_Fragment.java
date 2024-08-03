package com.sp.fitsandthrift;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class MyItemDetails_Fragment extends Fragment {

    private static final String ARG_ITEM = "item";

    private Item item;

    public static MyItemDetails_Fragment newInstance(Item item) {
        MyItemDetails_Fragment fragment = new MyItemDetails_Fragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (Item) getArguments().getSerializable(ARG_ITEM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_item_details_layout, container, false);

        ImageView itemImageView = view.findViewById(R.id.itemImageView);
        TextView descriptionTextView = view.findViewById(R.id.DescriptionTextView);
        TextView colorTextView = view.findViewById(R.id.colorTextView);
        TextView genderTextView = view.findViewById(R.id.genderTextView);
        TextView conditionTextView = view.findViewById(R.id.conditionTextView);
        TextView typeTextView = view.findViewById(R.id.typeTextView);
        TextView sizeTextView = view.findViewById(R.id.sizeTextView);
        Button editButton = view.findViewById(R.id.edit_item_details);
        ImageView backButton = view.findViewById(R.id.backButton);

        Uri itemUri = Uri.parse(item.getImageUri());
        Glide.with(this).load(itemUri).into(itemImageView);
        descriptionTextView.setText(item.getItemDescription());
        colorTextView.setText(item.getColor());
        genderTextView.setText(item.getGender());
        conditionTextView.setText(item.getItemCondition());
        typeTextView.setText(item.getItemType());
        sizeTextView.setText(item.getSize());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditItemFragment editItemFragment = EditItemFragment.newInstance(item);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame, editItemFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        return view;
    }
}
