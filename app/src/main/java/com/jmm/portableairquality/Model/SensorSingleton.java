package com.jmm.portableairquality.Model;

public class SensorSingleton {
    int Co2Alarm,VocAlarm;
    float PmAlarm;
    public static int  Co2Default=8500;
    public static int  VocDefault=2500;
    public static float  PmDefault=35;

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

    public float getPmAlarm() {
        return PmAlarm;
    }

    public void setPmAlarm(float pmAlarm) {
        PmAlarm = pmAlarm;
    }

    public String toString(){
        return Integer.toString(this.Co2Alarm);

    }
}
