package com.example.moneymanagement;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanagement.adapter.CategoryImageItemAdapter;
import com.example.moneymanagement.models.Category;
import com.example.moneymanagement.models.CategoryImage;
import com.example.moneymanagement.support.CategoryImageOnItemSelected;
import com.example.moneymanagement.support.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CategoryFormActivity extends AppCompatActivity implements CategoryImageOnItemSelected {
    Button addButton, updateButton;
    ImageButton backButton, deleteButton;
    ImageView categoryImageView;
    View colorView;
    CardView selectColorView;
    RadioGroup radioGroup;
    RecyclerView recyclerView;
    EditText categoryNameEditText;
    String colorString = "#fff8b858";
    List<CategoryImage> categoryImageList = new ArrayList<>();
    CategoryImageItemAdapter adapter;
    Category selectedCategory;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_form);

        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.backButton);
        categoryNameEditText = findViewById(R.id.editTextTextCateName);
        categoryImageView = findViewById(R.id.categoryImageView);
        colorView = findViewById(R.id.colorView);
        selectColorView = findViewById(R.id.selectColorView);
        radioGroup = findViewById(R.id.kindRadioGroup);
        recyclerView = findViewById(R.id.recyclerView);

        //Config add and update
        selectedCategory = (Category) getIntent().getSerializableExtra("category");
        if (selectedCategory == null) {
            updateButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);

//            categoryImageList.get(0).setSelected(true);
        } else {
            updateButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.GONE);

            String imageName = selectedCategory.getCategoryImage().getImageName();
            int resID = getResources().getIdentifier(imageName , "drawable" , getPackageName());
            colorString = selectedCategory.getImageColor();

            categoryNameEditText.setText(selectedCategory.getName());
            categoryImageView.setImageResource(resID);
            colorView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorString)));
            switch (selectedCategory.getKind()) {
                case 0:
                    radioGroup.check(R.id.expenseRadioButton);
                    break;
                case 1:
                    radioGroup.check(R.id.incomeRadioButton);
            }

//            categoryImageList.get(selectedCategory.getCategoryImage().getId()).setSelected(true);
        }

        //RecycleView
        adapter = new CategoryImageItemAdapter(this, categoryImageList, colorString, this);
        GridLayoutManager manager = new GridLayoutManager(this, 6);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        //Firebase to get categoryImage
        getAllCategoryImage();

        //Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Set color picker to selectColorView
        selectColorView.setBackgroundColor(Color.parseColor(colorString));
        selectColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        //Add Button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = getIntent().getIntExtra("lastIndex", -1) + 1;
                String name = categoryNameEditText.getText().toString();
                int kind = 0;
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.expenseRadioButton:
                        kind = 0;
                        break;
                    case R.id.incomeRadioButton:
                        kind = 1;
                        break;
                }

                int categoryImageSelected = adapter.getSelected();
                CategoryImage categoryImage = categoryImageList.get(categoryImageSelected);

                if (name.isEmpty()) {
                    Toast.makeText(CategoryFormActivity.this, "Missing information", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Firebase add category
                Category newCategory = new Category(id, name, kind, colorString, categoryImage);
                saveCategory(newCategory);
            }
        });

        //Update Button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = categoryNameEditText.getText().toString();
                int kind = 0;
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.expenseRadioButton:
                        kind = 0;
                        break;
                    case R.id.incomeRadioButton:
                        kind = 1;
                        break;
                }

                int categoryImageSelected = adapter.getSelected();
                CategoryImage categoryImage = categoryImageList.get(categoryImageSelected);

                if (name.isEmpty()) {
                    Toast.makeText(CategoryFormActivity.this, "Missing information", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Firebase update category
                selectedCategory.setName(name);
                selectedCategory.setKind(kind);
                selectedCategory.setCategoryImage(categoryImage);
                selectedCategory.setImageColor(colorString);

                saveCategory(selectedCategory);
            }
        });

        //Delete Button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory(selectedCategory);
            }
        });

    }

    private void saveCategory(Category category){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("user")
                .document(""+user.getEmail())
                .collection("category")
                .document(""+category.getId())
                .set(category);

        finish();

    }

    private void deleteCategory(Category category) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("user")
                .document(""+user.getEmail())
                .collection("category")
                .document(""+category.getId())
                .delete();
        finish();
    }

    private void getAllCategoryImage() {
        for (int i = 0; i< Constant.imageName.length; i++){
            CategoryImage categoryImage = new CategoryImage(i, Constant.imageName[i]);
            categoryImageList.add(categoryImage);
        }

        if (selectedCategory!=null) {
            int selectedImage = selectedCategory.getCategoryImage().getId();
            categoryImageList.get(selectedImage).setSelected(true);
            adapter.setSelected(selectedImage);
        } else {
            //Set default select 0
            categoryImageList.get(0).setSelected(true);
        }
        adapter.notifyDataSetChanged();

    }

    private void openColorPicker(){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, Color.parseColor(colorString), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                colorString = "#"+Integer.toHexString(color).substring(2);
                selectColorView.setBackgroundColor(Color.parseColor(colorString));
                colorView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorString)));
                adapter.setColor(colorString);
                adapter.notifyDataSetChanged();
            }
        });
        colorPicker.show();
    }

    @Override
    public void onItemClick(View v, int position) {
        //Set category Image
        CategoryImage categoryImage = categoryImageList.get(position);

        String imageName = categoryImage.getImageName();
        int resID = getResources().getIdentifier(imageName , "drawable" , getPackageName());

        categoryImageView.setImageResource(resID);
    }
}
