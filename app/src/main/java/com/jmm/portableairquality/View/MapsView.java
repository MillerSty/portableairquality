package com.jmm.portableairquality.View;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.jmm.portableairquality.R;

public class MapsView extends AppCompatActivity {
    GoogleMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview2);
        Fragment fragment = new MapsFrag();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commitNow();
    }
}
