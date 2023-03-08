package com.example.watercheckapp.settings;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.watercheckapp.AOI;
import com.example.watercheckapp.DrawerBaseActivity;
import com.example.watercheckapp.JSONMethods;
import com.example.watercheckapp.MqttCallbackImpl;
import com.example.watercheckapp.R;
import com.example.watercheckapp.alarms.Alarm;
import com.example.watercheckapp.alarms.AlarmTypes;
import com.example.watercheckapp.databinding.ActivitySensorBinding;
import com.example.watercheckapp.databinding.ActivitySettingsBinding;
import com.example.watercheckapp.home.MyHomeService;
import com.example.watercheckapp.sensors.SensorsActivity;
import com.example.watercheckapp.sensors.SensorsData;
import com.example.watercheckapp.sensors.SensorsRecViewAdapter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SettingsActivity extends DrawerBaseActivity {
    //ActivitySettingsBinding activitySettingsBinding;
    ConstraintLayout settingsLayout;
    TextView sensorInfo, maxPhTxt, minPhTxt, maxWaterLvlTxt, minWaterLvlTxt, waterChangeTxt;
    EditText editTextMeasurePeriod, maxPhEdt, minPhEdt, maxWaterLvlEdt, minWaterLvlEdt, waterChangeEdt;
    Button confirmButton;
    MqttCallbackImpl mqttCallback = new MqttCallbackImpl();
    String topic = "sensor/" + JSONMethods.chosenSensor + "/configuration";
    // private RecyclerView recyclerView;

    // TODO: nawigacja od menu z settings

    //TODO: cofanie w settingsach


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        activitySettingsBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
//        setContentView(activitySettingsBinding.getRoot());
//        allocateActivityTitle("Settings");
        setContentView(R.layout.activity_settings);

        //allocateActivityTitle("Settings");
        //settingsLayout = findViewById(R.id.settings_view);
        //recyclerView = findViewById(R.id.settingsRecycleView);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sensorInfo = findViewById(R.id.sensorInfo);
        confirmButton = findViewById(R.id.confirmSettingButton);
        editTextMeasurePeriod = findViewById(R.id.editTextMeasurePeriod);
        maxPhEdt = findViewById(R.id.maxPhEdit);
        minPhEdt = findViewById(R.id.minPhEdit);
        maxWaterLvlEdt = findViewById(R.id.maxWaterLvlEdit);
        minWaterLvlEdt = findViewById(R.id.minWaterLvlEdit);
        waterChangeEdt = findViewById(R.id.waterChangeEdit);


        MyHomeService.checkRedundance();


        for (int i = 0; i < JSONMethods.sensorsList.size(); i++) {
            if (JSONMethods.sensorsList.get(i).getSensor_id().equals(JSONMethods.chosenSensor)) {
                editTextMeasurePeriod.setText(JSONMethods.sensorsList.get(i).getMeasure_period());
                sensorInfo.setText("Sensor " + JSONMethods.sensorsList.get(i).getSensor_number() + "\n\nDescription: " + JSONMethods.sensorsList.get(i).getDescription());
            }
        }


        AOI.findCorrectAlarms(JSONMethods.chosenSensor);

        maxPhEdt.setText(JSONMethods.correctAlarmsList.get(0).getValue());
        minPhEdt.setText(JSONMethods.correctAlarmsList.get(1).getValue());
        maxWaterLvlEdt.setText(JSONMethods.correctAlarmsList.get(2).getValue());
        minWaterLvlEdt.setText(JSONMethods.correctAlarmsList.get(3).getValue());
        waterChangeEdt.setText(JSONMethods.correctAlarmsList.get(4).getValue());


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isDigitsOnly(editTextMeasurePeriod.getText())) {
                    editTextMeasurePeriod.setError("Wrong input");
                    return;
                }
                if (!TextUtils.isDigitsOnly(maxPhEdt.getText())) {
                    maxPhEdt.setError("Wrong input");
                    return;
                }
                if (!TextUtils.isDigitsOnly(minPhEdt.getText())) {
                    minPhEdt.setError("Wrong input");
                    return;
                }
                if (!TextUtils.isDigitsOnly(maxWaterLvlEdt.getText())) {
                    maxWaterLvlEdt.setError("Wrong input");
                    return;
                }
                if (!TextUtils.isDigitsOnly(minWaterLvlEdt.getText())) {
                    minWaterLvlEdt.setError("Wrong input");
                    return;
                }


                int i = 0;
                for (Alarm a : JSONMethods.alarmsList) {
                    if (JSONMethods.alarmsList.get(i).getId().equals(JSONMethods.chosenSensor) && JSONMethods.alarmsList.get(i).getT() == AlarmTypes.TOO_HIGH_PH) {
                        JSONMethods.alarmsList.get(i).setValue(maxPhEdt.getText().toString());
                    } else if (JSONMethods.alarmsList.get(i).getId().equals(JSONMethods.chosenSensor) && JSONMethods.alarmsList.get(i).getT() == AlarmTypes.TOO_LOW_PH) {
                        JSONMethods.alarmsList.get(i).setValue(minPhEdt.getText().toString());
                    } else if (JSONMethods.alarmsList.get(i).getId().equals(JSONMethods.chosenSensor) && JSONMethods.alarmsList.get(i).getT() == AlarmTypes.TOO_HIGH_WATER_LEVEL) {
                        JSONMethods.alarmsList.get(i).setValue(maxWaterLvlEdt.getText().toString());
                    } else if (JSONMethods.alarmsList.get(i).getId().equals(JSONMethods.chosenSensor) && JSONMethods.alarmsList.get(i).getT() == AlarmTypes.TOO_LOW_WATER_LEVEL) {
                        JSONMethods.alarmsList.get(i).setValue(minWaterLvlEdt.getText().toString());
                    } else if (JSONMethods.alarmsList.get(i).getId().equals(JSONMethods.chosenSensor) && JSONMethods.alarmsList.get(i).getT() == AlarmTypes.TOO_FAST_WATER_LEVEL_CHANGE) {
                        JSONMethods.alarmsList.get(i).setValue(waterChangeEdt.getText().toString());
                    }
                    i++;
                }
                for (int j = 0; j < JSONMethods.sensorsList.size(); j++) {
                    if (JSONMethods.sensorsList.get(j).getSensor_id().equals(JSONMethods.chosenSensor) && JSONMethods.sensorsList.get(j).getMeasure_period() != editTextMeasurePeriod.getText().toString()) {
                        JSONMethods.sensorsList.get(j).setMeasure_period(editTextMeasurePeriod.getText().toString());
                        String message = JSONMethods.sendSensorConfig(JSONMethods.chosenSensor, editTextMeasurePeriod.getText().toString());
                        mqttCallback.clientPublish(topic, message);
                    }
                }
                saveAlarmSettings(JSONMethods.chosenSensor);
                Intent intent = new Intent(getApplicationContext(), SensorsActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Configuration successful", Toast.LENGTH_LONG).show();
            }
        });


        // tego tu nie będzie, alarmy bd dodawane przy pobieraniu listy
        // tutaj to jest dla testu !!!!!!!!!!!!!!!!!!!!!


        //jsonMethods.addAlarms(jsonMethods.chosenSensor);

