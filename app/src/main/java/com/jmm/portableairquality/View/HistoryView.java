package com.jmm.portableairquality.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jmm.portableairquality.Controller.DatabaseControl;
import com.jmm.portableairquality.Model.BluetoothHandler;
import com.jmm.portableairquality.Model.DataEntry;
import com.jmm.portableairquality.R;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class HistoryView extends AppCompatActivity {
    BottomNavigationView navbot;
    LineChart chart;
    List<DataEntry> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        chart = (LineChart) findViewById(R.id.chart);
        navbot=findViewById(R.id.bottom_nav);
        navbot.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        navbot.setSelectedItemId(R.id.menu_history);
        //some function here to get the data from the database
        updateChart(data);
        registerReceiver(refresh, new IntentFilter(BluetoothHandler.MEASUREMENT_CCS)); //basic attempt at making automated refresh
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_settings:
                Intent goToSettings=new Intent(HistoryView.this, SettingsView.class);
                startActivity(goToSettings);
//                showToast("CLICKED SETTINGS");
                return true;
            case R.id.menu_home:
                Intent goToHome=new Intent(HistoryView.this, HomeView.class);
                startActivity(goToHome);

                return true;
            case R.id.menu_history:

                return true;
            default:
                return false;
        }
    }

    private void updateChart(List<DataEntry> historicalData) { //send this function sorted historical data of arbitrary length
        float present = historicalData.get(historicalData.size()-1).timestamp;
        List<Entry> co2 = new ArrayList<Entry>();
        List<Entry> voc = new ArrayList<Entry>();
        for (int i = 0; i < historicalData.size(); i++) { //represent the data in terms of seconds behind present
            historicalData.get(i).timestamp -= present;
            co2.add(new Entry(historicalData.get(i).timestamp, historicalData.get(i).co2Entry));
            voc.add(new Entry(historicalData.get(i).timestamp, historicalData.get(i).vocEntry));
        }

        LineDataSet co2Data = new LineDataSet(co2, "eCO2");
        co2Data.setAxisDependency(YAxis.AxisDependency.RIGHT); //set it to the right AXIS because it's all negative values
        LineDataSet vocData = new LineDataSet(voc, "VOCs");
        vocData.setAxisDependency(YAxis.AxisDependency.RIGHT);

        List<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(co2Data);
        sets.add(vocData);
        LineData allData = new LineData(sets);
        chart.setData(allData);
        chart.setNoDataText("oh no! no data :(");
        chart.invalidate();
    }

    private final BroadcastReceiver refresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //function to pull new data from database
            updateChart(data);
        }
    };
}