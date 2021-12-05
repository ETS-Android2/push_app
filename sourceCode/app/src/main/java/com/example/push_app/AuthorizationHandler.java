package com.example.push_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

/**
 * Handles user authorization.
 */
public class AuthorizationHandler extends AppCompatActivity {

    /**
     * Create an ActivityResultLauncher which registers a callback for the FirebaseUI Activity result contract
     */
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );




    /**
     * To kick off the FirebaseUI sign in flow, create a sign in intent with your preferred sign-in methods
     */
    public void createSignInIntent() {

        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_authorization_handler)
                .setEmailButtonId(R.id.button_email)
                .setAnonymousButtonId(R.id.button_anonymous)
                .build();

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setAuthMethodPickerLayout(customLayout)
                .setTheme(R.style.FullscreenTheme)
                .setLogo(R.mipmap.logo_1)
                .build();
        signInLauncher.launch(signInIntent);
    }


    /**
     * Method to show sign-in providers from the Firebase AuthUI.
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            //show email on Toast, replaces dots to underscores for json in Firebase

            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();

            if (user != null) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        writeNewUserToFirebase(user);
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();

            }

            Intent startMain = new Intent(AuthorizationHandler.this, MainActivity.class);
            AuthorizationHandler.this.startActivity(startMain);


        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
        AuthorizationHandler.this.finish();
    }

    /**
     * Saves info about user to realtime database. Prior to saving it checks whether data ias already there.
     * @param user FirebaseUser
     */
    private void writeNewUserToFirebase(FirebaseUser user) {

        DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance("<firebase instance>").getReference();
        String userUid = user.getUid();
        DatabaseReference userNameRef = mFirebaseDatabase.child("users").child(userUid);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    if (!user.isAnonymous()) {
                        userNameRef.child("email").setValue(user.getEmail());
                        userNameRef.child("name").setValue(user.getDisplayName());
                    } else {
                        userNameRef.child("email").setValue("anonymous");
                        userNameRef.child("name").setValue("anonymous");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FIREBASE SAVING", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);
        FCMHandler.enableFCM();
    }

    /**
     * Create the auth activity
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_empty);

        createSignInIntent();
    }


    /**
     * When user changes the window he's focused on,
     * the app should be still fullscreen.
     * @param hasFocus: says if it's fullscreen or running in the background.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUI();

        //hiding action bar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
//        makes notification bar completely transparent
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }

    /**
     * Hides the system UI with several flags.
     * Sets the IMMERSIVE flag.
     * Sets the content to appear under the system bars so that the content
     * doesn't resize when the system bars hide and show.
     */
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
