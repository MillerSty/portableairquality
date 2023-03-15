// bluetooth code adapted from https://github.com/weliem/blessed-android/blob/master/app/src/main/java/com/welie/blessedexample/MainActivity.java
package com.jmm.portableairquality.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
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
import com.jmm.portableairquality.R;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothPeripheral;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int ACCESS_LOCATION_REQUEST = 2;

    BottomNavigationView navbot;
    TextView co2Display;
    TextView vocDisplay;
    TextView tempDisplay;
    TextView humDisplay;
    Button booboo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        co2Display = findViewById(R.id.co2Display);
        vocDisplay = findViewById(R.id.vocDisplay);
        tempDisplay = findViewById(R.id.tempDisplay);
        humDisplay = findViewById(R.id.humDisplay);
        booboo = findViewById(R.id.changeColor);
        navbot = findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this);
        navbot.setSelectedItemId(R.id.menu_home);
        Log.d("Color", Integer.toHexString(R.color.red_200));

        booboo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // test button
            }
        });

        registerReceiver(locationReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));
        registerReceiver(ccsReceiver, new IntentFilter(BluetoothHandler.MEASUREMENT_CCS));
        registerReceiver(dhtReceiver, new IntentFilter(BluetoothHandler.MEASUREMENT_DHT));
        registerReceiver(pm1Receiver, new IntentFilter(BluetoothHandler.MEASUREMENT_PM1));
        registerReceiver(pm2Receiver, new IntentFilter(BluetoothHandler.MEASUREMENT_PM2));
        registerReceiver(pm10Receiver, new IntentFilter(BluetoothHandler.MEASUREMENT_PM10));
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_settings:
                //Intent goToSettings=new Intent(HomeActivity.this, ConnectDevice.class);
                //startActivity(goToSettings);
                showToast("CLICKED SETTINGS");
                return true;
            case R.id.menu_home:
                showToast("CLICKED HOME");
                return true;
            case R.id.menu_history:
                showToast("CLICKED HISTORY");
                return true;
            default:
                return false;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(ccsReceiver);
        unregisterReceiver(dhtReceiver);
        unregisterReceiver(pm1Receiver);
        unregisterReceiver(pm2Receiver);
        unregisterReceiver(pm10Receiver);
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
            co2Display.setText(Long.toString(measurement.co2));
            vocDisplay.setText(Long.toString(measurement.voc));
        }
    };

    private final BroadcastReceiver dhtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_EXTRA_PERIPHERAL));
            DhtMeasurement measurement = (DhtMeasurement) intent.getSerializableExtra(BluetoothHandler.MEASUREMENT_DHT_EXTRA);
            tempDisplay.setText(Float.toString(measurement.temp));
            humDisplay.setText(Float.toString(measurement.hum));
        }
    };

    private final BroadcastReceiver pm1Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_EXTRA_PERIPHERAL));
            Pm1Measurement measurement = (Pm1Measurement) intent.getSerializableExtra(BluetoothHandler.MEASUREMENT_PM1_EXTRA);
            // TODO pm1, pm2.5, pm10 textviews
        }
    };

    private final BroadcastReceiver pm2Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_EXTRA_PERIPHERAL));
            Pm2Measurement measurement = (Pm2Measurement) intent.getSerializableExtra(BluetoothHandler.MEASUREMENT_PM2_EXTRA);
            // TODO pm1, pm2.5, pm10 textviews
        }
    };

    private final BroadcastReceiver pm10Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_EXTRA_PERIPHERAL));
            Pm10Measurement measurement = (Pm10Measurement) intent.getSerializableExtra(BluetoothHandler.MEASUREMENT_PM10_EXTRA);
            // TODO pm1, pm2.5, pm10 textviews
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
            new AlertDialog.Builder(HomeActivity.this)
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
            new AlertDialog.Builder(HomeActivity.this)
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
}

