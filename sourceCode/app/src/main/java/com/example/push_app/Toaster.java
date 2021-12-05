package com.example.push_app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Simple static class that provides an ability to make toast message that doesn't have to be executed o a main thread.
 */
public class Toaster {

    public static void postToastMessage(final String message, Context context) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
