package com.example.push_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

/**
 * First screen, quick activity displaying the icon and name,
 * then it goes to the main activity.
 */
public class StartupActivity extends AppCompatActivity {

    final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final int TIME = 2000;
    private String question;

    /**
     * Creates the activity, calls other activities after 2000 milliseconds = 2 sec.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        Intent in = getIntent();
        Bundle bud = in.getExtras();

        if (bud != null) {
            question = bud.getString("body");
        } else {
            question = "no question obtained";
        }

        ActivityStarter starter = new ActivityStarter(question);
        starter.start();
    }

    /**
     * Test method that logs the contents of intent's bundle.
     * @param bundle: extra data attached to intent.
     * @return bundle converted to string.
     */
    public static String bundle2string(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }Bundle";
        return string;
    }

    /**
     * Class used to launch next activity - MainActivity. It also attaches a push notification's contents if one was received.
     * It extends Thread in order not to run on a main thread making the app unresponsive.
     */
    private class ActivityStarter extends Thread {
        String s;

        public ActivityStarter(String input) {
            this.s = input;
        }

        @Override
        public void run() {

            Intent nextIntent;

            try {
                Thread.sleep(TIME);
            } catch (Exception e) {
                Log.e("SplashScreen", Objects.requireNonNull(e.getMessage()));
            }

            if (auth.getCurrentUser() != null) {
                nextIntent = new Intent(StartupActivity.this, MainActivity.class);
                nextIntent.putExtra("fetchedQuestion", this.s);
            } else {
                nextIntent = new Intent(StartupActivity.this, AuthorizationHandler.class);
            }

            StartupActivity.this.startActivity(nextIntent);
            StartupActivity.this.finish();
        }
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

//      hiding action bar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null){
            actionbar.hide();
        }

//      makes notification bar completely transparent
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
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}