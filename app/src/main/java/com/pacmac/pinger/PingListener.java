package com.pacmac.pinger;

/**
 * Created by pacmac on 2018-05-24.
 */

public interface PingListener {

    void onComplete(String result);
    void onStep(String line);
}
