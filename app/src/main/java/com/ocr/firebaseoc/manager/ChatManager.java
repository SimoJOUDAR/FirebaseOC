package com.ocr.firebaseoc.manager;

import android.net.Uri;

import com.google.firebase.firestore.Query;
import com.ocr.firebaseoc.repository.ChatRepository;

public class ChatManager {

    private static volatile ChatManager instance;
    private ChatRepository chatRepository;

    private ChatManager() {
        chatRepository = ChatRepository.getInstance();
    }

    public static ChatManager getInstance() {
        ChatManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ChatManager.class) {
            if (instance == null) {
                instance = new ChatManager();
            }
            return instance;
        }
    }

    /*************************
     * Display Conversation *
     ***********************/

    public Query getAllMessageForChat(String chat){
        return chatRepository.getAllMessageForChat(chat);
    }

    /***********************
     * Send a new Message *
     *********************/

    public void createMessageForChat(String message, String chat){
        chatRepository.createMessageForChat(message, chat);
    }

    /*******************
     * Send a picture *
     *****************/
    public void sendMessageWithImageForChat(String message, Uri imageUri, String chat){
        chatRepository.uploadImage(imageUri, chat).addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                chatRepository.createMessageWithImageForChat(uri.toString(), message, chat);
            });
        });
    }

}
