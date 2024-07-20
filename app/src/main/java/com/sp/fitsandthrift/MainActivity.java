package com.sp.fitsandthrift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frame;
    private FirebaseAuth auth;
    private FirebaseUser user;

    Home_fragment homeFragment;
    about_fragment aboutFragment;
    me_fragment meFragment;
    notification_fragment notificationFragment;
    review_fragment reviewFragment;
    Trade_fragment tradeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        homeFragment = new Home_fragment();
        aboutFragment = new about_fragment();
        meFragment = new me_fragment();
        notificationFragment = new notification_fragment();
        reviewFragment = new review_fragment();
        tradeFragment = new Trade_fragment();

        bottomNavigationView = findViewById(R.id.nav);
        frame = findViewById(R.id.frame);

        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Set default fragment
        loadFragment(new Home_fragment(), true);

        bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.home1);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                resetIcons();

                if (itemId == R.id.home) {
                    loadFragment(new Home_fragment(), false);
                    item.setIcon(R.drawable.home1); // Change to selected icon
                } else if (itemId == R.id.trade) {
                    loadFragment(new Trade_fragment(), false);
                    item.setIcon(R.drawable.activetrade); // Change to selected icon
                } else if (itemId == R.id.noti) {
                    loadFragment(new notification_fragment(), false);
                    item.setIcon(R.drawable.bell); // Change to selected icon
                } else if (itemId == R.id.me) {
                    String email = user.getEmail();
                    me_fragment meFragment = me_fragment.newInstance("", email); // Pass the email
                    loadFragment(meFragment, false);
                    item.setIcon(R.drawable.activeprofile); // Change to selected icon
                }

                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check again if the user is signed in when the activity starts
        user = auth.getCurrentUser();
        if (user == null) {
            navigateToLogin();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frame, fragment);
        } else {
            fragmentTransaction.replace(R.id.frame, fragment);
        }
        fragmentTransaction.commit();
    }

    private void resetIcons() {
        MenuItem homeItem = bottomNavigationView.getMenu().findItem(R.id.home);
        MenuItem tradeItem = bottomNavigationView.getMenu().findItem(R.id.trade);
        MenuItem notiItem = bottomNavigationView.getMenu().findItem(R.id.noti);
        MenuItem meItem = bottomNavigationView.getMenu().findItem(R.id.me);

        homeItem.setIcon(R.drawable.home); // Default icon for home
        tradeItem.setIcon(R.drawable.trade); // Default icon for trade
        notiItem.setIcon(R.drawable.noti); // Default icon for notification
        meItem.setIcon(R.drawable.profile1); // Default icon for me
    }
}
