package com.example.watercheckapp;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;

import com.example.watercheckapp.databinding.ActivityMapBinding;
import com.example.watercheckapp.home.MyHomeService;
import com.example.watercheckapp.sensors.SensorsActivity;
import com.example.watercheckapp.settings.SettingsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends DrawerBaseActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    ActivityMapBinding activityMapBinding;
    JSONMethods jsonMethods = new JSONMethods();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMapBinding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(activityMapBinding.getRoot());
        allocateActivityTitle("Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_layout);
        mapFragment.getMapAsync(this);
        MyHomeService.checkRedundance();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
//        if(!MqttCallbackImpl.finishedProcessFromThread) {

            Marker[] allMarkers = new Marker[JSONMethods.sensorsList.size()];


            for (int i = 0; i < JSONMethods.sensorsList.size(); i++) {
                LatLng sensorPosition = new LatLng(Double.parseDouble(JSONMethods.sensorsList.get(i).getLang_s()), Double.parseDouble(JSONMethods.sensorsList.get(i).getLong_s()));
                String sensorNumber = String.valueOf(JSONMethods.sensorsList.get(i).getSensor_number());
                String measurementsFromJSON = "PH: " + JSONMethods.sensorsList.get(i).getPh() + "   Water level: " + JSONMethods.sensorsList.get(i).getWater_level();
                allMarkers[i] = googleMap.addMarker(new MarkerOptions()
                        .position(sensorPosition)
                        .title("Sensor " + sensorNumber)
                        .snippet(measurementsFromJSON));
                googleMap.setOnInfoWindowLongClickListener(this::onInfoWindowLongClick);

            }

            LatLng sensorPosition2 = new LatLng(Double.parseDouble(JSONMethods.sensorsList.get(0).getLang_s()), Double.parseDouble(JSONMethods.sensorsList.get(0).getLong_s()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sensorPosition2));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sensorPosition2, 10.0f));

        //}
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        startActivity(new Intent(this, SensorsActivity.class));
        overridePendingTransition(0,0);

    }
    public void onInfoWindowLongClick(Marker marker){
        String splitString = marker.getTitle().toString();
        String[] sArray = splitString.split(" ");
        for (int i=0;i<JSONMethods.sensorsList.size();i++){
            if(JSONMethods.sensorsList.get(i).getSensor_number()==Integer.parseInt(sArray[1])){
                JSONMethods.chosenSensor = JSONMethods.sensorsList.get(i).getSensor_id();
            }
        }

        startActivity(new Intent(this, SettingsActivity.class));
        overridePendingTransition(0,0);

    }

}

