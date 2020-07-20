package com.example.toVisit_Varinder_C0779368_android.placesApi;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

public class FetchDirectionData extends AsyncTask<Object,String ,String>{

    String googleDirectionsData;
    GoogleMap mMap;
    String url;

    String distance;
    String duration;

    LatLng latLng/*, userL*/;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];
//        userL = (LatLng) objects[3];
        FetchUrl fetchURL = new FetchUrl();
        try {
            googleDirectionsData = fetchURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {

        HashMap<String, String> distances = null;
        DataParser distancesParser = new DataParser();
        distances = distancesParser.parseDistance(s);

        distance = distances.get("distance");
        duration = distances.get("duration");

        Log.i("Main", "onPostExecute: " + distance +"..." + duration);

        mMap.clear();
        // we create marker options
        System.out.println("snippet");
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .draggable(false)
                .title("Duration : " + duration)
                .snippet("Distance : " + distance);
        mMap.addMarker(markerOptions);


//        MarkerOptions markerOptions2 = new MarkerOptions()
//                .position(userL)
//                .draggable(false)
//                .title("Duration : " + duration).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//                .snippet("Distance : " + distance);
//        mMap.addMarker(markerOptions2);

        /*---------------------------------*/
//
        String[] directionsList;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(s);
        Log.d("", "onPostExecute: " + directionsList);
        displayDirections(directionsList);

    }

    private void displayDirections(String[] directionsList) {
        int count = directionsList.length;
        for (int i = 0; i < count; i++) {
            PolylineOptions options = new PolylineOptions()
                    .color(Color.RED)
                    .width(10)
                    .addAll(PolyUtil.decode(directionsList[i]));
            mMap.addPolyline(options);
        }
    }

}

