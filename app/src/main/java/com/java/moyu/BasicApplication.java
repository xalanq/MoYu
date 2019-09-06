package com.java.moyu;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class BasicApplication extends Application {

    private static Context context;
    private static Toast toast;
    private static boolean isNight = true;

    public static Context getContext() {
        return context;
    }

    public static boolean isNight() {
        return isNight;
    }

    public static void setNight(boolean value) {
        isNight = value;
    }

    public static void showToast(String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

}
