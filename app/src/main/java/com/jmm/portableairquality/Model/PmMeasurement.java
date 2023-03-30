package com.jmm.portableairquality.Model;

import java.io.Serializable;

public class PmMeasurement implements Serializable {
    public int  pm;

    public PmMeasurement(byte[] value) {
        pm = (value[1] & 0xFF) << 8 | (value[0] & 0xFF);
    }
}
