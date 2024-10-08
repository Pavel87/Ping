package com.pacmac.pinger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

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
    protected static boolean isIPv4Valid(final String ip) {
        // IP address
        String[] elements = ip.split("\\.");
        if (elements.length != 4) {
            return false;
        }
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    protected static boolean isIPv6Valid(final String ip) {
        byte[] ipInBytes = ip.toLowerCase().getBytes();

        for (byte b : ipInBytes) {
            if ((b < (byte) '0' || b > (byte) '9') && b != (byte) ':' && (b < (byte) 'a' || b > (byte) 'f')) {
                return false;
            }
        }
        return true;
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

    public static boolean isURLValid(String address) {
        address = address.toLowerCase();
        byte lastChar = ((byte) address.charAt(address.length() - 1));
        if (address.length() == 0
                || address.contains("http")
                || !address.contains(".")
                || address.contains("://")
                || (lastChar < (byte) 'a'
                || lastChar > (byte) 'z')) {
            return false;
        }

        return Utility.isValidURL(address);
    }

    protected static void setStringToPreference(Context context, String value, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PINGER_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    protected static String getStringFromPreference(Context context, String defaultValue, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PINGER_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
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
            return progress * 2;
        }
    }

    protected static int getTTLFromProgress(int progress) {
        if (progress < 100) {
            return progress + 1;
        } else {
            return progress + 50;
        }
    }

    public static void sendShareIntent(Context context, String pingAddress, String output) {

        Calendar calendar = Calendar.getInstance();

        File file = new File(context.getCacheDir(), String.format(Locale.ENGLISH,
                "ping_result_%d%d%d_%d-%d.txt",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));

        try {
            file.createNewFile();

            BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsolutePath(), false));
            out.write(output);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ping for: " + pingAddress);

        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Ping results are attatched in " + file.getName());

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));

        Uri fileUri = null;
        try {
            fileUri = FileProvider.getUriForFile(context, "com.pacmac.pinger.fileprovider", file);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        ClipData clipData = new ClipData(new ClipDescription("Ping Result",
                new String[]{ClipDescription.MIMETYPE_TEXT_URILIST}), new ClipData.Item(fileUri));
        intentShareFile.setClipData(clipData);

        intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri);

        // Grant temporary read permission to the content URI
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentShareFile.setType("application/*");
        context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }

    public static void transitionLeftToRight(Activity activity) {
        if (activity != null) {
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
