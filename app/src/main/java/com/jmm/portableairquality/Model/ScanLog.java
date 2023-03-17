package com.jmm.portableairquality.Model;
import java.util.Timer;
import java.util.TimerTask;

public class ScanLog {
    private BluetoothHandler btHandler;
    private Timer timer;

    public SensorPollingTask(BluetoothHandler btHandler) {
        this.btHandler = btHandler;
        this.timer = new Timer();
    }

    public void start(long intervalMillis) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Poll sensors via BluetoothHandler
                btHandler.btCentral.scanForPeripherals();
            }
        }, 0, intervalMillis);
    }

    public void stop() {
        timer.cancel();
    }
}
}
