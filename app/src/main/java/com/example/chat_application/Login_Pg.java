package com.example.chat_application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class Login_Pg extends AppCompatActivity {

    CountryCodePicker country_code;
    EditText mb_num;
    Button Send_otp;
    ProgressBar pg_1;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pg);

        country_code = findViewById(R.id.country_code);
        mb_num = findViewById(R.id.mb_num);
        Send_otp = findViewById(R.id.otp_btn);
        pg_1 = findViewById(R.id.pg_1);

        pg_1.setVisibility(View.GONE);

        country_code.registerCarrierNumberEditText(mb_num);

        Send_otp.setOnClickListener((v -> {
            if(!country_code.isValidFullNumber()){
                mb_num.setError("Phone Number Is Not Valid");
                return;
            }
            Intent intent = new Intent(Login_Pg.this, Login_otp.class);
            intent.putExtra("phone", country_code.getFullNumberWithPlus());
            startActivity(intent);
        }));
    }
}