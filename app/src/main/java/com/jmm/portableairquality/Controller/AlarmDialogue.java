package com.jmm.portableairquality.Controller;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.jmm.portableairquality.Model.SensorSingleton;
import com.jmm.portableairquality.R;

public class AlarmDialogue extends DialogFragment {
    //TODO extend pmAlarmLevel for 1,2,10
    protected EditText co2AlarmLevel, vocAlarmLevel, pm1AlarmLevel,pm2AlarmLevel,pm10AlarmLevel;
    protected Button save, cancel;
    SensorSingleton sensorSingleton;

    //    public AlarmDialogue() {
//        // Required empty public constructor
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate layout
        View view = inflater.inflate(R.layout.alarm_dialogue, container);
        init(view);
        //do code for stuff here

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save data
                if(!co2AlarmLevel.getText().toString().isEmpty()){
                sensorSingleton.Instance.setCo2Alarm(Integer.parseInt(co2AlarmLevel.getText().toString()));}
                if(!vocAlarmLevel.getText().toString().isEmpty()){
                sensorSingleton.Instance.setVocAlarm(Integer.parseInt(vocAlarmLevel.getText().toString()));}
                if(!pm1AlarmLevel.getText().toString().isEmpty()){
                sensorSingleton.Instance.setPmAlarm(Integer.parseInt(pm1AlarmLevel.getText().toString()));}

                dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cancel
                dismiss();
            }
        });
        return view;
    }

    private void init(View v) {
        co2AlarmLevel = v.findViewById(R.id.etCo2Alarm);
        vocAlarmLevel = v.findViewById(R.id.etVocAlarm);
        pm1AlarmLevel = v.findViewById(R.id.etPm1Alarm);
        pm2AlarmLevel = v.findViewById(R.id.etPm2Alarm);
        pm10AlarmLevel = v.findViewById(R.id.etPm10Alarm);
        save = v.findViewById(R.id.btnSave);
        cancel = v.findViewById(R.id.btnCancel);
        sensorSingleton = new SensorSingleton();

    }
}
