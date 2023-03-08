package com.example.watercheckapp.alarms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.watercheckapp.JSONMethods;
import com.example.watercheckapp.R;

import java.util.ArrayList;

public class AlarmListAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> encounteredAlarmListAd = new ArrayList<>();
    LayoutInflater inflater;

    public AlarmListAdapter(Context context, ArrayList<String> occuredAlarmsListAd) {
        this.context = context;
        this.encounteredAlarmListAd = occuredAlarmsListAd;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return encounteredAlarmListAd.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            convertView = inflater.inflate(R.layout.activity_alarms_list_view, null);
            TextView alarmInfo = (TextView) convertView.findViewById(R.id.alarmInfo);
            Button deleteAlarmButton = (Button) convertView.findViewById(R.id.deleteAlarmButton);
            alarmInfo.setText(encounteredAlarmListAd.get(position));

            deleteAlarmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    encounteredAlarmListAd.remove(position);
                    notifyDataSetChanged();

                }
            });

            return convertView;

    }

}
