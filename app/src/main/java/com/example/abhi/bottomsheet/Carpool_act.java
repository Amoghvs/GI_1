package com.example.abhi.bottomsheet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Carpool_act extends FragmentActivity implements OnMapReadyCallback, PlaceSelectionListener {

    private static final String LOG_TAG = "PlaceSelectionListener";

    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
            .setCountry("IN")
            .build();



    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.info_win, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }


    private GoogleMap mMap;
    GPSTracker gps;
    public double latitude,longitude,new_lat,new_lng;
    public LatLng latLng;
    public  LatLng my_loc;
    public List<Address> addresses;
    public String address,city,state,country,knownName,postalCode;
    int i=0,rando,final_val;
    public double val;
    Marker marker,j1,j2,j3,i1,i2,i3;
    Button mylocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_act);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setHint("Search your Destination");
        // autocompleteFragment.setBoundsBias(BOUNDS_MOUNTAIN_VIEW);
        autocompleteFragment.setFilter(typeFilter);


        mylocation = (Button) findViewById(R.id.my_loc);
        mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pont_myloc();
            }
        });

        gps = new GPSTracker(Carpool_act.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Geocoder geocoder;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }

            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();

            // \n is for new line
            // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }

    @Override
    public void onPlaceSelected(Place place) {
        i++;
        Log.i(LOG_TAG, "Place Selected: " + place.getName());
        latLng = place.getLatLng();
        if (i>1){
            marker.remove();
        }
        marker= mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title(knownName + address)
                .snippet(city +", "+state));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }, 1000);
        mMap.setTrafficEnabled(true);
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());


    }

    @Override
    public void onError(Status status) {
        Log.e(LOG_TAG, "onError: Status = " + status.toString());
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setAllGesturesEnabled(true);
        // Add a marker in Sydney and move the camera
        my_loc = new LatLng(latitude, longitude);
        pont_myloc();
    }

    private void pont_myloc() {
        mMap.addMarker(new MarkerOptions().position(my_loc).title(knownName + address).snippet(city +", "+state));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(my_loc));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my_loc, 10));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(my_loc, 16));
            }
        }, 1000);
        mMap.setTrafficEnabled(true);
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
    }


}
