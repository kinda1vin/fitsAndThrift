package com.sp.fitsandthrift;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class WomenFootwearFragment extends Fragment {
    private ImageView backImageView;
    private ImageView cartButton;
    private boolean isCartDisplayed = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_women_footwear, container, false);
        backImageView = rootView.findViewById(R.id.backIcon);
        cartButton = rootView.findViewById(R.id.cart);

        // Navigate to Back
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new Home_Fragment());
                fragmentTransaction.addToBackStack(null); // Add to back stack to allow back navigation
                fragmentTransaction.commit();
            }
        });

        //Navigate to Cart
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
        // Inflate the layout for this fragment
        return rootView;
    }
}
