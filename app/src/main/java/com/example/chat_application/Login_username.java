package com.example.chat_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chat_application.model.User_model;
import com.example.chat_application.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class Login_username extends AppCompatActivity {

    EditText username;
    Button login_btn;
    ProgressBar pg_3;
    String phn_num;

    User_model user_model;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        username = findViewById(R.id.username);
        login_btn = findViewById(R.id.login_btn);
        pg_3 = findViewById(R.id.pg_3);

        phn_num = getIntent().getExtras().getString("phone");
        getUsername();

        login_btn.setOnClickListener((v -> {
            setUsername();
        }));
    }

    void setUsername(){
        String entered_username= username.getText().toString();
        if(entered_username.isEmpty() || username.length()<3){
            username.setError("Username length should be at least 3 chars");
            return;
        }
        set_in_progress(true);
        if(user_model!=null){
            user_model.setUsername(entered_username);
        }
        else{
            user_model = new User_model(phn_num, entered_username, Timestamp.now(), FirebaseUtil.current_userid());
        }

        FirebaseUtil.current_user_details().set(user_model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                set_in_progress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(Login_username.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }
    void getUsername(){
        set_in_progress(true);
        FirebaseUtil.current_user_details().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                set_in_progress(false);
                if(task.isSuccessful()){
                    user_model = task.getResult().toObject(User_model.class);
                    if(user_model!= null){
                        username.setText(user_model.getUsername());
                    }
                }
            }
        });
    }

    void set_in_progress(boolean inProgress){
        if(inProgress){
            pg_3.setVisibility(View.VISIBLE);
            login_btn.setVisibility(View.GONE);
        }
        else{
            pg_3.setVisibility(View.GONE);
           login_btn.setVisibility(View.VISIBLE);
        }
    }
}