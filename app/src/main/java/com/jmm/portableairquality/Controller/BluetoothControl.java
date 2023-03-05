package com.jmm.portableairquality.Controller;

import static android.app.PendingIntent.getActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.app.PendingIntent.getActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothControl extends Thread {
    private BluetoothSocket bluetoothSocket;
    private BluetoothAdapter bluetoothAdapter;
    private InputStream inputStream;
    private OutputStream outputStream;
    BluetoothDevice bluetoothDevice;
    private byte[] byteBuffer;
    private static String TAG="BluetoothControl.java";
    Handler handler;
    Message message;
    public interface MessageConstants{
        public static int MESSAGE_READ=0;
        public static int MESSAGE_WRITE=1;
        public static int MESSAGE_TOAST=2;}


    Context context;
    public BluetoothControl(BluetoothSocket bluetoothSocket,Handler newHandler) throws IOException {
        this.bluetoothSocket = bluetoothSocket;
        BluetoothSocket temp=null;
        InputStream tmpI=null;
        OutputStream tmpO=null;
        handler=newHandler;
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        try{
            tmpI= bluetoothSocket.getInputStream();
        }catch(IOException e){
            Log.e(TAG," Sockert create failed "+e.toString());
        }
        try{
            tmpO=bluetoothSocket.getOutputStream();
        }catch(IOException e){
            Log.d(TAG," Socket Output stream failed "+e.toString());
        }

        inputStream=tmpI;
        outputStream=tmpO;
    }

    public void runs() throws IOException {
        byte[] buffer=new byte[1024];
        int bytes;
        byteBuffer=new byte[]{11};
        write(byteBuffer);
//        while(true){
//            inputStream.read(buffer);
////            message.what=STATE
//            handler.obtainMessage(5,bytes,-1,buffer).sendToTarget();
//        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while(true) {
            try{
                // The following 5 lines below have been extracted from the cited source
                // ----
                String response = bufferedReader.readLine();
                Message newMessage = new Message();
                newMessage.what = MessageConstants.MESSAGE_READ;
                newMessage.obj = response;
                handler.sendMessage(newMessage);
                // ----
                bluetoothSocket.close();
            }

            catch(Exception e) {
                Log.d("Problem","Something is Wrong 2" + e.getMessage());
                break;
            }}
    }


    public void write(byte[] bytes) throws IOException {
        try {
            outputStream.write(bytes);
            Message writtenMessage= handler.obtainMessage(MessageConstants.MESSAGE_WRITE,-1,-1,byteBuffer);
            writtenMessage.sendToTarget();
        }catch(IOException e){
            Log.d(TAG," Error sending data "+e.toString());
            Message writeErrorMsg=handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
            Bundle bundle=new Bundle();
            bundle.putString("Toast", " couldnt send data to server device");
            writeErrorMsg.setData(bundle);
            handler.sendMessage(writeErrorMsg);

        }
    }

    //To get a UUID to use with your app, you can use one of the many random UUID generators on the web, then initialize a UUID with fromString(String).
//    public void createListener(String suid, UUID uid) throws IOException {
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            bluetoothSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(suid, uid);
//            bluetoothDevice=bluetoothAdapter.
//            bluetoothSocket= bluetoothAdapter.listenUsingRfcommWithServiceRecord(suid,uid);
//
//        }
//
//    }
    public void discovery(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if(!bluetoothAdapter.isDiscovering()){Log.d("DISCOVERY BT","Making your device discoverable");
                bluetoothAdapter.startDiscovery();
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.e("CANCEL BT", "Could not close the connect socket", e);
        }
    }




}
