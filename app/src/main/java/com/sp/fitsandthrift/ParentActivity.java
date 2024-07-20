package com.sp.fitsandthrift;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        // Pass the userid to the fragment
        Intent intent = getIntent();
        String userid = intent.getStringExtra("userid");

        Bundle bundle = new Bundle();
        bundle.putString("userid", userid);

        Fragment meFragment = new me_fragment();
        meFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, meFragment)
                .commit();
    }
}
