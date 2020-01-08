package com.pacmac.pinger;

import android.os.AsyncTask;


/**
 * Created by pacmac on 2018-05-24.
 */
public final class AsyncPingTask extends AsyncTask<String, String, String> {

    private PingListener listener = null;
    private boolean useIPv6 = false;

    public AsyncPingTask(boolean useIPv6, PingListener listener) {
        this.useIPv6 = useIPv6;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... command) {
        return Ping.ping(this, useIPv6, command[0]);
    }

    protected void publishResponse(String line) {
        publishProgress(line);

    }

    @Override
    protected void onProgressUpdate(String... lines) {
        if (listener != null) {
            listener.onStep(lines[0]);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.onComplete(result);
        }
    }


}
