package com.example.moneymanagement.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneymanagement.LoginActivity;
import com.example.moneymanagement.R;
import com.example.moneymanagement.TransactionFormActivity;
import com.example.moneymanagement.adapter.TransactionDateItemAdapter;
import com.example.moneymanagement.models.Category;
import com.example.moneymanagement.models.Transaction;
import com.example.moneymanagement.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class FragmentHome extends Fragment {
    FloatingActionButton floatingActionButton;
    ImageButton logOutButton;
    ListView listView;
    TextView expenseTextView, incomeTextView, welcomeTextView;
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
        welcomeTextView = v.findViewById(R.id.welcomeTextView);
        expenseTextView = v.findViewById(R.id.expenseTextView);
        incomeTextView = v.findViewById(R.id.incomeTextView);

        getUser();

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
                List<Category> categoryList = (List<Category>) getActivity().getIntent().getSerializableExtra("categoryList");

                if (categoryList.isEmpty()) {
                    Toast.makeText(getContext(), "You don't have any category", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getContext(), TransactionFormActivity.class);
                    int lastIndex = -1;
                    if (!transactionList.isEmpty()) {
                        lastIndex = transactionList.get(transactionList.size()-1).getId();
                    }

                    intent.putExtra("lastIndex", lastIndex);
                    intent.putExtra("category", categoryList.get(0));
                    startActivity(intent);
                }

            }
        });

        getAllTransaction();

        return v;
    }

    private void getAllTransaction(){
        Query transactionListRef = db.collection("user")
                .document(""+user.getEmail())
                .collection("transaction")
                .orderBy("id");
        transactionListRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    transactionList.clear();
                    timestampList.clear();
                    int expense = 0, income = 0;

                    for (DocumentSnapshot document : snapshots.getDocuments()) {
                        Transaction transaction = document.toObject(Transaction.class);
                        transactionList.add(transaction);

                        //Calculate amount
                        if (transaction.getCategory().getKind() == 0) {
                            expense += transaction.getAmount();
                        } else {
                            income += transaction.getAmount();
                        }

                        //Set timestamp
                        Timestamp currentTimeStamp = transaction.getDate();
                        if (!timestampList.stream().anyMatch(new Predicate<Timestamp>() {
                            @Override
                            public boolean test(Timestamp timestamp) {
                                if (dateFormat.format(timestamp.toDate()).equals(dateFormat.format(currentTimeStamp.toDate()))) {
                                    return true;
                                }
                                return false;
                            }
                        })){
                            timestampList.add(currentTimeStamp);
                        }
                        timestampList.sort(new Comparator<Timestamp>() {
                            @Override
                            public int compare(Timestamp o1, Timestamp o2) {
                                return o1.compareTo(o2);
                            }
                        });
                    }

                    configAmountText(expense, income);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void getUser(){
        db.collection("user")
                .document(""+user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        System.out.println(task.getResult().getData());
                        User user = task.getResult().toObject(User.class);
                        welcomeTextView.setText("Welcome "+user.getName());
                    }
                });
    }

    private void configAmountText(long expense, long income){
        expenseTextView.setText("-"+expense);
        incomeTextView.setText("+"+income);
    }
}
