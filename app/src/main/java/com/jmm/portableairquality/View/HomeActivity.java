package com.jmm.portableairquality.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jmm.portableairquality.Controller.BluetoothModel;
import com.jmm.portableairquality.R;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView navbot;
    TextView co2Display;
    TextView vocDisplay;
    TextView tempDisplay;
    TextView humDisplay;

    Button booboo;
    Boolean flag=false;

    BluetoothAdapter bluey;
    BluetoothSocket sockey;
    BluetoothModel drivey;
    private static final int REQUEST_ENABLE_BT = 0;
    String MAC_ADD = "24:6F:28:1A:72:9E"; //SparkFun Thing MAC Address
    static String TAG = "CONNECT_DEVICE_ACTIVITY";

    String[] PERMISSIONS = {
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.ACCESS_FINE_LOCATION
            , android.Manifest.permission.BLUETOOTH_ADMIN
            , android.Manifest.permission.BLUETOOTH_CONNECT
            , android.Manifest.permission.BLUETOOTH_SCAN
            , android.Manifest.permission.ACCESS_COARSE_LOCATION
            , android.Manifest.permission.READ_EXTERNAL_STORAGE
            , android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    String permission=" Permission Granted: ";
    String denied=" Permission Denied: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        co2Display=findViewById(R.id.co2Display);
        vocDisplay=findViewById(R.id.vocDisplay);
        tempDisplay=findViewById(R.id.tempDisplay);
        humDisplay=findViewById(R.id.humDisplay);

        booboo=findViewById(R.id.changeColor);

        navbot=findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this);
        navbot.setSelectedItemId(R.id.menu_home);
        Log.d("Color",Integer.toHexString(R.color.red_200));


        booboo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readSensorData();
            }
        });


        bluey = BluetoothAdapter.getDefaultAdapter();
        initBT();
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_settings:
                Intent goToSettings=new Intent(HomeActivity.this, ConnectDevice.class);
                startActivity(goToSettings);
                return true;
            case R.id.menu_home:
                Toast.makeText(this," CLICKED HOME", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_history:
                Toast.makeText(this," CLICKED HISTORY", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }

    }

//    void setBooboo() {
//        if(flag){
//            Log.d("Button","Button");
//            display.setBackground(getResources().getDrawable(R.drawable.sensor_display_red));
//            flag=!flag;
//        }
//        else{display.setBackground(getResources().getDrawable(R.drawable.sensor_display_yellow));   flag=!flag;}
//    }

    void initBT() {
        // check if bluetooth is on and request user if not
        if (!bluey.isEnabled()) {
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
        // assuming the user said yes
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            try {
                BluetoothDevice device = bluey.getRemoteDevice(MAC_ADD);
                ParcelUuid[] uuid = device.getUuids();
                String uid = uuid[0].toString();
                sockey = device.createRfcommSocketToServiceRecord(UUID.fromString(uid));
                //bluetoothAdapter.cancelDiscovery();
                sockey.connect();
                drivey = new BluetoothModel(sockey);
            } catch (IOException e) {
                Log.d(TAG, "Socket Creation Failed");
            }
            // if no ask user and then create socket
        }
    }

    void readSensorData() {
        try {
            String str = "0";
            byte[] byteArr = str.getBytes("UTF-8");
            drivey.write(byteArr);
        } catch(Exception e) {
            // nothing
        }
        byte[] msg = drivey.read();
        int co2 = ((msg[1] & 0xFF) << 8 | msg[0] & 0xFF);
        int voc = ((msg[3] & 0xFF) << 8 | msg[2] & 0xFF);
        int tempInt = msg[4] & 0xFF;
        int tempFrac = msg[5] & 0xFF;
        int humInt = msg[6] & 0xFF;
        int humFrac = msg[7] &0xFF;
        float temp = tempInt + (float)tempFrac/100;
        float hum = humInt + (float)humFrac/100;

        String co2Text = "eCO2:\n"+ co2 + "ppm";
        String vocText = "tVOC\n"+ voc + "ppb";
        String tempText = "Temp:\n"+ temp + "??C";
        String humText = "Humidity:\n"+ hum + "%";

        co2Display.setText(co2Text);
        vocDisplay.setText(vocText);
        tempDisplay.setText(tempText);
        humDisplay.setText(humText);
    }

    //toast message function
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}



//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//
//        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
//        if(requestCode== permissionConfig.BLUETOOTH) {
//            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, permission + permissionConfig.BLUETOOTH, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, denied + permissionConfig.BLUETOOTH, Toast.LENGTH_SHORT).show();
//            }
//        }
//        if(requestCode== permissionConfig.ACCESS_FINE_LOCATION) {
//            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, permission + permissionConfig.ACCESS_FINE_LOCATION, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, denied + permissionConfig.ACCESS_FINE_LOCATION, Toast.LENGTH_SHORT).show();
//            }
//        }
//        if(requestCode== permissionConfig.BLUETOOTH_ADMIN) {
//            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, permission + permissionConfig.BLUETOOTH_ADMIN, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, denied + permissionConfig.BLUETOOTH_ADMIN, Toast.LENGTH_SHORT).show();
//            }
//        }
//        if(requestCode== permissionConfig.BLUETOOTH_CONNECT) {
//            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, permission + permissionConfig.BLUETOOTH_CONNECT, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, denied + permissionConfig.BLUETOOTH_CONNECT, Toast.LENGTH_SHORT).show();
//            }
//        }
//        if(requestCode== permissionConfig.BLUETOOTH_SCAN) {
//            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, permission + permissionConfig.BLUETOOTH_SCAN, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, denied + permissionConfig.BLUETOOTH_SCAN, Toast.LENGTH_SHORT).show();
//            }
//        }
//        if(requestCode== permissionConfig.ACCESS_COARSE_LOCATION) {
//            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, permission + permissionConfig.ACCESS_COARSE_LOCATION, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, denied + permissionConfig.ACCESS_COARSE_LOCATION, Toast.LENGTH_SHORT).show();
//            }
//        }
//        if(requestCode== permissionConfig.READ_EXTERNAL_STORAGE) {
//            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, permission + permissionConfig.READ_EXTERNAL_STORAGE, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, denied + permissionConfig.READ_EXTERNAL_STORAGE, Toast.LENGTH_SHORT).show();
//            }
//        }
//        if(requestCode== permissionConfig.WRITE_EXTERNAL_STORAGE) {
//            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, permission + permissionConfig.WRITE_EXTERNAL_STORAGE, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, denied + permissionConfig.WRITE_EXTERNAL_STORAGE, Toast.LENGTH_SHORT).show();
//            }
//        }
//
//            //where 100 is associated to a permission
////            if(grantResults.isNotEmpty() && PackageManager.PERMISSION_GRANTED){
////            show a toast that things are accepted
////            }else { show erro toast}
////            else if(other permissions){ chaing of else if for permission granting}
//
//        }
//        switch (requestCode) {
//            case 1: {
//
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Toast.makeText(HomeActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
   // }


//    public void checkPermission(String permission, int request){
//        if(ContextCompat.checkSelfPermission(this,permission)==PackageManager.PERMISSION_DENIED){
//            ActivityCompat.requestPermissions(this,PERMISSIONS,request);
//
//        }else{
//            Toast.makeText(this,"Permission accepter",Toast.LENGTH_SHORT).show();
//        }
        //note this function is already implemented
//        onRequestPermissionsResult();


//}
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.bottom_bar,menu);
////     Manifest.permission.
//        return super.onCreateOptionsMenu(menu);
//    }

//}