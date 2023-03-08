package com.example.watercheckapp.home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.watercheckapp.DrawerBaseActivity;
import com.example.watercheckapp.JSONMethods;
import com.example.watercheckapp.MapActivity;
import com.example.watercheckapp.MqttCallbackImpl;
import com.example.watercheckapp.R;
import com.example.watercheckapp.alarms.AlarmActivity;
import com.example.watercheckapp.databinding.ActivityHomeBinding;
import com.example.watercheckapp.history.HistoryActivity;
import com.example.watercheckapp.sensors.SensorsActivity;
import com.example.watercheckapp.settings.SettingsActivity;

public class HomeActivity extends DrawerBaseActivity {
    public static Context context;
    public static String CHANNEL_ID = "1";
    ActivityHomeBinding activityHomeBinding;
    SwipeRefreshLayout swipeRefreshLayout;
    CardView sensorC,mapC,alarmsC,historyC;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());
        allocateActivityTitle("Home");
        sensorC = findViewById(R.id.sensor_home);
        mapC = findViewById(R.id.map_home);
        alarmsC = findViewById(R.id.alarm_home);
        historyC = findViewById(R.id.history_home);
        swipeRefreshLayout=findViewById(R.id.swiperefresh);

        MyHomeService service = new MyHomeService(new MqttCallbackImpl() {
            @Override
            public void onMqttConnected() {
                
                //progressBar.setVisibility(View.GONE);

                // Enable user interaction with the UI
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

        createNotificationChannel();
        Intent intent = new Intent(this,service.getClass());
        startService(intent);

        sensorC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SensorsActivity.class);
                startActivity(intent);
            }
        });
        mapC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });
        historyC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });
        alarmsC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(intent);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        // Your code goes here
                        // In this code, we are just
                        // changing the text in the textbox
                        JSONMethods.sensorsList.clear();
                        JSONMethods.alarmsList.clear();
                        stopService(intent);
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        MyHomeService.checkRedundance();

                        // This line is important as it explicitly
                        // refreshes only once
                        // If "true" it implicitly refreshes forever
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );








    }


    public  void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
//    public  static void checkMessage(String topic, String message){
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());
//
//        Intent intent = new Intent(context.getApplicationContext(), AlarmActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), HomeActivity.CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_baseline_notification_important_12)
//                .setContentTitle("My notification")
//                .setContentText("Hello World!")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                // Set the intent that will fire when the user taps the notification
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        notificationManager.notify(notificationId, builder.build());
//
//    }
}