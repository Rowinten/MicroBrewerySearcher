package com.example.rowin.microbrewerysearcher.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rowin on 9/25/2017.
 */

public class Geocoding {
    private Geocoder geocoder;

    public Geocoding(Context context){
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    public Address getCoordinatesFrom(String address) {
        List<Address> address_list = null;
        Address coordinates = null;

        try {
            Log.v("Address", address);
            address_list = geocoder.getFromLocationName(address, 1);
            Log.v("addressList", String.valueOf(address_list));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address_list != null && address_list.size() > 0) {
            coordinates = address_list.get(0);
        }

        return coordinates;
    }
}
