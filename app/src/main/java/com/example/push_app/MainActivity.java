package com.example.push_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;



import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;


import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;


import com.example.push_app.fragments.HomeFragment;
import com.example.push_app.fragments.AboutFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.ArrayList;



/**
 * Main activity, the main menu so to speak.
 * It displays footer and weather, it handles fragments.
 * "root" class
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navBar;
    private HomeFragment homeFragment;
    private AboutFragment aboutFragment;


    private ArrayList<Integer> selectedIndex;


    /**
     * Creates the activity.
     * It is the frame which hosts all fragments, it also handles communication between them
     * @param savedInstanceState: previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFragments(getIntent());

        navBar = findViewById(R.id.nav_bar_bottom);
        navBar.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                homeFragment).commit();

        selectedIndex = new ArrayList<>();
    }


    /**
     * initializes app's fragments
     */

    private void initializeFragments(Intent intent){

        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }

        if (aboutFragment == null) {
            aboutFragment = new AboutFragment();
        }
    }


    /**
     * Creates listener for navigation bar to detect user's input and make switch to desired Fragment
     * It is outside onCreate() to make it more easy to read and reduce "size" of it
     **/
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                /**
                 * @param item: item that was selected by the user
                 **/
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = homeFragment;
                            break;
                        case R.id.nav_about:
                            selectedFragment = aboutFragment;
                            break;
                    }

                    assert selectedFragment != null;

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
                    fragmentTransaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
                    fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
                    fragmentTransaction.commit();

                    return true; //display changes to navigation bar, false = do not update nav bar
                }

            };

    /**
     * Handles pressed back button, hitting it on home fragment exits to phone's home screen
     * when user is currently on different fragment app goes to home/current weather
     */
    @Override
    public void onBackPressed() {
        if (navBar.getSelectedItemId() == R.id.nav_home) {
            super.onBackPressed();
        } else {
            navBar.setSelectedItemId(R.id.nav_home);
        }
    }

    /**
     * Called by the system when the device configuration changes while your activity is running.
     * Note that this will only be called if you have selected configurations you would like to
     * handle with the R.attr.configChanges attribute in your manifest. If any configuration change
     * occurs that is not selected to be reported by that attribute, then instead of reporting it
     * the system will stop and restart the activity (to have it launched with the new configuration).
     * It is called because of: <activity android:name=".MainActivity"
     *                  android:configChanges="orientation|screenSize|screenLayout|keyboardHidden"/>
     * in manifest.
     * @param newConfig: The new device configuration. This value must never be null.
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (navBar.getSelectedItemId()) {
            case R.id.nav_home:
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    homeFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            homeFragment).commit();

                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                    homeFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            homeFragment).commit();
                }

                break;
            case R.id.nav_about:
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    aboutFragment = new AboutFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            aboutFragment).commit();

                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                    aboutFragment = new AboutFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            aboutFragment).commit();
                }
                break;
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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