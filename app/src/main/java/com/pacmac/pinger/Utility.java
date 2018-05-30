package com.pacmac.pinger;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pacmac on 2018-05-27.
 */

public class Utility {


    protected static void setIntToPreference(Context context, int value, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PINGER_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    protected static int getIntFromPreference(Context context, int defaultValue, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PINGER_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

}
