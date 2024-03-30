package com.example.chat_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat_application.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Login_otp extends AppCompatActivity {

    EditText otp;
    Long timeout_sec = 60L;
    String verification_code;
    PhoneAuthProvider.ForceResendingToken resending_token;
    Button next_btn;
    ProgressBar pg_2;
    TextView resend_otp;
    String phn_num;
    FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        otp = findViewById(R.id.otp);
        next_btn = findViewById(R.id.next_btn);
        pg_2 = findViewById(R.id.pg_2);
        resend_otp = findViewById(R.id.resend_otp);

        phn_num = getIntent().getExtras().getString("phone");

        send_otp(phn_num, false);

        next_btn.setOnClickListener(v -> {
                String entered_otp = otp.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_code, entered_otp);
                signIn(credential);
                set_in_progress(true);
        });

        resend_otp.setOnClickListener((v)->{
            send_otp(phn_num, true);
        });
    }

    void send_otp(String phn_num, boolean isResend){
        start_resend_timer();
        set_in_progress(true);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(m_Auth)
                .setPhoneNumber(phn_num)
                .setTimeout(timeout_sec, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        set_in_progress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        AndroidUtil.showToast(getApplicationContext(),"OTP Verification Failed");
                        set_in_progress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verification_code = s;
                        resending_token = forceResendingToken;
                        AndroidUtil.showToast(getApplicationContext(),"OTP Sent Successfully");
                        set_in_progress(false);
                    }
                });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resending_token).build());
        }
        else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }
    void set_in_progress(boolean inProgress){
        if(inProgress){
            pg_2.setVisibility(View.VISIBLE);
            next_btn.setVisibility(View.GONE);
        }
        else{
            pg_2.setVisibility(View.GONE);
            next_btn.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        set_in_progress(true);
        m_Auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                set_in_progress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(Login_otp.this, Login_username.class);
                    intent.putExtra("phone", phn_num);
                    startActivity(intent);
                }
                else{
                    AndroidUtil.showToast(getApplicationContext(),"OTP Verification Failed");
                }
            }
        });
    }

    void start_resend_timer(){
        resend_otp.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeout_sec--;
                resend_otp.setText("Resend OTP in "+timeout_sec+" seconds");
                if(timeout_sec<=0){
                    timeout_sec=60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resend_otp.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);
    }
}