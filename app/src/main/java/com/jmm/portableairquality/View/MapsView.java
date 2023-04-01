package com.jmm.portableairquality.View;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jmm.portableairquality.R;

public class MapsView extends AppCompatActivity {
    GoogleMap mMap;
    Fragment fragment;
    BottomNavigationView navbot;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Fade());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview2);
        navbot=findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        navbot.setSelectedItemId(R.id.menu_map);
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
//        activeNetworkInfo != null ? activeNetworkInfo.isConnected();

    if(activeNetworkInfo!=null) {
        fragment = new MapsFrag();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commitNow();
    }
    else{
        showToast("cant display map, not connected to network");
        Intent goToHome = new Intent(this, HomeView.class);
        startActivity(goToHome);
    }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_settings:

               Intent goToSettings = new Intent(this, SettingsView.class);
//               goToSettings.
                startActivity(goToSettings, ActivityOptions.makeSceneTransitionAnimation(MapsView.this).toBundle());
                return true;
            case R.id.menu_map:
                return true;
            case R.id.menu_home:
                Intent goToHome = new Intent(this, HomeView.class);
                startActivity(goToHome, ActivityOptions.makeSceneTransitionAnimation(MapsView.this).toBundle());
                return true;
            case R.id.menu_history:
                Intent goToHistory = new Intent(this, HistoryView.class);
                startActivity(goToHistory, ActivityOptions.makeSceneTransitionAnimation(MapsView.this).toBundle());
                return true;
            default:
                return false;
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
