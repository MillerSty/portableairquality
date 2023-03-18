// bluetooth code adapted from https://github.com/weliem/blessed-android/blob/master/app/src/main/java/com/welie/blessedexample/MainActivity.java
package com.jmm.portableairquality.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.jmm.portableairquality.Model.BluetoothHandler;
import com.jmm.portableairquality.Model.CcsMeasurement;
import com.jmm.portableairquality.Model.DhtMeasurement;
import com.jmm.portableairquality.Model.Pm10Measurement;
import com.jmm.portableairquality.Model.Pm1Measurement;
import com.jmm.portableairquality.Model.Pm2Measurement;
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
    BottomNavigationView navbot;
    TextView co2Display,vocDisplay,tempDisplay,humDisplay,pm1Display,pm2Display,pm10Display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initSensorAlarm();
        registerReceiver(locationReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));
        registerReceiver(ccsReceiver, new IntentFilter(BluetoothHandler.MEASUREMENT_CCS));
        registerReceiver(dhtReceiver, new IntentFilter(BluetoothHandler.MEASUREMENT_DHT));
        registerReceiver(pm1Receiver, new IntentFilter(BluetoothHandler.MEASUREMENT_PM1));
        registerReceiver(pm2Receiver, new IntentFilter(BluetoothHandler.MEASUREMENT_PM2));
        registerReceiver(pm10Receiver, new IntentFilter(BluetoothHandler.MEASUREMENT_PM10));
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("my note","My notif",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }
protected void onStart() {
    super.onStart();



    co2Display.addTextChangedListener(new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            int co2=SensorDataDatabaseHelper.COLUMN_CO;
//            if( co2>SensorSingleton.Instance.getCo2Alarm()){
//            Notification("Co2");}
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
//            int voc=SensorDataDatabaseHelper.COLUMN_VOC;
//            if( voc>SensorSingleton.Instance.getVocAlarm()){
//                Notification("Voc");}
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    });
    pm1Display.addTextChangedListener(new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            float pm1=SensorDataDatabaseHelper.COLUMN_PM1;
//            if( pm1>SensorSingleton.Instance.getPm1Alarm()){
//                Notification("Pm1");}
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    });
    pm2Display.addTextChangedListener(new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            float pm2=SensorDataDatabaseHelper.COLUMN_PM2;
//            if( pm2>SensorSingleton.Instance.getPm2Alarm()){
//                Notification("Pm2");}
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    });
    pm10Display.addTextChangedListener(new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            float pm10=SensorDataDatabaseHelper.COLUMN_PM10;
//            if( pm10>SensorSingleton.Instance.getPm10Alarm()){
//                Notification("Pm10");}
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
        unregisterReceiver(pm1Receiver);
        unregisterReceiver(pm2Receiver);
        unregisterReceiver(pm10Receiver);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_settings:
                Intent goToSettings = new Intent(this, SettingsView.class);
                startActivity(goToSettings);
            case R.id.menu_home:
                return true;
            case R.id.menu_history:
                Intent goToHistory = new Intent(this, HistoryView.class);
                startActivity(goToHistory);
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

    private final BroadcastReceiver ccsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_EXTRA_PERIPHERAL));
            CcsMeasurement measurement = (CcsMeasurement) intent.getSerializableExtra(BluetoothHandler.MEASUREMENT_CCS_EXTRA);
            co2Display.setText("Co2:\n"+Long.toString(measurement.co2));
            vocDisplay.setText("VOC:\n"+Long.toString(measurement.voc));
        }
    };

    private final BroadcastReceiver dhtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_EXTRA_PERIPHERAL));
            DhtMeasurement measurement = (DhtMeasurement) intent.getSerializableExtra(BluetoothHandler.MEASUREMENT_DHT_EXTRA);
            tempDisplay.setText(Float.toString(measurement.temp)+"\u00B0");
            humDisplay.setText(Float.toString(measurement.hum)+"%");
        }
    };

    private final BroadcastReceiver pm1Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_EXTRA_PERIPHERAL));
            Pm1Measurement measurement = (Pm1Measurement) intent.getSerializableExtra(BluetoothHandler.MEASUREMENT_PM1_EXTRA);
            pm1Display.setText("PM1:\n"+Integer.toString(measurement.pm1));

        }
    };

    private final BroadcastReceiver pm2Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_EXTRA_PERIPHERAL));
            Pm2Measurement measurement = (Pm2Measurement) intent.getSerializableExtra(BluetoothHandler.MEASUREMENT_PM2_EXTRA);
            pm2Display.setText("PM2:\n"+Integer.toString(measurement.pm2));

        }
    };

    private final BroadcastReceiver pm10Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_EXTRA_PERIPHERAL));
            Pm10Measurement measurement = (Pm10Measurement) intent.getSerializableExtra(BluetoothHandler.MEASUREMENT_PM10_EXTRA);
            pm10Display.setText("PM10:\n"+Integer.toString(measurement.pm10));

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
                initBtHandler();
            }
        } else {
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
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
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
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            checkPermissions();
                        }
                    })
                    .create()
                    .show();
        }
    }
    @SuppressLint("MissingPermission")
    public void Notification(String sensor_alert){
        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("my note","My big note",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager= getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            builder= new NotificationCompat.Builder(HomeView.this, "my note");
            builder.setChannelId("my note");
        }
        else{
            builder= new NotificationCompat.Builder(HomeView.this,"my notif");
        }

        builder.setContentTitle(sensor_alert+" beyond threshold")
                .setContentText(sensor_alert+" has gone beyond the limit set.")
                .setSmallIcon(R.drawable.humidity)
                .setAutoCancel(true);
        //can also set intent to go to when clicked
        NotificationManagerCompat managerCompat= NotificationManagerCompat.from(HomeView.this);

        //TODO add permission check android.permission.POST_NOTIFICATIONS
        managerCompat.notify(1,builder.build());
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
        } else if (sensor.equals("pm")) { //ug per cumib meter, ug/m3
            return -1;
        } else return -1;
    }

    //TODO add proper bounds of Pm readings
    public void textViewHandler(String sensor, int readingInt) {
        boolean flagRed = false, flagYellow = false, flagGreen = false;
        switch (sensor) {
            case "co2":
                if (readingInt >= 0 && readingInt <= 1000) {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_green));
//                flagGreen
                } else if (readingInt > 1000 && readingInt <= 8000) {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_yellow));
