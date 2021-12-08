package com.example.push_app.handlers;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * HaProvides functionality to refresh Firebase Cloud Messaging Token.
 */
public class FCMHandler {

    /**
     * Enables FCM auto initialization --> generates ne token.
     */
    public static void enableFCM(){
        // Enable FCM via enable Auto-init service which generate new token and receive in FCMService
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    /**
     * Disables FCM auto initialization and deletes existing token.
     */
    public static void disableFCM(){
        // Disable auto init
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);
        new Thread(() -> {
            FirebaseMessaging.getInstance().deleteToken();
        }).start();
    }

}