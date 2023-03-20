package com.jmm.portableairquality.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jmm.portableairquality.Controller.AlarmDialogue;
import com.jmm.portableairquality.Model.SensorSingleton;
import com.jmm.portableairquality.R;

public class SettingsView extends AppCompatActivity {
    BottomNavigationView navbot;
    ImageView alarm;
    Switch swiss;
    Boolean isChecked;
    SensorSingleton Sensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO Smaller text size for alarmdialgoue
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        navbot=findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        navbot.setSelectedItemId(R.id.menu_settings);
        alarm=findViewById(R.id.IvAlarm);
        swiss=findViewById(R.id.switch1);
        isChecked=false;



        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });


    }
    protected void onResume() {
        super.onResume();
        swiss.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    compoundButton.setChecked(true);
//                    compoundButton.setText("NIGHT MODE");
                    isChecked=false;}


                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    compoundButton.setChecked(false);
//                    compoundButton.setText("DAY MODE");
                    isChecked=true;
                }
            }
        });
//        showToast(Sensor.Instance.toString());

    }
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_settings:

//                showToast("CLICKED SETTINGS");
                return true;
            case R.id.menu_home:
                Intent goToSettings=new Intent(SettingsView.this, HomeView.class);
                startActivity(goToSettings);

                return true;
            case R.id.menu_history:
                Intent goToHistory=new Intent(SettingsView.this, HistoryView.class);
                startActivity(goToHistory);
                return true;
            default:
                return false;
        }
    }

    private void showEditDialog() {
//        FragmentManager fm = getSupportFragmentManager();
//       AlarmDialogue editNameDialogFragment = AlarmDialogue.newInstance("Some Title");
//        editNameDialogFragment.show(fm, "fragment_edit_name");
        AlarmDialogue dm=new AlarmDialogue();
        dm.show(getSupportFragmentManager(),"TAG");

    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}