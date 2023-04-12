package com.example.watercheckapp.history;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.watercheckapp.DrawerBaseActivity;
import com.example.watercheckapp.JSONMethods;
import com.example.watercheckapp.MqttCallbackImpl;
import com.example.watercheckapp.R;
import com.example.watercheckapp.databinding.ActivityHistoryBinding;
import com.example.watercheckapp.databinding.ActivityHomeBinding;
import com.example.watercheckapp.home.MyHomeService;
import com.example.watercheckapp.sensors.SensorsData;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends DrawerBaseActivity{
    EditText idEdt,startDateEdt,startHoursEdt,stopDateEdt,stopHoursEdt;
    Button idConfirmButton;

    GraphView graph1,graph2;
    ActivityHistoryBinding historyBinding;
    MqttCallbackImpl mqttCallback =new MqttCallbackImpl();
    String topic_history_response,topic_history_request;

    String dateStart1Str,dateStart2Str,dateStop1Str,dateStop2Str;

    SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    long unixTimeStart,unixTimeStop;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        historyBinding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(historyBinding.getRoot());
        allocateActivityTitle("History");

        idEdt = findViewById(R.id.idEditTextIDHistory);
        idConfirmButton =findViewById(R.id.confirmIdBtn);
        startDateEdt = findViewById(R.id.idEditTextTimeStartHistoryDate);
        startHoursEdt = findViewById(R.id.idEditTextTimeStartHistoryDateHours);
        stopDateEdt = findViewById(R.id.idEditTextTimeStopHistoryDate);
        stopHoursEdt = findViewById(R.id.idEditTextTimeStopHistoryDateHours);

        graph1 = findViewById(R.id.graph1);
        graph2 = findViewById(R.id.graph2);
        MyHomeService.checkRedundance();


        topic_history_response = "app/"+ JSONMethods.USER_ID+"/history/response";
        topic_history_request = "app/"+ JSONMethods.USER_ID+"/history/request";



        idConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graph1.removeAllSeries();
                graph2.removeAllSeries();
                MqttCallbackImpl.finishedProcess2 =false;

                for (int i =0;i<JSONMethods.sensorsList.size();i++){
                    if(JSONMethods.sensorsList.get(i).getSensor_number()==Integer.parseInt(idEdt.getText().toString())){
                        JSONMethods.chosenSensor = JSONMethods.sensorsList.get(i).getSensor_id();
                    }
                }
                try {
                    if(!TextUtils.isDigitsOnly(idEdt.getText())){
                        idEdt.setError("Wrong sensor value");
                        return;
                    }else if(!isIdOnTheList(idEdt.getText().toString())){
                        idEdt.setError("No sensor with given number ");
                        return;
                    }
                    dateStart1Str= startDateEdt.getText().toString();
                    Date dateStart1 = formatter1.parse(dateStart1Str);
                    dateStart2Str= startHoursEdt.getText().toString();
                    Date dateStart2 = formatter2.parse(dateStart2Str);
                    dateStop1Str= stopDateEdt.getText().toString();
                    Date dateStop1 = formatter1.parse(dateStop1Str);
                    dateStop2Str= stopHoursEdt.getText().toString();
                    Date dateStop2 = formatter2.parse(dateStop2Str);

                     unixTimeStart = (dateStart1.getTime()/1000)+(dateStart2.getTime()/1000);
                     unixTimeStop = (dateStop1.getTime()/1000)+(dateStop2.getTime()/1000);

                } catch (ParseException e) {
                    Toast.makeText(HistoryActivity.this, "Wrong date format", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
                if(unixTimeStart>=unixTimeStop){
                    Toast.makeText(HistoryActivity.this, "Wrong dates values", Toast.LENGTH_LONG).show();
                    return;
                }
                String message = JSONMethods.sendHistoryRequest(JSONMethods.chosenSensor,unixTimeStart,unixTimeStop);
                mqttCallback.clientPublish(topic_history_request,message);

                while (!MqttCallbackImpl.finishedProcess2){
                    MqttCallbackImpl.finishedProcess2FromThread=false;
                    runnableSeconds runnableSeconds = new runnableSeconds(2);
                    Thread thread = new Thread(runnableSeconds);
                    thread.start();
                }
                if(MqttCallbackImpl.finishedProcess2FromThread){
                    Toast.makeText(HistoryActivity.this,"Cannot connect with service",Toast.LENGTH_LONG).show();
                    return;
                }






                LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>();
                PointsGraphSeries<DataPoint> series11 = new PointsGraphSeries<>();
                LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>();
                PointsGraphSeries<DataPoint> series22 = new PointsGraphSeries<>();

                float y;
                Date x;

                for (int i=0;i<JSONMethods.historyList.size();i++){
                   y = Float.parseFloat(JSONMethods.historyList.get(i).getPh());
                   x = new Date(Long.parseLong(JSONMethods.historyList.get(i).getTimestamp())*1000);

                   series1.appendData(new DataPoint(x,y),true,500);
                    series11.appendData(new DataPoint(x,y),true,500);
                }
                for (int i=0;i<JSONMethods.historyList.size();i++) {
                    y = Float.parseFloat(JSONMethods.historyList.get(i).getWater_level());
                    x = new Date(Long.parseLong(JSONMethods.historyList.get(i).getTimestamp()) * 1000);

                    series2.appendData(new DataPoint(x, y), true, 500);
                    series22.appendData(new DataPoint(x,y),true,500);
                }



                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat ddf = new SimpleDateFormat("dd.MM.yy");
//                }
                if((unixTimeStop-unixTimeStart)>86400){
                    graph1.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext(),ddf));
                    graph2.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext(),ddf));

                }else{
                    graph1.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext(),sdf));
                    graph2.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext(),sdf));

                }
                graph1.getGridLabelRenderer().setNumHorizontalLabels(3);


                graph2.getGridLabelRenderer().setNumHorizontalLabels(3);



                graph1.getViewport().setScrollable(true);
                graph1.getViewport().setScalableY(true);

                if(!JSONMethods.historyList.isEmpty()) {
                    graph1.getViewport().setMinX(new Date(Long.parseLong(JSONMethods.historyList.get(0).getTimestamp()) * 1000).getTime());
                    graph1.getViewport().setMaxX(new Date(Long.parseLong(JSONMethods.historyList.get(JSONMethods.historyList.size() - 1).getTimestamp()) * 1000).getTime());
                    graph1.getViewport().setXAxisBoundsManual(true);
                }
                    graph1.getViewport().setScrollableY(true);
                    graph1.getViewport().setMinY(0);
                    graph1.getViewport().setMaxY(14);
                    graph1.getViewport().setYAxisBoundsManual(true);

                if(!JSONMethods.historyList.isEmpty()) {
                    graph2.getViewport().setMinX(new Date(Long.parseLong(JSONMethods.historyList.get(0).getTimestamp()) * 1000).getTime());
                    graph2.getViewport().setMaxX(new Date(Long.parseLong(JSONMethods.historyList.get(JSONMethods.historyList.size() - 1).getTimestamp()) * 1000).getTime());
                    graph2.getViewport().setXAxisBoundsManual(true);
                }
                    graph2.getViewport().setScrollableY(true);
                    graph2.getViewport().setMinY(0);
                    graph2.getViewport().setMaxY(500);
                    graph2.getViewport().setYAxisBoundsManual(true);


                series11.setSize(5.0f);
                series11.setColor(Color.BLACK);
                series22.setSize(5.0f);
                series22.setColor(Color.BLACK);
                graph1.addSeries(series1);
                graph1.addSeries(series11);
                graph2.addSeries(series2);
                graph2.addSeries(series22);





            }
        });
    }

    private boolean isIdOnTheList(String id){
        for (SensorsData s:JSONMethods.sensorsList
             ) {
            if(s.getSensor_number()==Integer.parseInt(id)){
                return true;
            }
        }
        return false;
    }
    private class runnableSeconds implements Runnable{
        int seconds;
         runnableSeconds(int seconds){
            this.seconds=seconds;
        }


        @Override
        public void run() {
             for(int i=0;i<seconds;i++){
                 try {
                     Thread.sleep(1000);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             }
             MqttCallbackImpl.finishedProcess2=true;
             MqttCallbackImpl.finishedProcess2FromThread=true;
        }
    }

}