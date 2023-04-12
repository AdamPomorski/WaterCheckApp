package com.example.watercheckapp;

import android.util.Log;

import com.example.watercheckapp.alarms.Alarm;
import com.example.watercheckapp.alarms.AlarmTypes;

import com.example.watercheckapp.home.MyHomeService;
import com.example.watercheckapp.sensors.SensorsData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class JSONMethods implements AOI{
    //public static String USER_ID = UUID.randomUUID().toString();
    public static String USER_ID = "1";
    private static int sensor_number;
    public static String sensor_id;
    public static String timestamp;
    public static String ph;
    public static String water_level;

    public static String measure_period = "120";
    public static String lang_s;
    public static String long_s;
    public static String description;
    
    public static String chosenSensor;

    public static String faultPhValue;
    public static String faultWLValue;
    public static String faultChangeValue;

    private static String previousValue;
    public static int tooBigWaterLevelChangeValue=0;


    public static  ArrayList<SensorsData> sensorsList = new ArrayList<>();

    public static  ArrayList<SensorsData> historyList = new ArrayList<>();




    private static String TAG = "JSON_METHODS";


    public static int findSensorNumberByID(String id){
        Integer output = 0;
        for (SensorsData s: JSONMethods.sensorsList){
            if(s.getSensor_id().equals(id)){
                output = s.getSensor_number();
            }
        }
        return output;
    }



    public JSONMethods() {
    }


    public static void getSensorValuesFromJson(String jsonStr){
        if(jsonStr!=null){
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray measurement = jsonObject.getJSONArray("measurement");

                for (int i =0;i<measurement.length();i++){
                    JSONObject m = measurement.getJSONObject(i);
                     sensor_id = m.getString("sensor_id");
                     timestamp = m.getString("timestamp");
                     ph = m.getString("ph");
                     water_level = m.getString("water_level");

                    JSONMethods.chosenSensor = sensor_id;


                    boolean isListed = false;
                    for(int j=0; j<sensorsList.size();j++){
                        if(sensorsList.get(j).getSensor_id().equals(sensor_id)){
                            sensorsList.get(j).setTimestamp(timestamp);
                            sensorsList.get(j).setPh(ph);
                            previousValue = sensorsList.get(j).getWater_level();
                            String check = AOI.findAlarmValueByID(sensor_id,AlarmTypes.TOO_FAST_WATER_LEVEL_CHANGE);

                            if(previousValue!=null) {
                                if (Integer.parseInt(water_level) - Integer.parseInt(previousValue) > Integer.parseInt(check)) {

                                    tooBigWaterLevelChangeValue = Integer.parseInt(water_level) - Integer.parseInt(previousValue);
                                }
                            }
                            sensorsList.get(j).setWater_level(water_level);
                            isListed = true;

                        }
                    }
                    if(!isListed) {
                        sensorsList.add(new SensorsData(sensor_id, timestamp, ph, water_level));
                        AOI.addAlarms(sensor_id);
                    }


                isListed = false;



                }
            }catch (final JSONException e){
                Log.i(TAG,"Json parsing error: " + e.getMessage());

            }
        }



    }
    public static void getSensorListFromJson(String jsonStr){
        if(MqttCallbackImpl.listRequestCounter<=1) {
            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray sensors = (JSONArray) jsonObject.get("sensors");

                    for (int i = 0; i < sensors.length(); i++) {
                        JSONObject s = sensors.getJSONObject(i);
                        sensor_id = s.getString("sensor_id");
                        measure_period = s.getString("measure_period");
                        lang_s = s.getString("lang");
                        long_s = s.getString("long");
                        description = s.getString("description");
                        sensor_number = (i + 1);


                        sensorsList.add(new SensorsData(sensor_number, sensor_id, measure_period, lang_s, long_s, description));
                        AOI.addAlarms(sensor_id);


                    }


                } catch (final JSONException e) {
                    Log.i(TAG, "Json parsing error: " + e.getMessage());

                }
            }
        }

    }
    public static ArrayList<SensorsData> getHistoryFromJson(String jsonStr) {
        ArrayList<SensorsData> localList = new ArrayList<>();
        if (jsonStr != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray history = (JSONArray) jsonObject.get("history");

                for (int i = 0; i < history.length(); i++) {
                    JSONObject h = history.getJSONObject(i);
                    sensor_id = h.getString("sensor_id");
                    timestamp = h.getString("timestamp");
                    ph = h.getString("ph");
                    water_level = h.getString("water_level");

                    localList.add(new SensorsData(sensor_id, timestamp, ph, water_level));


                }


            } catch (final JSONException e) {
                Log.i(TAG, "Json parsing error: " + e.getMessage());

            }

        }
        Collections.sort(localList,Comparator.comparing(SensorsData::getTimestamp));

        return localList;
    }
    public static String sendSensorConfig(String sensor_idL,String measure_periodL){
        String jsonStr ;
        long unixTime = System.currentTimeMillis() / 1000L;
        JSONObject jo = new JSONObject();
        try {
            jo.put("sensor_id",sensor_idL);
            jo.put("timestamp",unixTime);
            jo.put("measure_period",Integer.parseInt(measure_periodL));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonStr = jo.toString();


        return jsonStr;

    }

    public static String sendHistoryRequest(String sensor_id,long startTime,long stopTime){
        String jsonStr;
        JSONObject jo = new JSONObject();
        try {
            jo.put("sensor_id",sensor_id);
            jo.put("timestamp_start",startTime);
            jo.put("timestamp_stop",stopTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonStr = jo.toString();

        return jsonStr;
    }

}
