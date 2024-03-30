package com.example.chat_application;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chat_application.model.User_model;
import com.example.chat_application.utils.AndroidUtil;
import com.example.chat_application.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Profile_Fragment extends Fragment {

    ImageView profile_img;
    EditText username;
    EditText phn;
    Button profile_update_btn;
    ProgressBar pg_4;
    TextView logout;
    User_model current_user_model;
    ActivityResultLauncher<Intent> image_pick_launcher;
    Uri select_img_uri;

    public Profile_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image_pick_launcher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result ->{
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data != null && data.getData()!=null){
                            select_img_uri = data.getData();
                            AndroidUtil.set_profile_pic(getContext(),select_img_uri,profile_img);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_, container, false);
        profile_img = view.findViewById(R.id.profile_img);
        username = view.findViewById(R.id.username);
        phn = view.findViewById(R.id.phn);
        profile_update_btn = view.findViewById(R.id.profile_update_btn);
        pg_4 = view.findViewById(R.id.pg_4);
        logout = view.findViewById(R.id.logout);

        getUserData();

        profile_update_btn.setOnClickListener(v -> {
            updateBtnClick();
        });

        logout.setOnClickListener(v -> {
            FirebaseUtil.logout();
            Intent intent = new Intent(getContext(), Splash_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        profile_img.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            image_pick_launcher.launch(intent);
                            return null;
                        }
                    });
        });

        return view;
    }

    void updateBtnClick(){
        String new_username= username.getText().toString();
        if(new_username.isEmpty() || username.length()<3){
            username.setError("Username length should be at least 3 chars");
            return;
        }

        current_user_model.setUsername(new_username);
        set_in_progress(true);
        if(select_img_uri!=null){
            FirebaseUtil.get_current_profile_pic_storage().putFile(select_img_uri)
                    .addOnCompleteListener(task -> {
                        update_to_firestore();
                    });
        }
        else{
            update_to_firestore();
        }

    }

    void update_to_firestore(){
        FirebaseUtil.current_user_details().set(current_user_model)
                .addOnCompleteListener(task -> {
                    set_in_progress(false);
                    if(task.isSuccessful()){
                        AndroidUtil.showToast(getContext(),"Updated Successfully");
                    }
                    else{
                        AndroidUtil.showToast(getContext(),"Updated Failed");
                    }
                });
    }

    void getUserData(){
        set_in_progress(true);
        FirebaseUtil.get_current_profile_pic_storage().getDownloadUrl()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Uri uri = task.getResult();
                                AndroidUtil.set_profile_pic(getContext(), uri, profile_img);
                            }
                        });
        FirebaseUtil.current_user_details().get().addOnCompleteListener(task -> {
            set_in_progress(false);
            current_user_model = task.getResult().toObject(User_model.class);
            username.setText(current_user_model.getUsername());
            phn.setText(current_user_model.getPhone());
        });
    }

    void set_in_progress(boolean inProgress){
        if(inProgress){
            pg_4.setVisibility(View.VISIBLE);
            profile_update_btn.setVisibility(View.GONE);
        }
        else{
            pg_4.setVisibility(View.GONE);
            profile_update_btn.setVisibility(View.VISIBLE);
        }
    }
}