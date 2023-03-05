package com.jmm.portableairquality.Model;

public class permissionConfig {

   public static int BLUETOOTH=100;

    public static int  ACCESS_FINE_LOCATION=101;
    public static   int  BLUETOOTH_ADMIN=102;
    public  static  int  BLUETOOTH_CONNECT=103;
    public static   int  BLUETOOTH_SCAN=104;
    public static   int  ACCESS_COARSE_LOCATION=105;
    public static   int  READ_EXTERNAL_STORAGE=106;
    public  static  int  WRITE_EXTERNAL_STORAGE=107;
public static int[] array(){
    int[] a= new int[]{100, 101, 102, 103, 104, 105, 106, 107};
 return  new int[]{100, 101, 102, 103, 104, 105, 106, 107};
}
    public int getBLUETOOTH() {
        return BLUETOOTH;
    }

    public void setBLUETOOTH(int BLUETOOTH) {
        this.BLUETOOTH = BLUETOOTH;
    }

    public int getACCESS_FINE_LOCATION() {
        return ACCESS_FINE_LOCATION;
    }

    public void setACCESS_FINE_LOCATION(int ACCESS_FINE_LOCATION) {
        this.ACCESS_FINE_LOCATION = ACCESS_FINE_LOCATION;
    }

    public int getBLUETOOTH_ADMIN() {
        return BLUETOOTH_ADMIN;
    }

    public void setBLUETOOTH_ADMIN(int BLUETOOTH_ADMIN) {
        this.BLUETOOTH_ADMIN = BLUETOOTH_ADMIN;
    }

    public int getBLUETOOTH_CONNECT() {
        return BLUETOOTH_CONNECT;
    }

    public void setBLUETOOTH_CONNECT(int BLUETOOTH_CONNECT) {
        this.BLUETOOTH_CONNECT = BLUETOOTH_CONNECT;
    }

    public int getBLUETOOTH_SCAN() {
        return BLUETOOTH_SCAN;
    }

    public void setBLUETOOTH_SCAN(int BLUETOOTH_SCAN) {
        this.BLUETOOTH_SCAN = BLUETOOTH_SCAN;
    }

    public int getACCESS_COARSE_LOCATION() {
        return ACCESS_COARSE_LOCATION;
    }

    public void setACCESS_COARSE_LOCATION(int ACCESS_COARSE_LOCATION) {
        this.ACCESS_COARSE_LOCATION = ACCESS_COARSE_LOCATION;
    }

    public int getREAD_EXTERNAL_STORAGE() {
        return READ_EXTERNAL_STORAGE;
    }

    public void setREAD_EXTERNAL_STORAGE(int READ_EXTERNAL_STORAGE) {
        this.READ_EXTERNAL_STORAGE = READ_EXTERNAL_STORAGE;
    }

    public int getWRITE_EXTERNAL_STORAGE() {
        return WRITE_EXTERNAL_STORAGE;
    }

    public void setWRITE_EXTERNAL_STORAGE(int WRITE_EXTERNAL_STORAGE) {
        this.WRITE_EXTERNAL_STORAGE = WRITE_EXTERNAL_STORAGE;
    }
}
