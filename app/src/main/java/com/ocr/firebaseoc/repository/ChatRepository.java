package com.ocr.firebaseoc.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public final class ChatRepository {

    private static final String CHAT_COLLECTION = "chats";
    private static final String MESSAGE_COLLECTION = "messages";
    private static volatile ChatRepository instance;

    private ChatRepository() { }

    public static ChatRepository getInstance() {
        ChatRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ChatRepository.class) {
            if (instance == null) {
                instance = new ChatRepository();
            }
            return instance;
        }
    }

    public CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(CHAT_COLLECTION);
    }

    // When using comparison operators (also called query operators)
    // <whereEqualTo(), whereLessThan(), orderBy(), limit()>
    // The return type of the method becomes "Query"
    public Query getAllMessageForChat(String chat){
        return this.getChatCollection()
                .document(chat)
                .collection(MESSAGE_COLLECTION)
                .orderBy("dateCreated")   // To fetch the most recent messages objects
                .limit(50);   // To fetch no more than 50 message objects
    }

}