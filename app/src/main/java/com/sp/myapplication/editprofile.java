package com.sp.myapplication;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.github.dhaval2404.imagepicker.ImagePicker;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class editprofile extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextGmail;
    private EditText editTextGender;
    private EditText editTextph;
    private Button buttonUpdate;

    private ImageView pic;
    ActivityResultLauncher<Intent>imagePickLauncher;
    Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        pic= findViewById(R.id.pic);
        editTextName = findViewById(R.id.newname);
        editTextGmail = findViewById(R.id.newmail);
        editTextGender = findViewById(R.id.gender);
        editTextph = findViewById(R.id.newph);
        buttonUpdate = findViewById(R.id.save_btn);
        imagePickLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result ->{
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data= result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri=data.getData();
                            AndroidUtil.setProfilePic(this,selectedImageUri,pic);
                        }
                    }
                }
        );


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                updateProfile();
            }
        });
        pic.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512).createIntent(new Function1<Intent, Unit>(){
                @Override
                public Unit invoke(Intent intent) {
                    imagePickLauncher.launch(intent);
                    return null;
                }
            });
        });

    }

    private void updateProfile() {
        String name = editTextName.getText().toString().trim();
        String gmail = editTextGmail.getText().toString().trim();
        String gender = editTextGender.getText().toString().trim();
        String phone = editTextph.getText().toString().trim();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("name", name);
        resultIntent.putExtra("gmail", gmail);
        resultIntent.putExtra("gender", gender);
        resultIntent.putExtra("phone", phone);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}