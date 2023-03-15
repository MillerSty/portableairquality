package com.jmm.portableairquality.Controller;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jmm.portableairquality.Model.sensors;
import com.jmm.portableairquality.R;

public class AlarmDialogue extends DialogFragment {
    protected EditText co2AlarmLevel,vocAlarmLevel,pmAlarmLevel;
    protected Button save,cancel;
    sensors Sensors;
//    public AlarmDialogue() {
//        // Required empty public constructor
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate layout
        View view=inflater.inflate(R.layout.alarm_dialogue, container);
        init(view);
        //do code for stuff here

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save data
                sensors.Instance.setCo2Alarm(Integer.parseInt(co2AlarmLevel.getText().toString()));
                sensors.Instance.setVocAlarm(Integer.parseInt(vocAlarmLevel.getText().toString()));
                sensors.Instance.setPmAlarm(Integer.parseInt(pmAlarmLevel.getText().toString()));
                //log works therefore singleton works
                Log.d("TAG",Integer.toString(sensors.Instance.getCo2Alarm()));
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

    private void init(View v){
        co2AlarmLevel=v.findViewById(R.id.etCo2Alarm);
        vocAlarmLevel=v.findViewById(R.id.etVocAlarm);
        pmAlarmLevel=v.findViewById(R.id.etPmAlarm);
        save=v.findViewById(R.id.btnSave);
        cancel=v.findViewById(R.id.btnCancel);
        Sensors=new sensors();

    }
}
