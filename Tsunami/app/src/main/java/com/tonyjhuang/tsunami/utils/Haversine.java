package com.tonyjhuang.tsunami.utils;

/**
 * Created by tony on 1/22/15.
 */
public class Haversine {
    public static final double R = 6372.8; // In kilometers

    public static float haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (float) (R * c);
    }

    public static float haversineInMiles(double lat1, double lon1, double lat2, double lon2) {
        return haversine(lat1, lon1, lat2, lon2) / 1.609344f;
    }
}
