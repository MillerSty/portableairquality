// code adapted from https://github.com/weliem/blessed-android/blob/master/app/src/main/java/com/welie/blessedexample/BluetoothHandler.java
package com.jmm.portableairquality.Model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;
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
    public static final String MEASUREMENT_PM1 = "paq.measurement.pm1";
    public static final String MEASUREMENT_PM1_EXTRA = "paq.measurement.pm1.extra";
    public static final String MEASUREMENT_PM2 = "paq.measurement.pm2";
    public static final String MEASUREMENT_PM2_EXTRA = "paq.measurement.pm2.extra";
    public static final String MEASUREMENT_PM10 = "paq.measurement.pm10";
    public static final String MEASUREMENT_PM10_EXTRA = "paq.measurement.pm10.extra";
    public static final String MEASUREMENT_EXTRA_PERIPHERAL = "paq.measurement.peripheral";

    private static final UUID PAQ_SERVICE_UUID = UUID.fromString("87843d21-fd03-4307-8a95-bd3d76b49644");

    private static final UUID CCS_CHAR_UUID = UUID.fromString("5f551915-bdc8-4bac-b9df-12256620e1cf");
    private static final UUID DHT_CHAR_UUID = UUID.fromString("f78ebbff-c8b7-4107-93de-889a6a06d408");
    private static final UUID PM1_CHAR_UUID = UUID.fromString("ca73b3ba-39f6-4ab3-91ae-186dc9577d99");
    private static final UUID PM2_CHAR_UUID = UUID.fromString("168c693b-b9c8-4053-9fde-be87f7d14b72");
    private static final UUID PM10_CHAR_UUID = UUID.fromString("8e713220-d13a-484d-a251-36d1aa957ecb");

    public BluetoothCentralManager btCentral;
    private static BluetoothHandler btHandler = null;
    private final Context context;
    private final Handler handler = new Handler();

    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(@NonNull BluetoothPeripheral peripheral) {
            // turn on notifications for the different characteristics

            peripheral.setNotify(PAQ_SERVICE_UUID, DHT_CHAR_UUID, true);
            peripheral.setNotify(PAQ_SERVICE_UUID, PM1_CHAR_UUID, true);
            peripheral.setNotify(PAQ_SERVICE_UUID, PM2_CHAR_UUID, true);
            peripheral.setNotify(PAQ_SERVICE_UUID, PM10_CHAR_UUID, true);
            peripheral.setNotify(PAQ_SERVICE_UUID, CCS_CHAR_UUID, true);
        }

        @Override
        public void onCharacteristicUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            if (status != GattStatus.SUCCESS) return;

            UUID characteristicUUID = characteristic.getUuid();

            if (characteristicUUID.equals(CCS_CHAR_UUID)) {
                CcsMeasurement measurement = new CcsMeasurement(value);
                Intent intent = new Intent(MEASUREMENT_CCS);
                intent.putExtra(MEASUREMENT_CCS_EXTRA, measurement);
                sendMeasurement(intent, peripheral);
            } else if (characteristicUUID.equals(DHT_CHAR_UUID)) {
                DhtMeasurement measurement = new DhtMeasurement(value);

                Intent intent = new Intent(MEASUREMENT_DHT);
                intent.putExtra(MEASUREMENT_DHT_EXTRA, measurement);
                sendMeasurement(intent, peripheral);
            } else if (characteristicUUID.equals(PM1_CHAR_UUID)) {
                Pm1Measurement measurement = new Pm1Measurement(value);
                Intent intent = new Intent(MEASUREMENT_PM1);
                intent.putExtra(MEASUREMENT_PM1_EXTRA, measurement);
                sendMeasurement(intent, peripheral);
            } else if (characteristicUUID.equals(PM2_CHAR_UUID)) {
                Pm2Measurement measurement = new Pm2Measurement(value);
                Intent intent = new Intent(MEASUREMENT_PM2);
                intent.putExtra(MEASUREMENT_PM2_EXTRA, measurement);
                sendMeasurement(intent, peripheral);
            } else if (characteristicUUID.equals(PM10_CHAR_UUID)) {
                Pm10Measurement measurement = new Pm10Measurement(value);
                Intent intent = new Intent(MEASUREMENT_PM10);
                intent.putExtra(MEASUREMENT_PM10_EXTRA, measurement);
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
        }

        @Override
        public void onDisconnectedPeripheral(@NotNull final BluetoothPeripheral peripheral, final @NotNull HciStatus status) {
            Log.d("BTModel", "Disconnected from device");

            // Reconnect to this device when it becomes available again
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btCentral.autoConnectPeripheral(peripheral, peripheralCallback);
                }
            }, 5000);
        }

        @Override
        public void onDiscoveredPeripheral(@NotNull BluetoothPeripheral peripheral, @NotNull ScanResult scanResult) {
            Log.d("BTModel", String.format("Found Peripheral", peripheral.getName()));

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
    }

    private void startScan() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btCentral.scanForPeripheralsWithServices(new UUID[] {PAQ_SERVICE_UUID});
            }
        }, 1000);
    }
}


