package com.example.watercheckapp.settings;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watercheckapp.JSONMethods;
import com.example.watercheckapp.R;
import com.example.watercheckapp.alarms.Alarm;
import com.example.watercheckapp.alarms.AlarmTypes;
import com.example.watercheckapp.sensors.SensorsData;

import java.util.ArrayList;

public class SettingsRecViewAdapter extends RecyclerView.Adapter<SettingsRecViewAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Alarm> alarmList = new ArrayList<>();
    JSONMethods jsonMethods;

    AlarmTypes[] alarmTypesArray = AlarmTypes.values();



    public SettingsRecViewAdapter(Context context){this.context = context;}


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_list_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsRecViewAdapter.ViewHolder holder, final int position) {


            if(alarmList.get(position).getId().equals(JSONMethods.chosenSensor)){
                if(alarmList.get(position).getT()==AlarmTypes.TOO_HIGH_PH){
                    holder.alarmType.setText("MAX PH");
                    holder.editAlarmValue.setText(alarmList.get(position).getValue());
                }else if(alarmList.get(position).getT()==AlarmTypes.TOO_LOW_PH){
                    holder.alarmType.setText("MIN PH");
                    holder.editAlarmValue.setText(alarmList.get(position).getValue());
                }else if(alarmList.get(position).getT()==AlarmTypes.TOO_HIGH_WATER_LEVEL){
                    holder.alarmType.setText("MAX WATER LVL");
                    holder.editAlarmValue.setText(alarmList.get(position).getValue());
                }else if(alarmList.get(position).getT()==AlarmTypes.TOO_LOW_WATER_LEVEL){
                    holder.alarmType.setText("MIN WATER LVL");
                    holder.editAlarmValue.setText(alarmList.get(position).getValue());
                }else if(alarmList.get(position).getT()==AlarmTypes.TOO_FAST_WATER_LEVEL_CHANGE){
                    holder.alarmType.setText("MAX WATER CHANGE");
                    holder.editAlarmValue.setText(alarmList.get(position).getValue());
                }


        }else{
                holder.itemView.clearFocus();
            }




    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public void setAlarmList(ArrayList<Alarm> alarmList) {
        this.alarmList = alarmList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView alarmType;
        private EditText editAlarmValue;
        private CardView parentSettings;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            parentSettings = itemView.findViewById(R.id.parentSettings);
            alarmType = itemView.findViewById(R.id.alarmType);
            editAlarmValue = itemView.findViewById(R.id.editTextAlarmValue);


        }

    }

}
