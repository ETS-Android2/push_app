package com.example.push_app.handlers;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 *  Static class used to handle database calls.
 */
public class DatabaseHandler {
    private static final String TAG = "DatabaseHandler";

    /**
     * Writes to database
     * @param path: where the data should be saved
     * @param payload: what should be saved
     */
    public static void sendToDB(DatabaseReference path, String payload) {

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    path.getParent().setValue(payload);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };
        path.addListenerForSingleValueEvent(eventListener);
    }

}
