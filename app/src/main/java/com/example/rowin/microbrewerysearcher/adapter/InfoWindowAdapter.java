package com.example.rowin.microbrewerysearcher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.example.rowin.microbrewerysearcher.R;
import com.example.rowin.microbrewerysearcher.activity.MapsActivity;
import com.example.rowin.microbrewerysearcher.model.Brewery;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Rowin on 9/29/2017.
 */

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;
    private Brewery nearestBrewery;
    private View myContentView;
    private TextView breweryName, openText, address, beerName, beerStyle, beerVolume, beerAlcohol, beerKeg;
    private ImageView openImage;
    private Context context;

    public InfoWindowAdapter(Context context, Brewery nearestBrewery){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.nearestBrewery = nearestBrewery;
        this.context = context;
    }

    private void initializeView(){
        myContentView = inflater.inflate(R.layout.custom_info_window, null);
        breweryName = (TextView) myContentView.findViewById(R.id.brewery_name);
        openText = (TextView) myContentView.findViewById(R.id.open_text);
        address = (TextView) myContentView.findViewById(R.id.address);
        beerName = (TextView) myContentView.findViewById(R.id.beer_name);
        beerStyle = (TextView) myContentView.findViewById(R.id.beer_style);
        beerVolume = (TextView) myContentView.findViewById(R.id.beer_volume);
        beerAlcohol = (TextView) myContentView.findViewById(R.id.beer_alcohol);
        beerKeg = (TextView) myContentView.findViewById(R.id.beer_keg);
        openImage = (ImageView) myContentView.findViewById(R.id.open_image);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Boolean breweryOpen = MapsActivity.isBreweryOpen(nearestBrewery.getOpeningTimes());

        initializeView();

        if (breweryOpen) {
            openImage.setImageResource(R.drawable.green_round);
            openText.setText(R.string.brewery_opened);
        } else {
            openImage.setImageResource(R.drawable.red_round);
            openText.setText(R.string.brewery_closed);
        }

        breweryName.setText(marker.getTitle());
        address.setText(context.getString(R.string.te_address, nearestBrewery.getAddress(), nearestBrewery.getCity()));

        if(nearestBrewery.getBeer() != null) {

            beerName.setText(context.getString(R.string.sells, nearestBrewery.getBeer().getBeerName()));
            beerStyle.setText(context.getString(R.string.style, nearestBrewery.getBeer().getStyle()));
            beerVolume.setText(context.getString(R.string.volume, nearestBrewery.getBeer().getVolume()));
            beerAlcohol.setText(context.getString(R.string.alcohol, nearestBrewery.getBeer().getAlcohol()));
            beerKeg.setText(context.getString(R.string.keg, nearestBrewery.getBeer().getKeg()));
        } else {
            LayoutParams openTextParams = (LayoutParams)openText.getLayoutParams();
            LayoutParams openImageParams = (LayoutParams) openImage.getLayoutParams();
            openTextParams.addRule(RelativeLayout.BELOW, address.getId());
            openImageParams.addRule(RelativeLayout.BELOW, address.getId());

            beerName.setVisibility(View.GONE);
            beerStyle.setVisibility(View.GONE);
            beerVolume.setVisibility(View.GONE);
            beerAlcohol.setVisibility(View.GONE);
            beerKeg.setVisibility(View.GONE);
        }


        return myContentView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
