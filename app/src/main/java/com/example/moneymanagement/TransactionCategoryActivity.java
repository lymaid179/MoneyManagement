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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TransactionCategoryActivity extends AppCompatActivity implements CategoryOnItemSelected {
    View backgroundView;
    TabLayout tabLayout;
    ListView listView;
    CategoryItemAdapter adapter;
    List<Category> categoryList = new ArrayList<>();
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
        adapter = new CategoryItemAdapter(this, categoryList, this);
        listView.setAdapter(adapter);

        getCategoryByKind(0);

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

    private  void getCategoryByKind(int kind) {
        categoryList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("user")
                .document(""+user.getEmail())
                .collection("category")
                .whereEqualTo("kind", kind)
                .orderBy("id")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println(document.getData()+"");
                                Category category = document.toObject(Category.class);

                                categoryList.add(category);
                            }
                            adapter.notifyDataSetChanged();

                        }
                    }
                });
    }

    @Override
    public void onItemClick(View v, int position) {
        Category category = categoryList.get(position);

        setResult(Activity.RESULT_OK, getIntent().putExtra("category", category));
        finish();
    }
}
