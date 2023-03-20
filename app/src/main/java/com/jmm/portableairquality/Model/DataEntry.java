package com.jmm.portableairquality.Model;

public class DataEntry implements Comparable<DataEntry> {
    public int co2Entry, vocEntry;
    public float tempEntry, humEntry, pm;
    public long timestamp; //should be in seconds

    public DataEntry(int co2Entry, int vocEntry, float tempEntry, float humEntry, float pm, long timestamp) {
        this.co2Entry = co2Entry;
        this.vocEntry = vocEntry;
        this.tempEntry = tempEntry;
        this.humEntry = humEntry;
        this.pm = pm;
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

    @Override
    public int compareTo(DataEntry dataEntry) {
        return (int)(this.timestamp - dataEntry.timestamp);
    }
}
