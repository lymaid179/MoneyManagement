package com.example.moneymanagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneymanagement.adapter.CategoryItemAdapter;
import com.example.moneymanagement.models.Category;
import com.example.moneymanagement.support.CategoryOnItemSelected;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TransactionCategoryActivity extends AppCompatActivity implements CategoryOnItemSelected {
    View backgroundView;
    TabLayout tabLayout;
    ListView listView;
    CategoryItemAdapter adapter;
    List<Category> categoryList = new ArrayList<>();
    List<Category> adapterCategoryList = new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_category);

        backgroundView = findViewById(R.id.backgroundView);
        tabLayout = findViewById(R.id.tabLayout);
        listView = findViewById(R.id.listView);

        //Click background to exit
        backgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //ListView
        adapter = new CategoryItemAdapter(this, adapterCategoryList, this);
        listView.setAdapter(adapter);

        getAllCategory();

        //TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Expense"));
        tabLayout.addTab(tabLayout.newTab().setText("Income"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getCategoryByKind(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void getAllCategory(){
        categoryList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query categoryListRef = db.collection("user")
                .document(""+user.getEmail())
                .collection("category")
                .orderBy("id");

        categoryListRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        System.out.println(document.getData()+"");
                        Category category = document.toObject(Category.class);

                        categoryList.add(category);
                    }
                    getCategoryByKind(tabLayout.getSelectedTabPosition());
                }
            }
        });


    }

    private void getCategoryByKind(int kind) {
        adapterCategoryList.clear();
        adapterCategoryList.addAll(categoryList.stream().filter(new Predicate<Category>() {
            @Override
            public boolean test(Category category) {
                return category.getKind() == kind;
            }
        }).collect(Collectors.toList()));
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(View v, int position) {
        Category category = adapterCategoryList.get(position);

        setResult(Activity.RESULT_OK, getIntent().putExtra("category", category));
        finish();
    }
}
