package com.example.push_app.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.push_app.handlers.AuthorizationHandler;
import com.example.push_app.handlers.FCMHandler;
import com.example.push_app.R;
import com.example.push_app.handlers.SettingsPopDelete;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import com.example.push_app.handlers.Toaster;

/**
 * Home fragment, it is the main screen, it handles user logout, deleting his data, subscribing to topics.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private DatabaseReference mFirebaseDatabase;
    private FirebaseUser currentUser;

    private TextView helloMyNameIs, username, removeData;
    private Button deleteAccountButton, logoutButton, topicClearButton;
    private EditText topicName;

    String loggedUser;


    /**
     * Method that is called in the beginning of the lifecycle
     * @param savedInstanceState - Bundle with data passed from parent
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseDatabase = FirebaseDatabase.getInstance("https://push-app-85ab0-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        loggedUser = getArguments().getString("loggedUser");

    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional,
     * and non-graphical fragments can return null. This will be called between onCreate(Bundle)
     * and onActivityCreated(Bundle)
     *
     * @param inflater:          The LayoutInflater object that can be used to inflate any
     *                           views in the fragment,
     * @param container:         If non-null, this is the parent view that the fragment's UI should
     *                           be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState: If non-null, this fragment is being re-constructed from
     *                           a previous saved state as given here.
     **/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        helloMyNameIs = view.findViewById(R.id.hello);
        username = view.findViewById(R.id.user_name);
        removeData = view.findViewById(R.id.loc);
        deleteAccountButton = view.findViewById(R.id.button_delete);
        logoutButton = view.findViewById(R.id.button_register);

        topicName = view.findViewById(R.id.enter_topic);
        topicClearButton = view.findViewById(R.id.button_unsub);



        deleteAccountButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        topicClearButton.setOnClickListener(this);

        topicName.setOnEditorActionListener(editorActionListener);

        if (loggedUser != null && !loggedUser.equals("")){
            username.setText(loggedUser);
        } else {
            username.setText(R.string.placeholder_3);
            deleteAccountButton.setEnabled(false);

        }

        return view;
    }


    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        /**
         * This method checks what is the type of special button on the keyboard. Special button is
         * displayed in place of "enter" key, it could be "done", "search" (this case), "next", "send"
         * @param v: EditText
         * @param actionId: "key code" - what was pressed (enter, next, done, ...)
         * @param event: If triggered by an enter key, this is the event; otherwise, this is null.
         * @return boolean: true if you have consumed the action, else false.
         */
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String obtainedText = String.valueOf(topicName.getText());
                if (!obtainedText.isEmpty()) {
                    subToTopic(obtainedText);
                    v.setText("");
                    v.clearFocus();
                } else {
                    Toast.makeText(getContext(), "Please enter topic name",Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    };


    /**
     * Handling all of the buttons interactions.
     * @param v - Button
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_delete:

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        deleteFirebaseData(mFirebaseDatabase, deleteAccountButton, topicClearButton, topicName);
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
                break;

            case R.id.button_register:
                Toast.makeText(getActivity(), "You will be signed out shortly, bye.", Toast.LENGTH_SHORT).show();
                signOut();
                break;

            case R.id.button_unsub:
                Runnable runnable1 = new Runnable() {
                    @Override
                    public void run() {
                        unsubAllTopics();
                    }
                };
                Thread thread1 = new Thread(runnable1);
                thread1.start();
                break;
        }

    }

    /**
     * Signs out the user. Is user choose option "login as a guest", his data is erased from the database.
     * He is also purged from authorization service.
     */
    public void signOut() {
        if (currentUser.isAnonymous()) {
            mFirebaseDatabase.child("users").child(currentUser.getUid()).child("/").removeValue();
            currentUser.delete();
        }

        FCMHandler.disableFCM();

        AuthUI.getInstance().signOut(requireActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent startAuthIntent = new Intent(getActivity(), AuthorizationHandler.class);
                        startActivity(startAuthIntent);
                        getActivity().finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Called when users deletes his data
     * @param firebaseRef - reference to firebase database
     * @param delete - delete button (to disable it)
     * @param sub - unsubscribe button (to disable it)
     * @param editText - subscribe field (to disable it)
     */
    public void deleteFirebaseData(DatabaseReference firebaseRef, Button delete, Button sub, EditText editText) {
        SettingsPopDelete popup = new SettingsPopDelete(firebaseRef, delete, sub, editText);
        popup.show(requireActivity().getSupportFragmentManager(), "popupDelete");
    }

    /**
     * Subscribe to the topic, and save its name to database.
     * @param topicName - topic to subscribe to
     */
    public void subToTopic(String topicName) {
        FirebaseMessaging.getInstance().subscribeToTopic(topicName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.success);
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                DatabaseReference userNameRef = mFirebaseDatabase.child("users").child(currentUser.getUid()).child("topics");
                                ValueEventListener eventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        long maxID = dataSnapshot.getChildrenCount();
                                            userNameRef.child("topic_" + (maxID + 1)).setValue(topicName);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.d("FIREBASE SAVING", databaseError.getMessage()); //Don't ignore errors!
                                    }
                                };
                                userNameRef.addListenerForSingleValueEvent(eventListener);
                            }
                        };

                        Thread thread = new Thread(runnable);
                        thread.start();

                        if (!task.isSuccessful()) {
                            msg = getString(R.string.error);
                        }
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * TEST subToTopic - only writing to database, no actual subscribing!
     * @param topicName - topic to subscribe to
     */
    public void subToTopicTest(String topicName) {
        DatabaseReference userNameRef = mFirebaseDatabase.child("users").child(currentUser.getUid()).child("topics");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long maxID = dataSnapshot.getChildrenCount();
                userNameRef.child("topic_" + (maxID + 1)).setValue(topicName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FIREBASE SAVING", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);
    }

    /**
     * Unsubscribe user from all of his topics and deletes them from database. Called by unsub button.
     * NOTE: you can't manually delete topic from FCM, it is deleted automatically when there isn't anyone subscribed to it (70% sure).
     */
    public void unsubAllTopics() {
        mFirebaseDatabase.child("users").child(currentUser.getUid()).child("topics").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d("firebase", String.valueOf(task.getException()));
                    Toaster.postToastMessage(getString(R.string.error), getActivity());
                }
                else {
                    for (DataSnapshot data : task.getResult().getChildren()) {
                        String topicName = (String) data.getValue();
                        String key = (String) data.getKey();
                        if (topicName != null && !topicName.equals("")) {
                            Log.e("READ FROM DATABASE onComplete: ", topicName);
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("unsub", "unsub from topic " + topicName);
                                        Toaster.postToastMessage(getString(R.string.error), getActivity());
                                    } else {
                                        Log.e("READ FROM DATABASE onComplete: ", "unsubscribed");
                                        mFirebaseDatabase.child("users").child(currentUser.getUid()).child("topics").child(key).removeValue();
                                    }
                                }
                            });
                        }
                    }
                    Toaster.postToastMessage(getString(R.string.success), getActivity());
                }
            }
        });
    }
}

