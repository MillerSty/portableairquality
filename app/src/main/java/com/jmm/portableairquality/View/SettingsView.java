package com.jmm.portableairquality.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jmm.portableairquality.R;

public class SettingsView extends AppCompatActivity {
    BottomNavigationView navbot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        navbot=findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        navbot.setSelectedItemId(R.id.menu_settings);

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
}
