package com.example.toVisit_Varinder_C0779368_android.placesApi;

import android.os.AsyncTask;
import android.util.Log;

import com.example.toVisit_Varinder_C0779368_android.resultData.Result;
import com.example.toVisit_Varinder_C0779368_android.resultData.ResultData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearPlaces extends AsyncTask<Object, String, String> {
    String place_data, url_location;
    GoogleMap map;


    @Override
    protected String doInBackground(Object... objects) {
        map = (GoogleMap) objects[0];
        url_location = (String) objects[1];

        FetchUrl url = new FetchUrl();
        try {
            place_data = url.readUrl(url_location);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return place_data;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("RESULT", s);
        Gson gson = new Gson();
        ResultData resultData = gson.fromJson(s, ResultData.class);
//        List<HashMap<String, String>> placeList = null;
        ArrayList<ResultData> placeList = new ArrayList<>();
        DataParser dataParser = new DataParser();
//        placeList = dataParser.parse(s);
        placeList.add(resultData);
        showNearByPlaces(placeList);

    }


    private void showNearByPlaces(ArrayList<ResultData> placesList) {
        for (int i = 0; i < placesList.get(0).getResults().size(); i++) {
            Result result=placesList.get(0).getResults().get(i);
            MarkerOptions markerOptions = new MarkerOptions();
            String name = result.getName();
            String vicinity =result.getVicinity();
            double lat = result.getGeometry().getLocation().getLat();
            double longi = result.getGeometry().getLocation().getLng();

            LatLng latLng = new LatLng(lat, longi);
            markerOptions.position(latLng);

            markerOptions.title(name + ":" + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            map.addMarker(markerOptions);
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.moveCamera(CameraUpdateFactory.zoomTo(10));

        }

    }
}



