package br.edu.ifrs.musicnotes.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Firebase {

    private static FirebaseAuth authInstance = null;
    private static DatabaseReference databaseReference = null;
    private static String userId = null;

    private static FirebaseAuth getAuthInstance() {
        authInstance = FirebaseAuth.getInstance();
        return authInstance;
    }

    public static boolean isUserLogged() {
        if (authInstance == null) {
            getAuthInstance();
        }
        FirebaseUser currentUser = getAuthInstance().getCurrentUser();
        return currentUser != null;
    }

    public static String getUserId() {
        if (authInstance == null) {
            getAuthInstance();
        }
        userId = getAuthInstance().getCurrentUser().getUid();
        return userId;
    }

    public static DatabaseReference getDatabaseReference() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        return databaseReference;
    }

    public static DatabaseReference getAlbumsNode() {
        if (databaseReference == null) {
            getDatabaseReference();
        }
        if (userId == null) {
            getUserId();
        }
        return databaseReference.child("users").child(userId).child("albums");
    }

}
