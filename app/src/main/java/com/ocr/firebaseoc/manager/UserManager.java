package com.ocr.firebaseoc.manager;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ocr.firebaseoc.model.User;
import com.ocr.firebaseoc.repository.UserRepository;

public class UserManager {

    // This class implements a Singleton pattern (one instance only)

    private static volatile UserManager instance;
    public UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        // synchronized : to make the instruction inside used by only one thread at a time. If other threads depend on the results of these instructions, they'd wait until synchronized() {} finishes.
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged(){
        return (userRepository.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context){
        return userRepository.signOut(context);
    }

    /*****************************************
     *  CRUD (Create, Read, Update, Delete  *
     ***************************************/

    // Create
    public void createUser(){
        userRepository.createUser();
    }

    // Read
    public Task<User> getUserData(){
        // Gets the user from Firestore as <DocumentSnapshot> and casts it into a User model Object
        return userRepository.getUserData().continueWith(task -> task.getResult().toObject(User.class)) ;
        // "continueWith" to proceed with the previous results into more transformation
        // toObject() serializes the object received and then deserializes it into the class model argument
    }

    // Update
    public Task<Void> updateUsername(String username){
        return userRepository.updateUsername(username);
    }

    // Update
    public void updateIsMentor(Boolean isMentor){
        userRepository.updateIsMentor(isMentor);
    }

    // Delete
    public Task<Void> deleteUser(Context context){
        // Deletes the user account from AuthUI
        return userRepository.deleteUser(context).addOnCompleteListener(task -> {
            // Once done, deletes the user data from Firestore
            userRepository.deleteUserFromFirestore();
        });
    }

}
