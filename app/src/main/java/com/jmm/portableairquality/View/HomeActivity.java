package com.jmm.portableairquality.View;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.jmm.portableairquality.Model.BluetoothHandler;
import com.jmm.portableairquality.R;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothPeripheral;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_ENABLE_BT = 1;

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
        }
    }

    private boolean isBluetoothEnabled() {
        BluetoothAdapter bluey = BluetoothAdapter.getDefaultAdapter();
        if (bluey == null) return false;

        return bluey.isEnabled();
    }

    private void initBtHandler() {
        BluetoothHandler.getInstance(this);
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

    private final BroadcastReceiver ccsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_CCS)); // getStringExtra?
            //TODO
        }
    };

    private final BroadcastReceiver dhtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_DHT)); // getStringExtra?
            //TODO
        }
    };

    private final BroadcastReceiver pm1Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_PM1)); // getStringExtra?
            //TODO
        }
    };

    private final BroadcastReceiver pm2Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_PM2)); // getStringExtra?
            //TODO
        }
    };

    private final BroadcastReceiver pm10Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothPeripheral peripheral = getPeripheral(intent.getStringExtra(BluetoothHandler.MEASUREMENT_PM10)); // getStringExtra?
            //TODO
        }
    };

    private BluetoothPeripheral getPeripheral(String peripheralAddress) {
        BluetoothCentralManager central = BluetoothHandler.getInstance(getApplicationContext()).btCentral;
        return central.getPeripheral(peripheralAddress);
    }


//    String co2Text = "eCO2:\n" + co2 + "ppm";
//    String vocText = "tVOC\n" + voc + "ppb";
//    String tempText = "Temp:\n" + temp + "°C";
//    String humText = "Humidity:\n" + hum + "%";
//
//    co2Display.setText(co2Text);
//    vocDisplay.setText(vocText);
//    tempDisplay.setText(tempText);
//    humDisplay.setText(humText);
//    //TODO save data to DB


    //toast message function
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

