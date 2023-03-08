package com.example.watercheckapp.sensors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watercheckapp.JSONMethods;
import com.example.watercheckapp.R;
import com.example.watercheckapp.settings.SettingsActivity;

import java.util.ArrayList;

public class SensorsRecViewAdapter extends RecyclerView.Adapter<SensorsRecViewAdapter.ViewHolder>{

    private Context context;
    private ArrayList<SensorsData> sensorsData = new ArrayList<>();
    JSONMethods jsonMethods;


    public SensorsRecViewAdapter(Context context){this.context = context;}


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensors_list_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SensorsRecViewAdapter.ViewHolder holder, final int position) {

        holder.sensorNumber.setText("Sensor "+sensorsData.get(position).getSensor_number());
        holder.sensorIdName.setText("ID: "+sensorsData.get(position).getSensor_id());
        holder.phTxt.setText("Ph: "+sensorsData.get(position).getPh());
        holder.waterLevelTxt.setText("Water level: "+sensorsData.get(position).getWater_level());
        holder.editSettigsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonMethods.chosenSensor = sensorsData.get(position).getSensor_id();
                context.startActivity(new Intent(context, SettingsActivity.class));
                //overridePendingTransition(0,0);
            }
        });



    }

    @Override
    public int getItemCount() {
        return sensorsData.size();
    }

    public void setSensorsData(ArrayList<SensorsData> sensorsData) {
        this.sensorsData = sensorsData;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView sensorIdName, phTxt, waterLevelTxt,sensorNumber;
        private Button editSettigsButton;
        private CardView parent;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            sensorNumber=itemView.findViewById(R.id.sensorName2);
            parent = itemView.findViewById(R.id.parent);
            sensorIdName = itemView.findViewById(R.id.sensorIdName_list);
            phTxt = itemView.findViewById(R.id.ph_list);
            waterLevelTxt = itemView.findViewById(R.id.waterLevel_list);
            editSettigsButton = itemView.findViewById(R.id.editSettingsButton_list);

        }

    }

}