//        SettingsRecViewAdapter adapter = new SettingsRecViewAdapter(this);
//        adapter.setAlarmList(JSONMethods.correctAlarmsList);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


//        ViewGroup viewGroup = (ViewGroup) settingsLayout;

//        RelativeLayout relativeLayout = new RelativeLayout(this);
//        relativeLayout.setLayoutParams(RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
//        settingsLayout.addView(relativeLayout);
//        ArrayList<SensorsData> sensorsData = new ArrayList<>();
//        sensorsData.add(new SensorsData(jsonMethods.sensor_id,jsonMethods.ph,"130",jsonMethods.ph_alarm,jsonMethods.water_level_alarm));
//        sensorsData.add(new SensorsData("11","11","120",jsonMethods.ph_alarm,jsonMethods.water_level_alarm));
//        sensorsData.add(new SensorsData("12","11","120",jsonMethods.ph_alarm,jsonMethods.water_level_alarm));


    }

    public void saveAlarmSettings(String id) {

        FileOutputStream fos = null;

        try {
            fos = getApplicationContext().openFileOutput(id + ".txt", MODE_PRIVATE);
            String toFileString = JSONMethods.chosenSensor + " " + maxPhEdt.getText().toString() + " " + minPhEdt.getText().toString() + " " + maxWaterLvlEdt.getText().toString() + " " +minWaterLvlEdt.getText().toString()+" "+ waterChangeEdt.getText().toString();
            fos.write(toFileString.getBytes());
            //Toast.makeText(getApplicationContext(),"Saved "+ getApplicationContext().getFilesDir() ,Toast.LENGTH_LONG).show();
            Log.i("Save", "Saved " + getApplicationContext().getFilesDir());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}