package com.example.chat_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.chat_application.utils.FirebaseUtil;

public class Splash_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseUtil.isLoogedIn()){
                    startActivity(new Intent(Splash_Activity.this, MainActivity.class));
                }
                else{
                    startActivity(new Intent(Splash_Activity.this, Login_Pg.class));
                }
                finish();
            }
        }, 1000);
    }
}