package com.jmm.portableairquality.Model;

public class SensorSingleton {
    int Co2Alarm,VocAlarm;
    float PmAlarm;
    public static int  Co2Default=8500;
    public static int  VocDefault=2500;
    public static int  PmDefault=35;


    public static int  GlobalGreen=456;
    public static int  GlobalYellow=1367;
    public static int  GlobalRed=4532;
    public static int check_flag=0;
    public static SensorSingleton Instance=null;
    public SensorSingleton(){}

    public int getCheck_flag() {
        return check_flag;
    }

    public void setCheck_flag(int check_flag) {
        this.check_flag = check_flag;
    }

    public static SensorSingleton getInstance(){
        if(Instance==null){
            Instance=new SensorSingleton();

        }

        return Instance;

    }
    public int getCo2Alarm() {
        return Co2Alarm;
    }

    public void setCo2Alarm(int co2Alarm) {
        check_flag=1;Co2Alarm = co2Alarm;
    }

    public int getVocAlarm() {
        return VocAlarm;
    }

    public void setVocAlarm(int vocAlarm) {
        check_flag=1;VocAlarm = vocAlarm;
    }

    public float getPmAlarm() {
        return PmAlarm;
    }

    public void setPmAlarm(float pmAlarm) {
        check_flag=1;PmAlarm = pmAlarm;
    }

    public String toString(){
        return Integer.toString(this.Co2Alarm);

    }
}
