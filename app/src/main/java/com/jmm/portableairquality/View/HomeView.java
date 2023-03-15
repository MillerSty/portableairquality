package com.jmm.portableairquality.View;
import static java.lang.Thread.sleep;
import com.jmm.portableairquality.Model.Data;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jmm.portableairquality.Controller.BluetoothModel;
import com.jmm.portableairquality.R;

import java.io.IOException;
import java.util.UUID;
//co2 reference levels:
//https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.onsetcomp.com%2Fblog%2Fimprove-your-iaq-and-monitor-co2&psig=AOvVaw0M9_dsv9drmD09f1TpiSWW&ust=1678454133577000&source=images&cd=vfe&ved=0CA8QjRxqFwoTCKi8mKT3zv0CFQAAAAAdAAAAABAD
//voc reference levels:
//https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.semanticscholar.org%2Fpaper%2FTotal-Volatile-Organic-Compounds-%2528TVOC%2529-and-Indoor%2F85fd410bd8946b621dfde0298c9555b3c78b5df0&psig=AOvVaw1TTRW6qEOwOD_cKrlvc8Tr&ust=1678466726338000&source=images&cd=vfe&ved=0CBAQjRxqFwoTCKiKzfqlz_0CFQAAAAAdAAAAABAi
public class HomeView extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView navbot;
    TextView co2Display;
    TextView vocDisplay;
    TextView tempDisplay;
    TextView humDisplay;

    BluetoothAdapter bluey;
    BluetoothSocket sockey;
    BluetoothModel drivey;
    private static final int REQUEST_ENABLE_BT = 0;
    //    String MAC_ADD = "24:6F:28:1A:72:9E"; //SparkFun Thing MAC Address
    String MAC_ADD = "70:B8:F6:5C:C3:6E"; //simulated device
    static String TAG = "CONNECT_DEVICE_ACTIVITY";

    Handler handler;

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
        setContentView(R.layout.activity_main_alternate);
        initializeOnCreate();
        initBT();
        initHandler();
        /*
        This will work for setting tips to homeView
        LinearLayout parent=findViewById(R.id.testable);
        View childview= LayoutInflater.from(getApplicationContext()).inflate(R.layout.textable,parent,false);
        View childview2= LayoutInflater.from(getApplicationContext()).inflate(R.layout.textable2,parent,false);
        View childview3=LayoutInflater.from(getApplicationContext()).inflate(R.layout.textable2,parent,false);
        parent.addView(childview2);
        parent.addView(childview);
        parent.addView(childview3);

        */


//can we do if bluetoothadapter is connected here to disable bluetooth functionality?
        Thread updateData = new Thread(() -> {
            while(true) {
                parseSensorData();
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        updateData.start();
    }


    public void initializeOnCreate(){
        co2Display=findViewById(R.id.co2Display);
        vocDisplay=findViewById(R.id.vocDisplay);
        tempDisplay=findViewById(R.id.tempDisplay);
        humDisplay=findViewById(R.id.humDisplay);

        navbot=findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this);
        navbot.setSelectedItemId(R.id.menu_home);

    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_settings:
                Intent goToSettings=new Intent(this, SettingsView.class);
                startActivity(goToSettings);
            case R.id.menu_home:
                return true;
            case R.id.menu_history:
                Intent goToHistory=new Intent(this, HistoryView.class);
                startActivity(goToHistory);
            default:
                return false;
        }
    }



    void initHandler() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    if (Data.sensorData == null) {return;}
                    int co2 = ((Data.sensorData[1] & 0xFF) << 8 | Data.sensorData[0] & 0xFF);
                    int voc = ((Data.sensorData[3] & 0xFF) << 8 | Data.sensorData[2] & 0xFF);
                    int tempInt = Data.sensorData[4] & 0xFF;
                    int tempFrac = Data.sensorData[5] & 0xFF;
                    int humInt = Data.sensorData[6] & 0xFF;
                    int humFrac = Data.sensorData[7] &0xFF;
                    float temp = tempInt + (float)tempFrac/100;
                    float hum = humInt + (float)humFrac/100;


                    String co2Text = "eCO2:\n"+ co2 + "ppm";
                    String vocText = "tVOC\n"+ voc+ "ppb";
                    String tempText = ""+ temp + "°C"; //"Temperature:\n"+ temp + "°C";
                    String humText = ""+ hum + "%";  // "Humidity:\n"+ hum + "%";

                    co2Display.setText(co2Text);
                    vocDisplay.setText(vocText);
                    tempDisplay.setText(tempText);
                    humDisplay.setText(humText);
                    //TODO save data to DB
                    textViewHandler("co2",co2);

                    textViewHandler("voc",voc);


                }
            }

        };
    }

    void initBT() {
        bluey = BluetoothAdapter.getDefaultAdapter();
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
                sockey.connect();
                drivey = new BluetoothModel(sockey);
                drivey.start();
            } catch (IOException e) {
                Log.d(TAG, "Socket Creation Failed");
            }
        }
    }

    void parseSensorData() {
        Message msg = new Message();
        handler.sendEmptyMessage(1);
    }
    public int PPxToPercent(int sensorReading,String sensor){
        if(sensor.equals("co2")){
            return sensorReading=sensorReading/10_000; // this is going to be very small

        } else if (sensor.equals("voc")) {
            return sensorReading=sensorReading/1_000_000;
        }
        else if (sensor.equals("pm")){ //ug per cumib meter, ug/m3
            return -1;
        }
        else return -1;
    }
    //reverse PPxToPercent
    public int PercentToPPx(int sensorReading,String sensor){
        if(sensor.equals("co2")){
            return sensorReading=sensorReading*10_000; // this is going to be very small

        } else if (sensor.equals("voc")) {
            return sensorReading=sensorReading*1_000_000;
        }
        else if (sensor.equals("pm")){ //ug per cumib meter, ug/m3
            return -1;
        }
        else return -1;
    }
    //TODO set colors and text?
    public void textViewHandler(String sensor,int readingInt){
        boolean flagRed=false,flagYellow=false,flagGreen=false;
        switch(sensor){
            case "co2":
                if(readingInt>=0&&readingInt<=1000){
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_green));
//                flagGreen
                }
                else if(readingInt>1000&&readingInt<=8000){
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_yellow));
//                flagYellow
                }
                else {
                    co2Display.setBackground(getResources().getDrawable(R.drawable.sensor_display_red));
//                flagRed
                } break; //0-1000 ppm is good(green), 1500-800 unhealthy(yellow), 800-30000 serious health risk(orange), 30000+ critical (red) [ppm]
            case "voc":
                if(readingInt>=0&&readingInt<=220){
                    vocDisplay.setBackground(getResources().getDrawable(R.drawable.sensor_display_green));
//                flagGreen
                }
                else if(readingInt>220&&readingInt<=660){
                    vocDisplay.setBackground(getResources().getDrawable(R.drawable.sensor_display_yellow));
//                flagYellow
                }
                else {
                    vocDisplay.setBackground(getResources().getDrawable(R.drawable.sensor_display_red));
//                flagRed
                } break;//0-220 is good (green), 220-660 ( yellow), 660-2000(orange), 2000+(red) [ppb]
            case"pm":
                //not implemented yet
                break;
            default:break;}
    }


    //toast message function
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}