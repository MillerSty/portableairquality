package com.jmm.portableairquality.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.jmm.portableairquality.Model.SensorSingleton;

public class LocationControl implements LocationListener {
    LocationManager locationManager;
    public static LocationControl Instance = new LocationControl();
    Context contextt;
    static float minDistance = 1f; //50-75 is good
    static int minTime = 2000; //1000000
    public LocationControl() {
    }

    public void handleLocation(Context context) {
        if (context != null) {
            contextt = context;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestLocation(context);
                    handler.postDelayed(this, 2000);
                }
            }, 1000);
        }
    }

    @SuppressLint("MissingPermission")
    public void requestLocation(Context context) {
        if (locationManager != null) {
            String bestProvider = locationManager.getBestProvider(new Criteria(), false);
            locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, this);
            Log.d("LAT LONG", "FALUJAH");
        } else {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        SharedPreferences sp = contextt.getSharedPreferences("hey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        location.setAccuracy(10f);
        Location oldLocation = new Location("");
        oldLocation.setLatitude(Double.longBitsToDouble(sp.getLong("Lat", 0)));
        oldLocation.setLongitude(Double.longBitsToDouble(sp.getLong("Long", 0)));

        if (location.distanceTo(oldLocation) > minDistance) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            editor.putLong("Lat", Double.doubleToRawLongBits(latitude));
            editor.putLong("Long", Double.doubleToRawLongBits(longitude));
            editor.apply();
            Log.d("LAT LONG", "HERPY");
        }
        else{
            //maybe a position count to see if we are staying in the same spot
        }
        //then check
//        locationManager.removeUpdates(this);
    }
}
