package com.djx.pin.beans;

/**
 * Created by Administrator on 2016/6/27.
 */
public class LocationNearbyInfo {
    private String location;
    private String locationRoad;

    public LocationNearbyInfo(String location, String locationRoad) {
        this.location = location;
        this.locationRoad = locationRoad;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationRoad() {
        return locationRoad;
    }

    public void setLocationRoad(String locationRoad) {
        this.locationRoad = locationRoad;
    }

    @Override
    public String toString() {
        return "LocationNearbyInfo{" +
                "location='" + location + '\'' +
                ", locationRoad='" + locationRoad + '\'' +
                '}';
    }
}
