package com.dsingley.proxyautoconfiguration.util;

import lombok.experimental.UtilityClass;

import java.security.Security;


@UtilityClass
public class NetworkAddresses {
    private static final String PROPERTY_CACHE_TTL = "networkaddress.cache.ttl";
    private static final String PROPERTY_NEGATIVE_CACHE_TTL = "networkaddress.cache.negative.ttl";

    /**
     *  @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/net/properties.html">https://docs.oracle.com/javase/8/docs/technotes/guides/net/properties.html</a>
     */
    public static void disableCaching() {
        setCacheTtl(0);
        setNegativeCacheTtl(0);
    }

    /**
     *  @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/net/properties.html">https://docs.oracle.com/javase/8/docs/technotes/guides/net/properties.html</a>
     */
    public static void setCacheTtl(int seconds) {
        Security.setProperty(PROPERTY_CACHE_TTL, String.valueOf(seconds));
    }

    /**
     *  @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/net/properties.html">https://docs.oracle.com/javase/8/docs/technotes/guides/net/properties.html</a>
     */
    public static void setNegativeCacheTtl(int seconds) {
        Security.setProperty(PROPERTY_NEGATIVE_CACHE_TTL, String.valueOf(seconds));
    }
}
