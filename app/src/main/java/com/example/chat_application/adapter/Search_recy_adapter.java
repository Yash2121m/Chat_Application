package com.example.chat_application.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_application.Chat_Activity;
import com.example.chat_application.R;
import com.example.chat_application.model.User_model;
import com.example.chat_application.utils.AndroidUtil;
import com.example.chat_application.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class Search_recy_adapter extends FirestoreRecyclerAdapter<User_model, Search_recy_adapter.UserModelViewHolder> {

    Context context;
    public Search_recy_adapter(@NonNull FirestoreRecyclerOptions<User_model> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull User_model model) {
        holder.username_txt.setText(model.getUsername());
        holder.phn_num.setText(model.getPhone());
        if(model.getUserid().equals(FirebaseUtil.current_userid())){
            holder.username_txt.setText(model.getUsername()+" (Me)");
        }

        FirebaseUtil.get_other_profile_pic_storage(model.getUserid()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.set_profile_pic(context, uri, holder.profile_pic);
                    }
                });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat_Activity.class);
            AndroidUtil.passUserModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item, parent,false);
        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{

        TextView username_txt;
        TextView phn_num;
        ImageView profile_pic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);

            username_txt = itemView.findViewById(R.id.username_txt);
            phn_num = itemView.findViewById(R.id.phn_num);
            profile_pic = itemView.findViewById(R.id.profile_pic);
        }
    }
}
