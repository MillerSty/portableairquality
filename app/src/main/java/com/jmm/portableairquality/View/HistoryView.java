package com.jmm.portableairquality.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jmm.portableairquality.R;

public class HistoryView extends AppCompatActivity {
    BottomNavigationView navbot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        navbot=findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        navbot.setSelectedItemId(R.id.menu_history);

    }
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_settings:
                Intent goToSettings=new Intent(HistoryView.this, SettingsView.class);
                startActivity(goToSettings);
//                showToast("CLICKED SETTINGS");
                return true;
            case R.id.menu_home:
                Intent goToHome=new Intent(HistoryView.this, HomeView.class);
                startActivity(goToHome);

                return true;
            case R.id.menu_history:

                return true;
            default:
                return false;
        }
    }
}