package com.sp.fitsandthrift;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class uploadItem extends AppCompatActivity {

    private EditText itemDescription;
    private EditText color;
    private EditText size;
    private Spinner itemCondition;
    private Spinner itemGender;
    private Spinner itemType;
    private Button btnupload;
    private ImageButton imgbutton;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private ImageView backIcon;
    FirebaseFirestore dbroot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadpage);

        itemDescription = findViewById(R.id.itemDescription);
        size = findViewById(R.id.size);
        color = findViewById(R.id.color);
        itemCondition = findViewById(R.id.itemCondition);
        itemGender = findViewById(R.id.itemGender);
        itemType = findViewById(R.id.itemType);

        dbroot = FirebaseFirestore.getInstance();

        btnupload = findViewById(R.id.upload);
        btnupload.setOnClickListener(onSave);

        imgbutton = findViewById(R.id.imgbutton);
        imgbutton.setOnClickListener(onUpload);

        backIcon = findViewById(R.id.backIcon);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        itemCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemCondition.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> itemConditionList = new ArrayList<>();
        itemConditionList.add("New");
        itemConditionList.add("Once wear");
        itemConditionList.add("Twice wear");
        ArrayAdapter<String> itemConditionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemConditionList);
        itemConditionAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        itemCondition.setAdapter(itemConditionAdapter);

        itemGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemGender.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayList<String> itemGenderList = new ArrayList<>();
        itemGenderList.add("Male");
        itemGenderList.add("Female");
        ArrayAdapter<String> itemGenderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemGenderList);
        itemGenderAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        itemGender.setAdapter(itemGenderAdapter);

        itemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemType.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayList<String> itemTypeList = new ArrayList<>();
        itemTypeList.add("Clothing");
        itemTypeList.add("Footwear");
        ArrayAdapter<String> itemTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemTypeList);
        itemTypeAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        itemType.setAdapter(itemTypeAdapter);
    }

    private View.OnClickListener onSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String brandnamestr = itemDescription.getText().toString();
            String sizestr = size.getText().toString();
            String colorstr = color.getText().toString();
            String conditionstr = itemCondition.getSelectedItem().toString();
            String genderstr = itemGender.getSelectedItem().toString();
            String itemtypestr = itemType.getSelectedItem().toString();

            if (TextUtils.isEmpty(brandnamestr) || TextUtils.isEmpty(sizestr) || TextUtils.isEmpty(colorstr) || TextUtils.isEmpty(conditionstr) || TextUtils.isEmpty(genderstr) || TextUtils.isEmpty(itemtypestr)) {
                Toast.makeText(getApplicationContext(), "Please fill up the details", Toast.LENGTH_LONG).show();
            } else if (imageUri == null) {
                Toast.makeText(getApplicationContext(), "Please upload the photo from gallery", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent();
                intent.putExtra("itemDescription", brandnamestr);
                intent.putExtra("imageUri", imageUri.toString());
                setResult(RESULT_OK, intent);
                insertdata();
                finish();
            }
        }
    };

    private View.OnClickListener onUpload = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            if (data != null) {
                Uri sourceUri = data.getData();
                try {
                    // Open a stream to read the data from the URI
                    InputStream is = getContentResolver().openInputStream(sourceUri);

                    // Create a new file in your app's private storage area
                    File outputDir = getApplicationContext().getCacheDir();
                    File outputFile = File.createTempFile("image", null, outputDir);

                    // Open a stream to write the data to the new file
                    OutputStream os = new FileOutputStream(outputFile);

                    // Copy the data
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }

                    // Close the streams
                    os.flush();
                    os.close();
                    is.close();

                    // Update the imageUri to point to the new file
                    imageUri = Uri.fromFile(outputFile);

                    // Load the image into the ImageButton
                    imgbutton.setImageURI(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.itemViewHolder>{
        Context context;
        ArrayList<Item> items;

        public ItemAdapter(Context context, ArrayList<Item> items) {
            this.context = context;
            this.items = items;
        }

        @NonNull
        @Override
        public ItemAdapter.itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent, false);
            return new itemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemAdapter.itemViewHolder holder, int position) {
            Item item = items.get(position);
            holder.itemDescription.setText(item.getItemDescription());
            Uri itemUri = item.getImageUriAsUri();
            if (itemUri != null) {
                Glide.with(context).load(itemUri).into(holder.imageUri);
            } else {
                // set a default image or clear the ImageView if the Uri is null
                holder.imageUri.setImageDrawable(null);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class itemViewHolder extends RecyclerView.ViewHolder{
            ImageView imageUri;
            TextView itemDescription;

            public itemViewHolder(@NonNull View itemView) {
                super(itemView);
                imageUri = itemView.findViewById(R.id.imageUri);
                itemDescription = itemView.findViewById(R.id.itemDescription);
            }
        }
    }

    public void insertdata() {
        // Create a storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to 'images/imageUri'
        StorageReference imageRef = storageRef.child("images/" + imageUri.getLastPathSegment());

        // Upload the image
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // Get the download URL and store it in Firestore
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    // Get the current user's ID
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // Create a new document and get its ID
                    DocumentReference newItemRef = dbroot.collection("items").document();
                    String itemID = newItemRef.getId();

                    // Store the download URL in Firestore
                    Map<String, Object> items = new HashMap<>();
                    items.put("itemDescription", itemDescription.getText().toString().trim());
                    items.put("color", color.getText().toString().trim());
                    items.put("size", size.getText().toString().trim());
                    items.put("gender", itemGender.getSelectedItem().toString());
                    items.put("itemCondition", itemCondition.getSelectedItem().toString());
                    items.put("itemType", itemType.getSelectedItem().toString());
                    items.put("imageUri", downloadUri.toString());
                    items.put("userID", userID); // Add the userID to the item
                    items.put("itemID", itemID);
                    items.put("trade_status", false); // Set trade_status to false by default

                    newItemRef.set(items)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    itemDescription.setText("");
                                    color.setText("");
                                    size.setText("");
                                    itemGender.setSelection(0);
                                    itemCondition.setSelection(0);
                                    itemType.setSelection(0);

                                    SharedPreferences sharedPreferences = getSharedPreferences("imageUridatashare", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("imageUri", downloadUri.toString());
                                    editor.apply();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
}
