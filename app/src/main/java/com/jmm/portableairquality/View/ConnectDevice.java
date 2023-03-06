package com.jmm.portableairquality.View;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.jmm.portableairquality.Controller.BluetoothModel;
import com.jmm.portableairquality.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.jmm.portableairquality.Controller.BluetoothModel;
import com.jmm.portableairquality.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

// how to pair in app https://www.youtube.com/watch?v=YJ0JQXcNNTA
// this was made following this tutorial https://www.youtube.com/watch?v=wLRQ9EClYuA
public class ConnectDevice extends AppCompatActivity {
    static String TAG = "CONNECT_DEVICE_ACTIVITY";
    String MAC_ADD = "24:6F:28:1A:72:9E"; //for esp thing
    TextView mStatusBlueTv, mPairedTv, mReading;
    ImageView mBlueIv;
    Button mOnbtn, mOffBtn, mDiscoverableBtn, mPairedBtn, mcreateListener, mgetRead;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothsocket;
    Handler handler;
    BluetoothModel bluetoothDriver;
    ArrayList<String> appendable;
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectdevice);
        appendable = new ArrayList<>();
        //viewbinding instead to initialize things
        mReading = findViewById(R.id.ReadTv);
//        mStatusBlueTv = findViewById(R.id.statusBluetoothtv);
        mPairedTv = findViewById(R.id.pairedTv);
        mBlueIv = findViewById(R.id.blueIv);
        mOnbtn = findViewById(R.id.onBtn);
        mOffBtn = findViewById(R.id.offBtn);
//        mDiscoverableBtn = findViewById(R.id.discoverableBtn);
        mPairedBtn = findViewById(R.id.pairedBtn);
        mgetRead = findViewById(R.id.btnRead);
        mcreateListener = findViewById(R.id.createListener);

        //adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //TODO feature we worked on
        handler = new Handler(Looper.myLooper());

        // if bluetooth is enabled, create a listener
        if (checkBT()) {
            try {
                createListener(MAC_ADD);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            turnOnBluetooth();
        }


        mgetRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    callRead();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        mOnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnBluetooth();
                checkBT();
            }
        });


        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOffBluetooth();
                checkBT();
            }

        });

        //discover blutooth btn
        mDiscoverableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDiscovery();
            }
        });

        //get paired devices btn click
        mPairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPairedDevices();
            }
        });

        mcreateListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createListener(MAC_ADD);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    void startDiscovery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (!bluetoothAdapter.isDiscovering()) {
            showToast("Making your device discoverable");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(intent, REQUEST_DISCOVER_BT);
        }
    }

    void turnOnBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            showToast("Turning on bluetooth...");
            //intent to on bluetooth
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        } else {
            showToast("Bluetooth already on");
        }

        checkBT();
    }

    void turnOffBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                showToast("No permission to turn off?");
                return;
            }
            bluetoothAdapter.disable();
            showToast("Turning bluetooth off");
            mBlueIv.setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            showToast("Bluetooth is already off");
        }
    }

    void getPairedDevices() {
        String str = "";
        if (bluetoothAdapter.isEnabled()) {
            mPairedTv.setText("Paired Devices: ");

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                appendable.add("Device "+device.getName()+ " ,"+ device);
                //mPairedTv.setText("\nDevice: " + device.getAlias() + " \nMAC: " + device);
            }
        } else {
            //bluetooth is off so cant get paired devices
            showToast("Turn on bt to get paired devices");
        }
        for(int i=0;i<appendable.size();i++){
            str= str+" \n "+appendable.get(i);

        }
        mPairedTv.setText(str);
    }

    public Boolean checkBT() {
        //check if bluetooth is available or not
        Boolean status = false;
        if (bluetoothAdapter == null) {
            mStatusBlueTv.setText("Bluetooth is not available");
        } else {
            mStatusBlueTv.setText("Bluetooth is available");
        }

        //if bluetooth is enabled  set image view accordingly
        if (bluetoothAdapter.isEnabled()) {
            mBlueIv.setBackgroundColor(getResources().getColor(R.color.red_200));
            status = true;
        } else {
            mBlueIv.setBackgroundColor(getResources().getColor(R.color.black));
        }
        for (int i = 0; i < appendable.size(); i++) {
            mPairedTv.append(appendable.get(i));
        }
        return status;
    }

    //TODO Functionality we worked on
    public void createListener(String mac_address) throws IOException {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            try {
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mac_address);
                ParcelUuid[] uuid = device.getUuids();
                String uid = uuid[0].toString();
                bluetoothsocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uid));
                //bluetoothAdapter.cancelDiscovery();
                bluetoothsocket.connect();
                bluetoothDriver = new BluetoothModel(bluetoothsocket, handler);
            } catch (IOException e) {
                Log.d(TAG, "HELLO ERROR MY OLD FRIEND");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    //bluetooth is on
                    mBlueIv.setBackgroundColor(getResources().getColor(R.color.red_200));
                    showToast("Bluetooth is on");
                } else if (resultCode == RESULT_CANCELED){
                    mBlueIv.setBackgroundColor(getResources().getColor(R.color.black));
                    showToast("Bluetooth could not On");
                } else {
                    mBlueIv.setBackgroundColor(getResources().getColor(R.color.black));
                    showToast("Bluetooth could not On");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    //toast message function
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void callRead() throws IOException {
        //connect to PAQ
        //TODO createListener, if it doesnt work off the bat
        try {
            String str = "0";
            byte[] byteArr = str.getBytes("UTF-8");
            bluetoothDriver.write(byteArr);
        } catch(Exception e) {
            // nothing
        }
        byte[] msg = bluetoothDriver.read();
        int co2 = (msg[1] << 8 | msg[0]) & 0xFFFF;
        int voc = (msg[3] << 8 | msg[2]) & 0xFFFF;
        int tempInt = msg[4] & 0xFF;
        int tempFrac = msg[5] & 0xFF;
        int humInt = msg[6] & 0xFF;
        int humFrac = msg[7] &0xFF;
        float temp = tempInt + tempFrac/100;
        float hum = humInt + humFrac/100;

        mReading.setText(co2 + ", " + voc + "\n" + temp + ", " + hum);
    }
}