package com.sp.fitsandthrift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

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

    // Constants for fragment tags
    private static final String HOME_FRAGMENT_TAG = "HOME_FRAGMENT";
    private static final String TRADE_FRAGMENT_TAG = "TRADE_FRAGMENT";
    private static final String NOTI_FRAGMENT_TAG = "NOTI_FRAGMENT";
    private static final String CHAT_FRAGMENT_TAG = "CHAT_FRAGMENT";
    private static final String ME_FRAGMENT_TAG = "ME_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            navigateToLogin();
            return; // Ensure to return here to prevent further execution
        }

        homeFragment = new Home_Fragment();
        aboutFragment = new about_fragment();
        meFragment = new me_fragment();
        notificationFragment = new notification_fragment();
        reviewFragment = new review_fragment();
        tradeFragment = new Trade_fragment();
        chatFragment = new ChatFragment();

        bottomNavigationView = findViewById(R.id.nav);
        frame = findViewById(R.id.frame);

        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Set default fragment
        if (getIntent().getBooleanExtra("loadChatFragment", false)) {
            bottomNavigationView.setSelectedItemId(R.id.chat);
            loadFragment(chatFragment, CHAT_FRAGMENT_TAG, false);
        } else {
            loadFragment(homeFragment, HOME_FRAGMENT_TAG, false);
        }

        // Default icon for home
        bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.home1);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            resetIcons();

            if (itemId == R.id.home) {
                loadFragment(homeFragment, HOME_FRAGMENT_TAG, false);
                item.setIcon(R.drawable.home1);
            } else if (itemId == R.id.trade) {
                loadFragment(tradeFragment, TRADE_FRAGMENT_TAG, false);
                item.setIcon(R.drawable.activetrade);
            } else if (itemId == R.id.noti) {
                loadFragment(notificationFragment, NOTI_FRAGMENT_TAG, false);
                item.setIcon(R.drawable.bell);
            } else if (itemId == R.id.chat) {
                loadFragment(chatFragment, CHAT_FRAGMENT_TAG, false);
                item.setIcon(R.drawable.cmt);
            } else if (itemId == R.id.me) {
                if (user != null) {
                    String email = user.getEmail();
                    meFragment = me_fragment.newInstance(email);
                    loadFragment(meFragment, ME_FRAGMENT_TAG, false);
                    item.setIcon(R.drawable.activeprofile);
                }
            }

            return true;
        });

        getFCMToken();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set the chat icon when coming back from ChatActivity
        if (getIntent().getBooleanExtra("loadChatFragment", false)) {
            bottomNavigationView.setSelectedItemId(R.id.chat);
            loadFragment(chatFragment, CHAT_FRAGMENT_TAG, false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    private void loadFragment(Fragment fragment, String tag, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);

        if (existingFragment == null) {
            fragmentTransaction.replace(R.id.frame, fragment, tag);
            if (addToBackStack) {
                fragmentTransaction.addToBackStack(tag);
            }
        } else {
            fragmentTransaction.show(existingFragment);
        }

        fragmentTransaction.commit();
    }

    private void resetIcons() {
        MenuItem homeItem = bottomNavigationView.getMenu().findItem(R.id.home);
        MenuItem tradeItem = bottomNavigationView.getMenu().findItem(R.id.trade);
        MenuItem notiItem = bottomNavigationView.getMenu().findItem(R.id.noti);
        MenuItem meItem = bottomNavigationView.getMenu().findItem(R.id.me);
        MenuItem chatItem = bottomNavigationView.getMenu().findItem(R.id.chat);

        homeItem.setIcon(R.drawable.home);
        tradeItem.setIcon(R.drawable.trade);
        notiItem.setIcon(R.drawable.noti);
        meItem.setIcon(R.drawable.profile1);
        chatItem.setIcon(R.drawable.chat);
    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.i("My token", token);
            }
        });
    }
}
