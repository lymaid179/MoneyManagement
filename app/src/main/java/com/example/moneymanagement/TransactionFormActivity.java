package com.example.moneymanagement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneymanagement.models.Category;
import com.example.moneymanagement.models.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionFormActivity extends AppCompatActivity {
    EditText amountEditText, noteEditText;
    View dateView, categoryColorView;
    LinearLayout categoryView;
    ImageView backButton, deleteButton, categoryImageView;
    Button addButton, updateButton;
    TextView dateText, categoryName;
    Category selectedCategory;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_form);

        amountEditText = findViewById(R.id.editTextTextAmount);
        noteEditText = findViewById(R.id.editTextNote);
        dateView = findViewById(R.id.dateView);
        dateText = findViewById(R.id.textDate);
        backButton = findViewById(R.id.backButton);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        categoryView = findViewById(R.id.categoryView);
        categoryColorView = findViewById(R.id.colorView);
        categoryImageView = findViewById(R.id.categoryImageView);
        categoryName = findViewById(R.id.TextViewCateName);


        //Config add and update transaction
        final Date[] date = {new Date()};
        Intent intent = getIntent();
        Transaction selectedTransaction = (Transaction) intent.getParcelableExtra("transaction");
        selectedCategory = (Category) intent.getSerializableExtra("category");

        if (selectedTransaction == null) {
            updateButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
        } else {
            updateButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.GONE);

            date[0] = selectedTransaction.getDate().toDate();
            amountEditText.setText(""+selectedTransaction.getAmount());
            noteEditText.setText(selectedTransaction.getNote());
        }

        configCategoryView(selectedCategory);

        //Default focus to amountEditText
        amountEditText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_FORCED);

        //Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Choose Category
        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryIntent = new Intent(TransactionFormActivity.this, TransactionCategoryActivity.class);
                startActivityForResult(categoryIntent, 0);
            }
        });

        //DatePicker
        dateText.setText(""+dateFormat.format(date[0]));
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(date[0]);
                int cy = c.get(Calendar.YEAR);
                int cm = c.get(Calendar.MONTH);
                int cd = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(TransactionFormActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                                Calendar c = Calendar.getInstance();
                                c.set(y, m, d);
                                date[0] = c.getTime();

                                dateText.setText(""+dateFormat.format(date[0]));
                            }
                        }, cy, cm, cd);
                dialog.show();
            }
        });

        //Add Button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int id = getIntent().getIntExtra("lastIndex", -1) +1;
                    long amount = Long.parseLong(amountEditText.getText().toString());
                    String note = noteEditText.getText().toString();
                    Timestamp timestamp = new Timestamp(date[0]);

                    if (amount == 0) {
                        Toast.makeText(TransactionFormActivity.this, "Amount has to greater than 0", Toast.LENGTH_SHORT).show();
                    } else if (amount > Math.pow(10,12)) {
                        Toast.makeText(TransactionFormActivity.this, "Amount is too big", Toast.LENGTH_SHORT).show();
                    } else {
                        Transaction transaction = new Transaction(id, amount, note, timestamp, selectedCategory);
                        saveTransaction(transaction);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(TransactionFormActivity.this, "Missing information", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    long amount = Long.parseLong(amountEditText.getText().toString());
                    String note = noteEditText.getText().toString();
                    Timestamp timestamp = new Timestamp(date[0]);

                    if (amount == 0) {
                        Toast.makeText(TransactionFormActivity.this, "Amount has to greater than 0", Toast.LENGTH_SHORT).show();
                    } else if (amount > Math.pow(10,12)) {
                        Toast.makeText(TransactionFormActivity.this, "Amount is too big", Toast.LENGTH_SHORT).show();
                    } else {
                        selectedTransaction.setAmount(amount);
                        selectedTransaction.setDate(timestamp);
                        selectedTransaction.setNote(note);
                        selectedTransaction.setCategory(selectedCategory);
                        saveTransaction(selectedTransaction);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(TransactionFormActivity.this, "Missing information", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTransaction(selectedTransaction);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            selectedCategory = (Category) data.getSerializableExtra("category");
            configCategoryView(selectedCategory);
        }
    }

    private void saveTransaction(Transaction transaction){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("user")
                .document(""+user.getEmail())
                .collection("transaction")
                .document(""+transaction.getId())
                .set(transaction);

        finish();
    }

    private void deleteTransaction(Transaction transaction) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("user")
                .document(""+user.getEmail())
                .collection("transaction")
                .document(""+transaction.getId())
                .delete();
        finish();
    }

    private void configCategoryView(Category category) {
        String imageName = category.getCategoryImage().getImageName();
        int resID = getResources().getIdentifier(imageName , "drawable" , getPackageName());

        categoryImageView.setImageResource(resID);
        categoryColorView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(category.getImageColor())));
        categoryName.setText(category.getName());
    }

}
