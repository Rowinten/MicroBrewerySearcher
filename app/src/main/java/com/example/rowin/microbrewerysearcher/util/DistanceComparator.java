package com.example.rowin.microbrewerysearcher.util;

import com.example.rowin.microbrewerysearcher.model.Brewery;

import java.util.Comparator;

/**
 * Created by Rowin on 10/1/2017.
 */

public class DistanceComparator implements Comparator<Brewery> {

    @Override
    public int compare(Brewery o1, Brewery o2) {
        return o1.getDistanceBetweenBrewery() - o2.getDistanceBetweenBrewery();
    }
}

