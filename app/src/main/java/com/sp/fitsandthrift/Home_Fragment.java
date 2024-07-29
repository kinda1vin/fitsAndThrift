package com.sp.fitsandthrift;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class Home_Fragment extends Fragment {

    private TextView womenTextView;
    private TextView menTextView;
    private Button clothingButton;
    private Button footwearButton;
    private ImageView cartButton;
    private boolean isWomenSelected = false;
    private boolean isMenSelected = false;
    private boolean isCartDisplayed = true;
    private ClothingFragment clothingFragment;
    private FootWearFragment footWearFragment;
    private String category = "ALL";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_, container, false);

        womenTextView = rootView.findViewById(R.id.women);
        menTextView = rootView.findViewById(R.id.men);
        clothingButton = rootView.findViewById(R.id.clothing);
        footwearButton = rootView.findViewById(R.id.footwear);
        cartButton = rootView.findViewById(R.id.cart);

        clothingFragment = new ClothingFragment();// initialize the clothing fragment
        footWearFragment = new FootWearFragment();// initialize the footwear fragment


        womenTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if women is selected
                isWomenSelected = true;
                isMenSelected = false;
                womenTextView.setPaintFlags(womenTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                menTextView.setPaintFlags(menTextView.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            }
        });


        menTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if men is selected
                isMenSelected = true;
                isWomenSelected = false;
                menTextView.setPaintFlags((womenTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG));
                womenTextView.setPaintFlags(womenTextView.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            }
        });

        //Navigate to clothing activities
        clothingButton.setOnClickListener(v -> {
            if (isWomenSelected) {
                category = "Female";
            } else if (isMenSelected) {
                category = "Male";
            } else {
                category = "All";
            }

            ClothingFragment fragment = new ClothingFragment();
            Bundle args = new Bundle();
            args.putString("category", category);
            fragment.setArguments(args);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Navigate to Footwear activities
        footwearButton.setOnClickListener(v -> {
            if (isWomenSelected) {
                category = "Female";
            } else if (isMenSelected) {
                category = "Male";
            } else {
                category = "All";
            }
            FootWearFragment fragment = new FootWearFragment();
            Bundle args = new Bundle();
            args.putString("category", category);
            fragment.setArguments(args);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        });

        // Navigate to cart
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCartDisplayed) {
                    // change icon
                    cartButton.setImageResource(R.drawable.cart_filled);
                } else {
                    cartButton.setImageResource(R.drawable.cart_notfilled);
                }
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new cartFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return rootView;
    }
}