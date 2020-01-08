package com.pacmac.pinger;

/**
 * Created by pacmac on 2018-05-27.
 */

public class Constants {

    protected final static String VERSION = "1.6";

    protected final static int PING_SETTINGS_RC = 8;
    protected final static int PING_WRITE_EXT_STORAGE_RC = 88;

    protected final static String INFINITY = "\u221E";

    protected final static int PING_COUNT_DEFAULT = 4;
    protected final static int PING_SIZE_DEFAULT = 55; // getPacketSizeFromProgress(55) == 56 Bytes
    protected final static int PING_INTERVAL_DEFAULT = 5; // getIntervalFromProgress(5) == 1 second
    protected final static int PING_TTL_DEFAULT = 63; // getTTLFromProgress(63) == 64 ttl
    protected final static int PING_DEADLINE_DEFAULT = 0;
    protected final static boolean PING_TIMESTAMPS_DEFAULT = false;
    protected final static boolean PING_ROUTE_DEFAULT = false;
    protected final static boolean PING_IP_VERSION_DEFAULT = false;
    protected final static String PING_ADDRESS_DEFAULT = "9.9.9.9";

    protected final static String PINGER_PREF_FILE = "PINGER";

    protected final static String PING_COUNT_PREF = "c";
    protected final static String PING_SIZE_PREF = "s";
    protected final static String PING_INTERVAL_PREF = "i";
    protected final static String PING_TTL_PREF = "t";
    protected final static String PING_DEADLINE_PREF = "w";
    protected final static String PING_TIMESTAMPS_PREF = "T";
    protected final static String PING_ROUTE_PREF = "R";
    protected final static String PING_ADDRESS_PREF = "ADDRESS";
    protected final static String PING_IP_VERSION_PREF = "IP_VERSION";

}
