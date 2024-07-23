package com.sp.fitsandthrift;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;


public class uploadItem extends AppCompatActivity {

    private EditText brandname;
    private EditText color;
    private EditText size;
    private EditText condition;
    private Button btnupload;
    private ImageButton imgbutton;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadpage);

        brandname = findViewById(R.id.brandname);
        size = findViewById(R.id.size);
        color = findViewById(R.id.color);
        condition = findViewById(R.id.condition);

        btnupload = findViewById(R.id.upload);
        btnupload.setOnClickListener(onSave);

        imgbutton = (ImageButton)findViewById(R.id.imgbutton);
        imgbutton.setOnClickListener(onUpload);


    }

    private View.OnClickListener onSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String brandnamestr = brandname.getText().toString();
            String sizestr = size.getText().toString();
            String colorstr = color.getText().toString();
            String conditionstr = condition.getText().toString();

            String combinestr = brandnamestr + "\n" + sizestr + "\n" + colorstr + "\n" + conditionstr;
            if(brandnamestr.isEmpty() || sizestr.isEmpty() || colorstr.isEmpty() || conditionstr.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill up the details", Toast.LENGTH_LONG).show();
            } else if(imageUri==null){
                Toast.makeText(getApplicationContext(),"Please upload the photo from gallery", Toast.LENGTH_LONG).show();
            }
            else{
                Trade_fragment tradeFragment = new Trade_fragment();
                Bundle args = new Bundle();
                args.putString("itemdesc", brandnamestr);
                args.putString("imageUri", imageUri.toString());
                tradeFragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, tradeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }


        }
    };

    private  View.OnClickListener onUpload = new View.OnClickListener() {
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
                imageUri = data.getData();
                imgbutton.setImageURI(imageUri);
            }
        }
    }


    public static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        private List<Item> items;
        private Context context;

        public ItemAdapter(Context context, List<Item> items) {
            this.context = context;
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Item item = items.get(position);
            holder.itemdesc.setText(item.getItemdesc());
            holder.imageUri.setImageURI(item.getImageUri());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageUri;
            public TextView itemdesc;

            public ViewHolder(View itemView) {
                super(itemView);
                imageUri = itemView.findViewById(R.id.imageUri);
                itemdesc = itemView.findViewById(R.id.itemdesc);
            }

        }
    }
}