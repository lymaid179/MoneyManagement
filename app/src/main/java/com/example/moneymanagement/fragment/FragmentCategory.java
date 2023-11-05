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
import com.example.moneymanagement.models.Transaction;
import com.example.moneymanagement.support.CategoryOnItemSelected;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FragmentCategory extends Fragment implements CategoryOnItemSelected {
    ListView listView;
    List<Category> categoryList = new ArrayList<>();
    List<Category> adapterCategoryList = new ArrayList<>();
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
                //Put id to add
                int lastIndex = -1;
                if (!categoryList.isEmpty()) {
                    lastIndex = categoryList.get(categoryList.size()-1).getId();
                }
                intent.putExtra("lastIndex", lastIndex);
                startActivity(intent);
            }
        });

        //ListView
        adapter = new CategoryItemAdapter(getContext(), adapterCategoryList, this);
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

        getAllCategory();
        return v;
    }

    private void getAllCategory(){
        categoryList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query categoryListRef = db.collection("user")
                .document(""+user.getEmail())
                .collection("category")
                .orderBy("id");
        categoryListRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                System.out.println("ADDED");
                                Category categoryAdd = dc.getDocument().toObject(Category.class);
                                categoryList.add(categoryAdd);
                                break;
                            case MODIFIED:
                                System.out.println("MODIFIED");
                                Category categoryUpdate = dc.getDocument().toObject(Category.class);

                                for (int i = 0; i<categoryList.size(); i++) {
                                    Category oldCategory = categoryList.get(i);
                                    if (oldCategory.getId() == categoryUpdate.getId()) {
                                        updateTransactionWithCate(oldCategory, categoryUpdate);

                                        System.out.println(i);
                                        categoryList.get(i).setName(categoryUpdate.getName());
                                        categoryList.get(i).setKind(categoryUpdate.getKind());
                                        categoryList.get(i).setImageColor(categoryUpdate.getImageColor());
                                        categoryList.get(i).setCategoryImage(categoryUpdate.getCategoryImage());

                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                System.out.println("REMOVED");
                                Category categoryRemove = dc.getDocument().toObject(Category.class);
                                categoryList.removeIf(new Predicate<Category>() {
                                    @Override
                                    public boolean test(Category category) {
                                        return category.getId() == categoryRemove.getId();
                                    }
                                });

                                //Remove transaction with that cate
                                removeTransactionWithCate(categoryRemove);
                                break;
                        }
                        getCategoryByKind(tabLayout.getSelectedTabPosition());

                        Intent intent = getActivity().getIntent();
                        intent.putExtra("categoryList", (Serializable) categoryList);
                    }
                }
            }
        });

    }

    private void updateTransactionWithCate(Category oldCategory, Category updateCategory) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("user")
                .document(""+user.getEmail())
                .collection("transaction")
                .whereEqualTo("category", oldCategory)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Transaction transaction = document.toObject(Transaction.class);
                                transaction.setCategory(updateCategory);

                                db.collection("user")
                                        .document(""+user.getEmail())
                                        .collection("transaction")
                                        .document(""+transaction.getId())
                                        .set(transaction);
                            }
                        }
                    }
                });
    }

    private void removeTransactionWithCate(Category category) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("user")
                .document(""+user.getEmail())
                .collection("transaction")
                .whereEqualTo("category", category)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Transaction transaction = document.toObject(Transaction.class);

                                db.collection("user")
                                        .document(""+user.getEmail())
                                        .collection("transaction")
                                        .document(""+transaction.getId())
                                        .delete();
                            }
                        }
                    }
                });
    }

    private  void getCategoryByKind(int kind) {
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
        Intent intent = new Intent(getContext(), CategoryFormActivity.class);
        Category category = adapterCategoryList.get(position);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
