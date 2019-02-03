package com.pacmac.pinger;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.pacmac.pinger", appContext.getPackageName());
    }


    @Test
    public void pingTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext().getApplicationContext();

//        //dhcp address
//        WifiManager wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
//        DhcpInfo dhcpInformation = wifiManager.getDhcpInfo();
//        String ipAddress = intToInetAddress(dhcpInformation.ipAddress).getHostAddress();
//        String gatewayAddress = intToInetAddress(dhcpInformation.gateway).getHostAddress();
//         dhcpInformation.toString();

        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress()) {
                    Log.e("PaCMAC",  inetAddress.getHostAddress().toString());
                }
            }
        }

    }

    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

}
