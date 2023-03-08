package com.example.watercheckapp.home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.watercheckapp.AOI;
import com.example.watercheckapp.JSONMethods;
import com.example.watercheckapp.MqttCallbackImpl;
import com.example.watercheckapp.R;
import com.example.watercheckapp.alarms.Alarm;
import com.example.watercheckapp.alarms.AlarmActivity;
import com.example.watercheckapp.alarms.AlarmTypes;

import com.example.watercheckapp.history.HistoryActivity;
import com.example.watercheckapp.sensors.SensorsData;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MyHomeService extends Service {
    private static final String TAG = "Service";
    private static final String FILE_NAME = "occurredAlarms.txt";
    public static String CHANNEL_ID = "ServiceID";
    public static int notificationId = 0;
    private static String host, topic_data, topic_app_history_response, topic_app_sensors_response, topic_config, topic_sensors_request;
    private static int port;
    private static String notMsg, message;
    private static boolean sensor_list_request = false;
    private MqttCallbackImpl mqttCallback;

    public MyHomeService() {
    }

    public MyHomeService(MqttCallbackImpl mqttCallback) {
        this.mqttCallback = mqttCallback;
    }



    @Override
    public void onCreate() {
        super.onCreate();

        new ConnectMqttTask(mqttCallback, getApplicationContext()).execute();

        loadAlarmValues();

        createNotificationChannel();

        Log.i(TAG, "Service started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        intent = new Intent(getApplicationContext(), AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        new Thread(

                new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Service running");
                        while (true) {


                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (MqttCallbackImpl.connectionLostFlag) {
                                MqttCallbackImpl.connectionLostFlag = false;
                                Intent intent = new Intent(getApplicationContext(), MyHomeService.class);
                                startService(intent);
                            }
                            if (MqttCallbackImpl.tooHighPhNtF) {
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy   HH:mm:ss");
                                LocalDateTime now = LocalDateTime.now();
                                notMsg = dtf.format(now.plusHours(1)).toString() + "\n\nPh on sensor " + JSONMethods.findSensorNumberByID(JSONMethods.chosenSensor) + " is " + JSONMethods.faultPhValue + " which is over the limit: " + AOI.findAlarmValueByID(JSONMethods.chosenSensor, AlarmTypes.TOO_HIGH_PH);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_baseline_notification_important_12)
                                        .setContentTitle("TOO HIGH PH ALARM!")
                                        .setContentText(notMsg)
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                notificationManager.notify(notificationId, builder.build());
                                JSONMethods.occurredAlarmsList.add(notMsg);
                                save();


                                notificationId++;
                                MqttCallbackImpl.tooHighPhNtF = false;
                            }
                            if (MqttCallbackImpl.tooLowPhNtF) {
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy   HH:mm:ss");
                                LocalDateTime now = LocalDateTime.now();
                                notMsg = dtf.format(now.plusHours(1)).toString() + "\n\nPh on sensor " + JSONMethods.findSensorNumberByID(JSONMethods.chosenSensor) + " is " + JSONMethods.faultPhValue + " which is under the limit: " + AOI.findAlarmValueByID(JSONMethods.chosenSensor, AlarmTypes.TOO_LOW_PH);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_baseline_notification_important_12)
                                        .setContentTitle("TOO LOW PH ALARM!")
                                        .setContentText(notMsg)
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                notificationManager.notify(notificationId, builder.build());
                                JSONMethods.occurredAlarmsList.add(notMsg);
                                save();

                                notificationId++;
                                MqttCallbackImpl.tooLowPhNtF = false;
                            }
                            if (MqttCallbackImpl.tooHighWLNtF) {
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy   HH:mm:ss");
                                LocalDateTime now = LocalDateTime.now();
                                notMsg = dtf.format(now.plusHours(1)).toString() + "\n\nWater level on sensor " + JSONMethods.findSensorNumberByID(JSONMethods.chosenSensor) + " is " + JSONMethods.faultWLValue + " which is over the limit: " + AOI.findAlarmValueByID(JSONMethods.chosenSensor, AlarmTypes.TOO_HIGH_WATER_LEVEL);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_baseline_notification_important_12)
                                        .setContentTitle("TOO HIGH WATER LEVEL ALARM!")
                                        .setContentText(notMsg)
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                notificationManager.notify(notificationId, builder.build());
                                JSONMethods.occurredAlarmsList.add(notMsg);
                                save();

                                notificationId++;
                                MqttCallbackImpl.tooHighWLNtF = false;
                            }
                            if (MqttCallbackImpl.tooLowWLNtF) {
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy   HH:mm:ss");
                                LocalDateTime now = LocalDateTime.now();
                                notMsg = dtf.format(now.plusHours(1)).toString() + "\n\nWater level on sensor " + JSONMethods.findSensorNumberByID(JSONMethods.chosenSensor) + " is " + JSONMethods.faultWLValue + " which is over the limit: " + AOI.findAlarmValueByID(JSONMethods.chosenSensor, AlarmTypes.TOO_LOW_WATER_LEVEL);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_baseline_notification_important_12)
                                        .setContentTitle("TOO LOW WATER LEVEL ALARM!")
                                        .setContentText(notMsg)
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                notificationManager.notify(notificationId, builder.build());
                                JSONMethods.occurredAlarmsList.add(notMsg);
                                save();

                                notificationId++;
                                MqttCallbackImpl.tooLowWLNtF = false;
                            }
                            if (JSONMethods.tooBigWaterLevelChangeValue != 0) {
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy   HH:mm:ss");
                                LocalDateTime now = LocalDateTime.now();
                                notMsg = dtf.format(now.plusHours(1)).toString() + "\n\nWater Level on sensor " + JSONMethods.findSensorNumberByID(JSONMethods.chosenSensor) + " has increased by " + JSONMethods.tooBigWaterLevelChangeValue + " which is over the limit: " + AOI.findAlarmValueByID(JSONMethods.chosenSensor, AlarmTypes.TOO_FAST_WATER_LEVEL_CHANGE);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_baseline_notification_important_12)
                                        .setContentTitle("TOO FAST WATER LEVEL CHANGE ALARM!")
                                        .setContentText(notMsg)
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        // Set the intent that will fire when the user taps the notification
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);
                                notificationManager.notify(notificationId, builder.build());
                                JSONMethods.occurredAlarmsList.add(notMsg);
                                save();

                                notificationId++;


                                JSONMethods.tooBigWaterLevelChangeValue = 0;
                            }


                        }


                    }
                }
        ).start();


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    public static void checkRedundance() {

        Set<String> nameSet = new HashSet<>();
        List<SensorsData> newList = JSONMethods.sensorsList.stream()
                .filter(e -> nameSet.add(e.getSensor_id()))
                .collect(Collectors.toList());
        JSONMethods.sensorsList.clear();
        JSONMethods.sensorsList = new ArrayList<>(newList);
        Set<String> nameSet2 = new HashSet<>();
        List<SensorsData> newList2 = JSONMethods.sensorsList.stream()
                .filter(e -> nameSet.add(e.getSensor_id()))
                .collect(Collectors.toList());
        JSONMethods.sensorsList.clear();
        JSONMethods.sensorsList = new ArrayList<>(newList);

    }


    private void save() {

        FileOutputStream fos = null;
        File file = new File(getFilesDir(), FILE_NAME);
        if (file.exists()) {
            try {
                fos = getApplicationContext().openFileOutput(FILE_NAME, MODE_APPEND);
                fos.write(10);
                fos.write(notMsg.getBytes());
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
        }else{
            firstTimeSave();
        }

    }

    private void firstTimeSave() {

        FileOutputStream fos = null;
        for (SensorsData s : JSONMethods.sensorsList
        ) {

            try {
                fos = getApplicationContext().openFileOutput(s.getSensor_id() + ".txt", MODE_APPEND);
                String toFileString = s.getSensor_id() + " 9 5 100 50 15";
                fos.write(toFileString.getBytes());
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

    private void loadAlarmValues() {

        FileInputStream fis = null;
        for (SensorsData s : JSONMethods.sensorsList
        ) {
            try {
                fis = getApplicationContext().openFileInput(s.getSensor_id() + ".txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                // Error occurred when opening raw file for reading.
            } finally {
                String content = stringBuilder.toString();
                String[] valuesArray = content.split(" ", 10);
                int i = 1;
                if (valuesArray[0].equals(s.getSensor_id())) {
                    for (AlarmTypes alarmType : AlarmTypes.values()) {
                        for (Alarm a : JSONMethods.alarmsList
                        ) {
                            if (a.getId().equals(s.getSensor_id()) && a.getT() == alarmType) {
                                a.setValue(valuesArray[i]);
                            }
                        }
                        i++;
                    }
                }

                Log.i("load", content);
            }

        }


    }

    public void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static class ConnectMqttTask extends AsyncTask<Void, Void, Void> {
        private MqttCallbackImpl mqttCallback;
        private Context context;

        public ConnectMqttTask(MqttCallbackImpl mqttCallback, Context context) {
            this.mqttCallback = mqttCallback;
            this.context = context;

        }

        @Override
        protected Void doInBackground(Void... params) {
            host = "test.mosquitto.org";
            port = 1883;
            topic_data = "sensor/+/data";
            topic_app_history_response = "app/" + JSONMethods.USER_ID + "/history/response";
            topic_app_sensors_response = "app/" + JSONMethods.USER_ID + "/sensors/response";
            topic_config = "sensor/+/configuration";
            topic_sensors_request = "app/" + JSONMethods.USER_ID + "/sensors/request";
            mqttCallback = new MqttCallbackImpl();
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setUserName("rw");
            mqttCallback.connect(host, port, mqttConnectOptions);
            mqttCallback.subscribe(topic_data);
            mqttCallback.subscribe(topic_app_history_response);
            mqttCallback.subscribe(topic_app_sensors_response);
            mqttCallback.subscribe(topic_config);

            sensor_list_request = true;
            if (sensor_list_request) {
                JSONObject jo = new JSONObject();
                try {
                    jo.put("sensor_list_request", sensor_list_request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                message = jo.toString();
            }
            mqttCallback.clientPublish(topic_sensors_request, message);
            MqttCallbackImpl.finishedProcess = false;



           /* THIS FRAGMENT IS RESPONSIBLE FOR WAITING FOR THE RESPONSE FROM SERVER
              AS THE SERVER IS NOT ACTIVE RIGHT NOW TO EMULATE HOW THE APPLICATION WORKS
              SOME VALUES WILL BE SET INSIDE THE PROGRAM

            while (!MqttCallbackImpl.finishedProcess) {
                MqttCallbackImpl.finishedProcessFromThread = false;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            if (MqttCallbackImpl.finishedProcessFromThread) {
                Toast.makeText(context, "Cannot connect with the service", Toast.LENGTH_LONG).show();
                return null;
            }

            */
            JSONMethods.sensorsList.add(new SensorsData(1, "ex12-77bd-x8y2", "150", "52", "51", "description"));
            AOI.addAlarms("ex12-77bd-x8y2");
            JSONMethods.sensorsList.add(new SensorsData(2, "dd11-71cd-x8y3", "120", "55", "50", "description 2"));
            AOI.addAlarms("dd11-71cd-x8y3");


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Notify callback that connection is established
            mqttCallback.onMqttConnected();
        }
    }

}