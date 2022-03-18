package com.ocr.firebaseoc.repository;

import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ocr.firebaseoc.manager.UserManager;
import com.ocr.firebaseoc.model.Message;

import java.util.UUID;

public final class ChatRepository {

    private static final String CHAT_COLLECTION = "chats";
    private static final String MESSAGE_COLLECTION = "messages";
    private static volatile ChatRepository instance;

    private UserManager userManager;

    private ChatRepository() { this.userManager = UserManager.getInstance(); }

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

    /*************************
     * Display Conversation *
     ***********************/

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

    /***********************
     * Send a new Message *
     *********************/

    // Creates the message object and adds it to DB Firestore
    public void createMessageForChat(String textMessage, String chat){

        userManager.getUserData().addOnSuccessListener(user -> {
            // Create the Message object
            Message message = new Message(textMessage, user);

            // Store Message to Firestore
            this.getChatCollection()
                    .document(chat)
                    .collection(MESSAGE_COLLECTION)
                    .add(message);
        });

    }



    // Upload image to Firebase Storage and return its URL
    public UploadTask uploadImage(Uri imageUri, String chat){
        String uuid = UUID.randomUUID().toString(); // Generates UUID : Universally Unique Identifier
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(chat + "/" + uuid); // Generates a FirebaseStorage URL to host the picture
        return mImageRef.putFile(imageUri);  // return an UploadTask that uploads the picture to the URL
    }


    // Creates a Message carrying the picture's URL and adds it to FirebaseStorage
    public void createMessageWithImageForChat(String urlImage, String textMessage, String chat){
        userManager.getUserData().addOnSuccessListener(user -> {
            // Creating Message with the URL image
            Message message = new Message(textMessage, urlImage, user);

            // Storing Message on Firestore
            this.getChatCollection()
                    .document(chat)
                    .collection(MESSAGE_COLLECTION)
                    .add(message);
        });
    }

}