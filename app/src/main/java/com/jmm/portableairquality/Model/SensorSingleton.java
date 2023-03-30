package com.jmm.portableairquality.Model;

import android.graphics.Color;

public class SensorSingleton {
    int Co2Alarm,VocAlarm,Pm1Alarm,Pm2Alarm,Pm10Alarm;
    public static int  Co2Default=8500;
    public static int  VocDefault=2500;
    public static int  Pm1Default=40;
    public static int  Pm2Default=40;
    public static int  Pm10Default=250;

    public static int  GlobalGreen=456;
    public static int  GlobalYellow=1367;
    public static int  GlobalRed=4532;

public static SensorSingleton Instance=new SensorSingleton();

    public int getCo2Alarm() {
        return Co2Alarm;
    }

    public void setCo2Alarm(int co2Alarm) {
        Co2Alarm = co2Alarm;
    }

    public int getVocAlarm() {
        return VocAlarm;
    }

    public void setVocAlarm(int vocAlarm) {
        VocAlarm = vocAlarm;
    }

    public int getPm1Alarm() {
        return Pm1Alarm;
    }

    public void setPm1Alarm(int pmAlarm) {
        Pm1Alarm = pmAlarm;
    }


    public int getPm2Alarm() {
        return Pm2Alarm;
    }

    public void setPm2Alarm(int pmAlarm) {
        Pm2Alarm = pmAlarm;
    }


    public int getPm10Alarm() {
        return Pm10Alarm;
    }

    public void setPm10Alarm(int pmAlarm) {
        Pm10Alarm = pmAlarm;
    }


    public String toString(){
        return Integer.toString(this.Co2Alarm);

    }
}
