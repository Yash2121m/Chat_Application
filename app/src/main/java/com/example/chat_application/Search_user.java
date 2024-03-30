package com.example.chat_application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.chat_application.adapter.Search_recy_adapter;
import com.example.chat_application.model.User_model;
import com.example.chat_application.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class Search_user extends AppCompatActivity {

    EditText search_username;
    ImageButton search_user;
    ImageButton back;
    RecyclerView recyclerView;
    Search_recy_adapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        search_username = findViewById(R.id.search_username);
        search_user = findViewById(R.id.search_user);
        back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recyclerview);

        search_username.requestFocus();

        back.setOnClickListener(v -> {
            onBackPressed();
        });

        search_user.setOnClickListener(v -> {
            String search_term = search_username.getText().toString();
            if(search_term.isEmpty() || search_term.length()<3){
                search_username.setError("Invalid Username");
                return;
            }
            setup_search_recyclerview(search_term);
        });
    }

    void setup_search_recyclerview(String search_term){

        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username",search_term);

        FirestoreRecyclerOptions<User_model> options = new FirestoreRecyclerOptions.Builder<User_model>()
                .setQuery(query, User_model.class).build();

        adapter = new Search_recy_adapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.startListening();
    }
}