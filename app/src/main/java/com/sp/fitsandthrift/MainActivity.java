package com.sp.fitsandthrift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sp.fitsandthrift.Firebase.Util;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frame;
    private FirebaseAuth auth;
    private FirebaseUser user;

    Home_Fragment homeFragment;
    about_fragment aboutFragment;
    me_fragment meFragment;

    ChatFragment chatFragment;
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

        homeFragment = new Home_Fragment();
        aboutFragment = new about_fragment();
        meFragment = new me_fragment();
        notificationFragment = new notification_fragment();
        reviewFragment = new review_fragment();
        tradeFragment = new Trade_fragment();
        chatFragment = new ChatFragment();
        meFragment= new me_fragment();

        bottomNavigationView = findViewById(R.id.nav);

        frame = findViewById(R.id.frame);

        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Handle Intent extras to load specific fragments
        boolean loadMeFragment = getIntent().getBooleanExtra("loadMeFragment", false);
        boolean loadChatFragment = getIntent().getBooleanExtra("loadChatFragment", false);

        if (loadMeFragment) {
            bottomNavigationView.setSelectedItemId(R.id.me);
            loadFragment(meFragment, true);
        } else if (loadChatFragment) {
            bottomNavigationView.setSelectedItemId(R.id.chat);
            loadFragment(chatFragment, true);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.home);
            loadFragment(homeFragment, true);
        }

        bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.home1);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                resetIcons();

                if (itemId == R.id.home) {
                    loadFragment(homeFragment, false);
                    item.setIcon(R.drawable.home1); // Change to selected icon
                } else if (itemId == R.id.trade) {
                    loadFragment(tradeFragment, false);
                    item.setIcon(R.drawable.activetrade); // Change to selected icon
                } else if (itemId == R.id.noti) {
                    loadFragment(notificationFragment, false);
                    item.setIcon(R.drawable.bell); // Change to selected icon
                } else if (itemId == R.id.chat) {
                    loadFragment(chatFragment, false);
                    item.setIcon(R.drawable.cmt); // Change to selected icon
                } else if (itemId == R.id.me) {
                    if (user != null) {
                        String email = user.getEmail();
                        me_fragment meFragment = me_fragment.newInstance(email);
                        loadFragment(meFragment, false);
                        item.setIcon(R.drawable.activeprofile);
                    }
                }

                return true;
            }
        });

        getFCMToken();
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
        MenuItem chatItem = bottomNavigationView.getMenu().findItem(R.id.chat);

        homeItem.setIcon(R.drawable.home); // Default icon for home
        tradeItem.setIcon(R.drawable.trade); // Default icon for trade
        notiItem.setIcon(R.drawable.noti); // Default icon for notification
        meItem.setIcon(R.drawable.profile1); // Default icon for me
        chatItem.setIcon(R.drawable.chat); // Default icon for chat

    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.i("My token", token);
                if (user != null) {
                    Util.currentUserDetails().update("fcmToken", token);
                } else {
                    Log.w("MainActivity", "User is null, cannot update FCM token");
                }
            }
        });
    }

}
