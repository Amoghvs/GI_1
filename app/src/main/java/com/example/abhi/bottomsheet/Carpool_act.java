package com.example.abhi.bottomsheet;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;

import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.abhi.bottomsheet.POJO.*;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


import java.io.IOException;
import java.text.DecimalFormat;

import java.util.Locale;
import java.util.Random;


public class Carpool_act extends FragmentActivity implements OnMapReadyCallback, PlaceSelectionListener {

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;
        public TextView tvTitle;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.info_win, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            tvTitle= ((TextView)myContentsView.findViewById(R.id.title));
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

    private static final String LOG_TAG = "PlaceSelectionListener";

    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
            .setCountry("IN")
            .build();
    private GoogleMap mMap;
    GPSTracker gps;
    public double latitude,longitude,new_lat,new_lng,calc_dist;
    public LatLng latLng,m_latlng;
    public  LatLng my_loc;
    public List<Address> addresses;
    public String address,city,state,country,knownName,postalCode;
    int i=0,rando,j=0;
    public double val;
    Marker marker,j1,j2,j3,i1,i2,i3;
    Button mylocation,join,initiate;
    char k,n;
    String distance,dista,time,ret_dis,ret_dur;
    Polyline line;
    private static final String TAG = Recycler_act.class.getSimpleName();
    TextView dur,dis;
    CardView cardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_act);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cardView = findViewById(R.id.li);
        cardView.setVisibility(View.INVISIBLE);
        dur = findViewById(R.id.duration);
        dis = findViewById(R.id.distance);
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
            Toast.makeText(getApplicationContext(),address,Toast.LENGTH_SHORT).show();


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
        cardView.setVisibility(View.VISIBLE);
        initiate.setVisibility(View.VISIBLE);
        join.setVisibility(View.VISIBLE );
        Log.i(LOG_TAG, "Place Selected: " + place.getName());
        latLng = place.getLatLng();
        if (i>1){
            marker.remove();
        }
        build_retrofit_and_get_response("driving");
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
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());

        place_marker();
    }

    private void place_marker() {
        j++;
        if (j>1){
            remove_marker();
        }

        Random r = new Random();
        rando = r.nextInt(10 - 1) + 1;
        val=rando*0.001;

        new_lat=latitude+val;
        new_lng=longitude+val;
        ret_dis= dist_btw(new_lat,new_lng);
        ret_dur= dur_btw(new_lat,new_lng);

        j1 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(new_lat, new_lng))
                .infoWindowAnchor(0.5f, 0.5f)
                .title("Akash")
                .snippet(ret_dis +"   "+ret_dur)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        ///mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);


        rando = r.nextInt(10 - 1) + 1;
        val=rando*0.001;
        new_lat=latitude-val;
        new_lng=longitude+val;
        ret_dis= dist_btw(new_lat,new_lng);
        ret_dur= dur_btw(new_lat,new_lng);

        j2 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(new_lat, new_lng))
                .anchor(0.5f, 0.5f)
                .title("Abhinav")
                .snippet(ret_dis +"   "+ret_dur)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        rando = r.nextInt(10 - 1) + 1;
        val=rando*0.001;
        new_lat=latitude-val;
        new_lng=longitude-val;
        ret_dis= dist_btw(new_lat,new_lng);
        ret_dur= dur_btw(new_lat,new_lng);
        j3 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(new_lat, new_lng))
                .anchor(0.5f, 0.5f)
                .title("Shivam")
                .snippet(ret_dis +"   "+ret_dur)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        rando = r.nextInt(10 - 1) + 1;
        val=rando*0.001;
        new_lat=latitude+val;
        new_lng=longitude-val;
        ret_dis= dist_btw(new_lat,new_lng);
        ret_dur= dur_btw(new_lat,new_lng);
        i1 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(new_lat, new_lng))
                .anchor(0.5f, 0.5f)
                .title("Sasidhar")
                .snippet(ret_dis +"   "+ret_dur)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        rando = r.nextInt(10 - 1) + 1;
        val=rando*0.001;
        new_lat=latitude-val;
        new_lng=longitude-val;
        ret_dis= dist_btw(new_lat,new_lng);
        ret_dur= dur_btw(new_lat,new_lng);
        i2 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(new_lat, new_lng))
                .anchor(0.5f, 0.5f)
                .title("Somanath")
                .snippet(ret_dis +"   "+ret_dur)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        rando = r.nextInt(10 - 1) + 1;
        val=rando*0.001;
        new_lat=latitude-val;
        new_lng=longitude+val;
        ret_dis= dist_btw(new_lat,new_lng);
        ret_dur= dur_btw(new_lat,new_lng);
        i3 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(new_lat, new_lng))
                .anchor(0.5f, 0.5f)
                .title("Mohit")
                .snippet(ret_dis +"   "+ret_dur)
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

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

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
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
    }

    private void build_retrofit_and_get_response(String type) {

        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);

        Call<Example> call = service.getDistanceDuration("metric", latitude + "," + longitude,latLng.latitude + "," + latLng.longitude, type);

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response, Retrofit retrofit) {

                try {
                    //Remove previous line from map
                    if (line != null) {
                        line.remove();
                    }
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < response.body().getRoutes().size(); i++) {
                        String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                        String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                        dur.setText(time);
                        dis.setText(distance);
                        //ShowDistanceDuration.setText("Distance:" + distance + ", Duration:" + time);
                        String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                        List<LatLng> list = decodePoly(encodedString);
                        line = mMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(15)
                                //.color(Color)
                                .color(Color.CYAN)
                                .geodesic(true)
                        );
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

    }


    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    private String dist_btw(double m_lat, double m_lng) {

        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);

        Call<Example> call = service.getDistanceDuration("metric", latitude + "," + longitude, m_lat + "," + m_lng, "driving");

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response, Retrofit retrofit) {


                // This loop will go through all the results and add marker on each location.
                for (int i = 0; i < response.body().getRoutes().size(); i++) {
                     dista = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();

                }
            }


            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

        return dista;
    }

    private String dur_btw(double m_lat, double m_lng) {

        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);

        Call<Example> call = service.getDistanceDuration("metric", latitude + "," + longitude, m_lat + "," + m_lng, "driving");

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response, Retrofit retrofit) {


                // This loop will go through all the results and add marker on each location.
                for (int i = 0; i < response.body().getRoutes().size(); i++) {
                     time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();

                }
            }


            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

        return time;
    }

    private void remove_marker(){
        j1.remove();
        j2.remove();
        j3.remove();
        i1.remove();
        i2.remove();
        i3.remove();
    }


}
