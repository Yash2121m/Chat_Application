package com.example.chat_application.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;


public class FirebaseUtil {

    public static String current_userid(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoogedIn(){
        if(current_userid()!=null){
            return true;
        }
        return false;
    }

    public static DocumentReference current_user_details(){
        return FirebaseFirestore.getInstance().collection("users").document(current_userid());
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1, String userId2){
        if (userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else {
            return userId2+"_"+userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.current_userid())){
            return allUserCollectionReference().document(userIds.get(1));
        }
        else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference get_current_profile_pic_storage(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.current_userid());
    }

    public static StorageReference get_other_profile_pic_storage(String otheruserid){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otheruserid);
    }
}
