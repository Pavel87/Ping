package com.pacmac.pinger;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pacmac on 2018-05-27.
 */

public class Utility {

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    /**
     * Validate ip address with regular expression
     *
     * @param ip ip address for validation
     * @return true valid ip address, false invalid ip address
     */
    private static boolean validateIP(final String ip) {
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    /* Returns true if url is valid */
    private static boolean isValidURL(String url) {
        /* Try creating a valid URL */
        try {
            new URL("http://" + url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAddressValid(String address) {
        if (address.length() == 0) {
            return false;
        }

        if (!address.contains(".")) {
            return false;
        }

        // IP address
        String[] elements = address.split("\\.");
        if (elements.length == 4) {
            return Utility.validateIP(address);
        }
        return Utility.isValidURL(address);
    }

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

    protected static void setBooleanToPreference(Context context, boolean value, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PINGER_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    protected static boolean getBooleanFromPreference(Context context, boolean defaultValue, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PINGER_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    protected static double getIntervalFromProgress(int progress) {
        if (progress < 4) {
            return progress * 0.2 + 0.2;
        } else if (progress < 64) {
            return (progress - 3);
        } else if (progress == 100) {
            return 300;
        } else {
            return 5 * (progress - 63) + 60;
        }
    }

    protected static int getPacketSizeFromProgress(int progress) {
        if (progress < 100) {
            return progress + 1;
        } else {
            return progress*2;
        }
    }

    protected static int getTTLFromProgress(int progress) {
        if (progress < 100) {
            return progress + 1;
        } else {
            return progress + 50;
        }
    }

}
