package com.sp.fitsandthrift;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EditItemFragment extends Fragment {

    private static final String ARG_ITEM = "item";

    private Item item;

    private EditText descriptionEditText;
    private EditText colorEditText;
    private EditText sizeEditText;
    private Spinner conditionSpinner;
    private Spinner genderSpinner;
    private Spinner typeSpinner;
    private Button updateButton;

    public static EditItemFragment newInstance(Item item) {
        EditItemFragment fragment = new EditItemFragment();
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
        View view = inflater.inflate(R.layout.fragment_edit_item_layout, container, false);

        descriptionEditText = view.findViewById(R.id.itemDescription);
        colorEditText = view.findViewById(R.id.color);
        sizeEditText = view.findViewById(R.id.size);
        conditionSpinner = view.findViewById(R.id.itemCondition);
        genderSpinner = view.findViewById(R.id.itemGender);
        typeSpinner = view.findViewById(R.id.itemType);
        updateButton = view.findViewById(R.id.update_item);

        initializeSpinners();

        // Set the initial values
        if (item != null) {
            descriptionEditText.setText(item.getItemDescription());
            colorEditText.setText(item.getColor());
            sizeEditText.setText(item.getSize());
            setSpinnerValue(conditionSpinner, item.getItemCondition());
            setSpinnerValue(genderSpinner, item.getGender());
            setSpinnerValue(typeSpinner, item.getItemType());
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
            }
        });

        return view;
    }

    private void initializeSpinners() {
        // Initialize condition spinner
        ArrayList<String> itemConditionList = new ArrayList<>();
        itemConditionList.add("New");
        itemConditionList.add("Once wear");
        itemConditionList.add("Twice wear");
        ArrayAdapter<String> itemConditionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, itemConditionList);
        itemConditionAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        conditionSpinner.setAdapter(itemConditionAdapter);

        // Initialize gender spinner
        ArrayList<String> itemGenderList = new ArrayList<>();
        itemGenderList.add("Male");
        itemGenderList.add("Female");
        ArrayAdapter<String> itemGenderAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, itemGenderList);
        itemGenderAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        genderSpinner.setAdapter(itemGenderAdapter);

        // Initialize type spinner
        ArrayList<String> itemTypeList = new ArrayList<>();
        itemTypeList.add("Clothing");
        itemTypeList.add("Footwear");
        ArrayAdapter<String> itemTypeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, itemTypeList);
        itemTypeAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        typeSpinner.setAdapter(itemTypeAdapter);
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).equals(value)) {
                spinner.setSelection(position);
                return;
            }
        }
    }

    private void updateItem() {
        String description = descriptionEditText.getText().toString();
        String color = colorEditText.getText().toString();
        String size = sizeEditText.getText().toString();
        String condition = conditionSpinner.getSelectedItem().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        String type = typeSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(description) || TextUtils.isEmpty(color) || TextUtils.isEmpty(size)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        item.setItemDescription(description);
        item.setColor(color);
        item.setSize(size);
        item.setItemCondition(condition);
        item.setGender(gender);
        item.setItemType(type);

        db.collection("items").document(item.getItemID()).set(item)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Item updated", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
