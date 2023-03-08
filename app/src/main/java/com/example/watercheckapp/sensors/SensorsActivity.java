package com.example.watercheckapp.sensors;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.watercheckapp.DrawerBaseActivity;
import com.example.watercheckapp.JSONMethods;
import com.example.watercheckapp.MapActivity;
import com.example.watercheckapp.MqttCallbackImpl;
import com.example.watercheckapp.R;
import com.example.watercheckapp.databinding.ActivitySensorBinding;
import com.example.watercheckapp.home.HomeActivity;
import com.example.watercheckapp.home.MyHomeService;
import com.example.watercheckapp.settings.SettingsActivity;

import java.util.ArrayList;

public class SensorsActivity extends DrawerBaseActivity {
     ActivitySensorBinding activitySensorBinding;
     ImageButton mapBtn;
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<SensorsData> s = JSONMethods.sensorsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySensorBinding = ActivitySensorBinding.inflate(getLayoutInflater());
        setContentView(activitySensorBinding.getRoot());
        allocateActivityTitle("Sensors");
        recyclerView = findViewById(R.id.sensorsRecycleView);
        mapBtn = findViewById(R.id.mapBtn);
        swipeRefreshLayout=findViewById(R.id.swiperefreshSensor);
        MyHomeService.checkRedundance();
        s = JSONMethods.sensorsList;



        // dodać tutaj update przy odpytywaniu na początku programu o dostępne czujniki TO:DO

        //JSONMethods.sensorsList.add(new SensorsData(jsonMethods.sensor_id,jsonMethods.ph,"130"));
        //s.add(new SensorsData(jsonMethods.sensor_id,jsonMethods.ph,"130"));

        //SPOSÓB NA ODCZYT DANYCH Z LISTY CZUJNIKÓW!!!!!!!!!!!!

//        for (SensorsData s: sensorsData
//             ) {
//            if(s.getSensor_id().equals("123")){
//
//            }
//
//        }



        SensorsRecViewAdapter adapter = new SensorsRecViewAdapter(this);
        adapter.setSensorsData(s);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        // Your code goes here
                        // In this code, we are just
                        // changing the text in the textbox
                        startActivity(new Intent(getApplicationContext(), SensorsActivity.class));
                        MyHomeService.checkRedundance();

                        // This line is important as it explicitly
                        // refreshes only once
                        // If "true" it implicitly refreshes forever
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
                overridePendingTransition(0,0);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //s.clear();
    }
}