//                flagYellow
                } else {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_red));
//                flagRed
                }
                break; //0-1000 ppm is good(green), 1500-8000 unhealthy(yellow), 8000-30000 serious health risk(orange), 30000+ critical (red) [ppm]
            case "voc":
                if (readingInt >= 0 && readingInt <= 220) {
                    vocDisplay.setBackground(getResources().getDrawable(R.drawable.sensor_display_green));
//                flagGreen
                } else if (readingInt > 220 && readingInt <= 660) {
                    vocDisplay.setBackground(getResources().getDrawable(R.drawable.sensor_display_yellow));
//                flagYellow
                } else {
                    vocDisplay.setBackground(getResources().getDrawable(R.drawable.sensor_display_red));
//                flagRed
                }
                break;//0-220 is good (green), 220-660 ( yellow), 660-2000(orange), 2000+(red) [ppb]
            case "pm1":
                //TODO Set default values, these are copied from Co2
                if (readingInt >= 0 && readingInt <= 1111) {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_green));
//                flagGreen
                } else if (readingInt > 1000 && readingInt <= 8000) {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_yellow));
//                flagYellow
                } else {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_red));
//                flagRed
                }
                break;
            case "pm2":
                if (readingInt >= 0 && readingInt <= 1222) {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_green));
//                flagGreen
                } else if (readingInt > 1000 && readingInt <= 8000) {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_yellow));
//                flagYellow
                } else {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_red));
//                flagRed
                }
                break;
            case "pm10":
                if (readingInt >= 0 && readingInt <= 1333) {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_green));
//                flagGreen
                } else if (readingInt > 1000 && readingInt <= 8000) {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_yellow));
//                flagYellow
                } else {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_red));
//                flagRed
                }
                break;
            default:
                break;
        }
    }

    //init function for View and Alarm
    public void initView() {
        co2Display = findViewById(R.id.co2Display);
        vocDisplay = findViewById(R.id.vocDisplay);
        tempDisplay = findViewById(R.id.tempDisplay);
        humDisplay = findViewById(R.id.humDisplay);
        pm1Display=findViewById(R.id.pm1Display);
        pm2Display=findViewById(R.id.pm2Display);
        pm10Display=findViewById(R.id.pm10Display);
        navbot = findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this);
        navbot.setSelectedItemId(R.id.menu_home);
    }

    public void initSensorAlarm() {
        sensorSingleton.Instance.setCo2Alarm(sensorSingleton.Co2Default);
        sensorSingleton.Instance.setVocAlarm(sensorSingleton.VocDefault);
        sensorSingleton.Instance.setPm1Alarm(sensorSingleton.Pm1Default);
        sensorSingleton.Instance.setPm2Alarm(sensorSingleton.Pm2Default);
        sensorSingleton.Instance.setPm10Alarm(sensorSingleton.Pm10Default);

    }
}

