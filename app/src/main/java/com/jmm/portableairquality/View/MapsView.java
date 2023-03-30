package com.jmm.portableairquality.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.jmm.portableairquality.R;

public class MapsView extends AppCompatActivity {
GoogleMap mMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview2);
//        SharedPreferences sharedPref = this.getSharedPreferences("hey", Context.MODE_PRIVATE);
//        LatLng latlong = new LatLng(Double.longBitsToDouble(sharedPref.getLong("Lat",0)),Double.longBitsToDouble(sharedPref.getLong("Long",0)));
        Fragment fragment= new MapsFrag();
//        if(fragment!=null) {
           getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commitNow();
//        }
    }
}
