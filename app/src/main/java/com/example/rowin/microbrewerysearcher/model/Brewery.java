package com.example.rowin.microbrewerysearcher.model;

import android.location.Address;
import android.location.Location;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Rowin on 9/24/2017.
 */

public class Brewery{
    private String breweryName;
    private String address;
    private String zipcode;
    private String city;
    private JSONArray openingTimes;
    private Double longitude;
    private Double latitude;
    private Beer beer;
    private Integer distanceBetweenBrewery;

    public Brewery(String name, Beer beer, String address, String zipcode, String city, double latitude, double longitude, JSONArray openingTimes){
        this.breweryName = name;
        this.address = address;
        this.zipcode = zipcode;
        this.city = city;
        this.openingTimes = openingTimes;
        this.latitude = latitude;
        this.longitude = longitude;
        this.setBeer(beer);
    }

    public String getBreweryName() {
        return breweryName;
    }

    public void setBreweryName(String name) {
        this.breweryName = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public JSONArray getOpeningTimes() {
        return openingTimes;
    }

    public void setOpeningTimes(JSONArray openingTimes) {
        this.openingTimes = openingTimes;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }


    public static void setDistanceByInput(Address input, ArrayList<Brewery> breweryList){
        for(int i = 0; i < breweryList.size(); i++){
            float[] results = new float[1];
            Brewery currentBrewery = breweryList.get(i);
            Location.distanceBetween(input.getLatitude(), input.getLongitude(), currentBrewery.getLatitude(), currentBrewery.getLongitude(), results);
            Integer distanceInKilometers = Math.round(results[0]/1000);
            currentBrewery.setDistanceBetweenBrewery(distanceInKilometers);
        }
    }

    public Beer getBeer() {
        return beer;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public Integer getDistanceBetweenBrewery() {
        return distanceBetweenBrewery;
    }

    public void setDistanceBetweenBrewery(Integer distanceBetweenBrewery) {
        this.distanceBetweenBrewery = distanceBetweenBrewery;
    }
}
