package com.example.watercheckapp.alarms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.example.watercheckapp.DrawerBaseActivity;
import com.example.watercheckapp.JSONMethods;
import com.example.watercheckapp.R;
import com.example.watercheckapp.databinding.ActivityAlarmBinding;
import com.example.watercheckapp.databinding.ActivityHomeBinding;
import com.example.watercheckapp.home.HomeActivity;
import com.example.watercheckapp.home.MyHomeService;

import java.util.ArrayList;

public class AlarmActivity extends DrawerBaseActivity {
   ActivityAlarmBinding alarmBinding;
   ArrayList<String> localoccurredAlarmsList = new ArrayList<>();


    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        alarmBinding = ActivityAlarmBinding.inflate(getLayoutInflater());
        setContentView(alarmBinding.getRoot());
        allocateActivityTitle("Alarms");
        MyHomeService.checkRedundance();

//TODO: wyswietlanie info o alarmie + zapisywanie na stałe (pewnie do plik)



//        occuredAlarmsList.add("UWAGA UWAGA ");
//        occuredAlarmsList.add("UWAGA UWAGA 2222 ");
//        occuredAlarmsList.add("UWAGA UWAGA 2222 ");
//        occuredAlarmsList.add("UWAGA UWAGA 2222 ");
//        occuredAlarmsList.add("UWAGA UWAGA 2222 ");
//        occuredAlarmsList.add("UWAGA UWAGA 2222 ");
//        occuredAlarmsList.add("UWAGA UWAGA 2222 ");
//        occuredAlarmsList.add("UWAGA UWAGA 2222 ");
//        occuredAlarmsList.add("UWAGA UWAGA 2222 ");
//        occuredAlarmsList.add("UWAGA UWAGA 2222 ");
//        occuredAlarmsList.add("UWAGA UWAGA 2222 ");

        localoccurredAlarmsList = JSONMethods.occurredAlarmsList;

        listView = (ListView) findViewById(R.id.alarmsListView);
        AlarmListAdapter alarmListAdapter = new AlarmListAdapter(getApplicationContext(),localoccurredAlarmsList);
        listView.setAdapter(alarmListAdapter);


    }

    @Override
    protected void onStop() {
        super.onStop();
        //localoccurredAlarmsList.clear();
    }
}