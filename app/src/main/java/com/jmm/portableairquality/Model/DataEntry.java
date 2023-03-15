package com.jmm.portableairquality.Model;

import java.io.Serializable;

public class DataEntry implements Serializable {
    public float co2Entry, vocEntry, tempEntry, humEntry;
    public float timestamp; //should be in seconds

    public DataEntry(float co2Entry, float vocEntry, float tempEntry, float humEntry, long timestamp) {
        this.co2Entry = co2Entry;
        this.vocEntry = vocEntry;
        this.tempEntry = tempEntry;
        this.humEntry = humEntry;
        this.timestamp = timestamp;
    }
    //method to sort data entries by generation time
    public static boolean soonerThan(DataEntry a, DataEntry b) {
        if (a.timestamp < b.timestamp)
            return true;
        return false;
    }
    //^^
    public static boolean laterThan(DataEntry a, DataEntry b) {
        if (a.timestamp > b.timestamp)
            return true;
        return false;
    }
}