package com.example.push_app;

import android.app.Application;

/**
 * Class that holds information about app status. Visible or not visible.
 */
public class AppStatus extends Application {

    /**
     * Returns app status
     * @return app status
     */
    public static boolean isActivityVisible() {
        return activityVisible;
    }

    /**
     * Sets app status to visible.
     */
    public static void activityResumed() {
        activityVisible = true;
    }

    /**
     * Sets app status to not visible.
     */
    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;
}
