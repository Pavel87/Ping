package com.pacmac.pinger;

import android.os.AsyncTask;

class SaveToSDTask extends AsyncTask<String, Void, Boolean> {

    private ExportListener exportListener = null;

    protected SaveToSDTask(ExportListener exportListener) {
        this.exportListener = exportListener;
    }

    @Override
    protected Boolean doInBackground(String... output) {
        return Utility.saveResultToSD(output[0], output[1]);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(exportListener != null) {
            exportListener.onExportComplete(success);
        }
    }
}

interface ExportListener {
    void onExportComplete(boolean success);
}
