package com.example.e610.appsinnovatetask;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.e610.appsinnovatetask.Utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    /*** 4.Maps Activity shows random locations on Map GPS and shows and change the label ***/

    private GoogleMap mMap;
    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        gpsTracker=new GPSTracker(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

         double Lat= gpsTracker.getLatitude();
         double Lng= gpsTracker.getLongitude();
        if(gpsTracker.canGetLocation() && Lat!=0 && Lng!=0 ){
            LatLng myLocation= new LatLng(Lat,Lng);

            mMap.addMarker(new MarkerOptions().position(myLocation).title("your location").snippet("your location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

            LatLng location1= new LatLng(Lat+7,Lng);
            mMap.addMarker(new MarkerOptions().position(myLocation).title("other location").snippet("other location"));

            LatLng location2= new LatLng(Lat,Lng+7);
            mMap.addMarker(new MarkerOptions().position(myLocation).title("other location").snippet("other location"));
        }else{
            // Add a marker in Sydney and move the camera
            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").snippet("Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            LatLng cityNearFromSydney = new LatLng(-34, 165);
            mMap.addMarker(new MarkerOptions().position(cityNearFromSydney).title("Marker in cityNearFromSydney").snippet("cityNearFromSydney"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(cityNearFromSydney));

            LatLng cityNearFromSydney1 = new LatLng(-20, 151);
            mMap.addMarker(new MarkerOptions().position(cityNearFromSydney1).title("Marker in cityNearFromSydney1").snippet("cityNearFromSydney1"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(cityNearFromSydney));
        }


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
        mMap.setMyLocationEnabled(true);
    }
}
