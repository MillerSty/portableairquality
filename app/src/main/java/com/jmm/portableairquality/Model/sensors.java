package com.jmm.portableairquality.Model;

public class sensors {
    int Co2Alarm,VocAlarm,PmAlarm;
public static sensors Instance=new sensors();

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

    public int getPmAlarm() {
        return PmAlarm;
    }

    public void setPmAlarm(int pmAlarm) {
        PmAlarm = pmAlarm;
    }
    public String toString(){
        return Integer.toString(this.Co2Alarm);

    }
}
