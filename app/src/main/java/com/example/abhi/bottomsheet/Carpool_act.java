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
import java.text.DecimalFormat;
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
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_SHORT).show();
                }
            });

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private static final String LOG_TAG = "PlaceSelectionListener";


    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
            .setCountry("IN")
            .build();
    private GoogleMap mMap;
    GPSTracker gps;
    public double latitude,longitude,new_lat,new_lng,calc_dist;
    public LatLng latLng;
    public  LatLng my_loc;
    public List<Address> addresses;
    public String address,city,state,country,knownName,postalCode;
    int i=0,rando,j=0;
    public double val;
    Marker marker,j1,j2,j3,i1,i2,i3;
    Button mylocation,join,initiate;
    char k,n;

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


        initiate = (Button) findViewById(R.id.initiateb);
        initiate.setVisibility(View.INVISIBLE);
        initiate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i1.setVisible(false);
                i2.setVisible(false);
                i3.setVisible(false);
                j1.setVisible(true);
                j2.setVisible(true);
                j3.setVisible(true);
            }
        });
        join = (Button) findViewById(R.id.joinb);
        join.setVisibility(View.INVISIBLE);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                j1.setVisible(false);
                j2.setVisible(false);
                j3.setVisible(false);
                i1.setVisible(true);
                i2.setVisible(true);
                i3.setVisible(true);
            }
        });
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
        initiate.setVisibility(View.VISIBLE);
        join.setVisibility(View.VISIBLE );
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        }, 1000);
        mMap.setTrafficEnabled(true);
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());

        place_marker();
    }

    private void place_marker() {
        j++;
        if (j>1){
            j1.remove();
            j2.remove();
            j3.remove();
            i1.remove();
            i2.remove();
            i3.remove();
        }
        Random r = new Random();
        rando = r.nextInt(10 - 1) + 1;
        val=rando*0.001;

        new_lat=latitude+val;
        new_lng=longitude+val;
        calc_dist=distance(latitude,longitude,new_lat,new_lng,n);

        j1 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(new_lat, new_lng))
                .infoWindowAnchor(0.5f, 0.5f)
                .title("Akash")
                .snippet(Double.toString(calc_dist)+" km away")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        ///mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);


        rando = r.nextInt(10 - 1) + 1;
        val=rando*0.001;
        new_lat=latitude-val;
        new_lng=longitude+val;
        calc_dist=distance(latitude,longitude,new_lat,new_lng,k);

        j2 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(new_lat, new_lng))
                .anchor(0.5f, 0.5f)
                .title("Abhinav")
                .snippet(Double.toString(calc_dist)+" km away")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        rando = r.nextInt(10 - 1) + 1;
        val=rando*0.001;
        new_lat=latitude-val;
        new_lng=longitude-val;
        calc_dist=distance(latitude,longitude,new_lat,new_lng,k);
        j3 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(new_lat, new_lng))
                .anchor(0.5f, 0.5f)
                .title("Shivam")
                .snippet(Double.toString(calc_dist)+" km away")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        rando = r.nextInt(10 - 1) + 1;
        val=rando*0.001;
        new_lat=latitude+val;
        new_lng=longitude-val;
        calc_dist=distance(latitude,longitude,new_lat,new_lng,k);
        i1 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(new_lat, new_lng))
                .anchor(0.5f, 0.5f)
                .title("Sasidhar")
                .snippet(Double.toString(calc_dist)+" km away")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        rando = r.nextInt(10 - 1) + 1;
        val=rando*0.001;
        new_lat=latitude-val;
        new_lng=longitude-val;
        calc_dist=distance(latitude,longitude,new_lat,new_lng,n);
        i2 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(new_lat, new_lng))
                .anchor(0.5f, 0.5f)
                .title("Somanath")
                .snippet(Double.toString(calc_dist)+" km away")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        rando = r.nextInt(10 - 1) + 1;
        val=rando*0.001;
        new_lat=latitude-val;
        new_lng=longitude+val;
        calc_dist=distance(latitude,longitude,new_lat,new_lng,n);
        i3 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(new_lat, new_lng))
                .anchor(0.5f, 0.5f)
                .title("Mohit")
                .snippet(Double.toString(calc_dist)+" km away")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));



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


    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'k') {
            dist = dist * 1.609344;
        } else if (unit == 'n') {
            dist = dist * 0.8684;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        dist = Double.valueOf(df.format(dist));
        return (dist);
    }
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
