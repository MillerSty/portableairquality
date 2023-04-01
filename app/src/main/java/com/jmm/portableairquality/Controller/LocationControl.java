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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.jmm.portableairquality.Model.SensorSingleton;

public class LocationControl implements LocationListener {
    LocationManager locationManager;
    public static LocationControl Instance = new LocationControl();
    Context contextt;
    static float minDistance = 5f; //50-75 is good
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
            Criteria critera=new Criteria();
            critera.setAccuracy(Criteria.ACCURACY_FINE);
            String bestProvider = locationManager.getBestProvider(critera, false);

            locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, this);
            Log.d("LAT LONG", "FALUJAH");
        } else {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location newLocation) {
        SharedPreferences sp = contextt.getSharedPreferences("hey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Location oldLocation = new Location("");
        oldLocation.setLatitude(Double.longBitsToDouble(sp.getLong("Lat", 0)));
        oldLocation.setLongitude(Double.longBitsToDouble(sp.getLong("Long", 0)));


        if(!(newLocation.getLatitude()==0||newLocation.getLongitude()==0))
        {
            if (newLocation.hasSpeed()&&newLocation.getSpeed() > 1.0) {
                double latitude = newLocation.getLatitude();
                double longitude = newLocation.getLongitude();
                editor.putLong("Lat", Double.doubleToRawLongBits(latitude));
                editor.putLong("Long", Double.doubleToRawLongBits(longitude));
                editor.apply();
            } else {
                if(newLocation.getLatitude()==0||newLocation.getLongitude()==0||oldLocation.getLongitude()==0||oldLocation.getLatitude()==0){
                double latitude = newLocation.getLatitude();
                double longitude = newLocation.getLongitude();
                editor.putLong("Lat", Double.doubleToRawLongBits(latitude));
                editor.putLong("Long", Double.doubleToRawLongBits(longitude));
                    editor.apply();
                }
            }

        }
    }
    private void showToast(String message) {
        Toast.makeText(contextt.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
