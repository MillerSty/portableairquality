package com.jmm.portableairquality.Model;

import java.io.Serializable;

public class CcsMeasurement implements Serializable {
    public long co2;
    public long voc;

    public CcsMeasurement(byte[] value) {
        co2 = (value[3] & 0xFF) << 8 | value[2] & 0xFF;
        voc = (value[1] & 0xFF) << 8 | value[0] & 0xFF;
    }
}
