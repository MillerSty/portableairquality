package com.jmm.portableairquality.View;

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

import com.jmm.portableairquality.Controller.BluetoothControl;
import com.jmm.portableairquality.R;

import java.io.IOException;
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

import com.jmm.portableairquality.Controller.BluetoothControl;
import com.jmm.portableairquality.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

// how to pair in app https://www.youtube.com/watch?v=YJ0JQXcNNTA
// this was made following this tutorial https://www.youtube.com/watch?v=wLRQ9EClYuA
public class ConnectDevice extends AppCompatActivity {
    static String TAG = "CONNECT_DEVICE_ACTIVITY";
    String MAC_ADD = "24:F:28:1A:72:9E"; //for esp thing
    //String MAC_ADD = "F4:4E:FD:F0:FA:FA"; //try with home bt speaker
    TextView mStatusBlueTv, mPairedTv, mReading;
    ImageView mBlueIv;
    Button mOnbtn, mOffBtn, mDiscoverableBtn, mPairedBtn, mcreateListener, mgetRead;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothsocket;
    Handler handler;
    BluetoothControl bluetoothControl;
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
        mStatusBlueTv = findViewById(R.id.statusBluetoothtv);
        mPairedTv = findViewById(R.id.pairedTv);
        mBlueIv = findViewById(R.id.blueIv);
        mOnbtn = findViewById(R.id.onBtn);
        mOffBtn = findViewById(R.id.offBtn);
        mDiscoverableBtn = findViewById(R.id.discoverableBtn);
        mPairedBtn = findViewById(R.id.pairedBtn);
        mgetRead = findViewById(R.id.btnRead);
        mcreateListener = findViewById(R.id.createListener);

        //adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //TODO feature we worked on

//checkBT(); //checks if bluetooth is enabled?


        mgetRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callRead();
                //mgetRead.setText(Reading)
            }
        });
        mOnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    showToast("Turning on bluetooth...");
                    //intent to on bluetooth
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                } else {
                    showToast("Bluetooth already on");
                }

                checkBT();
            }
        });
        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                    showToast("Turning bluetooth off");
                    mBlueIv.setBackgroundColor(getResources().getColor(R.color.black));
                } else {
                    showToast("Bluetooth is already off");
                }
                checkBT();
            }

        });

//discover blutooth btn
        mDiscoverableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isDiscovering()) {
                    showToast("Making your device discoverable");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }

            }
        });

        //get paired devices btn click
        mPairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str="";
                if (bluetoothAdapter.isEnabled()) {
                    mPairedTv.setText("Paired Devices: ");
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

    public void checkBT() {
        //check if bluetooth is available or not
        if (bluetoothAdapter == null) {
            mStatusBlueTv.setText("Bluetooth is not available");
        } else {
            mStatusBlueTv.setText("Bluetooth is available");
        }

        //if bluetooth is enabled  set image view accordingly
        if (bluetoothAdapter.isEnabled()) {
            mBlueIv.setBackgroundColor(getResources().getColor(R.color.red_200));
        } else {
            mBlueIv.setBackgroundColor(getResources().getColor(R.color.black));
        }
        for (int i = 0; i < appendable.size(); i++) {
            mPairedTv.append(appendable.get(i));
        }

    }

    //TODO Functionality we worked on
    public void createListener(String mac_address) throws IOException {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            try {
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mac_address);
                ParcelUuid[] uuid = device.getUuids();
                String uid = uuid[0].toString();
                bluetoothsocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uid));
                bluetoothAdapter.cancelDiscovery();
                bluetoothsocket.connect();
                bluetoothControl = new BluetoothControl(bluetoothsocket, handler);
                bluetoothControl.start();

                String str = "hel";
                byte[] byteArr = str.getBytes("UTF-8");
                bluetoothControl.write(byteArr);
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

    private void callRead() {
        //connect to PAQ
        //TODO createListener, if it doesnt work off the bat
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message messageReceived) {
                super.handleMessage(messageReceived);
                if (messageReceived.what == BluetoothControl.MessageConstants.MESSAGE_READ) {
                    String readMessage = (String) messageReceived.obj;
                    float temp = Float.parseFloat(readMessage);
                    mgetRead.setText((int) temp);
                }

            }
        };
    }
}
