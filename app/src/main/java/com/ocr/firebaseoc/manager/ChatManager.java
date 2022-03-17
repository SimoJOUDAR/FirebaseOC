package com.ocr.firebaseoc.manager;

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

    public Query getAllMessageForChat(String chat){
        return chatRepository.getAllMessageForChat(chat);
    }

    public void createMessageForChat(String message, String chat){
        chatRepository.createMessageForChat(message, chat);
    }

}
