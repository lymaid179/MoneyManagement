package com.example.moneymanagement.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneymanagement.CategoryFormActivity;
import com.example.moneymanagement.R;
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

public class FragmentCategory extends Fragment implements CategoryOnItemSelected {
    ListView listView;
    List<Category> categoryList = new ArrayList<>();
    TabLayout tabLayout;
    CategoryItemAdapter adapter;
    ImageButton addButton;
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, container, false);

        listView = v.findViewById(R.id.listView);
        tabLayout = v.findViewById(R.id.tabLayout);
        addButton = v.findViewById(R.id.addButton);

        //AddButton
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CategoryFormActivity.class);
                startActivity(intent);
            }
        });

        //ListView
        adapter = new CategoryItemAdapter(getContext(), categoryList, this);
        listView.setAdapter(adapter);

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

//        getCategoryByKind(tabLayout.getSelectedTabPosition());
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Firebase to get category by kind
        getCategoryByKind(tabLayout.getSelectedTabPosition());
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
        Intent intent = new Intent(getContext(), CategoryFormActivity.class);
        Category category = categoryList.get(position);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
