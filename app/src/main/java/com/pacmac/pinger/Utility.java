package com.pacmac.pinger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
    protected static boolean validateIP(final String ip) {
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
        if (address.length() == 0 || address.contains("http") || !address.contains(".") || address.contains("://")) {
            return false;
        }

        // IP address
        String[] elements = address.split("\\.");
        if (elements.length == 4) {
            return Utility.validateIP(address);
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

    protected static void shareResult(Activity activity, String pingAddress, String output) {
        try {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Ping for: " + pingAddress);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, output);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getApplicationContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
            } else {
                activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static boolean saveResultToSD(String output, String address) {
        boolean result = false;

        File pingExportFolder = new File(Environment.getExternalStorageDirectory(), "ICMP Ping");
        if (!pingExportFolder.exists()) {
            pingExportFolder.mkdir();
        }

        File pingExportFile = new File(pingExportFolder, "ping_" + address + ".txt");
        try {
            if (pingExportFile.exists()) {
                pingExportFile.delete();
            }
            pingExportFile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(pingExportFile.getAbsolutePath(), false));
            out.write(new Date().toString());
            out.write(" : \n");
            out.write(output);
            out.flush();
            out.close();
            result = true;
        } catch (Exception e) {
            try {
                if(out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return result;
    }

    protected static void displayExplanationForPermission(AppCompatActivity act, final String permission) {

        final AppCompatActivity mActivity = act;
        AlertDialog.Builder builder = new AlertDialog.Builder(act, 0)
                .setCancelable(true).setMessage(act.getString(R.string.permission_explanation)).setTitle("Permission Required")
                .setPositiveButton((act.getResources().getString(R.string.permission_proceed)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(mActivity, permission);
                    }
                })
                .setNegativeButton(act.getResources().getString(R.string.permission_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    protected static void requestPermissions(Activity activity, String permission) {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(activity, new String[]{permission}, Constants.PING_WRITE_EXT_STORAGE_RC);
    }

}
