package com.jmm.portableairquality.Controller;
import com.jmm.portableairquality.Model.Data;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothModel extends Thread{
    private final BluetoothSocket bluetoothSocket;
    private final BluetoothAdapter bluetoothAdapter;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final static String TAG="BluetoothModel.java";

    public BluetoothModel(BluetoothSocket bluetoothSocket) throws IOException {
        this.bluetoothSocket = bluetoothSocket;
        InputStream tmpI = null;
        OutputStream tmpO = null;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try{
            tmpI = bluetoothSocket.getInputStream();
        }catch(IOException e){
            Log.e(TAG," Socket create failed " + e.toString());
        }
        try{
            tmpO = bluetoothSocket.getOutputStream();
        }catch(IOException e){
            Log.d(TAG," Socket Output stream failed " + e.toString());
        }

        inputStream = tmpI;
        outputStream = tmpO;
    }

    public void run() {
        while(true) {
            // request data
            requestData();
            // read data
            Data.sensorData = read();

            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void requestData() {
        try {
            outputStream.write(48 );
        }catch(IOException e){
            Log.d(TAG," Error sending data " + e.toString());
        }
    }

    public byte[] read() {
        byte[] buffer = new byte[9];

        // Try to read into buffer from inputStream
        try {
            inputStream.read(buffer,0,9);
        } catch (IOException e) {
            Log.d(TAG, "Error reading data " + e.toString());
        }
        return buffer;
    }
}
