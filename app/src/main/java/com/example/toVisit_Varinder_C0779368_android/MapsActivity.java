package com.example.toVisit_Varinder_C0779368_android;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.toVisit_Varinder_C0779368_android.databaseHelper.FavouritePlaceDatabase;
import com.example.toVisit_Varinder_C0779368_android.placesApi.FetchDirectionData;
import com.example.toVisit_Varinder_C0779368_android.placesApi.NearPlaces;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions markerOptions = new MarkerOptions();
    private EditText placeText;
    private Button btnFind;
    private int PROXIMITY_RADIUS = 40000;
    private FavouritePlaceDatabase appDb;
    private double latitude;
    private double longitude;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private LatLng customMarker;
    private LatLng currentLocation;
    private final int REQUEST_CODE = 1;
    private Geocoder geocoder;
    private List<Address> addresses;
    private String address;
    private Spinner spinnerMapType;
    private AlertDialog.Builder builder;
    private String favouriteLocationTitle;
    private boolean isFromList = false;
    private double favouriteLatitude;
    private double favouriteLongitude;
    private int favouritePlaceId;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        setContentView(R.layout.activity_maps);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            favouritePlaceId = bundle.getInt("placeId", 0);
            favouriteLatitude = bundle.getDouble("latitude", 0);
            favouriteLongitude = bundle.getDouble("longitude", 0);
            favouriteLocationTitle = bundle.getString("title", null);
            isFromList = bundle.getBoolean("isFromList", false);
        }

        initMap();
        initViews();

        spinnerMapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {

                    case 0:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        Toast.makeText(MapsActivity.this, "Normal Map Selected", Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        Toast.makeText(MapsActivity.this, "Hybrid Map Selected", Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        Toast.makeText(MapsActivity.this, "Normal  Map Selected", Toast.LENGTH_SHORT).show();
                        break;

                    case 3:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        Toast.makeText(MapsActivity.this, "Terrain Map Selected", Toast.LENGTH_SHORT).show();
                        break;

                    case 4:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        Toast.makeText(MapsActivity.this, "Satellite Map Selected", Toast.LENGTH_SHORT).show();
                        break;

                    default:

                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initViews() {
        spinnerMapType = findViewById(R.id.choosethemap);
        builder = new AlertDialog.Builder(this);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.typesofmaps, R.layout.layout_spinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMapType.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getUserLocation();
        setLocationManager();

        appDb = FavouritePlaceDatabase.getInstance(MapsActivity.this);

        if (!isFromList) {
            findViewById(R.id.btn_direction).setVisibility(View.GONE);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mMap.clear();
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                    mMap.addMarker(markerOptions
                            .position(latLng));
                }
            });
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    latitude = marker.getPosition().latitude;
                    longitude = marker.getPosition().longitude;
                    final LatLng latLng = new LatLng(latitude, longitude);
                    showDialogBox(latLng);
                    return true;
                }
            });
        } else {
            findViewById(R.id.btn_restaurant).setVisibility(View.GONE);
            findViewById(R.id.btn_museum).setVisibility(View.GONE);
            findViewById(R.id.btn_cafe).setVisibility(View.GONE);
            findViewById(R.id.btn_clear).setVisibility(View.GONE);

            LatLng latLng = new LatLng(favouriteLatitude, favouriteLongitude);
            googleMap.addMarker(new MarkerOptions().position(latLng)
                    .title(favouriteLocationTitle)
                    .draggable(true));
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                    double latitude = marker.getPosition().latitude;
                    double longitude = marker.getPosition().longitude;
                    LatLng latLng = new LatLng(latitude, longitude);
                    getAddressFromGeoCoder(latLng);
                    addressOfPlaces(latLng);

                }
            });
        }

    }

    private void showDialogBox(final LatLng latLng) {
        builder.setMessage("Add " + getAddressFromGeoCoder(latLng) + " to your favourite list")
                .setTitle("Make Favourite")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addressOfPlaces(latLng);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
//        alert.setTitle("AlertDialogExample");
        alert.show();
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        mMap.setMyLocationEnabled(true);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
        setMyLocation();
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        final String API_KEY = "AIzaSyDgGLi8rHJ3LK1w5MjM3Ed8N3rtkKdGhV4";
        StringBuilder placeUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placeUrl.append("location=" + latitude + "," + longitude);
        placeUrl.append("&radius=" + PROXIMITY_RADIUS);
        placeUrl.append("&type=" + nearbyPlace);
        placeUrl.append("&key=AIzaSyB45lwuNXNnXYsc3WHA1QyJKIkxqE-Rb7A" /*+ getResources().getString(R.string.google_maps_key)*/);
        System.out.println(placeUrl.toString());
        return placeUrl.toString();
    }

    private void setMyLocation() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    currentLocation = userLocation;
                    if (count == 0) {
                        count++;
                        CameraPosition cameraPosition = CameraPosition.builder()
                                .target(userLocation)
                                .zoom(15)
                                .bearing(0)
                                .tilt(45)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        mMap.addMarker(new MarkerOptions().position(userLocation)
                                .title("your location"));
                    }
                }
            }
        };
    }


    private void setLocationManager() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        currentLocation = latLng;
        Log.d("LOCATION", latLng.latitude + "," + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

//    private class GeoCoderHandler extends Handler {
//        @Override
//        public void handleMessage(Message message) {
//            String locationAddress;
//            switch (message.what) {
//                case 1:
//                    Bundle bundle = message.getData();
//                    locationAddress = bundle.getString("address");
//                    break;
//                default:
//                    locationAddress = null;
//            }
//
//            FavouriteData favouriteData = new FavouriteData();
//            favouriteData.setLongitude(longitude);
//            favouriteData.setLatitude(latitude);
//            if (locationAddress.equals("null") || locationAddress.isEmpty()) {
//                favouriteData.setTitle(String.valueOf(System.currentTimeMillis()));
//            } else {
//                favouriteData.setTitle(locationAddress);
//            }
//            appDb.favouriteDataDao().insertFavouritePlaceData(favouriteData);
//            Toast.makeText(MapsActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
//        }
//    }

    private boolean checkPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setMyLocation();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    private void addressOfPlaces(LatLng latLng) {

        Location crntLocation = new Location("crntlocation");
        crntLocation.setLatitude(currentLocation.latitude);
        crntLocation.setLongitude(currentLocation.longitude);

        Location newLocation = new Location("newlocation");
        newLocation.setLatitude(latLng.latitude);
        newLocation.setLongitude(latLng.longitude);


//float distance = crntLocation.distanceTo(newLocation);  in meters
        float distance = crntLocation.distanceTo(newLocation) / 1000; // in km


        FavouriteData favouriteData = new FavouriteData();
        favouriteData.setLatitude(latLng.latitude);
        favouriteData.setLongitude(latLng.longitude);
        favouriteData.setTitle(address);
        favouriteData.setDistance(distance);
        if (isFromList) {
            favouriteData.setPlace_id(favouritePlaceId);
            appDb.favouriteDataDao().updateFavouriteData(favouriteData);

        } else {
            appDb.favouriteDataDao().insertFavouritePlaceData(favouriteData);
        }
    }

    private String getAddressFromGeoCoder(LatLng latLng) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        String addDate = simpleDateFormat.format(calendar.getTime());
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (!addresses.isEmpty()) {
                address = addresses.get(0).getLocality() + " " + addresses.get(0).getAddressLine(0);
                System.out.println(addresses.get(0).getAddressLine(0));
                if (address.equals("null")) {
                    address = addDate;
                }
            } else {
                address = addDate;
            }


        } catch (IOException e) {
            e.printStackTrace();

        }
        return address;
    }

    public void btnClick(View view) {


        Object[] dataTransfer = new Object[2];
        ;
        String url;
        NearPlaces getNearbyPlaceData = new NearPlaces();

        switch (view.getId()) {
            case R.id.btn_restaurant:
                // get the url from place api
//                29.693395, 76.969468
                url = getUrl(currentLocation.latitude, currentLocation.longitude, "restaurant");

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlaceData.execute(dataTransfer);
                Toast.makeText(this, "Restaurants", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_museum:
                url = getUrl(currentLocation.latitude, currentLocation.longitude, "museum");

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlaceData.execute(dataTransfer);
                Toast.makeText(this, "Museum", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_cafe:
                url = getUrl(currentLocation.latitude, currentLocation.longitude, "cafe");
                dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlaceData.execute(dataTransfer);
                Toast.makeText(this, "Cafe", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_clear:
                mMap.clear();
                count = 0;
                break;

            case R.id.btn_direction:
                customMarker = new LatLng(favouriteLatitude, favouriteLongitude);
                dataTransfer = new Object[4];
                url = getDirectionUrl();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = customMarker;
//                dataTransfer[3] = new LatLng(29.693395, 76.969468);
                FetchDirectionData getDirectionsData = new FetchDirectionData();
                // execute asynchronously
                getDirectionsData.execute(dataTransfer);
                break;
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private String getDirectionUrl() {
        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionUrl.append("origin=" + currentLocation.latitude + "," + currentLocation.longitude);
        googleDirectionUrl.append("&destination=" + favouriteLatitude + "," + favouriteLongitude);
        googleDirectionUrl.append("&key=AIzaSyB45lwuNXNnXYsc3WHA1QyJKIkxqE-Rb7A");
        Log.d("", "getDirectionUrl: " + googleDirectionUrl);
        return googleDirectionUrl.toString();
//        29.6982° N, 77.0304° E
    }
}