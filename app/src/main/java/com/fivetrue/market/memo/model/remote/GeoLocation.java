package com.fivetrue.market.memo.model.remote;

/**
 * Created by kwonojin on 2017. 3. 14..
 */

public class GeoLocation {

    private Location location;
    private float accuracy;

    public Location getLocation() {
        return location;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public static class Location{
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }


        @Override
        public String toString() {
            return "Location{" +
                    "lat=" + lat +
                    ", lng=" + lng +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GeoLocation{" +
                "location=" + location +
                ", accuracy=" + accuracy +
                '}';
    }
}
