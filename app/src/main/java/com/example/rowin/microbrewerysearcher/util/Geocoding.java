package com.example.rowin.microbrewerysearcher.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

/**
 * Created by Rowin on 9/25/2017.
 */

public class Geocoding {
    private Geocoder geocoder;

    public Geocoding(Context context){
        geocoder = new Geocoder(context);
    }

    public Address getCoordinatesFrom(String postalCode) {
        List<Address> address_list = null;
        Address address = null;

        try {
            address_list = geocoder.getFromLocationName(postalCode, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address_list != null && address_list.size() > 0) {
            address = address_list.get(0);

        }
        return address;
    }
}
