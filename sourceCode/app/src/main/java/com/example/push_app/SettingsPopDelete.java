package com.example.push_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Displays pop up window informing user that his data will be permanently deleted.
 */
public class SettingsPopDelete extends AppCompatDialogFragment {

    DatabaseReference firebase;
    Button delButton;
    Button unsubButton;
    EditText editText;
    public SettingsPopDelete(DatabaseReference obtainedRef, Button first, Button third, EditText editText) {
        this.firebase = obtainedRef;
        this.delButton = first;
        this.unsubButton = third;
        this.editText = editText;

    }


    /**
     * This is typically used to show an AlertDialog instead of a generic Dialog
     *
     * @param savedInstanceState The last saved instance state of the Fragment, or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete app's data?")
                .setMessage("WARNING!\nThis will wipe all your data from our service!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearDatabase();
                        Toaster.postToastMessage("Your data has been deleted.", getActivity());
                    }
                })

                .setNegativeButton("No, go back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        return builder.create();

    }

    /**
     * Delete all entries in the database after confirming.
     */
    private void clearDatabase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.firebase.child("users").child(user.getUid()).child("/").removeValue();
        deleteUser();
    }

    /**
     * Disables certain buttons, so that they are not clickable
     */
    private void disableButtons() {
        this.editText.setText("");
        this.editText.setFocusable(false);
        this.unsubButton.setEnabled(false);
        this.delButton.setEnabled(false);
    }

    /**
     * Deletes currently logged user
     */
    public void deleteUser() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                disableButtons();
                            }
                        }
                    });
        }
    }
}
