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
import com.example.chat_application.model.ChatRoomModel;
import com.example.chat_application.model.User_model;
import com.example.chat_application.utils.AndroidUtil;
import com.example.chat_application.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        boolean lastMsgSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.current_userid());
                        User_model otherUserModel = task.getResult().toObject(User_model.class);

                        FirebaseUtil.get_other_profile_pic_storage(otherUserModel.getUserid()).getDownloadUrl()
                                .addOnCompleteListener(t -> {
                                    if(t.isSuccessful()){
                                        Uri uri = t.getResult();
                                        AndroidUtil.set_profile_pic(context, uri, holder.profile_pic);
                                    }
                                });

                        holder.username_txt.setText(otherUserModel.getUsername());
                        if(lastMsgSentByMe){
                            holder.last_msg.setText("You : "+model.getLastMasssage());
                        }
                        else {
                            holder.last_msg.setText(model.getLastMasssage());
                        }
                        holder.last_msg_time.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        holder.itemView.setOnClickListener(v -> {
                            Intent intent = new Intent(context, Chat_Activity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });

                    }
                });
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_view, parent,false);
        return new ChatRoomModelViewHolder(view);
    }

    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder{

        TextView username_txt;
        TextView last_msg;
        TextView last_msg_time;
        ImageView profile_pic;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);

            username_txt = itemView.findViewById(R.id.username_txt);
            last_msg = itemView.findViewById(R.id.last_msg);
            last_msg_time = itemView.findViewById(R.id.last_msg_time);
            profile_pic = itemView.findViewById(R.id.profile_pic);
        }
    }
}
