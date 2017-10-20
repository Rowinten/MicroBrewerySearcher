package com.example.rowin.microbrewerysearcher.activity;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.rowin.microbrewerysearcher.R;
import com.example.rowin.microbrewerysearcher.adapter.CustomRecyclerAdapter;
import com.example.rowin.microbrewerysearcher.adapter.InfoWindowAdapter;
import com.example.rowin.microbrewerysearcher.model.Beer;
import com.example.rowin.microbrewerysearcher.model.Brewery;
import com.example.rowin.microbrewerysearcher.util.DistanceComparator;
import com.example.rowin.microbrewerysearcher.util.Geocoding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;

    private EditText postalcode_edit_text;
    public CardView errorContainer;
    private DrawerLayout drawerLayout;
    private RecyclerView drawerList;
    private ImageButton drawerIcon;
    private ArrayList<Brewery> breweryList = new ArrayList<>();

    Geocoding geocoding = new Geocoding(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initializeViews();
        drawerIcon.setEnabled(false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Focus Netherlands at startup
        LatLng NL = new LatLng(52.093007639638216, 5.4052734375);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NL, 7));

        //Executes asynctask with given urls
        String[] urls = {"http://downloads.oberon.nl/opdracht/brouwerijen.php", "http://downloads.oberon.nl/opdracht/bieren.php"};
        new RequestTask().execute(urls);
    }

    //Initializes all views
    private void initializeViews(){
        postalcode_edit_text = (EditText) findViewById(R.id.postalcode_edit_text);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (RecyclerView) findViewById(R.id.left_drawer);
        errorContainer = (CardView) findViewById(R.id.error_container);
        drawerIcon = (ImageButton) findViewById(R.id.nav_drawer_icon);
    }

    //Opens the Navigation Drawer
    public void openDrawer(View view){
        drawerLayout.openDrawer(Gravity.START);
    }

    public void showNearestBreweryOnMap(View view){
        String input = postalcode_edit_text.getText().toString();
        //Gets lat & long from input
        Address inputCoordinates = geocoding.getCoordinatesFrom(input);
        //Checks if the input is actually a zipcode or not and if the input returns a valid address
        if(isInputZipCode(input) && inputCoordinates != null){
            //hides error message, sets drawer icon button on enabled and hides the soft input keyboard.
            errorContainer.setVisibility(View.INVISIBLE);
            drawerIcon.setEnabled(true);
            hideKeyboard(view);

            //sets distance from input for all breweries, if coordinates from input != null
            Brewery.setDistanceByInput(inputCoordinates, breweryList);
            //sorts the distance for all breweries from input from closest to furthest
            Collections.sort(breweryList, new DistanceComparator());
            //Sets the adapter for the recycler view, in navigation drawer
            setRecyclerViewAdapter();

            //Creates marker with info of closest brewery
            Brewery nearestBrewery = breweryList.get(0);
            createAndZoomToMarker(nearestBrewery);

        } else{
            //Shows the container, containing the error message
            errorContainer.setVisibility(View.VISIBLE);
        }
    }

    public void createAndZoomToMarker(Brewery nearestBrewery) {
        removePreviousMarker();

        //sets adapter for a custom info window layout
        mMap.setInfoWindowAdapter(new InfoWindowAdapter(this, nearestBrewery));
        LatLng latLng = new LatLng(nearestBrewery.getLatitude(), nearestBrewery.getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(nearestBrewery.getBreweryName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.beer_icon)));
        marker.setPosition(latLng);
        marker.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void removePreviousMarker(){
        if(marker!=null){
            marker.remove();
        }
    }

    private void setRecyclerViewAdapter(){
        drawerList.setAdapter(new CustomRecyclerAdapter(this, breweryList, new CustomRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Brewery brewery) {

                createAndZoomToMarker(brewery);
            }
        }));
        drawerList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private Boolean isInputZipCode(String input){
        String regex = "^[1-9][0-9]{3}\\s?[a-zA-Z]{2}$";

        return input.matches(regex);
    }

    public static Boolean isBreweryOpen(JSONArray openingTimes){
        String currentDay = getCurrentDayOfWeek();

        return openingTimes.toString().contains(currentDay);
    }

    private static String getCurrentDayOfWeek(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        Date date = new Date();

        return sdf.format(date);
    }

    private class RequestTask extends AsyncTask<String, String, String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL breweryUrl = new URL(urls[0]);
                URL beerUrl = new URL(urls[1]);
                String breweryResponse = getResponseFrom(breweryUrl);
                String beerResponse = getResponseFrom(beerUrl);

                storeJsonValues(breweryResponse, beerResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void storeJsonValues(String breweryResponseContent, String beerResponseContent){
            try {
                JSONObject breweryResponse = new JSONObject(breweryResponseContent);
                JSONArray breweryJsonArray = breweryResponse.getJSONArray("breweries");

                JSONObject beerResponse = new JSONObject(beerResponseContent);
                JSONArray beerJsonArray = beerResponse.getJSONArray("beers");

                for(int i = 0; i < breweryJsonArray.length(); i++){
                    JSONObject breweryJsonObject = breweryJsonArray.getJSONObject(i);
                    createBreweryObjectFrom(breweryJsonObject, beerJsonArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private void createBreweryObjectFrom(JSONObject breweryJsonObject, JSONArray beerJsonArray) throws JSONException{
            String breweryName = breweryJsonObject.getString("name");
            String breweryAddress = breweryJsonObject.getString("address");
            String breweryZipcode = breweryJsonObject.getString("zipcode");
            String breweryCity = breweryJsonObject.getString("city");
            JSONArray openingTimes = breweryJsonObject.getJSONArray("open");

            Address address = geocoding.getCoordinatesFrom(breweryAddress + ", " + breweryCity);
            Double latitude = address.getLatitude();
            Double longitude = address.getLongitude();

            Beer beerObject = createBeerObjectFrom(beerJsonArray, breweryName);

            Brewery brew = new Brewery(breweryName, beerObject, breweryAddress,breweryZipcode,breweryCity,latitude, longitude, openingTimes);
            breweryList.add(brew);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private Beer createBeerObjectFrom(JSONArray beerJsonArray, String breweryName) throws JSONException{
            Beer beer = null;
            for(int j = 0; j < beerJsonArray.length(); j++){
                JSONObject beerObject = beerJsonArray.getJSONObject(j);
                String breweryNameOfBeer = beerObject.getString("brewery");
                if(Objects.equals(breweryNameOfBeer, breweryName)){
                    String beerName = beerObject.getString("name");
                    String beerStyle = beerObject.getString("style");
                    Integer beerVolume = beerObject.getInt("volume");
                    Integer beerAlcohol = beerObject.getInt("alcohol");
                    String beerKeg = beerObject.getString("keg");
                    beer = new Beer(beerName, beerStyle, beerVolume, beerAlcohol, beerKeg);
                }
            }

            return beer;
        }

        private String getResponseFrom(URL url) throws IOException{
            String responseContent = null;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                responseContent = readStream(connection.getInputStream());
                connection.disconnect();
            }

            return responseContent;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuilder response = new StringBuilder();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }
    }
}

