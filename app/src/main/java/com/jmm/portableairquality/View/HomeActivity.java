package com.jmm.portableairquality.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.jmm.portableairquality.Manifest;
import com.jmm.portableairquality.Model.permissionConfig;
import com.jmm.portableairquality.R;

import java.io.OptionalDataException;
import java.security.Permission;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {

    TextView display;
    Button blueblue;
Button booboo;
PermissionChecker pc;
Boolean flag=false;
BluetoothAdapter bluey;
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
String denied=" Permission Dnied: ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        lateinit var binding= Acti
//        checkpermission(Manifest.permission.WRITE_EXTERNAL SOTRAGE,PERMISSION_CODE)
//        Manifest.
//        int[] a=permissionConfig.array();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        for(int i=0;i<PERMISSIONS.length;i++){
//            checkPermission(PERMISSIONS[0],a[0] );}

        blueblue=findViewById(R.id.bluetoothbtn);
        display=findViewById(R.id.display);
        booboo=findViewById(R.id.changeColor);

        Log.d("Color",Integer.toHexString(R.color.red_200));




blueblue.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(getApplicationContext(),ConnectDevice.class);
        startActivity(intent);
    }
});
        booboo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag){
                    display.setBackground(getResources().getDrawable(R.drawable.sensor_display_red));
                    flag=!flag;
                }
                else{display.setBackground(getResources().getDrawable(R.drawable.sensor_display_yellow));   flag=!flag;}
            }
        });
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.bottom_bar,menu);
//     Manifest.permission.
        return super.onCreateOptionsMenu(menu);
    }

}