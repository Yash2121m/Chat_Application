package com.example.chat_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chat_application.adapter.Chat_recy_Adapter;
import com.example.chat_application.adapter.Search_recy_adapter;
import com.example.chat_application.model.ChatMessageModel;
import com.example.chat_application.model.ChatRoomModel;
import com.example.chat_application.model.User_model;
import com.example.chat_application.utils.AndroidUtil;
import com.example.chat_application.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class Chat_Activity extends AppCompatActivity {

    User_model otherUser;

    String chatRoomId;
    ChatRoomModel chatRoomModel;

    EditText  messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView OtherUsername;
    RecyclerView recyclerView;

    Chat_recy_Adapter adapter;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get Usermodel
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatroomId(FirebaseUtil.current_userid(),otherUser.getUserid());

        messageInput = findViewById(R.id.chat_msg);
        sendMessageBtn = findViewById(R.id.send_msg);
        backBtn = findViewById(R.id.back_btn);
        OtherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recy);
        imageView = findViewById(R.id.profile_pic);

        FirebaseUtil.get_other_profile_pic_storage(otherUser.getUserid()).getDownloadUrl()
                .addOnCompleteListener(t -> {
                    if(t.isSuccessful()){
                        Uri uri = t.getResult();
                        AndroidUtil.set_profile_pic(this, uri, imageView);
                    }
                });

        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });
        OtherUsername.setText(otherUser.getUsername());

        sendMessageBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty()){
                return;
            }
            sendMessageToUser(message);
        });

        getOrCreateChatRoomModel();

        setupChatRecycleView();

    }

    void  setupChatRecycleView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new Chat_recy_Adapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message){

        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.current_userid());
        chatRoomModel.setLastMasssage(message);
        FirebaseUtil.getChatroomReference(chatRoomId).set(chatRoomModel);


        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.current_userid(),Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatRoomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
                        }
                    }
                });
    }

    void getOrCreateChatRoomModel(){
        FirebaseUtil.getChatroomReference(chatRoomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if(chatRoomModel == null){
                    //For First Time Chatting
                    chatRoomModel = new ChatRoomModel(
                            chatRoomId,
                            Arrays.asList(FirebaseUtil.current_userid(),otherUser.getUserid()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatRoomId).set(chatRoomModel);
                }
            }
        });
    }
}