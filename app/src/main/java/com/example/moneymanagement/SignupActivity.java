package com.example.moneymanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneymanagement.models.Category;
import com.example.moneymanagement.models.CategoryImage;
import com.example.moneymanagement.models.User;
import com.example.moneymanagement.support.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText emailTxt, passwordTxt, nameTxt, confirmPasswordTxt;
    ProgressBar progressBar;
    TextView loginTxt;
    Button signUpButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        emailTxt = findViewById(R.id.editTextTextEmailAddress);
        passwordTxt = findViewById(R.id.editTextTextPassword);
        nameTxt = findViewById(R.id.editTextTextName);
        confirmPasswordTxt = findViewById(R.id.editTextTextConfirmPassword);
        progressBar = findViewById(R.id.progressBar);
        loginTxt = findViewById(R.id.textViewLogin);
        signUpButton = findViewById(R.id.signupButton);

        progressBar.setVisibility(View.GONE);

        //Firebase
        mAuth = FirebaseAuth.getInstance();

        //Login
        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });

        //Signup
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String name = String.valueOf(nameTxt.getText());
                String email = String.valueOf(emailTxt.getText());
                String password = String.valueOf(passwordTxt.getText());
                String confirmPassword = String.valueOf(confirmPasswordTxt.getText());

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty()){
                    Toast.makeText(SignupActivity.this, "Missing Information", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignupActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    //Upload user to firebase
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    User user = new User(name, email);
                                    db.collection("user").document(email).set(user);

                                    //Add default cate to user
                                    addDefaultCategory(user);

                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignupActivity.this, "" + task.getException().getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }


    private void addDefaultCategory(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (int i = 0; i< Constant.cateName.length; i++) {
            int index = i;
            int imageId = Constant.imageId[index];

            //Add default cate
            CategoryImage categoryImage = new CategoryImage(imageId, Constant.imageName[imageId]);
            Category category = new Category(index, Constant.cateName[index], Constant.kind[index], Constant.imageColor[index], categoryImage);

            db.collection("user")
                    .document(""+user.getEmail())
                    .collection("category")
                    .document(""+ index)
                    .set(category);

            }
    }
}
