package com.jmm.portableairquality.Controller;

import static android.app.PendingIntent.getActivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class BluetoothModel {
    private BluetoothSocket bluetoothSocket;
    private BluetoothAdapter bluetoothAdapter;
    private InputStream inputStream;
    private OutputStream outputStream;
    private static String TAG="BluetoothModel.java";

    public BluetoothModel(BluetoothSocket bluetoothSocket) throws IOException {
        this.bluetoothSocket = bluetoothSocket;
        InputStream tmpI=null;
        OutputStream tmpO=null;
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        try{
            tmpI= bluetoothSocket.getInputStream();
        }catch(IOException e){
            Log.e(TAG," Socket create failed "+e.toString());
        }
        try{
            tmpO=bluetoothSocket.getOutputStream();
        }catch(IOException e){
            Log.d(TAG," Socket Output stream failed "+e.toString());
        }

        inputStream=tmpI;
        outputStream=tmpO;
    }

    public void write(byte[] bytes) throws IOException {
        try {
            outputStream.write(bytes);
        }catch(IOException e){
            Log.d(TAG," Error sending data "+e.toString());
        }
    }

    public byte[] read() {
        byte[] buffer = new byte[13];

        // Try to read into buffer from inputStream
        try {
            inputStream.read(buffer,0,13);
        } catch (IOException e) {
            // nothing
        }
        return buffer;
    }
}
