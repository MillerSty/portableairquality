package com.jmm.portableairquality.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class LocationControl implements LocationListener {
    LocationManager locationManager;
    public static LocationControl Instance = new LocationControl();
    Context context;
    static float minDistance = 5f; //50-75 is good
    static int minTime = 2000; //1000000
    public String bestProvider;

    public LocationControl() {}

    public void handleLocation(Context context) {
        if (context != null) {
            this.context = context;

            enableLocation();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestLocation();
                    handler.postDelayed(this, 2000);
                }
            }, 1000);
        }
    }

    @SuppressLint("MissingPermission")
    public void requestLocation() {
        locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onLocationChanged(@NonNull Location newLocation) {
        SharedPreferences sp = context.getSharedPreferences("hey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        Location oldLocation = new Location("");
        oldLocation.setLatitude(Double.longBitsToDouble(sp.getLong("Lat", 0)));
        oldLocation.setLongitude(Double.longBitsToDouble(sp.getLong("Long", 0)));

        if(!(newLocation.getLatitude()==0 || newLocation.getLongitude()==0))
        {
            if (newLocation.hasSpeed() && newLocation.getSpeed() > 1.0) {
                double latitude = newLocation.getLatitude();
                double longitude = newLocation.getLongitude();
                editor.putLong("Lat", Double.doubleToRawLongBits(latitude));
                editor.putLong("Long", Double.doubleToRawLongBits(longitude));
                editor.apply();
            } else {
                if(newLocation.getLatitude()==0 || newLocation.getLongitude()==0 || oldLocation.getLongitude()==0 || oldLocation.getLatitude()==0){
                double latitude = newLocation.getLatitude();
                double longitude = newLocation.getLongitude();
                editor.putLong("Lat", Double.doubleToRawLongBits(latitude));
                editor.putLong("Long", Double.doubleToRawLongBits(longitude));
                    editor.apply();
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void enableLocation() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            bestProvider = locationManager.getBestProvider(criteria, false);

            if(!locationManager.isProviderEnabled(bestProvider)) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(myIntent);
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        showToast("Location services disabled. No map data will be available");
    }

    @Override
    public void onProviderEnabled(String provider) {}

    private void showToast(String message) {
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
