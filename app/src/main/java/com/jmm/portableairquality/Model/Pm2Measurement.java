package com.jmm.portableairquality.Model;

import java.io.Serializable;

public class Pm2Measurement implements Serializable {
    public int  pm2;

    public Pm2Measurement(byte[] value) {
        pm2 = value[1] << 8 | value[0];
    }
}
