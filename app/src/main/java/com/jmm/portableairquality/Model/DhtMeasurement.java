package com.jmm.portableairquality.Model;

import java.io.Serializable;

public class DhtMeasurement implements Serializable {
    public float temp;
    public float hum;

    public DhtMeasurement(byte[] value) {
        int tempInt, tempFrac, humInt, humFrac;
        tempInt = value[3];
        tempFrac = value[2];
        humInt = value[1];
        humFrac = value[0];

        temp = Float.parseFloat(tempInt + "." + tempFrac);
        hum = Float.parseFloat(humInt + "." + humFrac);
    }
}
