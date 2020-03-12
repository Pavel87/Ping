package com.pacmac.pinger;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
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
        Context context = androidx.test.InstrumentationRegistry.getInstrumentation().getTargetContext();


        assertEquals("com.pacmac.pinger", context.getPackageName());
    }

    @Test
    public void validateAddress() {
        assertTrue(Utility.isIPv6Valid("fe20:23:::01"));
        assertTrue(Utility.isIPv6Valid("2607:f8b0:400a"));
        assertTrue(Utility.isIPv6Valid("2001:569:be80:750"));
        assertTrue(Utility.isIPv6Valid("fe20:23:::01"));
        assertTrue(Utility.isIPv6Valid("2001:db8:85a3::8a2e:370"));
        assertFalse(Utility.isIPv6Valid("g001:db8:85a3::8a2e:370"));
        assertFalse(Utility.isIPv6Valid("wwsdasda.cz"));



        assertTrue(Utility.isIPv4Valid("192.1.3.1"));
        assertTrue(Utility.isIPv4Valid("127.0.0.1"));
        assertTrue(Utility.isIPv4Valid("77.34.76.12"));
        assertFalse(Utility.isIPv4Valid("77.34.76:12"));


        assertFalse(Utility.isURLValid("www.sda.as1"));
        assertFalse(Utility.isURLValid("www.sda.as."));
        assertFalse(Utility.isURLValid("www.sda.asA:!"));
        assertFalse(Utility.isURLValid("sda.asA:!"));
        assertTrue(Utility.isURLValid("sda.cz"));
    }

    @Test
    public void pingTest() throws Exception {
        Context appContext = androidx.test.InstrumentationRegistry.getInstrumentation().getTargetContext();

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
