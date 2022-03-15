package com.ocr.firebaseoc.repository;

import android.content.Context;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ocr.firebaseoc.model.User;

public class UserRepository {

    // This class implements a Singleton pattern (one instance only)

    private static volatile UserRepository instance;

    private static final String COLLECTION_NAME = "users";
    private static final String USERNAME_FIELD = "username";
    private static final String IS_MENTOR_FIELD = "isMentor";

    private UserRepository() {}

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        // synchronized : to make the instruction inside used by only one thread at a time. If other threads depend on the results of these instructions, they'd wait until synchronized() {} finishes.
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);   // sign out user from AuthUI
    }

    public Task<Void> deleteUser(Context context){
        return AuthUI.getInstance().delete(context);   // Delete user from AuthUI
    }


    /**************
    *  Firestore  *
    **************/


    // Save our User in our DB Firestore after he's correctly logged in with AuthUI
    public void createUser() {

        // Gathering user's data from AuthUI
        FirebaseUser user = getCurrentUser();
        if(user != null){
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();

            User userToCreate = new User(uid, username, urlPicture);

            // Gathering user's complementary data from Firestore
            Task<DocumentSnapshot> userData = getUserData();
            // If the user already exists in Firestore, we get his data (isMentor)
            userData.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.contains(IS_MENTOR_FIELD)){
                    userToCreate.setIsMentor((Boolean) documentSnapshot.get(IS_MENTOR_FIELD));
                }

                // Saving our user inside our DB Firestore
                this.getUsersCollection().document(uid).set(userToCreate);
            });
        }
    }

    // Get User Data from Firestore
    public Task<DocumentSnapshot> getUserData(){
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return this.getUsersCollection().document(uid).get();
        }else{
            return null;
        }
    }


    // Gets the Collection Reference
    private CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Gets user's id
    private String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null)? user.getUid() : null;
    }

    // Updates User Username
    public Task<Void> updateUsername(String username) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            return this.getUsersCollection().document(uid).update(USERNAME_FIELD, username);
        }else{
            return null;
        }
    }

    // Updates User isMentor
    public void updateIsMentor(Boolean isMentor) {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            this.getUsersCollection().document(uid).update(IS_MENTOR_FIELD, isMentor);
        }
    }

    // Deletes the User from Firestore
    public void deleteUserFromFirestore() {
        String uid = this.getCurrentUserUID();
        if(uid != null){
            this.getUsersCollection().document(uid).delete();
        }
    }
}
