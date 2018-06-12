package com.pacmac.pinger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by pacmac on 2018-05-24.
 */

public final class Ping {

    private static boolean isStop = false;
    private static boolean isRunning = false;
    private static Process process = null;

    public static String ping(AsyncPingTask task, String command) {
        isStop = false;
        isRunning = true;
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec(
                    "/system/bin/ping " + command);
            reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while (!isStop && (i = reader.read(buffer)) > 0) {
                String line =new String(buffer, 0, i);
                task.publishResponse(line);
                output.append(line);
            }
            if(isStop) {
                task.publishResponse("Ping has been canceled.");
            }

            process.destroy();
            isRunning = false;
            return output.toString();

        } catch (IOException e) {
            task.publishResponse("Ping has been canceled.");
            return "Ping has been canceled.";
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void cancelProcess() {
        if (process != null) {
            process.destroy();
            isStop = true;
            isRunning = false;
        }
    }

}
