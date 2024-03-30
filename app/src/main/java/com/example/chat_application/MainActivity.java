package com.example.chat_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottom_nav;
    ImageButton search;
    Chat_Fragment chat_fragment;
    Profile_Fragment profile_fragment;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chat_fragment = new Chat_Fragment();
        profile_fragment = new Profile_Fragment();

        bottom_nav = findViewById(R.id.bottom_nav);
        search = findViewById(R.id.search);


        search.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Search_user.class));
        });


        bottom_nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menu_chat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, chat_fragment).commit();
                }
                if(item.getItemId() == R.id.menu_user){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, profile_fragment).commit();
                }
                return true;
            }
        });
        bottom_nav.setSelectedItemId(R.id.menu_chat);
    }
}