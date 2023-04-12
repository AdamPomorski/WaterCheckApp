package com.example.watercheckapp;

import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;


import com.example.watercheckapp.alarms.AlarmTypes;
import com.example.watercheckapp.home.MyHomeService;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;


public class MqttCallbackImpl implements MqttCallback {
    public static MqttClient client;
    MutableLiveData<String> mutableLiveData; //kopia zmiennej dzielonej
    public static boolean isConnected; //flaga statusu po połączenia
    public static ArrayList<String> subscribedTopics = new ArrayList<>();
    JSONMethods jsonMethods = new JSONMethods();

    public static boolean tooHighPhNtF = false;
    public static boolean tooLowPhNtF = false;
    public static boolean tooHighWLNtF = false;
    public static boolean tooLowWLNtF = false;
    public static boolean tooFastChangeNtF = false;
    public static boolean finishedProcess = false;
    public static boolean finishedProcess2 = false;
    public static boolean finishedProcess2FromThread = false;
    public static boolean finishedProcessFromThread = false;
    public static boolean connectionLostFlag = false;
    public static boolean deliveryOfSensorListCompleted = false;
    public static int listRequestCounter = 0;

    private String TAG = "MqttCallback";









    public MqttCallbackImpl(MutableLiveData<String> mutableLiveData) {
        this.mutableLiveData = mutableLiveData;
        isConnected = false;
    }

    public MqttCallbackImpl() {
    }




    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        finishedProcess = false;
        finishedProcess2 = false;

        if(topic.contains("/data")){
            JSONMethods.getSensorValuesFromJson(message.toString());
            for (int i =0;i<JSONMethods.sensorsList.size();i++) {
                for (int j =0;j<JSONMethods.alarmsList.size();j++) {
                    if (JSONMethods.sensorsList.get(i).getSensor_id().equals(JSONMethods.alarmsList.get(j).getId())&&JSONMethods.sensorsList.get(i).getSensor_id().equals(JSONMethods.chosenSensor)){

                        if(Float.parseFloat(JSONMethods.sensorsList.get(i).getPh())>Float.parseFloat(JSONMethods.alarmsList.get(j).getValue()) && JSONMethods.alarmsList.get(j).getT()== AlarmTypes.TOO_HIGH_PH){

                            tooHighPhNtF = true;
                            JSONMethods.faultPhValue = JSONMethods.sensorsList.get(i).getPh();
                        }
                        if(Float.parseFloat(JSONMethods.sensorsList.get(i).getPh())<Float.parseFloat(JSONMethods.alarmsList.get(j).getValue()) && JSONMethods.alarmsList.get(j).getT()== AlarmTypes.TOO_LOW_PH){

                            tooLowPhNtF = true;
                            JSONMethods.faultPhValue = JSONMethods.sensorsList.get(i).getPh();

                        }
                        if(Integer.parseInt(JSONMethods.sensorsList.get(i).getWater_level())>Integer.parseInt(JSONMethods.alarmsList.get(j).getValue()) && JSONMethods.alarmsList.get(j).getT()== AlarmTypes.TOO_HIGH_WATER_LEVEL){

                            tooHighWLNtF = true;
                            JSONMethods.faultWLValue = JSONMethods.sensorsList.get(i).getWater_level();
                        }
                        if(Integer.parseInt(JSONMethods.sensorsList.get(i).getWater_level())<Integer.parseInt(JSONMethods.alarmsList.get(j).getValue()) && JSONMethods.alarmsList.get(j).getT()== AlarmTypes.TOO_LOW_WATER_LEVEL){

                            tooLowWLNtF = true;
                            JSONMethods.faultWLValue = JSONMethods.sensorsList.get(i).getWater_level();
                        }
//                        if(Integer.parseInt(JSONMethods.sensorsList.get(i).getSensor_id())>Integer.parseInt(JSONMethods.alarmsList.get(j).getValue()) && JSONMethods.alarmsList.get(j).getT()== AlarmTypes.TOO_FAST_WATER_LEVEL_CHANGE){
//
//                            tooFastChangeNtF = true;
//                        }


                    }
                }
            }



        }if(topic.contains("response")&&topic.contains("sensors")){
            deliveryOfSensorListCompleted = true;
            JSONMethods.getSensorListFromJson(message.toString());
            listRequestCounter++;
            finishedProcess = true;

        }if(topic.contains("response")&&topic.contains("history")){
            JSONMethods.historyList.clear();
            JSONMethods.historyList = JSONMethods.getHistoryFromJson(message.toString());
            finishedProcess2 = true;

        }



    }


    @Override
    public void connectionLost(Throwable cause) {

        Log.i(TAG,"Connection was lost");
        connectionLostFlag = true;


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
//... – dla testów to może pozostać puste
    }

    public boolean disConnect() {
        try {
            client.disconnect();
            isConnected = false;
            Log.i(TAG,"Disconected from service");
            //HomeViewModel.addLog("Has been disconnected from broker");
            return true;
        } catch (MqttException e) {
            Log.i(TAG,"Cannot disconnect from service");
            //HomeViewModel.addLog("Can't disconnect from broker.");
            return false;
        }
    }


    public boolean connect(String domain, int port, MqttConnectOptions options) {
        try {
            client = new MqttClient("tcp://" + domain + ":" + port,
                    MqttClient.generateClientId(), new MemoryPersistence());
            client.setCallback(this);
            client.connect(options);
            isConnected = true;
            //HomeViewModel.addLog("Has been connected to " + domain + ":" + port);
            Log.i("MqttConnect","Has been connected to " + domain + ":" + port);
            return true;
        } catch (MqttException e) {
            System.out.println(e.getCause());

            return false;
        }
    }


    public boolean subscribe(String topic) {
        try {
            client.subscribe(topic);
            subscribedTopics.add(topic);

            Log.i("MqttSubscribed","Subscribed "+topic);
            return true;
        } catch (MqttException e) {
            //HomeViewModel.addLog(topic + ": Can't subscribe.");
            Log.i("MqttSubscribed","Unsubscribed");
            return false;
        }


    }

    public boolean unSubscribe (String topic) {
        try {
            client.unsubscribe(topic);
            subscribedTopics.remove(topic);
            return true;
        } catch (MqttException e) {
            return false;
        }
    }
    public void clientPublish(String mqttTopic,String message) {
        try {
            client.publish(mqttTopic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
        }
    }
    public void onMqttConnected(){}

    }
