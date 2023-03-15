package com.jmm.portableairquality.Model;

import java.io.Serializable;

public class Pm10Measurement implements Serializable {
    public int  pm10;

    public Pm10Measurement(byte[] value) {
        pm10 = value[1] << 8 | value[0];
    }
}
