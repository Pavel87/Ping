package com.pacmac.pinger;

import android.app.Application;

import com.tutelatechnologies.sdk.framework.TUException;
import com.tutelatechnologies.sdk.framework.TutelaSDKFactory;


/**
 * Created by pacmac on 2018-06-11.
 */

public final class PingApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        try {
            TutelaSDKFactory.getTheSDK().initializeWithApiKey("ieg9ioa9qhlbnff2714e6s1a8n", getApplicationContext());
        } catch (TUException e) {
            e.printStackTrace();
        }

    }
}
