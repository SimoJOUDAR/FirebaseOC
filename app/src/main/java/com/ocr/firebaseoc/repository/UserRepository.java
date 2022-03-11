package com.ocr.firebaseoc.repository;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserRepository {

    // This class implements a Singleton pattern (one instance only)

    private static volatile UserRepository instance;

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

}
