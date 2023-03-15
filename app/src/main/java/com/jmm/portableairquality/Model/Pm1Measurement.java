package com.jmm.portableairquality.Model;

import java.io.Serializable;

public class Pm1Measurement implements Serializable {
    public int  pm1;

    public Pm1Measurement(byte[] value) {
        pm1 = value[1] << 8 | value[0];
    }
}
