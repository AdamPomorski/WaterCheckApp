package com.example.watercheckapp;

import com.example.watercheckapp.alarms.Alarm;
import com.example.watercheckapp.alarms.AlarmTypes;

import java.util.ArrayList;

public interface AOI {
    String too_high_ph_value = "9";
    String too_low_ph_value = "5";
    String too_high_water_level_value = "100";
    String too_low_water_level_value = "50";
    String too_fast_water_level_change = "15";
    ArrayList<String> occurredAlarmsList = new ArrayList<>();
    ArrayList<String> alarmValues = new ArrayList<>();
    ArrayList<Alarm> alarmsList = new ArrayList<>();
    ArrayList<Alarm> correctAlarmsList = new ArrayList<>();

    static void addAlarmValues(){
        alarmValues.add(0,too_high_ph_value);
        alarmValues.add(1,too_low_ph_value);
        alarmValues.add(2,too_high_water_level_value);
        alarmValues.add(3,too_low_water_level_value);
        alarmValues.add(4,too_fast_water_level_change);
    }

     static void findCorrectAlarms(String id){
        correctAlarmsList.clear();

        for (Alarm a: alarmsList
        ) {
            if(a.getId().equals(id)){
                correctAlarmsList.add(a);
            }
        }
    }

     static String findAlarmValueByID(String id, AlarmTypes alarmTypes){
        String output = new String();
        for (int i =0;i<JSONMethods.sensorsList.size();i++) {
            for (int j = 0; j < JSONMethods.alarmsList.size(); j++) {
                if (JSONMethods.sensorsList.get(i).getSensor_id().equals(JSONMethods.alarmsList.get(j).getId()) && JSONMethods.sensorsList.get(i).getSensor_id().equals(id) && JSONMethods.alarmsList.get(j).getT()==alarmTypes){

                    output = alarmsList.get(j).getValue();
                }
            }
        }


        return output;
    }
     static void addAlarms(String id){
        AOI.addAlarmValues();
        int i=0;
        for (AlarmTypes alarmType: AlarmTypes.values()) {
            Alarm alarm = new Alarm(alarmType,id,alarmValues.get(i));
            alarmsList.add(alarm);
            i++;
        }
    }

}
