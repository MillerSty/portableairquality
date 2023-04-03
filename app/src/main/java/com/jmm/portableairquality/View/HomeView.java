// bluetooth code adapted from https://github.com/weliem/blessed-android/blob/master/app/src/main/java/com/welie/blessedexample/MainActivity.java
package com.jmm.portableairquality.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;

import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.jmm.portableairquality.Controller.LocationControl;
import com.jmm.portableairquality.Model.BluetoothHandler;
import com.jmm.portableairquality.Model.CcsMeasurement;
import com.jmm.portableairquality.Model.DhtMeasurement;
import com.jmm.portableairquality.Model.PmMeasurement;
import com.jmm.portableairquality.Model.SensorDataDatabaseHelper;
import com.jmm.portableairquality.Model.SensorSingleton;
import com.jmm.portableairquality.R;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothPeripheral;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeView extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int ACCESS_LOCATION_REQUEST = 2;
    private SensorSingleton sensorSingleton;
    private SharedPreferences swissPref;
    BottomNavigationView navbot;
    TextView co2Display, vocDisplay, tempDisplay, humDisplay, pmDisplay;
    public int co2, voc;
    public float pm;
    boolean FN_co2=false,FN_voc=false,FN_pm=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setExitTransition(new Fade());
        swissPref=getSharedPreferences("switch",Context.MODE_PRIVATE);

        initView();
        initSensorAlarm();
        registerReceiver(locationReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));
        registerReceiver(ccsReceiver, new IntentFilter(BluetoothHandler.MEASUREMENT_CCS));
        registerReceiver(dhtReceiver, new IntentFilter(BluetoothHandler.MEASUREMENT_DHT));
        registerReceiver(pmReceiver, new IntentFilter(BluetoothHandler.MEASUREMENT_PM));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("my note", "My notif", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        SharedPreferences sharedPref = this.getSharedPreferences("graphColour", Context.MODE_PRIVATE);
        if(sharedPref.getInt("Temp_Color",0)==0||sharedPref.getInt("Co2_Color",0)==0){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("Temp_Color",getResources().getColor(R.color.tempColour));
            editor.putInt("Hum_Color",getResources().getColor(R.color.humColour));
            editor.putInt("Co2_Color",getResources().getColor(R.color.co2Colour));
            editor.putInt("VoC_Color",getResources().getColor(R.color.vocColour));
            editor.putInt("Pm_Color",getResources().getColor(R.color.pmColour));
            editor.apply();
        }
    }

    protected void onStart() {
        super.onStart();


        co2Display.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textViewHandler("co2",co2,0);
                if( co2>SensorSingleton.Instance.getCo2Alarm()){
                    FN_co2=true;
                    Notification("Co2");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        vocDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textViewHandler("voc",voc,0);
                if( voc>SensorSingleton.Instance.getVocAlarm()){
                    FN_voc=true;
                    Notification("Voc");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        pmDisplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textViewHandler("pm2",0,pm);
                if( pm>SensorSingleton.Instance.getPmAlarm()){
                    FN_pm=true;
                    Notification("Pm");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();

        if (!isBluetoothEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_ENABLE_BT);
        } else {
            checkPermissions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(ccsReceiver);
        unregisterReceiver(dhtReceiver);
        unregisterReceiver(pmReceiver);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_settings:
                Intent goToSettings = new Intent(this, SettingsView.class);
                startActivity(goToSettings , ActivityOptions.makeSceneTransitionAnimation(HomeView.this).toBundle());
                return true;

            case R.id.menu_map:
                Intent goToMap = new Intent(this, MapsView.class);
                startActivity(goToMap, ActivityOptions.makeSceneTransitionAnimation(HomeView.this).toBundle());
                return true;

            case R.id.menu_home:
                return true;

            case R.id.menu_history:
                Intent goToHistory = new Intent(this, HistoryView.class);
                startActivity(goToHistory, ActivityOptions.makeSceneTransitionAnimation(HomeView.this).toBundle());
                return true;

            default:
                return false;
        }
    }

    private boolean isBluetoothEnabled() {
        BluetoothAdapter bluey = getBluetoothManager().getAdapter();
        if (bluey == null) return false;

        return bluey.isEnabled();
    }

    private void initBtHandler() {
        BluetoothHandler.getInstance(getApplicationContext());
    }

    private BluetoothManager getBluetoothManager() {
        return Objects.requireNonNull((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE), "Cannot get BluetoothManager");
    }

    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(LocationManager.MODE_CHANGED_ACTION)) {
                boolean isEnabled = areLocationServicesEnabled();
                Log.d("HomeActivity", String.format("Location service state changed to: %s", isEnabled ? "on" : "off"));
                checkPermissions();
            }
        }
    };

    //TODO MAPVIEW NAV NOT WORKING and DOESNT HIGHLIGHT MAP FRAG
    //TODO NAV NOT SLIDE
    // IF CHANGE NAV VIEWS A BUNCH IT WONT LOAD MAP
    //MAP TOAST WHEN CLICKING SETTINGS
    //ONPROVIDERDISABLED
    //TODO Graph colour change
    //NOTIFICATION FIX
    //REPOPULATE SETTINGS WHEN OPENING DIALOGUE
    //WEIGH AVERAGE OR JUST WHEN ONE IS YELLOW FOR LCOATION POLYLINES
    //ADD IN REFERENCE
    // RING BACKGROUND COLOUR TRANSPARENT

    private final BroadcastReceiver ccsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CcsMeasurement measurement = (CcsMeasurement) intent.getSerializableExtra(BluetoothHandler.MEASUREMENT_CCS_EXTRA);
            co2 = (int)measurement.co2;
            co2Display.setText("Co2:\n" + Long.toString(measurement.co2));
            voc = (int)measurement.voc;
            vocDisplay.setText("VOC:\n" + Long.toString(measurement.voc));
        }
    };

    private final BroadcastReceiver dhtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DhtMeasurement measurement = (DhtMeasurement) intent.getSerializableExtra(BluetoothHandler.MEASUREMENT_DHT_EXTRA);
            tempDisplay.setText(Float.toString(measurement.temp) + "\u00B0");
            humDisplay.setText(Float.toString(measurement.hum) + "%");
        }
    };

    private final BroadcastReceiver pmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PmMeasurement measurement = (PmMeasurement) intent.getSerializableExtra(BluetoothHandler.MEASUREMENT_PM_EXTRA);
            pmDisplay.setText("PM2.5:\n" + Integer.toString(measurement.pm));
        }
    };

    private BluetoothPeripheral getPeripheral(String peripheralAddress) {
        BluetoothCentralManager central = BluetoothHandler.getInstance(getApplicationContext()).btCentral;
        return central.getPeripheral(peripheralAddress);
    }

    //toast message function
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // following sections deals with bluetooth and location permissions
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] missingPermissions = getMissingPermissions(getRequiredPermissions());
            if (missingPermissions.length > 0) {
                requestPermissions(missingPermissions, ACCESS_LOCATION_REQUEST);
            } else {
                permissionsGranted();
            }
        }
    }

    private String[] getMissingPermissions(String[] requiredPermissions) {
        List<String> missingPermissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String requiredPermission : requiredPermissions) {
                if (getApplicationContext().checkSelfPermission(requiredPermission) != PackageManager.PERMISSION_GRANTED) {
                    missingPermissions.add(requiredPermission);
                }
            }
        }
        return missingPermissions.toArray(new String[0]);
    }

    private String[] getRequiredPermissions() {
        int targetSdkVersion = getApplicationInfo().targetSdkVersion;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && targetSdkVersion >= Build.VERSION_CODES.S) {
            return new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && targetSdkVersion >= Build.VERSION_CODES.Q) {
            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        } else return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
    }

    private void permissionsGranted() {
        // Check if Location services are on because they are required to make scanning work for SDK < 31
        int targetSdkVersion = getApplicationInfo().targetSdkVersion;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && targetSdkVersion < Build.VERSION_CODES.S) {
            if (checkLocationServices()) {
                LocationControl.Instance.handleLocation(this);
                initBtHandler();
            }
        } else {
            LocationControl.Instance.handleLocation(this);
            initBtHandler();
        }
    }

    private boolean areLocationServicesEnabled() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Log.d("HomeActivity", "could not get location manager");
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return locationManager.isLocationEnabled();
        } else {
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            return isGpsEnabled || isNetworkEnabled;
        }
    }

    private boolean checkLocationServices() {
        if (!areLocationServicesEnabled()) {
            new AlertDialog.Builder(HomeView.this)
                    .setTitle("Location services are not enabled")
                    .setMessage("Scanning for Bluetooth peripherals requires locations services to be enabled.") // Want to enable?
                    .setPositiveButton("Enable", (dialogInterface, i) -> {
                        dialogInterface.cancel();
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    })
                    .create()
                    .show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if all permission were granted
        boolean allGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            permissionsGranted();
        } else {
            new AlertDialog.Builder(HomeView.this)
                    .setTitle("Permission is required for scanning Bluetooth peripherals")
                    .setMessage("Please grant permissions")
                    .setPositiveButton("Retry", (dialogInterface, i) -> {
                        dialogInterface.cancel();
                        checkPermissions();
                    })
                    .create()
                    .show();
        }
    }

    @SuppressLint("MissingPermission")
    public void Notification(String sensor_alert) {
        Intent notifyIntent = new Intent(this, HomeView.class);

        // Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("my note", "My big note", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(HomeView.this, "my note");
            builder.setChannelId("my note");
        } else {
            builder = new NotificationCompat.Builder(HomeView.this, "my notif");
        }

        builder.setContentTitle(sensor_alert + " beyond threshold")
                .setContentText(sensor_alert + " has gone beyond the limit set.")
                .setSmallIcon(R.drawable.humidity)
                .setAutoCancel(true)
                .setContentIntent(notifyPendingIntent);

        //can also set intent to go to when clicked
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(HomeView.this);

        //TODO add permission check android.permission.POST_NOTIFICATIONS
        managerCompat.notify(1, builder.build());
    }

    public int PPxToPercent(int sensorReading, String sensor) {
        if (sensor.equals("co2")) {
            return sensorReading = sensorReading / 10_000; // this is going to be very small

        } else if (sensor.equals("voc")) {
            return sensorReading = sensorReading / 1_000_000;
        } else if (sensor.equals("pm")) { //ug per cubed meter, ug/m3
            return -1;
        } else return -1;
    }

    //reverse PPxToPercent
    public int PercentToPPx(int sensorReading, String sensor) {
        if (sensor.equals("co2")) {
            return sensorReading = sensorReading * 10_000; // this is going to be very small

        } else if (sensor.equals("voc")) {
            return sensorReading = sensorReading * 1_000_000;
        } else if (sensor.equals("pm")) { //ug per cubic meter, ug/m3
            return -1;
        } else return -1;
    }

    //TODO add proper bounds of Pm readings
    public void textViewHandler(String sensor, int readingInt,float readingPm) {
        switch (sensor) {
            case "co2":
                if (readingInt >= 0 && readingInt <= 1000) {
                    co2Display.setBackground(ContextCompat.getDrawable(this, R.drawable.sensor_display_green));
//                flagGreen
                } else if (readingInt > 1000 && readingInt <= 8000) {
                    co2Display.setBackground(ContextCompat.getDrawable(this, R.drawable.sensor_display_yellow));
//                flagYellow
                } else {
                    co2Display.setBackground(ContextCompat.getDrawable(this, R.drawable.sensor_display_red));
//                flagRed
                }
                break; //0-1000 ppm is good(green), 1500-8000 unhealthy(yellow), 8000-30000 serious health risk(orange), 30000+ critical (red) [ppm]
            case "voc":
                if (readingInt >= 0 && readingInt <= 220) {
                    vocDisplay.setBackground(ContextCompat.getDrawable(this, R.drawable.sensor_display_green));
//                flagGreen
                } else if (readingInt > 220 && readingInt <= 660) {
                    vocDisplay.setBackground(ContextCompat.getDrawable(this, R.drawable.sensor_display_yellow));
//                flagYellow
                } else {
                    vocDisplay.setBackground(ContextCompat.getDrawable(this, R.drawable.sensor_display_red));
//                flagRed
                }
                break;//0-220 is good (green), 220-660 ( yellow), 660-2000(orange), 2000+(red) [ppb]
            case "pm":
                //TODO Set default for PM values, these are copied from Co2
                if (readingPm >= 0f && readingPm <= 12f) {
                    pmDisplay.setBackground(getResources().getDrawable(R.drawable.sensor_display_green));
//                flagGreen
                } else if (readingPm > 12f && readingPm <= 35f) {
                    pmDisplay.setBackground(getResources().getDrawable(R.drawable.sensor_display_yellow));
//                flagYellow
                } else {
                    pmDisplay.setBackground(getResources().getDrawable(R.drawable.sensor_display_red));
//                flagRed
                }
                break;
            default:
                break;
        }
    }

    //init function for View and Alarm
    public void initView() {
        if(!swissPref.getBoolean("swiss",false)){

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        co2Display = findViewById(R.id.co2Display);
        vocDisplay = findViewById(R.id.vocDisplay);
        tempDisplay = findViewById(R.id.tempDisplay);
        humDisplay = findViewById(R.id.humDisplay);
        pmDisplay = findViewById(R.id.pmDisplay);

        navbot = findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this);
        navbot.setSelectedItemId(R.id.menu_home);

    }

    public void initSensorAlarm() {
        sensorSingleton.Instance.setCo2Alarm(sensorSingleton.Co2Default);
        sensorSingleton.Instance.setVocAlarm(sensorSingleton.VocDefault);
        sensorSingleton.Instance.setPmAlarm(sensorSingleton.PmDefault);
    }
}

