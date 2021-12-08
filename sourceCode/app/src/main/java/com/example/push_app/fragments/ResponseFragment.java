package com.example.push_app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.push_app.handlers.DatabaseHandler;
import com.example.push_app.R;
import com.example.push_app.handlers.Toaster;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Response fragment, it handles user answers for questions obtained from push notification.
 */
public class ResponseFragment extends Fragment {

    private DatabaseReference mFirebaseDatabase;
    private FirebaseUser currentUser;

    private TextView questionTitle;
    private EditText enterResponse;

    private String fetchedQuestion;

    SharedPreferences sharedPref;

    /**
     * System method called when the fragment is created but still not visible. Here it tries to contents of push notification.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getActivity().getSharedPreferences("FETCHED_QUESTIONS", Context.MODE_PRIVATE);
    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional,
     * and non-graphical fragments can return null. This will be called between onCreate(Bundle)
     * and onActivityCreated(Bundle)
     *
     * @param inflater: The LayoutInflater object that can be used to inflate any
     *                  views in the fragment,
     *
     * @param container: If non-null, this is the parent view that the fragment's UI should
     *                   be attached to. The fragment should not add the view itself,
     *                   but this can be used to generate the LayoutParams of the view.
     *
     * @param savedInstanceState : If non-null, this fragment is being re-constructed from
     *                             a previous saved state as given here.
     **/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_response, container, false);

        questionTitle = view.findViewById(R.id.question_title);

        enterResponse = view.findViewById(R.id.enter_response);
        enterResponse.setOnEditorActionListener(editorActionListener);

        if (sharedPref.contains("question")) {
            fetchedQuestion = sharedPref.getString("question", getString(R.string.response_default));

            if(!fetchedQuestion.equals(getString(R.string.response_default))) {
                enterResponse.setFocusableInTouchMode(true);
                enterResponse.setFocusable(true);
            }
        } else {
            fetchedQuestion = getString(R.string.response_default);
            Log.e("ResponseFragment", "Error with fetching the question.");
        }

        questionTitle.setText(fetchedQuestion);

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
                String obtainedText = String.valueOf(enterResponse.getText());
                if (!obtainedText.isEmpty()) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            sendResponse(obtainedText, questionTitle.getText().toString());
                            Toaster.postToastMessage("Your message has been saved", getContext());
                        }
                    };

                    Thread thread = new Thread(runnable);
                    thread.start();

                    v.setText("");
                    v.clearFocus();
                    v.setFocusable(false);
                    questionTitle.setText(getString(R.string.response_default));
                    sharedPref.edit().clear().commit();
                } else {
                    Toaster.postToastMessage("Please provide an answer.", getContext());
//                    Toast.makeText(getContext(), "Please provide an answer.",Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    };

    /**
     * Saves user response to Firebase Realtime Database instance.
     * @param body: user response
     * @param request: question
     */
    private void sendResponse(String body, String request) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance("https://push-app-85ab0-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        DatabaseReference respRef = mFirebaseDatabase.child("users").child(user.getUid()).child("response").child(request).child("dummy_thicc_key");

        DatabaseHandler.sendToDB(respRef, body);
    }
}