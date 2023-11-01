package com.example.moneymanagement.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneymanagement.LoginActivity;
import com.example.moneymanagement.MainActivity;
import com.example.moneymanagement.R;
import com.example.moneymanagement.TransactionFormActivity;
import com.example.moneymanagement.adapter.TransactionDateItemAdapter;
import com.example.moneymanagement.adapter.TransactionItemAdapter;
import com.example.moneymanagement.models.Category;
import com.example.moneymanagement.models.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {
    FloatingActionButton floatingActionButton;
    ImageButton logOutButton;
    ListView listView;
    List<Transaction> transactionList = new ArrayList<>();
    List<Timestamp> timestampList = new ArrayList<>();
    TransactionDateItemAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmet_home, container, false);

        logOutButton = v.findViewById(R.id.logOutButton);
        floatingActionButton = v.findViewById(R.id.floatingActionButton);
        listView = v.findViewById(R.id.listView);

        //Logout Button
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //ListView
        adapter = new TransactionDateItemAdapter(getContext(), timestampList, transactionList);
        listView.setAdapter(adapter);

        //FloatingActionButton
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set transaction default cate is first cate
                db.collection("user")
                        .document(""+user.getEmail())
                        .collection("category")
                        .orderBy("id")
                        .limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Category category = document.toObject(Category.class);

                                        Intent intent = new Intent(getContext(), TransactionFormActivity.class);
                                        intent.putExtra("category", category);
                                        startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(getContext(), "You don't have any category", Toast.LENGTH_SHORT);
                                }
                            }
                        });
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get transaction to config ListView
        getAllTransaction();
    }

    private void getAllTransaction(){
        transactionList.clear();
        timestampList.clear();
        db.collection("user")
                .document(""+user.getEmail())
                .collection("transaction")
                .orderBy("date")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Transaction transaction = document.toObject(Transaction.class);
                                transactionList.add(transaction);

                                Timestamp currentTimeStamp = transaction.getDate();
                                if (timestampList.size() > 0) {
                                    Timestamp lastTimeStamp = timestampList.get(timestampList.size() - 1);

                                    if (!dateFormat.format(lastTimeStamp.toDate()).equals(dateFormat.format(currentTimeStamp.toDate()))) {
                                        timestampList.add(currentTimeStamp);
                                    }
                                } else {
                                    timestampList.add(currentTimeStamp);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
