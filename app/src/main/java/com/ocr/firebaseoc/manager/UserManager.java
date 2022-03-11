package com.ocr.firebaseoc.manager;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    public Task<Void> deleteUser(Context context){
        return userRepository.deleteUser(context);
    }

}
