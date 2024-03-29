// code adapted from https://github.com/weliem/blessed-android/blob/master/app/src/main/java/com/welie/blessedexample/BluetoothHandler.java
package com.jmm.portableairquality.Model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.UUID;

import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.BondState;
import com.welie.blessed.GattStatus;
import com.welie.blessed.HciStatus;

public class BluetoothHandler {
    private static final String MAC_ADD = "24:6F:28:1A:72:9E"; //SparkFun Thing MAC Address
    public static final String MEASUREMENT_CCS = "paq.measurement.css";
    public static final String MEASUREMENT_CCS_EXTRA = "paq.measurement.css.extra";
    public static final String MEASUREMENT_DHT = "paq.measurement.dht";
    public static final String MEASUREMENT_DHT_EXTRA = "paq.measurement.dht.extra";
    public static final String MEASUREMENT_PM = "paq.measurement.pm1";
    public static final String MEASUREMENT_PM_EXTRA = "paq.measurement.pm1.extra";
    public static final String MEASUREMENT_EXTRA_PERIPHERAL = "paq.measurement.peripheral";

    public static final String UPDATED = "paq.measurement.updated";

    private static final UUID PAQ_SERVICE_UUID = UUID.fromString("87843d21-fd03-4307-8a95-bd3d76b49644");

    private static final UUID CCS_CHAR_UUID = UUID.fromString("5f551915-bdc8-4bac-b9df-12256620e1cf");
    private static final UUID DHT_CHAR_UUID = UUID.fromString("f78ebbff-c8b7-4107-93de-889a6a06d408");
    private static final UUID PM_CHAR_UUID = UUID.fromString("ca73b3ba-39f6-4ab3-91ae-186dc9577d99");

    public BluetoothCentralManager btCentral;
    private static BluetoothHandler btHandler = null;
    private final Context context;
    private final Handler handler = new Handler();

    SensorDataDatabaseHelper db;

    long co2, voc;
    float temp, hum, pm;

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(@NonNull BluetoothPeripheral peripheral) {
            // turn on notifications for the different characteristics

            peripheral.setNotify(PAQ_SERVICE_UUID, DHT_CHAR_UUID, true);
            peripheral.setNotify(PAQ_SERVICE_UUID, PM_CHAR_UUID, true);
            peripheral.setNotify(PAQ_SERVICE_UUID, CCS_CHAR_UUID, true);
        }

        @Override
        public void onCharacteristicUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            if (status != GattStatus.SUCCESS) return;

            // send intent to update the graph
            Intent intent = new Intent(UPDATED);
            sendMeasurement(intent, peripheral);

            UUID characteristicUUID = characteristic.getUuid();

            if (characteristicUUID.equals(CCS_CHAR_UUID)) {
                CcsMeasurement measurement = new CcsMeasurement(value);

                co2 = measurement.co2;
                voc = measurement.voc;

                intent = new Intent(MEASUREMENT_CCS);
                intent.putExtra(MEASUREMENT_CCS_EXTRA, measurement);
                sendMeasurement(intent, peripheral);
            } else if (characteristicUUID.equals(DHT_CHAR_UUID)) {
                DhtMeasurement measurement = new DhtMeasurement(value);

                temp = measurement.temp;
                hum = measurement.hum;

                intent = new Intent(MEASUREMENT_DHT);
                intent.putExtra(MEASUREMENT_DHT_EXTRA, measurement);
                sendMeasurement(intent, peripheral);
            } else if (characteristicUUID.equals(PM_CHAR_UUID)) {
                PmMeasurement measurement = new PmMeasurement(value);

                pm = measurement.pm;

                intent = new Intent(MEASUREMENT_PM);
                intent.putExtra(MEASUREMENT_PM_EXTRA, measurement);
                sendMeasurement(intent, peripheral);
            }
        }
    };

    private void sendMeasurement(@NotNull Intent intent, @NotNull BluetoothPeripheral peripheral) {
        intent.putExtra(MEASUREMENT_EXTRA_PERIPHERAL, peripheral.getAddress());
        context.sendBroadcast(intent);
    }

    private final BluetoothCentralManagerCallback managerCallback = new BluetoothCentralManagerCallback() {
        @Override
        public void onConnectedPeripheral(@NotNull BluetoothPeripheral peripheral) {
            Log.d("BTModel", "Connected to device");
            db = new SensorDataDatabaseHelper(context.getApplicationContext());
        }

        @Override
        public void onDisconnectedPeripheral(@NotNull final BluetoothPeripheral peripheral, final @NotNull HciStatus status) {
            Log.d("BTModel", "Disconnected from device");

            // Reconnect to this device when it becomes available again
            handler.postDelayed(() -> btCentral.autoConnectPeripheral(peripheral, peripheralCallback), 2000);
        }

        @Override
        public void onDiscoveredPeripheral(@NotNull BluetoothPeripheral peripheral, @NotNull ScanResult scanResult) {
            if (peripheral.getAddress() == MAC_ADD && peripheral.getBondState() == BondState.NONE) {
                btCentral.stopScan();
                // Create a bond immediately to avoid double pairing popups
                btCentral.createBond(peripheral, peripheralCallback);
            } else {
                btCentral.connectPeripheral(peripheral, peripheralCallback); // only connects to bonded devices?
            }
        }

        @Override
        public void onBluetoothAdapterStateChanged(int state) {
            if (state == BluetoothAdapter.STATE_ON) {
                // Bluetooth is on now, start scanning again
                // Scan for peripherals with a certain service UUIDs
                btCentral.startPairingPopupHack();
                startScan();
            }
        }
    };

    public static synchronized BluetoothHandler getInstance(Context context) {
        if (btHandler == null) {
            btHandler = new BluetoothHandler(context.getApplicationContext());
        }
        return btHandler;
    }

    private BluetoothHandler(Context context) {
        this.context = context;

        btCentral = new BluetoothCentralManager(context, managerCallback, new Handler());

        btCentral.startPairingPopupHack();
        startScan();
        Thread thread = new Thread() {
            @Override
            public void run() {
                SensorDataDatabaseHelper db = SensorDataDatabaseHelper.getInstance(context);
                Date date;

                while(true) {
                    SharedPreferences sharedPref = context.getSharedPreferences("hey",Context.MODE_PRIVATE);
                    double latitude= Double.longBitsToDouble(sharedPref.getLong("Lat", 0));
                    double longitude=Double.longBitsToDouble(sharedPref.getLong("Long",0));
                    int nox = 0;
                    date = new Date();
                    db.addSensorData(date, latitude, longitude, temp, hum, pm, nox, co2, voc);
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        thread.start();
    }

    private void startScan() {
        handler.postDelayed(() -> btCentral.scanForPeripheralsWithServices(new UUID[] {PAQ_SERVICE_UUID}), 1000);
    }
}


