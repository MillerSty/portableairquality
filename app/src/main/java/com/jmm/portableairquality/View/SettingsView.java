package com.jmm.portableairquality.View;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jmm.portableairquality.Controller.ColorDialogue;
import com.jmm.portableairquality.Model.SensorSingleton;
import com.jmm.portableairquality.R;

public class SettingsView extends AppCompatActivity {
    BottomNavigationView navbot;
    Switch swiss;
    Boolean isChecked;
    protected EditText co2AlarmLevel, vocAlarmLevel, pmAlarmLevel;
    protected Button save;
    SensorSingleton sensorSingleton;
    private View Co2Preview, VoCPreview, PmPreview, TempPreview, HumPreview;
    private Drawable co2Draw, vocDraw, pmDraw, tempDraw, humDraw;
    private int mDefaultColor;
    private SharedPreferences sharedPref,swissPref,alarmPref;
    SharedPreferences.Editor swissEdit;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Fade());



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPref = this.getSharedPreferences("graphColour", Context.MODE_PRIVATE);
        swissPref=this.getSharedPreferences("switch", Context.MODE_PRIVATE);
        alarmPref=this.getSharedPreferences("alarm", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        swissEdit = swissPref.edit();
        initView();

        Co2Preview.setOnClickListener(v -> {
            ColorDialogue CD = new ColorDialogue();
            CD.show(getSupportFragmentManager(), "Co2");
        });
        VoCPreview.setOnClickListener(v -> {
            ColorDialogue CD = new ColorDialogue();
            CD.show(getSupportFragmentManager(), "VoC");
        });
        PmPreview.setOnClickListener(v -> {
            ColorDialogue CD = new ColorDialogue();
            CD.show(getSupportFragmentManager(), "Pm");
        });
        TempPreview.setOnClickListener(v -> {
            ColorDialogue CD = new ColorDialogue();
            CD.show(getSupportFragmentManager(), "Temp");
        });
        HumPreview.setOnClickListener(v -> {
            ColorDialogue CD = new ColorDialogue();
            CD.show(getSupportFragmentManager(), "Hum");
        });
        getSupportFragmentManager().setFragmentResultListener("Co2_Color", this, (requestKey, result) -> {
            mDefaultColor = result.getInt("Co2_Color");

            co2Draw.clearColorFilter();
            co2Draw.setColorFilter(mDefaultColor, PorterDuff.Mode.MULTIPLY);
            Co2Preview.setBackground(co2Draw);
            editor.putInt("Co2_Color", mDefaultColor);
            editor.apply();
        });
        getSupportFragmentManager().setFragmentResultListener("VoC_Color", this, (requestKey, result) -> {
            mDefaultColor = result.getInt("VoC_Color");
            vocDraw.clearColorFilter();
            vocDraw.setColorFilter(mDefaultColor, PorterDuff.Mode.MULTIPLY);
            VoCPreview.setBackground(vocDraw);
            editor.putInt("VoC_Color", mDefaultColor);
            editor.apply();
        });
        getSupportFragmentManager().setFragmentResultListener("Pm_Color", this, (requestKey, result) -> {
            mDefaultColor = result.getInt("Pm_Color");
            pmDraw.clearColorFilter();
            pmDraw.setColorFilter(mDefaultColor, PorterDuff.Mode.MULTIPLY);
            PmPreview.setBackground(pmDraw);
            editor.putInt("Pm_Color", mDefaultColor);
            editor.apply();
        });
        getSupportFragmentManager().setFragmentResultListener("Temp_Color", this, (requestKey, result) -> {
            mDefaultColor = result.getInt("Temp_Color");
            tempDraw.clearColorFilter();
            tempDraw.setColorFilter(mDefaultColor, PorterDuff.Mode.MULTIPLY);
            TempPreview.setBackground(tempDraw);
            editor.putInt("Temp_Color", mDefaultColor);
            editor.apply();
        });
        getSupportFragmentManager().setFragmentResultListener("Hum_Color", this, (requestKey, result) -> {
            mDefaultColor = result.getInt("Hum_Color");
            humDraw.clearColorFilter();
            humDraw.setColorFilter(mDefaultColor, PorterDuff.Mode.MULTIPLY);
            HumPreview.setBackground(humDraw);
            editor.putInt("Hum_Color", mDefaultColor);
            editor.apply();
        });
    }
    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onStart() {
        super.onStart();

        swiss.setOnClickListener(view -> {
            if (swiss.isChecked()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                swissEdit.putBoolean("swiss", swiss.isChecked());
                swissEdit.apply();
                swiss.setText("Set to Light Mode");
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                swiss.setChecked(false);
                swissEdit.putBoolean("swiss", swiss.isChecked());
                swissEdit.apply();
                swiss.setText("Set to Dark Mode");
            }
        });

            save.setOnClickListener((View view1) -> {
                SharedPreferences.Editor alarmEdit = alarmPref.edit();
                try{
                    if(!co2AlarmLevel.getText().toString().isEmpty()){
                        sensorSingleton.Instance.setCo2Alarm(Integer.parseInt(co2AlarmLevel.getText().toString()));
                        alarmEdit.putInt("co2Alarm",Integer.parseInt(co2AlarmLevel.getText().toString()));
                    }
                    if(!vocAlarmLevel.getText().toString().isEmpty()){
                        sensorSingleton.Instance.setVocAlarm(Integer.parseInt(vocAlarmLevel.getText().toString()));
                        alarmEdit.putInt("vocAlarm",Integer.parseInt(vocAlarmLevel.getText().toString()));}
                    if(!pmAlarmLevel.getText().toString().isEmpty()){
                        sensorSingleton.Instance.setPmAlarm(Float.parseFloat(pmAlarmLevel.getText().toString()));
                        alarmEdit.putFloat("pmAlarm",Float.parseFloat(pmAlarmLevel.getText().toString()));
                    }

                    alarmEdit.apply();
                    showToast("Settings Saved");
                }
                catch(Exception e){
                    showToast("Enter Valid alarm levels please");
                }
            });
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_settings:
                return true;

            case R.id.menu_home:
                Intent goToSettings = new Intent(SettingsView.this, HomeView.class);
                startActivity(goToSettings, ActivityOptions.makeSceneTransitionAnimation(SettingsView.this).toBundle());
                return true;

            case R.id.menu_map:
                Intent goToMap = new Intent(this, MapsView.class);
                startActivity(goToMap, ActivityOptions.makeSceneTransitionAnimation(SettingsView.this).toBundle());
                return true;

            case R.id.menu_history:
                Intent goToHistory = new Intent(SettingsView.this, HistoryView.class);
                startActivity(goToHistory, ActivityOptions.makeSceneTransitionAnimation(SettingsView.this).toBundle());
                return true;

            default:
                return false;
        }
    }

    public void initView() {
        navbot = findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        navbot.setSelectedItemId(R.id.menu_settings);
        sensorSingleton = new SensorSingleton();

        save = findViewById(R.id.btnSave);

        swiss = (Switch) findViewById(R.id.switch1);
        if (!swissPref.getBoolean("swiss", false)) {
            swiss.setChecked(false);
            swiss.setText("Set to Dark Mode");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            swiss.setChecked(true);
            swiss.setText("Set to Light Mode");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        co2AlarmLevel = findViewById(R.id.etCo2Alarm);
        vocAlarmLevel = findViewById(R.id.etVocAlarm);
        pmAlarmLevel = findViewById(R.id.etPmAlarm);

        Co2Preview = findViewById(R.id.preview_Co2);
        VoCPreview = findViewById(R.id.preview_VoC);
        PmPreview = findViewById(R.id.preview_Pm);
        TempPreview = findViewById(R.id.preview_Temp);
        HumPreview = findViewById(R.id.preview_Hum);

        co2Draw = getDrawable(R.drawable.color_select);
        vocDraw = getDrawable(R.drawable.color_select);
        pmDraw = getDrawable(R.drawable.color_select);
        tempDraw = getDrawable(R.drawable.color_select);
        humDraw = getDrawable(R.drawable.color_select);

        if (alarmPref.getInt("co2Alarm", 0) == 0 || alarmPref.getInt("vocAlarm", 0) == 0) {
            co2AlarmLevel.setText(Integer.toString(SensorSingleton.Co2Default));
            vocAlarmLevel.setText(Integer.toString(SensorSingleton.VocDefault));
            pmAlarmLevel.setText(Float.toString(SensorSingleton.PmDefault));
        } else {
            co2AlarmLevel.setText(Integer.toString(alarmPref.getInt("co2Alarm", 0)));
            vocAlarmLevel.setText(Integer.toString(alarmPref.getInt("vocAlarm", 0)));
            pmAlarmLevel.setText(Float.toString(alarmPref.getFloat("pmAlarm", 0f)));

        }
        if (sharedPref.getInt("Temp_Color", 0) == 0 || sharedPref.getInt("Co2_Color", 0) == 0) {

            co2Draw.setColorFilter(getResources().getColor(R.color.co2Colour), PorterDuff.Mode.MULTIPLY);
            Co2Preview.setBackground(co2Draw);

            vocDraw.setColorFilter(getResources().getColor(R.color.vocColour), PorterDuff.Mode.MULTIPLY);
            VoCPreview.setBackground(vocDraw);

            pmDraw.setColorFilter(getResources().getColor(R.color.pmColour), PorterDuff.Mode.MULTIPLY);
            PmPreview.setBackground(pmDraw);


            tempDraw.setColorFilter(getResources().getColor(R.color.tempColour), PorterDuff.Mode.MULTIPLY);
            TempPreview.setBackground(tempDraw);

            humDraw.setColorFilter(getResources().getColor(R.color.humColour), PorterDuff.Mode.MULTIPLY);
            HumPreview.setBackground(humDraw);
        } else {
            co2Draw.clearColorFilter();
            co2Draw.setColorFilter(sharedPref.getInt("Co2_Color", 0), PorterDuff.Mode.MULTIPLY);
            Co2Preview.setBackground(co2Draw);

            vocDraw.clearColorFilter();
            vocDraw.setColorFilter(sharedPref.getInt("VoC_Color", 0), PorterDuff.Mode.MULTIPLY);
            VoCPreview.setBackground(vocDraw);

            pmDraw.clearColorFilter();
            pmDraw.setColorFilter(sharedPref.getInt("Pm_Color", 0), PorterDuff.Mode.MULTIPLY);
            PmPreview.setBackground(pmDraw);

            tempDraw.clearColorFilter();
            tempDraw.setColorFilter(sharedPref.getInt("Temp_Color", 0), PorterDuff.Mode.MULTIPLY);
            TempPreview.setBackground(tempDraw);

            humDraw.clearColorFilter();
            humDraw.setColorFilter(sharedPref.getInt("Hum_Color", 0), PorterDuff.Mode.MULTIPLY);
            HumPreview.setBackground(humDraw);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}